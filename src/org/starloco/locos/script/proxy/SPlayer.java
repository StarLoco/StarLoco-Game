package org.starloco.locos.script.proxy;

import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.impl.NonsuspendableFunctionException;
import org.classdump.luna.runtime.*;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.game.action.ExchangeAction;

public class SPlayer extends DefaultUserdata<Player> {
    private static final ImmutableTable META_TABLE = new ImmutableTable.Builder()
            .add("ask", new AskCall())
            .add("endDialog", new EndDialogCall())
            .build();

    public SPlayer(Player userValue) {
        super(META_TABLE, userValue);
    }

    private static class AskCall extends AbstractFunction3<SPlayer, Integer, int[]> {
        @Override
        public void invoke(ExecutionContext context, SPlayer player, Integer question, int[] answers) {
            Player p = player.getUserValue();
            SocketManager.GAME_SEND_QUESTION_PACKET(p.getGameClient(), question, answers, ""); // FIXME ARGS
        }
        @Override
        public void resume(ExecutionContext context, Object suspendedState) {
            throw new NonsuspendableFunctionException();
        }
    }
    private static class EndDialogCall extends AbstractFunction1<SPlayer> {
        @Override
        public void invoke(ExecutionContext context, SPlayer player) {
            Player p = player.getUserValue();
            if (p.getExchangeAction() == null || p.getExchangeAction().getType() != ExchangeAction.TALKING_WITH){
                return;
            }
            p.setAway(false);
            p.setExchangeAction(null);
            SocketManager.GAME_SEND_END_DIALOG_PACKET(p.getGameClient());
        }
        @Override
        public void resume(ExecutionContext context, Object suspendedState) {
            throw new NonsuspendableFunctionException();
        }
    }
}
