package org.starloco.locos.script.proxy;

import org.classdump.luna.Table;
import org.classdump.luna.impl.DefaultTable;
import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.lib.ArgumentIterator;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.entity.monster.MobGroupDef;
import org.starloco.locos.entity.monster.MonsterGrade;
import org.starloco.locos.entity.monster.MonsterGroup;
import org.starloco.locos.script.ScriptVM;
import org.starloco.locos.script.types.MetaTables;

import java.util.*;

public class SMap extends DefaultUserdata<GameMap> {
    private static final ImmutableTable META_TABLE= MetaTables.MetaTable(MetaTables.ReflectIndexTable(SMap.class));

    public SMap(GameMap userValue) { super(META_TABLE, userValue); }

    @SuppressWarnings("unused")
    private static int id(GameMap m) {
        return m.getId();
    }

    @SuppressWarnings("unused")
    private static SSubArea subArea(GameMap m) {
        return m.getSubArea().scripted();
    }

    @SuppressWarnings("unused")
    private static Table cellPlayers(GameMap m, ArgumentIterator args) {
        int cellID = args.nextInt();
        if(m.getCase(cellID) == null) return DefaultTable.factory().newTable();
        return ScriptVM.scriptedValsTable(m.getCase(cellID).getPlayers().stream());
    }

    @SuppressWarnings("unused")
    private static Table mobGroupById(GameMap m, ArgumentIterator args) {
        int id = args.nextInt();
        return Optional.ofNullable(m.getMobGroups().get(id)).map(MonsterGroup::getMobs)
            .map(Map::values)
            .map(Collection::stream)
            .map(s -> s.map(MonsterGrade::scripted))
            .map(ScriptVM::listOf)
            .orElse(null);
    }

    @SuppressWarnings("unused")
    private static Table mobGroups(GameMap m) {
        return ScriptVM.listOf(m.getMobGroups().values().stream()
            .map(MonsterGroup::getMobs)
            .map(Map::values)
            .map(Collection::stream)
            .map(s -> s.map(MonsterGrade::scripted))
            .map(ScriptVM::listOf)
        );
    }

    @SuppressWarnings("unused")
    private static int spawnGroupDef(GameMap m, ArgumentIterator args) {
        MobGroupDef def = MobGroupDef.Mapper.get().from(args.nextTable());

        return m.spawnMobGroup(def, true);
    }
}
