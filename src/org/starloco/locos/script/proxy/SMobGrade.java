package org.starloco.locos.script.proxy;

import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.starloco.locos.entity.monster.MonsterGrade;
import org.starloco.locos.script.types.MetaTables;

public class SMobGrade extends DefaultUserdata<MonsterGrade> {
    private static final ImmutableTable META_TABLE= MetaTables.MetaTable(MetaTables.ReflectIndexTable(SMobGrade.class));

    public SMobGrade(MonsterGrade userValue) {
        super(META_TABLE, userValue);
    }

    @SuppressWarnings("unused")
    private static int id(MonsterGrade p) {
        return p.getTemplate().getId();
    }

    @SuppressWarnings("unused")
    private static int grade(MonsterGrade p) {
        return p.getGrade();
    }

    @SuppressWarnings("unused")
    private static int level(MonsterGrade p) {
        return p.getLevel();
    }
}
