package org.starloco.locos.event.type;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.event.IEvent;

/**
 * Created by Locos on 02/10/2016.
 */
public abstract class Event implements IEvent {

    protected final byte id, maxPlayers;
    protected final String name;
    protected final String description;
    protected GameMap map;

    public Event(byte id, byte maxPlayers, String name, String description) {
        this.id = id;
        this.maxPlayers = maxPlayers;
        this.name = name;
        this.description = description;
    }

    public byte getEventId() {
        return id;
    }

    public byte getMaxPlayers() {
        return maxPlayers;
    }

    public String getEventName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public GameMap getMap() {
        return map;
    }

    public static void wait(int time) {
        long newTime = System.currentTimeMillis() + time;

        while (System.currentTimeMillis() < newTime) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void kickPlayer(Player player);
}
