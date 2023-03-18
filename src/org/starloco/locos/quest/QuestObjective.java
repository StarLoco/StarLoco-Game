package org.starloco.locos.quest;

import org.apache.commons.lang.StringUtils;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.game.world.World;

import java.util.HashMap;
import java.util.Map;

public class QuestObjective {

    public final static Map<Integer, QuestObjective> objectives = new HashMap<>();

    private final int id, type, stepId, validationType;
    private final NpcTemplate npc;
    private Quest quest = null;
    private final String condition;
    private int monsterId, quantity;
    private final Map<Integer, Integer> itemsNeeded = new HashMap<>(); //ItemID,Quantity

    public QuestObjective(int id, int type, int stepId, int validationType, int npcTemplateId, String condition, String items, String monsters) {
        this.id = id;
        this.type = type;
        this.stepId = stepId;
        this.validationType = validationType;
        this.npc = World.world.getNPCTemplate(npcTemplateId);
        this.condition = condition;
        this.handleItems(items);
        this.handleMonsters(monsters);

        QuestStep questStep = QuestStep.steps.get(stepId);
        if (questStep != null) {
            questStep.addQuestStep(this);
        }
    }

    private void handleMonsters(String monsters) {
        if (StringUtils.isNotBlank(monsters) && monsters.contains(",") && !monsters.equals("0")) {
            String[] data = monsters.split(",");
            this.monsterId = Integer.parseInt(data[0]);
            this.quantity = Integer.parseInt(data[1]); // Des qu�tes avec le truc vide ! ><
        }
    }

    private void handleItems(String items) {
        if (StringUtils.isNotBlank(items)) {
            for (String item : items.split(";")) {
                String[] data = item.split(",");
                this.itemsNeeded.put(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getStepId() {
        return stepId;
    }

    public int getValidationType() {
        return validationType;
    }

    public NpcTemplate getNpc() {
        return npc;
    }

    public Quest getQuest() {
        return quest;
    }

    void setQuestData(Quest quest) {
        this.quest = quest;
    }

    public String getCondition() {
        return condition;
    }

    public int getMonsterId() {
        return monsterId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Map<Integer, Integer> getItemsNeeded() {
        return itemsNeeded;
    }
}
