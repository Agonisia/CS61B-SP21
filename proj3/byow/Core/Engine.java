package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 60;
    public static final int FONT_SIZE = 30;
    public static final int TIME_CONVERT = 1000;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        Menu menu = new Menu(WIDTH, HEIGHT);
        menu.generateMainMenu();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char command = StdDraw.nextKeyTyped();
                if (command == 'N' || command == 'n') {
                    newGame();
                } else if (command == 'L' || command == 'l') {
                    loadGame();
                } else if (command == 'Q' || command == 'q') {
                    System.exit(0);
                }
            }
        }
    }

    private static void newGame() {
        Menu menu = new Menu(WIDTH, HEIGHT);
        long seed = menu.generateSeedMenu();

        Mondial mondial = new Mondial(WIDTH, HEIGHT, seed);
        TETile[][] world = WorldBuilder.generateWorld(mondial);
        Player player = Player.generatePlayer(world, mondial);
        Exit exit = Exit.generateExit(world, player);

        gameStart(mondial, world, player, exit);

    }

    private static void loadGame() {
        Archive loadedArchive = Archive.load();

        Mondial mondial = loadedArchive.getMondial();
        TETile[][] world = loadedArchive.getWorld();
        Player player = loadedArchive.getPlayer();
        Exit exit = loadedArchive.getExit();

        gameStart(mondial, world, player, exit);
    }

    private static void gameStart(Mondial mondial, TETile[][] world, Player player, Exit exit) {
        TERenderer ter = new TERenderer();
        ter.initialize(mondial.width, mondial.height);

        // Render the initial view
        TETile[][] view = WorldBuilder.generateView(world, player, player.sightRange);
        ter.renderFrame(view);

        boolean gameClear = false;
        boolean gameSave = false;

        // Start the timer
        long startTime = System.currentTimeMillis();

        while (!gameClear) {
            if (StdDraw.hasNextKeyTyped()) {
                char command = StdDraw.nextKeyTyped();

                // Generate the updated view
                view = player.move(world, Character.toString(command));

                // Render the updated view
                ter.renderFrame(view);

                if (player.pos.x == exit.pos.x && player.pos.y == exit.pos.y) {
                    gameClear = true;

                    // Stop the timer and calculate the elapsed time
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = (endTime - startTime) / TIME_CONVERT; // Convert to seconds

                    StdDraw.clear();
                    StdDraw.clear(Color.black);
                    Font gameOverFont = new Font("Courier New", Font.BOLD, FONT_SIZE);
                    StdDraw.setFont(gameOverFont);
                    StdDraw.setPenColor(Color.white);
                    int midWidth = WIDTH / 2;
                    int midHeight = HEIGHT / 2;
                    StdDraw.text(midWidth, midHeight,
                            "Congratulations on completing CS61B!");
                    StdDraw.text(midWidth, midHeight - 2,
                            "You passed maze " + mondial.seed
                                    + " using " + elapsedTime + " seconds!");
                    StdDraw.show();
                }

                // If the user presses ":q" or ":Q", save the game and exit
                if (command == ':') {
                    gameSave = true;
                } else if ((command == 'Q' || command == 'q') && gameSave) {
                    Archive archive = new Archive(mondial, world, player, exit);
                    archive.save();
                    System.exit(0);
                } else {
                    gameSave = false;
                }
            }
        }
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters. (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww") The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        char firstOperation = input.charAt(0); // 'n' for new or 'l' for load or 's' for save.
        if (firstOperation == 'n' || firstOperation == 'N') {
            finalWorldFrame = newGameFromString(input);
        } else if (firstOperation == 'l' || firstOperation == 'L') {
            finalWorldFrame = loadGameFromString(input);
        } else if (firstOperation == 'q' || firstOperation == 'Q') {
            System.exit(0);
        } else {
            finalWorldFrame = newGameFromString(input);
        }

        return finalWorldFrame;
    }

    private static TETile[][] newGameFromString(String input) {
        long seed = getSeedFromString(input);

        Mondial mondial = new Mondial(WIDTH, HEIGHT, seed);
        TETile[][] world = WorldBuilder.generateWorld(mondial);
        Player player = Player.generatePlayer(world, mondial);
        Exit exit = Exit.generateExit(world, player);

        int indexS = input.indexOf('s');
        String inputString = input.substring(indexS + 1);
        controlWithString(mondial, world, player, exit, inputString);

        return world;
    }

    private static TETile[][] loadGameFromString(String input) {
        Archive loadedArchive = Archive.load();

        Mondial mondial = loadedArchive.getMondial();
        TETile[][] world = loadedArchive.getWorld();
        Player player = loadedArchive.getPlayer();
        Exit exit = loadedArchive.getExit();

        int indexL = input.indexOf('l');
        String inputString = input.substring(indexL + 1);
        controlWithString(mondial, world, player, exit, inputString);

        return world;
    }

    private static long getSeedFromString(String input) {
        // regular expression
        String pattern = "[sSwWaAdD]";

        // Find the first match
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(input);

        if (m.find()) {
            int index = m.start();
            return Long.parseLong(input.substring(1, index));
        } else {
            throw new IllegalArgumentException("Please enter directions: wasd");
        }
    }

    private static void controlWithString(Mondial mondial, TETile[][] world,
                                          Player player, Exit exit,
                                          String input) {

        boolean gameClear = false;
        int i = 0;

        while (!gameClear && i < input.length()) {
            char command = input.charAt(i);

            world = player.stringMove(world, Character.toString(command));

            if (player.pos.x == exit.pos.x && player.pos.y == exit.pos.y) {
                gameClear = true;
            }

            /* if the User press ":q" or ":Q", save the game and exit. */
            if (input.charAt(i) == ':') {
                if (i + 1 < input.length()
                        && (input.charAt(i + 1) == 'q' || input.charAt(i + 1) == 'Q')) {
                    Archive archive = new Archive(mondial, world, player, exit);
                    archive.save();
                    break;
                }
            }

            i = i + 1;
        }
    }
}
