package org.starloco.locos.job;

import org.starloco.locos.common.Formulas;

import java.util.ArrayList;

public class JobConstant {

    //Jobs
    public static final int JOB_BUCHERON = 2;
    public static final int JOB_F_EPEE = 11;
    public static final int JOB_S_ARC = 13;
    public static final int JOB_F_MARTEAU = 14;
    public static final int JOB_CORDONIER = 15;
    public static final int JOB_BIJOUTIER = 16;
    public static final int JOB_F_DAGUE = 17;
    public static final int JOB_S_BATON = 18;
    public static final int JOB_S_BAGUETTE = 19;
    public static final int JOB_F_PELLE = 20;
    public static final int JOB_MINEUR = 24;
    public static final int JOB_BOULANGER = 25;
    public static final int JOB_ALCHIMISTE = 26;
    public static final int JOB_TAILLEUR = 27;
    public static final int JOB_PAYSAN = 28;
    public static final int JOB_F_HACHES = 31;
    public static final int JOB_PECHEUR = 36;
    public static final int JOB_CHASSEUR = 41;
    public static final int JOB_FM_DAGUE = 43;
    public static final int JOB_FM_EPEE = 44;
    public static final int JOB_FM_MARTEAU = 45;
    public static final int JOB_FM_PELLE = 46;
    public static final int JOB_FM_HACHES = 47;
    public static final int JOB_SM_ARC = 48;
    public static final int JOB_SM_BAGUETTE = 49;
    public static final int JOB_SM_BATON = 50;
    public static final int JOB_BOUCHER = 56;
    public static final int JOB_POISSONNIER = 58;
    public static final int JOB_F_BOUCLIER = 60;
    public static final int JOB_CORDOMAGE = 62;
    public static final int JOB_JOAILLOMAGE = 63;
    public static final int JOB_COSTUMAGE = 64;
    public static final int JOB_BRICOLEUR = 65;
    //INTERACTIVE OBJET
    public static final int IOBJECT_STATE_FULL = 1;
    public static final int IOBJECT_STATE_EMPTYING = 2;
    public static final int IOBJECT_STATE_EMPTY = 3;
    public static final int IOBJECT_STATE_EMPTY2 = 4;
    public static final int IOBJECT_STATE_FULLING = 5;

    //Action de M�tier {skillID,objetRecolt�,objSp�cial}
    public static final int[][] JOB_ACTION = {
            //Bucheron
            {101}, {6, 303}, {39, 473}, {40, 476}, {10, 460}, {141, 2357}, {139, 2358}, {37, 471}, {154, 7013}, {33, 461}, {41, 474}, {34, 449}, {174, 7925}, {155, 7016}, {38, 472}, {35, 470}, {158, 7014},
            //Mineur
            {48}, {32}, {24, 312}, {25, 441}, {26, 442}, {28, 443}, {56, 445}, {162, 7032}, {55, 444}, {29, 350}, {31, 446}, {30, 313}, {161, 7033},
            //P�cheur
            {133},
            //Rivi�re
            {124, 1782, 1844, 603},  // Petit poissons (riv)
            {125, 1844, 603, 1847, 1794}, // Poisson (mer)
            {126, 603, 1847, 1794, 1779}, // Gros poisson (riv)
            {127, 1847, 1794, 1779, 1801}, // Poisson g�ant (riv)
            //Mer
            {128, 598, 1757, 1750}, // Petit Poissons (mer)
            {129, 1757, 1805, 600}, // Poisson (mer)
            {130, 1805, 1750, 1784, 600}, // Gros poisson (mer)
            {131, 600, 1805, 602, 1784}, // Poisson g�ant (mer)
            //OTHER
            {136, 2187}, {140, 1759}, {140, 1799},
            //Alchi
            {23}, {68, 421}, {69, 428}, {71, 395}, {72, 380}, {73, 593}, {74, 594}, {160, 7059},
            //Paysan
            {122}, {47}, {45, 289}, {53, 400}, {57, 533}, {46, 401}, {50, 423}, {52, 532}, {159, 7018}, {58, 405}, {54, 425},
            //Boulanger
            {109}, {27},
            //Poissonier
            {135},
            //Boucher
            {134},
            //Chasseur
            {132},
            //Tailleur
            {64}, {123}, {63},
            //Bijoutier
            {11}, {12},
            //Cordonnier
            {13}, {14},
            //Forgeur Ep�e
            {145}, {20},
            //Forgeur Marteau
            {144}, {19},
            //Forgeur Dague
            {142}, {18},
            //Forgeur Pelle
            {146}, {21},
            //Forgeur Hache
            {65}, {143},
            //Forgemage de Hache
            {115},
            //Forgemage de dagues
            {1},
            //Forgemage de marteau
            {116},
            //Forgemage d'�p�e
            {113},
            //Forgemage Pelle
            {117},
            //SculpteMage baton
            {120},
            //Sculptemage de baguette
            {119},
            //Sculptemage d'arc
            {118},
            //Costumage
            {165}, {166}, {167},
            //Cordomage
            {163}, {164},
            //Joyaumage
            {169}, {168},
            //Bricoleur
            {171}, {182},
            //Sculpteur de Arc
            {15}, {149},
            //Sculpteur de Baton
            {17}, {147},
            //Sculpteur de Baguette
            {16}, {148},
            //Forgeur de bouclier
            {156},
            //F�e d'artifice
            {151},
            //Etabli moon
            {110},
            //Briseur de ressource
            {121},
            //Etabli a patate
            {22}

    };

