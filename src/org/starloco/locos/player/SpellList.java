package org.starloco.locos.player;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.world.World;

import java.util.Optional;

public class SpellList {
    protected final Int2IntMap spellLevels;

    SpellList(Int2IntMap spellLevels) {
        this.spellLevels = spellLevels;
    }

    public Optional<Spell.SortStats> getSpellInfo(int spellID) {
        return Optional.ofNullable(spellLevels.getOrDefault(spellID, null))
            .flatMap(spellLvl -> World.world.getSpell(spellID)
                    .map(s -> s.getStatsByLevel(spellLvl))
            );
    }

    public static class PlayerSpellList extends SpellList {
        protected final NewPlayer player;

        PlayerSpellList(NewPlayer player) {
            super(new Int2IntArrayMap());
            this.player = player;
        }
    }
}
