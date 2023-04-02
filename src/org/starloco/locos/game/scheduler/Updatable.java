package org.starloco.locos.game.scheduler;

/**
 * Created by Locos on 23/06/2015.
 */
public abstract class Updatable<T> implements IUpdatable<T> {

    private final long wait;
    protected long lastTime = System.currentTimeMillis();

    public Updatable(int wait) {
        this.wait = wait;
    }

    protected boolean verify() {
        if(System.currentTimeMillis() - this.lastTime > this.wait) {
            this.lastTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

}
