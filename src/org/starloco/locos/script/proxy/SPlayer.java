package org.starloco.locos.script.proxy;

import org.classdump.luna.ByteString;
import org.classdump.luna.Metatables;
import org.classdump.luna.Table;
import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.impl.NonsuspendableFunctionException;
import org.classdump.luna.lib.AbstractLibFunction;
import org.classdump.luna.lib.ArgumentIterator;
import org.classdump.luna.runtime.*;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.script.ScriptVM;

import java.util.List;

public class SPlayer extends DefaultUserdata<Player> {
    private static final ImmutableTable INDEX_TABLE = new ImmutableTable.Builder()
            // Calls
            .add("ask", new AskCall())
            .add("endDialog", new EndDialogCall())
            .add("teleport", new TeleportCall())
            .add("openBank", new OpenBankCall())
            .add("getItem", new GetItemCall())
            .add("consumeItem", new ConsumeItemCall())
            .build();
    private static final ImmutableTable META_TABLE = new ImmutableTable.Builder()
            .add(Metatables.MT_INDEX, INDEX_TABLE)
            .build();

    public SPlayer(Player userValue) {
        super(META_TABLE, userValue);
    }

    private static class AskCall extends AbstractLibFunction {
        @Override
        protected String name() { return "player:ask"; }

        @Override
        protected void invoke(ExecutionContext context, ArgumentIterator args) throws ResolvedControlThrowable {
            Player p = args.nextUserdata("SPlayer", SPlayer.class).getUserValue();
            int question = args.nextInt();
            List<Integer> answersInts = ScriptVM.intsFromLuaTable(args.nextOptionalTable(null));
            ByteString param = args.nextOptionalString(null);


            String paramVal = param == null ? null : p.getStringVar(param.toString());
            SocketManager.GAME_SEND_QUESTION_PACKET(p.getGameClient(), question, answersInts, paramVal);

            context.getReturnBuffer().setTo();
        }
    }

    private static class EndDialogCall extends AbstractLibFunction {
        @Override
        protected String name() { return "player:endDialog"; }

        @Override
        public void invoke(ExecutionContext context, ArgumentIterator args) {
            Player p = args.nextUserdata("SPlayer", SPlayer.class).getUserValue();
            if (p.getExchangeAction() == null || p.getExchangeAction().getType() != ExchangeAction.TALKING_WITH ){
                return;
            }
            p.setAway(false);
            p.setExchangeAction(null);
            SocketManager.GAME_SEND_END_DIALOG_PACKET(p.getGameClient());

            context.getReturnBuffer().setTo();
        }
    }

    private static class OpenBankCall extends AbstractFunction1<SPlayer> {
        @Override
        public void invoke(ExecutionContext context, SPlayer player) {
           player.getUserValue().openBank();

            context.getReturnBuffer().setTo();
        }
        @Override
        public void resume(ExecutionContext context, Object suspendedState) { throw new NonsuspendableFunctionException(); }
    }

    private static class TeleportCall extends AbstractFunction3<SPlayer, Long, Long> {
        @Override
        public void invoke(ExecutionContext context, SPlayer player, Long lMapID, Long lCellID) {
            player.getUserValue().teleport(lMapID.shortValue(), lCellID.intValue());

            context.getReturnBuffer().setTo();
        }
        @Override
        public void resume(ExecutionContext context, Object suspendedState) { throw new NonsuspendableFunctionException(); }
    }

    private static class GetItemCall extends AbstractFunction2<SPlayer, Long> {
        @Override
        public void invoke(ExecutionContext context, SPlayer player, Long lItemID) {
            GameObject item = player.getUserValue().getItemTemplate(lItemID.intValue(), 1);
            if(item == null) {
                // No item return null
                context.getReturnBuffer().setTo();
                return;
            }
            context.getReturnBuffer().setTo(item.Scripted());
        }
        @Override
        public void resume(ExecutionContext context, Object suspendedState) { throw new NonsuspendableFunctionException(); }
    }

    private static class ConsumeItemCall extends AbstractFunction3<SPlayer, Long, Long> {
        @Override
        public void invoke(ExecutionContext context, SPlayer player, Long lItemID, Long lQuantity) {
            boolean consumed = player.getUserValue().removeByTemplateID(lItemID.intValue(), lQuantity.intValue());

            context.getReturnBuffer().setTo(consumed);
        }
        @Override
        public void resume(ExecutionContext context, Object suspendedState) { throw new NonsuspendableFunctionException(); }
    }
}
