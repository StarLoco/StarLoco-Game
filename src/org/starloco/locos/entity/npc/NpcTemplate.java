package org.starloco.locos.entity.npc;

import it.unimi.dsi.fastutil.ints.IntList;
import org.starloco.locos.quest.Quest;

import java.util.*;
import java.util.stream.Collectors;

public class NpcTemplate {
    private final int id, gfxId, scaleX, scaleY, sex, color1, color2, color3;
    private final List<Integer> accessories;
    private final int customArtWork;
    // private Quest quest;

    public NpcTemplate(int id, int gfxId, int scaleX, int scaleY, int sex, int color1, int color2, int color3, List<Integer> accessories, int customArtWork) {
        this.id = id;
        this.gfxId = gfxId;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.sex = sex;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.accessories = accessories;
        this.customArtWork = customArtWork;
    }

    public int getId() {
        return id;
    }

    public int getGfxId() {
        return gfxId;
    }

    public int getScaleX() {
        return scaleX;
    }

    public int getScaleY() {
        return scaleY;
    }

    public int getSex() {
        return sex;
    }

    public int getColor1() {
        return color1;
    }

    public int getColor2() {
        return color2;
    }

    public int getColor3() {
        return color3;
    }

    public String encodeAccessories() {
        return accessories.stream().map(Integer::toHexString).collect(Collectors.joining(","));
    }

    public int getCustomArtWork() {
        return customArtWork;
    }

    public Quest getQuest() {
        return null; // FIXME Diabu
        //return quest;
    }

    public boolean isBankClerk() {
        switch(id) {
            case 100:
            case 520:
            case 522:
            case 691:
            case 692:
                return true;
            default:
                return false;
        }
    }
}
