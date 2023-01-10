package org.dionthorn.isekairpg.worlds;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.dionthorn.isekairpg.graphics.TileSet;
import org.dionthorn.isekairpg.utilities.Dice;

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
        VERY_SMALL,// 2^2 = 4  *4  Regions = 16   Regions
        SMALL,     // 2^3 = 8  *8  Regions = 64   Regions
        MEDIUM,    // 2^4 = 16 *16 Regions = 256  Regions
        LARGE      // 2^5 = 32 *32 Regions = 1024 Regions
        // Very_Large at 64 would take too much memory.
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
        regionSize = (int) Math.pow(2, worldSize.ordinal() + 2);
        regions = new Region[regionSize][regionSize];
    }

    /**
     * Will populate the World with Regions
     */
    public void create() {
        for (int y = 0; y < regionSize; y++) {
            for (int x = 0; x < regionSize; x++) {
                int roll = Dice.d20.roll();
                if (roll == 20) {
                    // 20(1) 5%
                    regions[y][x] = new Region(Region.Biome.TUNDRA, x, y, regionSize);
                } else if (roll == 19) {
                    // 19(1) 5%
                    regions[y][x] = new Region(Region.Biome.DESERT, x, y, regionSize);
                } else if (roll > 16) {
                    // 17-18(2) 10%
                    regions[y][x] = new Region(Region.Biome.MOUNTAIN, x, y, regionSize);
                } else if (roll > 10) {
                    // 11-16(6) 30%
                    regions[y][x] = new Region(Region.Biome.PLAINS, x, y, regionSize);
                } else if (roll > 5) {
                    // 6-10(5) 25%
                    regions[y][x] = new Region(Region.Biome.FOREST, x, y, regionSize);
                } else {
                    // 1-5(5) 25%
                    regions[y][x] = new Region(Region.Biome.HILLS, x, y, regionSize);
                }
            }
        }
    }

    // Logical Getters

    /**
     * Will provide the Region at coordinate (x, y)
     *
     * @param x int representing the target x coordinate
     * @param y int representing the target y coordinate
     * @return Region representing the Region at coordinate (x, y)
     * will return null if Region doesn't exist at x,y
     */
    public Region getRegion(int x, int y) {
        Region toGet = null;
        try {
            toGet = regions[y][x];
        } catch (Exception e) {
            // if x,y doesn't exist in this area will return null
        }
        return toGet;
    }

    public Canvas getRegionMapCanvas(int regionX, int regionY) {
        Canvas map = new Canvas(regionSize * TileSet.TILE_SIZE, regionSize * TileSet.TILE_SIZE);
        GraphicsContext gc = map.getGraphicsContext2D();
        for (int x = 0; x < regionSize; x++) {
            for (int y = 0; y < regionSize; y++) {
                Region.Biome biome = getRegions()[y][x].getBiome();
                gc.drawImage(
                        Region.BIOME_TILES[biome.ordinal()],
                        TileSet.TILE_SIZE * x,
                        TileSet.TILE_SIZE * y
                );
            }
        }
        // highlight player region with red rect
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeRect(TileSet.TILE_SIZE * regionX, TileSet.TILE_SIZE * regionY, TileSet.TILE_SIZE, TileSet.TILE_SIZE);

        return map;
    }

    // Pure Getters

    /**
     * Will provide the region size which when squared is the count of all regions
     *
     * @return int representing the length of one side of the square of all regions
     */
    public int getRegionSize() { return regionSize; }

    /**
     * Will provide the Region[][] of all Regions in this World
     *
     * @return Region[][] representing all Regions in this World
     */
    public Region[][] getRegions() { return regions; }

    /**
     * Will provide the World current Size
     *
     * @return Size representing the size of the World
     */
    public Size getWorldSize() { return worldSize; }

}
