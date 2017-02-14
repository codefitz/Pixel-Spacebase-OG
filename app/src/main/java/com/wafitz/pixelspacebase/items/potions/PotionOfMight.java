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
package com.wafitz.pixelspacebase.items.potions;

import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.Badges;

public class PotionOfMight extends PotionOfStrength {

	{
		name = "Potion of Might";
	}
	
	@Override
	protected void apply( Hero hero ) {
		setKnown();
		
		hero.STR++;
		hero.HT += 10;
		hero.HP += 10;
		hero.sprite.showStatus( CharSprite.POSITIVE, "+2 str, +10 ht" );
		GLog.p( "Newfound strength surges through your body." );
		
		Badges.validateStrengthAttained();
	}
	
	@Override
	public String desc() {
		return
			"This powerful liquid will course through your muscles, permanently " +
			"increasing your strength by 2 points and health by 10 points.";
	}
	
	@Override
	public int price() {
		return isKnown() ? 200 * quantity : super.price();
	}
}
