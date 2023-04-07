package org.starloco.locos.entity.monster;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.game.world.World;
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
            int grade = p.second.get(Formulas.getRandomValue(0, p.second.size()));
            return World.world.getMonstre(p.first).getGrades().get(grade);
        }).collect(Collectors.toList());
    }
}