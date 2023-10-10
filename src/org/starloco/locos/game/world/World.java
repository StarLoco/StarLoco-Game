package org.starloco.locos.game.world;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.starloco.locos.anims.Animation;
import org.starloco.locos.area.Area;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.MapData;
import org.starloco.locos.area.map.ScriptMapData;
import org.starloco.locos.entity.map.*;
import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.ConditionParser;
import org.starloco.locos.common.CryptManager;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.*;
import org.starloco.locos.database.data.login.*;
import org.starloco.locos.entity.Collector;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.entity.pet.Pet;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.hdv.BigStore;
import org.starloco.locos.hdv.BigStoreListing;
import org.starloco.locos.job.Job;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectSet;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.object.entity.Fragment;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.guild.Guild;
import org.starloco.locos.script.DataScriptVM;
import org.starloco.locos.script.Scripted;
import org.starloco.locos.script.proxy.SWorld;
import org.starloco.locos.util.TimerWaiter;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class World implements Scripted<SWorld> {
    private final SWorld scriptVal;

    public final static World world = new World();

    public Logger logger = (Logger) LoggerFactory.getLogger(World.class);
    private final Map<Integer, Account> accounts = new HashMap<>();
    private final Map<Integer, Player> players = new HashMap<>();
    private final Map<Integer, GameMap> maps = new ConcurrentHashMap<>();
    private final Map<Integer, MapData> mapsData = new ConcurrentHashMap<>();
    private final Map<Integer, GameObject> objects = new ConcurrentHashMap<>();

    private volatile ExperienceTables experiences;

    private final Map<Integer, Spell> spells = new HashMap<>();
    private final Map<Integer, ObjectTemplate> ObjTemplates = new HashMap<>();
    private final Map<Integer, Monster> MobTemplates = new HashMap<>();
    private final Map<Integer, NpcTemplate> npcsTemplate = new HashMap<>();
    private final Map<Integer, Mount> Dragodindes = new HashMap<>();
    private final Map<Integer, Area> areas = new HashMap<>();
    private final Map<Integer, SubArea> subAreas = new HashMap<>();
    private final Map<Integer, Job> Jobs = new HashMap<>();
    private final Map<Integer, ArrayList<Couple<Integer, Integer>>> Crafts = new HashMap<>();
    private final Map<Integer, ObjectSet> ItemSets = new HashMap<>();
    private final Map<Integer, Guild> Guildes = new HashMap<>();
    private final Map<Integer, BigStore> Hdvs = new HashMap<>();
    private final Map<Integer, Map<Integer, List<BigStoreListing>>> hdvsItems = new HashMap<>();
    private final Map<Integer, Animation> animations = new HashMap<>();
    private final Map<Integer, org.starloco.locos.entity.map.MountPark> MountPark = new HashMap<>();
    private final Map<Integer, Trunk> Trunks = new HashMap<>();
    private final Map<Integer, Collector> collectors = new HashMap<>();
    private final Map<Integer, House> Houses = new HashMap<>();
    private final Map<Integer, Collection<Integer>> Seller = new HashMap<>();
    private final StringBuilder Challenges = new StringBuilder();
    private final Map<Integer, Prism> Prismes = new HashMap<>();
    private final Map<Integer, Map<String, String>> fullmorphs = new HashMap<>();
    private final Map<Integer, Pet> Pets = new HashMap<>();
    private final Map<Integer, PetEntry> PetsEntry = new HashMap<>();
    private final Map<String, Map<String, String>> mobsGroupsFix = new HashMap<>();
    private final Map<Integer, Map<String, Map<String, Integer>>> extraMonstre = new HashMap<>();
    private final Map<Integer, GameMap> extraMonstreOnMap = new HashMap<>();
    private final Map<Integer, Long> delayCollectors = new HashMap<>();
    private final Map<Integer, InteractiveObjectTemplate> interactiveObjects = new HashMap<>();
    private final Map<Integer,Integer> spriteToObject = new HashMap<>();

    // Single threaded executor to avoid concurrency issues. Will be optimized once we refactor threads
    public final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);

    public World() {
        this.scriptVal = new SWorld(this);
    }

    public Map<Integer, Long> getDelayCollectors() {
        return delayCollectors;
    }

    private final HouseManager houseManager = new HouseManager();

    public HouseManager getHouseManager() {
        return houseManager;
    }

    private final CryptManager cryptManager = new CryptManager();

    public CryptManager getCryptManager() {
        return cryptManager;
    }

    private final ConditionParser conditionManager = new ConditionParser();

    public ConditionParser getConditionManager() {
        return conditionManager;
    }

    //region Accounts data
    public void addAccount(Account account) {
        accounts.put(account.getId(), account);
    }

    public Account ensureAccountLoaded(int id) {
        Account account = accounts.get(id);
        if (account == null) {
            logger.info("Loading account #{}", id);
            DatabaseManager.get(AccountData.class).load(id);
        }
        return accounts.get(id);
    }

    public Collection<Account> getAccounts() {
        return accounts.values();
    }

    public List<Account> getAccountsByIp(String ip) {
        List<Account> accounts = new ArrayList<>();
        this.accounts.values().stream().filter(account -> account != null && account.getLastIP() != null &&
                account.getLastIP().equalsIgnoreCase(ip)).forEach(accounts::add);
        return accounts;
    }

    public Account getAccountByPseudo(String pseudo) {
        for (Account account : accounts.values())
            if (account.getPseudo().equals(pseudo))
                return account;
        return null;
    }
    //endregion

    //region Players data
    public Collection<Player> getPlayers() {
        return players.values();
    }

    public void addPlayer(Player player) {
        players.put(player.getId(), player);
    }

    public Player getPlayerByName(String name) {
        for (Player player : players.values())
            if (player.getName().equalsIgnoreCase(name))
                return player;
        return null;
    }

    public Player getPlayer(int id) {
        return players.get(id);
    }

    public List<Player> getOnlinePlayers() {
        return players.values().stream().filter(player -> player.isOnline() && player.getGameClient() != null).collect(Collectors.toList());
    }
    //endregion

    //region Maps data
    public Collection<GameMap> getMaps() {
        return maps.values();
    }

    public void addMapData(MapData map) {
        mapsData.put(map.id, map);

        // Update zaap list
        if (map instanceof ScriptMapData) {
            Integer c = ((ScriptMapData) map).zaapCell;

            if (c == null || c < 0) {
                Constant.ZAAPS.remove(map.id);
            } else {
                Constant.ZAAPS.put(map.id, c);
            }
        }

        // Make sure the subArea knows of that map
        Optional.ofNullable(subAreas.get(map.subAreaID)).ifPresent(s -> s.addMapID(map.id));
    }

    public Optional<MapData> getMapData(int id) {
        return Optional.ofNullable(mapsData.get(id));
    }

    public GameMap getMap(int id) {
        // Atomically get or load map
        return maps.computeIfAbsent(id, mapID -> {
            Optional<MapData> data = getMapData(mapID);
            if(!data.isPresent()) throw new IllegalStateException(String.format("no data found for map #%d", mapID));
            return new GameMap(mapsData.get(mapID));
        });
    }

    //endregion

    //region Objects data
    public CopyOnWriteArrayList<GameObject> getGameObjects() {
        return new CopyOnWriteArrayList<>(objects.values());
    }

    public void addGameObject(GameObject gameObject) {
        if (gameObject != null && !this.objects.containsKey(gameObject.getGuid())) {
            objects.put(gameObject.getGuid(), gameObject);
        }
    }

    public GameObject getGameObject(int id) {
        GameObject object = objects.get(id);
        if (object == null) {
            object = DatabaseManager.get(ObjectData.class).load(id);
        }
        return object;
    }

    public void removeGameObject(int id) {
        if (objects.containsKey(id)) {
            ((ObjectData) DatabaseManager.get(ObjectData.class)).delete(this.getGameObject(id));
            objects.remove(id);
        }
    }
    //endregion

    public Map<Integer, Spell> getSpells() {
        return spells;
    }

    public Map<Integer, ObjectTemplate> getObjectsTemplates() {
        return ObjTemplates;
    }

    public Map<Integer, Mount> getMounts() {
        return Dragodindes;
    }

    public Map<Integer, Area> getAreas() {
        return areas;
    }

    public Map<Integer, SubArea> getSubAreas() {
        return subAreas;
    }

    public Map<Integer, Guild> getGuilds() {
        return Guildes;
    }

    public Map<Integer, MountPark> getMountparks() {
        return MountPark;
    }

    public Map<Integer, Trunk> getTrunks() {
        return Trunks;
    }

    public Map<Integer, Collector> getCollectors() {
        return collectors;
    }

    public Map<Integer, House> getHouses() {
        return Houses;
    }

    public Map<Integer, Prism> getPrisms() {
        return Prismes;
    }

    public Map<Integer, Map<String, Map<String, Integer>>> getExtraMonsters() {
        return extraMonstre;
    }


    public void loadScripts() {
        logger.debug("Loading script engine");
        try {
            DataScriptVM.init();
        } catch (Exception e) {
            logger.error("init NpcScriptVM failed", e);
            throw new RuntimeException("init NpcScriptVM failed", e);
        }
    }

    public void createWorld() {
        logger.info("Loading of data..");
        long time = System.currentTimeMillis();

        DatabaseManager.get(ServerData.class).loadFully();
        logger.debug("The reset of the logged players were done successfully.");

        DatabaseManager.get(AdData.class).loadFully();
        logger.debug("The ads were loaded successfully.");

        DatabaseManager.get(FullMorphData.class).loadFully();
        logger.debug("The incarnations were loaded successfully.");

        DatabaseManager.get(ExtraMonsterData.class).loadFully();
        logger.debug("The extra-monsters were loaded successfully.");

        DatabaseManager.get(SpellData.class).loadFully();
        logger.debug("The spells were loaded successfully.");

        DatabaseManager.get(MonsterData.class).loadFully();
        logger.debug("The monsters were loaded successfully.");

        DatabaseManager.get(ObjectTemplateData.class).loadFully();
        logger.debug("The template objects were loaded successfully.");

        DatabaseManager.get(PrismData.class).loadFully();
        logger.debug("The prisms were loaded successfully.");

        DatabaseManager.get(BaseAreaData.class).loadFully();
        logger.debug("The statics areas data were loaded successfully.");
        DatabaseManager.get(AreaData.class).loadFully();
        logger.debug("The dynamics areas data were loaded successfully.");

        DatabaseManager.get(BaseSubAreaData.class).loadFully();
        logger.debug("The statics sub-areas data were loaded successfully.");
        DatabaseManager.get(SubAreaData.class).loadFully();
        logger.debug("The dynamics sub-areas data were loaded successfully.");

        DatabaseManager.get(CraftData.class).loadFully();
        logger.debug("The crafts were loaded successfully.");

        DatabaseManager.get(JobData.class).loadFully();
        logger.debug("The jobs were loaded successfully.");

        DatabaseManager.get(ObjectSetData.class).loadFully();
        logger.debug("The panoplies were loaded successfully.");

//        DatabaseManager.get(ScriptedCellData.class).loadFully();
//        logger.debug("The scripted cells were loaded successfully.");
//
//        DatabaseManager.get(EndFightActionData.class).loadFully();
//        logger.debug("The end fight actions were loaded successfully.");
//
//        DatabaseManager.get(NpcData.class).loadFully();
//        logger.debug("The placement of non-player character were done successfully.");

        DatabaseManager.get(ObjectActionData.class).loadFully();
        logger.debug("The action of objects were loaded successfully.");

        DatabaseManager.get(DropData.class).loadFully();
        logger.debug("The drops were loaded successfully.");

        logger.debug("The mounts were loaded successfully.");

        DatabaseManager.get(GuildMemberData.class).loadFully();
        logger.debug("The guilds and guild members were loaded successfully.");

        DatabaseManager.get(PetData.class).loadFully();
        logger.debug("The pets were loaded successfully.");

        DatabaseManager.get(PetTemplateData.class).loadFully();
        logger.debug("The templates of pets were loaded successfully.");

        DatabaseManager.get(BaseMountParkData.class).loadFully();
        logger.debug("The statics parks of the mounts were loaded successfully.");
        DatabaseManager.get(MountParkData.class).loadFully();
        logger.debug("The dynamics parks of the mounts were loaded successfully.");

        DatabaseManager.get(CollectorData.class).loadFully();
        logger.debug("The collectors were loaded successfully.");

        DatabaseManager.get(BaseHouseData.class).loadFully();
        logger.debug("The statics houses were loaded successfully.");
        DatabaseManager.get(HouseData.class).loadFully();
        logger.debug("The dynamics houses were loaded successfully.");

        DatabaseManager.get(BaseTrunkData.class).loadFully();
        logger.debug("The statics trunks were loaded successfully.");
        DatabaseManager.get(TrunkData.class).loadFully();
        logger.debug("The dynamics trunks were loaded successfully.");

        DatabaseManager.get(ZaapiData.class).loadFully();
        logger.debug("The zappys were loaded successfully.");

        DatabaseManager.get(ChallengeData.class).loadFully();
        logger.debug("The challenges were loaded successfully.");

        DatabaseManager.get(HdvData.class).loadFully();
        logger.debug("The hotels of sales were loaded successfully.");

        DatabaseManager.get(BigStoreListingData.class).loadFully();
        logger.debug("The objects of hotels were loaded successfully.");

        DatabaseManager.get(DungeonData.class).loadFully();
        logger.debug("The dungeons were loaded successfully.");

        DatabaseManager.get(RuneData.class).loadFully();
        logger.debug("The runes were loaded successfully.");

//        loadExtraMonster();
//        logger.debug("The adding of extra-monsters on the maps were done successfully.");

//        loadMonsterOnMap();
//        logger.debug("The adding of mobs groups on the maps were done successfully.");

//        DatabaseManager.get(GangsterData.class).loadFully();
//        logger.debug("The adding of gangsters on the maps were done successfully.");

//        logger.debug("Initialization of the dungeons : Dragon Pig.");
//        PigDragon.initialize();
//        logger.debug("Initialization of the dungeons : Labyrinth of the Minotoror.");
//        Minotoror.initialize();

        // Load auction
        DatabaseManager.get(AuctionData.class).loadFully();
        logger.debug("Initialization and loading auction : ok.");

        DatabaseManager.get(ShopObjectData.class).loadFully();
        logger.debug("Initialization and loading shop objects : ok.");

        ((ServerData) DatabaseManager.get(ServerData.class)).update(time);
        logger.info("All data was loaded successfully at "
                + new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.FRANCE).format(new Date()) + " in "
                + new SimpleDateFormat("mm", Locale.FRANCE).format((System.currentTimeMillis() - time)) + " min "
                + new SimpleDateFormat("ss", Locale.FRANCE).format((System.currentTimeMillis() - time)) + " s.");
        logger.setLevel(Level.ALL);
    }

    public void addExtraMonster(int idMob, String superArea, String subArea, int chances) {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, Integer> _map = new HashMap<>();
        _map.put(subArea, chances);
        map.put(superArea, _map);
        extraMonstre.put(idMob, map);
    }

    public Map<Integer, GameMap> getExtraMonsterOnMap() {
        return extraMonstreOnMap;
    }

    public void loadExtraMonster() {
        ArrayList<GameMap> mapPossible = new ArrayList<>();
        for (Entry<Integer, Map<String, Map<String, Integer>>> i : extraMonstre.entrySet()) {
            try {
                Map<String, Map<String, Integer>> map = i.getValue();

                for (Entry<String, Map<String, Integer>> areaChances : map.entrySet()) {
                    Integer chances = null;
                    for (Entry<String, Integer> _e : areaChances.getValue().entrySet()) {
                        Integer _c = _e.getValue();
                        if (_c != null && _c != -1)
                            chances = _c;
                    }
                    if (!areaChances.getKey().equals("")) {// Si la superArea n'est pas null
                        for (String ar : areaChances.getKey().split(",")) {
                            Area Area = areas.get(Integer.parseInt(ar));
                            for (GameMap Map : Area.getMaps()) {
                                if (Map == null)
                                    continue;
                                if (Map.haveMobFix())
                                    continue;
                                if (!Map.isPossibleToPutMonster())
                                    continue;

                                if (chances != null)
                                    Map.addMobExtra(i.getKey(), chances);
                                else if (!mapPossible.contains(Map))
                                    mapPossible.add(Map);
                            }
                        }
                    }
                    if (areaChances.getValue() != null) // Si l'area n'est pas null
                    {
                        for (Entry<String, Integer> area : areaChances.getValue().entrySet()) {
                            String areas = area.getKey();
                            for (String sub : areas.split(",")) {
                                SubArea subArea = null;
                                try {
                                    subArea = subAreas.get(Integer.parseInt(sub));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (subArea == null)
                                    continue;
                                for (GameMap Map : subArea.getMaps()) {
                                    if (Map == null)
                                        continue;
                                    if (Map.haveMobFix())
                                        continue;
                                    if (!Map.isPossibleToPutMonster())
                                        continue;

                                    if (chances != null)
                                        Map.addMobExtra(i.getKey(), chances);
                                    if (!mapPossible.contains(Map))
                                        mapPossible.add(Map);
                                }
                            }
                        }
                    }
                }
                if (mapPossible.size() <= 0) {
                    throw new Exception(" no maps was found for the extra monster " + i.getKey() + ".");
                } else {
                    GameMap randomMap;
                    if (mapPossible.size() == 1)
                        randomMap = mapPossible.get(0);
                    else
                        randomMap = mapPossible.get(Formulas.getRandomValue(0, mapPossible.size() - 1));
                    if (randomMap == null)
                        throw new Exception("the random map is null.");
                    if (getMonstre(i.getKey()) == null)
                        throw new Exception("the monster template of the extra monster is invalid (id : " + i.getKey() + ").");
                    if (randomMap.loadExtraMonsterOnMap(i.getKey()))
                        extraMonstreOnMap.put(i.getKey(), randomMap);
                    else
                        throw new Exception("a empty mobs group or invalid monster.");
                }

                mapPossible.clear();
            } catch (Exception e) {
                e.printStackTrace();
                mapPossible.clear();
                logger.error("An error occurred when the server try to put extra-monster caused by : " + e.getMessage());
            }
        }
    }

    public Map<String, String> getGroupFix(int map, int cell) {
        return mobsGroupsFix.get(map + ";" + cell);
    }

    public void addGroupFix(String str, String mob, int Time) {
        mobsGroupsFix.put(str, new HashMap<>());
        mobsGroupsFix.get(str).put("groupData", mob);
        mobsGroupsFix.get(str).put("timer", Time + "");
    }

    public void loadMonsterOnMap() {
        DatabaseManager.get(HeroicMobsGroupsData.class).loadFully();
        ((HeroicMobsGroupsData) DatabaseManager.get(HeroicMobsGroupsData.class)).loadFix();

        maps.values().stream().filter(Objects::nonNull).forEach(map -> {
            try {
                map.loadMonsterOnMap();
            } catch (Exception e) {
                logger.error("An error occurred when the server try to put monster on the map id " + map.getId() + ".");
            }
        });
    }

    public Area getArea(int areaID) {
        return areas.get(areaID);
    }


    public SubArea getSubArea(int areaID) {
        return subAreas.get(areaID);
    }

    public void addArea(Area area) {
        areas.put(area.getId(), area);
    }


    public void addSubArea(SubArea SA) {
        subAreas.put(SA.getId(), SA);
    }

    public String getSousZoneStateString() {
        String str = "";
        boolean first = false;
        for (SubArea subarea : subAreas.values()) {
            if (!subarea.getConquerable())
                continue;
            if (first)
                str += "|";
            str += subarea.getId() + ";" + subarea.getAlignment();
            first = true;
        }
        return str;
    }

    public double getBalanceArea(Area area, int alignement) {
        int cant = 0;
        for (SubArea subarea : subAreas.values()) {
            if (subarea.getArea() == area
                    && subarea.getAlignment() == alignement)
                cant++;
        }
        if (cant == 0)
            return 0;
        return Math.rint((1000 * cant / (area.getSubAreas().size())) / 10);
    }

    public double getBalanceWorld(int alignement) {
        int cant = 0;
        for (SubArea subarea : subAreas.values()) {
            if (subarea.getAlignment() == alignement)
                cant++;
        }
        if (cant == 0)
            return 0;
        return Math.rint((10 * cant / 4) / 10);
    }

    public double getConquestBonus(Player player) {
        if (player == null) return 1;
        if (player.getAlignment() == 0) return 1;
        final double factor = 1 + (getBalanceWorld(player.getAlignment()) * Math.rint((player.getGrade() / 2.5) + 1)) / 100;
        if (factor < 1) return 1;
        return factor;
    }

    public void registerObjectTemplate(InteractiveObjectTemplate t) {
        synchronized (this.interactiveObjects) {
            this.interactiveObjects.put(t.getId(), t);
        }
    }

    public void setObjectForSprites(Map<Integer, Integer> map) {
        synchronized (this.spriteToObject){
            this.spriteToObject.clear();
            this.spriteToObject.putAll(map);
        }
    }

    public Optional<Integer> getObjectIDForSprite(int spriteID) {
        synchronized (this.spriteToObject){
            return Optional.ofNullable(this.spriteToObject.get(spriteID));
        }
    }

    public Optional<InteractiveObjectTemplate> getObject(int objectID) {
        synchronized (interactiveObjects){
            return Optional.ofNullable(interactiveObjects.get(objectID));
        }
    }

    public Optional<InteractiveObjectTemplate> getObjectBySprite(int spriteID) {
        return getObjectIDForSprite(spriteID).flatMap(this::getObject);
    }

    public ExperienceTables getExperiences() {
        return experiences;
    }

    public void setExperiences(ExperienceTables t) {
        this.experiences = t;
    }

    public NpcTemplate getNPCTemplate(int guid) {
        return npcsTemplate.get(guid);
    }

    public void addNpcTemplate(NpcTemplate temp) {
        if (npcsTemplate.containsKey(temp.getId()) && temp.legacy == null) {
            Main.logger.trace("Overwriting npc template #{} with script", temp.getId());
        }
        npcsTemplate.put(temp.getId(), temp);
    }

    public void removePlayer(Player player) {
        if (player.getGuild() != null) {
            if (player.getGuild().getPlayers().size() <= 1) {
                removeGuild(player.getGuild().getId());
            } else if (player.getGuildMember().getRank() == 1) {
                int curMaxRight = 0;
                Player leader = null;

                for (Player newLeader : player.getGuild().getPlayers())
                    if (newLeader != player && newLeader.getGuildMember().getRights() < curMaxRight)
                        leader = newLeader;

                player.getGuild().removeMember(player);
                if (leader != null)
                    leader.getGuildMember().setRank(1);
            } else {
                player.getGuild().removeMember(player);
            }
        }
        if (player.getWife() != 0) {
            Player wife = getPlayer(player.getWife());

            if (wife != null) {
                wife.setWife(0);
            }
        }
        player.remove();
        unloadPerso(player.getId());
        players.remove(player.getId());
    }

    public void unloadPerso(Player perso) {
        unloadPerso(perso.getId());//UnLoad du perso+item
        players.remove(perso.getId());
    }

    public void addSort(Spell sort) {
        spells.put(sort.getId(), sort);
    }

    public Spell getSort(int id) {
        return spells.get(id);
    }

    public void addObjTemplate(ObjectTemplate obj) {
        ObjTemplates.put(obj.getId(), obj);
    }

    public ObjectTemplate getObjTemplate(int id) {
        return ObjTemplates.get(id);
    }

    public ArrayList<ObjectTemplate> getEtherealWeapons(int level) {
        ArrayList<ObjectTemplate> array = new ArrayList<>();
        final int levelMin = (level - 5 < 0 ? 0 : level - 5), levelMax = level + 5;
        getObjectsTemplates().values().stream().filter(objectTemplate -> objectTemplate != null && objectTemplate.getStrTemplate().contains("32c#")
                && (levelMin < objectTemplate.getLevel() && objectTemplate.getLevel() < levelMax) && objectTemplate.getType() != 93).forEach(array::add);
        return array;
    }

    public void addMobTemplate(int id, Monster mob) {
        MobTemplates.put(id, mob);
    }

    public Monster getMonstre(int id) {
        return MobTemplates.get(id);
    }

    public Collection<Monster> getMonstres() {
        return MobTemplates.values();
    }

    public String getStatOfAlign() {
        int ange = 0;
        int demon = 0;
        int total = 0;
        for (Player i : getPlayers()) {
            if (i == null)
                continue;
            if (i.getAlignment() == 1)
                ange++;
            if (i.getAlignment() == 2)
                demon++;
            total++;
        }
        ange = ange / total;
        demon = demon / total;
        if (ange > demon)
            return "Les Brâkmarien sont actuellement en minorité, je peux donc te proposer de rejoindre les rangs Brâkmarien ?";
        else if (demon > ange)
            return "Les Bontarien sont actuellement en minorité, je peux donc te proposer de rejoindre les rangs Bontarien ?";
        else
            return " Aucune milice est actuellement en minorité, je peux donc te proposer de rejoindre aléatoirement une milice ?";
    }

    public Mount getMountById(int id) {

        Mount mount = Dragodindes.get(id);
        if (mount == null) {
            ((MountData) DatabaseManager.get(MountData.class)).load(id);
            mount = Dragodindes.get(id);
        }
        return mount;
    }

    public void addMount(Mount mount) {
        Dragodindes.put(mount.getId(), mount);
    }

    public void removeMount(int id) {
        Dragodindes.remove(id);
    }

    public Collection<Job> getJobs() {
        return Jobs.values();
    }

    public Job getMetier(int id) {
        return Jobs.get(id);
    }

    public void addJob(Job metier) {
        Jobs.put(metier.getId(), metier);
    }

    public void addCraft(int id, ArrayList<Couple<Integer, Integer>> m) {
        Crafts.put(id, m);
    }

    public ArrayList<Couple<Integer, Integer>> getCraft(int i) {
        return Crafts.get(i);
    }

    public void addFullMorph(int morphID, String name, int gfxID,
                             String spells, String[] args) {
        if (fullmorphs.get(morphID) != null)
            return;

        fullmorphs.put(morphID, new HashMap<>());

        fullmorphs.get(morphID).put("name", name);
        fullmorphs.get(morphID).put("gfxid", gfxID + "");
        fullmorphs.get(morphID).put("spells", spells);
        if (args != null) {
            fullmorphs.get(morphID).put("vie", args[0]);
            fullmorphs.get(morphID).put("pa", args[1]);
            fullmorphs.get(morphID).put("pm", args[2]);
            fullmorphs.get(morphID).put("vitalite", args[3]);
            fullmorphs.get(morphID).put("sagesse", args[4]);
            fullmorphs.get(morphID).put("terre", args[5]);
            fullmorphs.get(morphID).put("feu", args[6]);
            fullmorphs.get(morphID).put("eau", args[7]);
            fullmorphs.get(morphID).put("air", args[8]);
            fullmorphs.get(morphID).put("initiative", args[9]);
            fullmorphs.get(morphID).put("stats", args[10]);
            fullmorphs.get(morphID).put("donjon", args[11]);
        }
    }

    public Map<String, String> getFullMorph(int morphID) {
        return fullmorphs.get(morphID);
    }

    public int getObjectByIngredientForJob(ArrayList<Integer> list,
                                           Map<Integer, Integer> ingredients) {
        if (list == null)
            return -1;
        for (int tID : list) {
            ArrayList<Couple<Integer, Integer>> craft = getCraft(tID);
            if (craft == null)
                continue;
            if (craft.size() != ingredients.size())
                continue;
            boolean ok = true;
            for (Couple<Integer, Integer> c : craft) {
                if (!((ingredients.get(c.first) + " ").equals(c.second + " "))) //si ingredient non pr�sent ou mauvaise quantit�
                    ok = false;
            }
            if (ok)
                return tID;
        }
        return -1;
    }


    public void addItemSet(ObjectSet itemSet) {
        ItemSets.put(itemSet.getId(), itemSet);
    }

    public ObjectSet getItemSet(int tID) {
        return ItemSets.get(tID);
    }

    public int getItemSetNumber() {
        return ItemSets.size();
    }

    public ArrayList<GameMap> getMapByPosInArray(int mapX, int mapY) {
        ArrayList<GameMap> i = new ArrayList<>();
        for (GameMap map : maps.values())
            if (map.getX() == mapX && map.getY() == mapY)
                i.add(map);
        return i;
    }

    public List<Integer> getMapIdByPosInSuperArea(int mapX, int mapY, int superArea) {
        return mapsData.values().stream()
                .filter(Objects::nonNull)
                .filter(map -> map.x == mapX && map.y == mapY)
                .filter(map -> Optional.ofNullable(map.getSubArea()).map(SubArea::getArea).map(Area::getSuperArea).orElse(-1) == superArea)
                .map(md -> md.id)
                .collect(Collectors.toList());
    }

    public void addGuild(Guild g) {
        Guildes.put(g.getId(), g);
    }

    public boolean guildNameIsUsed(String name) {
        for (Guild g : Guildes.values())
            if (g.getName().equalsIgnoreCase(name))
                return true;
        return false;
    }

    public boolean guildEmblemIsUsed(String emb) {
        for (Guild g : Guildes.values()) {
            if (g.getEmblem().equals(emb))
                return true;
        }
        return false;
    }

    public Guild getGuild(int i) {
        Guild guild = Guildes.get(i);
        if (guild == null) {
            ((GuildData) DatabaseManager.get(GuildData.class)).load(i);
            guild = Guildes.get(i);
        }
        return guild;
    }

    public int getGuildByName(String name) {
        for (Guild g : Guildes.values()) {
            if (g.getName().equalsIgnoreCase(name))
                return g.getId();
        }
        return -1;
    }

    public long getGuildXpMax(int lvl) {
        return experiences.guilds.maxXpAt(lvl);
    }

    public int getZaapCellIdByMapId(int i) {
        return Constant.ZAAPS.getOrDefault(i, -1);
    }

    public int getEncloCellIdByMapId(int i) {
        GameMap map = getMap(i);
        if (map != null && map.getMountPark() != null && map.getMountPark().getCell() > 0)
            return map.getMountPark().getCell();
        return -1;
    }

    public void delDragoByID(int getId) {
        Dragodindes.remove(getId);
    }

    public void removeGuild(int id) {
        ((GuildMemberData) DatabaseManager.get(GuildMemberData.class)).deleteAll(id);
        ((GuildData) DatabaseManager.get(GuildData.class)).delete(this.Guildes.get(id));
        this.getHouseManager().removeHouseGuild(id);
        GameMap.removeMountPark(id);
        Collector.removeCollector(id);
        Guildes.remove(id);
    }

    public void unloadPerso(int g) {
        Player toRem = players.get(g);
        if (!toRem.getItems().isEmpty())
            for (Entry<Integer, GameObject> curObj : toRem.getItems().entrySet())
                objects.remove(curObj.getKey());

    }

    public GameObject newObjet(int id, int template, int qua, int pos, String stats, int puit) {
        if (getObjTemplate(template) == null) {
            return null;
        }

        if (template == 8378) {
            return new Fragment(id, stats);
        } else if (getObjTemplate(template).isFilledSoulStone()) {
            return new SoulStone(id, qua, template, pos, stats);
        } else if (getObjTemplate(template).getType() == 24 && (Constant.isCertificatDopeuls(getObjTemplate(template).getId()) || getObjTemplate(template).getId() == 6653)) {
            try {
                Map<Integer, String> txtStat = new HashMap<>();
                txtStat.put(Constant.STATS_DATE, stats.substring(3) + "");
                return new GameObject(id, template, qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(false, null), new ArrayList<>(), new HashMap<>(), txtStat, puit);
            } catch (Exception e) {
                e.printStackTrace();
                return new GameObject(id, template, qua, pos, stats, 0);
            }
        } else {
            return new GameObject(id, template, qua, pos, stats, 0);
        }
    }

    public Map<Integer, Integer> getChangeHdv() {
        Map<Integer, Integer> changeHdv = new HashMap<>();
        changeHdv.put(8753, 8759); // HDV Annimaux
        changeHdv.put(4607, 4271); // HDV Alchimistes
        changeHdv.put(4622, 4216); // HDV Bijoutiers
        changeHdv.put(4627, 4232); // HDV Bricoleurs
        changeHdv.put(5112, 4178); // HDV B�cherons
        changeHdv.put(4562, 4183); // HDV Cordonniers
        changeHdv.put(8754, 8760); // HDV Biblioth�que
        changeHdv.put(5317, 4098); // HDV Forgerons
        changeHdv.put(4615, 4247); // HDV P�cheurs
        changeHdv.put(4646, 4262); // HDV Ressources
        changeHdv.put(8756, 8757); // HDV Forgemagie
        changeHdv.put(4618, 4174); // HDV Sculpteurs
        changeHdv.put(4588, 4172); // HDV Tailleurs
        changeHdv.put(8482, 10129); // HDV �mes
        changeHdv.put(4595, 4287); // HDV Bouchers
        changeHdv.put(4630, 2221); // HDV Boulangers
        changeHdv.put(5311, 4179); // HDV Mineurs
        changeHdv.put(4629, 4299); // HDV Paysans
        return changeHdv;
    }

    // Utilis� deux fois. Pour tous les modes HDV dans la fonction getHdv ci-dessous et dans le mode Vente de GameClient.java
    public int changeHdv(int map) {
        Map<Integer, Integer> changeHdv = getChangeHdv();
        if (changeHdv.containsKey(map)) {
            map = changeHdv.get(map);
        }
        return map;
    }

    public BigStore getHdv(int map) {
        return Hdvs.get(changeHdv(map));
    }

    public void addHdvItem(int compteID, int hdvID, BigStoreListing toAdd) {
        //Si le compte n'est pas dans la memoire
        hdvsItems.computeIfAbsent(compteID, k -> new HashMap<>()); //Ajout du compte cle:compteID et un nouveau Map<hdvID,items<>>
        hdvsItems.get(compteID).computeIfAbsent(hdvID, k -> new ArrayList<>()).add(toAdd);
    }

    public void removeHdvItem(int compteID, int hdvID, BigStoreListing toDel) {
        hdvsItems.get(compteID).get(hdvID).remove(toDel);
    }

    public void addHdv(BigStore toAdd) {
        Hdvs.put(toAdd.getHdvId(), toAdd);
    }

    public Map<Integer, List<BigStoreListing>> getMyItems(
            int compteID) {
        //Si le compte n'est pas dans la memoire
        hdvsItems.computeIfAbsent(compteID, k -> new HashMap<>());//Ajout du compte cl�:compteID et un nouveau Map<hdvID,items
        return hdvsItems.get(compteID);
    }

    public Collection<ObjectTemplate> getObjTemplates() {
        return ObjTemplates.values();
    }

    public void priestRequest(Player boy, Player girl, Player asked) {
        if (boy.getSexe() == 0 && girl.getSexe() == 1) {
            final GameMap map = boy.getCurMap();
            if (boy.getWife() != 0) {// 0 : femme | 1 = homme
                boy.setBlockMovement(false);
                SocketManager.GAME_SEND_MESSAGE_TO_MAP(map, boy.getName() + " est déjà marier !", "B9121B");
                return;
            }
            if (girl.getWife() != 0) {
                boy.setBlockMovement(false);
                SocketManager.GAME_SEND_MESSAGE_TO_MAP(map, girl.getName() + " est déjà marier !", "B9121B");
                return;
            }
            SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(map, "", -1, "Prêtre", "world.world.maried.pietre.ask.accept");
            SocketManager.GAME_SEND_WEDDING(617, (boy == asked ? boy.getId() : girl.getId()), (boy == asked ? girl.getId() : boy.getId()), -1);
        }
    }


    public void wedding(Player boy, Player girl, int isOK) {
        if (isOK > 0) {
            SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(boy.getCurMap(), "", -1, "Prêtre", "world.world.maried.pietre.ask.ok");
            boy.setWife(girl.getId());
            girl.setWife(boy.getId());
        } else {
            SocketManager.GAME_SEND_Im_PACKET_TO_MAP(boy.getCurMap(), "048;" + boy.getName() + "~" + girl.getName());
        }
        boy.setisOK(0);
        boy.setBlockMovement(false);
        girl.setisOK(0);
        girl.setBlockMovement(false);
    }

    public Optional<Animation> getAnimation(int AnimationId) {
        return Optional.ofNullable(animations.get(AnimationId));
    }

    public void addAnimation(Animation animation) {
        animations.put(animation.id, animation);
    }

    public void addHouse(House house) {
        Houses.put(house.getId(), house);
    }

    public House getHouse(int id) {
        return Houses.get(id);
    }

    public void addCollector(Collector Collector) {
        collectors.put(Collector.getId(), Collector);
    }

    public Collector getCollector(int CollectorID) {
        return collectors.get(CollectorID);
    }

    public void addTrunk(Trunk trunk) {
        Trunks.put(trunk.getId(), trunk);
    }

    public Trunk getTrunk(int id) {
        return Trunks.get(id);
    }

    public void addMountPark(MountPark mp) {
        MountPark.put(mp.getMap(), mp);
    }

    public Map<Integer, MountPark> getMountParks() {
        return MountPark;
    }

    public String parseMPtoGuild(int GuildID) {
        Guild G = getGuild(GuildID);
        byte enclosMax = (byte) Math.floor(G.getLvl() / 10);
        StringBuilder packet = new StringBuilder();
        packet.append(enclosMax);

        for (Entry<Integer, MountPark> mp : MountPark.entrySet()) {
            if (mp.getValue().getGuild() != null
                    && mp.getValue().getGuild().getId() == GuildID) {
                packet.append("|").append(mp.getValue().getMap()).append(";").append(mp.getValue().getSize()).append(";").append(mp.getValue().getMaxObject());// Nombre d'objets pour le dernier
                if (mp.getValue().getListOfRaising().size() > 0) {
                    packet.append(";");
                    boolean primero = false;
                    for (Integer id : mp.getValue().getListOfRaising()) {
                        Mount dd = getMountById(id);
                        if (dd != null) {
                            if (primero)
                                packet.append(",");
                            packet.append(dd.getColor()).append(",").append(dd.getName()).append(",");
                            if (getPlayer(dd.getOwner()) == null)
                                packet.append("Sans maitre");
                            else
                                packet.append(getPlayer(dd.getOwner()).getName());
                            primero = true;
                        }
                    }
                }
            }
        }
        return packet.toString();
    }

    public int totalMPGuild(int GuildID) {
        int i = 0;
        for (Entry<Integer, MountPark> mp : MountPark.entrySet())
            if (mp.getValue().getGuild() != null && mp.getValue().getGuild().getId() == GuildID)
                i++;
        return i;
    }

    public void addChallenge(String chal) {
        if (!Challenges.toString().isEmpty())
            Challenges.append(";");
        Challenges.append(chal);
    }

    public synchronized void addPrisme(Prism Prisme) {
        Prismes.put(Prisme.getId(), Prisme);
    }

    public Prism getPrisme(int id) {
        return Prismes.get(id);
    }

    public void removePrisme(int id) {
        Prismes.remove(id);
    }

    public Collection<Prism> AllPrisme() {
        if (Prismes.size() > 0)
            return Prismes.values();
        return null;
    }

    public String PrismesGeoposition(Player player, int alignement) {
        StringBuilder str = new StringBuilder();
        boolean first = false;
        int subareas = 0;

        for (SubArea subarea : subAreas.values()) {
            if (player.getCurMap() != null && player.getCurMap().getSubArea() != null)
                if (subarea.getArea().getSuperArea() != player.getCurMap().getSubArea().getArea().getSuperArea())
                    continue;
            if (!subarea.getConquerable())
                continue;
            if (first)
                str.append(";");
            str.append(subarea.getId()).append(",").append(subarea.getAlignment() == 0 ? -1 : subarea.getAlignment()).append(",0,");
            if (subarea.getPrism() == null)
                str.append(0 + ",1");
            else
                str.append(subarea.getPrism().getMap()).append(",1");
            first = true;
            subareas++;
        }
        if (alignement == 1) str.append("|").append(Area.bontarians);
        else if (alignement == 2) str.append("|").append(Area.brakmarians);

        str.append("|").append(areas.size()).append("|");
        first = false;
        for (Area area : areas.values()) {
            if (area.getAlignement() == 0)
                continue;
            if (first)
                str.append(";");
            str.append(area.getId()).append(",").append(area.getAlignement()).append(",1,").append(area.getPrismId() == 0 ? 0 : 1);
            first = true;
        }

        Map<Integer, Long> factionAreaCounts = subAreaCountByFaction();
        long bontaAndBrakCount = factionAreaCounts.getOrDefault(Constant.ALIGNEMENT_BONTARIEN, 0L) + factionAreaCounts.getOrDefault(Constant.ALIGNEMENT_BRAKMARIEN, 0L);
        if (alignement == 1)
            str.insert(0, Area.bontarians + "|" + subareas + "|"
                    + (subareas - (bontaAndBrakCount)) + "|");
        else if (alignement == 2)
            str.insert(0, Area.brakmarians + "|" + subareas + "|"
                    + (subareas - (bontaAndBrakCount)) + "|");
        return str.toString();
    }

    public Map<Integer, Long> subAreaCountByFaction() {
        return subAreas.values().stream().collect(Collectors.groupingBy(SubArea::getAlignment, Collectors.counting()));
    }

    public void showPrismes(Player perso) {
        for (SubArea subarea : subAreas.values()) {
            if (subarea.getAlignment() == 0)
                continue;
            SocketManager.GAME_SEND_am_ALIGN_PACKET_TO_SUBAREA(perso, subarea.getId()
                    + "|" + subarea.getAlignment() + "|1");
        }
    }

    public synchronized int getNextIDPrisme() {
        int max = -102;
        for (int a : Prismes.keySet())
            if (a < max)
                max = a;
        return max - 3;
    }

    public void addPets(Pet pets) {
        Pets.put(pets.getTemplateId(), pets);
    }

    public Pet getPets(int Tid) {
        return Pets.get(Tid);
    }

    public Collection<Pet> getPets() {
        return Pets.values();
    }

    public void addPetsEntry(PetEntry pets) {
        PetsEntry.put(pets.getObjectId(), pets);
    }

    public PetEntry getPetsEntry(int guid) {
        return PetsEntry.get(guid);
    }

    public PetEntry removePetsEntry(int guid) {
        return PetsEntry.remove(guid);
    }

    public String getChallengeFromConditions(boolean sevEnn,
                                             boolean sevAll, boolean bothSex, boolean EvenEnn, boolean MoreEnn,
                                             boolean hasCaw, boolean hasChaf, boolean hasRoul, boolean hasArak,
                                             int isBoss, boolean ecartLvlPlayer, boolean hasArround,
                                             boolean hasIndirectDamage, boolean isSolo) {
        StringBuilder toReturn = new StringBuilder();
        boolean isFirst = true, isGood = false;
        int cond;

        for (String chal : Challenges.toString().split(";")) {
            if (!isFirst && isGood)
                toReturn.append(";");
            isGood = true;
            int id = Integer.parseInt(chal.split(",")[0]);
            cond = Integer.parseInt(chal.split(",")[4]);
            //Necessite plusieurs ennemis
            if (((cond & 1) == 1) && !sevEnn)
                isGood = false;
            //Necessite plusieurs allies
            if ((((cond >> 1) & 1) == 1) && !sevAll)
                isGood = false;
            //Necessite les deux sexes
            if ((((cond >> 2) & 1) == 1) && !bothSex)
                isGood = false;
            //Necessite un nombre pair d'ennemis
            if ((((cond >> 3) & 1) == 1) && !EvenEnn)
                isGood = false;
            //Necessite plus d'ennemis que d'allies
            if ((((cond >> 4) & 1) == 1) && !MoreEnn)
                isGood = false;
            //Jardinier
            if (!hasCaw && (id == 7))
                isGood = false;
            //Fossoyeur
            if (!hasChaf && (id == 12))
                isGood = false;
            //Casino Royal
            if (!hasRoul && (id == 14))
                isGood = false;
            //Araknophile
            if (!hasArak && (id == 15))
                isGood = false;
            //Les mules d'abord
            if (!ecartLvlPlayer && (id == 48))
                isGood = false;
            //Contre un boss de donjon
            if (isBoss != -1 && id == 5)
                isGood = false;
            //Hardi
            if (!hasArround && id == 36)
                isGood = false;
            //Mains propre
            if (!hasIndirectDamage && id == 19)
                isGood = false;

            switch (id) {
                case 47:
                case 46:
                case 45:
                case 44:
                    if (isSolo)
                        isGood = false;
                    break;
            }

            switch (isBoss) {
                case 1045://Kimbo
                    switch (id) {
                        case 37:
                        case 8:
                        case 1:
                        case 2:
                            isGood = false;
                            break;
                    }
                    break;
                case 1072://Tynril
                case 1085://Tynril
                case 1086://Tynril
                case 1087://Tynril
                    switch (id) {
                        case 36:
                        case 20:
                            isGood = false;
                            break;
                    }
                    break;
                case 1071://Rasboul Majeur
                    switch (id) {
                        case 9:
                        case 22:
                        case 17:
                        case 47:
                            isGood = false;
                            break;
                    }
                    break;
                case 780://Skeunk
                    switch (id) {
                        case 35:
                        case 25:
                        case 4:
                        case 32:
                        case 3:
                        case 31:
                        case 34:
                            isGood = false;
                            break;
                    }
                    break;
                case 113://DC
                    switch (id) {
                        case 12:
                        case 15:
                        case 7:
                        case 41:
                            isGood = false;
                            break;
                    }
                    break;
                case 612://Maitre pandore
                    switch (id) {
                        case 20:
                        case 37:
                            isGood = false;
                            break;
                    }
                    break;
                case 478://Bworker
                case 568://Tanukoui san
                case 940://Rat blanc
                    switch (id) {
                        case 20:
                            isGood = false;
                            break;
                    }
                    break;
                case 1188://Blop multi
                    switch (id) {
                        case 20:
                        case 46:
                        case 44:
                            isGood = false;
                            break;
                    }
                    break;

                case 865://Grozila
                case 866://Grasmera
                    switch (id) {
                        case 31:
                        case 32:
                            isGood = false;
                            break;
                    }
                    break;

            }
            if (isGood)
                toReturn.append(chal);
            isFirst = false;
        }
        return toReturn.toString();
    }

    public void verifyClone(Player p) {
        if (p.getCurCell() != null && p.getFight() == null) {
            if (p.getCurCell().getPlayers().contains(p)) {
                p.getCurCell().removePlayer(p);
                ((PlayerData) DatabaseManager.get(PlayerData.class)).update(p);
            }
        }
        if (p.isOnline())
            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(p);
    }

    public ArrayList<String> getRandomChallenge(int nombreChal,
                                                String challenges) {
        String MovingChals = ";1;2;8;36;37;39;40;";// Challenges de d�placements incompatibles
        boolean hasMovingChal = false;
        String TargetChals = ";3;4;10;25;31;32;34;35;38;42;";// ceux qui ciblent
        boolean hasTargetChal = false;
        String SpellChals = ";5;6;9;11;19;20;24;41;";// ceux qui obligent � caster sp�cialement
        boolean hasSpellChal = false;
        String KillerChals = ";28;29;30;44;45;46;48;";// ceux qui disent qui doit tuer
        boolean hasKillerChal = false;
        String HealChals = ";18;43;";// ceux qui emp�chent de soigner
        boolean hasHealChal = false;

        int compteur = 0, i;
        ArrayList<String> toReturn = new ArrayList<>();
        String chal;
        while (compteur < 100 && toReturn.size() < nombreChal) {
            compteur++;
            i = Formulas.getRandomValue(1, challenges.split(";").length);
            chal = challenges.split(";")[i - 1];// challenge au hasard dans la liste

            if (!toReturn.contains(chal))// si le challenge n'y etait pas encore
            {
                if (MovingChals.contains(";" + chal.split(",")[0] + ";"))// s'il appartient a une liste
                    if (!hasMovingChal)// et qu'aucun de la liste n'a ete choisi deja
                    {
                        hasMovingChal = true;
                        toReturn.add(chal);
                        continue;
                    } else
                        continue;
                if (TargetChals.contains(";" + chal.split(",")[0] + ";"))
                    if (!hasTargetChal) {
                        hasTargetChal = true;
                        toReturn.add(chal);
                        continue;
                    } else
                        continue;
                if (SpellChals.contains(";" + chal.split(",")[0] + ";"))
                    if (!hasSpellChal) {
                        hasSpellChal = true;
                        toReturn.add(chal);
                        continue;
                    } else
                        continue;
                if (KillerChals.contains(";" + chal.split(",")[0] + ";"))
                    if (!hasKillerChal) {
                        hasKillerChal = true;
                        toReturn.add(chal);
                        continue;
                    } else
                        continue;
                if (HealChals.contains(";" + chal.split(",")[0] + ";"))
                    if (!hasHealChal) {
                        hasHealChal = true;
                        toReturn.add(chal);
                        continue;
                    } else
                        continue;
                toReturn.add(chal);
            }
            compteur++;
        }
        return toReturn;
    }

    public Collector getCollectorByMap(int id) {

        for (Entry<Integer, Collector> Collector : getCollectors().entrySet()) {
            GameMap map = getMap(Collector.getValue().getMap());
            if (map.getId() == id) {
                return Collector.getValue();
            }
        }
        return null;
    }

    public void addSeller(Player player) {
        if (player.getStoreItems().isEmpty())
            return;

        int map = player.getCurMap().getId();

        if (Seller.get(map) == null) {
            ArrayList<Integer> players = new ArrayList<>();
            players.add(player.getId());
            Seller.put(map, players);
        } else {
            ArrayList<Integer> players = new ArrayList<>();
            players.add(player.getId());
            players.addAll(Seller.get(map));
            Seller.remove(map);
            Seller.put(map, players);
        }
    }

    public Collection<Integer> getSeller(int map) {
        return Seller.get(map);
    }

    public void removeSeller(int player, int map) {
        if (getSeller(map) != null)
            Seller.get(map).remove(player);
    }

    public double getTauxObtentionIntermediaire(double bonus, boolean b1, boolean b2) {
        double taux = bonus;
        // 100.0 + 2*(30.0 + 2*10.0) => true true
        // 30.0 + 2*(10.0 + 2*3.0) => true false
        // 10.0 + 2*(3.0 + 2*1.0) => true true
        if (b1) {
            if (bonus == 100.0)
                taux += 2.0 * getTauxObtentionIntermediaire(30.0, true, b2);
            if (bonus == 30.0)
                taux += 2.0 * getTauxObtentionIntermediaire(10.0, (!b2), b2); // Si b2 est false alors on calculera 2*3.0 dans 10.0
            if (bonus == 10.0)
                taux += 2.0 * getTauxObtentionIntermediaire(3.0, (b2), b2); // Si b2 est true alors on calculera apr�s
            else if (bonus == 3.0)
                taux += 2.0 * getTauxObtentionIntermediaire(1.0, false, b2);
        }

        return taux;
    }

    public int getMetierByMaging(int idMaging) {
        int mId = -1;
        switch (idMaging) {
            case 43: // FM Dagues
                mId = 17;
                break;
            case 44: // FM Ep�es
                mId = 11;
                break;
            case 45: // FM Marteaux
                mId = 14;
                break;
            case 46: // FM Pelles
                mId = 20;
                break;
            case 47: // FM Haches
                mId = 31;
                break;
            case 48: // FM Arcs
                mId = 13;
                break;
            case 49: // FM Baguettes
                mId = 19;
                break;
            case 50: // FM B�tons
                mId = 18;
                break;
            case 62: // Cordo
                mId = 15;
                break;
            case 63: // Jaillo
                mId = 16;
                break;
            case 64: // Costu
                mId = 27;
                break;
        }
        return mId;
    }

    public int getTempleByClasse(int classe) {
        int temple = -1;
        switch (classe) {
            case Constant.CLASS_FECA: // f�ca
                temple = 1554;
                break;
            case Constant.CLASS_OSAMODAS: // osa
                temple = 1546;
                break;
            case Constant.CLASS_ENUTROF: // �nu
                temple = 1470;
                break;
            case Constant.CLASS_SRAM: // sram
                temple = 6926;
                break;
            case Constant.CLASS_XELOR: // xelor
                temple = 1469;
                break;
            case Constant.CLASS_ECAFLIP: // �ca
                temple = 1544;
                break;
            case Constant.CLASS_ENIRIPSA: // �ni
                temple = 6928;
                break;
            case Constant.CLASS_IOP: // iop
                temple = 1549;
                break;
            case Constant.CLASS_CRA: // cra
                temple = 1558;
                break;
            case Constant.CLASS_SADIDA: // sadi
                temple = 1466;
                break;
            case Constant.CLASS_SACRIEUR: // sacri
                temple = 6949;
                break;
            case Constant.CLASS_PANDAWA: // panda
                temple = 8490;
                break;
        }
        return temple;
    }

    public void sendMessageToAll(String key, Object... str) {
        TimerWaiter.addNext(() -> World.world.getOnlinePlayers().stream()
                        .filter(player -> player != null && player.getGameClient() != null && player.isOnline() && player.getLang() != null)
                        .forEach(player -> player.sendMessage(player.getLang().trans(key, str))),
                0, TimeUnit.SECONDS);
    }

    @Override
    public SWorld scripted() {
        return scriptVal;
    }

    public Account getAccount(int id) {
        return accounts.get(id);
    }


    public static class Drop {
        private final int objectId;
        private final int ceil;
        private final int action;
        private final int level;
        private final String condition;
        private ArrayList<Double> percents;
        private double localPercent;

        public Drop(int objectId, ArrayList<Double> percents, int ceil, int action, int level, String condition) {
            this.objectId = objectId;
            this.percents = percents;
            this.ceil = ceil;
            this.action = action;
            this.level = level;
            this.condition = condition;
        }

        public Drop(int objectId, double percent, int ceil) {
            this.objectId = objectId;
            this.localPercent = percent;
            this.ceil = ceil;
            this.action = -1;
            this.level = -1;
            this.condition = "";
        }

        public int getObjectId() {
            return objectId;
        }

        public int getCeil() {
            return ceil;
        }

        public int getAction() {
            return action;
        }

        public int getLevel() {
            return level;
        }

        public String getCondition() {
            return condition;
        }

        public double getLocalPercent() {
            return localPercent;
        }

        public Drop copy(int grade) {
            Drop drop = new Drop(this.objectId, null, this.ceil, this.action, this.level, this.condition);
            if (this.percents == null) return null;
            if (this.percents.isEmpty()) return null;
            try {
                if (this.percents.get(grade - 1) == null) return null;
                drop.localPercent = this.percents.get(grade - 1);
            } catch (IndexOutOfBoundsException ignored) {
                return null;
            }
            return drop;
        }
    }

    public static class Couple<L, R> {
        public L first;
        public R second;

        public Couple(L s, R i) {
            this.first = s;
            this.second = i;
        }

        public L getFirst() { return first; }
        public R getSecond() { return second; }
    }

}
