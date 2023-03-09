package org.starloco.locos.fight;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.labyrinth.Gladiatrool;
import org.starloco.locos.area.map.labyrinth.Minotoror;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Party;
import org.starloco.locos.client.other.Stalk;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.CollectorData;
import org.starloco.locos.database.data.game.PrismData;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.dynamic.FormuleOfficiel;
import org.starloco.locos.entity.Collector;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.entity.monster.boss.Bandit;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.fight.ia.IAHandler;
import org.starloco.locos.fight.spells.LaunchedSpell;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.fight.traps.Glyph;
import org.starloco.locos.fight.traps.Trap;
import org.starloco.locos.fight.turn.Turn;
import org.starloco.locos.game.GameClient;
import org.starloco.locos.game.action.GameAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.game.world.World.Drop;
import org.starloco.locos.guild.Guild;
import org.starloco.locos.job.JobConstant;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.other.Action;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.quest.QuestPlayer;
import org.starloco.locos.util.TimerWaiter;

public class Fight {

    private int id, state = 0, guildId = -1, type = -1;
    /**
     * type/state -> byte
     **/
    private int st1, st2;
    private int curPlayer, captWinner = -1;
    private int curFighterPa, curFighterPm;
    private int curFighterUsedPa, curFighterUsedPm;
    private final Map<Integer, Fighter> team0 = new HashMap<>();
    private final Map<Integer, Fighter> team1 = new HashMap<>();
    private final Map<Integer, Fighter> deadList = new HashMap<>();
    private final Map<Integer, Player> viewer = new HashMap<>();
    private ArrayList<GameCase> start0 = new ArrayList<>();
    private ArrayList<GameCase> start1 = new ArrayList<>();
    private final Map<Integer, Challenge> allChallenges = new HashMap<>();
    private final Map<Integer, GameCase> rholBack = new HashMap<>();
    private final List<Glyph> allGlyphs = new ArrayList<>();
    private final List<Trap> allTraps = new ArrayList<>();
    private List<Fighter> orderPlaying = new ArrayList<>();
    private final ArrayList<Fighter> capturer = new ArrayList<>(8);
    private final ArrayList<Fighter> trainer = new ArrayList<>(8);
    private long launchTime = 0, startTime = 0;
    private boolean locked0 = false, locked1 = false;
    private boolean onlyGroup0 = false, onlyGroup1 = false;
    private boolean help0 = false, help1 = false;
    private boolean viewerOk = true;
    private boolean haveKnight = false;
    private boolean isBegin = false;
    private boolean checkTimer = false;
    private boolean finish = false;
    private boolean collectorProtect = false;
    private boolean ingladiatroll = false;
    private String curAction = "";
    private Monster.MobGroup mobGroup;
    private Collector collector;
    private Prism prism;
    private GameMap map, mapOld;
    private Fighter init0, init1;
    private SoulStone fullSoul;
    private Turn turn;

    private int totalTurns = 0;
    private String defenders = "";
    private int trainerWinner = -1;
    private int nextId = -100;
    private Monster.MobGroup mobGroup2;

