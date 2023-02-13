package org.starloco.locos.game.scheduler.entity;

import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.game.scheduler.Updatable;
import org.starloco.locos.game.world.World;
import org.starloco.locos.util.TimerWaiter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldPub extends Updatable {
    public final static Updatable updatable = new WorldPub(600000);

    private int last;

    public WorldPub(int wait) {
        super(wait);
    }

    @Override
    public void update() {
        if (this.verify()) {
            int pub;
            do {
                pub = Formulas.getRandomValue(1, 15);
            } while (pub == last);

            World.world.sendMessageToAll("world.worldpub.pub." + pub);

            //SocketManager.GAME_SEND_MESSAGE_TO_ALL("(Message Auto) : " + pub, "046380");
            last = pub;
            //TimerWaiter.update();
        }
    }

    @Override
    public Object get() {
        return null;
    }
}