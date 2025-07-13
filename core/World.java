package core;
import java.awt.*;
import java.util.*;

import tileengine.TETile;
import tileengine.Tileset;

import static core.Main.*;


public class World {
    long seed;
    Random numGenerator;
    TETile[][] screenTiles;
    TETile[][] coinRoom;
    HashMap<Point, Room> coordinateMap;
    ArrayList<Room> allRooms;
    int edgeRoom1X, edgeRoom1Y, edgeRoom2X, edgeRoom2Y;
    static int numCoins;


    //create the map
    public World(long seed, TETile[][] tiles) {
        this.seed = seed;
        this.screenTiles = tiles;
        numGenerator = new Random(seed);
        coordinateMap = new HashMap<>();
        allRooms = new ArrayList<>();

        //MUST fill window with NOTHING first, THEN build rooms
        //TETile[][] screenTiles = new TETile[ourWorldWidth][ourWorldHeight];
        for (int x = 0; x < ourWorldWidth; x++) {
            for (int y = 0; y < ourWorldHeight; y++) {
                screenTiles[x][y] = Tileset.NOTHING;
            }
        }

    }

    public World(TETile[][] coinTiles) {
        this.coinRoom = coinTiles;

        //fill up the coin room
        for (int x = 0; x < ourWorldWidth; x++) {
            for (int y = 0; y < ourWorldHeight; y++) {
                coinRoom[x][y] = Tileset.NOTHING;
            }
        }
    }

    public TETile[][] createCoinRoom() {
        int center = ourWorldWidth / 4;
        int coinRoomWidth = center * 2;
        int coinRoomHeight = 10;

        //build top side and bottom side
        for (int x = center; x < center + coinRoomWidth; x++) {
            coinRoom[x][center] = Tileset.WALL;
            coinRoom[x][center + coinRoomHeight - 1] = Tileset.WALL;
        }

        //build left side and right side
        for (int y = center; y < center + coinRoomHeight; y++) {
            coinRoom[center][y] = Tileset.WALL;
            coinRoom[center + coinRoomWidth - 1][y] = Tileset.WALL;
        }

        // fill in floors
        for (int x = center + 1; x < center + coinRoomWidth - 1; x++) {
            for (int y = center + 1; y < center + coinRoomHeight - 1; y++) {
                coinRoom[x][y] = Tileset.FLOOR;
            }
        }

        //add in the COINS
        Random coinGen = new Random(gameSeed);
        numCoins = coinGen.nextInt(10) + 20;
        int i = 0;
        while (i < numCoins) {
            int xCoin = coinGen.nextInt(coinRoomWidth) + center;
            int yCoin = coinGen.nextInt(coinRoomHeight) + center;
            if (coinRoom[xCoin][yCoin] == Tileset.FLOOR) {
                coinRoom[xCoin][yCoin] = Tileset.UNLOCKED_DOOR;
                i++;
            }
        }
        return coinRoom;
    }


    /**
     * Methods for ROOM and HALLWAY are placeholders
     * <p> nothing is final, change everything as needed
     * <p/>
     */
    /** check if we can add Room **/
    public boolean canWeAddRoom(Room currRoom, Map<String, ArrayList<Integer>> currRoomWallCoords) {
        ArrayList<Integer> leftSideCoords = currRoomWallCoords.get("Left");
        ArrayList<Integer> rightSideCoords = currRoomWallCoords.get("Right");
        ArrayList<Integer> topCoords = currRoomWallCoords.get("Top");
        ArrayList<Integer> bottomCoords = currRoomWallCoords.get("Bottom");
        boolean foo = canWeAddRoomHelper(currRoom, "Left", leftSideCoords);
        boolean boo = canWeAddRoomHelper(currRoom, "Right", rightSideCoords);
        boolean zoo = canWeAddRoomHelper(currRoom, "Top", topCoords);
        boolean moo = canWeAddRoomHelper(currRoom, "Bottom", bottomCoords);

        return foo && boo && zoo && moo;
    }

