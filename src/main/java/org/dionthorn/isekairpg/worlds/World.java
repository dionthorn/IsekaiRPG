package org.dionthorn.isekairpg.worlds;

import org.dionthorn.isekairpg.utility.Dice;

import java.util.Locale;

public class World extends AbstractLocation {

    public enum Size {
        SMALL,
        MEDIUM,
        LARGE
    }

    private final Region[][] regions;
    private final int regionSize;

    /**
     * The World has Regions, The World determines the Region types,
     * how many cities, characters, resources etc. should be determined at world creation.
     *
     * Regions are huge and may have many nations or none in them
     */
    public World(Size size) {
        super(0, 0); // World x,y is 0,0
        // World has no parent, it is a container of all the Regions
        int regionSquare = (int) Math.pow(2, size.ordinal() + 2); // Small is 2^(0+2), Medium is 2^1+2 ...
        regionSize = regionSquare;
        int areaSquare   = (int) Math.pow(2, size.ordinal() + 2) + 2; // Small is (2^(0+2))+2 ...
        regions = new Region[regionSquare][regionSquare];
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

    public int getRegionSize() { return regionSize; }

    public Region[][] getRegions() { return regions; }

    public Region getRegion(int x, int y) { return regions[y][x]; }

    public String getRegionMap(int x, int y) {
        String highlightedName = getRegion(x, y).getName();
        StringBuilder result = new StringBuilder(
                String.format("World of %s Region Map\nHighlighted Region: %s\n\n", getName(), highlightedName)
        );
        int[] biomeCount = new int[Region.Biome.values().length];
        for(Region[] regionLayer: regions) {
            result.append("  ");
            for(Region region: regionLayer) {
                if(region.getX() == x && region.getY() == y) {
                    result.append("[").append(region.getBiome().name().charAt(0)).append("]");
                } else {
                    result.append(" ").append(region.getBiome().name().charAt(0)).append(" ");
                }
                biomeCount[region.getBiome().ordinal()]++;
            }
            result.append("\n");
        }
        result.append("\n");
        for(Region.Biome biome: Region.Biome.values()) {
            result.append(String.format("\n%-10s: %d", biome.name().toLowerCase(Locale.ROOT), biomeCount[biome.ordinal()]));
        }

        return result.toString();
    }

}
