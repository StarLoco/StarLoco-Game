package org.starloco.locos.fight.spells;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.entity.monster.MonsterGrade;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.fight.traps.Glyph;
import org.starloco.locos.fight.traps.Trap;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.util.TimerWaiter;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SpellEffect implements Cloneable {

	private int effectID;
	private int turns = 0;
	private String jet = "0d0+0";
	private int chance = 100;
	private String args;
	private int value = 0;
	private Fighter caster = null;
	private final int spell;
	private int spellLvl = 1;
	private boolean debuffable = true;
	private GameCase cell = null;

	public SpellEffect(int aID, String aArgs, int aSpell, int aSpellLevel) {
		effectID = aID;
		args = aArgs;
		spell = aSpell;
		spellLvl = aSpellLevel;
		try {
			value = Integer.parseInt(args.split(";")[0]);
			turns = Integer.parseInt(args.split(";")[3]);
			chance = Integer.parseInt(args.split(";")[4]);
			jet = args.split(";")[5];
		} catch (Exception ignored) {
		}
	}

	public SpellEffect(int id, int value2, int turns2, boolean debuff, Fighter aCaster, String args2, int aspell) {
		effectID = id;
		value = value2;
		turns = turns2;
		debuffable = debuff;
		caster = aCaster;
		args = args2;
		spell = aspell;
		try {
			jet = args.split(";")[5];
		} catch (Exception ignored) {
		}
	}

	public boolean isPoison() {
		return spell == 66 || spell == 164 || spell == 71 || spell == 196 || spell == 219 || spell == 181 || spell == 200;
	}

	public static ArrayList<Fighter> getTargets(ArrayList<GameCase> cells) {
		ArrayList<Fighter> targets = new ArrayList<>();
		for (GameCase aCell : cells) {
			if (aCell == null) continue;
			Fighter f = aCell.getFirstFighter();
			if (f == null) continue;
			targets.add(f);
		}
		return targets;
	}

	public int applyOnHitBuffs(int finalDommage, Fighter target, Fighter caster, Fight fight, int elementId) {
		for (int id : Constant.ON_HIT_BUFFS) {
			final List<SpellEffect> effects = target.getBuffsByEffectID(id);

			if (!effects.isEmpty()) {
				switch (id) {
					case 114:
						for (SpellEffect effect : effects) {
							if (effect.spell == 521) {
								finalDommage = finalDommage * 2;
							}
						}
						continue;
					case 107: // Damage return
						int totalDamageReturn = 0;

						for (SpellEffect effect : effects) {
							if (this.spell == 66 || spell == 71 || spell == 196 || spell == 181 || spell == 200 || target.getId() == caster.getId())
								break;
							if (caster.hasBuff(765) && caster.getBuff(765) != null && !caster.getBuff(765).getCaster().isDead()) {
								// Sacrifice
								effect.applyEffect_765B(fight, caster);
								caster = caster.getBuff(765).getCaster();
							}

							String[] args = effect.getArgs().split(";");
							float factor = 1 + (target.getTotalStats().getEffect(Constant.STATS_ADD_SAGE) / 100.0f);
							int damageReturn;
							try {
								if (Integer.parseInt(args[1]) != -1) {
									damageReturn = (int) (factor * Formulas.getRandomValue(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
								} else {
									damageReturn = (int) (factor * Integer.parseInt(args[0]));
								}
							} catch (Exception e) {
								return finalDommage;
							}

							damageReturn = Math.min(damageReturn, finalDommage);
							finalDommage -= damageReturn;

							damageReturn = Formulas.applyResistancesOnDamage(elementId, damageReturn, caster, true);
							damageReturn = Math.min(damageReturn, caster.getPdv());
							finalDommage = Math.max(0, finalDommage);
							damageReturn -= Formulas.getArmorResist(caster, -1);

							if (caster.hasBuff(149) && caster.getBuff(149).spell == 197) {
								damageReturn = 0;
							}
							if (caster.hasBuff(105)) { // Immunity
								damageReturn = damageReturn - caster.getBuff(105).getValue();
							}
							if (damageReturn < 0) {
								damageReturn = 0;
							}
							if (damageReturn > 0) {
								if (damageReturn > caster.getPdv()) damageReturn = caster.getPdv();
								caster.removePdv(caster, damageReturn);
								target.removePdvMax(damageReturn / 10);
								totalDamageReturn += damageReturn;
							}
						}

						if (caster.hasBuff(105)) { // Immunity
							SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, String.valueOf(caster.getId()), target.getId() + "," + caster.getBuff(105).getValue());
						}
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 107, "-1", target.getId() + "," + totalDamageReturn);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, String.valueOf(caster.getId()), caster.getId() + ",-" + totalDamageReturn);
						continue;
					default:
						for (SpellEffect buff : effects) {
							switch (id) {
								case 138:
									if (buff.getSpell() == 1039) {
										int stats = 0;
										if (elementId == Constant.ELEMENT_AIR)
											stats = 217;
										else if (elementId == Constant.ELEMENT_EAU)
											stats = 216;
										else if (elementId == Constant.ELEMENT_FEU)
											stats = 218;
										else if (elementId == Constant.ELEMENT_NEUTRE)
											stats = 219;
										else if (elementId == Constant.ELEMENT_TERRE)
											stats = 215;
										SpellEffect b = target.getBuff(stats);
										if (b != null) {
											int val = b.getValue();
											int turns = b.getTurn();
											int duration = b.getTurn();
											String args = b.getArgs();
											for (int i : Constant.getOppositeStats(stats))
												target.addBuff(i, val, turns, true, buff.getSpell(), args, caster, false, true);
											target.addBuff(stats, val, duration, true, buff.getSpell(), args, caster, false, true);
										}
									}
									break;
								case 9://Derobade
									if (this.isPoison())
										continue;
									if (caster == target || Formulas.getRandomValue(0, 99) + 1 >= buff.getValue())
										continue;

									int distance = PathFinding.getDistanceBetween(fight.getMap(), target.getCell().getId(), caster.getCell().getId());
									int nbCell = Integer.parseInt(buff.getArgs().split(";")[1]);

									if (distance > 1) {
										continue;
									}
									if (nbCell == 0)
										continue;
									int exCase = target.getCell().getId(), newCellId = PathFinding.newCaseAfterPush(fight, caster.getCell(), target.getCell(), nbCell, false);

									if (newCellId < 0) { // blocked
										int a = -newCellId;
										a = nbCell - a;
										newCellId = PathFinding.newCaseAfterPush(fight, caster.getCell(), target.getCell(), a, false);
										if (newCellId == 0) {
											finalDommage = Formulas.getRandomValue(28, 33);
											SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 5, target.getId() + "", target.getId() + "," + newCellId);
											break;
										}
										if (fight.getMap().getCase(newCellId) == null)
											continue;
									}
									if (newCellId == 0) continue;

									GameCase cacheCell = target.getCell();
									cacheCell.getFighters().clear();

									target.setCell(fight.getMap().getCase(newCellId));
									target.getCell().addFighter(target);
									SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 5, target.getId() + "", target.getId() + "," + newCellId);

									Fighter holding = target.getIsHolding();
									if (holding != null) {
										holding.setState(Constant.ETAT_PORTE, 0);
										target.setState(Constant.ETAT_PORTEUR, 0);
										holding.setHoldedBy(null);
										target.setIsHolding(null);
										holding.setCell(cacheCell);
										cacheCell.addFighter(holding);
										SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, target.getId() + "", target.getId() + "," + Constant.ETAT_PORTEUR + ",0");
										SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, holding.getId() + "", holding.getId() + "," + Constant.ETAT_PORTE + ",0");
										SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 51, target.getId() + "", cacheCell.getId() + "");
									}

									fight.checkTraps(target);
									if (exCase != newCellId)
										finalDommage = 0;
									break;

								case 79://chance éca
									try {
										String[] infos = buff.getArgs().split(";");
										int coefDom = Integer.parseInt(infos[0]);
										int coefHeal = Integer.parseInt(infos[1]);
										int chance = Integer.parseInt(infos[2]);
										int jet = Formulas.getRandomValue(0, 99);

										if (jet < chance)//Soin
										{
											finalDommage = -(finalDommage * coefHeal);
											if (-finalDommage > (target.getPdvMax() - target.getPdv()))
												finalDommage = -(target.getPdvMax() - target.getPdv());
										} else//Dommage
											finalDommage = finalDommage * coefDom;
									} catch (Exception e) {
										e.printStackTrace();
									}
									break;
								case 606://Chatiment (acncien)
									int stat = buff.getValue();
									int jet = Formulas.getRandomJet(caster, target, buff.getJet());
									target.addBuff(stat, jet, -1, false, buff.getSpell(), buff.getArgs(), caster, false, true);
									SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, stat, caster.getId() + "", target.getId() + "," + jet + "," + -1);
									break;
								case 607://Chatiment (acncien)
									stat = buff.getValue();
									jet = Formulas.getRandomJet(caster, target, buff.getJet());
									target.addBuff(stat, jet, -1, false, buff.getSpell(), buff.getArgs(), caster, false, true);
									SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, stat, caster.getId() + "", target.getId() + "," + jet + "," + -1);
									break;
								case 608://Chatiment (acncien)
									stat = buff.getValue();
									jet = Formulas.getRandomJet(caster, target, buff.getJet());
									target.addBuff(stat, jet, -1, false, buff.getSpell(), buff.getArgs(), caster, false, true);
									SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, stat, caster.getId() + "", target.getId() + "," + jet + "," + -1);
									break;
								case 609://Chatiment (acncien)
									stat = buff.getValue();
									jet = Formulas.getRandomJet(caster, target, buff.getJet());
									target.addBuff(stat, jet, -1, false, buff.getSpell(), buff.getArgs(), caster, false, true);
									SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, stat, caster.getId() + "", target.getId() + "," + jet + "," + -1);
									break;
								case 611://Chatiment (acncien)
									stat = buff.getValue();
									jet = Formulas.getRandomJet(caster, target, buff.getJet());
									target.addBuff(stat, jet, -1, false, buff.getSpell(), buff.getArgs(), caster, false, true);
									SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, stat, caster.getId() + "", target.getId() + "," + jet + "," + -1);
									break;
								case 788://Chatiments
									if (this.spell == 66 || spell == 71 || spell == 196 || spell == 213 || spell == 181 || spell == 200)
										continue;
									int factor = (caster.getPlayer() == null && caster.getInvocator() == null ? 1 : 2), gain = finalDommage / factor, max = 0;
									stat = buff.getValue();

									try {
										max = Integer.parseInt(buff.getArgs().split(";")[1]);
									} catch (Exception e) {
										e.printStackTrace();
										continue;
									}

									//on retire au max possible la valeur déjà gagné sur le chati
									int oldValue = (target.getChatiValue().get(stat) == null ? 0 : target.getChatiValue().get(stat));
									max -= oldValue;
									//Si gain trop grand, on le reduit au max
									if (gain > max) gain = max;
									//On met a jour les valeurs des chatis
									if (gain == 0) continue;
									int newValue = oldValue + gain;

									if (stat == 125) {
										SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_ADD_VIE, caster.getId() + "", target.getId() + "," + gain + "," + 5);
										target.setPdv(target.getPdv() + gain);
										if (target.getPlayer() != null)
											SocketManager.GAME_SEND_STATS_PACKET(target.getPlayer());
									} else {
										target.getChatiValue().put(stat, newValue);
										target.addBuff(stat, gain, 5, true, -1, buff.getArgs(), caster, false, true);
										SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, stat, caster.getId() + "", target.getId() + "," + gain + "," + 5);
									}
									target.getChatiValue().put(stat, newValue);
									break;

								default:
									break;
							}
						}
						break;
				}
			}
		}

		return finalDommage;
	}

	public int getTurn() {
		return turns;
	}

	public void setTurn(int turn) {
		this.turns = turn;
	}

	public boolean isDebuffabe() {
		return debuffable;
	}

	public int getEffectID() {
		return effectID;
	}

	public void setEffectID(int id) {
		effectID = id;
	}

	public String getJet() {
		return jet;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int i) {
		value = i;
	}

	public int getChance() {
		return chance;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String newArgs) {
		args = newArgs;
	}

	public int getMaxMinSpell(Fighter fighter, int value) {
		int val = value;
		if (fighter.hasBuff(782)) {
			int max = Integer.parseInt(args.split(";")[1]);
			if (max == -1)
				max = Integer.parseInt(args.split(";")[0]);
			val = max;
		} else if (fighter.hasBuff(781))
			val = Integer.parseInt(args.split(";")[0]);
		return val;
	}

	public int decrementDuration() {
		if(turns > 0) turns -= 1;
		return turns;
	}

	public void applyBeginingBuff(Fight fight, Fighter fighter) {
		ArrayList<Fighter> targets = new ArrayList<>();
		targets.add(fighter);
		int turns = this.turns;
		this.turns = -1;
		this.applyToFight(fight, this.caster, targets, false);
		this.turns = turns;
	}

	public void applyToFight(Fight fight, Fighter perso, GameCase Cell, ArrayList<Fighter> targets) {
		cell = Cell;
		Collections.reverse(targets);
		applyToFight(fight, perso, targets, false);
	}

	public Fighter getCaster() {
		return caster;
	}

	public int getSpell() {
		return spell;
	}

	public void applyToFight(Fight fight, Fighter acaster, ArrayList<Fighter> targets, boolean isCaC) {
		try {
			if (turns != -1)//Si ce n'est pas un buff qu'on applique en début de tour
				turns = Integer.parseInt(args.split(";")[3]);
		} catch (NumberFormatException ignored) {}
		caster = acaster;
		try {
			jet = args.split(";")[5];
		} catch (Exception ignored) {}

		if (caster.getPlayer() != null) {
			Player perso = caster.getPlayer();
			if (perso.getObjectsClassSpell().containsKey(spell)) {
				int modi = 0;
				if (effectID == 108)
					modi = perso.getValueOfClassObject(spell, 284);
				else if (effectID >= 91 && effectID <= 100)
					modi = perso.getValueOfClassObject(spell, 283);
				String jeta = jet.split("\\+")[0];
				int bonus = Integer.parseInt(jet.split("\\+")[1]) + modi;
				jet = jeta + "+" + bonus;
			}
		}

		switch (effectID) {
			case 4://Fuite/Bond du félin/ Bond du iop / téléport
				applyEffect_4(fight);
				break;
			case 5://Repousse de X case
				applyEffect_5(targets, fight);
				break;
			case 6://Attire de X case
				applyEffect_6(targets, fight);
				break;
			case 8://Echange les place de 2 joueur
				applyEffect_8(targets, fight);
				break;
			case 9://Esquive une attaque en reculant de 1 case
				applyEffect_9(targets);
				break;
			case 50://Porter
				applyEffect_50(fight);
				break;
			case 51://jeter
				applyEffect_51(fight);
				break;
			case 77://Vol de PM
				applyEffect_77(targets, fight);
				break;
			case 78://Bonus PM
				applyEffect_78(targets);
				break;
			case 79:// + X chance(%) dommage subis * Y sinon soigné de dommage *Z
				applyEffect_79(targets);
				break;
			case 81:// Cura, PDV devueltos
				applyEffect_81(targets, fight);
				break;
			case 82://Vol de Vie fixe
				applyEffect_82(targets, fight);
				break;
			case 84://Vol de PA
				applyEffect_84(targets, fight);
				break;
			case 85://Dommage Eau %vie
				applyEffect_85(targets, fight);
				break;
			case 86://Dommage Terre %vie
				applyEffect_86(targets, fight);
				break;
			case 87://Dommage Air %vie
				applyEffect_87(targets, fight);
				break;
			case 88://Dommage feu %vie
				applyEffect_88(targets, fight);
				break;
			case 89://Dommage neutre %vie
				applyEffect_89(targets, fight);
				break;
			case 90://Donne X% de sa vie
				applyEffect_90(targets, fight);
				break;
			case 91://Vol de Vie Eau
				applyEffect_91(targets, fight, isCaC);
				break;
			case 92://Vol de Vie Terre
				applyEffect_92(targets, fight, isCaC);
				break;
			case 93://Vol de Vie Air
				applyEffect_93(targets, fight, isCaC);
				break;
			case 94://Vol de Vie feu
				applyEffect_94(targets, fight, isCaC);
				break;
			case 95://Vol de Vie neutre
				applyEffect_95(targets, fight, isCaC);
				break;
			case 96://Dommage Eau
				applyEffect_96(targets, fight, isCaC);
				break;
			case 97://Dommage Terre
				applyEffect_97(targets, fight, isCaC);
				break;
			case 98://Dommage Air
				applyEffect_98(targets, fight, isCaC);
				break;
			case 99://Dommage feu
				applyEffect_99(targets, fight, isCaC);
				break;
			case 100://Dommage neutre
				applyEffect_100(targets, fight, isCaC);
				break;
			case 101://Retrait PA
				applyEffect_101(targets, fight);
				break;
			case 105://Dommages réduits de X
				applyEffect_105(targets);
				break;
			case 106://Renvoie de sort
				applyEffect_106(targets);
				break;
			case 107://Renvoie de dom
				applyEffect_107(targets);
				break;
			case 108://Soin
				applyEffect_108(targets, fight, isCaC);
				break;
			case 109://Dommage pour le lanceur
				applyEffect_109(fight);
				break;
			case 110://+ X vie
				applyEffect_110(targets, fight);
				break;
			case 111://+ X PA
				applyEffect_111(targets, fight);
				break;
			case 112://+Dom
				applyEffect_112(targets, fight);
				break;
			case 114://Multiplie les dommages par X
				applyEffect_114(targets, fight);
				break;
			case 115://+Cc
				applyEffect_115(targets);
				break;
			case 116://Malus PO
				applyEffect_116(targets);
				break;
			case 117://Bonus PO
				applyEffect_117(targets);
				break;
			case 118://Bonus force
				applyEffect_118(targets, fight);
				break;
			case 119://Bonus Agilité
				applyEffect_119(targets);
				break;
			case 120://Bonus PA
				applyEffect_120(fight);
				break;
			case 121://+Dom
				applyEffect_121(targets);
				break;
			case 122://+EC
				applyEffect_122(targets);
				break;
			case 123://+Chance
				applyEffect_123(targets);
				break;
			case 124://+Sagesse
				applyEffect_124(targets);
				break;
			case 125://+Vitalité
				applyEffect_125(targets);
				break;
			case 126://+Intelligence
				applyEffect_126(targets, fight);
				break;
			case 127://Retrait PM
				applyEffect_127(targets, fight);
				break;
			case 128://+PM
				applyEffect_128(targets, fight);
				break;
			case 130://Vol de kamas
				applyEffect_130(fight, targets);
				break;
			case 131://Poison : X Pdv  par PA
				applyEffect_131(targets);
				break;
			case 132://Enleve les envoutements
				applyEffect_132(targets, fight);
				break;
			case 138://%dom
				applyEffect_138(targets);
				break;
			case 140://Passer le tour
				applyEffect_140(targets);
				break;
			case 141://Tue la cible
				applyEffect_141(fight, targets);
				break;
			case 142://Dommages physique
				applyEffect_142(targets);
				break;
			case 143:// PDV rendu
				applyEffect_143(targets, fight);
				break;
			case 144:// - Dommages (pas bosté)
				applyEffect_144(targets);
			case 145://Malus Dommage
				applyEffect_145(targets);
				break;
			case 149://Change l'apparence
				applyEffect_149(fight, targets);
				break;
			case 150://Invisibilité
				applyEffect_150(fight, targets);
				break;
			case 152:// - Chance
				applyEffect_152(targets);
				break;
			case 153:// - Vita
				applyEffect_153(targets);
				break;
			case 154:// - Agi
				applyEffect_154(targets);
				break;
			case 155:// - Intel
				applyEffect_155(targets);
				break;
			case 156:// - Sagesse
				applyEffect_156(targets);
				break;
			case 157:// - Force
				applyEffect_157(targets);
				break;
			case 160:// + Esquive PA
				applyEffect_160(targets);
				break;
			case 161:// + Esquive PM
				applyEffect_161(targets);
				break;
			case 162:// - Esquive PA
				applyEffect_162(targets);
				break;
			case 163:// - Esquive PM
				applyEffect_163(fight, targets);
				break;
			case 164:// Daños reducidos en x%
				applyEffect_164(targets);
				break;
			case 165:// Maîtrises
				applyEffect_165();
				break;
			case 168://Perte PA non esquivable
				applyEffect_168(targets, fight);
				break;
			case 169://Perte PM non esquivable
				applyEffect_169(targets, fight);
				break;
			case 171://Malus CC
				applyEffect_171(targets);
				break;
			case 176:// + prospection
				applyEffect_176(targets);
				break;
			case 177:// - prospection
				applyEffect_177(targets);
				break;
			case 178:// + soin
				applyEffect_178(targets);
				break;
			case 179:// - soin
				applyEffect_179(targets);
				break;
			case 180://Double du sram
				applyEffect_180(fight);
				break;
			case 181://Invoque une créature
				applyEffect_181(fight);
				break;
			case 182://+ Crea Invoc
				applyEffect_182(targets);
				break;
			case 183://Resist Magique
				applyEffect_183(targets);
				break;
			case 184://Resist Physique
				applyEffect_184(targets);
				break;
			case 185://Invoque une creature statique
				applyEffect_185(fight);
				break;
			case 186://Diminue les dommages %
				applyEffect_186(targets);
				break;
			case 202://Perception
				applyEffect_202(fight, targets);
				break;
			case 210://Resist % terre
				applyEffect_210(fight, targets);
				break;
			case 211://Resist % eau
				applyEffect_211(targets);
				break;
			case 212://Resist % air
				applyEffect_212(targets);
				break;
			case 213://Resist % feu
				applyEffect_213(targets);
				break;
			case 214://Resist % neutre
				applyEffect_214(targets);
				break;
			case 215://Faiblesse % terre
				applyEffect_215(targets);
				break;
			case 216://Faiblesse % eau
				applyEffect_216(targets);
				break;
			case 217://Faiblesse % air
				applyEffect_217(targets);
				break;
			case 218://Faiblesse % feu
				applyEffect_218(targets);
				break;
			case 219://Faiblesse % neutre
				applyEffect_219(targets);
				break;
			case 220:// Renvoie dommage
				applyEffect_220(targets);
				break;
			case 265://Reduit les Dom de X
				applyEffect_265(targets);
				break;
			case 266://Vol Chance
				applyEffect_266(targets);
				break;
			case 267://Vol vitalité
				applyEffect_267(targets);
				break;
			case 268://Vol agitlité
				applyEffect_268(targets);
				break;
			case 269://Vol intell
				applyEffect_269(targets);
				break;
			case 270://Vol sagesse
				applyEffect_270(targets);
				break;
			case 271://Vol force
				applyEffect_271(targets);
				break;
			case 293://Augmente les dégâts de base du sort X de Y
				applyEffect_293();
				break;
			case 320://Vol de PO
				applyEffect_320(targets);
				break;
			case 400://Créer un  piège
				applyEffect_400(fight);
				break;
			case 401://Créer une glyphe
				applyEffect_401(fight);
				break;
			case 402://Glyphe des Blop
				applyEffect_402(fight);
				break;
			//case 606://Ancien chati
			//case 607:
			//case 608:
			//case 609:
			//case 611:
			//	applyEffect_606To611(targets, fight);
			//	break;
			case 666://Pas d'effet complémentaire
				break;
			case 671://Dommages : X% de la vie de l'attaquant (neutre)
				applyEffect_671(targets, fight);
				break;
			case 672://Dommages : X% de la vie de l'attaquant (neutre)
				applyEffect_672(targets, fight);
				break;
			case 765://Sacrifice
				applyEffect_765(targets);
				break;
			case 776://Enleve %vita pendant l'attaque
				applyEffect_776(targets);
				break;
			case 780://laisse spirituelle
				applyEffect_780(fight);
				break;
			case 781://Minimize les effets aléatoires
				applyEffect_781(targets);
				break;
			case 782://Maximise les effets aléatoires
				applyEffect_782(targets);
				break;
			case 783://Pousse jusqu'a la case visé
				applyEffect_783(fight);
				break;
			case 784://Raulebaque
				applyEffect_784(fight);
				break;
			case 786://Soin pendant l'attaque
				applyEffect_786(targets);
				break;
			case 787://Change etat
				applyEffect_787(targets);
				break;
			case 788://Chatiment de X sur Y tours
				applyEffect_788(targets);
				break;
			case 950://Etat X
				applyEffect_950(fight, targets);
				break;
			case 951://Enleve l'Etat X
				applyEffect_951(fight, targets);
				break;
			case 1000: // Glyph pair
				applyEffect_1000(fight);
				break;
			case 1001: // Glyph impair
				applyEffect_1001(fight);
				break;
			case 1002: // Glyph kaskargo
				applyEffect_1002(fight);
				break;
			default:
				GameServer.a();
				break;
		}
	}


	private void applyEffect_4(Fight fight) {
		if (turns > 1 || caster.getHoldedBy() != null || caster.getIsHolding() != null)
			return;//Olol bondir 3 tours apres ?

		if (cell.isWalkable(true, true, cell.getId()) && !fight.isOccuped(cell.getId()))//Si la case est prise, on va �viter que les joueurs se montent dessus *-*
		{
			caster.getCell().getFighters().clear();
			caster.setCell(cell);
			caster.getCell().addFighter(caster);

			ArrayList<Trap> P = (new ArrayList<Trap>());
			P.addAll(fight.getTraps());
			for (Trap p : P) {
				int dist = PathFinding.getDistanceBetween(fight.getMap(), p.getCell().getId(), caster.getCell().getId());
				//on active le piege
				if (dist <= p.getSize())
					p.onTraped(caster);
			}
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, caster.getId()
					+ "", caster.getId() + "," + cell.getId());
		}
	}

	private void applyEffect_5(ArrayList<Fighter> targets, Fight fight) {
		if (targets.size() == 1 && spell == 120 || spell == 310)
			if (!targets.get(0).isDead())
				caster.setOldCible(targets.get(0));

		if (turns <= 0) {
			switch (spell) {
				case 73://Piége répulsif
				case 418://Fléche de dispersion
				case 151://Soufle
				case 165://Flèche enflammé
					targets = this.sortTargets(targets, fight);
					break;
			}


			for (Fighter target : targets) {
				if (target.haveState(Constant.ETAT_ENRACINE))
					continue;

				GameCase cell = this.cell;

				if (target.getCell().getId() == this.cell.getId() || spell == 73)
					cell = caster.getCell();

				int newCellId = PathFinding.newCaseAfterPush(fight, cell, target.getCell(), value, spell == 1688);

				if (newCellId == 0)
					return;
				if (newCellId < 0) { // dérobase = 0 dmg
					int a = -newCellId;
					int factor = Formulas.getRandomJet(caster, target, "1d8+0"); // 2 à 9
					double level = caster.isInvocation() ? caster.getInvocator().getLvl() / 50.00 : caster.getLvl() / 50.00;

					if (level < 0.1) level = 0.1;

					int finalDmg = (int) ((8+ factor * level) * a);
					if (finalDmg < 1) finalDmg = 1;
					if (finalDmg > target.getPdv()) finalDmg = target.getPdv();

					if (target.hasBuff(184)) {
						finalDmg = finalDmg - target.getBuff(184).getValue();//Réduction physique
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(184).getValue());
					}
					if (target.hasBuff(105) && // poison passe à travers les réductions
							!(this.spell == 66 || spell == 71 ||spell == 196 || spell == 213 || spell == 181 || spell == 200)) {

						finalDmg = finalDmg - target.getBuff(105).getValue();//Immu
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(105).getValue());
					}
					if (finalDmg > 0) {
						if(finalDmg > 200) finalDmg = Formulas.getRandomValue(189, 211);
						target.removePdv(caster, finalDmg);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId() + "", target.getId() + ",-" + finalDmg);
						if (target.getPdv() <= 0) {
							fight.onFighterDie(target, target);//caster avant
							if (target.canPlay() && target.getPlayer() != null) fight.endTurn(false);
							else if (target.canPlay()) target.setCanPlay(false);
							return;
						}
					}
					a = value - a;
					// Flèche de dispersion, Harponnage
					GameCase launchCell = spell == 2028 || spell == 418 || spell == 987 ? cell : caster.getCell();
					newCellId = PathFinding.newCaseAfterPush(fight, launchCell, target.getCell(), a, spell == 73);

					char dir = PathFinding.getDirBetweenTwoCase(cell.getId(), target.getCell().getId(), fight.getMap(), true);
					GameCase nextCase = fight.getMap().getCase(PathFinding.GetCaseIDFromDirrection(target.getCell().getId(), dir, fight.getMap(), true));

					if (nextCase != null && nextCase.getFirstFighter() != null) {
						Fighter wallTarget = nextCase.getFirstFighter();
						finalDmg = finalDmg / 2;
						if (finalDmg < 1) finalDmg = 1;
						wallTarget.removePdv(caster, finalDmg);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId() + "", wallTarget.getId() + ",-" + finalDmg);
						if (wallTarget.getPdv() <= 0)
                            fight.onFighterDie(wallTarget, target);//caster avant
					}

					if (newCellId == 0)
						continue;
					if (fight.getMap().getCase(newCellId) == null)
						continue;
				}

				GameCase cacheCell = target.getCell();
				cacheCell.getFighters().clear();

				target.setCell(fight.getMap().getCase(newCellId));
				target.getCell().addFighter(target);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 5, target.getId() + "", target.getId() + "," + newCellId);

				Fighter holding = target.getIsHolding();
				if(holding != null) {
					holding.setState(Constant.ETAT_PORTE, 0);
					target.setState(Constant.ETAT_PORTEUR, 0);
					holding.setHoldedBy(null);
					target.setIsHolding(null);
					holding.setCell(cacheCell);
					cacheCell.addFighter(holding);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, target.getId() + "", target.getId() + "," + Constant.ETAT_PORTEUR + ",0");
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, holding.getId() + "", holding.getId() + "," + Constant.ETAT_PORTE + ",0");
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 51, target.getId() + "", cacheCell.getId() + "");
				}

				TimerWaiter.addNext(() -> fight.checkTraps(target), 750, TimeUnit.MILLISECONDS);

				if(fight.getType() == 7) {
					if(target.getMob() != null) {
						if(newCellId == 373 || newCellId == 359 || newCellId == 345) {
							fight.verifIfTeamBoufbowl(0);
							return;
						}
						if(newCellId == 105 || newCellId == 119 || newCellId == 133) {
							fight.verifIfTeamBoufbowl(1);
							return;
						}
					}
				}
			}
		}
	}

	private void applyEffect_6(ArrayList<Fighter> targets, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : targets) {
				if((target.getMob() != null && 556 == target.getMob().getTemplate().getId()) || target.haveState(Constant.ETAT_ENRACINE))
					continue;

				GameCase eCell = cell;
				//Si meme case
				if (target.getCell().getId() == cell.getId()) {
					//on prend la cellule caster
					eCell = caster.getCell();
				}
				int newCellID = PathFinding.newCaseAfterPush(fight, eCell, target.getCell(), -value);
				if (newCellID == 0)
					continue;

				if (newCellID < 0)//S'il a �t� bloqu�
				{
					int a = -(value + newCellID);
					newCellID = PathFinding.newCaseAfterPush(fight, caster.getCell(), target.getCell(), a);
					if (newCellID == 0)
						continue;
					if (fight.getMap().getCase(newCellID) == null)
						continue;
				}


				GameCase cacheCell = target.getCell();
				cacheCell.getFighters().clear();

				target.setCell(fight.getMap().getCase(newCellID));
				target.getCell().addFighter(target);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 5, caster.getId() + "", target.getId() + "," + newCellID);

				Fighter holding = target.getIsHolding();

				if(holding != null) {
					holding.setState(Constant.ETAT_PORTE, 0);
					target.setState(Constant.ETAT_PORTEUR, 0);
					holding.setHoldedBy(null);
					target.setIsHolding(null);
					holding.setCell(cacheCell);
					cacheCell.addFighter(holding);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, target.getId() + "", target.getId() + "," + Constant.ETAT_PORTEUR + ",0");
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, holding.getId() + "", holding.getId() + "," + Constant.ETAT_PORTE + ",0");
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 51, target.getId() + "", cacheCell.getId() + "");
				}

				TimerWaiter.addNext(() -> fight.checkTraps(target), 750, TimeUnit.MILLISECONDS);
			}
		}
	}

	private void applyEffect_8(ArrayList<Fighter> targets, Fight fight) {
		if (targets.isEmpty())
			return;
		Fighter target = targets.get(0);
		if (target == null || target.haveState(Constant.ETAT_PORTE) || target.haveState(Constant.ETAT_PORTEUR))
			return;

		if(caster.getIsHolding() != null || caster.getHoldedBy() != null
				|| target.getHoldedBy() != null || target.getIsHolding() != null)
			return;
		switch (spell) {
			case 438://Transpo
				//si les 2 joueurs ne sont pas dans la meme team, on ignore
				if (target.getTeam() != caster.getTeam())
					return;
				break;

			case 445://Coop
				//si les 2 joueurs sont dans la meme team, on ignore
				if (target.getTeam() == caster.getTeam())
					return;
				break;

			case 449://D�tour
				if(target.haveState(Constant.ETAT_ENRACINE))
					return;
				break;
			default:
				break;
		}
		//on enleve les persos des cases
		target.getCell().getFighters().clear();
		caster.getCell().getFighters().clear();
		//on retient les cases
		GameCase exTarget = target.getCell();
		GameCase exCaster = caster.getCell();
		//on �change les cases
		target.setCell(exCaster);
		caster.setCell(exTarget);
		//on ajoute les fighters aux cases
		target.getCell().addFighter(target);
		caster.getCell().addFighter(caster);
		ArrayList<Trap> P = (new ArrayList<>());
		P.addAll(fight.getTraps());
		for (Trap p : P) {
			int dist = PathFinding.getDistanceBetween(fight.getMap(), p.getCell().getId(), target.getCell().getId());
			int dist2 = PathFinding.getDistanceBetween(fight.getMap(), p.getCell().getId(), caster.getCell().getId());
			//on active le piege
			if (dist <= p.getSize())
				p.onTraped(target);
			else if (dist2 <= p.getSize())
				p.onTraped(caster);
		}
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, caster.getId() + "", target.getId() + "," + exCaster.getId());
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, caster.getId() + "", caster.getId() + "," + exTarget.getId());
	}

	private void applyEffect_9(ArrayList<Fighter> targets) {
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_50(Fight fight) {
		//Porter
		Fighter target = cell.getFirstFighter();
		if (target == null) return;
		if (target.getMob() != null)
			for (int i : Constant.STATIC_INVOCATIONS)
				if (i == target.getMob().getTemplate().getId())
					return;
		if (target.haveState(6)) return;//Stabilisation

		//on enleve le porté de sa case
		target.getCell().getFighters().clear();
		//on lui définie sa nouvelle case
		target.setCell(caster.getCell());

		//on applique les états
		target.setState(Constant.ETAT_PORTE, 1);
		caster.setState(Constant.ETAT_PORTEUR, 1);
		//on lie les 2 Fighter
		target.setHoldedBy(caster);
		caster.setIsHolding(target);

		//on envoie les packets
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", caster.getId() + "," + Constant.ETAT_PORTEUR + ",1");
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, target.getId() + "", target.getId() + "," + Constant.ETAT_PORTE + ",1");
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 50, caster.getId() + "", "" + target.getId());
	}

	private void applyEffect_51(final Fight fight) {
		//Si case pas libre
		if (!cell.isWalkable(true) || cell.getFighters().size() > 0) return;
		Fighter target = caster.getIsHolding();
		if (target == null) return;
		//if(target.isState(6))return;//Stabilisation
		//on ajoute le porté a sa case
		target.setCell(cell);
		target.getCell().addFighter(target);
		//on enleve les états
		target.setState(Constant.ETAT_PORTE, 0);
		caster.setState(Constant.ETAT_PORTEUR, 0);
		//on dé-lie les 2 Fighter
		target.setHoldedBy(null);
		caster.setIsHolding(null);

		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", caster.getId() + "," + Constant.ETAT_PORTEUR + ",0");
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, target.getId() + "", target.getId() + "," + Constant.ETAT_PORTE + ",0");
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 51, caster.getId() + "", cell.getId() + "");
		fight.checkTraps(target);
	}

	private void applyEffect_77(ArrayList<Fighter> targets, Fight fight) {
		int total = 0;

		for (Fighter target : targets) {
			int value = Formulas.getRandomJet(caster, target, jet);
			int pointsLost = Formulas.getPointsLost('a', value, caster, target);

			if (pointsLost < value)
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 309, caster.getId() + "", target.getId() + "," + (value - pointsLost));
			if (pointsLost < 1)
				continue;

			target.addBuff(Constant.STATS_REM_PM, pointsLost, turns == 0 ? 1 : turns, false, spell, args, caster, false, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_PM, caster.getId() + "", target.getId() + ",-" + pointsLost + "," + turns);
			total += pointsLost;
		}
		if (total != 0) {
			caster.addBuff(Constant.STATS_ADD_PM, total, turns, true, spell, args, caster, true, true);
			//Gain de PM pendant le tour de jeu
			if (caster.canPlay())
				caster.setCurPm(fight, total);
		}
	}

	private void applyEffect_78(ArrayList<Fighter> targets) { //Bonus PA
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_79(ArrayList<Fighter> targets) {
		if (turns < 1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, -1, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_81(ArrayList<Fighter> targets, Fight fight) {// healcion
		if (turns <= 0) {
			String[] jet = args.split(";");
			int heal = 0;
			if (jet.length < 6) {
				heal = 1;
			} else {
				heal = Formulas.getRandomJet(caster, null, jet[5]);
			}
			int heal2 = heal;
			for (Fighter cible : targets) {
				if (cible.isDead())
					continue;
				if (spell == 521)// ruse kistoune
					if (cible.getTeam2() != caster.getTeam2())
						continue;
				heal = getMaxMinSpell(cible, heal);
				int pdvMax = cible.getPdvMax();
				int healFinal = Formulas.calculFinalHealCac(caster, heal, false);
				if ((healFinal + cible.getPdv()) > pdvMax)
					healFinal = pdvMax - cible.getPdv();
				if (healFinal < 1)
					healFinal = 0;
				cible.removePdv(caster, -healFinal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, caster.getId()
						+ "", cible.getId() + "," + healFinal + "," + Constant.getColorByElement(Constant.ELEMENT_FEU));
				heal = heal2;
			}
		} else {
			for (Fighter target : targets) {
				if (!target.isDead()) {
					target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);
				}
			}
		}
	}

	private void applyEffect_82(ArrayList<Fighter> targets, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : targets) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);
				//si la cible a le buff renvoie de sort et que le sort peut etre renvoyer
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId() + "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_NEUTRE, dmg, false, false, spell);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_NEUTRE);//S'il y a des buffs sp�ciaux
				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage);
				//Vol de vie
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId() + "", caster.getId() + "," + heal);

				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, target);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_84(ArrayList<Fighter> targets, Fight fight) {
		int total = 0;

		for (Fighter target : targets) {
			int value = Formulas.getRandomJet(caster, target, jet);
			int pointsLost = Formulas.getPointsLost('a', value, caster, target);

			if (pointsLost < value)
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 308, caster.getId() + "", target.getId() + "," + (value - pointsLost));
			if (pointsLost < 1)
				continue;

			target.addBuff(Constant.STATS_REM_PA, pointsLost, turns == 0 ? 1 : turns, false, spell, args, caster, false, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_PA, caster.getId() + "", target.getId() + ",-" + pointsLost + "," + turns);
			total += pointsLost;
		}

		if (total != 0) {
			caster.addBuff(Constant.STATS_ADD_PA, total, 0, true, spell, args, caster, true, true);

			if (caster.canPlay()) {
				caster.setCurPa(fight, total);
			}
		}
	}

	private void applyEffect_85(ArrayList<Fighter> targets, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : targets) {

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId() + "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int resP = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_EAU);
				int resF = target.getTotalStats().getEffect(Constant.STATS_ADD_R_EAU);
				if (target.getPlayer() != null)//Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_EAU);
					resF += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_EAU);
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);//%age de pdv inflig�
				int val = caster.getPdv() / 100 * dmg;//Valeur des d�gats
				//retrait de la r�sist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;//Reduc %resis
				val -= reduc;
				if (val < 0)
					val = 0;

				val = applyOnHitBuffs(val, target, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs sp�ciaux

				if (val > target.getPdv())
					val = target.getPdv();//Target va mourrir
				target.removePdv(caster, val);
				this.checkLifeTree(target, val);
				val = -(val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + val);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_86(ArrayList<Fighter> targets, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : targets) {

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int resP = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_TER);
				int resF = target.getTotalStats().getEffect(Constant.STATS_ADD_R_TER);
				if (target.getPlayer() != null)//Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_TER);
					resF += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_TER);
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);//%age de pdv inflig�
				int val = caster.getPdv() / 100 * dmg;//Valeur des d�gats
				//retrait de la r�sist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;//Reduc %resis
				val -= reduc;
				if (val < 0)
					val = 0;

				val = applyOnHitBuffs(val, target, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs sp�ciaux

				if (val > target.getPdv())
					val = target.getPdv();//Target va mourrir
				target.removePdv(caster, val);
				this.checkLifeTree(target, val);
				val = -(val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + val + "," + Constant.ELEMENT_TERRE);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_87(ArrayList<Fighter> targets, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : targets) {

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId() + "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}

				int resP = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_AIR);
				int resF = target.getTotalStats().getEffect(Constant.STATS_ADD_R_AIR);
				if (target.getPlayer() != null)//Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_AIR);
					resF += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_AIR);
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);//%age de pdv inflig�
				int val = caster.getPdv() / 100 * dmg;//Valeur des d�gats
				//retrait de la r�sist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;//Reduc %resis
				val -= reduc;
				if (val < 0)
					val = 0;

				val = applyOnHitBuffs(val, target, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs sp�ciaux

				if (val > target.getPdv())
					val = target.getPdv();//Target va mourrir
				target.removePdv(caster, val);
				this.checkLifeTree(target, val);
				val = -(val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId() + "", target.getId() + "," + val);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_88(ArrayList<Fighter> targets, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : targets) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId() + "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int resP = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_FEU);
				int resF = target.getTotalStats().getEffect(Constant.STATS_ADD_R_FEU);
				if (target.getPlayer() != null)//Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_FEU);
					resF += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_FEU);
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);//%age de pdv inflig�
				int val = caster.getPdv() / 100 * dmg;//Valeur des d�gats
				//retrait de la r�sist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;//Reduc %resis
				val -= reduc;
				if (val < 0)
					val = 0;

				val = applyOnHitBuffs(val, target, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs sp�ciaux

				if (val > target.getPdv())
					val = target.getPdv();//Target va mourrir
				target.removePdv(caster, val);

				this.checkLifeTree(target, val);
				val = -(val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + val);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_89(ArrayList<Fighter> targets, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : targets) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int resP = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_NEU);
				int resF = target.getTotalStats().getEffect(Constant.STATS_ADD_R_NEU);
				if (target.getPlayer() != null)//Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_NEU);
					resF += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_NEU);
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);//%age de pdv inflig�
				int val = caster.getPdv() / 100 * dmg;//Valeur des d�gats
				//retrait de la r�sist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;//Reduc %resis
				val -= reduc;
				int armor = 0;
				for (SpellEffect SE : target.getBuffsByEffectID(105)) {
					int intell = target.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
					int carac = target.getTotalStats().getEffect(Constant.STATS_ADD_FORC);
					int value = SE.getValue();
					int a = value * (100 + intell / 2 + carac / 2) / 100;
					armor += a;
				}
				if (armor > 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + armor);
					val = val - armor;
				}
				if (val < 0) val = 0;
				val = applyOnHitBuffs(val, target, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs sp�ciaux

				if (val > target.getPdv()) val = target.getPdv();//Target va mourrir
				target.removePdv(caster, val);

				this.checkLifeTree(target, val);
				val = -(val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + val + "," + Constant.ELEMENT_NEUTRE);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, value, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_90(ArrayList<Fighter> targets, Fight fight) {
		if (turns <= 0)//Si Direct
		{
			int pAge = Formulas.getRandomJet(caster, null, args.split(";")[5]);
			int val = pAge * (caster.getPdv() / 100);
			//Calcul des Doms recus par le lanceur
			int finalDommage = applyOnHitBuffs(val, caster, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs sp�ciaux

			if (finalDommage > caster.getPdv())
				finalDommage = caster.getPdv();//Caster va mourrir
			caster.removePdv(caster, finalDommage);
			finalDommage = -(finalDommage);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
					+ "", caster.getId() + "," + finalDommage + "," + Constant.ELEMENT_NEUTRE);

			//Application du soin
			for (Fighter target : targets) {
				if ((val + target.getPdv()) > target.getPdvMax())
					val = target.getPdvMax() - target.getPdv();//Target va mourrir
				target.removePdv(caster, -val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + ",+" + val);
			}
			if (caster.getPdv() <= 0)
				fight.onFighterDie(caster, caster);
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_91(ArrayList<Fighter> targets, Fight fight,
								boolean isCaC)//vole eau
	{
		if (isCaC) {
			for (Fighter target : targets) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_EAU, dmg, false, true, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_EAU);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.getColorByElement(Constant.ELEMENT_EAU));
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
						+ "", caster.getId() + "," + heal);
				if (target.getMob() != null)
					checkMonsters(fight, target, 91);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : targets) {

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_EAU, dmg, false, false, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_EAU);//S'il y a des buffs sp�ciaux
				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.getColorByElement(Constant.ELEMENT_EAU));
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
						+ "", caster.getId() + "," + heal);
				if (target.getMob() != null)
					checkMonsters(fight, target, 91);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, target);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_92(ArrayList<Fighter> targets, Fight fight,
								boolean isCaC)//vole terre
	{
		if (caster.isHide())
			caster.unHide(spell);
		if (isCaC) {
			for (Fighter target : targets) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_TERRE, dmg, false, true, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_TERRE);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.ELEMENT_TERRE);
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
						+ "", caster.getId() + "," + heal);
				if (target.getMob() != null)
					checkMonsters(fight, target, 92);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			for (Fighter target : targets) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_TERRE, dmg, false, false, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_TERRE);//S'il y a des buffs sp�ciaux
				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.ELEMENT_TERRE);
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
						+ "", caster.getId() + "," + heal);
				if (target.getMob() != null)
					checkMonsters(fight, target, 92);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, target);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_93(ArrayList<Fighter> targets, Fight fight,
								boolean isCaC)//vole air
	{
		if (caster.isHide())
			caster.unHide(spell);
		if (isCaC) {
			for (Fighter target : targets) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_AIR, dmg, false, true, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_AIR);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.ELEMENT_AIR);
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
						+ "", caster.getId() + "," + heal);
				if (target.getMob() != null)
					checkMonsters(fight, target, 93);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			for (Fighter target : targets) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_AIR, dmg, false, false, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_AIR);//S'il y a des buffs sp�ciaux
				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.ELEMENT_AIR);

				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
						+ "", caster.getId() + "," + heal);
				if (target.getMob() != null)
					checkMonsters(fight, target, 93);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, target);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_94(ArrayList<Fighter> targets, Fight fight,
								boolean isCaC) {
		if (caster.isHide())
			caster.unHide(spell);
		if (isCaC)//CaC feu
		{
			for (Fighter target : targets) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_FEU, dmg, false, true, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_FEU);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.getColorByElement(Constant.ELEMENT_FEU));
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId() + "", caster.getId() + "," + heal);
				if (target.getMob() != null)
					checkMonsters(fight, target, 94);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			for (Fighter target : targets) {

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_FEU, dmg, false, false, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_FEU);//S'il y a des buffs sp�ciaux
				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.getColorByElement(Constant.ELEMENT_FEU));
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
						+ "", caster.getId() + "," + heal);
				if (target.getMob() != null)
					checkMonsters(fight, target, 94);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, target);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_95(ArrayList<Fighter> targets, Fight fight,
								boolean isCaC) {
		if (caster.isHide())
			caster.unHide(spell);
		if (isCaC)//CaC Eau
		{
			for (Fighter target : targets) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_NEUTRE, dmg, false, true, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_NEUTRE);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);

				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.ELEMENT_NEUTRE);
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
						+ "", caster.getId() + "," + heal);
				if (target.getMob() != null)
					checkMonsters(fight, target, 95);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			for (Fighter target : targets) {

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_NEUTRE, dmg, false, false, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_NEUTRE);//S'il y a des buffs sp�ciaux
				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.ELEMENT_NEUTRE);

				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
						+ "", caster.getId() + "," + heal);
				if (target.getMob() != null)
					checkMonsters(fight, target, 95);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, target);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_96(ArrayList<Fighter> targets, Fight fight,
								boolean isCaC)//dmg eau
	{
		if (isCaC)//CaC Eau
		{
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : targets) {
				if (caster.isMob() && (caster.getTeam2() == target.getTeam2())
						&& !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);

				//Si le sort est boost� par un buff sp�cifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_EAU, dmg, false, true, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_EAU);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);

				this.checkLifeTree(target, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.getColorByElement(Constant.ELEMENT_EAU));
				if (target.getMob() != null)
					checkMonsters(fight, target, 96);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : targets) {
				if (spell != 946 && caster.isMob() && (caster.getTeam2() == target.getTeam2()) && !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);

				//Si le sort est boost� par un buff sp�cifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_EAU, dmg, false, false, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_EAU);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);

				this.checkLifeTree(target, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.getColorByElement(Constant.ELEMENT_EAU));
				if (target.getMob() != null)
					checkMonsters(fight, target, 96);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if(!target.trapped) {
						if (target.canPlay() && target.getPlayer() != null)
							fight.endTurn(false);
						else if (target.canPlay())
							target.setCanPlay(false);
					}
				}
			}
		} else {
			for (Fighter target : targets)
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
		}
	}

	private void applyEffect_97(ArrayList<Fighter> targets, Fight fight,
								boolean isCaC)//dmg terre
	{
		if (isCaC)//CaC Terre
		{
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : targets) {
				if (caster.isMob()) {
					if (spell != 946 && caster.getTeam2() == target.getTeam2() && !caster.isInvocation())
						continue; // Les monstres de s'entretuent pas
				}

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);

				//Si le sort est boost� par un buff sp�cifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_TERRE, dmg, false, true, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_TERRE);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);

				this.checkLifeTree(target, finalDommage);

				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.ELEMENT_TERRE);
				if (target.getMob() != null)
					checkMonsters(fight, target, 97);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : targets) {
				if (spell != 946 &&caster.isMob() && (caster.getTeam2() == target.getTeam2()) && !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort

				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId() + "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);

				//Si le sort est boost� par un buff sp�cifique
				if (caster.hasBuff(293) || caster.haveState(300)) {
					if (caster.haveState(300))
						caster.setState(300, 0);
					for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
						if (SE == null)
							continue;
						if (SE.getValue() == spell) {
							int add = -1;
							try {
								add = Integer.parseInt(SE.getArgs().split(";")[2]);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (add <= 0)
								continue;
							dmg += add;
						}
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_TERRE, dmg, false, false, spell);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_TERRE);//S'il y a des buffs sp�ciaux
				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);

				this.checkLifeTree(target, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.ELEMENT_TERRE);
				if (target.getMob() != null)
					checkMonsters(fight, target, 97);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (!target.trapped) {
						if (target.canPlay() && target.getPlayer() != null)
							fight.endTurn(false);
						else if (target.canPlay())
							target.setCanPlay(false);
					}
				}
			}
		} else {
			if (spell == 470) {
				for (Fighter target : targets) {
					if (target.getTeam() == caster.getTeam())
						continue;
					target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
				}
			}
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_98(ArrayList<Fighter> targets, Fight fight,
								boolean isCaC)//dmg air
	{
		if (isCaC)//CaC Air
		{
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : targets) {
				if (spell != 946 && caster.isMob() && (caster.getTeam2() == target.getTeam2()) && !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);

				//Si le sort est boost� par un buff sp�cifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}

				// applyEffect_142

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_AIR, dmg, false, true, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_AIR);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);

				this.checkLifeTree(target, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.ELEMENT_AIR);
				if (target.getMob() != null)
					checkMonsters(fight, target, 98);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : targets) {
				if (spell != 946 &&caster.isMob() && (caster.getTeam2() == target.getTeam2()) && !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);

				//Si le sort est boost� par un buff sp�cifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_AIR, dmg, false, false, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_AIR);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir

				target.removePdv(caster, finalDommage);

				this.checkLifeTree(target, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.ELEMENT_AIR);
				if (target.getMob() != null)
					checkMonsters(fight, target, 98);

				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if(!target.trapped) {
						if (target.canPlay() && target.getPlayer() != null)
							fight.endTurn(false);
						else if (target.canPlay())
							target.setCanPlay(false);
					}
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_99(ArrayList<Fighter> targets, Fight fight,
								boolean isCaC)//dmg feu
	{
		if (caster.isHide())
			caster.unHide(spell);

		if (isCaC)//CaC Feu
		{
			for (Fighter target : targets) {
				if (spell != 946 && caster.isMob() && (caster.getTeam2() == target.getTeam2()) && !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);

				//Si le sort est boost� par un buff sp�cifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_FEU, dmg, false, true, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_FEU);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);

				this.checkLifeTree(target, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.getColorByElement(Constant.ELEMENT_FEU));
				if (target.getMob() != null)
					checkMonsters(fight, target, 99);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			for (Fighter target : targets) {
				if (spell != 946 && caster.isMob() && (caster.getTeam2() == target.getTeam2()) && !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas
				if (spell == 36 && target == caster)//Frappe du Craqueleur ne tape pas l'osa
				{
					continue;
				}

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);

				//Si le sort est boost� par un buff sp�cifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_FEU, dmg, false, false, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_FEU);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);

				this.checkLifeTree(target, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.getColorByElement(Constant.ELEMENT_FEU));
				if (target.getMob() != null)
					checkMonsters(fight, target, 99);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if(!target.trapped) {
						if (target.canPlay() && target.getPlayer() != null)
							fight.endTurn(false);
						else if (target.canPlay())
							target.setCanPlay(false);
					}
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_100(ArrayList<Fighter> targets, Fight fight,
								 boolean isCaC) {
		if (caster.isHide())
			caster.unHide(spell);
		if (fight.getType() == 7)
			return;
		if (isCaC)//CaC Neutre
		{
			for (Fighter target : targets) {
				if (caster.isMob() && (caster.getTeam2() == target.getTeam2())
						&& !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);

				//Si le sort est boost� par un buff sp�cifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_NEUTRE, dmg, false, true, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_NEUTRE);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);

				this.checkLifeTree(target, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.ELEMENT_NEUTRE);
				if (target.getMob() != null)
					checkMonsters(fight, target, 100);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPlayer() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			for (Fighter target : targets) {
				if (spell != 946 && caster.isMob() && (caster.getTeam2() == target.getTeam2()) && !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1"); // le lanceur devient donc la cible
					target = caster;
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);

				//Si le sort est boost� par un buff sp�cifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_NEUTRE, dmg, false, false, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_NEUTRE);//S'il y a des buffs sp�ciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				this.checkLifeTree(target, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + Constant.ELEMENT_NEUTRE);
				if (target.getMob() != null)
					checkMonsters(fight, target, 100);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if(!target.trapped) {
						if (target.canPlay() && target.getPlayer() != null)
							fight.endTurn(false);
						else if (target.canPlay())
							target.setCanPlay(false);
					}
				}
			}
		} else {
			for (Fighter target : targets) {
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
			}
		}
	}

	private void applyEffect_101(ArrayList<Fighter> targets, Fight fight) {
		for (Fighter target : targets) {
			if (target.hasBuff(788)) {
				SpellEffect buff = target.getBuff(788);
				if (buff != null && buff.getValue() == 101) {
					target.addBuff(111, value, buff.getTurn(), true, buff.getSpell(), args, target, false, true);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, String.valueOf(target.getId()), target.getId() + ",+" + value);
				}
			}

			if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
				// The caster become the target
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, String.valueOf(target.getId()), target.getId() + ",1");
				target = caster;
			}

			int value = Formulas.getRandomJet(caster, target, jet);
			int pointsLost = Formulas.getPointsLost('a', value, caster, target);

			if ((value - pointsLost) > 0) {
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 308, String.valueOf(caster.getId()), target.getId() + "," + (value - pointsLost));
			}
			if (pointsLost > 0) {
				target.addBuff(effectID, pointsLost, turns, false, spell, args, caster, false, true);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, String.valueOf(target.getId()), target.getId() + ",-" + pointsLost);
			}

			if (fight.getFighterByGameOrder() == target) {
				fight.setCurFighterPa(fight.getCurFighterPa() - pointsLost);
			}

			if (target.getMob() != null) {
				if (turns <= 0) {
					this.checkMonsters(fight, target, effectID);
				} else if (target.getMob().getTemplate().getId() == 1071) {
					// If "Rasboul" monster
					target.addBuff(111, value, turns, true, spell, args, target, false, true);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 111, String.valueOf(target.getId()), target.getId() + ",+" + value);
				}
			}
		}
	}

	private void applyEffect_105(ArrayList<Fighter> targets) {
		int val = Formulas.getRandomJet(caster, null, jet);
		if (val == -1) return;
		for (Fighter target : targets) {
			target.addBuff(effectID, val, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_106(ArrayList<Fighter> targets) {
		int val = -1;
		try {
			val = Integer.parseInt(args.split(";")[1]);//Niveau de sort max
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (val == -1)
			return;

		for (Fighter target : targets) {
			target.addBuff(effectID, val, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_107(ArrayList<Fighter> targets) {
		if (turns >= 1)
			for (Fighter target : targets)
				target.addBuff(effectID, value, turns, true, spell, args, caster, false, true);//on applique un buff
	}

	private void applyEffect_108(ArrayList<Fighter> targets, Fight fight, boolean isCaC) {// healcion
		if (spell == 441) return;
		if (isCaC) return;
		if (turns <= 0) {
			String[] jet = args.split(";");
			int heal;
			if (jet.length < 6) {
				heal = 1;
			} else {
				heal = Formulas.getRandomJet(caster, null, jet[5]);
			}
			int heal2 = heal;
			for (Fighter cible : targets) {
				if (cible.isDead())
					continue;
				if(cible.hasBuff(87) && cible.getBuff(87).getSpell() == 1009) {
					SpellEffect effect = cible.getBuff(87);
					int turns = effect.turns;
					effect.turns = 0;
					effect.applyEffect_87(new ArrayList<>(Collections.singletonList(cible)), fight);
					effect.turns = turns;
					continue;
				}
				if (caster.hasBuff(178))
					heal += caster.getBuffValue(178);
				if (caster.hasBuff(179))
					heal = heal - caster.getBuffValue(179);
				heal = getMaxMinSpell(cible, heal);
				int pdvMax = cible.getPdvMax();
				int healFinal = Formulas.calculFinalHealCac(caster, heal, false);
				if ((healFinal + cible.getPdv()) > pdvMax)
					healFinal = pdvMax - cible.getPdv();
				if (healFinal < 1)
					healFinal = 0;
				cible.removePdv(caster, -healFinal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, caster.getId() + "", cible.getId() + "," + healFinal);
				heal = heal2;
			}
		} else {
			targets.stream().filter(target -> !target.isDead()).forEach(target -> target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true));
		}
	}

	private void applyEffect_109(Fight fight)//Dommage pour le lanceur (fixes)
	{
		if (turns <= 0) {
			int dmg = Formulas.getRandomJet(caster, null, args.split(";")[5]);
			int finalDommage = Formulas.calculFinalDommage(fight, caster, caster, Constant.ELEMENT_NULL, dmg, false, false, spell);

			finalDommage = applyOnHitBuffs(finalDommage, caster, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs sp�ciaux
			if (finalDommage > caster.getPdv())
				finalDommage = caster.getPdv();//Caster va mourrir
			caster.removePdv(caster, finalDommage);
			finalDommage = -(finalDommage);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
					+ "", caster.getId() + "," + finalDommage + "," + Constant.ELEMENT_NEUTRE);

			if (caster.getPdv() <= 0) {
				fight.onFighterDie(caster, caster);

			}
		} else {
			caster.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
		}
	}

	private void applyEffect_110(ArrayList<Fighter> targets, Fight fight) {
		int val = Formulas.getRandomJet(caster, null, jet);
		if (val == -1) return;
		for (Fighter target : targets) {
			target.addBuff(effectID, val, turns, true, spell, args, caster, false, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId() + "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_111(ArrayList<Fighter> targets, Fight fight) {
		int val = Formulas.getRandomJet(caster, null, jet);
		if (val == -1) return;
		boolean repetibles = false;
		int lostPA = 0;
		for (Fighter target : targets) {
			if (spell == 115) {// odorat
				if (!repetibles) {
					lostPA = Formulas.getRandomJet(caster, target, jet);
					if (lostPA == -1)
						continue;
					value = lostPA;
				}
				target.addBuff(effectID, value, turns, true, spell, args, caster, false, true);
				repetibles = true;
				if (target.canPlay() && target == caster)
					target.setCurPa(fight, value);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId() + "", target.getId() + "," + value + "," + turns);
				continue;
			}

			if (spell == 521)// ruse kistoune
				if (target.getTeam2() != caster.getTeam2())
					continue;
			target.addBuff(effectID, val, turns, true, spell, args, caster, false, true);
			//Gain de PA pendant le tour de jeu
			if (target.canPlay() && target == caster)
				target.setCurPa(fight, val);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId() + "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_112(ArrayList<Fighter> targets, Fight fight) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if(value == -1) return;

		if (spell == 1090) {
			caster.addBuff(effectID, value, turns, true, spell, args, caster, false, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId() + "", caster.getId() + "," + value + "," + turns);
			return;
		}

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_114(ArrayList<Fighter> targets, Fight fight) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, false, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, String.valueOf(caster.getId()), target.getId() + "," + value + "," + turns);
		}
	}

	private void applyEffect_115(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_116(ArrayList<Fighter> targets) {//Malus PO
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_117(ArrayList<Fighter> targets) {//Bonus PO

		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
			//Gain de PO pendant le tour de jeu
			if (target.canPlay() && target == caster) {
				target.getTotalStats().addOneStat(Constant.STATS_ADD_PO, value);
			}
		}
	}

	private void applyEffect_118(ArrayList<Fighter> targets, Fight fight) {//Bonus Force
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		if (spell == 52) {//cupiditer
			targets = fight.getFighters(3);
		}

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_119(ArrayList<Fighter> targets) {//Bonus Agilit�
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_120(Fight fight)//Bonus PA
	{
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		caster.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		caster.setCurPa(fight, value);
	}

	private void applyEffect_121(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_122(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_123(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_124(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_125(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_126(ArrayList<Fighter> targets, Fight fight) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		if (spell == 52) { // Cupiditer
			targets = fight.getFighters(3);
		}

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_127(ArrayList<Fighter> targets, Fight fight) {
		for (Fighter target : targets) {
			int value = Formulas.getRandomJet(caster, target, jet);
			int pointsLost = Formulas.getPointsLost('m', value, caster, target);

			if ((value - pointsLost) > 0) {
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 309, String.valueOf(caster.getId()), target.getId() + "," + (value - pointsLost));
			}

			if (pointsLost > 0) {
				target.addBuff(effectID, pointsLost, turns, false, spell, args, caster, false, true);

				if (turns <= 1) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, String.valueOf(target.getId()), target.getId() + ",-" + pointsLost);
				}
				if (target.getMob() != null) {
					this.checkMonsters(fight, target, effectID);
				}
			}
		}
	}

	private void applyEffect_128(ArrayList<Fighter> targets, Fight fight) {
		int val = Formulas.getRandomJet(caster, null, jet);
		if (val == -1) return;
		boolean repetibles = false;
		int lostPM = 0;
		for (Fighter target : targets) {
			if (spell == 115) {// odorat
				if (!repetibles) {
					lostPM = Formulas.getRandomJet(caster, target, jet);
					if (lostPM == -1)
						continue;
					value = lostPM;
				}
				target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
				repetibles = true;
				if (target.canPlay() && target == caster)
					target.setCurPm(fight, value);
				continue;
			} else if (spell == 521)// ruse kistoune
				if (target.getTeam2() != caster.getTeam2())
					continue;

			target.addBuff(effectID, val, turns, true, spell, args, caster, true, true);
			//Gain de PM pendant le tour de jeu
			if (target.canPlay() && target == caster)
				target.setCurPm(fight, val);
		}
	}

	private void applyEffect_130(Fight fight, ArrayList<Fighter> targets) {
		if (turns <= 0) {
			for (Fighter target : targets) {
				int kamas = Formulas.getRandomJet(caster, target, args.split(";")[5]);
				if (caster.getPlayer() == null) break;
				if (fight.getType() != Constant.FIGHT_TYPE_CHALLENGE && target.getPlayer() != null) {
					target.getPlayer().addKamas(-kamas);
					if (target.getPlayer().getKamas() < 0)
						target.getPlayer().setKamas(0);
				} else {
					kamas = 0;
				}
				if (target.getMob() != null && target.getMob().getTemplate().getId() != 494) {
					caster.getPlayer().addKamas(kamas);
				} else {
					caster.getPlayer().addKamas(kamas);
				}

				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 130, caster.getId() + "", kamas + "");
			}
		} else {
			for (Fighter target : targets)
				target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);//on applique un buff
		}
	}

	private void applyEffect_131(ArrayList<Fighter> targets) {
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_132(ArrayList<Fighter> targets, Fight fight) {
		for (Fighter target : targets) {
			if (target.isHide())
				target.unHide(spell);
			target.debuff(this);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 132, String.valueOf(caster.getId()), String.valueOf(target.getId()));
		}
	}

	private void applyEffect_138(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_140(ArrayList<Fighter> targets) {
		for (Fighter target : targets) {
			target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_141(Fight fight, ArrayList<Fighter> targets) {
		for (Fighter target : targets) {
			Monster monster = target.getMob() != null ? target.getMob().getTemplate() : null;
			if(monster != null && monster.getId() == 1088)
				continue;
			if (target.hasBuff(765)) { // Sacrifice
				if (target.getBuff(765) != null && !target.getBuff(765).getCaster().isDead()) {
					applyEffect_765B(fight, target);
					target = target.getBuff(765).getCaster();
				}
			}

			target.setIsDead(true);
			fight.onFighterDie(target, caster);
		}
	}

	private void applyEffect_142(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		if(this.cell.getFirstFighter() == this.caster && !targets.contains(caster)) {
			targets.add(this.caster);
		}
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_143(ArrayList<Fighter> targets, Fight fight) {
		if (spell == 470) {
			String[] jet = args.split(";");
			int heal = 0;
			if (jet.length < 6) {
				heal = 1;
			} else {
				heal = Formulas.getRandomJet(caster, null, jet[5]);
			}
			int dmg2 = heal;
			for (Fighter cible : targets) {
				if (cible.getTeam() != caster.getTeam())
					continue;
				if (cible.isDead())
					continue;
				heal = getMaxMinSpell(cible, heal);
				int healFinal = Formulas.calculFinalHealCac(caster, heal, false);
				if ((healFinal + cible.getPdv()) > cible.getPdvMax())
					healFinal = cible.getPdvMax() - cible.getPdv();
				if (healFinal < 1)
					healFinal = 0;
				cible.removePdv(caster, -healFinal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, caster.getId()
						+ "", cible.getId() + "," + healFinal);
				heal = dmg2;
			}
			return;
		}
		if (turns <= 0) {
			String[] jet = args.split(";");
			int heal = 0;
			if (jet.length < 6) {
				heal = 1;
			} else {
				heal = Formulas.getRandomJet(caster, null, jet[5]);
			}
			int dmg2 = heal;
			for (Fighter cible : targets) {
				if (cible.isDead())
					continue;
				if(cible.hasBuff(87) && cible.getBuff(87).getSpell() == 1009) {
					SpellEffect effect = cible.getBuff(87);
					int turns = effect.turns;
					effect.turns = 0;
					effect.applyEffect_87(new ArrayList<>(Collections.singletonList(cible)), fight);
					effect.turns = turns;
					continue;
				}
				heal = getMaxMinSpell(cible, heal);
				int healFinal = Formulas.calculFinalHealCac(caster, heal, false);
				if (spell == 450) {
					healFinal = heal;
				}
				if ((healFinal + cible.getPdv()) > cible.getPdvMax())
					healFinal = cible.getPdvMax() - cible.getPdv();
				if (healFinal < 1)
					healFinal = 0;
				cible.removePdv(caster, -healFinal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, caster.getId()
						+ "", cible.getId() + "," + healFinal);
				heal = dmg2;
			}
		} else {
			for (Fighter cible : targets) {
				if (cible.isDead())
					continue;
				cible.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);
			}
		}
	}

	private void applyEffect_144(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		final int temp = value;
		for (Fighter target : targets) {
			value = getMaxMinSpell(target, value);
			target.addBuff(145, value, turns, true, spell, args, caster, true, true);
			value = temp;
		}
	}

	private void applyEffect_145(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_149(Fight fight, ArrayList<Fighter> targets) {
		int id = -1;

		try {
			id = Integer.parseInt(args.split(";")[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Fighter target : targets) {
			if (target.isDead())
				continue;
			if (spell == 686)
				if (target.getPlayer() != null
						&& target.getPlayer().getSexe() == 1
						|| target.getMob() != null
						&& target.getMob().getTemplate().getId() == 547)
					id = 8011;
			if (id == -1)
				id = target.getDefaultGfx();

			target.addBuff(effectID, id, turns, true, spell, args, caster, false, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId() + "", target.getId() + "," + target.getDefaultGfx() + "," + id + "," + (target.canPlay() ? turns + 1 : turns));
		}
	}

	private void applyEffect_150(Fight fight, ArrayList<Fighter> targets) {
		if(caster.getIsHolding() != null || caster.getHoldedBy() != null)
			return;
		if (turns == 0)
			return;

		if (spell == 547 || spell == 546 || spell == 548 || spell == 525) {
			caster.addBuff(effectID, 0, 3, true, spell, args, caster, false, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId() + "", caster.getId() + "," + (3 - 1));
			return;
		}

		for (Fighter target : targets) {
			if(target.getIsHolding() != null || target.getHoldedBy() != null || target.isHide())
				continue;
			target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId() + "", target.getId() + "," + (turns - 1));
		}
	}

	private void applyEffect_152(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1)
			return;
		int val2 = value;
		for (Fighter target : targets) {
			value = getMaxMinSpell(target, value);
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
			value = val2;
		}
	}

	private void applyEffect_153(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		final int temp = value;
		for (Fighter target : targets) {
			value = getMaxMinSpell(target, value);
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
			value = temp;
		}
	}

	private void applyEffect_154(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		int val2 = value;
		for (Fighter target : targets) {
			value = getMaxMinSpell(target, value);
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
			value = val2;
		}
	}

	private void applyEffect_155(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_156(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		int temp = value;
		for (Fighter target : targets) {
			value = getMaxMinSpell(target, value);
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
			value = temp;
		}
	}

	private void applyEffect_157(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		final int temp = value;
		for (Fighter target : targets) {
			value = getMaxMinSpell(target, value);
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
			value = temp;
		}
	}

	private void applyEffect_160(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_161(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_162(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_163(Fight fight, ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		if (targets.isEmpty() && spell == 310 && caster.getOldCible() != null) {
			SpellEffect effect = caster.getOldCible().addBuff(effectID, value, turns, true, spell, args, caster, false, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, String.valueOf(caster.getOldCible().getId()), caster.getOldCible().getId() + "," + value + "," + effect.getTurn());
		}
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_164(ArrayList<Fighter> targets) {
		if (value == -1)
			return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_165() {
		int value = -1;
		try {
			value = Integer.parseInt(args.split(";")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (value == -1)
			return;
		caster.addBuff(effectID, value, turns, true, spell, args, caster, false, true);
	}

	private void applyEffect_168(ArrayList<Fighter> targets, Fight fight) {// - PA, no esquivables
		int lostPA;
		boolean repetibles = false;

		for (Fighter target : targets) {
			if (target.isDead())
				continue;
			if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
				// The caster become the target
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId() + "", target.getId() + ",1");
				target = caster;
			}

			if (spell == 115) {// Odorat
				if (!repetibles) {
					lostPA = Formulas.getRandomJet(caster, target, jet);
					if (lostPA == -1)
						continue;
					value = lostPA;
				}
				repetibles = true;
			}

			target.addBuff(effectID, value, turns, true, spell, args, caster, false, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, target.getId() + "", target.getId() + ",-" + value);

			if (fight.getFighterByGameOrder() == target) {
				fight.setCurFighterPa(fight.getCurFighterPa() - value);
			}
			if (target.getMob() != null) {
				checkMonsters(fight, target, effectID);
			}
		}
	}


	private void applyEffect_169(ArrayList<Fighter> targets, Fight fight) { // - PM, no esquivables
		if (spell == 686 && caster.haveState(Constant.ETAT_SAOUL)) // anti bug saoul
			return;

		if (targets.isEmpty() && spell == 120 && caster.getOldCible() != null) {
			caster.getOldCible().addBuff(effectID, value, turns, false, spell, args, caster, false, true);
			if (turns <= 1) {
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, String.valueOf(caster.getOldCible().getId()), caster.getOldCible().getId() + ",-" + value);
			}
		}

		int lostPM;
		boolean repetibles = false;

		for (Fighter target : targets) {
			if (target.isDead())
				continue;
			if (spell == 115) { // Odorat
				if (!repetibles) {
					lostPM = Formulas.getRandomJet(caster, target, jet);
					if (lostPM == -1) continue;
					value = lostPM;
				}
				repetibles = true;
			}

			target.addBuff(effectID, value, turns, true, spell, args, caster, false, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, String.valueOf(target.getId()), target.getId() + ",-" + value);

			if (fight.getFighterByGameOrder() == target) {
				fight.setCurFighterPm(fight.getCurFighterPm() - value);
			}
			if (target.getMob() != null) {
				checkMonsters(fight, target, effectID);
			}
		}
	}

	private void applyEffect_171(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_176(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_177(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_178(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		final int temp = value;
		for (Fighter target : targets) {
			value = getMaxMinSpell(target, value);
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
			value = temp;
		}
	}

	private void applyEffect_179(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		final int temp = value;
		for (Fighter target : targets) {
			value = getMaxMinSpell(target, value);
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
			value = temp;
		}
	}

	private void applyEffect_180(Fight fight)//invocation
	{
		int cell = this.cell.getId();
		if (!this.cell.getFighters().isEmpty())
			return;
		int id = fight.getNextLowerFighterGuid();
		Player clone = Player.ClonePerso(caster.getPlayer(), -id - 10000, (caster.getPlayer().getMaxPdv() - ((caster.getLvl() - 1) * 5 + 50)));
		clone.setFight(fight);

		Fighter fighter = new Fighter(fight, clone);
		fighter.fullPdv();
		fighter.setTeam(caster.getTeam());
		fighter.setInvocator(caster);

		fight.getMap().getCase(cell).addFighter(fighter);
		fighter.setCell(fight.getMap().getCase(cell));

		fight.getOrderPlaying().add((fight.getOrderPlaying().indexOf(caster) + 1), fighter);
		fight.addFighterInTeam(fighter, caster.getTeam());

		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 180, caster.getId() + "", fighter.getGmPacket('+', true).substring(3));
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", fight.getGTL());

		fight.checkTraps(fighter);
	}

	private void applyEffect_181(Fight fight)//invocation
	{
		int cell = this.cell.getId();

		if (!this.cell.getFighters().isEmpty() || caster.nbrInvoc >= caster.getTotalStats().get(Constant.STATS_CREATURE))
			return;

		int id = -1, level = -1;

		try {
			String mobs = args.split(";")[0], levels = args.split(";")[1];

			if (mobs.contains(":")) {
				String[] split = mobs.split(":");
				id = Integer.parseInt(split[Formulas.getRandomValue(0, split.length - 1)]);
			} else {
				id = Integer.parseInt(mobs);
			}

			if (levels.contains(":")) {
				String[] split = levels.split(":");
				level = Integer.parseInt(split[Formulas.getRandomValue(0, split.length - 1)]);
			} else {
				level = Integer.parseInt(levels);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		MonsterGrade MG = null;
		Monster monster = World.world.getMonstre(id);
		if(monster == null) return;
		try {
			MG = monster.getGradeByLevel(level);
			if(MG == null) {
				MG = monster.getRandomGrade();
				if(MG == null) return;
			}
			MG = MG.getCopy();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (id == -1 || level == -1 || MG == null)
			return;

		MG.setInFightID(fight.getNextLowerFighterGuid());
		MG.setStatsInvocations(caster, id); // Augmenter les statistiques uniquement pour les invocations de personnages
		Fighter F = new Fighter(fight, MG);
		F.setTeam(caster.getTeam());
		F.setInvocator(caster);
		fight.getMap().getCase(cell).addFighter(F);
		F.setCell(fight.getMap().getCase(cell));
		fight.getOrderPlaying().add((fight.getOrderPlaying().indexOf(caster) + 1), F);
		fight.addFighterInTeam(F, caster.getTeam());
		String gm = F.getGmPacket('+', true).substring(3);
		String gtl = fight.getGTL();

		TimerWaiter.addNext(() -> {
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 181, caster.getId() + "", gm);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", gtl);
			caster.nbrInvoc++;
			fight.checkTraps(F);
		}, 100, TimeUnit.MILLISECONDS);
	}

	private void applyEffect_182(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_183(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_184(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_185(Fight fight) {
		int monster = -1, level = -1;

		try {
			monster = Integer.parseInt(args.split(";")[0]);
			level = Integer.parseInt(args.split(";")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		MonsterGrade monsterGrade;

		try {
			monsterGrade = World.world.getMonstre(monster).getGradeByLevel(level).getCopy();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		if (monster == -1 || level == -1 || monsterGrade == null)
			return;
		monsterGrade.setStatsInvocations(this.caster, monster);

		int id = fight.getNextLowerFighterGuid();
		monsterGrade.setInFightID(id);

		Fighter fighter = new Fighter(fight, monsterGrade);

		if (monster == 282 && this.caster.getPlayer() != null) {
			float factor = (1.0F + (caster.getLvl()) / 100.0F);
			fighter.setPdvMax(Math.round(monsterGrade.getPdvMax() * factor));
			fighter.setPdv(fighter.getPdvMax());
		}
		fighter.setTeam(this.caster.getTeam());
		fighter.setInvocator(this.caster);
		fighter.setState(Constant.ETAT_ENRACINE, 9999);

		fight.getMap().getCase(this.cell.getId()).addFighter(fighter);
		fighter.setCell(fight.getMap().getCase(this.cell.getId()));
		fight.addFighterInTeam(fighter, this.caster.getTeam());
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 185, this.caster.getId() + "", fighter.getGmPacket('+', true).substring(3));
	}

	private void applyEffect_186(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		for (Fighter target : targets) {
			value = getMaxMinSpell(target, value);
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_202(Fight fight, ArrayList<Fighter> targets) {
		if (spell == 113 || spell == 64) {
			for (Fighter target : targets) {
				if (target.isHide() && target != caster) {
					target.unHide(spell);
				}
			}
	
			for (Trap trap : fight.getTraps()) {
				trap.setIsUnHide(caster);
				trap.appear(caster);
			}
		}
	}

	private void applyEffect_210(Fight fight, ArrayList<Fighter> targets) {
		if (spell == 686 && caster.haveState(1))//anti bug saoul
		{
			int pa = 1;
			if (this.spellLvl == 5)
				pa = 2;
			else if (this.spellLvl == 4)
				pa = 3;
			else if (this.spellLvl == 3 || this.spellLvl == 2)
				pa = 4;
			else if (this.spellLvl == 1)
				pa = 5;

			caster.addBuff(111, pa, -1, true, spell, args, caster, false, true);
			//Gain de PA pendant le tour de jeu
			caster.setCurPa(fight, pa);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 111, caster.getId() + "", caster.getId() + "," + pa + "," + -1);
			return;
		}
		
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, spell != 2005, true);
		}
	}

	private void applyEffect_211(ArrayList<Fighter> targets) {
		if (spell == 686 && caster.haveState(1))//anti bug saoul
			return;
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, spell != 2005, true);
		}
	}

	private void applyEffect_212(ArrayList<Fighter> targets) {
		if (spell == 686 && caster.haveState(1))//anti bug saoul
			return;
		
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, spell != 2005, true);
		}
	}

	private void applyEffect_213(ArrayList<Fighter> targets) {
		if (spell == 686 && caster.haveState(1))//anti bug saoul
			return;
		
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, spell != 2005, true);
		}
	}

	private void applyEffect_214(ArrayList<Fighter> targets) {
		if (spell == 686 && caster.haveState(1))//anti bug saoul
			return;
		
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, spell != 2005, true);
		}
	}

	private void applyEffect_215(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_216(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_217(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;
		
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_218(ArrayList<Fighter> targets) {
		int val = Formulas.getRandomJet(caster, null, jet);
		if (val == -1) return;
		
		for (Fighter target : targets) {
			target.addBuff(effectID, val, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_219(ArrayList<Fighter> targets) {
		int val = Formulas.getRandomJet(caster, null, jet);
		if (val == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, val, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_220(ArrayList<Fighter> targets) {
		if (turns < 1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_265(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_266(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		int steal = 0;

		for (Fighter target : targets) {
			target.addBuff(Constant.STATS_REM_CHAN, value, turns, true, spell, args, caster, true, true);
			steal += value;
		}

		if (steal == 0) return;
		caster.addBuff(Constant.STATS_ADD_CHAN, steal, turns, true, spell, args, caster, true, true);
	}

	private void applyEffect_267(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		int steal = 0;

		for (Fighter target : targets) {
			target.addBuff(Constant.STATS_REM_VITA, value, turns, true, spell, args, caster, true, true);
			steal += value;
		}

		if (steal == 0) return;
		caster.addBuff(Constant.STATS_ADD_VITA, steal, turns, true, spell, args, caster, true, true);
	}

	private void applyEffect_268(ArrayList<Fighter> targets) {
		int val = Formulas.getRandomJet(caster, null, jet);
		int steal = 0;

		for (Fighter target : targets) {
			target.addBuff(Constant.STATS_REM_AGIL, val, turns, true, spell, args, caster, true, true);
			steal += val;
		}

		if (steal == 0) return;

		caster.addBuff(Constant.STATS_ADD_AGIL, steal, turns, true, spell, args, caster, true, true);
	}

	private void applyEffect_269(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		int steal = 0;

		for (Fighter target : targets) {
			target.addBuff(Constant.STATS_REM_INTE, value, turns, true, spell, args, caster, true, true);
			steal += value;
		}

		if (steal == 0) return;
		caster.addBuff(Constant.STATS_ADD_INTE, steal, turns, true, spell, args, caster, true, true);
	}

	private void applyEffect_270(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		int steal = 0;

		for (Fighter target : targets) {
			target.addBuff(Constant.STATS_REM_SAGE, value, turns, true, spell, args, caster, true, true);
			steal += value;
		}

		if (steal == 0) return;
		caster.addBuff(Constant.STATS_ADD_SAGE, steal, turns, true, spell, args, caster, true, true);
	}

	private void applyEffect_271(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		int steal = 0;

		for (Fighter target : targets) {
			target.addBuff(Constant.STATS_REM_FORC, value, turns, true, spell, args, caster, true, true);
			steal += value;
		}

		if (steal == 0) return;
		caster.addBuff(Constant.STATS_ADD_FORC, steal, turns, true, spell, args, caster, true, true);
	}

	private void applyEffect_293() {
		caster.addBuff(effectID, value, turns, false, spell, args, caster, false, true);
		caster.setState(300, turns + 1);
	}

	private void applyEffect_320(ArrayList<Fighter> targets) {
		int total = 0;

		for (Fighter target : targets) {
			target.addBuff(Constant.STATS_REM_PO, value, turns, true, spell, args, caster, true, true);
			total += value;
		}

		if (total != 0) {
			caster.addBuff(Constant.STATS_ADD_PO, total, turns, true, spell, args, caster, true, true);

			if (caster.canPlay()) {
				caster.getTotalStats().addOneStat(Constant.STATS_ADD_PO, total);
			}
		}
	}

	private void applyEffect_400(Fight fight) {
		if (!cell.isWalkable(true) && cell.getFirstFighter() != null && !cell.getFirstFighter().isHide())
			return;

		for (Trap trap : fight.getTraps()) {
			if (trap.getCell().getId() == cell.getId()) {
				return;
			}
		}

		String[] infos = args.split(";");
		int spellID = Short.parseShort(infos[0]);
		int level = Byte.parseByte(infos[1]);
		String po = World.world.getSort(spell).getStatsByLevel(spellLvl).getPorteeType();
		byte size = (byte) World.world.getCryptManager().getIntByHashedValue(po.charAt(1));
		SortStats TS = World.world.getSort(spellID).getStatsByLevel(level);
		Trap g = new Trap(fight, caster, cell, size, TS, spell);
		fight.getTraps().add(g);
		int unk = g.getColor();
		int team = caster.getTeam() + 1;

		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, team, 999, String.valueOf(caster.getId()), "GDZ+" + cell.getId() + ";" + size + ";" + unk);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, team, 999, String.valueOf(caster.getId()), "GDC" + cell.getId() + ";Haaaaaaaaz3005;");
	}

	private void applyEffect_401(Fight fight) {
		if (!cell.isWalkable(false) && cell.getFirstFighter() != null)
			return;

		String[] infos = args.split(";");
		int spellID = Short.parseShort(infos[0]);
		int level = Byte.parseByte(infos[1]);
		byte duration = Byte.parseByte(infos[3]);
		String po = World.world.getSort(spell).getStatsByLevel(spellLvl).getPorteeType();
		byte size = (byte) World.world.getCryptManager().getIntByHashedValue(po.charAt(1));
		SortStats TS = World.world.getSort(spellID).getStatsByLevel(level);
		Glyph g = new Glyph(fight, caster, cell, size, TS, duration, spell);
		fight.getGlyphs().add(g);
		int unk = g.getColor();

		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, String.valueOf(caster.getId()), "GDZ+" + cell.getId() + ";" + size + ";" + unk);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, String.valueOf(caster.getId()), "GDC" + cell.getId() + ";Haaaaaaaaa3005;");
	}

	private void applyEffect_402(Fight fight) {
		if (!cell.isWalkable(true))
			return;

		String[] infos = args.split(";");
		int spellID = Short.parseShort(infos[0]);
		int level = Byte.parseByte(infos[1]);
		byte duration = Byte.parseByte(infos[3]);
		String po = World.world.getSort(spell).getStatsByLevel(spellLvl).getPorteeType();
		byte size = (byte) World.world.getCryptManager().getIntByHashedValue(po.charAt(1));
		SortStats TS = World.world.getSort(spellID).getStatsByLevel(level);
		Glyph g = new Glyph(fight, caster, cell, size, TS, duration, spell);
		fight.getGlyphs().add(g);
		int unk = g.getColor();

		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, String.valueOf(caster.getId()), "GDZ+" + cell.getId() + ";" + size + ";" + unk);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, String.valueOf(caster.getId()), "GDC" + cell.getId() + ";Haaaaaaaaa3005;");
	}

	private void applyEffect_671(ArrayList<Fighter> targets, Fight fight) {
		if (turns <= 0) {
			for(Fighter target : targets) {
				if (target.hasBuff(765)) {
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0 && !this.isPoison()) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId() + "", target.getId() + ",1");
					target = caster;
				}
				int resP = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_NEU), resF = target.getTotalStats().getEffect(Constant.STATS_ADD_R_NEU);

				if (target.getPlayer() != null) {
					resP += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_NEU);
					resF += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_NEU);
				}

				int dmg = Formulas.getRandomJet(caster, target, args.split(";")[5]);// % de pdv
				dmg = getMaxMinSpell(target, dmg);
				int val = caster.getPdv() / 100 * dmg;// Valor de da�os
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;// Reduc
				// %resis
				val -= reduc;
				if (val < 0)
					val = 0;
				val = applyOnHitBuffs(val, target, caster, fight, Constant.ELEMENT_NULL);
				if (val > target.getPdv())
					val = target.getPdv();
				target.removePdv(caster, val);

				this.checkLifeTree(target, val);
				val = -(val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId() + "", target.getId() + "," + val + "," + Constant.ELEMENT_NEUTRE);
			}
		} else {
			caster.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_672(ArrayList<Fighter> targets, Fight fight) {
		//Punition
		//Formule de barge ? :/ Clair que ca punie ceux qui veulent l'utiliser x_x
		double val = ((double) Formulas.getRandomJet(caster, null, jet) / (double) 100);
		int pdvMax = caster.getPdvMaxOutFight();
		double pVie = (double) caster.getPdv() / (double) caster.getPdvMax();
		double rad = (double) 2 * Math.PI * (double) (pVie - 0.5);
		double cos = Math.cos(rad);
		double taux = (Math.pow((cos + 1), 2)) / (double) 4;
		double dgtMax = val * pdvMax;
		int dgt = (int) (taux * dgtMax);

		for (Fighter target : targets) {

			if (target.hasBuff(765))//sacrifice
			{
				if (target.getBuff(765) != null
						&& !target.getBuff(765).getCaster().isDead()) {
					applyEffect_765B(fight, target);
					target = target.getBuff(765).getCaster();
				}
			}
			//si la cible a le buff renvoie de sort
			if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && !this.isPoison()) {
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
						+ "", target.getId() + ",1");
				//le lanceur devient donc la cible
				target = caster;
			}

			int dmg = applyOnHitBuffs(dgt, target, caster, fight, Constant.ELEMENT_NEUTRE);//S'il y a des buffs sp�ciaux
			int neutralResistance = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_NEU), rem;
			if (neutralResistance > 2) {
				rem = (dmg * neutralResistance) / 100;
				dmg = dmg - rem;
			}
			if (neutralResistance < -2) {
				rem = ((-dmg) * (-neutralResistance)) / 100;
				dmg = dmg + rem;
			}
			if (target.hasBuff(105) && !(this.spell == 66 || spell == 71 ||spell == 196 || spell == 213 || spell == 181 || spell == 200)) {
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(105).getValue());
				int value = dmg - target.getBuff(105).getValue();
				dmg = Math.max(value, 0);
			}

			if (dmg > target.getPdv())
				dmg = target.getPdv();//Target va mourrir
			target.removePdv(caster, dmg);
			dmg = -(dmg);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId() + "", target.getId() + "," + dmg + "," + Constant.ELEMENT_NEUTRE);

			if (target.getPdv() <= 0) {
				fight.onFighterDie(target, target);
				if (target.canPlay() && target.getPlayer() != null)
					fight.endTurn(false);
				else if (target.canPlay())
					target.setCanPlay(false);
			}
		}
	}

	private void applyEffect_765(ArrayList<Fighter> targets) {
		for (Fighter target : targets) {
			target.addBuff(effectID, 0, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_765B(Fight fight, Fighter target) {
		Fighter sacrified = target.getBuff(765).getCaster();

		if(sacrified.getIsHolding() != null || sacrified.getHoldedBy() != null
				|| target.getHoldedBy() != null || target.getIsHolding() != null)
			return;

		GameCase cell1 = sacrified.getCell();
		GameCase cell2 = target.getCell();

		sacrified.getCell().getFighters().clear();
		target.getCell().getFighters().clear();
		sacrified.setCell(cell2);
		sacrified.getCell().addFighter(sacrified);
		target.setCell(cell1);
		target.getCell().addFighter(target);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, target.getId() + "", target.getId() + "," + cell1.getId());
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, sacrified.getId() + "", sacrified.getId() + "," + cell2.getId());
	}

	private void applyEffect_776(ArrayList<Fighter> targets) {
		int value = Formulas.getRandomJet(caster, null, jet);
		if (value == -1) return;

		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, true, true);
		}
	}

	private void applyEffect_780(Fight fight) {
		Fighter target = null, targetPlayer = null;

		for (Fighter fighter : fight.getDeadList().values()) {
			if (!fighter.hasLeft() && fighter.getTeam() == caster.getTeam()) {
				if(fighter.getPlayer() != null)
					targetPlayer = fighter;
				target = fighter;
			}
		}

		if(targetPlayer != null)
			target = targetPlayer;
		if (target == null)
			return;

		target.setInvocator(caster);
		fight.addFighterInTeam(target, target.getTeam());
		target.setIsDead(false);
		target.getFightBuff().clear();
		if(fight.getOrderPlaying().contains(target)) {
			fight.getOrderPlaying().remove(target);
			fight.getOrderPlaying().add((fight.getOrderPlaying().indexOf(target.getInvocator()) + 1), target);
			fight.setCurPlayer(fight.getOrderPlaying().indexOf(caster));
		} else if (target.isInvocation())
			fight.getOrderPlaying().add((fight.getOrderPlaying().indexOf(target.getInvocator()) + 1), target);

		target.nbrInvoc = 0;
		target.setCell(cell);
		target.getCell().addFighter(target);

		target.fullPdv();
		int percent = (100 - value) * target.getPdvMax() / 100;
		target.removePdv(caster, percent);

		String gm = target.getGmPacket('+', true).substring(3);
		String gtl = fight.getGTL();
		SocketManager.GAME_SEND_GIC_PACKETS_TO_FIGHT(fight, 7);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 780, target.getId() + "", gm);
		SocketManager.GAME_SEND_GTL_PACKET_TO_FIGHT(fight, 7);
		SocketManager.GAME_SEND_GTM_PACKET_TO_FIGHT(fight, 7);
		//SocketManager.GAME_SEND_FIGHT_PLAYER_JOIN(fight, 7, target);

		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, target.getId() + "", gtl);
		if (!target.isInvocation())
			SocketManager.GAME_SEND_STATS_PACKET(target.getPlayer());

		fight.removeDead(target);
	}

	private void applyEffect_781(ArrayList<Fighter> targets) {
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, debuffable, spell, args, caster, false, true);
		}
	}

	private void applyEffect_782(ArrayList<Fighter> targets) {
		if(targets.size() > 1) {
			targets.remove(this.caster);
		}
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, debuffable, spell, args, caster, false, true);
		}
	}

	private void applyEffect_783(Fight fight) {
		GameCase casterCell = caster.getCell();
		char dir = PathFinding.getDirBetweenTwoCase(casterCell.getId(), cell.getId(), fight.getMap(), true);
		int targetCellId = PathFinding.GetCaseIDFromDirrection(casterCell.getId(), dir, fight.getMap(), true);
		GameCase targetCell = fight.getMap().getCase(targetCellId);

		if (targetCell == null || targetCell.getFighters().isEmpty())
			return;

		Fighter target = targetCell.getFirstFighter();

		if(target.haveState(Constant.ETAT_ENRACINE))
			return;
		if (target.getMob() != null)
			for (int i : Constant.STATIC_INVOCATIONS)
				if (i == target.getMob().getTemplate().getId())
					return;

		int limit = 0;
		while (PathFinding.GetCaseIDFromDirrection(targetCellId, dir, fight.getMap(), true) != cell.getId()) {
			if (PathFinding.GetCaseIDFromDirrection(targetCellId, dir, fight.getMap(), true) == -1)
				return;
			targetCellId = PathFinding.GetCaseIDFromDirrection(targetCellId, dir, fight.getMap(), true);
			limit++;
			if (limit > 50)
				return;
		}

		GameCase newCell = PathFinding.checkIfCanPushEntity(fight, casterCell.getId(), cell.getId(), dir);
		if (newCell != null) cell = newCell;

		target.getCell().getFighters().clear();
		target.setCell(cell);
		target.getCell().addFighter(target);

		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 5, String.valueOf(caster.getId()), target.getId() + "," + cell.getId());
		TimerWaiter.addNext(() -> fight.checkTraps(target), 750, TimeUnit.MILLISECONDS);
	}

	private void applyEffect_784(Fight fight) {
		Map<Integer, GameCase> origPos = fight.getRholBack(); // les positions de début de combat

		ArrayList<Fighter> list = fight.getFighters(3); // on copie la liste des fighters
		for (int i = 1; i < list.size(); i++) {   // on boucle si tout le monde est à la place
			for (Fighter F : list) {
				if (F == null || F.isDead() || !origPos.containsKey(F.getId()) || F.getCell().getId() == origPos.get(F.getId()).getId()) {
					continue;
				}

				if (origPos.get(F.getId()).getFirstFighter() == null) {
					F.getCell().getFighters().clear();
					F.setCell(origPos.get(F.getId()));
					F.getCell().addFighter(F);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, F.getId() + "", F.getId() + "," + F.getCell().getId());
				}
			}
		}
	}

	private void applyEffect_786(ArrayList<Fighter> targets) {
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_787(ArrayList<Fighter> targets) {
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, true, spell, args, caster, false, true);
		}
	}

	private void applyEffect_788(ArrayList<Fighter> targets) {
		for (Fighter target : targets) {
			target.addBuff(effectID, value, turns, false, spell, args, target, false, true);
		}
	}

	private void applyEffect_950(Fight fight, ArrayList<Fighter> targets) {
		int id = -1;
		try {
			id = Integer.parseInt(args.split(";")[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (id == -1)
			return;
		if (id == 31 || id == 32 || id == 33 || id == 34) {
			for (Fighter mob : fight.getTeam1().values()) {
				mob.setState(id, turns);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", mob.getId() + "," + id + ",1");
				mob.addBuff(effectID, value, turns, false, spell, args, mob, false, true);
			}
		}else {
			for (Fighter target : targets) {
				if (spell == 139 && target.getTeam() != caster.getTeam())//Mot d'altruisme on saute les ennemis ?
					continue;
				if (turns <= 0) {
					target.setState(id, turns);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + id + ",1");
				} else {
					target.setState(id, turns);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + id + ",1");
					target.addBuff(effectID, value, turns, false, spell, args, target, false, true);
				}
				if (spell == 686) {
					target.unHide(686);
				}
			}
		}
	}

	private void applyEffect_951(Fight fight, ArrayList<Fighter> targets) {
		int id = -1;
		try {
			id = Integer.parseInt(args.split(";")[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (id == -1)
			return;

		for (Fighter target : targets) {
			//Si la cible n'a pas l'�tat
			if (!target.haveState(id))
				continue;
			//on enleve l'�tat
			target.setState(id, 0);
			//on envoie le packet
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + id + ",0");
		}
	}

	private void applyEffect_1000(Fight fight) {
		String[] infos = args.split(";");
		int spellID = Short.parseShort(infos[0]);
		int level = Byte.parseByte(infos[1]);
		byte duration = 1;
		SortStats TS = World.world.getSort(spellID).getStatsByLevel(level);
		GameCase celll = null;
		int casenbr = 0;
		boolean quatorze = false;
		for (GameCase entry : fight.getMap().getCases()) {
			casenbr = casenbr + 1;

			if (casenbr == 14 && quatorze) {
				quatorze = false;
				casenbr = 0;
			}
			if (casenbr == 15) {
				quatorze = true;
				casenbr = 0;
			}
			if (quatorze)
				continue;
			celll = entry;
			if (celll == null)
				continue;
			switch (celll.getId()) {
				case 28:
				case 57:
				case 86:
				case 115:
				case 144:
				case 173:
				case 202:
				case 231:
				case 260:
				case 289:
				case 318:
				case 347:
				case 376:
				case 405:
				case 434:
				case 463:
					continue;
			}
			if (!celll.isWalkable(false))
				continue;
			Glyph g = new Glyph(fight, caster, celll, (byte) 0, TS, duration, spell);
			fight.getGlyphs().add(g);

			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", "GDZ+" + celll.getId() + ";" + 0 + ";" + g.getColor());
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", "GDC" + celll.getId() + ";Haaaaaaaaa3005;");
		}
	}

	private void applyEffect_1001(Fight fight) {
		String[] infos = args.split(";");
		int spellID = Short.parseShort(infos[0]);
		int level = Byte.parseByte(infos[1]);
		byte duration = 1;
		SortStats TS = World.world.getSort(spellID).getStatsByLevel(level);
		GameCase celll = null;
		int casenbr = 0;
		boolean quatorze = false;
		for (GameCase entry : fight.getMap().getCases()) {
			casenbr = casenbr + 1;

			if (casenbr == 14 && quatorze) {
				quatorze = false;
				casenbr = 0;
			}
			if (casenbr == 15) {
				quatorze = true;
				casenbr = 0;
			}
			if (!quatorze)
				continue;
			celll = entry;
			if (celll == null)
				continue;
			if (!celll.isWalkable(false))
				continue;
			Glyph g = new Glyph(fight, caster, celll, (byte) 0, TS, duration, spell);
			fight.getGlyphs().add(g);
			int unk = g.getColor();
			String str = "GDZ+" + celll.getId() + ";" + 0 + ";" + unk;
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", str);
			str = "GDC" + celll.getId() + ";Haaaaaaaaa3005;";
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", str);
		}
	}

	private void applyEffect_1002(Fight fight) {
		String[] infos = args.split(";");
		int spellID = Short.parseShort(infos[0]);
		int level = Byte.parseByte(infos[1]);
		byte duration = 100;
		SortStats TS = World.world.getSort(spellID).getStatsByLevel(level);

		if (cell.isWalkable(true) && !fight.isOccuped(cell.getId())) {
			caster.getCell().getFighters().clear();
			caster.setCell(cell);
			caster.getCell().addFighter(caster);
			new ArrayList<>(fight.getTraps()).stream().filter(trap -> PathFinding.getDistanceBetween(fight.getMap(), trap.getCell().getId(), caster.getCell().getId()) <= trap.getSize()).forEach(trap -> trap.onTraped(caster));
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, caster.getId() + "", caster.getId() + "," + cell.getId());
		}

		Glyph g = new Glyph(fight, caster, cell, (byte) 0, TS, duration, spell);
		fight.getGlyphs().add(g);
		int unk = g.getColor();
		String str = "GDZ+" + cell.getId() + ";" + 0 + ";" + unk;
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", str);
		str = "GDC" + cell.getId() + ";Haaaaaaaaa3005;";
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", str);
	}

	private ArrayList<Fighter> sortTargets(ArrayList<Fighter> targets, Fight fight) {
		ArrayList<Fighter> array = new ArrayList<>();
		int max = -1;
		int distance;

		for (Fighter f : targets) {
			distance = PathFinding.getDistanceBetween(fight.getMap(), this.cell.getId(), f.getCell().getId());
			if (distance > max)
				max = distance;
		}

		for (int i = max; i >= 0; i--) {
			Iterator<Fighter> it = targets.iterator();
			while (it.hasNext()) {
				Fighter f = it.next();
				distance = PathFinding.getDistanceBetween(fight.getMap(), this.cell.getId(), f.getCell().getId());
				if (distance == i) {
					array.add(f);
					it.remove();
				}
			}
		}

		return array;
	}

	public void checkMonsters(Fight fight, Fighter target, int effet) {
		switch (target.getMob().getTemplate().getId()) {
			case 232://meulou
				if (target.hasBuff(112)) {
					target.addBuff(112, 5, -1, true, spell, args, caster, false, true);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 112, caster.getId() + "", target.getId() + "," + 5 + "," + -1);
				}
				break;
			case 233:
				if (effet == 168 || effet == 101) {
					target.addBuff(128, 1, 2, true, spell, args, target, false, true);
					//Gain de PM pendant le tour de jeu
					target.setCurPm(fight, 1);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
							+ "", target.getId() + "," + 1 + "," + 2);
				}
				if (effet == 169 || effet == 127)//rall pm don pa
				{
					target.addBuff(111, 1, 2, true, spell, args, target, false, true);
					//Gain de PA pendant le tour de jeu
					target.setCurPa(fight, 1);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
							+ "", target.getId() + "," + 1 + "," + 2);
				}
				if (effet == 100 || effet == 97) {
					int healFinal = 200;
					if ((healFinal + caster.getPdv()) > caster.getPdvMax())
						healFinal = caster.getPdvMax() - caster.getPdv();
					if (healFinal < 1)
						healFinal = 0;
					caster.removePdv(caster, healFinal);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, caster.getId()
							+ "", target.getId() + "," + healFinal);

				}
				break;
			case 1045://kimbo
				if (effet == 99 || effet == 98 || effet == 94 || effet == 93) {
					target.setState(30, 1);//etat pair
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
							+ "", target.getId() + "," + 30 + ",1");
					if (target.haveState(29)) {
						target.setState(29, 0);//etat 29 impair
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 29 + ",0");
					}
				} else if (effet == 97 || effet == 96 || effet == 92 || effet == 91) {
					target.setState(29, 1);//etat etat si pair
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
							+ "", target.getId() + "," + 29 + ",1");
					if (target.haveState(30)) {
						target.setState(30, 0);//etat 29 si pair
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 30 + ",0");
					}
				}
				break;
			case 423://kralamour
				if (effet == 99 || effet == 94) {
					target.setState(37, 1);//etat tersiaire feu
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 37 + ",1");
					if (target.haveState(38))//secondaire terre
					{
						target.setState(38, 0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 38 + ",0");
					}
					if (target.haveState(36))//quanternaire eau
					{
						target.setState(36, 0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 36 + ",0");
					}
					if (target.haveState(35))//primaire air
					{
						target.setState(35, 0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 35 + ",0");
					}
				} else if (effet == 98 || effet == 93) {
					target.setState(35, 1);//etat primaire air
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 35 + ",1");
					if (target.haveState(38))//secondaire terre
					{
						target.setState(38, 0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 38 + ",0");
					}
					if (target.haveState(36))//quanternaire eau
					{
						target.setState(36, 0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 36 + ",0");
					}
					if (target.haveState(37))//tersiaire feu
					{
						target.setState(37, 0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 37 + ",0");
					}
				} else if (effet == 97 || effet == 92) {
					target.setState(38, 1);//etat secondaire terre
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 38 + ",1");
					if (target.haveState(35))//primaire air
					{
						target.setState(35, 0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 35 + ",0");
					}
					if (target.haveState(36))//quanternaire eau
					{
						target.setState(36, 0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 36 + ",0");
					}
					if (target.haveState(37))//tersiaire feu
					{
						target.setState(37, 0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 37 + ",0");
					}
				} else if (effet == 96 || effet == 91) {
					target.setState(36, 1);//etat quanternaire eau
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 36 + ",1");
					if (target.haveState(35)) {//primaire air
						target.setState(35, 0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 35 + ",0");
					}
					if (target.haveState(38)) {//secondaire terre
						target.setState(38, 0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 38 + ",0");
					}
					if (target.haveState(37)) {//tersiaire feu
						target.setState(37, 0);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + 37 + ",0");
					}
				}

				break;
			case 1071://Rasboul
				if (effet == 99 || effet == 94) {
					if (target.hasBuff(214)) {
						target.addBuff(218, 50, 4, false, 1039, "", target, false, true);// - 50 feu
						target.addBuff(210, 50, 4, false, 1039, "", target, false, true);// + 50 terre
						target.addBuff(211, 50, 4, false, 1039, "", target, false, true);// + 50 eau
						target.addBuff(212, 50, 4, false, 1039, "", target, false, true);// + 50 air
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1039, caster.getId()
								+ "", target.getId() + "," + "" + "," + 1);
					}
				} else if (effet == 98 || effet == 93) {
					if (target.hasBuff(214)) {
						target.addBuff(217, 50, 4, false, 1039, "", target, false, true);// - 50 air
						target.addBuff(210, 50, 4, false, 1039, "", target, false, true);// + 50 terre
						target.addBuff(211, 50, 4, false, 1039, "", target, false, true);// + 50 eau
						target.addBuff(213, 50, 4, false, 1039, "", target, false, true);// + 50 feu
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1039, caster.getId()
								+ "", target.getId() + "," + "" + "," + 1);
					}
				} else if (effet == 97 || effet == 92) {
					if (target.hasBuff(214)) {
						target.addBuff(215, 50, 4, false, 1039, "", target, false, true);// - 50 terre
						target.addBuff(212, 50, 4, false, 1039, "", target, false, true);// + 50 air
						target.addBuff(211, 50, 4, false, 1039, "", target, false, true);// + 50 eau
						target.addBuff(213, 50, 4, false, 1039, "", target, false, true);// + 50 feu
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1039, caster.getId()
								+ "", target.getId() + "," + "" + "," + 1);
					}
				} else if (effet == 96 || effet == 91) {
					if (target.hasBuff(214)) {
						target.addBuff(216, 50, 4, false, 1039, "", target, false, true);// - 50 eau
						target.addBuff(212, 50, 4, false, 1039, "", target, false, true);// + 50 air
						target.addBuff(210, 50, 4, false, 1039, "", target, false, true);// + 50 terre
						target.addBuff(213, 50, 4, false, 1039, "", target, false, true);// + 50 feu
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1039, caster.getId()
								+ "", target.getId() + "," + "" + "," + 1);
					}
				}
				if (effet == 101) {
					target.addBuff(111, value, 1, true, spell, args, target, false, true);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 111, target.getId()
							+ "", target.getId() + ",+" + value);
				}
				break;
		}
	}
	
	private void checkLifeTree(Fighter target, int value) {
		if (target.hasBuff(786)) {
			if ((value + caster.getPdv()) > caster.getPdvMax())
				value = caster.getPdvMax() - caster.getPdv();
			caster.removePdv(caster, -value);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(target.getFight(), 7, 100, target.getId() + "", caster.getId() + ",+" + value);
		}
	}

	@Override
	public SpellEffect clone() {
		final SpellEffect clone;
		try {
			clone = (SpellEffect) super.clone();
		}
		catch (CloneNotSupportedException ex) {
			throw new RuntimeException("superclass messed up", ex);
		}
		return clone;
	}
}