package org.starloco.locos.entity.exchange;

import org.starloco.locos.player.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Logging;
import org.starloco.locos.item.FullItem;
import org.starloco.locos.item.ItemTemplate;

import java.util.ArrayList;

public class PlayerExchange extends Exchange {

    public PlayerExchange(Player player1, Player player2) {
        super(player1, player2);
    }

    private boolean isPodsOK(byte i) {
        if (this instanceof CraftSecure)
            return true;

        int newpods = 0;
        int oldpods = 0;
        if (i == 1) {
            int podsmax = this.player1.getMaxPod();
            int pods = this.player1.getPodUsed();
            for (Couple<Integer, Integer> couple : items2) {
                if (couple.second == 0)
                    continue;
                FullItem obj = World.world.getGameObject(couple.first);
                newpods += obj.template().getPod() * couple.second;
            }
            if (newpods == 0) {
                return true;
            }
            for (Couple<Integer, Integer> couple : items1) {
                if (couple.second == 0)
                    continue;
                FullItem obj = World.world.getGameObject(couple.first);
                oldpods += obj.template().getPod() * couple.second;
            }
            if ((newpods + pods - oldpods) > podsmax) {
                // Erreur 70
                // 1 + 70 => 170
                SocketManager.GAME_SEND_Im_PACKET(this.player1, "170");
                return false;
            }
        } else {
            int podsmax = this.player2.getMaxPod();
            int pods = this.player2.getPodUsed();
            for (Couple<Integer, Integer> couple : items1) {
                if (couple.second == 0)
                    continue;
                FullItem obj = World.world.getGameObject(couple.first);
                newpods += obj.template().getPod() * couple.second;
            }
            if (newpods == 0) {
                return true;
            }
            for (Couple<Integer, Integer> couple : items2) {
                if (couple.second == 0)
                    continue;
                FullItem obj = World.world.getGameObject(couple.first);
                oldpods += obj.template().getPod() * couple.second;
            }
            if ((newpods + pods - oldpods) > podsmax) {
                SocketManager.GAME_SEND_Im_PACKET(this.player2, "170");
                return false;
            }
        }
        return true;
    }

    public synchronized long getKamas(int guid) {
        int i = 0;
        if (this.player1.getId() == guid)
            i = 1;
        else if (this.player2.getId() == guid)
            i = 2;

        if (i == 1)
            return kamas1;
        else if (i == 2)
            return kamas2;
        return 0;
    }

