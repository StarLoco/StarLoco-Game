package org.starloco.locos.fight.ia.type.invocations.dopeuls;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.spells.Spell;

/**
 * Created by Locos on 09/04/2018.
 */
public class Eniripsa extends AbstractEasyIA {

    public Eniripsa(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        switch (this.flag) {
            case 0: // Mot d'épine
                Spell.SortStats spell = get().findSpell(fighter, 2096);
                Fighter friend = fighter.getInvocator();
                if(fight.canLaunchSpell(fighter, spell, friend.getCell())) {
                    if (get().moveToAttack(fight, fighter, friend, spell))
                        setNextParams(-1, 5, 1500);
                    else if (get().tryCastSpell(fight, fighter, friend, spell.getSpellID()) == 0)
                        this.time = 1500;
                }
                break;
            case 1: // Mot d'envol
                if (get().tryCastSpell(fight, fighter, fighter, 2099) == 0)
                    this.time = 1500;
                break;
            case 2: // Mot drainant
                spell = get().findSpell(fighter, 2090);
                Fighter target = get().getNearestEnnemy(fight, fighter, true);
                if (get().moveToAttack(fight, fighter, target, spell))
                    setNextParams(1, 3, 1500);
                else if (get().tryCastSpell(fight, fighter, target, spell.getSpellID()) == 0)
                    this.time = 1500;
                break;
            case 3: // Mot blessant
                target = get().getNearestEnnemy(fight, fighter, true);
                if(get().moveToAttack(fight, fighter, target, get().findSpell(fighter, 122)))
                    setNextParams(2, 2, 1500);
                else if (get().tryCastSpell(fight, fighter, target, 122) == 0)
                    setNextParams(2, 2, 1500);
                else {
                    get().moveFarIfPossible(fight, fighter);
                    time = 1500;
                }
                break;
        }
    }
}
