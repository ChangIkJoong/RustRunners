package main.model.levels;

import java.util.ArrayList;
import java.util.List;

import main.model.entities.entity.DeathSprite;
import main.model.entities.entity.Entity;
import main.model.entities.entity.MovingPlatform;
import main.model.entities.entity.SpawnPlatform;
import main.model.entities.entity.Spike;
import main.model.entities.entity.TriggerPlatform;
import main.model.entities.entity.TriggerSpike;
import utilities.GameConfig;

public class Level {
    private final int[][] lvlData;
    private final int[][] lvlObstacleData;
    private final int[][] lvlObjData;
    private final float spawnX;
    private final float spawnY;
    private int deathScore;
    private final List<MovingPlatform> movingPlatforms;
    private final List<TriggerPlatform> triggerPlatforms;
    private final List<Spike> spikes;
    private final List<TriggerSpike> triggerSpikes;
    private SpawnPlatform spawnPlatform;

    private final List<int[]> triggerPlatformPositions;
    private final List<DeathSprite> deathSprites;

    public Level(int[][] lvlData, int[][] lvlObstacleData, int[][] lvlObjData, float spawnX, float spawnY) {
        this.lvlData = lvlData;
        this.lvlObstacleData = lvlObstacleData;
        this.lvlObjData = lvlObjData;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.movingPlatforms = new ArrayList<>();
        this.triggerPlatforms = new ArrayList<>();
        this.spikes = new ArrayList<>();
        this.triggerSpikes = new ArrayList<>();
        this.triggerPlatformPositions = new ArrayList<>();
        this.deathSprites = new ArrayList<>();
    }

    public void createTriggerPlatformsFromTile(int tileId, int spriteId, float targetOffsetX,
                                               float targetOffsetY, float speed,
                                               boolean shouldReturn, boolean solid) {
        for (int y = 0; y < lvlObstacleData.length; y++) {
            for (int x = 0; x < lvlObstacleData[y].length; x++) {
                if (lvlObstacleData[y][x] == tileId) {
                    float posX = x * GameConfig.TILES_SIZE;
                    float posY = y * GameConfig.TILES_SIZE;

                    TriggerPlatform platform = new TriggerPlatform(
                            posX, posY,
                            posX + targetOffsetX, posY + targetOffsetY,
                            GameConfig.TILES_SIZE, GameConfig.TILES_SIZE,
                            speed,
                            spriteId,
                            shouldReturn);
                    platform.setSolid(solid);
                    triggerPlatforms.add(platform);

                    triggerPlatformPositions.add(new int[]{x, y});
                    lvlData[y][x] = 80;
                }
            }
        }
    }

    public boolean isTriggerPlatformPosition(int x, int y) {
        for (int[] pos : triggerPlatformPositions) {
            if (pos[0] == x && pos[1] == y) {
                return true;
            }
        }
        return false;
    }

    public void createGroupedTriggerPlatformFromTile(
            int tileId, float targetOffsetX, float targetOffsetY, float speed,
            boolean shouldReturn, boolean solid, boolean shouldLoop) {

        List<int[]> positions = new ArrayList<>();
        for (int y = 0; y < lvlObstacleData.length; y++) {
            for (int x = 0; x < lvlObstacleData[y].length; x++) {
                if (lvlObstacleData[y][x] == tileId) {
                    positions.add(new int[]{x, y});
                }
            }
        }

        if (positions.isEmpty()) {
            return;
        }

        int minX = positions.get(0)[0];
        int maxX = positions.get(0)[0];
        int minY = positions.get(0)[1];
        int maxY = positions.get(0)[1];

        for (int[] pos : positions) {
            minX = Math.min(minX, pos[0]);
            maxX = Math.max(maxX, pos[0]);
            minY = Math.min(minY, pos[1]);
            maxY = Math.max(maxY, pos[1]);
        }

        float posX = minX * GameConfig.TILES_SIZE;
        float posY = minY * GameConfig.TILES_SIZE;
        int width = (maxX - minX + 1) * GameConfig.TILES_SIZE;
        int height = (maxY - minY + 1) * GameConfig.TILES_SIZE;

        int[] first = positions.get(0);
        int firstSpriteId = lvlData[first[1]][first[0]];

        TriggerPlatform platform = new TriggerPlatform(
                posX, posY,
                posX + targetOffsetX, posY + targetOffsetY,
                width, height,
                speed,
                firstSpriteId,
                shouldReturn
        );

        platform.setSolid(solid);
        platform.setLoop(shouldLoop);

        platform.setFirstTileOffset(
                (first[0] - minX) * GameConfig.TILES_SIZE,
                (first[1] - minY) * GameConfig.TILES_SIZE
        );

        platform.setHitboxSize(
                (int) (width * 1.5), (int) (height * 1.5),
                (int) (posX - width * 0.25), (int) (posY - height * 0.25)
        );

        for (int i = 1; i < positions.size(); i++) {
            int[] pos = positions.get(i);
            float relX = (pos[0] - minX) * GameConfig.TILES_SIZE;
            float relY = (pos[1] - minY) * GameConfig.TILES_SIZE;
            int tileSpriteId = lvlData[pos[1]][pos[0]];
            platform.addTile(relX, relY, tileSpriteId);
        }

        triggerPlatforms.add(platform);

        for (int[] pos : positions) {
            triggerPlatformPositions.add(pos);
            lvlData[pos[1]][pos[0]] = 80;
        }
    }

