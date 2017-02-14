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
package com.wafitz.pixelspacebase.items;

import java.util.HashMap;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.armor.Armor;
import com.wafitz.pixelspacebase.items.armor.ClothArmor;
import com.wafitz.pixelspacebase.items.armor.LeatherArmor;
import com.wafitz.pixelspacebase.items.armor.MailArmor;
import com.wafitz.pixelspacebase.items.armor.PlateArmor;
import com.wafitz.pixelspacebase.items.armor.ScaleArmor;
import com.wafitz.pixelspacebase.items.bags.Bag;
import com.wafitz.pixelspacebase.items.food.Food;
import com.wafitz.pixelspacebase.items.food.MysteryMeat;
import com.wafitz.pixelspacebase.items.food.Pasty;
import com.wafitz.pixelspacebase.items.potions.Potion;
import com.wafitz.pixelspacebase.items.potions.PotionOfExperience;
import com.wafitz.pixelspacebase.items.potions.PotionOfFrost;
import com.wafitz.pixelspacebase.items.potions.PotionOfHealing;
import com.wafitz.pixelspacebase.items.potions.PotionOfInvisibility;
import com.wafitz.pixelspacebase.items.potions.PotionOfLevitation;
import com.wafitz.pixelspacebase.items.potions.PotionOfLiquidFlame;
import com.wafitz.pixelspacebase.items.potions.PotionOfMight;
import com.wafitz.pixelspacebase.items.potions.PotionOfMindVision;
import com.wafitz.pixelspacebase.items.potions.PotionOfParalyticGas;
import com.wafitz.pixelspacebase.items.potions.PotionOfPurity;
import com.wafitz.pixelspacebase.items.potions.PotionOfStrength;
import com.wafitz.pixelspacebase.items.potions.PotionOfToxicGas;
import com.wafitz.pixelspacebase.items.rings.Ring;
import com.wafitz.pixelspacebase.items.rings.RingOfAccuracy;
import com.wafitz.pixelspacebase.items.rings.RingOfDetection;
import com.wafitz.pixelspacebase.items.rings.RingOfElements;
import com.wafitz.pixelspacebase.items.rings.RingOfEvasion;
import com.wafitz.pixelspacebase.items.rings.RingOfHaggler;
import com.wafitz.pixelspacebase.items.rings.RingOfHaste;
import com.wafitz.pixelspacebase.items.rings.RingOfHerbalism;
import com.wafitz.pixelspacebase.items.rings.RingOfMending;
import com.wafitz.pixelspacebase.items.rings.RingOfPower;
import com.wafitz.pixelspacebase.items.rings.RingOfSatiety;
import com.wafitz.pixelspacebase.items.rings.RingOfShadows;
import com.wafitz.pixelspacebase.items.rings.RingOfThorns;
import com.wafitz.pixelspacebase.items.scrolls.Scroll;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfChallenge;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfEnchantment;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfIdentify;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfLullaby;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfMagicMapping;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfMirrorImage;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfPsionicBlast;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfRecharging;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfRemoveCurse;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfTeleportation;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfTerror;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfUpgrade;
import com.wafitz.pixelspacebase.items.wands.Wand;
import com.wafitz.pixelspacebase.items.wands.WandOfAmok;
import com.wafitz.pixelspacebase.items.wands.WandOfAvalanche;
import com.wafitz.pixelspacebase.items.wands.WandOfBlink;
import com.wafitz.pixelspacebase.items.wands.WandOfDisintegration;
import com.wafitz.pixelspacebase.items.wands.WandOfFirebolt;
import com.wafitz.pixelspacebase.items.wands.WandOfFlock;
import com.wafitz.pixelspacebase.items.wands.WandOfLightning;
import com.wafitz.pixelspacebase.items.wands.WandOfMagicMissile;
import com.wafitz.pixelspacebase.items.wands.WandOfPoison;
import com.wafitz.pixelspacebase.items.wands.WandOfReach;
import com.wafitz.pixelspacebase.items.wands.WandOfRegrowth;
import com.wafitz.pixelspacebase.items.wands.WandOfSlowness;
import com.wafitz.pixelspacebase.items.wands.WandOfTeleportation;
import com.wafitz.pixelspacebase.items.weapon.Weapon;
import com.wafitz.pixelspacebase.items.weapon.melee.BattleAxe;
import com.wafitz.pixelspacebase.items.weapon.melee.Dagger;
import com.wafitz.pixelspacebase.items.weapon.melee.Glaive;
import com.wafitz.pixelspacebase.items.weapon.melee.Knuckles;
import com.wafitz.pixelspacebase.items.weapon.melee.Longsword;
import com.wafitz.pixelspacebase.items.weapon.melee.Mace;
import com.wafitz.pixelspacebase.items.weapon.melee.Quarterstaff;
import com.wafitz.pixelspacebase.items.weapon.melee.ShortSword;
import com.wafitz.pixelspacebase.items.weapon.melee.Spear;
import com.wafitz.pixelspacebase.items.weapon.melee.Sword;
import com.wafitz.pixelspacebase.items.weapon.melee.WarHammer;
import com.wafitz.pixelspacebase.items.weapon.missiles.Boomerang;
import com.wafitz.pixelspacebase.items.weapon.missiles.CurareDart;
import com.wafitz.pixelspacebase.items.weapon.missiles.Dart;
import com.wafitz.pixelspacebase.items.weapon.missiles.IncendiaryDart;
import com.wafitz.pixelspacebase.items.weapon.missiles.Javelin;
import com.wafitz.pixelspacebase.items.weapon.missiles.Shuriken;
import com.wafitz.pixelspacebase.items.weapon.missiles.Tamahawk;
import com.wafitz.pixelspacebase.plants.Dreamweed;
import com.wafitz.pixelspacebase.plants.Earthroot;
import com.wafitz.pixelspacebase.plants.Fadeleaf;
import com.wafitz.pixelspacebase.plants.Firebloom;
import com.wafitz.pixelspacebase.plants.Icecap;
import com.wafitz.pixelspacebase.plants.Plant;
import com.wafitz.pixelspacebase.plants.Rotberry;
import com.wafitz.pixelspacebase.plants.Sorrowmoss;
import com.wafitz.pixelspacebase.plants.Sungrass;
import com.watabou.utils.Random;

