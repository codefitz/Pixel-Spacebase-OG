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

import android.util.Log;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.Statistics;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.ui.GameLog;
import com.wafitz.pixelspacebase.windows.WndError;
import com.wafitz.pixelspacebase.windows.WndStory;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.io.FileNotFoundException;

import static com.wafitz.pixelspacebase.Dungeon.bossLevel;

public class InterlevelScene extends PixelScene {

    private static final float TIME_TO_FADE = 0.3f;

    private static final String TXT_DESCENDING	= "Descending...";
    private static final String TXT_ASCENDING	= "Ascending...";
    private static final String TXT_LOADING		= "Loading...";
    private static final String TXT_RESURRECTING= "Resurrecting...";
    private static final String TXT_RETURNING	= "Returning...";
    private static final String TXT_FALLING		= "Falling...";

    private static final String ERR_FILE_NOT_FOUND	= "File not found. For some reason.";
    private static final String ERR_GENERIC			= "Something went wrong..."	;

    public enum Mode {
        DESCEND, ASCEND, CONTINUE, RESURRECT, RETURN, FALL, NONE
    }

    public static Mode mode;

    public static int returnDepth;
    public static int returnPos;

    public static boolean noStory = false;

    public static boolean fallIntoPit;

    private enum Phase {
        FADE_IN, STATIC, FADE_OUT
    }

    private Phase phase;
    private float timeLeft;

    private BitmapText message;

    private Thread thread;
    private String error = null;

    @Override
    public void create() {
        super.create();

        String text = "";
        switch (mode) {
            case DESCEND:
                text = TXT_DESCENDING;
                break;
            case ASCEND:
                text = TXT_ASCENDING;
                break;
            case CONTINUE:
                text = TXT_LOADING;
                break;
            case RESURRECT:
                text = TXT_RESURRECTING;
                break;
            case RETURN:
                text = TXT_RETURNING;
                break;
            case FALL:
                text = TXT_FALLING;
                break;
            default:
        }

        message = PixelScene.createText( text, 9 );
        message.measure();
        message.x = (Camera.main.width - message.width()) / 2;
        message.y = (Camera.main.height - message.height()) / 2;
        add( message );

        phase = Phase.FADE_IN;
        timeLeft = TIME_TO_FADE;

        thread = new Thread() {
            @Override
            public void run() {

                try {

                    Generator.reset();

                    switch (mode) {
                        case DESCEND:
                            descend();
                            break;
                        case ASCEND:
                            ascend();
                            break;
                        case CONTINUE:
                            restore();
                            break;
                        case RESURRECT:
                            resurrect();
                            break;
                        case RETURN:
                            returnTo();
                            break;
                        case FALL:
                            fall();
                            break;
                        default:
                    }

                    if ((Dungeon.depth % 5) == 0) {
                        Sample.INSTANCE.load( Assets.SND_BOSS );
                    }

                } catch (FileNotFoundException e) {

                    error = ERR_FILE_NOT_FOUND;

                } catch (Exception e ) {

                    //error = ERR_GENERIC;
                    try {
                        messed_up();
                    } catch (Exception e1) {
                        error = ERR_GENERIC;
                    }

                }

                if (phase == Phase.STATIC && error == null) {
                    phase = Phase.FADE_OUT;
                    timeLeft = TIME_TO_FADE;
                }
            }
        };
        thread.start();
    }

    @Override
    public void update() {
        super.update();

        float p = timeLeft / TIME_TO_FADE;

        switch (phase) {

            case FADE_IN:
                message.alpha( 1 - p );
                if ((timeLeft -= Game.elapsed) <= 0) {
                    if (!thread.isAlive() && error == null) {
                        phase = Phase.FADE_OUT;
                        timeLeft = TIME_TO_FADE;
                    } else {
                        phase = Phase.STATIC;
                    }
                }
                break;

            case FADE_OUT:
                message.alpha( p );
                if (mode == Mode.CONTINUE || (mode == Mode.DESCEND && Dungeon.depth == 1)) {
                    Music.INSTANCE.volume( p );
                }
                if ((timeLeft -= Game.elapsed) <= 0) {
                    Game.switchScene( GameScene.class );
                }
                break;

            case STATIC:
                if (error != null) {
                    add( new WndError( error ) {
                        public void onBackPressed() {
                            super.onBackPressed();
                            Game.switchScene( StartScene.class );
                        }
                    } );
                    error = null;
                }
                break;
        }
    }

