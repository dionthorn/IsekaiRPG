package org.dionthorn.isekairpg.worlds;

import org.dionthorn.isekairpg.utility.Dice;

import java.util.Locale;

public class Region extends AbstractLocation {

    private final Area[][] areas;
    private final Biome biome;
    private final int areaSize;

    /**
     * All biomes that roll 1-9 on a d20 will spawn a WILD Setting Area
     * PLAINS   - OPEN, DUNGEON, CAMP, VILLAGE, TOWN, CASTLE Setting Areas
     * HILLS    - OPEN, DUNGEON, CAMP, VILLAGE, TOWN, CASTLE
     * MOUNTAIN - OPEN, DUNGEON, CAMP
     * FOREST   - OPEN, DUNGEON, CAMP, VILLAGE, TOWN
     * DESERT   - OPEN, DUNGEON, CAMP, VILLAGE
     * TUNDRA   - OPEN, DUNGEON, CAMP, VILLAGE
     */
    public enum Biome {
        PLAINS,
        HILLS,
        MOUNTAIN,
        FOREST,
        DESERT,
        TUNDRA;
    }

    /**
     * Regions have Areas. The Region determines the Area types
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
                areas[yArea][xArea] = new Area(this, Area.Setting.WILD, xArea, yArea);
            }
        }

        // roll a die and spawn some type of civilization
        // a roll of 18-20 15% chance will spawn:
        //   1-2 nations which rule from castles, 1-2 towns, 1-2 villages, 1-2 hamlets
        //
        // a roll of 14-17 20% chance will spawn
        //   0-1 towns, 1-2 villages, 1-2 hamlets
        //
        // a roll of 05-13 45% chance will spawn
        //   0-1 villages, 0-2 hamlets
        //
        // a roll of 01-04 20% chance will spawn
        //   1-4 dungeons
        //
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

    private void populateNations(Dice chanceX, Dice chanceY) {
        int nationCount = Dice.d2.roll();
        for(int nationSteps=0; nationSteps<nationCount; nationSteps++) {
            boolean validCastleSpot = false;
            // populate a single castle this is the head of a nation
            while(!validCastleSpot) {
                int castleX = chanceX.roll() - 1;
                int castleY = chanceY.roll() - 1;
                if(areas[castleY][castleX].getSetting() == Area.Setting.WILD || areas[castleY][castleX].getSetting() == Area.Setting.TAMED) {
                    areas[castleY][castleX] = new Area(this, Area.Setting.CASTLE, castleX, castleY);
                    // tame land around castle
                    tameAround(castleX, castleY);
                    validCastleSpot = true;
                }
            }
            // populate with 1-2 towns
            int townCount = Dice.d2.roll();
            for(int townSteps=0; townSteps<townCount; townSteps++) {
                boolean validTownSpot = false;
                while(!validTownSpot) {
                    int townX = chanceX.roll() - 1;
                    int townY = chanceY.roll() - 1;
                    if(areas[townY][townX].getSetting() == Area.Setting.WILD || areas[townY][townX].getSetting() == Area.Setting.TAMED) {
                        areas[townY][townX] = new Area(this, Area.Setting.TOWN, townX, townY);
                        // tame land around town
                        tameAround(townX, townY);
                        validTownSpot = true;
                    }
                }
            }
            // populate with 1-2 villages
            int villageCount = Dice.d2.roll();
            for(int villageSteps=0; villageSteps<villageCount; villageSteps++) {
                boolean validVillageSpot = false;
                while(!validVillageSpot) {
                    int villageX = chanceX.roll() - 1;
                    int villageY = chanceY.roll() - 1;
                    if(areas[villageY][villageX].getSetting() == Area.Setting.WILD || areas[villageY][villageX].getSetting() == Area.Setting.TAMED) {
                        areas[villageY][villageX] = new Area(this, Area.Setting.VILLAGE, villageX, villageY);
                        validVillageSpot = true;
                    }
                }
            }
            //populate with 1-2 hamlets
            int hamletCount = Dice.d2.roll();
            for(int hamletSteps=0; hamletSteps<hamletCount; hamletSteps++) {
                boolean validHamletSpot = false;
                while(!validHamletSpot) {
                    int hamletX = chanceX.roll() - 1;
                    int hamletY = chanceY.roll() - 1;
                    if(areas[hamletY][hamletX].getSetting() == Area.Setting.WILD || areas[hamletY][hamletX].getSetting() == Area.Setting.TAMED) {
                        areas[hamletY][hamletX] = new Area(this, Area.Setting.VILLAGE, hamletX, hamletY);
                        validHamletSpot = true;
                    }
                }
            }
        }
    }

    private void populateHigh(Dice chanceX, Dice chanceY) {
        // populate with 0-1 towns
        int townCount = Dice.d2.roll() - 1;
        for(int townSteps=0; townSteps<townCount; townSteps++) {
            boolean validTownSpot = false;
            while(!validTownSpot) {
                int townX = chanceX.roll() - 1;
                int townY = chanceY.roll() - 1;
                if(areas[townY][townX].getSetting() == Area.Setting.WILD || areas[townY][townX].getSetting() == Area.Setting.TAMED) {
                    areas[townY][townX] = new Area(this, Area.Setting.TOWN, townX, townY);
                    tameAround(townX, townY);
                    validTownSpot = true;
                }
            }
        }
        // populate with 1-2 villages
        int villageCount = Dice.d2.roll();
        for(int villageSteps=0; villageSteps<villageCount; villageSteps++) {
            boolean validVillageSpot = false;
            while(!validVillageSpot) {
                int villageX = chanceX.roll() - 1;
                int villageY = chanceY.roll() - 1;
                if(areas[villageY][villageX].getSetting() == Area.Setting.WILD || areas[villageY][villageX].getSetting() == Area.Setting.TAMED) {
                    areas[villageY][villageX] = new Area(this, Area.Setting.VILLAGE, villageX, villageY);
                    validVillageSpot = true;
                }
            }
        }
        //populate with 1-2 hamlets
        int hamletCount = Dice.d2.roll();
        for(int hamletSteps=0; hamletSteps<hamletCount; hamletSteps++) {
            boolean validHamletSpot = false;
            while(!validHamletSpot) {
                int hamletX = chanceX.roll() - 1;
                int hamletY = chanceY.roll() - 1;
                if(areas[hamletY][hamletX].getSetting() == Area.Setting.WILD || areas[hamletY][hamletX].getSetting() == Area.Setting.TAMED) {
                    areas[hamletY][hamletX] = new Area(this, Area.Setting.VILLAGE, hamletX, hamletY);
                    validHamletSpot = true;
                }
            }
        }
    }

    private void populateLow(Dice chanceX, Dice chanceY) {
        // populate with 0-1 villages
        int villageCount = Dice.d2.roll() - 1;
        for(int villageSteps=0; villageSteps<villageCount; villageSteps++) {
            boolean validVillageSpot = false;
            while(!validVillageSpot) {
                int villageX = chanceX.roll() - 1;
                int villageY = chanceY.roll() - 1;
                if(areas[villageY][villageX].getSetting() == Area.Setting.WILD || areas[villageY][villageX].getSetting() == Area.Setting.TAMED) {
                    areas[villageY][villageX] = new Area(this, Area.Setting.VILLAGE, villageX, villageY);
                    validVillageSpot = true;
                }
            }
        }
        //populate with 0-2 hamlets
        int hamletCount = new Dice(3).roll() - 1;
        for(int hamletSteps=0; hamletSteps<hamletCount; hamletSteps++) {
            boolean validHamletSpot = false;
            while(!validHamletSpot) {
                int hamletX = chanceX.roll() - 1;
                int hamletY = chanceY.roll() - 1;
                if(areas[hamletY][hamletX].getSetting() == Area.Setting.WILD || areas[hamletY][hamletX].getSetting() == Area.Setting.TAMED) {
                    areas[hamletY][hamletX] = new Area(this, Area.Setting.VILLAGE, hamletX, hamletY);
                    validHamletSpot = true;
                }
            }
        }
    }

    private void populateDungeon(Dice chanceX, Dice chanceY) {
        // populate with 1-4 dungeons!
        int dungeonCount = Dice.d4.roll();
        for(int dungeonSteps=0; dungeonSteps<dungeonCount; dungeonSteps++) {
            boolean validDungeonSpot = false;
            while(!validDungeonSpot) {
                int dungeonX = chanceX.roll() - 1;
                int dungeonY = chanceY.roll() - 1;
                if(areas[dungeonY][dungeonX].getSetting() == Area.Setting.WILD) {
                    areas[dungeonY][dungeonX] = new Area(this, Area.Setting.DUNGEON, dungeonX, dungeonY);
                    validDungeonSpot = true;
                }
            }
        }
    }

    private void tameAround(int x, int y) {
        for(int tameX=x-1; tameX<x+2; tameX++) {
            for(int tameY=y-1; tameY<y+2; tameY++) {
                if(tameX >= 0 && tameY >= 0 && tameX < areaSize && tameY < areaSize) {
                    if(areas[tameY][tameX].getSetting() == Area.Setting.WILD || areas[y][x].getSetting() == Area.Setting.TAMED) {
                        areas[tameY][tameX] = new Area(this, Area.Setting.TAMED, tameX, tameY);
                    }
                }
            }
        }
    }

    public int getAreaSize() { return areaSize; }

    public Area[][] getAreas() { return areas; }

    public Area getArea(int x, int y) { return areas[y][x]; }

    public Biome getBiome() { return biome; }

    public String getAreaMap(int x, int y) {
        String highlightedName = getArea(x, y).getName();
        StringBuilder result = new StringBuilder(String.format("Region of %s %s map\nHighlighted: %s\n\n", getName(), biome.name().toLowerCase(Locale.ROOT), highlightedName));
        int[] settingCount = new int[Area.Setting.values().length];
        for(Area[] areaLayer: areas) {
            result.append("  ");
            for(Area area: areaLayer) {
                if(area.getX() == x && area.getY() == y) {
                    result.append("[").append(area.getSetting().name(), 0, 2).append("]");
                } else {
                    result.append(" ").append(area.getSetting().name(), 0, 2).append(" ");
                }
                settingCount[area.getSetting().ordinal()]++;
            }
            result.append("\n");
        }
        result.append("\n");
        for(Area.Setting setting: Area.Setting.values()) {
            result.append(String.format("\n%-10s: %d", setting.name().toLowerCase(Locale.ROOT), settingCount[setting.ordinal()]));
        }
        return result.toString();
    }

}
