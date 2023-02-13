package org.starloco.locos.fight.ia.util.newia;

import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.ia.util.AStarPathFinding;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.ia.util.newia.action.AttackAction;
import org.starloco.locos.fight.ia.util.newia.action.MoveAction;
import org.starloco.locos.fight.spells.Spell;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created by Locos on 28/04/2018.
 */
public class HealFighterMind extends FighterMind {

    public HealFighterMind(AbstractEasyIA ia) {
        super(ia);

        for(Fighter target : this.getFriends()) {
            final AStarPathFinding aStar = new AStarPathFinding(ia.getFight(), ia.getFighter().getCell().getId(), target.getCell().getId());

            final LinkedList<Spell.SortStats> sortedSpells = new LinkedList<>();
            for(Spell.SortStats spell : ia.getHealsSpells()) {
                if (getPdvPer(target) < 100 && ia.getFight().canLaunchSpell(ia.getFighter(), spell, target.getCell()))
                    sortedSpells.addLast(spell);
            }

            this.fightersCases.addLast(new FighterCase(target, aStar, sortedSpells));
        }

        this.fightersCases.sort(Comparator.comparingInt(t0 -> t0 != null && t0.getShortestPath() != null ? t0.getShortestPath().size() : 999));
        this.init();
    }

    public void init() {
        for(FighterCase cas : this.fightersCases) {
            for(Spell.SortStats spell : cas.getSortedSpells()) {
                // Si sort de corps-à-corps ((buff sois-même)
                if(spell.getMaxPO() == 0 && cas.getFighter() == ia.getFighter()) {
                    if (ia.getFight().canCastSpell1(ia.getFighter(), spell, ia.getFighter().getCell(), -1)) {
                        this.highPriorityActions.addFirst(new AttackAction(ia.getFighter(), ia.getFighter().getCell(), spell));
                    }
                // Sinon, sort de distance
                } else if (ia.getFight().canCastSpell1(ia.getFighter(), spell, cas.getFighter().getCell(), -1)) {
                    this.highPriorityActions.addLast(new AttackAction(ia.getFighter(), cas.getFighter().getCell(), spell));
                }
            }
        }

        if(this.highPriorityActions != null && !this.highPriorityActions.isEmpty())
            return;

        // Gérer les priorités basse
        for(FighterCase cas : this.fightersCases) {
            for (Spell.SortStats spell : cas.getSortedSpells()) {
                if (ia.getFight().canLaunchSpell(ia.getFighter(), spell, cas.getFighter().getCell()) && Function.getInstance().moveToAttack(ia.getFight(), ia.getFighter(), cas.getFighter().getCell(), spell, false)) {
                    this.lowPriorityActions.addLast(new MoveAction(ia.getFighter(), spell, cas.getFighter().getCell()));
                    this.lowPriorityActions.addLast(new AttackAction(ia.getFighter(), cas.getFighter().getCell(), spell));
                }
            }
        }
    }

    private int getPdvPer(Fighter target) {
        return ((target.getPdv() * 100) / target.getPdvMax());
    }
}