public class Generator {

	public static enum Category {
		WEAPON	( 15,	Weapon.class ),
		ARMOR	( 10,	Armor.class ),
		POTION	( 50,	Potion.class ),
		SCROLL	( 40,	Scroll.class ),
		WAND	( 4,	Wand.class ),
		RING	( 2,	Ring.class ),
		SEED	( 5,	Plant.Seed.class ),
		FOOD	( 0,	Food.class ),
		GOLD	( 50,	Gold.class ),
		MISC	( 5,	Item.class );
		
		public Class<?>[] classes;
		public float[] probs;
		
		public float prob;
		public Class<? extends Item> superClass;
		
		private Category( float prob, Class<? extends Item> superClass ) {
			this.prob = prob;
			this.superClass = superClass;
		}
		
		public static int order( Item item ) {
			for (int i=0; i < values().length; i++) {
				if (values()[i].superClass.isInstance( item )) {
					return i;
				}
			}
			
			return item instanceof Bag ? Integer.MAX_VALUE : Integer.MAX_VALUE - 1;
		}
	};
	
	private static HashMap<Category,Float> categoryProbs = new HashMap<Generator.Category, Float>();
	
	static {
		
		Category.GOLD.classes = new Class<?>[]{ 
			Gold.class };
		Category.GOLD.probs = new float[]{ 1 };
		
		Category.SCROLL.classes = new Class<?>[]{ 
			ScrollOfIdentify.class,
			ScrollOfTeleportation.class,
			ScrollOfRemoveCurse.class,
			ScrollOfRecharging.class,
			ScrollOfMagicMapping.class,
			ScrollOfChallenge.class,
			ScrollOfTerror.class,
			ScrollOfLullaby.class,
			ScrollOfPsionicBlast.class,
			ScrollOfMirrorImage.class,
			ScrollOfUpgrade.class,
			ScrollOfEnchantment.class };
		Category.SCROLL.probs = new float[]{ 30, 10, 15, 10, 15, 12, 8, 8, 4, 6, 0, 1 };
		
		Category.POTION.classes = new Class<?>[]{ 
			PotionOfHealing.class,
			PotionOfExperience.class,
			PotionOfToxicGas.class,
			PotionOfParalyticGas.class,
			PotionOfLiquidFlame.class,
			PotionOfLevitation.class,
			PotionOfStrength.class,
			PotionOfMindVision.class,
			PotionOfPurity.class,
			PotionOfInvisibility.class,
			PotionOfMight.class,
			PotionOfFrost.class };
		Category.POTION.probs = new float[]{ 45, 4, 15, 10, 15, 10, 0, 20, 12, 10, 0, 10 };
		
		Category.WAND.classes = new Class<?>[]{ 
			WandOfTeleportation.class,
			WandOfSlowness.class,
			WandOfFirebolt.class,
			WandOfRegrowth.class,
			WandOfPoison.class,
			WandOfBlink.class,
			WandOfLightning.class,
			WandOfAmok.class,
			WandOfReach.class,
			WandOfFlock.class,
			WandOfMagicMissile.class,
			WandOfDisintegration.class,
			WandOfAvalanche.class };
		Category.WAND.probs = new float[]{ 10, 10, 15, 6, 10, 11, 15, 10, 6, 10, 0, 5, 5 };
		
		Category.WEAPON.classes = new Class<?>[]{ 
			Dagger.class,
			Knuckles.class,
			Quarterstaff.class,
			Spear.class,
			Mace.class,
			Sword.class,
			Longsword.class,
			BattleAxe.class,
			WarHammer.class,
			Glaive.class,
			ShortSword.class,
			Dart.class,
			Javelin.class,
			IncendiaryDart.class,
			CurareDart.class,
			Shuriken.class,
			Boomerang.class,
			Tamahawk.class };
		Category.WEAPON.probs = new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1 };
		
