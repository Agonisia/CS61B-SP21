package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    private static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int dx, int dy) {
            return new Position(this.x + dx, this.y + dy);
        }
    }

    public static Position getBottomNeighbor(Position pos, int n) {
        return pos.shift(0, -(2 * n));
    }

    public static Position getTopRightNeighbor(Position pos, int n) {
        return pos.shift(2 * n - 1, n);
    }

    public static Position getBottomRightNeighbor(Position pos, int n) {
        return pos.shift(2 * n - 1, -n);
    }

    public static void drawRow(TETile[][] tiles, Position pos, TETile tile, int length) {
        for (int dx = 0; dx < length; dx++) {
            tiles[pos.x + dx][pos.y] = tile;
        }
    }

    public static void addHexagon(TETile[][] tiles, Position pos, TETile tile, int size) {
        if (size < 2) {
            return;
        }
        addHexagonHelper(tiles, pos, tile, size - 1, size);
    }

    public static void addHexagonHelper(TETile[][] tiles, Position pos, TETile tile, int b,
                                        int t) {
        Position startOfRow = pos.shift(b, 0);
        drawRow(tiles, startOfRow, tile, t);

        if (b > 0) {
            Position nextP = pos.shift(0, -1);
            addHexagonHelper(tiles, nextP, tile, b - 1, t + 2);
        }

        Position startOfReflectedRow = startOfRow.shift(0, -(2 * b + 1));
        drawRow(tiles, startOfReflectedRow, tile, t);
    }

    public static void addHexColum(TETile[][] tiles, Position pos, int size, int num) {
        if (num < 1) {
            return;
        }

        addHexagon(tiles, pos, randomTile(), size);

        if (num > 1) {
            Position bottomNeighbor = getBottomNeighbor(pos, size);
            addHexColum(tiles, bottomNeighbor, size, num - 1);
        }
    }


    /**
     * Fills the given 2D array of tiles with Nothing!
     * @param tiles
     */
    public static void fillBoardWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /** Picks a RANDOM tile with a 20% change of being
     *  a grass, 20% chance of being a flower, 20%
     *  chance of being a tree, 20% chance of being sand,
     *  and finally 20% chance being a mountain
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.GRASS;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.TREE;
            case 3: return Tileset.SAND;
            case 4: return Tileset.MOUNTAIN;
            default: return Tileset.NOTHING;
        }
    }

    public static void drawWorld(TETile[][] tiles, Position pos, int hexSize, int tessSize) {
        addHexColum(tiles, pos, hexSize, tessSize);

        for (int i = 1; i < tessSize; i++) {
            pos = getTopRightNeighbor(pos, hexSize);
            addHexColum(tiles, pos, hexSize, tessSize + i);
        }

        for (int i = tessSize - 2; i >= 0; i--) {
            pos = getBottomRightNeighbor(pos, hexSize);
            addHexColum(tiles, pos, hexSize, tessSize + i);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillBoardWithNothing(world);
        Position anchor = new Position(10, 35);
        drawWorld(world, anchor, 3, 4);
        ter.renderFrame(world);
    }

}
