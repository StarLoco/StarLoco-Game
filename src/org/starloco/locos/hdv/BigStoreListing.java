package org.starloco.locos.hdv;

import org.starloco.locos.game.world.World;
import org.starloco.locos.object.GameObject;

public class BigStoreListing {
    private int id;
    private int hdvId;
    private int lineId;
    private final int owner;
    private final int price;
    private final BigStoreListingLotSize lotSize;
    private final GameObject gameObject;

    public BigStoreListing(int price, byte amount, int owner, GameObject gameObject) {
        this(-1, price, amount, owner, gameObject);
    }

    public BigStoreListing(int id, int price, byte amount, int owner, GameObject gameObject) {
        this.id = id;
        this.price = price;
        this.lotSize = BigStoreListingLotSize.fromValue(amount);
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

    void setLineId(int lineId) {
        this.lineId = lineId;
    }
    public int getLineId() {
        return lineId;
    }

    public int getOwner() {
        return this.owner;
    }

    public int getPrice() {
        return this.price;
    }

    public BigStoreListingLotSize getLotSize() {
        return this.lotSize;
    }

    public GameObject getGameObject() {
        return this.gameObject;
    }

    public String parseToEL() {
        // For EL packet, we want to be able to identify each listing, so we return the listing ID
        StringBuilder toReturn = new StringBuilder();
        int duration = World.world.getHdv(hdvId).getDuration();
        toReturn.append(this.getId()).append(";").append(lotSize.amount).append(";").append(this.getGameObject().getTemplate().getId()).append(";").append(this.getGameObject().encodeStats()).append(";").append(this.price).append(";").append(duration);
        return toReturn.toString();
    }

    public String parseToEmK() {
        StringBuilder toReturn = new StringBuilder();
        int duration = World.world.getHdv(hdvId).getDuration();
        toReturn.append(this.getGameObject().getGuid()).append("|").append(lotSize.amount).append("|").append(this.getGameObject().getTemplate().getId()).append("|").append(this.getGameObject().encodeStats()).append("|").append(this.price).append("|").append(duration);
        return toReturn.toString();
    }
}