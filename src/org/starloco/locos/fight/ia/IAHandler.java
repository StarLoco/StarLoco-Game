package org.starloco.locos.fight.ia;

import org.starloco.locos.entity.monster.Monster.MobGrade;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.type.*;
import org.starloco.locos.fight.ia.type.boss.*;
import org.starloco.locos.fight.ia.type.invocations.*;
import org.starloco.locos.fight.ia.type.invocations.dopeuls.*;

/**
 * Created by Locos on 18/09/2015.
 */
public class IAHandler {

    public static void select(final Fight fight, final Fighter fighter) {
        IA ia = new Blank(fight, fighter);
        MobGrade mobGrade = fighter.getMob();

        if (mobGrade == null) {
            if (fighter.isDouble())
                ia = new Blocker(fight, fighter, (byte) 4);
            if (fighter.isCollector())
                ia = new IAPerco(fight, fighter, (byte) 7);
        } else if (mobGrade.getTemplate() == null) {
            ia.setStop(true);
            ia.endTurn();
        } else {
            //region select ia
            switch (mobGrade.getTemplate().getIa()) {
                case 1: // Random attack friend/enemy
                    ia = new IA1(fight, fighter, (byte) 4);
                    break;
                case 2: // Aggressif
                    ia = new IA2(fight, fighter, (byte) 7);
                    break;
                case 3:// Mi distance
                    ia = new IA3(fight, fighter, (byte) 7);
                    break;

                case 10: // Kralamour géant
                    ia = new IA10(fight, fighter, (byte) 4);
                    break;
                case 11:// Tentacule du Kralamour
                    ia = new IA11(fight, fighter, (byte) 2);
                    break;

                case 17://IA KIMBO
                    ia = new IA17(fight, fighter, (byte) 6);
                    break;
                case 18://Disciple
                    ia = new IA18(fight, fighter, (byte) 4);
                    break;
                case 20: //IA Kaskargo
                    ia = new IA20(fight, fighter, (byte) 2);
                    break;

                case 22: //IA Rasboul
                    ia = new IA22(fight, fighter, (byte) 4);
                    break;
                case 23: //IA Rasboul mineur
                    ia = new IA23(fight, fighter, (byte) 3);
                    break;

                //region Player invocations
                case 100: // Invocation du Chafer/Chaferfu lancier
                    ia = new Chafer(fight, fighter, (byte) 4);
                    break;
                case 102: // Lapino & Gonflabe & Sac animé
                    ia = new Lapino(fight, fighter, (byte) 4);
                    break;
                case 107: // Tonneau
                    ia = new Tonneau(fight, fighter, (byte) 4);
                    break;
                //endregion

                //region Dopeuls
                case 110: //Pandawa
                    ia = new Pandawa(fight, fighter, (byte) 5);
                    break;
                case 111: //Feca
                    ia = new Feca(fight, fighter, (byte) 4);
                    break;
                case 112: //Sacrieur
                    ia = new Sacrieur(fight, fighter, (byte) 4);
                    break;
                case 113: //Sadida
                    ia = new Sadida(fight, fighter, (byte) 4);
                    break;
                case 114: //Osamodas
                    ia = new Osamodas(fight, fighter, (byte) 4);
                    break;
                case 115: //Enutrof
                    ia = new Enutrof(fight, fighter, (byte) 5);
                    break;
                case 116: //Sram
                    ia = new Sram(fight, fighter, (byte) 4);
                    break;
                case 117: //Xélor
                    ia = new Xelor(fight, fighter, (byte) 4);
                    break;
                case 118: //Ecaflip
                    ia = new Ecaflip(fight, fighter, (byte) 4);
                    break;
                case 119: //Eniripsa
                    ia = new Eniripsa(fight, fighter, (byte) 4);
                    break;
                case 120: //Iop
                    ia = new Iop(fight, fighter, (byte) 4);
                    break;
                case 121: //Cra
                    ia = new Cra(fight, fighter, (byte) 4);
                    break;
                default:
                    ia = new Blank(fight, fighter);
                    break;
                //endregion
            }
            //endregion
        }

        final IA finalIA = ia;
        ia.addNext(finalIA::apply, 250);
    }
}
