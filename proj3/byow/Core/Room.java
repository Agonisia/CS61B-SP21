package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class Room {
    int height;
    int width;
    Position pos;

    public static final int ROOM_NUM_LIMIT = 3614;

    public static final int ROOM_WIDTH_MIN = 4;
    public static final int ROOM_HEIGHT_MIN = 4;
    public static final int ROOM_WIDTH_MAX = 10;
    public static final int ROOM_HEIGHT_MAX = 10;

    // Static Random instance for reuse
    private static Random random;

    // Static block to initialize the Random instance
    static {
        random = new Random();
    }

    Room(int roomWidth, int roomHeight, Position pos) {
        width = roomWidth;
        height = roomHeight;
        this.pos = pos;
    }

    /**
     * Returns a list including all the non-overlapped rooms generated and drawn on the map.
     */
    public static ArrayList<Room> generateRooms(TETile[][] world, Mondial mondial) {
        random.setSeed(mondial.seed);

        ArrayList<Room> rooms = new ArrayList<>();
        for (int i = 0; i < ROOM_NUM_LIMIT; i += 1) {
            int roomWidth = RandomUtils.uniform(random, ROOM_WIDTH_MIN, ROOM_WIDTH_MAX);
            int roomHeight = RandomUtils.uniform(random, ROOM_HEIGHT_MIN, ROOM_HEIGHT_MAX);
            int randPosX = RandomUtils.uniform(random, mondial.width);
            int randPosY = RandomUtils.uniform(random, mondial.height);
            Position roomPos = new Position(randPosX, randPosY);

            Room newRoom = new Room(roomWidth, roomHeight, roomPos);
            if (!newRoom.isOverlap(world, mondial)) {
                rooms.add(newRoom);
                newRoom.makeSingleRoom(world, mondial);
            }
        }

        rooms = sortRooms(rooms);
        return rooms;
    }

    /**
     * Sorts the rooms for the further connection by the hallways in ascending order.
     * Uses minimumRoom() method to find the "minimum" room in rooms.
     *
     * @param rooms the generated rooms on the map
     * @return sorted rooms
     */
    private static ArrayList<Room> sortRooms(ArrayList<Room> rooms) {
        ArrayList<Room> sortedRooms = new ArrayList<>();
        while (!rooms.isEmpty()) {
            int minRoomIndex = minimumRoom(rooms);
            sortedRooms.add(rooms.remove(minRoomIndex));
        }
        return sortedRooms;
    }

    /**
     * Returns the index of the "minimum" room in the rooms list.
     * Helper function for sortRooms() method.
     *
     * The "minimum" room is determined by the sum of its position's x and y coordinates,
     * which serves as a measure of its distance from the origin (0, 0).
     * This heuristic prioritizes rooms closer to the top-left corner.
     *
     * @param rooms the list of generated rooms on the map
     * @return the index of the "minimum" room in the rooms list
     */
    private static int minimumRoom(ArrayList<Room> rooms) {
        int minIndex = 0;
        int comparatorMin = rooms.get(0).pos.x + rooms.get(0).pos.y;

        for (int i = 1; i < rooms.size(); i += 1) {
            int comparator = rooms.get(i).pos.x + rooms.get(i).pos.y;
            if (comparator < comparatorMin) {
                comparatorMin = comparator;
                minIndex = i;
            }
        }
        return minIndex;
    }

    /** Generates a room with floor and make it surrounded by the walls. */
    public void makeSingleRoom(TETile[][] world, Mondial mondial) {
        int worldWidth = mondial.width;
        int worldHeight = mondial.height;

        // Fill the whole room with walls and replace the inner walls with floor.
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                int worldX = pos.x + x;
                int worldY = pos.y + y;
                if (worldX < worldWidth && worldY < worldHeight) {
                    if (world[worldX][worldY] != Tileset.FLOOR) {
                        world[worldX][worldY] = Tileset.WALL;
                    }
                }
            }
        }
        for (int x = 1; x < width - 1; x += 1) {
            for (int y = 1; y < height - 1; y += 1) {
                int worldX = pos.x + x;
                int worldY = pos.y + y;
                if (worldX < worldWidth && worldY < worldHeight) {
                    world[worldX][worldY] = Tileset.FLOOR;
                }
            }
        }
    }

    /** Returns false if this room and existing rooms overlap each other, and true otherwise. */
    public boolean isOverlap(TETile[][] world, Mondial mondial) {
        int roomEndX = pos.x + width;
        int roomEndY = pos.y + height;

        // Consider the new room out of the world bound as room-world overlap.
        if (roomEndX > mondial.width || roomEndY > mondial.height) {
            return true;
        }
        for (int i = pos.x; i < roomEndX; i += 1) {
            for (int j = pos.y; j < roomEndY; j += 1) {
                if (world[i][j] == Tileset.FLOOR) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a random position "in" the room (a FLOOR).
     *
     * @param mondial the mgp class includes the width, height and seed of the generated map/world.
     * @return a random position within the room.
     */
    public Position innerPosRandom(Mondial mondial) {
        int innerX = pos.x + 1 + RandomUtils.uniform(random, width - 2);  // from 1 to width - 2
        int innerY = pos.y + 1 + RandomUtils.uniform(random, height - 2); // from 1 to height - 2
        return new Position(innerX, innerY);
    }
}