    /** check if we can add Room helper **/
    public boolean canWeAddRoomHelper(Room currRoom, String side, ArrayList<Integer> sideCoords) {
        if (side.equals("Left") && currRoom.xCoord >= 0) {
            for (int y : sideCoords) {
                for (int i = 1; i <= 2; i++) {
                    int checkX = currRoom.xCoord - i;
                    if (checkX >= 0 && screenTiles[checkX][y] != Tileset.NOTHING) {
                        return false;
                    }
                }
            }
        } else if (side.equals("Right") && currRoom.xCoord + currRoom.width < ourWorldWidth) {
            for (int y : sideCoords) {
                for (int i = 1; i <= 2; i++) {
                    int checkX = currRoom.xCoord +currRoom.width - 1 + i;
                    if (checkX < ourWorldWidth && screenTiles[checkX][y] != Tileset.NOTHING) {
                        return false;
                    }
                }
            }
        } else if (side.equals("Top") && currRoom.yCoord + currRoom.height < ourWorldHeight) {
            for (int x : sideCoords) {
                for (int i = 1; i <= 2; i++) {
                    int checkY = currRoom.yCoord + currRoom.height - 1 + i;
                    if (checkY < ourWorldHeight && screenTiles[x][checkY] != Tileset.NOTHING) {
                        return false;
                    }
                }
            }
        } else if (side.equals("Bottom") && currRoom.yCoord >= 0) {
            for (int x : sideCoords) {
                for (int i = 1; i <= 2; i++) {
                    int checkY = currRoom.yCoord - i;
                    if (checkY >= 0 && screenTiles[x][checkY] != Tileset.NOTHING) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** creates the room **/
    public TETile[][] createRoom(){
        int totalRooms = 10 + numGenerator.nextInt(7);
        int roomsAdded = 0, coverage = 0;
        int totalWorldTiles = ourWorldHeight * ourWorldWidth;
        int retryGeneratingRoom = 0;
        int maxRetryAddRooms = 1000;
        int startRoomX = 0, startRoomY = 0, startRoomHeight = 0,
                startRoomWidth = 0, endRoomX = 0, endRoomY = 0;
        int numPortals = 0;

        while (roomsAdded < totalRooms && retryGeneratingRoom < maxRetryAddRooms && coverage < totalWorldTiles * 0.5) {
            int minRoomSize = 7;

            if (roomsAdded == 0) {
                leftRightEdgeRoomsConnected();
                roomsAdded += 2;
            }

            //pick random width/height sizes
            int width = numGenerator.nextInt(5) + minRoomSize;
            int height = numGenerator.nextInt(5) + minRoomSize;

            //pick random location in WINDOW that will fit the window (won't go outside)
            int maxXLocation = ourWorldWidth - width;
            int maxYLocation = ourWorldHeight - height;

            int xCoord = numGenerator.nextInt(maxXLocation);
            int yCoord = numGenerator.nextInt(maxYLocation);

            Room currRoom = new Room(xCoord, yCoord, width, height);
            // check if we can add a room
            if (canWeAddRoom(currRoom, currRoom.getRoomWallCoordinates())) {
                //build bottom side and top
                for (int x = xCoord; x < xCoord + width; x++) {
                    screenTiles[x][yCoord] = Tileset.WALL;
                    screenTiles[x][yCoord + height - 1] = Tileset.WALL;
                    coverage += 2;
                }

                //build left side and right side
                for (int y = yCoord; y < yCoord + height; y++) {
                    screenTiles[xCoord][y] = Tileset.WALL;
                    screenTiles[xCoord + width - 1][y] = Tileset.WALL;
                    coverage += 2;
                }

                // fill in floors
                for (int x = xCoord + 1; x < xCoord + width - 1; x++) {
                    for (int y = yCoord + 1; y < yCoord + height - 1; y++) {
                        screenTiles[x][y] = Tileset.FLOOR;
                        coverage += 1;
                    }
                }

                roomsAdded += 1;
                retryGeneratingRoom = 0;

                //add in a portal to COINS in Room
                if (numPortals == 0) {
                    int calcX = width / 2;
                    int calcY = height / 2;
                    screenTiles[xCoord + calcX][yCoord + calcY] = Tileset.FLOWER;
                    numPortals++;
                    System.out.println("suss");
                }

                // generating hallways portion
                if (roomsAdded == 3) {
                    createHallway(edgeRoom2X, edgeRoom2Y, xCoord, yCoord);
                    startRoomX = xCoord;
                    startRoomY = yCoord;
                } else {
                    endRoomX = xCoord;
                    endRoomY = yCoord;
                    createHallway(startRoomX, startRoomY, endRoomX, endRoomY);

                    // update startRoom + endRoom coordinates to be one we connected to
                    startRoomX = endRoomX;
                    startRoomY = endRoomY;
                }

            } else {
                retryGeneratingRoom += 1;
            }
        }
        return screenTiles;
    }

    public void createRightEdgeRoom() {
        int width = numGenerator.nextInt(5) + 9;
        int height = numGenerator.nextInt(5) + 9;

        int xCoord1 = ourWorldWidth - width;
        int maxYLocation = ourWorldHeight - height;
        int yCoord1 = numGenerator.nextInt(maxYLocation);

        // build top bottom
        for (int x = xCoord1; x < xCoord1 + width; x++) {
            screenTiles[x][yCoord1] = Tileset.WALL;
            screenTiles[x][yCoord1 + height - 1] = Tileset.WALL;
        }

        // build left only
        for (int y = yCoord1 + 1; y < yCoord1 + height - 1; y++) {
            screenTiles[xCoord1][y] = Tileset.WALL;
        }

        //fill floor
        for (int x = xCoord1 + 1; x < xCoord1 + width; x++) {
            for (int y = yCoord1 + 1; y < yCoord1 + height - 1; y++) {
                screenTiles[x][y] = Tileset.FLOOR;
            }
        }

        edgeRoom2X = xCoord1;
        edgeRoom2Y = yCoord1;
    }

    public void createLeftEdgeRoom() {
        int width = numGenerator.nextInt(4) + 9;
        int height = numGenerator.nextInt(4) + 9;

        int xCoord1 = 0;
        int maxYLocation = ourWorldHeight - height;
        int yCoord1 = numGenerator.nextInt(maxYLocation);

        //build top bottom
        for (int x = xCoord1; x < width; x++) {
            screenTiles[x][yCoord1] = Tileset.WALL;
            screenTiles[x][yCoord1 + height - 1] = Tileset.WALL;
        }

        //build right only
        for (int y = yCoord1 + 1; y < yCoord1 + height - 1; y++) {
            screenTiles[xCoord1 + width - 1][y] = Tileset.WALL;
        }

        //fill floor
        for (int x = xCoord1; x < xCoord1 + width - 1; x++) {
            for (int y = yCoord1 + 1; y < yCoord1 + height - 1; y++) {
                screenTiles[x][y] = Tileset.FLOOR;
            }
        }

        edgeRoom1X = xCoord1 + width - 2; //start from right
        edgeRoom1Y = yCoord1;
    }

    public void leftRightEdgeRoomsConnected() {
        createLeftEdgeRoom();
        createRightEdgeRoom();
        createHallway(edgeRoom1X, edgeRoom1Y, edgeRoom2X, edgeRoom2Y);
        //return screenTiles;
    }


    public void createHallway(int startRoomX, int startRoomY, int endRoomX, int endRoomY) {
        startRoomX += 1;
        startRoomY += 1;
        endRoomX += 1;      //Trev change **
        endRoomY += 1;
        if (startRoomX < endRoomX) {
            // go right
            for (int x = startRoomX; x <= endRoomX; x++) {
                if (screenTiles[x][startRoomY] != Tileset.FLOWER) {
                    screenTiles[x][startRoomY] = Tileset.FLOOR;
                }
                if (screenTiles[x][startRoomY + 1] == Tileset.NOTHING) {
                    screenTiles[x][startRoomY + 1] = Tileset.WALL;
                }
                if (screenTiles[x][startRoomY - 1] == Tileset.NOTHING) {
                    screenTiles[x][startRoomY - 1] = Tileset.WALL;
                }
            }
        } else if (startRoomX > endRoomX) {
            // go left
            for (int x = startRoomX; x >= endRoomX; x--) {
                if (screenTiles[x][startRoomY] != Tileset.FLOWER) {
                    screenTiles[x][startRoomY] = Tileset.FLOOR;
                }
                if (screenTiles[x][startRoomY + 1] == Tileset.NOTHING) {
                    screenTiles[x][startRoomY + 1] = Tileset.WALL;
                }
                if (screenTiles[x][startRoomY - 1] == Tileset.NOTHING) {
                    screenTiles[x][startRoomY - 1] = Tileset.WALL;
                }
            }
        }

        if (startRoomY < endRoomY) {
            // go up
            for (int y = startRoomY; y <= endRoomY; y++) {
                if (screenTiles[endRoomX][y] != Tileset.FLOWER) {
                    screenTiles[endRoomX][y] = Tileset.FLOOR;
                }
                if (screenTiles[endRoomX - 1][y] == Tileset.NOTHING) {
                    screenTiles[endRoomX - 1][y] = Tileset.WALL;
                }
                if (screenTiles[endRoomX + 1][y] == Tileset.NOTHING) {
                    screenTiles[endRoomX + 1][y] = Tileset.WALL;
                }
            }
        } else if (startRoomY > endRoomY) {
            // go down
            for (int y = startRoomY; y >= endRoomY; y--) {
                if (screenTiles[endRoomX][y] != Tileset.FLOWER) {
                    screenTiles[endRoomX][y] = Tileset.FLOOR;
                }
                if (screenTiles[endRoomX - 1][y] == Tileset.NOTHING) {
                    screenTiles[endRoomX - 1][y] = Tileset.WALL;
                }
                if (screenTiles[endRoomX + 1][y] == Tileset.NOTHING) {
                    screenTiles[endRoomX + 1][y] = Tileset.WALL;
                }
            }
        }
    }
}