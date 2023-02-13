package org.starloco.locos.fight.ia.type.boss;

import org.starloco.locos.common.SocketManager;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

import java.util.Collection;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA18 extends AbstractIA {

    private boolean pair = false, impair = false, ok = false;

    public IA18(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            Fighter kimbo = this.findKimbo();
            int time = 0;

            if (this.ok && this.fighter.getCurPm(this.fight) > 0) {
                if (Function.getInstance().moveNearIfPossible(this.fight, this.fighter, kimbo)) {
                    this.stop = true;
                    time = 1500;
                }
            } else {
                if (this.pair || this.impair) {
                    if (this.pair) {
                        this.attackGlyph(this.fighter, 1072);
                    } else {
                        this.attackGlyph(this.fighter, 1073);
                    }
                }
            }



            this.addNext(this::decrementCount, time);
        } else {
            this.stop = true;
        }
    }

    private Fighter findKimbo() {
        int id = this.fight.getTeamId(this.fighter.getId());
        Collection<Fighter> fighters = this.fight.getTeam(id).values();

        for(Fighter fighter : fighters) {
            if(fighter.getMob() !=  null) {
                if(fighter.getMob().getTemplate().getId() == 1045) {
                    if(fighter.haveState(30)) {
                        fighter.setState(30, 0);
                        this.pair = true;
                        this.fighter.setState(30, 1);
                    }
                    if(fighter.haveState(29)) {
                        fighter.setState(29, 0);
                        this.impair = true;
                        this.fighter.setState(29, 1);
                    }
                    return fighter;
                }
            }
        }
        return null;
    }

    public void attackGlyph(Fighter target, int id) {
        if (this.fight == null || this.fighter == null || target == null)
            return;
        Spell.SortStats spell = Function.getInstance().findSpell(this.fighter, id);
        int attack = fight.tryCastSpell(fighter, spell, target.getCell().getId());

        if (attack != 0) {
            this.ok = true;
            this.fight.getAllGlyphs().stream().filter(entry -> entry.getCell().getId() == this.fighter.getCell().getId()).forEach(entry -> {
                this.fighter.addBuff(128, 1, 1, true, 1072, "", this.fighter,  null);
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 78, this.fighter.getId() + "", this.fighter.getId() + "," + "" + "," + 1);
            });
        }
    }
}