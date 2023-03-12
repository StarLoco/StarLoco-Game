package org.starloco.locos.entity.npc;

import org.starloco.locos.client.Player;
import org.starloco.locos.other.Action;
import org.starloco.locos.quest.Quest;

import java.util.ArrayList;

public class NpcAnswer {

    private int id;
    private ArrayList<Action> actions = new ArrayList<>();
    private Quest quest = null;

    public NpcAnswer(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }

    public void addAction(Action action0) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.addAll(this.actions);

        for (Action action1 : actions)
            if (action1.getId() == action0.getId())
                getActions().remove(action1);

        this.actions.add(action0);
    }

    public boolean apply(Player player) {
        boolean leave = true;
        for (Action action : this.getActions())
            leave = action.apply(player, null, -1, -1);
        return leave;
    }

    public boolean isAnotherDialog() {
        for (Action action : getActions())
            if (action.getId() == 1) //1 = Discours NPC
                return true;
        return false;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }
}