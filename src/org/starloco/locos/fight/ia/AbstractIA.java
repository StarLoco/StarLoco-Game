package org.starloco.locos.fight.ia;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.util.TimerWaiter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Locos on 18/09/2015.
 */
public abstract class AbstractIA implements IA {

    private final static ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    protected Fight fight;
    protected Fighter fighter;
    protected boolean stop;
    protected byte count;

    public AbstractIA(Fight fight, Fighter fighter, byte count) {
        this.fight = fight;
        this.fighter = fighter;
        this.count = count;
    }

    public Fight getFight() {
        return fight;
    }

    public Fighter getFighter() {
        return fighter;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void endTurn() {
        this.fight.endTurn(false, this.fighter);
    }

    protected void decrementCount() {
        this.count--;
        if(this.stop || this.count == 0) {
            this.endTurn();
        } else {
            this.apply();
        }
    }

    public void addNext(Runnable runnable, Integer time) {
        executor.schedule(TimerWaiter.catchRunnable(runnable),time,TimeUnit.MILLISECONDS);
    }
}