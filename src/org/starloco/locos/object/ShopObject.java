package org.starloco.locos.object;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Locos on 29/08/2018.
 */
public class ShopObject {

    public static final List<ShopObject> objects = new ArrayList<>();

    private final short id;
    private final ObjectTemplate template;
    private final boolean jp;
    private final short price;

    public ShopObject(short id, ObjectTemplate template, boolean jp, short price) {
        this.id = id;
        this.template = template;
        this.jp = jp;
        this.price = price;
    }

    public ObjectTemplate getTemplate() {
        return template;
    }

    public short getPrice() {
        return price;
    }

    public short getId() {
        return id;
    }

    public boolean isJp() {
        return jp;
    }
}
