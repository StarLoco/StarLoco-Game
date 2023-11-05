package org.starloco.locos.fight;

import org.starloco.locos.client.other.Stats;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.fight.spells.Spell;

import java.util.Optional;
import java.util.stream.Stream;

public class CloneFighter extends Fighter {
    protected final PlayerFighter summoner;
    protected CloneFighter(int id, Fight f, PlayerFighter summoner) {
        super(id, f);
        this.summoner = summoner;
    }

    @Override
    public int getType() {
        return 10;
    }

    @Override
    public int getLvl() {
        return summoner.getLvl();
    }

    @Override
    public int baseMaxPdv() {
        return summoner.baseMaxPdv();
    }

    @Override
    protected Stats getBaseStats() {
        return summoner.getBaseStats();
    }

    @Override
    public int getDefaultGfx() {
        return summoner.getDefaultGfx();
    }

    @Override
    public Optional<Spell.SortStats> spellRankForID(int id) {
        return Optional.empty();
    }

    @Override
    public String getPacketsName() {
        return summoner.getPacketsName();
    }

    @Override
    Stream<String> getGMPacketParts() {
        return summoner.getGMPacketParts();
    }

    @Override
    Optional<Mount> getMount() {
        return summoner.getMount();
    }

    @Override
    protected String getMountColors() {
        return summoner.getMountColors();
    }

    @Override
    public boolean isInvocation() {
        return true;
    }
    @Override
    public Fighter getInvocator() {
        return summoner;
    }
}
