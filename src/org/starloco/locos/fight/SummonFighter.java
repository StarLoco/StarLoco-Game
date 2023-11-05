package org.starloco.locos.fight;

import org.starloco.locos.client.other.Stats;
import org.starloco.locos.entity.monster.MonsterGrade;
import org.starloco.locos.kernel.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SummonFighter extends MobFighter {
    protected Fighter summoner;

    protected SummonFighter(int id, Fight f, MonsterGrade mobGrade, Fighter summoner) {
        super(id, f, mobGrade);
        this.summoner = summoner;
    }

    @Override
    public Stats getBaseStats() {
        if(!(summoner instanceof PlayerFighter)) return super.getBaseStats();

        // Summons from player have a bonus
        Map<Integer, Integer> stats = new HashMap<>(super.getBaseStats().getEffects());

//        if (mobID == 264 && caster.getMob() != null)
//            pdvMax = 425;
//        if (mobID == 114 && caster.getMob() != null)
//            pdvMax = 35;
//        if (mobID == 115 && caster.getMob() != null)
//            pdvMax = 90;
//        if (mobID == 262 && caster.getPlayer() != null)
//            pdvMax = 225;
//        if (mobID == 246 && caster.getPlayer() != null)
//            pdvMax = 80;
//        if (mobID == 1108 && caster.getPlayer() != null)
//            pdvMax = 490;

        // https://www.dofus.com/fr/forum/1003-divers/293131-calculer-vie-invoquations
        double summonerBoost = 1 + (summoner.getLvl() / 100D);
        Stream.of(
            Constant.STATS_ADD_SAGE,
            Constant.STATS_ADD_FORC,
            Constant.STATS_ADD_INTE,
            Constant.STATS_ADD_CHAN,
            Constant.STATS_ADD_AGIL,
            Constant.STATS_ADD_VITA)
                .forEach(stat -> stats.put(stat, (int)Math.floor(stats.get(stat) * summonerBoost)));

        return new Stats(stats);
    }
}
