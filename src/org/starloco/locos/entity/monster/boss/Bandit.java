package org.starloco.locos.entity.monster.boss;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.GangsterData;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.game.world.World;
import org.starloco.locos.util.TimerWaiter;

import java.util.ArrayList;

public class Bandit {
    private static Bandit bandits;
    private final ArrayList<Monster> monsters = new ArrayList<>();
    private final ArrayList<GameMap> maps = new ArrayList<>();
    private long time;
    private boolean isPop = false;

    public Bandit(String mobs, String maps, long time) {
        if (!mobs.equalsIgnoreCase("")) {
            for (String mob : mobs.split(",")) {
                Integer _mob = null;
                try {
                    _mob = Integer.parseInt(mob);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (_mob == null)
                    continue;

                Monster monstre = World.world.getMonstre(_mob);
                if (monstre == null) {
                    continue;
                }
                this.monsters.add(monstre);
            }
        }

        if (!maps.equalsIgnoreCase("")) {
            for (String str : maps.split(",")) {
                short id;
                try {
                    id = Short.parseShort(str);
                    GameMap map = World.world.getMap(id);
                    if (map == null || map.getMountPark() != null)
                        continue;
                    this.maps.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        this.time = time;
        bandits = this;

        run();
    }

    public static Bandit getBandits() {
        return bandits;
    }

    public static void run() {
        Bandit bandit = getBandits();
        if (bandit.isPop) {
            TimerWaiter.addNext(Bandit::run, 1000 * 60 * 60);
        } else {
            long time = bandit.getTime();
            long actuel = System.currentTimeMillis();
            if (time <= 0) {
                pop(bandit, actuel);
            } else {
                int random = Formulas.getRandomValue(6, 18);
                long timeRandom = 1000 * 60 * 60 * random; // Temps en MS d'heures entre les repops des bandits
                if (time + timeRandom <= actuel) {
                    pop(bandit, actuel);
                } else {
                    TimerWaiter.addNext(Bandit::run, 1000 * 60 * 60);
                }
            }
        }
    }

    private static void pop(final Bandit bandit, final long actuel) {
        try {
            bandit.setTime(actuel);
            int nbMap = bandit.getMaps().size();
            int random = Formulas.getRandomValue(0, nbMap - 1);
            GameMap map = bandit.getMaps().get(random);
            StringBuilder groupData = new StringBuilder();
            for (Monster monstre : bandit.getMonsters()) {
                Integer id = monstre.getId();
                Integer lvl = monstre.getRandomGrade().getLevel();
                if (groupData.toString().equalsIgnoreCase(""))
                    groupData = new StringBuilder(id + "," + lvl + "," + lvl);
                else
                    groupData.append(";").append(id).append(",").append(lvl).append(",").append(lvl);
            }
            map.nextObjectId++;
            map.spawnNewGroup(false, map.getRandomFreeCellId(), groupData.toString(), "");
            ((GangsterData) DatabaseManager.get(GangsterData.class)).update(bandit);
        } catch (Exception e) {
            e.printStackTrace();
            TimerWaiter.addNext(() -> pop(bandit, actuel), 60000);
        }
    }

    public ArrayList<Monster> getMonsters() {
        return this.monsters;
    }

    public ArrayList<GameMap> getMaps() {
        return this.maps;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setPop(boolean isPop) {
        this.isPop = isPop;
    }

    public String parseMobs() {
        StringBuilder str = new StringBuilder();
        for(Monster monster : this.monsters) {
            if(str.length() > 0) str.append(",");
            str.append(monster.getId());
        }
        return str.toString();
    }
}
