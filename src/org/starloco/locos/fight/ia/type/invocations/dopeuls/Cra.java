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
public class Cra extends AbstractEasyIA {

    public Cra(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        switch (this.flag) {
            case 0: // Tir éloigné
                Spell.SortStats spell = get().findSpell(fighter, 172);
                if(fight.canLaunchSpell(fighter, spell, fighter.getCell())) {
                    if (get().moveNearIfPossible(fight, fighter, fighter.getInvocator()))
                        setNextParams(-1, 5, 1500);
                    else if (get().tryCastSpell(fight, fighter, fighter, spell.getSpellID()) == 0)
                        this.time = 1500;
                }
                break;
            case 1: // Tir puissant
                spell = get().findSpell(fighter, 166);
                Fighter friend = fighter.getInvocator();
                if(fight.canLaunchSpell(fighter, spell, friend.getCell())) {
                    if (get().moveToAttack(fight, fighter, friend, spell))
                        setNextParams(0, 4, 1500);
                    else if (get().tryCastSpell(fight, fighter, friend, spell.getSpellID()) == 0)
                        this.time = 1500;
                }
                break;
            case 2: // Flèche ralentissante
                spell = get().findSpell(fighter, 177);
                Fighter enemy = get().getNearestEnnemy(fight, fighter, true);
                if (spell != null && spell.getPACost() <= this.fighter.getCurPa(this.fight) && enemy != null) {
                    if (Function.getInstance().tryCastSpell(this.fight, this.fighter, enemy, spell.getSpell().getId()) == 0) {
                        this.setNextParams(1, 3, 1500);
                    } else {
                        GameCase cell1 = fighter.getCell(), cell2 = enemy.getCell();
                        char dir = PathFinding.getDirBetweenTwoCase(cell1.getId(), cell2.getId(), fight.getMap(), true);
                        if (!PathFinding.casesAreInSameLine(fight.getMap(), cell1.getId(), cell2.getId(), dir, spell.getMaxPO())) {
                            if (Function.getInstance().moveToAttack(fight, fighter, enemy, spell)) {
                                this.setNextParams(1, 3, 1500);
                            }
                        } else {
                            if (Function.getInstance().moveToAttack(fight, fighter, enemy, spell)) {
                                this.setNextParams(1, 3, 1500);
                            }//time = Function.getInstance().moveautourIfPossible(fight, fighter, enemy) /*? 2000 : 0*/;
                        }
                    }
                }
                break;
            case 3: // Flèche harcelante
                Fighter target = get().getNearestEnnemy(fight, fighter, true);
                if(get().moveToAttack(fight, fighter, target, get().findSpell(fighter, 173)))
                    setNextParams(2, 2, 1500);
                else if (get().tryCastSpell(fight, fighter, target, 173) == 0)
                    setNextParams(2, 2, 1500);
                else {
                    get().moveFarIfPossible(fight, fighter);
                    time = 1500;
                }
                break;
        }
    }
}
