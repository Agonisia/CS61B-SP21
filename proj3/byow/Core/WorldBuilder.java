package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;

public class WorldBuilder {


    public static TETile[][] generateWorld(Mondial mondial) {
        TETile[][] world = generateEmptyWorld(mondial);
        ArrayList<Room> rooms = Room.generateRooms(world, mondial);
        Hallway.generateHallway(world, mondial, rooms);

        return world;

    }

    private static TETile[][] generateEmptyWorld(Mondial mondial) {
        TETile[][] world = new TETile[mondial.width][mondial.height];
        for (int x = 0; x < mondial.width; x += 1) {
            for (int y = 0; y < mondial.height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        return world;
    }

    /**
     * Generates a view of the world limited to the player's sight range.
     * @param world the complete game world
     * @param player the player object containing position
     * @param sightRange the range of the player's sight
     * @return a TETile[][] representing the player's limited view of the world
     */
    public static TETile[][] generateView(TETile[][] world, Player player, int sightRange) {
        int width = world.length;
        int height = world[0].length;
        TETile[][] view = new TETile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int distance = Math.abs(player.pos.x - x) + Math.abs(player.pos.y - y);

                if (distance <= sightRange) {
                    view[x][y] = world[x][y];
                } else if (distance <= sightRange + 1) {
                    view[x][y] = Tileset.GRADIENT1;
                } else if (distance <= sightRange + 2) {
                    view[x][y] = Tileset.GRADIENT2;
                } else if (distance <= sightRange + 3) {
                    view[x][y] = Tileset.GRADIENT3;
                } else {
                    view[x][y] = Tileset.NOTHING;
                }
            }
        }

        return view;
    }

}
