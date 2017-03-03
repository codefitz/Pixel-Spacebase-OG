/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Pixel Spacebase
 * Copyright (C) 2017 Wes Fitzpatrick
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
package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Challenges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.Statistics;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Alchemy;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.WellWater;
import com.wafitz.pixelspacebase.actors.buffs.Awareness;
import com.wafitz.pixelspacebase.actors.buffs.Blindness;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.MindVision;
import com.wafitz.pixelspacebase.actors.buffs.Shadows;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.actors.mobs.Bestiary;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.effects.particles.FlowParticle;
import com.wafitz.pixelspacebase.effects.particles.WindParticle;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Gold;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.armor.Armor;
import com.wafitz.pixelspacebase.items.bags.ScrollHolder;
import com.wafitz.pixelspacebase.items.bags.SeedPouch;
import com.wafitz.pixelspacebase.items.food.Food;
import com.wafitz.pixelspacebase.items.potions.PotionOfHealing;
import com.wafitz.pixelspacebase.items.potions.PotionOfStrength;
import com.wafitz.pixelspacebase.items.scrolls.Scroll;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfEnchantment;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfUpgrade;
import com.wafitz.pixelspacebase.levels.features.Chasm;
import com.wafitz.pixelspacebase.levels.features.Door;
import com.wafitz.pixelspacebase.levels.features.HighGrass;
import com.wafitz.pixelspacebase.levels.painters.Painter;
import com.wafitz.pixelspacebase.levels.traps.AlarmTrap;
import com.wafitz.pixelspacebase.levels.traps.FireTrap;
import com.wafitz.pixelspacebase.levels.traps.GrippingTrap;
import com.wafitz.pixelspacebase.levels.traps.LightningTrap;
import com.wafitz.pixelspacebase.levels.traps.ParalyticTrap;
import com.wafitz.pixelspacebase.levels.traps.PoisonTrap;
import com.wafitz.pixelspacebase.levels.traps.SummoningTrap;
import com.wafitz.pixelspacebase.levels.traps.ToxicTrap;
import com.wafitz.pixelspacebase.mechanics.ShadowCaster;
import com.wafitz.pixelspacebase.plants.Plant;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.Scene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Level implements Bundlable {

    protected static final float TIME_TO_RESPAWN = 50;
    private static final String TXT_HIDDEN_PLATE_CLICKS = "A hidden pressure plate clicks!";
    private static final String MAP = "map";
    private static final String VISITED = "visited";
    private static final String MAPPED = "mapped";
    private static final String ENTRANCE = "entrance";
    private static final String EXIT		= "exit";
	private static final String HEAPS		= "heaps";
	private static final String PLANTS		= "plants";
	private static final String MOBS		= "mobs";
	private static final String BLOBS		= "blobs";

    public static boolean[] fieldOfView;
    public static boolean[] passable;
    public static boolean[] losBlocking;
    public static boolean[] flamable;
    public static boolean[] secret;
    public static boolean[] solid;
    public static boolean[] avoid;
    public static boolean[] water;
    public static boolean[] pit;
    public static boolean[] discoverable;
    protected static boolean pitRoomNeeded = false;
    protected static boolean weakFloorCreated = false;
    public int[] map;
    public boolean[] visited;
    public boolean[] mapped;
    public int viewDistance = Dungeon.isChallenged(Challenges.DARKNESS) ? 3 : 8;
    public Feeling feeling = Feeling.NONE;
    public int entrance;
    public int exit;
    public HashSet<Mob> mobs;
    public SparseArray<Heap> heaps;
    public HashMap<Class<? extends Blob>, Blob> blobs;
    public SparseArray<Plant> plants;
    public int color1 = 0x004400;
    public int color2 = 0x88CC44;
    protected int width;
    protected int height;
    protected int length;
    protected ArrayList<Item> itemsToSpawn = new ArrayList<>();

    public static void set(int cell, int terrain) {
        Painter.set(Dungeon.level, cell, terrain);

        int flags = Terrain.flags[terrain];
        passable[cell] = (flags & Terrain.PASSABLE) != 0;
        losBlocking[cell] = (flags & Terrain.LOS_BLOCKING) != 0;
        flamable[cell] = (flags & Terrain.FLAMABLE) != 0;
        secret[cell] = (flags & Terrain.SECRET) != 0;
        solid[cell] = (flags & Terrain.SOLID) != 0;
        avoid[cell] = (flags & Terrain.AVOID) != 0;
        pit[cell] = (flags & Terrain.PIT) != 0;
        water[cell] = terrain == Terrain.WATER || terrain >= Terrain.WATER_TILES;
    }

    public void create() {

        setupSize();
        PathFinder.setMapSize(width(), height());
        passable = new boolean[length()];
        losBlocking = new boolean[length()];
        flamable = new boolean[length()];
        secret = new boolean[length()];
        solid = new boolean[length()];
        avoid = new boolean[length()];
        water = new boolean[length()];
        pit = new boolean[length()];

        map = new int[length()];
        visited = new boolean[length()];
        Arrays.fill(visited, false);
        mapped = new boolean[length()];
        Arrays.fill(mapped, false);

        if (!Dungeon.bossLevel()) {
            addItemToSpawn(Generator.random(Generator.Category.FOOD));
            if (Dungeon.posNeeded()) {
                addItemToSpawn(new PotionOfStrength());
                Dungeon.potionOfStrength++;
            }
            if (Dungeon.souNeeded()) {
                addItemToSpawn(new ScrollOfUpgrade());
                Dungeon.scrollsOfUpgrade++;
            }
            if (Dungeon.soeNeeded()) {
                addItemToSpawn(new ScrollOfEnchantment());
                Dungeon.scrollsOfEnchantment++;
            }

            if (Dungeon.depth > 1) {
                switch (Random.Int(10)) {
                    case 0:
                        if (!Dungeon.bossLevel(Dungeon.depth + 1)) {
                            feeling = Feeling.CHASM;
                        }
                        break;
                    case 1:
                        feeling = Feeling.WATER;
                        break;
                    case 2:
                        feeling = Feeling.GRASS;
                        break;
                }
            }
        }

        boolean pitNeeded = Dungeon.depth > 1 && weakFloorCreated;

		do {
            Arrays.fill(map, feeling == Feeling.CHASM ? Terrain.CHASM : Terrain.WALL);

			pitRoomNeeded = pitNeeded;
			weakFloorCreated = false;

            mobs = new HashSet<>();
            heaps = new SparseArray<>();
            blobs = new HashMap<>();
            plants = new SparseArray<>();

        } while (!build());

		decorate();

		buildFlagMaps();

		cleanWalls();

		createMobs();

		createItems();

    }

    protected void setupSize() {
        if (width == 0 || height == 0)
            width = Random.Int(16, 52);
        height = width >= 44 ? Random.Int(16, 36) : Random.Int(30, 52);
        //width = height = 32;
        length = width * height;

    }

    public void reset() {

        for (Mob mob : mobs.toArray(new Mob[0])) {
            if (!mob.reset()) {
                mobs.remove( mob );
			}
		}
		createMobs();
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {

        if (bundle.contains("width") && bundle.contains("height")) {
            width = bundle.getInt("width");
            height = bundle.getInt("height");
        } else
            width = height = 32; //default sizes
        length = width * height;
        PathFinder.setMapSize(width(), height());

        mobs = new HashSet<>();
        heaps = new SparseArray<>();
        blobs = new HashMap<>();
        plants = new SparseArray<>();

        map = bundle.getIntArray(MAP);
        visited	= bundle.getBooleanArray( VISITED );
		mapped	= bundle.getBooleanArray( MAPPED );
		
		entrance	= bundle.getInt( ENTRANCE );
		exit		= bundle.getInt( EXIT );

		weakFloorCreated = false;
		
		Collection<Bundlable> collection = bundle.getCollection( HEAPS );
		for (Bundlable h : collection) {
			Heap heap = (Heap)h;
            if (!heap.isEmpty())
                heaps.put(heap.pos, heap);
        }

        collection = bundle.getCollection(PLANTS);
        for (Bundlable p : collection) {
            Plant plant = (Plant) p;
            plants.put( plant.pos, plant );
		}
		
		collection = bundle.getCollection( MOBS );
		for (Bundlable m : collection) {
			Mob mob = (Mob)m;
			if (mob != null) {
				mobs.add( mob );
			}
		}
		
		collection = bundle.getCollection( BLOBS );
		for (Bundlable b : collection) {
			Blob blob = (Blob)b;
			blobs.put( blob.getClass(), blob );
		}
		
		buildFlagMaps();
		cleanWalls();
	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
        bundle.put("width", width);
        bundle.put("height", height);
        bundle.put(MAP, map);
        bundle.put(VISITED, visited);
        bundle.put(MAPPED, mapped);
        bundle.put(ENTRANCE, entrance);
        bundle.put( EXIT, exit );
		bundle.put( HEAPS, heaps.values() );
		bundle.put( PLANTS, plants.values() );
		bundle.put( MOBS, mobs );
		bundle.put( BLOBS, blobs.values() );
	}
	
	public int tunnelTile() {
		return feeling == Feeling.CHASM ? Terrain.EMPTY_SP : Terrain.EMPTY;
	}

    public int width() {
        if (width == 0)
            setupSize();
        return width;
    }

    public int height() {
        if (height == 0)
            setupSize();
        return height;
    }

    public int length() {
        if (length == 0)
            setupSize();
        return length;
    }

	public String tilesTex() {
		return null;
	}
	
	public String waterTex() {
		return null;
	}
	
	abstract protected boolean build();

	abstract protected void decorate();

	abstract protected void createMobs();

	abstract protected void createItems();
	
	public void addVisuals( Scene scene ) {
        for (int i = 0; i < length(); i++) {
            if (pit[i]) {
                scene.add(new WindParticle.Wind(i));
                if (i >= width() && water[i - width()]) {
                    scene.add(new FlowParticle.Flow(i - width()));
                }
            }
        }
    }

    public int nMobs() {
        return 0;
    }

    public Mob findMob(int pos) {
        for (Mob mob : mobs) {
            if (mob.pos == pos) {
                return mob;
            }
        }
        return null;
    }

    public Actor respawner() {
        return new Actor() {
            @Override
            protected boolean act() {
                if (mobs.size() < nMobs()) {

                    Mob mob = Bestiary.mutable(Dungeon.depth);
                    mob.state = mob.WANDERING;
                    mob.pos = randomRespawnCell();
                    if (Dungeon.hero.isAlive() && mob.pos != -1) {
                        GameScene.add(mob);
                        if (Statistics.amuletObtained) {
							mob.beckon( Dungeon.hero.pos );
						}
					}
				}
				spend( Dungeon.nightMode || Statistics.amuletObtained ? TIME_TO_RESPAWN / 2 : TIME_TO_RESPAWN );
				return true;
			}
		};
	}
	
	public int randomRespawnCell() {
		int cell;
		do {
            cell = Random.Int(length());
        } while (!passable[cell] || Dungeon.visible[cell] || Actor.findChar(cell) != null);
        return cell;
    }

    public int randomDestination() {
        int cell;
		do {
            cell = Random.Int(length());
        } while (!passable[cell]);
        return cell;
    }

    public void addItemToSpawn(Item item) {
        if (item != null) {
			itemsToSpawn.add( item );
		}
	}
	
	public Item itemToSpanAsPrize() {
		if (Random.Int( itemsToSpawn.size() + 1 ) > 0) {
			Item item = Random.element( itemsToSpawn );
			itemsToSpawn.remove( item );
			return item;
		} else {
			return null;
		}
	}

    protected void buildFlagMaps() {

        fieldOfView = new boolean[length()];

        passable = new boolean[length()];
        losBlocking = new boolean[length()];
        flamable = new boolean[length()];
        secret = new boolean[length()];
        solid = new boolean[length()];
        avoid = new boolean[length()];
        water = new boolean[length()];
        pit = new boolean[length()];

        for (int i = 0; i < length(); i++) {
            int flags = Terrain.flags[map[i]];
            passable[i] = (flags & Terrain.PASSABLE) != 0;
            losBlocking[i] = (flags & Terrain.LOS_BLOCKING) != 0;
            flamable[i] = (flags & Terrain.FLAMABLE) != 0;
            secret[i]		= (flags & Terrain.SECRET) != 0;
			solid[i]		= (flags & Terrain.SOLID) != 0;
			avoid[i]		= (flags & Terrain.AVOID) != 0;
			water[i]		= (flags & Terrain.LIQUID) != 0;
			pit[i]			= (flags & Terrain.PIT) != 0;
		}

        int lastRow = length() - width();
        for (int i = 0; i < width(); i++) {
            passable[i] = avoid[i] = false;
            passable[lastRow + i] = avoid[lastRow + i] = false;
        }
        for (int i = width(); i < lastRow; i += width()) {
            passable[i] = avoid[i] = false;
            passable[i + width() - 1] = avoid[i + width() - 1] = false;
        }

        for (int i = width(); i < length() - width(); i++) {

            if (water[i]) {
                map[i] = getWaterTile(i);
            }

            if (pit[i]) {
                if (!pit[i - width()]) {
                    int c = map[i - width()];
                    if (c == Terrain.EMPTY_SP || c == Terrain.STATUE_SP) {
                        map[i] = Terrain.CHASM_FLOOR_SP;
                    } else if (water[i - width()]) {
                        map[i] = Terrain.CHASM_WATER;
                    } else if ((Terrain.flags[c] & Terrain.UNSTITCHABLE) != 0) {
                        map[i] = Terrain.CHASM_WALL;
                    } else {
                        map[i] = Terrain.CHASM_FLOOR;
					}
				}
			}
		}
	}
	
	private int getWaterTile( int pos ) {
		int t = Terrain.WATER_TILES;
        for (int j = 0; j < PathFinder.NEIGHBOURS4.length; j++) {
            if ((Terrain.flags[map[pos + PathFinder.NEIGHBOURS4[j]]] & Terrain.UNSTITCHABLE) != 0) {
                t += 1 << j;
            }
        }
        return t;
    }
	
	public void destroy( int pos ) {
		if ((Terrain.flags[map[pos]] & Terrain.UNSTITCHABLE) == 0) {
			
			set( pos, Terrain.EMBERS );
			
		} else {
			boolean flood = false;
            for (int j = 0; j < PathFinder.NEIGHBOURS4.length; j++) {
                if (water[pos + PathFinder.NEIGHBOURS4[j]]) {
                    flood = true;
                    break;
                }
            }
            if (flood) {
				set( pos, getWaterTile( pos ) );
			} else {
				set( pos, Terrain.EMBERS );
			}
		}
	}

    protected void cleanWalls() {
        discoverable = new boolean[length()];
        for (int i = 0; i < length(); i++) {

            boolean d = false;

            for (int j = 0; j < PathFinder.NEIGHBOURS9.length; j++) {
                int n = i + PathFinder.NEIGHBOURS9[j];
                if (n >= 0 && n < length() && map[n] != Terrain.WALL && map[n] != Terrain.WALL_DECO) {
                    d = true;
                    break;
                }
            }

            if (d) {
				d = false;

                for (int j = 0; j < PathFinder.NEIGHBOURS9.length; j++) {
                    int n = i + PathFinder.NEIGHBOURS9[j];
                    if (n >= 0 && n < length() && !pit[n]) {
                        d = true;
                        break;
                    }
                }
            }
			
			discoverable[i] = d;
		}
	}
	
	public Heap drop( Item item, int cell ) {

		if (Dungeon.isChallenged( Challenges.NO_FOOD ) && item instanceof Food) {
			item = new Gold( item.price() );
		} else
		if (Dungeon.isChallenged( Challenges.NO_ARMOR ) && item instanceof Armor) {
			item = new Gold( item.price() );
		} else
		if (Dungeon.isChallenged( Challenges.NO_HEALING ) && item instanceof PotionOfHealing) {
			item = new Gold( item.price() );
		} else
		if (Dungeon.isChallenged( Challenges.NO_HERBALISM ) && item instanceof SeedPouch) {
			item = new Gold( item.price() );
		} else
		if (Dungeon.isChallenged( Challenges.NO_SCROLLS ) && (item instanceof Scroll || item instanceof ScrollHolder)) {
			if (item instanceof ScrollOfUpgrade) {
				// These scrolls still can be found
			} else {
				item = new Gold( item.price() );
			}
		}

		if ((map[cell] == Terrain.ALCHEMY) && !(item instanceof Plant.Seed)) {
			int n;
			do {
                n = cell + PathFinder.NEIGHBOURS8[Random.Int(8)];
            } while (map[n] != Terrain.EMPTY_SP);
            cell = n;
        }

        Heap heap = heaps.get(cell);
        if (heap == null) {

			heap = new Heap();
            //heap.seen = Dungeon.visible[cell];
            heap.pos = cell;
            if (map[cell] == Terrain.CHASM || (Dungeon.level != null && pit[cell])) {
                Dungeon.dropToChasm(item);
                GameScene.discard(heap);
            } else {
                heaps.put(cell, heap);
                GameScene.add( heap );
			}

		} else if (heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST) {

			int n;
			do {
                n = cell + PathFinder.NEIGHBOURS8[Random.Int(8)];
            } while (!Level.passable[n] && !Level.avoid[n]);
            return drop(item, n);

        }
        heap.drop(item);

        if (Dungeon.level != null) {
            press(cell, null);
        }

        return heap;
    }

    public Plant plant( Plant.Seed seed, int pos ) {
		Plant plant = plants.get( pos );
		if (plant != null) {
			plant.wither();
		}

		plant = seed.couch( pos );
		plants.put( pos, plant );

		GameScene.add( plant );

		return plant;
	}
	
	public void uproot( int pos ) {
		plants.delete( pos );
	}

    public void discover(int cell) {
        set(cell, Terrain.discover(map[cell]));
        GameScene.updateMap(cell);
    }

    public int pitCell() {
        return randomRespawnCell();
    }

    public void press(int cell, Char ch) {

		if (pit[cell] && ch == Dungeon.hero) {
			Chasm.heroFall( cell );
			return;
		}

        boolean trap = false;

        switch (map[cell]) {

            case Terrain.SECRET_TOXIC_TRAP:
			GLog.i( TXT_HIDDEN_PLATE_CLICKS );
		case Terrain.TOXIC_TRAP:
			trap = true;
			ToxicTrap.trigger( cell, ch );
			break;

            case Terrain.SECRET_FIRE_TRAP:
			GLog.i( TXT_HIDDEN_PLATE_CLICKS );
		case Terrain.FIRE_TRAP:
			trap = true;
			FireTrap.trigger( cell, ch );
			break;

            case Terrain.SECRET_PARALYTIC_TRAP:
			GLog.i( TXT_HIDDEN_PLATE_CLICKS );
		case Terrain.PARALYTIC_TRAP:
			trap = true;
			ParalyticTrap.trigger( cell,  ch );
			break;

            case Terrain.SECRET_POISON_TRAP:
			GLog.i( TXT_HIDDEN_PLATE_CLICKS );
		case Terrain.POISON_TRAP:
			trap = true;
			PoisonTrap.trigger( cell, ch );
			break;

            case Terrain.SECRET_ALARM_TRAP:
			GLog.i( TXT_HIDDEN_PLATE_CLICKS );
		case Terrain.ALARM_TRAP:
			trap = true;
			AlarmTrap.trigger( cell, ch );
			break;

            case Terrain.SECRET_LIGHTNING_TRAP:
			GLog.i( TXT_HIDDEN_PLATE_CLICKS );
		case Terrain.LIGHTNING_TRAP:
			trap = true;
			LightningTrap.trigger( cell, ch );
			break;

            case Terrain.SECRET_GRIPPING_TRAP:
			GLog.i( TXT_HIDDEN_PLATE_CLICKS );
		case Terrain.GRIPPING_TRAP:
			trap = true;
			GrippingTrap.trigger( cell, ch );
			break;

            case Terrain.SECRET_SUMMONING_TRAP:
			GLog.i( TXT_HIDDEN_PLATE_CLICKS );
		case Terrain.SUMMONING_TRAP:
			trap = true;
			SummoningTrap.trigger( cell, ch );
			break;

            case Terrain.HIGH_GRASS:
			HighGrass.trample( this, cell, ch );
			break;

            case Terrain.WELL:
			WellWater.affectCell( cell );
			break;

            case Terrain.ALCHEMY:
			if (ch == null) {
				Alchemy.transmute( cell );
			}
			break;

            case Terrain.DOOR:
			Door.enter( cell );
			break;
		}

        if (trap) {
			Sample.INSTANCE.play( Assets.SND_TRAP );
			if (ch == Dungeon.hero) {
				Dungeon.hero.interrupt();
			}
			set( cell, Terrain.INACTIVE_TRAP );
			GameScene.updateMap( cell );
		}

        Plant plant = plants.get( cell );
		if (plant != null) {
			plant.activate( ch );
		}
	}

    public void mobPress( Mob mob ) {

		int cell = mob.pos;

        if (pit[cell] && !mob.flying) {
			Chasm.mobFall( mob );
			return;
		}

        boolean trap = true;
		switch (map[cell]) {

            case Terrain.TOXIC_TRAP:
			ToxicTrap.trigger( cell,  mob );
			break;

            case Terrain.FIRE_TRAP:
			FireTrap.trigger( cell, mob );
			break;

            case Terrain.PARALYTIC_TRAP:
			ParalyticTrap.trigger( cell,  mob );
			break;

            case Terrain.POISON_TRAP:
			PoisonTrap.trigger( cell, mob );
			break;

            case Terrain.ALARM_TRAP:
			AlarmTrap.trigger( cell,  mob );
			break;

            case Terrain.LIGHTNING_TRAP:
			LightningTrap.trigger( cell, mob );
			break;

            case Terrain.GRIPPING_TRAP:
			GrippingTrap.trigger( cell, mob );
			break;

            case Terrain.SUMMONING_TRAP:
			SummoningTrap.trigger( cell, mob );
			break;

            case Terrain.DOOR:
			Door.enter( cell );

            default:
			trap = false;
		}

        if (trap) {
			if (Dungeon.visible[cell]) {
				Sample.INSTANCE.play( Assets.SND_TRAP );
			}
			set( cell, Terrain.INACTIVE_TRAP );
			GameScene.updateMap( cell );
		}

        Plant plant = plants.get( cell );
		if (plant != null) {
			plant.activate( mob );
		}
	}
	
	public boolean[] updateFieldOfView( Char c ) {

        int cx = c.pos % width();
        int cy = c.pos / width();

        boolean sighted = c.buff(Blindness.class) == null && c.buff(Shadows.class) == null && c.isAlive();
        if (sighted) {
            ShadowCaster.castShadow(cx, cy, fieldOfView, c.viewDistance);
        } else {
            Arrays.fill( fieldOfView, false );
		}

		int sense = 1;
		if (c.isAlive()) {
			for (Buff b : c.buffs( MindVision.class )) {
				sense = Math.max( ((MindVision)b).distance, sense );
			}
		}

        if (!sighted || sense > 1) {

            int ax = Math.max( 0, cx - sense );
            int bx = Math.min(cx + sense, width() - 1);
            int ay = Math.max(0, cy - sense);
            int by = Math.min(cy + sense, height() - 1);

			int len = bx - ax + 1;
            int pos = ax + ay * width();
            for (int y = ay; y <= by; y++, pos += width()) {
                Arrays.fill(fieldOfView, pos, pos + len, true);
            }

            for (int i = 0; i < length(); i++) {
                fieldOfView[i] &= discoverable[i];
            }
        }

        if (c.isAlive()) {
            if (c.buff( MindVision.class ) != null) {
				for (Mob mob : mobs) {
					int p = mob.pos;
					fieldOfView[p] = true;
					fieldOfView[p + 1] = true;
					fieldOfView[p - 1] = true;
                    fieldOfView[p + width() + 1] = true;
                    fieldOfView[p + width() - 1] = true;
                    fieldOfView[p - width() + 1] = true;
                    fieldOfView[p - width() - 1] = true;
                    fieldOfView[p + width()] = true;
                    fieldOfView[p - width()] = true;
                }
            } else if (c == Dungeon.hero && ((Hero) c).heroClass == HeroClass.HUNTRESS) {
                for (Mob mob : mobs) {
                    int p = mob.pos;
                    if (distance( c.pos, p) == 2) {
						fieldOfView[p] = true;
						fieldOfView[p + 1] = true;
						fieldOfView[p - 1] = true;
                        fieldOfView[p + width() + 1] = true;
                        fieldOfView[p + width() - 1] = true;
                        fieldOfView[p - width() + 1] = true;
                        fieldOfView[p - width() - 1] = true;
                        fieldOfView[p + width()] = true;
                        fieldOfView[p - width()] = true;
                    }
                }
            }
            if (c.buff(Awareness.class) != null) {
                for (Heap heap : heaps.values()) {
					int p = heap.pos;
					fieldOfView[p] = true;
					fieldOfView[p + 1] = true;
					fieldOfView[p - 1] = true;
                    fieldOfView[p + width() + 1] = true;
                    fieldOfView[p + width() - 1] = true;
                    fieldOfView[p - width() + 1] = true;
                    fieldOfView[p - width() - 1] = true;
                    fieldOfView[p + width()] = true;
                    fieldOfView[p - width()] = true;
                }
            }
        }

        return fieldOfView;
    }

    public int distance(int a, int b) {
        int ax = a % width();
        int ay = a / width();
        int bx = b % width();
        int by = b / width();
        return Math.max(Math.abs(ax - bx), Math.abs(ay - by));
    }

    public boolean adjacent(int a, int b) {
        int diff = Math.abs(a - b);
        return diff == 1 || diff == width() || diff == width() + 1 || diff == width() - 1;
    }

    //returns true if the input is a valid tile within the level
    public boolean insideMap(int tile) {
        //top and bottom row and beyond
        return !((tile < width || tile >= length - width) ||
                //left and right column
                (tile % width == 0 || tile % width == width - 1));
    }

    public Point cellToPoint(int cell) {
        return new Point(cell % width(), cell / width());
    }

    public int pointToCell(Point p) {
        return p.x + p.y * width();
    }

    public String tileName(int tile) {

        if (tile >= Terrain.WATER_TILES) {
            return tileName(Terrain.WATER);
        }

		if (tile != Terrain.CHASM && (Terrain.flags[tile] & Terrain.PIT) != 0) {
			return tileName( Terrain.CHASM );
		}

        switch (tile) {
		case Terrain.CHASM:
			return "Chasm";
		case Terrain.EMPTY:
		case Terrain.EMPTY_SP:
		case Terrain.EMPTY_DECO:
		case Terrain.SECRET_TOXIC_TRAP:
		case Terrain.SECRET_FIRE_TRAP:
		case Terrain.SECRET_PARALYTIC_TRAP:
		case Terrain.SECRET_POISON_TRAP:
		case Terrain.SECRET_ALARM_TRAP:
		case Terrain.SECRET_LIGHTNING_TRAP:
			return "Floor";
		case Terrain.GRASS:
			return "Grass";
		case Terrain.WATER:
			return "Water";
		case Terrain.WALL:
		case Terrain.WALL_DECO:
		case Terrain.SECRET_DOOR:
			return "Wall";
		case Terrain.DOOR:
			return "Closed door";
		case Terrain.OPEN_DOOR:
			return "Open door";
		case Terrain.ENTRANCE:
			return "Depth entrance";
		case Terrain.EXIT:
			return "Depth exit";
		case Terrain.EMBERS:
			return "Embers";
		case Terrain.LOCKED_DOOR:
			return "Locked door";
		case Terrain.PEDESTAL:
			return "Pedestal";
		case Terrain.BARRICADE:
			return "Barricade";
		case Terrain.HIGH_GRASS:
			return "High grass";
		case Terrain.LOCKED_EXIT:
			return "Locked depth exit";
		case Terrain.UNLOCKED_EXIT:
			return "Unlocked depth exit";
		case Terrain.SIGN:
			return "Sign";
		case Terrain.WELL:
			return "Well";
		case Terrain.EMPTY_WELL:
			return "Empty well";
		case Terrain.STATUE:
		case Terrain.STATUE_SP:
			return "Statue";
		case Terrain.TOXIC_TRAP:
			return "Toxic gas trap";
		case Terrain.FIRE_TRAP:
			return "Fire trap";
		case Terrain.PARALYTIC_TRAP:
			return "Paralytic gas trap";
		case Terrain.POISON_TRAP:
			return "Poison dart trap";
		case Terrain.ALARM_TRAP:
			return "Alarm trap";
		case Terrain.LIGHTNING_TRAP:
			return "Lightning trap";
		case Terrain.GRIPPING_TRAP:
			return "Gripping trap";
		case Terrain.SUMMONING_TRAP:
			return "Summoning trap";
		case Terrain.INACTIVE_TRAP:
			return "Triggered trap";
		case Terrain.BOOKSHELF:
			return "Bookshelf";
		case Terrain.ALCHEMY:
			return "Alchemy pot";
		default:
			return "???";
		}
	}

    public String tileDesc( int tile ) {

        switch (tile) {
		case Terrain.CHASM:
			return "You can't see the bottom.";
		case Terrain.WATER:
			return "In case of burning step into the water to extinguish the fire.";
		case Terrain.ENTRANCE:
			return "Stairs lead up to the upper depth.";
		case Terrain.EXIT:
		case Terrain.UNLOCKED_EXIT:
			return "Stairs lead down to the lower depth.";
		case Terrain.EMBERS:
			return "Embers cover the floor.";
		case Terrain.HIGH_GRASS:
			return "Dense vegetation blocks the view.";
		case Terrain.LOCKED_DOOR:
			return "This door is locked, you need a matching key to unlock it.";
		case Terrain.LOCKED_EXIT:
			return "Heavy bars block the stairs leading down.";
		case Terrain.BARRICADE:
			return "The wooden barricade is firmly set but has dried over the years. Might it burn?";
		case Terrain.SIGN:
			return "You can't read the text from here.";
		case Terrain.TOXIC_TRAP:
		case Terrain.FIRE_TRAP:
		case Terrain.PARALYTIC_TRAP:
		case Terrain.POISON_TRAP:
		case Terrain.ALARM_TRAP:
		case Terrain.LIGHTNING_TRAP:
		case Terrain.GRIPPING_TRAP:
		case Terrain.SUMMONING_TRAP:
			return "Stepping onto a hidden pressure plate will activate the trap.";
		case Terrain.INACTIVE_TRAP:
			return "The trap has been triggered before and it's not dangerous anymore.";
		case Terrain.STATUE:
		case Terrain.STATUE_SP:
			return "Someone wanted to adorn this place, but failed, obviously.";
		case Terrain.ALCHEMY:
			return "Drop some seeds here to cook a potion.";
		case Terrain.EMPTY_WELL:
			return "The well has run dry.";
		default:
			if (tile >= Terrain.WATER_TILES) {
				return tileDesc( Terrain.WATER );
			}
			if ((Terrain.flags[tile] & Terrain.PIT) != 0) {
				return tileDesc( Terrain.CHASM );
			}
			return "";
		}
    }

    public enum Feeling {
        NONE,
        CHASM,
        WATER,
        GRASS
    }
}
