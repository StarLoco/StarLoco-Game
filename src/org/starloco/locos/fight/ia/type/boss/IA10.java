package org.starloco.locos.fight.ia.type.boss;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.ia.util.newia.AttackFighterMind;
import org.starloco.locos.fight.ia.util.newia.BuffFighterMind;
import org.starloco.locos.fight.ia.util.newia.FighterMind;
import org.starloco.locos.fight.ia.util.newia.InvocationFighterMind;
import org.starloco.locos.fight.ia.util.newia.action.AttackAction;
import org.starloco.locos.fight.ia.util.newia.action.IAAction;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA10 extends AbstractEasyIA {

    public IA10(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        switch(this.flag) {
            case 1:
                //region Buff
                if ((fight.getCurFighterPa() > 0) && fight.getMap() != null) {
                    BuffFighterMind mind = new BuffFighterMind(this);

                    final LinkedList<IAAction> actions = mind.getHighPriorityActions().isEmpty() ? mind.getLowPriorityActions() : mind.getHighPriorityActions();
                    IAAction action;
                    if ((action = mind.executeActions(actions)) != null) {
                        this.setNextParams(0, 5, action.getWaitingTime());
                        break;
                    }
                }
                //endregion
                break;
            case 2:
                //region Invocations
                if ((fight.getCurFighterPa() > 0) && fight.getMap() != null) {
                    InvocationFighterMind mind = new InvocationFighterMind(this);

                    final LinkedList<IAAction> actions = mind.getHighPriorityActions().isEmpty() ? mind.getLowPriorityActions() : mind.getHighPriorityActions();
                    IAAction action;

                    Iterator<IAAction> iterator = actions.iterator();
                    while (iterator.hasNext()) {
                        AttackAction aa = (AttackAction) iterator.next();
                        switch(aa.getSpell().getSpellID()) {
                            case 1107: // Primaire
                                if(get().hasMobInFight(fight, 424)) iterator.remove();
                                break;
                            case 1108: // Secondaire
                                if(get().hasMobInFight(fight, 1092)) iterator.remove();
                                break;
                            case 1109: // Tertiaire
                                if(get().hasMobInFight(fight, 1091)) iterator.remove();
                                break;
                            case 1110: // Quaternaire
                                if(get().hasMobInFight(fight, 1090)) iterator.remove();
                                break;
                        }
                    }

                    if ((action = mind.executeActions(actions)) != null) {
                        if(get().hasMobInFight(fight, 1092))
                            fighter.setState(38, 0);
                        else if(get().hasMobInFight(fight, 1091))
                            fighter.setState(37, 0);
                        else if(get().hasMobInFight(fight, 1090))
                            fighter.setState(36, 0);
                        else if(get().hasMobInFight(fight, 424))
                            fighter.setState(35, 0);

                        this.setNextParams(1, 4, action.getWaitingTime() + 300);
                        break;
                    }
                }
                //endregion
                break;
            case 3:
                //region Attack
                if (fight.getCurFighterPa() > 0 && fight.getMap() != null) {
                    FighterMind mind = new AttackFighterMind(this);
                    IAAction action;
                    if ((action = mind.executeActions(mind.getHighPriorityActions())) != null) {
                        this.setNextParams(2, 3, action.getWaitingTime());
                        break;
                    }
                }
                //endregion
                break;
            case 4:
                //region Buff
                if ((fight.getCurFighterPa() > 0) && fight.getMap() != null) {
                    BuffFighterMind mind = new BuffFighterMind(this);

                    final LinkedList<IAAction> actions = mind.getHighPriorityActions().isEmpty() ? mind.getLowPriorityActions() : mind.getHighPriorityActions();
                    IAAction action;
                    if ((action = mind.executeActions(actions)) != null) {
                        this.setNextParams(3, 2, action.getWaitingTime());
                        break;
                    }
                }
                //endregion
                break;
        }
    }
}