package org.dionthorn.isekairpg.worlds;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.dionthorn.isekairpg.Engine;
import org.dionthorn.isekairpg.graphics.TileSet;
import org.dionthorn.isekairpg.utilities.Dice;

/**
 * The Region class contains Areas and has an assigned Biome which determines what type of Areas are created
 */
public class Region extends AbstractLocation {

    private final Area[][] areas;
    private final Biome biome;
    private final int areaSize;

    /**
     * All biomes that roll 1-9 on a d20 will spawn a WILD Setting Area
     * PLAINS   - OPEN, DUNGEON, HAMLET, VILLAGE, TOWN, CASTLE Setting Areas
     * HILLS    - OPEN, DUNGEON, HAMLET, VILLAGE, TOWN, CASTLE
     * MOUNTAIN - OPEN, DUNGEON, HAMLET
     * FOREST   - OPEN, DUNGEON, HAMLET, VILLAGE, TOWN
     * DESERT   - OPEN, DUNGEON, HAMLET, VILLAGE
     * TUNDRA   - OPEN, DUNGEON, HAMLET, VILLAGE
     */
    // You are at [placeName] a [Place.Type] in [areaName] a [Area.Setting] of [regionName] a [Region.Biome] region
    public enum Biome { PLAINS, HILLS, MOUNTAIN, FOREST, DESERT, TUNDRA }

    // Image[] index with [Biome.ordinal()]
    public static final Image[] BIOME_TILES = new TileSet("Biomes.png").getTiles();

    /**
     * Regions have Areas. The Region Biome determines the Area Settings
     */
    public Region(Biome biome, int x, int y, int areaSize) {
        super(x, y);
        // regions do not have a 'parent' location, all the regions together are considered the 'world'
        // you can access the world object anywhere with Engine.getGameState().getWorld()
        this.biome = biome;
        this.areaSize = areaSize;
        areas = new Area[areaSize][areaSize];

        // Fill region with wild lands
        for(int yArea=0; yArea<areaSize; yArea++) {
            for(int xArea=0; xArea<areaSize; xArea++) {
                areas[yArea][xArea] = new Area(this, Area.Setting.WILDS, xArea, yArea);
            }
        }

        int roll = Dice.d20.roll();
        Dice chanceX = new Dice(areaSize);
        Dice chanceY = new Dice(areaSize);
        if(roll > 16) {
            // 1-2 nations blueprint on a 17-20 (20%)
            populateNations(chanceX, chanceY);
        } else if(roll > 12) {
            // no nations but populated blueprint on a 13-16 (20%)
            populateHigh(chanceX, chanceY);
        } else if(roll > 4) {
            // sparsely populated blueprint on a 5-12 (40%)
            populateLow(chanceX, chanceY);
        } else {
            // dungeon blueprint on a 1-4 (20%)
            populateDungeon(chanceX, chanceY);
        }
    }

