package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serial;
import java.io.Serializable;

public class Exit implements Serializable {

    @Serial
    private static final long serialVersionUID = 114514;

    Position pos;

    Exit(Position exitPos) {
        this.pos = exitPos;
    }

    /**
     * Generate the exit from the maze based on the given game map and player coordinates
     * @param world current game map
     * @param player Generate exits based on player coordinates
     * @return Coordinates of exit
     */
    public static Exit generateExit(TETile[][] world, Player player) {
        final int xScale = 3614;
        final int yScale = 3614;

        for (int x = Math.max(0, player.pos.x - xScale) + 1;
             x < Math.min(player.pos.x + xScale, Engine.WIDTH) - 1; x++) {
            for (int y = Math.max(0, player.pos.y - yScale) + 1;
                 y < Math.min(player.pos.y + yScale, Engine.HEIGHT) - 1; y++) {
                Position doorPos = new Position(x, y);
                if (isExitPosValid(world, doorPos)) {
                    world[doorPos.x][doorPos.y] = Tileset.EXIT;
                    return new Exit(doorPos);
                }
            }
        }
        // Return a default invalid position if no valid exit is found
        return null;
    }


    /**
     * A helper function evaluating if the generated export is legal or not
     * A legitimate exit satisfies the following conditions
     * 1. it is adjacent to a floor
     * 2. it is adjacent to two walls and one nothing, or three walls.
     * @param world current game map
     * @param exitPos generated exit position
     * @return true of false
     */
    private static boolean isExitPosValid(TETile[][] world, Position exitPos) {
        if (world[exitPos.x][exitPos.y] != Tileset.WALL) {
            return false;
        }
        int numWalls = 0;
        int numNothing = 0;
        int numFloor = 0;
        Position[] neighbors = {
            new Position(exitPos.x - 1, exitPos.y),
            new Position(exitPos.x + 1, exitPos.y),
            new Position(exitPos.x, exitPos.y + 1),
            new Position(exitPos.x, exitPos.y - 1)
        };
        for (Position neighbor : neighbors) {
            if (world[neighbor.x][neighbor.y] == Tileset.WALL) {
                numWalls++;
            } else if (world[neighbor.x][neighbor.y] == Tileset.FLOOR) {
                numFloor++;
            } else if (world[neighbor.x][neighbor.y] == Tileset.NOTHING) {
                numNothing++;
            }
        }
        return numFloor == 1 && (numNothing == 1 && numWalls == 2 || numWalls == 3);
    }
}