		Category.ARMOR.classes = new Class<?>[]{ 
			ClothArmor.class,
			LeatherArmor.class,
			MailArmor.class,
			ScaleArmor.class,
			PlateArmor.class };
		Category.ARMOR.probs = new float[]{ 1, 1, 1, 1, 1 };
		
		Category.FOOD.classes = new Class<?>[]{ 
			Food.class, 
			Pasty.class,
			MysteryMeat.class };
		Category.FOOD.probs = new float[]{ 4, 1, 0 };
			
		Category.RING.classes = new Class<?>[]{ 
			RingOfMending.class,
			RingOfDetection.class,
			RingOfShadows.class,
			RingOfPower.class,
			RingOfHerbalism.class,
			RingOfAccuracy.class,
			RingOfEvasion.class,
			RingOfSatiety.class,
			RingOfHaste.class,
			RingOfElements.class,
			RingOfHaggler.class,
			RingOfThorns.class };
		Category.RING.probs = new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 };
		
		Category.SEED.classes = new Class<?>[]{ 
			Firebloom.Seed.class,
			Icecap.Seed.class,
			Sorrowmoss.Seed.class,
			Dreamweed.Seed.class,
			Sungrass.Seed.class,
			Earthroot.Seed.class,
			Fadeleaf.Seed.class,
			Rotberry.Seed.class };
		Category.SEED.probs = new float[]{ 1, 1, 1, 1, 1, 1, 1, 0 };
		
		Category.MISC.classes = new Class<?>[]{ 
			Bomb.class,
			Honeypot.class};
		Category.MISC.probs = new float[]{ 2, 1 };
	}
	
	public static void reset() {
		for (Category cat : Category.values()) {
			categoryProbs.put( cat, cat.prob );
		}
	}
	
	public static Item random() {
		return random( Random.chances( categoryProbs ) );
	}
	
	public static Item random( Category cat ) {
		try {
			
			categoryProbs.put( cat, categoryProbs.get( cat ) / 2 );
			
			switch (cat) {
			case ARMOR:
				return randomArmor();
			case WEAPON:
				return randomWeapon();
			default:
				return ((Item)cat.classes[Random.chances( cat.probs )].newInstance()).random();
			}
			
		} catch (Exception e) {

			return null;
			
		}
	}
	
	public static Item random( Class<? extends Item> cl ) {
		try {
			
			return ((Item)cl.newInstance()).random();
			
		} catch (Exception e) {

			return null;
			
		}
	}
	
	public static Armor randomArmor() throws Exception {
		
		int curStr = Hero.STARTING_STR + Dungeon.potionOfStrength;
		
		Category cat = Category.ARMOR;
		
		Armor a1 = (Armor)cat.classes[Random.chances( cat.probs )].newInstance();
		Armor a2 = (Armor)cat.classes[Random.chances( cat.probs )].newInstance();
		
		a1.random();
		a2.random();
		
		return Math.abs( curStr - a1.STR ) < Math.abs( curStr - a2.STR ) ? a1 : a2;
	}
	
	public static Weapon randomWeapon() throws Exception {
		
		int curStr = Hero.STARTING_STR + Dungeon.potionOfStrength;
		
		Category cat = Category.WEAPON;
		
		Weapon w1 = (Weapon)cat.classes[Random.chances( cat.probs )].newInstance();
		Weapon w2 = (Weapon)cat.classes[Random.chances( cat.probs )].newInstance();
		
		w1.random();
		w2.random();
		
		return Math.abs( curStr - w1.STR ) < Math.abs( curStr - w2.STR ) ? w1 : w2;
	}
}
