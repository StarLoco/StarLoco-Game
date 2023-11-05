package org.starloco.locos.fight;

import org.starloco.locos.client.other.Stats;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.kernel.Constant;

import java.util.Optional;
import java.util.stream.Stream;

public class PrismFighter extends Fighter {
    protected final Prism prism;

    protected PrismFighter(int id, Fight fight, Prism prism) {
        super(id, fight);
        this.prism = prism;
    }

    @Override
    public String getPacketsName() {
        return String.valueOf(prism.getAlignment()==1?1111:1112);
    }

    @Override
    Stream<String> getGMPacketParts() {
        return Stream.of(
            "-2",
            prism.gfx + "^100",
            String.valueOf(prism.getLevel()),
            "-1;-1;-1",
            "0,0,0,0",
            String.valueOf(this.getPdvMax()),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_PA)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_PM)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_RP_NEU)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_RP_TER)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_RP_FEU)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_RP_EAU)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_RP_AIR)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_ADODGE)),
            String.valueOf(getTotalStats().getEffect(Constant.STATS_ADD_MDODGE))
        );
    }

    @Override
    public int getType() {
        return 7;
    }

    @Override
    public int getLvl() {
        return prism.getLevel();
    }

    @Override
    public int baseMaxPdv() {
        return prism.getLevel() * 10000;
    }

    @Override
    protected Stats getBaseStats() {
        return prism.getStats();
    }

    @Override
    public int getDefaultGfx() {
        return prism.gfx;
    }

    @Override
    public Optional<Spell.SortStats> spellRankForID(int id) {
        return Optional.empty();
    }

    @Override
    public Prism getPrism() {
        return prism;
    }
}
