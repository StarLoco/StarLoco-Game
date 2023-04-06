package org.starloco.locos.area.map;

import org.starloco.locos.area.Area;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.client.Player;
import org.starloco.locos.entity.monster.MonsterGrade;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.game.world.World;
import org.starloco.locos.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Holds all static data for maps
public abstract class MapData {
    // MapData must only contain final fields. It's a READ ONLY class
    public final int id;
    public final String date;
    public final String key;
    public final int width, height;
    public final int x, y;
    public final int subAreaID;

    public final boolean noSellers, noCollectors, noPrisms, noTp, noDefy, noAgro, noCanal;
    public final int mobGroupsMaxCount;
    public final int mobGroupsMaxSize;

    // Temporary variable to be able to copy the map.
    // Eventually, we should split GameCase from CellData, or use neither of those
    public final String cellsData;
    public final List<MonsterGrade> mobPossibles;
    public final String placesStr;

    private final Map<Integer, Pair<Integer,Integer>> npcs = new HashMap<>();

    protected MapData(int id, String date, String key, String cellsData, int width, int height, int x, int y, int subAreaID, boolean noSellers, boolean noCollectors, boolean noPrisms, boolean noTp, boolean noDefy, boolean noAgro, boolean noCanal, int mobGroupsMaxCount, int mobGroupsMaxSize, List<MonsterGrade> mobPossibles, String placesStr) {
        this.id = id;
        this.date = date;
        this.key = key;
        this.cellsData = cellsData;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.subAreaID = subAreaID;
        this.noSellers = noSellers;
        this.noCollectors = noCollectors;
        this.noPrisms = noPrisms;
        this.noTp = noTp;
        this.noDefy = noDefy;
        this.noAgro = noAgro;
        this.noCanal = noCanal;
        this.mobGroupsMaxCount = mobGroupsMaxCount;
        this.mobGroupsMaxSize = mobGroupsMaxSize;
        this.mobPossibles = mobPossibles;
        this.placesStr = placesStr;
    }

    public SubArea getSubArea() { return World.world.getSubArea(subAreaID); }
    public Area getArea() { return getSubArea().getArea(); }

    // TODO: Replace with Pair<List<Integer>,List<Integer>>
    public String getPlaces() { return placesStr; }

    public Map<Integer, Pair<Integer, Integer>> getNPCs() { return npcs; };

    public void addNpc(int id, int cellid, int orientation) {
        this.npcs.put(id, new Pair<>(cellid, orientation));
    }

    public String getForbidden() {
        return (noSellers ? 1 : 0) + ";" + (noCollectors ? 1 : 0) + ";" + (noPrisms ? 1 : 0) + ";" + (noTp ? 1 : 0) +
                ";" + (noDefy ? 1 : 0) + ";" + (noAgro ? 1 : 0) + ";" + (noCanal ? 1 : 0);
    }

    public abstract void onMoveEnd(Player p);
    public abstract boolean cellHasMoveEndActions(int cellId);

    public abstract boolean hasFightEndForType(int type);
    public abstract void onFightEnd(Fight f, Player p, List<Fighter> winTeam, List<Fighter> looseTeam);
}
