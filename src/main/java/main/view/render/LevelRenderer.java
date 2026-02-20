package main.view.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import main.model.entities.entityLogic.DeathSprite;
import main.model.entities.entityLogic.MovingPlatform;
import main.model.entities.entityLogic.SpawnPlatform;
import main.model.entities.entityLogic.Spike;
import main.model.entities.entityLogic.TriggerPlatform;
import main.model.entities.entityLogic.TriggerSpike;
import main.model.levels.Level;
import main.model.levels.LevelManager;
import utilities.GameConfig;
import utilities.LoadSave;

/**
 * View-side renderer for levels and level entities.
 */
public class LevelRenderer {

    private final BufferedImage[] levelSprite;
    private final BufferedImage[] objectSprite;
    private final BufferedImage background;
    private final BufferedImage spawnTube;
    private final BufferedImage deathSprite;

    public LevelRenderer() {
        this.background = LoadSave.getSpriteAtlas(LoadSave.BG_DATA);
        this.spawnTube = LoadSave.getSpriteAtlas(LoadSave.SPAWN_TUBE);
        this.deathSprite = LoadSave.getSpriteAtlas(LoadSave.PLAYER_DEAD);
        this.levelSprite = importLevelSprites();
        this.objectSprite = importObjectSprites();
    }

    public void renderBackgroundAndTerrain(Graphics g, LevelManager levelManager) {
        if (background != null) {
            g.drawImage(background, 0, 0, GameConfig.GAME_WIDTH, GameConfig.GAME_HEIGHT, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, GameConfig.GAME_WIDTH, GameConfig.GAME_HEIGHT);
        }

        Level currentLevel = levelManager.getCurrentLvl();

        for (TriggerSpike triggerSpike : currentLevel.getTriggerSpikes()) {
            renderTriggerSpike(g, triggerSpike);
        }

        for (int j = 0; j < GameConfig.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < GameConfig.TILES_IN_WIDTH; i++) {
                int index = currentLevel.getSpriteIndex(i, j);
                BufferedImage tile = getSprite(levelSprite, index);
                if (tile != null) {
                    g.drawImage(tile,
                            i * GameConfig.TILES_SIZE,
                            j * GameConfig.TILES_SIZE,
                            GameConfig.TILES_SIZE,
                            GameConfig.TILES_SIZE,
                            null);
                }
            }
        }

