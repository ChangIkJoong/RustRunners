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

    public void onMoveLeftPressed() {
    }

    public void onMoveLeftReleased() {
    }

    public void onMoveRightPressed() {
    }

    public void onMoveRightReleased() {
    }

    public void onJumpPressed() {
    }

    public void onJumpReleased() {
    }

    public void onTogglePause() {
    }

    public void onGoToMenu() {
    }

    public void onPlayJumpSound() {
    }

    public void onLeaderboardNextLevel() {
    }

    public void onLeaderboardPreviousLevel() {
    }

    public void onMenuNameTyped(char c) {
    }

    public void onMenuNameControlKey(int keyCode) {
    }

    public void onMouseMoved(int x, int y) {
    }

    public void onMousePressed(int x, int y) {
    }

    public void onMouseReleased(int x, int y) {
    }
}
