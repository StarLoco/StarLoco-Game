package org.starloco.locos.job;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.entity.InteractiveObject;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.GameClient;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.action.GameAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.job.maging.Rune;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Logging;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.util.RandomStats;
import org.starloco.locos.util.TimerWaiter;

import java.util.*;
import java.util.Map.Entry;

public class JobAction {

    public Map<Integer, Integer> ingredients = new TreeMap<>(), lastCraft = new TreeMap<>();
    public Player player;
    public String data = "";
    public boolean broke = false, broken = false, isRepeat = false;
    private final int id;
    private int min = 1, max = 1;
    private final boolean isCraft;
    private int chan = 100, time = 0, xpWin = 0;
    private JobStat SM;
    private JobCraft jobCraft;
    public JobCraft oldJobCraft;
    private int reConfigingRunes = -1;

    public JobAction(int sk, int min, int max, boolean craft, int arg, int xpWin) {
        this.id = sk;
        this.min = min;
        this.max = max;
        this.isCraft = craft;
        this.xpWin = xpWin;
        if (craft) this.chan = arg;
        else this.time = arg;
    }

    public int getId() {
        return this.id;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public boolean isCraft() {
        return this.isCraft;
    }

    public int getChance() {
        return this.chan;
    }

    public int getTime() {
        return this.time;
    }

    public int getXpWin() {
        return this.xpWin;
    }

    public JobStat getJobStat() {
        return this.SM;
    }

    public JobCraft getJobCraft() {
        return this.jobCraft;
    }

    public void setJobCraft(JobCraft jobCraft) {
        this.jobCraft = jobCraft;
    }

    public void startCraft(Player P) {
        this.jobCraft = new JobCraft(this, P);
    }

    public void startAction(Player P, InteractiveObject IO, GameAction GA, GameCase cell, JobStat SM) {
        this.SM = SM;
        this.player = P;

        if (P.getObjetByPos(Constant.ITEM_POS_ARME) != null && SM.getTemplate().getId() == 36) {
            if (World.world.getMetier(36).isValidTool(P.getObjetByPos(Constant.ITEM_POS_ARME).getTemplate().getId())) {
                int dist = PathFinding.getDistanceBetween(P.getCurMap(), P.getCurCell().getId(), cell.getId());
                int distItem = JobConstant.getDistCanne(P.getObjetByPos(Constant.ITEM_POS_ARME).getTemplate().getId());
                if (distItem < dist) {
                    SocketManager.GAME_SEND_MESSAGE(P, "Vous êtes trop loin pour pouvoir pécher ce poisson !");
                    SocketManager.GAME_SEND_GA_PACKET(P.getGameClient(), "", "0", "", "");
                    P.setExchangeAction(null);
                    P.setDoAction(false);
                    return;
                }
            }
        }
        if (!this.isCraft) {
            P.getGameClient().action = System.currentTimeMillis();
            IO.setInteractive(false);
            IO.setState(JobConstant.IOBJECT_STATE_EMPTYING);
            SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(P.getCurMap(), cell);
            if(P.walkFast) {
                TimerWaiter.addNext(() -> SocketManager.GAME_SEND_GA_PACKET_TO_MAP(P.getCurMap(), "" + GA.id, 501, P.getId() + "", cell.getId() + "," + this.time), 500);
                P.getLang().trans("jobaction.disable.walkfast");
            } else {
                SocketManager.GAME_SEND_GA_PACKET_TO_MAP(P.getCurMap(), "" + GA.id, 501, P.getId() + "", cell.getId() + "," + this.time);
            }
        } else {
            P.setAway(true);
            IO.setState(JobConstant.IOBJECT_STATE_EMPTYING);
            P.setExchangeAction(new ExchangeAction<>(ExchangeAction.CRAFTING, this));
            SocketManager.GAME_SEND_ECK_PACKET(P, 3, this.min + ";" + this.id);
            SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(P.getCurMap(), cell);
        }
    }

    public void startAction(Player P, InteractiveObject IO, GameAction GA, GameCase cell) {
        this.player = P;
        P.setAway(true);
        IO.setState(JobConstant.IOBJECT_STATE_EMPTYING);//FIXME trouver la bonne valeur
        P.setExchangeAction(new ExchangeAction<>(ExchangeAction.CRAFTING, this));
        SocketManager.GAME_SEND_ECK_PACKET(P, 3, this.min + ";" + this.id);//this.min => Nbr de Case de l'interface
        SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(P.getCurMap(), cell);
    }

    public void endAction(Player player, InteractiveObject IO, GameAction GA, GameCase cell) {
        if(!this.isCraft && player.getGameClient().action != 0) {
            if(System.currentTimeMillis() - player.getGameClient().action < this.time - 500) {
                /*String ip = player.getAccount().getCurrentIp();
                ((BanIpData) DatabaseManager.get(BanIpData.class)).delete(ip);
                ((BanIpData) DatabaseManager.get(BanIpData.class)).insert(ip);
                for (Account a : World.world.getAccountsByIp(ip)) {
                    if (!a.getLastIP().equalsIgnoreCase(ip))
                        continue;*/

                    /*a.setBanned(true);
                    if(a.getCurrentPlayer() != null)
                        a.getGameClient().kick();*/
                //}
                player.getGameClient().kick();
                return;
            }
        }

        player.setDoAction(false);
        if (IO == null)
            return;
        if (!this.isCraft) {
            IO.setState(3);
            IO.disable();
            SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(player.getCurMap(), cell);
            int qua = (this.max > this.min ? Formulas.getRandomValue(this.min, this.max) : this.min);

            if (SM.getTemplate().getId() == 36) {
                if (qua > 0)
                    SM.addXp(player, (long) (this.getXpWin() * Config.rateJob * World.world.getConquestBonus(player)));
            } else
                SM.addXp(player, (long) (this.getXpWin() * Config.rateJob * World.world.getConquestBonus(player)));

            int tID = JobConstant.getObjectByJobSkill(this.id);

            if (SM.getTemplate().getId() == 36 && qua > 0) {
                if (Formulas.getRandomValue(1, 500) <= 4) {
                    int id = JobConstant.getPoissonRare(tID);
                    if (id != -1) {
                        ObjectTemplate template = World.world.getObjTemplate(id);
                        if (template != null) {
                            GameObject object = template.createNewItem(qua, false);
                            if (player.addObjet(object, true))
                                World.world.addGameObject(object);
                        }
                    }
                }
            }


            ObjectTemplate T = World.world.getObjTemplate(tID);
            if (T == null)
                return;
            GameObject O = T.createNewItem(qua, false);

            if (player.addObjet(O, true))
                World.world.addGameObject(O);
            SocketManager.GAME_SEND_IQ_PACKET(player, player.getId(), qua);
            SocketManager.GAME_SEND_Ow_PACKET(player);

            if ((player.getMetierBySkill(this.id).get_lvl() >= 30 && Formulas.getRandomValue(1, 50) > 48)) {
                for (int[] protector : JobConstant.JOB_PROTECTORS) {
                    if (tID == protector[1]) {
                        int monsterLvl = JobConstant.getProtectorLvl(player.getLevel());
                        player.getCurMap().startFightVersusProtectors(player, new Monster.MobGroup(player.getCurMap().nextObjectId, player.getCurMap(), cell.getId(), protector[0] + "," + monsterLvl + "," + monsterLvl));
                        player.getCurMap().nextObjectId--;
                        break;
                    }
                }
            }
        }
        player.setAway(false);
    }

    private int addCraftObject(Player player, GameObject newObj) {
        for (Entry<Integer, GameObject> entry : player.getItems().entrySet()) {
            GameObject obj = entry.getValue();
            if (obj.getTemplate().getId() == newObj.getTemplate().getId() && obj.getTxtStat().equals(newObj.getTxtStat())
                    && obj.getStats().isSameStats(newObj.getStats()) && obj.getPosition() == Constant.ITEM_POS_NO_EQUIPED) {
                obj.setQuantity(obj.getQuantity() + newObj.getQuantity());//On ajoute QUA item a la quantit� de l'objet existant
                SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(player, obj);
                return obj.getGuid();
            }
        }

        player.getItems().put(newObj.getGuid(), newObj);
        SocketManager.GAME_SEND_OAKO_PACKET(player, newObj);
        World.world.addGameObject(newObj);
        return -1;
    }

    public void addIngredient(Player player, int id, int quantity) {
        int oldQuantity = this.ingredients.get(id) == null ? 0 : this.ingredients.get(id);
        if(quantity < 0) if(- quantity > oldQuantity) return;

        this.ingredients.remove(id);
        oldQuantity += quantity;

        if (oldQuantity > 0) {
            this.ingredients.put(id, oldQuantity);
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(player, 'O', "+", id + "|" + oldQuantity);
        } else {
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(player, 'O', "-", id + "");
        }
    }

    public byte sizeList(Map<Player, ArrayList<Couple<Integer, Integer>>> list) {
        byte size = 0;

        for (ArrayList<Couple<Integer, Integer>> entry : list.values()) {
            for (Couple<Integer, Integer> couple : entry) {
                GameObject object = World.world.getGameObject(couple.first);
                if (object != null) {
                    ObjectTemplate objectTemplate = object.getTemplate();
                    if (objectTemplate != null && objectTemplate.getId() != 7508) size++;
                }
            }
        }
        return size;
    }

    public void putLastCraftIngredients() {
        if (this.player == null || this.lastCraft == null || !this.ingredients.isEmpty()) return;

        this.ingredients.clear();
        this.ingredients.putAll(this.lastCraft);
        this.ingredients.entrySet().stream().filter(e -> World.world.getGameObject(e.getKey()) != null)
                .filter(e -> !(World.world.getGameObject(e.getKey()).getQuantity() < e.getValue()))
                .forEach(e -> SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player, 'O', "+", e.getKey() + "|" + e.getValue()));
    }

    public void resetCraft() {
        this.ingredients.clear();
        this.lastCraft.clear();
        this.oldJobCraft = null;
        this.jobCraft = null;
    }

    public boolean craftPublicMode(Player crafter, Player receiver, Map<Player, ArrayList<Couple<Integer, Integer>>> list) {
        if (!this.isCraft) return false;

        this.player = crafter;
        JobStat SM = this.player.getMetierBySkill(this.id);
        boolean signed = false;

        if (this.id == 1 || this.id == 113 || this.id == 115 || this.id == 116 || this.id == 117 || this.id == 118 || this.id == 119 || this.id == 120 || (this.id >= 163 && this.id <= 169)) {
            this.SM = SM;
            //craftMaging1(isRepeat, 0);
            return true;
        }

        Map<Integer, Integer> items = new HashMap<>();

        for (Entry<Player, ArrayList<Couple<Integer, Integer>>> entry : list.entrySet()) {
            Player player = entry.getKey();

            for (Couple<Integer, Integer> e : entry.getValue()) {
                if (!player.hasItemGuid(e.first)) {
                    SocketManager.GAME_SEND_Ec_PACKET(player, "EI");
                    SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                    return false;
                }

                GameObject gameObject = World.world.getGameObject(e.first);
                if (gameObject == null) {
                    SocketManager.GAME_SEND_Ec_PACKET(player, "EI");
                    SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                    return false;
                }
                if (gameObject.getQuantity() < e.second) {
                    SocketManager.GAME_SEND_Ec_PACKET(player, "EI");
                    SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                    return false;
                }

                int newQua = gameObject.getQuantity() - e.second;

                if (newQua < 0)
                    return false;

                if (newQua == 0) {
                    player.removeItem(e.first);
                    World.world.removeGameObject(e.first);
                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, e.first);
                } else {
                    gameObject.setQuantity(newQua);
                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(player, gameObject);
                }

                if(items.containsKey(gameObject.getTemplate().getId())) {
                    int template = gameObject.getTemplate().getId();
                    int quantity = e.second + items.get(template);
                    items.remove(template);
                    items.put(template, quantity);
                } else {
                    items.put(gameObject.getTemplate().getId(), e.second);
                }
            }
        }

