package org.starloco.locos.dynamic;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.entity.Collector;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;

import java.util.ArrayList;

public class FormuleOfficiel {

    public static long getXp(Object object, ArrayList<Fighter> winners,
                             long groupXp, byte nbonus, int star, int challenge, int lvlMax,
                             int lvlMin, int lvlLoosers, int lvlWinners) {
        if (lvlMin <= 0 || object == null)
            return 0;
        if (object instanceof Fighter) {
            Fighter fighter = (Fighter) object;
            Player player = fighter.getPlayer();

            if (winners.contains(fighter)) {
                if (lvlWinners <= 0)
                    return 0;

                double sagesse = fighter.getLvl() * 0.5 + fighter.getPlayer().getTotalStats(true)
                        .getEffect(Constant.STATS_ADD_SAGE), nvGrpMonster = ((double) lvlMax / (double) lvlMin),
                        bonus = 1.0, rapport = ((double) lvlLoosers / (double) lvlWinners);

                if (winners.size() == 1)
                    rapport = 0.6;
                else if (rapport == 0)
                    return 0;
                else if (rapport <= 1.1 && rapport >= 0.9)
                    rapport = 1;
                else {
                    if (rapport > 1)
                        rapport = 1 / rapport;
                    if (rapport < 0.01)
                        rapport = 0.01;
                }

                int sizeGroupe = 0;
                for (Fighter f : winners) {
                    if (f.getPlayer() != null && !f.isInvocation()
                            && !f.isMob() && !f.isCollector() && !f.isDouble())
                        sizeGroupe++;
                }
                if (sizeGroupe < 1)
                    return 0;
                if (sizeGroupe > 8)
                    sizeGroupe = 8;

                if (nbonus > 8)
                    nbonus = 8;
                switch (nbonus) {
                    case 0:
                        bonus = 0.5;
                        break;
                    case 1:
                        bonus = 0.5;
                        break;
                    case 2:
                        bonus = 2.1;
                        break;
                    case 3:
                        bonus = 3.2;
                        break;
                    case 4:
                        bonus = 4.3;
                        break;
                    case 5:
                        bonus = 5.4;
                        break;
                    case 6:
                        bonus = 6.5;
                        break;
                    case 7:
                        bonus = 7.8;
                        break;
                    case 8:
                        bonus = 9;
                        break;
                }
                if (nvGrpMonster == 0)
                    return 0;
                else if (nvGrpMonster < 3.0)
                    nvGrpMonster = 1;
                else
                    nvGrpMonster = 1 / nvGrpMonster;

                if (nvGrpMonster < 0)
                    nvGrpMonster = 0;
                else if (nvGrpMonster > 1)
                    nvGrpMonster = 1;

                long total = (long) (((1 + (sagesse / 100)) * (1 + (challenge / 100)) * (1 + (star / 100))
                        * (bonus + rapport) * (nvGrpMonster) * (groupXp / sizeGroupe))
                        * (player.getLevel() != 199 ? Config.rateXp : 1) * World.world.getConquestBonus(fighter.getPlayer()));

                if(Config.modeHeroic && player.getLevel() < player.getDeadLevel()) {
                    total *= 2;
                }
                if(player.getLevel() != 199 && player.getAccount().isSubscribeWithoutCondition()) {
                    long newTotal = (long) (total * 1.2);
                    player.sendMessage(player.getLang().trans("dynamic.formuleofficiel.exp", (newTotal - total)));
                    return newTotal;
                }
                return total;
            }
        } else if (object instanceof Collector) {
            Collector collector = (Collector) object;

            if (World.world.getGuild(collector.getGuildId()) == null)
                return 0;

            if (lvlWinners <= 0)
                return 0;

            double sagesse = World.world.getGuild(collector.getGuildId()).getLvl()
                    * 0.5
                    + World.world.getGuild(collector.getGuildId()).getStats(Constant.STATS_ADD_SAGE), nvGrpMonster = ((double) lvlMax / (double) lvlMin), bonus = 1.0, rapport = ((double) lvlLoosers / (double) lvlWinners);

            if (winners.size() == 1)
                rapport = 0.6;
            else if (rapport == 0)
                return 0;
            else if (rapport <= 1.1 && rapport >= 0.9)
                rapport = 1;
            else {
                if (rapport > 1)
                    rapport = 1 / rapport;
                if (rapport < 0.01)
                    rapport = 0.01;
            }

            int sizeGroupe = 0;
            for (Fighter f : winners) {
                if (f.getPlayer() != null && !f.isInvocation()
                        && !f.isMob() && !f.isCollector() && !f.isDouble())
                    sizeGroupe++;
            }
            if (sizeGroupe < 1)
                return 0;
            if (sizeGroupe > 8)
                sizeGroupe = 8;

            if (nbonus > 8)
                nbonus = 8;
            switch (nbonus) {
                case 0:
                    bonus = 0.5;
                    break;
                case 1:
                    bonus = 0.5;
                    break;
                case 2:
                    bonus = 2.1;
                    break;
                case 3:
                    bonus = 3.2;
                    break;
                case 4:
                    bonus = 4.3;
                    break;
                case 5:
                    bonus = 5.4;
                    break;
                case 6:
                    bonus = 6.5;
                    break;
                case 7:
                    bonus = 7.8;
                    break;
                case 8:
                    bonus = 9;
                    break;
            }
            if (nvGrpMonster == 0)
                return 0;
            else if (nvGrpMonster < 3.0)
                nvGrpMonster = 1;
            else
                nvGrpMonster = 1 / nvGrpMonster;

            if (nvGrpMonster < 0)
                nvGrpMonster = 0;
            else if (nvGrpMonster > 1)
                nvGrpMonster = 1;

            return (long) (((1 + ((sagesse + star + challenge) / 100))
                    * (bonus + rapport) * (nvGrpMonster) * (groupXp / sizeGroupe)) * Config.rateXp);
        }
        return 0;
    }
}