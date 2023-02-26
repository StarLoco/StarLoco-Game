package org.starloco.locos.entity.monster;

import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Drop;

import java.util.*;

public class Monster {
    private final int id;
    private int gfxId;
    private int align;
    private String colors;
    private int ia;
    private int minKamas;
    private int maxKamas;
    private final Map<Integer, MonsterGrade> grades = new HashMap<>();
    private final ArrayList<Drop> drops = new ArrayList<>();
    private boolean capturable;
    private int aggroDistance = 0;

    public Monster(int id, int gfxId, int align, String colors,
                   String thisGrades, String thisSpells, String thisStats,
                   String thisStatsInfos, String thisPdvs, String thisPoints,
                   String thisInit, int minKamas, int maxKamas, String thisXp, int ia,
                   boolean capturable, int aggroDistance) {
        this.id = id;
        this.gfxId = gfxId;
        this.align = align;
        this.colors = colors;
        this.minKamas = minKamas;
        this.maxKamas = maxKamas;
        this.ia = ia;
        this.capturable = capturable;
        this.aggroDistance = aggroDistance;
        int G = 1;

        for (int n = 0; n < 12; n++) {
            try {
                //Grades
                String[] split = thisGrades.split("\\|");
                String grade = split[n];
                String[] infos = grade.split("@");
                int level = Integer.parseInt(infos[0]);
                String resists = infos[1];
                //Stats
                String stats = thisStats.split("\\|")[n];
                //Spells
                String spells = "";
                if (!thisSpells.equalsIgnoreCase("||||")
                        && !thisSpells.equalsIgnoreCase("")
                        && !thisSpells.equalsIgnoreCase("-1")) {
                    spells = thisSpells.split("\\|")[n];
                    if (spells.equals("-1"))
                        spells = "";
                }
                //PDVMax//init
                int pdvmax = 1;
                int init = 1;

                try {
                    pdvmax = Integer.parseInt(thisPdvs.split("\\|")[n]);
                    init = Integer.parseInt(thisInit.split("\\|")[n]);
                } catch (Exception e) {
                    World.world.logger.error("  > Error : Monster (id:"+ id +", grade: " + n + " : Life or initiative unreadable.", e);
                }
                //PA / PM
                int PA = 3;
                int PM = 3;
                int xp = 10;

                try {
                    String[] pts = thisPoints.split("\\|")[n].split(";");
                    try {
                        PA = Integer.parseInt(pts[0]);
                        PM = Integer.parseInt(pts[1]);
                        xp = Integer.parseInt(thisXp.split("\\|")[n]);
                    } catch (Exception e) {
                        World.world.logger.error("  > Error : Monster (id:"+ id +", grade: " + n + " : PA, PM or experience unreadable.", e);
                    }
                } catch (Exception e) {
                    World.world.logger.error("  > Error : Monster (id:"+ id +", grade: " + n + " : column 'points' unreadable.", e);
                }
                grades.put(G, new MonsterGrade(this, G, level, PA, PM, resists, stats, thisStatsInfos, spells, pdvmax, init, xp, n));
                G++;
            } catch (Exception e) {
                // ok, pour les dopeuls ...
                //TODO: Enlever toutes les erreurs
            }
        }
    }

    public void setInfos(int gfxId, int align, String colors,
                         String thisGrades, String thisSpells, String thisStats,
                         String thisStatsInfos, String thisPdvs, String thisPoints,
                         String thisInit, int minKamas, int maxKamas, String thisXp, int ia,
                         boolean capturable, int aggroDistance) {
        this.gfxId = gfxId;
        this.align = align;
        this.colors = colors;
        this.minKamas = minKamas;
        this.maxKamas = maxKamas;
        this.ia = ia;
        this.capturable = capturable;
        this.aggroDistance = aggroDistance;
        int G = 1;
        grades.clear();
        for (int n = 0; n < 12; n++) {
            try {
                //Grades
                String[] split = thisGrades.split("\\|");
                //if(split.length < n)
                String grade = split[n];
                String[] infos = grade.split("@");
                int level = Integer.parseInt(infos[0]);
                String resists = infos[1];
                //Stats
                String stats = thisStats.split("\\|")[n];
                //Spells
                String spells = thisSpells.split("\\|")[n];
                if (spells.equals("-1"))
                    spells = "";
                //PDVMax//init
                int pdvmax = 1;
                int init = 1;

                try {
                    pdvmax = Integer.parseInt(thisPdvs.split("\\|")[n]);
                    init = Integer.parseInt(thisInit.split("\\|")[n]);
                } catch (Exception e) {
                    World.world.logger.error("  > Error : Monster (id:"+ id +", grade: " + n + " : Life or initiative unreadable.", e);
                }
                //PA / PM
                int PA = 3;
                int PM = 3;
                int xp = 10;

                try {
                    String[] pts = thisPoints.split("\\|")[n].split(";");
                    try {
                        PA = Integer.parseInt(pts[0]);
                        PM = Integer.parseInt(pts[1]);
                        xp = Integer.parseInt(thisXp.split("\\|")[n]);
                    } catch (Exception e) {
                        World.world.logger.error("  > Error : Monster (id:"+ id +", grade: " + n + " : PA, PM or experience unreadable.", e);
                    }
                } catch (Exception e) {
                    World.world.logger.error("  > Error : Monster (id:"+ id +", grade: " + n + " : column 'points' unreadable.", e);
                }
                grades.put(G, new MonsterGrade(this, G, level, PA, PM, resists, stats, thisStatsInfos, spells, pdvmax, init, xp, n));
                G++;
            } catch (Exception e) {
                // ok pour les dopeuls
                //e.printStackTrace();
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public int getGfxId() {
        return this.gfxId;
    }

    public int getAlign() {
        return this.align;
    }

    public String getColors() {
        return this.colors;
    }

    public int getIa() {
        return this.ia;
    }

    public int getMinKamas() {
        return this.minKamas;
    }

    public int getMaxKamas() {
        return this.maxKamas;
    }

    public Map<Integer, MonsterGrade> getGrades() {
        return this.grades;
    }

    public void addDrop(Drop D) {
        this.drops.add(D);
    }

    public ArrayList<Drop> getDrops() {
        return this.drops;
    }

    public boolean isCapturable() {
        return this.capturable;
    }

    public int getAggroDistance() {
        return this.aggroDistance;
    }

    public MonsterGrade getGradeByLevel(int lvl) {
        if(this.getGrades() == null) return null;
        for (MonsterGrade grade : this.getGrades().values())
            if (grade != null && grade.getLevel() == lvl)
                return grade;
        return null;
    }

    public MonsterGrade getRandomGrade() {
        if(this.getGrades() == null) return null;
        int randomGrade = (int) (Math.random() * (6 - 1)) + 1, random = 1;

        for (MonsterGrade grade : this.getGrades().values()) {
            if (grade != null && random == randomGrade) return grade;
            else random++;
        }

        return null;
    }

}
