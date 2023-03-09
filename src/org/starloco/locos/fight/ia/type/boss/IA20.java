package org.starloco.locos.fight.ia.type.boss;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA20 extends AbstractIA  {

    private boolean coop = false;

    public IA20(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            Fighter nearestEnnemy = Function.getInstance().getEnnemyWithDistance(this.fight, this.fighter, 0, 10, null);

            if(nearestEnnemy == null)
                nearestEnnemy = Function.getInstance().getEnnemyWithDistance(this.fight, this.fighter, 0, 60, null);

            //int dist = PathFinding.getDistanceBetweenTwoCase(this.fight.getMaps(), this.fighter.getCell(), nearestEnnemy == null ? null : nearestEnnemy.getCell());
            Function.getInstance().moveNearIfPossible(this.fight, this.fighter, nearestEnnemy);

            if(!this.coop) {
                List<Integer> cells = this.getGlyphCells();

                if (cells.contains(this.fighter.getCell().getId())) {
                    nearestEnnemy = Function.getInstance().getEnnemyWithDistance(this.fight, this.fighter, 0, 10, null);
                    if (!(this.tpIfPossibleKaskargo(this.fight, this.fighter, nearestEnnemy) == 0)) {
                        Function.getInstance().moveNearIfPossible(this.fight, this.fighter, nearestEnnemy);
                        this.coop = this.tpIfPossibleKaskargo(this.fight, this.fighter, nearestEnnemy) == 0;
                    } else {
                        this.coop = true;
                    }
                }
            }

            this.attackIfPossibleKaskargo(nearestEnnemy);
            this.addNext(this::decrementCount, 1000);
        } else {
            this.stop = true;
        }
    }

    private List<Integer> getGlyphCells() {
        List<Integer> cells = new ArrayList<>();
        cells.addAll(this.fight.getAllGlyphs()
                .stream().filter(glyph -> glyph != null && glyph.getCaster().getId() == this.fighter.getId())
                .map(glyph -> glyph.getCell().getId()).collect(Collectors.toList()));
        return cells;
    }

    private int tpIfPossibleKaskargo(Fight fight, Fighter fighter, Fighter target) {
        if (fight == null || fighter == null || target == null)
            return 0;

        int attack = fight.tryCastSpell(fighter, Function.getInstance().findSpell(fighter, 445), target.getCell().getId());
        return attack;
    }

    private int attackIfPossibleKaskargo(Fighter ennemy) {
        if (this.fight == null || this.fighter == null || ennemy == null) return 666;

        Spell.SortStats spellStat = Function.getInstance().findSpell(this.fighter, 949);

        List<Integer> cells = this.getGlyphCells();
        cells.add(ennemy.getCell().getId());

        GameCase bestCell = null;
        int temp = 0, dist = 3;

        if(PathFinding.getDistanceBetween(this.fight.getMap(), this.fighter.getCell().getId(), ennemy.getCell().getId()) <= 1) {
            bestCell = this.fight.getMap().getCase(PathFinding.getAvailableCellArround(this.fight, ennemy.getCell().getId(), null));
        } else {
            List<GameCase> path = PathFinding.getShortestPathBetween(this.fight.getMap(), this.fighter.getCell().getId(), ennemy.getCell().getId(), 3);
            for (GameCase cell : path) {
                if (cells.contains(cell.getId())) continue;

                temp = PathFinding.getDistanceBetweenTwoCase(this.fight.getMap(), this.fighter.getCell(), cell);
                if (temp < dist && !PathFinding.haveFighterOnThisCell(temp, this.fight, false)) {
                    bestCell = cell;
                }
            }

            if (bestCell == null) {
                char dir = PathFinding.getDirEntreDosCeldas(this.fight.getMap(), this.fighter.getCell().getId(), ennemy.getCell().getId());
                path = PathFinding.getCellsByDir(this.fight, this.fighter.getCell().getId(), dir, 3);
                if(path.size() == 0) return 10;
                bestCell = path.get(path.size() - 1);
            }
        }

        if(PathFinding.haveFighterOnThisCell(bestCell.getId(), this.fight, false))
            return 10;
        return fight.tryCastSpell(this.fighter, spellStat, bestCell.getId());
    }
}