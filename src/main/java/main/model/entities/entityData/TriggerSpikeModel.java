package main.model.entities.entityData;

import java.awt.geom.Rectangle2D;

public class TriggerSpikeModel {

    private Rectangle2D.Float hitbox;

    private float startX;
    private float startY;
    private float targetX;
    private float targetY;
    private float speed;

    private int spriteId;

    private boolean triggered;
    private boolean reachedTarget;

    private float triggerDistance;
    private boolean shouldReturn;

    private boolean movingToTarget = true;

    private boolean waitingAtTarget;
    private long waitStartTime;
    private long waitDurationMs = 500;

    private int id = -1;

    public TriggerSpikeModel(Rectangle2D.Float hitbox,
                             float startX, float startY,
                             float targetX, float targetY,
                             float speed,
                             float triggerDistance,
                             int spriteId,
                             boolean shouldReturn,
                             int id) {
        this.hitbox = hitbox;
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed;
        this.triggerDistance = triggerDistance;
        this.spriteId = spriteId;
        this.shouldReturn = shouldReturn;
        this.id = id;
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

    public int getSpriteId() {
        return spriteId;
    }

    public void setSpriteId(int spriteId) {
        this.spriteId = spriteId;
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

    public float getTriggerDistance() {
        return triggerDistance;
    }

    public void setTriggerDistance(float triggerDistance) {
        this.triggerDistance = triggerDistance;
    }

    public boolean isShouldReturn() {
        return shouldReturn;
    }

    public void setShouldReturn(boolean shouldReturn) {
        this.shouldReturn = shouldReturn;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
