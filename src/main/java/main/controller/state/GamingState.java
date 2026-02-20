package main.controller.state;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.controller.GameController;

public class GamingState extends GameBaseState {

    private boolean jumpSoundArmed = false;

    public GamingState(GameController controller) {
        super(controller);
    }

    @Override
    public void render(Graphics g) {
        controller.renderGame(g);
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
        controller.getPlayer().setLeft(true);
    }

    @Override
    public void onMoveLeftReleased() {
        controller.getPlayer().setLeft(false);
    }

    @Override
    public void onMoveRightPressed() {
        controller.getPlayer().setRight(true);
    }

    @Override
    public void onMoveRightReleased() {
        controller.getPlayer().setRight(false);
    }

    @Override
    public void onJumpPressed() {
        controller.getPlayer().setJump(true);
    }

    @Override
    public void onJumpReleased() {
        controller.getPlayer().setJump(false);
        jumpSoundArmed = false;
    }

    @Override
    public void onTogglePause() {
        controller.togglePauseInternal();
    }

    @Override
    public void onGoToMenu() {
        controller.setGameState(GameController.GameState.MENU);
    }

    @Override
    public void onPlayJumpSound() {
        if (!jumpSoundArmed) {
            AudioController.getInstance().playJump();
            jumpSoundArmed = true;
        }
    }
}
