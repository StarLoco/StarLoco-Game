package org.starloco.locos.fight.ia.type.invocations;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.kernel.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Locos on 04/10/2015.
 */
public class Tonneau extends AbstractNeedSpell  {

    private final List<Integer> fighters = new ArrayList<>();

    public Tonneau(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            int time = 0;
            if (this.fighter.getHoldedBy() != null) {
                if (Function.getInstance().tryCastSpell(fight, fighter, fighter, 1675) == 0)
                    time = 2500;
            } else {
                List<Fighter> fighters = this.getFightersInline(Function.getInstance().findSpell(fighter, 916));
                fighters.removeIf((f) -> this.fighters.contains(f.getId()));

                if(fighters.size() > 0) {
                    if(Function.getInstance().tryCastSpell(fight, fighter, fighters.get(0), 916) == 0) {
                        time = 2500;
                    }
                    this.fighters.add(fighters.get(0).getId());
                }
            }

            addNext(this::decrementCount, time);
        } else {
            this.stop = true;
        }
    }

    private List<Fighter> getFightersInline(Spell.SortStats spell) {
        List<Fighter> fighters = new ArrayList<>();
        for(Fighter target : fight.getFighters(3)) {
            if(target.getTeam() != fighter.getTeam() || target.haveState(Constant.ETAT_SAOUL)) {
                int c1 = target.getCell().getId(), c2 = fighter.getCell().getId();
                char dir = PathFinding.getDirBetweenTwoCase(c1, c2, fight.getMap(), true);
                if(!PathFinding.isNextTo(fight.getMap(), c1, c2) && PathFinding.casesAreInSameLine(fight.getMap(), c1, c2, dir, 666)) {
                    if(fight.canCastSpell1(fighter, spell, target.getCell(), -1))
                        fighters.add(target);
                }
            }
        }
        return fighters;
    }

}