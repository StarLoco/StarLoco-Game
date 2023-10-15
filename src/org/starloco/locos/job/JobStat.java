package org.starloco.locos.job;

import org.starloco.locos.entity.map.InteractiveObject;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.data.game.ExperienceTables;
import org.starloco.locos.game.action.GameAction;
import org.starloco.locos.game.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JobStat {

    private int id;
    private Job template;
    private int lvl;
    private long xp;
    private ArrayList<JobAction> posActions = new ArrayList<>();
    private boolean isCheap = false;
    private boolean freeOnFails = false;
    private boolean noRessource = false;
    private JobAction curAction;
    private int slotsPublic, position;

    public JobStat(int id, Job tp, int lvl, long xp) {
        this.id = id;
        this.template = tp;
        this.lvl = lvl;
        this.xp = xp;
        this.posActions = JobConstant.getPosActionsToJob(tp.getId(), lvl);
    }

    public int getId() {
        return this.id;
    }

    public Job getTemplate() {
        return this.template;
    }

    public int get_lvl() {
        return this.lvl;
    }

    public long getXp() {
        return this.xp;
    }

    public int getSlotsPublic() {
        return this.slotsPublic;
    }

    public void setSlotsPublic(int slots) {
        this.slotsPublic = slots;
    }

    public int getPosition() {
        return this.position;
    }

    public JobAction getJobActionBySkill(int skill) {
        for (JobAction JA : this.posActions)
            if (JA.getId() == skill)
                return JA;
        return null;
    }

    public void addXp(Player P, long xp) {
        if(xp<0) throw new IllegalArgumentException("xp must be positive");
        if (this.lvl > 99)
            return;
        int exLvl = this.lvl;
        this.xp += xp;

        ExperienceTables.ExperienceTable xpTable = World.world.getExperiences().jobs;
        while (this.xp >= xpTable.maxXpAt(this.lvl) && this.lvl < xpTable.maxLevel())
            levelUp(P, false);

        if (this.lvl > exLvl && P.isOnline()) {
            ArrayList<JobStat> list = new ArrayList<>();
            list.add(this);

            SocketManager.GAME_SEND_JS_PACKET(P, list);
            SocketManager.GAME_SEND_JN_PACKET(P, this.template.getId(), this.lvl);
            SocketManager.GAME_SEND_STATS_PACKET(P);
            SocketManager.GAME_SEND_Ow_PACKET(P);
            SocketManager.GAME_SEND_JO_PACKET(P, list);
        }
    }

    public String getXpString(String s) {
        ExperienceTables.ExperienceTable xpTable = World.world.getExperiences().jobs;
        return xpTable.minXpAt(this.lvl) + s + this.xp + s + xpTable.maxXpAt(this.lvl);
    }

    public void levelUp(Player P, boolean send) {
        this.lvl++;
        this.posActions = JobConstant.getPosActionsToJob(this.template.getId(), this.lvl);

        if (send) {
            //on creer la listes des JobStats a envoyer (Seulement celle ci)
            List<JobStat> list = Collections.singletonList(this);
            SocketManager.GAME_SEND_JS_PACKET(P, list);
            SocketManager.GAME_SEND_STATS_PACKET(P);
            SocketManager.GAME_SEND_Ow_PACKET(P);
            SocketManager.GAME_SEND_JN_PACKET(P, this.template.getId(), this.lvl);
            SocketManager.GAME_SEND_JO_PACKET(P, list);
        }
    }

    public String parseJS() {
        StringBuilder str = new StringBuilder();
        str.append("|").append(this.template.getId()).append(";");
        boolean first = true;
        for (JobAction JA : this.posActions) {
            if (!first)
                str.append(",");
            else
                first = false;
            str.append(JA.getId()).append("~").append(JA.getMin()).append("~");
            if (JA.isCraft())
                str.append("0~0~").append(JA.getChance());
            else
                str.append(JA.getMax()).append("~0~").append(JA.getTime());
        }
        return str.toString();
    }

    public int getOptBinValue() {
        int nbr = 0;
        nbr += (this.isCheap ? 1 : 0);
        nbr += (this.freeOnFails ? 2 : 0);
        nbr += (this.noRessource ? 4 : 0);
        return nbr;
    }

    public void setOptBinValue(int bin) {
        this.isCheap = false;
        this.freeOnFails = false;
        this.noRessource = false;
        this.noRessource = (bin & 4) == 4;
        this.freeOnFails = (bin & 2) == 2;
        this.isCheap = (bin & 1) == 1;
    }

    public boolean isValidMapAction(int id) {
        for (JobAction JA : this.posActions)
            if (JA.getId() == id)
                return true;
        return false;
    }
}
