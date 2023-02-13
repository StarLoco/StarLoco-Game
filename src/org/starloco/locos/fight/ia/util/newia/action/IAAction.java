package org.starloco.locos.fight.ia.util.newia.action;

/**
 * Created by Locos on 28/04/2018.
 */
public interface IAAction {

    byte MOVE = 0, ATTACK = 1;

    byte getType();
    short getWaitingTime();
    boolean execute();
}
