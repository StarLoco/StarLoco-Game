package org.starloco.locos.script.proxy;

import org.classdump.luna.ByteString;
import org.classdump.luna.Metatables;
import org.classdump.luna.Table;
import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.impl.NonsuspendableFunctionException;
import org.classdump.luna.runtime.*;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.script.ScriptVM;

import java.util.List;

public class SPlayer extends DefaultUserdata<Player> {
    private static final ImmutableTable INDEX_TABLE = new ImmutableTable.Builder()
            // Calls
            .add("ask", new AskCall())
            .add("endDialog", new EndDialogCall())
            .add("openBank", new OpenBankCall())
            .build();
    private static final ImmutableTable META_TABLE = new ImmutableTable.Builder()
            .add(Metatables.MT_INDEX, INDEX_TABLE)
            .build();

    public SPlayer(Player userValue) {
        super(META_TABLE, userValue);
    }

    private static class AskCall extends AbstractFunction4<SPlayer, Long, Table, ByteString> {
        @Override
        public void invoke(ExecutionContext context, SPlayer player, Long question, Table answers, ByteString param) {
            Player p = player.getUserValue();
            List<Integer> answersInts = ScriptVM.intsFromLuaTable(answers);
            String paramVal = param == null ? null : p.getStringVar(param.toString());
            SocketManager.GAME_SEND_QUESTION_PACKET(p.getGameClient(), Math.toIntExact(question), answersInts, paramVal);

            context.getReturnBuffer().setTo();
        }
        @Override
        public void resume(ExecutionContext context, Object suspendedState) { throw new NonsuspendableFunctionException();  }
    }

    private static class EndDialogCall extends AbstractFunction1<SPlayer> {
        @Override
        public void invoke(ExecutionContext context, SPlayer player) {
            Player p = player.getUserValue();
            if (p.getExchangeAction() == null || p.getExchangeAction().getType() != ExchangeAction.TALKING_WITH ){
                return;
            }
            p.setAway(false);
            p.setExchangeAction(null);
            SocketManager.GAME_SEND_END_DIALOG_PACKET(p.getGameClient());

            context.getReturnBuffer().setTo();
        }
        @Override
        public void resume(ExecutionContext context, Object suspendedState) { throw new NonsuspendableFunctionException(); }
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
}
