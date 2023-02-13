package org.starloco.locos.fight.ia.util.newia;

import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractEasyIA;
import org.starloco.locos.fight.ia.util.newia.action.IAAction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Locos on 14/05/2018.
 */
public abstract class FighterMind {

    protected final AbstractEasyIA ia;
    protected final LinkedList<FighterCase> fightersCases = new LinkedList<>();

    protected LinkedList<IAAction> highPriorityActions, lowPriorityActions;

    public FighterMind(AbstractEasyIA ia) {
        this.ia = ia;
        this.highPriorityActions = new LinkedList<>();
        this.lowPriorityActions = new LinkedList<>();
    }

    public abstract void init();

    public IAAction executeActions(LinkedList<IAAction> actions) {
        if (!actions.isEmpty()) {
            IAAction action = actions.pollFirst();
            if(!action.execute())
                return null;
            return action;
        }
        return null;
    }

    public LinkedList<IAAction> getHighPriorityActions() {
        return highPriorityActions;
    }

    public LinkedList<IAAction> getLowPriorityActions() {
        return lowPriorityActions;
    }

    public Collection<Fighter> getEnemies(boolean withHide) {
        return ia.getFight().getTeam(ia.getFighter().getTeam() + 1 == 1 ? 2 : 1).values().stream().filter(f -> !f.isDead() && (withHide || !f.isHide())).collect(Collectors.toSet());
    }

    public Collection<Fighter> getFriends() {
        return ia.getFight().getTeam(ia.getFighter().getTeam() + 1).values().stream().filter(f -> !f.isDead() && !f.isHide()).collect(Collectors.toSet());
    }

    public Collection<Fighter> getInvocations() {
        return getEnemies(false).stream().filter(Fighter::isInvocation).collect(Collectors.toSet());
    }
}