        SocketManager.GAME_SEND_Ow_PACKET(this.player);


        //Rune de signature
        if (items.containsKey(7508))
            if (SM.get_lvl() == 100)
                signed = true;

        items.remove(7508);
        int template = World.world.getObjectByIngredientForJob(SM.getTemplate().getListBySkill(this.id), items);

        if (template == -1 || !SM.getTemplate().canCraft(this.id, template)) {
            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
            receiver.send("EcEI");
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");
            items.clear();
            return false;
        }

        boolean success = JobConstant.getChanceByNbrCaseByLvl(SM.get_lvl(), items.size()) >= Formulas.getRandomValue(1, 100);

        if (Logging.USE_LOG)
            Logging.getInstance().write("SecureCraft", this.player.getName() + " à crafter avec " + (success ? "SUCCES" : "ECHEC") + " l'item " + template + " (" + World.world.getObjTemplate(template).getName() + ") pour " + receiver.getName());
        if (!success) {
            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EF");
            SocketManager.GAME_SEND_Ec_PACKET(receiver, "EF");
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-" + template);
            SocketManager.GAME_SEND_Im_PACKET(this.player, "0118");
        } else {
            GameObject newObj = World.world.getObjTemplate(template).createNewItem(1, false);
            if (signed) newObj.addTxtStat(988, this.player.getName());
            int guid = this.addCraftObject(receiver, newObj);
            if(guid == -1) guid = newObj.getGuid();
            String stats = newObj.parseStatsString();

            this.player.send("ErKO+" + guid + "|1|" + template + "|" + stats);
            receiver.send("ErKO+" + guid + "|1|" + template + "|" + stats);
            this.player.send("EcK;" + template + ";T" + receiver.getName() + ";" + stats);
            receiver.send("EcK;" + template + ";B" + crafter.getName() + ";" + stats);

            SocketManager.GAME_SEND_Ow_PACKET(this.player);
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+" + template);
        }

        int winXP = Formulas.calculXpWinCraft(SM.get_lvl(), this.ingredients.size()) * Config.rateJob;
        if (SM.getTemplate().getId() == 28 && winXP == 1)
            winXP = 10;
        if (success) {
            SM.addXp(this.player, winXP);
            ArrayList<JobStat> SMs = new ArrayList<>();
            SMs.add(SM);
            SocketManager.GAME_SEND_JX_PACKET(this.player, SMs);
        }

