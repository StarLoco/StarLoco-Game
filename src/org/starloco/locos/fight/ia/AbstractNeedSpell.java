package org.starloco.locos.fight.ia;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.fight.spells.SpellEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Locos on 04/10/2015.
 */
public abstract class AbstractNeedSpell extends AbstractIA {

    protected List<SortStats> buffs, glyphs, invocations, cacs, highests;

    public AbstractNeedSpell(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);

        this.buffs = AbstractNeedSpell.getListSpellOf(fighter, "BUFF");
        this.glyphs = AbstractNeedSpell.getListSpellOf(fighter, "GLYPH");
        this.invocations = AbstractNeedSpell.getListSpellOf(fighter, "INVOCATION");
        this.cacs = AbstractNeedSpell.getListSpellOf(fighter, "CAC");
        this.highests = AbstractNeedSpell.getListSpellOf(fighter, "HIGHEST");
    }

    private static List<SortStats> getListSpellOf(Fighter fighter, String type) {
        final List<SortStats> spells = new ArrayList<>();

        for(SortStats spell : fighter.getMob().getSpells().values()) {
            if(spells.contains(spell)) continue;
            switch(type) {
                case "BUFF":
                    if(spell.getSpell().getType() == 1) spells.add(spell);
                    break;
                case "GLYPH":
                    if(spell.getSpell().getType() == 4) spells.add(spell);
                    break;
                case "INVOCATION":
                    spells.addAll(spell.getEffects().stream().filter(spellEffect -> spellEffect.getEffectID() == 181).map(spellEffect -> spell).collect(Collectors.toList()));
                    break;
                case "CAC":
                    if(spell.getSpell().getType() == 0) {
                        boolean effect = false;
                        for(SpellEffect spellEffect : spell.getEffects())
                            if(spellEffect.getEffectID() == 4 || spellEffect.getEffectID() == 6)
                                effect = true;
                        if(!effect && spell.getMaxPO() < 3) spells.add(spell);
                    }
                    break;
                case "HIGHEST":
                    if(spell.getSpell().getType() == 0) {
                        boolean effect = false;
                        for(SpellEffect spellEffect : spell.getEffects())
                            if(spellEffect.getEffectID() == 4 || spellEffect.getEffectID() == 6)
                                effect = true;
                        if(effect && spell.getSpellID() != 805) continue;
                        if(spell.getMaxPO() > 1) spells.add(spell);
                    }
                    break;
            }
        }
        return spells;
    }
}
