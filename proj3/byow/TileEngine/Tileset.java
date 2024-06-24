package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.darkGray, "you");
    public static final TETile WALL = new TETile('#', new Color(222, 107, 107), new Color(110, 49, 49),
            "wall");
    public static final TETile FLOOR = new TETile('·', new Color(3, 33, 3), Color.darkGray,
            "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile EXIT = new TETile('→', Color.white, new Color(32, 222, 100), "exit");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");

    // for sight range
    public static final TETile GRADIENT1 = new TETile('·', Color.DARK_GRAY, Color.BLACK, "gradient1");
    public static final TETile GRADIENT2 = new TETile('·', Color.GRAY, Color.BLACK, "gradient2");
    public static final TETile GRADIENT3 = new TETile('·', Color.LIGHT_GRAY, Color.BLACK, "gradient3");

}


