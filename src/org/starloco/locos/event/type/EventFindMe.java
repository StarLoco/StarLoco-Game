package org.starloco.locos.event.type;

import org.starloco.locos.area.Area;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.entity.npc.Npc;
import org.starloco.locos.event.EventManager;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.util.TimerWaiter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Locos on 22/10/2016.
 */
public class EventFindMe extends Event {

    private GameMap map;
    private GameCase cell;
    private GameObject object;
    private int count = 0;
    private long time;

    public EventFindMe(byte id, byte maxPlayers, String name, String description) {
        super(id, maxPlayers, name, description);
    }

    public GameMap getMap() {
        return map;
    }

    public GameCase getCell() {
        return cell;
    }

    @Override
    public void prepare() {
        // Generate an item by the level of the population
        this.map = this.getRandomMap();
        this.cell = this.map.getCase(this.map.getRandomFreeCellId());
        this.object = World.world.getObjTemplate(26001) .createNewItem(1, false);
    }

    private GameMap getRandomMap() {
        final List<GameMap> maps = new ArrayList<>();
        final Area area = World.world.getArea(18);
        for(SubArea sub : area.getSubAreas())
            if(sub != null && sub.getId() != 440 && sub.getId() != 447)
                maps.addAll(sub.getMaps());
        return maps.get(Formulas.random.nextInt(maps.size()));
    }

    @Override
    public void perform() {
        this.cell.addDroppedItem(this.object);
        for(Player player : World.world.getOnlinePlayers()) {
            player.sendTypeMessage("Event",  player.getLang().trans("event.findme.find", map.getSubArea().getName()));
        }
        this.time = System.currentTimeMillis();
        TimerWaiter.addNext(this::execute, 5000);
    }

    @Override
    public void execute() {
        if (this.cell.getDroppedItem(false) == null) {
            this.close();
        } else if (count % 30 == 0) {
            boolean end = System.currentTimeMillis() - this.time > (30 * 60 * 1_000);
            String name = end ? map.getSubArea().getName() + " - [" + map.getX() + ", " + map.getY() + "]" : map.getSubArea().getName();
            for (Player player : World.world.getOnlinePlayers())
                player.sendTypeMessage("Event", player.getLang().trans("event.findme.find", name));
            TimerWaiter.addNext(this::execute, 5000);
        } else {
            TimerWaiter.addNext(this::execute, 5000);
        }
        count = count + 1;
    }

    @Override
    public void close() {
        for(Player player : World.world.getOnlinePlayers()) {
            player.sendTypeMessage("Event", player.getLang().trans("event.findme.win"));
        }
        EventManager.getInstance().finishCurrentEvent();
    }

    @Override
    public boolean onReceivePacket(EventManager manager, Player player, String packet) throws Exception {
        return false;
    }

    @Override
    public GameCase getEmptyCellForPlayer(Player player) {
        return null;
    }

    @Override
    public void kickPlayer(Player player) { }
}
