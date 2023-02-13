package org.starloco.locos.fight.traps;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;

public class Glyph {

    private Fighter caster;
    private GameCase cell;
    private byte size;
    private int spell;
    private SortStats trapSpell;
    private byte duration;
    private Fight fight;
    private int color;

    public Glyph(Fight fight, Fighter caster, GameCase cell, byte size,
                 SortStats trapSpell, byte duration, int spell) {
        this.fight = fight;
        this.caster = caster;
        this.cell = cell;
        this.spell = spell;
        this.size = size;
        this.trapSpell = trapSpell;
        this.duration = duration;
        this.color = Constant.getGlyphColor(spell);
    }

    public Fighter getCaster() {
        return this.caster;
    }

    public GameCase getCell() {
        return this.cell;
    }

    public byte getSize() {
        return this.size;
    }

    public int getSpell() {
        return this.spell;
    }

    public int decrementDuration() {
        //if(this.duration == -1) return -1;
        this.duration--;
        return this.duration;
    }

    public int getColor() {
        return this.color;
    }

    public void onTrapped(Fighter target) {
        if(this.spell == 1072 || this.spell == 1073) {//glyph pair/impair
            if(target.getMob() != null) {
                if(target.getMob().getTemplate().getId() == 1045) {
                    if(this.spell == 1072) {
                        target.addBuff(217, 400, 2, false, 1077, "", target, null);// - 400 air
                        target.addBuff(218, 400, 2, false, 1077, "", target, null);// - 400 feu
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1077, caster.getId() + "", target.getId() + "," + "" + "," + 1);
                        this.fight.getFighters(7).stream().filter(fighter -> fighter.getPlayer() != null && fighter.getPlayer().isOnline()).forEach(fighter -> {
                            fighter.getPlayer().send("GA;217;-100;" + target.getId() + ",400,1");
                            fighter.getPlayer().send("GA;218;-100;" + target.getId() + ",400,1");
                        });
                    } else {
                        target.addBuff(215, 400, 2, false, 1077, "", target, null);// - 400 terre
                        target.addBuff(216, 400, 2, false, 1077, "", target, null);// - 400 eau

                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1077, caster.getId() + "", target.getId() + "," + "" + "," + 1);

                        this.fight.getFighters(7).stream().filter(fighter -> fighter.getPlayer() != null && fighter.getPlayer().isOnline()).forEach(fighter -> {
                            fighter.getPlayer().send("GA;216;-100;" + target.getId() + ",400,1");
                            fighter.getPlayer().send("GA;215;-100;" + target.getId() + ",400,1");
                        });
                    }
                } else {
                    this.fight.onFighterDie(target, target);
                }
            } else {
                fight.onFighterDie(target, target);
            }
        } else {
            Spell spell = World.world.getSort(this.spell);

            for(Integer integer : spell.getEffectTargets())
                if(integer == 2 && target == this.caster)
                    return;

            String str = this.spell + "," + this.cell.getId() + ", 0, 1, 1," + this.caster.getId();
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 307, target.getId() + "", str);
            target.trapped = true;
            this.trapSpell.applySpellEffectToFight(this.fight, this.caster, target.getCell(), false, true);
            this.fight.verifIfTeamAllDead();
        }
    }

    public void disappear() {
        SocketManager.GAME_SEND_GDZ_PACKET_TO_FIGHT(this.fight, 7, "-", this.cell.getId(), this.size, this.color);
        SocketManager.GAME_SEND_GDC_PACKET_TO_FIGHT(this.fight, 7, this.cell.getId());
    }
}