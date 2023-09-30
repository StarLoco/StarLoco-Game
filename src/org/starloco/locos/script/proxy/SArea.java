package org.starloco.locos.script.proxy;

import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.starloco.locos.area.Area;
import org.starloco.locos.script.types.MetaTables;

public class SArea extends DefaultUserdata<Area> {
    private static final ImmutableTable META_TABLE= MetaTables.MetaTable(MetaTables.ReflectIndexTable(SArea.class));

    public SArea(Area userValue) {
        super(META_TABLE, userValue);
    }

    @SuppressWarnings("unused")
    private static int id(Area a) {
        return a.getId();
    }
}
