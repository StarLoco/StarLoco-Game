package org.starloco.locos.entity;

import org.starloco.locos.area.Area;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.util.TimerWaiter;

import java.util.HashMap;

public class Prism {

    public final static byte NEW = 1, NORMAL = 2, FIGHTING = 3;

    private int id;
    private byte alignment, state;
    private int level;
    private short Map;
    private int cell;
    private int name;
    private int gfx;
    private int honor = 0;
    private int area = -1;
    private Fight fight;
    private java.util.Map<Integer, Integer> stats = new HashMap<>();

    public Prism(int id, byte alignment, int level, short Map, int cell, int honor, int area) {
        this.id = id;
        this.alignment = alignment;
        this.state = NEW;
        this.level = level;
        this.Map = Map;
        this.cell = cell;
        if (alignment == 1) {
            this.name = 1111;
            this.gfx = 8101;
        } else {
            this.name = 1112;
            this.gfx = 8100;
        }
        this.honor = honor;
        this.area = area;
        this.fight = null;
        TimerWaiter.addNext(() -> this.state = NORMAL, 60 * 60_000);
    }

    public static void parseAttack(Player player) {
        for (Prism prism : World.world.AllPrisme())
            if (prism.getFight() != null && player.getAlignment() == prism.getAlignment())
                SocketManager.SEND_Cp_INFO_ATTAQUANT_PRISME(player, attackerOfPrisme(prism.getId(), prism.getMap(), prism.getFight().getId()));
    }

    public static void parseDefense(Player player) {
        for (Prism prism : World.world.AllPrisme())
            if (prism.getFight() != null && player.getAlignment() == prism.getAlignment())
                SocketManager.SEND_CP_INFO_DEFENSEURS_PRISME(player, defenderOfPrisme(prism.getId(), prism.getMap(), prism.getFight().getId()));
    }

    public static String attackerOfPrisme(int id, short MapId, int FightId) {
        String str = "+";
        str += Integer.toString(id, 36);
        GameMap gameMap = World.world.getMap(MapId);
        if(gameMap != null) {
            for (Fight fight : gameMap.getFights()) {
                if (fight.getId() == FightId) {
                    for (Fighter fighter : fight.getFighters(1)) {
                        if (fighter.getPlayer() == null)
                            continue;
                        str += "|";
                        str += Integer.toString(fighter.getPlayer().getId(), 36) + ";";
                        str += fighter.getPlayer().getName() + ";";
                        str += fighter.getPlayer().getLevel() + ";";
                        str += "0;";
                    }
                }
            }
        }
        return str;
    }

    public static String defenderOfPrisme(int id, short MapId, int FightId) {
        String str = "+";
        String stra = "";
        str += Integer.toString(id, 36);
        GameMap gameMap = World.world.getMap(MapId);
        if(gameMap != null) {
            for (Fight fight : gameMap.getFights()) {
                if (fight.getId() == FightId) {
                    for (Fighter fighter : fight.getFighters(2)) {
                        if (fighter.getPlayer() == null)
                            continue;
                        str += "|";
                        str += Integer.toString(fighter.getPlayer().getId(), 36)
                                + ";";
                        str += fighter.getPlayer().getName() + ";";
                        str += fighter.getPlayer().getGfxId() + ";";
                        str += fighter.getPlayer().getLevel() + ";";
                        str += Integer.toString(fighter.getPlayer().getColor1(), 36)
                                + ";";
                        str += Integer.toString(fighter.getPlayer().getColor2(), 36)
                                + ";";
                        str += Integer.toString(fighter.getPlayer().getColor3(), 36)
                                + ";";
                        if (fight.getFighters(2).size() > 7)
                            str += "1;";
                        else
                            str += "0;";
                    }
                    stra = str.substring(1);
                    stra = "-" + stra;
                    fight.setDefenders(stra);
                }
            }
        }
        return str;
    }

    public int getId() {
        return this.id;
    }

    public byte getAlignment() {
        return this.alignment;
    }

    public byte getState() {
        return state;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int i) {
        this.level = i;
    }

    public short getMap() {
        return this.Map;
    }

    public int getCell() {
        return this.cell;
    }

    public void setCell(int i) {
        this.cell = i;
    }

    public int getHonor() {
        return this.honor;
    }

    public void addHonor(int i) {
        this.honor += i;
    }

    public int getGrade() {
        if (this.honor >= 17500)
            return 10;
        for (int n = 1; n <= 10; n++)
            if (this.honor < World.world.getExpLevel(n).pvp)
                return n - 1;
        return 1;
    }

    public int getConquestArea() {
        return this.area;
    }

    public void setConquestArea(int i) {
        this.area = i;
    }

    public Fight getFight() {
        return this.fight;
    }

    public void setFight(Fight fight) { this.fight = fight; }

    public Stats getStats() {
        return new Stats(this.stats);
    }

    public void refreshStats() {
        int feu = 1000 + (500 * this.level);
        int intel = 1000 + (500 * this.level);
        int agi = 1000 + (500 * this.level);
        int sagesse = 1000 + (500 * this.level);
        int chance = 1000 + (500 * this.level);
        int resistance = 9 * this.level;
        this.stats.clear();
        this.stats.put(Constant.STATS_ADD_FORC, feu);
        this.stats.put(Constant.STATS_ADD_INTE, intel);
        this.stats.put(Constant.STATS_ADD_AGIL, agi);
        this.stats.put(Constant.STATS_ADD_SAGE, sagesse);
        this.stats.put(Constant.STATS_ADD_CHAN, chance);
        this.stats.put(Constant.STATS_ADD_RP_NEU, resistance);
        this.stats.put(Constant.STATS_ADD_RP_FEU, resistance);
        this.stats.put(Constant.STATS_ADD_RP_EAU, resistance);
        this.stats.put(Constant.STATS_ADD_RP_AIR, resistance);
        this.stats.put(Constant.STATS_ADD_RP_TER, resistance);
        this.stats.put(Constant.STATS_ADD_AFLEE, resistance);
        this.stats.put(Constant.STATS_ADD_MFLEE, resistance);
        this.stats.put(Constant.STATS_ADD_PA, 6);
        this.stats.put(Constant.STATS_ADD_PM, 0);
    }

    public int getX() {
        GameMap Map = World.world.getMap(this.Map);
        return Map.getX();
    }

    public int getY() {
        GameMap Map = World.world.getMap(this.Map);
        return Map.getY();
    }

    public SubArea getSubArea() {
        GameMap Map = World.world.getMap(this.Map);
        return Map.getSubArea();
    }

    public Area getArea() {
        GameMap Map = World.world.getMap(this.Map);
        return Map.getSubArea().getArea();
    }

    public String parseToGM() {
        if (this.getFight() != null)
            return "";
        return "GM|+" + this.cell + ";1;0;" + this.id + ";" + this.name + ";-10;" + this.gfx + "^100;" + this.level + ";" + getGrade() + ";" + this.alignment;
    }
}
