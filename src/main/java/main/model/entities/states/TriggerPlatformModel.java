package main.model.entities.states;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class TriggerPlatformModel {

    private Rectangle2D.Float hitbox;

    private float startX;
    private float startY;
    private float targetX;
    private float targetY;
    private float speed;

    private boolean triggered;
    private boolean reachedTarget;
    private boolean movingToTarget = true;

    private boolean waitingAtTarget;
    private long waitStartTime;
    private long waitDurationMs = 1000;

    private boolean solid;
    private boolean loop;
    private boolean shouldReturn;

    private int firstTileSpriteId;
    private final List<float[]> tilePositions = new ArrayList<>();
    private final List<Integer> tileSpriteIds = new ArrayList<>();
    private float firstTileOffsetX;
    private float firstTileOffsetY;

    public TriggerPlatformModel(Rectangle2D.Float hitbox, float startX, float startY,
                                float targetX, float targetY, float speed,
                                int firstTileSpriteId, boolean shouldReturn) {
        this.hitbox = hitbox;
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed;
        this.firstTileSpriteId = firstTileSpriteId;
        this.shouldReturn = shouldReturn;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rectangle2D.Float hitbox) {
        this.hitbox = hitbox;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getTargetX() {
        return targetX;
    }

    public void setTargetX(float targetX) {
        this.targetX = targetX;
    }

    public float getTargetY() {
        return targetY;
    }

    public void setTargetY(float targetY) {
        this.targetY = targetY;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public boolean isReachedTarget() {
        return reachedTarget;
    }

    public void setReachedTarget(boolean reachedTarget) {
        this.reachedTarget = reachedTarget;
    }

    public boolean isMovingToTarget() {
        return movingToTarget;
    }

    public void setMovingToTarget(boolean movingToTarget) {
        this.movingToTarget = movingToTarget;
    }

    public boolean isWaitingAtTarget() {
        return waitingAtTarget;
    }

    public void setWaitingAtTarget(boolean waitingAtTarget) {
        this.waitingAtTarget = waitingAtTarget;
    }

    public long getWaitStartTime() {
        return waitStartTime;
    }

    public void setWaitStartTime(long waitStartTime) {
        this.waitStartTime = waitStartTime;
    }

    public long getWaitDurationMs() {
        return waitDurationMs;
    }

    public void setWaitDurationMs(long waitDurationMs) {
        this.waitDurationMs = waitDurationMs;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isShouldReturn() {
        return shouldReturn;
    }

    public void setShouldReturn(boolean shouldReturn) {
        this.shouldReturn = shouldReturn;
    }

    public int getFirstTileSpriteId() {
        return firstTileSpriteId;
    }

    public void setFirstTileSpriteId(int firstTileSpriteId) {
        this.firstTileSpriteId = firstTileSpriteId;
    }

    public List<float[]> getTilePositions() {
        return tilePositions;
    }

    public List<Integer> getTileSpriteIds() {
        return tileSpriteIds;
    }

    public float getFirstTileOffsetX() {
        return firstTileOffsetX;
    }

    public void setFirstTileOffsetX(float firstTileOffsetX) {
        this.firstTileOffsetX = firstTileOffsetX;
    }

    public float getFirstTileOffsetY() {
        return firstTileOffsetY;
    }

    public void setFirstTileOffsetY(float firstTileOffsetY) {
        this.firstTileOffsetY = firstTileOffsetY;
    }

    public void addTile(float relX, float relY, int tileSpriteId) {
        tilePositions.add(new float[]{relX, relY});
        tileSpriteIds.add(tileSpriteId);
    }
}
