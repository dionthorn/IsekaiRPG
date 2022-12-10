package org.dionthorn.isekairpg.worlds;

import org.dionthorn.isekairpg.utilities.Names;
import java.util.Locale;

/**
 * The abstract location class which all locations extend from provides basic location information
 */
public class AbstractLocation {

    private final String name;
    private final int x, y;
    private AbstractLocation parent = null;

    /**
     * creates a new abstract location and provides a random name for the location from Names.getPlaceName()
     * @param x int representing the locations x coordinate
     * @param y int representing the locations y coordinate
     */
    public AbstractLocation(int x, int y) {
        this.name = Names.getPlaceName();
        this.x = x;
        this.y = y;
    }

    // Logical Getters

    /**
     * Will generate the locations map key which informs user of the map tile types
     * @return String representing the locations map key
     */
    public String getKey() {
        StringBuilder result = new StringBuilder();
        if(this instanceof Area) {
            for(Place.Type type: Place.Type.values()) {
                String mapLetter = type.name().substring(0,1);
                result.append("\n  ").append(mapLetter).append(" ").append(type.name().toLowerCase(Locale.ROOT));
            }
        } else if(this instanceof Region) {
            for(Area.Setting setting: Area.Setting.values()) {
                String mapLetter = setting.name().substring(0,1);
                result.append("\n  ").append(mapLetter).append(" ").append(setting.name().toLowerCase(Locale.ROOT));
            }
        } else if(this instanceof World) {
            for(Region.Biome biome: Region.Biome.values()) {
                String mapLetter = biome.name().substring(0,1);
                result.append("\n  ").append(mapLetter).append(" ").append(biome.name().toLowerCase(Locale.ROOT));
            }
        }
        return result.toString();
    }

    // Pure Getters

    /**
     * Provides the name of this AbstractLocation
     * @return String representing this AbstractLocations name
     */
    public String getName() { return name; }

    /**
     * Provides the x coordinate of this AbstractLocation
     * @return int representing this AbstractLocations x coordinate
     */
    public int getX() { return x; }

    /**
     * Provides the y coordinate of this AbstractLocation
     * @return int representing this AbstractLocations y coordinate
     */
    public int getY() { return y; }

    /**
     * Provides this AbstractLocations associated parent, will return null if this is a Region
     * @return AbstractLocation representing this AbstractLocations associated parent, null if this is a Region
     */
    public AbstractLocation getParent() { return parent; }

    // Pure Setters

    /**
     * Will set this AbstractLocations parent AbstractLocation
     * Places have Area parents, Areas have Region parents, Regions have null parent
     * @param parent AbstractLocation representing this AbstractLocations parent
     */
    public void setParent(AbstractLocation parent) { this.parent = parent; }

}
