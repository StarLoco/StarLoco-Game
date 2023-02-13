package org.starloco.locos.job.maging;

import org.starloco.locos.game.world.World.Couple;

import java.util.ArrayList;

public class BreakingObject {

    private ArrayList<Couple<Integer, Integer>> objects = new ArrayList<>();
    private int count = 0;
    private boolean stop = false;

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isStop() {
        return stop;
    }

    public void setObjects(ArrayList<Couple<Integer, Integer>> objects) {
        this.objects = objects;
    }

    public ArrayList<Couple<Integer, Integer>> getObjects() {
        return objects;
    }

    public synchronized int addObject(int id, int quantity) {
        Couple<Integer, Integer> couple = this.search(id);

        if (couple == null) {
            this.objects.add(new Couple<>(id, quantity));
            return quantity;
        } else {
            couple.second += quantity;
            return couple.second;
        }
    }

    public synchronized int removeObject(int id, int quantity) {
        Couple<Integer, Integer> couple = this.search(id);

        if (couple != null) {
            if (quantity > couple.second) {
                this.objects.remove(couple);
                return quantity;
            } else {
                couple.second -= quantity;
                if (couple.second <= 0) {
                    this.objects.remove(couple);
                    return 0;
                }
                return couple.second;
            }
        }
        return 0;
    }

    private Couple<Integer, Integer> search(int id) {
        for (Couple<Integer, Integer> couple : this.objects)
            if (couple.first == id)
                return couple;
        return null;
    }
}