    public boolean isOnSolidPlatform(java.awt.geom.Rectangle2D.Float playerHitbox) {
        for (TriggerPlatform platform : triggerPlatforms) {
            if (platform.isSolid()) {
                java.awt.geom.Rectangle2D.Float platHitbox = platform.getSpriteHitbox();
                float playerBottom = playerHitbox.y + playerHitbox.height;
                float platformTop = platHitbox.y;

                boolean verticallyAligned = playerBottom >= platformTop && playerBottom <= platformTop + 5;
                boolean horizontallyOverlapping =
                        playerHitbox.x + playerHitbox.width > platHitbox.x &&
                                playerHitbox.x < platHitbox.x + platHitbox.width;

                if (verticallyAligned && horizontallyOverlapping) {
                    return true;
                }
            }
        }
        return false;
    }

    public float getSolidPlatformY(java.awt.geom.Rectangle2D.Float playerHitbox, float airSpeed) {
        for (TriggerPlatform platform : triggerPlatforms) {
            if (platform.isSolid()) {
                java.awt.geom.Rectangle2D.Float platHitbox = platform.getSpriteHitbox();
                float playerBottom = playerHitbox.y + playerHitbox.height;
                float platformTop = platHitbox.y;

                boolean crossedPlatform = playerBottom >= platformTop && playerBottom <= platformTop + airSpeed + 5;
                boolean horizontallyOverlapping =
                        playerHitbox.x + playerHitbox.width > platHitbox.x &&
                                playerHitbox.x < platHitbox.x + platHitbox.width;

                if (crossedPlatform && horizontallyOverlapping) {
                    return platformTop - playerHitbox.height;
                }
            }
        }
        return -1;
    }

    //redundant?
    public void addMovingPlatform(MovingPlatform platform) {
        movingPlatforms.add(platform);
    }
    //redundant?
    public void addTriggerPlatform(TriggerPlatform platform) {
        triggerPlatforms.add(platform);
    }

    public boolean updatePlatforms(Entity player) {
        boolean platformTriggered = false;

        for (MovingPlatform platform : movingPlatforms) {
            platform.update();
        }
        for (TriggerPlatform platform : triggerPlatforms) {
            if (!platform.isTriggered() && platform.checkPlayerCollision(player)) {
                if (platform.trigger()) {
                    platformTriggered = true;
                }
            }

            float oldX = platform.getHitbox().x;
            float oldY = platform.getHitbox().y;

            platform.update();

            float dx = platform.getHitbox().x - oldX;
            float dy = platform.getHitbox().y - oldY;

            if ((dx != 0 || dy != 0) && platform.isSolid()) {
                java.awt.geom.Rectangle2D.Float platHitbox = platform.getSpriteHitbox();
                float prevPlatX = platHitbox.x - dx;
                float prevPlatY = platHitbox.y - dy;

                java.awt.geom.Rectangle2D.Float playerHitbox = player.getHitbox();
                float playerBottom = playerHitbox.y + playerHitbox.height;

                boolean verticallyAligned = playerBottom >= prevPlatY && playerBottom <= prevPlatY + 5;
                boolean horizontallyOverlapping =
                        playerHitbox.x + playerHitbox.width > prevPlatX &&
                                playerHitbox.x < prevPlatX + platHitbox.width;

                if (verticallyAligned && horizontallyOverlapping) {
                    if (utilities.HelpMethods.canMoveHere(
                            player.getHitbox().x + dx,
                            player.getHitbox().y + dy,
                            player.getHitbox().width,
                            player.getHitbox().height,
                            lvlData)) {

                        player.getHitbox().x += dx;
                        player.getHitbox().y += dy;
                    }
                }
            }
        }

        return platformTriggered;
    }

    public List<MovingPlatform> getMovingPlatforms() {
        return movingPlatforms;
    }

    public List<TriggerPlatform> getTriggerPlatforms() {
        return triggerPlatforms;
    }

    public List<Spike> getSpikes() {
        return spikes;
    }

    public List<TriggerSpike> getTriggerSpikes() {
        return triggerSpikes;
    }

    public List<DeathSprite> getDeathSprites() {
        return deathSprites;
    }

