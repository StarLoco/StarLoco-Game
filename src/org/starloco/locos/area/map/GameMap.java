package org.starloco.locos.area.map;

import org.starloco.locos.area.Area;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.entity.InteractiveDoor;
import org.starloco.locos.area.map.entity.MountPark;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Party;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.MountParkData;
import org.starloco.locos.database.data.login.MountData;
import org.starloco.locos.entity.Collector;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.entity.npc.Npc;
import org.starloco.locos.entity.npc.NpcMovable;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.game.scheduler.Updatable;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.*;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.other.Action;
import org.starloco.locos.util.TimerWaiter;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class GameMap {

    public static final Map<String, ArrayList<GameObject>> fixMobGroupObjects = new HashMap<>();
    public static final Updatable updatable = new Updatable(30000) {
        private final ArrayList<RespawnGroup> groups = new ArrayList<>();

        @Override
        public void update() {
            if(!this.groups.isEmpty()) {
                long time = System.currentTimeMillis(), random = Formulas.getRandomValue(120000, 300000);

                for(RespawnGroup respawnGroup : new ArrayList<>(this.groups)) {
                    if(respawnGroup == null) continue;
                    if(respawnGroup.cell != -1) {
                        Map<String, String> data = World.world.getGroupFix(respawnGroup.map.id, respawnGroup.cell);

                        if(data != null && time - respawnGroup.lastTime > Long.parseLong(data.get("timer"))) {
                            respawnGroup.map.addStaticGroup(respawnGroup.cell, data.get("groupData"), true);
                            this.groups.remove(respawnGroup);
                        }
                    } else if(time - respawnGroup.lastTime > random) {
                        respawnGroup.map.spawnGroup(-1, 1, true, -1);
                        this.groups.remove(respawnGroup);
                    }
                }
            }

            if(this.verify()) {
                if(Config.autoReboot) {
                    if (Reboot.check()) {
                        if ((System.currentTimeMillis() - Config.startTime) > 60000) {
                            for (Player player : World.world.getOnlinePlayers()) {
                                if(player.getFight() != null)
                                    player.getFight().endFight((byte) 0);
                                player.send(this.toString());
                            }
                            try { Thread.sleep(5000); } catch (Exception ignored) {}
                            Main.stop("Automatic restart");
                            return;
                        }
                    }
                }

                TimerWaiter.addNext(() -> {
                    final List<GameMap> mapsAlreadyMoved = new ArrayList<>();
                    for (Player player : World.world.getOnlinePlayers()) {
                        GameMap map = player.getCurMap();
                        if (map != null && !mapsAlreadyMoved.contains(map)) {
                            map.onMapMonsterDeplacement();
                            mapsAlreadyMoved.add(map);
                        }
                    }

                    for(Mount mount : World.world.getMounts().values()) {
                        GameMap map = World.world.getMap(mount.getMapId());
                        if (map != null && !mapsAlreadyMoved.contains(map) && map.getMountPark() != null) {
                            map.getMountPark().startMoveMounts();
                            mapsAlreadyMoved.add(map);
                        }
                    }

                    World.world.getCollectors().values().forEach(Collector::moveOnMap);
                }, 1, TimeUnit.SECONDS);

                NpcMovable.moveAll();
            }
        }

        @Override
        public ArrayList<RespawnGroup> get() {
            return groups;
        }
    };

    public int nextObjectId = -1;
    public boolean noSellers = false, noCollectors = false, noPrisms = false, noTp = false, noDefy = false, noAgro = false, noCanal = false;
    private short id;
    private String date, key, placesStr;
    private byte w, h, X = 0, Y = 0, maxGroup = 3, maxSize, minSize, fixSize;
    private int maxTeam = 0;
    private boolean isMute = false;
    private SubArea subArea;
    private MountPark mountPark;
    private CellCacheImpl cellCache;
    private List<GameCase> cases = new ArrayList<>();
    private List<Fight> fights = new ArrayList<>();
    private ArrayList<Monster.MobGrade> mobPossibles = new ArrayList<>();
    private Map<Integer, Monster.MobGroup> mobGroups = new HashMap<>();
    private Map<Integer, Monster.MobGroup> fixMobGroups = new HashMap<>();
    private Map<Integer, Npc> npcs = new HashMap<>();
    private Map<Integer, ArrayList<Action>> endFightAction = new HashMap<>();
    private Map<Integer, Integer> mobExtras = new HashMap<>();

    public GameMap(short id, String date, byte w, byte h, String key, String places, String dData, String monsters, String mapPos, byte maxGroup, byte fixSize, byte minSize, byte maxSize, String forbidden, byte sniffed) {
        this.id = id;
        this.date = date;
        this.w = w;
        this.h = h;
        this.key = key;
        this.placesStr = places;
        this.maxGroup = maxGroup;
        this.maxSize = maxSize;
        this.minSize = minSize;
        this.fixSize = fixSize;
        this.cases = World.world.getCryptManager().decompileMapData(this, dData, sniffed);

        try {
            if (!places.equalsIgnoreCase("") && !places.equalsIgnoreCase("|"))
                this.maxTeam = (places.split("\\|")[1].length() / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String[] mapInfos = mapPos.split(",");
            this.X = Byte.parseByte(mapInfos[0]);
            this.Y = Byte.parseByte(mapInfos[1]);
            int subArea = Integer.parseInt(mapInfos[2]);

            if (subArea == 0 && id == 32) {
                this.subArea = World.world.getSubArea(subArea);
                if (this.subArea != null) this.subArea.addMap(this);
            } else if (subArea != 0) {
                this.subArea = World.world.getSubArea(subArea);
                if (this.subArea != null) this.subArea.addMap(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Map " + id);
            System.out.println(e);
            Main.stop("GameMap1");
        }

        try {
            String[] split = forbidden.split(";");
            noSellers = split[0].equals("1");
            noCollectors = split[1].equals("1");
            noPrisms = split[2].equals("1");
            noTp = split[3].equals("1");
            noDefy = split[4].equals("1");
            noAgro = split[5].equals("1");
            noCanal = split[6].equals("1");
        } catch (Exception e) {}

        String unique = "";
        if(monsters.contains("@")) {
            String[] split = monsters.split("@");
            unique = split[0];
            monsters = split[1];
        }

        for (String mob : monsters.split("\\|")) {
            if (mob.equals("")) continue;
            int id1, lvl;
            try {
                id1 = Integer.parseInt(mob.split(",")[0]);
                lvl = Integer.parseInt(mob.split(",")[1]);
            } catch (NumberFormatException e) {
                System.err.println("Error map id on monsters : " + this.id);
                e.printStackTrace();
                continue;
            }
            if (id1 == 0 || lvl == 0)
                continue;
            if (World.world.getMonstre(id1) == null)
                continue;
            if (World.world.getMonstre(id1).getGradeByLevel(lvl) == null)
                continue;
            if (Config.modeHalloween) {
                switch (id1) {
                    case 98://Tofu
                        if (World.world.getMonstre(794) != null)
                            if (World.world.getMonstre(794).getGradeByLevel(lvl) != null)
                                id1 = 794;
                        break;
                    case 101://Bouftou
                        if (World.world.getMonstre(793) != null)
                            if (World.world.getMonstre(793).getGradeByLevel(lvl) != null)
                                id1 = 793;
                        break;
                }
            }

            boolean pass = false;
            for(Monster.MobGrade grade : this.mobPossibles) {
                if(unique.contains(String.valueOf(grade.getTemplate().getId())) && id1 == grade.getTemplate().getId()) {
                    pass = true;
                    break;
                }
            }
            if(!pass) {
                this.mobPossibles.add(World.world.getMonstre(id1).getGradeByLevel(lvl));
            }
        }
    }

    public GameMap(short id, String date, byte w, byte h, String key, String places) {
        this.id = id;
        this.date = date;
        this.w = w;
        this.h = h;
        this.key = key;
        this.placesStr = places;
        this.cases = new ArrayList<>();
    }

    public GameMap(short id, String date, byte w, byte h, String key,
                   String places, byte x, byte y, byte maxGroup, byte fixSize,
                   byte minSize, byte maxSize) {
        this.id = id;
        this.date = date;
        this.w = w;
        this.h = h;
        this.key = key;
        this.placesStr = places;
        this.X = x;
        this.Y = y;
        this.maxGroup = maxGroup;
        this.maxSize = maxSize;
        this.minSize = minSize;
        this.fixSize = fixSize;
    }

    public void setCellCache(CellCacheImpl cache) {
        this.cellCache = cache;
    }

    public CellCacheImpl getCellCache() {
        return cellCache;
    }

    public String getForbidden() {
        return (noSellers ? 1 : 0) + ";" + (noCollectors ? 1 : 0) + ";" + (noPrisms ? 1 : 0) + ";" + (noTp ? 1 : 0) +
                ";" + (noDefy ? 1 : 0) + ";" + (noAgro ? 1 : 0) + ";" + (noCanal ? 1 : 0);
    }

    public static void removeMountPark(int guildId) {
        try {
            World.world.getMountPark().values().stream().filter(park -> park.getGuild() != null).filter(park -> park.getGuild().getId() == guildId).forEach(park -> {
                if (!park.getListOfRaising().isEmpty()) {
                    for (Integer id : new ArrayList<>(park.getListOfRaising())) {
                        if (World.world.getMountById(id) == null) {
                            park.delRaising(id);
                            continue;
                        }
                        World.world.removeMount(id);
                        ((MountData) DatabaseManager.get(MountData.class)).delete(World.world.getMountById(id));
                    }
                    park.getListOfRaising().clear();
                }
                if (!park.getEtable().isEmpty()) {
                    for (Mount mount : new ArrayList<>(park.getEtable())) {
                        if (mount == null) continue;
                        World.world.removeMount(mount.getId());
                        ((MountData) DatabaseManager.get(MountData.class)).delete(mount);
                    }
                    park.getEtable().clear();
                }

                park.setOwner(0);
                park.setGuild(null);
                park.setPrice(3000000);
                ((MountParkData) DatabaseManager.get(MountParkData.class)).update(park);

                for (Player p : park.getMap().getPlayers())
                    SocketManager.GAME_SEND_Rp_PACKET(p, park);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getObjResist(Player perso, int cellid, int itemID) {
        MountPark MP = perso.getCurMap().getMountPark();
        String packets = "";
        if (MP == null || MP.getObject().size() == 0)
            return 0;
        for (Entry<Integer, Map<Integer, Integer>> entry : MP.getObjDurab().entrySet()) {
            for (Entry<Integer, Integer> entry2 : entry.getValue().entrySet()) {
                if (cellid == entry.getKey())
                    packets += entry.getKey() + ";" + entry2.getValue() + ";" + entry2.getKey();
            }
        }
        int cell, durability, durabilityMax;
        try {
            String[] infos = packets.split(";");
            cell = Integer.parseInt(infos[0]);
            if (itemID == 7798 || itemID == 7605 || itemID == 7606 || itemID == 7625 || itemID == 7628 || itemID == 7634) {
                durability = Integer.parseInt(infos[1]);
            } else {
                durability = Integer.parseInt(infos[1]) - 1;
            }
            durabilityMax = Integer.parseInt(infos[2]);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        if (durability <= 0) {
            //if (MP.delObject(cell)) {
                durability = 0;
                Map<Integer, Integer> InDurab = new HashMap<>();
                InDurab.put(durabilityMax, durability);
                MP.getObjDurab().put(cell, InDurab);
                SocketManager.SEND_GDO_PUT_OBJECT_MOUNT(perso.getCurMap(), cell
                        + ";" + itemID + ";1;" + durability + ";" + durabilityMax);
                return 0;
            //}
        } else {
            Map<Integer, Integer> InDurab = new HashMap<>();
            InDurab.put(durabilityMax, durability);
            MP.getObjDurab().put(cell, InDurab);
            SocketManager.SEND_GDO_PUT_OBJECT_MOUNT(perso.getCurMap(), cell
                    + ";" + itemID + ";1;" + durability + ";" + durabilityMax);
        }
        return durabilityMax;
    }

    public static int getObjResist(MountPark MP, int cellid, int itemID) {
        String packets = "";
        if (MP == null || MP.getObject().size() == 0)
            return 0;
        for (Entry<Integer, Map<Integer, Integer>> entry : MP.getObjDurab().entrySet()) {
            for (Entry<Integer, Integer> entry2 : entry.getValue().entrySet()) {
                if (cellid == entry.getKey())
                    packets += entry.getKey() + ";" + entry2.getValue() + ";"
                            + entry2.getKey();
            }
        }
        String[] infos = packets.split(";");
        int cell = Integer.parseInt(infos[0]), durability;
        if (itemID == 7798 || itemID == 7605 || itemID == 7606
                || itemID == 7625 || itemID == 7628 || itemID == 7634) {
            durability = Integer.parseInt(infos[1]);
        } else {
            durability = Integer.parseInt(infos[1]) - 1;
        }
        int durabilityMax = Integer.parseInt(infos[2]);

        if (durability <= 0) {
            //if (MP.delObject(cell)) {
            durability = 0;
            Map<Integer, Integer> InDurab = new HashMap<>();
            InDurab.put(durabilityMax, durability);
            MP.getObjDurab().put(cell, InDurab);
            SocketManager.SEND_GDO_PUT_OBJECT_MOUNT(MP.getMap(), cell
                    + ";" + itemID + ";1;" + durability + ";" + durabilityMax);
            return 0;
            //}
        } else {
            Map<Integer, Integer> InDurab = new HashMap<>();
            InDurab.put(durabilityMax, durability);
            MP.getObjDurab().put(cell, InDurab);
            SocketManager.SEND_GDO_PUT_OBJECT_MOUNT(MP.getMap(), cell + ";"
                    + itemID + ";1;" + durability + ";" + durabilityMax);
        }
        return durabilityMax;
    }

    public void setInfos(String date, String monsters, String mapPos,
                         byte maxGroup, byte fixSize, byte minSize, byte maxSize,
                         String forbidden) {
        this.date = date;
        this.mobPossibles.clear();

        try {
            String[] split = forbidden.split(";");
            noSellers = split[0].equals("1");
            noCollectors = split[1].equals("1");
            noPrisms = split[2].equals("1");
            noTp = split[3].equals("1");
            noDefy = split[4].equals("1");
            noAgro = split[5].equals("1");
            noCanal = split[6].equals("1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String unique = "";
        if(monsters.contains("@")) {
            String[] split = monsters.split("@");
            unique = split[0];
            monsters = split[1];
        }

        for (String mob : monsters.split("\\|")) {
            if (mob.equals(""))
                continue;
            int id1, lvl;
            try {
                id1 = Integer.parseInt(mob.split(",")[0]);
                lvl = Integer.parseInt(mob.split(",")[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                continue;
            }
            if (id1 == 0 || lvl == 0)
                continue;
            if (World.world.getMonstre(id1) == null)
                continue;
            if (World.world.getMonstre(id1).getGradeByLevel(lvl) == null)
                continue;
            boolean pass = false;
            for(Monster.MobGrade grade : this.mobPossibles) {
                if(unique.contains(String.valueOf(grade.getTemplate().getId())) && id1 == grade.getTemplate().getId()) {
                    pass = true;
                    break;
                }
            }
            if(!pass) {
                this.mobPossibles.add(World.world.getMonstre(id1).getGradeByLevel(lvl));
            }
        }
        try {
            String[] mapInfos = mapPos.split(",");
            this.X = Byte.parseByte(mapInfos[0]);
            this.Y = Byte.parseByte(mapInfos[1]);
            int subArea = Integer.parseInt(mapInfos[2]);
            if (subArea == 0 && id == 32) {
                this.subArea = World.world.getSubArea(subArea);
                if (this.subArea != null)
                    this.subArea.addMap(this);
            } else if (subArea != 0) {
                this.subArea = World.world.getSubArea(subArea);
                if (this.subArea != null)
                    this.subArea.addMap(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Main.stop("GameMap2");
        }
        this.maxGroup = maxGroup;
        this.maxSize = maxSize;
        this.minSize = minSize;
        this.fixSize = fixSize;
    }

    public void addMobExtra(Integer id, Integer chances) {
        this.mobExtras.put(id, chances);
    }

    public void setGs(byte maxGroup, byte minSize, byte fixSize, byte maxSize) {
        this.maxGroup = maxGroup;
        this.maxSize = maxSize;
        this.minSize = minSize;
        this.fixSize = fixSize;
    }

    public ArrayList<Monster.MobGrade> getMobPossibles() {
        return this.mobPossibles;
    }

    public void setMobPossibles(String monsters) {
        if (monsters == null || monsters.equals(""))
            return;

        this.mobPossibles = new ArrayList<>();

        for (String mob : monsters.split("\\|")) {
            if (mob.equals(""))
                continue;
            int id1, lvl;
            try {
                id1 = Integer.parseInt(mob.split(",")[0]);
                lvl = Integer.parseInt(mob.split(",")[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                continue;
            }
            if (id1 == 0 || lvl == 0)
                continue;
            if (World.world.getMonstre(id1) == null)
                continue;
            if (World.world.getMonstre(id1).getGradeByLevel(lvl) == null)
                continue;
            if (Config.modeHalloween) {
                switch (id1) {
                    case 98://Tofu
                        if (World.world.getMonstre(794) != null)
                            if (World.world.getMonstre(794).getGradeByLevel(lvl) != null)
                                id1 = 794;
                        break;
                    case 101://Bouftou
                        if (World.world.getMonstre(793) != null)
                            if (World.world.getMonstre(793).getGradeByLevel(lvl) != null)
                                id1 = 793;
                        break;
                }
            }

            this.mobPossibles.add(World.world.getMonstre(id1).getGradeByLevel(lvl));
        }
    }

    public byte getMaxSize() {
        return this.maxSize;
    }

    public byte getMinSize() {
        return this.minSize;
    }

    public byte getFixSize() {
        return this.fixSize;
    }

    public short getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public byte getW() {
        return w;
    }

    public byte getH() {
        return h;
    }

    public String getKey() {
        return key;
    }

    public String getPlaces() {
        return placesStr;
    }

    public void setPlaces(String place) {
        this.placesStr = place;
    }

    public List<GameCase> getCases() {
        return cases;
    }

    private void setCases(List<GameCase> cases) {
        this.cases = cases;
    }

    public GameCase getCase(int id) {
        for(GameCase gameCase : this.cases)
            if(gameCase.getId() == (id))
                return gameCase;
        return null;
    }

    public void removeCase(int id) {
        Iterator<GameCase> iterator = this.cases.iterator();

        while(iterator.hasNext()) {
            GameCase gameCase = iterator.next();
            if(gameCase != null && gameCase.getId() == id) {
                iterator.remove();
                break;
            }
        }
    }

    public Fight newFight(Player init1, Player init2, int type) {
        if (init1.getFight() != null || init2.getFight() != null)
            return null;
        int id = 1;
        if(this.fights == null)
            this.fights = new ArrayList<>();
        if (!this.fights.isEmpty())
            id = ((Fight) (this.fights.toArray()[this.fights.size() - 1])).getId() + 1;
        Fight f = new Fight(type, id, this, init1, init2);
        this.fights.add(f);

        return f;
    }

    public void removeFight(int id) {
        if(this.fights != null) {
            Iterator<Fight> iterator = this.getFights().iterator();
            while(iterator.hasNext()) {
                Fight fight = iterator.next();
                if(fight != null && fight.getId() == id) {
                    iterator.remove();
                    break;
                }
            }

            if(this.fights.isEmpty()) this.fights = null;
        }
    }

    public int getNbrFight() {
        return fights == null ? 0 : this.fights.size();
    }

    public Fight getFight(int id) {
        final Fight[] fight = {null};

        if(this.fights != null)
            this.fights.stream().filter(all -> all.getId() == id).forEach(selected -> fight[0] = selected);
        
        return fight[0];
    }

    public List<Fight> getFights() {
        if(this.fights == null)
            return new ArrayList<>();
        return fights;
    }

    public Map<Integer, Monster.MobGroup> getMobGroups() {
        return this.mobGroups;
    }

    public Map<Integer, Monster.MobGroup> getFixMobGroups() {
        return fixMobGroups;
    }

    public void removeNpcOrMobGroup(int id) {
        this.npcs.remove(id);
        this.mobGroups.remove(id);
    }

    public Npc addNpc(int npcID, int cellID, int dir) {
        NpcTemplate temp = World.world.getNPCTemplate(npcID);
        if (temp == null)
            return null;
        if (getCase(cellID) == null)
            return null;
        Npc npc;
        if(temp.getPath().isEmpty())
            npc = new Npc(this.nextObjectId, cellID, (byte) dir, temp);
        else
            npc = new NpcMovable(this.nextObjectId, cellID, (byte) dir, this.id, temp);
        this.npcs.put(this.nextObjectId, npc);
        this.nextObjectId--;
        return npc;
    }

    public Map<Integer, Npc> getNpcs() {
        return this.npcs;
    }

    public Npc getNpc(int id) {
        return this.npcs.get(id);
    }

    public Npc getNpcByTemplateId(int id) {
        for(Npc npc : this.npcs.values())
            if(npc != null && npc.getTemplate().getId() == id)
                return npc;
        return null;
    }

    public Npc RemoveNpc(int id) {
        return this.npcs.remove(id);
    }

    public void applyEndFightAction(Player player) {
        if (this.endFightAction.get(player.needEndFight()) == null)
            return;
        if (this.id == 8545 && player.hasMobGroup() != null && player.hasMobGroup().getMobs().size() == 6) {
            for (Action A : this.endFightAction.get(player.needEndFight())) {
                String args = A.getArgs();
                A.setArgs("8547,390");
                A.apply(player, null, -1, -1);
                A.setArgs(args);
            }
        } else {
            for (Action A : this.endFightAction.get(player.needEndFight()))
                A.apply(player, null, -1, -1);
        }
        player.setNeededEndFight(-1, null);
    }

    public boolean hasEndFightAction(int type) {
        return this.endFightAction.get(type) != null;
    }

    public Map<Integer, ArrayList<Action>> getEndFightAction() {
        return endFightAction;
    }

    public void addEndFightAction(int type, Action A) {
        if (this.endFightAction.get(type) == null)
            this.endFightAction.put(type, new ArrayList<>()); // On retire l'action si elle existait d�j�
        delEndFightAction(type, A.getId());
        this.endFightAction.get(type).add(A);
    }

    public void delEndFightAction(int type, int aType) {
        if (this.endFightAction.get(type) != null)
            new ArrayList<>(this.endFightAction.get(type)).stream().filter(A -> A.getId() == aType).forEach(A -> this.endFightAction.get(type).remove(A));
    }

    public void delAllEndFightAction() {
        this.endFightAction.clear();
    }

    public int getX() {
        return this.X;
    }

    public int getY() {
        return this.Y;
    }

    public SubArea getSubArea() {
        return this.subArea;
    }

    public Area getArea() {
        return this.subArea != null ? this.subArea.getArea() : null;
    }

    public MountPark getMountPark() {
        return this.mountPark;
    }

    public void setMountPark(MountPark mountPark) {
        this.mountPark = mountPark;
    }

    public int getMaxGroupNumb() {
        return this.maxGroup;
    }

    public int getMaxTeam() {
        return this.maxTeam;
    }

    public boolean containsForbiddenCellSpawn(int id) {
        if(this.mountPark != null)
            if(this.mountPark.getCellAndObject().containsKey(id))
                return true;
        return false;
    }

    public GameMap getMapCopy() {
        List<GameCase> cases = new ArrayList<>();

        GameMap map = new GameMap(id, date, w, h, key, placesStr);

        for (GameCase gameCase : this.cases) {
            if (map.getId() == 8279) {
                switch (gameCase.getId()) {
                    case 187:
                    case 170:
                    case 156:
                    case 142:
                    case 128:
                    case 114:
                    case 100:
                    case 86:
                        continue;
                }
            }

            cases.add(new GameCase(map, gameCase.getId(), gameCase.isWalkable(true, true, -1), gameCase.isLoS(), (gameCase.getObject() == null ? -1 : gameCase.getObject().getId())));
        }
        map.setCases(cases);
        map.setCellCache(this.getCellCache());
        return map;
    }

    public GameMap getMapCopyIdentic(boolean keepCellAction) {
        GameMap map = new GameMap(id, date, w, h, key, placesStr, X, Y, maxGroup, fixSize, minSize, maxSize);
        List<GameCase> cases = new ArrayList<>();
        this.cases.forEach(entry -> {
            GameCase cell = new GameCase(map, entry.getId(), entry.isWalkable(false), entry.isLoS(), (entry.getObject() == null ? -1 : entry.getObject().getId()));
            if(keepCellAction && entry.getOnCellStopAction()) {
                ArrayList<Action> actions = new ArrayList<>();
                for(Action action : entry.getOnCellStop())
                    actions.add(new Action(action.getId(), action.getArgs(), "", action.getMap()));
                cell.setOnCellStop(actions);
            }
            cases.add(cell);
        });
        map.setCases(cases);
        map.setCellCache(this.getCellCache());
        return map;
    }

    public void addPlayer(Player perso) {
        SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(this, perso);	
        perso.getCurCell().addPlayer(perso);
        if (perso.getEnergy() > 0) {
            if (perso.getEnergy() >= 10000)
                return;
            if (Constant.isTaverne(this) && perso.getTimeTaverne() == 0) {
                perso.setTimeTaverne(System.currentTimeMillis());
            } else if (perso.getTimeTaverne() != 0) {
                int gain = (int) ((System.currentTimeMillis() - perso.getTimeTaverne()) / 1000);
                if(gain >= 10000) gain = 10000 - perso.getEnergy();
                perso.setEnergy(perso.getEnergy() + gain);
                if (perso.getEnergy() >= 10000) perso.setEnergy(10000);
                SocketManager.GAME_SEND_Im_PACKET(perso, "092;" + gain);
                SocketManager.GAME_SEND_STATS_PACKET(perso);
                perso.setTimeTaverne(0);
            }
        }
    }

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> player = new ArrayList<>();
        for (GameCase c : cases)
            player.addAll(new ArrayList<>(c.getPlayers()));
        return player;
    }

    public void sendFloorItems(Player player) {
        StringBuilder builder = new StringBuilder("GDO");
        this.cases.stream().filter(c -> c.getDroppedItem(false) != null)
                .forEach(c -> builder.append("+").append(c.getId()).append(";").append(c.getDroppedItem(false).getTemplate().getId()).append(";0|"));
        player.send(builder.toString());
    }

    public void delAllDropItem() {
        for (GameCase gameCase : this.cases) {
            SocketManager.GAME_SEND_GDO_PACKET_TO_MAP(this, '-', gameCase.getId(), 0, 0);
            gameCase.clearDroppedItem();
        }
    }

    public int getStoreCount() {
        if (World.world.getSeller(this.getId()) == null)
            return 0;
        return World.world.getSeller(this.getId()).size();
    }

    public boolean haveMobFix() {
        return this.fixMobGroups.size() > 0;
    }

    public boolean isPossibleToPutMonster() {
        return !this.cases.isEmpty() && this.maxGroup > 0 && this.mobPossibles.size() > 0;
    }

    public boolean loadExtraMonsterOnMap(int idMob) {
        if (World.world.getMonstre(idMob) == null)
            return false;
        Monster.MobGrade grade = World.world.getMonstre(idMob).getRandomGrade();
        int cell = this.getRandomFreeCellId();

        Monster.MobGroup group = new Monster.MobGroup(this.nextObjectId, Constant.ALIGNEMENT_NEUTRE, this.mobPossibles, this, cell, this.fixSize, this.maxSize, this.maxSize, grade);
        if (group.getMobs().isEmpty())
            return false;
        this.mobGroups.put(this.nextObjectId, group);
        this.nextObjectId--;
        return true;
    }

    public void loadMonsterOnMap() {
        if (maxGroup == 0)
            return;
        spawnGroup(Constant.ALIGNEMENT_NEUTRE, this.maxGroup, false, -1);//Spawn des groupes d'alignement neutre
        spawnGroup(Constant.ALIGNEMENT_BONTARIEN, 1, false, -1);//Spawn du groupe de gardes bontarien s'il y a
        spawnGroup(Constant.ALIGNEMENT_BRAKMARIEN, 1, false, -1);//Spawn du groupe de gardes brakmarien s'il y a
    }

    public void mute() {
        this.isMute = !this.isMute;
    }

    public boolean isMute() {
        return this.isMute;
    }

    public boolean isAggroByMob(Player player, int cell) {
        if (placesStr.equalsIgnoreCase("|"))
            return false;
        if (player.getCurMap().getId() != id || !player.canAggro())
            return false;
        for (Monster.MobGroup group : this.mobGroups.values()) {
            if (player.getAlignment() == 0 && group.getAlignement() > 0)
                continue;
            if (player.getAlignment() == 1 && group.getAlignement() == 1)
                continue;
            if (player.getAlignment() == 2 && group.getAlignement() == 2)
                continue;

            if (this.subArea != null) {
                group.setSubArea(this.subArea.getId());
                group.changeAgro();
            }
            if (PathFinding.getDistanceBetween(this, cell, group.getCellId()) <= group.getAggroDistance() && group.getAggroDistance() > 0)//S'il y aggro
                if (World.world.getConditionManager().validConditions(player, group.getCondition()))
                    return true;
        }
        return false;
    }

    public void spawnAfterTimeGroup() {
        ((ArrayList<RespawnGroup>) updatable.get()).add(new RespawnGroup(this, -1, System.currentTimeMillis()));
    }

    public void spawnAfterTimeGroupFix(final int cell) {
        ((ArrayList<RespawnGroup>) updatable.get()).add(new RespawnGroup(this, cell, System.currentTimeMillis()));
    }

    private static class RespawnGroup {

        private final GameMap map;
        private final int cell;
        private final long lastTime;

        public RespawnGroup(GameMap map, int cell, long lastTime) {
            this.map = map;
            this.cell = cell;
            this.lastTime = lastTime;
        }
    }

    public void spawnGroup(int align, int nbr, boolean log, int cellID) {
        if (nbr < 1)
            return;
        if (this.mobGroups.size() + this.fixMobGroups.size() >= this.maxGroup)
            return;
        for (int a = 1; a <= nbr; a++) {
            // mobExtras
            ArrayList<Monster.MobGrade> mobPoss = new ArrayList<>(this.mobPossibles);
            if (!this.mobExtras.isEmpty()) {
                for (Entry<Integer, Integer> entry : this.mobExtras.entrySet()) {
                    if (entry.getKey() == 499) // Si c'est un minotoboule de nowel
                        if (!Config.modeChristmas) // Si ce n'est pas nowel
                            continue;
                    int random = Formulas.getRandomValue(0, 99);
                    while (entry.getValue() > random) {
                        Monster mob = World.world.getMonstre(entry.getKey());
                        if (mob == null)
                            continue;
                        Monster.MobGrade mobG = mob.getRandomGrade();
                        if (mobG == null)
                            continue;
                        mobPoss.add(mobG);
                        if (entry.getKey() == 422 || entry.getKey() == 499) // un seul DDV / Minotoboule
                            break;
                        random = Formulas.getRandomValue(0, 99);
                    }
                }
            }

            while(this.mobGroups.get(this.nextObjectId) != null)
                this.nextObjectId--;

            Monster.MobGroup group = new Monster.MobGroup(this.nextObjectId, align, mobPoss, this, cellID, this.fixSize, this.minSize, this.maxSize, null);

            if (group.getMobs().isEmpty())
                continue;
            this.mobGroups.put(this.nextObjectId, group);
            if (log)
                SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, group);
            this.nextObjectId--;
        }
    }

    public void respawnGroup(Monster.MobGroup group) {
        this.mobGroups.put(group.getId(), group);
        SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, group);
    }

    public void spawnGroupWith(Monster m) {
        while(this.mobGroups.get(this.nextObjectId) != null)
            this.nextObjectId--;
        Monster.MobGrade _m = null;
        while (_m == null)
            _m = m.getRandomGrade();
        int cell = this.getRandomFreeCellId();
        while (this.containsForbiddenCellSpawn(cell))
            cell = this.getRandomFreeCellId();

        Monster.MobGroup group = new Monster.MobGroup(this.nextObjectId, -1, this.mobPossibles, this, cell, this.fixSize, this.minSize, this.maxSize, _m);
        group.setIsFix(false);
        this.mobGroups.put(this.nextObjectId, group);
        SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, group);
        this.nextObjectId--;
    }


    public void spawnNewGroup(boolean timer, int cellID, String groupData, String condition) {
        while(this.mobGroups.get(this.nextObjectId) != null)
            this.nextObjectId--;
        while (this.containsForbiddenCellSpawn(cellID))
            cellID = this.getRandomFreeCellId();

        Monster.MobGroup group = new Monster.MobGroup(this.nextObjectId, this, cellID, groupData);
        if (group.getMobs().isEmpty())
            return;
        this.mobGroups.put(this.nextObjectId, group);
        group.setCondition(condition);
        group.setIsFix(false);
        SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, group);
        this.nextObjectId--;
        if (timer)
            group.startCondTimer();
    }

    public Monster.MobGroup spawnGroupGladiatrool(String groupData) {
        while(this.mobGroups.get(this.nextObjectId) != null)
            this.nextObjectId--;
        int cell = this.getRandomFreeCellId();
        while (this.containsForbiddenCellSpawn(cell))
            cell = this.getRandomFreeCellId();
        Monster.MobGroup group = new Monster.MobGroup(this.nextObjectId, this, cell, groupData);
        if (group.getMobs().isEmpty())
            return group;
        this.mobGroups.put(this.nextObjectId, group);
        group.setIsFix(false);
        SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, group);

        this.nextObjectId--;
        return group;
    }

    public Monster.MobGroup spawnGroupOnCommand(int cellID, String groupData, boolean send) {
        while(this.mobGroups.get(this.nextObjectId) != null)
            this.nextObjectId--;
        Monster.MobGroup group = new Monster.MobGroup(this.nextObjectId, this, cellID, groupData);
        if (group.getMobs().isEmpty())
            return group;
        this.mobGroups.put(this.nextObjectId, group);
        group.setIsFix(false);
        if (send)
            SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, group);

        this.nextObjectId--;
        return group;
    }

    public void addStaticGroup(int cellID, String groupData, boolean b) {
        while(this.mobGroups.get(this.nextObjectId) != null)
            this.nextObjectId--;
        Monster.MobGroup group = new Monster.MobGroup(this.nextObjectId, this, cellID, groupData);

        if (group.getMobs().isEmpty())
            return;
        this.mobGroups.put(this.nextObjectId, group);
        this.nextObjectId--;
        this.fixMobGroups.put(-1000 + this.nextObjectId, group);
        if (b)
            SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, group);
    }

    public void refreshSpawns() {
        for (int id : this.mobGroups.keySet()) {
            SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(this, id);
        }
        this.mobGroups.clear();
        this.mobGroups.putAll(this.fixMobGroups);
        for (Monster.MobGroup mg : this.fixMobGroups.values())
            SocketManager.GAME_SEND_MAP_MOBS_GM_PACKET(this, mg);

        spawnGroup(Constant.ALIGNEMENT_NEUTRE, this.maxGroup, true, -1);//Spawn des groupes d'alignement neutre
        spawnGroup(Constant.ALIGNEMENT_BONTARIEN, 1, true, -1);//Spawn du groupe de gardes bontarien s'il y a
        spawnGroup(Constant.ALIGNEMENT_BRAKMARIEN, 1, true, -1);//Spawn du groupe de gardes brakmarien s'il y a
    }

    public String getGMsPackets() {
        StringBuilder packet = new StringBuilder();
        cases.stream().filter(Objects::nonNull)
                .forEach(cell -> new ArrayList<>(cell.getPlayers()).stream()
                .filter(Objects::nonNull)
                .forEach(player -> packet.append("GM|+").append(player.parseToGM()).append('\u0000')));
        return packet.toString();
    }

    public String getFightersGMsPackets(Fight fight) {
        StringBuilder packet = new StringBuilder("GM");
        for (GameCase cell : this.cases)
            new ArrayList<>(cell.getFighters()).stream().filter(fighter -> fighter.getFight() == fight)
                    .forEach(fighter -> packet.append("|").append(fighter.getGmPacket('+', false)));
        return packet.toString();
    }

    public String getFighterGMPacket(Player player) {
        Fighter target = player.getFight().getFighterByPerso(player);
        for (GameCase cell : this.cases)
            for(Fighter fighter : cell.getFighters())
                if(fighter.getFight() == player.getFight() && fighter == target)
                    return "GM|" + fighter.getGmPacket('~', false);
        return "";
    }

    public String getFighterGMPacket(Fighter target) {
        for (GameCase cell : this.cases)
            for(Fighter fighter : cell.getFighters())
                if(fighter.getFight() == target.getFight() && fighter == target)
                    return "GM|" + fighter.getGmPacket('~', false);
        return "";
    }

    public String getMobGroupGMsPackets() {
        if (this.mobGroups.isEmpty())
            return "";

        StringBuilder packet = new StringBuilder();
        packet.append("GM|");
        boolean isFirst = true;
        for (Monster.MobGroup entry : this.mobGroups.values()) {
            String GM = entry.parseGM();
            if (GM.equals(""))
                continue;

            if (!isFirst)
                packet.append("|");

            packet.append(GM);
            isFirst = false;
        }
        return packet.toString();
    }

    public String getPrismeGMPacket() {
        String str = "";
        Collection<Prism> prisms = World.world.AllPrisme();
        if (prisms != null) {
            for (Prism prism : prisms) {
                if (prism.getMap() == this.id) {
                    str = prism.parseToGM();
                    break;
                }
            }
        }
        return str;
    }

    public String getNpcsGMsPackets(Player p) {
        if (this.npcs.isEmpty())
            return "";

        StringBuilder packet = new StringBuilder();
        packet.append("GM|");
        boolean isFirst = true;
        for (Entry<Integer, Npc> entry : this.npcs.entrySet()) {
            String GM = entry.getValue().parse(false, p);
            if (GM.equals(""))
                continue;

            if (!isFirst)
                packet.append("|");

            packet.append(GM);
            isFirst = false;
        }
        return packet.toString();
    }

    public String getObjectsGDsPackets() {
        StringBuilder packet = new StringBuilder("GDF");
        this.cases.stream().filter(gameCase -> gameCase.getObject() != null)
                .forEach(gameCase -> packet.append("|").append(gameCase.getId()).append(";").append(gameCase.getObject().getState())
                        .append(";").append((gameCase.getObject().isInteractive() ? "1" : "0")));
        return packet.toString();
    }

    public void startFightMonsterVersusMonster(Monster.MobGroup group1, Monster.MobGroup group2) {
        int id = 1;
        if(this.fights == null)
            this.fights = new ArrayList<>();
        if (!this.fights.isEmpty())
            id = ((Fight) (this.fights.toArray()[this.fights.size() - 1])).getId() + 1;

        this.mobGroups.remove(group1.getId());
        this.mobGroups.remove(group2.getId());
        Fight fight = new Fight(id, this, group1, group2);
        this.fights.add(fight);
        SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
    }

    public void startFightVersusMonstres(Player player, Monster.MobGroup group) {
        if (player.getFight() != null)
            return;
        if (player.isInAreaNotSubscribe()) {
            SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(player.getGameClient(), 'S');
            return;
        }
        if (this.placesStr.isEmpty() || this.placesStr.equals("|")) {
            player.sendMessage(player.getLang().trans("area.map.gamemap.place.empty"));
            return;
        }
        if (Main.fightAsBlocked)
            return;
        if (player.isDead() == 1)
            return;
        if (player.getAlignment() == 0 && group.getAlignement() > 0)
            return;
        if (player.getAlignment() == 1 && group.getAlignement() == 1)
            return;
        if (player.getAlignment() == 2 && group.getAlignement() == 2)
            return;
        if (!player.canAggro())
            return;
        if(player.afterFight)
            return;
        if (!group.getCondition().equals(""))
            if (!World.world.getConditionManager().validConditions(player, group.getCondition())) {
                SocketManager.GAME_SEND_Im_PACKET(player, "119");
                return;
            }

        final Party party = player.getParty();

        if(party != null && party.getMaster() != null && !party.getMaster().getName().equals(player.getName()) && party.isWithTheMaster(player, false, false)) return;

        int id = 1;
        if(this.fights == null)
            this.fights = new ArrayList<>();
        if (!this.fights.isEmpty())
            id = ((Fight) (this.fights.toArray()[this.fights.size() - 1])).getId() + 1;

        this.mobGroups.remove(group.getId());
        Fight fight = new Fight(id, this, player, group);
        this.fights.add(fight);
        SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);

        if(party != null && party.getMaster() != null && party.getMaster().getName().equals(player.getName())) {
            party.getPlayers().stream().filter((follower) -> party.isWithTheMaster(follower, false, false)).forEach(follower -> {
                TimerWaiter.addNext(() -> {
                    if (fight.getPrism() != null)
                        fight.joinPrismFight(follower, (fight.getTeam0().containsKey(player.getId()) ? 0 : 1));
                    else
                        fight.joinFight(follower, player.getId());
                }, follower.getParty().getOptionByPlayer(follower).getSecond(), TimeUnit.SECONDS);
            });
        }
    }

    public void startFightVersusProtectors(Player player, Monster.MobGroup group) {
        if (Main.fightAsBlocked || player == null || player.getFight() != null || player.isDead() == 1 || !player.canAggro())
            return;
        if (player.isInAreaNotSubscribe()) {
            SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(player.getGameClient(), 'S');
            return;
        }

        int id = 1;

        if (this.placesStr.isEmpty() || this.placesStr.equals("|")) {
            player.sendMessage(player.getLang().trans("area.map.gamemap.place.empty"));
            return;
        }
        if(this.fights == null)
            this.fights = new ArrayList<>();
        if (!this.fights.isEmpty())
            id = ((Fight) (this.fights.toArray()[this.fights.size() - 1])).getId() + 1;
        Fight fight = new Fight(id, this, player, group, Constant.FIGHT_TYPE_PVM);
        fight.startFight();
        this.fights.add(fight);
        SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
    }

    public void startFigthVersusDopeuls(Player perso, Monster.MobGroup group)//RaZoR
    {
        if (perso.getFight() != null)
            return;
        if (perso.isInAreaNotSubscribe()) {
            SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(perso.getGameClient(), 'S');
            return;
        }
        int id = 1;
        if (perso.isDead() == 1)
            return;
        if (!perso.canAggro())
            return;
        if(this.fights == null)
            this.fights = new ArrayList<>();
        if (!this.fights.isEmpty())
            id = ((Fight) (this.fights.toArray()[this.fights.size() - 1])).getId() + 1;
        this.fights.add(new Fight(id, this, perso, group, Constant.FIGHT_TYPE_DOPEUL));
        SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
    }

    public void startFightVersusPercepteur(Player perso, Collector perco) {
        if (perso.getFight() != null)
            return;
        if (perso.isInAreaNotSubscribe()) {
            SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(perso.getGameClient(), 'S');
            return;
        }
        if (Main.fightAsBlocked)
            return;
        if (perso.isDead() == 1)
            return;
        if (!perso.canAggro())
            return;
        int id = 1;
        if(this.fights == null)
            this.fights = new ArrayList<>();
        if (!this.fights.isEmpty())
            id = ((Fight) (this.fights.toArray()[this.fights.size() - 1])).getId() + 1;

        this.fights.add(new Fight(id, this, perso, perco));
        SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
    }

    public void startFightVersusPrisme(Player perso, Prism Prisme) {
        if (perso.getFight() != null)
            return;
        if (perso.isInAreaNotSubscribe()) {
            SocketManager.GAME_SEND_EXCHANGE_REQUEST_ERROR(perso.getGameClient(), 'S');
            return;
        }
        if (Main.fightAsBlocked || perso.isDead() == 1 || !perso.canAggro())
            return;
        int id = 1;
        if(this.fights == null)
            this.fights = new ArrayList<>();
        if (!this.fights.isEmpty())
            id = ((Fight) (this.fights.toArray()[this.fights.size() - 1])).getId() + 1;
        this.fights.add(new Fight(id, this, perso, Prisme));
        SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
    }

    public int getRandomFreeCellId() {
        ArrayList<Integer> freecell = new ArrayList<>();

        for (GameCase entry : cases) {
            if (entry == null)
                continue;
            if (!entry.isWalkable(true))
                continue;
            if (entry.getObject() != null)
                continue;
            if(this.id == 8279) {
                switch(entry.getId()) {
                    case 86:
                    case 100:
                    case 114:
                    case 128:
                    case 142:
                    case 156:
                    case 170:
                    case 184:
                    case 198:
                        continue;
                }
            }
            if (this.mountPark != null)
                if (this.mountPark.getCellOfObject().contains((int) entry.getId()))
                    continue;

            boolean ok = true;
            if (this.mobGroups != null)
                for (Monster.MobGroup mg : this.mobGroups.values())
                    if (mg != null)
                        if (mg.getCellId() == entry.getId())
                            ok = false;
            if (this.npcs != null)
                for (Npc npc : this.npcs.values())
                    if (npc != null)
                        if (npc.getCellId() == entry.getId())
                            ok = false;
            if (!ok || !entry.getPlayers().isEmpty() || entry.getOnCellStopAction())
                continue;
            freecell.add(entry.getId());
        }

        if (freecell.isEmpty())
            return -1;
        return freecell.get(Formulas.getRandomValue(0, freecell.size() - 1));
    }

    public int getRandomNearFreeCellId(int cellid)//obtenir une cell al�atoire et proche
    {
        ArrayList<Integer> freecell = new ArrayList<>();
        ArrayList<Integer> cases = new ArrayList<>();

        cases.add((cellid + 1));
        cases.add((cellid - 1));
        cases.add((cellid + 2));
        cases.add((cellid - 2));
        cases.add((cellid + 14));
        cases.add((cellid - 14));
        cases.add((cellid + 15));
        cases.add((cellid - 15));
        cases.add((cellid + 16));
        cases.add((cellid - 16));
        cases.add((cellid + 27));
        cases.add((cellid - 27));
        cases.add((cellid + 28));
        cases.add((cellid - 28));
        cases.add((cellid + 29));
        cases.add((cellid - 29));
        cases.add((cellid + 30));
        cases.add((cellid - 30));
        cases.add((cellid + 31));
        cases.add((cellid - 31));
        cases.add((cellid + 42));
        cases.add((cellid - 42));
        cases.add((cellid + 43));
        cases.add((cellid - 43));
        cases.add((cellid + 44));
        cases.add((cellid - 44));
        cases.add((cellid + 45));
        cases.add((cellid - 45));
        cases.add((cellid + 57));
        cases.add((cellid - 57));
        cases.add((cellid + 58));
        cases.add((cellid - 58));
        cases.add((cellid + 59));
        cases.add((cellid - 59));

        for (int entry : cases) {
            GameCase gameCase = this.getCase(entry);
            if (gameCase == null)
                continue;
            if(gameCase.getOnCellStopAction())
                continue;
            //Si la case n'est pas marchable
            if (!gameCase.isWalkable(true))
                continue;
            //Si la case est prise par un groupe de monstre
            boolean ok = true;
            for (Entry<Integer, Monster.MobGroup> mgEntry : this.mobGroups.entrySet())
                if (mgEntry.getValue().getCellId() == gameCase.getId())
                    ok = false;
            if (!ok)
                continue;
            //Si la case est prise par un npc
            ok = true;
            for (Entry<Integer, Npc> npcEntry : this.npcs.entrySet())
                if (npcEntry.getValue().getCellId() == gameCase.getId())
                    ok = false;
            if (!ok)
                continue;
            //Si la case est prise par un joueur
            if (!gameCase.getPlayers().isEmpty())
                continue;
            //Sinon
            freecell.add(gameCase.getId());
        }
        if (freecell.isEmpty())
            return -1;
        int rand = Formulas.getRandomValue(0, freecell.size() - 1);
        return freecell.get(rand);
    }

    public void onMapMonsterDeplacement() {
        if (getMobGroups().size() == 0)
            return;
        int RandNumb = Formulas.getRandomValue(1, getMobGroups().size());
        int i = 0;
        for (Monster.MobGroup group : getMobGroups().values()) {
            if(group.isFix() && this.id != 8279)
                continue;
            switch (this.id) {
                case 8279:// W:15   H:17
                    final int cell1 = group.getCellId();
                    final GameCase cell2 = this.getCase((cell1 - 15)), cell3 = this.getCase((cell1 - 15 + 1));
                    final GameCase cell4 = this.getCase((cell1 + 15 - 1)),
                            cell5 = this.getCase((cell1 + 15));
                    boolean case2 = (cell2 != null && (cell2.isWalkable(true) && (cell2.getPlayers().isEmpty())));
                    boolean case3 = (cell3 != null && (cell3.isWalkable(true) && (cell3.getPlayers().isEmpty())));
                    boolean case4 = (cell4 != null && (cell4.isWalkable(true) && (cell4.getPlayers().isEmpty())));
                    boolean case5 = (cell5 != null && (cell5.isWalkable(true) && (cell5.getPlayers().isEmpty())));
                    ArrayList<Boolean> array = new ArrayList<>();
                    array.add(case2);
                    array.add(case3);
                    array.add(case4);
                    array.add(case5);

                    int count = 0;
                    for (boolean bo : array)
                        if (bo)
                            count++;

                    if (count == 0)
                        return;
                    if (count == 1) {
                        GameCase newCell = (case2 ? cell2 : (case3 ? cell3 : (case4 ? cell4 : cell5)));
                        GameCase nextCell = null;
                        if (newCell == null)
                            return;

                        if (newCell.equals(cell2)) {
                            if (checkCell(newCell.getId() - 15)) {
                                nextCell = this.getCase(newCell.getId() - 15);
                                if (this.checkCell(nextCell.getId() - 15)) {
                                    nextCell = this.getCase(nextCell.getId() - 15);
                                }
                            }
                        } else if (newCell.equals(cell3)) {
                            if (this.checkCell(newCell.getId() - 15 + 1)) {
                                nextCell = this.getCase(newCell.getId() - 15 + 1);
                                if (this.getCase(nextCell.getId() - 15 + 1) != null) {
                                    nextCell = this.getCase(nextCell.getId() - 15 + 1);
                                }
                            }
                        } else if (newCell.equals(cell4)) {
                            if (this.checkCell(newCell.getId() + 15 - 1)) {
                                nextCell = this.getCase(newCell.getId() + 15 - 1);
                                if (this.checkCell(nextCell.getId() + 15 - 1)) {
                                    nextCell = this.getCase(nextCell.getId() + 15 - 1);
                                }
                            }
                        } else if (newCell.equals(cell5)) {
                            if (this.checkCell(newCell.getId() + 15)) {
                                nextCell = this.getCase(newCell.getId() + 15);
                                if (this.checkCell(nextCell.getId() + 15)) {
                                    nextCell = this.getCase(nextCell.getId() + 15);
                                }
                            }
                        }

                        String pathstr;
                        try {
                            pathstr = PathFinding.getShortestStringPathBetween(this, group.getCellId(), nextCell.getId(), 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        if (pathstr == null)
                            return;
                        group.setCellId(nextCell.getId());
                        for (Player z : getPlayers())
                            SocketManager.GAME_SEND_GA_PACKET(z.getGameClient(), "0", "1", group.getId()
                                    + "", pathstr);
                    } else {
                        if (group.isFix())
                            continue;
                        i++;
                        if (i != RandNumb)
                            continue;

                        int cell = -1;
                        while (cell == -1 || cell == 383 || cell == 384
                                || cell == 398 || cell == 369)
                            cell = getRandomNearFreeCellId(group.getCellId());
                        String pathstr;
                        try {
                            pathstr = PathFinding.getShortestStringPathBetween(this, group.getCellId(), cell, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        if (pathstr == null)
                            return;
                        group.setCellId(cell);
                        for (Player z : getPlayers())
                            SocketManager.GAME_SEND_GA_PACKET(z.getGameClient(), "0", "1", group.getId() + "", pathstr);
                    }
                    break;

                default:
                    if (group.isFix())
                        continue;
                    i++;
                    if (i != RandNumb)
                        continue;
                    int cell = getRandomNearFreeCellId(group.getCellId());
                    String pathstr;
                    try {
                        pathstr = PathFinding.getShortestStringPathBetween(this, group.getCellId(), cell, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (pathstr == null)
                        return;
                    group.setCellId(cell);
                    for (Player z : getPlayers())
                        if(z != null)
                            SocketManager.GAME_SEND_GA_PACKET(z.getGameClient(), "0", "1", group.getId() + "", pathstr);
                    break;
            }

        }
    }

    public boolean checkCell(int id) {
        return this.getCase(id - 15) != null && this.getCase(id - 15).isWalkable(true);
    }

    public String getObjects() {
        if (this.mountPark == null || this.mountPark.getObject().size() == 0)
            return "";
        StringBuilder packets = new StringBuilder("GDO+");
        boolean first = true;
        for (Entry<Integer, Map<Integer, Integer>> entry : this.mountPark.getObject().entrySet()) {
            for (Entry<Integer, Integer> entry2 : entry.getValue().entrySet()) {
                if (!first) packets.append("|");
                int cellidDurab = entry.getKey();
                packets.append(entry.getKey()).append(";").append(entry2.getKey()).append(";1;").append(getObjDurable(cellidDurab));
                first = false;
            }
        }
        return packets.toString();
    }

    public String getObjDurable(int CellID) {
        String packets = "";
        for (Entry<Integer, Map<Integer, Integer>> entry : this.mountPark.getObjDurab().entrySet()) {
            for (Entry<Integer, Integer> entry2 : entry.getValue().entrySet()) {
                if (CellID == entry.getKey())
                    packets += entry2.getValue() + ";" + entry2.getKey();
            }
        }
        return packets;
    }

    public boolean cellSideLeft(int cell) {
        int ladoIzq = this.w;
        for (int i = 0; i < this.w; i++) {
            if (cell == ladoIzq)
                return true;
            ladoIzq = ladoIzq + (this.w * 2) - 1;
        }
        return false;
    }

    public boolean cellSideRight(int cell) {
        int ladoDer = 2 * (this.w - 1);
        for (int i = 0; i < this.w; i++) {
            if (cell == ladoDer)
                return true;
            ladoDer = ladoDer + (this.w * 2) - 1;
        }
        return false;
    }

    public boolean cellSide(int cell1, int cell2) {
        if (cellSideLeft(cell1))
            if (cell2 == cell1 + (this.w - 1) || cell2 == cell1 - this.w)
                return true;
        if (cellSideRight(cell1))
            if (cell2 == cell1 + this.w || cell2 == cell1 - (this.w - 1))
                return true;
        return false;
    }

    public String getGMOfMount(boolean ok) {
        if(this.mountPark == null || this.mountPark.getListOfRaising().size() == 0)
            return "";

        ArrayList<Mount> mounts = new ArrayList<>();

        for(Integer id : this.mountPark.getListOfRaising()) {
            Mount mount = World.world.getMountById(id);

            if(mount != null)
                if(this.getPlayer(mount.getOwner()) != null || this.mountPark.getGuild() != null)
                    mounts.add(mount);
        }

        if (ok)
            for(Player target : this.getPlayers())
                SocketManager.GAME_SEND_GM_MOUNT(target.getGameClient(), this, false);

        return this.getGMOfMount(mounts);
    }

    public String getGMOfMount(ArrayList<Mount> mounts) {
        if(this.mountPark == null || this.mountPark.getListOfRaising().size() == 0)
            return "";
        StringBuilder packets = new StringBuilder();
        packets.append("GM|+");
        boolean first = true;
        for(Mount mount : mounts) {
            String GM = mount.parseToGM();
            if(GM == null || GM.equals(""))
                continue;
            if(!first)
                packets.append("|+");
            packets.append(GM);
            first = false;
        }

        return packets.toString();
    }

    public Player getPlayer(int id) {
        for(GameCase cell : cases)
            for(Player player : cell.getPlayers())
                if(player != null)
                    if(player.getId() == id)
                        return player;
        return null;
    }

    public void onPlayerArriveOnCell(Player player, int id) {
        final GameCase cell = this.getCase(id);

        if (cell == null)
            return;

        if (cell.getDroppedItem(false) != null) {
            if (!Main.mapAsBlocked) {
                synchronized (cell) {
                    GameObject obj = cell.getDroppedItem(true);

                    if (obj != null) {
                        if (Logging.USE_LOG)
                            Logging.getInstance().write("Object", "GetInOnTheFloor : " + player.getName() + " a ramassé [" + obj.getTemplate().getId() + "@" + obj.getGuid() + ";" + obj.getQuantity() + "]");
                        if (player.addObjet(obj, true))
                            World.world.addGameObject(obj);
                        SocketManager.GAME_SEND_GDO_PACKET_TO_MAP(this, '-', id, 0, 0);
                        SocketManager.GAME_SEND_Ow_PACKET(player);
                    }
                }
            } else {
                SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("area.map.gamemap.onplayerarriveoncell"));
            }
        }

        InteractiveDoor.check(player, this);
        this.getCase(id).applyOnCellStopActions(player);
        if (this.placesStr.equalsIgnoreCase("|"))
            return;
        if (player.getCurMap().getId() != this.id || !player.canAggro())
            return;

        for (Monster.MobGroup group : this.mobGroups.values()) {
            if (PathFinding.getDistanceBetween(this, id, group.getCellId()) <= group.getAggroDistance()) {//S'il y aggr
                startFightVersusMonstres(player, group);
                return;
            }
        }
    }

    public void send(String packet) {
        this.getPlayers().stream().filter(Objects::nonNull).forEach(player -> player.send(packet));
    }

	public Fight newFightbouf(Player init1, Player init2, int fightTypeChallenge) {
		
		if (init1.getFight() != null || init2.getFight() != null)
            return null;
        int id = 1;
        if(this.fights == null)
            this.fights = new ArrayList<>();
        if (!this.fights.isEmpty())
            id = ((Fight) (this.fights.toArray()[this.fights.size() - 1])).getId() + 1;
        Fight f = new Fight(id, init1, init2);
        this.fights.add(f);
        SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this);
        return f;
	
	}
}
