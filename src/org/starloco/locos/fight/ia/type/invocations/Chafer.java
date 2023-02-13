package org.starloco.locos.fight.ia.type.invocations;

import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Locos on 04/10/2015.
 */
public class Chafer extends AbstractNeedSpell  {

    private int flag = 0;
    private Fighter target;

    public Chafer(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
        if(fighter.getMob() != null && fighter.getMob().getTemplate().getId() == 1108)
            this.flag = -1; // Chaferfu lancier to buff himself
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            int time = 0;
            Fighter friend = Function.getInstance().getNearestFriend(this.fight, this.fighter);
            Fighter enemy = Function.getInstance().getNearestEnnemy(this.fight, this.fighter, true);

            if(this.target == null) {
                if (friend != null && Formulas.getRandomValue(1, 3) == 1) {
                    target = friend;
                } else target = enemy;
            }

            if(this.target == null) {
                time = Function.getInstance().moveFarIfPossible(this.fight, this.fighter);
            } else {
                switch (this.flag) {
                    case -1:
                        SortStats spell = Function.getInstance().getBuffSpell(fight, fighter, fighter);

                        if(spell != null && fight.canLaunchSpell(fighter, spell, fighter.getCell())) {
                            if(fight.tryCastSpell(this.fighter, spell, fighter.getCell().getId()) == 0) {
                                time = 1500;
                            }
                        }
                        break;
                    case 0:
                        short cell = (short) this.fighter.getCell().getId();
                        if(PathFinding.getEnemyFighterArround(this.fighter.getCell().getId(), this.fight.getMap(), this.fight, true) != null) {
                            Function.getInstance().moveautourIfPossible(this.fight, this.fighter, target);
                            time = 1500;
                            break;
                        }
                        if (Function.getInstance().moveNearIfPossible(this.fight, this.fighter, this.target)) {
                            time = 1500;
                            break;
                        }
                        if(cell == this.fighter.getCell().getId()) {
                            time = 0;
                            if(friend != null && PathFinding.getDistanceBetweenTwoCase(this.fight.getMap(), this.fighter.getCell(), friend.getCell()) > 1) {
                                this.target = enemy;
                                this.count = 4;
                                this.flag = -1;
                            }
                        }
                        break;
                    case 1:
                    case 2:
                    case 3:
                        spell = Function.getInstance().getBestSpellForTargetDopeul(this.fight, this.fighter, target, this.fighter.getCell().getId(), new ArrayList<>(this.fighter.getMob().getSpells().values()));
                        if (spell != null && Function.getInstance().tryCastSpell(this.fight, this.fighter, target, spell.getSpell().getId()) == 0) {
                            if(spell.getMaxLaunchByTarget() == 1) {
                                List<Fighter> fighters = PathFinding.getEnemyFighterArround(this.fighter.getCell().getId(), this.fight.getMap(), this.fight, false);
                                if(fighters != null) {
                                    if (fighters.contains(target))
                                        fighters.remove(target);
                                    int i = fighters.size() - 1;
                                    if (i >= 0) {
                                        target = fighters.get(Formulas.random.nextInt(fighters.size()));
                                    } else target = target == friend ? enemy : friend;
                                }

                                this.count = 4;
                                this.flag = -1;
                                break;
                            }
                            this.count = 4;
                            this.flag = -1;
                            this.target = null;
                            time = 1000;
                        } else {
                            if (Function.getInstance().moveNearIfPossible(this.fight, this.fighter, this.target)) {
                                time = 2000;
                            } else {
                                this.stop = true;
                                time = 1000;
                            }
                        }
                        break;
                }
                this.flag++;
            }


            if(this.fighter.getCurPa(this.fight) == 0 && this.fighter.getCurPm(this.fight) == 0) {
                this.stop = true;
                time = 1000;
            }

            addNext(this::decrementCount, time);
        } else {
            this.stop = true;
        }
    }

}