package core;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static core.Main.*;


public class Movement {
    static TETile[][] screenTiles;
    static TETile[][] coinRooms;
    static int currAvatarXCoord;
    static int currAvatarYCoord;
    static int pastXCoord;
    static int pastYCoord;
    static boolean gameRunning;
    List<Character> allKeysTyped = new ArrayList<>();
    
    public Movement(TETile[][] inputScreenTiles, TETile[][] inputCoinRooms) {
        screenTiles = inputScreenTiles;
        coinRooms = inputCoinRooms;
    }

    /** generates character onto board **/
    public void generateCharacter() {
        Random randomTile = new Random();
        while (true) {
            int xCoord = randomTile.nextInt(40);
            int yCoord = randomTile.nextInt(40);

            if (screenTiles[xCoord][yCoord] == Tileset.FLOOR) {
                screenTiles[xCoord][yCoord] = Tileset.AVATAR;
                currAvatarXCoord = xCoord;
                currAvatarYCoord = yCoord;
                break;
            }
        }
    }

    /** updates the movement along the way **/
    public void updateMovement() {
        if (StdDraw.hasNextKeyTyped()) {
            char keyTyped = StdDraw.nextKeyTyped();
            // quit and save ting if press :q or :Q
            allKeysTyped.add(keyTyped);
            if (allKeysTyped.size() > 1) {
                char colon = allKeysTyped.get(allKeysTyped.size() - 2);
                char q = allKeysTyped.getLast();
                if (colon == ':' && (q == 'q' || q == 'Q')) {
                    saveGame();
                    System.exit(0);
                }
            }

            switch(keyTyped) {
                case 'W', 'w':
                    currAvatarYCoord += 1;      // new move up POSITION
                    if (validMove(currAvatarXCoord, currAvatarYCoord)) {
                        if (screenTiles[currAvatarXCoord][currAvatarYCoord] == Tileset.FLOWER) {
                            pastXCoord = currAvatarXCoord;
                            pastYCoord = currAvatarYCoord;
                            Main.enterCoinScreen = true;         //enter the COIN WORLD
                        }
                        screenTiles[currAvatarXCoord][currAvatarYCoord - 1] = Tileset.FLOOR;
                        screenTiles[currAvatarXCoord][currAvatarYCoord] = Tileset.AVATAR;
                    } else {
                        currAvatarYCoord -= 1;
                    }
                    break;
                case 'A', 'a':
                    currAvatarXCoord -= 1;// new move Left POSITION
                    if (currAvatarXCoord < 0) {
                        //spawn on right side
                        screenTiles[currAvatarXCoord + 1][currAvatarYCoord] = Tileset.FLOOR;
                        spawnRight();
                    } else {
                        if (validMove(currAvatarXCoord, currAvatarYCoord)) {
                            if (screenTiles[currAvatarXCoord][currAvatarYCoord] == Tileset.FLOWER) {
                                Main.enterCoinScreen = true;
                                pastXCoord = currAvatarXCoord;
                                pastYCoord = currAvatarYCoord;
                            }
                            screenTiles[currAvatarXCoord + 1][currAvatarYCoord] = Tileset.FLOOR;
                            screenTiles[currAvatarXCoord][currAvatarYCoord] = Tileset.AVATAR;
                        } else {
                            currAvatarXCoord += 1;
                        }
                    }
                    break;
                case 'S', 's':
                    currAvatarYCoord -= 1;      // new move Down POSITION
                    if (validMove(currAvatarXCoord, currAvatarYCoord)) {
                        if (screenTiles[currAvatarXCoord][currAvatarYCoord] == Tileset.FLOWER) {
                            Main.enterCoinScreen = true;
                            pastXCoord = currAvatarXCoord;
                            pastYCoord = currAvatarYCoord;
                        }
                        screenTiles[currAvatarXCoord][currAvatarYCoord + 1] = Tileset.FLOOR;
                        screenTiles[currAvatarXCoord][currAvatarYCoord] = Tileset.AVATAR;
                    } else {
                        currAvatarYCoord += 1;
                    }
                    break;
                case 'D', 'd':
                    currAvatarXCoord += 1;      // new move Right POSITION
                    if (currAvatarXCoord >= ourWorldWidth) {
                        screenTiles[currAvatarXCoord - 1][currAvatarYCoord] = Tileset.FLOOR;
                        spawnLeft();
                    } else {
                        if (validMove(currAvatarXCoord, currAvatarYCoord)) {
                            if (screenTiles[currAvatarXCoord][currAvatarYCoord] == Tileset.FLOWER) {
                                Main.enterCoinScreen = true;
                                pastXCoord = currAvatarXCoord;
                                pastYCoord = currAvatarYCoord;
                            }
                            screenTiles[currAvatarXCoord - 1][currAvatarYCoord] = Tileset.FLOOR;
                            screenTiles[currAvatarXCoord][currAvatarYCoord] = Tileset.AVATAR;
                        } else {
                            currAvatarXCoord -= 1;
                        }
                    }
                    break;
            }
        }
    }

