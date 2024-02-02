package org.starloco.locos.auction;

import org.starloco.locos.player.Player;
import org.starloco.locos.item.Item;

/**
 * Created by Locos on 31/01/2018.
 */
public class Auction {

    private int price;
    private byte retry;
    private Player owner, customer;
    private Item object;

    public Auction(int price, Player owner, Item object, byte retry) {
        this.price = price;
        this.retry = retry;
        this.owner = owner;
        this.object = object;
    }

    public int getPrice() {
        return price;
    }

    public Player getOwner() {
        return owner;
    }

    public Player getCustomer() {
        return customer;
    }

    public void setCustomer(Player customer) {
        this.customer = customer;
    }

    public Item getObject() {
        return object;
    }

    public void setObject(Item object) {
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
