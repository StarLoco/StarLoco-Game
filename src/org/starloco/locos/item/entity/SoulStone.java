package org.starloco.locos.item.entity;

import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.ObjectData;
import org.starloco.locos.item.FullItem;
import org.starloco.locos.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoulStone extends FullItem {

    private ArrayList<Pair<Integer, Integer>> monsters;

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

    public List<Pair<Integer, Integer>> getMonsters() {
        return monsters;
    }

    public Stream<Integer> getMonsterIDs() {
        return monsters.stream().map(Pair::getFirst);
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
                    Pair<Integer, Integer> couple = new Pair<>(id, level);
                    this.monsters.add(couple);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Optional<SoulStone> safeCast(FullItem obj) {
        if(obj instanceof SoulStone) return Optional.of((SoulStone) obj);
        return Optional.empty();
    }

    @Override
    public String encodeStats() {
        return this.monsters.stream().map(s -> "26f####"+Integer.toString(s.first, 16)).collect(Collectors.joining(","));
    }

    public String parseGroupData() {
        StringBuilder toReturn = new StringBuilder();
        boolean isFirst = true;
        for (Pair<Integer, Integer> curMob : this.monsters) {
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
        for (Pair<Integer, Integer> curMob : this.monsters) {
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
