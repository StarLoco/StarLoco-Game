package org.starloco.locos.script.proxy;

import org.classdump.luna.ByteString;
import org.classdump.luna.Table;
import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.lib.ArgumentIterator;
import org.classdump.luna.runtime.LuaFunction;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.entity.monster.MobGroupDef;
import org.starloco.locos.entity.monster.MonsterGroup;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.action.type.NpcDialogActionData;
import org.starloco.locos.game.world.World;
import org.starloco.locos.job.Job;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.quest.QuestProgress;
import org.starloco.locos.script.DataScriptVM;
import org.starloco.locos.script.ScriptVM;
import org.starloco.locos.script.types.MetaTables;
import org.starloco.locos.util.Pair;

import java.util.Collections;
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
    private static void savePosition(Player p, ArgumentIterator args) {
        int mapId = args.nextInt();
        int cellId = args.nextInt();
        boolean send = args.nextOptionalBoolean(true);

        p.setSavePos(mapId, cellId);
        if(send) {
            SocketManager.GAME_SEND_Im_PACKET(p, "06");
        }
    }

    @SuppressWarnings("unused")
    private static void openZaap(Player p) {
        p.openZaapMenu();
    }

    @SuppressWarnings("unused")
    private static void openTrunk(Player p, ArgumentIterator args) {
        int cellID = args.nextInt();
        p.openTrunk(cellID);
    }

    @SuppressWarnings("unused")
    private static boolean setExchangeAction(Player p, ArgumentIterator args) {
        if(p.getExchangeAction() != null) return false;

        int t = args.nextInt();
        Object v = args.nextOptionalAny(null);

        p.setExchangeAction(new ExchangeAction<>((byte) t, v));
        return true;
    }

    @SuppressWarnings("unused")
    private static boolean clearExchangeAction(Player p, ArgumentIterator args) {
        int t = args.nextInt();

        ExchangeAction<?> a = p.getExchangeAction();
        if(a.getType() != t) {
            return false;
        }

        p.setExchangeAction(null);
        return true;
    }

    @SuppressWarnings("unused")
    private static void useCraftSkill(Player p, ArgumentIterator args) {
        int skillId = args.nextInt();
        int ingredientsCount = args.nextInt();

        p.useCraftSkill(skillId, ingredientsCount);
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
        long xp = args.nextInteger();
        boolean show = args.nextOptionalBoolean(true);

        if(show)SocketManager.GAME_SEND_Im_PACKET(p, "08;" + xp);
        p.addXp(xp);
        return true;
    }


    @SuppressWarnings("unused")
    private static int life(Player p) {
        return p.getCurPdv();
    }

    @SuppressWarnings("unused")
    private static int maxLife(Player p) {
        return p.getMaxPdv();
    }

    @SuppressWarnings("unused")
    private static void modLife(Player p, ArgumentIterator args) {
        int life = args.nextInt();

        p.setPdv(p.getCurPdv() + life);
    }

    @SuppressWarnings("unused")
    private static void setLifePercent(Player p, ArgumentIterator args) {
        int percent = args.nextInt();

        p.setPdv((int) ((p.getMaxPdv() * 100L) / percent));
    }

    @SuppressWarnings("unused")
    private static int energy(Player p) {
        return p.getEnergy();
    }

    @SuppressWarnings("unused")
    private static void modEnergy(Player p, ArgumentIterator args) {
        int energy = args.nextInt();

        int newEnergy = Math.min(10000, Math.max(0, p.getEnergy() + energy));
        p.setEnergy(newEnergy);

        if(newEnergy == 0) p.setGhost();
    }

    @SuppressWarnings("unused")
    private static boolean isGhost(Player p) {
        return p.isGhost();
    }

    @SuppressWarnings("unused")
    private static boolean resurrect(Player p) {
        if(!p.isGhost()) return false;
        p.setAlive();
        return true;
    }

    @SuppressWarnings("unused")
    private static void sendAction(Player p, ArgumentIterator args) {
        int actionID = args.nextInt();
        String actionIDStr = "";
        if(actionID != -1) actionIDStr = String.valueOf(actionID);

        int actionType = args.nextInt();
        String actionValue = args.nextString().toString();

        SocketManager.GAME_SEND_GA_PACKET(p.getGameClient(), actionIDStr, String.valueOf(actionType),  String.valueOf(p.getId()), actionValue.toString());
    }

    @SuppressWarnings("unused")
    private static void sendInfoMsg(Player p, ArgumentIterator args) {
        int type = args.nextInt();
        if(type>9) throw new IllegalArgumentException("SPlayer:sendInfoMsg type param must be < 10");
        int msgId = args.nextInt();

        SocketManager.GAME_SEND_Im_PACKET(p, type+String.valueOf(msgId));
    }

    @SuppressWarnings("unused")
    private static void startScenario(Player p, ArgumentIterator args) {
        int id = args.nextInt();
        ByteString date = args.nextString();
        LuaFunction<?,?,?,?,?> onEnd = args.nextFunction();

        p.startScenario(id, date.toString(), (player, succeed) -> DataScriptVM.getInstance().call(onEnd, player.scripted(), succeed));
    }

    @SuppressWarnings("unused")
    private static void openDocument(Player p, ArgumentIterator args) {
        int id = args.nextInt();
        String date = args.nextString().toString();

        p.openDocument(id, date);
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

    @SuppressWarnings("unused")
    private static void pauseDialog(Player p) {
        if (p.getExchangeAction() == null || p.getExchangeAction().getType() != ExchangeAction.TALKING_WITH ){
            return;
        }
        p.setAway(false);
        p.setExchangeAction(null);
        SocketManager.GAME_SEND_PAUSE_DIALOG_PACKET(p.getGameClient());
    }
    //endregion

    //region Quests (Private)

    @SuppressWarnings("unused")
    private static boolean _questAvailable(Player p, ArgumentIterator args) {
        return p.getQuestProgress(args.nextInt()) == null;
    }

    @SuppressWarnings("unused")
    private static boolean _questFinished(Player p, ArgumentIterator args) {
        return Optional.ofNullable(p.getQuestProgress(args.nextInt())).map(QuestProgress::isFinished).orElse(false);
    }

    @SuppressWarnings("unused")
    private static boolean _questOngoing(Player p, ArgumentIterator args) {
        return Optional.ofNullable(p.getQuestProgress(args.nextInt())).map(s -> !s.isFinished()).orElse(false);
    }

    @SuppressWarnings("unused")
    private static Table _ongoingQuests(Player p) {
        return ScriptVM.listOf(p.getQuestProgressions().map(QuestProgress::getQuestId));
    }

    @SuppressWarnings("unused")
    private static boolean _startQuest(Player p, ArgumentIterator args) {
        int id = args.nextInt();
        int sId = args.nextInt();
        boolean isAccountQuest = args.nextOptionalBoolean(false);

        if (p.getQuestProgress(id) != null) return false;

        int progressOwnerId = isAccountQuest? QuestProgress.NO_PLAYER_ID: p.getId();

        p.addQuestProgression(new QuestProgress(p.getAccID(), progressOwnerId, id, sId));

        SocketManager.GAME_SEND_Im_PACKET(p, "054;" + id);
        p.saveQuestProgress();
        return true;
    }

    @SuppressWarnings("unused")
    private static int _currentStep(Player p, ArgumentIterator args) {
        int qID = args.nextInt();

        QuestProgress qp = p.getQuestProgress(qID);
        if(qp == null) return 0;
        if(qp.isFinished()) return 0;

        return qp.getCurrentStep();
    }
    @SuppressWarnings("unused")
    private static Table _completedObjectives(Player p, ArgumentIterator args) {
        int qID = args.nextInt();

        QuestProgress qp = p.getQuestProgress(qID);
        if(qp.isFinished()) return null;

        return ScriptVM.listOf(qp.getCompletedObjectives().stream());
    }

    @SuppressWarnings("unused")
    private static boolean _completeObjective(Player p, ArgumentIterator args) {
        int qID = args.nextInt();
        int oID = args.nextInt();

        QuestProgress qp = p.getQuestProgress(qID);
        if(qp.isFinished()) return false;

        if(qp.hasCompletedObjective(oID)) return false;

        qp.completeObjective(oID);
        SocketManager.GAME_SEND_Im_PACKET(p, "055;" + qID);

        p.saveQuestProgress();
        return true;
    }

    @SuppressWarnings("unused")
    private static boolean _setCurrentStep(Player p, ArgumentIterator args) {
        int qID = args.nextInt();
        int sID = args.nextInt();

        QuestProgress qp = p.getQuestProgress(qID);
        if(qp.isFinished()) return false;
        if(qp.getCurrentStep() == sID) return false;

        qp.setCurrentStep(sID);

        p.saveQuestProgress();
        return true;
    }

    @SuppressWarnings("unused")
    private static boolean _completeQuest(Player p, ArgumentIterator args) {
        int qID = args.nextInt();
        boolean remove = args.nextOptionalBoolean(false);

        QuestProgress qp = p.getQuestProgress(qID);
        if(qp.isFinished()) return false;

        SocketManager.GAME_SEND_Im_PACKET(p, "056;" + qID);
        if(remove) {
            p.delQuestProgress(qp);
            return true;
        }
        qp.markFinished();
        p.saveQuestProgress();

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
        boolean sendIm = args.nextOptionalBoolean(true);

        p.setSavePos(mapId, cellId);
        if(sendIm) {
            SocketManager.GAME_SEND_Im_PACKET(p, "06");
        }
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
    private static boolean consumeItemGUID(Player p, ArgumentIterator args) {
        int itemGUID = args.nextInt();
        int quantity = args.nextInt();
        return p.removeItem(itemGUID, quantity, true, true);
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

    @SuppressWarnings("unused")
    private static void showReceivedItem(Player p, ArgumentIterator args) {
        int actorID = args.nextInt();
        int quantity = args.nextInt();

        p.showReceivedItem(actorID, quantity);
    }
    //endregion

    //region Jobs
    @SuppressWarnings("unused")
    private static Table jobs(Player p, ArgumentIterator args) {
        return ScriptVM.listOf(p.getMetiers().values().stream().map(JobStat::getTemplate).map(Job::getId));
    }

    @SuppressWarnings("unused")
    private static boolean tryLearnJob(Player p, ArgumentIterator args) {
        int jobID = args.nextInt();
        return p.tryLearnJob(jobID);
    }

    @SuppressWarnings("unused")
    private static boolean tryUnlearnJob(Player p, ArgumentIterator args) {
        int jobID = args.nextInt();
        return p.unlearnJob(jobID);
    }

    @SuppressWarnings("unused")
    private static boolean canLearnJob(Player p, ArgumentIterator args) {
        int jobID = args.nextInt();
        boolean send = args.nextOptionalBoolean(false);
        return p.canLearnJob(jobID, send);
    }

    @SuppressWarnings("unused")
    private static int jobLevel(Player p, ArgumentIterator args) {
        int jobID = args.nextInt();

        return Optional.ofNullable(p.getMetierByID(jobID))
            .map(JobStat::get_lvl)
            .orElse(0);
    }

    @SuppressWarnings("unused")
    private static boolean addJobXP(Player p, ArgumentIterator args) {
        int jobID = args.nextInt();
        int xpDelta = args.nextInt();
        boolean send = args.nextOptionalBoolean(true);

        if(xpDelta<0) return false; // Not supported

        JobStat js = p.getMetierByID(jobID);
        if(js == null) return false;
        js.addXp(p, xpDelta);

        SocketManager.GAME_SEND_JX_PACKET(p, Collections.singletonList(js));
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

    @SuppressWarnings("unused")
    private static void spellResetPanel(Player p) {
        p.spellResetPanel();
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

    //region Stats
    @SuppressWarnings("unused")
    private static int baseStat(Player p, ArgumentIterator args) {
        int statID = args.nextInt();
                return p.getStats().getEffect(statID);
    }

    @SuppressWarnings("unused")
    private static int modScrollStat(Player p, ArgumentIterator args) {
        int statID = args.nextInt();
        int val = args.nextInt();

        return p.getStatsParcho().addOneStat(statID, val);
    }

    @SuppressWarnings("unused")
    private static void resetStats(Player p, ArgumentIterator args) {
        boolean includeScrolls = args.nextOptionalBoolean(false);

        p.resetStats(includeScrolls);
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
