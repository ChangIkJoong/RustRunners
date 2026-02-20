package main.model.entities.entityLogic;

public class SpawnPlatform extends Entity {
    private static final long WAIT_AT_BOTTOM_MS = 300;

    private final float startY;
    private final float loweredY;
    private final float speed;
    private boolean lowering = false;
    private boolean raising = false;
    private boolean atBottom = false;
    private long atBottomTime;

    public SpawnPlatform(float x, float y, int width, int height, float lowerDistance, float speed) {
        super(x, y, width, height);
        this.startY = y;
        this.loweredY = y + lowerDistance;
        this.speed = speed;
        initHitbox(x, y, width, height);
    }

    public void triggerSpawn() {
        lowering = true;
        raising = false;
        atBottom = false;
    }

    public void update() {
        if (lowering) {
            hitbox.y += speed;
            if (hitbox.y >= loweredY) {
                hitbox.y = loweredY;
                lowering = false;
                atBottom = true;
                atBottomTime = System.currentTimeMillis();
            }
        } else if (atBottom) {
            if (System.currentTimeMillis() - atBottomTime >= WAIT_AT_BOTTOM_MS) {
                atBottom = false;
                raising = true;
            }
        } else if (raising) {
            hitbox.y -= speed;
            if (hitbox.y <= startY) {
                hitbox.y = startY;
                raising = false;
            }
        }
    }

    public boolean hasReachedBottom() {
        return atBottom || raising;
    }

    public boolean isAnimating() {
        return lowering || atBottom || raising;
    }

    public void reset() {
        hitbox.y = startY;
        lowering = false;
        raising = false;
        atBottom = false;
    }
}
