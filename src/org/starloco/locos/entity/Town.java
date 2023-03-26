package org.starloco.locos.entity;

import org.starloco.locos.area.Area;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.game.world.World;

public class Town {

    public static Town[] TOWNS = {
            new Town(1, World.world.getArea(23), World.world.getMap( 7951), 10, World.world.getMap( 7951), 1),
            //new Town(2, World.world.getArea(1), World.world.getMap(1), 10, World.world.getMap(1), 1),
            //new Town(3, World.world.getArea(1), World.world.getMap(1), 10, World.world.getMap(1), 1),
            //new Town(4, World.world.getArea(1), World.world.getMap(1), 10, World.world.getMap(1), 1),
            /*new Town(5, World.world.getArea(1), World.world.getMap(1), 10, World.world.getMap(1), 1),
            new Town(6, World.world.getArea(1), World.world.getMap(1), 10, World.world.getMap(1), 1),
            new Town(6, World.world.getArea(1), World.world.getMap(1), 10, World.world.getMap(1), 1),*/
    };

    private final int id;
    private final Area area;
    private final GameMap mainDoorMap;
    private final int mainDoorOpeningDuration;
    private final GameMap prismRoomMap;
    private final int prismRoomOpeningDuration;

    public Town(int id, Area area, GameMap mainDoorMap, int mainDoorOpeningDuration, GameMap prismRoomMap, /*short prs,*/ int prismRoomOpeningDuration) {
        this.id = id;
        this.area = area;
        this.mainDoorMap = mainDoorMap;
        this.mainDoorOpeningDuration = mainDoorOpeningDuration;
        this.prismRoomMap = prismRoomMap;
        this.prismRoomOpeningDuration = prismRoomOpeningDuration;
    }

    public int getId() {
        return id;
    }

    public Area getArea() {
        return area;
    }

    public int getAlignment() {
        return area.getAlignement();
    }

    public GameMap getMainDoorMap() {
        return mainDoorMap;
    }

    public int getMainDoorOpeningDuration() {
        return mainDoorOpeningDuration;
    }

    public GameMap getPrismRoomMap() {
        return prismRoomMap;
    }

    public int getPrismRoomOpeningDuration() {
        return prismRoomOpeningDuration;
    }


}
