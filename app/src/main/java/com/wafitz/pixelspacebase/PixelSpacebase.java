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
package com.wafitz.pixelspacebase;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.wafitz.pixelspacebase.actors.blobs.Foliage;
import com.wafitz.pixelspacebase.actors.blobs.WaterOfHealth;
import com.wafitz.pixelspacebase.actors.buffs.Shadows;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.FetidRat;
import com.wafitz.pixelspacebase.actors.mobs.npcs.MirrorImage;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Shopkeeper;
import com.wafitz.pixelspacebase.items.quest.DriedRose;
import com.wafitz.pixelspacebase.items.rings.RingOfElements;
import com.wafitz.pixelspacebase.items.rings.RingOfMending;
import com.wafitz.pixelspacebase.items.rings.RingOfPower;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfEnchantment;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfPsionicBlast;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfUpgrade;
import com.wafitz.pixelspacebase.items.wands.WandOfReach;
import com.wafitz.pixelspacebase.items.weapon.enchantments.Shock;
import com.wafitz.pixelspacebase.items.weapon.missiles.Boomerang;
import com.wafitz.pixelspacebase.plants.Dreamweed;
import com.wafitz.pixelspacebase.plants.Rotberry;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.scenes.TitleScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;

import javax.microedition.khronos.opengles.GL10;

public class PixelSpacebase extends Game {
	