    public synchronized boolean toogleOk(int guid) {
        byte i = (byte) (this.player1.getId() == guid ? 1 : 2);
        if (this.isPodsOK(i)) {
            if (i == 1) {
                ok1 = !ok1;
                SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok1, guid);
                SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok1, guid);
            } else if (i == 2) {
                ok2 = !ok2;
                SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok2, guid);
                SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok2, guid);
            }
            return (ok1 && ok2);
        }
        return false;
    }

    public synchronized void setKamas(int guid, long k) {
        ok1 = false;
        ok2 = false;

        int i = 0;
        if (this.player1.getId() == guid)
            i = 1;
        else if (this.player2.getId() == guid)
            i = 2;
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok1, this.player1.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok1, this.player1.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok2, this.player2.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok2, this.player2.getId());
        if (k < 0)
            return;
        if (i == 1) {
            kamas1 = k;
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player1, 'G', "", k
                    + "");
            SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player2.getGameClient(), 'G', "", k
                    + "");
        } else if (i == 2) {
            kamas2 = k;
            SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player1.getGameClient(), 'G', "", k
                    + "");
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player2, 'G', "", k
                    + "");
        }
    }

    public synchronized void cancel() {
        if (this.player1.getAccount() != null)
            if (this.player1.getGameClient() != null)
                SocketManager.GAME_SEND_EV_PACKET(this.player1.getGameClient());
        if (this.player2.getAccount() != null)
            if (this.player2.getGameClient() != null)
                SocketManager.GAME_SEND_EV_PACKET(this.player2.getGameClient());
        this.player1.setExchangeAction(null);
        this.player2.setExchangeAction(null);
    }

    public synchronized void apply() {
        String str = "";
        try {
            str += this.player1.getName() + " : ";
            for (Couple<Integer, Integer> couple1 : items1) {
                str += ", ["
                        + World.world.getGameObject(couple1.first).template().getId()
                        + "@" + couple1.first + ";" + couple1.second + "]";
            }
            str += " avec " + kamas1 + " K.\n";
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            str += "Avec " + this.player2.getName();
            for (Couple<Integer, Integer> couple2 : items2) {
                str += ", ["
                        + World.world.getGameObject(couple2.first).template().getId()
                        + "@" + couple2.first + ";" + couple2.second + "]";
            }
            str += " avec " + kamas2 + " K.";
            if (Logging.USE_LOG)
                Logging.getInstance().write("Object", "Exchange : " + str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Gestion des Kamas
        this.player1.addKamas((-kamas1 + kamas2));
        this.player2.addKamas((-kamas2 + kamas1));
        for (Couple<Integer, Integer> couple : items1) // Les items du player vers le player2
        {
            if (couple.second == 0)
                continue;
            if (World.world.getGameObject(couple.first) == null)
                continue;
            if (World.world.getGameObject(couple.first).getPosition() != Constant.ITEM_POS_NO_EQUIPED)
                continue;
            if (!this.player1.hasItemGuid(couple.first))//Si le player n'a pas l'item (Ne devrait pas arriver : wpepro)
            {
                couple.second = 0;//On met la quantit� a 0 pour �viter les problemes
                continue;
            }
            FullItem obj = World.world.getGameObject(couple.first);
            if ((obj.getQuantity() - couple.second) < 1)//S'il ne reste plus d'item apres l'�change
            {
                this.player1.removeItem(couple.first);
                couple.second = obj.getQuantity();
                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player1, couple.first);
                if (!this.player2.addItem(obj, true, false))//Si le joueur avait un item similaire
                    World.world.removeGameObject(couple.first);//On supprime l'item inutile
            } else {
                obj.setQuantity(obj.getQuantity() - couple.second);
                SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player1, obj);
                FullItem newObj = obj.getClone(couple.second, true);
                if (this.player2.addItem(newObj, true, false))//Si le joueur n'avait pas d'item similaire
                    World.world.addGameObject(newObj);//On ajoute l'item au World
            }
        }
        for (Couple<Integer, Integer> couple : items2) {
            if (couple.second == 0)
                continue;
            if (World.world.getGameObject(couple.first) == null)
                continue;
            if (World.world.getGameObject(couple.first).getPosition() != Constant.ITEM_POS_NO_EQUIPED)
                continue;
            if (!this.player2.hasItemGuid(couple.first))//Si le player n'a pas l'item (Ne devrait pas arriver)
            {
                couple.second = 0;//On met la quantit� a 0 pour �viter les problemes
                continue;
            }
            this.giveObject(couple, World.world.getGameObject(couple.first));
        }
        //Fin
        this.player1.setExchangeAction(null);
        this.player2.setExchangeAction(null);
        SocketManager.GAME_SEND_Ow_PACKET(this.player1);
        SocketManager.GAME_SEND_Ow_PACKET(this.player2);
        SocketManager.GAME_SEND_STATS_PACKET(this.player1);
        SocketManager.GAME_SEND_STATS_PACKET(this.player2);
        SocketManager.GAME_SEND_EXCHANGE_VALID(this.player1.getGameClient(), 'a');
        SocketManager.GAME_SEND_EXCHANGE_VALID(this.player2.getGameClient(), 'a');
        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(this.player1);
        ((PlayerData) DatabaseManager.get(PlayerData.class)).update(this.player2);
    }

    protected void giveObject(Couple<Integer, Integer> couple, FullItem object) {
        if(object == null) return;
        if ((object.getQuantity() - couple.second) < 1) {
            this.player2.removeItem(couple.first);
            couple.second = object.getQuantity();
            SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player2, couple.first);
            if (!this.player1.addItem(object, true, false)) World.world.removeGameObject(couple.first);
        } else {
            object.setQuantity(object.getQuantity() - couple.second);
            SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player2, object);
            FullItem newObj = object.getClone(couple.second, true);
            if (this.player1.addItem(newObj, true, false)) World.world.addGameObject(newObj);
        }
    }

    public synchronized void addItem(int guid, int qua, int pguid) {
        ok1 = false;
        ok2 = false;

        FullItem obj = World.world.getGameObject(guid);
        int i = 0;

        if (this.player1.getId() == pguid)
            i = 1;
        if (this.player2.getId() == pguid)
            i = 2;

        if (qua == 1)
            qua = 1;
        String str = guid + "|" + qua;
        if (obj == null)
            return;
        if (obj.getPosition() != Constant.ITEM_POS_NO_EQUIPED)
            return;

        if (this instanceof CraftSecure) {
            ArrayList<ItemTemplate> tmp = new ArrayList<>();
            for (Couple<Integer, Integer> couple : this.items1) {
                FullItem _tmp = World.world.getGameObject(couple.first);
                if (_tmp == null)
                    continue;
                if (!tmp.contains(_tmp.template()))
                    tmp.add(_tmp.template());
            }
            for (Couple<Integer, Integer> couple : this.items2) {
                FullItem _tmp = World.world.getGameObject(couple.first);
                if (_tmp == null)
                    continue;
                if (!tmp.contains(_tmp.template()))
                    tmp.add(_tmp.template());
            }

            if (!tmp.contains(obj.template())) {
                if (tmp.size() + 1 > ((CraftSecure) this).getMaxCase()) {
                    SocketManager.GAME_SEND_MESSAGE((this.player1.getId() == pguid) ? this.player1 : this.player2, "Impossible d'ajouter plus d'ingrédients.", "B9121B");
                    return;
                }
            }
        }

        String add = "|" + obj.template().getId() + "|"
                + obj.encodeStats();
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok1, this.player1.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok1, this.player1.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok2, this.player2.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok2, this.player2.getId());
        if (i == 1) {
            Couple<Integer, Integer> couple = getCoupleInList(items1, guid);
            if (couple != null) {
                couple.second += qua;
                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player1, 'O', "+", ""
                        + guid + "|" + couple.second);
                SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player2.getGameClient(), 'O', "+", ""
                        + guid + "|" + couple.second + add);
                return;
            }
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player1, 'O', "+", str);
            SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player2.getGameClient(), 'O', "+", str
                    + add);
            items1.add(new Couple<>(guid, qua));
        } else if (i == 2) {
            Couple<Integer, Integer> couple = getCoupleInList(items2, guid);
            if (couple != null) {
                couple.second += qua;
                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player2, 'O', "+", ""
                        + guid + "|" + couple.second);
                SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player1.getGameClient(), 'O', "+", ""
                        + guid + "|" + couple.second + add);
                return;
            }
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player2, 'O', "+", str);
            SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player1.getGameClient(), 'O', "+", str
                    + add);
            items2.add(new Couple<>(guid, qua));
        }
    }

    public synchronized void removeItem(int guid, int qua, int pguid) {
        int i = 0;
        if (this.player1.getId() == pguid)
            i = 1;
        else if (this.player2.getId() == pguid)
            i = 2;
        ok1 = false;
        ok2 = false;

        SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok1, this.player1.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok1, this.player1.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player1.getGameClient(), ok2, this.player2.getId());
        SocketManager.GAME_SEND_EXCHANGE_OK(this.player2.getGameClient(), ok2, this.player2.getId());

        FullItem object = World.world.getGameObject(guid);
        if (object == null) return;
        String add = "|" + object.template().getId() + "|" + object.encodeStats();

        if (i == 1) {
            Couple<Integer, Integer> couple = getCoupleInList(items1, guid);

            if(couple == null) return;
            int newQua = couple.second - qua;

            if (newQua < 1) {
                items1.remove(couple);
                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player1, 'O', "-", "" + guid);
                SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player2.getGameClient(), 'O', "-", "" + guid);
            } else {
                couple.second = newQua;
                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player1, 'O', "+", "" + guid + "|" + newQua);
                SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player2.getGameClient(), 'O', "+", "" + guid + "|" + newQua + add);
            }
        } else if (i == 2) {
            Couple<Integer, Integer> couple = getCoupleInList(items2, guid);

            if(couple == null) return;
            int newQua = couple.second - qua;

            if (newQua < 1) {
                items2.remove(couple);
                SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player1.getGameClient(), 'O', "-", "" + guid);
                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player2, 'O', "-", "" + guid);
            } else {
                couple.second = newQua;
                SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player1.getGameClient(), 'O', "+", "" + guid + "|" + newQua + add);
                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player2, 'O', "+", "" + guid + "|" + newQua);
            }
        }
    }

    public synchronized int getQuaItem(int itemID, int playerGuid) {
        ArrayList<Couple<Integer, Integer>> items;
        if (this.player1.getId() == playerGuid)
            items = items1;
        else
            items = items2;
        for (Couple<Integer, Integer> curCoupl : items)
            if (curCoupl.first == itemID)
                return curCoupl.second;
        return 0;
    }

    /**
     * Other Exchange *
     */
    public static class NpcExchangePets {
        private Player player;
        private NpcTemplate npc;
        private long kamas1 = 0;
        private long kamas2 = 0;
        private ArrayList<Couple<Integer, Integer>> items1 = new ArrayList<>();
        private ArrayList<Couple<Integer, Integer>> items2 = new ArrayList<>();
        private boolean ok1;
        private boolean ok2;

        public NpcExchangePets(Player p, NpcTemplate n) {
            this.player = p;
            this.npc = n;
        }

        public synchronized void toogleOK(boolean paramBoolean) {
            if (paramBoolean) {
                this.ok2 = (!this.ok2);
                SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
            } else {
                this.ok1 = (!this.ok1);
                SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok1, this.player.getId());
            }
            if ((this.ok2) && (this.ok1))
                apply();
        }

        public synchronized void setKamas(boolean paramBoolean, long paramLong) {
            if (paramLong < 0L)
                return;
            this.ok2 = (this.ok1 = false);
            SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok1, this.player.getId());
            SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
            if (paramBoolean) {
                this.kamas2 = paramLong;
                SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player.getGameClient(), 'G', "", paramLong
                        + "");
                return;
            }
            if (paramLong > this.player.getKamas())
                return;
            this.kamas1 = paramLong;
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player, 'G', "", paramLong
                    + "");
        }

        public synchronized void cancel() {
            if ((this.player.getAccount() != null) && (this.player.getGameClient() != null))
                SocketManager.GAME_SEND_EV_PACKET(this.player.getGameClient());
            this.player.setExchangeAction(null);
        }

        public synchronized void apply() {
            FullItem objetToChange = null;
            for (Couple<Integer, Integer> couple : items1) {
                if (couple.second == 0)
                    continue;
                if (World.world.getGameObject(couple.first).getPosition() != Constant.ITEM_POS_NO_EQUIPED)
                    continue;
                if (!player.hasItemGuid(couple.first))//Si le player n'a pas l'item (Ne devrait pas arriver)
                {
                    couple.second = 0;//On met la quantit� a 0 pour �viter les problemes
                    continue;
                }
                FullItem obj = World.world.getGameObject(couple.first);
                objetToChange = obj;
                if ((obj.getQuantity() - couple.second) < 1)//S'il ne reste plus d'item apres l'�change
                {
                    player.removeItem(couple.first);
                    couple.second = obj.getQuantity();
                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, couple.first);
                } else {
                    obj.setQuantity(obj.getQuantity() - couple.second);
                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(player, obj);
                }
            }

            for (Couple<Integer, Integer> couple1 : items2) {
                if (couple1.second == 0)
                    continue;
                if (World.world.getItemTemplate(couple1.first) == null)
                    continue;
                if (World.world.getGameObject(objetToChange.getGuid()) == null)
                    continue;
                FullItem obj1 = null;
                if (World.world.getItemTemplate(couple1.first).getTypeID() == 18)
                    obj1 = World.world.getItemTemplate(couple1.first).createNewFamilier(objetToChange);
                if (World.world.getItemTemplate(couple1.first).getTypeID() == 77)
                    obj1 = World.world.getItemTemplate(couple1.first).createNewCertificat(objetToChange);

                if (obj1 == null)
                    continue;
                if (this.player.addItem(obj1, true, false))
                    World.world.addGameObject(obj1);
                SocketManager.GAME_SEND_Im_PACKET(this.player, "021;"
                        + couple1.second + "~" + couple1.first);
            }
            World.world.removeGameObject(objetToChange.getGuid());
            this.player.setExchangeAction(null);
            SocketManager.GAME_SEND_EXCHANGE_VALID(this.player.getGameClient(), 'a');
            SocketManager.GAME_SEND_Ow_PACKET(this.player);
            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(this.player);
        }

        public synchronized void addItem(int obj, int qua) {
            if (qua <= 0)
                return;
            if (World.world.getGameObject(obj) == null)
                return;
            this.ok1 = (this.ok2 = false);
            SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok1, this.player.getId());
            SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
            String str = obj + "|" + qua;
            Couple<Integer, Integer> couple = getCoupleInList(items1, obj);
            if (couple != null) {
                couple.second += qua;
                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(player, 'O', "+", ""
                        + obj + "|" + couple.second);
                return;
            }
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(player, 'O', "+", str);
            items1.add(new Couple<Integer, Integer>(obj, qua));
            if (verifIfAlonePets() || verifIfAloneParcho()) {
                if (items1.size() == 1) {
                    int id = -1;
                    FullItem objet = null;
                    for (Couple<Integer, Integer> i : items1) {
                        if (World.world.getGameObject(i.first) == null)
                            continue;
                        objet = World.world.getGameObject(i.first);
                        if (World.world.getGameObject(i.first).template().getTypeID() == 18) {
                            id = Constant.getParchoByIdPets(World.world.getGameObject(i.first).template().getId());
                        } else if (World.world.getGameObject(i.first).template().getTypeID() == 77) {
                            id = Constant.getPetsByIdParcho(World.world.getGameObject(i.first).template().getId());
                        }
                    }
                    if (id == -1)
                        return;
                    String str1 = id + "|" + 1 + "|" + id + "|"
                            + objet.encodeStats();
                    this.items2.add(new Couple<Integer, Integer>(id, 1));
                    SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player.getGameClient(), 'O', "+", str1);
                    this.ok2 = true;
                    SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
                } else {
                    clearNpcItems();
                    this.ok2 = false;
                    SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
                }
            } else {
                clearNpcItems();
                this.ok2 = false;
                SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
            }
        }

        public synchronized void removeItem(int guid, int qua) {
            if (qua < 0)
                return;
            this.ok1 = (this.ok2 = false);
            SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok1, this.player.getId());
            SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
            if (World.world.getGameObject(guid) == null)
                return;
            Couple<Integer, Integer> couple = getCoupleInList(items1, guid);
            int newQua = couple.second - qua;
            if (newQua < 1)//Si il n'y a pu d'item
            {
                items1.remove(couple);
                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player, 'O', "-", ""
                        + guid);
            } else {
                couple.second = newQua;
                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player, 'O', "+", ""
                        + guid + "|" + newQua);
            }
            if (verifIfAlonePets()) {
                if (items1.size() == 1) {
                    int id = -1;
                    FullItem objet = null;
                    for (Couple<Integer, Integer> i : items1) {
                        if (World.world.getGameObject(i.first) == null)
                            continue;
                        objet = World.world.getGameObject(i.first);
                        if (World.world.getGameObject(i.first).template().getTypeID() == 18) {
                            id = Constant.getParchoByIdPets(World.world.getGameObject(i.first).template().getId());
                        } else if (World.world.getGameObject(i.first).template().getTypeID() == 77) {
                            id = Constant.getPetsByIdParcho(World.world.getGameObject(i.first).template().getId());
                        }
                    }
                    if (id == -1)
                        return;
                    String str = id + "|" + 1 + "|" + id + "|"
                            + objet.encodeStats();
                    this.items2.add(new Couple<Integer, Integer>(id, 1));
                    SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player.getGameClient(), 'O', "+", str);
                    this.ok2 = true;
                    SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
                } else {
                    clearNpcItems();
                    this.ok2 = false;
                    SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
                }
            } else {
                clearNpcItems();
                this.ok2 = false;
                SocketManager.GAME_SEND_EXCHANGE_OK(this.player.getGameClient(), this.ok2);
            }
        }

        public boolean verifIfAlonePets() {
            for (Couple<Integer, Integer> i : items1)
                if (World.world.getGameObject(i.first).template().getTypeID() != 18)
                    return false;
            return true;
        }

        public boolean verifIfAloneParcho() {
            for (Couple<Integer, Integer> i : items1)
                if (World.world.getGameObject(i.first).template().getTypeID() != 77)
                    return false;
            return true;
        }

        public synchronized void clearNpcItems() {
            for (Couple<Integer, Integer> i : items2)
                SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.player.getGameClient(), 'O', "-", i.first
                        + "");
            this.items2.clear();
        }

        private synchronized Couple<Integer, Integer> getCoupleInList(
                ArrayList<Couple<Integer, Integer>> items, int guid) {
            for (Couple<Integer, Integer> couple : items)
                if (couple.first == guid)
                    return couple;
            return null;
        }

        public synchronized int getQuaItem(int obj, boolean b) {
            ArrayList<Couple<Integer, Integer>> list;
            if (b)
                list = this.items2;
            else
                list = this.items1;

            for (Couple<Integer, Integer> item : list)
                if (item.first == obj)
                    return item.second;
            return 0;
        }

        public NpcTemplate getNpc() {
            return npc;
        }

        public void setNpc(NpcTemplate npc) {
            this.npc = npc;
        }
    }

    public static class NpcRessurectPets {
        private Player perso;
        private NpcTemplate npc;
        private long kamas1 = 0;
        private long kamas2 = 0;
        private ArrayList<Couple<Integer, Integer>> items1 = new ArrayList<Couple<Integer, Integer>>();
        private ArrayList<Couple<Integer, Integer>> items2 = new ArrayList<Couple<Integer, Integer>>();
        private boolean ok1;
        private boolean ok2;

        public NpcRessurectPets(Player p, NpcTemplate n) {
            this.perso = p;
            this.npc = n;
        }

        public synchronized long getKamas(boolean b) {
            if (b)
                return this.kamas2;
            return this.kamas1;
        }

        public synchronized void toogleOK(boolean paramBoolean) {
            if (paramBoolean) {
                this.ok2 = (!this.ok2);
                SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok2);
            } else {
                this.ok1 = (!this.ok1);
                SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok1, this.perso.getId());
            }
            if ((this.ok2) && (this.ok1))
                apply();
        }

        public synchronized void setKamas(boolean paramBoolean, long paramLong) {
            if (paramLong < 0L)
                return;
            this.ok2 = (this.ok1 = false);
            SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok1, this.perso.getId());
            SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok2);
            if (paramBoolean) {
                this.kamas2 = paramLong;
                SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.perso.getGameClient(), 'G', "", paramLong
                        + "");
                return;
            }
            if (paramLong > this.perso.getKamas())
                return;
            this.kamas1 = paramLong;
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.perso, 'G', "", paramLong
                    + "");
        }

        public synchronized void cancel() {
            if ((this.perso.getAccount() != null)
                    && (this.perso.getGameClient() != null))
                SocketManager.GAME_SEND_EV_PACKET(this.perso.getGameClient());
            this.perso.setExchangeAction(null);
        }

        public synchronized void apply() {
            for (Couple<Integer, Integer> item : items1) {
                FullItem object = World.world.getGameObject(item.first);
                if (object.template().getId() == 8012) {
                    if ((object.getQuantity() - item.second) < 1) {
                        perso.removeItem(item.first);
                        item.second = object.getQuantity();
                        SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(perso, item.first);
                    } else {
                        object.setQuantity(object.getQuantity() - item.second);
                        SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(perso, object);
                    }
                } else {
                    PetEntry pet = World.world.getPetsEntry_legacy(item.first);
                    if (pet != null) {
                        pet.resurrection();
                        SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(this.perso, object);
                    }
                }
            }
            this.perso.setExchangeAction(null);
            SocketManager.GAME_SEND_EXCHANGE_VALID(this.perso.getGameClient(), 'a');
            SocketManager.GAME_SEND_Ow_PACKET(this.perso);
            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(this.perso);
        }

        public synchronized void addItem(int obj, int qua) {
            if (qua <= 0)
                return;
            if (World.world.getGameObject(obj) == null)
                return;
            this.ok1 = (this.ok2 = false);
            SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok1, this.perso.getId());
            SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok2);
            String str = obj + "|" + qua;
            Couple<Integer, Integer> couple = getCoupleInList(items1, obj);
            if (couple != null) {
                couple.second += qua;
                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(perso, 'O', "+", ""
                        + obj + "|" + couple.second);
                return;
            }
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(perso, 'O', "+", str);
            items1.add(new Couple<Integer, Integer>(obj, qua));
            if (verification()) {
                if (items1.size() == 2) {
                    int id = -1;
                    FullItem objet = null;

                    for (Couple<Integer, Integer> i : items1) {
                        objet = World.world.getGameObject(i.first);

                        if (objet == null)
                            continue;
                        if (objet.template().getTypeID() == 90) {
                            PetEntry pet = World.world.getPetsEntry_legacy(i.first);
                            if(pet != null) {
                                id = pet.getTemplate();
                                break;
                            }
                        }
                    }

                    if (id == -1 || objet == null)
                        return;
                    String str1 = id + "|" + 1 + "|" + id + "|"
                            + objet.encodeStats();
                    this.items2.add(new Couple<Integer, Integer>(id, 1));
                    SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.perso.getGameClient(), 'O', "+", str1);
                    this.ok2 = true;
                    SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok2);
                } else {
                    clearNpcItems();
                    this.ok2 = false;
                    SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok2);
                }
            } else {
                clearNpcItems();
                this.ok2 = false;
                SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok2);
            }
        }

        public synchronized void removeItem(int guid, int qua) {
            if (qua < 0)
                return;
            this.ok1 = (this.ok2 = false);
            SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok1, this.perso.getId());
            SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok2);
            if (World.world.getGameObject(guid) == null)
                return;
            Couple<Integer, Integer> couple = getCoupleInList(items1, guid);
            int newQua = couple.second - qua;
            if (newQua < 1)//Si il n'y a pu d'item
            {
                items1.remove(couple);
                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.perso, 'O', "-", ""
                        + guid);
            } else {
                couple.second = newQua;
                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.perso, 'O', "+", ""
                        + guid + "|" + newQua);
            }
            if (verification()) {
                if (items1.size() == 2) {
                    int id = -1;
                    FullItem objet = null;

                    for (Couple<Integer, Integer> i : items1) {
                        objet = World.world.getGameObject(i.first);

                        if (objet == null)
                            continue;
                        if (objet.template().getTypeID() == 90) {
                            id = World.world.getPetsEntry_legacy(i.first).getTemplate();
                            break;
                        }
                    }

                    if (id == -1 || objet == null)
                        return;

                    String str = id + "|" + 1 + "|" + id + "|"
                            + objet.encodeStats();
                    this.items2.add(new Couple<Integer, Integer>(id, 1));
                    SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.perso.getGameClient(), 'O', "+", str);
                    this.ok2 = true;
                    SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok2);
                } else {
                    clearNpcItems();
                    this.ok2 = false;
                    SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok2);
                }
            } else {
                clearNpcItems();
                this.ok2 = false;
                SocketManager.GAME_SEND_EXCHANGE_OK(this.perso.getGameClient(), this.ok2);
            }
        }

        public boolean verification() {
            boolean verif = true;
            for (Couple<Integer, Integer> item : items1) {
                FullItem object = World.world.getGameObject(item.first);
                if ((object.template().getId() != 8012 && object.template().getTypeID() != 90)
                        || item.second > 1)
                    verif = false;
            }
            return verif;
        }

        public synchronized void clearNpcItems() {
            for (Couple<Integer, Integer> i : items2)
                SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK(this.perso.getGameClient(), 'O', "-", i.first
                        + "");
            this.items2.clear();
        }

        private synchronized Couple<Integer, Integer> getCoupleInList(
                ArrayList<Couple<Integer, Integer>> items, int guid) {
            for (Couple<Integer, Integer> couple : items)
                if (couple.first == guid)
                    return couple;
            return null;
        }

        public synchronized int getQuaItem(int obj, boolean b) {
            ArrayList<Couple<Integer, Integer>> list;
            if (b)
                list = this.items2;
            else
                list = this.items1;

            for (Couple<Integer, Integer> item : list)
                if (item.first == obj)
                    return item.second;
            return 0;
        }

        public NpcTemplate getNpc() {
            return npc;
        }

        public void setNpc(NpcTemplate npc) {
            this.npc = npc;
        }
    }
    /** Fin Other Exchange **/
}