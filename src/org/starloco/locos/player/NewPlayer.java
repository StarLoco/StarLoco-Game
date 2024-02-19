package org.starloco.locos.player;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.world.World;

import java.util.Optional;

public class NewPlayer {
    protected final SpellList spells = new SpellList.PlayerSpellList(this);
}