package org.starloco.locos.fight.spells;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Challenge;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Main;

import java.util.*;
import java.util.Map.Entry;

public class Spell {

    private int id;
    private String name;
    private int spriteId;
    private String spriteInfo;
    private int type;
    private short duration;
    private ArrayList<Integer> effectTargets = new ArrayList<>();
    private ArrayList<Integer> effectTargetsCC = new ArrayList<>();
    private Map<Integer, SortStats> spellsStats = new HashMap<>();
    private List<Byte> invalidStates, neededStates;

    public Spell(int id, String name, int spriteId, String spriteInfo, String effectTargets, int type, short duration, String invalidState, String neededState) {
        this.id = id;
        this.name = name;
        this.spriteId = spriteId;
        this.spriteInfo = spriteInfo;
        this.duration = duration;
        this.type = type;
        this.parseEffectTargets(effectTargets);
        this.parseStates(invalidState, neededState);
    }

    public void setInfo(int spriteId, String spriteInfo, String effectTargets, int type, short duration) {
        this.spriteId = spriteId;
        this.spriteInfo = spriteInfo;
        this.type = type;
        this.duration = duration;
        this.parseEffectTargets(effectTargets);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private int getSpriteId() {
        return spriteId;
    }

    private String getSpriteInfo() {
        return spriteInfo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public short getDuration() {
        return duration;
    }

    public ArrayList<Integer> getEffectTargets() {
        return effectTargets;
    }

    public ArrayList<Integer> getEffectTargetsCC() {
        return effectTargetsCC;
    }

    public SortStats getStatsByLevel(int lvl) {
        return spellsStats.get(lvl);
    }

    public Map<Integer, SortStats> getSpellsStats() {
        return spellsStats;
    }

    public void addSpellStats(Integer lvl, SortStats stats) {
        if (this.spellsStats.get(lvl) != null)
            this.spellsStats.remove(lvl);
        this.spellsStats.put(lvl, stats);
    }

    public boolean hasInvalidState(Fighter fighter) {
        if(this.invalidStates != null) {
            for (int state : this.invalidStates) {
                if(fighter.haveState(state))
                    return true;
            }
        }
        return false;
    }

    public boolean hasNeededState(Fighter fighter) {
        boolean ok = true;
        if(this.neededStates != null) {
            for (int state : this.neededStates) {
                if(!fighter.haveState(state))
                    ok = false;
            }
        }
        return ok;
    }

    private void parseEffectTargets(String effectTargets) {
        this.effectTargets.clear();
        this.effectTargetsCC.clear();

        if (effectTargets.equalsIgnoreCase("0")) {
            this.effectTargets.add(0);
            this.effectTargetsCC.add(0);
        } else {
            String nET = effectTargets.split(":")[0], ccET = "";

            if (effectTargets.split(":").length > 1)
                ccET = effectTargets.split(":")[1];
            for (String num : nET.split(";")) {
                try {
                    this.effectTargets.add(Integer.parseInt(num));
                } catch (Exception e) {
                    this.effectTargets.add(0);
                }
            }
            for (String num : ccET.split(";")) {
                try {
                    effectTargetsCC.add(Integer.parseInt(num));
                } catch (Exception e) {
                    effectTargetsCC.add(0);
                }
            }
        }
    }

    private void parseStates(String invalidState, String neededState) {
        if(invalidState != null && !invalidState.isEmpty()) {
            this.invalidStates = new ArrayList<>();
            for(String state : invalidState.split(",")) {
                this.invalidStates.add(Byte.parseByte(state));
            }
        }
        if(neededState != null && !neededState.isEmpty()) {
            this.neededStates = new ArrayList<>();
            for(String state : neededState.split(",")) {
                this.neededStates.add(Byte.parseByte(state));
            }
        }
    }

    public static class SortStats {

        private int spellID;
        private int level;
        private int PACost;
        private int minPO;
        private int maxPO;
        private int TauxCC;
        private int TauxEC;
        private boolean isLineLaunch;
        private boolean hasLDV;
        private boolean isEmptyCell;
        private boolean isModifPO;
        private int maxLaunchbyTurn;
        private int maxLaunchbyByTarget;
        private int coolDown;
        private int reqLevel;
        private boolean isEcEndTurn;
        private ArrayList<SpellEffect> effects;
        private ArrayList<SpellEffect> CCeffects;
        private String porteeType;

        public SortStats(int AspellID, int Alevel, int cost, int minPO,
                         int maxPO, int tauxCC, int tauxEC, boolean isLineLaunch,
                         boolean hasLDV, boolean isEmptyCell, boolean isModifPO,
                         int maxLaunchbyTurn, int maxLaunchbyByTarget, int coolDown,
                         int reqLevel, boolean isEcEndTurn, String effects,
                         String ceffects, String typePortee) {
            //effets, effetsCC, PaCost, PO Min, PO Max, Taux CC, Taux EC, line, LDV, emptyCell, PO Modif, maxByTurn, maxByTarget, Cooldown, type, level, endTurn
            this.spellID = AspellID;
            this.level = Alevel;
            this.PACost = cost;
            this.minPO = minPO;
            this.maxPO = maxPO;
            this.TauxCC = tauxCC;
            this.TauxEC = tauxEC;
            this.isLineLaunch = isLineLaunch;
            this.hasLDV = hasLDV;
            this.isEmptyCell = isEmptyCell;
            this.isModifPO = isModifPO;
            this.maxLaunchbyTurn = maxLaunchbyTurn;
            this.maxLaunchbyByTarget = maxLaunchbyByTarget;
            this.coolDown = coolDown;
            this.reqLevel = reqLevel;
            this.isEcEndTurn = isEcEndTurn;
            this.effects = parseEffect(effects);
            this.CCeffects = parseEffect(ceffects);
            this.porteeType = typePortee;
        }

        private ArrayList<SpellEffect> parseEffect(String e) {
            ArrayList<SpellEffect> effets = new ArrayList<SpellEffect>();
            String[] splt = e.split("\\|");
            for (String a : splt) {
                try {
                    if (e.equals("-1"))
                        continue;
                    int id = Integer.parseInt(a.split(";", 2)[0]);
                    String args = a.split(";", 2)[1];
                    effets.add(new SpellEffect(id, args, spellID, level));
                } catch (Exception f) {
                    f.printStackTrace();
                    Main.stop("parseEffect spell");
                }
            }
            return effets;
        }

        public int getSpellID() {
            return spellID;
        }

        public Spell getSpell() {
            return World.world.getSort(spellID);
        }

        public int getSpriteID() {
            return getSpell().getSpriteId();
        }

        public String getSpriteInfos() {
            return getSpell().getSpriteInfo();
        }

        public int getLevel() {
            return level;
        }

        public int getPACost() {
            return PACost;
        }

        public int getMinPO() {
            return minPO;
        }

        public int getMaxPO() {
            return maxPO;
        }

        public int getTauxCC() {
            return TauxCC;
        }

        public int getTauxEC() {
            return TauxEC;
        }

        public boolean isLineLaunch() {
            return isLineLaunch;
        }

        public boolean hasLDV() {
            return hasLDV;
        }

        public boolean isEmptyCell() {
            return isEmptyCell;
        }

        public boolean isModifPO() {
            return isModifPO;
        }

        public int getMaxLaunchbyTurn() {
            return maxLaunchbyTurn;
        }

        public int getMaxLaunchByTarget() {
            return maxLaunchbyByTarget;
        }

        public int getCoolDown() {
            return coolDown;
        }

        public int getReqLevel() {
            return reqLevel;
        }

        public boolean isEcEndTurn() {
            return isEcEndTurn;
        }

        public ArrayList<SpellEffect> getEffects() {
            return effects;
        }

        public ArrayList<SpellEffect> getCCeffects() {
            return CCeffects;
        }

        public String getPorteeType() {
            return porteeType;
        }

        public void applySpellEffectToFight(Fight fight, Fighter fighter, GameCase cell, ArrayList<GameCase> cells, boolean isCC) {
            // Seulement appell� par les pieges, or les sorts de piege
            List<SpellEffect> effects = isCC ? this.CCeffects : this.effects;
            int chance = Formulas.getRandomValue(0, 99), curMin = 0;

            for (SpellEffect effect : effects) {
                if (effect.getChance() != 0 && effect.getChance() != 100) {// Si pas 100%
                    if (chance <= curMin || chance >= (effect.getChance() + curMin)) {
                        curMin += effect.getChance();
                        continue;
                    }
                    curMin += effect.getChance();
                }

                final ArrayList<Fighter> targets = getTargets(cells);
                if ((fight.getType() != Constant.FIGHT_TYPE_CHALLENGE) && (fight.getAllChallenges().size() > 0)) {
                    for (Entry<Integer, Challenge> c : fight.getAllChallenges().entrySet()) {
                        if (c.getValue() == null)
                            continue;
                        c.getValue().onFightersAttacked(targets, fighter, effect, this.getSpellID(), true);
                    }
                }
                effect.applyToFight(fight, fighter, cell, targets);
            }
        }

        public ArrayList<Fighter> getTargets(List<GameCase> cells) {
            final ArrayList<Fighter> targets = new ArrayList<>();
            for (GameCase cell : cells) {
                if (cell != null) {
                    final Fighter target = cell.getFirstFighter();
                    if (target != null) targets.add(target);
                }
            }
            return targets;
        }

        public void applySpellEffectToFight(Fight fight, Fighter perso,
                                            GameCase cell, boolean isCC, boolean isTrap) {
            ArrayList<SpellEffect> effects = isCC ? CCeffects : this.effects;

            int jetChance = 0;
            if (this.getSpell().getId() == 101) // Si c'est roulette
            {
                jetChance = Formulas.getRandomValue(0, 75);
                if (jetChance % 2 == 0)
                    jetChance++;
            } else if (this.getSpell().getId() == 574) // Si c'est Ouverture hasardeuse fant�me
                jetChance = Formulas.getRandomValue(0, 96);
            else if (this.getSpell().getId() == 574) // Si c'est Ouverture hasardeuse
                jetChance = Formulas.getRandomValue(0, 95);
            else
                jetChance = Formulas.getRandomValue(0, 99);
            int curMin = 0;
            int num = 0;
            for (SpellEffect SE : effects) {
                try {
                    if (fight.getState() >= Constant.FIGHT_STATE_FINISHED)
                        return;
                    if (SE.getChance() != 0 && SE.getChance() != 100)// Si pas 100%
                    {
                        if (jetChance <= curMin
                                || jetChance >= (SE.getChance() + curMin)) {
                            curMin += SE.getChance();
                            num++;
                            continue;
                        }
                        curMin += SE.getChance();
                    }
                    int POnum = num * 2;
                    if (isCC) {
                        POnum += this.effects.size() * 2;// On zaap la partie du String des effets hors CC
                    }
                    ArrayList<GameCase> cells = PathFinding.getCellListFromAreaString(fight.getMap(), cell.getId(), perso.getCell().getId(), porteeType, POnum, isCC);
                    ArrayList<GameCase> finalCells = new ArrayList<GameCase>();
                    int TE = 0;
                    Spell S = World.world.getSort(spellID);
                    // on prend le targetFlag corespondant au num de l'effet

                    if (S != null && S.getEffectTargetsCC() != null && S.getEffectTargetsCC().size() > num && isCC)
                        TE = S.getEffectTargetsCC().get(num);
                    else if (S != null && S.getEffectTargets().size() > num && !isCC)
                        TE = S.getEffectTargets().get(num);

                    for (GameCase C : cells) {
                        if (C == null)
                            continue;
                        Fighter F = C.getFirstFighter();
                        if (F == null)
                            continue;
                        // Ne touches pas les alli�s : 1
                        if (((TE & 1) == 1) && (F.getTeam() == perso.getTeam()))
                            continue;
                        // Ne touche pas le lanceur : 2
                        if ((((TE >> 1) & 1) == 1) && (F.getId() == perso.getId()))
                            continue;
                        // Ne touche pas les ennemies : 4
                        if ((((TE >> 2) & 1) == 1) && (F.getTeam() != perso.getTeam()))
                            continue;
                        // Ne touche pas les combatants (seulement invocations) : 8
                        if ((((TE >> 3) & 1) == 1) && (!F.isInvocation()))
                            continue;
                        // Ne touche pas les invocations : 16
                        if ((((TE >> 4) & 1) == 1) && (F.isInvocation()))
                            continue;
                        // N'affecte que le lanceur : 32
                        if ((((TE >> 5) & 1) == 1) && (F.getId() != perso.getId()))
                            continue;
                        // N'affecte que les alliés (pas le lanceur) : 64
                        if ((((TE >> 6) & 1) == 1) && (F.getTeam() != perso.getTeam() || F.getId() == perso.getId()))
                            continue;
                        // N'affecte PERSONNE : 1024
                        if ((((TE >> 10) & 1) == 1))
                            continue;
                        // Si pas encore eu de continue, on ajoute la case, tout le monde : 0
                        finalCells.add(C);
                    }
                    // Si le sort n'affecte que le lanceur et que le lanceur n'est
                    // pas dans la zone

                    if (((TE >> 5) & 1) == 1)
                        if (!finalCells.contains(perso.getCell()))
                            finalCells.add(perso.getCell());
                    ArrayList<Fighter> cibles = SpellEffect.getTargets(SE, fight, finalCells);

                    if ((fight.getType() != Constant.FIGHT_TYPE_CHALLENGE)
                            && (fight.getAllChallenges().size() > 0)) {
                        for (Entry<Integer, Challenge> c : fight.getAllChallenges().entrySet()) {
                            if (c.getValue() == null)
                                continue;
                            c.getValue().onFightersAttacked(cibles, perso, SE, this.getSpellID(), isTrap);
                        }
                    }
                    SE.applyToFight(fight, perso, cell, cibles);
                    num++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean hasEffectOnTarget(Fighter caster, Fighter target) {
            boolean ok = false;
            int TE = 0, num =0;
            for (SpellEffect SE : this.effects) {
                if (this.getSpell() != null && this.getSpell().getEffectTargets().size() > num)
                    TE = getSpell().getEffectTargets().get(num);
                // Ne touches pas les alli�s : 1
                if (((TE & 1) == 1) && (target.getTeam() == caster.getTeam()))
                    continue;
                // Ne touche pas le lanceur : 2
                if ((((TE >> 1) & 1) == 1) && (target.getId() == caster.getId()))
                    continue;
                // Ne touche pas les ennemies : 4
                if ((((TE >> 2) & 1) == 1) && (target.getTeam() != caster.getTeam()))
                    continue;
                // Ne touche pas les combatants (seulement invocations) : 8
                if ((((TE >> 3) & 1) == 1) && (!target.isInvocation()))
                    continue;
                // Ne touche pas les invocations : 16
                if ((((TE >> 4) & 1) == 1) && (target.isInvocation()))
                    continue;
                // N'affecte que le lanceur : 32
                if ((((TE >> 5) & 1) == 1) && (target.getId() != caster.getId()))
                    continue;
                // N'affecte que les alliés (pas le lanceur) : 64
                if ((((TE >> 6) & 1) == 1) && (target.getTeam() != caster.getTeam() || target.getId() == caster.getId()))
                    continue;
                // N'affecte PERSONNE : 1024
                if ((((TE >> 10) & 1) == 1))
                    continue;
                num++;
                ok = true;
            }
            return ok;
        }
    }
}
