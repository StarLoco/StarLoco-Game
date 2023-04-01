package org.starloco.locos.quest;

import org.apache.commons.lang.StringUtils;
import org.starloco.locos.other.Action;
import org.starloco.locos.util.Pair;

import java.util.*;

/**
 * Created by Locos on 23/01/2017.
 */
public class QuestStep {

    public final static Map<Integer, QuestStep> steps = new HashMap<>();

    private final int id, xp, kamas;
    private final List<Pair<Integer, Integer>> items = new ArrayList<>();
    private final List<Action> actions = new ArrayList<>();
    private final List<QuestObjective> objectives = new ArrayList<>();

    public QuestStep(int id, int xp, int kamas, String items, String actions) {
        this.id = id;
        this.xp = xp;
        this.kamas = kamas;
        this.handleItems(items);
        this.handleActions(actions);
    }

    public int getId() {
        return id;
    }

    public int getXp() {
        return xp;
    }

    public int getKamas() {
        return kamas;
    }

    public List<Pair<Integer, Integer>> getItems() {
        return items;
    }

    private void handleItems(String items) {
        if (StringUtils.isNotBlank(items)) {
            for (String item : items.split(";")) {
                if (item.isEmpty())
                    continue;

                if (item.contains(",")) {
                    String[] data = item.split(",");
                    this.items.add(new Pair<>(Integer.parseInt(data[0]), Integer.parseInt(data[1])));
                    continue;
                }

                this.items.add(new Pair<>(Integer.parseInt(item), 1));
            }
        }
    }

    public List<Action> getActions() {
        return actions;
    }

    private void handleActions(String actions) {
        if (StringUtils.isNotBlank(actions)) {
            for (String action : actions.split(";")) {
                String[] data = action.split("\\|");
                this.actions.add(new Action(Integer.parseInt(data[0]), data[1], "-1"));
            }
        }
    }

    int getSizeUnique() {
        int cpt = 0;
        //TODO: why not objectives.size() ?
        ArrayList<Integer> id = new ArrayList<>();
        for (QuestObjective step : this.objectives) {
            if (!id.contains(step.getId())) {
                id.add(step.getId());
                cpt++;
            }
        }
        return cpt;
    }

    void addQuestStep(QuestObjective step) {
        if (!this.objectives.contains(step)) {
            this.objectives.add(step);
        }
    }
}