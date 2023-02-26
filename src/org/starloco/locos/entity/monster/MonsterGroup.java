package org.starloco.locos.entity.monster;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.entity.monster.boss.MaitreCorbac;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;

import java.util.*;

public class MonsterGroup {
    public final static MaitreCorbac MAITRE_CORBAC = new MaitreCorbac();

    private final int id;
    private int cellId;
    private int orientation = 2;
    private int align = -1;
    private short starBonus;
    private int aggroDistance = 0;
    private int subarea = -1;
    private boolean changeAgro = false;
    private boolean isFix = false;
    private boolean isExtraGroup = false;
    private Map<Integer, MonsterGrade> mobs = new HashMap<>();
    private String condition = "";
    private Timer condTimer;
    private ArrayList<GameObject> objects;

    public MonsterGroup(int Aid, int Aalign, ArrayList<MonsterGrade> possibles,
                        GameMap map, int cell, int fixSize, int minSize, int maxSize,
                        MonsterGrade extra) {

        id = Aid;
        align = Aalign;
        //D�termination du nombre de mob du groupe
        int rand = 0;
        int nbr = 0;
        //region Nombre de monstre
        if (fixSize > 0 && fixSize < 9) {
            nbr = fixSize;
        } else if (minSize != -1 && maxSize != -1 && maxSize != 0
                && (minSize < maxSize)) {
            if (minSize == 3 && maxSize == 8) {
                rand = Formulas.getRandomValue(0, 99);
                if (rand < 25) //3: 25%
                {
                    nbr = 3;
                } else if (rand < 48) //4:23%
                {
                    nbr = 4;
                } else if (rand < 51) //5:20%
                {
                    nbr = 5;
                } else if (rand < 85) //6:17%
                {
                    nbr = 6;
                } else if (rand < 95) //7:10%
                {
                    nbr = 7;
                } else
                //8:5%
                {
                    nbr = 8;
                }
            } else if (minSize == 1 && maxSize == 3) { // 21 - normalement tout astrub
                rand = Formulas.getRandomValue(0, 99);
                if (rand < 40) //1: 40%
                {
                    nbr = 1;
                } else if (rand < 75)//2: 35%
                {
                    nbr = 2;
                } else
                //3: 25%
                {
                    nbr = 3;
                }
            } else if (minSize == 1 && maxSize == 5) {
                rand = Formulas.getRandomValue(0, 99);
                if (rand < 30) //3: 30%
                {
                    nbr = 1;
                } else if (rand < 53) //4:23%
                {
                    nbr = 2;
                } else if (rand < 73) //5:20%
                {
                    nbr = 3;
                } else if (rand < 90) //6:17%
                {
                    nbr = 4;
                } else
                //8:10%
                {
                    nbr = 5;
                }
            } else if (minSize == 1 && maxSize == 4) {
                rand = Formulas.getRandomValue(0, 99);
                if (rand < 35) //3: 35%
                {
                    nbr = 1;
                } else if (rand < 61) //4:26%
                {
                    nbr = 2;
                } else if (rand < 82) //5:21%
                {
                    nbr = 3;
                } else
                //8:18%
                {
                    nbr = 4;
                }
            } else {
                nbr = Formulas.getRandomValue(minSize, maxSize);
            }
        } else if (minSize == -1) {
            switch (maxSize) {
                case 0:
                    return;
                case 1:
                    nbr = 1;
                    break;
                case 2:
                    nbr = Formulas.getRandomValue(1, 2); //1:50%	2:50%
                    break;
                case 3:
                    nbr = Formulas.getRandomValue(1, 3); //1:33.3334%	2:33.3334%	3:33.3334%
                    break;
                case 4:
                    rand = Formulas.getRandomValue(0, 99);
                    if (rand < 22) //1:22%
                        nbr = 1;
                    else if (rand < 48) //2:26%
                        nbr = 2;
                    else if (rand < 74) //3:26%
                        nbr = 3;
                    else
                        //4:26%
                        nbr = 4;
                    break;
                case 5:
                    rand = Formulas.getRandomValue(0, 99);
                    if (rand < 15) //1:15%
                        nbr = 1;
                    else if (rand < 35) //2:20%
                        nbr = 2;
                    else if (rand < 60) //3:25%
                        nbr = 3;
                    else if (rand < 85) //4:25%
                        nbr = 4;
                    else
                        //5:15%
                        nbr = 5;
                    break;
                case 6:
                    rand = Formulas.getRandomValue(0, 99);
                    if (rand < 10) //1:10%
                        nbr = 1;
                    else if (rand < 25) //2:15%
                        nbr = 2;
                    else if (rand < 45) //3:20%
                        nbr = 3;
                    else if (rand < 65) //4:20%
                        nbr = 4;
                    else if (rand < 85) //5:20%
                        nbr = 5;
                    else
                        //6:15%
                        nbr = 6;
                    break;
                case 7:
                    rand = Formulas.getRandomValue(0, 99);
                    if (rand < 9) //1:9%
                        nbr = 1;
                    else if (rand < 20) //2:11%
                        nbr = 2;
                    else if (rand < 35) //3:15%
                        nbr = 3;
                    else if (rand < 55) //4:20%
                        nbr = 4;
                    else if (rand < 75) //5:20%
                        nbr = 5;
                    else if (rand < 91) //6:16%
                        nbr = 6;
                    else
                        //7:9%
                        nbr = 7;
                    break;
                default:
                    rand = Formulas.getRandomValue(0, 99);
                    if (rand < 9) //1:9%
                        nbr = 1;
                    else if (rand < 20) //2:11%
                        nbr = 2;
                    else if (rand < 33) //3:13%
                        nbr = 3;
                    else if (rand < 50) //4:17%
                        nbr = 4;
                    else if (rand < 67) //5:17%
                        nbr = 5;
                    else if (rand < 80) //6:13%
                        nbr = 6;
                    else if (rand < 91) //7:11%
                        nbr = 7;
                    else
                        //8:9%
                        nbr = 8;
                    break;
            }
        } else {
            switch (minSize) {
                case 1:
                    rand = Formulas.getRandomValue(1, 8);
                    switch (rand) {
                        case 1:
                            nbr = 1;
                            break;
                        case 2:
                            nbr = 2;
                            break;
                        case 3:
                            nbr = 3;
                            break;
                        case 4:
                            nbr = 4;
                            break;
                        case 5:
                            nbr = 5;
                            break;
                        case 6:
                            nbr = 6;
                            break;
                        case 7:
                            nbr = 7;
                            break;
                        case 8:
                            nbr = 8;
                            break;
                    }
                    break;
                case 2:
                    rand = Formulas.getRandomValue(2, 8);
                    switch (rand) {
                        case 2:
                            nbr = 2;
                            break;
                        case 3:
                            nbr = 3;
                            break;
                        case 4:
                            nbr = 4;
                            break;
                        case 5:
                            nbr = 5;
                            break;
                        case 6:
                            nbr = 6;
                            break;
                        case 7:
                            nbr = 7;
                            break;
                        case 8:
                            nbr = 8;
                            break;
                    }
                    break;
                case 3:
                    rand = Formulas.getRandomValue(3, 8);
                    switch (rand) {
                        case 3:
                            nbr = 3;
                            break;
                        case 4:
                            nbr = 4;
                            break;
                        case 5:
                            nbr = 5;
                            break;
                        case 6:
                            nbr = 6;
                            break;
                        case 7:
                            nbr = 7;
                            break;
                        case 8:
                            nbr = 8;
                            break;
                    }
                    break;
                case 4:
                    rand = Formulas.getRandomValue(4, 8);
                    switch (rand) {
                        case 4:
                            nbr = 4;
                            break;
                        case 5:
                            nbr = 5;
                            break;
                        case 6:
                            nbr = 6;
                            break;
                        case 7:
                            nbr = 7;
                            break;
                        case 8:
                            nbr = 8;
                            break;
                    }
                    break;
                case 5:
                    rand = Formulas.getRandomValue(5, 8);
                    switch (rand) {
                        case 5:
                            nbr = 5;
                            break;
                        case 6:
                            nbr = 6;
                            break;
                        case 7:
                            nbr = 7;
                            break;
                        case 8:
                            nbr = 8;
                            break;
                    }
                    break;
                case 6:
                    rand = Formulas.getRandomValue(6, 8);
                    switch (rand) {
                        case 6:
                            nbr = 6;
                            break;
                        case 7:
                            nbr = 7;
                            break;
                        case 8:
                            nbr = 8;
                            break;
                    }
                    break;
                case 7:
                    rand = Formulas.getRandomValue(7, 8);
                    switch (rand) {
                        case 7:
                            nbr = 7;
                            break;
                        case 8:
                            nbr = 8;
                            break;
                    }
                    break;
                case 8:
                    nbr = 8;
                    break;
                default:
                    rand = Formulas.getRandomValue(1, 8);
                    switch (rand) {
                        case 1:
                            nbr = 1;
                            break;
                        case 2:
                            nbr = 2;
                            break;
                        case 3:
                            nbr = 3;
                            break;
                        case 4:
                            nbr = 4;
                            break;
                        case 5:
                            nbr = 5;
                            break;
                        case 6:
                            nbr = 6;
                            break;
                        case 7:
                            nbr = 7;
                            break;
                        case 8:
                            nbr = 8;
                            break;
                    }
                    break;
            }
        }
        //endregion
        int guid = -1;
        boolean haveSameAlign = false;

        if (extra != null) {
            isExtraGroup = true;
            nbr--;
            this.mobs.put(guid, extra);
            guid--;
        }
        //On v�rifie qu'il existe des monstres de l'alignement demand� pour �viter les boucles infinies
        for (MonsterGrade mob : possibles)
            if (mob.getTemplate().getAlign() == this.align)
                haveSameAlign = true;
        if (!haveSameAlign)
            return;//S'il n'y en a pas
        for (int a = 0; a < nbr; a++) {
            MonsterGrade Mob = null;
            do {
                int random = Formulas.getRandomValue(0, possibles.size() - 1);//on prend un mob au hasard dans le tableau
                Mob = possibles.get(random).getCopy();
            } while (Mob.getTemplate().getAlign() != this.align);

            if (Mob.getTemplate().getAlign() != align)
                align = Mob.getTemplate().getAlign();
            this.mobs.put(guid, Mob);
            if (Mob.getTemplate().getAggroDistance() > this.aggroDistance)
                this.aggroDistance = Mob.getTemplate().getAggroDistance();
            guid--;
        }

        this.cellId = (cell == -1 ? map.getRandomFreeCellId() : cell);

        while (map.containsForbiddenCellSpawn(this.cellId))
            this.cellId = map.getRandomFreeCellId();
        if (this.cellId == 0)
            return;
        this.orientation = map != null && map.getId() == 11095 ? 3 : (Formulas.getRandomValue(0, 3) * 2) + 1;
        this.isFix = false;
        this.starBonus = 0;
    }

