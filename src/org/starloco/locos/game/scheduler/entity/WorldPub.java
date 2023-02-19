package org.starloco.locos.game.scheduler.entity;

import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.game.scheduler.Updatable;
import org.starloco.locos.util.TimerWaiter;

import java.util.ArrayList;

public class WorldPub extends Updatable {

    public final static Updatable updatable = new WorldPub(600000);
    public final static ArrayList<String> pubs = new ArrayList<>();

    private int last;

    public WorldPub(int wait) {
        super(wait);
    }

    @Override
    public void update() {
        if(!WorldPub.pubs.isEmpty()) {
            if (this.verify()) {
                int pub;
                do {
                    pub = Formulas.getRandomValue(0, pubs.size() - 1);
                } while (pub == last);

                last = pub;
                SocketManager.GAME_SEND_MESSAGE_TO_ALL("(Message Auto) : " + pubs.get(pub), "046380");
                TimerWaiter.update();
            }
        }
    }

    @Override
    public Object get() {
        return null;
    }
}