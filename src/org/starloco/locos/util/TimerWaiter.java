package org.starloco.locos.util;

import org.starloco.locos.game.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimerWaiter {

    private static final int numberOfThread = 10;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(numberOfThread);

    public static ScheduledFuture<?> addNext(Runnable run, long time, TimeUnit unit) {
        return TimerWaiter.scheduler.schedule(catchRunnable(run), time, unit);
    }

    public static ScheduledFuture<?> addNext(Runnable run, long time) {
        return TimerWaiter.addNext(run, time, TimeUnit.MILLISECONDS);
    }

    public static void update() {
        //numberOfThread = getNumberOfThread() + 20;
        //scheduler.shutdownNow();
        //scheduler = Executors.newScheduledThreadPool(numberOfThread);
    }

    private static int getNumberOfThread() {
        int fight = getNumberOfFight();
        int player = World.world.getOnlinePlayers().size();
        return (fight + player) / 30;
    }

    private static int getNumberOfFight() {
        final int[] fights = {0};
        World.world.getMaps().forEach(map -> fights[0] += map.getFights().size());
        return fights[0];
    }

    public static Runnable catchRunnable(Runnable run) {
        return () -> {
            try {
                run.run();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getCause().getMessage());
            }
        };
    }
}