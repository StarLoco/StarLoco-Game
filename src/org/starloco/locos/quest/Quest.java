package org.starloco.locos.quest;

import org.apache.commons.lang.StringUtils;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.ObjectData;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.action.type.NpcDialogActionData;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.other.Action;
import org.starloco.locos.util.Pair;

import java.util.*;
import java.util.Map.Entry;

public class Quest {

    public final static Map<Integer, Quest> quests = new HashMap<>();

    private final int id;
    private final int npcID;
    private final boolean delete;
    private Couple<Integer, Integer> condition = null;
    private final List<QuestObjective> objectives = new ArrayList<>();
    private final List<QuestStep> steps = new ArrayList<>();
    private final List<Action> actions = new ArrayList<>();

    public Quest(int id, int npc, boolean delete, String condition, String steps, String objectives, String action, String args) {
        this.id = id;
        this.npcID = npc;
        this.delete = delete;
        this.handleObjectives(objectives);
        this.handleSteps(steps);
        this.handleCondition(condition);
        this.handleActions(action, args);
    }

    public int getId() {
        return id;
    }

    public NpcTemplate getNpcTemplate() {
        return World.world.getNPCTemplate(npcID);
    }

    public boolean mustBeDeletedWhenFinished() {
        return delete;
    }

    public List<QuestObjective> getObjectives() {
        return objectives;
    }

    private void handleSteps(String objectives) {
        if (StringUtils.isNotBlank(objectives)) {
            Arrays.stream(objectives.split(";")).map(Integer::parseInt).map(QuestStep.steps::get)
                    .filter(Objects::nonNull).forEach(steps::add);
        }
    }

    private void handleObjectives(String steps) {
        if (StringUtils.isNotBlank(steps)) {
            Arrays.stream(steps.split(";")).map(Integer::parseInt).map(QuestObjective.objectives::get)
                    .filter(Objects::nonNull).forEach(s -> {
                        s.setQuestData(this);
                        objectives.add(s);
                    });
        }
    }

