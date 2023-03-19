package org.starloco.locos.hdv;

import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.entity.SoulStone;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Hdv {

    private interface CategoryFilter {
        boolean apply(int categoryId, HdvEntry entry);
    }
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
    private final ConcurrentHashMap<String,Integer> statHashToLineID = new ConcurrentHashMap<>();
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

    protected CategoryFilter categoryFilters(int category) {
        switch (category) {
            // Normal soul stone: List if it contains at least one monster that is not a boss nor an ArchMonster
            case 85: return (c, e) -> SoulStone.safeCast(e.getGameObject())
                .map(SoulStone::getMonsterIDs).orElse(Stream.empty()).map(World.world::getMonstre)
                .filter(Objects::nonNull)
                .anyMatch(m -> !m.isBoss() && !m.isArchMonster());
            // Dungeon soul stone: List if it contains at least one monster that is a boss
            case 124: return (c, e) -> SoulStone.safeCast(e.getGameObject())
                .map(SoulStone::getMonsterIDs).orElse(Stream.empty()).map(World.world::getMonstre)
                .filter(Objects::nonNull)
                .anyMatch(Monster::isBoss);
            // ArchMonster soul stone: List if it contains at least one monster that is a boss
            case 125: return (c, e) -> SoulStone.safeCast(e.getGameObject())
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

    public Optional<Integer> lineTemplateIDOverride(int category) {
        switch(category) {
            case 85:
                return Optional.of(7010);
            case 124:
                return Optional.of(10417);
            case 125:
                return Optional.of(10418);
            default:
                return Optional.empty();
        }
    }

//    public HdvLine getLine(int lineId) {
//        if (this.path == null || this.path.get(lineId) == null || this.getCategorys() == null)
//            return null;
//
//        int categoryId = this.path.get(lineId).first;
//        int templateId = this.path.get(lineId).second;
//
//        HdvCategory category = this.getCategorys().get(categoryId);
//
//        if (category == null)
//            return null;
//
//        HdvTemplate template = category.getTemplate(templateId);
//
//        if (template == null)
//            return null;
//
//        return template.getLine(lineId);
//    }
//
    public void addEntry(HdvEntry toAdd) {
        toAdd.setHdvId(this.getHdvId());
        synchronized (entriesLock) {
            entries.add(toAdd);
        }
        World.world.addHdvItem(toAdd.getOwner(), this.getHdvId(), toAdd);
    }
//
//    public boolean delEntry(HdvEntry toDel) {
//        boolean toReturn = this.getCategorys().get(toDel.getGameObject().getTemplate().getType()).delEntry(toDel);
//        if (toReturn) {
//            this.path.remove(toDel.getLineId());
//            World.world.removeHdvItem(toDel.getOwner(), toDel.getHdvId(), toDel);
//        }
//        return toReturn;
//    }

//    public synchronized boolean buyItem(int ligneID, byte amount, int price,
//                                        Player newOwner) {
//        boolean toReturn = true;
//        try {
//            if (newOwner.getKamas() < price)
//                return false;
//
//            HdvLine ligne = this.getLine(ligneID);
//            HdvEntry toBuy = ligne.doYouHave(amount, price);
//
//            if (toBuy.buy) return false;
//            toBuy.buy = true;
//
//            newOwner.addKamas(price * -1);//Retire l'argent � l'acheteur (prix et taxe de vente)
//
//            if (toBuy.getOwner() != -1) {
//                Account C = World.world.getAccount(toBuy.getOwner());
//                if (C != null)
//                    C.setBankKamas(C.getBankKamas() + toBuy.getPrice());//Ajoute l'argent au vendeur
//            }
//            SocketManager.GAME_SEND_STATS_PACKET(newOwner);//Met a jour les kamas de l'acheteur
//
//            toBuy.getGameObject().setPosition(Constant.ITEM_POS_NO_EQUIPED);
//            newOwner.addItem(toBuy.getGameObject(), true, false);//Ajoute l'objet au nouveau propri�taire
//            toBuy.getGameObject().getTemplate().newSold(toBuy.getAmountExp(), price);//Ajoute la ventes au statistiques
//            try {
//                String name = "undefined";
//
//                if(World.world.getAccount(toBuy.getOwner()) != null)
//                    name = World.world.getAccount(toBuy.getOwner()).getName();
//
//                Logging.getInstance().write("Object", "BuyHdv : "
//                        + newOwner.getName() + " : achat de "
//                        + toBuy.getGameObject().getTemplate().getName() + "(" + toBuy.getGameObject().getGuid() + ") x"
//                        + toBuy.getGameObject().getQuantity() + " venant du compte "
//                        + name);
//            } catch(Exception e) { e.printStackTrace(); }
//            delEntry(toBuy);//Retire l'item de l'HDV ainsi que de la liste du vendeur
//            ((HdvObjectData) DatabaseManager.get(HdvObjectData.class)).delete(toBuy);
//            if (World.world.getAccount(toBuy.getOwner()) != null
//                    && World.world.getAccount(toBuy.getOwner()).getCurrentPlayer() != null)
//                SocketManager.GAME_SEND_Im_PACKET(World.world.getAccount(toBuy.getOwner()).getCurrentPlayer(), "065;"
//                        + price
//                        + "~"
//                        + toBuy.getGameObject().getTemplate().getId()
//                        + "~" + toBuy.getGameObject().getTemplate().getId() + "~1");
//            //Si le vendeur est connecter, envoie du packet qui lui annonce la vente de son objet
//            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(newOwner);
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//            toReturn = false;
//        }
//
//        return toReturn;
//    }

    public int lineIDForStats(String key) {
        return statHashToLineID.computeIfAbsent(key, (ignored) -> nextLineID.getAndIncrement());
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

    public List<HdvEntry> getEntriesByTemplate(int categoryId, int template) {
        Function<HdvEntry,Stream<Integer>> templateMapper = categoryTemplateMapper(categoryId);
        synchronized (entriesLock) {
            return entries.stream().filter(e -> templateMapper.apply(e).anyMatch(i -> i == template)).collect(Collectors.toList());
        }
    }
}
