package org.starloco.locos.player;


import org.starloco.locos.client.other.Stats;
import org.starloco.locos.item.Item;
import org.starloco.locos.item.StaticGearItem;

import java.util.EnumMap;
import java.util.Optional;

public class Gear<I extends Item> {
    // Prevent gear from having both a mount and a pet equipped
    private static final boolean NO_PET_STACKING = true;
    private final EnumMap<GearSlot, I> slots;

    public Gear() {
        this(new EnumMap<>(GearSlot.class));
    }

    public Gear(EnumMap<GearSlot, I> slots) {
        if(NO_PET_STACKING && slots.get(GearSlot.MountSlot) != null && slots.get(GearSlot.PetSlot) != null) {
            throw new RuntimeException("cannot create gear that has both mount and pet");
        }
        this.slots = slots;
    }

    public Optional<Stats> stats() {
        return slots.values().stream()
            .map(Item::stats)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .reduce(Stats::cumulStat);
    }
}
