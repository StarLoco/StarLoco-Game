package org.starloco.locos.script;

import org.classdump.luna.Table;
import org.classdump.luna.impl.DefaultTable;
import org.classdump.luna.runtime.LuaFunction;
import org.starloco.locos.client.Player;
import org.starloco.locos.script.proxy.SPlayer;

public final class EventHandlers extends DefaultTable {
    private final DataScriptVM vm;
    private final DefaultTable players = new DefaultTable();
    private final DefaultTable maps = new DefaultTable();

    public EventHandlers( DataScriptVM vm) {
        this.vm = vm;
        rawset("players", players);
        rawset("maps", maps);
    }

    private LuaFunction<?,?,?,?,?> getHandler(Table t, String name) {
        return (LuaFunction<?,?,?,?,?>)t.rawget(name);
    }

    public void onFightEnd(SPlayer p, int type, boolean isWinner, Table winners, Table losers) {
        vm.call(getHandler(players, "onFightEnd"), p, type, isWinner, winners, losers);
    }
}
