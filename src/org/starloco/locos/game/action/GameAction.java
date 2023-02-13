package org.starloco.locos.game.action;

public class GameAction {

    public int id, actionId;
    public String packet, args;
    public boolean tp = false;

    public GameAction(int id, int actionId, String packet) {
        this.id = id;
        this.actionId = actionId;
        this.packet = packet;
    }
}