package org.starloco.locos.fight.ia.type.boss;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA23 extends AbstractIA  {

    public IA23(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            Fighter ennemy = Function.getInstance().getNearestFriendNoInvok(this.fight, this.fighter);

            if (!Function.getInstance().moveNearIfPossible(this.fight, this.fighter, ennemy))
                Function.getInstance().HealIfPossible(this.fight, this.fighter, false);

            addNext(this::decrementCount, 500);
        } else {
            this.stop = true;
        }
    }
}