package org.starloco.locos.hdv;

public enum BigStoreListingLotSize {
    SingleItem(0),
    TenItems(1),
    HundredItems(2);

    public final int value;
    public final int amount;

    BigStoreListingLotSize(int v) {
        this.value = v;
        this.amount = (int)Math.pow(10, v);
    }

    public static BigStoreListingLotSize fromValue(int b) {
        for(BigStoreListingLotSize s : BigStoreListingLotSize.values()) {
            if(s.value == b) return s;
        }
        return null;
    }
}
