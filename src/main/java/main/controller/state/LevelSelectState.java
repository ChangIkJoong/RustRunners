package main.controller.state;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.controller.Game;

public class LevelSelectState extends GameBaseState {

    public LevelSelectState(Game game) {
        super(game);
    }

    @Override
    public void update() {
        game.getLevelSelectView().update();
    }

    @Override
    public void render(Graphics g) {
        game.getLevelSelectView().draw(g);
    }

    @Override
    public void onEnter() {
        AudioController.getInstance().playJump();
    }
}