    private void handleCondition(String condition) {
        if(StringUtils.isNotBlank(condition)) {
            String[] data = condition.split(":");
            if (data.length > 0) {
                this.condition = new Couple<>(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
            }
        }
    }

    private void handleActions(String action, String args) {
        if(StringUtils.isNotBlank(action)) {
            int count = 0;
            String[] arguments = args.split(";");

            for (String id : action.split(",")) {
                actions.add(new Action(Integer.parseInt(id), arguments[count], "-1", null));
                count++;
            }
        }
    }

    private boolean isConditionValid(QuestPlayer questPlayer, QuestObjective objective) {
        switch (objective.getCondition()) {
            case "1": // Validate previous step
                return this.objectives.stream().noneMatch(step -> step != null && step.getId() != objective.getId() && !questPlayer.isQuestObjectiveIsValidate(step));
            case "0":
                return true;
        }
        return false;
    }

    public QuestObjective getCurrentObjective(QuestPlayer questPlayer) {
        return objectives.stream().filter(o -> !questPlayer.isQuestObjectiveIsValidate(o)).findFirst().orElse(null);
    }

    public int getCurrentObjectiveId(QuestPlayer questPlayer) {
        for (QuestObjective objective : objectives) {
            if (!questPlayer.isQuestObjectiveIsValidate(objective)) {
                return objective.getStepId();
            }
        }
        return 0;
    }

    private int getPreviousObjectiveId(QuestPlayer questPlayer) {
        if (steps.size() == 1)
            return 0;
        int previous = 0;
        for (QuestStep step : steps) {
            if (step.getId() == getCurrentObjectiveId(questPlayer))
                return previous;
            previous = step.getId();
        }
        return 0;
    }

    public int getNextStep(QuestStep fromStep) {
        if (fromStep == null)
            return 0;
        for (QuestStep toStep : steps) {
            if (toStep.getId() == fromStep.getId()) {
                int index = steps.indexOf(toStep);
                if (steps.size() <= index + 1)
                    return 0;
                return steps.get(index + 1).getId();
            }
        }
        return 0;
    }

    public boolean hasFinished(QuestPlayer questPlayer, QuestStep questStep) {
        return questPlayer.overQuestStep(questStep) && getNextStep(questStep) == 0;
    }

    public void update(Player player, boolean validation, int type) {
        QuestPlayer questPlayer = player.getQuestPersoByQuest(this);
        for (QuestObjective questObjective : this.objectives) {
            if (questObjective.getValidationType() != type || questPlayer.isQuestObjectiveIsValidate(questObjective)) //On a d�j� valid� la questEtape on passe
                continue;
            if (questObjective.getStepId() != getCurrentObjectiveId(questPlayer) || !isConditionValid(questPlayer, questObjective))
                continue;

            boolean refresh = validation;
            switch (questObjective.getType()) {
                case 3://Donner item
                    if (player.getExchangeAction() != null && player.getExchangeAction().getType() == ExchangeAction.TALKING_WITH &&
                            ((NpcDialogActionData) player.getExchangeAction().getValue()).isValid(player, questObjective.getNpc().getId())) {
                        for (Entry<Integer, Integer> entry : questObjective.getItemsNeeded().entrySet()) {
                            if (player.hasItemTemplate(entry.getKey(), entry.getValue(), false)) { //Il a l'item et la quantit�
                                player.removeItemByTemplateId(entry.getKey(), entry.getValue(), false); //On supprime donc
                                refresh = true;
                            }
                        }
                    }
                    break;

                case 0:
                case 1://Aller voir %
                case 9://Retourner voir %
                    if (questObjective.getCondition().equalsIgnoreCase("1")) { //Valider les questEtape avant
                        if (player.getExchangeAction() != null && player.getExchangeAction().getType() == ExchangeAction.TALKING_WITH &&
                                ((NpcDialogActionData) player.getExchangeAction().getValue()).isValid(player, questObjective.getNpc().getId())) {
                            if (isConditionValid(questPlayer, questObjective)) {
                                refresh = true;
                            }
                        }
                    } else {
                        if (player.getExchangeAction() != null && player.getExchangeAction().getType() == ExchangeAction.TALKING_WITH &&
                                ((NpcDialogActionData) player.getExchangeAction().getValue()).isValid(player, questObjective.getNpc().getId()))
                            refresh = true;
                    }
                    break;

                case 6: // monstres
                    for (Entry<Integer, Short> entry : questPlayer.getMonsterKill().entrySet()) {
                        if (entry.getKey() == questObjective.getMonsterId() && entry.getValue() >= questObjective.getQuantity()) {
                            refresh = true;
                            break;
                        }
                    }
                    break;

                case 10://Ramener prisonnier
                    if (player.getExchangeAction() != null && player.getExchangeAction().getType() == ExchangeAction.TALKING_WITH &&
                            ((NpcDialogActionData) player.getExchangeAction().getValue()).isValid(player, questObjective.getNpc().getId())) {
                        GameObject follower = player.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR);
                        if (follower != null) {
                            Map<Integer, Integer> itemNecessaryList = questObjective.getItemsNeeded();
                            for (Entry<Integer, Integer> entry2 : itemNecessaryList.entrySet()) {
                                if (entry2.getKey() == follower.getTemplate().getId()) {
                                    refresh = true;
                                    player.setMascotte(0);
                                }
                            }
                        }
                    }
                    break;
                case 12: //Pierre d'âme (ocre)
                    final List<Integer> monsters = new ArrayList<>(questObjective.getItemsNeeded().keySet());
                    final List<SoulStone> objects = new ArrayList<>();
                    final List<Integer> valid = new ArrayList<>();
                    final byte[] ok = {0};

                    player.getItems().values().stream().filter(object -> object.getTemplate().getType() == 85)
                            .forEach(object -> {
                        SoulStone soul = (SoulStone) object;
                        Iterator<Pair<Integer, Integer>> iterator = soul.getMonsters().iterator();

                        while (iterator.hasNext()) {
                            Pair<Integer, Integer> couple = iterator.next();

                            for (Integer monster : monsters) {
                                if (!valid.contains(monster) && couple.first.equals(monster)) {
                                    iterator.remove();
                                    ok[0] = 1;
                                    if (!objects.contains(soul))
                                        objects.add(soul);
                                    valid.add(monster);
                                    break;
                                }
                            }
                        }
                    });


                    for(SoulStone object : objects) {
                        if(object.getMonsters().isEmpty()) {
                            player.removeItem(object.getGuid(), object.getQuantity(), true, true);
                        } else {
                            SocketManager.GAME_SEND_UPDATE_ITEM(player, object);
                            ((ObjectData) DatabaseManager.get(ObjectData.class)).update(object);
                        }
                    }
                    refresh = ok[0] == 1;
                    break;
            }

            if (refresh) {
                QuestStep ansObjectif = QuestStep.steps.get(getCurrentObjectiveId(questPlayer));
                questPlayer.setQuestObjectiveValidate(questObjective);
                SocketManager.GAME_SEND_Im_PACKET(player, "055;" + id);
                if (hasFinished(questPlayer, ansObjectif)) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "056;" + id);
                    applyButinOfQuest(player, ansObjectif);
                    questPlayer.setFinished(true);
                } else {
                    if (getNextStep(ansObjectif) != 0) {
                        if (questPlayer.overQuestStep(ansObjectif))
                            applyButinOfQuest(player, ansObjectif);
                    }
                }
                ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
            }
        }
    }

    public boolean apply(Player player) {
        if (this.condition != null) {
            switch (this.condition.first) {
                case 1: // Niveau
                    if (player.getLevel() < this.condition.second) {
                        SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("quest.quest.applyquest"));
                        return false;
                    }
                    break;
                case 2:
                default:
                    break;
            }
        }

        player.addQuestPerso(new QuestPlayer(this.id, false, player.getId(), ""));
        SocketManager.GAME_SEND_Im_PACKET(player, "054;" + this.id);
        SocketManager.GAME_SEND_MAP_NPCS_GMS_PACKETS(player.getGameClient(), player.getCurMap()); // Hacky. Should be sending a GM|~ only for the quest giver

        if (!this.actions.isEmpty()) {
            for (Action action : this.actions) {
                action.apply(player, player, -1, -1);
            }
        }

        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
        return true;
    }

    public void applyButinOfQuest(Player player, QuestStep questStep) {
        long xp; int kamas;

        if ((xp = questStep.getXp()) > 0) { //Xp a donner
            player.addXp(xp * ((int) Config.rateXp));
            SocketManager.GAME_SEND_Im_PACKET(player, "08;" + (xp * ((int) Config.rateXp)));
            SocketManager.GAME_SEND_STATS_PACKET(player);
        }

        if (questStep.getItems().size() > 0) { //Item a donner
            for (Pair<Integer, Integer> entry : questStep.getItems()) {
                ObjectTemplate template = World.world.getObjTemplate(entry.first);
                int quantity = entry.second;
                GameObject object = template.createNewItem(quantity, false);

                if (player.addItem(object, true, false)) {
                    World.world.addGameObject(object);
                }
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + quantity + "~" + template.getId());
            }
        }

        if ((kamas = questStep.getKamas()) > 0) { //Kams a donner
            player.setKamas(player.getKamas() + (long) kamas);
            SocketManager.GAME_SEND_Im_PACKET(player, "045;" + kamas);
            SocketManager.GAME_SEND_STATS_PACKET(player);
        }

        if (getNextStep(questStep) != questStep.getId()) { //On passe au nouveau objectif on applique les actions
            for (Action action : questStep.getActions()) {
                action.apply(player, null, 0, 0);
            }
        }
    }

    public String encodeQS(Player player) {
        QuestPlayer questPlayer = player.getQuestPersoByQuest(this);
        int currentObjective = getCurrentObjectiveId(questPlayer), previousObjective = getPreviousObjectiveId(questPlayer);
        int nextStep = getNextStep(QuestStep.steps.get(getCurrentObjectiveId(questPlayer)));

        boolean first = true;
        final StringBuilder str = new StringBuilder("QS"), temp = new StringBuilder();
        str.append(id).append("|").append(currentObjective > 0 ? currentObjective : "").append("|");

        for (QuestObjective objective : objectives) {
            if (objective.getStepId() != currentObjective || !isConditionValid(questPlayer, objective))
                continue;
            if (!first) temp.append(";");
            temp.append(objective.getId()).append(",").append(questPlayer.isQuestObjectiveIsValidate(objective) ? 1 : 0);
            first = false;
        }
        str.append(temp).append("|").append(previousObjective > 0 ? previousObjective : "").append("|").append(nextStep > 0 ? nextStep : "");
        NpcTemplate npc = getNpcTemplate();
        if (npc != null && npc.legacy != null) {
            // FIXME Get dialog for step
            str.append("|").append(npc.legacy.getInitQuestionId(player.getCurMap().getId())).append("|");
        }

        return str.toString();
    }
}
