package org.starloco.locos.fight.ia.type.invocations.dopeuls;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.traps.Trap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Locos on 09/04/2018.
 */
public class Sram extends AbstractEasyIA {

    private List<Trap> traps = new ArrayList<>();

    public Sram(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void run() {
        switch (this.flag) {
            case 0: // Concentration de chakra
                if (get().tryCastSpell(fight, fighter, fighter, 62) == 0)
                    this.time = 1500;
                break;
            case 1: // Déplacement
                Fighter target = get().getNearestEnnemy(fight, fighter, true);
                if (get().moveNearIfPossible(fight, fighter, target))
                    this.time = 1500;
                break;

            case 2: // Fourvoiement
                List<GameCase> cells = get().getCellsAvailableAround(fighter, true, (byte) 0);
                boolean ok = false;
                for(GameCase cell : cells)
                    if(cell.getFirstFighter() != null && cell.getFirstFighter().getTeam() != fighter.getTeam())
                        ok = true;
                if(ok) {
                    if (get().tryCastSpell(fight, fighter, fighter, 68) == 0)
                        this.setNextParams(1, 3, 2000);
                } else {
                    if(fighter.getCurPm(fight) == 0) this.time = 500;
                    else this.setNextParams(0, 4, 200);
                }
                break;
            case 3: // Piège sournois
                target = get().getNearestEnnemy(fight, fighter, true);
                cells = get().getCellsAvailableAround(target, false, (byte) 1);
                List<GameCase> cellsCaster = get().getCellsAvailableAround(fighter, true, (byte) 0);
                cells.removeIf(cellsCaster::contains);
                cells.remove(target.getCell());

                if(!cells.isEmpty()) {
                    GameCase cell = cells.get(Formulas.getRandomValue(0, cells.size() - 1));
                    if (cell != null && fight.tryCastSpell(fighter, get().findSpell(fighter, 65), cell.getId()) == 0)
                        this.setNextParams(2, 2, 2500);
                    else {
                        get().moveFarIfPossible(fight, fighter);
                        time = 2000;
                    }
                } else {
                    get().moveFarIfPossible(fight, fighter);
                    time = 2000;
                }
                break;
        }
    }
}