    public Fight(int type, int id, GameMap map, Player perso, Player init2) {
        launchTime = System.currentTimeMillis();
        setType(type); // 0: D�fie (4: Pvm) 1:PVP (5:Perco)
        setId(id);
        setMap(map.getMapCopy());
        setMapOld(map);
        setInit0(new Fighter(this, perso));
        setInit1(new Fighter(this, init2));
        getTeam0().put(perso.getId(), getInit0());
        getTeam1().put(init2.getId(), getInit1());

        SocketManager.GAME_SEND_GDF_PACKET_TO_FIGHT(perso, this.getMap().getCases());
        // on disable le timer de regen cot� client
        int cancelBtn = getType() == Constant.FIGHT_TYPE_CHALLENGE ? 1 : 0;
        long time = getType() == Constant.FIGHT_TYPE_CHALLENGE ? 0 : Constant.TIME_START_FIGHT;
        SocketManager.GAME_SEND_FIGHT_GJK_PACKET_TO_FIGHT(this, 7, 2, cancelBtn, 1, 0, time, getType());
        if (init2.getAlignment() == 0 && (map.getSubArea() != null && map.getSubArea().getAlignment() == 0))
            setHaveKnight();


        int morph = perso.getGfxId();
        if (morph == 1109 || morph == 1046 || morph == 9001) {
            perso.unsetFullMorph();
            SocketManager.GAME_SEND_ALTER_GM_PACKET(perso.getCurMap(), perso);
        }

        if((init2.getStalk() == null ||  init2.getStalk().getTarget().getId() != perso.getId())) {
            this.start0 = World.world.getCryptManager().parseStartCell(getMap(), 0);
            this.start1 = World.world.getCryptManager().parseStartCell(getMap(), 1);

            SocketManager.GAME_SEND_FIGHT_PLACES_PACKET_TO_FIGHT(this, 1, getMap().getPlaces(), 0);
            SocketManager.GAME_SEND_FIGHT_PLACES_PACKET_TO_FIGHT(this, 2, getMap().getPlaces(), 1);

            getInit0().setCell(getRandomCell(this.start0));
            getInit1().setCell(getRandomCell(this.start1));
        } else {
            getInit0().setCell(getMap().getCase(perso.getCurCell().getId()));
            getInit1().setCell(getMap().getCase(init2.getCurCell().getId()));
        }

        setSt1(0);
        setSt2(1);

        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId() + "", perso.getId() + "," + Constant.ETAT_PORTE + ",0");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId() + "", perso.getId() + "," + Constant.ETAT_PORTEUR + ",0");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, init2.getId() + "", init2.getId() + "," + Constant.ETAT_PORTE + ",0");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, init2.getId() + "", init2.getId() + "," + Constant.ETAT_PORTEUR + ",0");

        if(perso.getCurCell() != null)
            perso.getCurCell().removePlayer(perso);
        if(init2.getCurCell() != null)
            init2.getCurCell().removePlayer(init2);

        getInit0().getCell().addFighter(getInit0());
        getInit1().getCell().addFighter(getInit1());
        perso.setFight(this);
        getInit0().setTeam(0);
        init2.setFight(this);
        getInit1().setTeam(1);
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(perso.getCurMap(), getInit0().getId());
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(init2.getCurMap(), getInit1().getId());

        if (getType() == 1) {
            SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_MAP(perso.getCurMap(), 0, getInit0().getId(), getInit1().getId(), perso.getCurCell().getId(), "0;"
                    + perso.getAlignment(), init2.getCurCell().getId(), "0;"
                    + init2.getAlignment());
        } else {
            SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_MAP(perso.getCurMap(), 0, getInit0().getId(), getInit1().getId(), perso.getCurCell().getId(), "0;-1", init2.getCurCell().getId(), "0;-1");
        }
        SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(perso.getCurMap(), getInit0().getId(), getInit0());
        SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(perso.getCurMap(), getInit1().getId(), getInit1());
        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS_TO_FIGHT(this, 7, getMap());

        setState(Constant.FIGHT_STATE_PLACE);
    }

    public Fight(int id, GameMap map, Monster.MobGroup group1, Monster.MobGroup group2) {
        launchTime = System.currentTimeMillis();
        setCheckTimer(true);
        setMobGroup(group1);
        setMobGroup2(group2);
        setType(Constant.FIGHT_TYPE_PVM); // (0: D�fie) 4: Pvm (1:PVP) (5:Perco)
        setId(id);
        setMap(map.getMapCopy());
        setMapOld(map);

        for (Entry<Integer, Monster.MobGrade> entry : group1.getMobs().entrySet()) {
            entry.getValue().setInFightID(entry.getKey());
            Fighter mob = new Fighter(this, entry.getValue());
            getTeam0().put(entry.getKey(), mob);
        }

        for (Entry<Integer, Monster.MobGrade> entry : group2.getMobs().entrySet()) {
            entry.getValue().setInFightID(entry.getKey());
            Fighter mob = new Fighter(this, entry.getValue());
            getTeam1().put(entry.getKey(), mob);
        }

        SocketManager.GAME_SEND_FIGHT_GJK_PACKET_TO_FIGHT(this, 1, 2, 0, 1, 0, 45000, getType());
        // on disable le timer de regen cot� client

        this.start0 = World.world.getCryptManager().parseStartCell(getMap(), 0);
        this.start1 = World.world.getCryptManager().parseStartCell(getMap(), 1);
        SocketManager.GAME_SEND_FIGHT_PLACES_PACKET_TO_FIGHT(this, 1, getMap().getPlaces(), 0);
        setSt1(0);
        setSt2(1);
        List<Entry<Integer, Fighter>> e = new ArrayList<>();
        e.addAll(getTeam1().entrySet());
        for (Entry<Integer, Fighter> entry : e) {
            Fighter f = entry.getValue();
            GameCase cell = getRandomCell(getStart1());
            if (cell == null) {
                getTeam1().remove(f.getId());
                continue;
            }
            if(getInit1() == null)
                setInit1(f);
            f.setCell(cell);
            f.getCell().addFighter(f);
            f.setTeam(1);
            f.fullPdv();
        }
        e.clear();
        e.addAll(getTeam0().entrySet());
        for (Entry<Integer, Fighter> entry : e) {
            Fighter f = entry.getValue();
            GameCase cell = getRandomCell(getStart0());
            if (cell == null) {
                getTeam0().remove(f.getId());
                continue;
            }
            if(getInit0() == null)
                setInit0(f);
            f.setCell(cell);
            f.getCell().addFighter(f);
            f.setTeam(0);
            f.fullPdv();
        }

        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getMapOld(), group1.getId());
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getMapOld(), group2.getId());

        int c = PathFinding.getNearestCellAround(getMap(), group1.getCellId(), group2.getCellId(), new ArrayList<>());
        SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_MAP(getMapOld(), 4, group1.getId(), group2.getId(), c, "0;-1", group1.getCellId(), "1;-1");

        for (Fighter f : getTeam0().values())
            SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getMapOld(), group1.getId(), f);
        for (Fighter f : getTeam1().values())
            SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getMapOld(), group2.getId(), f);
        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS_TO_FIGHT(this, 7, getMap());
        setState(Constant.FIGHT_STATE_PLACE);
        this.startFight();
    }

    public Fight(int id, GameMap map, Player perso, Monster.MobGroup group) {
        launchTime = System.currentTimeMillis();
        setCheckTimer(true);
        setMobGroup(group);
        demorph(perso);
        if(Constant.isInGladiatorDonjon(map.getId())) {
            ingladiatroll = true;
            Gladiatrool.respawn(map.getId());
        }
        setType(Constant.FIGHT_TYPE_PVM);

        setId(id);
        setMap(map.getMapCopy());
        setMapOld(map);
        setInit0(new Fighter(this, perso));
        getTeam0().put(perso.getId(), getInit0());
        for (Entry<Integer, Monster.MobGrade> entry : group.getMobs().entrySet()) {
            entry.getValue().setInFightID(entry.getKey());
            Fighter mob = new Fighter(this, entry.getValue());
            getTeam1().put(entry.getKey(), mob);
            if (entry.getValue().getTemplate().getId() == 832) // D�minoboule
                Minotoror.demi();
            else if (entry.getValue().getTemplate().getId() == 831) // Mominotoror
                Minotoror.momi();


        }


        SocketManager.GAME_SEND_FIGHT_GJK_PACKET_TO_FIGHT(this, 1, 2, 0, 1, 0, 45000, getType());
        SocketManager.GAME_SEND_GDF_PACKET_TO_FIGHT(perso, this.getMap().getCases());
        // on disable le timer de regen cot� client

        scheduleTimer(45);

        this.start0 = World.world.getCryptManager().parseStartCell(getMap(), 0);
        this.start1 = World.world.getCryptManager().parseStartCell(getMap(), 1);
        SocketManager.GAME_SEND_FIGHT_PLACES_PACKET_TO_FIGHT(this, 1, getMap().getPlaces(), 0);
        setSt1(0);
        setSt2(1);
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId() + "", perso.getId() + "," + Constant.ETAT_PORTE + ",0");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId() + "", perso.getId() + "," + Constant.ETAT_PORTEUR + ",0");
        List<Entry<Integer, Fighter>> e = new ArrayList<>();
        e.addAll(getTeam1().entrySet());
        for (Entry<Integer, Fighter> entry : e) {
            Fighter f = entry.getValue();
            GameCase cell = getRandomCell(getStart1());
            if (cell == null) {
                f.setIsDead(true);
                getTeam1().remove(f.getId());
                continue;
            }
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, f.getId() + "", f.getId() + "," + Constant.ETAT_PORTE + ",0");
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, f.getId() + "", f.getId() + "," + Constant.ETAT_PORTEUR + ",0");
            f.setCell(cell);
            f.getCell().addFighter(f);
            f.setTeam(1);
            f.fullPdv();
        }
        getInit0().setCell(getRandomCell(getStart0()));

        if (getInit0().getPlayer().getCurCell() != null)
            getInit0().getPlayer().getCurCell().removePlayer(getInit0().getPlayer());

        getInit0().getCell().addFighter(getInit0());

        getInit0().getPlayer().setFight(this);
        getInit0().setTeam(0);
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getInit0().getPlayer().getCurMap(), getInit0().getId());
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getInit0().getPlayer().getCurMap(), group.getId());

        int c = PathFinding.getNearestCellAround(getInit0().getPlayer().getCurMap(), getInit0().getPlayer().getCurCell().getId(), group.getCellId(), new ArrayList<>());
        if (c < 0)
            c = getInit0().getPlayer().getCurCell().getId();

        SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), 4, getInit0().getId(), group.getId(), c, "0;-1", group.getCellId(), "1;-1");
        SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), getInit0().getId(), getInit0());
        for (Fighter f : getTeam1().values())
            SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), group.getId(), f);
        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS_TO_FIGHT(this, 7, getMap());
        setState(Constant.FIGHT_STATE_PLACE);
    }

    public Fight(int id, GameMap map, Player perso, Monster.MobGroup group, int type) {
        launchTime = System.currentTimeMillis();
        setMobGroup(group);
        setType(type); // (0: D�fie) 4: Pvm (1:PVP) (5:Perco)
        setId(id);
        setMap(map.getMapCopy());
        setMapOld(map);
        demorph(perso);
        setInit0(new Fighter(this, perso));
        getTeam0().put(perso.getId(), getInit0());
        for (Entry<Integer, Monster.MobGrade> entry : group.getMobs().entrySet()) {
            entry.getValue().setInFightID(entry.getKey());
            Fighter mob = new Fighter(this, entry.getValue());
            getTeam1().put(entry.getKey(), mob);
            if (entry.getValue().getTemplate().getId() == 832) // D�minoboule
                Minotoror.demi();
            else if (entry.getValue().getTemplate().getId() == 831) // Mominotoror
                Minotoror.momi();
        }

        if (perso.getCurPdv() >= perso.getMaxPdv()) {
            int pdvMax = perso.getMaxPdv();
            perso.setPdv(pdvMax);
        }

        SocketManager.GAME_SEND_FIGHT_GJK_PACKET_TO_FIGHT(this, 1, 2, 0, 1, 0, 45000, getType());
        SocketManager.GAME_SEND_GDF_PACKET_TO_FIGHT(perso, this.getMap().getCases());
        // on disable le timer de regen cot� client

        scheduleTimer(45);

        this.start0 = World.world.getCryptManager().parseStartCell(getMap(), 0);
        this.start1 = World.world.getCryptManager().parseStartCell(getMap(), 1);
        SocketManager.GAME_SEND_FIGHT_PLACES_PACKET_TO_FIGHT(this, 1, getMap().getPlaces(), 0);
        setSt1(0);
        setSt2(1);
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId()
                + "", perso.getId() + "," + Constant.ETAT_PORTE + ",0");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId()
                + "", perso.getId() + "," + Constant.ETAT_PORTEUR + ",0");

        List<Entry<Integer, Fighter>> e = new ArrayList<>();
        e.addAll(getTeam1().entrySet());
        for (Entry<Integer, Fighter> entry : e) {
            Fighter f = entry.getValue();
            GameCase cell = getRandomCell(getStart1());
            if (cell == null) {
                getTeam1().remove(f.getId());
                continue;
            }

            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, f.getId()
                    + "", f.getId() + "," + Constant.ETAT_PORTE + ",0");
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, f.getId()
                    + "", f.getId() + "," + Constant.ETAT_PORTEUR + ",0");
            f.setCell(cell);
            f.getCell().addFighter(f);
            f.setTeam(1);
            f.fullPdv();
        }
        getInit0().setCell(getRandomCell(getStart0()));

        getInit0().getPlayer().getCurCell().removePlayer(getInit0().getPlayer());

        getInit0().getCell().addFighter(getInit0());

        getInit0().getPlayer().setFight(this);
        getInit0().setTeam(0);
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getInit0().getPlayer().getCurMap(), getInit0().getId());
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getInit0().getPlayer().getCurMap(), group.getId());
        SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), getInit0().getId(), getInit0());
        for (Fighter f : getTeam1().values())
            SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), group.getId(), f);
        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS_TO_FIGHT(this, 7, getMap());
        setState(Constant.FIGHT_STATE_PLACE);
    }

    public Fight(int id, GameMap map, Player perso, Collector perco) {
        if (perso.getFight() != null)
            return;
        launchTime = System.currentTimeMillis();
        setGuildId(perco.getGuildId());
        perco.setInFight((byte) 1);
        perco.set_inFightID((byte) id);

        demorph(perso);

        setType(Constant.FIGHT_TYPE_PVT); // (0: D�fie) (4: Pvm) (1:PVP) 5:Perco
        setId(id);
        setMap(map.getMapCopy());
        setMapOld(map);
        setInit0(new Fighter(this, perso));
        setCollector(perco);
        // on disable le timer de regen cot� client

        getTeam0().put(perso.getId(), getInit0());

        Fighter percoF = new Fighter(this, perco);
        getTeam1().put(-1, percoF);

        SocketManager.GAME_SEND_FIGHT_GJK_PACKET_TO_FIGHT(this, 1, 2, 0, 1, 0, 45000, getType()); // timer de combat
        SocketManager.GAME_SEND_GDF_PACKET_TO_FIGHT(perso, this.getMap().getCases());
        scheduleTimer(45);

        if (Formulas.random.nextBoolean()) {
            this.start0 = World.world.getCryptManager().parseStartCell(getMap(), 0);
            this.start1 = World.world.getCryptManager().parseStartCell(getMap(), 1);
            SocketManager.GAME_SEND_FIGHT_PLACES_PACKET_TO_FIGHT(this, 1, getMap().getPlaces(), 0);
            setSt1(0);
            setSt2(1);
        } else {
            this.start0 = World.world.getCryptManager().parseStartCell(getMap(), 1);
            this.start1 = World.world.getCryptManager().parseStartCell(getMap(), 0);
            setSt1(1);
            setSt2(0);
            SocketManager.GAME_SEND_FIGHT_PLACES_PACKET_TO_FIGHT(this, 1, getMap().getPlaces(), 1);
        }
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId()
                + "", perso.getId() + "," + Constant.ETAT_PORTE + ",0");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId()
                + "", perso.getId() + "," + Constant.ETAT_PORTEUR + ",0");

        List<Entry<Integer, Fighter>> e = new ArrayList<>();
        e.addAll(getTeam1().entrySet());
        for (Entry<Integer, Fighter> entry : e) {
            Fighter f = entry.getValue();
            GameCase cell = getRandomCell(this.start1);
            if (cell == null) {
                getTeam1().remove(f.getId());
                continue;
            }

            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, f.getId()
                    + "", f.getId() + "," + Constant.ETAT_PORTE + ",0");
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, f.getId()
                    + "", f.getId() + "," + Constant.ETAT_PORTEUR + ",0");
            f.setCell(cell);
            f.getCell().addFighter(f);
            f.setTeam(1);
            f.fullPdv();

        }
        getInit0().setCell(getRandomCell(this.start0));

        getInit0().getPlayer().getCurCell().removePlayer(getInit0().getPlayer());

        getInit0().getCell().addFighter(getInit0());

        getInit0().getPlayer().setFight(this);
        getInit0().setTeam(0);

        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getInit0().getPlayer().getCurMap(), getInit0().getId());
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getInit0().getPlayer().getCurMap(), perco.getId());

        int c = PathFinding.getNearestCellAround(getInit0().getPlayer().getCurMap(), getInit0().getPlayer().getCurCell().getId(), perco.getCell(), new ArrayList<>());
        if (c < 0)
            c = getInit0().getPlayer().getCurCell().getId();

        SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), 5, getInit0().getId(), perco.getId(), c, "0;-1", perco.getCell(), "3;-1");
        SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), getInit0().getId(), getInit0());

        for (Fighter f : getTeam1().values())
            SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), perco.getId(), f);

        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS_TO_FIGHT(this, 7, getMap());
        setState(Constant.FIGHT_STATE_PLACE);

        String str = "";
        if (this.getCollector() != null)
            str = "A" + this.getCollector().getFullName() + "|.|" + World.world.getMap(getCollector().getMap()).getX() + "|" + World.world.getMap(getCollector().getMap()).getY();

        for (Player z : World.world.getGuild(getGuildId()).getPlayers()) {
            if (z == null)
                continue;
            if (z.isOnline()) {
                SocketManager.GAME_SEND_gITM_PACKET(z, Collector.parseToGuild(z.getGuild().getId()));
                Collector.parseAttaque(z, getGuildId());
                Collector.parseDefense(z, getGuildId());
                SocketManager.SEND_gA_PERCEPTEUR(z, str);
            }
        }
    }

    public Fight(int id, GameMap Map, Player player, Prism prism) {
        launchTime = System.currentTimeMillis();
        prism.setFight(this);
        demorph(player);
        setType(Constant.FIGHT_TYPE_CONQUETE); // (0: Desafio) (4: Pvm) (1:PVP)
        // 5:Perco
        setId(id);
        setMap(Map.getMapCopy());
        setMapOld(Map);
        setInit0(new Fighter(this, player));
        setPrism(prism);

        getTeam0().put(player.getId(), getInit0());
        Fighter lPrisme = new Fighter(this, prism);
        setInit1(lPrisme);
        getTeam1().put(-1, lPrisme);
        SocketManager.GAME_SEND_FIGHT_GJK_PACKET_TO_FIGHT(this, 1, 2, 0, 1, 0, 60000, getType());
        SocketManager.GAME_SEND_GDF_PACKET_TO_FIGHT(player, this.getMap().getCases());
        scheduleTimer(60);

        if (Formulas.random.nextBoolean()) {
            this.start0 = World.world.getCryptManager().parseStartCell(getMap(), 0);
            this.start1 = World.world.getCryptManager().parseStartCell(getMap(), 1);
            SocketManager.GAME_SEND_FIGHT_PLACES_PACKET_TO_FIGHT(this, 1, getMap().getPlaces(), 0);
            setSt1(0);
            setSt2(1);
        } else {
            this.start0 = World.world.getCryptManager().parseStartCell(getMap(), 1);
            this.start1 = World.world.getCryptManager().parseStartCell(getMap(), 0);
            setSt1(1);
            setSt2(0);
            SocketManager.GAME_SEND_FIGHT_PLACES_PACKET_TO_FIGHT(this, 1, getMap().getPlaces(), 1);
        }
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, player.getId()
                + "", player.getId() + "," + Constant.ETAT_PORTE + ",0");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, player.getId()
                + "", player.getId() + "," + Constant.ETAT_PORTEUR + ",0");

        List<Entry<Integer, Fighter>> e = new ArrayList<>();
        e.addAll(getTeam1().entrySet());
        for (Entry<Integer, Fighter> entry : e) {
            Fighter f = entry.getValue();
            GameCase cell = getRandomCell(getStart1());
            if (cell == null) {
                getTeam1().remove(f.getId());
                continue;
            }
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, f.getId()
                    + "", f.getId() + "," + Constant.ETAT_PORTE + ",0");
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, f.getId()
                    + "", f.getId() + "," + Constant.ETAT_PORTEUR + ",0");
            f.setCell(cell);
            f.getCell().addFighter(f);
            f.setTeam(1);
            f.fullPdv();
        }
        getInit0().setCell(getRandomCell(getStart0()));
        getInit0().getPlayer().getCurCell().removePlayer(getInit0().getPlayer());
        getInit0().getCell().addFighter(getInit0());
        getInit0().getPlayer().setFight(this);
        getInit0().setTeam(0);
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getInit0().getPlayer().getCurMap(), getInit0().getId());
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getInit0().getPlayer().getCurMap(), prism.getId());

        int c = PathFinding.getNearestCellAround(getInit0().getPlayer().getCurMap(), getInit0().getPlayer().getCurCell().getId(), prism.getCell(), new ArrayList<>());
        if (c < 0)
            c = getInit0().getPlayer().getCurCell().getId();

        SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), 0, getInit0().getId(), prism.getId(), c, "0;"
                + getInit0().getPlayer().getAlignment(), prism.getCell(), "0;"
                + prism.getAlignment());
        SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), getInit0().getId(), getInit0());
        for (Fighter f : getTeam1().values())
            SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), prism.getId(), f);
        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS_TO_FIGHT(this, 7, getMap());
        setState(Constant.FIGHT_STATE_PLACE);
        String str = "";
        if (getPrism() != null)
            str = prism.getMap() + "|" + prism.getX() + "|" + prism.getY();
        for (Player z : World.world.getOnlinePlayers()) {
            if (z == null)
                continue;
            if (z.getAlignment() != prism.getAlignment())
                continue;
            SocketManager.SEND_CA_ATTAQUE_MESSAGE_PRISME(z, str);
        }
    }

    // BOUFBWAL
    public Fight(int id, Player perso, Player init2) {
        if (perso.getCurMap().getId() != 9862)
            return;
        perso.setFullMorphbouf(0);
        init2.setFullMorphbouf(1);


        SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(perso.getCurMap(), "", -1, "Boumawa", "fight.fight.boufbwal.start.fight", null);

        launchTime = System.currentTimeMillis();
        setType(7);
        setId(id);
        setMap(perso.getCurMap().getMapCopy());
        setMapOld(perso.getCurMap());
        setInit0(new Fighter(this, perso));
        setInit1(new Fighter(this, init2));
        getTeam0().put(perso.getId(), getInit0());
        getTeam1().put(init2.getId(), getInit1());


        Fighter mob = new Fighter(this, World.world.getMonstre(2931).getGradeByLevel(3));
        mob.isStatique = true;
        getTeam1().put(215, mob);

        SocketManager.GAME_SEND_GDF_PACKET_TO_FIGHT(perso, this.getMap().getCases());
        //on desactive le timer de regen cot� client
        int cancelBtn = 1;
        long time = 60000;
        scheduleTimer(60);
        SocketManager.GAME_SEND_FIGHT_GJK_PACKET_TO_FIGHT(this, 7, 2, cancelBtn, 1, 0, time, 0);


        int morph = perso.getGfxId();
        if (morph == 1109 || morph == 1046 || morph == 9001) {
            perso.unsetFullMorph();
            SocketManager.GAME_SEND_ALTER_GM_PACKET(perso.getCurMap(), perso);
        }


        this.start0 = World.world.getCryptManager().parseStartCell(getMap(), 0);
        this.start1 = World.world.getCryptManager().parseStartCell(getMap(), 1);
        SocketManager.GAME_SEND_FIGHT_PLACES_PACKET_TO_FIGHT(this, 1, getMap().getPlaces(), 0);
        SocketManager.GAME_SEND_FIGHT_PLACES_PACKET_TO_FIGHT(this, 2, getMap().getPlaces(), 1);
        setSt1(0);
        setSt2(1);

        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId() + "", perso.getId() + "," + Constant.ETAT_PORTE + ",0");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId() + "", perso.getId() + "," + Constant.ETAT_PORTEUR + ",0");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, init2.getId() + "", init2.getId() + "," + Constant.ETAT_PORTE + ",0");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, init2.getId() + "", init2.getId() + "," + Constant.ETAT_PORTEUR + ",0");


        mob.setCell(this.getMap().getCase(239));
        start0.remove(this.getMap().getCase(239));
        start1.remove(this.getMap().getCase(239));
        getInit0().setCell(getRandomCell(this.start0));
        getInit1().setCell(getRandomCell(this.start1));


        getInit0().getPlayer().getCurCell().removePlayer(getInit0().getPlayer());
        getInit1().getPlayer().getCurCell().removePlayer(getInit1().getPlayer());

        getInit0().getCell().addFighter(getInit0());
        getInit1().getCell().addFighter(getInit1());
        mob.getCell().addFighter(mob);
        getInit0().getPlayer().setFight(this);
        getInit0().setTeam(0);
        getInit1().getPlayer().setFight(this);
        getInit1().setTeam(1);
        mob.setTeam(1);


        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getInit0().getPlayer().getCurMap(), getInit0().getId());
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getInit1().getPlayer().getCurMap(), getInit1().getId());
        if (getType() == 1) {
            SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), 0, getInit0().getId(), getInit1().getId(), getInit0().getPlayer().getCurCell().getId(), "0;"
                    + getInit0().getPlayer().getAlignment(), getInit1().getPlayer().getCurCell().getId(), "0;"
                    + getInit1().getPlayer().getAlignment());
        } else {
            SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), 0, getInit0().getId(), getInit1().getId(), getInit0().getPlayer().getCurCell().getId(), "0;-1", getInit1().getPlayer().getCurCell().getId(), "0;-1");
        }
        SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), getInit0().getId(), getInit0());
        SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), getInit1().getId(), getInit1());

        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS_TO_FIGHT(this, 7, getMap());

        setState(Constant.FIGHT_STATE_PLACE);


    }

    public static void FightStateAddFlag(GameMap map, Player player) {
        map.getFights().stream().filter(fight -> fight.state == Constant.FIGHT_STATE_PLACE).forEach(fight -> {
            if (fight.type == Constant.FIGHT_TYPE_CHALLENGE) {
                SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_PLAYER(player, fight.init0.getPlayer().getCurMap(), 0,
                        fight.init0.getId(), fight.init1.getId(), fight.init0.getPlayer().getCurCell().getId(), "0;-1", fight.init1.getPlayer().getCurCell().getId(), "0;-1");
            } else if (fight.type == Constant.FIGHT_TYPE_AGRESSION) {
                SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_PLAYER(player, fight.init0.getPlayer().getCurMap(), 0,
                        fight.init0.getId(), fight.init1.getId(), fight.init0.getPlayer().getCurCell().getId(), "0;" + fight.init0.getPlayer().getAlignment(), fight.init1.getPlayer().getCurCell().getId(), "0;" + fight.init1.getPlayer().getAlignment());
            } else if (fight.type == Constant.FIGHT_TYPE_PVM) {
                SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_PLAYER(player, fight.init0.getPlayer().getCurMap(), 4,
                        fight.init0.getId(), fight.mobGroup.getId(), (fight.init0.getPlayer().getCurCell().getId() + 1), "0;-1", fight.mobGroup.getCellId(), "1;-1");
            } else if (fight.type == Constant.FIGHT_TYPE_PVT) {
                SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_PLAYER(player, fight.init0.getPlayer().getCurMap(), 5,
                        fight.init0.getId(), fight.collector.getId(), (fight.init0.getPlayer().getCurCell().getId() + 1), "0;-1", fight.collector.getCell(), "3;-1");
            } else if (fight.type == Constant.FIGHT_TYPE_CONQUETE) {
                SocketManager.GAME_SEND_GAME_ADDFLAG_PACKET_TO_PLAYER(player, fight.init0.getPlayer().getCurMap(), 0,
                        fight.init0.getId(), fight.prism.getId(), fight.init0.getPlayer().getCurCell().getId(), "0;" + fight.init0.getPlayer().getAlignment(), fight.prism.getCell(), "0;" + fight.prism.getAlignment());
            }
            SocketManager.GAME_SEND_REFRESH_TEAM_PACKET_TO_MAP(map, fight.init0.getId(), fight.team0.values());
            int id = fight.init1 == null ? (fight.collector == null ? (fight.prism == null ? -1 : fight.prism.getId()) : fight.collector.getId()) : fight.init1.getId();
            SocketManager.GAME_SEND_REFRESH_TEAM_PACKET_TO_MAP(map, id, fight.team1.values());
        });
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    void setState(int state) {
        this.state = state;
    }

    private int getGuildId() {
        return guildId;
    }

    private void setGuildId(int guildId) {
        this.guildId = guildId;
    }

    public int getType() {

        return type;
    }

    void setType(int type) {
        this.type = type;
    }

    private int getSt1() {
        return st1;
    }

    private void setSt1(int st1) {
        this.st1 = st1;
    }

    private int getSt2() {
        return st2;
    }

    private void setSt2(int st2) {
        this.st2 = st2;
    }

    public int getCurPlayer() {
        return curPlayer;
    }

    public void setCurPlayer(int curPlayer) {
        this.curPlayer = curPlayer;
    }

    private int getCaptWinner() {
        return captWinner;
    }

    private void setCaptWinner(int captWinner) {
        this.captWinner = captWinner;
    }

    public int getCurFighterPa() {
        return curFighterPa;
    }

    public void setCurFighterPa(int curFighterPa) {
        this.curFighterPa = curFighterPa;
    }

    public int getCurFighterPm() {
        return curFighterPm;
    }

    public void setCurFighterPm(int curFighterPm) {
        this.curFighterPm = curFighterPm;
    }

    private int getCurFighterUsedPa() {
        return curFighterUsedPa;
    }

    private void setCurFighterUsedPa() {
        this.curFighterUsedPa = 0;
    }

    int getCurFighterUsedPm() {
        return curFighterUsedPm;
    }

    private void setCurFighterUsedPm() {
        this.curFighterUsedPm = 0;
    }

    public Map<Integer, Fighter> getTeam(int team) {
        switch (team) {
            case 1:
                return team0;
            case 2:
                return team1;
        }
        return team0;
    }

    public Map<Integer, Fighter> getTeam0() {
        return team0;
    }

    public Map<Integer, Fighter> getTeam1() {
        return team1;
    }

    public Map<Integer, Fighter> getDeadList() {
        return deadList;
    }

    public boolean removeDead(Fighter target) {
        return deadList.remove(target.getId(), target);
    }

    Map<Integer, Player> getViewer() {
        return viewer;
    }

    ArrayList<GameCase> getStart0() {
        return start0;
    }

    ArrayList<GameCase> getStart1() {
        return start1;
    }

    public Map<Integer, Challenge> getAllChallenges() {
        return allChallenges;
    }

    public Map<Integer, GameCase> getRholBack() {
        return rholBack;
    }

    public List<Glyph> getAllGlyphs() {
        return allGlyphs;
    }

    public List<Trap> getAllTraps() {
        return allTraps;
    }

    ArrayList<Fighter> getCapturer() {
        return capturer;
    }

    ArrayList<Fighter> getTrainer() {
        return trainer;
    }

    public long getStartTime() {
        return startTime;
    }

    void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getLaunchTime() {
        return launchTime;
    }

    boolean isLocked0() {
        return locked0;
    }

    void setLocked0(boolean locked0) {
        this.locked0 = locked0;
    }

    boolean isLocked1() {
        return locked1;
    }

    void setLocked1(boolean locked1) {
        this.locked1 = locked1;
    }

    boolean isOnlyGroup0() {
        return onlyGroup0;
    }

    void setOnlyGroup0(boolean onlyGroup0) {
        this.onlyGroup0 = onlyGroup0;
    }

    boolean isOnlyGroup1() {
        return onlyGroup1;
    }

    void setOnlyGroup1(boolean onlyGroup1) {
        this.onlyGroup1 = onlyGroup1;
    }

    boolean isHelp0() {
        return help0;
    }

    void setHelp0(boolean help0) {
        this.help0 = help0;
    }

    boolean isHelp1() {
        return help1;
    }

    void setHelp1(boolean help1) {
        this.help1 = help1;
    }

    boolean isViewerOk() {
        return viewerOk;
    }

    void setViewerOk(boolean viewerOk) {
        this.viewerOk = viewerOk;
    }

    boolean isHaveKnight() {
        return haveKnight;
    }

    void setHaveKnight() {
        this.haveKnight = true;
    }

    public boolean isBegin() {
        return isBegin;
    }

    void setBegin() {
        this.isBegin = true;
    }

    boolean isCheckTimer() {
        return checkTimer;
    }

    private void setCheckTimer(boolean checkTimer) {
        this.checkTimer = checkTimer;
    }

    public String getCurAction() {
        return curAction;
    }

    public void setCurAction(String curAction) {
        this.curAction = curAction;
    }

    Monster.MobGroup getMobGroup() {
        return mobGroup;
    }

    void setMobGroup(Monster.MobGroup mobGroup) {
        this.mobGroup = mobGroup;
    }

    Collector getCollector() {
        return collector;
    }

    void setCollector(Collector collector) {
        this.collector = collector;
    }

    public Prism getPrism() {
        return prism;
    }

    void setPrism(Prism prism) {
        this.prism = prism;
    }

    public GameMap getMap() {
        return map;
    }

    void setMap(GameMap map) {
        this.map = map;
    }

    public GameMap getMapOld() {
        return mapOld;
    }

    void setMapOld(GameMap mapOld) {
        this.mapOld = mapOld;
    }

    public Fighter getInit0() {
        return init0;
    }

    void setInit0(Fighter init0) {
        this.init0 = init0;
    }

    public Fighter getInit1() {
        return init1;
    }

    void setInit1(Fighter init1) {
        this.init1 = init1;
    }

    SoulStone getFullSoul() {
        return fullSoul;
    }

    void setFullSoul(SoulStone fullSoul) {
        this.fullSoul = fullSoul;
    }

    String getDefenders() {
        return defenders;
    }

    public void setDefenders(String defenders) {
        this.defenders = defenders;
    }

    int getTrainerWinner() {
        return trainerWinner;
    }

    void setTrainerWinner(int trainerWinner) {
        this.trainerWinner = trainerWinner;
    }

    public boolean isFinish() {
        return finish;
    }

    public int getTeamId(int guid) {
        if (getTeam0().containsKey(guid))
            return 1;
        if (getTeam1().containsKey(guid))
            return 2;
        if (getViewer().containsKey(guid))
            return 4;
        return -1;
    }

    public int getOtherTeamId(int guid) {
        if (getTeam0().containsKey(guid))
            return 2;
        if (getTeam1().containsKey(guid))
            return 1;
        return -1;
    }

    void scheduleTimer(int time) {
        TimerWaiter.addNext(() -> {
            if (!this.isBegin) {
                if (this.getState() != Constant.FIGHT_STATE_ACTIVE)
                    this.startFight();
            }
        }, time, TimeUnit.SECONDS);
    }

    private void demorph(Player p) {
        if (!p.getMorphMode() && p.isMorph() && (p.getGroupe() == null) && (p.getMorphId() != 8006 && p.getMorphId() != 8007 && p.getMorphId() != 8009))
            p.unsetMorph();
    }

    public void startFight() {
        this.launchTime = -1;
        this.startTime = System.currentTimeMillis();
        if (this.collector != null && !this.collectorProtect) {
            ArrayList<Player> protectors = new ArrayList<>(collector.getDefenseFight().values());
            for (Player player : protectors) {
                if (player.getFight() == null && !player.isAway()) {
                    player.setOldPosition();

                    if (player.getCurMap().getId() != this.getMapOld().getId()) {
                        player.teleport(this.getMapOld().getId(), this.collector.getCell());
                    }

                    TimerWaiter.addNext(() -> this.joinCollectorFight(player, collector.getId()), 1000);
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("fight.startfight.collector.error"));
                    collector.delDefenseFight(player);
                }
                player.send("gITP-" + collector.getId() + "|" + Integer.toString(player.getId(), 36));
            }

            this.collectorProtect = true;
            this.scheduleTimer(15);
            return;
        }

        if (getState() >= Constant.FIGHT_STATE_ACTIVE)
            return;

        if (this.getType() == Constant.FIGHT_TYPE_PVM) {
            if (this.getMobGroup().isFix() && isCheckTimer() && this.getMapOld().getId() != 6826 && this.getMapOld().getId() != 10332 && this.getMapOld().getId() != 7388)
                this.getMapOld().spawnAfterTimeGroupFix(this.getMobGroup().getCellId());// Respawn d'un groupe fix
            if (!Config.modeHeroic)
                if (!this.getMobGroup().isFix() && this.isCheckTimer())
                    this.getMapOld().spawnAfterTimeGroup();// Respawn d'un groupe
        }

        if (getType() == Constant.FIGHT_TYPE_CONQUETE) {
            for (Player z : World.world.getOnlinePlayers()) {
                if (z == null)
                    continue;
                if (z.getAlignment() == getPrism().getAlignment()) {
                    Prism.parseAttack(z);
                    Prism.parseDefense(z);
                }
            }
        }

        if (getType() == Constant.FIGHT_TYPE_PVT && getCollector() != null)
            getCollector().setInFight((byte) 2);

        setState(Constant.FIGHT_STATE_ACTIVE);
        setStartTime(System.currentTimeMillis());
        SocketManager.GAME_SEND_GAME_REMFLAG_PACKET_TO_MAP(getMapOld(), getInit0().getId());

        for (Fighter fighter : this.getFighters(3)) {
            Player player = fighter.getPlayer();

            if (player != null) {
                player.refreshObjectsClass();
            }
        }

        if (isHaveKnight() && getType() == Constant.FIGHT_TYPE_AGRESSION)
            addChevalier();

        setCheckTimer(false);
        SocketManager.GAME_SEND_GIC_PACKETS_TO_FIGHT(this, 7);
        SocketManager.GAME_SEND_GS_PACKET_TO_FIGHT(this, 7);
        initOrderPlaying();
        setCurPlayer(-1);
        SocketManager.GAME_SEND_GTL_PACKET_TO_FIGHT(this, 7);
        SocketManager.GAME_SEND_GTM_PACKET_TO_FIGHT(this, 7);

        /** Challenges **/
        if ((getType() == Constant.FIGHT_TYPE_PVM
                || getType() == Constant.FIGHT_TYPE_DOPEUL) && !ingladiatroll) {
            boolean hasMale = false, hasFemale = false, hasDisciple = false;
            boolean hasCawotte = false, hasChafer = false, hasRoulette = false, hasArakne = false, hasArround = false;
            boolean severalEnnemies, severalAllies, bothSexes, EvenEnnemies, MoreEnnemies, ecartLvlPlayer = false;
            int hasBoss = -1;

            if (this.getTeam0().size() > 1) {
                int lowLvl1 = 201, lowLvl2 = 201;

                for (Fighter fighter : getTeam0().values())
                    if (fighter.getLvl() < lowLvl1)
                        lowLvl1 = fighter.getLvl();
                for (Fighter fighter : getTeam0().values())
                    if (fighter.getLvl() < lowLvl2
                            && fighter.getLvl() > lowLvl1)
                        lowLvl2 = fighter.getLvl();
                if (lowLvl2 - lowLvl1 > 10)
                    ecartLvlPlayer = true;
            }

            for (Fighter f : getTeam0().values()) {
                Player player = f.getPlayer();
                if (f.getPlayer() != null) {
                    switch (player.getClasse()) {
                        case Constant.CLASS_OSAMODAS:
                        case Constant.CLASS_FECA:
                        case Constant.CLASS_SADIDA:
                        case Constant.CLASS_XELOR:
                        case Constant.CLASS_SRAM:
                            hasDisciple = true;
                            break;
                    }

                    if (player.hasSpell(367))
                        hasCawotte = true;
                    if (player.hasSpell(373))
                        hasChafer = true;
                    if (player.hasSpell(101))
                        hasRoulette = true;
                    if (player.hasSpell(370))
                        hasArakne = true;
                    if (player.getSexe() == 0)
                        hasMale = true;
                    if (player.getSexe() == 1)
                        hasFemale = true;
                }
            }


            for (Fighter fighter : getTeam1().values()) {
                if (fighter.getMob() != null) {
                    if (fighter.getMob().getTemplate() != null) {
                        if (Constant.BOSS.contains(String.valueOf(fighter.getMob().getTemplate().getId())))
                            hasBoss = fighter.getMob().getTemplate().getId();
                        for (Fighter fighter2 : getTeam0().values())
                            if (PathFinding.getDistanceBetween(this.getMap(), fighter2.getCell().getId(), fighter.getCell().getId()) >= 5)
                                hasArround = true;

                    }
                }
            }

            for (Fighter fighter : getTeam1().values()) {
                if (fighter.getMob() != null) {
                    if (fighter.getMob().getTemplate() != null) {
                        switch (fighter.getMob().getTemplate().getId()) {
                            case 98:// Tofu all
                            case 111:
                            case 120:
                            case 382:
                            case 473:
                            case 794:
                            case 796:
                            case 800:
                            case 801:
                            case 803:
                            case 805:
                            case 806:
                            case 807:
                            case 808:
                            case 841:
                            case 847:
                            case 868:
                            case 970:
                            case 171:// Dragodinde all
                            case 200:
                            case 666:
                            case 582:// TouchParak
                                hasArround = false;
                                break;
                        }
                    }
                }
            }

            severalEnnemies = (getTeam1().size() >= 2);
            severalAllies = (getTeam0().size() >= 2);
            bothSexes = (!(!hasMale || !hasFemale));
            EvenEnnemies = (getTeam1().size() % 2 == 0);
            MoreEnnemies = (getTeam1().size() >= getTeam0().size());

            String challenges = World.world.getChallengeFromConditions(severalEnnemies, severalAllies, bothSexes, EvenEnnemies, MoreEnnemies, hasCawotte, hasChafer, hasRoulette, hasArakne, hasBoss, ecartLvlPlayer, hasArround, hasDisciple, (this.getTeam0().size() != 1));
            String[] chalInfo;

            int challengeID, challengeXP, challengeDP, bonusGroupe;
            int challengeNumber = ((this.getMapOld().hasEndFightAction(this.getType()) || SoulStone.isInArenaMap(this.getMapOld().getId())) ? 2 : 1);

            for (String chalInfos : World.world.getRandomChallenge(challengeNumber, challenges)) {
                chalInfo = chalInfos.split(",");
                challengeID = Integer.parseInt(chalInfo[0]);
                challengeXP = Integer.parseInt(chalInfo[1]);
                challengeDP = Integer.parseInt(chalInfo[2]);
                bonusGroupe = Integer.parseInt(chalInfo[3]);
                bonusGroupe *= getTeam1().size();
                getAllChallenges().put(challengeID, new Challenge(this, challengeID, challengeXP + bonusGroupe, challengeDP + bonusGroupe));
            }
            for (Entry<Integer, Challenge> c : getAllChallenges().entrySet()) {
                if (c.getValue() == null)
                    continue;
                c.getValue().fightStart();
                SocketManager.GAME_SEND_CHALLENGE_FIGHT(this, 1, c.getValue().parseToPacket());
            }
        }
        /** Challenges **/

        for (Fighter F : getFighters(3)) {
            Player player = F.getPlayer();
            if (player != null) {
                player.setReady(false);
                if (player.isOnMount())
                    SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, player.getId() + "", player.getId() + "," + Constant.ETAT_CHEVAUCHANT + ",1");
            }
        }

        this.startTurn();
        this.getFighters(3).stream().filter(Objects::nonNull).forEach(F -> getRholBack().put(F.getId(), F.getCell()));
        this.setBegin();
    }

    public void leftFight(Player playerCaster, Player playerTarget) {
        if (playerCaster == null)
            return;

        final Fighter caster = this.getFighterByPerso(playerCaster), target = (playerTarget != null ? this.getFighterByPerso(playerTarget) : null);

        if (caster != null) {
            switch (getType()) {
                case 7://boufbowl
                    if (this.getState() >= Constant.FIGHT_STATE_ACTIVE)
                        break;
                case Constant.FIGHT_TYPE_ROYAL:
                case Constant.FIGHT_TYPE_CHALLENGE:
                case Constant.FIGHT_TYPE_AGRESSION:
                case Constant.FIGHT_TYPE_PVM:
                case Constant.FIGHT_TYPE_PVT:
                case Constant.FIGHT_TYPE_CONQUETE:
                case Constant.FIGHT_TYPE_DOPEUL:
                    if (this.getState() >= Constant.FIGHT_STATE_ACTIVE) {
                        //region Active
                        caster.setLeft(true);
                        this.onFighterDie(caster, caster);

                        if (this.getFighterByGameOrder() != null && this.getFighterByGameOrder().getId() == caster.getId())
                            endTurn(false, caster);

                        final Player player = caster.getPlayer();
                        player.setDuelId(-1);
                        player.setReady(false);
                        player.setFight(null);
                        player.setAway(false);

                        this.verifIfTeamAllDead();


                        if (!this.finish) {
                            this.onPlayerLoose(caster);
                            SocketManager.GAME_SEND_GV_PACKET(caster.getPlayer());
                        }
                        //endregion
                    } else if (getState() == Constant.FIGHT_STATE_PLACE) {
                        //region Place
                        boolean isValid = false;
                        if (target != null) {
                            if (getInit0() != null && getInit0().getPlayer() != null && caster.getPlayer().getId() == getInit0().getPlayer().getId())
                                isValid = true;
                            if (getInit1() != null && getInit1().getPlayer() != null && caster.getPlayer().getId() == getInit1().getPlayer().getId())
                                isValid = true;
                        }

                        if (isValid) {// Celui qui fait l'action a lancer le combat et leave un autre personnage
                            //region Cas 0
                            if ((target.getTeam() == caster.getTeam()) && (target.getId() != caster.getId())) {
                                SocketManager.GAME_SEND_ON_FIGHTER_KICK(this, target.getPlayer().getId(), getTeamId(target.getId()));

                                if (getType() == Constant.FIGHT_TYPE_AGRESSION || getType() == Constant.FIGHT_TYPE_CHALLENGE || getType() == Constant.FIGHT_TYPE_PVT || getType() == Constant.FIGHT_TYPE_CONQUETE || getType() == Constant.FIGHT_TYPE_DOPEUL)
                                    SocketManager.GAME_SEND_ON_FIGHTER_KICK(this, target.getPlayer().getId(), getOtherTeamId(target.getId()));

                                final Player player = target.getPlayer();
                                player.setDuelId(-1);
                                player.setReady(false);
                                player.setFight(null);
                                player.setAway(false);

                                if (player.isOnline())
                                    SocketManager.GAME_SEND_GV_PACKET(player);

                                if (this.getType() == 7) {
                                    player.unsetFullMorphbouf();
                                    player.fullPDV();
                                    player.refreshLife(false);
                                }

                                // On le supprime de la team
                                if (this.getTeam0().containsKey(target.getId())) {
                                    target.getCell().removeFighter(target);
                                    this.getTeam0().remove(target.getId());
                                    SocketManager.GAME_SEND_REMOVE_IN_TEAM_PACKET_TO_MAP(getMapOld(), this.init0.getId() , target);
                                } else if (this.getTeam1().containsKey(target.getId())) {
                                    target.getCell().removeFighter(target);
                                    this.getTeam1().remove(target.getId());
                                    SocketManager.GAME_SEND_REMOVE_IN_TEAM_PACKET_TO_MAP(getMapOld(), this.init1.getId(), target);
                                }
                            }
                            //endregion
                        } else if (target == null) {// Il leave de son plein gr� donc (target = null)
                            boolean isValid2 = false;
                            if (this.getInit0() != null && this.getInit0().getPlayer() != null && caster.getPlayer().getId() == this.getInit0().getPlayer().getId())
                                isValid2 = true;
                            if (this.getInit1() != null && this.getInit1().getPlayer() != null && caster.getPlayer().getId() == this.getInit1().getPlayer().getId())
                                isValid2 = true;

                            if (isValid2) {// Soit il a lancer le combat => annulation du combat
                                //region Cas 1
                                for (Fighter fighter : this.getFighters(caster.getTeam2())) {
                                    final Player player = fighter.getPlayer();
                                    player.setDuelId(-1);
                                    player.setReady(false);
                                    player.setFight(null);
                                    player.setAway(false);
                                    fighter.setLeft(true);

                                    if (caster.getPlayer().getId() != fighter.getPlayer().getId()) {// Celui qui a join le fight revient sur la map
                                        if (player.isOnline())
                                            SocketManager.GAME_SEND_GV_PACKET(player);
                                    } else {// Celui qui a fait le fight meurt + perte honor
                                        if (getType() == Constant.FIGHT_TYPE_ROYAL || this.getType() == Constant.FIGHT_TYPE_AGRESSION || getType() == Constant.FIGHT_TYPE_PVM || getType() == Constant.FIGHT_TYPE_PVT || getType() == Constant.FIGHT_TYPE_CONQUETE) {
                                            int looseEnergy = Formulas.getLoosEnergy(player.getLevel(), getType() == 1, getType() == 5), totalEnergy = player.getEnergy() - looseEnergy;
                                            if (totalEnergy < 0) totalEnergy = 0;

                                            player.setEnergy(totalEnergy);
                                            player.setMascotte(0);

                                            if (player.isOnline())
                                                SocketManager.GAME_SEND_Im_PACKET(player, "034;" + looseEnergy);

                                            if (caster.getPlayer().getObjetByPos(Constant.ITEM_POS_FAMILIER) != null) {
                                                GameObject obj = caster.getPlayer().getObjetByPos(Constant.ITEM_POS_FAMILIER);
                                                if (obj != null) {
                                                    PetEntry pets = World.world.getPetsEntry(obj.getGuid());
                                                    if (pets != null)
                                                        pets.looseFight(caster.getPlayer());
                                                }
                                            }

                                            this.onPlayerLooseHonor(player);

                                            final int energy = totalEnergy;

                                            if (energy == 0) {
                                                if (getType() == Constant.FIGHT_TYPE_AGRESSION) {
                                                    for (Fighter enemy : (this.getTeam1().containsValue(caster) ? this.getTeam0() : this.getTeam1()).values()) {
                                                        if (enemy.getPlayer() != null && enemy.getPlayer().getStalk() != null && player != null) {
                                                            if (enemy.getPlayer().getStalk().getTarget() == caster.getPlayer()) {
                                                                player.teleportFaction(enemy.getPlayer().getAlignment());
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    TimerWaiter.addNext(player::setFuneral, 1, TimeUnit.SECONDS);
                                                } else {
                                                    TimerWaiter.addNext(player::setFuneral, 1, TimeUnit.SECONDS);
                                                }
                                            } else {
                                                if (getType() == Constant.FIGHT_TYPE_AGRESSION) {
                                                    for (Fighter enemy : (this.getTeam1().containsValue(caster) ? this.getTeam0() : this.getTeam1()).values()) {
                                                        if (enemy.getPlayer() != null) {
                                                            if (enemy.getPlayer().getStalk().getTarget() == caster.getPlayer()) {
                                                                player.teleportFaction(enemy.getPlayer().getAlignment());
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    if (player.isOnline()) {
                                                        String[] split = player.getSavePosition().split(",");
                                                        player.teleport(Short.parseShort(split[0]), Integer.parseInt(split[1]));
                                                    } else {
                                                        player.setNeededEndFightAction(new Action(1001, player.getSavePosition(), "", null));
                                                    }
                                                }
                                                player.setPdv(1);
                                            }
                                        }

                                        if (player.isOnline())
                                            SocketManager.GAME_SEND_GV_PACKET(player);
                                    }
                                }

                                if (getType() == Constant.FIGHT_TYPE_ROYAL || getType() == Constant.FIGHT_TYPE_AGRESSION || getType() == Constant.FIGHT_TYPE_CHALLENGE || getType() == Constant.FIGHT_TYPE_PVT || getType() == Constant.FIGHT_TYPE_CONQUETE) {
                                    for (Fighter f : this.getFighters(caster.getOtherTeam())) {
                                        if (f.getPlayer() == null)
                                            continue;
                                        final Player player = f.getPlayer();

                                        player.setDuelId(-1);
                                        player.setReady(false);
                                        player.setFight(null);
                                        player.setAway(false);

                                        if (player.isOnline())
                                            SocketManager.GAME_SEND_GV_PACKET(player);
                                    }
                                }

                                this.setState(4);// Nous assure de ne pas d�marrer le combat
                                this.getMapOld().removeFight(this.getId());
                                SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(this.getMapOld());
                                SocketManager.GAME_SEND_GAME_REMFLAG_PACKET_TO_MAP(this.getMapOld(), this.getInit0().getId());

                                if (getType() == Constant.FIGHT_TYPE_PVT) {
                                    // FIXME
                                    World.world.getGuild(getGuildId()).getPlayers().stream().filter(player -> player != null && player.isOnline()).forEach(player -> {
                                        SocketManager.GAME_SEND_gITM_PACKET(player, Collector.parseToGuild(player.getGuild().getId()));
                                        SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("fight.end.collector"));
                                    });

                                    this.getCollector().setInFight((byte) 0);
                                    this.getCollector().set_inFightID((byte) -1);

                                    World.world.getMap(this.getCollector().getMap()).getPlayers().stream().filter(Objects::nonNull)
                                            .forEach(player -> SocketManager.GAME_SEND_MAP_PERCO_GMS_PACKETS(player.getGameClient(), player.getCurMap()));
                                }
                                setMap(null);
                                this.orderPlaying = null;
                                //endregion
                            } else {// Soit il a rejoin le combat => Left de lui seul
                                //region Cas 2
                                SocketManager.GAME_SEND_ON_FIGHTER_KICK(this, caster.getPlayer().getId(), getTeamId(caster.getId()));

                                if (getType() == Constant.FIGHT_TYPE_ROYAL || getType() == Constant.FIGHT_TYPE_AGRESSION || getType() == Constant.FIGHT_TYPE_CHALLENGE || getType() == Constant.FIGHT_TYPE_PVT || getType() == Constant.FIGHT_TYPE_CONQUETE)
                                    SocketManager.GAME_SEND_ON_FIGHTER_KICK(this, caster.getPlayer().getId(), getOtherTeamId(caster.getId()));

                                final Player player = caster.getPlayer();
                                player.setDuelId(-1);
                                player.setReady(false);
                                player.setFight(null);
                                player.setAway(false);
                                caster.setLeft(true);
                                caster.hasLeft();

                                if (getType() == Constant.FIGHT_TYPE_ROYAL || getType() == Constant.FIGHT_TYPE_AGRESSION || getType() == Constant.FIGHT_TYPE_PVM || getType() == Constant.FIGHT_TYPE_PVT || getType() == Constant.FIGHT_TYPE_CONQUETE || getType() == Constant.FIGHT_TYPE_DOPEUL) {
                                    int loosEnergy = Formulas.getLoosEnergy(player.getLevel(), getType() == 1, getType() == 5), totalEnergy = player.getEnergy() - loosEnergy;
                                    if (totalEnergy < 0) totalEnergy = 0;

                                    player.setEnergy(totalEnergy);
                                    player.setMascotte(0);

                                    if (player.isOnline())
                                        SocketManager.GAME_SEND_Im_PACKET(player, "034;" + loosEnergy);
                                    if (caster.getPlayer().getObjetByPos(Constant.ITEM_POS_FAMILIER) != null) {
                                        GameObject obj = caster.getPlayer().getObjetByPos(Constant.ITEM_POS_FAMILIER);
                                        if (obj != null) {
                                            PetEntry pets = World.world.getPetsEntry(obj.getGuid());
                                            if (pets != null)
                                                pets.looseFight(caster.getPlayer());
                                        }
                                    }

                                    this.onPlayerLooseHonor(player);

                                    final int energy = totalEnergy;

                                    if (energy == 0) {
                                        if (getType() == Constant.FIGHT_TYPE_AGRESSION) {
                                            for (Fighter enemy : (this.getTeam1().containsValue(caster) ? this.getTeam0() : this.getTeam1()).values()) {
                                                if (enemy.getPlayer() != null) {
                                                    if (enemy.getPlayer().getStalk().getTarget() == caster.getPlayer()) {
                                                        player.teleportFaction(enemy.getPlayer().getAlignment());
                                                        break;
                                                    }
                                                }
                                            }
                                            TimerWaiter.addNext(player::setFuneral, 1, TimeUnit.SECONDS);
                                        } else {
                                            TimerWaiter.addNext(player::setFuneral, 1, TimeUnit.SECONDS);
                                        }
                                    } else {
                                        if (getType() == Constant.FIGHT_TYPE_AGRESSION) {
                                            for (Fighter enemy : (this.getTeam1().containsValue(caster) ? this.getTeam0() : this.getTeam1()).values()) {
                                                if (enemy.getPlayer() != null) {
                                                    if (enemy.getPlayer().getStalk().getTarget() == caster.getPlayer()) {
                                                        player.teleportFaction(enemy.getPlayer().getAlignment());
                                                        break;
                                                    }
                                                }
                                            }
                                        } else {
                                            if (getType() != Constant.FIGHT_TYPE_PVT)
                                                player.setNeededEndFightAction(new Action(1001, player.getSavePosition(), "", null));
                                            else if (!player.getCurMap().hasEndFightAction(0))
                                                player.setNeededEndFightAction(new Action(1001, player.getSavePosition(), "", null));
                                        }
                                        player.setPdv(1);
                                    }
                                }

                                if (player.isOnline())
                                    SocketManager.GAME_SEND_GV_PACKET(player);

                                // On le supprime de la team
                                if (this.getTeam0().containsKey(caster.getId())) {
                                    caster.getCell().removeFighter(caster);
                                    this.getTeam0().remove(caster.getId());
                                    SocketManager.GAME_SEND_REMOVE_IN_TEAM_PACKET_TO_MAP(getMapOld(), this.init0.getId() , target);
                                } else if (getTeam1().containsKey(caster.getId())) {
                                    caster.getCell().removeFighter(caster);
                                    this.getTeam1().remove(caster.getId());
                                    SocketManager.GAME_SEND_REMOVE_IN_TEAM_PACKET_TO_MAP(getMapOld(), this.init1.getId() , target);
                                }
                                //endregion
                            }
                        }
                        //endregion
                    }
                    break;
            }
        } else {
            SocketManager.GAME_SEND_GV_PACKET(playerCaster);
            this.getViewer().remove(playerCaster.getId());
            playerCaster.setFight(null);
            playerCaster.setAway(false);
        }
    }

    private void onPlayerLooseHonor(Player player) {
        if (getType() == Constant.FIGHT_TYPE_AGRESSION || getType() == Constant.FIGHT_TYPE_CONQUETE) {
            final Fighter caster = this.getFighterByPerso(player);
            if(caster == null || caster.hasLeft()) return;
            ArrayList<Fighter> winners =  new ArrayList<>(this.getTeam(caster.getTeam() == 0 ? 2 : 1).values());
            ArrayList<Fighter> loosers = new ArrayList<>(this.getTeam(caster.getTeam()+1).values());

            int honor = player.get_honor() - Formulas.calculHonorWin(winners, loosers, caster, false);
            if (honor < 0) honor = 0;
            player.set_honor(honor);
            if (player.isOnline())
                SocketManager.GAME_SEND_Im_PACKET(player, "076;" + honor);
        }
    }

    public void endFight(byte type) {
        if (this.launchTime > 1 || this.state == Constant.FIGHT_STATE_INIT)
            return;
        if (this.state == Constant.FIGHT_STATE_PLACE)
            this.startFight();

        Collection<Fighter> collection = type == 0 ? this.getTeam0().values() : type == 1 ? this.getTeam1().values() : null;
        List<Fighter> fighters = collection == null ? new ArrayList<>() : new ArrayList<>(collection);
        fighters.stream().filter(fighter -> fighter != null).forEach(fighter -> fighter.setIsDead(true));

        if (type == 0) this.type = -1;
        this.verifIfTeamAllDead();
    }

    public boolean verifIfTeamBoufbowl(int but) {
        return verifTeamBoufbowl(this, but);
    }

    private boolean verifTeamBoufbowl(Fight fight, int but) {


        if (!fight.finish) {
            final ArrayList<Fighter> fighters = new ArrayList<>();
            fighters.addAll(fight.getTeam0().values());
            fighters.addAll(fight.getTeam1().values());

            fight.finish = true;
            fight.turn.stop();
            fight.turn = null;

            try {

                fight.setState(Constant.FIGHT_STATE_FINISHED);
                //fight.closeSheduler();

                ArrayList<Fighter> winTeam = new ArrayList<>(), looseTeam = new ArrayList<>();

                if (but == 1) {
                    looseTeam.addAll(fight.getTeam0().values());
                    winTeam.addAll(fight.getTeam1().values());
                } else {
                    winTeam.addAll(fight.getTeam0().values());
                    looseTeam.addAll(fight.getTeam1().values());
                }

                fight.getInit0().getPlayer().getCurMap().removeFight(fight.getId());

                String packet = getGE(but == 1 ? 2 : 1);

                for (Fighter fighter : fighters) {
                    SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(fight.getMap(), fighter.getId());
                    if (fighter.hasLeft()) continue;
                    Player player = fighter.getPlayer();

                    if (player != null && fight.isBegin()) {
                        player.setFight(null);
                        player.setDuelId(-1);
                        player.setReady(false);

                        player.send(packet);
                    }
                }


                for (Player player : fight.getViewer().values()) {
                    player.refreshMapAfterFight();
                    player.setSpec(false);
                    SocketManager.send(player, packet);

                    if (player.getAccount().isBanned())
                        player.getGameClient().kick();
                }

                fight.setCurPlayer(-1);

                for (Fighter fighter : fight.getTeam1().values()) {
                    Player player = fighter.getPlayer();

                    if (player == null)
                        continue;

                    player.setDuelId(-1);
                    player.setReady(false);
                }

                for (Fighter fighter : fight.getFighters(3))
                    fighter.getFightBuff().clear();

                World.world.getMap(fight.getMap().getId()).removeFight(fight.getId());
                SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(World.world.getMap(fight.getMap().getId()));
                fight.setMap(null);
                fight.orderPlaying = null;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            } catch (Exception e) {
                for (Fighter fighter : fighters) {
                    Player player = fighter.getPlayer();
                    if (player != null) {
                        player.setDuelId(-1);
                        player.setReady(false);
                        player.setFight(null);
                        SocketManager.GAME_SEND_GV_PACKET(player);
                    }
                }
                System.out.println("Error : (verifIfTeamAllDead) : " + e.getMessage() + e.getStackTrace()[0].getLineNumber());
            }


            for (Fighter fighter : fighters) {
                Player player = fighter.getPlayer();


                if (player == null)
                    continue;

                if (player.getFight() != null)
                    player.setFight(null);

                player.refreshLife(false);

                if (!player.getCurCell().isWalkable(true))
                    player.teleport(player.getCurMap().getId(), player.getCurMap().getRandomFreeCellId());
                if (player.getAccount().isBanned())
                    player.getGameClient().kick();
                if (fighter.isDeconnected())
                    player.getAccount().disconnect(player);
                if (player.getMorphMode())
                    SocketManager.GAME_SEND_SPELL_LIST(player);
                if (player != null) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                    player.unsetFullMorphbouf();
                    player.fullPDV();
                    player.refreshLife(false);
                }

            }

            String name;
            if (but == 0)
                name = fight.getInit1().getPlayer().getName();
            else
                name = fight.getInit0().getPlayer().getName();
            SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(fight.getMapOld(), "", -1, "Boumawa", "fight.fight.boufbwal.end.fight", null);
            int team1 = this.getTeamId(getInit1().getPlayer().getId());
            int team2 = this.getTeamId(getInit0().getPlayer().getId());
            SocketManager.GAME_SEND_cMK_PACKET_TO_FIGHT(fight, team1, "", -1, "Boumawa", "Le match est terminé l'équipe gagnante est celle de : " + name + " .", null);
            SocketManager.GAME_SEND_cMK_PACKET_TO_FIGHT(fight, team2, "", -1, "Boumawa", "Le match est terminé l'équipe gagnante est celle de : " + name + " .", null);
            return true;
        }

        return false;


    }

    void startTurn() {
        if (verifyStillInFight())
            verifIfTeamAllDead();

        if (getState() >= Constant.FIGHT_STATE_FINISHED)
            return;

        setCurPlayer(getCurPlayer() + 1);
        setCurAction("");

        if (getCurPlayer() >= this.getOrderPlayingSize())
            setCurPlayer(0);
        if(getCurPlayer() == 0)
            this.totalTurns++;

        Fighter current = this.getFighterByGameOrder();

        setCurFighterPa(current.getPa());
        setCurFighterPm(current.getPm());
        setCurFighterUsedPa();
        setCurFighterUsedPm();

        if (current.hasLeft() || current.isDead()) {
            this.endTurn(false, current);
            return;
        }

        current.applyBeginningTurnBuff(this);

        if (current.isDead() && current.isInvocation()) {
            endTurn(false, this.getFighterByGameOrder());
            return;
        }

        if (getState() == Constant.FIGHT_STATE_FINISHED)
            return;

        if (current.getPdv() <= 0) {
            if(!current.trapped) {
                onFighterDie(current, getInit0());
                endTurn(false, current);
                return;
            }
        }
        // On actualise les sorts launch
        current.refreshLaunchedSort();
        // reset des Max des Chatis
        current.getChatiValue().clear();

        if (current.isDead() && !current.isInvocation()) {
            if(!current.trapped) {
                endTurn(false, current);
                return;
            }
        }
        if (current.getPlayer() != null)
            SocketManager.GAME_SEND_STATS_PACKET(current.getPlayer());

        if (current.hasBuff(Constant.EFFECT_PASS_TURN) || (current.getBuff(149) != null && current.getBuff(149).getSpell() == 197)) {
            endTurn(false, current);
            return;
        }

        int duration = Constant.TIME_BY_TURN;

        SocketManager.GAME_SEND_GAMETURNSTART_PACKET_TO_FIGHT(this, 7, current.getId(), duration, this.totalTurns);
        current.setCanPlay(true);
        this.turn = new Turn(this, current);

        // Gestion des glyphes
        ArrayList<Glyph> glyphs = new ArrayList<>(this.getAllGlyphs());// Copie du tableau

        for (Glyph glyph : glyphs) {
            if (glyph.getCaster().getId() == current.getId()) {
                if (glyph.decrementDuration() == 0) {
                    getAllGlyphs().remove(glyph);
                    glyph.disappear();
                    continue;
                }
            }

            if (PathFinding.getDistanceBetween(getMap(), current.getCell().getId(), glyph.getCell().getId()) <= glyph.getSize() && glyph.getSpell() != 476)
                glyph.onTrapped(current);
        }


        if ((getType() == Constant.FIGHT_TYPE_PVM) && (getAllChallenges().size() > 0) && !current.isInvocation() && !current.isDouble() && !current.isCollector()
                || getType() == Constant.FIGHT_TYPE_DOPEUL && (getAllChallenges().size() > 0) && !current.isInvocation() && !current.isDouble() && !current.isCollector())
            for (Challenge challenge : this.getAllChallenges().values())
                if (challenge != null)
                    challenge.onPlayerStartTurn(current);

        if (current.isDeconnected()) {
            current.setTurnRemaining();
            if (current.getTurnRemaining() <= 0) {
                if (current.getPlayer() != null) {
                    leftFight(current.getPlayer(), null);
                    current.getPlayer().disconnectInFight();
                } else {
                    onFighterDie(current, current);
                    current.setLeft(true);
                }
            } else {
                SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 7, "0162;" + current.getPacketsName() + "~" + current.getTurnRemaining());
                //this.endTurn(false, current);
                return;
            }
        }
        if(current.getPlayer() != null ) {
            Party party = current.getPlayer().getParty();
            Party.MasterOption option;
            if(party != null && party.getMaster() != null && (option = party.getOptionByPlayer(current.getPlayer())) != null) {
                if(option.passAuto()) {
                    this.endTurn(false, current);
                }
            }
        }

        if (current.getPlayer() == null || current.getDouble() != null || current.getCollector() != null)
            IAHandler.select(this, current);
    }

    public synchronized void endTurn(boolean onAction, Fighter f) {
        final Fighter current = this.getFighterByGameOrder();
        if (current != null)
            if (f == current)
                this.endTurn(onAction);
    }

    public synchronized void endTurn(final boolean onAction) {
        final Fighter current = this.getFighterByGameOrder();
        if (current == null)
            return;

        try {
            if (this.getState() >= Constant.FIGHT_STATE_FINISHED)
                return;
            if (this.turn != null)
                this.turn.stop();
            if (current.hasLeft() || current.isDead()) {
                this.startTurn();
                return;
            }

            if (!this.getCurAction().equals("")) {
                TimerWaiter.addNext(() -> this.endTurn(onAction, current), 100);
                return;
            }

            SocketManager.GAME_SEND_GAMETURNSTOP_PACKET_TO_FIGHT(this, 7, current.getId());
            current.setCanPlay(false);
            setCurAction("");

            if (onAction) TimerWaiter.addNext(() -> this.newTurn(current), 2100);
            else this.newTurn(current);
        } catch (NullPointerException e) {
            e.printStackTrace();
            this.endTurn(false);
        }
    }

    private void newTurn(Fighter current) {
        // Si empoisonn� (Cr�er une fonction applyEndTurnbuff si
        // d'autres effets existent)
        for (SpellEffect SE : current.getBuffsByEffectID(131)) {
            int pas = SE.getValue();
            int val = -1;
            try {
                val = Integer.parseInt(SE.getArgs().split(";")[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (val == -1)
                continue;

            int nbr = (int) Math.floor((double) getCurFighterUsedPa()
                    / (double) pas);
            int dgt = val * nbr;
            // Si poison paralysant
            if (SE.getSpell() == 200) {
                int inte = SE.getCaster().getTotalStats().getEffect(Constant.STATS_ADD_INTE);
                if (inte < 0)
                    inte = 0;
                int pdom = SE.getCaster().getTotalStats().getEffect(Constant.STATS_ADD_PERDOM);
                if (pdom < 0)
                    pdom = 0;
                // on applique le boost
                dgt = ((100 + inte + pdom) / 100) * dgt;
            }
            if (current.hasBuff(184)) {
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 105, current.getId() + "", current.getId() + "," + current.getBuff(184).getValue());
                dgt = dgt - current.getBuff(184).getValue();// R�duction physique
            }
            if (current.hasBuff(105)) {
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 105, current.getId() + "", current.getId() + "," + current.getBuff(105).getValue());
                dgt = dgt - current.getBuff(105).getValue();// Immu
            }

            if (dgt <= 0)
                continue;
            if (dgt > current.getPdv())
                dgt = current.getPdv();// va mourrir

            current.removePdv(current, dgt);
            dgt = -(dgt);
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 100, SE.getCaster().getId() + "", current.getId() + "," + dgt);
        }

        for (Glyph g : new ArrayList<>(getAllGlyphs())) {
            if (getState() >= Constant.FIGHT_STATE_FINISHED)
                return;
            // Si dans le glyphe
            int dist = PathFinding.getDistanceBetween(getMap(), current.getCell().getId(), g.getCell().getId());
            if (dist <= g.getSize() && g.getSpell() == 476)// 476 a effet en fin de tour, alors le joueur est dans le glyphe
                g.onTrapped(current);
        }

        if (!current.trapped && current.getPdv() <= 0) {
            onFighterDie(current, getInit0());
        }

        if ((getType() == Constant.FIGHT_TYPE_PVM) && (getAllChallenges().size() > 0) && !current.isInvocation()
                && !current.isDouble() && !current.isCollector() && current.getTeam() == 0 || getType() == Constant.FIGHT_TYPE_DOPEUL
                && (getAllChallenges().size() > 0) && !current.isInvocation() && !current.isDouble() && !current.isCollector() && current.getTeam() == 0) {
            for (Entry<Integer, Challenge> c : getAllChallenges().entrySet()) {
                if (c.getValue() == null)
                    continue;
                c.getValue().onPlayerEndTurn(current);
            }
        }
        setCurFighterUsedPa();
        setCurFighterUsedPm();
        setCurFighterPa(current.getTotalStats().getEffect(Constant.STATS_ADD_PA));
        setCurFighterPm(current.getTotalStats().getEffect(Constant.STATS_ADD_PM));
        current.refreshEndTurnBuff();
        if (current.getPlayer() != null)
            if (current.getPlayer().isOnline())
                SocketManager.GAME_SEND_STATS_PACKET(current.getPlayer());

        SocketManager.GAME_SEND_GTM_PACKET_TO_FIGHT(this, 7);
        SocketManager.GAME_SEND_GTR_PACKET_TO_FIGHT(this, 7, current.getId());
        // Timer d'une seconde � la fin du tour
        this.startTurn();
    }

    public void playerPass(Player player) {
        final Fighter fighter = getFighterByPerso(player);
        if (fighter != null)
            if (fighter.canPlay() && this.getCurAction().isEmpty())
                this.endTurn(false, fighter);
    }

    public synchronized void joinFight(Player perso, int guid) {
        long timeRestant = Constant.TIME_START_FIGHT - (System.currentTimeMillis() - launchTime);
        Fighter currentJoin = null;

        if (perso.isDead() == 1 || isBegin() || perso.getFight() != null)
            return;
        if (getType() == Constant.FIGHT_TYPE_AGRESSION || getType() == Constant.FIGHT_TYPE_CONQUETE || this.getType() == Constant.FIGHT_TYPE_PVT) {
            boolean multiIp = false;

            for (Fighter f : this.getFighters(7))
                if(f.getPlayer() != null)
                    if (perso.getAccount().getCurrentIp().compareTo(f.getPlayer().getAccount().getCurrentIp()) == 0)
                        multiIp = true;
            if (multiIp) {
                SocketManager.GAME_SEND_MESSAGE(perso, perso.getLang().trans("fight.join.with.sameip"));
                return;
            }
        }

        if (getTeam0().containsKey(guid)) {
            GameCase cell = getRandomCell(getStart0());
            if (cell == null)
                return;
            if (getType() == 7) {
                perso.setFullMorphbouf(0);
            }

            if (isOnlyGroup0()) {
                Party g = getInit0().getPlayer().getParty();
                if (g != null) {
                    if (!g.getPlayers().contains(perso)) {
                        SocketManager.GAME_SEND_GA903_ERROR_PACKET(perso.getGameClient(), 'f', guid);
                        return;
                    }
                }
            }
            if (getType() == Constant.FIGHT_TYPE_AGRESSION || getType() == Constant.FIGHT_TYPE_CONQUETE) {
                char type = getType() == Constant.FIGHT_TYPE_AGRESSION ? 'f' : 'a';
                if (perso.getAlignment() == Constant.ALIGNEMENT_NEUTRE) {
                    SocketManager.GAME_SEND_GA903_ERROR_PACKET(perso.getGameClient(), type, guid);
                    return;
                }
                if (getInit0().getPlayer().getAlignment() != perso.getAlignment()) {
                    SocketManager.GAME_SEND_GA903_ERROR_PACKET(perso.getGameClient(), type, guid);
                    return;
                }
                if(!perso.is_showWings())
                    perso.toggleWings('+');
            }
            if (getGuildId() > -1 && perso.getGuild() != null) {
                if (getGuildId() == perso.getGuild().getId()) {
                    SocketManager.GAME_SEND_GA903_ERROR_PACKET(perso.getGameClient(), 'f', guid);
                    return;
                }
            }
            if (isLocked0()) {
                SocketManager.GAME_SEND_GA903_ERROR_PACKET(perso.getGameClient(), 'f', guid);
                return;
            }
            if (this.getTeam0().size() >= 8 || this.start0.size() == this.getTeam0().size())
                return;
            if (getType() == Constant.FIGHT_TYPE_CHALLENGE)
                SocketManager.GAME_SEND_GJK_PACKET(perso, 2, 1, 1, 0, timeRestant, getType());
            else
                SocketManager.GAME_SEND_GJK_PACKET(perso, 2, 0, 1, 0, timeRestant, getType());

            SocketManager.GAME_SEND_FIGHT_PLACES_PACKET(perso.getGameClient(), getMap().getPlaces(), getSt1());
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId() + "", perso.getId() + "," + Constant.ETAT_PORTE + ",0");
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId() + "", perso.getId() + "," + Constant.ETAT_PORTEUR + ",0");
            SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(perso.getCurMap(), perso.getId());
            SocketManager.GAME_SEND_GDF_PACKET_TO_FIGHT(perso, this.getMap().getCases());
            Fighter f = new Fighter(this, perso);
            currentJoin = f;
            f.setTeam(0);
            getTeam0().put(perso.getId(), f);
            perso.setFight(this);
            f.setCell(cell);
            f.getCell().addFighter(f);
        } else if (getTeam1().containsKey(guid)) {
            if (getType() == 7) {
                perso.setFullMorphbouf(1);
            }
            GameCase cell = getRandomCell(getStart1());
            if (cell == null)
                return;
            if (isOnlyGroup1()) {
                Party g = getInit1().getPlayer().getParty();
                if (g != null) {
                    if (!g.getPlayers().contains(perso)) {
                        SocketManager.GAME_SEND_GA903_ERROR_PACKET(perso.getGameClient(), 'f', guid);
                        return;
                    }
                }
            }
            if (getType() == Constant.FIGHT_TYPE_AGRESSION || getType() == Constant.FIGHT_TYPE_CONQUETE) {
                char type = getType() == Constant.FIGHT_TYPE_AGRESSION ? 'f' : 'a';
                if (perso.getAlignment() == Constant.ALIGNEMENT_NEUTRE) {
                    SocketManager.GAME_SEND_GA903_ERROR_PACKET(perso.getGameClient(), type, guid);
                    return;
                }
                if (getInit1().getPlayer().getAlignment() != perso.getAlignment()) {
                    SocketManager.GAME_SEND_GA903_ERROR_PACKET(perso.getGameClient(), type, guid);
                    return;
                }
                if(!perso.is_showWings())
                    perso.toggleWings('+');
            }
            if (getGuildId() > -1 && perso.getGuild() != null) {
                if (getGuildId() == perso.getGuild().getId()) {
                    SocketManager.GAME_SEND_GA903_ERROR_PACKET(perso.getGameClient(), 'f', guid);
                    return;
                }
            }
            if (isLocked1()) {
                SocketManager.GAME_SEND_GA903_ERROR_PACKET(perso.getGameClient(), 'f', guid);
                return;
            }
            if (this.getTeam1().size() >= 8 || this.start1.size() == this.getTeam0().size())
                return;
            if (getType() == Constant.FIGHT_TYPE_CHALLENGE)
                SocketManager.GAME_SEND_GJK_PACKET(perso, 2, 1, 1, 0, 0, getType());
            else
                SocketManager.GAME_SEND_GJK_PACKET(perso, 2, 0, 1, 0, 0, getType());

            SocketManager.GAME_SEND_FIGHT_PLACES_PACKET(perso.getGameClient(), getMap().getPlaces(), getSt2());
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId() + "", perso.getId() + "," + Constant.ETAT_PORTE + ",0");
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, perso.getId() + "", perso.getId() + "," + Constant.ETAT_PORTEUR + ",0");
            SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(perso.getCurMap(), perso.getId());

            Fighter f = new Fighter(this, perso);
            currentJoin = f;
            f.setTeam(1);
            getTeam1().put(perso.getId(), f);
            perso.setFight(this);
            f.setCell(cell);
            f.getCell().addFighter(f);
        }

        demorph(perso);

        if (getTeam0().containsKey(guid) && getType() == 7)
            perso.setFullMorphbouf(0);

        else if (getTeam0().containsKey(guid) && getType() == 7)
            perso.setFullMorphbouf(1);


        if (currentJoin == null) return;
        perso.getCurCell().removePlayer(perso);

        SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(perso.getCurMap(), (currentJoin.getTeam() == 0 ? getInit0() : getInit1()).getId(), currentJoin);
        SocketManager.GAME_SEND_FIGHT_PLAYER_JOIN(this, 7, currentJoin);
        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS(this, getMap(), perso);

        if (getCollector() != null) {
            World.world.getGuild(getGuildId()).getPlayers().stream().filter(Player::isOnline).forEach(z -> {
                Collector.parseAttaque(z, getGuildId());
                Collector.parseDefense(z, getGuildId());
            });
        }
        if (getPrism() != null)
            World.world.getOnlinePlayers().stream().filter(Objects::nonNull).filter(z -> z.getAlignment() == getPrism().getAlignment()).forEach(z -> Prism.parseAttack(perso));
    }

    private synchronized void joinCollectorFight(final Player player,
                                                 final int collector) {
        final GameCase cell = getRandomCell(getStart1());

        if (cell == null)
            return;

        SocketManager.GAME_SEND_GJK_PACKET(player, 2, 0, 1, 0, 0, getType());
        SocketManager.GAME_SEND_FIGHT_PLACES_PACKET(player.getGameClient(), getMap().getPlaces(), getSt2());
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, player.getId() + "", player.getId() + "," + Constant.ETAT_PORTE + ",0");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, player.getId() + "", player.getId() + "," + Constant.ETAT_PORTEUR + ",0");
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(player.getCurMap(), player.getId());

        Fighter f = new Fighter(this, player);
        f.setTeam(1);
        getTeam1().put(player.getId(), f);
        player.setFight(this);
        f.setCell(cell);
        f.getCell().addFighter(f);
        player.getCurCell().removePlayer(player);

        SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(player.getCurMap(), collector, f);
        SocketManager.GAME_SEND_FIGHT_PLAYER_JOIN(this, 7, f);
        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS(this, getMap(), player);
        SocketManager.GAME_SEND_GDF_PACKET_TO_FIGHT(player, this.getMap().getCases());
    }

    public void joinPrismFight(final Player player, final int team) {
        final GameCase cell = getRandomCell((team == 1 ? this.start1 : this.start0));

        if (cell == null)
            return;

        int prismTeam = (this.getTeam0().containsKey(this.getPrism().getId()) ? 0 : 1);

        if (prismTeam == team) {
            if (player.getAlignment() != this.getPrism().getAlignment())
                return;
        } else {
            if (player.getAlignment() == this.getPrism().getAlignment())
                return;
        }

        long time = (this.launchTime + 60000) - System.currentTimeMillis();
        SocketManager.GAME_SEND_GJK_PACKET(player, 2, 0, 1, 0, time, getType());
        SocketManager.GAME_SEND_FIGHT_PLACES_PACKET(player.getGameClient(), getMap().getPlaces(), getSt2());
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, player.getId() + "", player.getId() + "," + Constant.ETAT_PORTE + ",0");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, player.getId() + "", player.getId() + "," + Constant.ETAT_PORTEUR + ",0");
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(player.getCurMap(), player.getId());

        Fighter f = new Fighter(this, player);
        f.setTeam(team);
        this.getTeam(team + 1).put(player.getId(), f);
        player.setFight(this);
        f.setCell(cell);
        demorph(player);
        f.getCell().addFighter(f);
        player.getCurCell().removePlayer(player);

        SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(player.getCurMap(), ((Fighter) this.getTeam(team + 1).values().toArray()[0]).getId(), f);
        SocketManager.GAME_SEND_FIGHT_PLAYER_JOIN(this, 7, f);
        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS(this, getMap(), player);
        SocketManager.GAME_SEND_GDF_PACKET_TO_FIGHT(player, this.getMap().getCases());
    }

    public void joinAsSpectator(Player p) {
        final Fighter current = this.getFighterByGameOrder();
        if (current == null)
            return;

        if (!isBegin() || p.getFight() != null) {
            SocketManager.GAME_SEND_Im_PACKET(p, "157");
            return;
        }
        if (p.getGroupe() == null) {
            if (!isViewerOk() || getState() != Constant.FIGHT_STATE_ACTIVE) {
                SocketManager.GAME_SEND_Im_PACKET(p, "157");
                return;
            }
        }
        demorph(p);
        p.getCurCell().removePlayer(p);
        SocketManager.GAME_SEND_GJK_PACKET(p, getState(), 0, 0, 1, 0, getType());
        SocketManager.GAME_SEND_GS_PACKET(p);
        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(p.getCurMap(), p.getId());
        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS(this, getMap(), p);
        SocketManager.GAME_SEND_GAMETURNSTART_PACKET(p, current.getId(), Constant.TIME_BY_TURN);
        SocketManager.GAME_SEND_GTL_PACKET(p, this);

        getViewer().put(p.getId(), p);
        p.setSpec(true);
        p.setFight(this);

        ArrayList<Fighter> all = new ArrayList<>();
        all.addAll(this.getTeam0().values());
        all.addAll(this.getTeam1().values());
        all.stream().filter(Fighter::isHide).forEach(f -> SocketManager.GAME_SEND_GA_PACKET(this, p, 150, f.getId() + "", f.getId() + ",4"));
        if (p.getGroupe() == null)
            SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 7, "036;" + p.getName());
        if ((getType() == Constant.FIGHT_TYPE_PVM) && (getAllChallenges().size() > 0) || getType() == Constant.FIGHT_TYPE_DOPEUL && getAllChallenges().size() > 0) {
            for (Entry<Integer, Challenge> c : getAllChallenges().entrySet()) {
                if (c.getValue() == null)
                    continue;
                SocketManager.GAME_SEND_CHALLENGE_PERSO(p, c.getValue().parseToPacket());
                if (!c.getValue().loose())
                    c.getValue().challengeSpecLoose(p);
            }
        }
        for(Glyph glyph : this.getAllGlyphs()) {
            SocketManager.GAME_SEND_GA_PACKET(this, p, 999, glyph.getCaster().getId() + "",
                    "GDZ+" + glyph.getCell().getId() + ";" + glyph.getSize() + ";" + glyph.getColor());
            SocketManager.GAME_SEND_GA_PACKET(this, p, 999, glyph.getCaster().getId() + "",
                    "GDC" + glyph.getCell().getId() + ";Haaaaaaaaa3005;");
        }
    }

    public void toggleLockTeam(int guid) {
        if (getInit0() != null && getInit0().getId() == guid) {
            setLocked0(!isLocked0());
            SocketManager.GAME_SEND_FIGHT_CHANGE_OPTION_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), isLocked0() ? '+' : '-', 'A', guid);
            SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 1, isLocked0() ? "095" : "096");
        } else if (getInit1() != null && getInit1().getId() == guid) {
            setLocked1(!isLocked1());
            SocketManager.GAME_SEND_FIGHT_CHANGE_OPTION_PACKET_TO_MAP(getInit1().getPlayer().getCurMap(), isLocked1() ? '+' : '-', 'A', guid);
            SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 2, isLocked1() ? "095" : "096");
        }
    }

    public void toggleLockSpec(Player player) {
        if ((this.getInit0() != null && this.getInit0().getId() == player.getId()) || (this.getInit1() != null && this.getInit1().getId() == player.getId())) {
            this.setViewerOk(!this.isViewerOk());

            if (!this.isViewerOk()) {
                new ArrayList<>(this.getViewer().values()).stream().filter(target -> target.getGroupe() == null).forEach(target -> {
                    SocketManager.GAME_SEND_GV_PACKET(target);
                    this.getViewer().remove(target.getId());
                    target.setFight(null);
                    target.setAway(false);
                    target.setSpec(false);
                });
                SocketManager.GAME_SEND_FIGHT_CHANGE_OPTION_PACKET_TO_MAP(player.getCurMap(), '+', 'S', player.getId());
                this.getFighters(3).stream().filter(fighter -> fighter.getPlayer() != null).forEach(fighter -> fighter.getPlayer().send("Im040;" + player.getName()));
            } else {
                SocketManager.GAME_SEND_FIGHT_CHANGE_OPTION_PACKET_TO_MAP(player.getCurMap(), '-', 'S', player.getId());
                this.getFighters(3).stream().filter(fighter -> fighter.getPlayer() != null).forEach(fighter -> fighter.getPlayer().send("Im039;" + player.getName()));
            }
        } else {
            player.send("BN");
        }
    }

    public void toggleOnlyGroup(int guid) {
        if (getInit0() != null && getInit0().getId() == guid) {
            setOnlyGroup0(!isOnlyGroup0());
            SocketManager.GAME_SEND_FIGHT_CHANGE_OPTION_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), isOnlyGroup0() ? '+' : '-', 'P', guid);
            SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 1, isOnlyGroup0() ? "093" : "094");
        } else if (getInit1() != null && getInit1().getId() == guid) {
            setOnlyGroup1(!isOnlyGroup1());
            SocketManager.GAME_SEND_FIGHT_CHANGE_OPTION_PACKET_TO_MAP(getInit1().getPlayer().getCurMap(), isOnlyGroup1() ? '+' : '-', 'P', guid);
            SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 2, isOnlyGroup1() ? "095" : "096");
        }
    }

    public void toggleHelp(int guid) {
        if (getInit0() != null && getInit0().getId() == guid) {
            setHelp0(!isHelp0());
            SocketManager.GAME_SEND_FIGHT_CHANGE_OPTION_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), isHelp0() ? '+' : '-', 'H', guid);
            SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 1, isHelp0() ? "0103" : "0104");
        } else if (getInit1() != null && getInit1().getId() == guid) {
            setHelp1(!isHelp1());
            SocketManager.GAME_SEND_FIGHT_CHANGE_OPTION_PACKET_TO_MAP(getInit1().getPlayer().getCurMap(), isHelp1() ? '+' : '-', 'H', guid);
            SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 2, isHelp1() ? "0103" : "0104");
        }
    }

    public void showCaseToTeam(int guid, int cellID) {
        int teams = getTeamId(guid) - 1;
        if (teams == 4)// Les spectateurs ne montrent pas.
            return;
        ArrayList<GameClient> PWs = new ArrayList<>();
        if (teams == 0) {
            PWs.addAll(getTeam0().entrySet().stream().filter(e -> e.getValue().getPlayer() != null
                    && e.getValue().getPlayer().getGameClient() != null).map(e -> e.getValue().getPlayer().getGameClient()).collect(Collectors.toList()));
        } else if (teams == 1) {
            PWs.addAll(getTeam1().entrySet().stream().filter(e -> e.getValue().getPlayer() != null
                    && e.getValue().getPlayer().getGameClient() != null).map(e -> e.getValue().getPlayer().getGameClient()).collect(Collectors.toList()));
        }
        SocketManager.GAME_SEND_FIGHT_SHOW_CASE(PWs, guid, cellID);
    }

    private void showCaseToAll(int guid, int cellID) {
        ArrayList<GameClient> PWs = new ArrayList<>();
        for (Entry<Integer, Fighter> e : getTeam0().entrySet()) {
            if (e.getValue().getPlayer() != null && e.getValue().getPlayer().getGameClient() != null)
                PWs.add(e.getValue().getPlayer().getGameClient());
        }
        for (Entry<Integer, Fighter> e : getTeam1().entrySet()) {
            if (e.getValue().getPlayer() != null
                    && e.getValue().getPlayer().getGameClient() != null)
                PWs.add(e.getValue().getPlayer().getGameClient());
        }
        for (Entry<Integer, Player> e : getViewer().entrySet()) {
            PWs.add(e.getValue().getGameClient());
        }
        SocketManager.GAME_SEND_FIGHT_SHOW_CASE(PWs, guid, cellID);
    }

    private void initOrderPlaying() {
        int j = 0;
        int k = 0;
        int start0 = 0;
        int start1 = 0;
        int curMaxIni0 = 0;
        int curMaxIni1 = 0;
        Fighter curMax0 = null;
        Fighter curMax1 = null;
        boolean team1_ready = false;
        boolean team2_ready = false;

        do {
            if (!team1_ready) {
                team1_ready = true;
                Map<Integer, Fighter> team = getTeam0();
                for (Entry<Integer, Fighter> entry : team.entrySet()) {
                    if (this.haveFighterInOrdreJeu(entry.getValue()))
                        continue;
                    team1_ready = false;

                    if (entry.getValue().getInitiative() >= curMaxIni0) {
                        curMaxIni0 = entry.getValue().getInitiative();
                        curMax0 = entry.getValue();
                    }
                    if (curMaxIni0 > start0)
                        start0 = curMaxIni0;
                }
            }
            if (!team2_ready) {
                team2_ready = true;
                for (Entry<Integer, Fighter> entry : getTeam1().entrySet()) {
                    if (this.haveFighterInOrdreJeu(entry.getValue()))
                        continue;
                    team2_ready = false;
                    if (entry.getValue().getInitiative() >= curMaxIni1) {
                        curMaxIni1 = entry.getValue().getInitiative();
                        curMax1 = entry.getValue();
                    }
                    if (curMaxIni1 > start1)
                        start1 = curMaxIni1;
                }
            }
            if (curMax1 == null && curMax0 == null) {
                return;
            }
            if (start0 > start1) {
                if (getFighters(1).size() > j) {
                    this.orderPlaying.add(curMax0);
                    j++;
                }
                if (getFighters(2).size() > k) {
                    this.orderPlaying.add(curMax1);
                    k++;
                }
            } else {
                if (getFighters(2).size() > j) {
                    this.orderPlaying.add(curMax1);
                    j++;
                }
                if (getFighters(1).size() > k) {
                    this.orderPlaying.add(curMax0);
                    k++;
                }
            }

            curMaxIni0 = 0;
            curMaxIni1 = 0;
            curMax0 = null;
            curMax1 = null;
        }
        while (this.getOrderPlayingSize() != getFighters(3).size());
    }

    public void tryCaC(Player perso, int cellID) {
        final Fighter current = this.getFighterByGameOrder();
        if (current == null)
            return;

        Fighter caster = getFighterByPerso(perso);

        if (caster == null)
            return;
        if (current.getId() != caster.getId())// Si ce n'est pas a lui de jouer
            return;
        if (!perso.canCac()) {
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 305, perso.getId() + "", "");// Echec Critique Cac
            endTurn(false, current);
            return;
        }
        if ((getType() == Constant.FIGHT_TYPE_PVM) && (getAllChallenges().size() > 0) || getType() == Constant.FIGHT_TYPE_DOPEUL && getAllChallenges().size() > 0) {
            for (Entry<Integer, Challenge> c : getAllChallenges().entrySet()) {
                if (c.getValue() == null)
                    continue;
                c.getValue().onPlayerCac(current);
            }
        }
        if (perso.getObjetByPos(Constant.ITEM_POS_ARME) == null) {
            tryCastSpell(caster, World.world.getSort(0).getStatsByLevel(1), cellID);
        } else {
            GameObject arme = perso.getObjetByPos(Constant.ITEM_POS_ARME);
            // Pierre d'�mes = EC
            if (arme.getTemplate().getType() == 83) {
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 305, perso.getId() + "", "");// Echec Critique Cac
                this.endTurn(false, current);
                return;
            }

            int PACost = arme.getTemplate().getPACost();

            if (getCurFighterPa() < PACost) {
                SocketManager.GAME_SEND_Im_PACKET(perso, "1170;" + getCurFighterPa() + "~" + PACost);
                return;
            }

            int dist = PathFinding.getDistanceBetween(getMap(), caster.getCell().getId(), cellID);
            int MaxPO = arme.getTemplate().getPOmax();
            int MinPO = arme.getTemplate().getPOmin();

            if (dist < MinPO || dist > MaxPO) {
                SocketManager.GAME_SEND_Im_PACKET(perso, "1171;" + MinPO + "~" + MaxPO + "~" + dist);
                return;
            }

            boolean isEc = arme.getTemplate().getTauxEC() != 0 && Formulas.getRandomValue(1, arme.getTemplate().getTauxEC()) == arme.getTemplate().getTauxEC();
            this.curFighterUsedPa += PACost;

            if (isEc) {
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 305, perso.getId() + "", "");// Echec Critique Cac
                endTurn(false, current);
            } else {
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 303, perso.getId() + "", cellID + "");
                boolean isCC = caster.testIfCC(arme.getTemplate().getTauxCC());
                if (isCC) {
                    SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 301, perso.getId() + "", "0");
                }

                // Si le joueur est invisible
                if (caster.isHide()) caster.unHide(-1);
                ArrayList<SpellEffect> effects = isCC ? arme.getCritEffects() : arme.getEffects();
                ArrayList<Fighter> targets = PathFinding.getCiblesByZoneByWeapon(this, arme.getTemplate().getType(), getMap().getCase(cellID), caster.getCell().getId());

                for (SpellEffect SE : effects) {
                    try {
                        if (getState() != Constant.FIGHT_STATE_ACTIVE)
                            break;

                        if (this.getType() != Constant.FIGHT_TYPE_CHALLENGE && this.getAllChallenges().size() > 0) {
                            this.getAllChallenges().values().stream().filter(Objects::nonNull).forEach(challenge -> challenge.onFightersAttacked(targets, caster, SE, -1, false));
                        }
                        SE.setTurn(0);
                        SE.applyToFight(this, caster, targets, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                /*
                 * 7172 Baguette Rhon
                 * 7156 Marteau Ronton
                 * 1355 Arc Hidsad
                 * 7182 Racine H�couanone
                 * 7040 Arc de Kuri
                 * 6539 Pelle Gicque
                 * 6519 Baguette de Kouartz
                 * 8118 Baguette du Scarabosse Dor�
                 */
                int idArme = arme.getTemplate().getId(), basePdvSoin = 1, pdvSoin;
                if (idArme == 7172 || idArme == 7156 || idArme == 1355 || idArme == 7182 || idArme == 7040 || idArme == 6539 || idArme == 6519 || idArme == 8118) {
                    pdvSoin = Constant.getArmeSoin(idArme);
                    if (pdvSoin != -1) {
                        if (isCC) {
                            basePdvSoin = basePdvSoin + arme.getTemplate().getBonusCC();
                            pdvSoin = pdvSoin + arme.getTemplate().getBonusCC();
                        }
                        int intel = perso.getStats().getEffect(Constant.STATS_ADD_INTE) + perso.getStuffStats().getEffect(Constant.STATS_ADD_INTE) + perso.getDonsStats().getEffect(Constant.STATS_ADD_INTE) + perso.getBuffsStats().getEffect(Constant.STATS_ADD_INTE);
                        int soins = perso.getStats().getEffect(Constant.STATS_ADD_SOIN) + perso.getStuffStats().getEffect(Constant.STATS_ADD_SOIN) + perso.getDonsStats().getEffect(Constant.STATS_ADD_SOIN) + perso.getBuffsStats().getEffect(Constant.STATS_ADD_SOIN);
                        int minSoin = basePdvSoin * (100 + intel) / 100 + soins;
                        int maxSoin = pdvSoin * (100 + intel) / 100 + soins;

                        for (Fighter target : targets) {
                            if (target == null) continue;
                            int finalSoin = Formulas.getRandomValue(minSoin, maxSoin);
                            if ((finalSoin + target.getPdv()) > target.getPdvMax())
                                finalSoin = target.getPdvMax() - target.getPdv();// Target

                            target.removePdv(target, -finalSoin);
                            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 100, target.getId() + "", target.getId() + ",+" + finalSoin);
                        }
                    }
                }
                setCurFighterPa(getCurFighterPa() - PACost);
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 102, perso.getId() + "", perso.getId() + ",-" + PACost);
            }
        }
    }

    public synchronized int tryCastSpell(Fighter fighter, SortStats spell, int cell) {
        final Fighter current = this.getFighterByGameOrder();

        if (current == null || spell == null || !this.getCurAction().isEmpty() || current.getId() != fighter.getId()) {
            return 10;
        }

        Player player = fighter.getPlayer();
        GameCase Cell = getMap().getCase(cell);
        setCurAction("casting");

        if (this.canCastSpell1(fighter, spell, Cell, -1)) {
            if (fighter.getPlayer() != null)
                SocketManager.GAME_SEND_STATS_PACKET(fighter.getPlayer()); // envoi des stats du lanceur
            if (fighter.getType() == 1 && player.getObjectsClassSpell().containsKey(spell.getSpellID())) {
                int value = player.getValueOfClassObject(spell.getSpellID(), 285);
                this.setCurFighterPa(getCurFighterPa() - (spell.getPACost() - value));
                this.curFighterUsedPa += spell.getPACost() - value;
            } else {
                setCurFighterPa(getCurFighterPa() - spell.getPACost());
                this.curFighterUsedPa += spell.getPACost();
            }

            boolean isEc = spell.getTauxEC() != 0 && Formulas.getRandomValue(1, spell.getTauxEC()) == spell.getTauxEC();
            if(fighter.hasBuff(782)) isEc = false;

            if (isEc) {
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 302, fighter.getId() + "", spell.getSpellID() + ""); // envoi de  l'EC
            } else {
                if (this.getType() != Constant.FIGHT_TYPE_CHALLENGE && this.getAllChallenges().size() > 0 && !current.isInvocation() && !current.isDouble() && !current.isCollector()) {
                    this.getAllChallenges().values().stream().filter(Objects::nonNull)
                            .forEach(challenge -> {
                                challenge.onPlayerAction(current, spell.getSpellID());
                                if (spell.getSpell().getId() != 0)
                                    challenge.onPlayerSpell(current, spell);
                            });
                }

                boolean isCC = fighter.testIfCC(spell.getTauxCC(), spell, fighter);
                if(fighter.hasBuff(781)) isCC = false;


                String sort = spell.getSpellID() + "," + cell + "," + spell.getSpriteID() + "," + spell.getLevel() + "," + spell.getSpriteInfos();
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 300, fighter.getId() + "", sort); // xx lance le sort

                if (isCC)
                    SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 301, fighter.getId() + "", sort); // CC !
                if (fighter.isHide()) // Si le joueur est invi, on montre la case
                {
                    if (spell.getSpellID() == 0)// Si le coup est Coup de Poing alors on refait apparaitre le personnage
                        fighter.unHide(cell);
                    else
                        showCaseToAll(fighter.getId(), fighter.getCell().getId());
                }
                spell.applySpellEffectToFight(this, fighter, Cell, isCC, false); // on applique les effets de l'arme
            }
            // le client ne peut continuer sans l'envoi de ce packet qui annonce le co�t en PA
            if (fighter.getType() == 1 && player.getObjectsClassSpell().containsKey(spell.getSpellID())) {
                int value = player.getValueOfClassObject(spell.getSpellID(), 285);
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 102, fighter.getId() + "", fighter.getId() + ",-" + (spell.getPACost() - value));
            } else {
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 102, fighter.getId() + "", fighter.getId() + ",-" + spell.getPACost());
            }
            if (!isEc)
                fighter.addLaunchedSort(Cell.getFirstFighter(), spell, fighter);

            if ((isEc && spell.isEcEndTurn())) {
                TimerWaiter.addNext(() -> this.setCurAction(""), 500);

                if (fighter.getMob() != null || fighter.isInvocation()) {
                    return 5;
                } else {
                    endTurn(false, current);
                    return 5;
                }
            }
        } else if (fighter.getMob() != null || fighter.isInvocation()) {
            TimerWaiter.addNext(() -> this.setCurAction(""), 600);
            return 10;
        }

        this.verifIfTeamAllDead();

        TimerWaiter.addNext(() -> {
            this.setCurAction("");
            if (fighter.getPlayer() != null) {
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 102, fighter.getId() + "", fighter.getId() + ",-0");
            }
        }, 1000);
        return 0;
    }

    public void forceCastSpellMob(Fighter fighter, SortStats spell, int cell) {
        final Fighter current = this.getFighterByGameOrder();
        GameCase Cell = getMap().getCase(cell);

        if(!this.canCastSpellMob(fighter, spell, Cell, -1))
            return;

        boolean isEc = spell.getTauxEC() != 0 && Formulas.getRandomValue(1, spell.getTauxEC()) == spell.getTauxEC();

        if (isEc) {
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 302, fighter.getId() + "", spell.getSpellID() + ""); // envoi de  l'EC
        } else {
            if (this.getType() != Constant.FIGHT_TYPE_CHALLENGE && this.getAllChallenges().size() > 0 && !current.isInvocation() && !current.isDouble() && !current.isCollector()) {
                this.getAllChallenges().values().stream().filter(Objects::nonNull)
                        .forEach(challenge -> {
                            challenge.onPlayerAction(current, spell.getSpellID());
                            if (spell.getSpell().getId() != 0)
                                challenge.onPlayerSpell(current, spell);
                        });
            }

            boolean isCC = fighter.testIfCC(spell.getTauxCC(), spell, fighter);
            String sort = spell.getSpellID() + "," + cell + "," + spell.getSpriteID() + "," + spell.getLevel() + "," + spell.getSpriteInfos();
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 300, fighter.getId() + "", sort); // xx lance le sort

            if (isCC)
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 301, fighter.getId() + "", sort); // CC !
            if (fighter.isHide()) // Si le joueur est invi, on montre la case
            {
                if (spell.getSpellID() == 0)// Si le coup est Coup de Poing alors on refait apparaitre le personnage
                    fighter.unHide(cell);
                else
                    showCaseToAll(fighter.getId(), fighter.getCell().getId());
            }
            spell.applySpellEffectToFight(this, fighter, Cell, isCC, false); // on applique les effets de l'arme
        }

        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 102, fighter.getId() + "", fighter.getId() + ",-" + spell.getPACost());

        if (!isEc)
            fighter.addLaunchedSort(Cell.getFirstFighter(), spell, fighter);

        if ((isEc && spell.isEcEndTurn())) {
            TimerWaiter.addNext(() -> this.setCurAction(""), 500);

            if (fighter.getMob() != null || fighter.isInvocation()) {
                return;
            } else {
                endTurn(false, current);
                return;
            }
        }

        this.verifIfTeamAllDead();

        TimerWaiter.addNext(() -> {
            this.setCurAction("");
            if (fighter.getPlayer() != null) {
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 102, fighter.getId() + "", fighter.getId() + ",-0");
            }
        }, 1000);
    }

    public boolean canCastSpell1(Fighter caster, SortStats spell, GameCase cell, int targetCell) {
        final Fighter current = this.getFighterByGameOrder();

        if (current == null)
            return false;

        int casterCell = targetCell <= -1 ? caster.getCell().getId() : targetCell;
        Player player = caster.getPlayer();

        if (spell == null) {
            if (player != null) {
                SocketManager.GAME_SEND_GA_CLEAR_PACKET_TO_FIGHT(this, 7);
                SocketManager.GAME_SEND_Im_PACKET(player, "1169");
                SocketManager.GAME_SEND_GAF_PACKET_TO_FIGHT(this, 7, 0, player.getId());
            }
            return false;
        }

        if (current.getId() != caster.getId()) {
            if (player != null)
                SocketManager.GAME_SEND_Im_PACKET(player, "1175");
            return false;
        }

        if(cell != null) {
            for (SpellEffect effect : spell.getEffects()) {
                if (effect.getEffectID() == 400) {
                    for (Trap trap : this.getAllTraps()) {
                        if (trap.getCell().getId() == cell.getId()) {
                            if(caster.getPlayer() != null)
                                caster.getPlayer().send("Im1229");
                            return false;
                        }
                    }
                }
            }
        }

        int usedPA;

        if (spell.getSpell().hasInvalidState(caster) || !spell.getSpell().hasNeededState(caster))
            return false;

        if (caster.getType() == 1 && player.getObjectsClassSpell().containsKey(spell.getSpellID())) {
            int modi = player.getValueOfClassObject(spell.getSpellID(), 285);
            usedPA = spell.getPACost() - modi;
        } else {
            usedPA = spell.getPACost();
        }

        if (getCurFighterPa() < usedPA) {
            if (player != null)
                SocketManager.GAME_SEND_Im_PACKET(player, "1170;" + getCurFighterPa() + "~" + spell.getPACost());
            return false;
        }

        if (cell == null) {
            if (player != null)
                SocketManager.GAME_SEND_Im_PACKET(player, "1172");
            return false;
        }

        if (caster.getType() == 1 && player.getObjectsClassSpell().containsKey(spell.getSpellID())) {
            int modi = player.getValueOfClassObject(spell.getSpellID(), 288);
            boolean modif = modi == 1;
            if (spell.isLineLaunch() && !modif && !PathFinding.casesAreInSameLine(getMap(), casterCell, cell.getId(), 'z', 70)) {
                SocketManager.GAME_SEND_Im_PACKET(player, "1173");
                return false;
            }
        } else if (spell.isLineLaunch() && !PathFinding.casesAreInSameLine(getMap(), casterCell, cell.getId(), 'z', 70)) {
            if (player != null)
                SocketManager.GAME_SEND_Im_PACKET(player, "1173");
            return false;
        }

        char dir = PathFinding.getDirBetweenTwoCase(casterCell, cell.getId(), getMap(), true);

        if (spell.getSpellID() == 67) {
            if (!PathFinding.checkLoS(getMap(), PathFinding.GetCaseIDFromDirrection(casterCell, dir, getMap(), true), cell.getId(), null, true)) {
                if (player != null)
                    SocketManager.GAME_SEND_Im_PACKET(player, "1174");
                return false;
            }
        }

        boolean hasModification = player != null && player.getObjectsClassSpell().containsKey(spell.getSpellID());
        if (caster.getType() == 1 && hasModification) {
            int modi = player.getValueOfClassObject(spell.getSpellID(), 289);
            boolean modif = modi == 1;
            if (spell.hasLDV() && !Formulas.checkLos(this.getMap(), (short) casterCell, (short) cell.getId()) && !modif) {
                SocketManager.GAME_SEND_Im_PACKET(player, "1174");
                return false;
            }
        }

        if (!hasModification && spell.hasLDV() && !Formulas.checkLos(this.getMap(), (short) casterCell, (short) cell.getId())) {
            if (player != null)
                SocketManager.GAME_SEND_Im_PACKET(player, "1174");
            return false;
        }

        int dist = PathFinding.getDistanceBetween(getMap(), casterCell, cell.getId());
        int maxAlc = spell.getMaxPO();
        int minAlc = spell.getMinPO();
        // + porté
        if (caster.getType() == 1 && player != null && player.getObjectsClassSpell().containsKey(spell.getSpellID())) {
            int modi = player.getValueOfClassObject(spell.getSpellID(), 281);
            maxAlc = maxAlc + modi;
        }// porté modifiable

        if (caster.getType() == 1 && player != null && player.getObjectsClassSpell().containsKey(spell.getSpellID())) {
            int modi = player.getValueOfClassObject(spell.getSpellID(), 282);
            boolean modif = modi == 1;
            if (spell.isModifPO() || modif) {
                maxAlc += caster.getTotalStats().getEffect(117);
                if (maxAlc <= minAlc)
                    maxAlc = minAlc + 1;
            }
        } else if (spell.isModifPO()) {
            maxAlc += caster.getTotalStats().getEffect(117);
            if (maxAlc < minAlc)
                maxAlc = minAlc + 1;
        }

        if (maxAlc < minAlc)
            maxAlc = minAlc;
        if (dist < minAlc || dist > maxAlc) {
            if (player != null)
                SocketManager.GAME_SEND_Im_PACKET(player, "1171;" + minAlc + "~" + maxAlc + "~" + dist);
            return false;
        }

        if (!LaunchedSpell.cooldownGood(caster, spell.getSpellID())) {
            return false;
        }

        int numLunch = spell.getMaxLaunchbyTurn();

        if (caster.getType() == 1 && player != null && player.getObjectsClassSpell().containsKey(spell.getSpellID()))
            numLunch += player.getValueOfClassObject(spell.getSpellID(), 290);

        if (numLunch - LaunchedSpell.getNbLaunch(caster, spell.getSpellID()) <= 0 && numLunch > 0) {
            return false;
        }

        if(!this.checkKrakenState(caster, spell)) {
            return false;
        }

        Fighter t = cell.getFirstFighter();
        int numLunchT = spell.getMaxLaunchByTarget();

        if (caster.getType() == 1 && player != null && player.getObjectsClassSpell().containsKey(spell.getSpellID()))
            numLunchT += player.getValueOfClassObject(spell.getSpellID(), 291);

        return !(numLunchT - LaunchedSpell.getNbLaunchTarget(caster, t, spell.getSpellID()) <= 0 && numLunchT > 0);
    }

    public boolean canLaunchSpell(Fighter caster, SortStats spell, GameCase cell) {
        if (spell == null || spell.getSpell().hasInvalidState(caster) || !spell.getSpell().hasNeededState(caster))
            return false;
        if (getCurFighterPa() < spell.getPACost())
            return false;
        if (!LaunchedSpell.cooldownGood(caster, spell.getSpellID()))
            return false;
        if (spell.getMaxLaunchbyTurn() - LaunchedSpell.getNbLaunch(caster, spell.getSpellID()) <= 0 && spell.getMaxLaunchbyTurn() > 0)
            return false;
        if(cell == null) return true;
        return !(spell.getMaxLaunchByTarget() -
                LaunchedSpell.getNbLaunchTarget(caster, cell.getFirstFighter(), spell.getSpellID()) <= 0 && spell.getMaxLaunchByTarget() > 0);
    }

    public boolean canCastSpellMob(Fighter caster, SortStats spell, GameCase cell, int targetCell) {
        final Fighter current = this.getFighterByGameOrder();

        if (current == null || spell == null || current.getId() != caster.getId())
            return false;
        if (spell.getSpell().hasInvalidState(caster) || getCurFighterPa() < spell.getPACost() || cell == null)
            return false;

        int casterCell = targetCell <= -1 ? caster.getCell().getId() : targetCell;

        if (spell.isLineLaunch() && !PathFinding.casesAreInSameLine(getMap(), casterCell, cell.getId(), 'z', 70))
            return false;
        if (spell.hasLDV() && !Formulas.checkLos(this.getMap(), (short) casterCell, (short) cell.getId()))
            return false;

        int dist = PathFinding.getDistanceBetween(getMap(), casterCell, cell.getId());
        int maxAlc = spell.getMaxPO();
        int minAlc = spell.getMinPO();

        if (spell.isModifPO()) {
            maxAlc += caster.getTotalStats().getEffect(117);
            if (maxAlc <= minAlc)
                maxAlc = minAlc + 1;
        }

        if (maxAlc < minAlc)
            maxAlc = minAlc;
        if (dist < minAlc || dist > maxAlc)
            return false;
        if (!LaunchedSpell.cooldownGood(caster, spell.getSpellID()))
            return false;

        int numLunch = spell.getMaxLaunchbyTurn();

        if (numLunch - LaunchedSpell.getNbLaunch(caster, spell.getSpellID()) <= 0 && numLunch > 0)
            return false;
        if(!this.checkKrakenState(caster, spell))
            return false;

        Fighter t = cell.getFirstFighter();
        int numLunchT = spell.getMaxLaunchByTarget();

        return !(numLunchT - LaunchedSpell.getNbLaunchTarget(caster, t, spell.getSpellID()) <= 0 && numLunchT > 0);
    }

    private boolean checkKrakenState(Fighter caster, SortStats spell) {
        switch(spell.getSpellID()) {
            case 1106:  // Krala
                return caster.haveState(31) && caster.haveState(32) && caster.haveState(33) && caster.haveState(34);
            case 1097: // Tentacule
                return caster.haveState(31);
            case 1098:
                return caster.haveState(32);
            case 1099:
                return caster.haveState(33);
        }
        return true;
    }

    public boolean onFighterDeplace(Fighter fighter, GameAction GA) {
        final Fighter current = this.getFighterByGameOrder();
        if (current == null)
            return false;

        String path = GA.args;
        if (path.equals(""))
            return false;
        if (this.getOrderPlayingSize() <= getCurPlayer())
            return false;
        if (current.getId() != fighter.getId() || this.getState() != Constant.FIGHT_STATE_ACTIVE)
            return false;

        List<Fighter> targets = PathFinding.getEnemiesAround(fighter.getCell().getId(), getMap(), this);
        this.setCurAction("deplace");

        if(!fighter.haveState(Constant.ETAT_ENRACINE)) {
            for (Fighter target : targets) {
                if (target != null && !target.haveState(Constant.ETAT_ENRACINE) && !target.haveState(Constant.ETAT_PORTE) && !fighter.isHide() && !target.isHide()) {
                    int esquive = Formulas.getTacleChance(fighter, target);
                    int rand = Formulas.getRandomValue(0, 99);

                    if (rand > esquive) {
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, GA.id, "104", fighter.getId() + ";", "");
                        int looseAP = getCurFighterPa() * esquive / 100;
                        if (looseAP < 0)
                            looseAP = -looseAP;
                        if (getCurFighterPm() < 0)
                            setCurFighterPm(0);
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, GA.id, "129", fighter.getId() + "", fighter.getId() + ",-" + getCurFighterPm());
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, GA.id, "102", fighter.getId() + "", fighter.getId() + ",-" + looseAP);
                        setCurFighterPm(0);
                        setCurFighterPa(getCurFighterPa() - looseAP);
                        this.setCurAction("");
                        if ((getType() == Constant.FIGHT_TYPE_PVM) && (getAllChallenges().size() > 0) && !current.isInvocation() && !current.isDouble() && !current.isCollector() || (getType() == Constant.FIGHT_TYPE_DOPEUL) && (getAllChallenges().size() > 0) && !current.isInvocation() && !current.isDouble() && !current.isCollector())
                            this.getAllChallenges().values().stream().filter(Objects::nonNull).forEach(c -> c.onPlayerMove(fighter, true));
                        return false;
                    }
                }
            }
        }
        AtomicReference<String> pathRef = new AtomicReference<>(path);
        int nStep = PathFinding.isValidPath(getMap(), fighter.getCell().getId(), pathRef, this, null, -1);
        String newPath = pathRef.get();

        if (nStep > getCurFighterPm() || nStep == -1000) {
            if (fighter.getPlayer() != null)
                SocketManager.GAME_SEND_GA_PACKET(fighter.getPlayer().getGameClient(), "", "0", "", "");
            this.setCurAction("");
            return false;
        }
        setCurFighterPm(getCurFighterPm() - nStep);
        this.curFighterUsedPm += nStep;

        int nextCellID = World.world.getCryptManager().cellCode_To_ID(newPath.substring(newPath.length() - 2));
        // les monstres n'ont pas de GAS//GAF
        if (current.getPlayer() != null)
            SocketManager.GAME_SEND_GAS_PACKET_TO_FIGHT(this, 7, current.getId());

        // Si le joueur n'est pas invisible
        if (!current.isHide()) {
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, GA.id, "1", current.getId() + "", "a" + World.world.getCryptManager().cellID_To_Code(fighter.getCell().getId()) + newPath);
        } else {
            if (current.getPlayer() != null) {
                // On envoie le path qu'au joueur qui se d�place
                GameClient out = current.getPlayer().getGameClient();
                SocketManager.GAME_SEND_GA_PACKET(out, GA.id + "", "1", current.getId() + "", "a" + World.world.getCryptManager().cellID_To_Code(fighter.getCell().getId()) + newPath);
            }
        }

        // Si port�
        final Fighter po = current.getHoldedBy();

        if (po != null) {
            // si le joueur va bouger
            if ((short) nextCellID != po.getCell().getId()) {
                // on retire les �tats
                po.setState(Constant.ETAT_PORTEUR, 0);
                current.setState(Constant.ETAT_PORTE, 0);
                // on retire d� lie les 2 fighters
                po.setIsHolding(null);
                current.setHoldedBy(null);
                // La nouvelle case sera d�finie plus tard dans le code. On
                // envoie les packets !
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 950, po.getId() + "", po.getId() + "," + Constant.ETAT_PORTEUR + ",0");
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 950, current.getId() + "", current.getId() + "," + Constant.ETAT_PORTE + ",0");
            }
        }

        current.getCell().getFighters().clear();
        current.setCell(getMap().getCase(nextCellID));
        current.getCell().addFighter(current);

        if (po != null)// m�me erreur que tant�t, bug ou plus de fighter sur la
            // case
            po.getCell().addFighter(po);
        if (nStep < 0) {
            nStep = nStep * (-1);
        }

        setCurAction("GA;129;" + current.getId() + ";" + current.getId() + ",-" + nStep);
        // Si porteur
        final Fighter po2 = current.getIsHolding();

        if (po2 != null && current.haveState(Constant.ETAT_PORTEUR)
                && po2.haveState(Constant.ETAT_PORTE)) {
            // on d�place le port� sur la case
            po2.setCell(current.getCell());
        }

        if (fighter.getPlayer() == null) {
            SocketManager.GAME_SEND_GAMEACTION_TO_FIGHT(this, 7, this.getCurAction());
            this.setCurAction("");
            new ArrayList<>(this.getAllTraps()).stream().filter(Objects::nonNull).filter(trap -> PathFinding.getDistanceBetween(getMap(), trap.getCell().getId(), current.getCell().getId()) <= trap.getSize()).forEach(trap -> trap.onTraped(current));
            return true;
        }

        if ((getType() == Constant.FIGHT_TYPE_PVM) && (getAllChallenges().size() > 0) && !current.isInvocation() && !current.isDouble() && !current.isCollector() || (getType() == Constant.FIGHT_TYPE_DOPEUL) && (getAllChallenges().size() > 0) && !current.isInvocation() && !current.isDouble() && !current.isCollector())
            this.getAllChallenges().values().stream().filter(Objects::nonNull).forEach(c -> c.onPlayerMove(fighter, false));

        fighter.getPlayer().getGameClient().addAction(GA);
        return true;
    }

    public void onFighterDie(Fighter target, Fighter caster) {
        final Fighter current = this.getFighterByGameOrder();

        if (current == null)
            return;

        Player player;
        //region Statistics
        if (Config.modeHeroic) {
            Player p = caster.getPlayer(), deadPlayer = target.getPlayer();
            if (deadPlayer != null) {
                byte type = caster.isMob() ? (byte) 2 : p == deadPlayer ? (byte) -1 : (byte) 1;
                long id = type == 1 ? p.getId() : type == 2 ? caster.getMob().getTemplate().getId() : 0;
                target.killedBy = new Couple<>(type, id);
            }
            if (p != null && target != caster && deadPlayer != null) p.increaseTotalKills();
        }
        if((player = target.getPlayer()) != null) {
             if(caster != target) {
                 Player temp = null;
                 boolean ok = false;
                 if(caster != null && (this.type == Constant.FIGHT_TYPE_AGRESSION || this.type == Constant.FIGHT_TYPE_CONQUETE || this.type == Constant.FIGHT_TYPE_ROYAL)) {
                     if(caster.getMob() != null && caster.getInvocator() != null && caster.getInvocator().getPlayer() != null) {
                         ok = true;
                         temp = caster.getInvocator().getPlayer();
                     } else if(caster.getPlayer() != null) {
                         temp = caster.getPlayer();
                         if(temp != player) {
                             ok = true;
                         }
                     }
                     if(ok && temp != null) {
                         GameObject object = World.world.getObjTemplate(10275).createNewItem(1, false);
                         if(temp.addObjet(object, true))
                             World.world.addGameObject(object);
                         SocketManager.GAME_SEND_Im_PACKET(temp, "021;1~10275");
                     }
                 }
             }
        }
        //endregion

        if (!target.hasLeft())
            this.getDeadList().put(target.getId(), target);
        target.setIsDead(true);
        target.getCell().getFighters().clear();

        if (target.haveState(Constant.ETAT_PORTEUR)) {
            Fighter f = target.getIsHolding();
            f.setCell(f.getCell());
            f.getCell().addFighter(f);// Le bug venait par manque de ceci, il ni avait plus de firstFighter
            f.setState(Constant.ETAT_PORTE, 0);// J'ajoute ceci quand m�me pour signaler qu'ils ne sont plus en �tat port�/porteur
            target.setState(Constant.ETAT_PORTEUR, 0);
            f.setHoldedBy(null);
            target.setIsHolding(null);
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 950, f.getId() + "", f.getId() + "," + Constant.ETAT_PORTE + ",0");
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 950, target.getId() + "", target.getId() + "," + Constant.ETAT_PORTEUR + ",0");
        }
        if ((this.getType() == Constant.FIGHT_TYPE_PVM) && (this.getAllChallenges().size() > 0) || this.getType() == Constant.FIGHT_TYPE_DOPEUL && this.getAllChallenges().size() > 0)
            this.getAllChallenges().values().stream().filter(Objects::nonNull).forEach(challenge -> challenge.onFighterDie(target));

        if (target.getTeam() == 0) {
            HashMap<Integer, Fighter> team = new HashMap<>(this.getTeam0());

            for (Fighter entry : team.values()) {
                if (entry.getInvocator() == null)
                    continue;
                if (entry.getPdv() == 0 || entry.isDead())
                    continue;

                if (entry.getInvocator().getId() == target.getId()) {
                    this.onFighterDie(entry, caster);

                    try {
                        if(entry.getPlayer() == null) {
                        if (this.getOrderPlaying() != null) {
                            int index = this.getOrderPlaying().indexOf(entry);
                            if (index != -1)
                                this.getOrderPlaying().remove(index);
                        }
                            if (this.getTeam0().containsKey(entry.getId()))
                                this.getTeam0().remove(entry.getId());
                            else if (this.getTeam1().containsKey(entry.getId()))
                                this.getTeam1().remove(entry.getId());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 999, target.getId() + "", this.getGTL());
                }
            }
        } else if (target.getTeam() == 1) {
            HashMap<Integer, Fighter> team = new HashMap<>(this.getTeam1());

            for (Fighter fighter : team.values()) {
                if (fighter.getInvocator() == null)
                    continue;
                if (fighter.getPdv() == 0 || fighter.isDead())
                    continue;
                if (fighter.getInvocator().getId() == target.getId()) {// si il a �t� invoqu� par le joueur mort
                    fighter.setLevelUp(true);
                    this.onFighterDie(fighter, caster);

                    if(fighter.getPlayer() == null) {
                        if (this.getOrderPlaying() != null && !this.getOrderPlaying().isEmpty()) {
                            try {
                                int index = this.getOrderPlaying().indexOf(fighter);
                                if (index != -1)
                                    this.getOrderPlaying().remove(index);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (this.getTeam0().containsKey(fighter.getId()))
                            this.getTeam0().remove(fighter.getId());
                        else if (this.getTeam1().containsKey(fighter.getId()))
                            this.getTeam1().remove(fighter.getId());
                    }
                    SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 999, target.getId() + "", getGTL());
                }
            }
        }
        if (target.getMob() != null) {
            try {
                if (target.isInvocation() && !target.isStatique) {
                    target.getInvocator().nbrInvoc--;
                    // Il ne peut plus jouer, et est mort on revient au joueur
                    // pr�cedent pour que le startTurn passe au suivant

                    if(current.getId() == target.getId()) {
                        if (!target.canPlay()) {
                            this.setCurAction("");
                            this.setCurPlayer(getCurPlayer() - 1);
                            this.endTurn(false, current);
                        }
                        // Il peut jouer, et est mort alors on passe son tour
                        // pour que l'autre joue, puis on le supprime de l'index
                        // sans probl�mes
                        else if (target.canPlay()) {
                            this.setCurAction("");
                            this.endTurn(false, current);
                        }
                    }

                    if (this.getOrderPlaying() != null && !this.getOrderPlaying().isEmpty()) {
                        int index = this.getOrderPlaying().indexOf(target);
                        // Si le joueur courant a un index plus �lev�, on le
                        // diminue pour �viter le outOfBound
                        if (index != -1) {
                            if (getCurPlayer() > index && getCurPlayer() > 0)
                                this.setCurPlayer(getCurPlayer() - 1);
                            this.getOrderPlaying().remove(index);
                        }

                        if (this.getCurPlayer() < 0)
                            return;
                        if (this.getTeam0().containsKey(target.getId()))
                            this.getTeam0().remove(target.getId());
                        else if (this.getTeam1().containsKey(target.getId()))
                            this.getTeam1().remove(target.getId());
                        //SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 999, target.getId() + "", this.getGTL());
                    }
                    this.setCurAction("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final Fighter casterFinal = caster;
        if ((getType() == Constant.FIGHT_TYPE_PVM || getType() == Constant.FIGHT_TYPE_DOPEUL) && getAllChallenges().size() > 0)
            this.getAllChallenges().values().stream().filter(Objects::nonNull).forEach(challenge -> challenge.onMobDie(target, casterFinal));

        new ArrayList<>(this.getAllGlyphs()).stream().filter(glyph -> glyph.getCaster().getId() == target.getId()).forEach(glyph -> {
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 7, 999, casterFinal.getId() + "", "GDZ-" + glyph.getCell().getId() + ";" + glyph.getSize() + ";" + glyph.getColor());
            SocketManager.GAME_SEND_GDC_PACKET_TO_FIGHT(this, 7, glyph.getCell().getId());
            this.getAllGlyphs().remove(glyph);
        });

        new ArrayList<>(this.getAllTraps()).stream().filter(trap -> trap.getCaster().getId() == target.getId()).forEach(trap -> {
            trap.desappear();
            this.getAllTraps().remove(trap);
        });

        if(target.getId() == caster.getId()) {
            TimerWaiter.addNext(() -> {
                SocketManager.GAME_SEND_FIGHT_PLAYER_DIE_TO_FIGHT(this, 7, target.getId());
                SocketManager.GAME_SEND_GTL_PACKET_TO_FIGHT(this, 7);

                if (target.canPlay() && current.getId() == target.getId() && !current.hasLeft())
                    this.endTurn(false, current);
            }, 3000);
        } else {
            SocketManager.GAME_SEND_FIGHT_PLAYER_DIE_TO_FIGHT(this, 7, target.getId());
            SocketManager.GAME_SEND_GTL_PACKET_TO_FIGHT(this, 7);

            if (target.canPlay() && current.getId() == target.getId() && !current.hasLeft())
                this.endTurn(false, current);
        }

        if (target.isCollector()) {// Le percepteur viens de mourrir on met fin au
            this.getFighters(target.getTeam2()).stream().filter(f -> !f.isDead()).forEach(f -> {
                this.onFighterDie(f, target);
                this.verifIfTeamAllDead();
            });
        }
        if (target.isPrisme()) {
            this.getFighters(target.getTeam2()).stream().filter(f -> !f.isDead()).forEach(f -> {
                this.onFighterDie(f, target);
                this.verifIfTeamAllDead();
            });
        }

        for (Fighter fighter : getFighters(3)) {
            ArrayList<SpellEffect> newBuffs = new ArrayList<>();
            for (SpellEffect entry : fighter.getFightBuff()) {
                switch (entry.getSpell()) {
                    case 78:
                    case 431:
                    case 433:
                    case 437:
                    case 441:
                    case 443:
                        newBuffs.add(entry);
                        continue;
                }
                if (entry.getCaster().getId() != target.getId())
                    newBuffs.add(entry);
            }
            fighter.getFightBuff().clear();
            fighter.getFightBuff().addAll(newBuffs);
        }
        SocketManager.GAME_SEND_GTL_PACKET_TO_FIGHT(this, 7);
        this.verifIfTeamAllDead();
    }

    public ArrayList<Fighter> getFighters(int teams) {// Entre 0 et 7, binaire([spec][t2][t1]).
        ArrayList<Fighter> fighters = new ArrayList<>();

        if (teams - 4 >= 0) {
            fighters.addAll(new ArrayList<>(this.getViewer().values()).stream().filter(Objects::nonNull).map(player -> new Fighter(this, player)).collect(Collectors.toList()));
            teams -= 4;
        }
        if (teams - 2 >= 0) {
            new ArrayList<>(this.getTeam1().values()).stream().filter(Objects::nonNull).forEach(fighters::add);
            teams -= 2;
        }
        if (teams - 1 >= 0)
            new ArrayList<>(this.getTeam0().values()).stream().filter(Objects::nonNull).forEach(fighters::add);
        return fighters;
    }

    ArrayList<Fighter> getFighters2(int teams) {
        ArrayList<Fighter> fighters = new ArrayList<>();

        if (teams == 0)
            fighters.addAll(getViewer().entrySet().stream().map(entry -> new Fighter(this, entry.getValue())).collect(Collectors.toList()));
        if (teams == 2)
            fighters.addAll(getTeam1().entrySet().stream().map(Entry::getValue).collect(Collectors.toList()));
        if (teams == 1)
            fighters.addAll(getTeam0().entrySet().stream().map(Entry::getValue).collect(Collectors.toList()));
        return fighters;
    }

    public Fighter getFighterByPerso(Player player) {
        Fighter fighter = null;
        if (this.getTeam0().get(player.getId()) != null)
            fighter = this.getTeam0().get(player.getId());
        if (this.getTeam1().get(player.getId()) != null)
            fighter = this.getTeam1().get(player.getId());
        return fighter;
    }

    private GameCase getRandomCell(List<GameCase> cells) {
        GameCase cell;
        if (cells.isEmpty()) return null;

        int limit = 0;
        do {
            cell = cells.get(Formulas.random.nextInt(cells.size()));
            limit++;
        } while ((cell == null || !cell.getFighters().isEmpty()) && limit < 80);

        if (limit == 80)
            return null;
        return cell;
    }

    public synchronized void exchangePlace(Player player, int cell) {
        Fighter fighter = getFighterByPerso(player);

        if (fighter == null || (collector != null && this.collectorProtect && collector.getDefenseFight() != null && !collector.getDefenseFight().containsValue(player)))
            return;

        int team = fighter.getTeam();
        boolean valid1 = false, valid2 = false;

        for (int a = 0; a < getStart0().size(); a++)
            if (getStart0() != null && getStart0().get(a) != null && getStart0().get(a).getId() == cell)
                valid1 = true;
        for (int a = 0; a < getStart1().size(); a++)
            if (getStart1() != null && getStart1().get(a) != null && getStart1().get(a).getId() == cell)
                valid2 = true;
        if (getState() != 2 || isOccuped(cell) || player.isReady() || (team == 0 && !valid1) || (team == 1 && !valid2))
            return;
        fighter.getCell().getFighters().clear();
        fighter.setCell(getMap().getCase(cell));
        getMap().getCase(cell).addFighter(fighter);
        SocketManager.GAME_SEND_FIGHT_CHANGE_PLACE_PACKET_TO_FIGHT(this, 3, getMap(), player.getId(), cell);
    }

    public boolean isOccuped(int cell) {
        return getMap().getCase(cell) == null || getMap().getCase(cell).getFighters().size() > 0;
    }

    public int getNextLowerFighterGuid() {
        return nextId--;
    }

    public void addFighterInTeam(Fighter f, int team) {
        if (team == 0)
            getTeam0().put(f.getId(), f);
        else if (team == 1)
            getTeam1().put(f.getId(), f);
    }

    private void addChevalier() {
        String groupData = "";
        int a = 0;
        for (Fighter F : getTeam0().values()) {
            if (F.getPlayer() == null)
                continue;
            if (getTeam1().size() > getTeam0().size())
                continue;
            groupData = groupData + "394,"
                    + Constant.getLevelForChevalier(F.getPlayer()) + ","
                    + Constant.getLevelForChevalier(F.getPlayer());
            if (a < getTeam0().size() - 1)
                groupData = groupData + ";";
            a++;
        }
        setMobGroup(new Monster.MobGroup(getMapOld().nextObjectId, this.map, getInit0().getPlayer().getCurCell().getId(), groupData));
        for (Entry<Integer, Monster.MobGrade> entry : getMobGroup().getMobs().entrySet()) {
            entry.getValue().setInFightID(entry.getKey());
            getTeam1().put(entry.getKey(), new Fighter(this, entry.getValue()));
        }
        List<Entry<Integer, Fighter>> e = new ArrayList<>();
        e.addAll(getTeam1().entrySet());
        for (Entry<Integer, Fighter> entry : e) {
            if (entry.getValue().getPlayer() != null)
                continue;
            Fighter f = entry.getValue();
            GameCase cell = getRandomCell(getStart1());
            if (cell == null) {
                getTeam1().remove(f.getId());
            } else {
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, f.getId()
                        + "", f.getId() + "," + Constant.ETAT_PORTE + ",0");
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, f.getId()
                        + "", f.getId() + "," + Constant.ETAT_PORTEUR + ",0");
                f.setCell(cell);
                f.getCell().addFighter(f);
                f.setTeam(1);
                SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_MAP(getInit0().getPlayer().getCurMap(), getMobGroup().getId(), entry.getValue());
            }
        }
        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS_TO_FIGHT(this, 7, getMap());
    }

    public boolean onPlayerDisconnection(Player player, boolean check) {
        if (this.getState() == Constant.FIGHT_STATE_INIT)
            return true;

        final Fighter current = this.getFighterByGameOrder(), target = this.getFighterByPerso(player);

        if (target == null)
            return false;
        if (player.start != null) {
            this.endFight((byte) 1);
            return true;
        }

        if (this.getState() == Constant.FIGHT_STATE_INIT || this.getState() == Constant.FIGHT_STATE_FINISHED) {
            if (!check)
                this.leftFight(player, null);
            return false;
        }

        if (target.getNbrDisconnection() >= 5 && !Config.modeHeroic) {
            if (!check) {
                this.leftFight(player, null);
                for (Fighter fighter : this.getFighters(7)) {
                    if (!(fighter.getPlayer() == null || !fighter.getPlayer().isOnline()))
                        SocketManager.GAME_SEND_MESSAGE(fighter.getPlayer(), target.getPacketsName() + " s'est déconnecté plus de 5 fois dans le même combat, nous avons décidé de lui faire abandonner.", "A00000");
                }
            }
            return false;
        }
        if (!check) {
            if (!player.getFight().getFighterByPerso(player).isDeconnected())
                SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 7, "1182;" + target.getPacketsName() + "~20");
            target.Disconnect();
        }
        if (current != null && current.getId() == target.getId())
            this.endTurn(false, current);
        return true;
    }

    public boolean onPlayerReconnection(final Player player) {
        final Fighter current = this.getFighterByGameOrder(), target = getFighterByPerso(player);

        if (target == null || this.getState() == Constant.FIGHT_STATE_FINISHED)
            return false;
        /*if (getState() == Constant.FIGHT_STATE_INIT)
            return false;*/
        target.Reconnect();

        SocketManager.GAME_SEND_Im_PACKET_TO_FIGHT(this, 7, "1184;" + target.getPacketsName());

        if (getState() == Constant.FIGHT_STATE_ACTIVE)
            SocketManager.GAME_SEND_GJK_PACKET(player, getState(), 0, 0, 0, 0, getType());// Join Fight => getState(), pas d'anulation...
        else {
            if (getType() == Constant.FIGHT_TYPE_CHALLENGE)
                SocketManager.GAME_SEND_GJK_PACKET(player, 2, 1, 1, 0, 0, getType());
            else
                SocketManager.GAME_SEND_GJK_PACKET(player, 2, 0, 1, 0, 0, getType());
        }

        Fighter f = (target.getTeam() == 0 ? getInit0() : getInit1());
        if(f != null)
            SocketManager.GAME_SEND_ADD_IN_TEAM_PACKET_TO_PLAYER(player, getMap(), f.getId(), target);// Indication de la team
        SocketManager.GAME_SEND_STATS_PACKET(player);

        SocketManager.GAME_SEND_MAP_FIGHT_GMS_PACKETS(this, getMap(), player);

        if (getState() == Constant.FIGHT_STATE_PLACE) {
            SocketManager.GAME_SEND_FIGHT_PLACES_PACKET(player.getGameClient(), getMap().getPlaces(), getSt1());
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, player.getId() + "", player.getId() + "," + Constant.ETAT_PORTE + ",0");
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this, 3, 950, player.getId() + "", player.getId() + "," + Constant.ETAT_PORTEUR + ",0");
        } else {

            SocketManager.GAME_SEND_GS_PACKET(player);// D�but du jeu
            SocketManager.GAME_SEND_GTL_PACKET(player, this);// Liste des tours

            if(current != null)
                SocketManager.GAME_SEND_GAMETURNSTART_PACKET(player, current.getId(), (int) (System.currentTimeMillis() - launchTime));

            if (this.getType() == Constant.FIGHT_TYPE_PVM || this.getType() == Constant.FIGHT_TYPE_DOPEUL && this.getAllChallenges().size() > 0) {
                this.getAllChallenges().values().stream().filter(challenge -> challenge != null && !challenge.loose())
                        .forEach(challenge -> {
                            SocketManager.GAME_SEND_CHALLENGE_PERSO(player, challenge.parseToPacket());
                            if (!challenge.loose())
                                challenge.challengeSpecLoose(player);
                        });
            }
            for (Fighter f1 : getFighters(3)) {
                f1.sendState(player);
            }
            // Si combat en cours on envois des im
            ArrayList<Fighter> all = new ArrayList<>();
            all.addAll(this.getTeam0().values());
            all.addAll(this.getTeam1().values());
            all.stream().filter(fighter -> fighter != null && (fighter.isHide())).forEach(f1 -> {
                SpellEffect effect = f1.getBuff(150);
                if(effect != null) {
                    SocketManager.GAME_SEND_GA_PACKET(this, player, 150, effect.getCaster().getId() + "", f1.getId() + "," + effect.getTurn());
                }
            });
            for(Trap trap : this.getAllTraps())
                if(trap.getCaster().getTeam() == target.getTeam())
                    trap.refresh(target);
            for(Glyph glyph : this.getAllGlyphs()) {
                SocketManager.GAME_SEND_GA_PACKET(this, target.getPlayer(), 999, glyph.getCaster().getId() + "",
                        "GDZ+" + glyph.getCell().getId() + ";" + glyph.getSize() + ";" + glyph.getColor());
                SocketManager.GAME_SEND_GA_PACKET(this, target.getPlayer(), 999, glyph.getCaster().getId() + "",
                        "GDC" + glyph.getCell().getId() + ";Haaaaaaaaa3005;");
            }
        }
        return true;
    }

    public void verifIfAllReady() {
        boolean val = true;
        if (getType() == Constant.FIGHT_TYPE_DOPEUL) {
            for (Fighter f : getTeam0().values()) {
                if (f == null || f.getPlayer() == null)
                    continue;
                Player perso = f.getPlayer();
                if (!perso.isReady())
                    val = false;
            }
            if (val)
                startFight();
            return;
        }

        for (int a = 0; a < getTeam0().size(); a++)
            if (!getTeam0().get(getTeam0().keySet().toArray()[a]).getPlayer().isReady())
                val = false;

        if (getType() != 4 && getType() != 5 && getType() != 7
                && getType() != Constant.FIGHT_TYPE_CONQUETE)
            for (int a = 0; a < getTeam1().size(); a++)
                if (!getTeam1().get(getTeam1().keySet().toArray()[a]).getPlayer().isReady())
                    val = false;

        if (getType() == 5 || getType() == 2)
            val = false;
        if (val)
            startFight();
    }

    private boolean verifyStillInFight()// Return true si au moins un joueur est encore dans le combat
    {
        if(this.mobGroup2 != null) return false;
        for (Fighter f : getTeam0().values()) {
            if (f.isCollector())
                return false;
            if (f.isInvocation() || f.isDead() || f.getPlayer() == null
                    || f.getMob() != null || f.getDouble() != null
                    || f.hasLeft())
                continue;
            if (f.getPlayer() != null
                    && f.getPlayer().getFight() != null
                    && f.getPlayer().getFight().getId() == this.getId()) // Si il n'est plus dans ce combat
                return false;
        }
        for (Fighter f : getTeam1().values()) {
            if (f.isCollector())
                return false;
            if (f.isInvocation() || f.isDead() || f.getPlayer() == null
                    || f.getMob() != null || f.getDouble() != null
                    || f.hasLeft())
                continue;
            if (f.getPlayer() != null
                    && f.getPlayer().getFight() != null
                    && f.getPlayer().getFight().getId() == this.getId()) // Si il n'est plus dans ce combat
                return false;
        }
        return true;
    }

    boolean verifIfTeamIsDead() {
        boolean finish = true;
        for (Entry<Integer, Fighter> entry : getTeam1().entrySet()) {
            if (entry.getValue().isInvocation())
                continue;
            if (!entry.getValue().isDead()) {
                finish = false;
                break;
            }
        }
        return finish;
    }

    public void verifIfTeamAllDead() {
        if (getState() >= Constant.FIGHT_STATE_FINISHED)
            return;

        boolean team0 = true, team1 = true;

        for (Fighter fighter : new ArrayList<>(getTeam0().values())) {
            if (fighter.isInvocation())
                continue;
            if (!fighter.isDead()) {
                team0 = false;
                break;
            }
        }

        for (Fighter fighter : new ArrayList<>(getTeam1().values())) {
            if (fighter.isInvocation())
                continue;
            if (!fighter.isDead()) {
                team1 = false;
                break;
            }
        }


        if ((team0 || team1 || verifyStillInFight()) && !this.finish) {
            this.finish = true;

            final Map<Integer, Fighter> copyTeam0 = new HashMap<>();
            final Map<Integer, Fighter> copyTeam1 = new HashMap<>();
            for (Entry<Integer, Fighter> entry : this.getTeam0().entrySet()) {
                if (entry.getValue().getMob() != null)
                    if (entry.getValue().getMob().getTemplate().getId() == 375)
                        Bandit.getBandits().setPop(false);
                copyTeam0.put(entry.getKey(), entry.getValue());
            }

            for (Entry<Integer, Fighter> entry : this.getTeam1().entrySet()) {
                if (entry.getValue().getMob() != null)
                    if (entry.getValue().getMob().getTemplate().getId() == 375)
                        Bandit.getBandits().setPop(false);
                copyTeam1.put(entry.getKey(), entry.getValue());
            }

            final boolean winners = team0;

            final ArrayList<Fighter> fighters = new ArrayList<>();
            final ArrayList<Fighter> winTeam = new ArrayList<>(), looseTeam = new ArrayList<>();
            fighters.addAll(copyTeam0.values());
            fighters.addAll(copyTeam1.values());
            if(this.turn != null) {
                this.turn.stop();
                this.turn = null;
            }

            try {
                String challenges = "";
                if ((this.getType() == Constant.FIGHT_TYPE_PVM && this.getAllChallenges().size() > 0)
                        || (getType() == Constant.FIGHT_TYPE_DOPEUL && this.getAllChallenges().size() > 0)) {
                    for (Challenge challenge : getAllChallenges().values()) {
                        if (challenge != null) {
                            challenge.fightEnd();
                            challenges += (challenges.isEmpty() ? challenge.getPacketEndFight() : "," + challenge.getPacketEndFight());
                        }
                    }
                }

                this.setState(Constant.FIGHT_STATE_FINISHED);


                if (winners) {
                    looseTeam.addAll(copyTeam0.values());
                    winTeam.addAll(copyTeam1.values());
                } else {
                    winTeam.addAll(copyTeam0.values());
                    looseTeam.addAll(copyTeam1.values());
                }

                if (this.type != -1) {
                    if (Constant.FIGHT_TYPE_PVM == this.getType() && this.getMapOld().hasEndFightAction(this.getType())) {
                        for (Fighter fighter : winTeam) {
                            Player player = fighter.getPlayer();
                            if (player == null)
                                continue;

                            player.setFight(null);

                            if (fighter.isDeconnected()) {
                                player.setNeededEndFight(this.getType(), this.getMobGroup());
                                player.getCurMap().applyEndFightAction(player);
                                player.setNeededEndFight(-1, null);
                            } else {
                                player.setNeededEndFight(this.getType(), this.getMobGroup());
                            }
                        }
                    }
                }

                final String packet = this.type == -1 ? "GE" : this.getGE(winners ? 2 : 1);

                for (Fighter fighter : fighters) {
                    Player player = fighter.getPlayer();
                    if (player != null) {
                        player.setFight(null);
                        SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(this.getMap(), fighter.getId());
                    }
                }

                this.setCurPlayer(-1);

                switch (this.getType()) {
                    case Constant.FIGHT_TYPE_CHALLENGE:
                    case Constant.FIGHT_TYPE_AGRESSION:
                    case Constant.FIGHT_TYPE_CONQUETE:
                        for (Fighter fighter : copyTeam1.values()) {
                            Player player = fighter.getPlayer();

                            if (player != null) {
                                player.setDuelId(-1);
                                player.setReady(false);
                            }
                        }
                        break;
                }

                for (Fighter fighter : this.getTeam0().values()) {
                    Player player = fighter.getPlayer();

                    if (player != null) {
                        player.setDuelId(-1);
                        player.setReady(false);
                    }
                }

                for (Fighter fighter : this.getFighters(3))
                    fighter.getFightBuff().clear();

                this.getMapOld().removeFight(this.getId());
                SocketManager.GAME_SEND_MAP_FIGHT_COUNT_TO_MAP(World.world.getMap(this.getMap().getId()));

                final String str = (this.getPrism() != null ? this.getPrism().getMap() + "|" + this.getPrism().getX() + "|" + this.getPrism().getY() : "");

                this.setMap(null);
                this.orderPlaying = null;

                if (this.type != -1) {
                    /** WINNER **/
                    for (Fighter fighter : winTeam) {
                        /** Collector **/
                        if (fighter.getCollector() != null) {
                            World.world.getGuild(getGuildId()).getPlayers().stream().filter(Objects::nonNull).filter(Player::isOnline).forEach(player -> {
                                SocketManager.GAME_SEND_gITM_PACKET(player, Collector.parseToGuild(player.getGuild().getId()));
                                SocketManager.GAME_SEND_PERCO_INFOS_PACKET(player, fighter.getCollector(), "S");
                            });

                            fighter.getCollector().setInFight((byte) 0);
                            fighter.getCollector().set_inFightID((byte) -1);
                            fighter.getCollector().clearDefenseFight();

                            this.getMapOld().getPlayers().stream().filter(Objects::nonNull)
                                    .forEach(player -> SocketManager.GAME_SEND_MAP_PERCO_GMS_PACKETS(player.getGameClient(), player.getCurMap()));
                        }
                        /** Prism **/
                        if (fighter.getPrism() != null) {
                            World.world.getOnlinePlayers().stream().filter(Objects::nonNull).filter(player -> player.getAlignment() == getPrism().getAlignment())
                                    .forEach(player -> SocketManager.SEND_CS_SURVIVRE_MESSAGE_PRISME(player, str));

                            fighter.getPrism().setFight(null);

                            this.getMapOld().getPlayers().stream().filter(Objects::nonNull)
                                    .forEach(player -> SocketManager.SEND_GM_PRISME_TO_MAP(player.getGameClient(), player.getCurMap()));
                        }

                        if (fighter.isInvocation())
                            continue;
                        if (fighter.hasLeft())
                            continue;
                        this.onPlayerWin(fighter, looseTeam);
                    }
                    /** END WINNER **/

                    /** LOOSER **/
                    for (Fighter fighter : looseTeam) {
                        if (fighter.getCollector() != null) {
                            World.world.getGuild(getGuildId()).getPlayers().stream().filter(Objects::nonNull).filter(Player::isOnline).forEach(player -> {
                                SocketManager.GAME_SEND_gITM_PACKET(player, Collector.parseToGuild(player.getGuild().getId()));
                                SocketManager.GAME_SEND_PERCO_INFOS_PACKET(player, fighter.getCollector(), "D");
                            });

                            this.getMapOld().RemoveNpc(fighter.getCollector().getId());
                            SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getMapOld(), fighter.getCollector().getId());
                            fighter.getCollector().reloadTimer();
                            this.getCollector().delCollector(fighter.getCollector().getId());
                            ((CollectorData) DatabaseManager.get(CollectorData.class)).delete(fighter.getCollector());
                        }

                        if (fighter.getPrism() != null) {
                            SubArea subarea = this.getMapOld().getSubArea();

                            for (Player player : World.world.getOnlinePlayers()) {
                                if (player == null)
                                    continue;

                                if (player.getAlignment() == 0) {
                                    SocketManager.GAME_SEND_am_ALIGN_PACKET_TO_SUBAREA(player, subarea.getId() + "|-1|1");
                                    continue;
                                }

                                if (player.getAlignment() == getPrism().getAlignment())
                                    SocketManager.SEND_CD_MORT_MESSAGE_PRISME(player, str);

                                SocketManager.GAME_SEND_am_ALIGN_PACKET_TO_SUBAREA(player, subarea.getId() + "|-1|0");

                                if (getPrism().getConquestArea() != -1) {
                                    SocketManager.GAME_SEND_aM_ALIGN_PACKET_TO_AREA(player, subarea.getArea().getId() + "|-1");
                                    subarea.getArea().setPrismId(0);
                                    subarea.getArea().setAlignement(0);
                                }
                                SocketManager.GAME_SEND_am_ALIGN_PACKET_TO_SUBAREA(player, subarea.getId() + "|0|1");
                            }
                            final int id = fighter.getPrism().getId();
                            fighter.getPrism().setFight(null);
                            subarea.setPrism(null);
                            subarea.setAlignment((byte) 0);
                            subarea.setConquerable(false);
                            TimerWaiter.addNext(() -> subarea.setConquerable(true), 5 * 60_000);
                            this.getMapOld().RemoveNpc(id);
                            SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(getMapOld(), id);
                            World.world.removePrisme(id);
                            ((PrismData) DatabaseManager.get(PrismData.class)).delete(fighter.getPrism());
                        }

                        if (fighter.getMob() != null)
                            continue;
                        if (fighter.isInvocation())
                            continue;

                        this.onPlayerLoose(fighter);
                    }
                    /** END LOOSER **/
                }

                for (Player player : this.getViewer().values()) {
                    player.refreshMapAfterFight();
                    player.setSpec(false);
                    player.send(packet);

                    if (player.getAccount().isBanned())
                        player.getGameClient().kick();
                }

                for (Fighter fighter : fighters) {
                    Player player = fighter.getPlayer();
                    if (player != null) {
                        if (this.isBegin()) {
                            if (player.getCurMap().getId() == 8357 && player.hasItemTemplate(7373, 1, false) && player.hasItemTemplate(7374, 1, false) && player.hasItemTemplate(7375, 1, false) && player.hasItemTemplate(7376, 1, false) && player.hasItemTemplate(7377, 1, false) && player.hasItemTemplate(7378, 1, false)) {
                                player.removeByTemplateID(7373, 1);
                                player.removeByTemplateID(7374, 1);
                                player.removeByTemplateID(7375, 1);
                                player.removeByTemplateID(7376, 1);
                                player.removeByTemplateID(7377, 1);
                                player.removeByTemplateID(7378, 1);
                            }
                            player.send(packet);
                        }


                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                for (Fighter fighter : fighters) {
                    Player player = fighter.getPlayer();
                    if (player != null) {
                        player.setDuelId(-1);
                        player.setReady(false);
                        player.setFight(null);
                        SocketManager.GAME_SEND_GV_PACKET(player);
                    }
                }
            }

            for (Fighter fighter : fighters) {
                Player player = fighter.getPlayer();

                if (player == null)
                    continue;

                if (player.getFight() != null)
                    player.setFight(null);

                player.refreshLife(false);

                if (!player.getCurCell().isWalkable(true))
                    player.teleport(player.getCurMap().getId(), player.getCurMap().getRandomFreeCellId());
                if (player.getAccount().isBanned())
                    player.getGameClient().kick();
                if (fighter.isDeconnected())
                    player.getAccount().disconnect(player);

                if (player.getMorphMode() && !Constant.isInGladiatorDonjon(this.getMap().getId()))
                    SocketManager.GAME_SEND_SPELL_LIST(player);

                if(player.getParty() != null && player.getParty().getMaster() != null && player.getParty().getMaster().getId() == player.getId()) {
                    player.getParty().moveAllPlayersToMaster(player.getCurCell(), true);
                    TimerWaiter.addNext(() -> {
                        for (Player target : player.getParty().getPlayers()) {
                            if (target.getId() != player.getId() && target.getCurMap().getId() == player.getCurMap().getId() && target.getFight() == null) {
                                if(player.getAccount() != null && target.getAccount() != null &&
                                        target.getAccount().getCurrentIp().equalsIgnoreCase(player.getAccount().getCurrentIp()))
                                    target.send("GE");
                            }
                        }
                    }, 1000);
                }

            }
        }

    }

    void onPlayerWin(Fighter fighter, ArrayList<Fighter> looseTeam) {
        Player player = fighter.getPlayer();

        if (player == null)
            return;

        player.afterFight = true;

        GameObject weapon = player.getObjetByPos(Constant.ITEM_POS_ARME);
        if (weapon != null) {
            if (weapon.getTxtStat().containsKey(Constant.STATS_RESIST)) {
                int statNew = Integer.parseInt(weapon.getTxtStat().get(Constant.STATS_RESIST), 16) - 1;
                if (statNew <= 0) {
                    SocketManager.send(player, "Im160");
                    player.removeItem(weapon.getGuid(), 1, true, true);
                } else {
                    weapon.getTxtStat().remove(Constant.STATS_RESIST); // on retire les stats "32c"
                    weapon.addTxtStat(Constant.STATS_RESIST, Integer.toHexString(statNew));// on ajout les bonnes stats
                    SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(player, weapon);
                }
            }
        }

        if (this.getType() != Constant.FIGHT_TYPE_CHALLENGE) {
            if (fighter.getPdv() <= 0)
                player.setPdv(1);
            else
                player.setPdv(fighter.getPdv());

            if (fighter.getLevelUp()) player.fullPDV();
        }


        if (this.getType() == 2)
            if (this.getPrism() != null)
                SocketManager.SEND_CP_INFO_DEFENSEURS_PRISME(player, this.getDefenders());

        if (this.getType() == Constant.FIGHT_TYPE_PVT)
            if (player.getGuildMember() != null)
                if (this.getCollector().getGuildId() == player.getGuildMember().getGuild().getId())
                    player.teleportOldMap();

        if (this.getType() == Constant.FIGHT_TYPE_PVM) {
            GameObject obj = player.getObjetByPos(Constant.ITEM_POS_FAMILIER);
            if (obj != null) {
                Map<Integer, Integer> souls = new HashMap<>();

                for (Fighter f : looseTeam) {
                    if (f.getMob() == null)
                        continue;

                    int id = f.getMob().getTemplate().getId();

                    if (!souls.isEmpty() && souls.containsKey(id))
                        souls.put(id, souls.get(id) + 1);
                    else
                        souls.put(id, 1);
                }
                if (!souls.isEmpty()) {
                    PetEntry pet = World.world.getPetsEntry(obj.getGuid());
                    if (pet != null)
                        pet.eatSouls(player, souls);
                }
            }

        }
    }

    void onPlayerLoose(Fighter fighter) {
        final Player player = fighter.getPlayer();

        if (player == null)
            return;
        if (player.getMorphMode() && player.donjon)
            player.unsetFullMorph();

        GameObject arme = player.getObjetByPos(Constant.ITEM_POS_ARME);

        if (arme != null) {
            if (arme.getTxtStat().containsKey(Constant.STATS_RESIST)) {
                int statNew = Integer.parseInt(arme.getTxtStat().get(Constant.STATS_RESIST), 16) - 1;
                if (statNew <= 0) {
                    SocketManager.send(player, "Im160");
                    player.removeItem(arme.getGuid(), 1, true, true);
                } else {
                    arme.getTxtStat().remove(Constant.STATS_RESIST); // on retire les stats "32c"
                    arme.addTxtStat(Constant.STATS_RESIST, Integer.toHexString(statNew));// on ajout les bonnes stats
                    SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(player, arme);
                }
            }
        }

        if (player.getObjetByPos(Constant.ITEM_POS_FAMILIER) != null && this.getType() != Constant.FIGHT_TYPE_CHALLENGE) {
            GameObject obj = player.getObjetByPos(Constant.ITEM_POS_FAMILIER);
            if (obj != null) {
                PetEntry pets = World.world.getPetsEntry(obj.getGuid());
                if (pets != null)
                    pets.looseFight(player);
            }
        }

        if (player.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR) != null)
            player.setMascotte(0);

        if (this.getType() == 2)
            if (this.getPrism() != null)
                SocketManager.SEND_CP_INFO_DEFENSEURS_PRISME(player, this.getDefenders());

        if (this.getType() != Constant.FIGHT_TYPE_CHALLENGE) {
            int loose = Formulas.getLoosEnergy(player.getLevel(), getType() == 1, getType() == 5);
            int energy = player.getEnergy() - loose;

            player.setEnergy((energy < 0 ? 0 : energy));

            if (player.isOnline())
                SocketManager.GAME_SEND_Im_PACKET(player, "034;" + loose);
            if (Config.modeHeroic) {
                if (fighter.killedBy != null)
                    player.die(fighter.killedBy.first, fighter.killedBy.second);
            } else {
                if (energy <= 0) {
                    if (this.getType() == Constant.FIGHT_TYPE_AGRESSION && fighter.getTraqued()) {
                        if (getTeam1().containsValue(fighter))
                            player.teleportFaction(this.getAlignementOfTraquer(this.getTeam0().values(), player));
                        else
                            player.teleportFaction(this.getAlignementOfTraquer(this.getTeam1().values(), player));
                        player.setEnergy(1);
                    } else {
                        TimerWaiter.addNext(player::setFuneral, 1, TimeUnit.SECONDS);
                    }
                } else {
                    if (this.getType() == Constant.FIGHT_TYPE_AGRESSION && fighter.getTraqued()) {
                        if (getTeam1().containsValue(fighter))
                            player.teleportFaction(this.getAlignementOfTraquer(this.getTeam0().values(), player));
                        else
                            player.teleportFaction(this.getAlignementOfTraquer(this.getTeam1().values(), player));
                    } else {
                        if (player.getCurMap() != null && player.getCurMap().getSubArea() != null && (player.getCurMap().getSubArea().getId() == 319 || player.getCurMap().getSubArea().getId() == 210)) {
                            player.setNeededEndFightAction(new Action(1001, "9558,224", "", null));
                            player.teleportLaby((short) 9558, 224);
                            TimerWaiter.addNext(() -> {
                                Minotoror.sendPacketMap(player); // Retarde le paquet sinon les portes sont ferm�s. Le paquet de GameInformation doit faire chier ce p�d�
                                player.setPdv(1);
                            }, 3500);
                        } else {
                            player.setNeededEndFightAction(new Action(1001, player.getSavePosition(), "", null));
                            player.setPdv(1);
                        }
                    }
                }
            }
        }
    }

    int getAlignementOfTraquer(Collection<Fighter> fighters,
                               Player player) {
        for (Fighter fighter : fighters)
            if (fighter.getPlayer() != null)
                if (fighter.getPlayer().getStalk().getTarget() == player)
                    return (int) fighter.getPlayer().getAlignment();
        return 0;
    }

    public void onGK(Player player) {
        final Fighter current = this.getFighterByGameOrder();
        if (current == null)
            return;
        if (getCurAction().equals("") || current.getId() != player.getId() || getState() != Constant.FIGHT_STATE_ACTIVE)
            return;

        SocketManager.GAME_SEND_GAMEACTION_TO_FIGHT(this, 7, this.getCurAction());
        SocketManager.GAME_SEND_GAF_PACKET_TO_FIGHT(this, 7, 2, current.getId());

        if (!this.getCurAction().equals("casting")) {
            final Fighter fighter = getFighterByPerso(player);
            final int currentCell = fighter.getCell().getId();


            for (Trap trap : new ArrayList<>(this.getAllTraps())) {
                if (PathFinding.getDistanceBetween(this.getMap(), trap.getCell().getId(), currentCell) <= trap.getSize())
                    trap.onTraped(fighter);
                if (this.getState() == Constant.FIGHT_STATE_FINISHED)
                    break;
            }
        }

        this.setCurAction("");
    }

    public String getGE(int win) {
        long t1 = System.currentTimeMillis();
        final StringBuilder packet = new StringBuilder();
        int type = Constant.FIGHT_TYPE_CHALLENGE;

        //region boufbawl de merde
        if (this.getType() == 7) {
            try {
                long time = System.currentTimeMillis() - getStartTime();
                int initGUID = getInit0().getId();
                StringBuilder Packet = new StringBuilder();
                Packet.append("GE").append(time);
                Packet.append("|").append(initGUID).append("|").append(0).append("|");

                ArrayList<Fighter> TEAM1 = new ArrayList<>(), TEAM2 = new ArrayList<>();
                if (win == 1) {
                    TEAM1.addAll(getTeam0().values());
                    TEAM2.addAll(getTeam1().values());
                } else {
                    TEAM1.addAll(getTeam1().values());
                    TEAM2.addAll(getTeam0().values());
                }

                for (Fighter i : TEAM1)//Les Gagnant
                {
                    if (i.isInvocation() && i.getMob() != null)
                        continue;//Pas d'invoc dans les gains
                    if (i.getMob() != null)
                        continue;
                    if (i.getDouble() != null)
                        continue;//Pas de double dans les gains
                    if (i.getPdv() == 0 || i.hasLeft())
                        Packet.append("0;").append(i.getId()).append(";").append(i.getPacketsName()).append(";").append(i.getLvl()).append(";1").append(";").append("0;0;0").append(";;;;|");
                    else
                        Packet.append("0;").append(i.getId()).append(";").append(i.getPacketsName()).append(";").append(i.getLvl()).append(";0").append(";").append("0;0;0").append(";;;;|");
                }
                // Fin gagnant
                for (Fighter i : TEAM2)//Les perdants
                {
                    if (i.isInvocation() && i.getMob() != null)
                        continue;//Pas d'invoc dans les gains
                    if (i.getMob() != null)
                        continue;
                    if (i.getDouble() != null)
                        continue;//Pas de double dans les gains
                    if (i.getPdv() == 0 || i.hasLeft())
                        Packet.append("2;").append(i.getId()).append(";").append(i.getPacketsName()).append(";").append(i.getLvl()).append(";1").append(";").append("0;0;0").append(";;;;|");
                    else
                        Packet.append("2;").append(i.getId()).append(";").append(i.getPacketsName()).append(";").append(i.getLvl()).append(";0").append(";").append("0;0;0").append(";;;;|");
                }

                return Packet.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        //endregion

        if (this.getType() == Constant.FIGHT_TYPE_AGRESSION || getType() == Constant.FIGHT_TYPE_CONQUETE)
            type = 1;
        if (this.getType() == Constant.FIGHT_TYPE_PVT)
            type = Constant.FIGHT_TYPE_CHALLENGE;
        if (this.getType() == 7) type = 0;

        packet.append("GE").append(System.currentTimeMillis() - getStartTime());
        if (getType() == Constant.FIGHT_TYPE_PVM && getMobGroup() != null)
            packet.append(';').append(getMobGroup().getStarBonus());
        packet.append("|").append(this.getInit0().getId()).append("|").append(type).append("|");

        //region Purge team
        ArrayList<Fighter> winners = new ArrayList<>(), loosers = new ArrayList<>();

        Iterator iterator = this.getTeam0().entrySet().iterator();
        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Fighter fighter = (Fighter) entry.getValue();

            if (fighter.isInvocation() && fighter.getMob() != null && fighter.getMob().getTemplate().getId() != 285)
                iterator.remove();
            if (fighter.isDouble()) iterator.remove();
        }

        iterator = this.getTeam1().entrySet().iterator();
        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Fighter fighter = (Fighter) entry.getValue();

            if (fighter.isInvocation() && fighter.getMob() != null && fighter.getMob().getTemplate().getId() != 285)
                iterator.remove();
            if (fighter.isDouble()) iterator.remove();
        }

        if (win == 1) {
            winners.addAll(this.getTeam0().values());
            loosers.addAll(this.getTeam1().values());
        } else {
            winners.addAll(this.getTeam1().values());
            loosers.addAll(this.getTeam0().values());
        }
        //endregion
        //region Gladitroll
        if (ingladiatroll) {
            try {
                long time = System.currentTimeMillis() - getStartTime();
                int initGUID = getInit0().getId();
                StringBuilder Packet = new StringBuilder();
                Packet.append("GE").append(time);
                Packet.append("|").append(initGUID).append("|").append(0).append("|");

                for (Fighter i : winners)//Les Gagnant
                {
                    Player player = i.getPlayer();
                    String gfx = "";
                    if(player != null){
                        gfx=player.getGfxId()+"";
                    }

                    if (i.isInvocation() && i.getMob() != null)
                        continue;//Pas d'invoc dans les gains
                    if (i.getMob() != null)
                        continue;
                    if (i.getDouble() != null)
                        continue;//Pas de double dans les gains
                    if (i.getPdv() == 0 || i.hasLeft() || i.isDead()) {
                        Packet.append("2;")
                                .append(i.getId()).append(";")
                                .append(i.getPacketsName()).append(";")
                                .append(i.getLvl()).append(";")
                                .append(gfx).append(";")
                                .append("1;;;;0;;;;0").append(";|");
                    }
                    else {
                        Packet.append("2;")
                                .append(i.getId()).append(";")
                                .append(i.getPacketsName()).append(";")
                                .append(i.getLvl()).append(";")
                                .append(gfx).append(";")
                                .append("0;;;;0;;;;0").append(";|");
                    }
                }
                // Fin gagnant
                for (Fighter i : loosers)//Les perdants
                {
                    if (i.isInvocation() && i.getMob() != null)
                        continue;//Pas d'invoc dans les gains
                    if (i.getDouble() != null)
                        continue;//Pas de double dans les gains

                    if (i.isDead() || i.hasLeft() || i.isDead() ) {
                        Packet.append("0;")
                                .append(i.getId()).append(";")
                                .append(i.getPacketsName()).append(";")
                                .append(i.getLvl()).append(";").append(";")
                                .append("1").append(";;;;;;;;;|");
                    }
                    else{
                        Packet.append("0;")
                                .append(i.getId()).append(";")
                                .append(i.getPacketsName()).append(";")
                                .append(i.getLvl()).append(";").append(";")
                                .append("0").append(";;;;;;;;;|");
                    }

                }
                return Packet.toString();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //endregion

        try {
            /* Var heroic mod **/
            boolean team = false;

            long totalXP = 0;
            for (Fighter F : loosers) {
                if (F.getMob() != null)
                    totalXP += F.getMob().getBaseXp();
                if (F.getPlayer() != null)
                    team = true;
            }

            //region Capture d'�mes
            boolean mobCapturable = true;
            for (Fighter fighter : loosers) {
                if (fighter.getMob() == null || fighter.getMob().getTemplate() == null || !fighter.getMob().getTemplate().isCapturable()) {
                    mobCapturable = false;
                }
                if (fighter.getMob() != null && fighter.getMob().getTemplate() != null) {
                    for (int[] protector : JobConstant.JOB_PROTECTORS) {
                        if (protector[0] == fighter.getMob().getTemplate().getId()) {
                            mobCapturable = false;
                        }
                    }
                }
            }

            if (mobCapturable && !SoulStone.isInArenaMap(this.getMapOld().getId())) {
                boolean isFirst = true;
                int maxLvl = 0;
                String stats = "";

                for (Fighter fighter : loosers) {
                    if (fighter.isInvocation() || fighter.getInvocator() != null)
                        continue;
                    stats += (isFirst ? "" : "|") + fighter.getMob().getTemplate().getId() + "," + fighter.getLvl();
                    isFirst = false;
                    if (fighter.getLvl() > maxLvl)
                        maxLvl = fighter.getLvl();
                }

                this.setFullSoul(new SoulStone(1, 7010, Constant.ITEM_POS_NO_EQUIPED, stats)); // Cr�e la pierre d'�me
                winners.stream().filter(F -> !F.isInvocation() && F.haveState(Constant.ETAT_CAPT_AME)).forEach(F -> getCapturer().add(F));

                if (this.getCapturer().size() > 0 && !SoulStone.isInArenaMap(this.getMapOld().getId())) // S'il y a des captureurs
                {
                    for (int i = 0; i < this.getCapturer().size(); i++) {
                        try {
                            Fighter f = this.getCapturer().get(Formulas.getRandomValue(0, this.getCapturer().size() - 1)); // R�cup�re un captureur au hasard dans la liste
                            if (f != null && f.getPlayer() != null) {
                                if (f.getPlayer().getObjetByPos(Constant.ITEM_POS_ARME) == null || !(f.getPlayer().getObjetByPos(Constant.ITEM_POS_ARME).getTemplate().getType() == Constant.ITEM_TYPE_PIERRE_AME)) {
                                    this.getCapturer().remove(f);
                                    continue;
                                }
                                Couple<Integer, Integer> playerSoulStone = Formulas.decompPierreAme(f.getPlayer().getObjetByPos(Constant.ITEM_POS_ARME));// R�cup�re les stats de la pierre �quipp�

                                if (playerSoulStone.second < maxLvl) {// Si la pierre est trop faible
                                    this.getCapturer().remove(f);
                                    continue;
                                }
                                if (Formulas.getRandomValue(1, 100) <= Formulas.totalCaptChance(playerSoulStone.first, f.getPlayer())) {// Si le joueur obtiens la capture Retire la pierre vide au personnage et lui envoie ce changement
                                    int emptySoulStone = f.getPlayer().getObjetByPos(Constant.ITEM_POS_ARME).getGuid();
                                    f.getPlayer().deleteItem(emptySoulStone);
                                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(f.getPlayer(), emptySoulStone);
                                    this.setCaptWinner(f.getId());
                                    break;
                                }
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //endregion

            //region Quest
            if (this.getType() == Constant.FIGHT_TYPE_PVM || this.getType() == Constant.FIGHT_TYPE_DOPEUL) {
                for (Fighter fighter : winners) {
                    Player player = fighter.getPlayer();
                    if (player == null) continue;

                    if (!player.getQuestPerso().isEmpty()) {
                        for (Fighter ennemy : loosers) {
                            if (ennemy.getMob() == null) continue;
                            if (ennemy.getMob().getTemplate() == null) continue;

                            for (QuestPlayer questP : player.getQuestPerso().values()) {
                                if (questP == null) continue;
                                Quest quest = questP.getQuest();
                                if (quest == null) continue;
                                quest.getQuestObjectives().stream().filter(qEtape -> !questP.isQuestObjectiveIsValidate(qEtape) && (qEtape.getType() == 0 || qEtape.getType() == 6)).filter(qEtape -> qEtape.getMonsterId() == ennemy.getMob().getTemplate().getId()).forEach(qEtape -> {
                                    try {
                                        player.getQuestPersoByQuest(qEtape.getQuestData()).getMonsterKill().put(ennemy.getMob().getTemplate().getId(), (short) 1);
                                        qEtape.getQuestData().updateQuestData(player, false, 2);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        player.sendMessage(player.getLang().trans("fight.fight.quest.report.admin") + e.getMessage());
                                    }
                                });
                            }
                        }
                    }
                }
            }
            //endregion

            //region Apprivoisement
            boolean amande = false, rousse = false, doree = false;

            for (Fighter fighter : loosers) {
                try {
                    if (fighter.getMob().getTemplate().getId() == 171)
                        amande = true;
                    if (fighter.getMob().getTemplate().getId() == 200)
                        rousse = true;
                    if (fighter.getMob().getTemplate().getId() == 666)
                        doree = true;
                } catch (Exception e) {
                    amande = false;
                    rousse = false;
                    doree = false;
                    break;
                }
            }
            if (amande || rousse || doree) {
                winners.stream().filter(fighter -> !fighter.isInvocation() && fighter.haveState(Constant.ETAT_APPRIVOISEMENT)).forEach(F -> getTrainer().add(F));
                if (getTrainer().size() > 0) {
                    for (int i = 0; i < getTrainer().size(); i++) {
                        try {
                            Fighter f = getTrainer().get(Formulas.getRandomValue(0, getTrainer().size() - 1)); // R�cup�re un captureur au hasard dans la liste
                            Player player = f.getPlayer();
                            if (player.getObjetByPos(Constant.ITEM_POS_ARME) == null || !(player.getObjetByPos(Constant.ITEM_POS_ARME).getTemplate().getType() == Constant.ITEM_TYPE_FILET_CAPTURE)) {
                                getTrainer().remove(f);
                                continue;
                            }
                            int chance = Formulas.getRandomValue(1, 100), appriChance = Formulas.totalAppriChance(amande, rousse, doree, player);
                            if (chance <= appriChance) {
                                // Retire le filet au personnage et lui envoie ce changement
                                int filet = player.getObjetByPos(Constant.ITEM_POS_ARME).getGuid();
                                player.deleteItem(filet);
                                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, filet);
                                setTrainerWinner(f.getId());
                                break;
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //endregion

            int memberGuild = 0;

            if (this.getType() == Constant.FIGHT_TYPE_PVT && win == 1)
                for (Fighter i : winners)
                    if (i.getPlayer() != null)
                        if (i.getPlayer().getGuildMember() != null)
                            memberGuild++;

            int lvlLoosers = 0, lvlWinners = 0, lvlMaxLooser = 0, lvlMax, lvlMin, challXp = 0;
            byte nbbonus = 0;
            for (Challenge c : getAllChallenges().values())
                if (c != null && c.getWin())
                    challXp += c.getXp();

            for (Fighter entry : loosers)
                lvlLoosers += entry.getLvl();

            for (Fighter entry : winners) {
                lvlWinners += entry.getLvl();
                if (entry.getLvl() > lvlMaxLooser
                        && entry.getPlayer() != null)
                    lvlMaxLooser = entry.getLvl();
            }
            if (lvlLoosers > lvlWinners) {
                lvlMax = lvlLoosers;
                lvlMin = lvlWinners;
            } else {
                lvlMax = lvlWinners;
                lvlMin = lvlLoosers;
            }
            for (Fighter entry : winners)
                if (entry.getLvl() > (lvlMaxLooser / 3)
                        && entry.getPlayer() != null)
                    nbbonus += 1;

            if (lvlWinners <= 0)
                lvlWinners = 1;

            Map<Integer, Integer> mobs = new HashMap<>();
            loosers.stream().filter(mob -> mob.getMob() != null).forEach(mob -> {
                if (mobs.get(mob.getMob().getTemplate().getId()) != null)
                    mobs.put(mob.getMob().getTemplate().getId(), mobs.get(mob.getMob().getTemplate().getId()) + 1); // Quantite
                else
                    mobs.put(mob.getMob().getTemplate().getId(), 1);
            });

            Collections.sort(winners);
            Map<Integer, StringBuilder> gains = new HashMap<>();

            //region Drop
            // Calcul the total prospecting.
            int totalProspecting = 0;
            double challengeFactor = 0, starFactor = this.getMobGroup() != null ? (this.getMobGroup().getStarBonus() / 100) + 1 : 1;

            for (Fighter fighter : winners)
                if (fighter != null && !fighter.isDouble())
                    if (!fighter.isInvocation() || (fighter.getMob() != null && fighter.getMob().getTemplate() != null && fighter.getMob().getTemplate().getId() == 285))
                        totalProspecting += fighter.getPros();

            if (starFactor < 1) starFactor = 1;
            if (totalProspecting < 0) totalProspecting = 0;
            // Calcul the total challenge percent.
            if (this.getType() == Constant.FIGHT_TYPE_PVM && this.getAllChallenges().size() > 0)
                for (Challenge challenge : this.getAllChallenges().values())
                    if (challenge.getWin()) challengeFactor += challenge.getDrop();
            if (challengeFactor < 1) challengeFactor = 1;
            challengeFactor = 1 + (challengeFactor / 100);

            ArrayList<Drop> dropsPlayers = new ArrayList<>(), dropsMeats = new ArrayList<>();
            Collection<GameObject> dropsCollector = null;
            Couple<Integer, Integer> kamas;

            if (this.getType() == Constant.FIGHT_TYPE_PVT && win == 1) {
                int kamasCollector = (int) Math.ceil(collector.getKamas() / winners.size());
                kamas = new Couple<>(kamasCollector, kamasCollector);
                dropsCollector = this.getCollector().getDrops();
            } else {
                int minKamas = 0, maxKamas = 0;
                for (Fighter fighter : loosers) {
                    if (!fighter.isInvocation() && fighter.getMob() != null && !fighter.isDouble()) {
                        minKamas += fighter.getMob().getTemplate().getMinKamas();
                        maxKamas += fighter.getMob().getTemplate().getMaxKamas();

                        for (Drop drop1 : fighter.getMob().getTemplate().getDrops()) {
                            switch (drop1.getAction()) {
                                case 1:
                                    Drop drop = drop1.copy(fighter.getMob().getGrade());
                                    if (drop == null) break;
                                    dropsMeats.add(drop);
                                    break;
                                default:
                                    if (drop1.getCeil() <= totalProspecting && fighter.getMob() != null) {
                                        drop = drop1.copy(fighter.getMob().getGrade());
                                        if (drop == null) break;
                                        dropsPlayers.add(drop);
                                    }
                                    break;
                            }
                        }
                    }
                }

                kamas = new Couple<>(minKamas, maxKamas);
            }
            // Sort fighter by prospecting.
            ArrayList<Fighter> temporary1 = new ArrayList<>();
            Fighter higherFighter = null;
            while (temporary1.size() < winners.size()) {
                int currentProspecting = -1;
                for (Fighter fighter : winners) {
                    if (fighter.getTotalStats().getEffect(Constant.STATS_ADD_PROS) > currentProspecting && !temporary1.contains(fighter)) {
                        higherFighter = fighter;
                        currentProspecting = fighter.getTotalStats().getEffect(Constant.STATS_ADD_PROS);
                    }
                }
                temporary1.add(higherFighter);
            }
            winners.clear();
            winners.addAll(temporary1);
            final NumberFormat formatter = new DecimalFormat("#0.000");
            //endregion

            //region Stalk
            Player curPlayer = null;
            boolean stalk = false;
            int quantity = 2;
            if (this.getType() == Constant.FIGHT_TYPE_AGRESSION) {
                boolean isAlone = true;

                for (Fighter fighter : winners)
                    if (!fighter.isInvocation())
                        curPlayer = fighter.getPlayer();

                for (Fighter fighter : winners)
                    if (fighter.getPlayer() != curPlayer && !fighter.isInvocation())
                        isAlone = false;

                if (isAlone) {
                    for (Fighter fighter : loosers) {
                        if (!fighter.isInvocation() && curPlayer != null && curPlayer.getStalk() != null && curPlayer.getStalk().getTarget() == fighter.getPlayer()) {
                            SocketManager.GAME_SEND_MESSAGE(curPlayer, "Thomas Sacre : Contrat fini, reviens me voir pour récuperer ta récompense.", "000000");
                            curPlayer.getStalk().setTime(-2);
                            stalk = true;
                            fighter.setTraqued(true);

                            Stalk stalkTarget = fighter.getPlayer().getStalk();

                            if (stalkTarget != null)
                                if (stalkTarget.getTarget() == curPlayer)
                                    quantity = 4;

                            GameObject object = World.world.getObjTemplate(10275).createNewItem(quantity, false);
                            if (curPlayer.addObjet(object, true))
                                World.world.addGameObject(object);
                            kamas = new Couple<>(1000 * quantity, 1000 * quantity);
                            curPlayer.addKamas(1000 * quantity);
                        }
                    }
                }

                if (!stalk) {
                    Player traqued = null;
                    curPlayer = null;

                    for (Fighter fighter : loosers)
                        if (fighter.getPlayer() != null)
                            if (fighter.getPlayer().getStalk() != null)
                                traqued = fighter.getPlayer().getStalk().getTarget();

                    if (traqued != null)
                        for (Fighter fighter : winners)
                            if (fighter.getPlayer() == traqued)
                                curPlayer = traqued;

                    if (curPlayer != null) {
                        kamas = new Couple<>(1000 * quantity, 1000 * quantity);
                        curPlayer.addKamas(1000 * quantity);
                        GameObject object = World.world.getObjTemplate(10275).createNewItem(quantity, false);
                        if (curPlayer.addObjet(object, true))
                            World.world.addGameObject(object);
                        stalk = true;
                    }
                }
            }
            //endregion Stalk

            //region Heroic
            Map<Player, String> list = null;
            ArrayList<GameObject> objects = null;

            if (Config.modeHeroic) {
                switch (this.getType()) {
                    case Constant.FIGHT_TYPE_AGRESSION:
                        final ArrayList<GameObject> objects1 = new ArrayList<>();
                        int money = 0;

                        for (final Fighter fighter : loosers) {
                            final Player player = fighter.getPlayer();
                            if (player != null) {
                                objects1.addAll(player.getItems().values());
                                money += player.getKamas();
                                totalXP += (player.getExp() * 10) / 100;

                                Iterator<GameObject> iterator1 = new ArrayList<>(player.getItems().values()).iterator();
                                while(iterator1.hasNext()) {
                                    GameObject toRemove = iterator1.next();
                                    if(toRemove != null) {
                                        player.removeItem(toRemove.getGuid(), toRemove.getQuantity(), true, false);
                                    }
                                    iterator1.remove();
                                }
                                player.setKamas(0);
                            }
                        }

                        kamas = new Couple<>(money, money);
                        list = Fight.give(objects1, winners);

                        break;
                    case Constant.FIGHT_TYPE_PVM:
                        try {
                            final Monster.MobGroup group = this.getMobGroup();

                            if (team) { // Players have loose the fight, mob win the fight
                                objects = new ArrayList<>();
                                for (final Fighter fighter : loosers) {
                                    final Player player = fighter.getPlayer();
                                    if (player != null)
                                        objects.addAll(player.getItems().values());
                                }

                                if (group.isFix()) {
                                    String infos = this.getMapOld().getId() + "," + group.getCellId();
                                    if (GameMap.fixMobGroupObjects.get(infos) != null) {
                                        objects.addAll(GameMap.fixMobGroupObjects.get(infos));
                                        GameMap.fixMobGroupObjects.remove(infos);
                                        GameMap.fixMobGroupObjects.put(infos, objects);
                                    } else {
                                        GameMap.fixMobGroupObjects.put(infos, objects);
                                        //((HeroicMobsGroupsData) DatabaseManager.get(HeroicMobsGroupsData.class)).insertFix(this.getMapOld().getId(), group, objects);
                                    }
                                } else {
                                    group.getObjects().addAll(objects);
                                    this.getMapOld().respawnGroup(group);
                                    //((HeroicMobsGroupsData) DatabaseManager.get(HeroicMobsGroupsData.class)).insert(this.getMapOld().getId(), group);
                                }
                            } else { // mob loose..
                                list = Fight.give(group.isFix() ? GameMap.fixMobGroupObjects.get(this.getMapOld().getId() + "," + group.getCellId()) : group.getObjects(), winners);
                                if (!group.isFix()) this.getMapOld().spawnAfterTimeGroup();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
            //endregion

            long t = System.currentTimeMillis();
            //region Winners
            for (Fighter i : winners) {
                if (i.isInvocation() && i.getMob() != null && i.getMob().getTemplate().getId() != 285)
                    continue;
                if (i.isDouble())
                    continue;

                final Player player = i.getPlayer();

                if (player != null && getType() != Constant.FIGHT_TYPE_CHALLENGE)
                    player.calculTurnCandy();
                if (getType() == Constant.FIGHT_TYPE_PVT || getType() == Constant.FIGHT_TYPE_PVM || getType() == Constant.FIGHT_TYPE_CHALLENGE || getType() == Constant.FIGHT_TYPE_DOPEUL) {
                    StringBuilder drops = new StringBuilder();
                    long xpPlayer = 0, xpGuild = 0, xpMount = 0;
                    int winKamas;

                    AtomicReference<Long> XP = new AtomicReference<>();
                    /** Xp,kamas **/
                    if (player != null) {
                        xpPlayer = FormuleOfficiel.getXp(i, winners, totalXP, nbbonus, (getMobGroup() != null ? getMobGroup().getStarBonus() : 0), challXp, lvlMax, lvlMin, lvlLoosers, lvlWinners);
                        XP.set(xpPlayer);

                        if (this.getType() == Constant.FIGHT_TYPE_PVT && win == 1) {
                            if (player != null && memberGuild != 0)
                                if (player.getGuildMember() != null)
                                    xpGuild = (int) Math.floor(this.getCollector().getXp() / memberGuild);
                        } else {
                            xpGuild = Formulas.getGuildXpWin(i, XP);
                        }

                        if (player.isOnMount()) {
                            xpMount = Formulas.getMountXpWin(i, XP);
                            player.getMount().addXp(xpMount);
                            SocketManager.GAME_SEND_Re_PACKET(player, "+", player.getMount());
                        }
                    }

                    winKamas = (int) ((this.getType() == Constant.FIGHT_TYPE_PVT && win == 1) ?
                            Math.floor(kamas.first / winners.size()) : Formulas.getKamasWin(i, winners, kamas.first, kamas.second));
                    /** Xp,kamas **/
                    /**********************/
                    /**       Drop       **/
                    /**********************/
                    Map<Integer, Integer> objectsWon = new HashMap<>(), itemWon2 = new HashMap<>();
                    if (this.getType() == Constant.FIGHT_TYPE_PVT && win == 1 && dropsCollector != null) {
                        int objectPerPlayer = (int) Math.floor(dropsCollector.size() / winners.size()), counter = 0;
                        ArrayList<GameObject> temporary2 = new ArrayList<>(dropsCollector);
                        Collections.shuffle(temporary2);

                        for (GameObject object : temporary2) {
                            if (counter <= objectPerPlayer) {
                                objectsWon.put(object.getTemplate().getId(), object.getQuantity());
                                dropsCollector.remove(object);
                                World.world.removeGameObject(object.getGuid());
                                counter++;
                            }
                        }
                    } else {
                        ArrayList<Drop> temporary3 = new ArrayList<>(dropsPlayers);
                        if(this.getType() == Constant.FIGHT_TYPE_PVM && this.mobGroup != null && this.mobGroup.getMobs().size() > 1 && Formulas.getRandomValue(0, 100) >= 98) {
                            List<ObjectTemplate> templates = World.world.getEtherealWeapons(i.isInvocation() ? i.getInvocator().getLvl() : i.getLvl());
                            if(templates.size() > 0) {
                                ObjectTemplate template = templates.get(templates.size() - 1);
                                temporary3.add(new Drop(template.getId(), 5, 0));
                            }
                        }
                        Collections.shuffle(temporary3);

                        for (Drop drop : temporary3) {
                            double prospecting = i.getPros() / 100.0;
                            if (prospecting < 1) prospecting = 1;


                            final double jet = Double.parseDouble(formatter.format(Math.random() * 100).replace(',', '.')),
                                    chance = Double.parseDouble(formatter.format(drop.getLocalPercent() * prospecting * World.world.getConquestBonus(player) * challengeFactor * starFactor * Config.rateDrop).replace(',', '.'));
                            boolean ok = false;

                            switch (drop.getAction()) {
                                case 4:
                                    if (player != null && World.world.getConditionManager().validConditions(player, "QE=" + drop.getCondition()))
                                        ok = true;
                                    break;
                            }
                            if (jet < chance || ok) {
                                ObjectTemplate objectTemplate = World.world.getObjTemplate(drop.getObjectId());

                                if (objectTemplate == null)
                                    continue;

                                quantity = 1;
                                boolean itsOk = false, unique = false;
                                switch (drop.getAction()) {
                                    case -2:
                                        unique = true;
                                        itsOk = true;
                                        break;
                                    case -1:// All items without condition.
                                        itsOk = true;
                                        break;

                                    case 1:// Is meat so..
                                        break;

                                    case 2:// Verification of the condition (MAP)
                                        for (String id : drop.getCondition().split(","))
                                            if (id.equals(String.valueOf(getMap().getId())))
                                                itsOk = true;
                                        break;

                                    case 3:// Alignement
                                        if (this.getMapOld().getSubArea() == null)
                                            break;
                                        switch (drop.getCondition()) {
                                            case "0":
                                                if (this.getMapOld().getSubArea().getAlignment() == 2)
                                                    itsOk = true;
                                                break;
                                            case "1":
                                                if (this.getMapOld().getSubArea().getAlignment() == 1)
                                                    itsOk = true;
                                                break;
                                            case "2":
                                                if (this.getMapOld().getSubArea().getAlignment() == 2)
                                                    itsOk = true;
                                                break;
                                            case "3":
                                                if (this.getMapOld().getSubArea().getAlignment() == 3)
                                                    itsOk = true;
                                                break;
                                            default:
                                                itsOk = true;
                                                break;
                                        }
                                        break;

                                    case 4: // Quete
                                        if (World.world.getConditionManager().validConditions(player, "QE=" + drop.getCondition()))
                                            itsOk = true;
                                        break;

                                    case 5: // Dropable une seule fois
                                        if (player == null) break;
                                        if (player.getNbItemTemplate(objectTemplate.getId()) > 0) break;
                                        itsOk = true;
                                        break;

                                    case 6: // Avoir l'objet
                                        if (player == null) break;
                                        int item = Integer.parseInt(drop.getCondition());
                                        if (item == 2039) {
                                            if (this.getMap().getId() == (short) 7388) {
                                                if (player.hasItemTemplate(item, 1, false))
                                                    itsOk = true;
                                            } else
                                                itsOk = false;
                                        } else if (player.hasItemTemplate(item, 1, false))
                                            itsOk = true;
                                        break;

                                    case 7:// Verification of the condition (MAP) mais pas plusieurs fois
                                        if (player == null) break;
                                        if (player.hasItemTemplate(objectTemplate.getId(), 1, false))
                                            break;
                                        for (String id : drop.getCondition().split(",")) {
                                            if (id.equals(String.valueOf(this.getMap().getId()))) {
                                                itsOk = true;
                                            }
                                        }
                                        break;

                                    case 8:// Win a specific quantity
                                        String[] split = drop.getCondition().split(",");
                                        quantity = Formulas.getRandomValue(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                                        itsOk = true;
                                        break;

                                    case 9:// Relique minotoror
                                        if (player != null && Minotoror.isValidMap(player.getCurMap()))
                                            itsOk = true;
                                        break;

                                    case 999:// Drop for collector
                                        itsOk = true;
                                        break;

                                    default:
                                        itsOk = true;
                                        break;
                                }
                                if (itsOk) {
                                    objectsWon.put(objectTemplate.getId(), (objectsWon.get(objectTemplate.getId()) == null ? quantity : (objectsWon.get(objectTemplate.getId())) + quantity));
                                    if (unique) dropsPlayers.remove(drop);
                                }
                            }
                        }
                        /** Drop Chasseur **/
                        if (player != null) {
                            ArrayList<Drop> temporary = new ArrayList<>(dropsMeats);
                            Collections.shuffle(temporary);

                            GameObject weapon = player.getObjetByPos(Constant.ITEM_POS_ARME);
                            boolean ok = weapon != null && weapon.getStats().getEffect(795) == 1;

                            if (ok) {
                                for (Drop drop : temporary) {
                                    final double jet = Double.parseDouble(formatter.format(Math.random() * 100).replace(',', '.')),
                                            chance = Double.parseDouble(formatter.format(drop.getLocalPercent() * (i.getPros() / 100.0)).replace(',', '.'));

                                    if (jet < chance) {
                                        ObjectTemplate objectTemplate = World.world.getObjTemplate(drop.getObjectId());

                                        if (drop.getAction() == 1 && objectTemplate != null && player.getMetierByID(41) != null && player.getMetierByID(41).get_lvl() >= drop.getLevel())
                                            itemWon2.put(objectTemplate.getId(), (itemWon2.get(objectTemplate.getId()) == null ? 0 : itemWon2.get(objectTemplate.getId())) + 1);
                                    }
                                }
                            }
                        }
                    }
                    if (player != null || (i.getMob() != null && i.getMob().getTemplate().getId() == 285)) {
                        if (player != null) {
                            if (this.getTrainerWinner() != -1 && i.getId() == this.getTrainerWinner() && player.getMount() == null) {
                                int color = Formulas.getCouleur(amande, rousse, doree);

                                Mount mount = new Mount(color, i.getId(), true);
                                player.setMount(mount);
                                SocketManager.GAME_SEND_Re_PACKET(player, "+", mount);
                                SocketManager.GAME_SEND_Rx_PACKET(player);
                                SocketManager.GAME_SEND_STATS_PACKET(player);
                                if (drops.length() > 0) drops.append(",");
                                switch (color) {
                                    case 20:
                                        drops.append("7807~1");
                                        break;
                                    case 10:
                                        drops.append("7809~1");
                                        break;
                                    case 18:
                                        drops.append("7864~1");
                                        break;
                                }
                            }
                            if (i.getId() == this.getCaptWinner() && this.getFullSoul() != null) {
                                if (drops.length() > 0)
                                    drops.append(",");
                                drops.append(this.getFullSoul().getTemplate().getId()).append("~").append(1);
                                if (player.addObjet(this.getFullSoul(), false))
                                    World.world.addGameObject(this.getFullSoul());
                            }
                            if (list != null) {
                                String value = list.get(i.getPlayer());
                                if (value != null && !value.isEmpty())
                                    drops.append((drops.length() == 0) ? "" : ",").append(value);
                            }
                        }

                        Player target = player != null ? player : i.getInvocator().getPlayer();

                        final Map<ObjectTemplate, Integer> dropsToAttribute = new HashMap<>();
                        for (Entry<Integer, Integer> entry : objectsWon.entrySet()) {
                            ObjectTemplate objectTemplate = World.world.getObjTemplate(entry.getKey());

                            if (player == null && i.getInvocator() == null) break;
                            if (objectTemplate == null || i.isDouble()) continue;
                            if (drops.length() > 0) drops.append(",");

                            drops.append(entry.getKey()).append("~").append(entry.getValue());
                            dropsToAttribute.put(objectTemplate, entry.getValue());
                        }
                        for (Entry<Integer, Integer> entry : itemWon2.entrySet()) {
                            ObjectTemplate objectTemplate = World.world.getObjTemplate(entry.getKey());

                            if (player == null && i.getInvocator().getPlayer() == null) break;
                            if (objectTemplate == null) continue;
                            if (drops.length() > 0) drops.append(",");

                            drops.append(entry.getKey()).append("~").append(entry.getValue());
                            dropsToAttribute.put(objectTemplate, entry.getValue());
                        }

                        TimerWaiter.addNext(() -> {
                            for(Entry<ObjectTemplate, Integer> entry : dropsToAttribute.entrySet()) {
                                ObjectTemplate template = entry.getKey();
                                if (template.getType() == 32 && player != null) {
                                    player.setMascotte(template.getId());
                                } else if (template.getType() == Constant.ITEM_TYPE_FAMILIER && Config.maxPets) {
                                    GameObject obj = template.createNewItem(1, false);
                                    if (target.addObjet(obj, true))//Si le joueur n'avait pas d'item similaire
                                        World.world.addGameObject(obj);
                                    SocketManager.GAME_SEND_Ow_PACKET(target);
                                } else {
                                    GameObject newObj = World.world.getObjTemplate(template.getId()).createNewItemWithoutDuplication(target.getItems().values(), entry.getValue(), false);
                                    if (World.world.getObjTemplate(template.getId()).getType() == Constant.ITEM_TYPE_CERTIF_MONTURE) {
                                        //obj.setMountStats(this.getPlayer(), null);
                                        Mount mount = new Mount(Constant.getMountColorByParchoTemplate(newObj.getTemplate().getId()), target.getId(), false);
                                        newObj.clearStats();
                                        newObj.getStats().addOneStat(995, (mount.getId()));
                                        newObj.getTxtStat().put(996, target.getName());
                                        newObj.getTxtStat().put(997, mount.getName());
                                        mount.setToMax();
                                    }

                                    if (newObj != null && target.getItems().get(newObj.getGuid()) == null) {
                                        if (target.addObjet(newObj, true))
                                            World.world.addGameObject(newObj);
                                    } else {
                                        SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(target, newObj);
                                    }
                                }
                            }
                        }, 1000);
                        if (this.getType() == Constant.FIGHT_TYPE_DOPEUL) {
                            for (Fighter F : loosers) {
                                Monster.MobGrade mob = F.getMob();
                                Monster m = mob.getTemplate();
                                if (m == null)
                                    continue;
                                int IDmob = m.getId();
                                if (drops.length() > 0) drops.append(",");
                                int id = Constant.getCertificatByDopeuls(IDmob);
                                if(id == -1) continue;
                                drops.append(id).append("~1");
                                // Certificat :
                                ObjectTemplate OT2 = World.world.getObjTemplate(Constant.getCertificatByDopeuls(IDmob));
                                if (OT2 != null) {
                                    GameObject obj2 = OT2.createNewItem(1, false);
                                    if (player.addObjet(obj2, true))// Si le joueur n'avait pas d'item similaire
                                        World.world.addGameObject(obj2);
                                    obj2.refreshStatsObjet("325#0#0#" + System.currentTimeMillis());
                                    ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
                                    SocketManager.GAME_SEND_Ow_PACKET(player);
                                }
                            }
                        }
                        if (this.getType() == Constant.FIGHT_TYPE_PVM && player != null) {
                            int bouftou = 0, tofu = 0;

                            for (Monster.MobGrade mob : getMobGroup().getMobs().values()) {
                                switch (mob.getTemplate().getId()) {
                                    case 793:
                                        bouftou++;
                                        break;
                                    case 794:
                                        tofu++;
                                        break;
                                    case 289:
                                        if (player.getCurMap().getSubArea().getId() == 211)
                                            Monster.MobGroup.MAITRE_CORBAC.repop(player.getCurMap().getId());
                                        break;
                                }
                            }

                            if (Config.modeHalloween) {
                                if ((bouftou > 0 || tofu > 0) && !player.hasEquiped(976)) {
                                    if (bouftou > tofu) {
                                        drops.append(drops.length() > 0 ? "," : "").append("8169~1");
                                        player.setMalediction(8169);
                                        player.setFullMorph(Formulas.getRandomValue(16, 20), false, false);
                                    } else if (tofu > bouftou) {
                                        drops.append(drops.length() > 0 ? "," : "").append("8170~1");
                                        player.setMalediction(8170);
                                        player.setFullMorph(Formulas.getRandomValue(21, 25), false, false);
                                    } else {
                                        switch (Formulas.getRandomValue(1, 2)) {
                                            case 1:
                                                drops.append(drops.length() > 0 ? "," : "").append("8169~1");
                                                player.setMalediction(8169);
                                                player.setFullMorph(Formulas.getRandomValue(16, 20), false, false);
                                                break;
                                            case 2:
                                                drops.append(drops.length() > 0 ? "," : "").append("8170~1");
                                                player.setMalediction(8170);
                                                player.setFullMorph(Formulas.getRandomValue(21, 25), false, false);
                                                break;
                                        }
                                    }
                                }
                            }

                            switch (player.getCurMap().getId()) {
                                case 8984:
                                    GameObject obj = World.world.getObjTemplate(8012).createNewItem(1, false);
                                    if (player.addObjet(obj, true))
                                        World.world.addGameObject(obj);
                                    drops.append(drops.length() > 0 ? "," : "").append("8012~1");
                                    break;
                            }
                        }
                        /**********************/
                        /**     Fin Drop     **/
                        /**********************/

                        if (player != null) {
                            xpPlayer = XP.get();
                            if (xpPlayer != 0) {
                                if (player.getMorphMode()) {
                                    GameObject obj = player.getObjetByPos(Constant.ITEM_POS_ARME);
                                    if (obj != null)
                                        if (Constant.isIncarnationWeapon(obj.getTemplate().getId()))
                                            if (player.addXpIncarnations(xpPlayer))
                                                i.setLevelUp(true);
                                } else if (player.addXp(xpPlayer))
                                    i.setLevelUp(true);
                            }

                            if (winKamas != 0)
                                player.addKamas(winKamas);
                            if (xpGuild > 0 && player.getGuildMember() != null)
                                player.getGuildMember().giveXpToGuild(xpGuild);
                        }
                        if (winKamas != 0 && i.isInvocation() && !i.isDouble() && i.getInvocator().getPlayer() != null)
                            i.getInvocator().getPlayer().addKamas(winKamas);
                    }

                    String gfx = "";
                    if(player != null){
                        gfx=player.getGfxId()+"";
                    }

                    StringBuilder p = new StringBuilder();
                    p.append("2;");
                    p.append(i.getId()).append(";");
                    p.append(i.getPacketsName()).append(";");
                    p.append(i.getLvl()).append(";");
                    p.append(gfx).append(";");
                    p.append((i.isDead() ? "1" : "0")).append(";");
                    p.append(i.xpString(";")).append(";");
                    p.append((xpPlayer == 0 ? "" : xpPlayer)).append(";");
                    p.append((xpGuild == 0 ? "" : xpGuild)).append(";");
                    p.append((xpMount == 0 ? "" : xpMount)).append(";");
                    p.append(drops).append(";");// Drop
                    p.append((winKamas == 0 ? "" : winKamas)).append("|");
                    gains.put(i.getId(), p);
                } else {
                    // Si c'est un neutre, on ne gagne pas de points
                    int winH = 0, winD = 0, winKamas = 0;
                    long winXp = 0;

                    if (this.getType() == Constant.FIGHT_TYPE_AGRESSION) {
                        if (i.isInvocation() || i.isPrisme() || i.isMob() || i.isDouble())
                            continue;

                        if (this.getType() == Constant.FIGHT_TYPE_AGRESSION) {
                            if (getInit1().getPlayer().getAlignment() != 0 && getInit0().getPlayer().getAlignment() != 0) {
                                if (getInit1().getPlayer().getAccount().getCurrentIp().compareTo(getInit0().getPlayer().getAccount().getCurrentIp()) != 0 || Config.allowMulePvp)
                                    winH = Formulas.calculHonorWin(winners, loosers, i, false);
                                if (player.getDeshonor() > 0)
                                    winD = -1;
                            }
                        } else if (this.getType() == Constant.FIGHT_TYPE_CONQUETE)
                            winH = Formulas.calculHonorWin(winners, loosers, i, true);

                        if (player.getAlignment() != 0) {
                            if (player.get_honor() + winH < 0)
                                winH = -player.get_honor();
                            player.addHonor(winH);
                            player.setDeshonor(player.getDeshonor() + winD);
                        }

                        int maxHonor = World.world.getExpLevel(player.getGrade() + 1).pvp;
                        if (maxHonor == -1) maxHonor = World.world.getExpLevel(player.getGrade()).pvp;

                        String gfx = "";
                        if(player != null){
                            gfx=player.getGfxId()+"";
                        }

                        StringBuilder temporary = new StringBuilder();
                        temporary.append("2;").append(i.getId()).append(";").append(i.getPacketsName()).append(";").append(i.getLvl()).append(";").append(gfx).append(";").append((i.isDead() ? "1" : "0")).append(";");
                        temporary.append(player.getAlignment() != Constant.ALIGNEMENT_NEUTRE ? World.world.getExpLevel(player.getGrade()).pvp : 0).append(";");
                        temporary.append(player.get_honor()).append(";");
                        temporary.append(player.getAlignment() != Constant.ALIGNEMENT_NEUTRE ? maxHonor : 0).append(";");
                        temporary.append(winH).append(";");
                        temporary.append(player.getGrade()).append(";");
                        temporary.append(player.getDeshonor()).append(";");
                        temporary.append(winD);
                        temporary.append(";");
                        temporary.append(stalk ? "10275~" + quantity : "");
                        if (Config.modeHeroic && list != null) {
                            String value;
                            if ((value = list.get(player)) != null)
                                if (!value.isEmpty())
                                    temporary.append(stalk ? "," : "").append(value);
                            winXp = totalXP / winners.size();
                            winKamas = Formulas.getRandomValue(kamas.first, kamas.second);
                            player.addXp(winXp);
                        }
                        temporary.append(";").append(winKamas).append(";0;0;0;").append(winXp).append("|");
                        gains.put(i.getId(), temporary);
                    } else if (this.getType() == Constant.FIGHT_TYPE_CONQUETE) {
                        if (player != null) {
                            winH = (int) (player.get_honor() * 0.1);
                            if (winH == 0)
                                winH = 50;
                            if (winH > 500)
                                winH = 500;
                            if (player.get_honor() + winH < 0)
                                winH = -player.get_honor();
                            player.addHonor(winH);
                            if (player.getDeshonor() - winD < 0)
                                winD = 0;
                            player.setDeshonor(player.getDeshonor() - winD);
                            int maxHonor = World.world.getExpLevel(player.getGrade() + 1).pvp;
                            if (maxHonor == -1)
                                maxHonor = World.world.getExpLevel(player.getGrade()).pvp;

                            String gfx = "";
                            if(player != null){
                                gfx=player.getGfxId()+"";
                            }

                            StringBuilder temporary = new StringBuilder();
                            temporary.append("2;").append(i.getId()).append(";").append(i.getPacketsName()).append(";").append(i.getLvl()).append(";").append(gfx).append(";").append((i.isDead() ? "1" : "0")).append(";");
                            temporary.append(player.getAlignment() != Constant.ALIGNEMENT_NEUTRE ? World.world.getExpLevel(player.getGrade()).pvp : 0).append(";");
                            temporary.append(player.get_honor()).append(";");
                            temporary.append(player.getAlignment() != Constant.ALIGNEMENT_NEUTRE ? maxHonor : 0).append(";");
                            temporary.append(winH).append(";");
                            temporary.append(player.getGrade()).append(";");
                            temporary.append(player.getDeshonor()).append(";");
                            temporary.append(winD);
                            temporary.append(";;0;0;0;0;0|");
                            gains.put(i.getId(), temporary);
                        } else {
                            final Prism prism = i.getPrism();
                            winH = winH * 5;
                            if (prism.getHonor() + winH < 0) winH = -prism.getHonor();
                            winH *= 3;
                            prism.addHonor(winH);

                            int maxHonor = World.world.getExpLevel(prism.getLevel() + 1).pvp;
                            if (maxHonor == -1)
                                maxHonor = World.world.getExpLevel(prism.getLevel()).pvp;

                            String gfx = "";
                            if(player != null){
                                gfx=player.getGfxId()+"";
                            }

                            StringBuilder temporary = new StringBuilder();
                            temporary.append("2;").append(i.getId()).append(";").append(i.getPacketsName()).append(";").append(i.getLvl()).append(";").append(gfx).append(";").append((i.isDead() ? "1" : "0")).append(";");
                            temporary.append(World.world.getExpLevel(prism.getLevel()).pvp).append(";");
                            temporary.append(prism.getHonor()).append(";");
                            temporary.append(maxHonor).append(";");
                            temporary.append(winH).append(";");
                            temporary.append(prism.getLevel()).append(";");
                            temporary.append("0;0;;0;0;0;0;0|");

                            gains.put(i.getId(), temporary);
                        }
                    }
                }
            }

            Collections.shuffle(winners);
            Map<Integer, Integer> invoks = new HashMap<>();

            winners.stream().filter(i -> i.isInvocation() && i.getMob() != null).filter(i -> i.getMob().getTemplate().getId() == 285).forEach(i -> invoks.put(i.getId(), i.getInvocator().getId()));

            if (invoks != null && invoks.size() > 0)
                for (Entry<Integer, Integer> entry : invoks.entrySet())
                    winners = this.deplace(winners, entry.getValue(), entry.getKey());

            winners.stream().filter(fighter -> !(fighter.isInvocation() && fighter.getMob() != null && fighter.getMob().getTemplate().getId() != 285)).filter(fighter -> !fighter.isDouble() && gains.get(fighter.getId()) != null).forEach(fighter -> packet.append(gains.get(fighter.getId()).toString()));

            //endregion End winner
            System.out.println("TEAM WINNERS : " + (System.currentTimeMillis() - t));
            t = System.currentTimeMillis();
            //region Looser
            for (Fighter i : loosers) {
                if (i.isInvocation() && i.getMob() != null && i.getMob().getTemplate().getId() != 285)
                    continue;
                if (i.isDouble())
                    continue;

                final Player player = i.getPlayer();

                if (player != null && this.getType() != Constant.FIGHT_TYPE_CHALLENGE)
                    player.calculTurnCandy();

                String gfx = "";
                if(player != null){
                    gfx=player.getGfxId()+"";
                }

                if (this.getType() != Constant.FIGHT_TYPE_AGRESSION && this.getType() != Constant.FIGHT_TYPE_CONQUETE) {
                    StringBuilder temporary = new StringBuilder();
                    if (i.getPdv() == 0 || i.hasLeft() || i.isDead())
                        temporary.append("0;").append(i.getId()).append(";").append(i.getPacketsName()).append(";").append(i.getLvl()).append(";").append(gfx).append(";1").append(";").append(i.xpString(";")).append(";;;;|");
                    else
                        temporary.append("0;").append(i.getId()).append(";").append(i.getPacketsName()).append(";").append(i.getLvl()).append(";").append(gfx).append(";0").append(";").append(i.xpString(";")).append(";;;;|");
                    packet.append(temporary);
                } else {
                    // Si c'est un neutre, on ne gagne pas de points
                    int winH = 0;
                    int winD = 0;
                    if (this.getType() == Constant.FIGHT_TYPE_AGRESSION) {
                        if (getInit1().getPlayer().getAlignment() != 0 && getInit0().getPlayer().getAlignment() != 0)
                            if (getInit1().getPlayer().getAccount().getCurrentIp().compareTo(getInit0().getPlayer().getAccount().getCurrentIp()) != 0 || Config.allowMulePvp)
                                winH = Formulas.calculHonorWin(winners, loosers, i, false);

                        if (player == null)
                            continue;
                        if (player.getAlignment() != 0) {
                            player.remHonor(player.get_honor() + winH < 0 ? -player.get_honor() : -winH);
                            if (player.getDeshonor() - winD < 0)
                                winD = 0;
                            player.setDeshonor(player.getDeshonor() - winD);
                        }

                        int maxHonor = World.world.getExpLevel(player.getGrade() + 1).pvp;
                        if (maxHonor == -1)
                            maxHonor = World.world.getExpLevel(player.getGrade()).pvp;

                        packet.append("0;").append(i.getId()).append(";").append(i.getPacketsName()).append(";").append(i.getLvl()).append(";").append(gfx).append(";").append((i.isDead() ? "1" : "0")).append(";");
                        packet.append(player.getAlignment() != Constant.ALIGNEMENT_NEUTRE ? World.world.getExpLevel(player.getGrade()).pvp : 0).append(";");
                        packet.append(player.get_honor()).append(";");
                        packet.append(player.getAlignment() != Constant.ALIGNEMENT_NEUTRE ? maxHonor : 0).append(";");
                        packet.append(winH).append(";");
                        packet.append(player.getGrade()).append(";");
                        packet.append(player.getDeshonor()).append(";");
                        packet.append(winD);
                        packet.append(";;0;0;0;0;0|");
                    } else if (this.getType() == Constant.FIGHT_TYPE_CONQUETE) {
                        winH = Formulas.calculHonorWin(winners, loosers, i, true);

                        if (player != null) {
                            winH = 0;
                            if (player.getDeshonor() - winD < 0)
                                winD = 0;
                            int maxHonor = World.world.getExpLevel(player.getGrade() + 1).pvp;
                            if (maxHonor == -1)
                                maxHonor = World.world.getExpLevel(player.getGrade()).pvp;

                            player.setDeshonor(player.getDeshonor() - winD);
                            packet.append("0;").append(i.getId()).append(";").append(i.getPacketsName()).append(";").append(i.getLvl()).append(";").append(gfx).append(";").append((i.isDead() ? "1" : "0")).append(";");
                            packet.append(player.getAlignment() != Constant.ALIGNEMENT_NEUTRE ? World.world.getExpLevel(player.getGrade()).pvp : 0).append(";");
                            packet.append(player.get_honor()).append(";");
                            packet.append(player.getAlignment() != Constant.ALIGNEMENT_NEUTRE ? maxHonor : 0).append(";");
                            packet.append(winH).append(";");
                            packet.append(player.getGrade()).append(";");
                            packet.append(player.getDeshonor()).append(";");
                            packet.append(winD);
                            packet.append(";;0;0;0;0;0|");
                        } else {
                            Prism prism = i.getPrism();

                            if (prism.getHonor() + winH < 0)
                                winH = -prism.getHonor();
                            int maxHonor = World.world.getExpLevel(prism.getLevel() + 1).pvp;
                            if (maxHonor == -1)
                                maxHonor = World.world.getExpLevel(prism.getLevel()).pvp;

                            prism.addHonor(winH);
                            packet.append("0;").append(i.getId()).append(";").append(i.getPacketsName()).append(";").append(i.getLvl()).append(";").append(gfx).append(";").append((i.isDead() ? "1" : "0")).append(";");
                            packet.append(World.world.getExpLevel(prism.getLevel()).pvp).append(";");
                            packet.append(prism.getHonor()).append(";");
                            packet.append(maxHonor).append(";");
                            packet.append(winH).append(";");
                            packet.append(prism.getLevel()).append(";");
                            packet.append("0;0;;0;0;0;0;0|");
                        }
                    }
                }
            }
            /** End Looser **/
            if (Collector.getCollectorByMapId(getMap().getId()) != null && getType() == Constant.FIGHT_TYPE_PVM) {
                Collector collector = Collector.getCollectorByMapId(getMap().getId());

                long winxp = FormuleOfficiel.getXp(collector, winners, totalXP, nbbonus, (getMobGroup() != null ? getMobGroup().getStarBonus() : 0), challXp, lvlMax, lvlMin, lvlLoosers, lvlWinners) / 10;
                long winkamas = (int) Math.floor(Formulas.getKamasWinPerco(kamas.first, kamas.second));

                collector.setXp(collector.getXp() + winxp);
                collector.setKamas(collector.getKamas() + winkamas);
                Guild guild = World.world.getGuild(collector.getGuildId());

                packet.append("5;").append(collector.getId()).append(";").append(collector.getFullName()).append(";").append(World.world.getGuild(collector.getGuildId()).getLvl()).append(";0;");
                packet.append(guild.getLvl()).append(";");
                packet.append(guild.getXp()).append(";");
                packet.append(World.world.getGuildXpMax(guild.getLvl())).append(";");
                packet.append(";");// XpGagner
                packet.append(winxp).append(";");// XpGuilde
                packet.append(";");// Monture

                String drops = "";
                ArrayList<Drop> temporary = new ArrayList<>(dropsPlayers);
                Collections.shuffle(temporary);
                Map<Integer, Integer> objectsWon = new HashMap<>();

                if (collector.getPodsTotal() < collector.getMaxPod()) {
                    for (Drop drop : temporary) {
                        final double jet = Double.parseDouble(formatter.format(Math.random() * 100).replace(',', '.')),
                                chance = (int) (drop.getLocalPercent() * (World.world.getGuild(collector.getGuildId()).getStats(Constant.STATS_ADD_PROS) / 100.0));

                        if (jet < chance) {
                            ObjectTemplate objectTemplate = World.world.getObjTemplate(drop.getObjectId());

                            if (objectTemplate == null)
                                continue;

                            boolean itsOk = false, unique = false;
                            switch (drop.getAction()) {
                                case -2:
                                    unique = true;
                                    itsOk = true;
                                    break;
                                case -1:// All items without condition.
                                    itsOk = true;
                                    break;

                                case 1:// Is meat so..
                                    break;

                                case 2:// Verification of the condition ( MAP )
                                    for (String id : drop.getCondition().split(","))
                                        if (id.equals(getMap().getId() + ""))
                                            itsOk = true;
                                    break;

                                case 3:// Alignement
                                    if (this.getMapOld().getSubArea() == null)
                                        break;
                                    switch (drop.getCondition()) {
                                        case "0":
                                            if (this.getMapOld().getSubArea().getAlignment() == 2)
                                                itsOk = true;
                                            break;
                                        case "1":
                                            if (this.getMapOld().getSubArea().getAlignment() == 1)
                                                itsOk = true;
                                            break;
                                        case "2":
                                            if (this.getMapOld().getSubArea().getAlignment() == 2)
                                                itsOk = true;
                                            break;
                                        case "3":
                                            if (this.getMapOld().getSubArea().getAlignment() == 3)
                                                itsOk = true;
                                            break;

                                        default:
                                            itsOk = true;
                                            break;
                                    }
                                    break;

                                case 4:
                                    if (objectTemplate.getId() == 2553)//Gros boulet
                                        itsOk = true;
                                    break;

                                case 5:
                                    itsOk = false;
                                    break;

                                case 6: // Les percepteurs ne font pas de qu�tes
                                case 7:
                                    break;

                                default:
                                    itsOk = true;
                                    break;
                            }

                            if (itsOk) {
                                objectsWon.put(objectTemplate.getId(), (objectsWon.get(objectTemplate.getId()) == null ? 0 : objectsWon.get(objectTemplate.getId())) + 1);

                                if (unique)
                                    dropsPlayers.remove(drop);
                            }
                        }
                    }

                    for (Entry<Integer, Integer> entry : objectsWon.entrySet()) {
                        ObjectTemplate objectTemplate = World.world.getObjTemplate(entry.getKey());

                        if (objectTemplate == null || collector.getPodsTotal() + objectTemplate.getPod() * entry.getValue() >= collector.getMaxPod())
                            continue;
                        if (drops.length() > 0) drops += ",";

                        drops += entry.getKey() + "~" + entry.getValue();

                        GameObject newObj = World.world.getObjTemplate(objectTemplate.getId()).createNewItemWithoutDuplication(collector.getOjects().values(), entry.getValue(), false);

                        if (newObj != null && collector.getOjects().get(newObj.getGuid()) == null) {
                            if (collector.addObjet(newObj))
                                World.world.addGameObject(newObj);
                        }
                    }
                }
                packet.append(drops).append(";");// Drop
                packet.append(winkamas).append("|");

                ((CollectorData) DatabaseManager.get(CollectorData.class)).update(collector);
            }
            //endregion
            System.out.println("TEAM LOOSERS : " + (System.currentTimeMillis() - t));
            System.out.println("FIN TEMPS DROP GE : " + (System.currentTimeMillis() - t1));
            return packet.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred when server went to give the 'GE' packet : " + e.getMessage() + " " + e.getLocalizedMessage());
        }
        return "";
    }

    ArrayList<Fighter> deplace(ArrayList<Fighter> TEAM1,
                               Integer Invocator, Integer Invocation) {
        int k = 0;
        int p = 0;
        int j = 0;
        int s = TEAM1.size() - 1;
        boolean b = true;
        Fighter invok = null;
        for (Fighter i : TEAM1) {
            if (i.getId() == Invocation) {
                invok = i;
                b = false;
            }
            if (!b && invok != i) {
                TEAM1.set((k - 1), i);
            }
            k++;
        }
        TEAM1.set(s, invok);
        k = 0;
        b = true;
        for (Fighter i : TEAM1) {
            if (i.getId() == Invocator) {
                p = k;
                b = false;
            }
            if (!b && i.getId() != Invocator) {
                j++;
                if (k < s)
                    TEAM1.set((s - j + 1), TEAM1.get(s - j));
            }
            k++;
        }
        TEAM1.set(p + 1, invok);
        return TEAM1;
    }

    public String getGTL() {
        String packet = "GTL";
        if (this.orderPlaying != null)
            for (Fighter f : this.orderPlaying)
                if (!f.isDead())
                    packet += "|" + f.getId();
        return packet + (char) 0x00;
    }

    public String parseFightInfos() {
        StringBuilder infos = new StringBuilder();
        infos.append(getId()).append(";");
        long time = startTime + TimeZone.getDefault().getRawOffset();
        infos.append((getStartTime() == 0 ? "-1" : time)).append(";");
        // Team1
        infos.append("0,");// 0 car toujours joueur :)
        switch (getType()) {
            case Constant.FIGHT_TYPE_CHALLENGE:
                infos.append("0,");
                infos.append(this.getTeamSizeWithoutInvocation(this.getTeam0().values())).append(";");
                // Team2
                infos.append("0,");
                infos.append("0,");
                infos.append(this.getTeamSizeWithoutInvocation(this.getTeam1().values())).append(";");
                break;

            case Constant.FIGHT_TYPE_AGRESSION:
                infos.append(getInit0().getPlayer().getAlignment()).append(",");
                infos.append(getTeam0().size()).append(";");
                // Team2
                infos.append("0,");
                infos.append(getInit1().getPlayer().getAlignment()).append(",");
                infos.append(this.getTeamSizeWithoutInvocation(this.getTeam1().values())).append(";");
                break;

            case Constant.FIGHT_TYPE_CONQUETE:
                infos.append(getInit0().getPlayer().getAlignment()).append(",");
                infos.append(this.getTeamSizeWithoutInvocation(this.getTeam0().values())).append(";");
                // Team2
                infos.append("0,");
                infos.append(getPrism().getAlignment()).append(",");
                infos.append(this.getTeamSizeWithoutInvocation(this.getTeam1().values())).append(";");
                break;

            case Constant.FIGHT_TYPE_PVM:
            case Constant.FIGHT_TYPE_DOPEUL:
                infos.append("0,");
                infos.append(this.getTeamSizeWithoutInvocation(this.getTeam0().values())).append(";");
                // Team2
                infos.append("1,");
                if (getTeam0().isEmpty())
                    infos.append("0,");
                else
                    infos.append(getTeam1().get(getTeam1().keySet().toArray()[0]).getMob().getTemplate().getAlign()).append(",");
                infos.append(this.getTeamSizeWithoutInvocation(this.getTeam1().values())).append(";");
                break;


            case Constant.FIGHT_TYPE_PVT:
                infos.append("0,");
                infos.append(this.getTeamSizeWithoutInvocation(this.getTeam0().values())).append(";");
                // Team2
                infos.append("3,");
                infos.append("0,");
                infos.append(this.getTeamSizeWithoutInvocation(this.getTeam1().values())).append(";");
                break;
        }
        return infos.toString();
    }

    int getTeamSizeWithoutInvocation(Collection<Fighter> fighters) {
        int i = 0;
        for (Fighter fighter : fighters) if (!fighter.isInvocation()) i++;
        return i;
    }

    public Fighter getFighterByGameOrder() {
        if (this.orderPlaying == null)
            return null;
        if (this.curPlayer >= this.orderPlaying.size())
            this.curPlayer = this.orderPlaying.size() - 1;
        if (this.curPlayer < 0)
            this.curPlayer = 0;
        if (this.orderPlaying.size() <= 0)
            return null;
        Fighter current = null;
        try {
            current = this.orderPlaying.get(this.curPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return current;
    }

    int getOrderPlayingSize() {
        if (this.orderPlaying == null)
            return 0;
        if (this.orderPlaying.size() <= 0)
            return 0;
        return this.orderPlaying.size();
    }

    boolean haveFighterInOrdreJeu(Fighter f) {
        return this.orderPlaying != null && f != null && this.orderPlaying.contains(f);
    }

    public List<Fighter> getOrderPlaying() {
        return this.orderPlaying;
    }

    public void cast(Fighter fighter, Runnable runnable, SortStats SS) {
        Fighter current = this.getFighterByGameOrder();
        if (current != null && fighter != null && current.getId() == fighter.getId()) {
            if (this.turn != null && System.currentTimeMillis() - this.turn.getStartTime() >= 31_000) {
                if(System.currentTimeMillis() - this.turn.getStartTime() >= 35_000) this.endTurn(false);
                return;
            }
            SocketManager.GAME_SEND_GAS_PACKET_TO_FIGHT(this, 7, fighter.getId());
            try { runnable.run(); } catch (Exception e) {e.printStackTrace();}
            SocketManager.GAME_SEND_GAF_PACKET_TO_FIGHT(this, 7, 0, fighter.getId());
        }
    }

    public static Map<Player, String> give(ArrayList<GameObject> objects, ArrayList<Fighter> winners) {
        final Map<Player, String> list = new HashMap<>();

        if(Config.modeHeroic) {
            final ArrayList<Player> players = new ArrayList<>();

            new ArrayList<>(winners).stream().filter(fighter -> fighter != null).forEach(fighter -> {
                final Player player = fighter.getPlayer();

                if (player != null) {
                    players.add(player);
                    list.put(player, "");
                }
            });

            if (players.size() > 0 && objects != null && !objects.isEmpty()) {
                byte count = -1;
                GameObject object;

                Iterator<GameObject> iterator = objects.iterator();
                while (iterator.hasNext()) {
                    object = objects.iterator().next();

                    if (object == null) {
                        iterator.remove();
                        continue;
                    }

                    count++;
                    final Player player = players.get(count);

                    if (player != null) {
                        object.setPosition(Constant.ITEM_POS_NO_EQUIPED);
                        player.addObjet(object, true);
                        String value = list.get(player);
                        value += (value.isEmpty() ? "" : ",") + object.getTemplate().getId() + "~" + object.getQuantity();
                        list.remove(player);
                        list.put(player, value);
                        objects.remove(object);
                    }
                    if (count >= players.size() - 1)
                        count = -1;

                }
            }
        }
        return list;
    }

    public void setMobGroup2(Monster.MobGroup mobGroup2) {
        this.mobGroup2 = mobGroup2;
    }
}
