package main.model.entities.entityLogic;

import main.model.entities.entityData.TriggerSpikeModel;

public class TriggerSpike extends Entity {

    private final TriggerSpikeModel model;

    public TriggerSpike(float x, float y, float targetX, float targetY, int width, int height,
                        float speed, float triggerDistance, int spriteId, boolean shouldReturn, int id,
                        int collisionWidth, int collisionHeight) {
        super(x, y, width, height);

        int xOffset = (width - collisionWidth) / 2;
        int yOffset = (height - collisionHeight) / 2;
        initHitbox(x + xOffset, y + yOffset, collisionWidth, collisionHeight);

        this.model = new TriggerSpikeModel(hitbox, x, y, targetX, targetY,
                speed, triggerDistance, spriteId, shouldReturn, id);
    }

    public int getId() {
        return model.getId();
    }

    public int getSpriteId() {
        return model.getSpriteId();
    }

    public void update() {
        if (!model.isTriggered() || model.isReachedTarget()) {
            return;
        }

        if (model.isWaitingAtTarget()) {
            if (System.currentTimeMillis() - model.getWaitStartTime() >= model.getWaitDurationMs()) {
                model.setWaitingAtTarget(false);
                model.setMovingToTarget(false);
            }
            return;
        }

        float destX = model.isMovingToTarget() ? model.getTargetX() : model.getStartX();
        float destY = model.isMovingToTarget() ? model.getTargetY() : model.getStartY();

        float dx = destX - hitbox.x;
        float dy = destY - hitbox.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        float speed = model.getSpeed();
        if (dist <= speed) {
            hitbox.x = destX;
            hitbox.y = destY;

            if (model.isMovingToTarget() && model.isShouldReturn()) {
                model.setWaitingAtTarget(true);
                model.setWaitStartTime(System.currentTimeMillis());
            } else {
                model.setReachedTarget(true);
            }
        } else {
            hitbox.x += (dx / dist) * speed;
            hitbox.y += (dy / dist) * speed;
        }
    }

    public boolean checkTriggerDistance(Entity player) {
        float px = player.getHitbox().x + player.getHitbox().width / 2;
        float py = player.getHitbox().y + player.getHitbox().height / 2;
        float sx = hitbox.x + hitbox.width / 2;
        float sy = hitbox.y + hitbox.height / 2;

        float dist = (float) Math.sqrt((px - sx) * (px - sx) + (py - sy) * (py - sy));
        return dist <= model.getTriggerDistance();
    }

    public boolean checkPlayerCollision(Entity player) {
        return hitbox.intersects(player.getHitbox());
    }

    public void trigger() {
        model.setTriggered(true);
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
}
