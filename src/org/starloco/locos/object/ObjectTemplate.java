package org.starloco.locos.object;

import org.apache.commons.lang.ArrayUtils;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.ObjectData;
import org.starloco.locos.database.data.login.PetData;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.other.Dopeul;

import java.util.*;

public class ObjectTemplate {

    private int id;
    private String strTemplate;
    private String name;
    private int type;
    private int level;
    private int pod;
    private int price;
    private int panoId;
    private String conditions;
    private int PACost, POmin, POmax, tauxCC, tauxEC,
            bonusCC;
    private boolean isTwoHanded;
    private long sold;
    private int avgPrice;
    private int points, newPrice;
    private int money=-1;
    private ArrayList<ObjectAction> onUseActions;

    public String toString() {
        return id + "";
    }

    public ObjectTemplate(int id, String strTemplate, String name, int type,
                          int level, int pod, int price, int panoId, String conditions,
                          String armesInfos, int sold, int avgPrice, int points, int newPrice) {
        this.id = id;
        this.strTemplate = strTemplate;
        this.name = name;
        this.type = type;
        this.level = level;
        this.pod = pod;
        this.price = price;
        this.panoId = panoId;
        this.conditions = conditions;
        this.PACost = -1;
        this.POmin = 1;
        this.POmax = 1;
        this.tauxCC = 100;
        this.tauxEC = 2;
        this.bonusCC = 0;
        this.sold = sold;
        this.avgPrice = avgPrice;
        this.points = points;
        this.newPrice = newPrice;
        this.money=-1;
        if(armesInfos.isEmpty()) return;
        try {
            String[] infos = armesInfos.split(";");
            PACost = Integer.parseInt(infos[0]);
            POmin = Integer.parseInt(infos[1]);
            POmax = Integer.parseInt(infos[2]);
            tauxCC = Integer.parseInt(infos[3]);
            tauxEC = Integer.parseInt(infos[4]);
            bonusCC = Integer.parseInt(infos[5]);
            isTwoHanded = infos[6].equals("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setInfos(String strTemplate, String name, int type, int level, int pod, int price, int panoId, String conditions, String armesInfos, int sold, int avgPrice, int points, int newPrice) {
        this.strTemplate = strTemplate;
        this.name = name;
        this.type = type;
        this.level = level;
        this.pod = pod;
        this.price = price;
        this.panoId = panoId;
        this.conditions = conditions;
        this.PACost = -1;
        this.POmin = 1;
        this.POmax = 1;
        this.tauxCC = 100;
        this.tauxEC = 2;
        this.bonusCC = 0;
        this.sold = sold;
        this.avgPrice = avgPrice;
        this.points = points;
        this.newPrice = newPrice;
        this.money=-1;
        try {
            String[] infos = armesInfos.split(";");
            PACost = Integer.parseInt(infos[0]);
            POmin = Integer.parseInt(infos[1]);
            POmax = Integer.parseInt(infos[2]);
            tauxCC = Integer.parseInt(infos[3]);
            tauxEC = Integer.parseInt(infos[4]);
            bonusCC = Integer.parseInt(infos[5]);
            isTwoHanded = infos[6].equals("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int id) {
        this.money = id;
    }

    public int getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(int id) {
        this.newPrice = id;
    }

    public String getStrTemplate() {
        return strTemplate;
    }

    public void setStrTemplate(String strTemplate) {
        this.strTemplate = strTemplate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPod() {
        return pod;
    }

    public void setPod(int pod) {
        this.pod = pod;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPanoId() {
        return panoId;
    }

    public void setPanoId(int panoId) {
        this.panoId = panoId;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public int getPACost() {
        return PACost;
    }

    public void setPACost(int pACost) {
        PACost = pACost;
    }

    public int getPOmin() {
        return POmin;
    }

    public void setPOmin(int pOmin) {
        POmin = pOmin;
    }

    public int getPOmax() {
        return POmax;
    }

    public void setPOmax(int pOmax) {
        POmax = pOmax;
    }

    public int getTauxCC() {
        return tauxCC;
    }

    public void setTauxCC(int tauxCC) {
        this.tauxCC = tauxCC;
    }

    public int getTauxEC() {
        return tauxEC;
    }

    public void setTauxEC(int tauxEC) {
        this.tauxEC = tauxEC;
    }

    public int getBonusCC() {
        return bonusCC;
    }

    public void setBonusCC(int bonusCC) {
        this.bonusCC = bonusCC;
    }

    public boolean isTwoHanded() {
        return isTwoHanded;
    }

    public void setTwoHanded(boolean isTwoHanded) {
        this.isTwoHanded = isTwoHanded;
    }

    public int getAvgPrice() {
        return avgPrice;
    }

    public long getSold() {
        return this.sold;
    }

    public int getPoints() {
        return this.points;
    }

    public void addAction(ObjectAction A) {
        if(this.onUseActions == null)
            this.onUseActions = new ArrayList<>();
        this.onUseActions.add(A);
    }

    public ArrayList<ObjectAction> getOnUseActions() {
        return onUseActions == null ? new ArrayList<>() : onUseActions;
    }

    public GameObject createNewCertificat(GameObject obj) {
        GameObject item = null;
        if (getType() == Constant.ITEM_TYPE_CERTIFICAT_CHANIL) {
            PetEntry myPets = World.world.getPetsEntry(obj.getGuid());
            Map<Integer, String> txtStat = new HashMap<>();
            Map<Integer, String> actualStat = obj.getTxtStat();
            if (actualStat.containsKey(Constant.STATS_PETS_PDV))
                txtStat.put(Constant.STATS_PETS_PDV, actualStat.get(Constant.STATS_PETS_PDV));
            if (actualStat.containsKey(Constant.STATS_PETS_DATE))
                txtStat.put(Constant.STATS_PETS_DATE, myPets.getLastEatDate() + "");
            if (actualStat.containsKey(Constant.STATS_PETS_POIDS))
                txtStat.put(Constant.STATS_PETS_POIDS, actualStat.get(Constant.STATS_PETS_POIDS));
            if (actualStat.containsKey(Constant.STATS_PETS_EPO))
                txtStat.put(Constant.STATS_PETS_EPO, actualStat.get(Constant.STATS_PETS_EPO));
            if (actualStat.containsKey(Constant.STATS_PETS_REPAS))
                txtStat.put(Constant.STATS_PETS_REPAS, actualStat.get(Constant.STATS_PETS_REPAS));
            item = new GameObject(-1, getId(), 1, Constant.ITEM_POS_NO_EQUIPED, obj.getStats(), new ArrayList<>(), new HashMap<>(), txtStat, 0);
            ((ObjectData) DatabaseManager.get(ObjectData.class)).insert(item);
            ((PetData) DatabaseManager.get(PetData.class)).delete(World.world.getPetsEntry(obj.getGuid()));
            World.world.removePetsEntry(obj.getGuid());
        }
        return item;
    }
//TODO: PRendre example ici !!!!
    public GameObject createNewFamilier(GameObject obj) {
        Map<Integer, String> stats = new HashMap<>();
        stats.putAll(obj.getTxtStat());
        GameObject object = new GameObject(-1, getId(), 1, Constant.ITEM_POS_NO_EQUIPED, obj.getStats(), new ArrayList<>(), new HashMap<>(), stats, 0);

        if(((ObjectData) DatabaseManager.get(ObjectData.class)).insert(object)) {
            PetEntry petEntry = new PetEntry(object.getGuid(), getId(), System.currentTimeMillis(), 0, Integer.parseInt(stats.get(Constant.STATS_PETS_PDV), 16), Integer.parseInt(stats.get(Constant.STATS_PETS_POIDS), 16), !stats.containsKey(Constant.STATS_PETS_EPO));

            if (((PetData) DatabaseManager.get(PetData.class)).insert(petEntry)) {
                World.world.addPetsEntry(petEntry);
                return object;
            }
        }
        return null;
    }

    public GameObject createNewBenediction(int turn) {
        Stats stats = generateNewStatsFromTemplate(getStrTemplate(), true);
        stats.addOneStat(Constant.STATS_TURN, turn);
        GameObject item = new GameObject(-1, getId(), 1, Constant.ITEM_POS_BENEDICTION, stats, new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
        if(((ObjectData) DatabaseManager.get(ObjectData.class)).insert(item))
            return item;
        return null;
    }

    public GameObject createNewMalediction() {
        Stats stats = generateNewStatsFromTemplate(getStrTemplate(), true);
        stats.addOneStat(Constant.STATS_TURN, 1);
        GameObject object = new GameObject(-1, getId(), 1, Constant.ITEM_POS_MALEDICTION, stats, new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
        if(((ObjectData) DatabaseManager.get(ObjectData.class)).insert(object))
            return object;
        return null;
    }

    public GameObject createNewRoleplayBuff() {
        Stats stats = generateNewStatsFromTemplate(getStrTemplate(), true);
        stats.addOneStat(Constant.STATS_TURN, 1);
        GameObject object = new GameObject(-1, getId(), 1, Constant.ITEM_POS_ROLEPLAY_BUFF, stats, new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
        if(((ObjectData) DatabaseManager.get(ObjectData.class)).insert(object))
            return object;
        return null;
    }

    public GameObject createNewCandy(int turn) {
        Stats stats = generateNewStatsFromTemplate(getStrTemplate(), true);
        stats.addOneStat(Constant.STATS_TURN, turn);
        GameObject item = new GameObject(-1, getId(), 1, Constant.ITEM_POS_BONBON, stats, new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
        if(((ObjectData) DatabaseManager.get(ObjectData.class)).insert(item))
            return item;
        return null;
    }

    public GameObject createNewTonique(int posTonique,String StatsToadd) {
        Stats stats = generateNewStatsFromTemplate(StatsToadd, true);
        GameObject item = new GameObject(-1, getId(), 1, posTonique, stats, new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
        if(((ObjectData) DatabaseManager.get(ObjectData.class)).insert(item))
            return item;
        return null;
    }

    public GameObject createNewToniqueEquilibrage(Stats stats) {
        GameObject item = new GameObject(-1, getId(), 1, Constant.ITEM_POS_TONIQUE_EQUILIBRAGE, stats, new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
        if(((ObjectData) DatabaseManager.get(ObjectData.class)).insert(item))
            return item;
        return null;
    }

    public GameObject createNewFollowPnj(int turn) {
        Stats stats = generateNewStatsFromTemplate(getStrTemplate(), true);
        stats.addOneStat(Constant.STATS_TURN, turn);
        stats.addOneStat(148, 0);
        GameObject item = new GameObject(id, getId(), 1, Constant.ITEM_POS_PNJ_SUIVEUR, stats, new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
        if(((ObjectData) DatabaseManager.get(ObjectData.class)).insert(item))
            return item;
        return null;
    }

    public GameObject createNewItem(int qua, boolean useMax) {
        try {
            GameObject item;
            if (getType() == Constant.ITEM_TYPE_QUETES && (Constant.isCertificatDopeuls(getId()) || getId() == 6653 || getId() == 12803 )) {
                Map<Integer, String> txtStat = new HashMap<>();
                txtStat.put(Constant.STATS_DATE, System.currentTimeMillis() + "");
                item = new GameObject(-1, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(false, null), new ArrayList<>(), new HashMap<>(), txtStat, 0);
            } else if (this.getId() == 10207) {
                item = new GameObject(-1, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(false, null), new ArrayList<>(), new HashMap<>(), Dopeul.generateStatsTrousseau(), 0);
            } else if (getType() == Constant.ITEM_TYPE_FAMILIER) {
                String jet = World.world.getPets(this.getId()).getJet();
                Stats stats = useMax ? generateNewStatsFromTemplate(jet, true) : new Stats(false, null);

                item = new GameObject(-1, getId(), 1, Constant.ITEM_POS_NO_EQUIPED, stats, new ArrayList<>(), new HashMap<>(), World.world.getPets(getId()).generateNewtxtStatsForPets(), 0);
                if (((ObjectData) DatabaseManager.get(ObjectData.class)).insert(item)) {
                    PetEntry pet = new PetEntry(item.getGuid(), getId(), System.currentTimeMillis(), 0, 10, 0, false);
                    World.world.addPetsEntry(pet);
                    ((PetData) DatabaseManager.get(PetData.class)).insert(pet);
                    return item;
                }
                return null;
            } else if (getType() == Constant.ITEM_TYPE_CERTIF_MONTURE) {
                item = new GameObject(-1, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, generateNewStatsFromTemplate(getStrTemplate(), useMax), getEffectTemplate(getStrTemplate()), new HashMap<>(), new HashMap<>(), 0);
            } else {
                if (getType() == Constant.ITEM_TYPE_OBJET_ELEVAGE) {
                    item = new GameObject(-1, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(false, null), new ArrayList<>(), new HashMap<>(), getStringResistance(getStrTemplate()), 0);
                } else if (Constant.isGladiatroolWeapon(getId())) {
                    Map<Integer, String> Stats = new HashMap<>();
                    Stats.put(Constant.STATS_EXCHANGE_IN, -1+"");
                    Stats.put(Constant.STATS_NIVEAU2, 1+"");
                    item = new GameObject(-1, getId(), qua, 1, generateNewStatsFromTemplate(getStrTemplate(), useMax), getEffectTemplate(getStrTemplate()),new HashMap<>() , Stats, 0);
                } else if (getId()==16001) {
                    Map<Integer, String> Stats = new HashMap<>();
                    Stats.put(Constant.STATS_EXCHANGE_IN, -1+"");
                    item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(), new ArrayList<SpellEffect>(), new HashMap<Integer, Integer>(), Stats, 0);
                }
                else {
                    Map<Integer, String> Stat = new HashMap<>();
                    switch (getType()) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                            if (getStrTemplate() == null || getStrTemplate().equalsIgnoreCase("") || getStrTemplate().length() <= 1)
                                break;
                            for (String stat : this.getStrTemplate().split(",")) {
                                if(!(stat == null || stat.length() == 0)) {
                                    String[] stats = stat.split("#");
                                    int id = Integer.parseInt(stats[0], 16);
                                    if (id == Constant.STATS_RESIST) Stat.put(id, stats[1]);
                                    if (id == Constant.STATS_EXCHANGE_IN) Stat.put(id, stats[1]);
                                }
                            }
                            break;
                    }
                    item = new GameObject(-1, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, generateNewStatsFromTemplate(getStrTemplate(), useMax), getEffectTemplate(getStrTemplate()), new HashMap<>(), Stat, 0);
                    item.getSpellStats().addAll(this.getSpellStatsTemplate());
                }
            }
            if (((ObjectData) DatabaseManager.get(ObjectData.class)).insert(item))
                return item;

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public GameObject createNewItemWithoutDuplication(Collection<GameObject> objects, int qua, boolean useMax) {
        int id = -1;
        GameObject item;
        if (getType() == Constant.ITEM_TYPE_QUETES && (Constant.isCertificatDopeuls(getId()) || getId() == 6653)) {
            Map<Integer, String> txtStat = new HashMap<>();
            txtStat.put(Constant.STATS_DATE, System.currentTimeMillis() + "");
            item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(false, null), new ArrayList<>(), new HashMap<>(), txtStat, 0);
        } else if (this.getId() == 10207) {
            item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(false, null), new ArrayList<>(), new HashMap<>(), Dopeul.generateStatsTrousseau(), 0);
        } else if (getType() == Constant.ITEM_TYPE_FAMILIER) {
            item = new GameObject(id, getId(), 1, Constant.ITEM_POS_NO_EQUIPED, (useMax ? generateNewStatsFromTemplate(World.world.getPets(this.getId()).getJet(), false) : new Stats(false, null)), new ArrayList<>(), new HashMap<>(), World.world.getPets(getId()).generateNewtxtStatsForPets(), 0);
            //Ajouter du Pets_data SQL et World
            if(((ObjectData) DatabaseManager.get(ObjectData.class)).insert(item)) {
                PetEntry pet = new PetEntry(item.getGuid(), getId(), System.currentTimeMillis(), 0, 10, 0, false);
                World.world.addPetsEntry(pet);
                ((PetData) DatabaseManager.get(PetData.class)).insert(pet);
                return item;
            }
            return null;
        } else if(getType() == Constant.ITEM_TYPE_CERTIF_MONTURE) {
            item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, generateNewStatsFromTemplate(getStrTemplate(), useMax), getEffectTemplate(getStrTemplate()), new HashMap<>(), new HashMap<>(), 0);
        } else {
            if (getType() == Constant.ITEM_TYPE_OBJET_ELEVAGE) {
                item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(false, null), new ArrayList<>(), new HashMap<>(), getStringResistance(getStrTemplate()), 0);
            } else if (Constant.isIncarnationWeapon(getId())) {
                Map<Integer, Integer> Stats = new HashMap<>();
                Stats.put(Constant.ERR_STATS_XP, 0);
                Stats.put(Constant.STATS_NIVEAU, 1);
                item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, generateNewStatsFromTemplate(getStrTemplate(), useMax), getEffectTemplate(getStrTemplate()), Stats, new HashMap<>(), 0);
            } else {
                Map<Integer, String> Stat = new HashMap<>();
                switch (getType()) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        String[] splitted = getStrTemplate().split(",");
                        for (String s : splitted) {
                            String[] stats = s.split("#");
                            int statID = Integer.parseInt(stats[0], 16);
                            if (statID == Constant.STATS_RESIST) {
                                String ResistanceIni = stats[1];
                                Stat.put(statID, ResistanceIni);
                            }
                        }
                        break;
                }
                item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, generateNewStatsFromTemplate(getStrTemplate(), useMax), getEffectTemplate(getStrTemplate()), new HashMap<Integer, Integer>(), Stat, 0);
                item.getSpellStats().addAll(this.getSpellStatsTemplate());
            }
        }

        for(GameObject object : objects) {
            if (World.world.getConditionManager().stackIfSimilar(object, item, true)) {
                object.setQuantity(object.getQuantity() + item.getQuantity());
                return object;
            }
        }
        ((ObjectData) DatabaseManager.get(ObjectData.class)).insert(item);
        return item;
    }

    private Map<Integer, String> getStringResistance(String statsTemplate) {
        Map<Integer, String> Stat = new HashMap<>();
        String[] splitted = statsTemplate.split(",");

        for (String s : splitted) {
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
            String ResistanceIni = stats[1];
            Stat.put(statID, ResistanceIni);
        }
        return Stat;
    }

    public ArrayList<String> getSpellStatsTemplate() {
        final ArrayList<String> spellStats = new ArrayList<>();

        if(!this.getStrTemplate().isEmpty()) {
            for (String stats : this.getStrTemplate().split(",")) {
                String[] split = stats.split("#");
                int id = Integer.parseInt(split[0], 16);

                if (id >= 281 && id <= 294) {
                    spellStats.add(stats);
                }
            }
        }
        return spellStats;
    }

    public Stats generateNewStatsFromTemplate(String statsTemplate,
                                              boolean useMax) {
        Stats itemStats = new Stats(false, null);
        //Si stats Vides
        if (statsTemplate.equals(""))
            return itemStats;

        String[] splitted = statsTemplate.split(",");
        for (String s : splitted) {
            if(!(s == null || s.length() == 0)) {
                String[] stats = s.split("#");
                int statID = Integer.parseInt(stats[0], 16);
                boolean follow = true;

                if(ArrayUtils.contains(Constant.ARMES_EFFECT_IDS, statID))
                    continue; //Si c'�tait un effet Actif d'arme
                if (statID >= 281 && statID <= 294)
                    continue;
                if (statID == Constant.STATS_RESIST)
                    continue;
                boolean isStatsInvalid = false;
                switch (statID) {
                    case 110:
                    case 139:
                    case 605:
                    case 614:
                    case Constant.STATS_EXCHANGE_IN:
                        isStatsInvalid = true;
                        break;
                    case 615:
                        itemStats.addOneStat(statID, Integer.parseInt(stats[3], 16));
                        break;
                }
                if (isStatsInvalid)
                    continue;
                String jet = "";
                int value = 1;
                try {
                    jet = stats[4];
                    value = Formulas.getRandomJet(null, null, jet);
                    if (useMax) {
                        try {
                            //on prend le jet max
                            int min = Integer.parseInt(stats[1], 16);
                            int max = 0;
                            try {
                                max = Integer.parseInt(stats[2], 16);
                            }
                            catch(Exception e){}
                            value = min;
                            if (max != 0)
                                value = max;
                        } catch (Exception e) {
                            e.printStackTrace();
                            value = Formulas.getRandomJet(null, null, jet);
                        }
                    }
                } catch (Exception e) {
                    System.err.println(statsTemplate + " : " + s + " : " + e.getMessage());
                }

                itemStats.addOneStat(statID, value);
            }
        }
        return itemStats;
    }

    private ArrayList<SpellEffect> getEffectTemplate(String statsTemplate) {
        ArrayList<SpellEffect> Effets = new ArrayList<>();
        if (statsTemplate.equals(""))
            return Effets;

        String[] splitted = statsTemplate.split(",");
        for (String s : splitted) {
            if(s == ""){
                continue;
            }
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
            for (int a : Constant.ARMES_EFFECT_IDS) {
                if (a == statID) {
                    String min = stats[1];
                    String max = stats[2];
                    String jet = stats[4];
                    String args = min + ";" + max + ";-1;-1;0;" + jet;
                    Effets.add(new SpellEffect(statID, args, 0, -1));
                }
            }
            switch (statID) {
                case 110:
                case 139:
                case 605:
                case 614:
                    String min = stats[1];
                    String max = stats[2];
                    String jet = stats[4];
                    String args = min + ";" + max + ";-1;-1;0;" + jet;
                    Effets.add(new SpellEffect(statID, args, 0, -1));
                    break;
            }
        }
        return Effets;
    }

    public String parseItemTemplateStats() {
        if(this.money == -1) {
            return getId() + ";" + getStrTemplate() + (this.newPrice > 0 ? ";" + this.newPrice : "");
        }
        else{
            return getId() + ";" + getStrTemplate() + ";" + this.money + ";" + (this.newPrice > 0 ? ";" + this.newPrice : "") +";;";
        }
    }

    public void applyAction(Player player, Player target, int objectId, short cellId, int quantity) {
        for (int i = 0; i < quantity; i++) {
            if (World.world.getGameObject(objectId) == null) return;
            if (World.world.getGameObject(objectId).getTemplate().getType() == 85 && World.world.getGameObject(objectId).getTemplate().getId() == 7010) {
                if (!SoulStone.isInArenaMap(player.getCurMap().getId()))
                    if (!SoulStone.isInArenaMap(player.getCurMap().getId()))
                        return;

                SoulStone soulStone = (SoulStone) World.world.getGameObject(objectId);

                player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), soulStone.parseGroupData(), "MiS=" + player.getId());
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + World.world.getGameObject(objectId).getTemplate().getId());
                player.removeItem(objectId, 1, true, true);
            } else {
                for (ObjectAction action : this.getOnUseActions())
                    action.apply(player, target, objectId, cellId);
            }
        }
    }

    public synchronized void newSold(int amount, int price) {
        long oldSold = getSold();
        sold += amount;
        avgPrice = (int) ((getAvgPrice() * oldSold + price) / getSold());
    }
    private final static List<Integer> bannedObjects = Arrays.asList(493,494,495,496,922,7098,9972,454, 577, 596, 492, 491, 493, 494, 495, 496, 498, 499, 500, 577, 579, 911, 922, 1501, 1520, 1539, 1560, 1561, 1562, 1563, 1564, 1565, 2154, 2155, 2156, 2170, 2376, 6663, 6713, 6839, 6840, 7098, 7493, 7495, 7650, 7913, 7920, 8539, 8540, 8854, 9031, 9202, 9396, 9627, 9961, 9544, 9545, 9546, 9547, 9548, 10125, 10126, 10127, 10133);

    public boolean isAnEquipment(boolean ethereal, List<Integer> bannedTypes) {
        if ((!ethereal && this.getStrTemplate().contains("32c#")) || this.getStrTemplate().isEmpty() || (this.conditions != null && this.conditions.contains("BI")))
            return false;
        if(bannedTypes != null && bannedTypes.contains(this.getType()))
            return false;

        switch (this.getType()) {
            case Constant.ITEM_TYPE_AMULETTE:
            case Constant.ITEM_TYPE_ANNEAU:
            case Constant.ITEM_TYPE_BOTTES:
            case Constant.ITEM_TYPE_CEINTURE:
            case Constant.ITEM_TYPE_COIFFE:
            case Constant.ITEM_TYPE_CAPE:
            case Constant.ITEM_TYPE_OUTIL:
            case Constant.ITEM_TYPE_EPEE:
            case Constant.ITEM_TYPE_SAC_DOS:
            case Constant.ITEM_TYPE_ARC:
            case Constant.ITEM_TYPE_PIOCHE:
            case Constant.ITEM_TYPE_HACHE:
            case Constant.ITEM_TYPE_MARTEAU:
            case Constant.ITEM_TYPE_BAGUETTE:
            case Constant.ITEM_TYPE_DAGUES:
            case Constant.ITEM_TYPE_BATON:
            case Constant.ITEM_TYPE_PELLE:
            case Constant.ITEM_TYPE_FAUX:
            case Constant.ITEM_TYPE_FAMILIER:
            case Constant.ITEM_TYPE_BOUCLIER:
                break;
            case Constant.ITEM_TYPE_CERTIF_MONTURE:
                if(getId() == 7806 || getId() == 7807 || getId() == 7809 || getId() == 7864 || getId() == 7865)
                    return false;
                break;
            default:
                return false;
        }
        return !bannedObjects.contains(this.getId());
    }
}