package org.starloco.locos.game.scheduler;

/**
 * Created by Locos on 24/06/2015.
 */
public interface IUpdatable {

    void update();
    default Object get() { return null; }
}
