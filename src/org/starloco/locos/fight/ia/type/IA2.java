package org.starloco.locos.fight.ia.type;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.ia.util.newia.*;
import org.starloco.locos.fight.ia.util.newia.action.AttackAction;
import org.starloco.locos.fight.ia.util.newia.action.IAAction;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.world.World.Couple;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Locos on 17/04/2018.
 */
public class IA2 extends AbstractEasyIA {

    private Couple<Fighter, Spell.SortStats> last = null;

    public IA2(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        Fighter target;

        if(last != null && last.second != null && last.first != null && last.first.getCell() != null) {
            this.setNextParams(this.flag - 1, this.count + 1, last.second.getSpell().getDuration());
            if(!(fight.tryCastSpell(fighter, last.second, last.first.getCell().getId()) == 0))
                last = null;
        } else {
            last = null;
            switch (this.flag) {
                case 1:
                    //region Invocations
                    if ((fight.getCurFighterPa() > 0) && fight.getMap() != null) {
                        InvocationFighterMind mind = new InvocationFighterMind(this);

                        final LinkedList<IAAction> actions = mind.getHighPriorityActions().isEmpty() ? mind.getLowPriorityActions() : mind.getHighPriorityActions();
                        IAAction action;
                        if ((action = mind.executeActions(actions)) != null) {
                            // On reset l'esprit de l'ia si plus d'actions
                            this.setNextParams(0, 7, action.getWaitingTime());
                            break;
                        }
                    }
                    //endregion
                    break;
                case 2:
                    //region Buff
                    if ((fight.getCurFighterPa() > 0) && fight.getMap() != null) {
                        BuffFighterMind mind = new BuffFighterMind(this);

                        final LinkedList<IAAction> actions = mind.getHighPriorityActions().isEmpty() ? mind.getLowPriorityActions() : mind.getHighPriorityActions();
                        IAAction action;
                        if ((action = mind.executeActions(actions)) != null) {
                            // On reset l'esprit de l'ia si plus d'actions
                            if (action instanceof AttackAction)
                                last = new Couple<>(((AttackAction) action).getCell().getFirstFighter(), ((AttackAction) action).getSpell());
                            this.setNextParams(1, 6, action.getWaitingTime());
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
                            // On reset l'esprit de l'ia si plus d'actions
                            if (action instanceof AttackAction)
                                last = new Couple<>(((AttackAction) action).getCell().getFirstFighter(), ((AttackAction) action).getSpell());
                            this.setNextParams(2, 5, action.getWaitingTime());
                            break;
                        }
                    }
                    //endregion
                    break;
                case 4:
                    //region Heal
                    if ((fight.getCurFighterPa() > 0) && fight.getMap() != null) {
                        HealFighterMind mind = new HealFighterMind(this);

                        final LinkedList<IAAction> actions = mind.getHighPriorityActions().isEmpty() ? mind.getLowPriorityActions() : mind.getHighPriorityActions();
                        IAAction action;
                        if ((action = mind.executeActions(actions)) != null) {
                            // On reset l'esprit de l'ia si plus d'actions
                            if (action instanceof AttackAction)
                                last = new Couple<>(((AttackAction) action).getCell().getFirstFighter(), ((AttackAction) action).getSpell());
                            this.setNextParams(3, 4, action.getWaitingTime());
                            break;
                        }
                    }
                    //endregion
                    break;
                case 5:
                    //region Attack
                    if ((fight.getCurFighterPa() > 0 || fight.getCurFighterPm() > 0) && fight.getMap() != null) {
                        FighterMind mind = new AttackFighterMind(this);
                        final LinkedList<IAAction> actions = mind.getHighPriorityActions().isEmpty() ? mind.getLowPriorityActions() : mind.getHighPriorityActions();
                        IAAction action;
                        if ((action = mind.executeActions(actions)) != null) {
                            // On reset l'esprit de l'ia si plus d'actions
                            if (action instanceof AttackAction)
                                last = new Couple<>(((AttackAction) action).getCell().getFirstFighter(), ((AttackAction) action).getSpell());
                            this.setNextParams(4, 3, action.getWaitingTime());
                            break;
                        }
                    }
                    //endregion
                    break;
                case 6:
                    //region Buff enemy / trap / glyph
                    target = Function.getInstance().getNearestEnnemy(fight, fighter, true);
                    if (target != null) {
                        if (!tryEnemyBuff()) {
                            if (!tryTrap(target)) {
                                if (this.fighter.getCurPm(fight) > 0) {
                                    if(Function.getInstance().moveToAttack(fight, fighter, target, null)) {
                                        this.time = 1250;
                                        if (this.fighter.getCurPm(fight) > 0) {
                                            this.setNextParams(5, 2, 1250);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //endregion
                    break;
            }
        }
    }

    private boolean tryEnemyBuff() {
        if(!this.enemyBuffs.isEmpty()) {
            int pm = fighter.getCurPm(fight);
            for(Fighter enemy : fight.getFighters(3)) {
                if(enemy.getTeam() == fighter.getTeam()) continue;
                for (Spell.SortStats s : this.enemyBuffs) {
                    if (s.getEffects().stream().filter(effect -> effect.getEffectID() == 132).count() >= 1) {
                        byte friendBuff = 0, enemyBuff = 0;
                        for (SpellEffect effect : enemy.getFightBuff()) {
                            if (effect.getCaster() != null && effect.getCaster().getTeam() == fighter.getTeam())
                                friendBuff++;
                            else enemyBuff++;
                        }
                        if (friendBuff > 2 || enemyBuff <= 1) continue;
                    }
                    if (get().moveToAttack(fight, fighter, enemy, s)) {
                        pm -= fighter.getCurPm(fight);
                        this.setNextParams(2, 3, pm <= 3 ? pm * 200 : pm * 100);
                        return true;
                    } else if (get().tryCastSpell(this, enemy, s)) {
                        this.setNextParams(0, 5, s.getSpell().getDuration());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean tryTrap(Fighter target) {
        if(this.traps.isEmpty()) return false;
        final List<GameCase> cells = get().getCellsAvailableAround(target, false, (byte) 1);
        final List<GameCase> cellsCaster = get().getCellsAvailableAround(fighter, true, (byte) 0);
        cells.removeIf(cellsCaster::contains);
        cells.remove(target.getCell());

        if(!cells.isEmpty()) {
            GameCase cell = cells.get(Formulas.getRandomValue(0, cells.size() - 1));
            for(Spell.SortStats spell : this.traps) {
                if (cell != null && fight.tryCastSpell(fighter, spell, cell.getId()) == 0) {
                    this.setNextParams(2, 2, spell.getSpell().getDuration());
                    return true;
                }
            }
        }
        return false;
    }
}
