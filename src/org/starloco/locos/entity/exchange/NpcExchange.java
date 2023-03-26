package org.starloco.locos.entity.exchange;

import org.starloco.locos.auction.Auction;
import org.starloco.locos.auction.AuctionManager;
import org.starloco.locos.client.BasePlayer;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NpcExchange {
    private BasePlayer player;
    private NpcTemplate npc;
    private long kamas1 = 0;
    private long kamas2 = 0;
    private ArrayList<World.Couple<Integer,Integer>> items1 = new ArrayList<>();
    private ArrayList<World.Couple<Integer,Integer>> items2 = new ArrayList<World.Couple<Integer,Integer>>();
    private boolean ok1;
    private boolean ok2;
    private Auction auction;

    public NpcExchange(BasePlayer p, NpcTemplate n) {
        this.player = p;
        AuctionManager.getInstance().onPlayerOpenExchange(player, this);
        this.setNpc(n);
    }

    public ArrayList<World.Couple<Integer,Integer>> getItems1() {
        return items1;
    }

    public synchronized long getKamas(boolean b) {
        if(b)return this.kamas2;
        return this.kamas1;
    }

    public boolean isPlayerOk() {
        return ok1;
    }

    public synchronized void toogleOK(boolean paramBoolean) {
        if(paramBoolean) {
            this.ok2 = (!this.ok2);
            SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
            AuctionManager.getInstance().onPlayerAccept(player, this);
        } else {
            this.ok1 = (!this.ok1);
            SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok1, this.player.getId());
            if(AuctionManager.getInstance().onPlayerAccept(null,this))
                return;
        }
        if((this.ok2) && (this.ok1))
            apply();
    }

    public synchronized void setKamas(boolean ok, long kamas) {
        if(kamas < 0L)
            return;
        this.ok2 = (this.ok1 = false);
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok1, this.player.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
        if(ok) {
            this.kamas2 = kamas;
            SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player.getGameClient(), 'G', "", String.valueOf(kamas));
            putAllGiveItem();
            return;
        }
        if(kamas > this.player.getKamas())
            return;
        this.kamas1 = kamas;
        SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player, 'G', "", String.valueOf(kamas));
        putAllGiveItem();
    }

    public synchronized void cancel() {
        if((this.player.getAccount() != null) && (this.player.getGameClient() != null))
            SocketManager.GAME_SEND_EV_PACKET(this.player.getGameClient());
        this.player.setExchangeAction(null);
    }

    public synchronized void apply() {
        for(World.Couple<Integer, Integer> couple : items1) {
            if(couple.second == 0)continue;
            if(World.world.getGameObject(couple.first).getPosition() != Constant.ITEM_POS_NO_EQUIPED)continue;
            if(!this.player.hasItemGuid(couple.first)) {
                couple.second = 0;//On met la quantité a 0 pour éviter les problemes
                continue;
            }
            GameObject obj = World.world.getGameObject(couple.first);
            if((obj.getQuantity() - couple.second) < 1) {
                this.player.removeItem(couple.first);
                if(this.auction != null) {
                    this.auction.setObject(obj);
                } else {
                    World.world.removeGameObject(obj.getGuid());
                }
                couple.second = obj.getQuantity();
                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, couple.first);
            } else {
                if(this.auction != null) {
                    this.auction.setObject(obj.getClone(couple.second, true));
                }
                obj.setQuantity(obj.getQuantity()-couple.second);
                SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, obj);
            }
        }

        for(World.Couple<Integer, Integer> couple1 : items2) {
            if(couple1.second == 0)continue;
            if(World.world.getObjTemplate(couple1.first) == null)continue;
            ObjectTemplate t = World.world.getObjTemplate(couple1.first);

            GameObject obj1 = t.createNewItem(couple1.second, false);
            if(this.player.addItem(obj1, true, false))
                World.world.addGameObject(obj1);

            if(t.getType() == Constant.ITEM_TYPE_CERTIF_MONTURE) {
                //obj.setMountStats(this.getPlayer(), null);
                Mount mount = new Mount(Constant.getMountColorByParchoTemplate(obj1.getTemplate().getId()), this.player.getId(), false);
                obj1.clearStats();
                obj1.getStats().addOneStat(995, (mount.getId()));
                obj1.getTxtStat().put(996, this.player.getName());
                obj1.getTxtStat().put(997, mount.getName());
                mount.setToMax();
            }

            SocketManager.GAME_SEND_Im_PACKET(this.player, "021;" + couple1.second + "~" + couple1.first);
        }
        this.player.setExchangeAction(null);
        SocketManager.GAME_SEND_EXCHANGE_VALID(this.player.getGameClient(), 'a');
        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(this.player);
    }

    public synchronized void addItem(int obj, int qua) {
        if(qua <= 0)return;
        if(World.world.getGameObject(obj) == null)return;
        this.ok1 = (this.ok2 = false);
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok1, this.player.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
        String str = obj + "|" + qua;
        World.Couple<Integer,Integer> couple = getCoupleInList(items1, obj);
        if(couple != null) {
            couple.second += qua;
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player, 'O', "+", ""+obj+"|"+couple.second);
            putAllGiveItem();
            return;
        }
        SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player, 'O', "+", str);
        items1.add(new World.Couple<>(obj, qua));
        putAllGiveItem();
    }

    public synchronized void removeItem(int guid, int qua) {
        if(qua < 0)return;
        this.ok1 = (this.ok2 = false);
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok1, this.player.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
        if(World.world.getGameObject(guid) == null)return;
        World.Couple<Integer,Integer> couple = getCoupleInList(items1,guid);
        int newQua = couple.second - qua;
        if(newQua <1) {
            items1.remove(couple);
            putAllGiveItem();
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player, 'O', "-", ""+guid);
        } else {
            couple.second = newQua;
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player, 'O', "+", ""+guid+"|"+newQua);
            putAllGiveItem();
        }
    }

    public synchronized int getQuaItem(int obj, boolean b) {
        ArrayList<World.Couple<Integer, Integer>> list;
        if(b)
            list = this.items2;
        else
            list = this.items1;
        for(World.Couple<Integer, Integer> item: list)
            if(item.first == obj)
                return item.second;
        return 0;
    }

    public synchronized void clearItems() {
        if(this.items2.isEmpty()) return;
        for(World.Couple<Integer, Integer> i: items2)
            SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player.getGameClient(), 'O', "-", i.first+"");
        this.kamas2 = 0;
        this.items2.clear();
        if(this.ok2) {
            this.ok2 = false;
            SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
        }
    }

    private synchronized World.Couple<Integer, Integer> getCoupleInList(ArrayList<World.Couple<Integer, Integer>> items, int guid) {
        for(World.Couple<Integer, Integer> couple : items)
            if(couple.first == guid)
                return couple;
        return null;
    }

    public synchronized void putAllGiveItem() {
        List<World.Couple<Integer,Integer>> itemsTemplates = items1.stream()
                // Get template ID for each object
                .map(i -> new World.Couple<>(World.world.getGameObject(i.first).getTemplate().getId(), i.second))
                .collect(Collectors.toList());
        World.Couple<Integer,Integer> outcome = this.npc.barterOutcome(this.player, itemsTemplates);

        this.clearItems();
        if(outcome == null) {
            if(this.ok2) {
                this.ok2 = false;
                SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
            }
            return;
        }

        String str = outcome.first + "|" + outcome.second + "|" + outcome.first + "|" + World.world.getObjTemplate(outcome.first).getStrTemplate();
        this.items2.add(new World.Couple<>(outcome.first, outcome.second));
        SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player.getGameClient(), 'O', "+", str);

        if(!this.ok2) {
            this.ok2 = true;
            SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
        }
    }

    public NpcTemplate getNpc() {
        return npc;
    }

    public void setNpc(NpcTemplate npc) {
        this.npc = npc;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Auction getAuction() {
        return auction;
    }
}