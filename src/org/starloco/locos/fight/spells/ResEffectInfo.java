package org.starloco.locos.fight.spells;

import org.starloco.locos.kernel.Constant;

public enum ResEffectInfo {

    ELEMENT_NULL(0,0,0,0,0),
    ELEMENT_NEUTRE(Constant.STATS_ADD_RP_NEU,Constant.STATS_ADD_R_NEU,Constant.STATS_ADD_RES_P,Constant.STATS_ADD_RP_PVP_NEU,Constant.STATS_ADD_R_PVP_NEU),
    ELEMENT_TERRE(Constant.STATS_ADD_RP_TER,Constant.STATS_ADD_R_TER,Constant.STATS_ADD_RES_P,Constant.STATS_ADD_RP_PVP_TER,Constant.STATS_ADD_R_PVP_TER),
    ELEMENT_EAU(Constant.STATS_ADD_RP_EAU,Constant.STATS_ADD_R_EAU,Constant.STATS_ADD_RES_M,Constant.STATS_ADD_RP_PVP_EAU,Constant.STATS_ADD_R_PVP_EAU),
    ELEMENT_FEU(Constant.STATS_ADD_RP_FEU,Constant.STATS_ADD_R_FEU,Constant.STATS_ADD_RES_M,Constant.STATS_ADD_RP_PVP_FEU,Constant.STATS_ADD_R_PVP_FEU),
    ELEMENT_AIR(Constant.STATS_ADD_RP_AIR,Constant.STATS_ADD_R_AIR,Constant.STATS_ADD_RES_M,Constant.STATS_ADD_RP_PVP_AIR,Constant.STATS_ADD_R_PVP_AIR);


    private final int percentElem;
    private final int fixedElem;
    private final int fixed;
    private final int percentElemPvP;
    private final int fixedElemPvP;

    ResEffectInfo(int percentElem, int fixedElem, int fixed, int percentElemPvP, int fixedElemPvP) {
        this.percentElem = percentElem;
        this.fixedElem = fixedElem;
        this.fixed = fixed;
        this.percentElemPvP = percentElemPvP;
        this.fixedElemPvP = fixedElemPvP;
    }

    public int getPercentElem() {
        return percentElem;
    }

    public int getFixedElem() {
        return fixedElem;
    }

    public int getFixed() {
        return fixed;
    }

    public int getPercentElemPvP() {
        return percentElemPvP;
    }

    public int getFixedElemPvP() {
        return fixedElemPvP;
    }

    public static ResEffectInfo forElement(int elementID) {
        switch (elementID) {
            case Constant.ELEMENT_NEUTRE:
                return ELEMENT_NEUTRE;
            case Constant.ELEMENT_AIR:
                return ELEMENT_AIR;
            case Constant.ELEMENT_EAU:
                return ELEMENT_EAU;
            case Constant.ELEMENT_FEU:
                return ELEMENT_FEU;
            case Constant.ELEMENT_TERRE:
                return ELEMENT_TERRE;
            default:
                return ELEMENT_NULL;
        }
    }
}