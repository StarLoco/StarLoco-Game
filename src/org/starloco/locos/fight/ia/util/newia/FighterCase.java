package org.starloco.locos.fight.ia.util.newia;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.util.AStarPathFinding;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Locos on 28/04/2018.
 */
public class FighterCase {

    private final Fighter fighter;
    private boolean discovered = false;
    private List<GameCase> shortestPath;
    private final LinkedList<Spell.SortStats> sortedSpells;

    FighterCase(Fighter fighter, AStarPathFinding aStar, LinkedList<Spell.SortStats> sortedSpells) {
        this.fighter = fighter;
        if(aStar != null) this.shortestPath = aStar.getShortestPath();
        this.sortedSpells = sortedSpells;
    }

    public Fighter getFighter() {
        return fighter;
    }

    public GameCase getFighterCell() {
        if(fighter.isHide() && !discovered) {
            byte pm = (byte) (fighter.getPm());
            final List<GameCase> cells = Function.getInstance().getCellsAvailableAround(fighter, pm == 1, pm);
            if(!cells.isEmpty()) {
                int index = Formulas.random.nextInt(cells.size() - 1);
                if(index >= 0) {
                    GameCase cell = cells.get(index);
                    if (cell != null) {
                        if (cell.getId() == fighter.getCell().getId())
                            discovered = true;
                        return cell;
                    }
                }
            }

        }
        return fighter.getCell();
    }

    public void setShortestPath(List<GameCase> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public List<GameCase> getShortestPath() {
        return shortestPath;
    }

    public LinkedList<Spell.SortStats> getSortedSpells() {
        return sortedSpells;
    }
}
