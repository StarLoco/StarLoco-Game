package org.starloco.locos.fight.ia.type.invocations;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;

/**
 * Created by Locos on 04/10/2015.
 */
public class Lapino extends AbstractNeedSpell  {

    private byte flag = 0;

    public Lapino(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            int time = 0;
            Fighter friend = Function.getInstance().getNearestFriendNoInvok(this.fight, this.fighter);

            if(friend == null) {
                time = Function.getInstance().moveFarIfPossible(this.fight, this.fighter);
            } else {
                switch (this.flag) {
                    case 0:
                        SortStats spell = Function.getInstance().getBestBuffSpell(fight, fighter, friend);
                        if(fight.canLaunchSpell(fighter, spell, friend.getCell())) {
                            if (Function.getInstance().moveToAttack(this.fight, this.fighter, friend, spell)) {
                                this.count = 4;
                                this.flag = -1;
                                time = 1500;
                            } else if (Function.getInstance().tryCastSpell(fight, fighter, friend, spell.getSpell().getId()) == 0) {
                                time = 1500;
                            }
                        }
                        break;
                    case 1:
                    case 2:
                        spell = Function.getInstance().getBestHealSpell(this.fight, this.fighter, friend);
                        if(spell != null) {
                            if (spell.getMaxPO() == 0) {
                                if (Function.getInstance().tryCastSpell(fight, fighter, fighter, spell.getSpellID()) == 0) {
                                    this.count = 4;
                                    this.flag = 0;
                                    time = 1000;
                                    break;
                                }
                            }
                            if (Function.getInstance().moveToAttack(this.fight, this.fighter, friend, spell)) {
                                time = 1500;
                                this.count = 3;
                                this.flag = 0;
                            } else if (Function.getInstance().HealIfPossible(this.fight, this.fighter, false, 95) == 0) {
                                this.count = 3;
                                this.flag = 0;
                                time = 1500;
                            } else if (Function.getInstance().HealIfPossible(this.fight, this.fighter, true, 95) == 0) {
                                this.stop = true;
                                time = 2000;
                            }
                        }
                        break;
                    case 3:
                        time = Function.getInstance().moveFarIfPossible(this.fight, this.fighter);
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