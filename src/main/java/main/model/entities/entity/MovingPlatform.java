package main.model.entities.entity;

import main.model.entities.states.MovingPlatformModel;

public class MovingPlatform extends Entity {

    private final MovingPlatformModel model;

    public MovingPlatform(float startX, float startY, float endX, float endY,
                          int width, int height, float speed) {
        super(startX, startY, width, height);
        initHitbox(startX, startY, width, height);
        this.model = new MovingPlatformModel(hitbox, startX, startY, endX, endY, speed);
    }

    public void update() {
        float targetX = model.isMovingToEnd() ? model.getEndX() : model.getStartX();
        float targetY = model.isMovingToEnd() ? model.getEndY() : model.getStartY();

        float dirX = targetX - hitbox.x;
        float dirY = targetY - hitbox.y;
        float distance = (float) Math.sqrt(dirX * dirX + dirY * dirY);

        float speed = model.getSpeed();
        if (distance < speed) {
            hitbox.x = targetX;
            hitbox.y = targetY;
            model.setMovingToEnd(!model.isMovingToEnd());
        } else {
            hitbox.x += (dirX / distance) * speed;
            hitbox.y += (dirY / distance) * speed;
        }

        x = hitbox.x;
        y = hitbox.y;
    }
}
