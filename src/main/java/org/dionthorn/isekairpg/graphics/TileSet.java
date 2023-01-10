package org.dionthorn.isekairpg.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.dionthorn.isekairpg.App;

import java.net.URI;
import java.util.ArrayList;

/**
 * In future update this will be used to draw maps with actual graphics
 */
public class TileSet {

    public static final URI GAME_ART_PATH = URI.create(String.valueOf(App.class.getResource("/Art/")));
    public static final int TILE_SIZE = 16; // 16*16 tiles
    private static final ArrayList<TileSet> tileSetCache = new ArrayList<>();
    private final String tileSetPath; // path to tile set original
    private final Image tileSetSrc; // tile set original image
    private final Image[] tiles;

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
        } else {
            tileSetSrc = new Image(GAME_ART_PATH + tileSetPath);
            tiles = makeTiles(tileSetSrc);
            if (tiles.length == 0) {
                System.err.println("NO TILES DETECTED");
            }
            TileSet.tileSetCache.add(this);
            tileSetCache.trimToSize();
        }
    }

    // pure getters

    public String getTileSetPath() { return tileSetPath; }

    public Image getTileSetSrc() { return tileSetSrc; }

    public Image[] getTiles() { return tiles; }

    // static methods

    private static Image[] makeTiles(Image tileSet) {
        int srcW = (int) tileSet.getWidth();
        int srcH = (int) tileSet.getHeight();
        int maxTiles = ((srcW / TILE_SIZE) * (srcH / TILE_SIZE));
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

}
