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
public class Sacrieur extends AbstractEasyIA {

    public Sacrieur(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        switch (this.flag) {
            case 0: // Chatiment agile
                Spell.SortStats spell = get().findSpell(fighter,437);
                if(spell != null) {
                    if(get().tryCastSpell(fight, fighter, fighter, spell.getSpellID()) == 0)
                        this.time = 1500;
                }
                break;
            case 1: // Attirance
                spell = get().findSpell(fighter, 434);
                Fighter enemy = get().getNearestEnnemy(fight, fighter, true);

                if(enemy != null) {
                    if (get().tryCastSpell(this.fight, this.fighter, enemy, spell.getSpell().getId()) == 0) {
                        this.setNextParams(1, 3, 1500);
                    } else {
                        GameCase cell1 = fighter.getCell(), cell2 = enemy.getCell();
                        if(cell1 != null && cell2 != null) {
                            char dir = PathFinding.getDirBetweenTwoCase(cell1.getId(), cell2.getId(), fight.getMap(), true);
                            if (!PathFinding.casesAreInSameLine(fight.getMap(), cell1.getId(), cell2.getId(), dir, spell.getMaxPO())) {
                                if (Function.getInstance().moveenfaceIfPossible(fight, fighter, enemy, spell.getMaxPO()) > 0) {
                                    setNextParams(0, 4, 1500);
                                }
                            }
                        }
                    }
                }
                break;
            case 2: // Pied du sacrieur
                Fighter target = get().getNearestEnnemy(fight, fighter, true);
                spell = get().findSpell(fighter,432);
                if(spell != null && target != null) {
                    if(get().tryCastSpell(fight, fighter, target, spell.getSpellID()) == 0)
                        this.setNextParams(1, 3, 1500);
                }
                break;
            case 3: // Absorption
                target = get().getNearestEnnemy(fight, fighter, true);
                spell = get().findSpell(fighter,442);
                if(spell != null && target != null) {
                    if(get().moveToAttack(fight, fighter, target, spell))
                        setNextParams(2, 2, 1500);
                    else if(get().tryCastSpell(fight, fighter, target, spell.getSpellID()) == 0)
                        setNextParams(2, 2, 1500);
                    else {
                        get().moveFarIfPossible(fight, fighter);
                        this.time = 1500;
                    }
                }
                break;
        }
    }
}