    /** teleport character onto right edge room **/
    public void spawnRight() {
        int rightEdge = ourWorldWidth - 1;
        Random randomNum = new Random();
        int randomNumToAdd = randomNum.nextInt(6);
        for (int y = 0; y < ourWorldHeight; y++) {
            if (screenTiles[rightEdge][y] == Tileset.FLOOR) {
                screenTiles[rightEdge][y + randomNumToAdd] = Tileset.AVATAR;
                currAvatarXCoord = rightEdge;
                currAvatarYCoord = y + randomNumToAdd;
                y = ourWorldHeight; //end for loop once cliopped room found
            }
        }
    }

    /** teleport character onto left edge room **/
    public void spawnLeft() {
        int leftEdge = 0;
        Random randomNum = new Random();
        int randomNumToAdd = randomNum.nextInt(6);
        for (int y = 0; y < ourWorldHeight; y++) {
            if (screenTiles[leftEdge][y] == Tileset.FLOOR) {
                screenTiles[leftEdge][y + randomNumToAdd] = Tileset.AVATAR;
                currAvatarXCoord = leftEdge;
                currAvatarYCoord = y + randomNumToAdd;
                y = ourWorldHeight; // end for loop thinging
            }
        }
    }

    /** checks if we can move in the direction we want to move: up, down, left, right **/
    public boolean validMove(int deltaX, int deltaY) {
        if (screenTiles[deltaX][deltaY] == Tileset.FLOOR || screenTiles[deltaX][deltaY] == Tileset.FLOWER) {
            return true;
        } else {
            StdAudio.play("/core/rickRoll.wav");
            return false;
        }
    }

    //same as above but checks the other 2D array
    public boolean validMoveCoinRoom(int deltaX, int deltaY) {
        if (coinRooms[deltaX][deltaY] == Tileset.UNLOCKED_DOOR || coinRooms[deltaX][deltaY] == Tileset.FLOOR) {
            return true;
        }
        return false;
    }

    /** saves the game and puts it into folder **/
    public void saveGame() {
        //create a string representation of screentiles
        StringBuilder gameString = new StringBuilder();
        // add in the game dimensions
        gameString.append(Main.gameSeed);
        gameString.append("\n");
        gameString.append(currAvatarXCoord);
        gameString.append("\n");
        gameString.append(currAvatarYCoord);
        FileUtils.writeFile("save.txt", gameString.toString());
    }

    /** loads the game and generates the avatar from where it previously was **/
    public static void loadSavedGame(String savedString) {
        In savedGame = new In(savedString);

        while (savedGame.hasNextLine()) {
            String firstLine = savedGame.readLine();
            Main.gameSeed = Long.parseLong(firstLine);

            String secondLine = savedGame.readLine();
            Main.savedCharacterX = Integer.parseInt(secondLine);

            String thirdLine = savedGame.readLine();
            Main.savedCharacterY = Integer.parseInt(thirdLine);
        }
    }
    public void generateCharacterAt(int x, int y) {
        screenTiles[x][y] = Tileset.AVATAR;
        currAvatarXCoord = x;
        currAvatarYCoord = y;
    }
//234523452
    public void generateCharacterCoin() {
        int center = ourWorldWidth / 2;
        int x = 25;
        int y = 13;
        coinRooms[x][y] = Tileset.AVATAR;
        currAvatarXCoord = x;
        currAvatarYCoord = y;
    }

