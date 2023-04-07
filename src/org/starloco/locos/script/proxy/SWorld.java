package org.starloco.locos.script.proxy;

import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.lib.ArgumentIterator;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.game.world.World;
import org.starloco.locos.script.types.MetaTables;

import java.util.Optional;

public class SWorld extends DefaultUserdata<World> {
    private static final ImmutableTable META_TABLE= MetaTables.MetaTable(MetaTables.ReflectIndexTable(SWorld.class));

    public SWorld(World userValue) {
        super(META_TABLE, userValue);
    }

    @SuppressWarnings("unused")
    private static SSubArea subArea(World world, ArgumentIterator args) {
        return Optional.ofNullable(world.getSubArea(args.nextInt())).map(SubArea::scripted).orElse(null);
    }

    @SuppressWarnings("unused")
    private static SMap map(World world, ArgumentIterator args) {
        return Optional.ofNullable(world.getMap(args.nextInt())).map(GameMap::scripted).orElse(null);
    }
}