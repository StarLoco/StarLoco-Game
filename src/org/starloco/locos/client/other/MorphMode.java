package org.starloco.locos.client.other;

import org.starloco.locos.kernel.Constant;

public class MorphMode {

    private final int id;
    private final int gfxId;
    private int hp;
    private final String name;
    private final String spells;
    private boolean dungeon = false;
    private final Stats stats;

    public MorphMode(int id, String name, int gfxId, String spells, int[] args) {
        this.id = id;
        this.name = name;
        this.gfxId = gfxId;
        this.spells = spells;
        this.stats = this.handleStats(args);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGfxId() {
        return gfxId;
    }

    public int getHp() {
        return hp;
    }

    public String getSpells() {
        return spells;
    }

    public boolean isDungeon() {
        return dungeon;
    }

    public boolean canUseWeapon() {
        return false;
    }

    public Stats getStats() {
        return stats;
    }

    private Stats handleStats(int[] args) {
        if(args.length == 0)
            return null;

        Stats stats = new Stats();
        this.hp = args[0];
        stats.addOneStat(Constant.STATS_ADD_PA, args[1]);
        stats.addOneStat(Constant.STATS_ADD_PM, args[2]);
        stats.addOneStat(Constant.STATS_ADD_VITA, args[3]);
        stats.addOneStat(Constant.STATS_ADD_SAGE, args[4]);
        stats.addOneStat(Constant.STATS_ADD_FORC, args[5]);
        stats.addOneStat(Constant.STATS_ADD_INTE, args[6]);
        stats.addOneStat(Constant.STATS_ADD_CHAN, args[7]);
        stats.addOneStat(Constant.STATS_ADD_AGIL, args[8]);
        stats.addOneStat(Constant.STATS_ADD_INIT, args[9]);
        stats.addOneStat(Constant.STATS_ADD_PROS, 100);
        stats.addOneStat(Constant.STATS_CREATURE, 1);
        this.dungeon = args[10] == 1;
        return stats;
    }
}
