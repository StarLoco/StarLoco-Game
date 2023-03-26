package org.starloco.locos.quest;

import org.starloco.locos.client.BasePlayer;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.QuestPlayerData;
import org.starloco.locos.game.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Locos on 23/01/2017.
 */
public class QuestPlayer {

    private int id;
    private final Quest quest;
    private boolean finished;
    private final BasePlayer player;
    private final Map<Integer, QuestObjective> objectivesValidate = new HashMap<>();
    private final Map<Integer, Short> monsterKill = new HashMap<>();

    public QuestPlayer(int id, int quest, boolean finished, int player, String objectives) {
        this.id = id;
        this.quest = Quest.quests.get(quest);
        this.finished = finished;
        this.player = World.world.getPlayer(player);
        this.handleObjectives(objectives);
    }

    public QuestPlayer(int quest, boolean finished, int player, String objectives) {
        this.quest = Quest.quests.get(quest);
        this.finished = finished;
        this.player = World.world.getPlayer(player);
        this.handleObjectives(objectives);
        ((QuestPlayerData) DatabaseManager.get(QuestPlayerData.class)).insert(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Quest getQuest() {
        return quest;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
        if (this.getQuest() != null && this.getQuest().mustBeDeletedWhenFinished()) {
            if (this.player != null && this.player.getQuestPerso() != null && this.player.getQuestPerso().containsKey(this.getId())) {
                this.player.delQuestPerso(this.getId());
                this.removeQuestPlayer();
            }
        } else if(this.getQuest() == null) {
            if (this.player.getQuestPerso().containsKey(this.getId())) {
                this.player.delQuestPerso(this.getId());
                this.removeQuestPlayer();
            }
        }
    }

    public BasePlayer getPlayer() {
        return player;
    }

    private void handleObjectives(String objectives) {
        try {
            String[] split = objectives.split(";");
            for (String data : split) {
                if (!data.isEmpty()) {
                    QuestObjective objective = QuestObjective.objectives.get(Integer.parseInt(data));
                    this.objectivesValidate.put(objective.getId(), objective);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isQuestObjectiveIsValidate(QuestObjective objective) {
        return objectivesValidate.containsKey(objective.getId());
    }

    public void setQuestObjectiveValidate(QuestObjective objective) {
        if (!objectivesValidate.containsKey(objective.getId()))
            objectivesValidate.put(objective.getId(), objective);
    }

    public String getQuestObjectivesToString() {
        StringBuilder str = new StringBuilder();
        int nb = 0;
        for (QuestObjective objective : this.objectivesValidate.values()) {
            nb++;
            str.append(objective.getId());
            if (nb < this.objectivesValidate.size())
                str.append(";");
        }
        return str.toString();
    }

    public Map<Integer, Short> getMonsterKill() {
        return monsterKill;
    }

    public boolean overQuestStep(QuestStep step) {
        int nbrQuest = 0;
        for (QuestObjective objective : this.objectivesValidate.values()) {
            if (objective.getStepId() == step.getId())
                nbrQuest++;
        }
        return step.getSizeUnique() == nbrQuest;
    }

    public boolean removeQuestPlayer() {
        ((QuestPlayerData) DatabaseManager.get(QuestPlayerData.class)).delete(this);
        return true;
    }
}