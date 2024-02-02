package org.starloco.locos.fight;

import org.classdump.luna.impl.ImmutableTable;
import org.starloco.locos.area.map.Actor;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.player.Player;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.entity.Collector;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.entity.monster.MonsterGrade;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.fight.spells.LaunchedSpell;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.script.Scripted;
import org.starloco.locos.util.TimerWaiter;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract  class Fighter implements Comparable<Fighter>, Scripted<Object>, Actor, Cloneable {
    protected final int id;
    protected final Fight fight;

    private int erodedLP = 0;

    private int nbrInvoc;
    private boolean trapped = false, glyphed = false;
    private boolean isStatique = false;
    private boolean canPlay = false;
    // protected int type = 0;
    private int team = -2;
    private GameCase cell;
    private int pdv;
    private boolean isDead;
    private boolean hasLeft;
    private Fighter isHolding;
    private Fighter holdedBy;
    private Fighter oldCible = null;
    private Fighter invocator;
    private boolean levelUp = false;
    private boolean isDeconnected = false;
    private int turnRemaining = 0;
    private int nbrDisconnection = 0;
    private boolean isTraqued = false;
    private Stats stats;
    private Map<Integer, Integer> state = new HashMap<>();
    protected final ArrayList<SpellEffect> fightBuffs = new ArrayList<>();
    private Map<Integer, Integer> chatiValue = new HashMap<>();
    private ArrayList<LaunchedSpell> launchedSpell = new ArrayList<>();
    private World.Couple<Byte, Long> killedBy;

    protected Fighter(int id, Fight f) {
        this.id = id;
        this.fight = f;
    }

    private void init() {
        this.pdv = this.getPdvMax();
    }

    public static Fighter NewPlayer(Fight f, Player player) {
        Fighter fi = new PlayerFighter(f, player);
        fi.init();
        return fi;
    }

    public static Fighter NewCollector(int id, Fight f, Collector collector) {
        Fighter fi = new CollectorFighter(id, f, collector);
        fi.init();
        return fi;
    }

    public static Fighter NewMob(int id, Fight f, MonsterGrade mg) {
        Fighter fi = new MobFighter(id, f, mg);
        fi.init();
        return fi;
    }

    public static Fighter NewPrism(int id, Fight f, Prism p) {
        Fighter fi = new PrismFighter(id, f, p);
        fi.init();
        return fi;
    }

    public static Fighter NewClone(int id, Fight f, PlayerFighter p) {
        Fighter fi = new CloneFighter(id, f, p);
        fi.init();
        return fi;
    }

    public static Fighter NewSummon(int id, Fight f, MonsterGrade mg, Fighter caster) {
        Fighter fi = new SummonFighter(id, f, mg, caster);
        fi.init();
        return fi;
    }

//    public Fighter(Fight f, Player player) {
//        this.fight = f;
//        if (player._isClone) {
//            this.type = 10;
//            setDouble(player);
//        } else {
//            this.type = 1;
//            this.player = player;
//        }
//        setId(player.getId());
//        this.pdvMax = player.getMaxPdv();
//        this.pdv = player.getCurPdv();
//        this.gfxId = getDefaultGfx();
//    }
//
//    public Fighter(Fight f, Collector Perco) {
//        this.fight = f;
//        this.type = 5;
//        setCollector(Perco);
//        setId(-1);
//        this.pdvMax = (World.world.getGuild(Perco.getGuildId()).getLvl() * 100);
//        this.pdv = (World.world.getGuild(Perco.getGuildId()).getLvl() * 100);
//        this.gfxId = 6000;
//    }

//    public Fighter(Fight Fight, Prism Prisme) {
//        this.fight = Fight;
//        this.type = 7;
//        setPrism(Prisme);
//        setId(-1);
//        this.pdvMax = Prisme.getLevel() * 10000;
//        this.pdv = Prisme.getLevel() * 10000;
//        this.gfxId = Prisme.getAlignment() == 1 ? 8101 : 8100;
//        Prisme.refreshStats();
//    }

    public int getId() {
        return this.id;
    }

    public abstract int getType();

    public abstract int getLvl();

    public abstract int baseMaxPdv();

    protected abstract Stats getBaseStats();

    public abstract int getDefaultGfx();

    @Override
    public Fighter clone() throws CloneNotSupportedException { return (Fighter) super.clone(); };

    protected String getMountColors() { return null; };




    public boolean canPlay() {
        return this.canPlay;
    }

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;
    }

    public Fight getFight() {
        return this.fight;
    }

    public void initFightBuffs() {}

    public void send(String pck) {}

    public abstract Optional<Spell.SortStats> spellRankForID(int id);

    public String xpString(String separator) {
        return "0" + separator + "0" + separator + "0";
    }

    public abstract String getPacketsName();

    abstract Stream<String> getGMPacketParts();

    Optional<Mount> getMount() { return Optional.empty(); };

    public int[] getColors() { return new int[]{-1, -1, -1}; }

