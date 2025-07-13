package core;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;

public class Main {
    public static final int ourWorldWidth = 50;
    public static final int ourWorldHeight = 50;
    public static long gameSeed = 0;
    public static boolean loadedCharacter = false;
    public static String savedGame;  // var for the saved gme we want to load
    public static int savedCharacterX;
    public static int savedCharacterY;
    public static boolean enterCoinScreen = false;
    public static boolean stayInCoins = true;
    public static int coinsRemaining;
    public static boolean wonGame = false;

    public static void main(String[] args) {
        Game.drawStartingScreen();

        // user types a key

        boolean continueRunning = true;
        while (continueRunning) {
            if (StdDraw.hasNextKeyTyped()) {
                char keyTyped = StdDraw.nextKeyTyped();
                switch(keyTyped) {
                    case 'N', 'n':
                        gameSeed = Game.enterSeedScreen(); // return seed entered by user
                        continueRunning = false;
                        break;
                    case 'L', 'l':
                        Movement.loadSavedGame("save.txt");
                        loadedCharacter = true;
                        continueRunning = false;
                        break;
                    case 'Q', 'q':
                        System.exit(0);
                }
            }
        }

        // build your own world!
        TERenderer screen = new TERenderer();
        screen.initialize(ourWorldWidth, ourWorldHeight);
        TETile[][] screenTiles = new TETile[ourWorldWidth][ourWorldWidth];
        TETile[][] coinScreenTiles = new TETile[ourWorldWidth][ourWorldWidth];

        //build the Game World
        World GameWorld = new World(gameSeed, screenTiles);
        TETile[][] gameWithRooms = GameWorld.createRoom();

        //build the Coin World
        World CoinWorld = new World(coinScreenTiles);
        TETile[][] coinRooms = CoinWorld.createCoinRoom();






        // GERNERATE COIN CHARACTER HERE ************************** ^^^^^


        //place the character down
        Movement gameMovement = new Movement(gameWithRooms, coinRooms);
        if (!loadedCharacter) { // generate a new character only if we don't need to load it from saved file
            gameMovement.generateCharacter();
        } else {
            gameMovement.generateCharacterAt(savedCharacterX, savedCharacterY);
        }
        while (true) {
            gameMovement.updateMovement();
            screen.renderFrame(gameWithRooms);
            if (wonGame) {
                gameMovement.generateCharacterAt(Movement.pastXCoord, Movement.pastYCoord);
                StdAudio.play("/core/AlliWantisU.wav");
                wonGame = false;
            }
            //check if entering coin room
            if (enterCoinScreen) {
                StdAudio.play("/core/dingding.wav");
                Game.enterCoinScreen();//enter coin Screen;
                TERenderer coinScreen = new TERenderer();
                gameMovement.generateCharacterCoin();
                coinScreen.initialize(ourWorldWidth, ourWorldHeight);
                //enter Coin World
                while (stayInCoins){
                    gameMovement.updateCoinMovement();
                    coinScreen.renderFrame(coinRooms);
                    if (World.numCoins == 0) {             //check if coinRemaining == 0 to STOP
                        stayInCoins = false;
                        enterCoinScreen = false;
                        wonGame = true;
                    }
                }
            }
        }
    }
}
