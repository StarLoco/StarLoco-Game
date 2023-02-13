package org.starloco.locos.entity.exchange;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.job.JobAction;
import org.starloco.locos.job.JobConstant;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CraftSecure extends PlayerExchange {

    private long payKamas = 0;
    private long payIfSuccessKamas = 0;
    private int maxCase = 9;

    private ArrayList<Couple<Integer, Integer>> payItems = new ArrayList<>();
    private ArrayList<Couple<Integer, Integer>> payItemsIfSuccess = new ArrayList<>();

    public CraftSecure(Player player1, Player player2) {
        super(player1, player2);
        JobStat job = this.player1.getMetierBySkill(this.player1.getIsCraftingType().get(1));
        if(job != null && job.getTemplate() != null) {
            this.maxCase = job.getTemplate().isMaging() ? 3 : JobConstant.getTotalCaseByJobLevel(job.get_lvl());
        } else {
            this.maxCase = 0;
            this.cancel();
        }
    }

    public Player getNeeder() {
        return player2;
    }

    public int getMaxCase() {
        return maxCase;
    }

    public synchronized void apply() {
        JobStat jobStat = this.player1.getMetierBySkill(this.player1.getIsCraftingType().get(1));

        if (jobStat == null)
            return;

        JobAction jobAction = jobStat.getJobActionBySkill(this.player1.getIsCraftingType().get(1));

        if (jobAction == null)
            return;

        Map<Player, ArrayList<Couple<Integer, Integer>>> items = new HashMap<>();
        items.put(this.player1, this.items1);
        items.put(this.player2, this.items2);

        int sizeList = jobAction.sizeList(items);

        boolean success = jobAction.craftPublicMode(this.player1, this.player2, items);

        this.player1.addKamas(payKamas + (success ? payIfSuccessKamas : 0));
        this.player2.addKamas(-payKamas - (success ? payIfSuccessKamas : 0));


        if (success) this.giveObjects(this.payItems, this.payItemsIfSuccess);
        else this.giveObjects(this.payItems);

        int winXP = 0;
        if (success)
            winXP = Formulas.calculXpWinCraft(jobStat.get_lvl(), sizeList) * Config.rateJob;
        else if (!jobStat.getTemplate().isMaging())
            winXP = Formulas.calculXpWinCraft(jobStat.get_lvl(), sizeList) * Config.rateJob;

        if (winXP > 0) {
            jobStat.addXp(this.player1, winXP);
            ArrayList<JobStat> SMs = new ArrayList<>();
            SMs.add(jobStat);
            SocketManager.GAME_SEND_JX_PACKET(this.player1, SMs);
        }

        SocketManager.GAME_SEND_STATS_PACKET(this.player1);
        SocketManager.GAME_SEND_STATS_PACKET(this.player2);

        this.payIfSuccessKamas = 0;
        this.payKamas = 0;
        this.payItems.clear();
        this.payItemsIfSuccess.clear();
        this.items1.clear();
        this.items2.clear();
        this.ok1 = false;
        this.ok2 = false;
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok1, this.player1.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok1, this.player1.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok2, this.player2.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok2, this.player2.getId());

    }

    @SafeVarargs
    private final void giveObjects(ArrayList<Couple<Integer, Integer>>... arrays) {
        for(ArrayList<Couple<Integer, Integer>> array : arrays) {
            for (Couple<Integer, Integer> couple : array) {
                if (couple.second == 0)
                    continue;

                GameObject object = World.world.getGameObject(couple.first);

                if (object == null)
                    continue;
                if (object.getPosition() != Constant.ITEM_POS_NO_EQUIPED)
                    continue;
                if (!this.player2.hasItemGuid(couple.first)) {
                    couple.second = 0;
                    continue;
                }

                this.giveObject(couple, object);
            }
        }
    }

    public synchronized void cancel() {
        this.send("EV");
        this.player1.getIsCraftingType().clear();
        this.player2.getIsCraftingType().clear();
        this.player1.setExchangeAction(null);
        this.player2.setExchangeAction(null);
    }

    public void setPayKamas(byte type, long kamas) {
        if (kamas < 0)
            return;
        if (this.player2.getKamas() < kamas)
            kamas = this.player2.getKamas();

        this.ok1 = false;
        this.ok2 = false;
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok1, this.player1.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok1, this.player1.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok2, this.player2.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok2, this.player2.getId());

        switch (type) {
            case 1:// Pay
                if (this.payIfSuccessKamas > 0 && ((kamas + this.payIfSuccessKamas) > this.player2.getKamas()))
                    kamas -= this.payIfSuccessKamas;

                this.payKamas = kamas;
                this.send("Ep1;G" + this.payKamas);
                break;
            case 2: // PayIfSuccess
                if (this.payKamas > 0 && ((kamas + this.payKamas) > this.player2.getKamas()))
                    kamas -= this.payKamas;

                this.payIfSuccessKamas = kamas;
                this.send("Ep2;G" + this.payIfSuccessKamas);
                break;
        }
    }

    public void setPayItems(byte type, boolean adding, int guid, int quantity) {
        GameObject object = World.world.getGameObject(guid);

        if (object == null)
            return;
        if (object.getPosition() != Constant.ITEM_POS_NO_EQUIPED || object.isAttach())
            return;

        this.ok1 = false;
        this.ok2 = false;
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok1, this.player1.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok1, this.player1.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok2, this.player2.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok2, this.player2.getId());

        if (adding) {
            this.addItem(object, quantity, type);
        } else {
            this.removeItem(object, quantity, type);
        }
    }

    private void addItem(GameObject object, int quantity, byte type) {
        if (object.getQuantity() < quantity)
            quantity = object.getQuantity();

        ArrayList<Couple<Integer, Integer>> items = (type == 1 ? this.payItems : this.payItemsIfSuccess);
        Couple<Integer, Integer> couple = getCoupleInList(items, object.getGuid());
        String add = "|" + object.getTemplate().getId() + "|" + object.parseStatsString();

        if (couple != null) {
            couple.second += quantity;
            this.player2.send("Ep" + type + ";O+" + object.getGuid() + "|" + couple.second);
            this.player1.send("Ep" + type + ";O+" + object.getGuid() + "|" + couple.second + add);
            return;
        }

        items.add(new Couple<>(object.getGuid(), quantity));
        this.player2.send("Ep" + type + ";O+" + object.getGuid() + "|" + quantity);
        this.player1.send("Ep" + type + ";O+" + object.getGuid() + "|" + quantity + add);
    }

    private void removeItem(GameObject object, int quantity, byte type) {
        ArrayList<Couple<Integer, Integer>> items = (type == 1 ? this.payItems : this.payItemsIfSuccess);
        Couple<Integer, Integer> couple = getCoupleInList(items, object.getGuid());

        if(couple == null) return;
        int newQua = couple.second - quantity;

        if (newQua < 1) {
            items.remove(couple);
            this.player1.send("Ep" + type + ";O-" + object.getGuid());
            this.player2.send("Ep" + type + ";O-" + object.getGuid());
        } else {
            couple.second = newQua;
            this.player2.send("Ep" + type + ";O+" + object.getGuid() + "|" + newQua);
            this.player1.send("Ep" + type + ";O+" + object.getGuid() + "|" + newQua + "|" + object.getTemplate().getId() + "|" + object.parseStatsString());
        }
    }

    private void send(String packet) {
        this.player1.send(packet);
        this.player2.send(packet);
    }
}