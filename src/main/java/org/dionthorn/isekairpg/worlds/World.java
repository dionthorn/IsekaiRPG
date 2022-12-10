package org.dionthorn.isekairpg.worlds;

import org.dionthorn.isekairpg.utilities.Dice;
import java.util.Locale;

/**
 * The World class holds all geographic information about the game world, Regions, Areas, Places.
 */
public class World extends AbstractLocation {

    private static World.Size worldSize;

    private final Region[][] regions;
    private final int regionSize;

    /**
     * How large or small to make the world at generation time
     */
    public enum Size {
        VERY_SMALL,
        SMALL,
        MEDIUM,
        LARGE
    }

    /**
     * The World has Regions, The World determines the Region types,
     * how many cities, characters, resources etc. should be determined at world creation.
     * <p>
     * Regions are huge and may have many nations or none in them
     */
    public World(Size size) {
        super(0, 0); // World x,y is 0,0
        worldSize = size;
        regionSize = (int) Math.pow(2, worldSize.ordinal() + 1);
        regions = new Region[regionSize][regionSize];
    }

    /**
     * Will populate the World with Regions
     */
    public void populate() {
        int regionSquare = (int) Math.pow(2, worldSize.ordinal() + 1);     // V. Small is 2^1, Small is 2^2 ...
        int areaSquare   = (int) Math.pow(2, worldSize.ordinal() + 1) + 2; // V. Small is (2^1)+2 ...
        for(int y=0; y<regionSquare; y++) {
            for(int x=0; x<regionSquare; x++) {
                int roll = Dice.d20.roll();
                if(roll == 20) {
                    // 20(1) 5%
                    regions[y][x] = new Region(Region.Biome.TUNDRA, x, y, areaSquare);
                } else if(roll == 19) {
                    // 19(1) 5%
                    regions[y][x] = new Region(Region.Biome.DESERT, x, y, areaSquare);
                } else if(roll > 16) {
                    // 17-18(2) 10%
                    regions[y][x] = new Region(Region.Biome.MOUNTAIN, x, y, areaSquare);
                } else if(roll > 10) {
                    // 11-16(6) 30%
                    regions[y][x] = new Region(Region.Biome.PLAINS, x, y, areaSquare);
                } else if(roll > 5) {
                    // 6-10(5) 25%
                    regions[y][x] = new Region(Region.Biome.FOREST, x, y, areaSquare);
                } else {
                    // 1-5(5) 25%
                    regions[y][x] = new Region(Region.Biome.HILLS, x, y, areaSquare);
                }
            }
        }
    }

    // Logical Getters

    /**
     * Will provide the Region at coordinate (x, y)
     * @param x int representing the target x coordinate
     * @param y int representing the target y coordinate
     * @return Region representing the Region at coordinate (x, y)
     * will return null if Region doesn't exist at x,y
     */
    public Region getRegion(int x, int y) {
        Region toGet = null;
        try {
            toGet = regions[y][x];
        } catch(Exception e) {
            // if x,y doesn't exist in this area will return null
        }
        return toGet;
    }

    /**
     * Will provide a String representation of all the Regions in the World and highlight the provided (x, y) Region
     * @param x int representing the target x coordinate Region to highlight
     * @param y int representing the target y coordinate Region to highlight
     * @return String representing a map of all Regions in this World while highlighting the provided (x, y) Region
     */
    public String getRegionMap(int x, int y) {
        String highlightedName = getRegion(x, y).getName();
        StringBuilder result = new StringBuilder(
                String.format("World of %s Region Map\nHighlighted Region: %s\n\n", getName(), highlightedName)
        );
        for(Region[] regionLayer: regions) {
            result.append("  ");
            for(Region region: regionLayer) {
                if(region.getX() == x && region.getY() == y) {
                    result.append("[").append(region.getBiome().name().charAt(0)).append("]");
                } else {
                    result.append(" ").append(region.getBiome().name().charAt(0)).append(" ");
                }
            }
            result.append("\n");
        }
        return result.toString();
    }

    public String getRegionCount() {
        StringBuilder result = new StringBuilder();
        int[] biomeCount = new int[Region.Biome.values().length];
        for(Region[] regionLayer: regions) {
            for(Region region: regionLayer) {
                biomeCount[region.getBiome().ordinal()]++;
            }
        }
        for(Region.Biome biome: Region.Biome.values()) {
            result.append(String.format("\n: %d", biomeCount[biome.ordinal()]));
        }
        return result.toString();
    }

    // Pure Getters

    /**
     * Will provide the region size which when squared is the count of all regions
     * @return int representing the length of one side of the square of all regions
     */
    public int getRegionSize() { return regionSize; }

    /**
     * Will provide the Region[][] of all Regions in this World
     * @return Region[][] representing all Regions in this World
     */
    public Region[][] getRegions() { return regions; }

    /**
     * Will provide the World current Size
     * @return Size representing the size of the World
     */
    public Size getWorldSize() { return worldSize; }

}
