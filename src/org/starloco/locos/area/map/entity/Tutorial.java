package org.starloco.locos.area.map.entity;

import org.starloco.locos.kernel.Main;
import org.starloco.locos.other.Action;

import java.util.ArrayList;

public class Tutorial {

    private int id;
    private ArrayList<Action> reward = new ArrayList<>(4);
    private Action start;
    private Action end;

    public Tutorial(int id, String reward, String start, String end) {
        this.id = id;
        try {
            for (String str : reward.split("\\$")) {
                if (str.isEmpty()) {
                    this.reward.add(null);
                } else {
                    String[] split = str.split("@");
                    if (split.length >= 2) {
                        this.reward.add(new Action(Integer.parseInt(split[0]), split[1], "", null));
                    } else {
                        this.reward.add(new Action(Integer.parseInt(split[0]), "", "", null));
                    }
                }
            }

            if (start.isEmpty()) {
                this.start = null;
            } else {
                String[] split = start.split("\\@");
                if (split.length >= 2)
                    this.start = new Action(Integer.parseInt(split[0]), split[1], "", null);
                else
                    this.start = new Action(Integer.parseInt(split[0]), "", "", null);
            }

            if (end.isEmpty()) {
                end = null;
            } else {
                String[] split = end.split("\\@");
                if (split.length >= 2)
                    this.end = new Action(Integer.parseInt(split[0]), split[1], "", null);
                else
                    this.end = new Action(Integer.parseInt(split[0]), "", "", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Main.stop("Tutorial");
        }
    }

    public int getId() {
        return id;
    }

    public Action getStart() {
        return start;
    }

    public Action getEnd() {
        return end;
    }

    public ArrayList<Action> getReward() {
        return reward;
    }
}
