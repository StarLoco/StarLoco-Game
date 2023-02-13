package org.starloco.locos.fight.ia.type.invocations.dopeuls;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.spells.Spell;

/**
 * Created by Locos on 09/04/2018.
 */
public class Feca extends AbstractEasyIA {

    public Feca(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        switch (this.flag) {
            case 0: // Buff Renvoie de sort
                Fighter friend = fighter.getInvocator();
                Spell.SortStats spell = get().findSpell(fighter,4);

                if(spell != null && friend != null) {
                    if(get().moveToAttack(fight, fighter, friend, spell))
                        setNextParams(-1, 5, 1500);
                    else if(get().tryCastSpell(fight, fighter, friend, spell.getSpellID()) == 0)
                        this.time = 1500;
                }
                break;
            case 1: // Buff science du bâton
                friend = fighter.getInvocator();
                spell = get().findSpell(fighter,16);
                if(spell != null && friend != null) {
                    if(get().tryCastSpell(fight, fighter, friend, spell.getSpellID()) == 0)
                        this.time = 1500;
                }
                break;
            case 2: // Aveuglement
                Fighter target = get().getNearestEnnemy(fight, fighter, true);
                spell = get().findSpell(fighter,2);
                if(spell != null && target != null) {
                    if(get().moveToAttack(fight, fighter, target, spell))
                        setNextParams(1, 3, 1500);
                    else if(get().tryCastSpell(fight, fighter, target, spell.getSpellID()) == 0)
                        this.time = 1500;
                }
                break;
            case 3: // Attaque naturelle
                target = get().getNearestEnnemy(fight, fighter, true);
                spell = get().findSpell(fighter,3);
                if(spell != null && target != null) {
                    if(get().tryCastSpell(fight, fighter, target, spell.getSpellID()) == 0)
                        setNextParams(2, 2, 1250);
                    else {
                        get().moveFarIfPossible(fight, fighter);
                        this.time = 1500;
                    }
                }
                break;
        }
    }
}
