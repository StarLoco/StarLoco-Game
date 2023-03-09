package org.starloco.locos.area.map.entity;

import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.util.TimerWaiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * Created by Locos on 12/06/2015.
 */

public class InteractiveDoor {

    private static final ArrayList<InteractiveDoor> interactiveDoors = new ArrayList<>();

    private final ArrayList<Short> maps = new ArrayList<>();
    private final Map<Short, ArrayList<Short>> doorsEnable = new HashMap<>(), doorsDisable = new HashMap<>();
    private final Map<Short, ArrayList<Short>> cellsEnable = new HashMap<>(), cellsDisable = new HashMap<>();
    private final Map<Short, ArrayList<Couple<Short, String>>> requiredCells = new HashMap<>();

    private Couple<Short, Short> button;
    private short time = 30;
    private boolean state = false;

    public InteractiveDoor(String maps, String doorsEnable, String doorsDisable, String cellsEnable, String cellsDisable, String requiredCells, String button, short time) {
        for(String map : maps.split(",")) this.maps.add(Short.parseShort(map));

        if(!doorsEnable.isEmpty()) this.stock(this.doorsEnable, doorsEnable);
        if(!doorsDisable.isEmpty()) this.stock(this.doorsDisable, doorsDisable);
        if(!cellsEnable.isEmpty()) this.stock(this.cellsEnable, cellsEnable);
        if(!cellsDisable.isEmpty()) this.stock(this.cellsDisable, cellsDisable);

        if(!requiredCells.isEmpty()) {
            for (String data : requiredCells.split("@")) {
                String[] split = data.split(":");
                short map = Short.parseShort(split[0]);
                String cells = split[1];

                for (String cell : cells.split(";")) {
                    split = cell.split(",");
                    if (!this.requiredCells.containsKey(map))
                        this.requiredCells.put(map, new ArrayList<>());
                    this.requiredCells.get(map).add(new Couple<>(Short.parseShort(split[0]), split.length > 1 ? split[1] : null));
                }
            }
        }

        if(!button.equals("-1")) {
            String[] split = button.split(",");
            this.button = new Couple<>(Short.parseShort(split[0]), Short.parseShort(split[1]));
        }

        this.time = time;
        InteractiveDoor.interactiveDoors.add(this);
    }

    private void stock(Map<Short, ArrayList<Short>> arrayListMap, String value) {
        for(String data : value.split("@")) {
            String[] split = data.split(":");
            short map = Short.parseShort(split[0]);
            String cells = split[1];

            for(String cell : cells.split(",")) {
                if(!arrayListMap.containsKey(map))
                    arrayListMap.put(map, new ArrayList<>());
                arrayListMap.get(map).add(Short.parseShort(cell));
            }
        }
    }

    static boolean tryActivate(Player player, GameCase gameCase) {
        for(InteractiveDoor interactiveDoor : InteractiveDoor.interactiveDoors) {
            if (interactiveDoor.button != null && player.getCurMap().getId() == interactiveDoor.button.first && gameCase.getId() == interactiveDoor.button.second) {
                interactiveDoor.check(player);
                return true;
            }
        }
        return false;
    }

    public static void show(Player player) {
        InteractiveDoor.interactiveDoors.stream().filter(interactiveDoor -> interactiveDoor.state).forEach(interactiveDoor -> {
            interactiveDoor.setState(interactiveDoor.cellsEnable, true, false, player);
            interactiveDoor.setState(interactiveDoor.cellsDisable, false, false, player);
            interactiveDoor.setState(interactiveDoor.doorsEnable, true, true, player);
            interactiveDoor.setState(interactiveDoor.doorsDisable, false, true, player);
        });
    }

    public static void check(Player player, GameMap gameMap) {
        try {
            for (InteractiveDoor interactiveDoor : InteractiveDoor.interactiveDoors)
                if (interactiveDoor.maps.contains(gameMap.getId()))
                    if (interactiveDoor.button == null && interactiveDoor.check(player))
                        break;
        } catch(Exception e) { e.printStackTrace(); }
    }

