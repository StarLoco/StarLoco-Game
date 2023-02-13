package org.starloco.locos.fight.ia;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.SpellEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Locos on 09/04/2018.
 */
public abstract class AbstractEasyIA extends AbstractIA {

    protected byte flag = 0;
    protected short time = 0;
    protected List<Spell.SortStats> attacks, friendBuffs, enemyBuffs, heals, teleportations, traps, invocations;

    public AbstractEasyIA(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
        this.attacks = getListSpellOf(fighter, "ATTACK");
        this.friendBuffs = getListSpellOf(fighter, "FRIEND-BUFF");
        this.enemyBuffs = getListSpellOf(fighter, "ENEMY-BUFF");
        this.heals = getListSpellOf(fighter, "HEAL");
        this.teleportations = getListSpellOf(fighter, "TP");
        this.traps = getListSpellOf(fighter, "TRAP");
        this.invocations = getListSpellOf(fighter, "INVOCATION");
    }

    protected void setNextParams(int flag, int count, int time) {
        this.flag = (byte) flag;
        this.count = (byte) count;
        this.time = (short) (time);
    }

    protected Function get() {
        return Function.getInstance();
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            if(!fight.getCurAction().isEmpty()) {
                this.addNext(this::apply, 100);
                return;
            }
            if(this.time == 0 && this.fighter.getCurPa(this.fight) == 0 && this.fighter.getCurPm(this.fight) == 0) {
                this.stop = true;
                time = 1000;
            }

            this.run();
            this.flag++;
            if(!this.fighter.isDead())
                this.addNext(this::decrementCount, (int) time);
            else if(this.fight.getFighterByGameOrder() != null && this.fighter.getId() == this.fight.getFighterByGameOrder().getId())
                this.endTurn();
        } else {
            this.stop = true;
            this.time = 500;
        }
    }

    public abstract void run();

    private List<Spell.SortStats> getListSpellOf(Fighter fighter, String type) {
        final List<Spell.SortStats> spells = new ArrayList<>();

        for(Spell.SortStats spell : fighter.getMob().getSpells().values()) {
            if(spells.contains(spell)) continue;
            switch(type) {
                case "ATTACK":
                    if(spell.getSpell().getType() == 0)
                        spells.add(spell);
                    break;
                case "FRIEND-BUFF":
                    if(spell.getSpell().getType() == 1) spells.add(spell);
                    break;
                case "ENEMY-BUFF":
                    if(spell.getSpell().getType() == 2) spells.add(spell);
                    break;
                case "HEAL":
                    if(spell.getSpell().getType() == 3) spells.add(spell);
                    break;
                case "TP":
                    if(spell.getSpell().getType() == 4)
                        spells.add(spell);
                    break;
                case "TRAP":
                    if(spell.getSpell().getType() == 5) spells.add(spell);
                    break;
                case "INVOCATION":
                    for(SpellEffect effect : spell.getEffects())
                        if(effect.getEffectID() == 181) {
                            spells.add(spell);
                            break;
                        }
                    break;
            }
        }
        return spells;
    }

    public List<Spell.SortStats> getAttacksSpells() {
        return attacks;
    }

    public List<Spell.SortStats> getFriendBuffsSpells() {
        return friendBuffs;
    }

    public List<Spell.SortStats> getHealsSpells() {
        return heals;
    }

    public List<Spell.SortStats> getTeleportations() {
        return teleportations;
    }

    public List<Spell.SortStats> getInvocations() {
        return invocations;
    }
}
