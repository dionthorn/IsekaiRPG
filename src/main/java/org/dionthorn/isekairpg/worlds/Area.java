package org.dionthorn.isekairpg.worlds;

import org.dionthorn.isekairpg.utility.Dice;

import java.util.Locale;

public class Area extends AbstractLocation {

    private final Place[][] places;
    private final Setting setting;
    private final int placeSize;

    /**
     * WILD is dangerous places but no communities(humanoid npc groups)
     * TAMED is not dangerous places but no communities
     * DUNGEON, HAMLET, VILLAGE, TOWN, CASTLE are 'communities'
     */
    public enum Setting {
        WILD,
        TAMED,
        DUNGEON,
        HAMLET,
        VILLAGE,
        TOWN,
        CASTLE;
    }

    /**
     * Areas have Places. The Area determines the Place types
     */
    public Area(Region parent, Setting setting, int x, int y) {
        super(x, y);
        setParent(parent);
        this.setting = setting;
        Region.Biome biome = ((Region) getParent()).getBiome();
        this.placeSize = 10; // hardcoded 10 for easier map movement and decent cell size of 100 places per Area
        places = new Place[placeSize][placeSize];

        for(int yPlace=0; yPlace<placeSize; yPlace++) {
            for(int xPlace=0; xPlace<placeSize; xPlace++) {
                int roll = Dice.d20.roll(); // chance roll
                if(setting == Setting.WILD || setting == Setting.TAMED) {
                    places[yPlace][xPlace] = new Place(this, Place.Type.OUTDOORS, xPlace, yPlace);
                } else if(setting == Setting.DUNGEON) {
                    // dungeons are special cases they get rooms and caves filled with monsters
                    // and potentially rare resources
                    if(roll > 10) {
                        // 50%
                        places[yPlace][xPlace] = new Place(this, Place.Type.CAVE, xPlace, yPlace);
                    } else {
                        // 50%
                        places[yPlace][xPlace] = new Place(this, Place.Type.ROOM, xPlace, yPlace);
                    }
                } else if(setting == Setting.HAMLET || setting == Setting.VILLAGE || setting == Setting.TOWN) {
                    if(biome == Region.Biome.PLAINS || biome == Region.Biome.HILLS) {
                        spawnPlainsOrHills(roll, xPlace, yPlace);
                    } else if(biome == Region.Biome.MOUNTAIN) {
                        spawnMountains(roll, xPlace, yPlace);
                    } else if(biome == Region.Biome.FOREST) {
                        spawnForests(roll, xPlace, yPlace);
                    } else if(biome == Region.Biome.DESERT) {
                        spawnDeserts(roll, xPlace, yPlace);
                    } else if(biome == Region.Biome.TUNDRA) {
                        spawnTundras(roll, xPlace, yPlace);
                    }
                } else if(setting == Setting.CASTLE) {
                    // castles are special cases, filled with rooms no services just politic and monarchies
                    // a castle is the head of a Nation AbstractGroup areas with multiple castles tend to be tense
                    places[yPlace][xPlace] = new Place(this, Place.Type.ROOM, xPlace, yPlace);
                }
            }
        }
    }

    private void spawnPlainsOrHills(int roll, int xPlace, int yPlace) {
        // plains and hills have all types of places
        if(roll > 16) {
            // 20%
            int typeRoll = Dice.d8.roll();
            if(typeRoll == 1) {
                places[yPlace][xPlace] = new Place(this, Place.Type.HUNTING, xPlace, yPlace);
            } else if(typeRoll == 2) {
                places[yPlace][xPlace] = new Place(this, Place.Type.FISHING, xPlace, yPlace);
            } else if(typeRoll == 3) {
                places[yPlace][xPlace] = new Place(this, Place.Type.FARMSTEAD, xPlace, yPlace);
            } else if(typeRoll == 4) {
                places[yPlace][xPlace] = new Place(this, Place.Type.TRADER, xPlace, yPlace);
            } else if(typeRoll == 5) {
                places[yPlace][xPlace] = new Place(this, Place.Type.MINE, xPlace, yPlace);
            } else if(typeRoll == 6) {
                places[yPlace][xPlace] = new Place(this, Place.Type.BLACKSMITH, xPlace, yPlace);
            } else if(typeRoll == 7) {
                places[yPlace][xPlace] = new Place(this, Place.Type.FORESTRY, xPlace, yPlace);
            } else if(typeRoll == 8) {
                places[yPlace][xPlace] = new Place(this, Place.Type.HOME, xPlace, yPlace);
            }
        } else {
            // 80%
            places[yPlace][xPlace] = new Place(this, Place.Type.OUTDOORS, xPlace, yPlace);
        }
    }

    private void spawnMountains(int roll, int xPlace, int yPlace) {
        // mountains lack farms and forestry
        if(roll > 16) {
            // 20%
            int typeRoll = Dice.d6.roll();
            if(typeRoll == 1) {
                places[yPlace][xPlace] = new Place(this, Place.Type.HUNTING, xPlace, yPlace);
            } else if(typeRoll == 2) {
                places[yPlace][xPlace] = new Place(this, Place.Type.FISHING, xPlace, yPlace);
            } else if(typeRoll == 3) {
                places[yPlace][xPlace] = new Place(this, Place.Type.TRADER, xPlace, yPlace);
            } else if(typeRoll == 4) {
                places[yPlace][xPlace] = new Place(this, Place.Type.MINE, xPlace, yPlace);
            } else if(typeRoll == 5) {
                places[yPlace][xPlace] = new Place(this, Place.Type.BLACKSMITH, xPlace, yPlace);
            } else if(typeRoll == 6) {
                places[yPlace][xPlace] = new Place(this, Place.Type.HOME, xPlace, yPlace);
            }
        } else {
            // 80%
            places[yPlace][xPlace] = new Place(this, Place.Type.OUTDOORS, xPlace, yPlace);
        }
    }

