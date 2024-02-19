package org.starloco.locos.entity.monster;

import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.script.proxy.SMobGrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MonsterGrade {
    private final static int baseSize = 90;
    private final static int sizeBonusPerGrade = 5;
    private final SMobGrade scriptVal;

    private Monster template;
    private int grade;
    private int level;
    private int pdv;
    private int pdvMax;
    private int init;
    private int pa;
    private int pm;
    private int size;
    private int baseXp = 10;
    private ArrayList<SpellEffect> fightBuffs = new ArrayList<>();
    private final Map<Integer, Integer> stats;
    private Map<Integer, Spell.SortStats> spells = new HashMap<>();
    private ArrayList<Integer> statsInfos = new ArrayList<>();

    public MonsterGrade(Monster template, int grade, int level, int pa, int pm, String resists, String strStats, String statsInfos, String allSpells, int pdvMax, int aInit, int xp) {
        this.scriptVal = new SMobGrade(this);
        this.size = baseSize + grade * sizeBonusPerGrade;
        this.template = template;
        this.grade = grade;
        this.level = level;
        this.pdvMax = pdvMax;
        this.pdv = pdvMax;
        this.pa = pa;
        this.pm = pm;
        this.baseXp = xp;
        this.init = aInit;
        this.spells.clear();


        HashMap<Integer, Integer> stats = new HashMap<>();

        String[] resist = resists.split(";"), stat = strStats.split(","), statInfos = statsInfos.split(";");

        for (String str : statInfos)
            this.statsInfos.add(Integer.parseInt(str));

        try {
            if (resist.length > 3) {
                stats.put(Constant.STATS_ADD_RP_NEU, Integer.parseInt(resist[0]));
                stats.put(Constant.STATS_ADD_RP_TER, Integer.parseInt(resist[1]));
                stats.put(Constant.STATS_ADD_RP_FEU, Integer.parseInt(resist[2]));
                stats.put(Constant.STATS_ADD_RP_EAU, Integer.parseInt(resist[3]));
                stats.put(Constant.STATS_ADD_RP_AIR, Integer.parseInt(resist[4]));
                stats.put(Constant.STATS_ADD_ADODGE, Integer.parseInt(resist[5]));
                stats.put(Constant.STATS_ADD_MDODGE, Integer.parseInt(resist[6]));
            } else {
                String[] split = resist[0].split(",");
                stats.put(-1, Integer.parseInt(split[0]));
                stats.put(-100, Integer.parseInt(split[1]));
                stats.put(Constant.STATS_ADD_ADODGE, Integer.parseInt(resist[1]));
                stats.put(Constant.STATS_ADD_MDODGE, Integer.parseInt(resist[2]));
            }

            stats.put(Constant.STATS_ADD_PA, pa);
            stats.put(Constant.STATS_ADD_PM, pm);
            stats.put(Constant.STATS_ADD_VITA, pdvMax);
            stats.put(Constant.STATS_ADD_FORC, Integer.parseInt(stat[0]));
            stats.put(Constant.STATS_ADD_SAGE, Integer.parseInt(stat[1]));
            stats.put(Constant.STATS_ADD_INTE, Integer.parseInt(stat[2]));
            stats.put(Constant.STATS_ADD_CHAN, Integer.parseInt(stat[3]));
            stats.put(Constant.STATS_ADD_AGIL, Integer.parseInt(stat[4]));
            stats.put(Constant.STATS_ADD_DOMA, Integer.parseInt(statInfos[0]));
            stats.put(Constant.STATS_ADD_PERDOM, Integer.parseInt(statInfos[1]));
            stats.put(Constant.STATS_ADD_SOIN, Integer.parseInt(statInfos[2]));
            stats.put(Constant.STATS_SUMMON_COUNT, Integer.parseInt(statInfos[3]));
            if (resist.length > 5) {
                stats.put(Constant.STATS_ADD_SAGE, Integer.parseInt(resist[5]) * 3);
            }

            this.stats = stats;
        } catch (Exception e) {
            throw new RuntimeException("Monster (id:" + template.getId() + ", grade: " + grade + ") : reading stats failed.", e);
        }

        if (!allSpells.equalsIgnoreCase("")) {
            String[] spells = allSpells.split(";");

            for (String str : spells) {
                if (str.equals("")) continue;
                String[] spellInfo = str.split("@");
                int id = -1, lvl;

                try {
                    id = Integer.parseInt(spellInfo[0]);
                    lvl = Integer.parseInt(spellInfo[1]);
                } catch (Exception e) {
                    World.world.logger.error("  > Error : Monster (id:" + template.getId() + ", grade: " + grade + ", spell: " + id + ") : reading spell id/level failed.", e);
                    continue;
                }

                Spell spell = World.world.getSort_Legacy(id);
                if (spell != null) {
                    Spell.SortStats spellStats = spell.getStatsByLevel(lvl);
                    if (spellStats != null) this.spells.put(id, spellStats);
                }
            }
        }
    }

    private MonsterGrade(Monster template, int grade, int level, int pdv,
                         int pdvMax, int pa, int pm,
                         Map<Integer, Integer> stats,
                         ArrayList<Integer> statsInfos,
                         Map<Integer, Spell.SortStats> spells, int xp) {
        this.scriptVal = new SMobGrade(this);
        this.size = baseSize + grade * sizeBonusPerGrade;
        this.template = template;
        this.grade = grade;
        this.level = level;
        this.pdv = pdv;
        this.pdvMax = pdvMax;
        this.pa = pa;
        this.pm = pm;
        this.stats = stats;
        this.statsInfos = statsInfos;
        this.spells = spells;
        this.baseXp = xp;
    }

    public MonsterGrade getCopy() {
        Map<Integer, Integer> newStats = new HashMap<>(this.stats);
        return new MonsterGrade(this.template, this.grade, this.level, this.pdv, this.pdvMax, this.pa, this.pm, newStats, this.statsInfos, this.spells, this.baseXp);
    }

    public void refresh() {
        if (this.spells.isEmpty())
            return;

        final StringBuilder spells = new StringBuilder();
        for (Map.Entry<Integer, Spell.SortStats> entry : this.spells.entrySet()) {
            spells.append((spells.length() == 0) ? entry.getKey() + "," + entry.getValue().getLevel() : ";" + entry.getKey() + "," + entry.getValue().getLevel());
        }

        this.spells.clear();

        for (String split : spells.toString().split(";")) {
            int id = Integer.parseInt(split.split(",")[0]);
            this.spells.put(id, World.world.getSort_Legacy(id).getStatsByLevel(Integer.parseInt(split.split(",")[1])));
        }
    }

    public int getSize() {
        return this.size;
    }

    public Monster getTemplate() {
        return this.template;
    }

    public int getGrade() {
        return this.grade;
    }

    public int getLevel() {
        return this.level;
    }

    public int getPdv() {
        return this.pdv;
    }

    public void setPdv(int pdv) {
        this.pdv = pdv;
    }

    public int getInit() {
        int fact = 4;
        int maxPdv = pdvMax;
        int curPdv = pdv;
        double coef = maxPdv / fact;

        coef += getStats().getEffect(Constant.STATS_ADD_INIT);
        coef += getStats().getEffect(Constant.STATS_ADD_AGIL);
        coef += getStats().getEffect(Constant.STATS_ADD_CHAN);
        coef += getStats().getEffect(Constant.STATS_ADD_INTE);
        coef += getStats().getEffect(Constant.STATS_ADD_FORC);

        int init = 1;
        if (maxPdv != 0)
            init = (int) (coef * ((double) curPdv / (double) maxPdv));
        if (init < 0)
            init = 0;
        return init + this.init;
    }

    public int getPa() {
        return this.pa;
    }

    public int getPm() {
        return this.pm;
    }

    public int getBaseXp() {
        return this.baseXp;
    }

    public ArrayList<SpellEffect> getBuffs() {
        return this.fightBuffs;
    }

    public Stats getStats() {
        if (this.getTemplate().getId() == 42 && !stats.containsKey(Constant.STATS_SUMMON_COUNT))
            stats.put(Constant.STATS_SUMMON_COUNT, 5);

        if (this.stats.get(-1) != null) {
            Map<Integer, Integer> stats = new HashMap<>(this.stats);
            stats.remove(-1);
            stats.remove(-100);

            int random = Formulas.getRandomValue(210, 214);
            int one = this.stats.get(-1), all = this.stats.get(-100);

            stats.put(Constant.STATS_ADD_RP_NEU, (random == Constant.STATS_ADD_RP_NEU ? one : all));
            stats.put(Constant.STATS_ADD_RP_TER, (random == Constant.STATS_ADD_RP_TER ? one : all));
            stats.put(Constant.STATS_ADD_RP_FEU, (random == Constant.STATS_ADD_RP_FEU ? one : all));
            stats.put(Constant.STATS_ADD_RP_EAU, (random == Constant.STATS_ADD_RP_EAU ? one : all));
            stats.put(Constant.STATS_ADD_RP_AIR, (random == Constant.STATS_ADD_RP_AIR ? one : all));
            return new Stats(stats);
        }
        return new Stats(this.stats);
    }

    public Map<Integer, Spell.SortStats> getSpells() {
        return this.spells;
    }

    public SMobGrade scripted() {
        return this.scriptVal;
    }
}
