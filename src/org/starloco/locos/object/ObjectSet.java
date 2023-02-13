package org.starloco.locos.object;

import org.starloco.locos.client.other.Stats;
import org.starloco.locos.game.world.World;

import java.util.ArrayList;

public class ObjectSet {

    private int id;
    private ArrayList<Stats> effects = new ArrayList<>();
    private ArrayList<ObjectTemplate> itemTemplates = new ArrayList<>();

    public ObjectSet(int id, String items, String bonuses) {
        this.id = id;

        for (String str : items.split(",")) {
            try {
                ObjectTemplate obj = World.world.getObjTemplate(Integer.parseInt(str.trim()));
                if (obj == null)
                    continue;
                this.itemTemplates.add(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.effects.add(new Stats());

        for (String str : bonuses.split(";")) {
            Stats S = new Stats();
            for (String str2 : str.split(",")) {
                if (!str2.equalsIgnoreCase("")) {
                    try {
                        String[] infos = str2.split(":");
                        int stat = Integer.parseInt(infos[0]);
                        int value = Integer.parseInt(infos[1]);
                        //on ajoute a la stat
                        S.addOneStat(stat, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            this.effects.add(S);
        }
    }

    public int getId() {
        return this.id;
    }

    public Stats getBonusStatByItemNumb(int numb) {
        if (numb > this.effects.size())
            return new Stats();
        return effects.get(numb - 1);
    }

    public ArrayList<ObjectTemplate> getItemTemplates() {
        return this.itemTemplates;
    }
}