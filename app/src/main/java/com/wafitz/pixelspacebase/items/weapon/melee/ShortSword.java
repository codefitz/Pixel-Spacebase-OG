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
package com.wafitz.pixelspacebase.items.weapon.melee;

import java.util.ArrayList;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfUpgrade;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.items.weapon.missiles.Boomerang;

public class ShortSword extends MeleeWeapon {
	
	public static final String AC_REFORGE	= "REFORGE";
	
	private static final String TXT_SELECT_WEAPON	= "Select a weapon to upgrade";
	
	private static final String TXT_REFORGED = 
		"you reforged the short sword to upgrade your %s";
	private static final String TXT_NOT_BOOMERANG = 
		"you can't upgrade a boomerang this way";
	
	private static final float TIME_TO_REFORGE	= 2f;
	
	private boolean  equipped;
	
	{
		name = "short sword";
		image = ItemSpriteSheet.SHORT_SWORD;
	}
	
	public ShortSword() {
		super( 1, 1f, 1f );
		
		STR = 11;
	}
	
	@Override
	protected int max0() {
		return 12;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (level() > 0) {
			actions.add( AC_REFORGE );
		}
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		if (action == AC_REFORGE) {
			
			if (hero.belongings.weapon == this) {
				equipped = true;
				hero.belongings.weapon = null;
			} else {
				equipped = false;
				detach( hero.belongings.backpack );
			}
			
			Item.curUser = hero;
			
			GameScene.selectItem( itemSelector, WndBag.Mode.WEAPON, TXT_SELECT_WEAPON );
			
		} else {
			
			super.execute( hero, action );
			
		}
	}
	
	@Override
	public String desc() {
		return 
			"It is indeed quite short, just a few inches longer, than a dagger.";
	}
	
	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null && !(item instanceof Boomerang)) {
				
				Sample.INSTANCE.play( Assets.SND_EVOKE );
				ScrollOfUpgrade.upgrade( Item.curUser );
				Item.evoke( Item.curUser );
				
				GLog.w( TXT_REFORGED, item.name() );
				
				((MeleeWeapon)item).safeUpgrade();
				Item.curUser.spendAndNext( TIME_TO_REFORGE );
				
				Badges.validateItemLevelAquired( item );
				
			} else {
				
				if (item instanceof Boomerang) {
					GLog.w( TXT_NOT_BOOMERANG );
				}
				
				if (equipped) {
					Item.curUser.belongings.weapon = ShortSword.this;
				} else {
					collect( Item.curUser.belongings.backpack );
				}
			}
		}
	};
}
