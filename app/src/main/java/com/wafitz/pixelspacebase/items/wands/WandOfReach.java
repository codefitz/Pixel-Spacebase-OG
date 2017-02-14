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
package com.wafitz.pixelspacebase.items.wands;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.effects.MagicMissile;
import com.wafitz.pixelspacebase.effects.Swap;
import com.wafitz.pixelspacebase.items.Dewdrop;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.potions.Potion;
import com.wafitz.pixelspacebase.items.scrolls.Scroll;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfEnchantment;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfUpgrade;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.items.potions.PotionOfMight;
import com.wafitz.pixelspacebase.items.potions.PotionOfStrength;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.watabou.utils.Callback;

public class WandOfReach extends Wand {

	private static final String TXT_YOU_NOW_HAVE	= "You have magically transported %s into your backpack"; 
	
	{
		name = "Wand of Reach";
		hitChars = false;
	}
	
	@Override
	protected void onZap( int cell ) {
		
		int reach = Math.min( Ballistica.distance, power() + 4 );
		
		boolean mapUpdated = false;
		for (int i=1; i < reach; i++) {
			
			int c = Ballistica.trace[i];
			
			int before = Dungeon.level.map[c];
			
			Char ch = Actor.findChar( c );
			if (ch != null) {
				Actor.addDelayed( new Swap( Item.curUser, ch ), -1 );
				break;
			}
			
			Heap heap = Dungeon.level.heaps.get( c );
			if (heap != null) {
				switch (heap.type) {
				case HEAP:
					transport( heap );
					break;
				case CHEST:
				case MIMIC:
				case TOMB:
				case SKELETON:
					heap.open( Item.curUser );
					break;
				default:
				}
				
				break;
			}
			
			Dungeon.level.press( c, null );
			if (before == Terrain.OPEN_DOOR) {	
				Level.set( c, Terrain.DOOR );
				GameScene.updateMap( c );
			} else if (Level.water[c]) {
				GameScene.ripple( c );
			}
			
			mapUpdated = mapUpdated || Dungeon.level.map[c] != before;
		}
		
		if (mapUpdated) {
			Dungeon.observe();
		}
	}
	
	private void transport( Heap heap ) {
		Item item = heap.pickUp();
		if (item.doPickUp( Item.curUser )) {
			
			if (item instanceof Dewdrop) {
				// Do nothing
			} else {
				if (((item instanceof ScrollOfUpgrade || item instanceof ScrollOfEnchantment) && ((Scroll)item).isKnown()) ||
					((item instanceof PotionOfStrength || item instanceof PotionOfMight) && ((Potion)item).isKnown())) {
					GLog.p( TXT_YOU_NOW_HAVE, item.name() );
				} else {
					GLog.i( TXT_YOU_NOW_HAVE, item.name() );
				}
			}

		} else {
			Dungeon.level.drop( item, Item.curUser.pos ).sprite.drop();
		}
	}
	
	protected void fx( int cell, Callback callback ) {
		MagicMissile.force( Item.curUser.sprite.parent, Item.curUser.pos, cell, callback );
		Sample.INSTANCE.play( Assets.SND_ZAP );
	}
	
	@Override
	public String desc() {
		return
			"This utility wand can be used to grab objects from a distance and to switch places with enemies. " +
			"Waves of magic force radiated from it will affect all cells on their way triggering traps, " +
			"trampling high vegetation, opening closed doors and closing open ones.";
	}
}
