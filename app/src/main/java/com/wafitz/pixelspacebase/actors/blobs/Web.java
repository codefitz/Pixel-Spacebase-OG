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
package com.wafitz.pixelspacebase.actors.blobs;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Roots;
import com.wafitz.pixelspacebase.effects.BlobEmitter;
import com.wafitz.pixelspacebase.effects.particles.WebParticle;
import com.wafitz.pixelspacebase.levels.Level;

public class Web extends Blob {
	
	@Override
	protected void evolve() {

		for (int i = 0; i < Dungeon.level.length(); i++) {
			
			int offv = cur[i] > 0 ? cur[i] - 1 : 0;
			off[i] = offv;
			
			if (offv > 0) {
				
				volume += offv;
				
				Char ch = Actor.findChar( i );
				if (ch != null) {
					Buff.prolong( ch, Roots.class, Actor.TICK );
				}
			}
		}
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		
		emitter.pour( WebParticle.FACTORY, 0.4f );
	}

	public void seed(Level level, int cell, int amount) {
		int diff = amount - cur[cell];
		if (diff > 0) {
			cur[cell] = amount;
			volume += diff;
		}
	}
	
	@Override
	public String tileDesc() {
		return "Everything is covered with a thick web here.";
	}
}
