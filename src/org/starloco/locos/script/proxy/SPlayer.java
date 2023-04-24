package org.starloco.locos.script.proxy;

import org.classdump.luna.ByteString;
import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.lib.ArgumentIterator;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.entity.monster.MobGroupDef;
import org.starloco.locos.entity.monster.MonsterGroup;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.action.type.NpcDialogActionData;
import org.starloco.locos.game.world.World;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.quest.QuestObjective;
import org.starloco.locos.quest.QuestPlayer;
import org.starloco.locos.quest.QuestStep;
import org.starloco.locos.script.ScriptVM;
import org.starloco.locos.script.types.MetaTables;
import org.starloco.locos.util.Pair;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class SPlayer extends DefaultUserdata<Player> {
    private static final ImmutableTable META_TABLE = MetaTables.MetaTable(MetaTables.ReflectIndexTable(SPlayer.class));

    public SPlayer(Player userValue) {
        super(META_TABLE, userValue);
    }

    //region Basic stuff
    @SuppressWarnings("unused")
    private static int id(Player p) {
        return p.getId();
    }

    @SuppressWarnings("unused")
    private static String name(Player p) {
        return p.getName();
    }

    @SuppressWarnings("unused")
    private static int level(Player p) {
        return p.getLevel();
    }

    @SuppressWarnings("unused")
    private static int breed(Player p) {
        return p.getClasse();
    }

    @SuppressWarnings("unused")
    private static int gender(Player p) {
       return p.getSexe();
    }

    @SuppressWarnings("unused")
    private static int faction(Player p) {
        return p.getAlignment();
    }

    @SuppressWarnings("unused")
    private static void openBank(Player p) {
        p.openBank();
    }

    @SuppressWarnings("unused")
    private static Object getCtxVal(Player p, ArgumentIterator args) {
        String key = args.nextString().toString();
        return Optional.ofNullable(p.getExchangeAction()).map(a -> a.getContextValue(key)).orElse(null);
    }

    @SuppressWarnings("unused")
    private static boolean setCtxVal(Player p, ArgumentIterator args) {
        String key = args.nextString().toString();
        Object val = args.next();

        if(p.getExchangeAction() == null) return false;

        p.getExchangeAction().putContextValue(key, val);
        return true;
    }

    @SuppressWarnings("unused")
    private static boolean addXP(Player p, ArgumentIterator args) {
        Number xp = args.nextNumber();

        return p.addXp(xp.longValue());
    }

    @SuppressWarnings("unused")
    private static boolean isGhost(Player p) {
        return p.isGhost();
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
    @SuppressWarnings("unused")
    private static void sendInfoMsg(Player p, ArgumentIterator args) {
        int type = args.nextInt();
        if(type>9) throw new IllegalArgumentException("SPlayer:sendInfoMsg type param must be < 10");
        int msgId = args.nextInt();

        SocketManager.GAME_SEND_Im_PACKET(p, type+""+msgId);
    }
    //endregion

    //region Dialogs
    @SuppressWarnings("unused")
    private static void ask(Player p, ArgumentIterator args) {
        int question = args.nextInt();
        List<Integer> answersInts = ScriptVM.intsFromLuaTable(args.nextOptionalTable(null));
        ByteString param = args.nextOptionalString(null);

        NpcDialogActionData data = ((NpcDialogActionData) p.getExchangeAction().getValue());
        data.setQuestionId(question);
        data.setAnswers(answersInts);

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
        return Optional.ofNullable(p.getQuestPersoByQuestId(args.nextInt())).map(QuestPlayer::isFinished).orElse(false);
    }

    @SuppressWarnings("unused")
    private static boolean questOngoing(Player p, ArgumentIterator args) {
        return Optional.ofNullable(p.getQuestPersoByQuestId(args.nextInt())).map(s -> !s.isFinished()).orElse(false);
    }

    @SuppressWarnings("unused")
    private static boolean startQuest(Player p, ArgumentIterator args) {
        int id = args.nextInt();

        Quest q = Quest.quests.get(id);
        if (q == null) return false;

        if (p.getQuestPersoByQuestId(id) != null) return false;

        return q.apply(p);
    }

    @SuppressWarnings("unused")
    private static boolean completeObjective(Player p, ArgumentIterator args) {
        int qID = args.nextInt();
        int oID = args.nextInt();

        QuestPlayer qp = p.getQuestPersoByQuestId(qID);
        if(qp == null) return false;

        Quest q = qp.getQuest();
        if(q == null) return false;

        QuestObjective qo = q.getObjectives().stream().filter(o -> o.getId() == oID).findFirst().orElse(null);
        if(qo == null) return false;

        QuestStep qs = QuestStep.steps.get(q.getCurrentObjectiveId(qp));
        if(qs == null) return false;

        qp.setQuestObjectiveValidate(qo);
        SocketManager.GAME_SEND_Im_PACKET(p, "055;" + qID);

        if (q.getNextStep(qs) == 0) {
            // Quest finished
            SocketManager.GAME_SEND_Im_PACKET(p, "056;" + qID);
            q.applyButinOfQuest(p, qs);
            qp.setFinished(true);
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
    private static Pair<Integer,Integer> savedPosition(Player p) {
        return p.getSavePosition();
    }

    @SuppressWarnings("unused")
    private static void setSavedPosition(Player p, ArgumentIterator args) {
        int mapId = args.nextInt();
        int cellId = args.nextInt();

        p.setSavePos(mapId, cellId);
    }

    @SuppressWarnings("unused")
    private static int mapID(Player p) {
        return p.getCurMap().getId();
    }

    @SuppressWarnings("unused")
    private static SMap map(Player p) {
        return p.getCurMap().scripted();
    }

    @SuppressWarnings("unused")
    private static int cell(Player p) {
        return p.getCurCell().getId();
    }

    @SuppressWarnings("unused")
    private static int orientation(Player p) {
        return p.get_orientation();
    }

    @SuppressWarnings("unused")
    private static void teleport(Player p, ArgumentIterator args) {
        int mapID = args.nextInt();
        int cellID = args.nextInt();
        p.teleport(mapID, cellID);
    }

    @SuppressWarnings("unused")
    private static void compassTo(Player p, ArgumentIterator args) {
        int mapId = args.nextInt();
        GameMap map = World.world.getMap(mapId);

        if (p.getFight() != null) return;
        SocketManager.GAME_SEND_FLAG_PACKET(p, map);
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
        int quantity = args.nextOptionalInt(1);
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
    private static boolean addItem(Player p, ArgumentIterator args) {
        int itemID = args.nextInt();
        int quantity = args.nextOptionalInt(1);
        int pos = args.nextOptionalInt(Constant.ITEM_POS_NO_EQUIPED);
        boolean isPerfect = args.nextOptionalBoolean(false);
        boolean display = args.nextOptionalBoolean(true);

        boolean posAlreadyFilled = p.getEquippedObjects().stream()
            .anyMatch(i -> i.getPosition() == pos);
        if(posAlreadyFilled) return false;

        ObjectTemplate tmpl = World.world.getObjTemplate(itemID);
        GameObject item = tmpl.createNewItem(quantity, isPerfect);
        item.setPosition(pos);

        p.addItem(item, pos==Constant.ITEM_POS_NO_EQUIPED, display);
        return true;
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
    private static boolean tryLearnJob(Player p, ArgumentIterator args) {
        int jobID = args.nextInt();
        return p.tryLearnJob(jobID);
    }
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
        int faction = args.nextInt();
        boolean replace = args.nextOptionalBoolean(false);

        int current = p.getAlignment();
        if(current == faction) return true;

        // We're not allowed to replace an existing faction
        if(!replace && current != Constant.ALIGNEMENT_NEUTRE) return false;

        p.modifAlignement(faction);
        return true;
    }
    //endregion

    //region Other
    @SuppressWarnings("unused")
    private static void forceFight(Player player, ArgumentIterator args) {
        MobGroupDef def = MobGroupDef.Mapper.get().from(args.nextTable());
        GameMap map = player.getCurMap();
        MonsterGroup group = new MonsterGroup(0, map, def);
        map.startFightVersusMonstres(player, group);
    }
    //endregion
}
