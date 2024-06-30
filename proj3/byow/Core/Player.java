package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;

public class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = 114514;
    private static final int MAX_ATTEMPT = 1000;
    final int sightRange = 10;
    Position pos;


    public Player(Position playerPosition) {
        this.pos = playerPosition;
    }


    /**
     * Generate a player at a random floor position on the game map.
     * @param world current game map
     * @param mondial object containing game settings, including the seed for randomness
     * @return Player object with generated position
     */
    public static Player generatePlayer(TETile[][] world, Mondial mondial) {
        Random rand = new Random(mondial.seed);
        Position playerPos = getRandomFloorPosition(world, rand);
        world[playerPos.x][playerPos.y] = Tileset.AVATAR;
        return new Player(playerPos);
    }

    /**
     * Helper method to find a random floor position in the world.
     * @param world current game map
     * @param rand Random object initialized with the game seed
     * @return Position object representing a random floor position
     */
    private static Position getRandomFloorPosition(TETile[][] world, Random rand) {
        int maxAttempts = MAX_ATTEMPT;
        int attempts = 0;
        while (attempts < maxAttempts) {
            int x = RandomUtils.uniform(rand, 0, Engine.WIDTH);
            int y = RandomUtils.uniform(rand, 0, Engine.HEIGHT);
            if (world[x][y] == Tileset.FLOOR) {
                return new Position(x, y);
            }
            attempts++;
        }
        throw new RuntimeException(
                "Failed to find a valid floor position for the player after " + maxAttempts + " attempts");
    }

    /**
     * Changes the position of @ according to the input command and returns the updated map
     *
     * @param world     current game map
     * @param moveInput player's command
     * @return Updated game map
     */
    public TETile[][] move(TETile[][] world, String moveInput) {
        for (char c : moveInput.toCharArray()) {
            switch (Character.toLowerCase(c)) {
                case 'w' -> tryMove(world, 0, 1);
                case 's' -> tryMove(world, 0, -1);
                case 'a' -> tryMove(world, -1, 0);
                case 'd' -> tryMove(world, 1, 0);
                default -> {
                    // Do nothing for invalid input
                }
            }
        }
        return WorldBuilder.generateView(world, this, sightRange);
    }

    private void tryMove(TETile[][] world, int dx, int dy) {
        int newX = pos.x + dx;
        int newY = pos.y + dy;
        if (isInBounds(world, newX, newY)
                && (world[newX][newY].equals(Tileset.FLOOR) || world[newX][newY].equals(Tileset.EXIT))) {
            world[pos.x][pos.y] = Tileset.FLOOR;
            world[newX][newY] = Tileset.AVATAR;
            pos = new Position(newX, newY);
        }
    }

    private boolean isInBounds(TETile[][] world, int x, int y) {
        return x >= 0 && x < world.length && y >= 0 && y < world[0].length;
    }
}
