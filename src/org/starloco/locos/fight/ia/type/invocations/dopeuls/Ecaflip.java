package org.starloco.locos.fight.ia.type.invocations.dopeuls;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.spells.Spell;

/**
 * Created by Locos on 09/04/2018.
 */
public class Ecaflip extends AbstractEasyIA {

    public Ecaflip(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        switch (this.flag) {
            case 0: // Réflexes
                Spell.SortStats spell = get().findSpell(fighter, 118);
                Fighter friend = fighter.getInvocator();
                if(fight.canLaunchSpell(fighter, spell, friend.getCell())) {
                    if (get().moveToAttack(fight, fighter, friend, spell))
                        setNextParams(-1, 5, 1500);
                    else if (get().tryCastSpell(fight, fighter, friend, spell.getSpellID()) == 0)
                        this.time = 1500;
                }
                break;
            case 1: // Roue de la fortune
                spell = get().findSpell(fighter, 118);
                friend = fighter.getInvocator();
                if(fight.canLaunchSpell(fighter, spell, friend.getCell())) {
                    if (get().moveToAttack(fight, fighter, friend, spell))
                        setNextParams(0, 4, 1500);
                    else if (get().tryCastSpell(fight, fighter, friend, spell.getSpellID()) == 0)
                        this.time = 1500;
                }
                break;
            case 2: // Roulette
                if (get().tryCastSpell(fight, fighter, fighter, 101) == 0)
                    this.time = 1500;
                break;
            case 3: // Bluff
                Fighter target = get().getNearestEnnemy(fight, fighter, true);
                if(get().moveToAttack(fight, fighter, target, get().findSpell(fighter, 109)))
                    setNextParams(2, 2, 1500);
                else if (get().tryCastSpell(fight, fighter, target, 109) == 0)
                    setNextParams(2, 2, 1500);
                else {
                    get().moveFarIfPossible(fight, fighter);
                    time = 1500;
                }
                break;
        }
    }
}
