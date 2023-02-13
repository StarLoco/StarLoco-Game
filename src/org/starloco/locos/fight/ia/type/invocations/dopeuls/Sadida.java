package org.starloco.locos.fight.ia.type.invocations.dopeuls;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.spells.Spell;

/**
 * Created by Locos on 09/04/2018.
 */
public class Sadida extends AbstractEasyIA {

    public Sadida(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        switch (this.flag) {
            case 0: // Connaissance des poupées
                Spell.SortStats spell = get().findSpell(fighter,199);
                if(spell != null) {
                    if(get().tryCastSpell(fight, fighter, fighter, spell.getSpellID()) == 0)
                        this.time = 1500;
                }
                break;
            case 1: // Déplacement invoation bloqueuse + folle  193 & 182
                spell = get().findSpell(fighter, 193);
                Spell.SortStats spell1 = get().findSpell(fighter, 182);

                if(fight.canLaunchSpell(fighter, spell, null) || fight.canLaunchSpell(fighter, spell1, null)) {
                    Fighter target = get().getNearestEnnemy(fight, fighter, true);
                    this.time = (short) (get().moveNearIfPossible(fight, fighter, target) ? 1500 : 0);
                }
                break;
            case 2: // Invocation bloqueuse + folle  193 & 182
                if (get().invocIfPossible(fight, fighter))
                    setNextParams(1, 3, 1500);
                break;
            case 3: // Ronce
                Fighter target = get().getNearestEnnemy(fight, fighter, true);
                spell = get().findSpell(fighter,183);
                if(spell != null && target != null) {
                    if(get().moveToAttack(fight, fighter, target, spell))
                        setNextParams(1, 2, 1500);
                    else if(get().tryCastSpell(fight, fighter, target, spell.getSpellID()) == 0)
                        setNextParams(1, 2, 1500);
                    else {
                        get().moveFarIfPossible(fight, fighter);
                        time = 1500;
                    }
                }
                break;
        }
    }
}