    private synchronized boolean check(Player player) {
        if(this.state) return false;
        boolean ok = true;

        for(Entry<Short, ArrayList<Couple<Short, String>>> requiredCells : this.requiredCells.entrySet()) {
            final GameMap gameMap = World.world.getMap(requiredCells.getKey());
            if(gameMap == null) continue;
            boolean loc = false;
            for(Couple<Short, String> couple : requiredCells.getValue()) {
                GameCase gameCase = gameMap.getCase(couple.first);
                if (gameCase == null) continue;

                switch(player.getCurMap().getId()) {
                    case 1884:
                        if(gameCase.getPlayers().size() > 0) {
                            loc = true;
                            ok = true;
                        }
                        break;
                }
                if(loc) break;

                if (couple.second != null) {
                    if (!Condition.isValid(player, gameCase, couple.second)) {
                        ok = false;
                        break;
                    }
                } else if (gameCase.getPlayers().size() == 0) {
                    ok = false;
                    break;
                }
            }

            if(!ok) break;
        }

        if(ok) {
            this.open();
            TimerWaiter.addNext(this::close, this.time, TimeUnit.SECONDS);
        }
        return ok;
    }

    private void open() {
        if(this.state) return;

        this.setState(this.cellsEnable, true, false, null);
        this.setState(this.cellsDisable, false, false, null);
        this.setState(this.doorsEnable, true, true, null);
        this.setState(this.doorsDisable, false, true, null);
        this.state = true;
    }

    private void close() {
        if(!this.state) return;

        this.setState(this.cellsEnable, false, false, null);
        this.setState(this.cellsDisable, true, false, null);
        this.setState(this.doorsEnable, false, true, null);
        this.setState(this.doorsDisable, true, true, null);
        this.state = false;
    }

    private void setState(Map<Short, ArrayList<Short>> arrayListMap, boolean active, boolean doors, Player player) {
        String packet = "GDF";

        for(Entry<Short, ArrayList<Short>> entry : arrayListMap.entrySet()) {
            GameMap gameMap = World.world.getMap(entry.getKey());
            if(gameMap == null) continue;
            if(player != null && player.getCurMap() != null && player.getCurMap().getId() != gameMap.getId()) continue;

            for(short cell : entry.getValue()) {
                if(doors)
                    packet += this.setStateDoor(cell, active, player != null);
                else
                    this.setStateCell(gameMap, cell, active, player);
            }

            if(player != null)
                player.send(packet);
            else for (Player target : gameMap.getPlayers())
                target.send(packet);
        }
    }

    private String setStateDoor(int cell, boolean active, boolean fast) {
        return "|" + cell + (!fast ? (active ? ";2" : ";4") : (active ? ";3" : ";1"));
    }

    private void setStateCell(GameMap gameMap, short cell, boolean active, Player player) {
        String packet = "GDC" + cell;
        GameCase gameCase = gameMap.getCase(cell), temporaryCell;
        gameMap.removeCase(cell);

        if(active) {
            temporaryCell = new GameCase(gameMap, cell, true, true, -1);
            temporaryCell.setOnCellStop(gameCase.getOnCellStop());
            gameMap.getCases().add(temporaryCell);
            packet += ";aaGaaaaaaa801;1";
        } else {
            temporaryCell = new GameCase(gameMap, cell, false, false, -1);
            temporaryCell.setOnCellStop(gameCase.getOnCellStop());
            gameMap.getCases().add(temporaryCell);
            packet += ";aaaaaaaaaa801;1";
        }

        if(player != null)
            player.send(packet);
        else for (Player target : gameMap.getPlayers())
            target.send(packet);
    }

    private static class Condition {
        static boolean isValid(Player player, GameCase gameCase, String request) {
            Jep jep = new Jep();
            request = request.replace("&", "&&").replace("=", "==").replace("|", "||").replace("!", "!=").replace("~", "==");
            try {
                // Item template cell
                GameObject object = gameCase.getDroppedItem(false);
                jep.addVariable("ITC", object != null ? object.getTemplate().getId() : -1);

                //Mob Group Cell
                if(request.contains("MGC")) request = Condition.parseMGC(player, request);
                //Plyaer Krala Cell
                if(request.contains("KRL")) request = Condition.parseKRL(player, request);

                //Parse request..
                jep.parse(request);
                Object result = jep.evaluate();
                return result != null ? Boolean.valueOf(result.toString()) : false;
            } catch (JepException e) {
                e.printStackTrace();
            }
            return false;
        }

        private static String parseKRL(Player player, String request) {
            if(player == null)
                return "0==1";
            String[] data = request.split("==")[1].split("-");
            short classId = Short.parseShort(data[0]);
            byte sex = Byte.parseByte(data[1]);
            return player.getSexe() == sex && player.getClasse() == classId ? "1==1" : "0==0";
        }

        private static String parseMGC(Player player, String request) {
            String[] data = request.split("==")[1].split("-");
            for(Monster.MobGroup mobGroup : player.getCurMap().getMobGroups().values())
                for(String id : data)
                    if (mobGroup.getCellId() == Short.parseShort(id))
                        return "1==1";
            return "1==0";
        }
    }
}
