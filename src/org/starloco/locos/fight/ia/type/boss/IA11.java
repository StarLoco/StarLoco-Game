package org.starloco.locos.fight.ia.type.boss;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

import java.util.List;
import java.util.Random;

public class IA11 extends AbstractIA {

    public IA11(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            this.tryLaunchSpellKraken();
            Fighter nearestEnnemy = Function.getInstance().getEnnemyWithDistance(this.fight, this.fighter, 0, 10, null);

            if(nearestEnnemy == null)
                nearestEnnemy = Function.getInstance().getEnnemyWithDistance(this.fight, this.fighter, 0, 50, null);

            Fighter arround = PathFinding.getEnemyAround(this.fighter.getCell().getId(), this.fight.getMap(), this.fight);
            if(arround == null)
                Function.getInstance().moveNearIfPossible(this.fight, this.fighter, nearestEnnemy);
            this.tryLaunchSpellKraken();
            this.tryLaunchOtherSpell();
            //Function.getInstance().attackIfPossible(this.fight, this.fighter, null);
            this.addNext(this::decrementCount, 1000);
        } else {
            this.stop = true;
        }
    }

    private void tryLaunchOtherSpell() {
        int spells[] = new int[] {261, 1100, 1101, 1102};
        Spell.SortStats spell = null;
        for(int id : spells) {
            spell = Function.getInstance().findSpell(this.fighter, id);
            if(spell != null) break;
        }

        int cell = Function.getInstance().getBestTargetZone(this.fight, this.fighter, spell, this.fighter.getCell().getId(), true);;

        if(cell == 0 || cell == -1) {
            List<Fighter> fighters = PathFinding.getEnemyFighterArround(this.fighter.getCell().getId(), this.fight.getMap(), this.fight, true);
            if (fighters != null && !fighters.isEmpty())
                cell = fighters.get(new Random().nextInt(fighters.size())).getCell().getId();
        } else {
            int nbTarget = cell / 1000;
            cell = cell - nbTarget * 1000;
        }
        if(cell != 0)
            this.fight.tryCastSpell(this.fighter, spell, cell);
        //else
            //this.fight.tryCastSpell(this.fighter, spell, this.fighter.getCell().getId());
    }

    private boolean tryLaunchSpellKraken() {
        if(this.tryLaunchSpellKraken(1096, -1))
            return true;
        if(this.tryLaunchSpellKraken(1097, 31))
            return true;
        if(this.tryLaunchSpellKraken(1098, 32))
            return true;
        if(this.tryLaunchSpellKraken(1099, 33))
            return true;
        return false;
    }

    private boolean tryLaunchSpellKraken(int id, int state) {
        boolean ok = false;
        Spell.SortStats spell = Function.getInstance().findSpell(this.fighter, id);

        if(spell != null) {
            if(state != -1 && !this.fighter.haveState(state))
                return ok;
            if(state != -1 && this.fighter.haveState(state+1))
                return true;

            int cell = 0;
            if(id == 1096) {
                if(this.fighter.getCurPa(this.fight) == 4) {
                    spell = Function.getInstance().findSpell(this.fighter, 261);
                    if(spell != null)
                        this.fight.tryCastSpell(this.fighter, spell, this.fighter.getCell().getId());
                }
            }

            List<Fighter> fighters = PathFinding.getEnemyFighterArround(this.fighter.getCell().getId(), this.fight.getMap(), this.fight, true);
            if(fighters != null && !fighters.isEmpty())
                cell = fighters.get(new Random().nextInt(fighters.size())).getCell().getId();

            if(cell != 0)
                this.fight.tryCastSpell(this.fighter, spell, cell);
            this.fight.tryCastSpell(this.fighter, spell, this.fighter.getCell().getId());
            ok = true;
        }
        return ok;
    }
}