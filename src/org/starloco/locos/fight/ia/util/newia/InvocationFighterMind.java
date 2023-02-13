package org.starloco.locos.fight.ia.util.newia;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.ia.util.AStarPathFinding;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.ia.util.newia.action.AttackAction;
import org.starloco.locos.fight.ia.util.newia.action.MoveAction;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.kernel.Constant;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created by Locos on 28/04/2018.
 */
public class InvocationFighterMind extends FighterMind {

    public InvocationFighterMind(AbstractEasyIA ia) {
        super(ia);

        if(ia.getFighter().nbrInvoc >= ia.getFighter().getTotalStats().get(Constant.STATS_CREATURE))
            return;

        for(Fighter target : this.getEnemies(false)) {
            final AStarPathFinding aStar = new AStarPathFinding(ia.getFight(), ia.getFighter().getCell().getId(), target.getCell().getId());

            final LinkedList<Spell.SortStats> sortedSpells = new LinkedList<>();
            for(Spell.SortStats spell : ia.getInvocations()) {
                if (spell != null && ia.getFight().canLaunchSpell(ia.getFighter(), spell, target.getCell()))
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
                // Si invocation de corps-à-corps
                if(spell.getMaxPO() <= 1) {
                    GameCase cell = ia.getFight().getMap().getCase(PathFinding.getAvailableCellArround(ia.getFight(), ia.getFighter().getCell().getId(), null));
                    if (cell != null && cell.isWalkable(true) && cell.getFirstFighter() == null && ia.getFight().canCastSpell1(ia.getFighter(), spell, cell, -1)) {
                        this.highPriorityActions.addFirst(new AttackAction(ia.getFighter(), cell, spell));
                    }
                // Sinon, sort de distance
                } else {
                    GameCase cell = ia.getFight().getMap().getCase(PathFinding.getAvailableCellArround(ia.getFight(), cas.getFighter().getCell().getId(), null));
                    if (cell != null && cell.getFirstFighter() == null && cell.isWalkable(true) && ia.getFight().canCastSpell1(ia.getFighter(), spell, cell, -1)) {
                        this.highPriorityActions.addLast(new AttackAction(ia.getFighter(), cell, spell));
                    }
                }
            }
        }

        if(this.highPriorityActions != null && !this.highPriorityActions.isEmpty())
            return;

        // Gérer les priorités basse
        for(FighterCase cas : this.fightersCases) {
            for (Spell.SortStats spell : cas.getSortedSpells()) {
                GameCase cell = ia.getFight().getMap().getCase(PathFinding.getAvailableCellArround(ia.getFight(), cas.getFighter().getCell().getId(), null));

                if (cell != null && cell.isWalkable(true) && cell.getFirstFighter() == null &&  ia.getFight().canLaunchSpell(ia.getFighter(), spell, cell) && Function.getInstance().moveToAttack(ia.getFight(), ia.getFighter(), cell, spell, false)) {
                    this.lowPriorityActions.addLast(new MoveAction(ia.getFighter(), spell, cell));
                    this.lowPriorityActions.addLast(new AttackAction(ia.getFighter(), cell, spell));
                }
            }
        }
    }
}
