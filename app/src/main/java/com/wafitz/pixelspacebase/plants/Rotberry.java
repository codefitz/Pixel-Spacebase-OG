package com.wafitz.pixelspacebase.plants;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.actors.buffs.Roots;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.ToxicGas;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.bags.Bag;
import com.wafitz.pixelspacebase.items.potions.PotionOfStrength;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

public class Rotberry extends Plant {
	
	private static final String TXT_DESC = 
		"Berries of this shrub taste like sweet, sweet death.";
	
	{
		image = 7;
		plantName = "Rotberry";
	}
	
	@Override
	public void activate( Char ch ) {
		super.activate( ch );
		
		GameScene.add( Blob.seed( pos, 100, ToxicGas.class ) );
		
		Dungeon.level.drop( new Seed(), pos ).sprite.drop();
		
		if (ch != null) {
			Buff.prolong( ch, Roots.class, Roots.TICK * 3 );
		}
	}
	
	@Override
	public String desc() {
		return TXT_DESC;
	}
	
	public static class Seed extends Plant.Seed {
		{
			plantName = "Rotberry";
			
			name = "seed of " + plantName;
			image = ItemSpriteSheet.SEED_ROTBERRY;
			
			plantClass = Rotberry.class;
			alchemyClass = PotionOfStrength.class;
		}
		
		@Override
		public boolean collect( Bag container ) {
			if (super.collect( container )) {
				
				if (Dungeon.level != null) {
					for (Mob mob : Dungeon.level.mobs) {
						mob.beckon( Dungeon.hero.pos );
					}
					
					GLog.w( "The seed emits a roar that echoes throughout the dungeon!" );
					CellEmitter.center( Dungeon.hero.pos ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
					Sample.INSTANCE.play( Assets.SND_CHALLENGE );
				}
				
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public String desc() {
			return TXT_DESC;
		}
	}
}