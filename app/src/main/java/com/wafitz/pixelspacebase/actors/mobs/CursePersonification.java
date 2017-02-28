/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.wafitz.pixelspacebase.actors.mobs;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Paralysis;
import com.wafitz.pixelspacebase.actors.buffs.Roots;
import com.wafitz.pixelspacebase.actors.buffs.Terror;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Ghost;
import com.wafitz.pixelspacebase.effects.Pushing;
import com.wafitz.pixelspacebase.items.weapon.enchantments.Death;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.sprites.CursePersonificationSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.HashSet;

public class CursePersonification extends Mob {

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();

	static {
		IMMUNITIES.add(Death.class);
		IMMUNITIES.add(Terror.class);
		IMMUNITIES.add(Paralysis.class);
		IMMUNITIES.add(Roots.class);
	}
	
	{
		name = "curse personification";
		spriteClass = CursePersonificationSprite.class;

		HP = HT = 10 + Dungeon.depth * 3;
		defenseSkill = 10 + Dungeon.depth;

		EXP = 3;
		maxLvl = 5;

		state = HUNTING;
		baseSpeed = 0.5f;
		flying = true;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 3, 5 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10 + Dungeon.depth;
	}
	
	@Override
	public int dr() {
		return 1;
	}
	
	@Override
	public int attackProc(Char enemy, int damage) {

		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			int ofs = PathFinder.NEIGHBOURS8[i];
			if (enemy.pos - pos == ofs) {
				int newPos = enemy.pos + ofs;
				if ((Level.passable[newPos] || Level.avoid[newPos]) && Actor.findChar( newPos ) == null) {

					Actor.addDelayed( new Pushing( enemy, enemy.pos, newPos ), -1 );

					enemy.pos = newPos;
					// FIXME
					if (enemy instanceof Mob) {
						Dungeon.level.mobPress( (Mob)enemy );
					} else {
						Dungeon.level.press( newPos, enemy );
					}

				}
				break;
			}
		}

		return super.attackProc( enemy, damage );
	}
	
	@Override
	protected boolean act() {
		if (HP > 0 && HP < HT) {
			HP++;
		}
		return super.act();
	}
	
	@Override
	public void die( Object cause ) {
		Ghost ghost = new Ghost();
		ghost.state = ghost.PASSIVE;
		Ghost.replace( this, ghost );
	}

	@Override
	public String description() {
		return
			"This creature resembles the sad ghost, but it swirls with darkness. " +
			"Its face bears an expression of despair.";
	}
	
	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
