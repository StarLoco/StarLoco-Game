package org.starloco.locos.script.proxy;

import org.classdump.luna.ByteString;
import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.lib.ArgumentIterator;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.script.ScriptVM;
import org.starloco.locos.script.types.MetaTables;

import java.util.List;

public class SPlayer extends DefaultUserdata<Player> {
    private static final ImmutableTable META_TABLE= MetaTables.MetaTable(MetaTables.ReflectIndexTable(SPlayer.class));

    public SPlayer(Player userValue) {
        super(META_TABLE, userValue);
    }

    @SuppressWarnings("unused")
    private static void ask(Player p, ArgumentIterator args) {
        int question = args.nextInt();
        List<Integer> answersInts = ScriptVM.intsFromLuaTable(args.nextOptionalTable(null));
        ByteString param = args.nextOptionalString(null);


        String paramVal = param == null ? null : p.getStringVar(param.toString());
        SocketManager.GAME_SEND_QUESTION_PACKET(p.getGameClient(), question, answersInts, paramVal);
    }

    @SuppressWarnings("unused")
    private static void endDialog(Player p) {
        if (p.getExchangeAction() == null || p.getExchangeAction().getType() != ExchangeAction.TALKING_WITH ){
            return;
        }
        p.setAway(false);
        p.setExchangeAction(null);
        SocketManager.GAME_SEND_END_DIALOG_PACKET(p.getGameClient());
    }

    @SuppressWarnings("unused")
    private static void teleport(Player p, ArgumentIterator args) {
        short mapID = (short)args.nextInt();
        int cellID = args.nextInt();
        p.teleport(mapID, cellID);
    }

    @SuppressWarnings("unused")
    private static void openBank(Player p) {
        p.openBank();
    }

    @SuppressWarnings("unused")
    private static SItem getItem(Player p, ArgumentIterator args) {
        int itemID = args.nextInt();
        int quantity = args.nextInt();
        GameObject item = p.getItemTemplate(itemID, quantity);
        if(item == null) {
            // No item return null
            return null;
        }
        return item.Scripted();
    }

    @SuppressWarnings("unused")
    private static boolean consumeItem(Player p, ArgumentIterator args) {
        int itemID = args.nextInt();
        int quantity = args.nextInt();
        return p.removeItemByTemplateId(itemID, quantity, true);
    }

    @SuppressWarnings("unused")
    private static long kamas(Player p) {
        return p.getKamas();
    }

    @SuppressWarnings("unused")
    private static boolean modKamas(Player p, ArgumentIterator args) {
        int quantity = args.nextInt();
        return p.modKamasDisplay(quantity);
    }

    @SuppressWarnings("unused")
    private static void addItem(Player p, ArgumentIterator args) {
        int itemID = args.nextInt();
        int quantity = args.nextInt();
        boolean isPerfect = args.nextOptionalBoolean(true);
        boolean display = args.nextOptionalBoolean(true);

        p.addItem(itemID, quantity, isPerfect, display);
    }

    @SuppressWarnings("unused")
    private static boolean tryBuyItem(Player p, ArgumentIterator args) {
        int itemID = args.nextInt();
        int unitPrice = args.nextInt();
        int quantity = args.nextOptionalInt(1);
        boolean isPerfect = args.nextOptionalBoolean(true);


        int totalPrice = unitPrice * quantity;
        if(!p.modKamasDisplay(-totalPrice)) return false;

        p.addItem(itemID, quantity, isPerfect, true);
        return true;
    }

    @SuppressWarnings("unused")
    private static int mapID(Player p, ArgumentIterator args) {
        //  TODO: Replace with map():SMap
        return p.getCurMap().getId();
    }

    @SuppressWarnings("unused")
    private static void sendAction(Player p, ArgumentIterator args) {
        int actionID = args.nextInt();
        String actionIDStr = "";
        if(actionID != -1) actionIDStr = String.valueOf(actionID);

        int actionType = args.nextInt();
        ByteString actionValue = args.nextString();

        SocketManager.GAME_SEND_GA_PACKET(p.getGameClient(), actionIDStr, String.valueOf(actionType),  String.valueOf(p.getId()), actionValue.toString());
    }
}
