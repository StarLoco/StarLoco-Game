package org.starloco.locos.fight.ia.util.newia.action;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

/**
 * Created by Locos on 28/04/2018.
 */
public class MoveAction implements IAAction {

    private final Fighter caster;
    private final Spell.SortStats spell;
    private final GameCase cell;
    private byte pm = 0;

    public MoveAction(Fighter caster, Spell.SortStats spell, GameCase cell) {
        this.caster = caster;
        this.spell = spell;
        this.cell = cell;
    }

    @Override
    public byte getType() {
        return MOVE;
    }

    @Override
    public short getWaitingTime() {
        pm -= caster.getCurPm(caster.getFight());
        return (short) (pm <= 3 ? pm * 250 : pm * 200);
    }

    @Override
    public boolean execute() {
        pm = (byte) caster.getCurPm(caster.getFight());
        return Function.getInstance().moveToAttack(caster.getFight(), caster, cell, spell, true);
    }
}
