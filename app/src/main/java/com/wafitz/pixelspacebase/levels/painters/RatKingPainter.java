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
package com.wafitz.pixelspacebase.levels.painters;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.mobs.npcs.RatKing;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Gold;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.weapon.missiles.MissileWeapon;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.watabou.utils.Random;

public class RatKingPainter extends Painter {

	public static void paint( Level level, Room room ) {

		fill( level, room, Terrain.WALL );
		fill( level, room, 1, Terrain.EMPTY_SP );
		
		Room.Door entrance = room.entrance();
		entrance.set( Room.Door.Type.HIDDEN );
		int door = entrance.x + entrance.y * Dungeon.level.width();
		
		for (int i=room.left + 1; i < room.right; i++) {
			addChest(level, (room.top + 1) * Dungeon.level.width() + i, door);
			addChest(level, (room.bottom - 1) * Dungeon.level.width() + i, door);
		}
		
		for (int i=room.top + 2; i < room.bottom - 1; i++) {
			addChest(level, i * Dungeon.level.width() + room.left + 1, door);
			addChest(level, i * Dungeon.level.width() + room.right - 1, door);
		}
		
		while (true) {
			Heap chest = level.heaps.get(level.pointToCell(room.random()));
			if (chest != null) {
				chest.type = Heap.Type.MIMIC;
				break;
			}
		}
		
		RatKing king = new RatKing();
		king.pos = level.pointToCell(room.random(1));
		level.mobs.add( king );
	}
	
	private static void addChest( Level level, int pos, int door ) {

		if (pos == door - 1 ||
				pos == door + 1 ||
				pos == door - Dungeon.level.width() ||
				pos == door + Dungeon.level.width()) {
			return;
		}
		
		Item prize;
		switch (Random.Int( 10 )) {
		case 0:
			prize = Generator.random( Generator.Category.WEAPON );
			if (prize instanceof MissileWeapon) {
				prize.quantity( 1 );
			} else {
				prize.degrade( Random.Int( 3 ) );
			}
			break;
		case 1:
			prize = Generator.random( Generator.Category.ARMOR ).degrade( Random.Int( 3 ) );
			break;
		default:
			prize = new Gold( Random.IntRange( 1, 5 ) );
			break;
		}
		
		level.drop( prize, pos ).type = Heap.Type.CHEST;
	}
}
