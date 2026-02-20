package utilities;

import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

public class HelpMethods {

    // transparent/passable
    private static final Set<Integer> NON_SOLID_TILES = new HashSet<Integer>() {
        {
            add(0);
            add(1);
            add(2);
            add(9);
            add(10);
            add(11);
            add(18);
            add(19);
            add(20);
            add(27);
            add(28);
            add(29);
            add(36);
            add(37);
            add(38);
            add(39);
            add(41);
            add(45);
            add(46);
            add(47);
            add(80);
        }
    };

    public static boolean canMoveHere(float x, float y, float width, float height, int[][] lvlData) {
        if (!checkIsSolid(x, y, lvlData)) {
            if (!checkIsSolid(x + width, y + height, lvlData)) {
                if (!checkIsSolid(x + width, y, lvlData)) {
                    if (!checkIsSolid(x, y + height, lvlData)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkIsSolid(float x, float y, int[][] lvlData) {
        if (x < 0 || x >= GameConfig.GAME_WIDTH) {
            return true;
        }
        if (y < 0 || y >= GameConfig.GAME_HEIGHT) {
            return true;
        }

        float xIndex = x / GameConfig.TILES_SIZE;
        float yIndex = y / GameConfig.TILES_SIZE;

        int value = lvlData[(int) yIndex][(int) xIndex];
        return !NON_SOLID_TILES.contains(value);
    }

    public static float getEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
        int currentTile = (int) (hitbox.x / GameConfig.TILES_SIZE);
        if (xSpeed > 0) {
            //höger
            int tileXPos = currentTile * GameConfig.TILES_SIZE;
            int xOffset = (int) (GameConfig.TILES_SIZE - hitbox.width);
            return tileXPos + xOffset - 1;
        } else {
            //vänster
            return currentTile * GameConfig.TILES_SIZE;
        }
    }


    public static float getEntityYPosUnderOrAbove(Rectangle2D.Float hitbox, float airSpeed) {
        int currentTile = (int) (hitbox.y / GameConfig.TILES_SIZE);
        if (airSpeed > 0) {
            // faller / på mark
            int tileYPos = currentTile * GameConfig.TILES_SIZE;
            int yOffset = (int) (GameConfig.TILES_SIZE - hitbox.height);
            return tileYPos + yOffset - 1;
        } else {
            // hoppar
            return currentTile * GameConfig.TILES_SIZE;
        }
    }

    public static boolean isEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
        //kolla pixel nere höger och vänster
        if (hitbox.y + hitbox.height >= GameConfig.GAME_HEIGHT - 32) {
            return false;
        }
        if (!checkIsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData)) {
            if (!checkIsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData)) {
                return false;

            }
        }
        return true;
    }

    public static boolean isEntityDead(Rectangle2D.Float hitbox, int[][] lvlData) {
        if (hitbox.y + hitbox.height >= GameConfig.GAME_HEIGHT - 5) {
            return true;
        }
        //implement collision with obstacle.


        return false;

    }

    public static void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000); // 2000 milliseconds = 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isOnLevelEnd(Rectangle2D.Float hitbox, int[][] lvlData) {
        // Check all corners of the hitbox for the level-end tile (45)
        int leftTile = (int) (hitbox.x / GameConfig.TILES_SIZE);
        int rightTile = (int) ((hitbox.x + hitbox.width) / GameConfig.TILES_SIZE);
        int topTile = (int) (hitbox.y / GameConfig.TILES_SIZE);
        int bottomTile = (int) ((hitbox.y + hitbox.height) / GameConfig.TILES_SIZE);

        // Bounds check
        if (leftTile < 0 || rightTile >= lvlData[0].length || topTile < 0 || bottomTile >= lvlData.length) {
            return false;
        }

        // Check if any part of the hitbox is on tile 45
        for (int y = topTile; y <= bottomTile; y++) {
            for (int x = leftTile; x <= rightTile; x++) {
                if (lvlData[y][x] == 45) {
                    return true;
                }
            }
        }
        return false;
    }


    public static float findGroundY(float x, float y, int spriteHeight, int[][] lvlData) {
        // Align Y to grid
        float alignedY = (float) (Math.floor(y / GameConfig.TILES_SIZE) * GameConfig.TILES_SIZE);

        // Create a temporary hitbox to check if already on ground
        Rectangle2D.Float tempHitbox = new Rectangle2D.Float(x, alignedY, GameConfig.TILES_SIZE, spriteHeight);
        if (isEntityOnFloor(tempHitbox, lvlData)) {
            return alignedY; // Already on ground
        }

        // Search downward for the first solid tile
        float currentY = alignedY;
        int maxSearch = (int) (GameConfig.GAME_HEIGHT / GameConfig.TILES_SIZE);

        for (int i = 0; i < maxSearch; i++) {
            currentY += GameConfig.TILES_SIZE;
            tempHitbox.y = currentY;

            if (isEntityOnFloor(tempHitbox, lvlData)) {
                // Found ground, position sprite on top of it (already grid-aligned)
                return currentY;
            }

            // Check if we've gone past the bottom of the level
            if (currentY + spriteHeight >= GameConfig.GAME_HEIGHT - 1000) {
                // No valid ground found - would fall off screen
                return -1;
            }
        }

        // No valid ground found
        return -1;
    }

}
