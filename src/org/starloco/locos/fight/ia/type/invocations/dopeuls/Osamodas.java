package org.starloco.locos.fight.ia.type.invocations.dopeuls;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.spells.Spell;

/**
 * Created by Locos on 09/04/2018.
 */
public class Osamodas extends AbstractEasyIA {

    private byte spell = 0;

    public Osamodas(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        switch (this.flag) {
            case 0: // Cri de l'ours
                Fighter friend = fighter.getInvocator();
                Spell.SortStats spell = get().findSpell(fighter, 23);
                int cellId = get().getBestTargetZone(fight, fighter, spell, fighter.getCell().getId(), false);
                int nbTarget = cellId / 1000;
                cellId = cellId - nbTarget * 1000;
                GameCase cell = cellId == 0 || cellId == -1 ? fighter.getInvocator().getCell() : fight.getMap().getCase(cellId);

                if (get().moveToAttack(fight, fighter, friend, get().findSpell(fighter, spell.getSpellID())))
                    this.setNextParams(-1, 5, 1500);
                else if (get().tryCastSpell(fight, fighter, friend, 23) == 0)
                    this.setNextParams(0, 4, 1500);
                break;
            case 1: // crocs du mulou
                friend = fighter.getInvocator();
                spell = get().findSpell(fighter, 22);
                if (get().moveToAttack(fight, fighter, friend, spell))
                    this.setNextParams(0, 4, 1500);
                else if (get().tryCastSpell(fight, fighter, friend, spell.getSpellID()) == 0)
                    this.setNextParams(1, 3, 1500);
                break;
            case 2: // déplacement félin
                friend = fighter.getInvocator();
                spell = get().findSpell(fighter, 29);

                if (get().moveToAttack(fight, fighter, friend, spell))
                    this.setNextParams(1, 3, 1500);
                else if (get().tryCastSpell(fight, fighter, friend, spell.getSpellID()) == 0)
                    this.setNextParams(2, 2, 1500);
                break;

            case 3: // Corbeau
                Fighter target = get().getNearestEnnemy(fight, fighter, true);
                if(get().moveToAttack(fight, fighter, target, get().findSpell(fighter, 24)))
                    this.setNextParams(2, 2, 1500);
                else if(get().tryCastSpell(fight, fighter, target, 24) == 0)
                    this.setNextParams(2, 2, 1500);
                else {
                    get().moveFarIfPossible(fight, fighter);
                    time = 1500;
                }
                break;
        }
    }
}
