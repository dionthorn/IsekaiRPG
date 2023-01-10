package org.dionthorn.isekairpg.worlds;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.dionthorn.isekairpg.graphics.TileSet;
import org.dionthorn.isekairpg.utilities.Dice;

import java.util.ArrayList;

/**
 * The Area class contains references to all it's associated Place objects as well as a Setting type
 * The Setting determines what kind of places are generated
 */
public class Area extends AbstractLocation {

    private final Place[][] places;
    private final Setting setting;

    /**
     * WILD is dangerous places but no communities
     * SAFE is not dangerous places but no communities
     * DUNGEON, HAMLET, VILLAGE, TOWN, CASTLE are communities
     */
    // You are at [placeName] a [Place.Type] in [areaName] a [Area.Setting] of [regionName] a [Region.Biome] region
    public enum Setting {
        WILDS(5),
        SAFEZONE(5),
        DUNGEON(4),
        HAMLET(4),
        VILLAGE(5),
        TOWN(6),
        CASTLE(4);

        private final int SIZE;

        Setting(int size) { this.SIZE = size; }

        public int getSize() { return SIZE; }
    }

    public static final Image[] SETTINGS_TILES = new TileSet("Settings.png").getTiles();

    /**
     * Areas have Places, the Setting determines Place types generated
     * @param parent Region representing the parent Region this Area is a part of for easy reference
     * @param setting Setting representing the type of Area this is
     * @param x int representing this Area x coordinate within it's parent Region
     * @param y int representing this Area y coordinate within it's parent Region
     */
    public Area(Region parent, Setting setting, int x, int y) {
        super(x, y);
        setParent(parent);
        this.setting = setting;
        Region.Biome biome = ((Region) getParent()).getBiome();
        places = new Place[setting.getSize()][setting.getSize()];

        for(int yPlace = 0; yPlace< setting.getSize(); yPlace++) {
            for(int xPlace = 0; xPlace< setting.getSize(); xPlace++) {
                int roll = Dice.d20.roll(); // chance roll
                if(setting == Setting.WILDS || setting == Setting.SAFEZONE) {
                    places[yPlace][xPlace] = new Place(this, Place.Type.OUTDOORS, xPlace, yPlace);
                } else if(setting == Setting.DUNGEON) {
                    // dungeons are special cases they get rooms and caves filled with monsters
                    // and potentially rare resources
                    if(roll > 10) {
                        // 50%
                        places[yPlace][xPlace] = new Place(this, Place.Type.CAVE, xPlace, yPlace);
                    } else {
                        // 50%
                        places[yPlace][xPlace] = new Place(this, Place.Type.INDOORS, xPlace, yPlace);
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
                    places[yPlace][xPlace] = new Place(this, Place.Type.INDOORS, xPlace, yPlace);
                }
            }
        }
        if(setting != Setting.WILDS && setting != Setting.SAFEZONE &&
                setting != Setting.CASTLE && setting != Setting.DUNGEON) {
            addGraveyard();
        }
    }

    private void addGraveyard() {
        // find a free outdoors space to turn into a graveyard every area should have 1 graveyard
        // except wild, safe, castle, dungeon
        ArrayList<Place> outdoorPlaces = new ArrayList<>();
        for(Place[] placeLayer: places) {
            for(Place place: placeLayer) {
                if(place.getType() == Place.Type.OUTDOORS) {
                    outdoorPlaces.add(place);
                }
            }
        }
        Dice randomOutdoor = new Dice(1, outdoorPlaces.size(), -1);
        outdoorPlaces.get(randomOutdoor.roll()).setType(Place.Type.GRAVEYARD);
    }

    /**
     * Generates places for Plains or Hills Setting
     * @param roll int representing the random Die roll for Place type
     * @param xPlace int representing the x coordinate of the Place to generate
     * @param yPlace int representing the y coordinate of the Place to generate
     */
    private void spawnPlainsOrHills(int roll, int xPlace, int yPlace) {
        // plains and hills have all types of places
        if(roll > 10) {
            // 50%
            int typeRoll = Dice.d8.roll();
            if(typeRoll == 1) {
                places[yPlace][xPlace] = new Place(this, Place.Type.RESERVE, xPlace, yPlace);
            } else if(typeRoll == 2) {
                places[yPlace][xPlace] = new Place(this, Place.Type.FISHERY, xPlace, yPlace);
            } else if(typeRoll == 3) {
                places[yPlace][xPlace] = new Place(this, Place.Type.FARM, xPlace, yPlace);
            } else if(typeRoll == 4) {
                places[yPlace][xPlace] = new Place(this, Place.Type.TRADER, xPlace, yPlace);
            } else if(typeRoll == 5) {
                places[yPlace][xPlace] = new Place(this, Place.Type.MINE, xPlace, yPlace);
            } else if(typeRoll == 6) {
                places[yPlace][xPlace] = new Place(this, Place.Type.BLACKSMITH, xPlace, yPlace);
            } else if(typeRoll == 7) {
                places[yPlace][xPlace] = new Place(this, Place.Type.WOODLAND, xPlace, yPlace);
            } else if(typeRoll == 8) {
                places[yPlace][xPlace] = new Place(this, Place.Type.INN, xPlace, yPlace);
            }
        } else {
            // 50%
            places[yPlace][xPlace] = new Place(this, Place.Type.OUTDOORS, xPlace, yPlace);
        }
    }

    /**
     * Generates places for a Mountain Setting
     * @param roll int representing the random Die roll for Place type
     * @param xPlace int representing the x coordinate of the Place to generate
     * @param yPlace int representing the y coordinate of the Place to generate
     */
    private void spawnMountains(int roll, int xPlace, int yPlace) {
        // mountains lack farms and forestry
        if(roll > 10) {
            // 50%
            int typeRoll = Dice.d6.roll();
            if(typeRoll == 1) {
                places[yPlace][xPlace] = new Place(this, Place.Type.RESERVE, xPlace, yPlace);
            } else if(typeRoll == 2) {
                places[yPlace][xPlace] = new Place(this, Place.Type.FISHERY, xPlace, yPlace);
            } else if(typeRoll == 3) {
                places[yPlace][xPlace] = new Place(this, Place.Type.TRADER, xPlace, yPlace);
            } else if(typeRoll == 4) {
                places[yPlace][xPlace] = new Place(this, Place.Type.MINE, xPlace, yPlace);
            } else if(typeRoll == 5) {
                places[yPlace][xPlace] = new Place(this, Place.Type.BLACKSMITH, xPlace, yPlace);
            } else if(typeRoll == 6) {
                places[yPlace][xPlace] = new Place(this, Place.Type.INN, xPlace, yPlace);
            }
        } else {
            // 50%
            places[yPlace][xPlace] = new Place(this, Place.Type.OUTDOORS, xPlace, yPlace);
        }
    }
    /**
     * Generates places for a Forest Setting
     * @param roll int representing the random Die roll for Place type
     * @param xPlace int representing the x coordinate of the Place to generate
     * @param yPlace int representing the y coordinate of the Place to generate
     */
    private void spawnForests(int roll, int xPlace, int yPlace) {
        // forests lack blacksmiths and mines
        if(roll > 10) {
            // 50%
            int typeRoll = Dice.d6.roll();
            if(typeRoll == 1) {
                places[yPlace][xPlace] = new Place(this, Place.Type.RESERVE, xPlace, yPlace);
            } else if(typeRoll == 2) {
                places[yPlace][xPlace] = new Place(this, Place.Type.FISHERY, xPlace, yPlace);
            } else if(typeRoll == 3) {
                places[yPlace][xPlace] = new Place(this, Place.Type.FARM, xPlace, yPlace);
            } else if(typeRoll == 4) {
                places[yPlace][xPlace] = new Place(this, Place.Type.TRADER, xPlace, yPlace);
            } else if(typeRoll == 5) {
                places[yPlace][xPlace] = new Place(this, Place.Type.INN, xPlace, yPlace);
            } else if(typeRoll == 6) {
                places[yPlace][xPlace] = new Place(this, Place.Type.WOODLAND, xPlace, yPlace);
            }
        } else {
            // 50%
            places[yPlace][xPlace] = new Place(this, Place.Type.OUTDOORS, xPlace, yPlace);
        }
    }

    /**
     * Generates places for a Desert Setting
     * @param roll int representing the random Die roll for Place type
     * @param xPlace int representing the x coordinate of the Place to generate
     * @param yPlace int representing the y coordinate of the Place to generate
     */
    private void spawnDeserts(int roll, int xPlace, int yPlace) {
        // deserts only have hunting
        if(roll > 16) {
            // 20% (17-20)
            places[yPlace][xPlace] = new Place(this, Place.Type.RESERVE, xPlace, yPlace);
        } else if(roll > 12) {
            // 20% (13-16)
            places[yPlace][xPlace] = new Place(this, Place.Type.INN, xPlace, yPlace);
        } else {
            // 60% (1-12)
            places[yPlace][xPlace] = new Place(this, Place.Type.OUTDOORS, xPlace, yPlace);
        }
    }

    /**
     * Generates places for a Tundra Setting
     * @param roll int representing the random Die roll for Place type
     * @param xPlace int representing the x coordinate of the Place to generate
     * @param yPlace int representing the y coordinate of the Place to generate
     */
    private void spawnTundras(int roll, int xPlace, int yPlace) {
        // tundras only have fishing
        if(roll > 16) {
            // 20% (17-20)
            places[yPlace][xPlace] = new Place(this, Place.Type.FISHERY, xPlace, yPlace);
        } else if(roll > 12) {
            // 20% (13-16)
            places[yPlace][xPlace] = new Place(this, Place.Type.INN, xPlace, yPlace);
        } else {
            // 60% (1-12)
            places[yPlace][xPlace] = new Place(this, Place.Type.OUTDOORS, xPlace, yPlace);
        }
    }

    /**
     * returns an int of the size of the places Place[][] sides
     * all location [][] are square so size is the length of the square
     * @return int representing the length of the square of the Place[][] of this Area
     */
    public int getPlaceSize() { return getSetting().getSize(); }

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
     * will return null if Place doesn't exist at x,y
     */
    public Place getPlace(int x, int y) {
        Place toGet = null;
        try {
            toGet = places[y][x];
        } catch(Exception e) {
            // if x,y doesn't exist in this area will return null
        }
        return toGet;
    }

    /**
     * returns the Area.Setting instance variable setting for this Area
     * @return Setting object representing this Area objects Area.Setting instance variable setting
     */
    public Setting getSetting() { return setting; }

    public Canvas getPlaceMapCanvas(int placeX, int placeY) {
        int placeSize = getPlaceSize();
        Canvas map = new Canvas(placeSize * TileSet.TILE_SIZE, placeSize * TileSet.TILE_SIZE);
        GraphicsContext gc = map.getGraphicsContext2D();

        for (int x = 0; x < placeSize; x++) {
            for (int y = 0; y < placeSize; y++) {
                Place place = getPlace(x, y);
                Region region = (Region) getParent();
                Region.Biome biome = region.getBiome();
                Area.Setting setting = region.getArea(getX(), getY()).getSetting();
                Place.Type type = place.getType();

                Image[] typesBiomeTiles = null;
                if(biome == Region.Biome.PLAINS) {
                    typesBiomeTiles = Place.TYPE_TILES_PLAINS;
                } else if(biome == Region.Biome.HILLS) {
                    typesBiomeTiles = Place.TYPE_TILES_HILLS;
                } else if(biome == Region.Biome.MOUNTAIN) {
                    typesBiomeTiles = Place.TYPE_TILES_MOUNTAINS;
                } else if(biome == Region.Biome.FOREST) {
                    typesBiomeTiles = Place.TYPE_TILES_FORESTS;
                } else if(biome == Region.Biome.DESERT) {
                    typesBiomeTiles = Place.TYPE_TILES_DESERTS;
                } else if(biome == Region.Biome.TUNDRA) {
                    typesBiomeTiles = Place.TYPE_TILES_TUNDRAS;
                }
                assert typesBiomeTiles != null;

                int tileID = (setting.ordinal() * Place.Type.values().length) + type.ordinal();

                gc.drawImage(
                        typesBiomeTiles[tileID],
                        TileSet.TILE_SIZE * x,
                        TileSet.TILE_SIZE * y
                );
            }
        }

        // highlight player area with red rect
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeRect(TileSet.TILE_SIZE * placeX, TileSet.TILE_SIZE * placeY, TileSet.TILE_SIZE, TileSet.TILE_SIZE);

        return map;
    }

}
