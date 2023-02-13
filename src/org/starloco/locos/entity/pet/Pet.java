package org.starloco.locos.entity.pet;

import org.starloco.locos.common.Formulas;
import org.starloco.locos.kernel.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Pet {

    private int templateId;
    private int type;                            //0 ne mange rien, 1 mange des creatures, 2 mange des objets, 3 mange un groupe d'objet.
    private String gap;                            //En heure 5,72 si type = 2 ou 3
    private String statsUp;
    private int max;
    private int gain;
    private int deadtemplate;
    private int epo;
    private Map<Integer, ArrayList<Integer>> categ = new HashMap<>();    // si type 3 StatID|categID#categID;StatID2| ...
    private Map<Integer, ArrayList<Integer>> template = new HashMap<>();    // si type 2 StatID|templateId#templateId#;StatID2| ...
    private Map<Integer, ArrayList<Map<Integer, Integer>>> monster = new HashMap<>();    // si type 1 StatID|monsterID,qua#monsterID,qua;StatID2|monsterID,qua#monsterID,qua ...
    private String jet;

    public Pet(int Tid, int type, String gap, String statsUp, int max, int gain, int Dtemplate, int epo, String jet) {
        this.templateId = Tid;
        this.type = type;
        this.gap = gap;
        this.statsUp = statsUp;
        decompileStatsUpItem();
        this.max = max;
        this.gain = gain;
        this.deadtemplate = Dtemplate;
        this.epo = epo;
        this.jet = jet;
    }

    public int getTemplateId() {
        return this.templateId;
    }

    public int getType() {
        return this.type;
    }

    public String getGap() {
        return this.gap;
    }

    public String getStatsUp() {
        return this.statsUp;
    }

    public int getMax() {
        return this.max;
    }

    public int getGain() {
        return this.gain;
    }

    public int getDeadTemplate() {
        return this.deadtemplate;
    }

    public int getEpo() {
        return this.epo;
    }

    public Map<Integer, ArrayList<Map<Integer, Integer>>> getMonsters() {
        return this.monster;
    }

    public int getNumbMonster(int StatID, int monsterID) {
        for (Entry<Integer, ArrayList<Map<Integer, Integer>>> ID : this.monster.entrySet()) {
            if (ID.getKey() == StatID) {
                for (Map<Integer, Integer> entry : ID.getValue()) {
                    for (Entry<Integer, Integer> monsterEntry : entry.entrySet()) {
                        if (monsterEntry.getKey() == monsterID) {
                            return monsterEntry.getValue();
                        }
                    }
                }
            }
        }
        return 0;
    }

    public void decompileStatsUpItem() {
        if (this.type == 3 || this.type == 2) {
            if (this.statsUp.contains(";"))//Plusieurs stats
            {
                for (String cut : this.statsUp.split(";"))//On coupe b2|41#49#62 puis 70|63#64
                {
                    String[] cut2 = cut.split("\\|");
                    int statsID = Integer.parseInt(cut2[0], 16);
                    ArrayList<Integer> ar = new ArrayList<Integer>();

                    for (String categ : cut2[1].split("#")) {
                        int categID = Integer.parseInt(categ);
                        ar.add(categID);
                    }
                    if (this.type == 3)
                        this.categ.put(statsID, ar);
                    if (this.type == 2)
                        this.template.put(statsID, ar);
                }

            } else
            //Un seul stats
            {
                String[] cut2 = this.statsUp.split("\\|");//On coupe b2 puis 41#49#62
                int statsID = Integer.parseInt(cut2[0], 16);
                ArrayList<Integer> ar = new ArrayList<Integer>();
                for (String categ : cut2[1].split("#")) {
                    int categID = Integer.parseInt(categ);
                    ar.add(categID);
                }
                if (this.type == 3)
                    this.categ.put(statsID, ar);
                if (this.type == 2)
                    this.template.put(statsID, ar);
            }
        } else if (this.type == 1) //StatID|monsterID,qua#monsterID,qua;StatID2|monsterID,qua#monsterID,qua
        {
            if (this.statsUp.contains(";"))//Plusieurs stats
            {
                for (String cut : this.statsUp.split(";"))//On coupe
                {
                    String[] cut2 = cut.split("\\|");
                    int statsID = Integer.parseInt(cut2[0], 16);
                    ArrayList<Map<Integer, Integer>> ar = new ArrayList<Map<Integer, Integer>>();
                    for (String soustotal : cut2[1].split("#")) {
                        int monsterID = 0;
                        int qua = 0;
                        for (String Iqua : soustotal.split(",")) {
                            if (monsterID == 0) {
                                monsterID = Integer.parseInt(Iqua);
                            } else {
                                qua = Integer.parseInt(Iqua);
                                Map<Integer, Integer> Mqua = new HashMap<Integer, Integer>();
                                Mqua.put(monsterID, qua);
                                ar.add(Mqua);
                                this.monster.put(statsID, ar);
                                monsterID = 0;
                            }
                        }
                    }
                }
            } else
            //Un seul stats 8a|64,50#65,50#68,50#72,50#96,50#97,40#99,40#179,40#182,10#181,10#180,1
            {
                String[] cut2 = this.statsUp.split("\\|");//On coupe 8a puis 64,50#65,50#68,50#72,50#96,50#97,40#99,40#179,40#182,10#181,10#180,1
                int statsID = Integer.parseInt(cut2[0], 16);
                ArrayList<Map<Integer, Integer>> ar = new ArrayList<Map<Integer, Integer>>();
                for (String categ : cut2[1].split("#")) {
                    int monsterID = 0;
                    int qua = 0;
                    for (String Iqua : categ.split(",")) {
                        if (monsterID == 0) {
                            monsterID = Integer.parseInt(Iqua);
                        } else {
                            qua = Integer.parseInt(Iqua);
                            Map<Integer, Integer> Mqua = new HashMap<Integer, Integer>();
                            Mqua.put(monsterID, qua);
                            ar.add(Mqua);
                            this.monster.put(statsID, ar);
                        }
                    }
                }
            }
        }
    }

    public boolean canEat(int Tid, int categID, int monsterId) {
        if (this.type == 1) {
            for (Entry<Integer, ArrayList<Map<Integer, Integer>>> ID : this.monster.entrySet())
                for (Map<Integer, Integer> entry : ID.getValue())
                    for (Entry<Integer, Integer> monsterEntry : entry.entrySet())
                        if (monsterEntry.getKey() == monsterId)
                            return true;
            return false;
        } else if (this.type == 2) {
            for (Entry<Integer, ArrayList<Integer>> ID : this.template.entrySet())
                if (ID.getValue().contains(Tid))
                    return true;
            return false;
        } else if (this.type == 3) {
            for (Entry<Integer, ArrayList<Integer>> ID : this.categ.entrySet())
                if (ID.getValue().contains(categID))
                    return true;
            return false;
        } else {
            return false;
        }
    }

    public int statsIdByEat(int Tid, int categID, int monsterId) {
        if (this.type == 1) {
            for (Entry<Integer, ArrayList<Map<Integer, Integer>>> ID : this.monster.entrySet())
                for (Map<Integer, Integer> entry : ID.getValue())
                    for (Entry<Integer, Integer> monsterEntry : entry.entrySet())
                        if (monsterEntry.getKey() == monsterId)
                            return ID.getKey();
            return 0;
        } else if (this.type == 2) {
            for (Entry<Integer, ArrayList<Integer>> ID : this.template.entrySet())
                if (ID.getValue().contains(Tid))
                    return ID.getKey();
            return 0;
        } else if (this.type == 3) {
            for (Entry<Integer, ArrayList<Integer>> ID : this.categ.entrySet())
                if (ID.getValue().contains(categID))
                    return ID.getKey();
            return 0;
        } else {
            return 0;
        }
    }

    public Map<Integer, String> generateNewtxtStatsForPets() {
        Map<Integer, String> txtStat = new HashMap<Integer, String>();
        txtStat.put(Constant.STATS_PETS_PDV, "a");
        txtStat.put(Constant.STATS_PETS_DATE, "0");
        txtStat.put(Constant.STATS_PETS_POIDS, "0");
        return txtStat;
    }

    public String getJet() {
        if(!this.jet.contains("|"))
            return jet;
        String[] split = this.jet.split("\\|");
        return split[Formulas.getRandomValue(1, split.length) - 1];
    }
}