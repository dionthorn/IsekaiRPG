package org.dionthorn.isekairpg.worlds;

import javafx.scene.image.Image;
import org.dionthorn.isekairpg.characters.AbstractCharacter;
import org.dionthorn.isekairpg.graphics.TileSet;

import java.util.ArrayList;

/**
 * The Place class has a Type which determines how the Place is represented and if NPCs will be spawned in there
 */
public class Place extends AbstractLocation {

    private Type type;
    private final ArrayList<AbstractCharacter> nearbyCharacters = new ArrayList<>();

    /**
     * FARM - a farm has a farmer NPC
     * FISHERY - a fishing area has a fisherman NPC
     * RESERVE - a hunting area has a hunter NPC
     * WOODLAND - a forestry area has a lumberjack NPC
     * MINE - a mining area has a miner NPC
     * INN - an owned inn
     * TRADER - a shop that can buy and sell goods has a merchant NPC
     * BLACKSMITH - a forge has a blacksmith NPC
     * GRAVEYARD - has a crypt-keeper who will collect bodies in the area the graveyard is in
     * CAVE - Mage or Bandits
     * INDOORS - a generic indoor place could be a castle room or dungeon room
     * OUTDOORS - a generic outside place if WILD has hostile NPC encounters if SAFE has no hostile NPC
     * THRONEROOM - a special room in a castle that holds the King of the Nation
     */
    // You are at [placeName] a [Place.Type] in [areaName] a [Area.Setting] of [regionName] a [Region.Biome] region
    public enum Type {
        FARM, FISHERY, RESERVE, WOODLAND, MINE, // resources
        INN, TRADER, BLACKSMITH, GRAVEYARD, // services
        CAVE, INDOORS, OUTDOORS, THRONEROOM // special
    }

    public static final Image[] TYPE_TILES_PLAINS = new TileSet("Types_Plains.png").getTiles();
    public static final Image[] TYPE_TILES_HILLS = new TileSet("Types_Hills.png").getTiles();
    public static final Image[] TYPE_TILES_MOUNTAINS = new TileSet("Types_Mountains.png").getTiles();
    public static final Image[] TYPE_TILES_FORESTS = new TileSet("Types_Forests.png").getTiles();
    public static final Image[] TYPE_TILES_DESERTS = new TileSet("Types_Deserts.png").getTiles();
    public static final Image[] TYPE_TILES_TUNDRAS = new TileSet("Types_Tundras.png").getTiles();

    /**
     * A Place is a part of an Area and has a Type which determines how the Place is handled
     * @param parent Area representing this Place parent Area
     * @param type Type representing this Place type
     * @param x int representing this Place x coordinate
     * @param y int representing this Place y coordinate
     */
    public Place(Area parent, Type type, int x, int y) {
        super(x, y);
        setParent(parent);
        this.type = type;
    }

    /**
     * Provides this Place associated Type
     * @return Type representing this Place type
     */
    public Type getType() { return type; }

    /**
     * used to set the KINGSROOM, can also be used to convert outdoors into another type
     * example: player builds a house
     * @param newType Place.Type representing the new type to set this Place
     */
    public void setType(Place.Type newType) { this.type = newType; }

    public ArrayList<AbstractCharacter> getNearbyCharacters() { return nearbyCharacters; }

}
