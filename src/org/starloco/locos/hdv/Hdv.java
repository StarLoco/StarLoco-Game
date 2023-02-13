package org.starloco.locos.hdv;

import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.game.HdvObjectData;
import org.starloco.locos.database.data.login.PlayerData;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Logging;
import org.starloco.locos.object.ObjectTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Hdv {

    private int hdvId;
    private float taxe;
    private short sellTime;
    private short maxAccountItem;
    private String strCategory;
    private short lvlMax;

    private Map<Integer, HdvCategory> categorys = new HashMap<Integer, HdvCategory>();
    private Map<Integer, Couple<Integer, Integer>> path = new HashMap<Integer, Couple<Integer, Integer>>(); //<LigneID,<CategID,TemplateID>>

    private DecimalFormat pattern = new DecimalFormat("0.0");

    public Hdv(int hdvID, float taxe, short sellTime, short maxItemCompte,
               short lvlMax, String strCategory) {
        this.hdvId = hdvID;
        this.taxe = taxe;
        this.maxAccountItem = maxItemCompte;
        this.strCategory = strCategory;
        this.lvlMax = lvlMax;
        int categId;
        for (String strCategID : strCategory.split(",")) {
            categId = Integer.parseInt(strCategID);
            this.categorys.put(categId, new HdvCategory(categId));
        }
    }

    public int getHdvId() {
        return hdvId;
    }

    public float getTaxe() {
        return taxe;
    }

    public short getSellTime() {
        return sellTime;
    }

    public short getMaxAccountItem() {
        return maxAccountItem;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public short getLvlMax() {
        return lvlMax;
    }

    public Map<Integer, HdvCategory> getCategorys() {
        return categorys;
    }

    public boolean haveCategory(int categ) {
        return categorys.containsKey(categ);
    }

    public HdvLine getLine(int lineId) {
        if (this.path == null || this.path.get(lineId) == null || this.getCategorys() == null)
            return null;

        int categoryId = this.path.get(lineId).first;
        int templateId = this.path.get(lineId).second;

        HdvCategory category = this.getCategorys().get(categoryId);

        if (category == null)
            return null;

        HdvTemplate template = category.getTemplate(templateId);

        if (template == null)
            return null;

        return template.getLine(lineId);
    }

    public void addEntry(HdvEntry toAdd, boolean load) {
        toAdd.setHdvId(this.getHdvId());
        int categoryId = toAdd.getGameObject().getTemplate().getType();
        int templateId = toAdd.getGameObject().getTemplate().getId();
        if (this.getCategorys().get(categoryId) == null)
            return;
        this.getCategorys().get(categoryId).addEntry(toAdd);
        this.path.put(toAdd.getLineId(), new Couple<>(categoryId, templateId));
        if (!load) {
            ((HdvObjectData) DatabaseManager.get(HdvObjectData.class)).insert(toAdd);
        }
        World.world.addHdvItem(toAdd.getOwner(), this.getHdvId(), toAdd);
    }

    public boolean delEntry(HdvEntry toDel) {
        boolean toReturn = this.getCategorys().get(toDel.getGameObject().getTemplate().getType()).delEntry(toDel);
        if (toReturn) {
            this.path.remove(toDel.getLineId());
            World.world.removeHdvItem(toDel.getOwner(), toDel.getHdvId(), toDel);
        }
        return toReturn;
    }

    public ArrayList<HdvEntry> getAllEntry() {
        ArrayList<HdvEntry> toReturn = new ArrayList<>();
        for (HdvCategory curCat : this.getCategorys().values())
            toReturn.addAll(curCat.getAllEntry());
        return toReturn;
    }

    public synchronized boolean buyItem(int ligneID, byte amount, int price,
                                        Player newOwner) {
        boolean toReturn = true;
        try {
            if (newOwner.getKamas() < price)
                return false;

            HdvLine ligne = this.getLine(ligneID);
            HdvEntry toBuy = ligne.doYouHave(amount, price);

            if (toBuy.buy) return false;
            toBuy.buy = true;

            newOwner.addKamas(price * -1);//Retire l'argent � l'acheteur (prix et taxe de vente)

            if (toBuy.getOwner() != -1) {
                Account C = World.world.getAccount(toBuy.getOwner());
                if (C != null)
                    C.setBankKamas(C.getBankKamas() + toBuy.getPrice());//Ajoute l'argent au vendeur
            }
            SocketManager.GAME_SEND_STATS_PACKET(newOwner);//Met a jour les kamas de l'acheteur

            toBuy.getGameObject().setPosition(Constant.ITEM_POS_NO_EQUIPED);
            newOwner.addObjet(toBuy.getGameObject(), true);//Ajoute l'objet au nouveau propri�taire
            toBuy.getGameObject().getTemplate().newSold(toBuy.getAmount(true), price);//Ajoute la ventes au statistiques
            try {
                String name = "undefined";

                if(World.world.getAccount(toBuy.getOwner()) != null)
                    name = World.world.getAccount(toBuy.getOwner()).getName();

                Logging.getInstance().write("Object", "BuyHdv : "
                        + newOwner.getName() + " : achat de "
                        + toBuy.getGameObject().getTemplate().getName() + "(" + toBuy.getGameObject().getGuid() + ") x"
                        + toBuy.getGameObject().getQuantity() + " venant du compte "
                        + name);
            } catch(Exception e) { e.printStackTrace(); }
            delEntry(toBuy);//Retire l'item de l'HDV ainsi que de la liste du vendeur
            ((HdvObjectData) DatabaseManager.get(HdvObjectData.class)).delete(toBuy);
            if (World.world.getAccount(toBuy.getOwner()) != null
                    && World.world.getAccount(toBuy.getOwner()).getCurrentPlayer() != null)
                SocketManager.GAME_SEND_Im_PACKET(World.world.getAccount(toBuy.getOwner()).getCurrentPlayer(), "065;"
                        + price
                        + "~"
                        + toBuy.getGameObject().getTemplate().getId()
                        + "~" + toBuy.getGameObject().getTemplate().getId() + "~1");
            //Si le vendeur est connecter, envoie du packet qui lui annonce la vente de son objet
            ((PlayerData) DatabaseManager.get(PlayerData.class)).update(newOwner);
        } catch (NullPointerException e) {
            e.printStackTrace();
            toReturn = false;
        }

        return toReturn;
    }

    public String parseToEHl(int templateID) {
        try {
            ObjectTemplate OT = World.world.getObjTemplate(templateID);
            HdvCategory Hdv = this.getCategorys().get(OT.getType());
            HdvTemplate HdvT = Hdv.getTemplate(templateID);
            if (HdvT == null) // Il a pu �tre achet� avant et supprim� de l'HDV. getTemplate devient null.
                return "";
            return HdvT.parseToEHl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
        //return this.getCategorys().getWaitingAccount(World.world.getObjTemplate(templateID).getType()).getTemplate(templateID).parseToEHl();
    }

    public String parseTemplate(int categID) {
        return this.getCategorys().get(categID).parseTemplate();
    }

    public String parseTaxe() {
        return pattern.format(this.getTaxe()).replace(",", ".");
    }
}
