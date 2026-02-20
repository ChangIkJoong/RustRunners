package main.controller.state;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.controller.Game;

public class GamingState extends GameBaseState {

    private boolean jumpSoundArmed = false;

    public GamingState(Game game) {
        super(game);
    }

    @Override
    public void render(Graphics g) {
        game.renderGame(g);
    }

    @Override
    public void onEnter() {
        AudioController controller = AudioController.getInstance();
        controller.stopAll();
        controller.playRespawn();
        controller.playGameMusic();
        jumpSoundArmed = false;
    }

    @Override
    public void onExit() {
        AudioController.getInstance().stopAll();
        jumpSoundArmed = false;
    }

    @Override
    public void onMoveLeftPressed() {
        game.getPlayer().setLeft(true);
    }

    @Override
    public void onMoveLeftReleased() {
        game.getPlayer().setLeft(false);
    }

    @Override
    public void onMoveRightPressed() {
        game.getPlayer().setRight(true);
    }

    @Override
    public void onMoveRightReleased() {
        game.getPlayer().setRight(false);
    }

    @Override
    public void onJumpPressed() {
        game.getPlayer().setJump(true);
    }

    @Override
    public void onJumpReleased() {
        game.getPlayer().setJump(false);
        jumpSoundArmed = false;
    }

    @Override
    public void onTogglePause() {
        game.togglePauseInternal();
    }

    @Override
    public void onGoToMenu() {
        game.setGameState(Game.GameState.MENU);
    }

    @Override
    public void onPlayJumpSound() {
        if (!jumpSoundArmed) {
            AudioController.getInstance().playJump();
            jumpSoundArmed = true;
        }
    }
}
