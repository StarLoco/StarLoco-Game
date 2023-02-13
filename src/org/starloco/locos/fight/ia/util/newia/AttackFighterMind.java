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

import java.util.*;

/**
 * Created by Locos on 28/04/2018.
 */
public class AttackFighterMind extends FighterMind {

    public AttackFighterMind(AbstractEasyIA ia) {
        super(ia);

        boolean attackInvocation = ia.getFighter().getMob().getTemplate().getId() == 676;

        for(Fighter target : attackInvocation ? this.getInvocations() : this.getEnemies(true)) {
            final FighterCase cas = new FighterCase(target, null, new LinkedList<>());
            final GameCase cell = cas.getFighterCell();
            if(cell == null) continue;
            final AStarPathFinding aStar = new AStarPathFinding(ia.getFight(), ia.getFighter().getCell().getId(), cell.getId());

            for(Spell.SortStats spell : ia.getAttacksSpells()) {
                if(spell.getEffects().stream().filter(effect -> effect.getEffectID() == 6).count() == 1)
                    if(aStar.getShortestPath().size() - 1 <= ia.getFighter().getCurPm(ia.getFight()))
                        continue;
                if (spell.getEffects().stream().filter(effect -> effect.getEffectID() == 4).count() == 1 || ia.getFight().canLaunchSpell(ia.getFighter(), spell, target.getCell()))
                    cas.getSortedSpells().addLast(spell);
            }

            cas.setShortestPath(aStar.getShortestPath());
            this.fightersCases.addLast(cas);
        }

        this.fightersCases.sort(Comparator.comparingInt(t0 -> t0 != null && t0.getShortestPath() != null ? t0.getShortestPath().size() + (t0.getFighter().isHide() ? t0.getFighter().getPm() : 0) : 999));
        this.init();
    }

    public void init() {
        for(FighterCase cas : this.fightersCases) {
            GameCase fighterCell = cas.getFighterCell();
            if(fighterCell == null) continue;

            for(Spell.SortStats spell : cas.getSortedSpells()) {
                if(spell.getEffects().stream().filter(effect -> effect.getEffectID() == 6).count() == 1) {
                    if(PathFinding.casesAreInSameLine(ia.getFight().getMap(), ia.getFighter().getCell(), fighterCell, spell.getMaxPO()))
                        this.highPriorityActions.addFirst(new AttackAction(ia.getFighter(), fighterCell, spell));
                    continue;
                }
                if(spell.getEffects().stream().filter(effect -> effect.getEffectID() == 4).count() == 1) {
                    if(cas.getShortestPath().size() - 1 > ia.getFighter().getCurPm(ia.getFight())) {
                        GameCase cell = ia.getFight().getMap().getCase(Function.getInstance().getMaxCellForTP(ia.getFight(), ia.getFighter(), cas.getFighter(), spell.getMaxPO()));
                        if (ia.getFight().canLaunchSpell(ia.getFighter(), spell, cell))
                            this.highPriorityActions.addLast(new AttackAction(ia.getFighter(), cell, spell));
                    }
                    continue;
                }
                // Si sort de corps-à-corps (fourvoiement)
                if(spell.getMaxPO() == 0) {
                    int dist = PathFinding.getDistanceBetween(ia.getFight().getMap(), ia.getFighter().getCell().getId(), fighterCell.getId());
                    // Si il est bien au corps à corps de la cible et qu'il peut le lancer sur lui
                    if (dist == 1 && ia.getFight().canCastSpell1(ia.getFighter(), spell, ia.getFighter().getCell(), -1)) {
                        this.highPriorityActions.addLast(new AttackAction(ia.getFighter(), ia.getFighter().getCell(), spell));
                        continue;
                    }
                }
                // Sinon, sort de distance
                if (ia.getFight().canCastSpell1(ia.getFighter(), spell, fighterCell, -1)) {
                    this.highPriorityActions.addLast(new AttackAction(ia.getFighter(), fighterCell, spell));
                }
            }
        }
        if(this.highPriorityActions != null && !this.highPriorityActions.isEmpty())
            return;
        // Gérer les priorités basse
        for(FighterCase cas : this.fightersCases) {
            GameCase fighterCell = cas.getFighterCell();
            if(fighterCell == null) continue;

            for (Spell.SortStats spell : cas.getSortedSpells()) {
                if (ia.getFight().canLaunchSpell(ia.getFighter(), spell, fighterCell) && Function.getInstance().moveToAttack(ia.getFight(), ia.getFighter(), fighterCell, spell, false)) {
                    this.lowPriorityActions.addLast(new MoveAction(ia.getFighter(), spell, fighterCell));
                    this.lowPriorityActions.addLast(new AttackAction(ia.getFighter(), fighterCell, spell));
                }
            }
        }
    }
}