    public void updateCoinMovement() {
        if (StdDraw.hasNextKeyTyped()) {
            char keyTyped = StdDraw.nextKeyTyped();
            // quit and save ting if press :q or :Q
            allKeysTyped.add(keyTyped);
            if (allKeysTyped.size() > 1) {
                char colon = allKeysTyped.get(allKeysTyped.size() - 2);
                char q = allKeysTyped.getLast();
                if (colon == ':' && (q == 'q' || q == 'Q')) {
                    saveGame();
                    System.exit(0);
                }
            }

            switch(keyTyped) {
                case 'W', 'w':
                    currAvatarYCoord += 1;      // new move up POSITION
                    if (validMoveCoinRoom(currAvatarXCoord, currAvatarYCoord)) {
                        if (collectCoinsSwitch()) {
                            coinRooms[currAvatarXCoord][currAvatarYCoord - 1] = Tileset.FLOOR;
                            coinRooms[currAvatarXCoord][currAvatarYCoord] = Tileset.AVATAR;
                        } else {
                            coinRooms[currAvatarXCoord][currAvatarYCoord - 1] = Tileset.FLOOR;
                            coinRooms[currAvatarXCoord][currAvatarYCoord] = Tileset.AVATAR;
                        }
                    } else {
                        currAvatarYCoord -= 1;
                    }
                    break;
                case 'A', 'a':
                    currAvatarXCoord -= 1;      // new move Left POSITION
                    if (validMoveCoinRoom(currAvatarXCoord, currAvatarYCoord)) {
                        if (collectCoinsSwitch()) {
                            coinRooms[currAvatarXCoord + 1][currAvatarYCoord] = Tileset.FLOOR;
                            coinRooms[currAvatarXCoord][currAvatarYCoord] = Tileset.AVATAR;
                        } else {
                            coinRooms[currAvatarXCoord + 1][currAvatarYCoord] = Tileset.FLOOR;
                            coinRooms[currAvatarXCoord][currAvatarYCoord] = Tileset.AVATAR;
                        }
                    } else {
                        currAvatarXCoord += 1;
                    }
                    break;
                case 'S', 's':
                    currAvatarYCoord -= 1;      // new move Down POSITION
                    if (validMoveCoinRoom(currAvatarXCoord, currAvatarYCoord)) {
                        if (collectCoinsSwitch()) {
                            coinRooms[currAvatarXCoord][currAvatarYCoord + 1] = Tileset.FLOOR;
                            coinRooms[currAvatarXCoord][currAvatarYCoord] = Tileset.AVATAR;
                        } else {
                            coinRooms[currAvatarXCoord][currAvatarYCoord + 1] = Tileset.FLOOR;
                            coinRooms[currAvatarXCoord][currAvatarYCoord] = Tileset.AVATAR;
                        }
                    } else {
                        currAvatarYCoord += 1;
                    }
                    break;
                case 'D', 'd':
                    currAvatarXCoord += 1;      // new move Right POSITION
                    if (validMoveCoinRoom(currAvatarXCoord, currAvatarYCoord)) {
                        if (collectCoinsSwitch()) {
                            coinRooms[currAvatarXCoord - 1][currAvatarYCoord] = Tileset.FLOOR;
                            coinRooms[currAvatarXCoord][currAvatarYCoord] = Tileset.AVATAR;
                        } else {
                            coinRooms[currAvatarXCoord - 1][currAvatarYCoord] = Tileset.FLOOR;
                            coinRooms[currAvatarXCoord][currAvatarYCoord] = Tileset.AVATAR;
                        }
                    } else {
                        currAvatarXCoord -= 1;
                    }
                    break;
            }
        }
    }
    public boolean collectCoinsSwitch() {
        //check if avatar position is at the Coins tile
        if (coinRooms[currAvatarXCoord][currAvatarYCoord] == Tileset.UNLOCKED_DOOR) {
            StdAudio.play("/core/coinSound.wav"); 
            World.numCoins--;
            return true;
        }
        return false;
    }

}
