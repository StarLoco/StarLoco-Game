package org.starloco.locos.fight.ia.util;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;

import java.util.*;

public class AStarPathFinding {

    private java.util.Map<Integer, Node> openList = new HashMap<>(), closeList = new LinkedHashMap<>();

    private Fight fight;
    private GameMap map;
    private int cellStart, cellEnd;


    public AStarPathFinding(GameMap map, int cellStart, int cellEnd) {
        this.map = map;
        this.cellStart = cellStart;
        this.cellEnd = cellEnd;
    }

    public AStarPathFinding(Fight fight, int cellStart, int cellEnd) {
        this.fight = fight;
        this.map = fight.getMap();
        this.cellStart = cellStart;
        this.cellEnd = cellEnd;
        GameCase cellEnd1 = map != null ? fight.getMap().getCase(cellEnd) : null;
        if(cellEnd1 != null && Function.getInstance().getCellsAvailableAround(fight, cellEnd1, true, (byte) 0).isEmpty()) {
            int dist = PathFinding.getDistanceBetween(map, cellStart, cellEnd);
            for (GameCase cell : Function.getInstance().getCellsAround(fight, cellEnd1)) {
                List<GameCase> cells2 =  Function.getInstance().getCellsAvailableAround(fight, cell, true, (byte) 0);
                for(GameCase check : cells2) {
                    int tmp = PathFinding.getDistanceBetween(map, cellStart, check.getId());
                    if(tmp < dist) {
                        this.cellEnd = check.getId();
                    }
                }
            }
            if(cellEnd1.getId() == this.cellEnd)
                this.cellEnd = -1;
        }
    }

    public ArrayList<GameCase> getShortestPath() {
        if(this.cellEnd == -1) return getPath();

        final Node start = new Node(map.getCase(cellStart), null);
        openList.put(cellStart, start);


        while (!openList.isEmpty() && !closeList.containsKey(cellEnd)) {
            char[] dirs = {'b', 'd', 'f', 'h'};
            final Node current = bestNode();

            boolean around = fight != null && PathFinding.cellArroundCaseIDisOccuped(fight, current.getCell().getId());

            if (current.getCell().getId() == cellEnd && !around)
                return getPath();

            addListClose(current);
            for (int loc0 = 0; loc0 < 4; loc0++) {
                int cellId = PathFinding.getCaseIDFromDirrection(current.getCell().getId(), dirs[loc0], map);

                final GameCase cell = map.getCase(cellId);
                if(cell == null) continue;

                final Node node = new Node(cell, current);
                if (node.getCell() == null || !node.getCell().isWalkable(true, true, -1) && cellId != cellEnd)
                    continue;

               boolean occupied = fight != null && PathFinding.haveFighterOnThisCell(cellId, fight, true);
                if (occupied && cellId != cellEnd || closeList.containsKey(cellId))
                    continue;

                if (openList.containsKey(cellId)) {
                    if (openList.get(cellId).getCountG() > getCostG(node)) {
                        current.setChild(openList.get(cellId));
                        openList.get(cellId).setParent(current);
                        openList.get(cellId).setCountG(getCostG(node));
                        openList.get(cellId).setHeristic(PathFinding.getDistanceBetween(map, cellId, cellEnd) * 10);
                        openList.get(cellId).setCountF(openList.get(cellId).getCountG() + openList.get(cellId).getHeristic());
                    }
                } else {
                    openList.put(cellId, node);
                    current.setChild(node);
                    node.setParent(current);
                    node.setCountG(getCostG(node));
                    node.setHeristic(PathFinding.getDistanceBetween(map, cellId, cellEnd) * 10);
                    node.setCountF(node.getCountG() + node.getHeristic());
                }
            }
        }
        return getPath();
    }

    private ArrayList<GameCase> getPath() {
        Node current = getLastNode(closeList);
        if (current == null)
            return null;

        ArrayList<GameCase> path = new ArrayList<>();
        Map<Integer, GameCase> path0 = new HashMap<>();
        for (int index = closeList.size(); current.getCell().getId() != cellStart; index--) {
            if (current.getCell().getId() == cellStart)
                continue;
            path0.put(index, map.getCase(current.getCell().getId()));
            current = current.getParent();

        }
        int index = -1;
        while (path.size() != path0.size()) {
            index++;
            GameCase cell = path0.get(index);
            if (cell != null) path.add(cell);
        }
        return path;
    }

    private Node getLastNode(Map<Integer, Node> list) {
        Node node = null;
        for (Node entry : list.values())
            node = entry;
        return node;
    }

    private Node bestNode() {
        int bestCountF = 150000;
        Node bestNode = null;
        for (Node node : openList.values()) {
            if (node.getCountF() < bestCountF) {
                bestCountF = node.getCountF();
                bestNode = node;
            }
        }
        return bestNode;
    }

    private void addListClose(Node node) {
        int id = node.getCell().getId();
        if (openList.containsKey(id)) openList.remove(id);
        if (!closeList.containsKey(id)) closeList.put(id, node);
    }

    private int getCostG(Node node) {
        int costG = 0;
        while (node.getCell().getId() == cellStart) {
            node = node.getParent();
            costG += 10;
        }
        return costG;
    }
}