	public PixelSpacebase() {
		super( TitleScene.class );
		
		com.watabou.utils.Bundle.addAlias( 
			ScrollOfUpgrade.class,
			"com.wafitz.pixelspacebase.items.scrolls.ScrollOfEnhancement" );
		com.watabou.utils.Bundle.addAlias( 
			WaterOfHealth.class,
			"com.wafitz.pixelspacebase.actors.blobs.Light" );
		com.watabou.utils.Bundle.addAlias( 
			RingOfMending.class,
			"com.wafitz.pixelspacebase.items.rings.RingOfRejuvenation" );
		com.watabou.utils.Bundle.addAlias( 
			WandOfReach.class,
			"com.wafitz.pixelspacebase.items.wands.WandOfTelekenesis" );
		com.watabou.utils.Bundle.addAlias( 
			Foliage.class,
			"com.wafitz.pixelspacebase.actors.blobs.Blooming" );
		com.watabou.utils.Bundle.addAlias( 
			Shadows.class,
			"com.wafitz.pixelspacebase.actors.buffs.Rejuvenation" );
		com.watabou.utils.Bundle.addAlias( 
			ScrollOfPsionicBlast.class,
			"com.wafitz.pixelspacebase.items.scrolls.ScrollOfNuclearBlast" );
		com.watabou.utils.Bundle.addAlias( 
			Hero.class,
			"com.wafitz.pixelspacebase.actors.Hero" );
		com.watabou.utils.Bundle.addAlias( 
			Shopkeeper.class,
			"com.wafitz.pixelspacebase.actors.mobs.Shopkeeper" );
		// 1.6.1
		com.watabou.utils.Bundle.addAlias( 
			DriedRose.class,
			"com.wafitz.pixelspacebase.items.DriedRose" );
		com.watabou.utils.Bundle.addAlias( 
			MirrorImage.class,
			"com.wafitz.pixelspacebase.items.scrolls.ScrollOfMirrorImage$MirrorImage" );
		// 1.6.4
		com.watabou.utils.Bundle.addAlias( 
			RingOfElements.class,
			"com.wafitz.pixelspacebase.items.rings.RingOfCleansing" );
		com.watabou.utils.Bundle.addAlias( 
			RingOfElements.class,
			"com.wafitz.pixelspacebase.items.rings.RingOfResistance" );
		com.watabou.utils.Bundle.addAlias( 
			Boomerang.class,
			"com.wafitz.pixelspacebase.items.weapon.missiles.RangersBoomerang" );
		com.watabou.utils.Bundle.addAlias( 
			RingOfPower.class,
			"com.wafitz.pixelspacebase.items.rings.RingOfEnergy" );
		// 1.7.2
		com.watabou.utils.Bundle.addAlias( 
			Dreamweed.class,
			"com.wafitz.pixelspacebase.plants.Blindweed" );
		com.watabou.utils.Bundle.addAlias( 
			Dreamweed.Seed.class,
			"com.wafitz.pixelspacebase.plants.Blindweed$Seed" );
		// 1.7.4
		com.watabou.utils.Bundle.addAlias( 
			Shock.class,
			"com.wafitz.pixelspacebase.items.weapon.enchantments.Piercing" );
		com.watabou.utils.Bundle.addAlias( 
			Shock.class,
			"com.wafitz.pixelspacebase.items.weapon.enchantments.Swing" );
		com.watabou.utils.Bundle.addAlias( 
			ScrollOfEnchantment.class,
			"com.wafitz.pixelspacebase.items.scrolls.ScrollOfWeaponUpgrade" );
		// 1.7.5
		com.watabou.utils.Bundle.addAlias( 
			ScrollOfEnchantment.class,
			"com.wafitz.pixelspacebase.items.Stylus" );
		// 1.8.0
		com.watabou.utils.Bundle.addAlias( 
			FetidRat.class,
			"com.wafitz.pixelspacebase.actors.mobs.npcs.Ghost$FetidRat" );
		com.watabou.utils.Bundle.addAlias( 
			Rotberry.class,
			"com.wafitz.pixelspacebase.actors.mobs.npcs.Wandmaker$Rotberry" );
		com.watabou.utils.Bundle.addAlias( 
			Rotberry.Seed.class,
			"com.wafitz.pixelspacebase.actors.mobs.npcs.Wandmaker$Rotberry$Seed" );
		// 1.9.0
		com.watabou.utils.Bundle.addAlias( 
			WandOfReach.class,
			"com.wafitz.pixelspacebase.items.wands.WandOfTelekinesis" );
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		updateImmersiveMode();

		DisplayMetrics metrics = new DisplayMetrics();
		instance.getWindowManager().getDefaultDisplay().getMetrics( metrics );
		boolean landscape = metrics.widthPixels > metrics.heightPixels;
		
		if (Preferences.INSTANCE.getBoolean( Preferences.KEY_LANDSCAPE, false ) != landscape) {
			landscape( !landscape );
		}
		
		Music.INSTANCE.enable( music() );
		Sample.INSTANCE.enable( soundFx() );
		
		Sample.INSTANCE.load( 
			Assets.SND_CLICK, 
			Assets.SND_BADGE, 
			Assets.SND_GOLD,
			
			Assets.SND_DESCEND,
			Assets.SND_STEP,
			Assets.SND_WATER,
			Assets.SND_OPEN,
			Assets.SND_UNLOCK,
			Assets.SND_ITEM,
			Assets.SND_DEWDROP, 
			Assets.SND_HIT, 
			Assets.SND_MISS,
			Assets.SND_EAT,
			Assets.SND_READ,
			Assets.SND_LULLABY,
			Assets.SND_DRINK,
			Assets.SND_SHATTER,
			Assets.SND_ZAP,
			Assets.SND_LIGHTNING,
			Assets.SND_LEVELUP,
			Assets.SND_DEATH,
			Assets.SND_CHALLENGE,
			Assets.SND_CURSED,
			Assets.SND_EVOKE,
			Assets.SND_TRAP,
			Assets.SND_TOMB,
			Assets.SND_ALERT,
			Assets.SND_MELD,
			Assets.SND_BOSS,
			Assets.SND_BLAST,
			Assets.SND_PLANT,
			Assets.SND_RAY,
			Assets.SND_BEACON,
			Assets.SND_TELEPORT,
			Assets.SND_CHARMS,
			Assets.SND_MASTERY,
			Assets.SND_PUFF,
			Assets.SND_ROCKS,
			Assets.SND_BURNING,
			Assets.SND_FALLING,
			Assets.SND_GHOST,
			Assets.SND_SECRET,
			Assets.SND_BONES,
			Assets.SND_BEE,
			Assets.SND_DEGRADE,
			Assets.SND_MIMIC );
	}
	
