package org.starloco.locos.database.data;

import java.util.ArrayList;
import java.util.List;

public class Item {
    public final long id;
    public final String hash;
    public final List<Effect> effects = new ArrayList<>();

    public Item(long id, String hash) {
        this.id = id;
        this.hash = hash;
    }
}
