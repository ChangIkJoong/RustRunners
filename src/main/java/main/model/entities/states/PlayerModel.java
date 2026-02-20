package main.model.entities.states;

import java.awt.geom.Rectangle2D;

import utilities.GameConfig;
import main.model.entities.entity.Player;

import static utilities.Constants.PlayerConstants.IDLE_RIGHT;

/**
 * Pure state holder for {@link Player}.
 *
 * <p>This class intentionally contains no rendering code and no sprite assets.
 * It exists so the Player's gameplay state can be separated from animation
 * assets / view concerns.
 */
public class PlayerModel {

    private Rectangle2D.Float hitbox;

    //movement state
    private boolean left;
    private boolean right;
    private boolean jump;
    private boolean moving;
    private boolean inAir;
    private boolean facingRight = true;

    //physics TODO maybe move this to a utilz?
    private float playerSpeed = 1.0f;
    private float airSpeed;
    private float gravity = 0.04f * GameConfig.SCALE;
    private float jumpSpeed = -2.5f;
    private float fallSpeedAfterCollision = 0.5f * GameConfig.SCALE;

    //spawn/state
    private float spawnX;
    private float spawnY;
    private boolean dead;
    private long deathTime;
    private boolean reachedLevelEnd;
    private int deathCount;

    // Animation state ( the renderer owns sprite frames)
    private int aniTick;
    private int aniIndex;
    private int aniSpeed = 15;
    private int playerAction = IDLE_RIGHT;

    //level data
    private int[][] lvlData;

    public PlayerModel(Rectangle2D.Float hitbox, float spawnX, float spawnY) {
        this.hitbox = hitbox;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rectangle2D.Float hitbox) {
        this.hitbox = hitbox;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public boolean isInAir() {
        return inAir;
    }

    public void setInAir(boolean inAir) {
        this.inAir = inAir;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public float getPlayerSpeed() {
        return playerSpeed;
    }

    public void setPlayerSpeed(float playerSpeed) {
        this.playerSpeed = playerSpeed;
    }

    public float getAirSpeed() {
        return airSpeed;
    }

    public void setAirSpeed(float airSpeed) {
        this.airSpeed = airSpeed;
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public float getJumpSpeed() {
        return jumpSpeed;
    }

    public void setJumpSpeed(float jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
    }

    public float getFallSpeedAfterCollision() {
        return fallSpeedAfterCollision;
    }

    public void setFallSpeedAfterCollision(float fallSpeedAfterCollision) {
        this.fallSpeedAfterCollision = fallSpeedAfterCollision;
    }

    public float getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(float spawnX) {
        this.spawnX = spawnX;
    }

    public float getSpawnY() {
        return spawnY;
    }

    public void setSpawnY(float spawnY) {
        this.spawnY = spawnY;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public long getDeathTime() {
        return deathTime;
    }

    public void setDeathTime(long deathTime) {
        this.deathTime = deathTime;
    }

    public boolean hasReachedLevelEnd() {
        return reachedLevelEnd;
    }

    public void setReachedLevelEnd(boolean reachedLevelEnd) {
        this.reachedLevelEnd = reachedLevelEnd;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public int getAniTick() {
        return aniTick;
    }

    public void setAniTick(int aniTick) {
        this.aniTick = aniTick;
    }

    public int getAniIndex() {
        return aniIndex;
    }

    public void setAniIndex(int aniIndex) {
        this.aniIndex = aniIndex;
    }

    public int getAniSpeed() {
        return aniSpeed;
    }

    public void setAniSpeed(int aniSpeed) {
        this.aniSpeed = aniSpeed;
    }

    public int getPlayerAction() {
        return playerAction;
    }

    public void setPlayerAction(int playerAction) {
        this.playerAction = playerAction;
    }

    public int[][] getLvlData() {
        return lvlData;
    }

    public void setLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
    }
}
