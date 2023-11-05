package org.starloco.locos.fight;

import org.starloco.locos.client.other.Stats;
import org.starloco.locos.entity.Collector;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.object.GameObject;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.Collection;

public class CollectorFighter extends Fighter {
    private final Collector collector;

    protected CollectorFighter(int id, Fight f, Collector collector) {
        super(id, f);
        this.collector = collector;
    }

    @Override
    public String getPacketsName() {
        return collector.getFullName();
    }

    @Override
    public int getType() {
        return 5;
    }

    @Override
    public int getLvl() {
        return collector.getGuild().getLvl();
    }

    @Override
    public int baseMaxPdv() {
        return collector.getGuild().getLvl() * 100;
    }

    @Override
    protected Stats getBaseStats() {
        return null;
    }

    @Override
    public int getDefaultGfx() {
        return 6000;
    }

    @Override
    public Optional<Spell.SortStats> spellRankForID(int id) {
        return Optional.ofNullable(collector.getGuild().getSpells().get(id));
    }

    @Override
    Stream<String> getGMPacketParts() {
        int lvl = getLvl();
        int resistance = Math.min(50, (int) Math.floor((double) lvl / 2));

        return Stream.of(
            "-6",
            "6000^100",
            String.valueOf(lvl),
            "1",
            "2",
            "4",
            String.valueOf(resistance),
            String.valueOf(resistance),
            String.valueOf(resistance),
            String.valueOf(resistance),
            String.valueOf(resistance),
            String.valueOf(resistance),
            String.valueOf(resistance)
        );
    }

    Collection<GameObject> collectorDrops() { return collector.getDrops(); }

    @Override
    public Collector getCollector() {
        return collector;
    }
}