	@Override
	public void onWindowFocusChanged( boolean hasFocus ) {
		
		super.onWindowFocusChanged( hasFocus );
		
		if (hasFocus) {
			updateImmersiveMode();
		}
	}
	
	public static void switchNoFade( Class<? extends PixelScene> c ) {
		PixelScene.noFade = true;
		switchScene( c );
	}
	
	/*
	 * ---> Preferences
	 */
	
	public static void landscape( boolean value ) {
		Game.instance.setRequestedOrientation( value ?
			ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
			ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		Preferences.INSTANCE.put( Preferences.KEY_LANDSCAPE, value );
	}
	
	public static boolean landscape() {
		return width > height;
	}

	// *** Set Dynamic Width and Height ***

	public static void level_width( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_WIDTH, value );
	}

	public static int lvl_width() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_WIDTH, 0 );
	}

	public static int level_height(int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_HEIGHT, value );
        return value;
    }

	public static int lvl_height() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_HEIGHT, 0 );
	}
	
	// *** IMMERSIVE MODE ****
	
	private static boolean immersiveModeChanged = false;
	
	@SuppressLint("NewApi")
	public static void immerse( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_IMMERSIVE, value );
		
		instance.runOnUiThread( new Runnable() {
			@Override
			public void run() {
				updateImmersiveMode();
				immersiveModeChanged = true;
			}
		} );
	}
	
	@Override
	public void onSurfaceChanged( GL10 gl, int width, int height ) {
		super.onSurfaceChanged( gl, width, height );
		
		if (immersiveModeChanged) {
			requestedReset = true;
			immersiveModeChanged = false;
		}
	}
	
	@SuppressLint("NewApi")
	public static void updateImmersiveMode() {
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			try {
				// Sometime NullPointerException happens here
				instance.getWindow().getDecorView().setSystemUiVisibility( 
					immersed() ?
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE | 
					View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | 
					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | 
					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | 
					View.SYSTEM_UI_FLAG_FULLSCREEN | 
					View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY 
					:
					0 );
			} catch (Exception e) {
				reportException( e );
			}
		}
	}
	
	public static boolean immersed() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_IMMERSIVE, false );
	}
	
	// *****************************
	
	public static void scaleUp( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_SCALE_UP, value );
		switchScene( TitleScene.class );
	}
	
	public static boolean scaleUp() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_SCALE_UP, true );
	}

	public static void zoom( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_ZOOM, value );
	}
	
	public static int zoom() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_ZOOM, 0 );
	}
	
	public static void music( boolean value ) {
		Music.INSTANCE.enable( value );
		Preferences.INSTANCE.put( Preferences.KEY_MUSIC, value );
	}
	
	public static boolean music() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_MUSIC, true );
	}
	
	public static void soundFx( boolean value ) {
		Sample.INSTANCE.enable( value );
		Preferences.INSTANCE.put( Preferences.KEY_SOUND_FX, value );
	}
	
	public static boolean soundFx() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_SOUND_FX, true );
	}
	
	public static void brightness( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_BRIGHTNESS, value );
		if (scene() instanceof GameScene) {
			((GameScene)scene()).brightness( value );
		}
	}
	
	public static boolean brightness() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_BRIGHTNESS, false );
	}
	
	public static void donated( String value ) {
		Preferences.INSTANCE.put( Preferences.KEY_DONATED, value );
	}
	
	public static String donated() {
		return Preferences.INSTANCE.getString( Preferences.KEY_DONATED, "" );
	}
	
	public static void lastClass( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_LAST_CLASS, value );
	}
	
	public static int lastClass() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_LAST_CLASS, 0 );
	}
	
	public static void challenges( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_CHALLENGES, value );
	}
	
	public static int challenges() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_CHALLENGES, 0 );
	}
	
	public static void intro( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_INTRO, value );
	}
	
	public static boolean intro() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_INTRO, true );
	}
	
	/*
	 * <--- Preferences
	 */
	
	public static void reportException( Throwable tr ) {
		Log.e( "PD", Log.getStackTraceString( tr ) ); 
	}
}