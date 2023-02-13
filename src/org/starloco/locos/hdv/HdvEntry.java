package org.starloco.locos.hdv;

import org.starloco.locos.object.GameObject;

public class HdvEntry implements Comparable<HdvEntry> {

    public boolean buy = false;
    private int id;
    private int hdvId;
    private int lineId;
    private int owner;
    private int price;
    private byte amount;            //Dans le format : 1=1 2=10 3=100
    private GameObject gameObject;

    public HdvEntry(int id, int price, byte amount, int owner, GameObject gameObject) {
        this.setId(id);
        this.price = price;
        this.amount = amount;
        this.gameObject = gameObject;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHdvId() {
        return this.hdvId;
    }

    public void setHdvId(int id) {
        this.hdvId = id;
    }

    public int getLineId() {
        return this.lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getOwner() {
        return this.owner;
    }

    public int getPrice() {
        return this.price;
    }

    public byte getAmount(boolean ok) {
        if (ok)
            return (byte) (Math.pow(10, (double) amount) / 10);
        else
            return this.amount;
    }

    public GameObject getGameObject() {
        return this.gameObject;
    }

    public String parseToEL() {
        StringBuilder toReturn = new StringBuilder();
        int count = getAmount(true);//Transf�re dans le format (1,10,100) le montant qui etait dans le format (1,2,3)
        toReturn.append(this.getLineId()).append(";").append(count).append(";").append(this.getGameObject().getTemplate().getId()).append(";").append(this.getGameObject().parseStatsString()).append(";").append(this.price).append(";350");//350 = temps restant
        return toReturn.toString();
    }

    public String parseToEmK() {
        StringBuilder toReturn = new StringBuilder();
        int count = getAmount(true);//Transf�re dans le format (1,10,100) le montant qui etait dans le format (1,2,3)
        toReturn.append(this.getGameObject().getGuid()).append("|").append(count).append("|").append(this.getGameObject().getTemplate().getId()).append("|").append(this.getGameObject().parseStatsString()).append("|").append(this.price).append("|350");//350 = temps restant
        return toReturn.toString();
    }

    public int compareTo(HdvEntry o) {
        HdvEntry e = o;
        int celuiCi = this.getPrice();
        int autre = e.getPrice();
        if (autre > celuiCi)
            return -1;
        if (autre == celuiCi)
            return 0;
        if (autre < celuiCi)
            return 1;
        return 0;
    }
}