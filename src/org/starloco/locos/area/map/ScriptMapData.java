package org.starloco.locos.area.map;

import org.classdump.luna.Table;
import org.classdump.luna.runtime.LuaFunction;
import org.starloco.locos.client.Player;
import org.starloco.locos.entity.monster.MobGroupDef;
import org.starloco.locos.entity.monster.MonsterGrade;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.game.world.World;
import org.starloco.locos.script.DataScriptVM;
import org.starloco.locos.script.ScriptMapper;
import org.starloco.locos.script.ScriptVM;
import org.starloco.locos.script.proxy.SSubArea;
import org.starloco.locos.util.Pair;

import javax.swing.text.html.Option;
import javax.xml.crypto.Data;
import java.util.*;
import java.util.stream.Collectors;

import static org.starloco.locos.script.ScriptVM.*;

public class ScriptMapData extends MapData {
    private final Table scriptVal;
    public final Integer zaapCell;

    private ScriptMapData(Table scriptVal, int id, String date, String key, String cellsData, int width, int height, int x, int y, int subAreaID, int capabilities, int mobGroupsMaxCount, List<MonsterGrade> mobPossibles, String placesStr, int mobGroupsMaxSize, Integer zaapCell) {
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
        this.scriptVal = scriptVal;
        this.zaapCell = zaapCell;
    }

    public static ScriptMapData build(Table val) {
        Table lMobGrades = (Table)val.rawget("allowedMobGrades");
        List<MonsterGrade> allowedMonsters = listOfIntPairs(lMobGrades).stream()
            .map(p -> Optional.ofNullable(World.world.getMonstre(p.first))
                    .map(m -> m.getGrades().get(p.second))
                    .orElse(null)
            )
            .filter(Objects::nonNull).collect(Collectors.toList());

        Integer zaapCell = Optional.ofNullable(val.rawget("zaapCell")).map(o -> (Long)o).map(Long::intValue).orElse(null);

        return new ScriptMapData(
            val,
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
            rawInt(val, "mobGroupsCount"),
            allowedMonsters, val.rawget("positions").toString(), rawInt(val, "mobGroupsSize"),
            zaapCell
        );
    }

    @Override
    public Map<Integer, Pair<Integer, Integer>> getNPCs() {
        return ScriptVM.mapFromScript((Table)recursiveGet(scriptVal, "npcs"),
            o -> ((Long)o).intValue(),
            o -> {
                Table pair = (Table)o;
                return new Pair<>(rawInt(pair, 1L), rawInt(pair, 2L));
            }
        );
    }

    @Override
    public List<MobGroupDef> getStaticGroups() {
        ScriptMapper<MobGroupDef> mapper = MobGroupDef.Mapper.get();
        return ScriptVM.<Table>listFromLuaTable((Table)recursiveGet(scriptVal, "staticGroups")).stream()
            .map(Table.class::cast)
            .map(mapper::from)
            .collect(Collectors.toList());
    }

    @Override
    public void onMoveEnd(Player p) {
        Object tmp = recursiveGet(scriptVal,"onMovementEnd");
        if(!(tmp instanceof Table)) return;
        Object onMovementEndFn = ((Table)tmp).rawget(p.getCurCell().getId());
        if(!(onMovementEndFn instanceof LuaFunction)) return;
        DataScriptVM.getInstance().call(onMovementEndFn, scriptVal, p.getCurMap().scripted(), p.scripted());
    }

    @Override
    public boolean cellHasMoveEndActions(int cellId) {
        Object tmp = recursiveGet(scriptVal,"onMovementEnd");
        if(!(tmp instanceof Table)) return false;

        Object onMovementEndFn = ((Table)tmp).rawget(cellId);
        if(!(onMovementEndFn instanceof LuaFunction)) return false;

        return true;
    }

    private Optional<Object> onFightFunctionByType(int type, String name) {
        Object tmp = recursiveGet(scriptVal,name);
        if(!(tmp instanceof Table)) return Optional.empty();

        Object fn = ((Table)tmp).rawget(type);
        if(!(fn instanceof LuaFunction)) return Optional.empty();
        return Optional.of(fn);
    }

    @Override
    public void onFightInit(Fight f, Collection<Fighter> team0, Collection<Fighter> team1) {
        onFightFunctionByType(f.getType(), "onFightInit").ifPresent(fn -> {
            Table t0 = ScriptVM.scriptedValsTable(team0);
            Table t1 = ScriptVM.scriptedValsTable(team1);

            DataScriptVM.getInstance().call(fn, scriptVal, f.getMapOld().scripted(), t0, t1);
        });
    }
    @Override
    public void onFightStart(Fight f, Collection<Fighter> team0, Collection<Fighter> team1) {
        onFightFunctionByType(f.getType(), "onFightStart").ifPresent(fn -> {
            Table t0 = ScriptVM.scriptedValsTable(team0);
            Table t1 = ScriptVM.scriptedValsTable(team1);

            DataScriptVM.getInstance().call(fn, scriptVal, f.getMapOld().scripted(), t0, t1);
        });
    }

    @Override
    public void onFightEnd(Fight f, Player p, List<Fighter> winTeam, List<Fighter> looseTeam) {
        boolean isWinner = winTeam.stream().map(Fighter::getPlayer).filter(Objects::nonNull).anyMatch(fp -> fp.getId() == p.getId());

        Table winners = ScriptVM.scriptedValsTable(winTeam);
        Table losers = ScriptVM.scriptedValsTable(looseTeam);

        DataScriptVM vm = DataScriptVM.getInstance();
        onFightFunctionByType(f.getType(), "onFightEnd").ifPresent(fn -> {
            vm.call(fn, p.scripted(), isWinner, winners, losers);
        });
        vm.handlers.onFightEnd(p, f.getType(), isWinner, winners, losers);
    }

    @Override
    public boolean hasFightEndForType(int type) {
        return onFightFunctionByType(type, "onFightEnd").isPresent();
    }

    public Table scripted() {
        return scriptVal;
    }
}