    public static final int[][] JOB_PROTECTORS = //{protectorId, itemId}
            {{782, 472}, {684, 289}, {684, 2018}, {685, 400}, {685, 2032}, {686, 533}, {686, 1671}, {687, 401}, {687, 2021}, {688, 423}, {688, 2026}, {689, 532}, {689, 2029}, {690, 7018}, {691, 405}, {692, 425}, {692, 2035}, {693, 312}, {694, 441}, {695, 442}, {696, 443}, {697, 445}, {698, 444}, {699, 7032}, {700, 350}, {701, 446}, {702, 313}, {703, 7033}, {704, 421}, {705, 428}, {706, 395}, {707, 380}, {708, 593}, {709, 594}, {710, 7059}, {711, 303}, {712, 473}, {713, 476}, {714, 460}, {715, 2358}, {716, 2357}, {717, 471}, {718, 461}, {719, 7013}, {720, 7925}, {721, 474}, {722, 449}, {723, 7016}, {724, 470}, {725, 7014}, {726, 1782}, {726, 1790}, {727, 607}, {727, 1844}, {727, 1846}, {728, 603}, {729, 598}, {730, 1757}, {730, 1759}, {731, 1750}, {732, 1847}, {732, 1749}, {733, 1794}, {733, 1796}, {734, 1805}, {734, 1807}, {735, 600}, {735, 1799}, {736, 1779}, {736, 1792}, {737, 1784}, {737, 1788}, {738, 1801}, {738, 1803}, {739, 602}, {739, 1853}};

    public static int getTotalCaseByJobLevel(int lvl) {
        if (lvl < 10) return 2;
        if (lvl == 100) return 9;
        return (lvl / 20) + 3;
    }

    public static int getChanceForMaxCase(int lvl) {
        if(lvl == 100)
            return 99;
        if (lvl < 10)
            return 50;
        return 54 + ((lvl / 10) - 1) * 5;
    }

    public static boolean isJobAction(int a) {
        for (int[] aJOB_ACTION : JOB_ACTION)
            if (aJOB_ACTION[0] == a)
                return true;
        return false;
    }

    public static int getObjectByJobSkill(int skID) {
        for (int[] aJOB_ACTION : JOB_ACTION) {
            if (aJOB_ACTION[0] == skID) {
                if (aJOB_ACTION.length > 2) {
                    return aJOB_ACTION[Formulas.getRandomValue(1, aJOB_ACTION.length - 1)];
                } else if (aJOB_ACTION.length > 1)
                    return aJOB_ACTION[1];
            }
        }
        return -1;
    }

