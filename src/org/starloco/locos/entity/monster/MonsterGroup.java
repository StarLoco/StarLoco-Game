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
    private int alignment;
    private short starBonus;
    private int aggroDistance = 0;
    private int subarea = -1;
    private boolean changeAgro = false, isFix = false;
    private Map<Integer, MonsterGrade> mobs = new HashMap<>();
    private String condition = "";
    private ArrayList<GameObject> objects;

    public MonsterGroup(int id, int alignment, ArrayList<MonsterGrade> possibles, GameMap map, int cell, int fixSize, MonsterGrade extra) {
        this.id = id;
        this.alignment = alignment;
        int groupSize = fixSize > 0 && fixSize < 9 ? fixSize : Formulas.nextGaussian(1, 8);
        int guid = -1;
        boolean haveSameAlign = false;

        if (extra != null) {
            groupSize--;
            this.mobs.put(guid, extra);
            guid--;
        }

        for (MonsterGrade mob : possibles) {
            if (mob.getTemplate().getAlign() == this.alignment) {
                haveSameAlign = true;
                break;
            }
        }
        if (!haveSameAlign) return;

        for (int a = 0; a < groupSize; a++) {
            MonsterGrade Mob;
            do {
                int random = Formulas.getRandomValue(0, possibles.size() - 1);
                Mob = possibles.get(random).getCopy();
            } while (Mob.getTemplate().getAlign() != this.alignment);

            if (Mob.getTemplate().getAlign() != this.alignment)
                this.alignment = Mob.getTemplate().getAlign();
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
        this.orientation = map.getId() == 11095 ? 3 : Formulas.getRandomValue(0, 3) * 2 + 1;
        this.isFix = false;
        this.starBonus = 0;
    }

    public MonsterGroup(int id, int cellId, String groupData, String objects, short stars) {
        this.id = id;
        this.alignment = Constant.ALIGNEMENT_NEUTRE;
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
                if (m.getAlign() != alignment)
                    alignment = m.getAlign();
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
        this.alignment = Constant.ALIGNEMENT_NEUTRE;
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
                if (m.getAlign() != alignment)
                    alignment = m.getAlign();
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
                // 96 : exploitation mini???re d'astrub
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
        return this.alignment;
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

    public Map<Integer, MonsterGrade> getMobs() {
        return this.mobs;
    }

    public void setMobs(Map<Integer, MonsterGrade> mobs) {
        this.mobs = mobs;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void startCondTimer() {
        new Timer().schedule(new TimerTask() {
            public void run() {
                condition = "";
            }
        }, 60000 * 10);
    }

    public ArrayList<GameObject> getObjects() {
        if (this.objects == null && Config.modeHeroic)
            this.objects = new ArrayList<>();
        else if (!Config.modeHeroic)
            return new ArrayList<>();
        return objects;
    }

    public String encodeGM() {
        final StringBuilder packet = new StringBuilder();

        if (!this.mobs.isEmpty()) {
            final StringBuilder ids = new StringBuilder(), gfx = new StringBuilder(), levels = new StringBuilder(), colors = new StringBuilder();
            boolean first = true;

            for (MonsterGrade monster : this.mobs.values()) {
                if (!first) {
                    ids.append(",");
                    gfx.append(",");
                    levels.append(",");
                }
                ids.append(monster.getTemplate().getId());
                gfx.append(monster.getTemplate().getGfxId()).append("^").append(monster.getSize());
                levels.append(monster.getLevel());
                colors.append(monster.getTemplate().getColors()).append(";0,0,0,0;");
                first = false;
            }
            packet.append("+").append(this.cellId).append(";").append(this.orientation).append(";");
            packet.append(getStarBonus());// bonus en pourcentage (???toile/20%) // Actuellement 1%/min
            packet.append(";").append(this.id).append(";").append(ids).append(";-3;").append(gfx).append(";").append(levels).append(";").append(colors);
        }

        return packet.toString();
    }
}
