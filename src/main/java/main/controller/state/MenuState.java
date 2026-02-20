package main.controller.state;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.controller.Game;

public class MenuState extends GameBaseState {

    public MenuState(Game game) {
        super(game);
    }

    @Override
    public void update() {
        game.getMainMenuView().update();
    }

    @Override
    public void render(Graphics g) {
        game.getMainMenuView().draw(g);
    }

    @Override
    public void onEnter() {
        AudioController.getInstance().playMenuMusic();
    }
}
