package org.starloco.locos.area.map;

import org.starloco.locos.area.Area;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.entity.InteractiveObject;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.CryptManager;
import org.starloco.locos.entity.monster.MobGroupDef;
import org.starloco.locos.entity.monster.MonsterGrade;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.game.world.World;
import org.starloco.locos.util.Pair;


import java.util.*;

// Holds all static data for maps
public abstract class MapData implements CellsDataProvider {
    // MapData must only contain final fields. It's a READ ONLY class
    public final int id;
    public final String date;
    public final String key;
    public final int width, height;
    public final int x, y;
    public final int subAreaID;

    public final boolean noSellers, noCollectors, noPrisms, noTp, noDefy, noAgro, noCanal;
    public final int mobGroupsMaxCount;
    public final int mobGroupsMinSize;
    public final int mobGroupsMaxSize;

    // Temporary variable to be able to copy the map.
    // Eventually, we should split GameCase from CellData, or use neither of those
    public final CellsDataProvider.RawCellsDataProvider cellsData;
    public final List<MonsterGrade> mobPossibles;
    public final String placesStr;
    private final HashMap<Integer, InteractiveObject.InteractiveObjectTemplate> interactiveObjects = new HashMap<>();


    protected MapData(int id, String date, String key, String cellsData, int width, int height, int x, int y, int subAreaID, boolean noSellers, boolean noCollectors, boolean noPrisms, boolean noTp, boolean noDefy, boolean noAgro, boolean noCanal, int mobGroupsMaxCount, int mobGroupsMinSize, int mobGroupsMaxSize, List<MonsterGrade> mobPossibles, String placesStr) {
        CryptManager cMgr = World.world.getCryptManager();

        String data = cellsData;
        if(cMgr.isMapCiphered(data)) {
            try {
                data = cMgr.decryptMapData(cellsData, key);
            } catch (Exception e) {
                throw new RuntimeException("Cannot decipher mapdata #"+id,e);
            }
        }
        // Decode b64
        byte[] dataBytes = new byte[data.length()];
        for (int i = 0; i < data.length(); i++) {
            dataBytes[i] = (byte) cMgr.getIntByHashedValue(data.charAt(i));
        }


        this.id = id;
        this.date = date;
        this.key = key;
        this.cellsData = new CellsDataProvider.RawCellsDataProvider(dataBytes);
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
        this.mobGroupsMinSize= mobGroupsMinSize;
        this.mobGroupsMaxSize = mobGroupsMaxSize;
        this.mobPossibles = mobPossibles;
        this.placesStr = placesStr;
    }

    public SubArea getSubArea() { return World.world.getSubArea(subAreaID); }
    public Area getArea() { return getSubArea().getArea(); }

    // TODO: Replace with Pair<List<Integer>,List<Integer>>
    public String getPlaces() { return placesStr; }

    public abstract Map<Integer, Pair<Integer, Integer>> getNPCs();


    public String getForbidden() {
        return (noSellers ? 1 : 0) + ";" + (noCollectors ? 1 : 0) + ";" + (noPrisms ? 1 : 0) + ";" + (noTp ? 1 : 0) +
                ";" + (noDefy ? 1 : 0) + ";" + (noAgro ? 1 : 0) + ";" + (noCanal ? 1 : 0);
    }
    public abstract List<MobGroupDef> getStaticGroups();

    public abstract void onMoveEnd(Player p);
    public abstract boolean cellHasMoveEndActions(int cellId);


    public void onFightInit(Fight f, Collection<Fighter> team0, Collection<Fighter> team1) {}
    public void onFightStart(Fight f, Collection<Fighter> team0, Collection<Fighter> team1) {}

    public abstract boolean hasFightEndForType(int type);
    public abstract void onFightEnd(Fight f, Player p, List<Fighter> winTeam, List<Fighter> looseTeam);

    public int cellCount() {
        return width * height + (width-1) * (height-1);
    }
    public Map<Integer, InteractiveObject.InteractiveObjectTemplate> interactiveObjects() { return Collections.unmodifiableMap(interactiveObjects); }

    public long cellData(int cellID) {
        return cellsData.cellData(cellID);
    }
    public int overrideMask(int cellID) {
        return cellsData.overrideMask(cellID);
    }
}