    private void spawnForests(int roll, int xPlace, int yPlace) {
        // forests lack blacksmiths and mines
        if(roll > 16) {
            // 20%
            int typeRoll = Dice.d6.roll();
            if(typeRoll == 1) {
                places[yPlace][xPlace] = new Place(this, Place.Type.HUNTING, xPlace, yPlace);
            } else if(typeRoll == 2) {
                places[yPlace][xPlace] = new Place(this, Place.Type.FISHING, xPlace, yPlace);
            } else if(typeRoll == 3) {
                places[yPlace][xPlace] = new Place(this, Place.Type.FARMSTEAD, xPlace, yPlace);
            } else if(typeRoll == 4) {
                places[yPlace][xPlace] = new Place(this, Place.Type.TRADER, xPlace, yPlace);
            } else if(typeRoll == 5) {
                places[yPlace][xPlace] = new Place(this, Place.Type.HOME, xPlace, yPlace);
            } else if(typeRoll == 6) {
                places[yPlace][xPlace] = new Place(this, Place.Type.FORESTRY, xPlace, yPlace);
            }
        } else {
            // 80%
            places[yPlace][xPlace] = new Place(this, Place.Type.OUTDOORS, xPlace, yPlace);
        }
    }

    private void spawnDeserts(int roll, int xPlace, int yPlace) {
        // deserts only have hunting
        if(roll == 20 || roll == 19) {
            // 10%
            places[yPlace][xPlace] = new Place(this, Place.Type.HUNTING, xPlace, yPlace);
        } else if(roll == 18) {
            // 5%
            places[yPlace][xPlace] = new Place(this, Place.Type.HOME, xPlace, yPlace);
        } else {
            // 75%
            places[yPlace][xPlace] = new Place(this, Place.Type.OUTDOORS, xPlace, yPlace);
        }
    }

    private void spawnTundras(int roll, int xPlace, int yPlace) {
        // tundras only have fishing
        if(roll == 20 || roll == 19) {
            // 10%
            places[yPlace][xPlace] = new Place(this, Place.Type.FISHING, xPlace, yPlace);
        } else if(roll == 18) {
            // 5%
            places[yPlace][xPlace] = new Place(this, Place.Type.HOME, xPlace, yPlace);
        } else {
            // 80%
            places[yPlace][xPlace] = new Place(this, Place.Type.OUTDOORS, xPlace, yPlace);
        }
    }

    /**
     * returns an int of the size of the places Place[][] sides
     * all location [][] are square so size is the length of the square
     * @return int representing the length of the square of the Place[][] of this Area
     */
    public int getPlaceSize() { return placeSize; }

    /**
     * returns the Place[][] of all places in this Area
     * @return Place[][] representing all places in this Area
     */
    public Place[][] getPlaces() { return places; }

    /**
     * returns the Place object at the x,y coordinate in this Area
     * @param x the target Place x coordinate
     * @param y the target Place y coordinate
     * @return Place object representing the Place at the target x,y in this Area
     */
    public Place getPlace(int x, int y) { return places[y][x]; }

    /**
     * returns the Area.Setting instance variable setting for this Area
     * @return Setting object representing this Area objects Area.Setting instance variable setting
     */
    public Setting getSetting() { return setting; }

    /**
     * returns a formatted String for the map of places in the area where x,y is highlighted with []
     * @param x x place coordinate to highlight
     * @param y y place coordinate to highlight
     * @return String formatted as the map of places in the area
     */
    public String getPlaceMap(int x, int y) {
        // we first add the title of the map
        StringBuilder result = new StringBuilder(
                String.format(
                        "%s of %s %s area map\n\n",
                        ((Region) getParent()).getBiome().name().toLowerCase(Locale.ROOT),
                        getName(),
                        setting.name().toLowerCase(Locale.ROOT)
                )
        );
        // we draw the 'map' and highlight the x,y provided
        // as well we count the areas place types
        int[] typeCount = new int[Place.Type.values().length];
        for(Place[] placeLayer: places) {
            result.append("  ");
            for(Place place: placeLayer) {
                if(place.getX() == x && place.getY() == y) {
                    result.append("[").append(place.getType().name(), 0, 2).append("]");
                } else {
                    result.append(" ").append(place.getType().name(), 0, 2).append(" ");
                }
                typeCount[place.getType().ordinal()]++;
            }
            result.append("\n");
        }
        // display the count of each place type in this area
        for(Place.Type type: Place.Type.values()) {
            result.append(
                    String.format(
                            "\n%-12s: %d", type.name().toLowerCase(Locale.ROOT),
                            typeCount[type.ordinal()]
                    )
            );
        }
        // return the formatted result String
        return result.toString();
    }

}
