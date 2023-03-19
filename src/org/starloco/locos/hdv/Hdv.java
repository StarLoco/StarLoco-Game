package org.starloco.locos.hdv;

import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.HdvObjectData;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Logging;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.util.Pair;

import javax.swing.text.html.Option;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Hdv {
    private interface CategoryFilter {
        boolean apply(int categoryId, HdvEntry entry);
    }

    public static class Line {
        public final int lineId;
        public final int itemTemplateId;
        public final String stats;
        public final int[] minPrices;

        public Line(int lineId, int itemTemplateId, String stats, int[] minPrices) {
            this.lineId = lineId;
            this.itemTemplateId = itemTemplateId;
            this.stats = stats;
            this.minPrices = minPrices;
        }
    }

    private static final Comparator<HdvEntry> priceASC = (o1, o2) -> o2.getPrice() - o1.getPrice();
    private static final DecimalFormat pattern = new DecimalFormat("0.0");
    // defaultCategoryFilter returns true if categoryId == template.type
    private static final CategoryFilter defaultCategoryFilter = (c, e) -> c == e.getGameObject().getTemplate().getType();

    private final int hdvId;
    private final float taxe;
    private short sellTime;
    private final short maxAccountItem;
    private final List<Integer> categories;
    private final short lvlMax;

    private final AtomicInteger nextLineID = new AtomicInteger(1);
    // K: LineID, V: Pair<TemplateID, Stats>
    private final HashMap<Integer, Pair<Integer, String>> lineIDToItemDesc = new HashMap<>();
    private final HashMap<Pair<Integer, String>, Integer> statHashToLineID = new HashMap<>();
    private final LinkedList<HdvEntry> entries = new LinkedList<>();
    //private final HashMap<Integer, List<HdvEntry>> entriesInCategory = new HashMap<>(); // Just a cache to avoid recomputing the list of entry
    private final Object entriesLock = new Object();


    // private Map<Integer, HdvCategory> categorys = new HashMap<Integer, HdvCategory>();
    // private Map<Integer, Couple<Integer, Integer>> path = new HashMap<Integer, Couple<Integer, Integer>>(); //<LigneID,<CategID,TemplateID>>


    public Hdv(int hdvID, float taxe, short sellTime, short maxItemCompte,
               short lvlMax, String strCategory) {
        this.hdvId = hdvID;
        this.taxe = taxe;
        this.maxAccountItem = maxItemCompte;
        this.categories = Arrays.stream(strCategory.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        this.lvlMax = lvlMax;
    }

    public int getHdvId() {
        return hdvId;
    }

    public float getTaxe() {
        return taxe;
    }

    public short getSellTime() {
        return sellTime;
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

    private Stream<HdvEntry> getEntriesByTemplate(int categoryId, int template) {
        Function<HdvEntry, Stream<Integer>> templateMapper = categoryTemplateMapper(categoryId);
        return entries.stream().filter(e -> templateMapper.apply(e).anyMatch(i -> i == template));
    }

    public List<Line> linesForTemplate(int category, int templateId) {
        synchronized (entriesLock) {
            return getEntriesByTemplate(category, templateId).collect(Collectors.groupingBy(HdvEntry::getLineID)).entrySet().stream()
                    .map(lineEntries -> {
                        Pair<Integer, String> desc = lineIDToItemDesc.get(lineEntries.getKey());

                        Map<Byte, Optional<HdvEntry>> cheapestByQuantity = lineEntries.getValue().stream()
                                .collect(Collectors.groupingBy(HdvEntry::getAmount, Collectors.minBy(priceASC)));

                        int[] cheapest = new int[]{
                                Optional.ofNullable(cheapestByQuantity.get((byte) 0)).flatMap(Function.identity()).map(HdvEntry::getPrice).orElse(0),
                                Optional.ofNullable(cheapestByQuantity.get((byte) 1)).flatMap(Function.identity()).map(HdvEntry::getPrice).orElse(0),
                                Optional.ofNullable(cheapestByQuantity.get((byte) 2)).flatMap(Function.identity()).map(HdvEntry::getPrice).orElse(0),
                        };

                        return new Line(lineEntries.getKey(), desc.first, desc.second, cheapest);
                    }).collect(Collectors.toList());
        }
    }

    private Optional<HdvEntry> cheapestEntry(int category, int templateId, int ligneId, int amountExp) {
        return getEntriesByTemplate(category, templateId)
            .filter(e -> e.getLineID() == ligneId)
            .filter(e -> e.getAmountExp() == amountExp)
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

    private Function<HdvEntry, Stream<Integer>> categoryTemplateMapper(int category) {
        switch (category) {
            case 85:
            case 124:
            case 125:
                return (HdvEntry e) -> SoulStone.safeCast(e.getGameObject()).map(SoulStone::getMonsterIDs).orElse(Stream.empty());
            default:
                return (HdvEntry e) -> Stream.of(e.getGameObject().getTemplate().getId());
        }

    }

    public void addEntry(HdvEntry toAdd) {
        toAdd.setHdvId(this.getHdvId());
        GameObject obj = Objects.requireNonNull(toAdd.getGameObject());
        synchronized (entriesLock) {
            Pair<Integer, String> key = new Pair<>(obj.getTemplate().getId(), obj.parseStatsString());
            int lineId = statHashToLineID.computeIfAbsent(key, (ignored) -> {
                int id = nextLineID.getAndIncrement();
                lineIDToItemDesc.put(id, key);
                return id;
            });
            toAdd.setLineID(lineId);
            if (lineId == 0) throw new IllegalStateException("LineID should not be 0");
            entries.add(toAdd);
        }
        World.world.addHdvItem(toAdd.getOwner(), this.getHdvId(), toAdd);
    }

    public boolean delEntry(HdvEntry entry) {
        if(!this.entries.remove(entry)) return false;

        World.world.removeHdvItem(entry.getOwner(), entry.getHdvId(), entry);
        return true;
    }

    public Optional<HdvEntry> buyItem(int category, int templateId, int lineID, byte amount, int price, Player newOwner) {
        synchronized (entriesLock) {
            return cheapestEntry(category, templateId, lineID, amount)
                .filter(e -> e.getGameObject() != null)
                .filter(e -> e.getOwner() != newOwner.getAccount().getId())
                .filter(e -> e.getPrice() == price)
                .map(e -> {
                    if(!newOwner.modKamasDisplay(-price)) {
                        return null;
                    }

                    Optional<Account> prevOwner = Optional.ofNullable(World.world.getAccount(e.getOwner()));
                    prevOwner.ifPresent(p -> p.setBankKamas(p.getBankKamas() + price));

                    GameObject obj = e.getGameObject();
                    newOwner.addItem(obj, true, false);//Ajoute l'objet au nouveau propri�taire
                    obj.setPosition(Constant.ITEM_POS_NO_EQUIPED);
                    obj.getTemplate().newSold(e.getAmountExp(), price);

                    delEntry(e);//Retire l'item de l'HDV ainsi que de la liste du vendeur
                    ((HdvObjectData) DatabaseManager.get(HdvObjectData.class)).delete(e);
                    return e;
                });
        }
    }

    public String parseTaxe() {
        return pattern.format(this.getTaxe()).replace(",", ".");
    }

    public List<Integer> getCategoryContent(int categ) {
        CategoryFilter filter = categoryFilters(categ);
        Function<HdvEntry, Stream<Integer>> templateMapper = categoryTemplateMapper(categ);
        synchronized (entriesLock) {
            return entries.stream().filter(c -> filter.apply(categ, c))
                .flatMap(templateMapper)
                .distinct()
                .collect(Collectors.toList());
        }
    }
}
