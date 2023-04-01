package org.starloco.locos.area.map;

import org.classdump.luna.Table;
import org.starloco.locos.client.Player;
import org.starloco.locos.entity.monster.MonsterGrade;
import org.starloco.locos.game.world.World;
import org.starloco.locos.script.ScriptVM;
import org.starloco.locos.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static org.starloco.locos.script.ScriptVM.*;

public class ScriptMapData extends MapData {

    private ScriptMapData(int id, String date, String key, String cellsData, int width, int height, int x, int y, int subAreaID, int capabilities, Map<Integer, Pair<Integer,Integer>> npcs, int mobGroupsMaxCount, int mobGroupsMaxSize, List<MonsterGrade> mobPossibles, String placesStr) {
        super(id,
            date,
            key,
            cellsData,
            width,
            height,
            x,
            y,
            subAreaID,
            (capabilities & 0x80) > 0,
            (capabilities & 0x100) > 0,
            (capabilities & 0x200) > 0,
            (capabilities & 0x8) > 0,
            (capabilities & 0x1) > 0,
            (capabilities & 0x2) > 0,
            (capabilities & 0x80) > 0,
            mobGroupsMaxCount,
            mobGroupsMaxSize,
            mobPossibles,
            placesStr);

        npcs.forEach((k,v) -> addNpc(k, v.first, v.second));
    }

    public static ScriptMapData build(Table val) {
        Table lMobGrades = (Table)val.rawget("allowedMobGrades");
        List<MonsterGrade> allowedMonsters = listOfIntPairs(lMobGrades).stream()
            .map(p -> Optional.ofNullable(World.world.getMonstre(p.first)).map(m -> m.getGrades().get(p.second)).orElse(null))
            .filter(Objects::nonNull).collect(Collectors.toList());

        Map<Integer, Pair<Integer,Integer>> npcs = ScriptVM.mapFromScript((Table)val.rawget("npcs"),
                o -> ((Long)o).intValue(),
                o -> {
                    Table pair = (Table)o;
                    return new Pair<>(rawInt(pair, 1L), rawInt(pair, 2L));
                }
        );

        return new ScriptMapData(
                rawInt(val, "id"),
            val.rawget("date").toString(),
            val.rawget("key").toString(),
            val.rawget("cellsData").toString(),
            rawInt(val, "width"),
            rawInt(val, "height"),
            rawInt(val, "x"),
            rawInt(val, "y"),
            rawInt(val, "subAreaId"),
            rawInt(val, "capabilities"),
            npcs,
            rawInt(val, "mobGroupsCount"),
            rawInt(val, "mobGroupsSize"),
            allowedMonsters,
            val.rawget("positions").toString()
        );
    }

    @Override
    public void onMoveEnd(Player p) {

    }
}
