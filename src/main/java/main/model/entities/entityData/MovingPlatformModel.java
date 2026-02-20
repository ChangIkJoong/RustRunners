package main.model.entities.entityData;
import java.awt.geom.Rectangle2D;

public class MovingPlatformModel {

    private Rectangle2D.Float hitbox;

    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float speed;
    private boolean movingToEnd = true;

    public MovingPlatformModel(
            Rectangle2D.Float hitbox, float startX, float startY,
            float endX, float endY, float speed) {
        this.hitbox = hitbox;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.speed = speed;
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

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isMovingToEnd() {
        return movingToEnd;
    }

    public void setMovingToEnd(boolean movingToEnd) {
        this.movingToEnd = movingToEnd;
    }
}

