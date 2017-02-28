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
package com.wafitz.pixelspacebase.levels.traps;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.ResultDescriptions;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Lightning;
import com.wafitz.pixelspacebase.effects.particles.SparkParticle;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.utils.Utils;
import com.watabou.noosa.Camera;
import com.watabou.utils.Random;

public class LightningTrap {

	public static final Electricity LIGHTNING = new Electricity();
	
	// 00x66CCEE
	private static final String name = "lightning trap";
	
	public static void trigger( int pos, Char ch ) {

		if (ch != null) {
			ch.damage( Math.max( 1, Random.Int( ch.HP / 3, 2 * ch.HP / 3 ) ), LIGHTNING );
			if (ch == Dungeon.hero) {

				Camera.main.shake( 2, 0.3f );

				if (!ch.isAlive()) {
					Dungeon.fail( Utils.format( ResultDescriptions.TRAP, name, Dungeon.depth ) );
					GLog.n( "You were killed by a discharge of a lightning trap..." );
				} else {
					((Hero)ch).belongings.charge( false );
				}
			}

			int[] points = new int[2];

			points[0] = pos - Dungeon.level.width();
			points[1] = pos + Dungeon.level.width();
			ch.sprite.parent.add( new Lightning( points, 2, null ) );

			points[0] = pos - 1;
			points[1] = pos + 1;
			ch.sprite.parent.add( new Lightning( points, 2, null ) );
		}

		CellEmitter.center( pos ).burst( SparkParticle.FACTORY, Random.IntRange( 3, 4 ) );

	}

	public static class Electricity {
	}
}
