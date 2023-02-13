package org.starloco.locos.fight.ia.type;

import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Locos on 18/09/2015.
 */
public class IAPerco extends AbstractIA {

	private byte flag = 0;
	private Collection<Spell.SortStats> spells;

    public IAPerco(Fight fight, Fighter fighter, byte b) {
        super(fight, fighter, b);
		this.spells = World.world.getGuild(this.fighter.getCollector().getGuildId()).getSpells().values();
	}

    @Override
    public void apply() {
		if (!this.stop && this.fighter.canPlay() && count > 0) {
			int time = 0;
			Fighter enemy = Function.getInstance().getNearestEnnemy(this.fight, this.fighter, true);

			if(enemy == null) {
				time = Function.getInstance().moveFarIfPossible(this.fight, this.fighter);
			} else {
				switch (this.flag) {
					case 0:
						int[] buffs = {461, 451, 452, 453, 454};
						Spell.SortStats spell = getBestSpell(buffs, fighter);
						if (spell != null && fight.tryCastSpell(fighter, spell, fighter.getCell().getId()) == 0) {
							time = 1500;
							if (getBestSpell(buffs, fighter) != null) {
								this.flag = -1;
								this.count = 6;
							}
						}
						break;
					case 1:
						Fighter target = this.getFightersForDebuffing();
						if (target != null) {
							spell = getBestSpell(new int[]{460}, fighter);
							if(spell != null) {
								if (Function.getInstance().moveToAttack(this.fight, this.fighter, target, spell)) {
									time = 2000;
									this.flag = 0;
									this.count = 5;
								} else {
									if (fight.tryCastSpell(fighter, spell, target.getCell().getId()) == 0)
										time = 1500;
								}
							}
						}
						break;
					case 2:
						int[] attacks = {458, 456, 457, 458, 462};
						target = Function.getInstance().getNearestEnnemy(fight, fighter, true);
						spell = getBestSpell(attacks, target);

						if(spell != null) {
							if(Function.getInstance().moveToAttack(fight, fighter, target, spell)) {
								time = 2000;
								this.flag = 1;
								this.count = 4;
								break;
							}
							if(spell.getSpell().getId() == 458 || spell.isLineLaunch()) { // Rocher
								int cell = Function.getInstance().getBestTargetZone(fight, fighter, spell, enemy.getCell().getId(), spell.isLineLaunch());
								int nbTarget = cell / 1000;
								cell = cell - nbTarget * 1000;
								if(nbTarget > 1) {
									if (this.fight.tryCastSpell(this.fighter, spell, cell) == 0) {
										time = 3000;
									}
								}
							} else {
								if (this.fight.tryCastSpell(this.fighter, spell, target.getCell().getId()) == 0) {
									time = 3000;
								}
							}
						}
						break;
					case 3:
						spell = getBestSpell(new int[]{459}, fighter);
						if(spell != null && fight.tryCastSpell(fighter, spell, fighter.getCell().getId()) == 0)
							time = 1500;
						break;
					case 4:
						if (Function.getInstance().moveFarIfPossible(fight, fighter) > 0) {
							time = 2500;
						}
						break;
				}
				this.flag++;
			}

			addNext(this::decrementCount, time);
		} else {
			this.stop = true;
		}
	}

	private Spell.SortStats getBestSpell(int[] wantedSpells, Fighter target) {
    	if(target != null) {
			for (Spell.SortStats spell : spells) {
				for (int wanted : wantedSpells) {
					if (spell != null && wanted == spell.getSpell().getId() &&
							fight.canLaunchSpell(fighter, spell, target.getCell()))
						return spell;
				}
			}
		}
		return null;
	}

	private Fighter getFightersForDebuffing() {
		List<Fighter> fightersForDebuffing = new ArrayList<>();
		for(Fighter temp : this.getFight().getFighters(7))
			if(!temp.isDead() && (temp.getFightBuff() != null && temp.getFightBuff().size() > 1) && temp.getTeam() != this.fighter.getTeam())
				if(PathFinding.getDistanceBetween(fight.getMap(), temp.getCell().getId(), fighter.getCell().getId()) <= 12)
					fightersForDebuffing.add(temp);
		if(fightersForDebuffing.isEmpty())
			return null;
		return fightersForDebuffing.get(Formulas.random.nextInt(fightersForDebuffing.size()));
	}
}
