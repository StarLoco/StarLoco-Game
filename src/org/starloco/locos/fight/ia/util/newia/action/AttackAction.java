package org.starloco.locos.fight.ia.util.newia.action;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.spells.Spell;

/**
 * Created by Locos on 28/04/2018.
 */
public class AttackAction implements IAAction {

    private final Fighter caster;
    private final GameCase cell;
    private final Spell.SortStats spell;

    public AttackAction(Fighter caster, GameCase cell, Spell.SortStats spell) {
        this.caster = caster;
        this.cell = cell;
        this.spell = spell;
    }

    public GameCase getCell() {
        return cell;
    }

    public Spell.SortStats getSpell() {
        return spell;
    }

    @Override
    public byte getType() {
        return ATTACK;
    }

    @Override
    public short getWaitingTime() {
        return spell.getSpell().getDuration();
    }

    @Override
    public boolean execute() {
        return caster.getFight().tryCastSpell(caster, spell, cell.getId()) == 0;
    }
}
