package org.starloco.locos.database.data;

public record ItemStack(Item item, long quantity) {
    public ItemStack {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
    }

    public String itemHash() {
        return this.item.hash;
    }
}
