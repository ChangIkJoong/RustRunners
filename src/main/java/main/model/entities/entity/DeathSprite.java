package main.model.entities.entity;

import utilities.GameConfig;

public class DeathSprite extends Entity {

    public DeathSprite(float x, float y) {
        super(x, y, GameConfig.TILES_SIZE, GameConfig.TILES_SIZE);
        initHitbox(x, y, GameConfig.TILES_SIZE, GameConfig.TILES_SIZE);
    }
}
