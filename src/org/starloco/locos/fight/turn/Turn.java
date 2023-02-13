package org.starloco.locos.fight.turn;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.util.TimerWaiter;

import java.util.concurrent.TimeUnit;

public class Turn implements Runnable {

    private final Fight fight;
    private final Fighter fighter;
    private final long start;
    private boolean stop = false;

    public Turn(Fight fight, Fighter fighter) {
        this.fight = fight;
        this.fighter = fighter;
        int duration = Constant.TIME_BY_TURN + 2000;
        TimerWaiter.addNext(this, duration, TimeUnit.MILLISECONDS);
        this.start = System.currentTimeMillis();
    }

    public long getStartTime() {
        return start;
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        if (this.stop || this.fighter.isDead()) {
            this.stop();
            return;
        }

        if (this.fight.getOrderPlaying() == null) {
            this.stop();
            return;
        }

        if (this.fight.getOrderPlaying().get(this.fight.getCurPlayer()) == null) {
            this.stop();
            return;
        }

        if (this.fight.getOrderPlaying().get(this.fight.getCurPlayer()) != this.fighter) {
            this.stop();
            return;
        }
        this.fight.endTurn(false);
    }
}