    public SpawnPlatform getSpawnPlatform() {
        return spawnPlatform;
    }

    public void resetPlatforms() {
        for (TriggerPlatform platform : triggerPlatforms) {
            platform.reset();
        }
        resetTriggerSpikes();
    }

    public void recordDeathPosition(float x, float y) {
        float groundY = utilities.HelpMethods.findGroundY(x, y, GameConfig.TILES_SIZE, lvlData);
        if (groundY >= 0) {
            deathSprites.add(new DeathSprite(x, groundY));
        }
    }

    public void clearDeathPositions() {
        deathSprites.clear();
    }

    public void createSpikesFromTile(int tileId, int spriteId) {
        for (int y = 0; y < lvlObstacleData.length; y++) {
            for (int x = 0; x < lvlObstacleData[y].length; x++) {
                if (lvlObstacleData[y][x] == tileId) {
                    float posX = x * GameConfig.TILES_SIZE;
                    float posY = y * GameConfig.TILES_SIZE;

                    Spike spike = new Spike(posX, posY,
                            GameConfig.TILES_SIZE, GameConfig.TILES_SIZE, spriteId);
                    spikes.add(spike);
                }
            }
        }
    }

    public boolean checkSpikeCollision(Entity player) {
        for (Spike spike : spikes) {
            if (spike.checkPlayerCollision(player)) {
                return true;
            }
        }
        return false;
    }

    public void createTriggerSpikesFromTile(
            int tileId, int spriteId, float targetOffsetX,
            float targetOffsetY, float speed,
            float triggerDistance,
            boolean shouldReturn, int id,
            int collisionWidth, int collisionHeight) {

        for (int y = 0; y < lvlObstacleData.length; y++) {
            for (int x = 0; x < lvlObstacleData[y].length; x++) {
                if (lvlObstacleData[y][x] == tileId) {
                    float posX = x * GameConfig.TILES_SIZE;
                    float posY = y * GameConfig.TILES_SIZE;

                    int cWidth = (collisionWidth > 0) ? collisionWidth : GameConfig.TILES_SIZE;
                    int cHeight = (collisionHeight > 0) ? collisionHeight : GameConfig.TILES_SIZE / 2;

                    TriggerSpike spike = new TriggerSpike(
                            posX, posY,
                            posX + targetOffsetX, posY + targetOffsetY,
                            GameConfig.TILES_SIZE, GameConfig.TILES_SIZE,
                            speed, triggerDistance,
                            spriteId,
                            shouldReturn,
                            id,
                            cWidth, cHeight
                    );
                    triggerSpikes.add(spike);
                }
            }
        }
    }

    public void updateTriggerSpikes(Entity player) {
        for (TriggerSpike spike : triggerSpikes) {
            if (!spike.isTriggered() && spike.checkTriggerDistance(player)) {
                spike.trigger();
                if (spike.getId() != -1) {
                    for (TriggerSpike otherSpike : triggerSpikes) {
                        if (otherSpike.getId() == spike.getId()) {
                            otherSpike.trigger();
                        }
                    }
                }
            }
            spike.update();
        }
    }

    public boolean checkTriggerSpikeCollision(Entity player) {
        for (TriggerSpike spike : triggerSpikes) {
            if (spike.checkPlayerCollision(player)) {
                return true;
            }
        }
        return false;
    }

    public void resetTriggerSpikes() {
        for (TriggerSpike spike : triggerSpikes) {
            spike.reset();
        }
    }

    public void setSpawnPlatform(SpawnPlatform platform) {
        this.spawnPlatform = platform;
    }

    public void updateSpawnPlatform() {
        if (spawnPlatform != null) {
            spawnPlatform.update();
        }
    }

    public void triggerSpawnPlatform() {
        if (spawnPlatform != null) {
            spawnPlatform.triggerSpawn();
        }
    }

    //test thing 1
    public boolean isSpawnPlatformAnimating() {
        return spawnPlatform != null && spawnPlatform.isAnimating();
    }
    //test thing 2
    public boolean hasSpawnPlatformReachedBottom() {
        return spawnPlatform == null || spawnPlatform.hasReachedBottom();
    }

    public int getSpriteIndex(int x, int y) {
        return lvlData[y][x];
    }

    public int getObjectSpriteIndex(int x, int y) {
        return lvlObjData[y][x];
    }

    public int[][] getLevelData() {
        return lvlData;
    }

    public float getSpawnX() {
        return spawnX;
    }

    public float getSpawnY() {
        return spawnY;
    }

    public void updateDeathScore(int death) {
        if (death == 0) {
            this.deathScore = death;
        } else if (death < getDeathCount()) {
            this.deathScore = death;
        }
        this.deathScore += 1;
    }

    public int getDeathCount() {
        return this.deathScore;
    }
}