    /**
     * Will generate Areas with Castles, Towns, Villages, Hamlets depending on the World size
     * VERY_SMALL = d2 castles d2 towns d4 villages d6 hamlets
     * @param chanceX Dice representing the x coordinate roll
     * @param chanceY Dice representing the y coordinate roll
     */
    private void populateNations(Dice chanceX, Dice chanceY) {
        World theWorld = Engine.getGameState().getWorld();
        World.Size worldSize = theWorld.getWorldSize();

        Dice countDie = Dice.d2; // default VERY_SMALL
        if(worldSize == World.Size.MEDIUM) {
            countDie = new Dice(3);
        } else if(worldSize == World.Size.LARGE) {
            countDie = new Dice(4);
        }
        int nationCount = countDie.roll();
        int tries = 0;
        for(int nationSteps=0; nationSteps<nationCount; nationSteps++) {
            boolean validCastleSpot = false;
            tries = 0;
            while(!validCastleSpot) {
                int castleX = chanceX.roll() - 1;
                int castleY = chanceY.roll() - 1;
                if(areas[castleY][castleX].getSetting() == Area.Setting.WILDS ||
                        areas[castleY][castleX].getSetting() == Area.Setting.SAFEZONE) {
                    areas[castleY][castleX] = new Area(this, Area.Setting.CASTLE, castleX, castleY);
                    // tame land around castle
                    tameAround(castleX, castleY);
                    validCastleSpot = true;
                }
                tries++;
                if(tries > 10) {
                    break;
                }
            }
            int townCount = countDie.roll();
            for(int townSteps=0; townSteps<townCount; townSteps++) {
                boolean validTownSpot = false;
                tries = 0;
                while(!validTownSpot) {
                    int townX = chanceX.roll() - 1;
                    int townY = chanceY.roll() - 1;
                    if(areas[townY][townX].getSetting() == Area.Setting.WILDS ||
                            areas[townY][townX].getSetting() == Area.Setting.SAFEZONE) {
                        areas[townY][townX] = new Area(this, Area.Setting.TOWN, townX, townY);
                        // tame land around town
                        tameAround(townX, townY);
                        validTownSpot = true;
                    }
                    tries++;
                    if(tries > 10) {
                        break;
                    }
                }
            }
            countDie = new Dice(countDie.getFaces() + 1);
            int villageCount = countDie.roll();
            for(int villageSteps=0; villageSteps<villageCount; villageSteps++) {
                boolean validVillageSpot = false;
                tries = 0;
                while(!validVillageSpot) {
                    int villageX = chanceX.roll() - 1;
                    int villageY = chanceY.roll() - 1;
                    if(areas[villageY][villageX].getSetting() == Area.Setting.WILDS ||
                            areas[villageY][villageX].getSetting() == Area.Setting.SAFEZONE) {
                        areas[villageY][villageX] = new Area(this, Area.Setting.VILLAGE, villageX, villageY);
                        tameAround(villageX, villageY);
                        validVillageSpot = true;
                    }
                    tries++;
                    if(tries > 10) {
                        break;
                    }
                }
            }
            countDie = new Dice(countDie.getFaces() + 1);
            int hamletCount = countDie.roll();
            for(int hamletSteps=0; hamletSteps<hamletCount; hamletSteps++) {
                boolean validHamletSpot = false;
                tries = 0;
                while(!validHamletSpot) {
                    int hamletX = chanceX.roll() - 1;
                    int hamletY = chanceY.roll() - 1;
                    if(areas[hamletY][hamletX].getSetting() == Area.Setting.WILDS ||
                            areas[hamletY][hamletX].getSetting() == Area.Setting.SAFEZONE) {
                        areas[hamletY][hamletX] = new Area(this, Area.Setting.HAMLET, hamletX, hamletY);
                        validHamletSpot = true;
                    }
                    tries++;
                    if(tries > 10) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Will generate Areas with 0-1 Towns, 1-2 Villages, 1-2 Hamlets
     * @param chanceX Dice representing the x coordinate roll
     * @param chanceY Dice representing the y coordinate roll
     */
    private void populateHigh(Dice chanceX, Dice chanceY) {
        World theWorld = Engine.getGameState().getWorld();
        World.Size worldSize = theWorld.getWorldSize();

        Dice countDie = Dice.d2; // default VERY_SMALL
        if(worldSize == World.Size.MEDIUM) {
            countDie = new Dice(3);
        } else if(worldSize == World.Size.LARGE) {
            countDie = new Dice(4);
        }

        // populate with 0-1 towns
        int townCount = countDie.roll() - 1;
        int tries = 0;
        for(int townSteps=0; townSteps<townCount; townSteps++) {
            boolean validTownSpot = false;
            while(!validTownSpot) {
                int townX = chanceX.roll() - 1;
                int townY = chanceY.roll() - 1;
                if(areas[townY][townX].getSetting() == Area.Setting.WILDS ||
                        areas[townY][townX].getSetting() == Area.Setting.SAFEZONE) {
                    areas[townY][townX] = new Area(this, Area.Setting.TOWN, townX, townY);
                    tameAround(townX, townY);
                    validTownSpot = true;
                }
                tries++;
                if(tries > 10) {
                    break;
                }
            }
        }
        // populate with 1-2 villages
        countDie = new Dice(countDie.getFaces() + 1);
        int villageCount = countDie.roll();
        for(int villageSteps=0; villageSteps<villageCount; villageSteps++) {
            boolean validVillageSpot = false;
            tries = 0;
            while(!validVillageSpot) {
                int villageX = chanceX.roll() - 1;
                int villageY = chanceY.roll() - 1;
                if(areas[villageY][villageX].getSetting() == Area.Setting.WILDS ||
                        areas[villageY][villageX].getSetting() == Area.Setting.SAFEZONE) {
                    areas[villageY][villageX] = new Area(this, Area.Setting.VILLAGE, villageX, villageY);
                    tameAround(villageX, villageY);
                    validVillageSpot = true;
                }
                tries++;
                if(tries > 10) {
                    break;
                }
            }
        }
        //populate with 1-2 hamlets
        countDie = new Dice(countDie.getFaces() + 1);
        int hamletCount = countDie.roll();
        for(int hamletSteps=0; hamletSteps<hamletCount; hamletSteps++) {
            boolean validHamletSpot = false;
            tries = 0;
            while(!validHamletSpot) {
                int hamletX = chanceX.roll() - 1;
                int hamletY = chanceY.roll() - 1;
                if(areas[hamletY][hamletX].getSetting() == Area.Setting.WILDS ||
                        areas[hamletY][hamletX].getSetting() == Area.Setting.SAFEZONE) {
                    areas[hamletY][hamletX] = new Area(this, Area.Setting.HAMLET, hamletX, hamletY);
                    validHamletSpot = true;
                }
                tries++;
                if(tries > 10) {
                    break;
                }
            }
        }
    }

    /**
     * Will generate Areas with 0-1 Villages, 0-2 Hamlets
     * @param chanceX Dice representing the x coordinate roll
     * @param chanceY Dice representing the y coordinate roll
     */
    private void populateLow(Dice chanceX, Dice chanceY) {
        World theWorld = Engine.getGameState().getWorld();
        World.Size worldSize = theWorld.getWorldSize();

        Dice countDie = Dice.d2; // default VERY_SMALL
        if(worldSize == World.Size.MEDIUM) {
            countDie = new Dice(3);
        } else if(worldSize == World.Size.LARGE) {
            countDie = new Dice(4);
        }

        // populate with 0-1 villages
        int villageCount = countDie.roll() - 1;
        int tries = 0;
        for(int villageSteps=0; villageSteps<villageCount; villageSteps++) {
            boolean validVillageSpot = false;
            while(!validVillageSpot) {
                int villageX = chanceX.roll() - 1;
                int villageY = chanceY.roll() - 1;
                if(areas[villageY][villageX].getSetting() == Area.Setting.WILDS ||
                        areas[villageY][villageX].getSetting() == Area.Setting.SAFEZONE) {
                    areas[villageY][villageX] = new Area(this, Area.Setting.VILLAGE, villageX, villageY);
                    tameAround(villageX, villageY);
                    validVillageSpot = true;
                }
                tries++;
                if(tries > 10) {
                    break;
                }
            }
        }
        //populate with 0-2 hamlets
        countDie = new Dice(countDie.getFaces() + 1);
        int hamletCount = countDie.roll() - 1;
        for(int hamletSteps=0; hamletSteps<hamletCount; hamletSteps++) {
            boolean validHamletSpot = false;
            tries = 0;
            while(!validHamletSpot) {
                int hamletX = chanceX.roll() - 1;
                int hamletY = chanceY.roll() - 1;
                if(areas[hamletY][hamletX].getSetting() == Area.Setting.WILDS ||
                        areas[hamletY][hamletX].getSetting() == Area.Setting.SAFEZONE) {
                    areas[hamletY][hamletX] = new Area(this, Area.Setting.HAMLET, hamletX, hamletY);
                    validHamletSpot = true;
                }
                tries++;
                if(tries > 10) {
                    break;
                }
            }
        }
    }

    /**
     * Will generate Areas with 1-4 Dungeons
     * @param chanceX Dice representing the x coordinate roll
     * @param chanceY Dice representing the y coordinate roll
     */
    private void populateDungeon(Dice chanceX, Dice chanceY) {
        // populate with 1-4 dungeons!
        int dungeonCount = Dice.d4.roll();
        int tries = 0;
        for(int dungeonSteps=0; dungeonSteps<dungeonCount; dungeonSteps++) {
            boolean validDungeonSpot = false;
            while(!validDungeonSpot) {
                int dungeonX = chanceX.roll() - 1;
                int dungeonY = chanceY.roll() - 1;
                if(areas[dungeonY][dungeonX].getSetting() == Area.Setting.WILDS) {
                    areas[dungeonY][dungeonX] = new Area(this, Area.Setting.DUNGEON, dungeonX, dungeonY);
                    validDungeonSpot = true;
                }
                tries++;
                if(tries > 10) {
                    break;
                }
            }
        }
    }

    /**
     * Will Tame the areas around the provided x,y coordinate
     * @param x int representing the x coordinate
     * @param y int representing the y coordinate
     */
    private void tameAround(int x, int y) {
        for(int tameX=x-1; tameX<x+2; tameX++) {
            for(int tameY=y-1; tameY<y+2; tameY++) {
                if((tameX >= 0 && tameY >= 0) && (tameX < areaSize && tameY < areaSize)) {
                    if(areas[tameY][tameX].getSetting() == Area.Setting.WILDS) {
                        areas[tameY][tameX] = new Area(this, Area.Setting.SAFEZONE, tameX, tameY);
                    }
                }
            }
        }
    }

    // Logical Getters

    /**
     * Will return the Area at the provided x,y coordinate in this Region
     * @param x int representing the target x coordinate
     * @param y int representing the target y coordinate
     * @return Area representing the target Area at the provided x,y coordinate in this Region
     * return null if Area doesn't exist at x,y
     */
    public Area getArea(int x, int y) {
        Area toGet = null;
        try {
            toGet = areas[y][x];
        } catch(Exception e) {
            // if x,y doesn't exist in this area will return null
        }
        return toGet;
    }

    public Canvas getAreaMapCanvas(int areaX, int areaY) {
        Canvas map = new Canvas(areaSize * TileSet.TILE_SIZE, areaSize * TileSet.TILE_SIZE);
        GraphicsContext gc = map.getGraphicsContext2D();

        for (int x = 0; x < areaSize; x++) {
            for (int y = 0; y < areaSize; y++) {
                Biome biome = getBiome();
                Area.Setting areaSetting = getArea(x, y).getSetting();
                int tileID = (biome.ordinal() * Area.Setting.values().length) + areaSetting.ordinal();
                gc.drawImage(
                        Area.SETTINGS_TILES[tileID],
                        TileSet.TILE_SIZE * x,
                        TileSet.TILE_SIZE * y
                );
            }
        }
        // highlight player area with red rect
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeRect(TileSet.TILE_SIZE * areaX, TileSet.TILE_SIZE * areaY, TileSet.TILE_SIZE, TileSet.TILE_SIZE);

        return map;
    }

    // pure getters

    /**
     * Will return the Regions square size
     * @return int representing the length of one side of the square of this Regions in Areas
     */
    public int getAreaSize() { return areaSize; }

    /**
     * Will return the Area[][] of this Region
     * @return Area[][] representing all areas in this Region
     */
    public Area[][] getAreas() { return areas; }

    /**
     * Will return the Biome associated to this Region
     * @return Biome representing the biome of this Region
     */
    public Biome getBiome() { return biome; }

}
