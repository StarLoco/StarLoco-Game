package org.starloco.locos.client.other;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.kernel.Constant;

public class Stalk {
    private long time;
    private Player target;

    public Stalk(long time, Player p) {
        this.time = time;
        this.target = p;
    }

    public Player getTarget() {
        return this.target;
    }

    public void setTarget(Player p) {
        this.target = p;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long t) {
        this.time = t;
    }

    public boolean onPlayerTryToFight(Player player, Player target) {
        if(this.target == null || target == null || this.target.getId() != target.getId())
            return false;
        if (player.getFight() != null|| target.getFight() != null || target.getCurMap().getId() != player.getCurMap().getId() || target.isDead() == 1 || player.isDead() == 1)
            return false;
        if(!player.canAggro() || !target.canAggro()) {
            player.sendTypeMessage("Stalk", player.canAggro() ? player.getLang().trans("game.battle.mode.game.dubg.battleground.canaggro.true") : player.getLang().trans("game.battle.mode.game.dubg.battleground.canaggro.false"));
            return false;
        }

        boolean canDefy = PathFinding.canWalkToThisCell(player.getCurMap(), player.getCurCell().getId(), target.getCurCell().getId(), true);

        if(canDefy && player.getCurCell().getId() != target.getCurCell().getId()) {
            if(player.getGameClient() != null)
                player.getGameClient().clearAllPanels(target);

            player.getCurMap().newFight(target, player, Constant.FIGHT_TYPE_AGRESSION);
            player.setAway(false);target.setAway(false);
            SocketManager.send(player, "ILF0");SocketManager.send(target, "ILF0");
            return true;
        }
        return false;
    }
}