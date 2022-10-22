package org.dionthorn.isekairpg.worlds;

import org.dionthorn.isekairpg.Engine;
import org.dionthorn.isekairpg.utilities.Dice;
import java.util.Locale;

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
        for(int nationSteps=0; nationSteps<nationCount; nationSteps++) {
            boolean validCastleSpot = false;
            while(!validCastleSpot) {
                int castleX = chanceX.roll() - 1;
                int castleY = chanceY.roll() - 1;
                if(areas[castleY][castleX].getSetting() == Area.Setting.WILDS ||
                        areas[castleY][castleX].getSetting() == Area.Setting.SAFE) {
                    areas[castleY][castleX] = new Area(this, Area.Setting.CASTLE, castleX, castleY);
                    // tame land around castle
                    tameAround(castleX, castleY);
                    validCastleSpot = true;
                }
            }
            int townCount = countDie.roll();
            for(int townSteps=0; townSteps<townCount; townSteps++) {
                boolean validTownSpot = false;
                while(!validTownSpot) {
                    int townX = chanceX.roll() - 1;
                    int townY = chanceY.roll() - 1;
                    if(areas[townY][townX].getSetting() == Area.Setting.WILDS ||
                            areas[townY][townX].getSetting() == Area.Setting.SAFE) {
                        areas[townY][townX] = new Area(this, Area.Setting.TOWN, townX, townY);
                        // tame land around town
                        tameAround(townX, townY);
                        validTownSpot = true;
                    }
                }
            }
            countDie = new Dice(countDie.getFaces() + 1);
            int villageCount = countDie.roll();
            for(int villageSteps=0; villageSteps<villageCount; villageSteps++) {
                boolean validVillageSpot = false;
                while(!validVillageSpot) {
                    int villageX = chanceX.roll() - 1;
                    int villageY = chanceY.roll() - 1;
                    if(areas[villageY][villageX].getSetting() == Area.Setting.WILDS ||
                            areas[villageY][villageX].getSetting() == Area.Setting.SAFE) {
                        areas[villageY][villageX] = new Area(this, Area.Setting.VILLAGE, villageX, villageY);
                        tameAround(villageX, villageY);
                        validVillageSpot = true;
                    }
                }
            }
            countDie = new Dice(countDie.getFaces() + 1);
            int hamletCount = countDie.roll();
            for(int hamletSteps=0; hamletSteps<hamletCount; hamletSteps++) {
                boolean validHamletSpot = false;
                while(!validHamletSpot) {
                    int hamletX = chanceX.roll() - 1;
                    int hamletY = chanceY.roll() - 1;
                    if(areas[hamletY][hamletX].getSetting() == Area.Setting.WILDS ||
                            areas[hamletY][hamletX].getSetting() == Area.Setting.SAFE) {
                        areas[hamletY][hamletX] = new Area(this, Area.Setting.HAMLET, hamletX, hamletY);
                        validHamletSpot = true;
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
        for(int townSteps=0; townSteps<townCount; townSteps++) {
            boolean validTownSpot = false;
            while(!validTownSpot) {
                int townX = chanceX.roll() - 1;
                int townY = chanceY.roll() - 1;
                if(areas[townY][townX].getSetting() == Area.Setting.WILDS ||
                        areas[townY][townX].getSetting() == Area.Setting.SAFE) {
                    areas[townY][townX] = new Area(this, Area.Setting.TOWN, townX, townY);
                    tameAround(townX, townY);
                    validTownSpot = true;
                }
            }
        }
        // populate with 1-2 villages
        countDie = new Dice(countDie.getFaces() + 1);
        int villageCount = countDie.roll();
        for(int villageSteps=0; villageSteps<villageCount; villageSteps++) {
            boolean validVillageSpot = false;
            while(!validVillageSpot) {
                int villageX = chanceX.roll() - 1;
                int villageY = chanceY.roll() - 1;
                if(areas[villageY][villageX].getSetting() == Area.Setting.WILDS ||
                        areas[villageY][villageX].getSetting() == Area.Setting.SAFE) {
                    areas[villageY][villageX] = new Area(this, Area.Setting.VILLAGE, villageX, villageY);
                    tameAround(villageX, villageY);
                    validVillageSpot = true;
                }
            }
        }
        //populate with 1-2 hamlets
        countDie = new Dice(countDie.getFaces() + 1);
        int hamletCount = countDie.roll();
        for(int hamletSteps=0; hamletSteps<hamletCount; hamletSteps++) {
            boolean validHamletSpot = false;
            while(!validHamletSpot) {
                int hamletX = chanceX.roll() - 1;
                int hamletY = chanceY.roll() - 1;
                if(areas[hamletY][hamletX].getSetting() == Area.Setting.WILDS ||
                        areas[hamletY][hamletX].getSetting() == Area.Setting.SAFE) {
                    areas[hamletY][hamletX] = new Area(this, Area.Setting.HAMLET, hamletX, hamletY);
                    validHamletSpot = true;
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
        for(int villageSteps=0; villageSteps<villageCount; villageSteps++) {
            boolean validVillageSpot = false;
            while(!validVillageSpot) {
                int villageX = chanceX.roll() - 1;
                int villageY = chanceY.roll() - 1;
                if(areas[villageY][villageX].getSetting() == Area.Setting.WILDS ||
                        areas[villageY][villageX].getSetting() == Area.Setting.SAFE) {
                    areas[villageY][villageX] = new Area(this, Area.Setting.VILLAGE, villageX, villageY);
                    tameAround(villageX, villageY);
                    validVillageSpot = true;
                }
            }
        }
        //populate with 0-2 hamlets
        countDie = new Dice(countDie.getFaces() + 1);
        int hamletCount = countDie.roll() - 1;
        for(int hamletSteps=0; hamletSteps<hamletCount; hamletSteps++) {
            boolean validHamletSpot = false;
            while(!validHamletSpot) {
                int hamletX = chanceX.roll() - 1;
                int hamletY = chanceY.roll() - 1;
                if(areas[hamletY][hamletX].getSetting() == Area.Setting.WILDS ||
                        areas[hamletY][hamletX].getSetting() == Area.Setting.SAFE) {
                    areas[hamletY][hamletX] = new Area(this, Area.Setting.HAMLET, hamletX, hamletY);
                    validHamletSpot = true;
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
        for(int dungeonSteps=0; dungeonSteps<dungeonCount; dungeonSteps++) {
            boolean validDungeonSpot = false;
            while(!validDungeonSpot) {
                int dungeonX = chanceX.roll() - 1;
                int dungeonY = chanceY.roll() - 1;
                if(areas[dungeonY][dungeonX].getSetting() == Area.Setting.WILDS) {
                    areas[dungeonY][dungeonX] = new Area(this, Area.Setting.DUNGEON, dungeonX, dungeonY);
                    validDungeonSpot = true;
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
                        areas[tameY][tameX] = new Area(this, Area.Setting.SAFE, tameX, tameY);
                    }
                }
            }
        }
    }

    // Logical Getters

    /**
     * Will return a String representation of the Areas contained in this Region
     * @param x int representing the x coordinate of the Area to highlight in the map
     * @param y int representing the y coordinate of the Area to highlight in the map
     * @return String representing the map of Areas contained in this Region
     */
    public String getAreaMap(int x, int y) {
        String highlightedName = getArea(x, y).getName();
        StringBuilder result = new StringBuilder(
                String.format(
                        "Region of %s %s map\nHighlighted: %s\n\n",
                        getName(),
                        biome.name().toLowerCase(Locale.ROOT),
                        highlightedName
                )
        );
        int[] settingCount = new int[Area.Setting.values().length];
        for(Area[] areaLayer: areas) {
            result.append("  ");
            for(Area area: areaLayer) {
                if(area.getX() == x && area.getY() == y) {
                    result.append("[").append(area.getSetting().name(), 0, 1).append("]");
                } else {
                    result.append(" ").append(area.getSetting().name(), 0, 1).append(" ");
                }
                settingCount[area.getSetting().ordinal()]++;
            }
            result.append("\n");
        }
        result.append("\n");
        for(Area.Setting setting: Area.Setting.values()) {
            result.append(
                    String.format(
                            "\n%-10s: %d",
                            setting.name().toLowerCase(Locale.ROOT),
                            settingCount[setting.ordinal()]
                    )
            );
        }
        return result.toString();
    }

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