        this.ingredients.clear();
        return success;
    }

    public boolean isMaging() {
        return this.id == 1 || this.id == 113 || this.id == 115 || this.id == 116 || this.id == 117
                || this.id == 118 || this.id == 119 || this.id == 120 || (this.id >= 163 && this.id <= 169);
    }

    synchronized void craft(boolean isRepeat) {
        if (!this.isCraft) return;

        if (this.isMaging()) {
            this.craftMaging1(isRepeat, 1);
            return;
        }

        Map<Integer, Integer> items = new HashMap<>();
        //on retire les items mis en ingr�dients
        for (Entry<Integer, Integer> e : this.ingredients.entrySet()) {
            if (!this.player.hasItemGuid(e.getKey())) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                return;
            }

            GameObject obj = World.world.getGameObject(e.getKey());

            if (obj == null) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                return;
            }
            if (obj.getQuantity() < e.getValue()) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                return;
            }

            int newQua = obj.getQuantity() - e.getValue();
            if (newQua < 0) return;

            if (newQua == 0) {
                this.player.removeItem(e.getKey());
                World.world.removeGameObject(e.getKey());
                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, e.getKey());
            } else {
                obj.setQuantity(newQua);
                SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, obj);
            }

            items.put(obj.getTemplate().getId(), e.getValue());
        }

        boolean signed = false;

        if (items.containsKey(7508)) {
            signed = true;
            items.remove(7508);
        }

        SocketManager.GAME_SEND_Ow_PACKET(this.player);

        boolean isUnjobSkill = this.getJobStat() == null;

        if (!isUnjobSkill) {
            JobStat SM = this.player.getMetierBySkill(this.id);
            int templateId = World.world.getObjectByIngredientForJob(SM.getTemplate().getListBySkill(this.id), items);
            //Recette non existante ou pas adapt� au m�tier
            if (templateId == -1 || !SM.getTemplate().canCraft(this.id, templateId)) {
                if (Logging.USE_LOG)
                    Logging.getInstance().write("Craft", this.player.getName() + " à crafter une recette inconnu : " + templateId  +")");
                player.sendMessage("Undefined craft (" + templateId + "), ingredients : (please contact an admin)");
                for(Entry<Integer, Integer> entry : ingredients.entrySet())
                    player.sendMessage(entry.getKey() + " x" + entry.getValue());
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");
                this.ingredients.clear();
                return;
            }

            int chan = JobConstant.getChanceByNbrCaseByLvl(SM.get_lvl(), this.ingredients.size());
            boolean success = chan >= Formulas.getRandomValue(0, 100);

            if(chan == 99) {
                success = chan * 2 >= Formulas.getRandomValue(0, 200);
            }
            if(SM.get_lvl() == 100)
                success = true;

            switch (this.id) {
                case 109:
                    success = true;
                    break;
            }

            if (Logging.USE_LOG)
                Logging.getInstance().write("Craft", this.player.getName() + " à crafter avec " + (success ? "SUCCES" : "ECHEC") + " l'item " + templateId + " (" + World.world.getObjTemplate(templateId).getName() + ")");
            if (!success) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EF");
                SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-" + templateId);
                SocketManager.GAME_SEND_Im_PACKET(this.player, "0118");
            } else {
                GameObject newObj = World.world.getObjTemplate(templateId).createNewItemWithoutDuplication(this.player.getItems().values(), 1, false);
                if(newObj != null) {
                    if (this.player.getItems().get(newObj.getGuid()) == null) {
                        if (this.player.addObjet(newObj, true))
                            World.world.addGameObject(newObj);
                    } else {
                        SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(this.player, newObj);
                    }
                    SocketManager.GAME_SEND_Ow_PACKET(this.player);
                    if (signed) newObj.addTxtStat(988, this.player.getName());
                    SocketManager.GAME_SEND_Em_PACKET(this.player, "KO+" + newObj.getGuid() + "|1|" + templateId + "|" + newObj.parseStatsString().replace(";", "#"));
                    SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;" + templateId);
                    SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+" + templateId);
                }
            }

            int winXP = 0;
            if (success)
                winXP = Formulas.calculXpWinCraft(SM.get_lvl(), this.ingredients.size()) * Config.rateJob;
            else if (!SM.getTemplate().isMaging())
                winXP = Formulas.calculXpWinCraft(SM.get_lvl(), this.ingredients.size()) * Config.rateJob;

            if (winXP > 0) {
                SM.addXp(this.player, winXP);
                ArrayList<JobStat> SMs = new ArrayList<>();
                SMs.add(SM);
                SocketManager.GAME_SEND_JX_PACKET(this.player, SMs);
            }
        } else {
            int templateId = World.world.getObjectByIngredientForJob(World.world.getMetier(this.id).getListBySkill(this.id), items);

            if (templateId == -1 || !World.world.getMetier(this.id).canCraft(this.id, templateId)) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");
                this.ingredients.clear();
                return;
            }

            GameObject newObj = World.world.getObjTemplate(templateId).createNewItemWithoutDuplication(this.player.getItems().values(), 1, false);

            if(newObj != null) {
                if (this.player.getItems().get(newObj.getGuid()) == null) {
                    if (this.player.addObjet(newObj, true))
                        World.world.addGameObject(newObj);
                } else {
                    SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(this.player, newObj);
                }

                if (signed) newObj.addTxtStat(988, this.player.getName());

                SocketManager.GAME_SEND_Ow_PACKET(this.player);
                SocketManager.GAME_SEND_Em_PACKET(this.player, "KO+" + newObj.getGuid() + "|1|" + templateId + "|" + newObj.parseStatsString().replace(";", "#"));
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;" + templateId);
                SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+" + templateId);
            }
        }
        this.lastCraft.clear();
        this.lastCraft.putAll(this.ingredients);
        this.ingredients.clear();

        if(!isRepeat) {
            this.oldJobCraft = this.jobCraft;
            this.jobCraft = null;
        }
    }

    public static float coefExo = 0.25f;

    /* ********FM TOUT POURRI*************/
    private synchronized boolean craftMaging(boolean isRepeat, Player receiver, Map<Player, ArrayList<Couple<Integer, Integer>>> items) {
        boolean isSigningRune = false;
        GameObject objectFm = null, signingRune = null, runeOrPotion = null;
        int lvlElementRune = 0, statId = -1, lvlQuaStatsRune = 0, statsAdd = 0, deleteID = -1, poid = 0, idRune = 0;
        boolean bonusRune = false;
        String statsObjectFm = "-1";

        final boolean secure = items != null && receiver != null;
        final Map<Integer, Integer> ingredients = items == null ? this.ingredients : new HashMap<>();

        if(items != null) {
            for(Entry<Player, ArrayList<Couple<Integer, Integer>>> entry : items.entrySet()) {
                for(Couple<Integer, Integer> couple : entry.getValue()) {
                    ingredients.put(couple.first, couple.second);
                }
            }
        }

        for (int id : ingredients.keySet()) {
            GameObject object = World.world.getGameObject(id);

            if(object == null) {
                if(!this.player.hasItemGuid(id) || (secure && !this.player.hasItemGuid(id) && !receiver.hasItemGuid(id))) {
                    SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                    SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");
                    ingredients.clear();
                    return false;
                }
            }

            int template = object.getTemplate().getId();
            if (object.getTemplate().getType() == 78)
                idRune = id;

            //region gros switch rune
            switch (template) {
                //region on s'en tape
                case 1333:
                    statId = 99;
                    lvlElementRune = object.getTemplate().getLevel();
                    runeOrPotion = object;
                    break;
                case 1335:
                    statId = 96;
                    lvlElementRune = object.getTemplate().getLevel();
                    runeOrPotion = object;
                    break;
                case 1337:
                    statId = 98;
                    lvlElementRune = object.getTemplate().getLevel();
                    runeOrPotion = object;
                    break;
                case 1338:
                    statId = 97;
                    lvlElementRune = object.getTemplate().getLevel();
                    runeOrPotion = object;
                    break;
                case 1340:
                    statId = 97;
                    lvlElementRune = object.getTemplate().getLevel();
                    runeOrPotion = object;
                    break;
                case 1341:
                    statId = 96;
                    lvlElementRune = object.getTemplate().getLevel();
                    runeOrPotion = object;
                    break;
                case 1342:
                    statId = 98;
                    lvlElementRune = object.getTemplate().getLevel();
                    runeOrPotion = object;
                    break;
                case 1343:
                    statId = 99;
                    lvlElementRune = object.getTemplate().getLevel();
                    runeOrPotion = object;
                    break;
                case 1345:
                    statId = 99;
                    lvlElementRune = object.getTemplate().getLevel();
                    runeOrPotion = object;
                    break;
                case 1346:
                    statId = 96;
                    lvlElementRune = object.getTemplate().getLevel();
                    runeOrPotion = object;
                    break;
                case 1347:
                    statId = 98;
                    lvlElementRune = object.getTemplate().getLevel();
                    runeOrPotion = object;
                    break;
                case 1348:
                    statId = 97;
                    lvlElementRune = object.getTemplate().getLevel();
                    runeOrPotion = object;
                    break;
                case 1519:
                    runeOrPotion = object;
                    statsObjectFm = "76";
                    statsAdd = 1;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1521:
                    runeOrPotion = object;
                    statsObjectFm = "7c";
                    statsAdd = 1;
                    poid = 6;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1522:
                    runeOrPotion = object;
                    statsObjectFm = "7e";
                    statsAdd = 1;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1523:
                    runeOrPotion = object;
                    statsObjectFm = "7d";
                    statsAdd = 3;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1524:
                    runeOrPotion = object;
                    statsObjectFm = "77";
                    statsAdd = 1;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1525:
                    runeOrPotion = object;
                    statsObjectFm = "7b";
                    statsAdd = 1;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1545:
                    runeOrPotion = object;
                    statsObjectFm = "76";
                    statsAdd = 3;
                    poid = 3;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1546:
                    runeOrPotion = object;
                    statsObjectFm = "7c";
                    statsAdd = 3;
                    poid = 18;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1547:
                    runeOrPotion = object;
                    statsObjectFm = "7e";
                    statsAdd = 3;
                    poid = 3;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1548:
                    runeOrPotion = object;
                    statsObjectFm = "7d";
                    statsAdd = 10;
                    poid = 10;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1549:
                    runeOrPotion = object;
                    statsObjectFm = "77";
                    statsAdd = 3;
                    poid = 3;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1550:
                    runeOrPotion = object;
                    statsObjectFm = "7b";
                    statsAdd = 3;
                    poid = 10;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1551:
                    runeOrPotion = object;
                    statsObjectFm = "76";
                    statsAdd = 10;
                    poid = 10;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1552:
                    runeOrPotion = object;
                    statsObjectFm = "7c";
                    statsAdd = 10;
                    poid = 50;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1553:
                    runeOrPotion = object;
                    statsObjectFm = "7e";
                    statsAdd = 10;
                    poid = 10;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1554:
                    runeOrPotion = object;
                    statsObjectFm = "7d";
                    statsAdd = 30;
                    poid = 10;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1555:
                    runeOrPotion = object;
                    statsObjectFm = "77";
                    statsAdd = 10;
                    poid = 10;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1556:
                    runeOrPotion = object;
                    statsObjectFm = "7b";
                    statsAdd = 10;
                    poid = 10;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1557:
                    runeOrPotion = object;
                    statsObjectFm = "6f";
                    statsAdd = 1;
                    poid = 100;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 1558:
                    runeOrPotion = object;
                    statsObjectFm = "80";
                    statsAdd = 1;
                    poid = 90;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7433:
                    runeOrPotion = object;
                    statsObjectFm = "73";
                    statsAdd = 1;
                    poid = 30;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7434:
                    runeOrPotion = object;
                    statsObjectFm = "b2";
                    statsAdd = 1;
                    poid = 20;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7435:
                    runeOrPotion = object;
                    statsObjectFm = "79";
                    statsAdd = 1;
                    poid = 20;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7436:
                    runeOrPotion = object;
                    statsObjectFm = "8a";
                    statsAdd = 1;
                    poid = 2;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7437:
                    runeOrPotion = object;
                    statsObjectFm = "dc";
                    statsAdd = 1;
                    poid = 2;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7438:
                    runeOrPotion = object;
                    statsObjectFm = "75";
                    statsAdd = 1;
                    poid = 50;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7442:
                    runeOrPotion = object;
                    statsObjectFm = "b6";
                    statsAdd = 1;
                    poid = 30;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7443:
                    runeOrPotion = object;
                    statsObjectFm = "9e";
                    statsAdd = 10;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7444:
                    runeOrPotion = object;
                    statsObjectFm = "9e";
                    statsAdd = 30;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7445:
                    runeOrPotion = object;
                    statsObjectFm = "9e";
                    statsAdd = 100;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7446:
                    runeOrPotion = object;
                    statsObjectFm = "e1";
                    statsAdd = 1;
                    poid = 15;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7447:
                    runeOrPotion = object;
                    statsObjectFm = "e2";
                    statsAdd = 1;
                    poid = 2;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7448:
                    runeOrPotion = object;
                    statsObjectFm = "ae";
                    statsAdd = 10;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7449:
                    runeOrPotion = object;
                    statsObjectFm = "ae";
                    statsAdd = 30;
                    poid = 3;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7450:
                    runeOrPotion = object;
                    statsObjectFm = "ae";
                    statsAdd = 100;
                    poid = 10;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7451:
                    runeOrPotion = object;
                    statsObjectFm = "b0";
                    statsAdd = 1;
                    poid = 5;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7452:
                    runeOrPotion = object;
                    statsObjectFm = "f3";
                    statsAdd = 1;
                    poid = 4;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7453:
                    runeOrPotion = object;
                    statsObjectFm = "f2";
                    statsAdd = 1;
                    poid = 4;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7454:
                    runeOrPotion = object;
                    statsObjectFm = "f1";
                    statsAdd = 1;
                    poid = 4;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7455:
                    runeOrPotion = object;
                    statsObjectFm = "f0";
                    statsAdd = 1;
                    poid = 4;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7456:
                    runeOrPotion = object;
                    statsObjectFm = "f4";
                    statsAdd = 1;
                    poid = 4;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7457:
                    runeOrPotion = object;
                    statsObjectFm = "d5";
                    statsAdd = 1;
                    poid = 5;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7458:
                    runeOrPotion = object;
                    statsObjectFm = "d4";
                    statsAdd = 1;
                    poid = 5;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7459:
                    runeOrPotion = object;
                    statsObjectFm = "d2";
                    statsAdd = 1;
                    poid = 5;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7460:
                    runeOrPotion = object;
                    statsObjectFm = "d6";
                    statsAdd = 1;
                    poid = 5;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7560:
                    runeOrPotion = object;
                    statsObjectFm = "d3";
                    statsAdd = 1;
                    poid = 5;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 8379:
                    runeOrPotion = object;
                    statsObjectFm = "7d";
                    statsAdd = 10;
                    poid = 10;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 10662:
                    runeOrPotion = object;
                    statsObjectFm = "b0";
                    statsAdd = 3;
                    poid = 15;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 10613:
                    runeOrPotion = object;
                    statsObjectFm = "e1";
                    statsAdd = 3;
                    poid = 15;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 10615:
                    runeOrPotion = object;
                    statsObjectFm = "e2";
                    statsAdd = 3;
                    poid = 5;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 10616:
                    runeOrPotion = object;
                    statsObjectFm = "e2";
                    statsAdd = 10;
                    poid = 15;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;

                case 10618:
                    runeOrPotion = object;
                    statsObjectFm = "8a";
                    statsAdd = 3;
                    poid = 5;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 10619:
                    runeOrPotion = object;
                    statsObjectFm = "8a";
                    statsAdd = 10;
                    poid = 20;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 7508:
                    isSigningRune = true;
                    signingRune = object;
                    break;
                case 11118:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "76";
                    statsAdd = 15;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 11119:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "7c";
                    statsAdd = 15;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 11120:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "7e";
                    statsAdd = 15;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 11121:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "7d";
                    statsAdd = 45;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 11122:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "77";
                    statsAdd = 15;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 11123:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "7b";
                    statsAdd = 15;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 11124:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "b0";
                    statsAdd = 10;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 11125:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "73";
                    statsAdd = 3;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 11126:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "b2";
                    statsAdd = 5;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 11127:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "70";
                    statsAdd = 5;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 11128:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "8a";
                    statsAdd = 10;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 11129:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "dc";
                    statsAdd = 5;
                    poid = 1;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                case 10057:
                    bonusRune = true;
                    runeOrPotion = object;
                    statsObjectFm = "31b";
                    statsAdd = 1;
                    poid = 0;
                    lvlQuaStatsRune = object.getTemplate().getLevel();
                    break;
                //endregion
                default:
                    int type = object.getTemplate().getType();
                    if ((type >= 1 && type <= 11) || (type >= 16 && type <= 22) || type == 81 || type == 102 || type == 114 || object.getTemplate().getPACost() > 0) {
                        final Player player = this.player.hasItemGuid(object.getGuid()) ? this.player : receiver;
                        objectFm = object;
                        SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK_FM(player.getGameClient(), 'O', "+", objectFm.getGuid() + "|" + 1);
                        deleteID = id;
                        GameObject newObj = objectFm.getClone(1, true); // Cr�ation d'un clone avec un nouveau identifiant

                        if (objectFm.getQuantity() > 1) { // S'il y avait plus d'un objet
                            int newQuant = objectFm.getQuantity() - 1; // On supprime celui que l'on a ajout�
                            objectFm.setQuantity(newQuant);
                            SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(player, objectFm);
                        } else {
                            World.world.removeGameObject(id);

                            player.removeItem(id);
                            SocketManager.GAME_SEND_DELETE_STATS_ITEM_FM(player, id);
                        }
                        objectFm = newObj; // Tout neuf avec un nouveau identifiant
                        break;
                    }
            }
            //endregion
        }

        //region Calcul formule
        double poid2 = getPwrPerEffet(Integer.parseInt(statsObjectFm, 16));
        if (poid2 > 0.0)
            poid = statsAdd * ((int) poid2);

        if (SM == null || objectFm == null || runeOrPotion == null) {
            if (objectFm != null) {
                World.world.addGameObject(objectFm);
                this.player.addObjet(objectFm);
            }

            if(receiver != null)
                SocketManager.GAME_SEND_Ec_PACKET(receiver, "EI");
            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");

            ingredients.clear();
            return false;
        }
        if (deleteID != -1) {
            this.ingredients.remove(deleteID);
        }

        final ObjectTemplate template = objectFm.getTemplate();
        ArrayList<Integer> chances = new ArrayList<>();

        int chance, lvlJob = SM.get_lvl(), currentWeightTotal = 1, pwrPerte;
        int objTemplateID = template.getId();
        String statStringObj = objectFm.parseStatsString();

        if (lvlElementRune > 0 && lvlQuaStatsRune == 0) {
            chance = Formulas.calculChanceByElement(lvlJob, template.getLevel(), lvlElementRune);
            if (chance > 100 - (lvlJob / 20))
                chance = 100 - (lvlJob / 20);
            if (chance < (lvlJob / 20))
                chance = (lvlJob / 20);
            chances.add(0, chance);
            chances.add(1, 0);
            chances.add(2, 100 - chance);
        } else if (lvlQuaStatsRune > 0 && lvlElementRune == 0) {
            int currentWeightStats = 1;
            if (!statStringObj.isEmpty()) {
                currentWeightTotal = currentTotalWeigthBase(statStringObj, objectFm); // Poids total de l'objet : PWRg
                currentWeightStats = currentWeithStats(objectFm, statsObjectFm); // Poids � ajouter : PWRcarac
            }

            int currentTotalBase = WeithTotalBase(objTemplateID); // Poids maximum de l'objet : PWRmax
            int currentMinBase = WeithTotalBaseMin(objTemplateID);

            if (currentTotalBase < 0)
                currentTotalBase = 0;
            if (currentWeightStats < 0)
                currentWeightStats = 0;
            if (currentWeightTotal < 0)
                currentWeightTotal = 0;

            float coef = 1;
            int baseStats = viewBaseStatsItem(objectFm, statsObjectFm), currentStats = viewActualStatsItem(objectFm, statsObjectFm);

            if (baseStats == 1 && currentStats == 1 || baseStats == 1 && currentStats == 0) {
                coef = 1.0f;
            } else if (baseStats == 2 && currentStats == 2) {
                coef = 0.50f;
            } else if (baseStats == 0 && currentStats == 0 || baseStats == 0 && currentStats == 1) {
                coef = coefExo;
            }

            float x = 1;
            boolean canFM = true;
            int statMax = getStatBaseMaxs(objectFm.getTemplate(), statsObjectFm), actualJet = getActualJet(objectFm, statsObjectFm);

            if (actualJet > statMax) {
                x = 0.8F;
                int overPerEffect = (int) getOverPerEffet(Integer.parseInt(statsObjectFm, 16));
                //if (statMax == 0)
                if (actualJet >= (statMax + overPerEffect))
                    canFM = false;
                if(Integer.parseInt(statsObjectFm, 16) == 111) {
                    if(objectFm.isOverFm2(111, 1))
                        if(!canFM)
                            canFM = true;
                } else if(Integer.parseInt(statsObjectFm, 16) == 128) {
                    if(objectFm.isOverFm2(128, 1))
                        if(!canFM)
                            canFM = true;
                }
            }
            if (lvlJob < (int) Math.floor(template.getLevel() / 2))
                canFM = false; // On rate le FM si le m�tier n'est pas suffidant

            int diff = (int) Math.abs((currentTotalBase * 1.3f) - currentWeightTotal);

            if (canFM) {
                chances = Formulas.chanceFM(currentTotalBase, currentMinBase, currentWeightTotal, currentWeightStats, poid, diff, coef, statMax, getStatBaseMins(objectFm.getTemplate(), statsObjectFm), currentStats(objectFm, statsObjectFm), x, bonusRune, statsAdd);
            } else {// Si l'objet est au dessus de l'over (impossible statistiquement ... mais evite un gelano 2 PA :p)
                chances.add(0, 0);
                chances.add(1, 0);
            }
        }

        int aleatoryChance = Formulas.getRandomValue(1, 100), SC = chances.get(0), SN = chances.get(1);
        boolean successC = (aleatoryChance <= SC), successN = (aleatoryChance <= (SC + SN));

        if(objectFm.getPuit() >= statsAdd) {
            if(runeOrPotion.getTemplate().getId() != 1558 && runeOrPotion.getTemplate().getId() != 1557 && runeOrPotion.getTemplate().getId() != 7438) {
                if(Formulas.getRandomValue(1, 2) == 1)
                    successC = true;
            }
        }

        if(runeOrPotion.getTemplate().getId() == 1558 || runeOrPotion.getTemplate().getId() == 1557)
            if(Formulas.getRandomValue(0, 100) == 1)
                successC = true;

        if (successC || successN) {
            int winXP = Formulas.calculXpWinFm(objectFm.getTemplate().getLevel(), poid) * Config.rateJob;
            if (winXP > 0) {
                SM.addXp(this.player, winXP);
                ArrayList<JobStat> SMs = new ArrayList<>();
                SMs.add(SM);
                SocketManager.GAME_SEND_JX_PACKET(this.player, SMs);
            }
        }
        //endregion

        //region succès critique
        if (successC) {
            int coef = 0;
            pwrPerte = 0;

            if (lvlElementRune == 1) coef = 50;
            else if (lvlElementRune == 25) coef = 65;
            else if (lvlElementRune == 50) coef = 85;
            if (isSigningRune)
                objectFm.addTxtStat(985, this.player.getName());

            if (lvlElementRune > 0 && lvlQuaStatsRune == 0) {
                for (SpellEffect effect : objectFm.getEffects()) {
                    if (effect.getEffectID() != 100)
                        continue;
                    String[] infos = effect.getArgs().split(";");
                    try {
                        int min = Integer.parseInt(infos[0], 16);
                        int max = Integer.parseInt(infos[1], 16);
                        int newMin = (min * coef) / 100;
                        int newMax = (max * coef) / 100;
                        if (newMin == 0)
                            newMin = 1;
                        String newRange = "1d" + (newMax - newMin + 1) + "+" + (newMin - 1);
                        String newArgs = Integer.toHexString(newMin) + ";" + Integer.toHexString(newMax) + ";-1;-1;0;" + newRange;
                        effect.setArgs(newArgs);
                        effect.setEffectID(statId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (lvlQuaStatsRune > 0 && lvlElementRune == 0) {
                boolean negative = false;
                int currentStats = viewActualStatsItem(objectFm, statsObjectFm);

                if (currentStats == 2) {
                    if (statsObjectFm.compareTo("7b") == 0) {
                        statsObjectFm = "98";
                        negative = true;
                    }
                    if (statsObjectFm.compareTo("77") == 0) {
                        statsObjectFm = "9a";
                        negative = true;
                    }
                    if (statsObjectFm.compareTo("7e") == 0) {
                        statsObjectFm = "9b";
                        negative = true;
                    }
                    if (statsObjectFm.compareTo("76") == 0) {
                        statsObjectFm = "9d";
                        negative = true;
                    }
                    if (statsObjectFm.compareTo("7c") == 0) {
                        statsObjectFm = "9c";
                        negative = true;
                    }
                    if (statsObjectFm.compareTo("7d") == 0) {
                        statsObjectFm = "99";
                        negative = true;
                    }
                }

                if (statStringObj.isEmpty()) {
                    String statsStr = statsObjectFm + "#" + Integer.toHexString(statsAdd) + "#0#0#0d0+" + statsAdd;
                    objectFm.clearStats();
                    objectFm.parseStringToStats(statsStr);
                } else {
                    String statsStr;
                    if (currentStats == 1 || currentStats == 2)
                        statsStr = objectFm.parseFMStatsString(statsObjectFm, objectFm, statsAdd, negative);
                    else
                        statsStr = objectFm.parseFMStatsString(statsObjectFm, objectFm, statsAdd, negative) + "," + statsObjectFm + "#" + Integer.toHexString(statsAdd) + "#0#0#0d0+" + statsAdd;

                    objectFm.clearStats();
                    objectFm.parseStringToStats(statsStr);
                }
            }

            String data = objectFm.getGuid() + "|1|" + objectFm.getTemplate().getId() + "|" + objectFm.parseStatsString();

            if (!this.isRepeat)
                this.reConfigingRunes = -1;
            if (this.reConfigingRunes != 0 || this.broken)
                if(receiver == null)
                    SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(this.player, 'O', "+", data);

            this.data = data;
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+" + objTemplateID);
            if(!secure) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;" + objTemplateID);
            }
        }
        //endregion
        //region Succès neutre
        else if (successN) {
            pwrPerte = 0;
            if (isSigningRune) {
                objectFm.addTxtStat(985, this.player.getName());
            }

            boolean negative = false;
            int currentStats = viewActualStatsItem(objectFm, statsObjectFm);

            if (currentStats == 2) {
                if (statsObjectFm.compareTo("7b") == 0) {
                    statsObjectFm = "98";
                    negative = true;
                }
                if (statsObjectFm.compareTo("77") == 0) {
                    statsObjectFm = "9a";
                    negative = true;
                }
                if (statsObjectFm.compareTo("7e") == 0) {
                    statsObjectFm = "9b";
                    negative = true;
                }
                if (statsObjectFm.compareTo("76") == 0) {
                    statsObjectFm = "9d";
                    negative = true;
                }
                if (statsObjectFm.compareTo("7c") == 0) {
                    statsObjectFm = "9c";
                    negative = true;
                }
                if (statsObjectFm.compareTo("7d") == 0) {
                    statsObjectFm = "99";
                    negative = true;
                }
            }
            if (statStringObj.isEmpty()) {
                String statsStr = statsObjectFm + "#" + Integer.toHexString(statsAdd) + "#0#0#0d0+" + statsAdd;
                objectFm.clearStats();
                objectFm.parseStringToStats(statsStr);
            } else {
                String statsStr;

                if (objectFm.getPuit() <= 0) {// EC en premier s'il n'y a pas de puits
                    statsStr = objectFm.parseStringStatsEC_FM(objectFm, statsAdd, runeOrPotion.getTemplate().getId());
                    objectFm.clearStats();
                    objectFm.parseStringToStats(statsStr);
                    pwrPerte = currentWeightTotal - currentTotalWeigthBase(statsStr, objectFm);
                }
                if (currentStats == 1 || currentStats == 2)
                    statsStr = objectFm.parseFMStatsString(statsObjectFm, objectFm, statsAdd, negative);
                else
                    statsStr = objectFm.parseFMStatsString(statsObjectFm, objectFm, statsAdd, negative) + "," + statsObjectFm + "#" + Integer.toHexString(statsAdd) + "#0#0#0d0+" + statsAdd;
                objectFm.clearStats();
                objectFm.parseStringToStats(statsStr);
            }

            String data = objectFm.getGuid() + "|1|" + objectFm.getTemplate().getId() + "|" + objectFm.parseStatsString();
            if (!this.isRepeat)
                this.reConfigingRunes = -1;
            if (this.reConfigingRunes != 0 || this.broken)
                if(receiver == null)
                    SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(this.player, 'O', "+", data);

            this.data = data;
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+" + objTemplateID);

            if (pwrPerte > 0) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EF");
                SocketManager.GAME_SEND_Im_PACKET(this.player, "0194");
            } else {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;" + objTemplateID);
            }
        }
        //endregion
        //region Echec critique
        else {// EC
            pwrPerte = 0;

            if (!statStringObj.isEmpty()) {
                String statsStr = objectFm.parseStringStatsEC_FM(objectFm, statsAdd, -1);
                objectFm.clearStats();
                objectFm.parseStringToStats(statsStr);
                pwrPerte = currentWeightTotal - currentTotalWeigthBase(statsStr, objectFm);
            }

            String data = objectFm.getGuid() + "|1|" + objectFm.getTemplate().getId() + "|" + objectFm.parseStatsString();
            if (!this.isRepeat)
                this.reConfigingRunes = -1;
            if (this.reConfigingRunes != 0 || this.broken)
                if(receiver == null)
                    SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(this.player, 'O', "+", data);

            this.data = data;
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-" + objTemplateID);
            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EF");

            if (pwrPerte > 0)
                SocketManager.GAME_SEND_Im_PACKET(this.player, "0117");
            else
                SocketManager.GAME_SEND_Im_PACKET(this.player, "0183");
        }
        //endregion

        objectFm.setPuit((objectFm.getPuit() + pwrPerte) - poid);
        int newQuantity = ingredients.get(idRune) == null ? 0 : ingredients.get(idRune) - 1;

        if (objectFm != null) {
            World.world.addGameObject(objectFm);
            if(receiver == null) {
                this.player.addObjet(objectFm);
            } else {
                receiver.addObjet(objectFm);
            }
        }

        if(receiver == null) {
            this.decrementObjectQuantity(this.player, signingRune);
            this.decrementObjectQuantity(this.player, runeOrPotion);
            this.player.send("EmKO-" + objectFm.getGuid() + "|1|");
            this.ingredients.clear();
            this.player.send("EMKO+" + objectFm.getGuid() + "|1");
            this.ingredients.put(objectFm.getGuid(), 1);

            if (newQuantity >= 1) {
                this.player.send("EMKO+" + idRune + "|" + newQuantity);
                this.ingredients.put(idRune, newQuantity);
            } else {
                this.player.send("EMKO-" + idRune);
            }
        } else {
            if(items != null) {
                for(Entry<Player, ArrayList<Couple<Integer, Integer>>> entry : items.entrySet()) {
                    final Player player = entry.getKey();
                    for(Couple<Integer, Integer> couple : entry.getValue()) {
                        if(signingRune != null && signingRune.getGuid() == couple.first)
                            this.decrementObjectQuantity(player, signingRune);
                        if(runeOrPotion.getGuid() == couple.first)
                            this.decrementObjectQuantity(player, runeOrPotion);
                        //player.send("EMKO-" + couple.first);

                    }
                }
            }

            String stats = objectFm.parseStatsString();
            this.player.send("ErKO+" + objectFm.getGuid() + "|1|" + template + "|" + stats);
            receiver.send("ErKO+" + objectFm.getGuid() + "|1|" + template + "|" + stats);
            this.player.send("EcK;" + template + ";T" + receiver.getName() + ";" + stats);
            receiver.send("EcK;" + template + ";B" + this.player.getName() + ";" + stats);

            if(!successC) {
                receiver.send("EcEF");
            }
        }

        this.lastCraft.clear();
        this.lastCraft.putAll(this.ingredients);

        SocketManager.GAME_SEND_Ow_PACKET(this.player);
        if (!isRepeat) this.setJobCraft(null);
        return true;
    }

    //region usefull function for fm
    private void decrementObjectQuantity(Player player, GameObject object) {
        if (object != null) {
            int newQua = object.getQuantity() - 1;
            if (newQua <= 0) {
                player.removeItem(object.getGuid(), object.getQuantity(), true, true);
                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, object.getGuid());
            } else {
                object.setQuantity(newQua);
                SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(player, object);
            }
        }
    }

    public static int getStatBaseMaxs(ObjectTemplate objMod, String statsModif) {
        String[] split = objMod.getStrTemplate().split(",");
        for (String s : split) {
            String[] stats = s.split("#");
            if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) > 0) {
            	
            } else if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) == 0) {
                int max = Integer.parseInt(stats[2], 16);
                if (max == 0)
                    max = Integer.parseInt(stats[1], 16);
                return max;
            }
        }
        return 0;
    }

    public static int getStatBaseMins(ObjectTemplate objMod, String statsModif) {
        String[] split = objMod.getStrTemplate().split(",");
        for (String s : split) {
            String[] stats = s.split("#");
            if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) > 0) {
            } else if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) == 0) {
                return Integer.parseInt(stats[1], 16);
            }
        }
        return 0;
    }

    public static int WeithTotalBaseMin(int objTemplateID) {
        int weight = 0;
        int alt = 0;
        String statsTemplate = "";
        statsTemplate = World.world.getObjTemplate(objTemplateID).getStrTemplate();
        if (statsTemplate == null || statsTemplate.isEmpty())
            return 0;
        String[] split = statsTemplate.split(",");
        for (String s : split) {
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
            boolean sig = true;
            for (int a : Constant.ARMES_EFFECT_IDS)
                if (a == statID)
                    sig = false;
            if (!sig)
                continue;
            String jet = "";
            int value = 1;
            try {
                jet = stats[4];
                value = Formulas.getRandomJet(null, null, jet);
                try {
                    int min = Integer.parseInt(stats[1], 16);
                    value = min;
                } catch (Exception e) {
                    value = Formulas.getRandomJet(null, null, jet);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            int statX = 1;
            if (statID == 125 || statID == 158 || statID == 174) {
                statX = 1;
            } else if (statID == 118 || statID == 126 || statID == 119
                    || statID == 123) {
                statX = 2;
            } else if (statID == 138 || statID == 666 || statID == 226
                    || statID == 220) // de
            // da�os,Trampas %
            {
                statX = 3;
            } else if (statID == 124 || statID == 176) {
                statX = 5;
            } else if (statID == 240 || statID == 241 || statID == 242
                    || statID == 243 || statID == 244)

            {
                statX = 7;
            } else if (statID == 210 || statID == 211 || statID == 212
                    || statID == 213 || statID == 214)

            {
                statX = 8;
            } else if (statID == 225 || statID == 121) {
                statX = 15;
            } else if (statID == 178 ) {
                statX = 20;
            } else if (statID == 115 || statID == 182) {
                statX = 30;
            } else if (statID == 117) {
                statX = 50;
            } else if (statID == 128) {
                statX = 90;
            } else if (statID == 111) {
                statX = 100;
            }
            weight = value * statX;
            alt += weight;
        }
        return alt;
    }

    public static int WeithTotalBase(int objTemplateID) {
        int weight = 0;
        int alt = 0;
        String statsTemplate = "";
        statsTemplate = World.world.getObjTemplate(objTemplateID).getStrTemplate();
        if (statsTemplate == null || statsTemplate.isEmpty())
            return 0;
        String[] split = statsTemplate.split(",");
        for (String s : split) {
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
            boolean sig = true;
            for (int a : Constant.ARMES_EFFECT_IDS)
                if (a == statID)
                    sig = false;
            if (!sig)
                continue;
            String jet = "";
            int value = 1;
            try {
                jet = stats[4];
                value = Formulas.getRandomJet(null, null, jet);
                try {
                    int min = Integer.parseInt(stats[1], 16);
                    int max = Integer.parseInt(stats[2], 16);
                    value = min;
                    if (max != 0)
                        value = max;
                } catch (Exception e) {
                    e.printStackTrace();
                    value = Formulas.getRandomJet(null, null, jet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            int statX = 1;
            if (statID == 125 || statID == 158 || statID == 174) {
                statX = 1;
            } else if (statID == 118 || statID == 126 || statID == 119
                    || statID == 123) {
                statX = 2;
            } else if (statID == 138 || statID == 666 || statID == 226
                    || statID == 220) // de
            // da�os,Trampas %
            {
                statX = 3;
            } else if (statID == 124 || statID == 176) {
                statX = 5;
            } else if (statID == 240 || statID == 241 || statID == 242
                    || statID == 243 || statID == 244)

            {
                statX = 7;
            } else if (statID == 210 || statID == 211 || statID == 212
                    || statID == 213 || statID == 214)

            {
                statX = 8;
            } else if (statID == 225 || statID == 121) {
                statX = 15;
            } else if (statID == 178 ) {
                statX = 20;
            } else if (statID == 115 || statID == 182) {
                statX = 30;
            } else if (statID == 117) {
                statX = 50;
            } else if (statID == 128) {
                statX = 90;
            } else if (statID == 111) {
                statX = 100;
            }
            weight = value * statX;
            alt += weight;
        }
        return alt;
    }

    public static int currentWeithStats(GameObject obj, String statsModif) {
        for (Entry<Integer, Integer> entry : obj.getStats().getEffects().entrySet()) {
            int statID = entry.getKey();
            if (Integer.toHexString(statID).toLowerCase().compareTo(statsModif.toLowerCase()) > 0) {
            } else if (Integer.toHexString(statID).toLowerCase().compareTo(statsModif.toLowerCase()) == 0) {
                int statX = 1;
                int coef = 1;
                int BaseStats = viewBaseStatsItem(obj, Integer.toHexString(statID));
                if (BaseStats == 2) {
                    coef = 3;
                } else if (BaseStats == 0) {
                    coef = 8;
                }
                if (statID == 125 || statID == 158 || statID == 174) {
                    statX = 1;
                } else if (statID == 118 || statID == 126 || statID == 119
                        || statID == 123)

                {
                    statX = 2;
                } else if (statID == 138 || statID == 666 || statID == 226
                        || statID == 220) // da�os,Trampas
                // %
                {
                    statX = 3;
                } else if (statID == 124 || statID == 176) {
                    statX = 5;
                } else if (statID == 240 || statID == 241 || statID == 242
                        || statID == 243 || statID == 244)

                {
                    statX = 7;
                } else if (statID == 210 || statID == 211 || statID == 212
                        || statID == 213 || statID == 214) {
                    statX = 8;
                } else if (statID == 225 || statID == 121) {
                    statX = 15;
                } else if (statID == 178 ) {
                    statX = 20;
                } else if (statID == 115 || statID == 182) {
                    statX = 30;
                } else if (statID == 117) {
                    statX = 50;
                } else if (statID == 128) {
                    statX = 90;
                } else if (statID == 111) {
                    statX = 100;
                }
                int Weight = entry.getValue() * statX * coef;
                return Weight;
            }
        }
        return 0;
    }

    public static int currentStats(GameObject obj, String statsModif) {
        for (Entry<Integer, Integer> entry : obj.getStats().getEffects().entrySet()) {
            int statID = entry.getKey();
            if (Integer.toHexString(statID).toLowerCase().compareTo(statsModif.toLowerCase()) > 0) {
            } else if (Integer.toHexString(statID).toLowerCase().compareTo(statsModif.toLowerCase()) == 0) {
                return entry.getValue();
            }
        }
        return 0;
    }

    public static int currentTotalWeigthBase(String statsModelo, GameObject obj) {
        if (statsModelo.equalsIgnoreCase(""))
            return 0;
        int Weigth = 0;
        int Alto = 0;
        String[] split = statsModelo.split(",");
        for (String s : split) {
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
            if (statID == 985 || statID == 988)
                continue;
            boolean xy = false;
            for (int a : Constant.ARMES_EFFECT_IDS)
                if (a == statID)
                    xy = true;
            if (xy)
                continue;
            String jet;
            int qua;
            try {
                jet = stats[4];
                qua = Formulas.getRandomJet(null, null, jet);
                try {
                    int min = Integer.parseInt(stats[1], 16);
                    int max = Integer.parseInt(stats[2], 16);
                    qua = min;
                    if (max != 0)
                        qua = max;
                } catch (Exception e) {
                    e.printStackTrace();
                    qua = Formulas.getRandomJet(null, null, jet);
                }
            } catch (Exception e) {
                continue;
                // Ok :/
            }
            int statX = 1;
            int coef = 1;
            int statsBase = viewBaseStatsItem(obj, stats[0]);
            if (statsBase == 2) {
                coef = 3;
            } else if (statsBase == 0) {
                coef = 2;
            }
            if (statID == 125 || statID == 158 || statID == 174) {
                statX = 1;
            } else if (statID == 118 || statID == 126 || statID == 119
                    || statID == 123) {
                statX = 2;
            } else if (statID == 138 || statID == 666 || statID == 226
                    || statID == 220) // de
            // da�os,Trampas %
            {
                statX = 3;
            } else if (statID == 124 || statID == 176) {
                statX = 5;
            } else if (statID == 240 || statID == 241 || statID == 242
                    || statID == 243 || statID == 244) {
                statX = 7;
            } else if (statID == 210 || statID == 211 || statID == 212
                    || statID == 213 || statID == 214)

            {
                statX = 8;
            } else if (statID == 225|| statID == 121) {
                statX = 15;
            } else if (statID == 178 ) {
                statX = 20;
            } else if (statID == 115 || statID == 182) {
                statX = 30;
            } else if (statID == 117) {
                statX = 50;
            } else if (statID == 128) {
                statX = 90;
            } else if (statID == 111) {
                statX = 100;
            }
            Weigth = qua * statX * coef;
            Alto += Weigth;
        }
        return Alto;
    }

    public static int getBaseMaxJet(int templateID, String statsModif) {
        ObjectTemplate t = World.world.getObjTemplate(templateID);
        String[] splitted = t.getStrTemplate().split(",");
        for (String s : splitted) {
            String[] stats = s.split("#");
            if (stats[0].compareTo(statsModif) > 0)//Effets n'existe pas de base
            {
            } else if (stats[0].compareTo(statsModif) == 0)//L'effet existe bien !
            {
                int max = Integer.parseInt(stats[2], 16);
                if (max == 0)
                    max = Integer.parseInt(stats[1], 16);//Pas de jet maximum on prend le minimum
                return max;
            }
        }
        return 0;
    }

    public static int getActualJet(GameObject obj, String statsModif) {
        for (Entry<Integer, Integer> entry : obj.getStats().getEffects().entrySet()) {
            if (Integer.toHexString(entry.getKey()).compareTo(statsModif) > 0)//Effets inutiles
            {
            } else if (Integer.toHexString(entry.getKey()).compareTo(statsModif) == 0)//L'effet existe bien !
            {
                int JetActual = entry.getValue();
                return JetActual;
            }
        }
        return 0;
    }

    public static byte viewActualStatsItem(GameObject obj, String stats)//retourne vrai si le stats est actuellement sur l'item
    {
        if (!obj.parseStatsString().isEmpty()) {
            for (Entry<Integer, Integer> entry : obj.getStats().getEffects().entrySet()) {
                if (Integer.toHexString(entry.getKey()).compareTo(stats) > 0)//Effets inutiles
                {
                    if (Integer.toHexString(entry.getKey()).compareTo("98") == 0
                            && stats.compareTo("7b") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("9a") == 0
                            && stats.compareTo("77") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("9b") == 0
                            && stats.compareTo("7e") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("9d") == 0
                            && stats.compareTo("76") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("74") == 0
                            && stats.compareTo("75") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("99") == 0
                            && stats.compareTo("7d") == 0) {
                        return 2;
                    } else {
                    }
                } else if (Integer.toHexString(entry.getKey()).compareTo(stats) == 0)//L'effet existe bien !
                {
                    return 1;
                }
            }
            return 0;
        } else {
            return 0;
        }
    }

    public static byte viewBaseStatsItem(GameObject obj, String ItemStats)//retourne vrai si le stats existe de base sur l'item
    {

        String[] splitted = obj.getTemplate().getStrTemplate().split(",");
        for (String s : splitted) {
            String[] stats = s.split("#");
            if (stats[0].compareTo(ItemStats) > 0)//Effets n'existe pas de base
            {
                if (stats[0].compareTo("98") == 0
                        && ItemStats.compareTo("7b") == 0) {
                    return 2;
                } else if (stats[0].compareTo("9a") == 0
                        && ItemStats.compareTo("77") == 0) {
                    return 2;
                } else if (stats[0].compareTo("9b") == 0
                        && ItemStats.compareTo("7e") == 0) {
                    return 2;
                } else if (stats[0].compareTo("9d") == 0
                        && ItemStats.compareTo("76") == 0) {
                    return 2;
                } else if (stats[0].compareTo("74") == 0
                        && ItemStats.compareTo("75") == 0) {
                    return 2;
                } else if (stats[0].compareTo("99") == 0
                        && ItemStats.compareTo("7d") == 0) {
                    return 2;
                } else {
                }
            } else if (stats[0].compareTo(ItemStats) == 0)//L'effet existe bien !
            {
                return 1;
            }
        }
        return 0;
    }

    public static double getPwrPerEffet(int effect) {
        double r = 0.0;
        switch (effect) {
            case Constant.STATS_ADD_PA:
                r = 100.0;
                break;
            case Constant.STATS_ADD_PM2:
                r = 90.0;
                break;
            case Constant.STATS_ADD_VIE:
                r = 0.25;
                break;
            case Constant.STATS_MULTIPLY_DOMMAGE:
                r = 100.0;
                break;
            case Constant.STATS_ADD_CC:
                r = 30.0;
                break;
            case Constant.STATS_ADD_PO:
                r = 51.0;
                break;
            case Constant.STATS_ADD_FORC:
                r = 1.0;
                break;
            case Constant.STATS_ADD_AGIL:
                r = 1.0;
                break;
            case Constant.STATS_ADD_PA2:
                r = 100.0;
                break;
            case Constant.STATS_ADD_DOMA:
                r = 20.0;
                break;
            case Constant.STATS_ADD_EC:
                r = 1.0;
                break;
            case Constant.STATS_ADD_CHAN:
                r = 1.0;
                break;
            case Constant.STATS_ADD_SAGE:
                r = 3.0;
                break;
            case Constant.STATS_ADD_VITA:
                r = 0.25;
                break;
            case Constant.STATS_ADD_INTE:
                r = 1.0;
                break;
            case Constant.STATS_ADD_PM:
                r = 90.0;
                break;
            case Constant.STATS_ADD_PERDOM:
                r = 2.0;
                break;
            case Constant.STATS_ADD_PDOM:
                r = 2.0;
                break;
            case Constant.STATS_ADD_PODS:
                r = 0.25;
                break;
            case Constant.STATS_ADD_AFLEE:
                r = 1.0;
                break;
            case Constant.STATS_ADD_MFLEE:
                r = 1.0;
                break;
            case Constant.STATS_ADD_INIT:
                r = 0.1;
                break;
            case Constant.STATS_ADD_PROS:
                r = 3.0;
                break;
            case Constant.STATS_ADD_SOIN:
                r = 20.0;
                break;
            case Constant.STATS_CREATURE:
                r = 30.0;
                break;
            case Constant.STATS_ADD_RP_TER:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_EAU:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_AIR:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_FEU:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_NEU:
                r = 6.0;
                break;
            case Constant.STATS_TRAPDOM:
                r = 15.0;
                break;
            case Constant.STATS_TRAPPER:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_FEU:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_NEU:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_TER:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_EAU:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_AIR:
                r = 2.0;
                break;
            case Constant.STATS_ADD_RP_PVP_TER:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_PVP_EAU:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_PVP_AIR:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_PVP_FEU:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_PVP_NEU:
                r = 6.0;
                break;
            case Constant.STATS_ADD_R_PVP_TER:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_PVP_EAU:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_PVP_AIR:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_PVP_FEU:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_PVP_NEU:
                r = 2.0;
                break;
        }
        return r;
    }

    public static double getOverPerEffet(int effect) {
        double r = 0.0;
        switch (effect) {
            case Constant.STATS_ADD_PA:
                r = 1.0;
                break;
            case Constant.STATS_ADD_PM2:
                r = 0.0;
                break;
            case Constant.STATS_ADD_VIE:
                r = 404.0;
                break;
            case Constant.STATS_MULTIPLY_DOMMAGE:
                r = 0.0;
                break;
            case Constant.STATS_ADD_CC:
                r = 3.0;
                break;
            case Constant.STATS_ADD_PO:
                r = 0.0;
                break;
            case Constant.STATS_ADD_FORC:
                r = 101.0;
                break;
            case Constant.STATS_ADD_AGIL:
                r = 101.0;
                break;
            case Constant.STATS_ADD_PA2:
                r = 0.0;
                break;
            case Constant.STATS_ADD_DOMA:
                r = 5.0;
                break;
            case Constant.STATS_ADD_EC:
                r = 0.0;
                break;
            case Constant.STATS_ADD_CHAN:
                r = 101.0;
                break;
            case Constant.STATS_ADD_SAGE:
                r = 33.0;
                break;
            case Constant.STATS_ADD_VITA:
                r = 404.0;
                break;
            case Constant.STATS_ADD_INTE:
                r = 101.0;
                break;
            case Constant.STATS_ADD_PM:
                r = 0.0;
                break;
            case Constant.STATS_ADD_PERDOM:
                r = 50.0;
                break;
            case Constant.STATS_ADD_PDOM:
                r = 50.0;
                break;
            case Constant.STATS_ADD_PODS:
                r = 404.0;
                break;
            case Constant.STATS_ADD_AFLEE:
                r = 0.0;
                break;
            case Constant.STATS_ADD_MFLEE:
                r = 0.0;
                break;
            case Constant.STATS_ADD_INIT:
                r = 1010.0;
                break;
            case Constant.STATS_ADD_PROS:
                r = 33.0;
                break;
            case Constant.STATS_ADD_SOIN:
                r = 5.0;
                break;
            case Constant.STATS_CREATURE:
                r = 3.0;
                break;
            case Constant.STATS_ADD_RP_TER:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_EAU:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_AIR:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_FEU:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_NEU:
                r = 16.0;
                break;
            case Constant.STATS_TRAPDOM:
                r = 6.0;
                break;
            case Constant.STATS_TRAPPER:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_FEU:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_NEU:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_TER:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_EAU:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_AIR:
                r = 50.0;
                break;
            case Constant.STATS_ADD_RP_PVP_TER:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_PVP_EAU:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_PVP_AIR:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_PVP_FEU:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_PVP_NEU:
                r = 16.0;
                break;
            case Constant.STATS_ADD_R_PVP_TER:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_PVP_EAU:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_PVP_AIR:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_PVP_FEU:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_PVP_NEU:
                r = 50.0;
                break;
        }
        return r;
    }
    //endregion
    /* *********************/

    //region Old craft with new formulas
    private synchronized void craftMaging1(boolean isReapeat, int repeat) {
        GameObject gameObject = null, runeObject = null, potionObject = null, signingObject = null;

        //region Vérification de craft
        /* Type : 26 = potion pour les cac
           Type : 78 = rune
           Signature : Type 50 ou Id 7508 */

        for(int id : this.ingredients.keySet()) {
            GameObject object = World.world.getGameObject(id);
            int type = object.getTemplate().getType();

            if(gameObject == null && this.isAvailableObject(this.getJobStat().getTemplate().getId(), type)) {
                gameObject = object;
            } else if(runeObject == null && type == 78)
                runeObject = object;
            else if(potionObject == null && type == 26)
                potionObject = object;
            else if(signingObject == null && object.getTemplate().getId() == 7508)
                signingObject = object;
        }

        if(gameObject == null || (runeObject == null && potionObject == null)) {
            GameClient.leaveExchange(this.player);
            return;
        }
        if(this.analyzeObject(gameObject)) {
            player.sendMessage("Impossible d'FM ce type d'objet pour le moment (avec faiblesses)");
            return;
        }
        //endregion Vérification de craft

        /* Poids max : 100 si > EC à 100%
           EXO : Si ça dépasse la valeur de la stats originale ou si elle n'existe pas */
        if(runeObject != null) {
            Rune runeTemplate = Rune.getRuneById(runeObject.getTemplate().getId()); // On trouve le template de la rune qu'on souhaite appliqué à l'item

            if (runeTemplate == null) { // Si elle n'existe pas..
                //Ne devrait pas arriver.
                return;
            }

            //region Initialisation des variables principales
            String[] originalSplitStats = gameObject.getTemplate().getStrTemplate().split(",");
            String[] actualObjectSplitStats = gameObject.parseStatsString().split(","); // Liste toutes les stats originale de l'objet

            String concernedOriginalJet = null, concernedActualJet = null; // Jet originale concerner
            float PWRGmin = 0, PWRGactual = 0, PWRGmax = 0;

            for (String jet : originalSplitStats) { // On fait une iteration de chaque ligne de l'objet originale
                if(jet.isEmpty()) continue;
                int id = Short.parseShort(jet.split("#")[0], 16);

                if (id == runeTemplate.getCharacteristic()) // Si l'ID de la stats est égale à l'ID de la stats de la rune
                    concernedOriginalJet = jet; // On met la ligne concerner a jour
            }

            int PWRexotique = 0;

            for (String jet : actualObjectSplitStats) { // On fait une iteration de chaque ligne de l'objet actuel
                if (jet.isEmpty()) continue;
                short id = Short.parseShort(jet.split("#")[0], 16);

                if (id == Constant.STATS_OWNER_1 || id == Constant.STATS_CHANGE_BY || id == Constant.STATS_BUILD_BY) continue;

                Rune rune = Rune.getRuneByCharacteristicAndByWeight(id);
                if (rune != null) PWRGactual += this.getPWR(rune, jet, (byte) 1);

                if (id == runeTemplate.getCharacteristic()) { // Si l'ID de la stats est égale à l'ID de la stats de la rune
                    concernedActualJet = String.valueOf(gameObject.getStats().getEffect(Integer.parseInt(jet.split("#")[0], 16))); // On met la ligne concerner a jour
                }

                boolean exist = false;
                for(String jet2 : originalSplitStats) {
                    if(jet2.isEmpty()) continue;
                    int id2 = Short.parseShort(jet2.split("#")[0], 16);
                    if(id == id2) {
                        exist = true;
                        break;
                    }
                }

                if(!exist) {
                    PWRexotique += this.getPWR(rune, jet, (byte) 1);
                }
            }

            short
                    actualJet = concernedActualJet == null ? 0 : Short.parseShort(concernedActualJet),
                    minJet = concernedOriginalJet == null ? 0 : (short) Formulas.getMinJet(concernedOriginalJet.split("#")[4]),
                    maxJet = concernedOriginalJet == null ? 1 : (short) Formulas.getMaxJet(concernedOriginalJet.split("#")[4]);
            //endregion Initialisation des variables principales

            //region Début des calculs des PWR & PWRG
            float PWGRune = runeTemplate.getWeight();
            float PWRRune = PWGRune / runeTemplate.getBonus();
            float
                    PWRactual = actualJet * PWRRune,
                    PWRmax = maxJet * PWRRune;

            for (String jet : originalSplitStats) {
                short id = Short.parseShort(jet.split("#")[0], 16);
                Rune rune = Rune.getRuneByCharacteristicAndByWeight(id);

                if(rune == null) continue;

                PWRGmin += this.getPWR(rune, jet, (byte) 0);
                PWRGmax += this.getPWR(rune, jet, (byte) 2);
            }
            //endregion Début des calculs des PWR & PWRG

            //region Réussite normal
            byte factorJet = 47, factorObject = 50, successLevel = 5;
                      float EtatJet = (maxJet - minJet) <= 0 ?
                    0 :
                    (((actualJet + runeTemplate.getBonus()) - minJet) * 100) / (maxJet - minJet);
            if(EtatJet < 60)
                EtatJet = 60;

            float EtatObjet = (PWRGmax - PWRGmin) <= 0 ?
                    0 :
                    (float) Math.ceil(((PWRGactual - PWRGmin) * 100 / (PWRGmax - PWRGmin)));
            if(EtatObjet < 15)
                EtatObjet = 15;

            float successJet = 1, successObject = 0;
            byte criticSuccess = 1, neutralSuccess = 50, criticFail = 1;

            if(concernedOriginalJet == null) {
                // CAS EXOTIQUE
                if(PWGRune < 50) {
                    factorJet = 40;
                    factorObject = 54;
                    successLevel = 5;
                    EtatJet = 100;
                }

                if(PWGRune <= 3 && (actualJet / maxJet) * 100 > 65 && PWRRune == 1)
                    EtatJet = 150;

                EtatObjet = (float) Math.ceil(15 + PWRactual + PWGRune * 3);

                if (EtatJet >= 80)
                    successJet = factorJet * EtatJet / 100;

                successObject = factorObject * EtatObjet / 100;

                criticSuccess = (byte) Math.ceil(100 - (successJet + successObject + successLevel));
                criticSuccess = criticSuccess < 0 ? 0 : criticSuccess;

                if (criticSuccess > 50)
                    neutralSuccess = (byte) (100 - criticSuccess);
                else if (criticSuccess < 25)
                    neutralSuccess = (byte) (50 - (40 - criticSuccess));

                criticFail = (byte) (100 - (neutralSuccess + criticSuccess));

                if(PWGRune > 50) {
                    // Pa/Pm/Po
                    criticSuccess = 1;
                    neutralSuccess = 0;
                    criticFail = 99;
                }
                if(PWRexotique >= 101) {
                    criticSuccess = 0;
                    neutralSuccess = 0;
                    criticFail = 100;
                }
            } else if(PWRactual + PWGRune > PWRmax && PWRactual + PWGRune < 101) {
                // CAS OVERMAX
                factorJet = 60;
                factorObject = 54;
                successLevel = 5;
                EtatJet = 100;

                if(PWGRune <= 3 && (actualJet / maxJet) * 100 > 65 && PWRRune == 1)
                    EtatJet = 150;
                if(PWGRune <= 3 && (actualJet / maxJet) * 100 > 80 && PWRRune == 1)
                    EtatJet = 300;
                if(PWGRune <= 3 && (actualJet / maxJet) * 100 > 85 && PWRRune == 3)
                    EtatJet = 200;

                if (EtatJet >= 80)
                    successJet = factorJet * EtatJet / 100;

                if (EtatObjet >= 50)
                    successObject = factorObject * EtatObjet / 100;
                else
                    successObject = EtatObjet;

                criticSuccess = (byte) Math.ceil(100 - (successJet + successObject + successLevel));
                criticSuccess = criticSuccess < 0 ? 0 : criticSuccess;

                if (criticSuccess > 50)
                    neutralSuccess = (byte) (100 - criticSuccess);
                else if (criticSuccess < 25)
                    neutralSuccess = (byte) (50 - (40 - criticSuccess));

                criticFail = (byte) (100 - (neutralSuccess + criticSuccess));

                if(criticSuccess > 25) {
                    criticSuccess = 25;
                    neutralSuccess = 25;
                    criticFail = 50;
                }
                if(criticSuccess <= 1) {
                    criticSuccess = 1;
                    neutralSuccess = 22;
                    criticFail = 77;
                }
            } else {
                // CAS NORMAL
                if(PWGRune <= 3 && (actualJet / maxJet) * 100 > 65 && PWRRune == 1)
                    EtatJet = 150;
                if(PWGRune <= 3 && (actualJet / maxJet) * 100 > 80 && PWRRune == 1)
                    EtatJet = 300;
                if(PWGRune <= 3 && (actualJet / maxJet) * 100 > 85 && PWRRune == 3)
                    EtatJet = 200;


                if (EtatJet >= 52)
                    successJet = factorJet * EtatJet / 100;
                else
                    successJet = EtatJet / 4;

                if (EtatObjet >= 50)
                    successObject = factorObject * EtatObjet / 100;
                else
                    successObject = EtatObjet;

                criticSuccess = (byte) Math.ceil(100 - (successJet + successObject + successLevel));
                criticSuccess = criticSuccess < 0 ? 0 : criticSuccess;

                if (criticSuccess > 50)
                    neutralSuccess = (byte) (100 - criticSuccess);
                else if (criticSuccess < 25)
                    neutralSuccess = (byte) (50 - (40 - criticSuccess));

                criticFail = (byte) (100 - (neutralSuccess + criticSuccess));

                if(criticSuccess < 15) {
                    criticSuccess = 15;
                    neutralSuccess = 50;
                    criticFail = 35;
                }
            }
            if(PWRactual + PWGRune > PWRmax && PWRactual + PWGRune >= 101) {
                criticSuccess = 0;
                neutralSuccess = 0;
                criticFail = 100;
            }
            //endregion

            RandomStats<Byte> randomStats = new RandomStats<>();
            randomStats.add((int) criticSuccess, (byte) 0);
            randomStats.add((int) neutralSuccess, (byte) 1);
            randomStats.add((int) criticFail, (byte) 2);
            byte result = randomStats.get();

            if(this.player.getGroupe() != null) {
                this.player.sendMessage("PWRGmin à max : " + PWRGmin + " | " + PWRGmax + " | " + PWRGactual);
                this.player.sendMessage("FO: " + factorObject + " EB: " + EtatObjet + " | FJ: " + factorJet + " | EJ: " + EtatJet);
                this.player.sendMessage("SC: " + criticSuccess + " | SN: " + neutralSuccess + " | EC: " + criticFail + " | R: " + result);
            }
            //region success critique
            if (result == 0) {
                int newQuantity = this.ingredients.get(runeObject.getGuid()) - 1;
                this.player.removeByTemplateID(runeObject.getTemplate().getId(), 1);

                int winXP = Formulas.calculXpWinFm(gameObject.getTemplate().getLevel(), (int) Math.floor(runeTemplate.getWeight())) * Config.rateJob;
                if (winXP > 0) this.SM.addXp(this.player, winXP);
                this.player.send("JX|" + this.SM.getTemplate().getId() + ";" + this.SM.get_lvl() + ";" + this.SM.getXpString(";") + ";");

                GameObject newObject = gameObject.getClone(1,true);
                this.player.removeItem(gameObject.getGuid(), 1, true, gameObject.getQuantity() == 1);

                if (signingObject != null) {
                    this.player.removeByTemplateID(signingObject.getTemplate().getId(), 1);
                    if (newObject.getTxtStat().containsKey(985))
                        newObject.getTxtStat().remove(985);
                    newObject.addTxtStat(985, this.player.getName());
                }

                newObject.getStats().addOneStat(runeTemplate.getCharacteristic(), runeTemplate.getBonus());

                if (this.player.addObjet(newObject, false))
                    World.world.addGameObject(newObject);

                SocketManager.GAME_SEND_Ow_PACKET(this.player);

                this.player.send("EmKO+" + newObject.getGuid() + "|1|" + newObject.getTemplate().getId() + "|" + newObject.parseStatsString());
                this.player.send("IO" + this.player.getId() + "|+" + newObject.getTemplate().getId()); // Icon tête joueur :  +/-
                this.player.send("EcK;" + newObject.getTemplate().getId());//Vous avez crée...

                this.ingredients.clear();
                this.player.send("EMKO+" + newObject.getGuid() + "|1");
                this.ingredients.put(newObject.getGuid(), 1);

                if (newQuantity >= 1) {
                    this.player.send("EMKO+" + runeObject.getGuid() + "|" + newQuantity);
                    this.ingredients.put(runeObject.getGuid(), newQuantity);
                }

                this.oldJobCraft = this.jobCraft;
                if (!isReapeat) this.setJobCraft(null);
                return;
            }
            //endregion

            int puit = gameObject.getPuit();
            float PWGLoose = PWGRune;
            if(this.player.getGroupe() != null)
                player.sendMessage("Puit before : " + puit);

            if (puit > 0)
                puit = Math.round(puit - PWGLoose);
            if (puit < 0) {
                PWGLoose = -puit;
                puit = 0;
            }
            if (puit > 0)
                puit = Math.round(puit - PWGLoose);

            if(this.player.getGroupe() != null)
                player.sendMessage("Puit after : " + puit);
            boolean cancel = false;
            //region Succès neutre
            if(result == 1) {
                if(actualObjectSplitStats.length == 1 && (actualObjectSplitStats[0].isEmpty() || Short.parseShort(actualObjectSplitStats[0].split("#")[0], 16) == runeTemplate.getCharacteristic()))
                    cancel = true;

                int winXP = Formulas.calculXpWinFm(gameObject.getTemplate().getLevel(), (int) Math.floor(runeTemplate.getWeight())) * Config.rateJob;
                if (winXP > 0) this.SM.addXp(this.player, winXP);

                if(!cancel) {
                    List<Short> blacklist = new ArrayList<>();
                    List<String> stats = getStatsToLoose(runeTemplate, actualObjectSplitStats, originalSplitStats, blacklist);
                    int brokeJet;

                    while (PWGLoose > 0 && !stats.isEmpty()) {
                        String jet = stats.get(Formulas.random.nextInt(stats.size()));
                        short id = Short.parseShort(jet.split("#")[0], 16);
                        if (id == Constant.STATS_OWNER_1 || id == Constant.STATS_CHANGE_BY || id == Constant.STATS_BUILD_BY) continue;
                        Rune rune = Rune.getRuneByCharacteristicAndByWeight(id);
                        float PWRJetRune = rune.getWeight() * rune.getBonus();
                        float PWRGJet = this.getPWR(rune, jet, (byte) 1);

                        if (PWGRune > 50) {
                            brokeJet = Math.round(10 + ((PWGRune * 20) / PWRJetRune));
                        } else if (PWGRune >= 10 && PWGRune <= 50) {
                            brokeJet = Math.round(10 + ((PWGRune * 50) / PWRJetRune));
                        } else {
                            brokeJet = Math.round(10 + ((PWGRune * 100) / PWRJetRune));
                        }

                        byte random = (byte) Formulas.getRandomValue(1, 100);
                        if (random > brokeJet) {
                            blacklist.add(id);
                        } else {
                            int puitLoose = Formulas.getRandomValue(1, (int) (Math.ceil(PWGLoose / PWRJetRune)));
                            int old = gameObject.getStats().get(id);
                            int value = gameObject.getStats().addOneStat(id, -puitLoose);
                            old = old - puitLoose;

                            PWGLoose = PWGLoose - (PWRGJet - (rune.getWeight() * value));
                            if(old < 0) PWGLoose += -old * rune.getWeight();
                        }
                        actualObjectSplitStats = gameObject.parseStatsString().split(",");
                        stats = getStatsToLoose(runeTemplate, actualObjectSplitStats, originalSplitStats, blacklist);
                    }

                    if(this.player.getGroupe() != null)
                        player.sendMessage("Puit remove PWGLoose : " + PWGLoose);
                    puit = -(int) Math.ceil(PWGLoose);
                }
            }
            //endregion

            //region Echec critique
            if(result == 2) {
                List<String> stats = getStatsToLoose(runeTemplate, actualObjectSplitStats, originalSplitStats, null);

                while(PWGLoose > 0 && !stats.isEmpty()) {
                    String jet = stats.get(Formulas.random.nextInt(stats.size()));
                    short id = Short.parseShort(jet.split("#")[0], 16);
                    if (id == Constant.STATS_OWNER_1 || id == Constant.STATS_CHANGE_BY || id == Constant.STATS_BUILD_BY) continue;
                    Rune rune = Rune.getRuneByCharacteristicAndByWeight(id);
                    float PWRJetRune = rune.getWeight() * rune.getBonus();
                    float PWRGJet = this.getPWR(rune, jet, (byte) 1);

                    int puitLoose = Formulas.getRandomValue(1, (int) (Math.ceil(PWGLoose / PWRJetRune)));
                    int old = gameObject.getStats().get(id);
                    int value = gameObject.getStats().addOneStat(id, -puitLoose);
                    old = old - puitLoose;

                    PWGLoose = PWGLoose - (PWRGJet - (rune.getWeight() * value));
                    if(old < 0) PWGLoose += -old * rune.getWeight();
                    actualObjectSplitStats = gameObject.parseStatsString().split(",");
                    stats = getStatsToLoose(runeTemplate, actualObjectSplitStats, originalSplitStats, null);
                }

                if(this.player.getGroupe() != null)
                    player.sendMessage("Puit remove PWGLoose : " + PWGLoose);
                puit = - (int) Math.ceil(PWGLoose);
            }
            //endregion

            int newQuantity = this.ingredients.get(runeObject.getGuid()) - 1;
            this.player.removeByTemplateID(runeObject.getTemplate().getId(), 1);

            GameObject newObject = gameObject.getClone(1, true);

            if(puit < 0) puit = 0;
            newObject.setPuit(puit);
            if(this.player.getGroupe() != null)
                player.sendMessage("Puit finish : " + puit);


            if (signingObject != null)
                this.player.removeByTemplateID(signingObject.getTemplate().getId(), 1);

            if(result == 1) { // succes neutre
                if (signingObject != null) {
                    if (newObject.getTxtStat().containsKey(985))
                        newObject.getTxtStat().remove(985);
                    newObject.addTxtStat(985, this.player.getName());
                }

                if(!cancel)
                    newObject.getStats().addOneStat(runeTemplate.getCharacteristic(), runeTemplate.getBonus());
                this.player.send("Im0194");//La magie n\'a pas parfaitement fonctionné..
            } else {
                this.player.send("Im0117");//La magie n'opère pas..
            }

            this.player.removeItem(gameObject.getGuid(), 1, true, true);
            if(this.player.addObjet(newObject, false))
                World.world.addGameObject(newObject);

            SocketManager.GAME_SEND_Ow_PACKET(this.player);

            this.player.send("EmKO+" + newObject.getGuid() + "|1|" + newObject.getTemplate().getId() + "|" + newObject.parseStatsString());

            this.player.send("IO" + this.player.getId() + "|-" + newObject.getTemplate().getId()); // Icon tête joueur :  +/-

            this.player.send("EMKO-" + gameObject.getGuid() + "|1");
            this.ingredients.clear();

            this.player.send("EMKO+" + newObject.getGuid() + "|1");
            this.ingredients.put(newObject.getGuid(), 1);


            if (newQuantity >= 1) {
                this.player.send("EMKO+" + runeObject.getGuid() + "|" + newQuantity);
                this.ingredients.put(runeObject.getGuid(), newQuantity);
            } else {
                this.player.send("EMKO-" + runeObject.getGuid());
            }

            this.oldJobCraft = this.jobCraft;
            if (!isReapeat) this.setJobCraft(null);
        } else if(potionObject != null) {

        }
        //endregion
    }

    private boolean analyzeObject(GameObject gameObject) {
        for(Entry<Integer, Integer> stat : gameObject.getStats().getEffects().entrySet()) {
            switch(stat.getKey()) {
                case Constant.STATS_REM_PA:
                case Constant.STATS_REM_PM:
                case Constant.STATS_REM_AGIL:
                case Constant.STATS_REM_CHAN:
                case Constant.STATS_REM_FORC:
                case Constant.STATS_REM_INTE:
                case Constant.STATS_REM_SAGE:
                case Constant.STATS_REM_VITA:
                case Constant.STATS_REM_PO:
                case Constant.STATS_REM_PA2:
                case Constant.STATS_REM_PROS:
                case Constant.STATS_REM_AFLEE:
                case Constant.STATS_REM_MFLEE:
                case Constant.STATS_REM_DOMA:
                case Constant.STATS_REM_INIT:
                case Constant.STATS_REM_PM2:
                case Constant.STATS_REM_PODS:
                case Constant.STATS_REM_R_AIR:
                case Constant.STATS_REM_R_FEU:
                case Constant.STATS_REM_R_TER:
                case Constant.STATS_REM_R_EAU:
                case Constant.STATS_REM_RP_AIR:
                case Constant.STATS_REM_RP_FEU:
                case Constant.STATS_REM_RP_TER:
                case Constant.STATS_REM_RP_EAU:
                case Constant.STATS_REM_R_NEU:
                case Constant.STATS_REM_RP_NEU:
                case Constant.STATS_REM_RP_PVP_AIR:
                case Constant.STATS_REM_RP_PVP_FEU:
                case Constant.STATS_REM_RP_PVP_TER:
                case Constant.STATS_REM_RP_PVP_EAU:
                case Constant.STATS_REM_RP_PVP_NEU:
                case Constant.STATS_REM_SOIN:
                case Constant.STATS_REM_CC:
                case Constant.STATS_REM_INVO:
                case Constant.STATS_REM_TRAPDOM:
                case Constant.STATS_REM_TRAPPER:
                case Constant.STATS_REM_RENVOI:
                    return true;
            }
        }
        return false;
    }

    private List<String> getStatsToLoose(Rune fm, String[] actualStats, String[] originalStats, List<Short> blacklist) {
        List<String> high = new ArrayList<>(), low = new ArrayList<>();
        for(String s1 : actualStats) {
            if(s1.isEmpty())
                continue;

            short id1 = Short.parseShort(s1.split("#")[0], 16);
            if (id1 == Constant.STATS_CHANGE_BY || id1 == Constant.STATS_BUILD_BY) continue;
            Rune r1 = Rune.getRuneByCharacteristic(id1);

            if(r1 == null || blacklist != null && (r1.getCharacteristic() == fm.getCharacteristic() || blacklist.stream().filter(i -> i == id1).count() == 1))
                continue;
            boolean exist = false, overmax = false;

            for (String s2 : originalStats) {
                if(s2.isEmpty())
                    continue;

                short id2 = Short.parseShort(s2.split("#")[0], 16);
                if (id2 == Constant.STATS_CHANGE_BY || id2 == Constant.STATS_BUILD_BY) continue;
                Rune r2 = Rune.getRuneByCharacteristic(id1);

                if(id1 == id2) {
                    exist = true;
                    float pwr1 = this.getPWR(r1, s1, (byte) 1), pwr2 = this.getPWR(r2, s2, (byte) 2);
                    if(pwr1 > pwr2)
                        overmax = true;
                }
            }

            if(!exist || overmax) high.add(s1);
            else low.add(s1);
        }
        return high.size() > 0 ? high : low;
    }

    private float getPWR(Rune rune, String jet, byte type) {
        float weight = rune == null ? 1 : Rune.getRuneByCharacteristicAndByWeight(rune.getCharacteristic()).getWeight();
        System.out.println("getPWR = Weight: " + weight + " | Type: " + type + " | Jet: " + jet);
        switch(type) {
            case 0:// min
                return weight * Formulas.getMinJet(jet.split("#")[4]);
            case 1:// actual
                return weight * Short.parseShort(jet.split("\\+")[jet.split("\\+").length - 1]);
            case 2:// max
                return weight * Formulas.getMaxJet(jet.split("#")[4]);
        }
        return 0;
    }

    private boolean isAvailableObject(int jobId, int type) {
        switch(jobId) {
            case 62://Cordomage
                return type == 10 || type == 11;
            case 63://Joaillomage
                return type == 1 || type == 9;
            case 64://Costumage
                return type == 16 || type == 17 || type == Constant.ITEM_TYPE_SAC_DOS;
            case 43://Forgemage de Dagues
                return type == 5;
            case 44://Forgemage d'Epées
                return type == 6;
            case 45://Forgemage de Marteaux
                return type == 7;
            case 46://Forgemage de Pelles
                return type == 8;
            case 47://Forgemage de Haches
                return type == 19;
            case 48://Sculptemage d'Arcs
                return type == 2;
            case 49://Sculptemage de Baguettes
                return type == 3;
            case 50://Sculptemage de Bâtons
                return type == 4;
        }
        return false;
    }
    //endregion Old craft with new formulas
}