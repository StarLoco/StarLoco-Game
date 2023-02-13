package org.starloco.locos.quest;

import org.starloco.locos.client.Player;
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
    private Quest quest = null;
    private boolean finish;
    private Player player;
    private Map<Integer, QuestObjective> objectivesValidate = new HashMap<>();
    private Map<Integer, Short> monsterKill = new HashMap<>();

    public QuestPlayer(int id, int quest, boolean finish, int player, String steps) {
        this.id = id;
        this.quest = Quest.getQuestById(quest);
        this.finish = finish;
        this.player = World.world.getPlayer(player);
        this.parseObjectives(steps);
    }

    public QuestPlayer(int quest, boolean finish, int player, String steps) {
        this.quest = Quest.getQuestById(quest);
        this.finish = finish;
        this.player = World.world.getPlayer(player);
        this.parseObjectives(steps);
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

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
        if (this.getQuest() != null && this.getQuest().isDelete()) {
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

    public Player getPlayer() {
        return player;
    }

    private void parseObjectives(String objectives) {
        try {
            String[] split = objectives.split(";");
            if (split.length > 0) {
                for (String data : split) {
                    if (!data.equalsIgnoreCase("")) {
                        QuestObjective step = QuestObjective.getObjectiveById(Integer.parseInt(data));
                        this.objectivesValidate.put(step.getId(), step);
                    }
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
            if (objective.getObjectif() == step.getId())
                nbrQuest++;
        }
        return step.getSizeUnique() == nbrQuest;
    }

    public boolean removeQuestPlayer() {
        ((QuestPlayerData) DatabaseManager.get(QuestPlayerData.class)).delete(this);
        return true;
    }
}