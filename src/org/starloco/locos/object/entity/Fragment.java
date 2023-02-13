package org.starloco.locos.object.entity;

import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.ObjectData;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.object.GameObject;

import java.util.ArrayList;

public class Fragment extends GameObject {

    private ArrayList<Couple<Integer, Integer>> runes;

    public Fragment(int id, String runes) {
        super(id);
        this.runes = new ArrayList<>();
        this.parseRunes(runes);
    }

    public Fragment(String runes) {
        super(-1);
        this.runes = new ArrayList<>();
        this.parseRunes(runes);
        ((ObjectData) DatabaseManager.get(ObjectData.class)).insert(this);
    }

    private void parseRunes(String runes) {
        if (!runes.isEmpty()) {
            for (String rune : runes.split(";")) {
                String[] split = rune.split(":");
                this.runes.add(new Couple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            }
        }
    }

    public ArrayList<Couple<Integer, Integer>> getRunes() {
        return runes;
    }

    public void addRune(int id) {
        Couple<Integer, Integer> rune = this.search(id);

        if (rune == null)
            this.runes.add(new Couple<>(id, 1));
        else
            rune.second += 1;
    }

    public Couple<Integer, Integer> search(int id) {
        for (Couple<Integer, Integer> couple : this.runes)
            if (couple.first == id)
                return couple;
        return null;
    }
}