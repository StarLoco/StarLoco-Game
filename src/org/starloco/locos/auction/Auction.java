package org.starloco.locos.auction;

import org.starloco.locos.client.BasePlayer;
import org.starloco.locos.object.GameObject;

/**
 * Created by Locos on 31/01/2018.
 */
public class Auction {

    private int price;
    private byte retry;
    private BasePlayer owner, customer;
    private GameObject object;

    public Auction(int price, BasePlayer owner, GameObject object, byte retry) {
        this.price = price;
        this.retry = retry;
        this.owner = owner;
        this.object = object;
    }

    public int getPrice() {
        return price;
    }

    public BasePlayer getOwner() {
        return owner;
    }

    public BasePlayer getCustomer() {
        return customer;
    }

    public void setCustomer(BasePlayer customer) {
        this.customer = customer;
    }

    public GameObject getObject() {
        return object;
    }

    public void setObject(GameObject object) {
        this.object = object;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public byte getRetry() {
        return retry;
    }

    public void incRetry() {
        this.retry++;
    }
}
