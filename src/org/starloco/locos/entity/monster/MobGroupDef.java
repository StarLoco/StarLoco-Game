package org.starloco.locos.entity.monster;
import org.starloco.locos.util.Pair;
import java.util.List;

public class MobGroupDef {
    public final int cellId;
    public final List<Pair<Integer,Integer>> mobGrades;

    public MobGroupDef(int cellId, List<Pair<Integer, Integer>> mobGrades) {
        this.cellId = cellId;
        this.mobGrades = mobGrades;
    }
}