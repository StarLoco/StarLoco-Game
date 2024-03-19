package org.starloco.locos.database.data;

import java.util.HashMap;
import java.util.Map;

public class InventoryData {
    public final String id;
    public final Map<String, ItemStack> stacks = new HashMap<>(); // K: ItemHash
    public final Map<Long, Long> currencies = new HashMap<>(); // For Kamas, and items with no stats

    public InventoryData(String id) {
        this.id = id;
    }
}