    private void descend() throws Exception {

        Actor.fixTime();
        GameLog.wipe();

        if (Dungeon.hero == null) {

            /*int width = Random.Int(16,52);
            int height = width >= 44 ? Random.Int(16,36) : Random.Int(30,52);
            //int width = 50;
            //int height = 16;
            int length = width * height;
            PixelSpacebase.level_width( width );
            PixelSpacebase.level_height( height );
            PixelSpacebase.level_length( length );*/

            Log.d("WAFITZ", "Running Dungeon.init()");

            Dungeon.init();
            Log.d("WAFITZ", "Dungeon initialised! noStory: " + noStory );
            if (noStory) {
                Log.d("WAFITZ", "Loading Dungeon.chapters.add(WndStory.ID_SEWERS)" );
                Dungeon.chapters.add( WndStory.ID_SEWERS );
                noStory = false;
            }
        } else {
            Log.d("WAFITZ", "Saving Level." );
            Dungeon.saveLevel();
        }

        Log.d("WAFITZ", "Dungeon depth = " + Dungeon.depth + ", Deepest floor: " + Statistics.deepestFloor );

        Level level;
        if (Dungeon.depth >= Statistics.deepestFloor) {
            Log.d("WAFITZ", "Starting newLevel()..." );
            level = Dungeon.newLevel();
        } else {
            Dungeon.depth++;
            Log.d("WAFITZ", "Set depth to " + Dungeon.depth + ", Loading level..." );
            level = Dungeon.loadLevel( Dungeon.hero.heroClass );
        }
        Log.d("WAFITZ", "Dungeon load finished - switching to entrance." );
        Dungeon.switchLevel( level, level.entrance );
    }

    private void fall() throws Exception {

        Actor.fixTime();
        Dungeon.saveLevel();

        Level level;
        if (Dungeon.depth >= Statistics.deepestFloor) {
            level = Dungeon.newLevel();
        } else {
            Dungeon.depth++;
            level = Dungeon.loadLevel( Dungeon.hero.heroClass );
        }
        Dungeon.switchLevel( level, fallIntoPit ? level.pitCell() : level.randomRespawnCell() );
    }

    private void ascend() throws Exception {
        Actor.fixTime();

        Dungeon.saveLevel();
        Dungeon.depth--;
        Level level = Dungeon.loadLevel( Dungeon.hero.heroClass );
        Dungeon.switchLevel( level, level.exit );
    }

    private void returnTo() throws Exception {

        Actor.fixTime();

        Dungeon.saveLevel();
        Dungeon.depth = returnDepth;
        Level level = Dungeon.loadLevel( Dungeon.hero.heroClass );
        //Dungeon.switchLevel( level, Level.resizingNeeded ? level.adjustPos( returnPos ) : returnPos );
        Dungeon.switchLevel( level, returnPos );
    }

    private void restore() throws Exception {

        Actor.fixTime();

        GameLog.wipe();

        Dungeon.loadGame( StartScene.curClass );
        if (Dungeon.depth == -1) {
            Dungeon.depth = Statistics.deepestFloor;
            Dungeon.switchLevel( Dungeon.loadLevel( StartScene.curClass ), -1 );
        } else {
            Level level = Dungeon.loadLevel( StartScene.curClass );
            //Dungeon.switchLevel( level, Level.resizingNeeded ? level.adjustPos( Dungeon.hero.pos ) : Dungeon.hero.pos );
            Dungeon.switchLevel( level, Dungeon.hero.pos );
        }
    }

    private void resurrect() throws Exception {

        Actor.fixTime();

        if (bossLevel()) {
            Dungeon.hero.resurrect( Dungeon.depth );
            Dungeon.depth--;
            Level level = Dungeon.newLevel();
            Dungeon.switchLevel( level, level.entrance );
        } else {
            Dungeon.hero.resurrect( -1 );
            Dungeon.resetLevel();
        }
    }