    public static int getChanceByNbrCaseByLvl(int lvl, int nbr) {
        if (nbr <= getTotalCaseByJobLevel(lvl) - 2)
            return 100;//99.999... normalement, mais osef
        return getChanceForMaxCase(lvl);
    }

    public static boolean isMageJob(int id) {
        return (id > 42 && id < 51) || (id > 61 && id < 65);
    }

    public static String actionMetier(int oficio) {
        switch (oficio) {
            case 62:
                return "163;164";
            case 63:
                return "169;168";
            case 64:
                return "165;166;167";
            case 45:
                return "116";
            case 46:
                return "117";
            case 67:
                return "115";
            case 43:
                return "1";
            case 44:
                return "113";
            case 48:
                return "118";
            case 49:
                return "119";
            case 50:
                return "120";
        }
        return "";
    }

    public static int getProtectorLvl(int lvl) {
        if (lvl < 40)
            return 10;
        if (lvl < 80)
            return 20;
        if (lvl < 120)
            return 30;
        if (lvl < 160)
            return 40;
        if (lvl < 200)
            return 50;
        return 50;
    }

    public static ArrayList<JobAction> getPosActionsToJob(int tID, int lvl) {
        ArrayList<JobAction> list = new ArrayList<>();
        int timeWin = lvl * 100, dropWin = lvl / 5, bonus = lvl == 100 ? 5 : 0, min = 1 + bonus;
        
        switch (tID) {
            case JOB_BIJOUTIER:
                //Faire Anneau
                list.add(new JobAction(11, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //Faire Amullette
                list.add(new JobAction(12, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_TAILLEUR:
                //Faire Sac
                list.add(new JobAction(64, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //Faire Cape
                list.add(new JobAction(123, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //Faire Chapeau
                list.add(new JobAction(63, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_F_BOUCLIER:
                //Forger Bouclier
                list.add(new JobAction(156, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_BRICOLEUR:
                //Faire clef
                list.add(new JobAction(171, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //Faire objet brico
                list.add(new JobAction(182, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_CORDONIER:
                //Faire botte
                list.add(new JobAction(13, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //Faire ceinture
                list.add(new JobAction(14, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_S_ARC:
                //Sculter Arc
                list.add(new JobAction(15, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //ReSculter Arc
                list.add(new JobAction(149, 3, 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_S_BATON:
                //Sculter Baton
                list.add(new JobAction(17, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //ReSculter Baton
                list.add(new JobAction(147, 3, 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_S_BAGUETTE:
                //Sculter Baguette
                list.add(new JobAction(16, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //ReSculter Baguette
                list.add(new JobAction(148, 3, 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_CORDOMAGE:
                //FM Bottes
                list.add(new JobAction(163, 3, 0, true, lvl, 0));
                //FM Ceinture
                list.add(new JobAction(164, 3, 0, true, lvl, 0));
                break;
            case JOB_JOAILLOMAGE:
                //FM Anneau
                list.add(new JobAction(169, 3, 0, true, lvl, 0));
                //FM  Amullette
                list.add(new JobAction(168, 3, 0, true, lvl, 0));
                break;
            case JOB_COSTUMAGE:
                //FM Chapeau
                list.add(new JobAction(165, 3, 0, true, lvl, 0));
                //FM Cape
                list.add(new JobAction(167, 3, 0, true, lvl, 0));
                //FM Sac
                list.add(new JobAction(166, 3, 0, true, lvl, 0));
                break;
            case JOB_F_EPEE:
                //Forger Ep�e
                list.add(new JobAction(20, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //Reforger Ep�e
                list.add(new JobAction(145, 3, 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_F_DAGUE:
                //Forger Dague
                list.add(new JobAction(142, 3, 0, true, getChanceForMaxCase(lvl), -1));
                //Reforger Dague
                list.add(new JobAction(18, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_F_MARTEAU:
                //Forger Marteau
                list.add(new JobAction(19, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //Reforger Marteau
                list.add(new JobAction(144, 3, 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_F_PELLE:
                //Forger Pelle
                list.add(new JobAction(21, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //Reforger Pelle
                list.add(new JobAction(146, 3, 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_F_HACHES:
                //Forger Hache
                list.add(new JobAction(65, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //Reforger Hache
                list.add(new JobAction(143, 3, 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_FM_HACHES:
                //Reforger une hache
                list.add(new JobAction(115, 3, 0, true, lvl, 0));
                break;
            case JOB_FM_DAGUE:
                //Reforger une dague
                list.add(new JobAction(1, 3, 0, true, lvl, 0));
                break;
            case JOB_FM_EPEE:
                //Reforger une �p�e
                list.add(new JobAction(113, 3, 0, true, lvl, 0));
                break;
            case JOB_FM_MARTEAU:
                //Reforger une marteau
                list.add(new JobAction(116, 3, 0, true, lvl, 0));
                break;
            case JOB_FM_PELLE:
                //Reforger une pelle
                list.add(new JobAction(117, 3, 0, true, lvl, 0));
                break;
            case JOB_SM_ARC:
                //Resculpter un arc
                list.add(new JobAction(118, 3, 0, true, lvl, 0));
                break;
            case JOB_SM_BATON:
                //Resculpter un baton
                list.add(new JobAction(120, 3, 0, true, lvl, 0));
                break;
            case JOB_SM_BAGUETTE:
                //Resculpter une baguette
                list.add(new JobAction(119, 3, 0, true, lvl, 0));
                break;
            case JOB_CHASSEUR:
                //Pr�parer
                list.add(new JobAction(132, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_BOUCHER:
                //Pr�parer une Viande
                list.add(new JobAction(134, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_POISSONNIER:
                //Preparer un Poisson
                list.add(new JobAction(135, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_BOULANGER:
                //Cuir le Pain
                list.add(new JobAction(27, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //Faire des Bonbons
                list.add(new JobAction(109, 3, 0, true, 100, -1));
                break;
            case JOB_MINEUR:
                if (lvl > 99) {
                    //Miner Dolomite
                    list.add(new JobAction(161, min, -18 + dropWin, false, 12000 - timeWin, 60));
                }
                if (lvl > 79) {
                    //Miner Or
                    list.add(new JobAction(30, min, -14 + dropWin, false, 12000 - timeWin, 55));
                }
                if (lvl > 69) {
                    //Miner Bauxite
                    list.add(new JobAction(31, min, -12 + dropWin, false, 12000 - timeWin, 50));
                }
                if (lvl > 59) {
                    //Miner Argent
                    list.add(new JobAction(29, min, -10 + dropWin, false, 12000 - timeWin, 40));
                }
                if (lvl > 49) {
                    //Miner Etain
                    list.add(new JobAction(55, min, -8 + dropWin, false, 12000 - timeWin, 35));
                    //Miner Silicate
                    list.add(new JobAction(162, min, -8 + dropWin, false, 12000 - timeWin, 35));
                }
                if (lvl > 39) {
                    //Miner Mangan�se
                    list.add(new JobAction(56, min, -6 + dropWin, false, 12000 - timeWin, 30));
                }
                if (lvl > 29) {
                    //Miner Kobalte
                    list.add(new JobAction(28, min, -4 + dropWin, false, 12000 - timeWin, 25));
                }
                if (lvl > 19) {
                    //Miner Bronze
                    list.add(new JobAction(26, min, -2 + dropWin, false, 12000 - timeWin, 20));
                }
                if (lvl > 9) {
                    //Miner Cuivre
                    list.add(new JobAction(25, min, dropWin, false, 12000 - timeWin, 15));
                }
                //Miner Fer
                list.add(new JobAction(24, min, 2 + dropWin, false, 12000 - timeWin, 10));
                //Fondre
                list.add(new JobAction(32, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //Polir
                list.add(new JobAction(48, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_PECHEUR:
                if (lvl > 74) {
                    //P�cher Poissons g�ants de mer
                    list.add(new JobAction(131, 0, 1, false, 12000 - timeWin, 35));
                }
                if (lvl > 69) {
                    //P�cher Poissons g�ants de rivi�re
                    list.add(new JobAction(127, 0, 1, false, 12000 - timeWin, 35));
                }
                if (lvl > 49) {
                    //P�cher Gros poissons de mers
                    list.add(new JobAction(130, 0, 1, false, 12000 - timeWin, 30));
                }
                if (lvl > 39) {
                    //P�cher Gros poissons de rivi�re
                    list.add(new JobAction(126, 0, 1, false, 12000 - timeWin, 25));
                }
                if (lvl > 19) {
                    //P�cher Poissons de mer
                    list.add(new JobAction(129, 0, 1, false, 12000 - timeWin, 20));
                }
                if (lvl > 9) {
                    //P�cher Poissons de rivi�re
                    list.add(new JobAction(125, 0, 1, false, 12000 - timeWin, 15));
                }
                //P�cher Ombre Etrange
                list.add(new JobAction(140, 0, 1, false, 12000 - timeWin, 50));
                //P�cher Pichon
                list.add(new JobAction(136, 1, 1, false, 12000 - timeWin, 5));
                //P�cher Petits poissons de rivi�re
                list.add(new JobAction(124, 0, 1, false, 12000 - timeWin, 10));
                //P�cher Petits poissons de mer
                list.add(new JobAction(128, 0, 1, false, 12000 - timeWin, 10));
                //Vider
                list.add(new JobAction(133, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_ALCHIMISTE:
                if (lvl > 49) {
                    //Cueillir Graine de Pandouille
                    list.add(new JobAction(160, min, -8 + dropWin, false, 12000 - timeWin, 35));
                    //Cueillir Edelweiss
                    list.add(new JobAction(74, min, -8 + dropWin, false, 12000 - timeWin, 35));
                }
                if (lvl > 39) {
                    //Cueillir Orchid�e
                    list.add(new JobAction(73, min, -6 + dropWin, false, 12000 - timeWin, 30));
                }
                if (lvl > 29) {
                    //Cueillir Menthe
                    list.add(new JobAction(72, min, -4 + dropWin, false, 12000 - timeWin, 25));
                }
                if (lvl > 19) {
                    //Cueillir Tr�fle
                    list.add(new JobAction(71, min, -2 + dropWin, false, 12000 - timeWin, 20));
                }
                if (lvl > 9) {
                    //Cueillir Chanvre
                    list.add(new JobAction(69, min, dropWin, false, 12000 - timeWin, 15));
                }
                //Cueillir Lin
                list.add(new JobAction(68, min, 2 + dropWin, false, 12000 - timeWin, 10));
                //Fabriquer une Potion
                list.add(new JobAction(23, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;
            case JOB_BUCHERON:
                if (lvl > 99) {
                    //Couper Bambou Sacr�
                    list.add(new JobAction(158, min, -18 + dropWin, false, 12000 - timeWin, 75));
                }
                if (lvl > 89) {
                    //Couper Orme
                    list.add(new JobAction(35, min, -16 + dropWin, false, 12000 - timeWin, 70));
                }
                if (lvl > 79) {
                    //Couper Charme
                    list.add(new JobAction(38, min, -14 + dropWin, false, 12000 - timeWin, 65));
                    //Couper Bambou Sombre
                    list.add(new JobAction(155, min, -14 + dropWin, false, 12000 - timeWin, 65));
                }
                if (lvl > 74) {
                    //Couper Kalyptus
                    list.add(new JobAction(174, min, -13 + dropWin, false, 12000 - timeWin, 55));
                }
                if (lvl > 69) {
                    //Couper Eb�ne
                    list.add(new JobAction(34, min, -12 + dropWin, false, 12000 - timeWin, 50));
                }
                if (lvl > 59) {
                    //Couper Merisier
                    list.add(new JobAction(41, min, -10 + dropWin, false, 12000 - timeWin, 45));
                }
                if (lvl > 49) {
                    //Couper If
                    list.add(new JobAction(33, min, -8 + dropWin, false, 12000 - timeWin, 40));
                    //Couper Bambou
                    list.add(new JobAction(154, min, -8 + dropWin, false, 12000 - timeWin, 40));
                }
                if (lvl > 39) {
                    //Couper Erable
                    list.add(new JobAction(37, min, -6 + dropWin, false, 12000 - timeWin, 35));
                }
                if (lvl > 34) {
                    //Couper Bombu
                    list.add(new JobAction(139, min, -5 + dropWin, false, 12000 - timeWin, 30));
                    //Couper Oliviolet
                    list.add(new JobAction(141, min, -5 + dropWin, false, 12000 - timeWin, 30));
                }
                if (lvl > 29) {
                    //Couper Ch�ne
                    list.add(new JobAction(10, min, -4 + dropWin, false, 12000 - timeWin, 25));
                }
                if (lvl > 19) {
                    //Couper Noyer
                    list.add(new JobAction(40, min, -2 + dropWin, false, 12000 - timeWin, 20));
                }
                if (lvl > 9) {
                    //Couper Ch�taignier
                    list.add(new JobAction(39, min, dropWin, false, 12000 - timeWin, 15));
                }
                //Couper Fr�ne
                list.add(new JobAction(6, min, 2 + dropWin, false, 12000 - timeWin, 10));
                //Scie
                list.add(new JobAction(101, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                break;

            case JOB_PAYSAN:
                if (lvl > 69) {
                    //Faucher Chanvre
                    list.add(new JobAction(54, min, -12 + dropWin, false, 12000 - timeWin, 45));
                }
                if (lvl > 59) {
                    //Faucher Malt
                    list.add(new JobAction(58, min, -10 + dropWin, false, 12000 - timeWin, 40));
                }
                if (lvl > 49) {
                    //Faucher Riz
                    list.add(new JobAction(159, min, -8 + dropWin, false, 12000 - timeWin, 35));
                    //Faucher Seigle
                    list.add(new JobAction(52, min, -8 + dropWin, false, 12000 - timeWin, 35));
                }
                if (lvl > 39) {
                    //Faucher Lin
                    list.add(new JobAction(50, min, -6 + dropWin, false, 12000 - timeWin, 30));
                }
                if (lvl > 29) {
                    //Faucher Houblon
                    list.add(new JobAction(46, min, -4 + dropWin, false, 12000 - timeWin, 25));
                }
                if (lvl > 19) {
                    //Faucher Avoine
                    list.add(new JobAction(57, min, -2 + dropWin, false, 12000 - timeWin, 20));
                }
                if (lvl > 9) {
                    //Faucher Orge
                    list.add(new JobAction(53, min, dropWin, false, 12000 - timeWin, 15));
                }
                //Faucher bl�
                list.add(new JobAction(45, min, 2 + dropWin, false, 12000 - timeWin, 10));
                //Moudre
                list.add(new JobAction(47, getTotalCaseByJobLevel(lvl), 0, true, getChanceForMaxCase(lvl), -1));
                //Egrener 100% 1 case tout le temps ?
                list.add(new JobAction(122, 1, 0, true, 100, 10));
                break;
        }
        return list;
    }

    public static int getDistCanne(int temp) {
        switch (temp) {
            case 8541://1 to 2
            case 6661:
            case 596:
                return 2;
            case 1866://1 to 3
                return 3;
            case 1865://1 to 4
            case 1864:
                return 4;
            case 1867://1 to 5
            case 2188:
                return 5;
            case 1863://1 to 6
            case 1862:
                return 6;
            case 1868://1 to 7
                return 7;
            case 1861://1 to 8
            case 1860:
                return 8;
            case 2366://1 to 9
                return 9;
        }
        return 0;
    }

    public static int getPoissonRare(int tID) {
        switch (tID) {
            case 598: // Greu
                return 1786;
            case 600: // Krala
                return 1799;
            case 602: // Requin
                return 1853;
            case 603: // Poisson Chaton
                return 1762;
            case 1750: // Poisson Pan�
                return 1754;
            case 1757: // Crabe Sourimi
                return 1759;
            case 1779: // Bar
                return 1779;
            case 1785: // Goujon
                return 1790;
            case 1784: // Raie
                return 1788;
            case 1794: // Carpe
                return 1796;
            case 1801: // Perche
                return 1803;
            case 1805: // Sardine
                return 1807;
            case 1844: // Truite
                return 1846;
            case 1847: // Brochet
                return 1849;
        }
        return -1;
    }
}