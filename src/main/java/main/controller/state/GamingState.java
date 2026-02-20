package main.controller.state;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.controller.Game;

public class GamingState extends GameBaseState {

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
    }

    @Override
    public void onExit() {
        AudioController.getInstance().stopAll();
    }
}
