package org.starloco.locos.object.entity;

import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.ObjectData;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.object.GameObject;

import java.util.ArrayList;

public class SoulStone extends GameObject {

    private ArrayList<Couple<Integer, Integer>> monsters;

    public SoulStone(int id, int quantity, int template, int pos, String strStats) {
        super(id, template, quantity, pos, strStats, 0);
        this.monsters = new ArrayList<>();
        this.stringToStats(strStats);
        ((ObjectData) DatabaseManager.get(ObjectData.class)).insert(this);
    }

    public SoulStone(int quantity, int template, int pos, String strStats) {
        super(-1, template, quantity, pos, strStats, 0);
        this.monsters = new ArrayList<>();
        this.stringToStats(strStats);
        ((ObjectData) DatabaseManager.get(ObjectData.class)).insert(this);
    }

    public ArrayList<Couple<Integer, Integer>> getMonsters() {
        return monsters;
    }

    private void stringToStats(String m) {
        if (!m.equalsIgnoreCase("")) {
            if (this.monsters == null)
                this.monsters = new ArrayList<>();

            String[] split = m.split("\\|");
            for (String s : split) {
                try {
                    int id = Integer.parseInt(s.split(",")[0]);
                    int level = Integer.parseInt(s.split(",")[1]);
                    Couple<Integer, Integer> couple = new Couple<>(id, level);
                    this.monsters.add(couple);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String parseStatsString() {
        StringBuilder stats = new StringBuilder();
        boolean isFirst = true;
        for (Couple<Integer, Integer> coupl : this.monsters) {
            if (!isFirst)
                stats.append(",");
            try {
                stats.append("26f#0#0#").append(Integer.toHexString(coupl.first));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            isFirst = false;
        }
        return stats.toString();
    }

    public String parseGroupData() {
        StringBuilder toReturn = new StringBuilder();
        boolean isFirst = true;
        for (Couple<Integer, Integer> curMob : this.monsters) {
            if (!isFirst)
                toReturn.append(";");
            toReturn.append(curMob.first).append(",").append(curMob.second).append(",").append(curMob.second);
            isFirst = false;
        }
        return toReturn.toString();
    }

    @Override
    public String parseToSave() {
        StringBuilder toReturn = new StringBuilder();
        boolean isFirst = true;
        for (Couple<Integer, Integer> curMob : this.monsters) {
            if (!isFirst)
                toReturn.append("|");
            toReturn.append(curMob.first).append(",").append(curMob.second);
            isFirst = false;
        }
        return toReturn.toString();
    }

    public static boolean isInArenaMap(int id) {
        return "10131,10132,10133,10134,10135,10136,10137,10138".contains(String.valueOf(id));
    }
}
