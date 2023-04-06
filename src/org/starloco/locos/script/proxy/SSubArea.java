package org.starloco.locos.script.proxy;

import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.script.types.MetaTables;

public class SSubArea extends DefaultUserdata<SubArea> {
    private static final ImmutableTable META_TABLE= MetaTables.MetaTable(MetaTables.ReflectIndexTable(SSubArea.class));

    public SSubArea(SubArea userValue) {
        super(META_TABLE, userValue);
    }

    @SuppressWarnings("unused")
    private static int id(SubArea sa) {
        return sa.getId();
    }

    @SuppressWarnings("unused")
    private static int faction(SubArea sa) {
        return sa.getAlignment();
    }

    @SuppressWarnings("unused")
    private static boolean conquerable(SubArea sa) { return sa.getConquerable(); }
}
