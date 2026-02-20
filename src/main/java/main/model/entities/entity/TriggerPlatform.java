package main.model.entities.entity;

import java.util.List;

import audio.controller.AudioController;
import main.model.entities.states.TriggerPlatformModel;

public class TriggerPlatform extends Entity {

    private final TriggerPlatformModel model;

    // Sprite hitbox object to avoid creating garbage.
    private final java.awt.geom.Rectangle2D.Float cachedSpriteHitbox = new java.awt.geom.Rectangle2D.Float();

    private AudioController audioController;

    public TriggerPlatform(float x, float y, float targetX, float targetY,
                           int width, int height, float speed, int firstTileSpriteId,
                           boolean shouldReturn) {

        super(x, y, width, height);
        initHitbox(x, y, width, height);

        this.model = new TriggerPlatformModel(
                hitbox, x, y, targetX, targetY, speed, firstTileSpriteId, shouldReturn
        );
    }

    private static final class Destination {
        final float x;
        final float y;

        Destination(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public void setFirstTileOffset(float offsetX, float offsetY) {
        model.setFirstTileOffsetX(offsetX);
        model.setFirstTileOffsetY(offsetY);
    }

    public float getFirstTileOffsetX() {
        return model.getFirstTileOffsetX();
    }

    public float getFirstTileOffsetY() {
        return model.getFirstTileOffsetY();
    }

    public int getFirstTileSpriteId() {
        return model.getFirstTileSpriteId();
    }

    public void addTile(float relX, float relY, int tileSpriteId) {
        model.addTile(relX, relY, tileSpriteId);
    }

    public List<float[]> getTilePositions() {
        return model.getTilePositions();
    }

    public List<Integer> getTileSpriteIds() {
        return model.getTileSpriteIds();
    }

    public void setLoop(boolean loop) {
        model.setLoop(loop);
    }

    private void triggerImmediatelyIfLooping() {
        if (model.isLoop() && !model.isTriggered()) {
            model.setTriggered(true);
        }
    }

    private boolean shouldMove() {
        return model.isTriggered() && !model.isReachedTarget();
    }

    private boolean handleWaiting() {
        if (!model.isWaitingAtTarget()) {
            return false;
        }

        long now = System.currentTimeMillis();
        long waitedMs = now - model.getWaitStartTime();
        if (waitedMs >= model.getWaitDurationMs()) {
            model.setWaitingAtTarget(false);
            model.setMovingToTarget(!model.isMovingToTarget());
        }
        return true;
    }

    private Destination getCurrentDestination() {
        boolean toTarget = model.isMovingToTarget();
        float x = toTarget ? model.getTargetX() : model.getStartX();
        float y = toTarget ? model.getTargetY() : model.getStartY();
        return new Destination(x, y);
    }

    private void moveOrArrive(float destX, float destY) {
        float dirX = destX - hitbox.x;
        float dirY = destY - hitbox.y;
        float distance = (float) Math.sqrt(dirX * dirX + dirY * dirY);

        float speed = model.getSpeed();
        if (distance >= speed) {
            stepTowards(dirX, dirY, distance, speed);
            return;
        }

        snapTo(destX, destY);
        onArrived();
    }

    private void stepTowards(float dirX, float dirY, float distance, float speed) {
        hitbox.x += (dirX / distance) * speed;
        hitbox.y += (dirY / distance) * speed;
    }

    private void snapTo(float x, float y) {
        hitbox.x = x;
        hitbox.y = y;
    }

    private void onArrived() {
        boolean loop = model.isLoop();
        boolean toTarget = model.isMovingToTarget();
        boolean shouldReturn = model.isShouldReturn();

        if (loop) {
            startWait();
            return;
        }

        if (toTarget && shouldReturn) {
            startWait();
            model.setMovingToTarget(false);
            return;
        }

        model.setReachedTarget(true);
    }

    private void startWait() {
        model.setWaitingAtTarget(true);
        model.setWaitStartTime(System.currentTimeMillis());
    }

    public boolean checkPlayerCollision(Entity player) {
        return hitbox.intersects(player.getHitbox());
    }

    public void trigger() {
        if (!model.isTriggered()) {
            model.setTriggered(true);
            if (audioController != null) {
                audioController.playPlatformSound();
            }
        }
    }

    public boolean isTriggered() {
        return model.isTriggered();
    }

    public void reset() {
        hitbox.x = model.getStartX();
        hitbox.y = model.getStartY();
        model.setTriggered(false);
        model.setReachedTarget(false);
        model.setMovingToTarget(true);
        model.setWaitingAtTarget(false);
    }

    public void setHitboxSize(int hitboxWidth, int hitboxHeight, int newX, int newY) {
        float offsetX = newX - hitbox.x;
        float offsetY = newY - hitbox.y;

        hitbox.width = hitboxWidth;
        hitbox.height = hitboxHeight;
        hitbox.x = newX;
        hitbox.y = newY;

        model.setStartX(newX);
        model.setStartY(newY);

        model.setTargetX(model.getTargetX() + offsetX);
        model.setTargetY(model.getTargetY() + offsetY);
    }

    public void setSolid(boolean solid) {
        model.setSolid(solid);
    }

    public boolean isSolid() {
        return model.isSolid();
    }

    public void setAudioController(AudioController audioController) {
        this.audioController = audioController;
    }

    public java.awt.geom.Rectangle2D.Float getSpriteHitbox() {
        float spriteAreaW = hitbox.width * 2f / 3f;
        float spriteAreaH = hitbox.height * 2f / 3f;
        float spriteAreaX = hitbox.x + hitbox.width / 6f;
        float spriteAreaY = hitbox.y + hitbox.height / 6f;

        cachedSpriteHitbox.setRect(spriteAreaX, spriteAreaY, spriteAreaW, spriteAreaH);
        return cachedSpriteHitbox;
    }

    public void update() {
        triggerImmediatelyIfLooping();

        if (!shouldMove()) {
            return;
        }

        if (handleWaiting()) {
            return;
        }

        Destination dest = getCurrentDestination();
        moveOrArrive(dest.x, dest.y);
    }
}