    public MonsterGroup(int id, GameMap map, int cellId, String groupData, String objects, short stars) {
        this.id = id;
        this.align = Constant.ALIGNEMENT_NEUTRE;
        this.cellId = cellId;
        this.isFix = false;
        this.orientation = (Formulas.getRandomValue(0, 3) * 2) + 1;
        this.starBonus = stars;

        int guid = -1;

        for (String data : groupData.split(";")) {
            if (data.equalsIgnoreCase(""))
                continue;
            String[] infos = data.split(",");

            try {
                int idMonster = Integer.parseInt(infos[0]);
                int min = Integer.parseInt(infos[1]);
                int max = Integer.parseInt(infos[2]);
                Monster m = World.world.getMonstre(idMonster);
                List<MonsterGrade> mgs = new ArrayList<MonsterGrade>();
                //on ajoute a la liste les grades possibles

                for (MonsterGrade MG : m.getGrades().values())
                    if (MG.getLevel() >= min && MG.getLevel() <= max)
                        mgs.add(MG);
                if (mgs.isEmpty())
                    continue;
                if (m.getAlign() != align)
                    align = m.getAlign();
                //On prend un grade au hasard entre 0 et size -1 parmis les mobs possibles
                this.mobs.put(guid, mgs.get(Formulas.getRandomValue(0, mgs.size() - 1)));
                if (m.getAggroDistance() > this.aggroDistance)
                    this.aggroDistance = m.getAggroDistance();
                guid--;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!objects.isEmpty()) {
            this.objects = new ArrayList<>();
            for (String value : objects.split(",")) {
                final GameObject gameObject = World.world.getGameObject(Integer.parseInt(value));
                if (gameObject != null)
                    this.objects.add(gameObject);
            }
        }
    }

    public MonsterGroup(int id, GameMap map, int cellId, String groupData) {
        this.id = id;
        this.align = Constant.ALIGNEMENT_NEUTRE;
        this.cellId = cellId;
        this.isFix = true;
        int guid = -1;
        boolean star = false;
        for (String data : groupData.split(";")) {
            if (data.equalsIgnoreCase(""))
                continue;
            String[] infos = data.split(",");
            try {
                int idMonster = Integer.parseInt(infos[0]);
                int min = Integer.parseInt(infos[1]);
                int max = Integer.parseInt(infos[2]);
                Monster m = World.world.getMonstre(idMonster);
                List<MonsterGrade> mgs = new ArrayList<MonsterGrade>();
                //on ajoute a la liste les grades possibles

                for (MonsterGrade MG : m.getGrades().values()) {
                    if (MG.getBaseXp() != 0)
                        star = true;
                    if (MG.getLevel() >= min && MG.getLevel() <= max)
                        mgs.add(MG);
                }
                if (mgs.isEmpty())
                    continue;
                if (m.getAlign() != align)
                    align = m.getAlign();
                //On prend un grade au hasard entre 0 et size -1 parmis les mobs possibles
                this.mobs.put(guid, mgs.get(Formulas.getRandomValue(0, mgs.size() - 1)));
                if (m.getAggroDistance() > this.aggroDistance)
                    this.aggroDistance = m.getAggroDistance();
                guid--;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.orientation = map != null && map.getId() == 11095 ? 3 : (Formulas.getRandomValue(0, 3) * 2) + 1;
        this.starBonus = (short) (star ? 0 : -1);
    }

    public void setSubArea(int sa) {
        this.subarea = sa;
    }

    public void changeAgro() {
        if (!changeAgro) {
            if (this.haveMineur()) {
                // 29 : sous-terrain
                // 96 : exploitation mini�re d'astrub
                // 31 : passage vers brakmar
                if (this.subarea != 29 && this.subarea != 96 && this.subarea != 31) {
                    this.removeAgro(118);
                }
            }
        }
        changeAgro = true;
    }

    public void removeAgro(int id) {
        this.aggroDistance = 0;
        for (Map.Entry<Integer, MonsterGrade> e : this.mobs.entrySet()) {
            MonsterGrade mb = e.getValue();
            if (mb.getTemplate().getId() != id) {
                if (mb.getTemplate().getAggroDistance() > this.aggroDistance) {
                    this.aggroDistance = mb.getTemplate().getAggroDistance();
                }
            }
        }
    }

    public boolean haveMineur() {
        for (Map.Entry<Integer, MonsterGrade> e : this.mobs.entrySet()) {
            MonsterGrade mb = e.getValue();
            if (mb.getTemplate().getId() == 118) {
                return true;
            }
        }
        return false;
    }

    public int getId() {
        return this.id;
    }

    public int getCellId() {
        return this.cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getAlignement() {
        return this.align;
    }

    public void addStarBonus() {
        if (this.getStarBonus() >= 150) {
            this.starBonus = 150;
        } else {
            this.starBonus += 5;
        }
    }

    public int getStarBonus() {
        return this.starBonus == -1 ? 0 : this.starBonus;
    }

    public void setStarBonus(short starBonus) {
        this.starBonus = starBonus;
    }

    public int getAggroDistance() {
        return this.aggroDistance;
    }

    public boolean isFix() {
        return this.isFix;
    }

    public void setIsFix(boolean isFix) {
        this.isFix = isFix;
    }

    public boolean getIsExtraGroup() {
        return this.isExtraGroup;
    }

    public Map<Integer, MonsterGrade> getMobs() {
        return this.mobs;
    }

    public void setMobs(Map<Integer, MonsterGrade> mobs) {
        this.mobs = mobs;
    }

    public MonsterGrade getMobGradeById(int id) {
        return this.mobs.get(id);
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void startCondTimer() {
        this.condTimer = new Timer();
        condTimer.schedule(new TimerTask() {
            public void run() {
                condition = "";
            }
        }, 60000 * 10);
    }

    public void stopConditionTimer() {
        try {
            this.condTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<GameObject> getObjects() {
        if (this.objects == null && Config.modeHeroic)
            this.objects = new ArrayList<>();
        else if (!Config.modeHeroic)
            return new ArrayList<>();
        return objects;
    }

    public String parseGM() {
        StringBuilder mobIDs = new StringBuilder();
        StringBuilder mobGFX = new StringBuilder();
        StringBuilder mobLevels = new StringBuilder();
        StringBuilder colors = new StringBuilder();
        StringBuilder toreturn = new StringBuilder();

        boolean isFirst = true;
        if (this.mobs.isEmpty())
            return "";

        for (Map.Entry<Integer, MonsterGrade> entry : this.mobs.entrySet()) {
            if (!isFirst) {
                mobIDs.append(",");
                mobGFX.append(",");
                mobLevels.append(",");
            }
            mobIDs.append(entry.getValue().getTemplate().getId());
            mobGFX.append(entry.getValue().getTemplate().getGfxId()).append("^").append(entry.getValue().getSize());
            mobLevels.append(entry.getValue().getLevel());
            colors.append(entry.getValue().getTemplate().getColors()).append(";0,0,0,0;");

            isFirst = false;
        }
        toreturn.append("+").append(this.cellId).append(";").append(this.orientation).append(";");
        toreturn.append(getStarBonus());// bonus en pourcentage (�toile/20%) // Actuellement 1%/min
        toreturn.append(";").append(this.id).append(";").append(mobIDs).append(";-3;").append(mobGFX).append(";").append(mobLevels).append(";").append(colors);
        return toreturn.toString();
    }
}
