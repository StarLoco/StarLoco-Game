package org.starloco.locos.fight.ia.type.invocations.dopeuls;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

/**
 * Created by Locos on 09/04/2018.
 */
public class Iop extends AbstractEasyIA {

    public Iop(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        switch (this.flag) {
            case 0: // Guide de bravoure
                if (get().tryCastSpell(fight, fighter, fighter, 147) == 0)
                    this.time = 1500;
                break;
            case 1: // Puissance
                Spell.SortStats spell = get().findSpell(fighter, 153);
                Fighter friend = fighter.getInvocator();
                if(fight.canLaunchSpell(fighter, spell, friend.getCell())) {
                    if (get().moveToAttack(fight, fighter, friend, spell))
                        setNextParams(0, 4, 1500);
                    else if (get().tryCastSpell(fight, fighter, friend, spell.getSpellID()) == 0)
                        this.time = 1500;
                }
                break;
            case 2: // Amplification
                if (get().tryCastSpell(fight, fighter, fighter, 148) == 0)
                    this.time = 1500;
                break;
            case 3: // Couper
                spell = get().findSpell(fighter, 150);
                Fighter enemy = get().getNearestEnnemy(fight, fighter, true);

                if (spell != null && spell.getPACost() <= this.fighter.getCurPa(this.fight) && enemy != null) {
                    if (Function.getInstance().tryCastSpell(this.fight, this.fighter, enemy, spell.getSpell().getId()) == 0) {
                        setNextParams(2, 2, 1500);
                        break;
                    } else {
                        GameCase cell1 = fighter.getCell(), cell2 = enemy.getCell();
                        char dir = PathFinding.getDirBetweenTwoCase(cell1.getId(), cell2.getId(), fight.getMap(), true);
                        if (!PathFinding.casesAreInSameLine(fight.getMap(), cell1.getId(), cell2.getId(), dir, spell.getMaxPO())) {
                            if(Function.getInstance().moveToAttack(fight, fighter, enemy, spell)) {
                                setNextParams(2, 2, 1750);
                                break;
                            }
                        } else {
                            if (Function.getInstance().moveenfaceIfPossible(fight, fighter, enemy, spell.getMaxPO()) > 0) {
                                setNextParams(2, 2, 2000);
                                break;
                            }
                        }
                    }
                }
                time = (short) get().moveFarIfPossible(fight, fighter);
                break;
        }
    }
}
