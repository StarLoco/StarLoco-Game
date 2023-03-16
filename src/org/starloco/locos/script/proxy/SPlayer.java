package org.starloco.locos.script.proxy;

import org.classdump.luna.ByteString;
import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.lib.ArgumentIterator;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.quest.QuestObjective;
import org.starloco.locos.quest.QuestPlayer;
import org.starloco.locos.quest.QuestStep;
import org.starloco.locos.script.ScriptVM;
import org.starloco.locos.script.types.MetaTables;
import org.starloco.locos.util.Pair;

import java.util.List;
import java.util.Optional;

public class SPlayer extends DefaultUserdata<Player> {
    private static final ImmutableTable META_TABLE= MetaTables.MetaTable(MetaTables.ReflectIndexTable(SPlayer.class));

    public SPlayer(Player userValue) {
        super(META_TABLE, userValue);
    }

    //region Basic stuff
    @SuppressWarnings("unused")
    private static int gender(Player p) {
       return p.getSexe();
    }


    @SuppressWarnings("unused")
    private static void openBank(Player p) {
        p.openBank();
    }

    @SuppressWarnings("unused")
    private static void sendAction(Player p, ArgumentIterator args) {
        int actionID = args.nextInt();
        String actionIDStr = "";
        if(actionID != -1) actionIDStr = String.valueOf(actionID);

        int actionType = args.nextInt();
        ByteString actionValue = args.nextString();

        SocketManager.GAME_SEND_GA_PACKET(p.getGameClient(), actionIDStr, String.valueOf(actionType),  String.valueOf(p.getId()), actionValue.toString());
    }

    //endregion

    //region Dialogs
    @SuppressWarnings("unused")
    private static void ask(Player p, ArgumentIterator args) {
        int question = args.nextInt();
        List<Integer> answersInts = ScriptVM.intsFromLuaTable(args.nextOptionalTable(null));
        ByteString param = args.nextOptionalString(null);


        String paramVal = param == null ? null : p.getStringVar(param.toString());
        SocketManager.GAME_SEND_QUESTION_PACKET(p.getGameClient(), question, answersInts, paramVal);
    }

    @SuppressWarnings("unused")
    private static void endDialog(Player p) {
        if (p.getExchangeAction() == null || p.getExchangeAction().getType() != ExchangeAction.TALKING_WITH ){
            return;
        }
        p.setAway(false);
        p.setExchangeAction(null);
        SocketManager.GAME_SEND_END_DIALOG_PACKET(p.getGameClient());
    }
    //endregion

    //region Quests

    @SuppressWarnings("unused")
    private static boolean questAvailable(Player p, ArgumentIterator args) {
        return p.getQuestPersoByQuestId(args.nextInt()) == null;
    }

    @SuppressWarnings("unused")
    private static boolean questFinished(Player p, ArgumentIterator args) {
        return Optional.ofNullable(p.getQuestPersoByQuestId(args.nextInt())).map(QuestPlayer::isFinish).orElse(false);
    }

    @SuppressWarnings("unused")
    private static boolean questOngoing(Player p, ArgumentIterator args) {
        return Optional.ofNullable(p.getQuestPersoByQuestId(args.nextInt())).map(s -> !s.isFinish()).orElse(false);
    }

    @SuppressWarnings("unused")
    private static boolean startQuest(Player p, ArgumentIterator args) {
        int id = args.nextInt();

        Quest q = Quest.getQuestById(id);
        if (q == null) return false;

        if (p.getQuestPersoByQuestId(id) != null) return false;

        return q.applyQuest(p);
    }

    @SuppressWarnings("unused")
    private static boolean completeObjective(Player p, ArgumentIterator args) {
        int qID = args.nextInt();
        int oID = args.nextInt();

        QuestPlayer qp = p.getQuestPersoByQuestId(qID);
        if(qp == null) return false;

        Quest q = qp.getQuest();
        if(q == null) return false;

        QuestObjective qo = q.getQuestObjectives().stream().filter(o -> o.getId() == oID).findFirst().orElse(null);
        if(qo == null) return false;

        QuestStep qs = QuestStep.getStepById(q.getObjectifCurrent(qp));
        if(qs == null) return false;

        qp.setQuestObjectiveValidate(qo);
        SocketManager.GAME_SEND_Im_PACKET(p, "055;" + qID);

        if (q.getNextObjectif(qs) == 0) {
            // Quest finished
            SocketManager.GAME_SEND_Im_PACKET(p, "056;" + qID);
            q.applyButinOfQuest(p, qs);
            qp.setFinish(true);
        } else if (qp.overQuestStep(qs))  q.applyButinOfQuest(p, qs);

        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(p);
        return true;
    }

    //endregion

    //region Emotes
    @SuppressWarnings("unused")
    public static boolean hasEmote(Player p, ArgumentIterator args) {
        int emote = args.nextInt();
        return p.getEmotes().contains(emote);
    }

