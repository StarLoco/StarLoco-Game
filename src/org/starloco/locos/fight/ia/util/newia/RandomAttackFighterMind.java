package org.starloco.locos.fight.ia.util.newia;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.ia.util.AStarPathFinding;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.ia.util.newia.action.AttackAction;
import org.starloco.locos.fight.ia.util.newia.action.MoveAction;
import org.starloco.locos.fight.spells.Spell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Locos on 28/04/2018.
 */
public class RandomAttackFighterMind extends FighterMind {

    private boolean friend;

    public RandomAttackFighterMind(AbstractEasyIA ia, boolean friend) {
        super(ia);
        this.friend = friend;

        final List<Fighter> fighters = new ArrayList<>();
        fighters.addAll(this.getFriends());
        fighters.addAll(this.getEnemies(false));
        for(Fighter target : fighters) {
            final AStarPathFinding aStar = new AStarPathFinding(ia.getFight(), ia.getFighter().getCell().getId(), target.getCell().getId());

            final LinkedList<Spell.SortStats> sortedSpells = new LinkedList<>();
            for(Spell.SortStats spell : ia.getAttacksSpells()) {
                if (ia.getFight().canLaunchSpell(ia.getFighter(), spell, target.getCell()))
                    sortedSpells.addLast(spell);
            }

            this.fightersCases.addLast(new FighterCase(target, aStar, sortedSpells));
        }

        this.fightersCases.sort(Comparator.comparingInt(t0 -> t0 != null && t0.getShortestPath() != null ? t0.getShortestPath().size() : 999));
        this.init();
    }

    public void init() {
        for(FighterCase cas : this.fightersCases) {
            if(friend && cas.getFighter().getTeam() == ia.getFighter().getTeam()) continue;
            for(Spell.SortStats spell : cas.getSortedSpells()) {
                // Si sort de corps-à-corps (fourvoiement)
                int dist = PathFinding.getDistanceBetween(ia.getFight().getMap(), ia.getFighter().getCell().getId(), cas.getFighter().getCell().getId());
                // Si il est bien au corps à corps de la cible et qu'il peut le lancer sur lui
                if (dist == 1 && ia.getFight().canCastSpell1(ia.getFighter(), spell, cas.getFighter().getCell(), -1)) {
                    this.highPriorityActions.addLast(new AttackAction(ia.getFighter(), cas.getFighter().getCell(), spell));
                }
                // Pas de sort à distance dans ce genre d'ia
            }
        }
        if(this.highPriorityActions != null && !this.highPriorityActions.isEmpty())
            return;
        // Gérer les priorités basse
        for(FighterCase cas : this.fightersCases) {
            if(friend && cas.getFighter().getTeam() == ia.getFighter().getTeam()) continue;
            for (Spell.SortStats spell : cas.getSortedSpells()) {
                if (ia.getFight().canLaunchSpell(ia.getFighter(), spell, cas.getFighter().getCell()) && Function.getInstance().moveToAttack(ia.getFight(), ia.getFighter(), cas.getFighter().getCell(), spell, false)) {
                    this.lowPriorityActions.addLast(new MoveAction(ia.getFighter(), spell, cas.getFighter().getCell()));
                    this.lowPriorityActions.addLast(new AttackAction(ia.getFighter(), cas.getFighter().getCell(), spell));
                }
            }
        }
    }
}
