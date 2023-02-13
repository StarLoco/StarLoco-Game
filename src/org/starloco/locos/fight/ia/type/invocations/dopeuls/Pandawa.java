package org.starloco.locos.fight.ia.type.invocations.dopeuls;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.spells.Spell;

/**
 * Created by Locos on 09/04/2018.
 */
public class Pandawa extends AbstractEasyIA {

    public Pandawa(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        switch (this.flag) {
            case 0: // Buff pandanlku
                Fighter friend = fighter.getInvocator();
                Spell.SortStats spell = get().findSpell(fighter,1677);

                if(spell != null && friend != null) {
                    if(get().moveToAttack(fight, fighter, friend, spell)) {
                        setNextParams(-1, 6, 1000);
                        break;
                    }
                    if(get().tryCastSpell(fight, fighter, friend, spell.getSpellID()) == 0) {
                        this.time = 1500;
                    }
                }
                break;
            case 1:
                Fighter target = get().getNearestEnnemy(fight, fighter, true);
                spell = get().findSpell(fighter,1678);
                if(get().moveToAttack(fight, fighter, target, spell)) {
                    this.time = 1000;
                }
                break;
            case 2:// Picole
                if(get().tryCastSpell(fight, fighter, fighter, 1676) == 0) {
                    this.time = 2000;
                }
                break;
            case 3: // Souillure
                target = get().getNearestEnnemy(fight, fighter, true);
                if(get().tryCastSpell(fight, fighter, target, 1678) == 0) {
                    this.time = 2000;
                }
                break;
            case 4: // Poing enflammé
                target = get().getNearestEnnemy(fight, fighter, true);
                if(get().tryCastSpell(fight, fighter, target, 687) == 0) {
                    this.setNextParams(3, 2, 1500);
                } else {
                    get().moveFarIfPossible(fight, fighter);
                }
                break;
        }
    }
}
