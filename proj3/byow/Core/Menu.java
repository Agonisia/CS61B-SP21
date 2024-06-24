package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;

public class Menu {
    private final int width;
    private final int height;
    public static final int CANVAS_SIZE = 16;
    public static final int FONT_SIZE = 30;
    public static final int TITLE_FONT_SIZE = 35;

    public Menu(int menuWidth, int menuHeight) {
        this.width = menuWidth;
        this.height = menuHeight;
    }

    /** Generates the main menu for the user. */
    public void generateMainMenu() {
        setupCanvas();
        drawMenu();
    }

    /** Generates the seed input menu and returns the seed as a long value. */
    public long generateSeedMenu() {
        setupCanvas();
        Font gameInfoFont = new Font("Courier New", Font.BOLD, FONT_SIZE);
        int midWidth = width / 2;
        int midHeight = height / 2;

        String seedString = "";
        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setFont(gameInfoFont);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(midWidth, midHeight + 4, "Please input a seed to generate world");
            StdDraw.text(midWidth, midHeight + 2, "Press 's' to start new game");
            StdDraw.text(midWidth, midHeight - 2, seedString);
            StdDraw.show();

            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            char digit = StdDraw.nextKeyTyped();
            if (digit == 's' || digit == 'S') {
                break;
            } else if (Character.isDigit(digit)) {
                seedString += digit;
            }
        }

        return seedString.isEmpty() ? 0 : Long.parseLong(seedString);
    }

    /** Sets up the canvas with the specified dimensions and background color. */
    private void setupCanvas() {
        StdDraw.setCanvasSize(width * CANVAS_SIZE, height * CANVAS_SIZE);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    /** Draws the main menu with title and options. */
    private void drawMenu() {
        int midWidth = width / 2;
        int midHeight = height / 2;

        StdDraw.setPenColor(Color.WHITE);
        Font fontTitle = new Font("Courier New", Font.BOLD, TITLE_FONT_SIZE);
        StdDraw.setFont(fontTitle);
        StdDraw.text(midWidth, midHeight + 5, "CS61B: Build Your own World");

        Font font = new Font("Courier New", Font.BOLD, FONT_SIZE);
        StdDraw.setFont(font);
        StdDraw.text(midWidth, midHeight, "New Game (N)");
        StdDraw.text(midWidth, midHeight - 2, "Load Game (L)");
        StdDraw.text(midWidth, midHeight - 4, "Quit (Q)");
        StdDraw.show();
    }
}
