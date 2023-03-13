package org.starloco.locos.entity.npc;

import org.classdump.luna.Table;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.data.game.SaleOffer;
import org.starloco.locos.database.data.game.SaleOffer.Currency;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.other.Action;
import org.starloco.locos.other.Dopeul;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.quest.QuestObjective;
import org.starloco.locos.quest.QuestPlayer;
import org.starloco.locos.script.NpcScriptVM;

import java.util.*;
import java.util.stream.Collectors;

import static org.starloco.locos.script.ScriptVM.*;

public class NpcTemplate {
    private final int id, gfxId, scaleX, scaleY, sex, color1, color2, color3;
    private final int[] accessories;
    private final int customArtWork;
    // private Quest quest;
    private final Table scriptVal;
    private final byte flags;

    public final LegacyData legacy;

    public NpcTemplate(int id, int bonus, int gfxId, int scaleX, int scaleY, int sex, int color1, int color2, int color3, String accessories, int extraClip, int customArtWork, String questions,
                       String sales, String quest, String exchanges, String path, byte flags) {
        this.scriptVal = null;
        this.legacy = new LegacyData(id, questions, sales, quest, exchanges, path);
        this.id = id;
        this.gfxId = gfxId;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.sex = sex;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.accessories = Arrays.stream(accessories.split(",")).mapToInt(s -> Integer.parseInt(s, 16)).toArray();
        this.customArtWork = customArtWork;
        this.flags = flags;
    }

    public NpcTemplate(Table val) {
        this.scriptVal = val;
        this.legacy = null;

        this.id = rawInt(val, "id");
        this.gfxId = rawInt(val, "gfxID");
        this.sex = rawInt(val, "gender");
        this.scaleX = rawInt(val, "scaleX");
        this.scaleY = rawInt(val, "scaleY");
        Table colors = (Table)val.rawget("colors");
        this.color1 = rawInt(colors,1);
        this.color2 = rawInt(colors,2);
        this.color3 = rawInt(colors,3);
        Table accessories = (Table)val.rawget("accessories");
        this.accessories = intArrayFromLuaTable(accessories);
        this.customArtWork = rawInt(val,"customArtwork");
        this.flags = (byte) rawInt(val, "flags");
    }


    public int getId() {
        return id;
    }

    public int getGfxId() {
        return gfxId;
    }

    public int getScaleX() {
        return scaleX;
    }

    public int getScaleY() {
        return scaleY;
    }

    public int getSex() {
        return sex;
    }

    public int getColor1() {
        return color1;
    }

    public int getColor2() {
        return color2;
    }

    public int getColor3() {
        return color3;
    }

    public String encodeAccessories() {
        return Arrays.stream(accessories).mapToObj(Integer::toHexString).collect(Collectors.joining(","));
    }

    public int getCustomArtWork() {
        return customArtWork;
    }

    public boolean isBankClerk() {
        switch(id) {
            case 100:
            case 520:
            case 522:
            case 691:
            case 692:
                return true;
            default:
                return false;
        }
    }

    public void onCreateDialog(Player player) { this.onDialog(player,  0,0);}
    public void onDialog(Player player, int question, int response) {
        if(scriptVal == null) {
            legacy.onDialog(this, player, question, response);
            return;
        }
        Object onTalk = recursiveGet(scriptVal,"onTalk");
        if(onTalk == null) return;
        NpcScriptVM.getInstance().call(onTalk, scriptVal, player.Scripted(), response);
    }
    public List<SaleOffer> salesList(Player player) {
        if(scriptVal == null) {
            return legacy.sales;
        }
        Object salesList = recursiveGet(scriptVal,"salesList");
        if(salesList == null) return Collections.emptyList();

        Object[] ret = NpcScriptVM.getInstance().call(salesList, scriptVal, player.Scripted());

        if(ret == null || ret.length == 0) return Collections.emptyList();
        List<Object> offers = fromLuaTable((Table) ret[0]);

        return offers.stream().map(o -> {
            Table t = (Table) o;
            int itemID = rawInt(t,"item");
            ObjectTemplate item = World.world.getObjTemplate(itemID);
            if(item==null) throw new IllegalArgumentException(String.format("unknown item template #%d", itemID));

            int price = rawOptionalInt(t, "price", item.getPrice());
            int currencyID = rawOptionalInt(t, "currency", 0);

            // Not specified defaults to kamas
            if(currencyID==0) currencyID = -1; // currency -1 -> Kamas

            Currency currency;
            if(currencyID<0) currency = Currency.nonItemCurrency(-currencyID);
            else currency = Currency.itemCurrency(currencyID);

            return new SaleOffer(item, price, currency);
        }).collect(Collectors.toList());
    }

