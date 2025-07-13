package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class Game {
    /**
     * draws the starting menu where the user can choose how they wanna load the game
     **/
    public static void drawStartingScreen() {
        StdDraw.setCanvasSize(800, 800); // set canvas size
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setXscale(0, 100);
        StdDraw.setYscale(0, 100);
        StdDraw.setPenRadius(0.50);
        StdDraw.setPenColor(StdDraw.WHITE);

        Font textFont = new Font("Monospaced", Font.PLAIN, 30);
        StdDraw.setFont(textFont);
        StdDraw.text(50, 80, "CS61B: BYOW");
        StdDraw.text(50, 55, "(N) New Game");
        StdDraw.text(50, 45, "(L) Load Game");
        StdDraw.text(50, 35, "(Q) Quit Game");
        StdDraw.text(50, 15, "By Trevor and Benny");
    }

    /**
     * draws the screen for when the user wants to generate a game using a seed
     **/
    public static long enterSeedScreen() {
        StdDraw.setCanvasSize(800, 800); // set canvas size
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setXscale(0, 100);
        StdDraw.setYscale(0, 100);
        StdDraw.setPenRadius(0.50);
        StdDraw.setPenColor(StdDraw.WHITE);

        Font textFont = new Font("Monospaced", Font.PLAIN, 30);
        StdDraw.setFont(textFont);
        StdDraw.text(50, 80, "CS61B: BYOW"); // redraw everything
        StdDraw.text(50, 55, "Enter seed followed by S or s"); // redraw everything

        // we can append the keys typed here
        StringBuilder seedString = new StringBuilder();
        boolean continueRunning = true;
        long seed;

        while (continueRunning) {
            if (StdDraw.hasNextKeyTyped()) {
                StdDraw.clear(StdDraw.BLACK); // clear screen each time to redraw with updated stuff
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.text(50, 80, "CS61B: BYOW"); // redraw everything
                StdDraw.text(50, 55, "Enter seed followed by S or s"); // redraw everything
                char keyTyped = StdDraw.nextKeyTyped();
                switch (keyTyped) {
                    case 'S', 's':
                        continueRunning = false;
                        break;
                    // enter anything besides s or S, just show onto screen
                    default:
                        seedString.append(keyTyped);
                        StdDraw.setPenColor(StdDraw.YELLOW);
                        StdDraw.text(50, 45, seedString.toString());
                }
            }
        }
        // once s or S is typed, we need to exit this while loop and keep the seed
        seed = Long.parseLong(seedString.toString());
        return seed;
    }

    public static void enterCoinScreen() {
        StdDraw.setCanvasSize(800, 800); // set canvas size
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setXscale(0, 100);
        StdDraw.setYscale(0, 100);
        StdDraw.setPenRadius(0.50);
        StdDraw.setPenColor(StdDraw.WHITE);

        Font textFont = new Font("Monospaced", Font.PLAIN, 30);
        StdDraw.setFont(textFont);
        StdDraw.text(50, 80, "Collect All the Coins!"); // redraw everything
        StdDraw.text(50, 55, "Press S to Start"); // redraw everything
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                StdDraw.clear(StdDraw.BLACK); // clear screen each time to redraw with updated stuff
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.text(50, 80, "Collect All the Coins!"); // redraw everything
                StdDraw.text(50, 55, "Press S to Start"); // redraw everything
                char keyTyped = StdDraw.nextKeyTyped();
                switch (keyTyped) {
                    case 'S', 's':
                        Main.stayInCoins = true;
                        return;
                }
            }
        }
    }
}
