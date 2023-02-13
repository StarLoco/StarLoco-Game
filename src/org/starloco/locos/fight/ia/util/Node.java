package org.starloco.locos.fight.ia.util;

import org.starloco.locos.area.map.GameCase;

public class Node {

    private int countG = 0, countF = 0, heristic = 0;
    private GameCase cell;
    private Node parent, child;

    public Node(GameCase cell, Node parent) {
        this.cell = cell;
        this.parent =  parent;
    }

    public int getCountG() {
        return countG;
    }

    public void setCountG(int countG) {
        this.countG = countG;
    }

    public int getCountF() {
        return countF;
    }

    public void setCountF(int countF) {
        this.countF = countF;
    }

    public int getHeristic() {
        return heristic;
    }

    public void setHeristic(int heristic) {
        this.heristic = heristic;
    }

    public GameCase getCell() {
        return cell;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setChild(Node child) {
        this.child = child;
    }

    public Node getChild() {
        return child;
    }
}
