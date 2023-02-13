package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.ia.util.newia.*;
import org.starloco.locos.fight.ia.util.newia.action.AttackAction;
import org.starloco.locos.fight.ia.util.newia.action.IAAction;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.world.World;

import java.util.LinkedList;

/**
 * Created by Locos on 01/06/2018.
 */
public class IA1 extends AbstractEasyIA {

    private boolean friend = false;
    private World.Couple<Fighter, Spell.SortStats> last = null;

    public IA1(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        if(last != null && last.second != null && last.first != null && last.first.getCell() != null) {
            this.setNextParams(this.flag - 1, this.count + 1, last.second.getSpell().getDuration());
            if(!(fight.tryCastSpell(fighter, last.second, last.first.getCell().getId()) == 0))
                last = null;
        } else {
            last = null;
            switch (this.flag) {
                case 1:
                    //region Buff
                    if ((fight.getCurFighterPa() > 0) && fight.getMap() != null) {
                        BuffFighterMind mind = new BuffFighterMind(this);

                        final LinkedList<IAAction> actions = mind.getHighPriorityActions().isEmpty() ? mind.getLowPriorityActions() : mind.getHighPriorityActions();
                        IAAction action;
                        if ((action = mind.executeActions(actions)) != null) {
                            // On reset l'esprit de l'ia si plus d'actions
                            if (action instanceof AttackAction)
                                last = new World.Couple<>(((AttackAction) action).getCell().getFirstFighter(), ((AttackAction) action).getSpell());
                            this.setNextParams(0, 4, action.getWaitingTime());
                            break;
                        }
                    }
                    //endregion
                    break;
                case 2:
                    //region Attack
                    if (fight.getCurFighterPa() > 0 && fight.getMap() != null) {
                        FighterMind mind = new RandomAttackFighterMind(this, friend);
                        IAAction action;
                        if ((action = mind.executeActions(mind.getHighPriorityActions())) != null) {
                            // On reset l'esprit de l'ia si plus d'actions

                            if (action instanceof AttackAction) {
                                Fighter target = ((AttackAction) action).getCell().getFirstFighter();
                                if(target == null || target.getTeam() == this.fighter.getTeam())
                                    friend = true;
                                last = new World.Couple<>(target, ((AttackAction) action).getSpell());
                            }
                            this.setNextParams(1, 3, action.getWaitingTime());
                            break;
                        }
                    }
                    //endregion
                    break;
                case 3:
                    int pm = fighter.getCurPm(fight);
                    if(get().moveToAttack(fight, fighter, get().getNearestEnnemy(fight, fighter, true), null)) {
                        pm -= fighter.getCurPm(fight);
                        time = (short)( (pm <= 3 ? pm * 200 : pm * 100) *2);
                    }
                    break;
            }
        }
    }
}