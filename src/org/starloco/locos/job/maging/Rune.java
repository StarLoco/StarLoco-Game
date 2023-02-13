package org.starloco.locos.job.maging;

import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;

import java.util.ArrayList;
import java.util.List;

public class Rune {

    public final static List<Rune> runes = new ArrayList<>();

    public static Rune getRuneById(int id) {
        for(Rune rune : runes)
            if(rune.getId() == id)
                return rune;
        return null;
    }

    public static Rune getRuneByCharacteristic(short stat) {
        for(Rune rune : runes)
            if(rune.getCharacteristic() == stat)
                return rune;
        return null;
    }

    public static Rune getRuneByCharacteristicAndByWeight(short stat) {
        Rune valid = null;
        float weight = 999;
        for(Rune rune : runes) {
            if (rune.getCharacteristic() == stat && weight > rune.getWeight()) {
                weight = rune.getWeight();
                valid = rune;
            }
        }
        return valid;
    }

    private short id, characteristic;
    private float weight;
    private byte bonus;

    public Rune(short id, float weight, byte bonus) {
        this.id = id;
        this.weight = weight;
        this.bonus = bonus;
        this.setCharacteristic();
        Rune.runes.add(this);
    }

    public short getId() {
        return id;
    }

    private void setCharacteristic() {
        this.characteristic = Short.parseShort(World.world.getObjTemplate(this.id).getStrTemplate().split("#")[0], 16);
        if(this.characteristic == 112)
            this.characteristic = Constant.STATS_ADD_DOMA;
    }

    public short getCharacteristic() {
        return characteristic;
    }

    public float getWeight() {
        return weight;
    }

    public byte getBonus() {
        return bonus;
    }

    public byte[] getChance() {
        return this.weight <= 1 ? new byte[] {66, 34, 0} : new byte[] {43, 50, 7};
    }
}