        for (MovingPlatform movingPlatform : currentLevel.getMovingPlatforms()) {
            renderMovingPlatform(g, movingPlatform);
        }
        for (TriggerPlatform triggerPlatform : currentLevel.getTriggerPlatforms()) {
            renderTriggerPlatform(g, triggerPlatform);
        }
        for (Spike spike : currentLevel.getSpikes()) {
            renderSpike(g, spike);
        }
        for (DeathSprite sprite : currentLevel.getDeathSprites()) {
            renderDeathSprite(g, sprite);
        }
    }

    public void renderObjectLayer(Graphics g, LevelManager levelManager) {
        Level currentLevel = levelManager.getCurrentLvl();
        for (int j = 0; j < GameConfig.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < GameConfig.TILES_IN_WIDTH; i++) {
                int index = currentLevel.getObjectSpriteIndex(i, j);
                if (index > 0) {
                    BufferedImage sprite = getSprite(objectSprite, index);
                    if (sprite != null) {
                        g.drawImage(sprite,
                                i * GameConfig.TILES_SIZE,
                                j * GameConfig.TILES_SIZE,
                                GameConfig.TILES_SIZE,
                                GameConfig.TILES_SIZE,
                                null);
                    }
                }
            }
        }
    }

    public void renderSpawnPlatform(Graphics g, Level level) {
        SpawnPlatform spawnPlatform = level.getSpawnPlatform();
        if (spawnPlatform == null) {
            return;
        }

        if (spawnTube != null) {
            g.drawImage(spawnTube,
                    (int) spawnPlatform.getHitbox().x,
                    (int) spawnPlatform.getHitbox().y,
                    (int) spawnPlatform.getHitbox().width,
                    (int) spawnPlatform.getHitbox().height,
                    null);
            return;
        }

        g.setColor(Color.CYAN);
        g.fillRect(
                (int) spawnPlatform.getHitbox().x,
                (int) spawnPlatform.getHitbox().y,
                (int) spawnPlatform.getHitbox().width,
                (int) spawnPlatform.getHitbox().height);
    }

    private void renderSpike(Graphics g, Spike spike) {
        BufferedImage sprite = getSprite(objectSprite, spike.getSpriteId());

        int drawX = (int) spike.getHitbox().x;
        int drawY = (int) (spike.getHitbox().y - spike.getHeight() / 2f);

        if (sprite != null) {
            g.drawImage(sprite, drawX, drawY, spike.getWidth(), spike.getHeight(), null);
            return;
        }

        g.setColor(Color.RED);
        g.fillRect(drawX, drawY, spike.getWidth(), spike.getHeight());
    }

    private void renderTriggerSpike(Graphics g, TriggerSpike spike) {
        BufferedImage sprite = getSprite(objectSprite, spike.getSpriteId());

        int drawX = (int) spike.getHitbox().x;
        int drawY = (int) (spike.getHitbox().y - spike.getHitbox().height);
        int drawW = (int) spike.getHitbox().width;
        int drawH = (int) (spike.getHitbox().height * 2);

        if (sprite != null) {
            g.drawImage(sprite, drawX, drawY, drawW, drawH, null);
            return;
        }

        g.setColor(Color.MAGENTA);
        g.fillRect(drawX, (int) spike.getHitbox().y, drawW, (int) spike.getHitbox().height);
    }

    private void renderTriggerPlatform(Graphics g, TriggerPlatform platform) {
        int tileSize = GameConfig.TILES_SIZE;

        float spriteAreaX = platform.getHitbox().x + platform.getHitbox().width / 6f;
        float spriteAreaY = platform.getHitbox().y + platform.getHitbox().height / 6f;

        int firstX = (int) (spriteAreaX + platform.getFirstTileOffsetX());
        int firstY = (int) (spriteAreaY + platform.getFirstTileOffsetY());

        BufferedImage firstTile = getSprite(levelSprite, platform.getFirstTileSpriteId());
        if (firstTile != null) {
            g.drawImage(firstTile, firstX, firstY, tileSize, tileSize, null);
        } else {
            g.setColor(Color.ORANGE);
            g.fillRect(firstX, firstY, tileSize, tileSize);
        }

        List<float[]> tilePositions = platform.getTilePositions();
        List<Integer> tileSpriteIds = platform.getTileSpriteIds();

        for (int i = 0; i < tilePositions.size(); i++) {
            float[] pos = tilePositions.get(i);
            int tileX = (int) (spriteAreaX + pos[0]);
            int tileY = (int) (spriteAreaY + pos[1]);
            BufferedImage tile = getSprite(levelSprite, tileSpriteIds.get(i));
            if (tile != null) {
                g.drawImage(tile, tileX, tileY, tileSize, tileSize, null);
            }
        }
    }

    private void renderMovingPlatform(Graphics g, MovingPlatform platform) {
        g.setColor(Color.GRAY);
        g.fillRect((int) platform.getHitbox().x, (int) platform.getHitbox().y,
                (int) platform.getHitbox().width, (int) platform.getHitbox().height);
    }

    private void renderDeathSprite(Graphics g, DeathSprite sprite) {
        if (deathSprite != null) {
            g.drawImage(deathSprite,
                    (int) sprite.getHitbox().x,
                    (int) sprite.getHitbox().y,
                    sprite.getWidth(),
                    sprite.getHeight(),
                    null);
            return;
        }

        g.setColor(Color.DARK_GRAY);
        g.fillRect((int) sprite.getHitbox().x,
                (int) sprite.getHitbox().y,
                sprite.getWidth(),
                sprite.getHeight());
    }

    private static BufferedImage getSprite(BufferedImage[] sprites, int index) {
        if (sprites == null || index < 0 || index >= sprites.length) {
            return null;
        }
        return sprites[index];
    }

    private static BufferedImage[] importLevelSprites() {
        BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.LEVEL_ATLAS);
        BufferedImage[] sprites = new BufferedImage[81];
        if (img == null) {
            return sprites;
        }
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 9; i++) {
                int index = j * 9 + i;
                sprites[index] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
        return sprites;
    }

    private static BufferedImage[] importObjectSprites() {
        BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.OBJECT_ATLAS);
        BufferedImage[] sprites = new BufferedImage[48];
        if (img == null) {
            return sprites;
        }
        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < 8; i++) {
                int index = j * 8 + i;
                sprites[index] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
        return sprites;
    }
}
