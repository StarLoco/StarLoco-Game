package org.starloco.locos.quest;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.ObjectData;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.other.Action;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Quest {

    //region Static method
    private static Map<Integer, Quest> questList = new HashMap<>();

    public static Map<Integer, Quest> getQuestList() {
        return questList;
    }

    public static Quest getQuestById(int id) {
        return questList.get(id);
    }

    public static void addQuest(Quest quest) {
        questList.put(quest.getId(), quest);
    }
    //endregion

    private int id;
    private ArrayList<QuestObjective> questObjectives = new ArrayList<>();
    private ArrayList<QuestStep> questStepList = new ArrayList<>();
    private NpcTemplate npc = null;
    private ArrayList<Action> actions = new ArrayList<>();
    private boolean delete;
    private Couple<Integer, Integer> condition = null;

    public Quest(int id, String steps, String objectifs, int npc, String action, String args, boolean delete, String condition) {
        this.id = id;
        this.delete = delete;

        if (!steps.equalsIgnoreCase("")) {
            String[] split = steps.split(";");
            if (split.length > 0) {
                for (String stepId : split) {
                    QuestObjective step = QuestObjective.getObjectiveById(Integer.parseInt(stepId));
                    if (step != null) {
                        step.setQuestData(this);
                        this.questObjectives.add(step);
                    }
                }
            }
        }

        if (!objectifs.equalsIgnoreCase("")) {
            String[] split = objectifs.split(";");

            if (split.length > 0) {
                for (String qObjectif : split) {
                    QuestStep objective = QuestStep.getStepById(Integer.parseInt(qObjectif));
                    if (objective != null) this.questStepList.add(objective);
                }
            }
        }

        if (!condition.equalsIgnoreCase("")) {
            try {
                String[] split = condition.split(":");
                if (split.length > 0) {
                    this.condition = new Couple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.npc = World.world.getNPCTemplate(npc);
        try {
            if (!action.equalsIgnoreCase("") && !args.equalsIgnoreCase("")) {
                String[] arguments = args.split(";");
                int nbr = 0;
                for (String loc0 : action.split(",")) {
                    int actionId = Integer.parseInt(loc0);
                    String arg = arguments[nbr];
                    actions.add(new Action(actionId, arg, -1 + "", null));
                    nbr++;
                }
            }
        } catch (Exception e) {
            World.world.logger.error("Erreur avec l action et les args de la quete " + this.id + ".", e);
        }
    }

    public int getId() {
        return id;
    }

    public boolean isDelete() {
        return this.delete;
    }

    public NpcTemplate getNpcTemplate() {
        return npc;
    }

    public ArrayList<QuestObjective> getQuestObjectives() {
        return questObjectives;
    }

    private boolean haveRespectCondition(QuestPlayer questPlayer, QuestObjective questObjective) {
        switch (questObjective.getCondition()) {
            case "1": //Valider les etapes d'avant
                boolean loc2 = true;
                for (QuestObjective step : this.questObjectives) {
                    if (step != null && step.getId() != questObjective.getId() && !questPlayer.isQuestObjectiveIsValidate(step)) {
                        loc2 = false;
                    }
                }
                return loc2;

            case "0":
                return true;
        }
        return false;
    }

    public String getGmQuestDataPacket(Player player) {
        QuestPlayer questPlayer = player.getQuestPersoByQuest(this);
        int loc1 = getObjectifCurrent(questPlayer);
        int loc2 = getObjectifPrevious(questPlayer);
        int loc3 = getNextObjectif(QuestStep.getStepById(getObjectifCurrent(questPlayer)));
        StringBuilder str = new StringBuilder();
        str.append(id).append("|");
        str.append(loc1 > 0 ? loc1 : "");
        str.append("|");

        StringBuilder str_prev = new StringBuilder();
        boolean loc4 = true;
        // Il y a une exeption dans le code ici pour la seconde �tape de papotage
        for (QuestObjective qEtape : questObjectives) {
            if (qEtape.getObjectif() != loc1)
                continue;
            if (!haveRespectCondition(questPlayer, qEtape))
                continue;
            if (!loc4)
                str_prev.append(";");
            str_prev.append(qEtape.getId());
            str_prev.append(",");
            str_prev.append(questPlayer.isQuestObjectiveIsValidate(qEtape) ? 1 : 0);
            loc4 = false;
        }
        str.append(str_prev);
        str.append("|");
        str.append(loc2 > 0 ? loc2 : "").append("|");
        str.append(loc3 > 0 ? loc3 : "");
        if (npc != null) {
            str.append("|");
            str.append(npc.getInitQuestionId(player.getCurMap().getId())).append("|");
        }
        return str.toString();
    }

    public QuestObjective getCurrentQuestStep(QuestPlayer questPlayer) {
        for (QuestObjective step : getQuestObjectives()) {
            if (!questPlayer.isQuestObjectiveIsValidate(step)) {
                return step;
            }
        }
        return null;
    }

    private int getObjectifCurrent(QuestPlayer questPlayer) {
        for (QuestObjective step : questObjectives) {
            if (!questPlayer.isQuestObjectiveIsValidate(step)) {
                return step.getObjectif();
            }
        }
        return 0;
    }

    private int getObjectifPrevious(QuestPlayer questPlayer) {
        if (questStepList.size() == 1)
            return 0;
        else {
            int previous = 0;
            for (QuestStep qObjectif : questStepList) {
                if (qObjectif.getId() == getObjectifCurrent(questPlayer)) return previous;
                else previous = qObjectif.getId();
            }
        }
        return 0;
    }

    private int getNextObjectif(QuestStep questStep) {
        if (questStep == null)
            return 0;
        for (QuestStep objectif : questStepList) {
            if (objectif.getId() == questStep.getId()) {
                int index = questStepList.indexOf(objectif);
                if (questStepList.size() <= index + 1)
                    return 0;
                return questStepList.get(index + 1).getId();
            }
        }
        return 0;
    }

    public void applyQuest(Player player) {
        if (this.condition != null) {
            switch (this.condition.first) {
                case 1: // Niveau
                    if (player.getLevel() < this.condition.second) {
                        SocketManager.GAME_SEND_MESSAGE(player, player.getLang().trans("quest.quest.applyquest"));
                        return;
                    }
                    break;
            }
        }

        QuestPlayer questPlayer = new QuestPlayer(this.id, false, player.getId(), "");
        player.addQuestPerso(questPlayer);
        SocketManager.GAME_SEND_Im_PACKET(player, "054;" + this.id);
        SocketManager.GAME_SEND_MAP_NPCS_GMS_PACKETS(player.getGameClient(), player.getCurMap());

        if (!this.actions.isEmpty()) {
            for (Action aAction : this.actions) {
                aAction.apply(player, player, -1, -1);
            }
        }

        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
    }

    public void updateQuestData(Player player, boolean validation, int type) {
        QuestPlayer questPlayer = player.getQuestPersoByQuest(this);
        for (QuestObjective questObjective : this.questObjectives) {
            if (questObjective.getValidationType() != type || questPlayer.isQuestObjectiveIsValidate(questObjective)) //On a d�j� valid� la questEtape on passe
                continue;
            if (questObjective.getObjectif() != getObjectifCurrent(questPlayer) || !haveRespectCondition(questPlayer, questObjective))
                continue;

            boolean refresh = false;

            if (validation)
                refresh = true;
            switch (questObjective.getType()) {
                case 3://Donner item
                    if (player.getExchangeAction() != null && player.getExchangeAction().getType() ==
                            ExchangeAction.TALKING_WITH && player.getCurMap().getNpc((Integer) player
                            .getExchangeAction().getValue()).getTemplate().getId() == questObjective.getNpc().getId()) {
                        for (Entry<Integer, Integer> entry : questObjective.getItemNecessaryList().entrySet()) {
                            if (player.hasItemTemplate(entry.getKey(), entry.getValue(), false)) { //Il a l'item et la quantit�
                                player.removeByTemplateID(entry.getKey(), entry.getValue()); //On supprime donc
                                refresh = true;
                            }
                        }
                    }
                    break;

                case 0:
                case 1://Aller voir %
                case 9://Retourner voir %
                    if (questObjective.getCondition().equalsIgnoreCase("1")) { //Valider les questEtape avant
                        if (player.getExchangeAction() != null && player.getExchangeAction().getType() == ExchangeAction.TALKING_WITH && player.getCurMap().getNpc((Integer) player.getExchangeAction().getValue()).getTemplate().getId() == questObjective.getNpc().getId()) {
                            if (haveRespectCondition(questPlayer, questObjective)) {
                                refresh = true;
                            }
                        }
                    } else {
                        if (player.getExchangeAction() != null && player.getExchangeAction().getType() == ExchangeAction.TALKING_WITH && player.getCurMap().getNpc((Integer) player.getExchangeAction().getValue()).getTemplate().getId() == questObjective.getNpc().getId())
                            refresh = true;
                    }
                    break;

                case 6: // monstres
                    for (Entry<Integer, Short> entry : questPlayer.getMonsterKill().entrySet())
                        if (entry.getKey() == questObjective.getMonsterId() && entry.getValue() >= questObjective.getQua())
                            refresh = true;
                    break;

                case 10://Ramener prisonnier
                    if (player.getExchangeAction() != null && player.getExchangeAction().getType() == ExchangeAction.TALKING_WITH && player.getCurMap().getNpc((Integer) player.getExchangeAction().getValue()).getTemplate().getId() == questObjective.getNpc().getId()) {
                        GameObject follower = player.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR);
                        if (follower != null) {
                            Map<Integer, Integer> itemNecessaryList = questObjective.getItemNecessaryList();
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
                    final List<Integer> monsters = questObjective.getItemNecessaryList().entrySet().stream().map(Entry::getKey).collect(Collectors.toList());
                    final List<SoulStone> objects = new ArrayList<>();
                    final List<Integer> valid = new ArrayList<>();
                    final byte[] ok = {0};

                    player.getItems().values().stream().filter(object -> object.getTemplate().getType() == 85)
                            .forEach(object -> {
                        SoulStone soul = (SoulStone) object;
                        Iterator<Couple<Integer, Integer>> iterator = soul.getMonsters().iterator();

                        while (iterator.hasNext()) {
                            Couple<Integer, Integer> couple = iterator.next();

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
                QuestStep ansObjectif = QuestStep.getStepById(getObjectifCurrent(questPlayer));
                questPlayer.setQuestObjectiveValidate(questObjective);
                SocketManager.GAME_SEND_Im_PACKET(player, "055;" + id);
                if (haveFinish(questPlayer, ansObjectif)) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "056;" + id);
                    applyButinOfQuest(player, ansObjectif);
                    questPlayer.setFinish(true);
                } else {
                    if (getNextObjectif(ansObjectif) != 0) {
                        if (questPlayer.overQuestStep(ansObjectif))
                            applyButinOfQuest(player, ansObjectif);
                    }
                }
                ((PlayerData) DatabaseManager.get(PlayerData.class)).update(player);
            }
        }
    }

    private boolean haveFinish(QuestPlayer questPlayer, QuestStep questStep) {
        return questPlayer.overQuestStep(questStep) && getNextObjectif(questStep) == 0;
    }

    private void applyButinOfQuest(Player player, QuestStep questStep) {
        long xp; int kamas;

        if ((xp = questStep.getXp()) > 0) { //Xp a donner
            player.addXp(xp * ((int) Config.rateXp));
            SocketManager.GAME_SEND_Im_PACKET(player, "08;" + (xp * ((int) Config.rateXp)));
            SocketManager.GAME_SEND_STATS_PACKET(player);
        }

        if (questStep.getObjects().size() > 0) { //Item a donner
            for (Entry<Integer, Integer> entry : questStep.getObjects().entrySet()) {
                ObjectTemplate template = World.world.getObjTemplate(entry.getKey());
                int quantity = entry.getValue();
                GameObject object = template.createNewItem(quantity, false);

                if (player.addObjet(object, true)) {
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

        if (getNextObjectif(questStep) != questStep.getId()) { //On passe au nouveau objectif on applique les actions
            for (Action action : questStep.getActions()) {
                action.apply(player, null, 0, 0);
            }
        }
    }
}
