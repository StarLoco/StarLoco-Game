package org.starloco.locos.entity.monster.boss;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.game.world.World;

import java.util.ArrayList;

public class MaitreCorbac {
    /*
     * Subarea : 211 Group : 289,120,200;825,90,98;823,90,98;824,80,88
     */
    private short oldMap;
    private short map;

    public MaitreCorbac() {
        repop((short) -1);
    }

    public void repop(short id) {
        if (this.oldMap == id)
            return;

        this.oldMap = id;

        ArrayList<GameMap> maps = new ArrayList<>();
        maps.addAll(World.world.getSubArea(211).getMaps());
        maps.remove(World.world.getMap((short) 9589));
        maps.remove(World.world.getMap((short) 9604));

        int index = Formulas.random.nextInt(maps.size());
        GameMap map = maps.get(index);

        while (map.getId() == id) {
            index = Formulas.random.nextInt(maps.size());
            map = maps.get(index);
        }

        this.map = map.getId();
        map.spawnGroupOnCommand(map.getRandomFreeCellId(), "289,120,200;825,90,98;823,90,98;824,80,88", true);
    }

    public int check() {
        switch (this.map) {
            case 9590:
            case 9594:
            case 9596:
            case 9600:
                return 3188;
            case 9592:
            case 9597:
            case 9593:
            case 9598:
                return 3193;
            case 9599:
            case 9591:
            case 9595:
            case 9603:
                return 3191;
            case 9601:
            case 9723:
            case 9602:
            case 9724:
                return 3194;
        }
        return -1;
    }
}