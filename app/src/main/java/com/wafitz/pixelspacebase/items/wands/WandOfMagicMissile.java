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

import java.util.ArrayList;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.ResultDescriptions;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfUpgrade;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.utils.Utils;
import com.watabou.utils.Random;

public class WandOfMagicMissile extends Wand {

	public static final String AC_DISENCHANT	= "DISENCHANT";
	
	private static final String TXT_SELECT_WAND	= "Select a wand to upgrade";
	
	private static final String TXT_DISENCHANTED = 
		"you disenchanted the Wand of Magic Missile and used its essence to upgrade your %s";
	
	private static final float TIME_TO_DISENCHANT	= 2f;
	
	private boolean disenchantEquipped;
	
	{
		name = "Wand of Magic Missile";
		image = ItemSpriteSheet.WAND_MAGIC_MISSILE;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (level() > 0) {
			actions.add( AC_DISENCHANT );
		}
		return actions;
	}
	
	@Override
	protected void onZap( int cell ) {
				
		Char ch = Actor.findChar( cell );
		if (ch != null) {	
			
			int level = power();
			
			ch.damage( Random.Int( 1, 6 + level * 2 ), this );
			ch.sprite.burst( 0xFF99CCFF, level / 2 + 2 );
			
			if (ch == Item.curUser && !ch.isAlive()) {
				Dungeon.fail( Utils.format( ResultDescriptions.WAND, name, Dungeon.depth ) );
				GLog.n( "You killed yourself with your own Wand of Magic Missile..." );
			}
		}
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		if (action.equals( AC_DISENCHANT )) {
			
			if (hero.belongings.weapon == this) {
				disenchantEquipped = true;
				hero.belongings.weapon = null;
				updateQuickslot();
			} else {
				disenchantEquipped = false;
				detach( hero.belongings.backpack );
			}
			
			Item.curUser = hero;
			GameScene.selectItem( itemSelector, WndBag.Mode.WAND, TXT_SELECT_WAND );
			
		} else {
		
			super.execute( hero, action );
			
		}
	}
	
	@Override
	protected boolean isKnown() {
		return true;
	}
	
	@Override
	public void setKnown() {
	}
	
	protected int initialCharges() {
		return 3;
	}
	
	@Override
	public String desc() {
		return
			"This wand launches missiles of pure magical energy, dealing moderate damage to a target creature.";
	}
	
	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null) {
				
				Sample.INSTANCE.play( Assets.SND_EVOKE );
				ScrollOfUpgrade.upgrade( Item.curUser );
				Item.evoke( Item.curUser );
				
				GLog.w( TXT_DISENCHANTED, item.name() );
				
				item.upgrade();
				Item.curUser.spendAndNext( TIME_TO_DISENCHANT );
				
				Badges.validateItemLevelAquired( item );
				
			} else {
				if (disenchantEquipped) {
					Item.curUser.belongings.weapon = WandOfMagicMissile.this;
					WandOfMagicMissile.this.updateQuickslot();
				} else {
					collect( Item.curUser.belongings.backpack );
				}
			}
		}
	};
}
