package byow.Core;

import java.io.Serial;
import java.io.Serializable;

/**
 * Data structure of the game map
 * Includes the length, width and map seed of the game.
 */
public class Mondial implements Serializable {
    @Serial
    private static final long serialVersionUID = 114514;

    int width;
    int height;
    long seed;

    Mondial(int width, int height, long gundamSeed) {
        this.width = width;
        this.height = height;
        this.seed = gundamSeed;
    }
}
