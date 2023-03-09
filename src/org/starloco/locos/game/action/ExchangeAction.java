package org.starloco.locos.game.action;

/**
 * Created by Locos on 25/10/2015.
 */
public class ExchangeAction<T> {

    public static final byte
            TALKING_WITH = 0,
            TRADING_WITH_ME = 1,
            TRADING_WITH_PLAYER = 2,
            TRADING_WITH_OFFLINE_PLAYER = 3,
            TRADING_WITH_NPC = 4,
            TRADING_WITH_NPC_EXCHANGE = 5,
            TRADING_WITH_NPC_PETS = 6,
            TRADING_WITH_COLLECTOR = 7,
            TRADING_WITH_NPC_PETS_RESURRECTION = 8,
            CRAFTING = 9,
            BREAKING_OBJECTS = 10,
            CRAFTING_SECURE_WITH = 11,
            AUCTION_HOUSE_BUYING = 12,
            AUCTION_HOUSE_SELLING = 13,
            IN_MOUNT = 14,
            IN_MOUNTPARK = 15,
            IN_TRUNK = 16,
            IN_BANK = 17,
            IN_ZAAPING = 18,
            IN_ZAPPI = 19,
            IN_PRISM = 20,
            IN_TUTORIAL = 21,
            FORGETTING_SPELL = 22,
            CRAFTING_BOOK = 23,
            LOCK_TRUNK = 24,
            LOCK_HOUSE = 25,
            CHOOSING_TONIC = 26;
    private byte type;
    private T value;

    public ExchangeAction(byte type, T value) {
        this.type = type;
        this.value = value;
    }

    public byte getType() {
        return type;
    }

    public T getValue() {
        return value;
    }
}
