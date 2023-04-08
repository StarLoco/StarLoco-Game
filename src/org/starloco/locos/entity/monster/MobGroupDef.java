package org.starloco.locos.entity.monster;
import org.classdump.luna.Table;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.game.world.World;
import org.starloco.locos.script.ScriptMapper;
import org.starloco.locos.script.ScriptVM;
import org.starloco.locos.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class MobGroupDef {
    public final int cellId;
    public final List<Pair<Integer, List<Integer>>> gradesForMobs;

    public MobGroupDef(int cellId, List<Pair<Integer, List<Integer>>> gradesForMobs) {
        this.cellId = cellId;
        this.gradesForMobs = gradesForMobs;
    }

    public List<MonsterGrade> randomize() {
        return gradesForMobs.stream().map(p -> {
            int grade = p.second.get(Formulas.getRandomValue(0, p.second.size()-1));
            return World.world.getMonstre(p.first).getGrades().get(grade);
        }).collect(Collectors.toList());
    }


    public static class Mapper implements ScriptMapper<MobGroupDef> {
        private static final Mapper INSTANCE = new Mapper();
        public static Mapper get() { return INSTANCE; }

        @Override
        public MobGroupDef from(Object o) {
            if(!(o instanceof Table)) throw new IllegalArgumentException("MobGroupDef must be a Table");
            Table t = (Table)o;
            int cellId = ScriptVM.rawInt(t, 1L);
            Table def = (Table) t.rawget(2L);

            return new MobGroupDef(cellId, ScriptVM.<Table>listFromLuaTable((Table)def)
                .stream()
                .map(ScriptVM::<Long,Table>toPair)
                .map(p -> new Pair<>(p.first.intValue(), ScriptVM.intsFromLuaTable(p.second)))
                .collect(Collectors.toList())
            );
        }

        @Override
        public Object to(MobGroupDef v) {
            throw new UnsupportedOperationException("MobGroupDef cannot be converted to script");
        }
    }
}