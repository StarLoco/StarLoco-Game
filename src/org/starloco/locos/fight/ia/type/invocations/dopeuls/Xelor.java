package org.starloco.locos.fight.ia.type.invocations.dopeuls;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.spells.Spell;

/**
 * Created by Locos on 09/04/2018.
 */
public class Xelor extends AbstractEasyIA {

    public Xelor(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        switch (this.flag) {
            case 0: // Invocation Cadran du xélor
                if(get().invocIfPossible(fight, fighter))
                    time = 1500;
                break;
            case 1: // Protection aveuglante
                Spell.SortStats spell = get().findSpell(fighter, 94);
                Fighter friend = fighter.getInvocator();
                if(fight.canLaunchSpell(fighter, spell, friend.getCell())) {
                    if (get().moveToAttack(fight, fighter, friend, spell))
                        setNextParams(0, 4, 1500);
                    else if (get().tryCastSpell(fight, fighter, fighter, spell.getSpellID()) == 0)
                        this.time = 1500;
                }
                break;
            case 2: // Démotivation
                Fighter target = get().getNearestEnnemy(fight, fighter, true);
                if (get().tryCastSpell(fight, fighter, target, 87) == 0)
                    this.time = 1500;
                break;
            case 3: // Aiguille
                target = get().getNearestEnnemy(fight, fighter, true);
                if(get().moveToAttack(fight, fighter, target, get().findSpell(fighter, 83)))
                    setNextParams(2, 2, 1500);
                else if (get().tryCastSpell(fight, fighter, target, 83) == 0)
                    setNextParams(2, 2, 1500);
                else {
                    get().moveFarIfPossible(fight, fighter);
                    time = 1500;
                }
                break;
        }
    }
}
