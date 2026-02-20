package main.controller.state;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.controller.Game;

public class LeaderboardState extends GameBaseState {

    public LeaderboardState(Game game) {
        super(game);
    }

    @Override
    public void update() {
        game.getLeaderboardView().update();
    }

    @Override
    public void render(Graphics g) {
        game.getLeaderboardView().draw(g);
    }

    @Override
    public void onEnter() {
        AudioController.getInstance().playRespawn();
    }
}
