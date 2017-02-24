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
package com.wafitz.pixelspacebase.scenes;

import com.wafitz.pixelspacebase.windows.WndStory;
import com.watabou.noosa.Game;

public class IntroScene extends PixelScene {

	private static final String TEXT = 	
		"Welcome to Pixel Spacebase!\n\n" +
		"" +
		"The spacebase is a large self-sustaining structure with medical facilities and replicators, " +
		"staffed by a humans, robots and alien crew.\n\n" +
		"" +
		"Of course being this far out in deep space, there is always the possibility of unfriendly " +
		"aliens and strange anomolies...!";
	
	@Override
	public void create() {
		super.create();
		
		add( new WndStory( TEXT ) {
			@Override
			public void hide() {
				super.hide();
				Game.switchScene( InterlevelScene.class );
			}
		} );
		
		fadeIn();
	}
}
