package org.starloco.locos.fight.spells;

import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.spells.Spell.SortStats;

import java.util.Map;

public class LaunchedSpell {

    private Fighter target = null;
    private SortStats spellStats;
    private int cooldown = 0;

    public LaunchedSpell(Fighter t, SortStats SS, Fighter caster) {
        this.target = t;
        this.spellStats = SS;
        if (caster.getType() == 1 && caster.getPlayer().getObjectsClassSpell().containsKey(SS.getSpellID())) {
            int modi = caster.getPlayer().getValueOfClassObject(SS.getSpellID(), 286);
            this.cooldown = SS.getCoolDown() - modi;
        } else {
            this.cooldown = SS.getCoolDown();
        }
    }

    public static boolean cooldownGood(Fighter fighter, int id) {
        for (LaunchedSpell S : fighter.getLaunchedSorts()) {
            if (S.getSpellId() == id && S.getCooldown() > 0)
                return false;
        }
        return true;
    }

    public static int getNbLaunch(Fighter fighter, int id) {
        int nb = 0;
        for (LaunchedSpell S : fighter.getLaunchedSorts())
            if (S.getSpellId() == id)
                nb++;
        return nb;
    }

    public static int getNbLaunchTarget(Fighter fighter, Fighter target, int id) {
        if (target == null)
            return 0;

        int nb = 0;
        for (LaunchedSpell S : fighter.getLaunchedSorts())
            if (S.target != null)
                if (S.getSpellId() == id && S.target.getId() == target.getId())
                    nb++;
        return nb;
    }

    public static int haveEffectTarget(Map<Integer, Fighter> f, Fighter target, int id) {
        if (target == null) return 0;
        int nb = 0;
        for(Fighter m : f.values())
            if(m != null)
                for (LaunchedSpell S : m.getLaunchedSorts())
                    if (S.target != null && S.target.getId() == target.getId())
                        for(SpellEffect e : S.spellStats.getEffects())
                            if(e.getEffectID() == id)
                                nb++;
        return nb;
    }

    public Fighter getTarget() {
        return this.target;
    }

    public int getSpellId() {
        return spellStats.getSpellID();
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public int decrementCooldown() {
        this.cooldown--;
        return cooldown;
    }

}