package org.starloco.locos.area;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;

import java.util.ArrayList;
import java.util.List;

public class SubArea {
    public static int BONTA = 0, BRAK = 0;

    private final int id;
    private final String name;
    private final Area area;
    private byte alignment;
    private Prism prism;
    private boolean conquerable;

    private final List<GameMap> maps = new ArrayList<>();
    private final List<Short> nearestSubAreas = new ArrayList<>();

    public SubArea(int id, String name, int area, String nearest) {
        this.id = id;
        this.name = name;
        this.area = World.world.getArea(area);
        if(!nearest.isEmpty())
            for(String i : nearest.split(","))
                this.nearestSubAreas.add(Short.parseShort(i));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Area getArea() {
        return area;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(byte alignment) {
        if (this.alignment == 1 && alignment == -1) BONTA--;
        else if (this.alignment == 2 && alignment == -1) BRAK--;
        else if (this.alignment == -1 && alignment == 1) BONTA++;
        else if (this.alignment == -1 && alignment == 2) BRAK++;
        this.alignment = alignment;
    }

    public Prism getPrism() {
        return prism;
    }

    public void setPrism(Prism prism) {
        this.prism = prism;
    }

    public boolean getConquerable() {
        return conquerable;
    }

    public void setConquerable(boolean conquerable) {
        this.conquerable = conquerable;
    }

    public List<GameMap> getMaps() {
        return maps;
    }

    public void addMap(GameMap Map) {
        this.maps.add(Map);
    }

    public boolean ownNearestSubArea(Player player) {
        for (short id : this.nearestSubAreas) {
            SubArea temp = World.world.getSubArea(id);
            if (temp != null && temp.getAlignment() == player.getAlignment())
                return true;
        }
        return false;
    }

    public boolean isMoreThanEnemies(Player player) {
        short bonta = 0, brak = 1;
        for(GameMap map : this.maps) {
            for (Player temp : map.getPlayers()) {
                if(!temp.isOnline()) continue;
                if (temp.getAlignment() == Constant.ALIGNEMENT_BONTARIEN) bonta++;
                else if (temp.getAlignment() == Constant.ALIGNEMENT_BRAKMARIEN) brak++;
            }
        }
        return player.getAlignment() == Constant.ALIGNEMENT_BRAKMARIEN ? brak > bonta : bonta > brak;
    }
}