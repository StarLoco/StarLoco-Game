package org.starloco.locos.fight.ia.type.invocations.dopeuls;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.spells.Spell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Locos on 09/04/2018.
 */
public class Enutrof extends AbstractEasyIA {

    private List<Fighter> fighters = new ArrayList<>();

    public Enutrof(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        time = 200;
        switch (this.flag) {
            case 0: // Accélération
                Fighter friend = fighter.getInvocator();
                if (get().moveToAttack(fight, fighter, friend, get().findSpell(fighter, 55)))
                    this.setNextParams(-1, 5, 1500);
                else if (get().tryCastSpell(fight, fighter, friend, 55) == 0)
                    this.time = 2000;
                break;
            case 1: // Cupidité
                if (get().tryCastSpell(fight, fighter, fighter, 52) == 0)
                    this.time = 2000;
                break;
            case 2: // Maladresse de masse
                if(fight.tryCastSpell(fighter, get().findSpell(fighter,54), fighter.getCell().getId()) == 0)
                    time = 2000;
                break;
            case 3: // Lancer de pièce
            case 4:
                Spell.SortStats spell = get().findSpell(fighter, 51);
                Fighter nearest = get().getNearestEnnemy(fight, fighter, true);
                Fighter target = get().getEnnemyWithDistance(fight, fighter, 0, 666, fighters);
                if(target == null) {
                    this.fighters.clear();
                    target = get().getEnnemyWithDistance(fight, fighter, 0, 666, fighters);
                }
                if(nearest != null && fight.canLaunchSpell(fighter, spell, nearest.getCell()) && get().tryCastSpell(fight, fighter, nearest, 51) == 0) {
                    this.setNextParams(2, 2, 1500);
                    this.fighters.add(nearest);
                } else {
                    if (get().moveToAttack(fight, fighter, target, get().findSpell(fighter, 51)))
                        this.setNextParams(2, 2, 1500);
                    else if (get().tryCastSpell(fight, fighter, target, 51) == 0) {
                        this.setNextParams(2, 2, 1500);
                        fighters.add(target);
                    } else {
                        get().moveFarIfPossible(fight, fighter);
                        time = 1500;
                    }
                }
                break;
        }
    }

    private boolean hasEnnemiesArround() {
        Spell.SortStats spell = get().findSpell(fighter, 54);
        if(spell != null) {
            for(Fighter t : fight.getFighters(3)) {
                if(t.getTeam() != fighter.getTeam()) {
                    int dist = PathFinding.getDirBetweenTwoCase(t.getCell().getId(), fighter.getCell().getId(), fight.getMap(), true);
                    if(dist <= 8) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
