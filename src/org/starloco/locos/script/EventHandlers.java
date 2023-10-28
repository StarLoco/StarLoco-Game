package org.starloco.locos.script;

import org.classdump.luna.Table;
import org.classdump.luna.impl.DefaultTable;
import org.classdump.luna.runtime.LuaFunction;
import org.starloco.locos.client.Player;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.quest.QuestInfo;

import java.util.List;
import java.util.Map;

import static org.starloco.locos.script.ScriptVM.mapToTable;

public class EventHandlers extends DefaultTable {
    private final DataScriptVM vm;
    private final DefaultTable players = new DefaultTable();

    public EventHandlers(DataScriptVM vm){
        this.vm = vm;
        this.rawset("players", players);
    }


    private LuaFunction<?,?,?,?,?> getHandler(Table t, String name) {
        Object mbFn = t.rawget(name);
        if(!(mbFn instanceof LuaFunction)) throw new IllegalArgumentException("event handler is not a function");
        return (LuaFunction<?,?,?,?,?>)mbFn;
    }

    public void onDialog(Player player, int npcID, int answer) {
        vm.call(getHandler(players, "onDialog"), player.scripted(), npcID, answer);
    }

    public void onMapEnter(Player player) {
        vm.call(getHandler(players, "onMapEnter"), player.scripted());
    }

    public void onSkillUse(Player player, int cellID, int skillID) {
        vm.call(getHandler(players, "onSkillUse"), player.scripted(), cellID, skillID);
    }

    public void onFightEnd(Player player, int type, boolean isWinner, List<Fighter> winners, List<Fighter> losers) {
        Table tWinners = ScriptVM.scriptedValsTable(winners);
        Table tLosers = ScriptVM.scriptedValsTable(losers);

        vm.call(getHandler(players, "onFightEnd"), player.scripted(), type, isWinner, tWinners, tLosers);
    }

    public QuestInfo questInfo(Player player, int id, int currentStep) {
        Object[] ret = vm.call(getHandler(players, "onQuestStatusRequest"), player.scripted(), id, currentStep);
        if(ret == null || ret.length == 0 || !(ret[0] instanceof Table)) return null;
        Table t = (Table)ret[0];

        return new QuestInfo(
            ScriptVM.intsFromLuaTable((Table)(t.rawget("objectives"))),
            ScriptVM.rawInteger(t, "previous"),
            ScriptVM.rawInteger(t, "next"),
            ScriptVM.rawInteger(t, "question"),
            (boolean) t.rawget("isAccount"),
            (boolean) t.rawget("isRepeatable")
        );
    }

    public void onDocQuestHref(Player player, int docID, int questID) {
        vm.call(getHandler(players, "onDocQuestHref"), player.scripted(), docID, questID);
    }

    public void onCraft(Player player, int skillID, Map<Integer,Integer> ingredients) {
        vm.call(getHandler(players, "onCraft"), player.scripted(), skillID,  mapToTable(ingredients));
    }
}
