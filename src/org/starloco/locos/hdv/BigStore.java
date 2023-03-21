package org.starloco.locos.hdv;

import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.BigStoreListingData;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.util.Pair;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BigStore {

    /**
     * TODO:
     *  - If items are sold at a too lower price, the server directly buy it for the milice
     *  - Remove items after X days if not bought
     */

    private interface CategoryFilter {
        boolean apply(int categoryId, BigStoreListing listing);
    }

    public static class CheapestListings {
        public final int lineId;
        public final int itemTemplateId;
        public final String stats;
        public final int[] minPrices;

        public CheapestListings(int lineId, int itemTemplateId, String stats, int[] minPrices) {
            this.lineId = lineId;
            this.itemTemplateId = itemTemplateId;
            this.stats = stats;
            this.minPrices = minPrices;
        }
    }

    private static final Comparator<BigStoreListing> priceASC = (o1, o2) -> o1.getPrice() - o2.getPrice();
    private static final DecimalFormat pattern = new DecimalFormat("0.0");
    // defaultCategoryFilter returns true if categoryId == template.type
    private static final CategoryFilter defaultCategoryFilter = (c, e) -> c == e.getGameObject().getTemplate().getType();

    private final int hdvId;
    private final float taxe;
    private final short duration;
    private final short maxAccountItem;
    private final List<Integer> categories;
    private final short lvlMax;
    private final AtomicInteger nextLineID = new AtomicInteger(1);
    // K: LineID, V: Pair<TemplateID, Stats>
    private final HashMap<Integer, Pair<Integer, String>> lineIDToItemDesc = new HashMap<>();
    private final HashMap<Pair<Integer, String>, Integer> statHashToLineID = new HashMap<>();
    private final LinkedList<BigStoreListing> listings = new LinkedList<>();
     private final Object listingsLock = new Object();

    public BigStore(int hdvID, float taxe, short duration, short maxItemCompte, short lvlMax, String strCategory) {
        this.hdvId = hdvID;
        this.taxe = taxe;
        this.maxAccountItem = maxItemCompte;
        this.duration = duration;
        this.categories = Arrays.stream(strCategory.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        this.lvlMax = lvlMax;
    }

    public int getHdvId() {
        return hdvId;
    }

    public float getTaxe() {
        return taxe;
    }

    public short getDuration() {
        return duration;
    }

    public short getMaxAccountItem() {
        return maxAccountItem;
    }

    public String getStrCategory() {
        return categories.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    public short getLvlMax() {
        return lvlMax;
    }

    private Stream<BigStoreListing> getEntriesByTemplate(int categoryId, int template) {
        Function<BigStoreListing, Stream<Integer>> templateMapper = categoryTemplateMapper(categoryId);
        return listings.stream().filter(e -> templateMapper.apply(e).anyMatch(i -> i == template));
    }

    public List<CheapestListings> linesForTemplate(int category, int templateId) {
        synchronized (listingsLock) {
            return getEntriesByTemplate(category, templateId).collect(Collectors.groupingBy(BigStoreListing::getLineId)).entrySet().stream()
                    .map(lineEntries -> {
                        Pair<Integer, String> desc = lineIDToItemDesc.get(lineEntries.getKey());

                        Map<Byte, Optional<BigStoreListing>> cheapestByQuantity = lineEntries.getValue().stream()
                                .collect(Collectors.groupingBy(BigStoreListing::getAmount, Collectors.minBy(priceASC)));

                        int[] cheapest = new int[]{
                                Optional.ofNullable(cheapestByQuantity.get((byte) 0)).flatMap(Function.identity()).map(BigStoreListing::getPrice).orElse(0),
                                Optional.ofNullable(cheapestByQuantity.get((byte) 1)).flatMap(Function.identity()).map(BigStoreListing::getPrice).orElse(0),
                                Optional.ofNullable(cheapestByQuantity.get((byte) 2)).flatMap(Function.identity()).map(BigStoreListing::getPrice).orElse(0),
                        };

                        return new CheapestListings(lineEntries.getKey(), desc.first, desc.second, cheapest);
                    }).collect(Collectors.toList());
        }
    }

    private Optional<BigStoreListing> cheapestListing(int category, int templateId, int ligneId, int amount) {
        return getEntriesByTemplate(category, templateId)
            .filter(e -> e.getLineId() == ligneId)
            .filter(e -> e.getAmount() == amount)
            .min(priceASC);
    }

    protected CategoryFilter categoryFilters(int category) {
        switch (category) {
            // Normal soul stone: List if it contains at least one monster that is not a boss nor an ArchMonster
            case 85:
                return (c, e) -> SoulStone.safeCast(e.getGameObject())
                        .map(SoulStone::getMonsterIDs).orElse(Stream.empty()).map(World.world::getMonstre)
                        .filter(Objects::nonNull)
                        .anyMatch(m -> !m.isBoss() && !m.isArchMonster());
            // Dungeon soul stone: List if it contains at least one monster that is a boss
            case 124:
                return (c, e) -> SoulStone.safeCast(e.getGameObject())
                        .map(SoulStone::getMonsterIDs).orElse(Stream.empty()).map(World.world::getMonstre)
                        .filter(Objects::nonNull)
                        .anyMatch(Monster::isBoss);
            // ArchMonster soul stone: List if it contains at least one monster that is a boss
            case 125:
                return (c, e) -> SoulStone.safeCast(e.getGameObject())
                        .map(SoulStone::getMonsterIDs).orElse(Stream.empty()).map(World.world::getMonstre)
                        .filter(Objects::nonNull)
                        .anyMatch(Monster::isArchMonster);
        }
        return defaultCategoryFilter;
    }

    private Function<BigStoreListing, Stream<Integer>> categoryTemplateMapper(int category) {
        switch (category) {
            case 85:
            case 124:
            case 125:
                return (BigStoreListing e) -> SoulStone.safeCast(e.getGameObject()).map(SoulStone::getMonsterIDs).orElse(Stream.empty());
            default:
                return (BigStoreListing e) -> Stream.of(e.getGameObject().getTemplate().getId());
        }

    }

    public boolean addEntry(BigStoreListing toAdd) {
        toAdd.setHdvId(this.getHdvId());
        GameObject obj = Objects.requireNonNull(toAdd.getGameObject());
        synchronized (listingsLock) {
            // If it doesn't have a guid yet, save it and get one
            if(toAdd.getId() == -1) {
                boolean saved = ((BigStoreListingData) DatabaseManager.get(BigStoreListingData.class)).insert(toAdd);
                if(!saved) {
                    // We failed to save, the listing didn't get a guid.
                    return false;
                }
            }

            Pair<Integer, String> key = new Pair<>(obj.getTemplate().getId(), obj.encodeStats());
            int lineId = statHashToLineID.computeIfAbsent(key, (ignored) -> {
                int id = nextLineID.getAndIncrement();
                lineIDToItemDesc.put(id, key);
                return id;
            });
            toAdd.setLineId(lineId);
            if (lineId == 0) throw new IllegalStateException("LineID should not be 0");
            listings.add(toAdd);

        }
        World.world.addHdvItem(toAdd.getOwner(), this.getHdvId(), toAdd);
        return true;
    }

    public boolean removeListing(Account account, int listingId) {
        Player player = account.getCurrentPlayer();
        List<BigStoreListing> accountListings = account.getHdvEntries(this.hdvId);
        if(accountListings.size() == 0) return false;
        if(player == null) return false;
        
        synchronized (listingsLock) {
            Optional<BigStoreListing> listing = accountListings.stream()
                .filter(e -> e.getId() == listingId)
                .findFirst();
            if(!listing.isPresent() || !this.listings.contains(listing.get())) return false;
            if(!deleteListing(listing.get())) return false;

            GameObject obj = listing.get().getGameObject();
            if (account.getCurrentPlayer().addObjetSimiler(obj, true, -1)) {
                World.world.removeGameObject(obj.getGuid());
            } else {
                account.getCurrentPlayer().addItem(obj, true);
            }

            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
            return true;
        }
    }

    private boolean deleteListing(BigStoreListing listing) {
        if(!this.listings.remove(listing)) return false;

        World.world.removeHdvItem(listing.getOwner(), listing.getHdvId(), listing);
        ((BigStoreListingData) DatabaseManager.get(BigStoreListingData.class)).delete(listing);
        return true;
    }

    public Optional<CheapestListings> getCheapestListings(int categoryId, int templateId, int lineId) {
        return linesForTemplate(categoryId, templateId).stream().filter(c -> c.lineId == lineId).findFirst();
    }

    public Optional<BigStoreListing> buyItem(int category, int templateId, int lineID, int amount, int price, Player newOwner) {
        synchronized (listingsLock) {
            return cheapestListing(category, templateId, lineID, amount)
                .filter(e -> e.getGameObject() != null)
                .filter(e -> e.getPrice() == price)
                .map(e -> {
                    if(!newOwner.modKamasDisplay(-price)) {
                        return null;
                    }

                    Optional<Account> prevOwner = Optional.ofNullable(World.world.getAccount(e.getOwner()));
                    prevOwner.ifPresent(p -> p.setBankKamas(p.getBankKamas() + price));

                    GameObject obj = e.getGameObject();
                    newOwner.addItem(obj, true, false);
                    obj.setPosition(Constant.ITEM_POS_NO_EQUIPED);
                    obj.getTemplate().newSold(e.getAmountExp(), price);

                    deleteListing(e);
                    return e;
                });
        }
    }

    public String parseTaxe() {
        return pattern.format(this.getTaxe()).replace(",", ".");
    }

    public List<Integer> getCategoryContent(int category) {
        CategoryFilter filter = categoryFilters(category);
        Function<BigStoreListing, Stream<Integer>> templateMapper = categoryTemplateMapper(category);
        synchronized (listingsLock) {
            return listings.stream().filter(c -> filter.apply(category, c))
                .flatMap(templateMapper)
                .distinct()
                .collect(Collectors.toList());
        }
    }
}
