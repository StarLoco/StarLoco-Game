package org.starloco.locos.fight.ia.type.invocations;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;

import java.util.ArrayList;

/**
 * Created by Locos on 04/10/2015.
 */
public class Blocker extends AbstractIA {

    private byte flag = 0;
    //private boolean invocation = false;

    public Blocker(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            int time = 0;
            Fighter enemy = Function.getInstance().getNearestEnnemy(this.fight, this.fighter, true);

            if(enemy != null) {
                switch (this.flag) {
                    case 0:
                        if (Function.getInstance().moveNearIfPossible(fight, fighter, enemy))
                            time = 2000;
                        else if(Function.getInstance().moveautourIfPossible(fight,fighter,enemy) > 0) {
                            time = 1500;
                            this.count = 4;
                            this.flag = -1;
                        }
                        break;
                    case 1:
                    case 2:
                    case 3:
                        if(this.fighter.getMob() == null) break;
                        SortStats spell = Function.getInstance().getBestSpellForTargetDopeul(this.fight, this.fighter, enemy, this.fighter.getCell().getId(), new ArrayList<>(this.fighter.getMob().getSpells().values()));
                        if (spell != null && Function.getInstance().tryCastSpell(this.fight, this.fighter, enemy,
                                spell.getSpell().getId()) == 0) {
                            this.count = 3;
                            this.flag = 0;
                            time = 2500;
                        } else {
                            this.stop = true;
                            time = 1000;
                        }
                        break;
                }
                this.flag++;
            }

            addNext(this::decrementCount, time);
        } else {
            this.stop = true;
        }
    }

}