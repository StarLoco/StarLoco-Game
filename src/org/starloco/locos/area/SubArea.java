package org.starloco.locos.area;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.player.Player;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.script.Scripted;
import org.starloco.locos.script.proxy.SSubArea;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class SubArea implements Scripted<SSubArea> {
    private final SSubArea scriptVal;
    private final int id;
    private final String name;
    private final Area area;
    private int alignment;
    private Prism prism;
    private boolean conquerable;

    private final HashSet<Integer> mapIDs = new HashSet<>();
    private final List<Short> nearestSubAreas = new ArrayList<>();

    public SubArea(int id, String name, int area, String nearest) {
        this.scriptVal = new SSubArea(this);
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

    public void setAlignment(int alignment) {
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
        return mapIDs.stream().map(World.world::getMap).collect(Collectors.toList());
    }

    public void addMapID(int mapID) {
        this.mapIDs.add(mapID);
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

        for(GameMap map : getMaps()) {
            for (Player temp : map.getPlayers()) {
                if(!temp.isOnline()) continue;
                if (temp.getAlignment() == Constant.ALIGNEMENT_BONTARIEN) bonta++;
                else if (temp.getAlignment() == Constant.ALIGNEMENT_BRAKMARIEN) brak++;
            }
        }
        return player.getAlignment() == Constant.ALIGNEMENT_BRAKMARIEN ? brak > bonta : bonta > brak;
    }

    @Override
    public SSubArea scripted() {
        return scriptVal;
    }
}