package org.starloco.locos.job;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.util.TimerWaiter;

import java.util.concurrent.TimeUnit;

public class JobCraft {

    private final static short CRAFT_TIME = 500;

    private JobAction jobAction;
    private int time = 0;
    private boolean itsOk = true;

    JobCraft(JobAction jobAction, Player player) {
        this.jobAction = jobAction;

        TimerWaiter.addNext(() -> {
            if (itsOk) jobAction.craft(false);
        }, CRAFT_TIME, TimeUnit.MILLISECONDS);
        TimerWaiter.addNext(() -> {
            if (!itsOk) repeat(time, time, player);
        }, CRAFT_TIME, TimeUnit.MILLISECONDS);
    }

    public JobAction getJobAction() {
        return jobAction;
    }

    public void setAction(int time) {
        this.time = time;
        this.jobAction.broken = false;
        this.itsOk = false;
    }

    private void repeat(final int time1, final int time2, final Player player) {
        final int j = time1 - time2;
        this.jobAction.player = player;
        this.jobAction.isRepeat = true;

        if (!this.check(player, j, time2) || time2 <= 0) {
            this.end();
        } else {
            TimerWaiter.addNext(() -> this.repeat(time1, (time2 - 1), player), CRAFT_TIME, TimeUnit.MILLISECONDS);
        }
    }

    private boolean check(final Player player, int j, int time2) {
        if (this.jobAction.broke || this.jobAction.broken || player.getExchangeAction() == null || !player.isOnline()) {
            if (player.getExchangeAction() == null)
                this.jobAction.broken = true;
            if (player.isOnline())
                SocketManager.GAME_SEND_Ea_PACKET(this.jobAction.player, this.jobAction.broken ? "2" : "4");
            return false;
        } else {
            SocketManager.GAME_SEND_EA_PACKET(this.jobAction.player, String.valueOf(time2));
            this.jobAction.craft(this.jobAction.isRepeat);
            if(!this.jobAction.isMaging())
                this.jobAction.ingredients.clear();
            this.jobAction.ingredients.putAll(this.jobAction.lastCraft);
            return true;
        }
    }

    private void end() {
        SocketManager.GAME_SEND_Ea_PACKET(this.jobAction.player, "1");
        this.jobAction.isRepeat = false;
        this.jobAction.setJobCraft(null);

        if(!this.jobAction.isMaging()) {
            this.jobAction.ingredients.clear();
        }
    }
}