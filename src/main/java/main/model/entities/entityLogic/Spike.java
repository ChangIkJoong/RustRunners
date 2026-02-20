package main.model.entities.entityLogic;

public class Spike extends Entity {

    private final int spriteId;

    public Spike(float x, float y, int width, int height, int spriteId) {
        super(x, y, width, height);
        // Hitbox is half height, positioned at bottom of sprite.
        initHitbox(x, y + height / 2f, width, height / 2f);
        this.spriteId = spriteId;
    }

    public int getSpriteId() {
        return spriteId;
    }

    public boolean checkPlayerCollision(Entity player) {
        return hitbox.intersects(player.getHitbox());
    }
}
