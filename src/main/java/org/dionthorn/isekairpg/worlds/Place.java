package org.dionthorn.isekairpg.worlds;

/**
 * The Place class has a Type which determines how the Place is represented and if NPCs will be spawned in there
 */
public class Place extends AbstractLocation {

    private Type type;

    /**
     * LODGING - an owned home or inn
     * TRADER - a shop that can buy and sell goods has a merchant NPC
     * BLACKSMITH - a forge has a blacksmith NPC
     * AGRICULTURE - a farm has a farmer NPC
     * FISHERY - a fishing area has a fisherman NPC
     * RESERVE - a hunting area has a hunter NPC
     * WOODLAND - a forestry area has a lumberjack NPC
     * MINE - a mining area has a miner NPC
     * CAVE - Mage or Bandits
     * INDOORS - a generic indoor place could be a castle room or dungeon room
     * OUTDOORS - a generic outside place if WILD has hostile NPC encounters if SAFE has no hostile NPC
     * KINGSROOM - a special room in a castle that holds the King of the Nation
     */
    // You are at [placeName] a [Place.Type] in [areaName] a [Area.Setting] of [regionName] a [Region.Biome] region
    public enum Type {
        LODGING, TRADER, BLACKSMITH, AGRICULTURE, FISHERY, RESERVE,
        WOODLAND, MINE, CAVE, INDOORS, OUTDOORS, KINGSROOM
    }

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

}
