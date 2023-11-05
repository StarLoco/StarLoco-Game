package org.starloco.locos.fight;

import org.starloco.locos.client.other.Stats;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.entity.monster.MonsterGrade;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;

import java.util.Optional;
import java.util.stream.Stream;

public class MobFighter extends Fighter {
    final static int ENUTROF_CHEST_ID = 285;

    protected final MonsterGrade mobGrade;

    protected MobFighter(int id, Fight f, MonsterGrade mobGrade) {
        super(id, f);
        this.mobGrade = mobGrade;
    }

    @Override
    public String getPacketsName() {
        return String.valueOf(mobGrade.getTemplate().getId());
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public int getLvl() {
        return mobGrade.getLevel();
    }

    @Override
    public int baseMaxPdv() {
        return getBaseStats().get(Constant.STATS_ADD_VITA);
    }

    @Override
    protected Stats getBaseStats() {
        return mobGrade.getStats();
    }

    @Override
    public int getDefaultGfx() {
        return mobGrade.getTemplate().getGfxId();
    }

    @Override
    public Optional<Spell.SortStats> spellRankForID(int id) {
        return Optional.ofNullable(mobGrade.getSpells().get(id));
    }

    public Monster getTemplate() {
        return mobGrade.getTemplate();
    }

    @Override
    protected Stream<String> getGMPacketParts() {
        return Stream.of(
            "-2",
            mobGrade.getTemplate().getGfxId() + "^" + mobGrade.getSize(),
            String.valueOf(mobGrade.getGrade()),
            mobGrade.getTemplate().getColors().replace(",", ";"),
            "0,0,0,0",
            String.valueOf(getPdvMax()),
            String.valueOf(mobGrade.getPa()),
            String.valueOf(mobGrade.getPm())
        );
    }


    @Override
    boolean canLoot() { return isInvocation() && mobGrade.getTemplate().getId() == ENUTROF_CHEST_ID; }

    @Override
    int minKamasReward() { return mobGrade.getTemplate().getMinKamas(); }

    @Override
    int maxKamasReward() { return mobGrade.getTemplate().getMaxKamas(); }

    @Override
    Stream<World.Drop> drops() { return mobGrade.getTemplate().getDrops().stream(); }

    public Object scripted() {
        return mobGrade.scripted();
    }

    @Override
    public MonsterGrade getMob() { return mobGrade; }
}
