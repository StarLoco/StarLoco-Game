package org.starloco.locos.script;

import org.classdump.luna.Table;
import org.classdump.luna.impl.DefaultTable;
import org.classdump.luna.runtime.LuaFunction;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.quest.QuestInfo;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public boolean onCraft(Player player, int skillID, Map<Integer,Integer> ingredients) {
        Table lIngredients = DataScriptVM.listOf(ingredients.entrySet().stream()
            .map(e ->
                player.getItems().get(e.getKey()).getItemView(e.getValue()).scripted()
            )
        );

        Object[] ret = vm.call(getHandler(players, "onCraft"), player.scripted(), skillID,  lIngredients);
        if(ret[0] == null) {
            // Craft failed ?
            return false;
        }
        int itemID = ((Long)ret[0]).intValue();

        GameObject item = World.world.getObjTemplate(itemID).createNewItem(1, false);
        if(player.addItem(item, true,false)) {
            World.world.addGameObject(item);
        } else {
            // Player had a similar item, should we use this GUID ?
        }

        String emkParams = String.join("|",
            String.valueOf(item.getGuid()),
            String.valueOf(item.getQuantity()),
            String.valueOf(itemID),
            item.encodeStats()
        );
        SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(player, 'O', "+", emkParams);
        SocketManager.GAME_SEND_Ec_PACKET(player, "K;"+itemID);
        SocketManager.GAME_SEND_IO_PACKET_TO_MAP(player.getCurMap(), player.getId(), "+"+itemID);

        return true;
    }
}