    public byte getFlags() {
        return flags;
    }

    @Deprecated
    public void setQuest(Quest quest) {
        if(this.legacy==null)return; // Scripted NPC
        this.legacy.quest = quest;
    }

    @Deprecated
    public Quest getQuest() {
        if(this.legacy==null)return null; // Scripted NPC
        return this.legacy.quest;
    }

    public int getExtraClip(Player p) {
        if(this.legacy==null) {
            // TODO Scripted NPC
            return -1;
        }
        Quest q = this.legacy.quest;
        if(q==null) return -1;

        QuestPlayer questPlayer = p.getQuestPersoByQuest(q);
        if (questPlayer == null) return 4;
        return -1;
    }

    public static class LegacyData {
        private final String path;
        // Chicken and egg problem in the DB: NPC references Quest that references NPC back. So this cannot be final.
        private Quest quest = null;
        private final Map<Integer,Integer> initQuestions = new HashMap<>();
        private final List<SaleOffer> sales = new ArrayList<>();
        private List<Couple<ArrayList<Couple<Integer, Integer>>, ArrayList<Couple<Integer, Integer>>>> exchanges;

        public LegacyData(int npcID, String questions, String sales, String quest, String exchanges, String path) {
            this.path = path;

            if (!quest.equalsIgnoreCase("")) this.quest = Quest.getQuestById(Integer.parseInt(quest));

            if (questions.split("\\|").length > 1) {
                for (String question : questions.split("\\|")) {
                    try {
                        initQuestions.put(Integer.parseInt(question.split(",")[0]), Integer.parseInt(question.split(",")[1]));
                    } catch (Exception e) {
                        e.printStackTrace();
                        World.world.logger.error("#1# Erreur sur une question id sur le PNJ d'id : " + npcID);
                    }
                }
            } else {
                if (questions.equalsIgnoreCase("")) this.initQuestions.put(-1, -1);
                else this.initQuestions.put(-1, Integer.parseInt(questions));
            }

            if (!sales.equals("")) {
                for (String obj : sales.split(",")) {
                    try {
                        ObjectTemplate template = World.world.getObjTemplate(Integer.parseInt(obj));
                        if (template != null)
                            this.sales.add(new SaleOffer(template, template.getPrice()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        World.world.logger.error("#2# Erreur sur un item en vente sur le PNJ d'id : " + npcID);
                    }
                }
            }

            if(!exchanges.equals("")) {
                try	{
                    this.exchanges = new ArrayList<>();
                    for(String data : exchanges.split("\\~")) {
                        ArrayList<Couple<Integer, Integer>> gives = new ArrayList<>(), gets = new ArrayList<>();

                        String[] split = data.split("\\|");
                        String give = split[1], get = split[0];

                        for(String obj : give.split("\\,")) {
                            split = obj.split("\\:");
                            gives.add(new Couple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                        }

                        for(String obj : get.split("\\,")) {
                            split = obj.split("\\:");
                            gets.add(new Couple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                        }
                        this.exchanges.add(new Couple<>(gets, gives));
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    World.world.logger.error("#3# Erreur sur l'exchanges sur le PNJ d'id : " + npcID);
                }
            }
        }

        public int getInitQuestionId(int id) {
            if (initQuestions.get(id) == null) {
                // Just return the 1st one
                for (Integer entry : initQuestions.values()) return entry;
            }
            return initQuestions.get(id);
        }

        public String getPath() {
            return path;
        }

        public List<ObjectTemplate> getAllItem() {
            return sales.stream().map(o -> o.itemTemplate).collect(Collectors.toList());
        }

        public boolean addItemVendor(ObjectTemplate template) {
            if (sales.stream().anyMatch(o -> o.itemTemplate == template))
                return false;
            sales.add(new SaleOffer(template));
            return true;
        }

        public boolean haveItem(int id) {
            return sales.stream().anyMatch(o -> o.itemTemplate.getId() == id);
        }


        public ArrayList<Couple<Integer,Integer>> checkGetObjects(ArrayList<Couple<Integer,Integer>> objects) {
            if(this.exchanges == null) return null;
            boolean ok;
            int multiple = 0, newMultiple = 0;

            for(Couple<ArrayList<Couple<Integer, Integer>>, ArrayList<Couple<Integer, Integer>>> entry0 : this.exchanges) {
                ok = true;
                for(Couple<Integer, Integer> entry1 : entry0.first) {
                    boolean ok1 = false;

                    for(Couple<Integer, Integer> entry2 : objects) {
                        if (entry1.first == World.world.getGameObject(entry2.first).getTemplate().getId() && (int) (entry2.second) % entry1.second == 0) {
                            ok1 = true;
                            newMultiple = entry2.second / entry1.second;

                            if(multiple == 0 || newMultiple == multiple) {
                                multiple = newMultiple;
                            } else {
                                ok1 = false;
                            }
                        }
                    }

                    if(!ok1) {
                        ok = false;
                        break;
                    }
                }

                final int fMultiple = multiple;

                if(ok && objects.size() == entry0.first.size()) {
                    if (fMultiple != 1) {
                        return entry0.second.stream().map(give -> new Couple<>(give.first, give.second * fMultiple))
                                .collect(Collectors.toCollection(ArrayList::new));
                    } else {
                        return entry0.second;
                    }
                } else {
                    multiple = newMultiple = 0;
                }
            }
            return null;
        }

        public void onDialog(NpcTemplate template, Player player, int question, int response) {
            try {
                if(response == 0)legacyCreateDialog(template, player);
                else legacyDialogResponse(template, player, question, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void legacyDialogResponse(NpcTemplate template, Player player, int questionID, int answerId) {
            NpcQuestion question = World.world.getNPCQuestion(questionID);
            NpcAnswer answer = World.world.getNpcAnswer(answerId);

            if (question == null || answer == null) {
                player.setIsOnDialogAction(-1);
                SocketManager.GAME_SEND_END_DIALOG_PACKET(player.getGameClient());
                return;
            }

            if (!player.getQuestPerso().isEmpty()) {
                for (QuestPlayer questPlayer : player.getQuestPerso().values()) {
                    if (questPlayer.isFinish() || questPlayer.getQuest() == null || questPlayer.getQuest().getNpcTemplate() == null)
                        continue;
                    for (QuestObjective questObjective : questPlayer.getQuest().getQuestObjectives()) {
                        if (questObjective == null || questPlayer.isQuestObjectiveIsValidate(questObjective) || template == null)
                            continue;

                        NpcTemplate questNpc = questObjective.getNpc();
                        NpcTemplate curNpc = template;

                        if (questNpc != null && curNpc != null && questNpc.getId() == curNpc.getId())
                            questPlayer.getQuest().updateQuestData(player, false, answerId);
                    }
                }
            }

            if (answerId == 6604 || answerId == 6605) {
                String stats = "", statsReplace = "";
                if (player.hasItemTemplate(10207, 1, false))
                    stats = player.getItemTemplate(10207).getTxtStat().get(Constant.STATS_NAME_DJ);
                try {
                    for(String answer0 : question.getAnwsers().split(";")) {
                        for (Action action : World.world.getNpcAnswer(Integer.parseInt(answer0)).getActions()) {
                            if ((action.getId() == 15 || action.getId() == 16) && player.hasItemTemplate(10207, 1, false)) {
                                for (String i : stats.split(",")) {
                                    GameMap map = player.getCurMap();
                                    if (map != null) {
                                        Npc npc0 = map.getNpc((Integer) player.getExchangeAction().getValue());
                                        if (npc0 != null && npc0.getTemplate() != null && Dopeul.parseConditionTrousseau(i.replace(" ", ""), npc0.getTemplate().getId(), map.getId())) {
                                            player.teleport(Short.parseShort(action.getArgs().split(",")[0]), Integer.parseInt(action.getArgs().split(",")[1]));
                                            switch(map.getId()) {
                                                case 9397:case 9538:
                                                    break;
                                                default:
                                                    statsReplace = i;
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }

                GameObject object = player.getItemTemplate(10207);
                if (answerId == 6605 && !statsReplace.isEmpty()) {
                    String newStats = "";
                    for (String i : stats.split(","))
                        if (!i.equals(statsReplace))
                            newStats += (newStats.isEmpty() ? i : "," + i);

                    object.getTxtStat().remove(Constant.STATS_NAME_DJ);
                    object.getTxtStat().put(Constant.STATS_NAME_DJ, newStats);
                }
                SocketManager.GAME_SEND_UPDATE_ITEM(player, object);
            } else if (answerId == 4628) {
                if (player.hasItemTemplate(9487, 1, false)) {
                    String date = player.getItemTemplate(9487, 1).getTxtStat().get(Constant.STATS_DATE);
                    long timeStamp = Long.parseLong(date);
                    if (System.currentTimeMillis() - timeStamp <= 1209600000) {
                        new Action(1, "5522", "", World.world.getMap((short) 10255)).apply(player, null, -1, -1);
                        return;
                    }
                }
                new Action(1, "5521", "", World.world.getMap((short) 10255)).apply(player, null, -1, -1);
                return;
            }

            boolean leave = answer.apply(player);

            if (!answer.isAnotherDialog()) {
                if (player.getIsOnDialogAction() == 1) {
                    player.setIsOnDialogAction(-1);
                } else {
                    if (leave) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(player.getGameClient());
                        if(player.getExchangeAction() != null && player.getExchangeAction().getType() == ExchangeAction.TALKING_WITH)
                            player.setExchangeAction(null);
                    }
                }
            }
        }
        private void legacyCreateDialog(NpcTemplate template, Player player) {
            int questionId = getInitQuestionId(player.getCurMap().getId());
            NpcQuestion question = World.world.getNPCQuestion(questionId);

            if (question == null) {
                SocketManager.GAME_SEND_END_DIALOG_PACKET(player.getGameClient());
                return;
            }
            if (template.id == 870) {
                Quest quest = Quest.getQuestById(185);
                if (quest != null) {
                    QuestPlayer questPlayer = player.getQuestPersoByQuest(quest);
                    if (questPlayer != null) {
                        if (questPlayer.isFinish()) {
                            SocketManager.GAME_SEND_END_DIALOG_PACKET(player.getGameClient());
                            return;
                        }
                    }
                }
            } else if (template.id == 891) {
                Quest quest = Quest.getQuestById(200);
                if (quest != null) {
                    if (player.getQuestPersoByQuest(quest) == null) {
                        quest.applyQuest(player);
                        Npc npc = player.getCurMap().getNpcByTemplateId(template.id);
                        player.send("GM|" + npc.encodeGM(true, player));
                    }
                }
            } else if (template.id == 925 && player.getCurMap().getId() == (short) 9402) {
                Quest quest = Quest.getQuestById(231);
                if (quest != null) {
                    QuestPlayer questPlayer = player.getQuestPersoByQuest(quest);
                    if (questPlayer != null) {
                        if (questPlayer.isFinish()) {
                            question = World.world.getNPCQuestion(4127);
                            if (question == null) {
                                SocketManager.GAME_SEND_END_DIALOG_PACKET(player.getGameClient());
                                return;
                            }
                        }
                    }
                }
            } else if (template.id == 577 && player.getCurMap().getId() == (short) 7596) {
                if (player.hasItemTemplate(2106, 1, false))
                    question = World.world.getNPCQuestion(2407);
            } else if (template.id == 1041 && player.getCurMap().getId() == (short) 10255 && questionId == 5516) {
                if (player.getAlignment() == 1) {// bontarien
                    if (player.getSexe() == 0)
                        question = World.world.getNPCQuestion(5519);
                    else
                        question = World.world.getNPCQuestion(5520);
                } else if (player.getAlignment() == 2) {// brakmarien
                    if (player.getSexe() == 0)
                        question = World.world.getNPCQuestion(5517);
                    else
                        question = World.world.getNPCQuestion(5518);
                } else { // Neutre ou mercenaire
                    question = World.world.getNPCQuestion(5516);
                }
            }

            ExchangeAction<Integer> exchangeAction = new ExchangeAction<>(ExchangeAction.TALKING_WITH, template.id);
            player.setExchangeAction(exchangeAction);

            SocketManager.GAME_SEND_QUESTION_PACKET(player.getGameClient(), question.parse(player));

            for (QuestPlayer questPlayer :  new ArrayList<>(player.getQuestPerso().values())) {
                boolean loc1 = false;
                for (QuestObjective questObjective : questPlayer.getQuest().getQuestObjectives())
                    if (questObjective.getNpc() != null && questObjective.getNpc().getId() == player.getCurMap().getNpc(exchangeAction.getValue()).getTemplate().getId())
                        loc1 = true;

                Quest quest = questPlayer.getQuest();
                if (quest == null || questPlayer.isFinish()) continue;
                NpcTemplate npcTemplate = quest.getNpcTemplate();
                if (npcTemplate == null && !loc1) continue;

                quest.updateQuestData(player, false, 0);
            }
        }
    }
}
