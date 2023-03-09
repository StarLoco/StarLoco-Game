package org.starloco.locos.common;

import org.starloco.locos.area.map.CellCacheImpl;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.guild.GuildMember;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class Formulas {

    public final static Random random = new Random();

    public static char[] shuffleCharArray(char[] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            char a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
        return ar;
    }

    public static int[] getRandomsInt(int[] table,int nb)
    {
        int[] randomInts = new int[nb];
        Random random = new Random();

        // Shuffle the array using Fisher-Yates algorithm
        for (int i = table.length - 1; i >= 1; i--) {
            int j = random.nextInt(i + 1);
            int temp = table[j];
            table[j] = table[i];
            table[i] = temp;
        }

        // Copy the first 7 elements of the shuffled array to the randomInts array
        System.arraycopy(table, 0, randomInts, 0, nb);
        return randomInts;
    }

    public static int countCell(int i) {
        if (i > 64)
            i = 64;
        return 2 * (i) * (i + 1);
    }

    public static int getRandomValue(int i1, int i2) {
        if (i2 < i1)
            return 0;
        return (random.nextInt((i2 - i1) + 1)) + i1;
    }

    public static int getMinJet(String jet) {
        int num = 0;
        try {
            int des = Integer.parseInt(jet.split("d")[0]);
            int add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);
            num = des + add;
            return num;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getMaxJet(String jet) {
        int num = 0;
        try {
            int des = Integer.parseInt(jet.split("d")[0]);
            int faces = Integer.parseInt(jet.split("d")[1].split("\\+")[0]);
            int add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);
            for (int a = 0; a < des; a++) {
                num += faces;
            }
            num += add;
            return num;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getRandomJet(Fighter caster, Fighter target, String jet)//1d5+6
    {
        if (target != null && target.hasBuff(782)) {
            return getMaxJet(jet);
        }
        if (caster != null && caster.hasBuff(781)) {
            return getMinJet(jet);
        }

        try {
            int num = 0;
            int des = Integer.parseInt(jet.split("d")[0]);
            int faces = Integer.parseInt(jet.split("d")[1].split("\\+")[0]);
            int add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);
            if (faces == 0 && add == 0) {
                num = getRandomValue(0, des);
            } else {
                for (int a = 0; a < des; a++) {
                    num += getRandomValue(1, faces);
                }
            }
            num += add;
            return num;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getMiddleJet(String jet)//1d5+6
    {
        try {
            int num = 0;
            int des = Integer.parseInt(jet.split("d")[0]);
            int faces = Integer.parseInt(jet.split("d")[1].split("\\+")[0]);
            int add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);
            num += ((1 + faces) / 2) * des;//on calcule moyenne
            num += add;
            return num;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getTacleChance(Fighter fight, Fighter fighter) {
        int agiTacleur = fight.getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
        int agiEnemi = fighter.getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
        int div = agiTacleur + agiEnemi + 50;
        if (div == 0)
            div = 1;
        int esquive = 300 * (agiTacleur + 25) / div - 100;
        return esquive;
    }

    public static int calculFinalHeal(Player caster, int jet) {
        int statC = caster.getTotalStats(false).getEffect(Constant.STATS_ADD_INTE);
        int soins = caster.getTotalStats(false).getEffect(Constant.STATS_ADD_SOIN);
        if (statC < 0)
            statC = 0;
        return (jet * (100 + statC) / 100) + soins;
    }

    public static int calculFinalHealCac(Fighter healer, int rank, boolean isCac) {
        int intel = healer.getTotalStats().getEffect(126);
        int heals = healer.getTotalStats().getEffect(178);
        if (intel < 0)
            intel = 0;
        float adic = 100;
        if (isCac)
            adic = 105;
        return (int) (rank * ((100.00 + intel) / adic) + heals / 2);
    }

    public static int calculXpWinCraft(int lvl, int numCase) {
        if (lvl == 100)
            return 0;
        switch (numCase) {
            case 1:
                if (lvl < 40)
                    return 1;
                return 0;
            case 2:
                if (lvl < 60)
                    return 10;
                return 0;
            case 3:
                if (lvl > 9 && lvl < 80)
                    return 25;
                return 0;
            case 4:
                if (lvl > 19)
                    return 50;
                return 0;
            case 5:
                if (lvl > 39)
                    return 100;
                return 0;
            case 6:
                if (lvl > 59)
                    return 250;
                return 0;
            case 7:
                if (lvl > 79)
                    return 500;
                return 0;
            case 8:
                if (lvl > 99)
                    return 1000;
                return 0;
        }
        return 0;
    }

    public static int calculXpWinFm(int lvl, int poid) {
        if (lvl <= 1) {
            if (poid <= 10)
                return 10;
            else if (poid <= 50)
                return 25;
            else
                return 50;
        }
        if (lvl <= 25) {
            if (poid <= 10)
                return 10;
            else
                return 50;
        } else if (lvl <= 50) {
            if (poid <= 1)
                return 10;
            if (poid <= 10)
                return 25;
            if (poid <= 50)
                return 50;
            else
                return 100;
        } else if (lvl <= 75) {
            if (poid <= 3)
                return 25;
            if (poid <= 10)
                return 50;
            if (poid <= 50)
                return 100;
            else
                return 250;
        } else if (lvl <= 100) {
            if (poid <= 3)
                return 50;
            if (poid <= 10)
                return 100;
            if (poid <= 50)
                return 250;
            else
                return 500;
        } else if (lvl <= 125) {
            if (poid <= 3)
                return 100;
            if (poid <= 10)
                return 250;
            if (poid <= 50)
                return 500;
            else
                return 1000;
        } else if (lvl <= 150) {
            if (poid <= 10)
                return 250;
            else
                return 1000;
        } else if (lvl <= 175) {
            if (poid <= 1)
                return 250;
            if (poid <= 10)
                return 500;
            else
                return 1000;
        } else {
            if (poid <= 1)
                return 500;
            else
                return 1000;
        }
    }

    public static int calculXpLooseCraft(int lvl, int numCase) {
        if (lvl == 100)
            return 0;
        switch (numCase) {
            case 1:
                if (lvl < 40)
                    return 1;
                return 0;
            case 2:
                if (lvl < 60)
                    return 5;
                return 0;
            case 3:
                if (lvl > 9 && lvl < 80)
                    return 12;
                return 0;
            case 4:
                if (lvl > 19)
                    return 25;
                return 0;
            case 5:
                if (lvl > 39)
                    return 50;
                return 0;
            case 6:
                if (lvl > 59)
                    return 125;
                return 0;
            case 7:
                if (lvl > 79)
                    return 250;
                return 0;
            case 8:
                if (lvl > 99)
                    return 500;
                return 0;
        }
        return 0;
    }



    public static int calculFinalDommage(Fight fight, Fighter caster, Fighter target, int statID, int jet, boolean isHeal, boolean isCaC, int spellid) {
        int value = calculFinalDommagee(fight, caster, target, statID, jet, isHeal, isCaC, spellid);

        return value > 0 ? value : 0;
    }

    public static int calculFinalDommagee(Fight fight, Fighter caster,
                                         Fighter target, int statID, int jet, boolean isHeal, boolean isCaC,
                                         int spellid) {
        float i = 0;//Bonus maitrise
        float j = 100; //Bonus de Classe
        float a = 1;//Calcul
        float num = 0;
        float statC = 0, domC = 0, perdomC = 0, resfT = 0, respT = 0, mulT = 1;
        int multiplier = 0;
        if(spellid != 450) { // Folie sanguinaire, on ne prend pas les dommages mais les résistances
            if (!isHeal) {
                domC = caster.getTotalStats().getEffect(Constant.STATS_ADD_DOMA);
                perdomC = caster.getTotalStats().getEffect(Constant.STATS_ADD_PERDOM);
                multiplier = caster.getTotalStats().getEffect(Constant.STATS_MULTIPLY_DOMMAGE);
                if (caster.hasBuff(114))
                    mulT = caster.getBuffValue(114);
            } else {
                domC = caster.getTotalStats().getEffect(Constant.STATS_ADD_SOIN);
            }
        }

        switch (statID) {
            case Constant.ELEMENT_NULL://Fixe
                statC = 0;
                resfT = 0;
                respT = 0;
                mulT = 1;
                break;
            case Constant.ELEMENT_NEUTRE://neutre
                if (spellid != 450)
                    statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_FORC);
                resfT = target.getTotalStats().getEffect(Constant.STATS_ADD_R_NEU);
                respT = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_NEU);
                if (caster.getPlayer() != null)//Si c'est un joueur
                {
                    respT += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_NEU);
                    resfT += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_NEU);
                }
                //on ajoute les dom Physique
                if(spellid != 450)
                    domC += caster.getTotalStats().getEffect(142);

                //Ajout de la resist Physique
                resfT += target.getTotalStats().getEffect(184);
                break;
            case Constant.ELEMENT_TERRE://force
                statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_FORC);
                resfT = target.getTotalStats().getEffect(Constant.STATS_ADD_R_TER);
                respT = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_TER);
                if (caster.getPlayer() != null)//Si c'est un joueur
                {
                    respT += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_TER);
                    resfT += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_TER);
                }
                //on ajout les dom Physique
                domC += caster.getTotalStats().getEffect(142);
                //Ajout de la resist Physique
                resfT += target.getTotalStats().getEffect(184);
                break;
            case Constant.ELEMENT_EAU://chance
                statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_CHAN);
                resfT = target.getTotalStats().getEffect(Constant.STATS_ADD_R_EAU);
                respT = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_EAU);
                if (caster.getPlayer() != null)//Si c'est un joueur
                {
                    respT += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_EAU);
                    resfT += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_EAU);
                }
                //Ajout de la resist Magique
                resfT += target.getTotalStats().getEffect(183);
                break;
            case Constant.ELEMENT_FEU://intell
                statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
                resfT = target.getTotalStats().getEffect(Constant.STATS_ADD_R_FEU);
                respT = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_FEU);
                if (caster.getPlayer() != null)//Si c'est un joueur
                {
                    respT += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_FEU);
                    resfT += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_FEU);
                }
                //Ajout de la resist Magique
                resfT += target.getTotalStats().getEffect(183);
                break;
            case Constant.ELEMENT_AIR://agilit�
                statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
                resfT = target.getTotalStats().getEffect(Constant.STATS_ADD_R_AIR);
                respT = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_AIR);
                if (caster.getPlayer() != null)//Si c'est un joueur
                {
                    respT += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_AIR);
                    resfT += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_AIR);
                }
                //Ajout de la resist Magique
                resfT += target.getTotalStats().getEffect(183);
                break;
        }
        //On bride la resistance a 50% si c'est un joueur
        if ((target.isCollector() || target.getMob() == null) && respT > 50) respT = 50;

        if (statC < 0)
            statC = 0;
        if (caster.getPlayer() != null && isCaC) {
            int ArmeType = caster.getPlayer().getObjetByPos(1).getTemplate().getType();
            j = Constant.getWeaponBonusByClass(ArmeType, caster.getPlayer().getClasse());
            if ((caster.getSpellValueBool(392)) && ArmeType == 2)//ARC
                i = caster.getMaitriseDmg(392);
            else if ((caster.getSpellValueBool(390)) && ArmeType == 4)//BATON
                i = caster.getMaitriseDmg(390);
            else if ((caster.getSpellValueBool(391)) && ArmeType == 6)//EPEE
                i = caster.getMaitriseDmg(391);
            else if ((caster.getSpellValueBool(393)) && ArmeType == 7)//MARTEAUX
                i = caster.getMaitriseDmg(393);
            else if ((caster.getSpellValueBool(394)) && ArmeType == 3)//BAGUETTE
                i = caster.getMaitriseDmg(394);
            else if ((caster.getSpellValueBool(395)) && ArmeType == 5)//DAGUES
                i = caster.getMaitriseDmg(395);
            else if ((caster.getSpellValueBool(396)) && ArmeType == 8)//PELLE
                i = caster.getMaitriseDmg(396);
            else if ((caster.getSpellValueBool(397)) && ArmeType == 19)//HACHE
                i = caster.getMaitriseDmg(397);
            a = (((100 + i) / 100) * (j / 100));
        }

        num = a * mulT * (jet * ((100 + statC + perdomC + (multiplier * 100)) / 100)) + domC;//d�gats bruts
        //Poisons
        if (spellid != -1) {
            switch (spellid) {
                case 66: // Poison Insidieux
                    statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
                    num = (jet * ((100 + statC + perdomC + (multiplier * 100)) / 100)) + domC;
                    int reduction = (int) ((num / (float) 100) * respT);
                    num -=reduction;

                    if (target.hasBuff(184)) {
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(184).getValue());
                        int value = (int) num - target.getBuff(184).getValue();
                        num = value > 0 ? value : 0 ;
                    }
                    return (int) num;

                case 164://FLèche empoisonnée
                case 71: // Piege empoissonee
                case 196: // Vent empoisonnee
                case 219: // Empoisonement
                    statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_FORC);
                    num = (jet * ((100 + statC + perdomC + (multiplier * 100)) / 100)) + domC;
                    reduction = (int) ((num / (float) 100) * respT);
                    num -=reduction;

                    if (target.hasBuff(184) && spellid != 71) {
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(184).getValue());
                        int value = (int) num - target.getBuff(184).getValue();
                        num = value > 0 ? value : 0 ;
                    }
                    return (int) num;

                case 181: // Tremblement
                case 200: // Poison paralysant
                    statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
                    num = (jet * ((100 + statC + perdomC + (multiplier * 100)) / 100)) + domC;
                    reduction = (int) ((num / (float) 100) * respT);
                    num -=reduction;

                    if (target.hasBuff(184)) {
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(184).getValue());
                        int value = (int) num - target.getBuff(184).getValue();
                        num = value > 0 ? value : 0 ;
                    }
                    return (int) num;
            }
        }

        //Renvoie
        if (caster.getId() != target.getId() && spellid != -1) {
            int returns = target.getTotalStatsLessBuff().getEffect(Constant.STATS_RETDOM);
            if (returns > 0 && !isHeal) {
                //returns = calculFinalDommage(fight, target, caster, statID, returns, false, false, -1);
                if (returns > num) returns = (int) num;
                num -= returns;

                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 107, "-1", target.getId() + "," + returns);

                if (returns > caster.getPdv()) returns = caster.getPdv();
                if (num < 1) num = 0;

                if (caster.hasBuff(105)) {
                    returns = returns - caster.getBuff(105).getValue();//Immu
                    if(returns <= 0)
                        returns = 0;
                    SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + caster.getBuff(105).getValue());
                }
                if(returns > 0) {
                    caster.removePdv(caster, returns);
                    if (caster.getPdv() <= returns)
                        fight.onFighterDie(caster, caster);
                }
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId() + "", caster.getId() + ",-" + returns);
            }
        }

        // Resistance % & armor
        int reduction = (int) ((num / (float) 100) * respT), armor = getArmorResist(target, statID);

        if (!isHeal) {
            num -= reduction; // Resistance %
            num -= armor; // Armor
            num -= resfT; // Resistance fix
            if (armor > 0)
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + armor);
        }

        if (num < 1) num = 0;
        if (target.getPlayer() != null) // 10% of dommages substract to the max pdv
            target.removePdvMax((int) Math.floor(num / 10));

        // The level of the mob help the damage
        if (caster.getPlayer() == null && !caster.isCollector())
            return (int) (num * Math.ceil((caster.getLvl() * 0.5) / 100));
        return (int) num;
    }

    public static int calculZaapCost(Player player, GameMap map1, GameMap map2) {
        return player.getAccount().isSubscribeWithoutCondition() ? 10 :
                10 * (Math.abs(map2.getX() - map1.getX()) + Math.abs(map2.getY() - map1.getY()) - 1);
    }

    private static int getArmorResist(Fighter target, int statID) {
        int armor = 0;
        for (SpellEffect SE : target.getBuffsByEffectID(265)) {
            Fighter fighter;

            switch (SE.getSpell()) {
                case 452:
                case 1://Armure incandescente
                    //Si pas element feu, on ignore l'armure
                    if (statID != Constant.ELEMENT_FEU)
                        continue;
                    //Les stats du f�ca sont prises en compte
                    fighter = SE.getCaster();
                    break;
                case 453:
                case 6://Armure Terrestre
                    //Si pas element terre/neutre, on ignore l'armure
                    if (statID != Constant.ELEMENT_TERRE
                            && statID != Constant.ELEMENT_NEUTRE)
                        continue;
                    //Les stats du f�ca sont prises en compte
                    fighter = SE.getCaster();
                    break;
                case 454:
                case 14://Armure Venteuse
                    //Si pas element air, on ignore l'armure
                    if (statID != Constant.ELEMENT_AIR)
                        continue;
                    //Les stats du f�ca sont prises en compte
                    fighter = SE.getCaster();
                    break;
                case 451:
                case 18://Armure aqueuse
                    //Si pas element eau, on ignore l'armure
                    if (statID != Constant.ELEMENT_EAU)
                        continue;
                    //Les stats du f�ca sont prises en compte
                    fighter = SE.getCaster();
                    break;

                default://Dans les autres cas on prend les stats de la cible et on ignore l'element de l'attaque
                    fighter = target;
                    break;
            }
            int intell = fighter.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
            int carac = 0;
            switch (statID) {
                case Constant.ELEMENT_AIR:
                    carac = fighter.getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
                    break;
                case Constant.ELEMENT_FEU:
                    carac = fighter.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
                    break;
                case Constant.ELEMENT_EAU:
                    carac = fighter.getTotalStats().getEffect(Constant.STATS_ADD_CHAN);
                    break;
                case Constant.ELEMENT_NEUTRE:
                case Constant.ELEMENT_TERRE:
                    carac = fighter.getTotalStats().getEffect(Constant.STATS_ADD_FORC);
                    break;
            }
            int value = SE.getValue();
            int a = value * (100 + intell / 2 + carac / 2)
                    / 100;
            armor += a;
        }
        for (SpellEffect SE : target.getBuffsByEffectID(105)) {
            int intell = target.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
            int carac = 0;
            switch (statID) {
                case Constant.ELEMENT_AIR:
                    carac = target.getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
                    break;
                case Constant.ELEMENT_FEU:
                    carac = target.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
                    break;
                case Constant.ELEMENT_EAU:
                    carac = target.getTotalStats().getEffect(Constant.STATS_ADD_CHAN);
                    break;
                case Constant.ELEMENT_NEUTRE:
                case Constant.ELEMENT_TERRE:
                    carac = target.getTotalStats().getEffect(Constant.STATS_ADD_FORC);
                    break;
            }
            int value = SE.getValue();
            int a = value * (100 + intell / 2 + carac / 2)
                    / 100;
            armor += a;
        }
        return armor;
    }

    public static long getGuildXpWin(Fighter perso, AtomicReference<Long> xpWin) {
        if (perso.getPlayer() == null)
            return 0;
        if (perso.getPlayer().getGuildMember() == null)
            return 0;

        GuildMember gm = perso.getPlayer().getGuildMember();

        double xp = (double) xpWin.get(),
                Lvl = perso.getLvl(),
                LvlGuild = perso.getPlayer().getGuild().getLvl(),
                pXpGive = (double) gm.getXpGive() / 100;

        double maxP = xp * pXpGive * 0.10; //Le maximum donn� � la guilde est 10% du montant pr�lev� sur l'xp du combat
        double diff = Math.abs(Lvl - LvlGuild); //Calcul l'�cart entre le niveau du personnage et le niveau de la guilde
        double toGuild;
        if (diff >= 70) {
            toGuild = maxP * 0.10; //Si l'�cart entre les deux level est de 70 ou plus, l'experience donn�e a la guilde est de 10% la valeur maximum de don
        } else if (diff >= 31 && diff <= 69) {
            toGuild = maxP - ((maxP * 0.10) * (Math.floor((diff + 30) / 10)));
        } else if (diff >= 10 && diff <= 30) {
            toGuild = maxP - ((maxP * 0.20) * (Math.floor(diff / 10)));
        } else
        //Si la diff�rence est [0,9]
        {
            toGuild = maxP;
        }
        xpWin.set((long) (xp - xp * pXpGive));
        if(toGuild > 1_000_000)
            toGuild = 1_486_215;
        return Math.round(toGuild);
    }

    public static long getMountXpWin(Fighter perso, AtomicReference<Long> xpWin) {
        if (perso.getPlayer() == null)
            return 0;
        if (perso.getPlayer().getMount() == null)
            return 0;

        int diff = Math.abs(perso.getLvl()
                - perso.getPlayer().getMount().getLevel());

        double coeff = 0;
        double xp = (double) xpWin.get();
        double pToMount = (double) perso.getPlayer().getMountXpGive() / 100 + 0.2;

        if (diff >= 0 && diff <= 9)
            coeff = 0.1;
        else if (diff >= 10 && diff <= 19)
            coeff = 0.08;
        else if (diff >= 20 && diff <= 29)
            coeff = 0.06;
        else if (diff >= 30 && diff <= 39)
            coeff = 0.04;
        else if (diff >= 40 && diff <= 49)
            coeff = 0.03;
        else if (diff >= 50 && diff <= 59)
            coeff = 0.02;
        else if (diff >= 60 && diff <= 69)
            coeff = 0.015;
        else
            coeff = 0.01;

        if (pToMount > 0.2)
            xpWin.set((long) (xp - (xp * (pToMount - 0.2))));

        return Math.round(xp * pToMount * coeff);
    }

    public static int getKamasWin(Fighter i, ArrayList<Fighter> winners,
                                  int maxk, int mink) {
        maxk++;
        int rkamas = (int) (Math.random() * (maxk - mink)) + mink;
        return Math.round(rkamas * Config.rateKamas);
    }

    public static int getKamasWinPerco(int maxk, int mink) {
        maxk++;
        int rkamas = (int) (Math.random() * (maxk - mink)) + mink;
        return Math.round(rkamas * Config.rateKamas);
    }

    public static Couple<Integer, Integer> decompPierreAme(GameObject toDecomp) {
        Couple<Integer, Integer> toReturn;
        String[] stats = toDecomp.parseStatsString().split("#");
        int lvlMax = Integer.parseInt(stats[3], 16);
        int chance = Integer.parseInt(stats[1], 16);
        toReturn = new Couple<Integer, Integer>(chance, lvlMax);

        return toReturn;
    }

    public static int totalCaptChance(int pierreChance, Player p) {
        int sortChance = 0;

        switch (p.getSortStatBySortIfHas(413).getLevel()) {
            case 1:
                sortChance = 1;
                break;
            case 2:
                sortChance = 3;
                break;
            case 3:
                sortChance = 6;
                break;
            case 4:
                sortChance = 10;
                break;
            case 5:
                sortChance = 15;
                break;
            case 6:
                sortChance = 25;
                break;
        }

        return sortChance + pierreChance;
    }

    public static int spellCost(int nb) {
        int total = 0;
        for (int i = 1; i < nb; i++) {
            total += i;
        }

        return total;
    }

    public static int getLoosEnergy(int lvl, boolean isAgression,
                                    boolean isPerco) {

        int returned = 5 * lvl;
        if (isAgression)
            returned *= (7 / 4);
        if (isPerco)
            returned *= (3 / 2);
        return returned;
    }

    public static int totalAppriChance(boolean Amande, boolean Rousse,
                                       boolean Doree, Player p) {
        int sortChance = 0;
        int ddChance = 0;
        switch (p.getSortStatBySortIfHas(414).getLevel()) {
            case 1:
                sortChance = 15;
                break;
            case 2:
                sortChance = 20;
                break;
            case 3:
                sortChance = 25;
                break;
            case 4:
                sortChance = 30;
                break;
            case 5:
                sortChance = 35;
                break;
            case 6:
                sortChance = 45;
                break;
        }
        if (Amande || Rousse)
            ddChance = 15;
        if (Doree)
            ddChance = 5;
        return sortChance + ddChance;
    }

    public static int getCouleur(boolean Amande, boolean Rousse, boolean Doree) {
        int Couleur = 0;
        if (Amande && !Rousse && !Doree)
            return 20;
        if (Rousse && !Amande && !Doree)
            return 10;
        if (Doree && !Amande && !Rousse)
            return 18;

        if (Amande && Rousse && !Doree) {
            int Chance = Formulas.getRandomValue(1, 2);
            if (Chance == 1)
                return 20;
            if (Chance == 2)
                return 10;
        }
        if (Amande && !Rousse && Doree) {
            int Chance = Formulas.getRandomValue(1, 2);
            if (Chance == 1)
                return 20;
            if (Chance == 2)
                return 18;
        }
        if (!Amande && Rousse && Doree) {
            int Chance = Formulas.getRandomValue(1, 2);
            if (Chance == 1)
                return 18;
            if (Chance == 2)
                return 10;
        }
        if (Amande && Rousse && Doree) {
            int Chance = Formulas.getRandomValue(1, 3);
            if (Chance == 1)
                return 20;
            if (Chance == 2)
                return 10;
            if (Chance == 3)
                return 18;
        }
        return Couleur;
    }

    public static int calculEnergieLooseForToogleMount(int pts) {
        if (pts <= 170)
            return 4;
        if (pts >= 171 && pts < 180)
            return 5;
        if (pts >= 180 && pts < 200)
            return 6;
        if (pts >= 200 && pts < 210)
            return 7;
        if (pts >= 210 && pts < 220)
            return 8;
        if (pts >= 220 && pts < 230)
            return 10;
        if (pts >= 230 && pts <= 240)
            return 12;
        return 10;
    }

    public static int getLvlDopeuls(int lvl)//Niveau du dopeul � combattre
    {
        if (lvl < 20)
            return 20;
        if (lvl > 19 && lvl < 40)
            return 40;
        if (lvl > 39 && lvl < 60)
            return 60;
        if (lvl > 59 && lvl < 80)
            return 80;
        if (lvl > 79 && lvl < 100)
            return 100;
        if (lvl > 99 && lvl < 120)
            return 120;
        if (lvl > 119 && lvl < 140)
            return 140;
        if (lvl > 139 && lvl < 160)
            return 160;
        if (lvl > 159 && lvl < 180)
            return 180;
        if (lvl > 180)
            return 200;
        return 200;
    }

    public static int calculChanceByElement(int lvlJob, int lvlObject,
                                            int lvlRune) {
        int K = 1;
        if (lvlRune == 1)
            K = 100;
        else if (lvlRune == 25)
            K = 175;
        else if (lvlRune == 50)
            K = 350;
        return lvlJob * 100 / (K + lvlObject);
    }

    public static ArrayList<Integer> chanceFM(int WeightTotalBase,
                                                        int WeightTotalBaseMin, int currentWeithTotal,
                                                        int currentWeightStats, int weight, int diff, float coef,
                                                        int maxStat, int minStat, int actualStat, float x,
                                                        boolean bonusRune, int statsAdd) {
        ArrayList<Integer> chances = new ArrayList<Integer>();
        float c = 1, m1 = (maxStat - (actualStat + statsAdd)), m2 = (maxStat - minStat);
        if ((1 - (m1 / m2)) > 1.0)
            c = (1 - ((1 - (m1 / m2)) / 2)) / 2;
        else if ((1 - (m1 / m2)) > 0.8)
            c = 1 - ((1 - (m1 / m2)) / 2);
        if (c < 0)
            c = 0;
        // la variable c reste � 1 si le jet ne depasse pas 80% sinon il diminue tr�s fortement. Si le jet d�passe 100% alors il diminue encore plus.

        int moyenne = (int) Math.floor(WeightTotalBase
                - ((WeightTotalBase - WeightTotalBaseMin) / 2));

        float mStat = ((float) moyenne / (float) currentWeithTotal); // Si l'item est un bon jet dans l'ensemble, diminue les chances sinon l'inverse.

        if (mStat > 1.2)
            mStat = 1.2F;
        float a = ((((((WeightTotalBase + diff) * coef) * mStat) * c) * x) * Config.rateFm);
        float b = (float) (Math.sqrt(currentWeithTotal + currentWeightStats) + weight);
        if (b < 1.0)
            b = 1.0F;

        int p1 = (int) Math.floor(a / b); // Succes critique
        int p2 = 0; // Succes neutre
        int p3 = 0; // Echec critique
        if (bonusRune)
            p1 += 20;
        if (p1 < 1) {
            p1 = 1;
            p2 = 0;
            p3 = 99;
        } else if (p1 > 100) {
            p1 = 66;
            p2 = 34;
        } else if (p1 > 66)
            p1 = 66;

        if (p2 == 0 && p3 == 0) {
            p2 = (int) Math.floor(a
                    / (Math.sqrt(currentWeithTotal + currentWeightStats)));
            if (p2 > (100 - p1))
                p2 = (100 - p1);
            if (p2 > 50)
                p2 = 50;
        }
        chances.add(0, p1);
        chances.add(1, p2);
        chances.add(2, p3);
        return chances;
    }

    public static String convertToDate(long time) {
        String hexDate = "#";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = formatter.format(time);

        String[] split = date.split("\\s");

        String[] split0 = split[0].split("-");
        hexDate += Integer.toHexString(Integer.parseInt(split0[0])) + "#";
        int mois = Integer.parseInt(split0[1]) - 1;
        int jour = Integer.parseInt(split0[2]);
        hexDate += Integer.toHexString(Integer.parseInt((mois < 10 ? "0" + mois : mois)
                + "" + (jour < 10 ? "0" + jour : jour)))
                + "#";

        String[] split1 = split[1].split(":");
        String heure = split1[0] + split1[1];
        hexDate += Integer.toHexString(Integer.parseInt(heure));
        return hexDate;
    }

    public static int getXpStalk(int lvl) {
        switch (lvl) {
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
                return 65000;
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
                return 90000;
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
                return 120000;
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
                return 160000;
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
                return 210000;
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
                return 270000;
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
                return 350000;
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
                return 440000;
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
                return 540000;
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
                return 650000;
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
                return 760000;
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
                return 880000;
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
                return 1000000;
            case 165:
            case 166:
            case 167:
            case 168:
            case 169:
                return 1130000;
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
                return 1300000;
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
                return 1500000;
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
                return 1700000;
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
                return 2000000;
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
                return 2500000;
            case 195:
            case 196:
            case 197:
            case 198:
            case 199:
            case 200:
                return 3000000;

        }
        return 65000;
    }

    public static String translateMsg(String msg) {
        String alpha = "a b c d e f g h i j k l n o p q r s t u v w x y z é è à ç & û â ê ô î ä ë ü ï ö";
        for (String i : alpha.split(" "))
            msg = msg.replace(i, "m");
        alpha = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z Ë Ü Ä Ï Ö Â Ê Û Î Ô";
        for (String i : alpha.split(" "))
            msg = msg.replace(i, "H");
        return msg;
    }

    //region Formulas fight honor
    public static int calculHonorWin(ArrayList<Fighter> winners, ArrayList<Fighter> loosers, Fighter current, boolean prism) {
        final Player player = current.getPlayer();
        if(player == null || winners == null ||loosers == null) return 0;
        if(prism && loosers.size() == 1 && loosers.get(0).getPrism() != null) return 0;

        double factor = Config.rateHonor * World.world.getConquestBonus(current.getPlayer());
        // [0] = winners, [1] loosers;
        int levelsTotalDivide[] = {countFightersLevel(winners), countFightersLevel(loosers)};
        int levelsTotal[] = {countFightersLevel(winners) * winners.size(), countFightersLevel(loosers) * loosers.size()};
        int gradesTotalDivide[] = {countFightersGrade(winners), countFightersGrade(loosers)};
        int diffLevels = levelsTotalDivide[0] - levelsTotalDivide[1], diffGrades = gradesTotalDivide[0] - gradesTotalDivide[1];

        // Condition for winners & loosers (the same)
        if(levelsTotalDivide[0] > levelsTotalDivide[1] + 20)
            return 0;

        if(winners.contains(current)) { // He win
            // Condition for the winners
            if(levelsTotal[0] - levelsTotal[1] > (20 * winners.size()))
                return 0;
            if(levelsTotalDivide[0] < levelsTotalDivide[1] - 20)
                diffLevels = -20;
            return (int) Math.round(((100 * factor) - diffLevels + (3 * - diffGrades) + 15 * loosers.size())) * (player.getAccount().isSubscribeWithoutCondition() ? 2 : 1) ;
        } else { // He loose
            // Variables for the loosers
            diffGrades = player.getGrade() - gradesTotalDivide[0];
            double multiplicator = 1;

            // Condition for the loosers
            if(levelsTotal[0] - levelsTotal[1] > (20 * loosers.size()))
                return 0;
            if(diffLevels <= 0) diffLevels = 1;
            if(diffGrades <= 0) diffGrades = 1;

            // Variable scalable
            switch(player.getGrade()) {
                case 3: multiplicator = 1.5; break;
                case 4: multiplicator = 2.0; break;
                case 5: multiplicator = 2.5; break;
                case 6: multiplicator = 3.0; break;
                case 7: multiplicator = 4.0; break;
                case 8: multiplicator = 6.0; break;
                case 9: multiplicator = 8.0; break;
                case 10: multiplicator = 12.0; break;
                /*case 3: multiplicator = 1.1; break;
                case 4: multiplicator = 1.2; break;
                case 5: multiplicator = 1.3; break;
                case 6: multiplicator = 1.5; break;
                case 7: multiplicator = 2.0; break;
                case 8: multiplicator = 3.0; break;
                case 9: multiplicator = 4.0; break;
                case 10: multiplicator = 5.0; break;*/
            }
            return (int) Math.round(((-100 * factor) - diffLevels - (3 * diffGrades) - 5 * winners.size()) * multiplicator);
        }
    }

    private static int countFightersLevel(ArrayList<Fighter> fighters) {
        int total = 0;
        for(Fighter fighter : fighters) if(fighter != null && fighter.getPlayer() != null) total += fighter.getLvl();
        return fighters.size() == 0 ? total : total / fighters.size();
    }

    private static int countFightersGrade(ArrayList<Fighter> fighters) {
        int total = 0;
        for(Fighter fighter : fighters) if(fighter != null && fighter.getPlayer() != null) total += fighter.getPlayer().getGrade();
        return total / fighters.size();
    }
    //endregion

    //region Formule esquive Pa/Pm
    public static int getPointsLost(char type, int value, Fighter caster, Fighter target) {
        float esquiveC = type == 'a' ? caster.getTotalStatsLessBuff().getEffect(Constant.STATS_ADD_AFLEE) : caster.getTotalStatsLessBuff().getEffect(Constant.STATS_ADD_MFLEE);
        float esquiveT = type == 'a' ? target.getTotalStats().getEffect(Constant.STATS_ADD_AFLEE) : target.getTotalStats().getEffect(Constant.STATS_ADD_MFLEE);
        float ptsMax = type == 'a' ? target.getTotalStatsLessBuff().getEffect(Constant.STATS_ADD_PA) : target.getTotalStatsLessBuff().getEffect(Constant.STATS_ADD_PM);
        if(target.isMob())
            ptsMax = type == 'a' ? target.getMob().getPa() : target.getMob().getPm();
        int loose = 0;

        for (int i = 0; i < value; i++) {
            float pts = (type == 'a' ? target.getPa() : target.getPm()) - loose;

            if (esquiveT <= 0) esquiveT = 1;
            if (esquiveC <= 0) esquiveC = 1;

            float result = Math.round((float) (pts / ptsMax * esquiveC / esquiveT * 0.5) * 100);

            /*
            pourcentage de chance de retirer un PA =
            PA ou PM restants de la cible / PA ou PM totaux de la cible * Retrait du lanceur / esquive de la cible.  1/2
             */
            int jet = getRandomValue(0, 100);

            if (result >= jet) loose++;
        }
        return loose;
    }
    //endregion

    //region Line of sight
    public static boolean checkLos(GameMap map, short castID, short targetID) {
        if(map == null || castID == targetID)
            return true;

        CellCacheImpl cache = map.getCellCache();

        if(cache == null)
            return false;
        // Only compute the coordinates once
        float curX = cache.getOrthX(castID);
        float curY = cache.getOrthY(castID);
        int dstX = cache.getOrthX(targetID);
        int dstY = cache.getOrthY(targetID);

        // Get the offset between the cells
        int offX = (dstX - (int)curX);
        int offY = (dstY - (int)curY);

        float steps = cache.getCellsDistance(castID, targetID);

        steps = Math.max(1,steps);
        curX += .5;
        curY += .5;

        // Raycast & losCheck
        for(int t=0; t < steps; t++, curX += offX/steps, curY += offY/steps) {
            int xFloored = (int) Math.floor(curX);
            int yFloored = (int) Math.floor(curY);
            short cellId = (short) cache.getOrthCellID(xFloored, yFloored);

            // Ignore blocking cell if we are between two cells
            if(curX == xFloored && curY == yFloored)
                continue;

            // We know the cell is busy cause the caster is on it
            // We also want to ignore the last cell cause the target blocks it anyway (But we should not reach it here anyway)
            if(cellId == targetID || cellId == castID)
                continue;

            GameCase cell = map.getCase(cellId);
            if(cell != null) {
                Fighter fighter = cell.getFirstFighter();
                if (!cell.isLoS() || (fighter != null && !fighter.isHide()))
                    return false;
            }
        }

        return true;
    }
    //endregion
}