    @SuppressWarnings("unused")
    public static boolean learnEmote(Player p, ArgumentIterator args) {
        int emote = args.nextInt();
        return p.addStaticEmote(emote);
    }
    //endregion

    //region Geolocation (Maps)
    @SuppressWarnings("unused")
    private static Pair<Integer,Integer> savedPosition(Player p, ArgumentIterator args) {
        return p.getSavePosition();
    }

    @SuppressWarnings("unused")
    private static int mapID(Player p, ArgumentIterator args) {
        //  TODO: Replace with map():SMap
        return p.getCurMap().getId();
    }

    @SuppressWarnings("unused")
    private static void teleport(Player p, ArgumentIterator args) {
        int mapID = args.nextInt();
        int cellID = args.nextInt();
        p.teleport(mapID, cellID);
    }
    //endregion

    //region Currency
    @SuppressWarnings("unused")
    private static long kamas(Player p) {
        return p.getKamas();
    }

    @SuppressWarnings("unused")
    private static boolean modKamas(Player p, ArgumentIterator args) {
        int quantity = args.nextInt();
        return p.modKamasDisplay(quantity);
    }
    //endregion

    //region Inventory/Gear
    @SuppressWarnings("unused")
    public static SItem gearAt(Player p, ArgumentIterator args) {
        int pos = args.nextInt();
        return p.getEquippedObjects().stream()
                .filter(i -> i.getPosition() == pos)
                .findFirst().map(GameObject::scripted)
                .orElse(null);
    }

    @SuppressWarnings("unused")
    public static Pair<Integer, Integer> pods(Player p) {
        return new Pair<>(p.getPodUsed(), p.getMaxPod());
    }

    @SuppressWarnings("unused")
    private static SItem getItem(Player p, ArgumentIterator args) {
        int itemID = args.nextInt();
        int quantity = args.nextInt();
        GameObject item = p.getItemTemplate(itemID, quantity);
        if(item == null) {
            // No item return null
            return null;
        }
        return item.scripted();
    }

    @SuppressWarnings("unused")
    private static boolean consumeItem(Player p, ArgumentIterator args) {
        int itemID = args.nextInt();
        int quantity = args.nextInt();
        return p.removeItemByTemplateId(itemID, quantity, true);
    }


    @SuppressWarnings("unused")
    private static void addItem(Player p, ArgumentIterator args) {
        int itemID = args.nextInt();
        int quantity = args.nextInt();
        boolean isPerfect = args.nextOptionalBoolean(true);
        boolean display = args.nextOptionalBoolean(true);

        p.addItem(itemID, quantity, isPerfect, display);
    }

    @SuppressWarnings("unused")
    private static boolean tryBuyItem(Player p, ArgumentIterator args) {
        int itemID = args.nextInt();
        int unitPrice = args.nextInt();
        int quantity = args.nextOptionalInt(1);
        boolean isPerfect = args.nextOptionalBoolean(true);


        int totalPrice = unitPrice * quantity;
        if(!p.modKamasDisplay(-totalPrice)) return false;

        p.addItem(itemID, quantity, isPerfect, true);
        return true;
    }
    //endregion

    //region Jobs
    @SuppressWarnings("unused")
    private static int jobLevel(Player p, ArgumentIterator args) {
        int jobID = args.nextInt();
        return Optional.ofNullable(p.getMetiers().get(jobID)).map(JobStat::get_lvl).orElse(0);
    }

    @SuppressWarnings("unused")
    private static boolean addJobXP(Player p, ArgumentIterator args) {
        int jobID = args.nextInt();
        int xpDelta = args.nextInt();

        if(xpDelta<0) return false; // Not supported

        JobStat js = p.getMetiers().get(jobID);
        if(js == null) return false;
        js.addXp(p, xpDelta);
        return true;
    }
    //endregion

    //region Spells
    @SuppressWarnings("unused")
    private static int spellLevel(Player p, ArgumentIterator args) {
        int spellID = args.nextInt();
        return p.getSpells().stream()
                .filter(s -> s.getSpellID() == spellID).findFirst()
                .map(Spell.SortStats::getLevel).orElse(0);
    }

    @SuppressWarnings("unused")
    private static boolean setSpellLevel(Player p, ArgumentIterator args) {
        int spellID = args.nextInt();
        int level = args.nextInt();
        boolean modPoints = args.nextOptionalBoolean(false);

        return p.ensureSpellLevel(spellID, level, modPoints, false);
    }
    //endregion

    //region Factions
    @SuppressWarnings("unused")
    private static boolean setFaction(Player p, ArgumentIterator args) {
        byte faction = (byte)args.nextInt();
        boolean replace = args.nextOptionalBoolean(false);

        byte current = p.getAlignment();
        if(current == faction) return true;

        // We're not allowed to replace an existing faction
        if(!replace && current != Constant.ALIGNEMENT_NEUTRE) return false;

        p.modifAlignement(faction);
        return true;
    }
    //endregion
}