    private void messed_up() throws Exception {

        // This is an attempt to do full dungeon recreation on app crash
        // due to the random w*h regenerating on new app instance that I can't get to
        // permanently store.

        // DESCEND inits and rebuilds the level we crashed on - but ignores the previous levels
        // causing a new crash/init which drops us back on a new level at the same depth
        // ASCEND essentially inits and re-builds the level below when the hero attempts to ascend
        // So we lose all dungeon maps, but not progress hopefully.

        // This is because I have a habit of force closing background apps when my phone slows down.
        // I'm sure others will do this too.
        // messed_up() is only invoked in a force-close scenario. Clean exits will function as normal.

        //error = "An anomoly from the 4th dimension caused the spacebase to reboot itself." +
        //"Returning to the spacebase... game may require restart.";

        Log.d("WAFITZ", "Messed Up - Invoked!");

        Level level;
        int pos;

        switch (mode) {

            case ASCEND:
                Log.d("WAFITZ", "Case ASCEND (going up)");
                Actor.fixTime();

                //Dungeon.init();
                GameLog.wipe();

                Dungeon.loadGame( Dungeon.gameFile(Dungeon.hero.heroClass), true );
                Dungeon.depth--;
                Dungeon.depth--;
                level = Dungeon.newLevel();

                //level = Dungeon.loadLevel( Dungeon.hero.heroClass );

                PathFinder.setMapSize(Level.WIDTH, Level.HEIGHT);

                Dungeon.saveLevel();

                Dungeon.switchLevel( level, level.exit );
                break;

            case CONTINUE:
                Log.d("WAFITZ", "Case CONTINUE (restore from crash)");
                Actor.fixTime();

                //Dungeon.init();
                GameLog.wipe();

                level = Dungeon.newLevel();
                Dungeon.loadGame( Dungeon.gameFile(Dungeon.hero.heroClass), true );

                Log.d("WAFITZ", "Dungeon depth is: " + Dungeon.depth);

                //level = Dungeon.loadLevel( Dungeon.hero.heroClass );
                pos = level.entrance;

                if (bossLevel()) {
                    Dungeon.depth--;
                    Log.d("WAFITZ", "Level is boss, bumping depth to: " + Dungeon.depth);
                    Dungeon.depth--;
                    level = Dungeon.newLevel();
                    pos = level.exit;
                }

                Log.d("WAFITZ", "level.WIDTH: " + Level.WIDTH + ", level.HEIGHT: " + Level.HEIGHT);
                Log.d("WAFITZ", "PixelPrefs.WIDTH: " + PixelSpacebase.lvl_width() + ", PixelPrefs.HEIGHT: " + PixelSpacebase.lvl_height());

                PathFinder.setMapSize( Level.WIDTH, Level.HEIGHT );

                Dungeon.saveLevel();

                Log.d("WAFITZ", "Switching. level...");

                //Dungeon.switchLevel( level, level.randomRespawnCell() );
                Dungeon.switchLevel( level, pos );
                break;

            case DESCEND:
                Log.d("WAFITZ", "Case Descending, depth is: " + Dungeon.depth);
                Actor.fixTime();

                //Dungeon.init();
                GameLog.wipe();
                Dungeon.depth--;
                Log.d("WAFITZ", "Depth lowered: " + Dungeon.depth);
                level = Dungeon.newLevel();
                Log.d("WAFITZ", "Depth reset by new level: " + Dungeon.depth);
                int realdepth = Dungeon.depth;
                Dungeon.loadGame( Dungeon.gameFile(Dungeon.hero.heroClass), true );
                Log.d("WAFITZ", "Depth reset by loadgame: " + Dungeon.depth);
                Dungeon.depth = realdepth;
                Log.d("WAFITZ", "Real depth set: " + Dungeon.depth);

                PathFinder.setMapSize( Level.WIDTH, Level.HEIGHT );

                Log.d("WAFITZ", "Switching. level...");

                Dungeon.saveLevel();

                Dungeon.switchLevel( level, level.entrance );
                break;

        }

		/*String MESSED_UP = "An anomaly from the 4th dimension caused the spacebase to reboot itself." +
		"Returning to the spacebase... game may require restart.";
        GameScene.show( new WndMessage( MESSED_UP ) );
        ready();*/

    }

    @Override
    protected void onBackPressed() {
        // Do nothing
    }
}