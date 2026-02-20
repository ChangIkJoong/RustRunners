package main.controller.state;

import java.awt.Graphics;

import main.controller.Game;

public abstract class GameBaseState {
    protected final Game game;

    protected GameBaseState(Game game) {
        this.game = game;
    }

    public void update() {
    }

    public void render(Graphics g) {
    }

    public void onEnter() {
    }

    public void onExit() {
    }
}
