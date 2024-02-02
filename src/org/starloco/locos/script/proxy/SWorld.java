package org.starloco.locos.script.proxy;

import org.classdump.luna.ByteString;
import org.classdump.luna.Table;
import org.classdump.luna.impl.DefaultTable;
import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.lib.ArgumentIterator;
import org.classdump.luna.runtime.LuaFunction;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Account;
import org.starloco.locos.player.Player;
import org.starloco.locos.game.world.World;
import org.starloco.locos.script.DataScriptVM;
import org.starloco.locos.script.types.MetaTables;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SWorld extends DefaultUserdata<World> {
    private static final ImmutableTable META_TABLE= MetaTables.MetaTable(MetaTables.ReflectIndexTable(SWorld.class));

    public SWorld(World userValue) {
        super(META_TABLE, userValue);
    }

    @SuppressWarnings("unused")
    private static Table datetime(World world) {
        ZonedDateTime zdt = ZonedDateTime.now();
        DefaultTable out = new DefaultTable();

        out.rawset("day", zdt.getDayOfMonth());
        out.rawset("month", zdt.getMonthValue());
        out.rawset("year", zdt.getYear());
        out.rawset("hour", zdt.getHour());
        out.rawset("min", zdt.getMinute());
        out.rawset("sec", zdt.getSecond());

        return out;
    }

    @SuppressWarnings("unused")
    private static long clock(World world) {
        return System.currentTimeMillis();
    }

    @SuppressWarnings("unused")
    private static SAccount account(World world, ArgumentIterator args) {
        Object arg = args.next();

        Account a;
        if(arg instanceof Long) {
            a = world.getAccount(((Long) arg).intValue());
        } else if(arg instanceof ByteString) {
            a = world.getAccountByPseudo(arg.toString());
        } else {
            throw new IllegalArgumentException("World:account param must be a number or a string");
        }

        return Optional.ofNullable(a).map(Account::scripted).orElse(null);
    }

    @SuppressWarnings("unused")
    private static SPlayer player(World world, ArgumentIterator args) {
        Object arg = args.next();

        Player p;
        if(arg instanceof Long) {
            p = world.getPlayer(((Long) arg).intValue());
        } else if(arg instanceof ByteString) {
            p = world.getPlayerByName(arg.toString());
        } else {
            throw new IllegalArgumentException("World:player param must be a number or a string");
        }

        return Optional.ofNullable(p).map(Player::scripted).orElse(null);
    }

    @SuppressWarnings("unused")
    private static SSubArea subArea(World world, ArgumentIterator args) {
        return Optional.ofNullable(world.getSubArea(args.nextInt())).map(SubArea::scripted).orElse(null);
    }

    @SuppressWarnings("unused")
    private static SMap map(World world, ArgumentIterator args) {
        return Optional.ofNullable(world.getMap(args.nextInt())).map(GameMap::scripted).orElse(null);
    }

    @SuppressWarnings("unused")
    private static void delayForMs(World world, ArgumentIterator args) {
        long delay = args.nextInteger();
        LuaFunction<?,?,?,?,?> fn = args.nextFunction();

        world.scheduler.schedule(() -> {
            DataScriptVM.getInstance().call(fn);
        }, delay, TimeUnit.MILLISECONDS);
    }
}
