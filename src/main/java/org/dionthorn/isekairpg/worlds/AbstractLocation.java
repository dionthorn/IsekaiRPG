package org.dionthorn.isekairpg.worlds;

import org.dionthorn.isekairpg.utility.Names;

import java.util.Locale;

public class AbstractLocation {

    private final String name;
    private final int x, y;
    private AbstractLocation parent = null;

    public AbstractLocation(int x, int y) {
        this.name = Names.getPlaceName();
        this.x = x;
        this.y = y;
    }

    public String getName() { return name; }

    public int getX() { return x; }

    public int getY() { return y; }

    public void setParent(AbstractLocation parent) { this.parent = parent; }

    public AbstractLocation getParent() { return parent; }

    public String getKey() {
        StringBuilder result = new StringBuilder("\n    Key:");
        if(this instanceof Area) {
            for(Place.Type type: Place.Type.values()) {
                String mapLetter = type.name().substring(0,2);
                result.append("\n  ").append(mapLetter).append(" ").append(type.name().toLowerCase(Locale.ROOT));
            }
        } else if(this instanceof Region) {
            for(Area.Setting setting: Area.Setting.values()) {
                String mapLetter = setting.name().substring(0,2);
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

}
