package org.starloco.locos.item;

import org.starloco.locos.client.other.Stats;

import java.util.Objects;
import java.util.Optional;

public class StaticGearItem implements Item {
    public final int templateID;
    private final Stats stats;

    public StaticGearItem(int templateID, Stats stats) {
        Objects.requireNonNull(stats);

        this.templateID = templateID;
        this.stats = stats;
    }
    
    @Override
    public int templateID() {
        return templateID;
    }

    @Override
    public Optional<Stats> stats() {
        return Optional.of(stats);
    }
}
