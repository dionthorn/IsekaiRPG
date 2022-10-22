package org.dionthorn.isekairpg.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * In future update this will be used to draw maps with actual graphics
 */
public class TileSet {

    private static final int TILE_SIZE = 16; // 16*16 tiles
    private static final ArrayList<TileSet> tileSetCache = new ArrayList<>();
    private final String tileSetPath; // path to tile set original
    private final Image tileSetSrc; // tile set original image
    private Image[] tiles;
    private Image blank;
    private int totalTiles;
    private final ArrayList<Integer> removedTileIDList = new ArrayList<>();

    public TileSet(String path) {
        boolean sameFound = false;
        int sameIndex = 0;
        for(int i=0; i<tileSetCache.size(); i++) {
            if(tileSetCache.get(i).getTileSetPath().equals(path)) {
                sameFound = true;
                sameIndex = i;
                break;
            }
        }
        tileSetPath = path;
        if(sameFound) {
            tileSetSrc = tileSetCache.get(sameIndex).getTileSetSrc();
            tiles = tileSetCache.get(sameIndex).getTiles();
            blank = tileSetCache.get(sameIndex).getBlank();
            Integer[] boxedArray = Arrays.stream(
                    tileSetCache.get(sameIndex).getRemovedTileID()
            ).boxed().toArray(Integer[]::new);
            Collections.addAll(removedTileIDList, boxedArray);
            totalTiles = tileSetCache.get(sameIndex).getTotalTiles();
        } else {
            tileSetSrc = new Image("/Art/" + tileSetPath);
            tiles = makeTiles(tileSetSrc);
            if (tiles.length == 0) {
                System.err.println("NO TILES DETECTED");
            } else {
                blank = tiles[tiles.length - 1];
                for (int step = 0; step < tiles.length; step++) {
                    if (areImagesSame(tiles[step], blank)) {
                        removedTileIDList.add(step);
                    }
                }
                tiles = removeSameElements(tiles, blank);
                totalTiles = tiles.length;
            }
            TileSet.tileSetCache.add(this);
            tileSetCache.trimToSize();
        }
    }

    // logical getters

    public int[] getRemovedTileID() {
        int[] toReturn = new int[removedTileIDList.size()];
        int step = 0;
        for(Integer i: removedTileIDList) {
            int ID = i;
            toReturn[step] = ID;
            step++;
        }
        return toReturn;
    }

    public Image getTile(int tileID) { return tiles[tileID]; }

    // pure getters

    public String getTileSetPath() { return tileSetPath; }

    public Image getTileSetSrc() { return tileSetSrc; }

    public Image[] getTiles() { return tiles; }

    public int getTotalTiles() { return totalTiles; }

    public Image getBlank() { return blank; }

    // static methods

    private static Image[] makeTiles(Image tileSet) {
        int srcW = (int) tileSet.getWidth();
        int srcH = (int) tileSet.getHeight();
        int maxTiles = ((srcW / TILE_SIZE) * (srcH/ TILE_SIZE));
        int maxTilesWidth = (srcW / TILE_SIZE);
        Image[] toReturn = new Image[maxTiles];
        PixelReader pr = tileSet.getPixelReader();
        PixelWriter pw;
        int wCount = 0;
        int hCount = 0;
        int xOffset;
        int yOffset;
        for(int i=0; i<maxTiles; i++) {
            WritableImage wImg = new WritableImage(TILE_SIZE, TILE_SIZE);
            pw = wImg.getPixelWriter();
            if(wCount >= maxTilesWidth) {
                wCount = 0;
                hCount++;
            }
            xOffset = wCount * TILE_SIZE;
            yOffset = hCount * TILE_SIZE;
            for(int readY = 0; readY < TILE_SIZE; readY++) {
                for(int readX = 0; readX < TILE_SIZE; readX++) {
                    Color color = pr.getColor(readX + xOffset, readY + yOffset);
                    pw.setColor(readX, readY, color);
                }
            }
            toReturn[i] = wImg;
            wCount++;
        }
        return toReturn;
    }

    private static boolean areImagesSame(Image imgA, Image imgB) {
        if(imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight()) {
            for(int x = 0; x<(int) imgA.getWidth(); x++) {
                for(int y = 0; y<(int) imgA.getHeight(); y++) {
                    if(!imgA.getPixelReader().getColor(x, y).equals(imgB.getPixelReader().getColor(x, y))) return false;
                }
            }
        }
        return true;
    }

    private static Image[] removeSameElements(Image[] original, Image toRemove) {
        ArrayList<Image> toReturn = new ArrayList<>();
        for(Image i: original) {
            if (!areImagesSame(i, toRemove)) {
                toReturn.add(i);
            }
        }
        toReturn.add(toRemove);
        toReturn.trimToSize();
        Image[] newArray = new Image[toReturn.size()];
        return toReturn.toArray(newArray);
    }

}
