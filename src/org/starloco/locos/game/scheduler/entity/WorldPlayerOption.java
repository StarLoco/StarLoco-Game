package org.starloco.locos.game.scheduler.entity;

import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.AccountData;
import org.starloco.locos.game.scheduler.Updatable;
import org.starloco.locos.game.world.World;

public class WorldPlayerOption extends Updatable {

    public final static Updatable updatable = new WorldPub(300000);

    public WorldPlayerOption(int wait) {
        super(wait);
    }

    @Override
    public void update() {
        if(this.verify()) {
            ((AccountData) DatabaseManager.get(AccountData.class)).updateVoteAll();
            World.world.getOnlinePlayers().stream().filter(player -> player != null && player.isOnline()).forEach(org.starloco.locos.client.Player::checkVote);
        }
    }

    @Override
    public Object get() {
        return null;
    }
}