//    public MonsterGrade getMob() {
//        if (this.type == 2)
//            return this.mob;
//        return null;
//    }
//
//    public boolean isMob() {
//        return (this.mob != null);
//    }
//
//    public Player getPlayer() {
//        if (this.type == 1)
//            return this.player;
//        return null;
//    }
//
//    public Player getDouble() {
//        return _double;
//    }
//
//    public boolean isDouble() {
//        return (this._double != null);
//    }
//
//    public void setDouble(Player _double) {
//        this._double = _double;
//    }
//
//    public Collector getCollector() {
//        if (this.type == 5)
//            return this.collector;
//        return null;
//    }
//
//    public boolean isCollector() {
//        return (this.collector != null);
//    }
//
//    public void setCollector(Collector collector) {
//        this.collector = collector;
//    }

//    public Prism getPrism() {
//        if (this.type == 7)
//            return this.prism;
//        return null;
//    }
//
//    public void setPrism(Prism prism) {
//        this.prism = prism;
//    }
//
//    public boolean isPrisme() {
//        return (this.prism != null);
//    }

    public int getTeam() {
        return this.team;
    }

    public void setTeam(int i) {
        this.team = i;
    }

    public int getTeam2() {
        return this.fight.getTeamId(getId());
    }

    public int getOtherTeam() {
        return this.fight.getOtherTeamId(getId());
    }

    public GameCase getCell() {
        return this.cell;
    }

    public void setCell(GameCase cell) {
        this.cell = cell;
    }

    public int getPdvMax() {
        return this.baseMaxPdv() + getBuffValue(Constant.STATS_ADD_VITA) - erodedLP;
    }

    public void removePdvMax(int pdv) {
        erodedLP += pdv;
        this.pdv = Math.min(getPdvMax(), this.pdv);
    }

    public int getPdv() {
        return (this.pdv + getBuffValue(Constant.STATS_ADD_VITA));
    }

    public void setPdv(int pdv) {
        this.pdv = Math.min(this.getPdvMax(), pdv);
    }

    public void removePdv(Fighter caster, int pdv) {
        if (pdv > 0)
            this.getFight().getAllChallenges().values().stream().filter(Objects::nonNull).forEach(challenge -> challenge.onFighterAttacked(caster, this));
        this.pdv -= pdv;
    }

    public void fullPdv() {
        this.pdv = this.getPdvMax();
    }

    public boolean isFullPdv() {
        return this.pdv == this.getPdvMax();
    }

    public boolean isDead() {
        return this.isDead;
    }

    public void setIsDead(boolean isDead) {
        this.isDead = isDead;
    }

    public boolean hasLeft() {
        return this.hasLeft;
    }

    public void setLeft(boolean hasLeft) {
        this.hasLeft = hasLeft;
    }

    public Fighter getIsHolding() {
        return this.isHolding;
    }

    public void setIsHolding(Fighter isHolding) {
        this.isHolding = isHolding;
    }

    public Fighter getHoldedBy() {
        return this.holdedBy;
    }

    public void setHoldedBy(Fighter holdedBy) {
        this.holdedBy = holdedBy;
    }

    public Fighter getOldCible() {
        return this.oldCible;
    }

    public void setOldCible(Fighter cible) {
        this.oldCible = cible;
    }

    public Fighter getInvocator() {
        return this.invocator;
    }

    public void setInvocator(Fighter invocator) {
        this.invocator = invocator;
    }

    public boolean isInvocation() {
        return (this.invocator != null);
    }

    public boolean getLevelUp() {
        return this.levelUp;
    }

    public void setLevelUp(boolean levelUp) {
        this.levelUp = levelUp;
    }

    public void Disconnect() {
        if (this.isDeconnected)
            return;
        this.isDeconnected = true;
        this.turnRemaining = 20;
        this.nbrDisconnection++;
    }

    public void Reconnect() {
        this.isDeconnected = false;
        this.turnRemaining = 0;
    }

    public boolean isDeconnected() {
        return !this.hasLeft && this.isDeconnected;
    }

    public int getTurnRemaining() {
        return this.turnRemaining;
    }

    public void setTurnRemaining() {
        this.turnRemaining--;
    }

    public int getNbrDisconnection() {
        return this.nbrDisconnection;
    }

    public boolean getTraqued() {
        return this.isTraqued;
    }

    public void setTraqued(boolean isTraqued) {
        this.isTraqued = isTraqued;
    }

    public void setState(int id, int t) {
        this.state.remove(id);
        if (t != 0) this.state.put(id, t);
    }

    public int getState(int id) {
        return this.state.get(id) != null ? this.state.get(id) : -1;
    }

    public boolean haveState(int id) {
        Integer turn = this.state.get(id);
        return turn != null && turn != 0;
    }

    public void sendState(Player p) {
        if (p != null && p.getAccount() != null && p.getGameClient() != null)
            for (Entry<Integer, Integer> state : this.state.entrySet())
                SocketManager.GAME_SEND_GA_PACKET(p.getGameClient(), 7 + "", 950 + "", getId() + "", getId() + "," + state.getKey() + ",1");
    }

    public int nbInvocation() {
        int i = 0;
        for (Entry<Integer, Fighter> entry : this.getFight().getTeam(this.getTeam2()).entrySet()) {
            Fighter f = entry.getValue();
            if (f.isInvocation() && !f.isStatique)
                if (f.getInvocator() == this)
                    i++;
        }
        return i;
    }

    public boolean isTrappedOrGlyphed() {
        return isTrapped() || isGlyphed();
    }

    public boolean isTrapped() {
        return trapped;
    }

    public void setTrapped(boolean trapped) {
        this.trapped = trapped;
    }

    public boolean isGlyphed() {
        return trapped;
    }

    public void setGlyphed(boolean glyphed) {
        this.glyphed = glyphed;
    }

    public ArrayList<SpellEffect> getFightBuff() {
        return this.fightBuffs;
    }

    private Stats getFightBuffStats() {
        Stats stats = new Stats();
        for (SpellEffect entry : this.fightBuffs)
            stats.addOneStat(entry.getEffectID(), entry.getValue());
        return stats;
    }

    public int getBuffValue(int id) {
        int value = 0;
        for (SpellEffect entry : this.fightBuffs)
            if (entry.getEffectID() == id)
                value += entry.getValue();
        return value;
    }

    public SpellEffect getBuff(int id) {
        for (SpellEffect entry : this.fightBuffs)
            if (entry.getEffectID() == id && entry.getTurn() > 0)
                return entry;
        return null;
    }

    public ArrayList<SpellEffect> getBuffsByEffectID(int effectId) {
        return this.fightBuffs.stream().filter(buff -> buff.getEffectID() == effectId).collect(Collectors.toCollection(ArrayList::new));
    }

    public Stats getTotalStatsLessBuff() {
        return getBaseStats();
    }

    public boolean hasBuff(int id) {
        for (SpellEffect entry : this.fightBuffs)
            if (entry.getEffectID() == id && entry.getTurn() > 0)
                return true;
        return false;
    }

    public SpellEffect addBuff(int id, int value, int duration, boolean debuff, int spellId, String args, Fighter caster, boolean sendGA, boolean sendGIE) {
        SpellEffect effect = new SpellEffect(id, value, duration, debuff, caster, args, spellId);

        Optional<MobFighter> mob = this.as(MobFighter.class);
        if(mob.isPresent()) {
            for(int id1 : Constant.STATIC_INVOCATIONS)
                if (id1 != 2750 && id1 == mob.get().getTemplate().getId())
                    return effect;
        }

        switch(spellId) {
            case 1099:
                if(mob.isPresent() && mob.get().getTemplate().getId() == 423)
                    return effect;
                break;
            case 99:case 5:case 20:case 127: case 89:case 126:case 115:case 192: case 4:case 1:case 6:
            case 14:case 18:case 7: case 284:case 197:case 704:case 168:case 45: case 159:case 171:case 167:case 511:case 513:
            case 686: case 701: // Sort pandawa (etat)
            case 431:case 433:case 437:case 443:case 441: // Chatiment
                debuff = true;
                break;
        }

        if(id == 606 || id == 607 || id == 608 || id == 609 || id == 611 || id == 125 || id == 114) debuff = true;
        else if(id == 293) debuff = false;

        switch(spellId) {
            // Feca spells shields
            case 1: case 4: case 5: case 6: case 7: case 14: case 18: case 20:
                if(this.getId() != caster.getId())
                    duration++;
                break;
        }
        //Si c'est le jouer actif qui s'autoBuff, on ajoute 1 a la durée
        if(this.getId() == caster.getId() && !this.isTrapped() && id != 84 && id != 950 && spellId != 446) {
            duration += 1;
            switch(spellId) {
                case 138: // Mot de silence
                case 170: // Fleche d'immo
                case 114: // Rekop
                    duration--;
                    break;
            }
        }
        // Cas infini
        if(mob.isPresent() && duration == 0)
            duration = -1;

        effect.setTurn(duration);
        this.fightBuffs.add(effect);

        if(Config.debug)
            System.out.println("- Ajout du Buff "+id+" sur le personnage fighter ("+this.getId()+") val : "+value+" duration : "+duration+" debuff : "+debuff+" spellid : "+spellId+" args : "+args+" !");
        switch(spellId) {
            // Feca spells shields
            case 1: case 4: case 5: case 6: case 7: case 14: case 18: case 20:
                if(this.getId() != caster.getId())
                    duration--;
                break;
        }
        if(id != 950 && spellId != 446) {
            switch (spellId) {
                case 170: // Fleche d'immo
                case 114: // Rekop
                case 101:// Roulette
                    if (duration != 0)
                        duration--;
                    break;
                default:
                    if (this.getId() == caster.getId() || this.isTrapped())
                        duration--;
            }
        }

        if(sendGA) {
            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effect.getEffectID(), String.valueOf(caster.getId()), this.getId() + "," + effect.getValue() + "," + effect.getTurn());
        }
        if(sendGIE) {
            fight.sendBuffPacket(this, effect, fight.getFighters(7), null, duration);
        }
        return effect;
    }

    public void debuff(SpellEffect effect) {
        Iterator<SpellEffect> it = this.fightBuffs.iterator();
        while (it.hasNext()) {
            SpellEffect spellEffect = it.next();

            if(spellEffect.getEffectID() == 293)
                continue;
            switch (spellEffect.getSpell()) {
                case 437: case 431: case 433: case 443: case 441://Châtiments
                case 1104: case 1105: // Krachau immo / ralenti
                    continue;
                case 197://Puissance sylvestre
                case 52://Cupidité
                case 228://Etourderie mortelle (DC)
                    it.remove();
                    continue;
            }

            if (spellEffect.isDebuffabe()) {
                it.remove();

                if (effect.getCaster() == this) {
                    switch (spellEffect.getEffectID()) {
                        case Constant.STATS_ADD_PA:
                        case Constant.STATS_ADD_PA2:
                            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 101, getId() + "", getId() + ",-" + spellEffect.getValue());
                            this.setCurPa(this.fight, this.getCurPa(fight) - spellEffect.getValue());
                            break;
                        case Constant.STATS_ADD_PM:
                        case Constant.STATS_ADD_PM2:
                            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 127, getId() + "", getId() + ",-" + spellEffect.getValue());
                            this.setCurPm(this.fight, this.getCurPm(fight) - spellEffect.getValue());
                            break;
                        //case Constant.STATS_REM_PA:
                        case Constant.STATS_REM_PA2: // Pa non esquivable
                            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 111, getId() + "", getId() + "," + spellEffect.getValue());
                            this.setCurPa(this.fight, this.getCurPa(fight) + spellEffect.getValue());
                            break;

                        //case Constant.STATS_REM_PM:
                        case Constant.STATS_REM_PM2: // Pm non esquivable (picole)
                            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 128, getId() + "", getId() + "," + spellEffect.getValue());
                            this.setCurPm(this.fight, this.getCurPm(fight) + spellEffect.getValue());
                            break;
                    }
                }
            }
        }
        ArrayList<SpellEffect> array = new ArrayList<>(this.fightBuffs);
        if (!array.isEmpty()) {
            this.fightBuffs.clear();
            TimerWaiter.addNext(() -> array.stream().filter(Objects::nonNull).forEach(spellEffect -> {
                this.addBuff(spellEffect.getEffectID(), spellEffect.getValue(), spellEffect.getTurn(), spellEffect.isDebuffabe(), spellEffect.getSpell(), spellEffect.getArgs(), spellEffect.getCaster(), false, false);
                int duration = effect.getCaster() != this ? spellEffect.getTurn() : spellEffect.getTurn() - 1;
                fight.sendBuffPacket(this, spellEffect, fight.getFighters(7), null, duration);
            }), 750);
        }

        if(!this.hasLeft){
            this.as(PlayerFighter.class).ifPresent(PlayerFighter::sendStats);
        }
    }

    private void refreshEndTurnShield() {
        for (Fighter fighter : this.fight.getFighters(7)) {
            Iterator<SpellEffect> iterator = fighter.getFightBuff().iterator();

            while (iterator.hasNext()) {
                SpellEffect effect = iterator.next();

                if (effect != null) {
                    switch (effect.getSpell()) {
                        // Feca spells shields
                        case 1: case 4: case 5: case 6: case 7: case 14: case 18: case 20:
                            if (this.getId() == effect.getCaster().getId()) {
                                if (effect.decrementDuration() == 0) {
                                    iterator.remove();
                                }
                                break;
                            }
                    }
                }
            }
        }
    }

    void refreshEndTurnBuff() {
        this.refreshEndTurnShield();
        Iterator<SpellEffect> iterator = this.fightBuffs.iterator();

        while (iterator.hasNext()) {

            SpellEffect effect = iterator.next();
            if (effect == null)
                continue;
            switch (effect.getSpell()) {
                // Feca spells shields
                case 1: case 4: case 5: case 6: case 7: case 14: case 18: case 20:
                    continue;
            }

            if (effect.decrementDuration() == 0 || effect.getCaster().isDead) {
                iterator.remove();
                switch (effect.getEffectID()) {
                    case 787: // Mot lotof
                        int id = Integer.parseInt(effect.getArgs().split(";")[0]);
                        int level = Integer.parseInt(effect.getArgs().split(";")[1]);
                        Spell spell = World.world.getSort(id);

                        for (SpellEffect e : spell.getStatsByLevel(level).getEffects()) {
                            if (e.getEffectID() == 89)
                                e.applyToFight(fight, this, this.getCell(), PathFinding.getFightersAround(this.getCell().getId(), this.fight.getMap()));
                        }
                        spell.getStatsByLevel(level).applySpellEffectToFight(fight, fight.getFighterByGameOrder(), cell, false, false);
                        break;
                    case 108:
                        if (effect.getSpell() == 441) {
                            //Baisse des pdvs max
                            erodedLP += effect.getValue();
                            //Baisse des pdvs actuel
                            if (this.pdv - effect.getValue() <= 0) {
                                this.fight.onFighterDie(this, this.holdedBy);
                                this.fight.verifIfTeamAllDead();
                            } else this.pdv = (this.pdv - effect.getValue());
                        }
                        break;
                    case 150: // Invisibilité
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 150, effect.getCaster().getId() + "", getId() + ",0");
                        SocketManager.GAME_SEND_GIC_PACKET_TO_FIGHT(this.fight, 7, this);
                        break;

                    case 950: // Etat
                        String args = effect.getArgs();
                        id = Integer.parseInt(args.split(";")[2]);

                        if (id != -1) {
                            setState(id, 0);
                            SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 950, effect.getCaster().getId() + "", effect.getCaster().getId() + "," + id + ",0");
                        }
                        break;
                }
            }
        }
    }

    void applyBeginningTurnBuff(Fight fight) {
        List<SpellEffect> effects = new ArrayList<>(this.fightBuffs);
        for (int effectID : Constant.BEGIN_TURN_BUFF) {
            effects.stream().filter(entry -> entry.getEffectID() == effectID)
                    .forEach(entry -> entry.applyBeginingBuff(fight, this));
        }
    }

    public ArrayList<LaunchedSpell> getLaunchedSorts() {
        return this.launchedSpell;
    }

    void refreshLaunchedSort() {
        this.launchedSpell.removeIf(launched -> launched.decrementCooldown() <= 0);
    }

    void addLaunchedSort(Fighter target, Spell.SortStats sort, Fighter fighter) {
        LaunchedSpell launched = new LaunchedSpell(target, sort, fighter);
        this.launchedSpell.add(launched);
    }

    public Stats getTotalStats() {
        return Stats.cumulStatFight(getBaseStats(), getFightBuffStats());
    }

    public int getMaitriseDmg(int id) {
        int value = 0;
        for (SpellEffect entry : this.fightBuffs)
            if (entry.getSpell() == id)
                value += entry.getValue();
        return value;
    }

    public boolean getSpellValueBool(int id) {
        for (SpellEffect entry : this.fightBuffs)
            if (entry.getSpell() == id)
                return true;
        return false;
    }

    boolean critStrikeCheck(int tauxCC) {
        if (tauxCC < 2)
            return false;
        int agi = getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
        if (agi < 0)
            agi = 0;
        tauxCC -= getTotalStats().getEffect(Constant.STATS_ADD_CC);
        tauxCC = (int) ((tauxCC * 2.9901) / Math.log(agi + 12));//Influence de l'agi
        if (tauxCC < 2)
            tauxCC = 2;
        int jet = Formulas.getRandomValue(1, tauxCC);
        return (jet == tauxCC);
    }

    protected int criticalStrikeModifier(int baseCC, int spellID) {
        // By default, there is no modifier
        return baseCC;
    }

    boolean critStrikeCheck(int porcCC, Spell.SortStats sSort, Fighter fighter) {
        if(porcCC == 0) return false;
         porcCC = criticalStrikeModifier(porcCC, sSort.getSpellID());
        int jet = Formulas.getRandomValue(1, porcCC);
        return (jet == porcCC);
    }

    int getInitiative() {
        return getTotalStats().getEffect(Constant.STATS_ADD_INIT);
    };

    public int getPa() {
        return getTotalStats().getEffect(Constant.STATS_ADD_PA);
    }

    public int getPm() {
        return getTotalStats().getEffect(Constant.STATS_ADD_PM);
    }

    int getPros() {
        return getTotalStats().getEffect(Constant.STATS_ADD_PROS);
    }

    public int getCurPa(Fight fight) {
        return fight.getCurFighterPa();
    }

    public void setCurPa(Fight fight, int pa) {
        fight.setCurFighterPa(fight.getCurFighterPa() + pa);
    }

    public int getCurPm(Fight fight) {
        return fight.getCurFighterPm();
    }

    public void setCurPm(Fight fight, int pm) {
        fight.setCurFighterPm(fight.getCurFighterPm() + pm);
    }

    public boolean canLaunchSpell(int spellID) {
        return spellRankForID(spellID).isPresent() && LaunchedSpell.cooldownGood(this, spellID);
    }

    public void unHide(int spellid) {
       //on retire le buff invi
        if (spellid != -1)// -1 : CAC
        {
            switch (spellid) {
                case 66:
                case 71:
                case 181:
                case 196:
                case 200:
                case 219:
                    return;
            }
        }
        ArrayList<SpellEffect> buffs = new ArrayList<>(getFightBuff());
        for (SpellEffect SE : buffs) {
            if (SE.getEffectID() == 150)
                getFightBuff().remove(SE);
        }
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 150, getId() + "", getId() + ",0");
        //On actualise la position
        SocketManager.GAME_SEND_GIC_PACKET_TO_FIGHT(this.fight, 7, this);
    }

    public boolean isHidden() {
        return hasBuff(150);
    }

    public Map<Integer, Integer> getChatiValue() {
        return this.chatiValue;
    }

    public String getGmPacket(char c, boolean withGm) {
        StringJoiner str = new StringJoiner(";", (withGm?"GM|":"")+c, "");
        str.add(String.valueOf(getCell().getId()));
        str.add("1");
        str.add("0");
        str.add(String.valueOf(getId()));
        str.add(getPacketsName());

        getGMPacketParts().forEach(str::add);

        str.add(String.valueOf(team));

        getMount().ifPresent(m -> str.add(m.getStringColor(getMountColors())));

        return str.toString();
    }

    public boolean isStatic() {
//        if(this.getMob() != null && this.getMob().getTemplate() != null)
//            for (int id : Constant.STATIC_INVOCATIONS)
//                if (id == this.getMob().getTemplate().getId())
//                    return true;
        return isStatique;
    }

    @Override
    public int compareTo(Fighter t) {
        if(this.isInvocation()) return 0;
        return this.getPros() - t.getPros();
    }

    @Override
    public Object scripted() {
        return (new ImmutableTable.Builder())
            .add("id", this.id)
            .add("type", this.getType())
            .add("level", this.getLvl())
            .build();
    }

    @Override
    public long Id() {
        return id;
    }

    @Override
    public String name() {
        return getPacketsName();
    }

    public void setStatic(boolean b) {
        isStatique = b;
    }

    public void modNbrInvoc(int i) {
        nbrInvoc += i;
    }

    public int getNbrInvoc() {
        return nbrInvoc;
    }

    public World.Couple<Byte, Long> getKilledBy() {
        return killedBy;
    }

    public void setKilledBy(World.Couple<Byte, Long> killer) {
        killedBy = killer;
    }

    private <T extends Fighter> Optional<T> as(Class<T> c) {
        return Optional.of(this).filter(c::isInstance).map(c::cast);
    }

    public boolean aiControlled() { return true; };

    boolean canLoot() { return false; }

    int minKamasReward() { return 0; }
    int maxKamasReward() { return 0; }

    Stream<World.Drop> drops() { return Stream.empty(); }

    // Tmp helpers to help compilation. This will eventually go
    @Deprecated
    public Player getPlayer() {
        return null;
    }

    @Deprecated
    public MonsterGrade getMob() {
        return null;
    }

    @Deprecated
    public Collector getCollector() {
        return null;
    }

    @Deprecated
    public Prism getPrism() {
        return null;
    }
}