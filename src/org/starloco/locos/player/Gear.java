package org.starloco.locos.player;


import org.starloco.locos.item.Item;

import java.util.EnumMap;

public class Gear<I extends Item> {
    // Prevent gear from having both a mount and a pet equipped
    private static final boolean NO_PET_STACKING = true;
    private final EnumMap<GearSlot, I> slots = new EnumMap<>(GearSlot.class);



}
