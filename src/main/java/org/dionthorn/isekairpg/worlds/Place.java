package org.dionthorn.isekairpg.worlds;

public class Place extends AbstractLocation {

    private final Type type;

    /**
     * HOME - an owned house
     * TRADER - a shop that can buy and sell goods has a merchant NPC
     * BLACKSMITH - a forge has a blacksmith NPC
     * FARMSTEAD - a farm has a farmer NPC
     * FISHING - a fishing area has a fisherman NPC
     * HUNTING - a hunting area has a hunter NPC
     * FORESTRY - a forestry area has a lumberjack NPC
     * MINE - a mining area has a miner NPC
     * CAVE - random encounter
     * ROOM - a generic room place could be a castle room or dungeon room or inn if in a town/village
     * OUTDOORS - a generic outside place if WILD has hostile NPC if TAMED has no hostile NPC
     */
    public enum Type {
        HOME,
        TRADER,
        BLACKSMITH,
        FARMSTEAD,
        FISHING,
        HUNTING,
        FORESTRY,
        MINE,
        CAVE,
        ROOM,
        OUTDOORS
    }

    /**
     * The Player occupies a single Place at a time.
     * A Place can have many occupants other than the player or none.
     * A Place may have workable resources such as farms, woods, mines, etc.
     *
     * Areas are made of Places
     *   - An area could be a Town (made of shops Places and home places), Dungeon (filled with room Places), etc.
     *
     * Regions are made of Areas
     *   - A region is biome level macroscopic so a large forest, mountain, fertile plains, tundra, desert, region.
     *
     * The World is made of Regions
     */
    public Place(Area parent, Type type, int x, int y) {
        super(x, y);
        setParent(parent);
        this.type = type;
    }

    public Type getType() { return type; }

}
