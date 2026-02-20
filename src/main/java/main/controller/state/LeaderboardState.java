package main.controller.state;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.controller.Game;
import main.view.states.Actions.LeaderboardActions;
import main.view.states.Leaderboard;

public class LeaderboardState extends GameBaseState implements LeaderboardActions {

    private final Leaderboard leaderboardView;

    public LeaderboardState(Game game, Leaderboard.LeaderboardDataSource dataSource) {
        super(game);
        this.leaderboardView = new Leaderboard(this, dataSource);
    }

    @Override
    public void update() {
        leaderboardView.update();
    }

    @Override
    public void render(Graphics g) {
        leaderboardView.draw(g);
    }

    @Override
    public void onEnter() {
        AudioController.getInstance().playRespawn();
    }

    @Override
    public void onGoToMenu() {
        game.setGameState(Game.GameState.MENU);
    }

    @Override
    public void onLeaderboardNextLevel() {
        leaderboardView.nextLevel();
    }

    @Override
    public void onLeaderboardPreviousLevel() {
        leaderboardView.previousLevel();
    }

    @Override
    public void onBackToMenu() {
        game.setGameState(Game.GameState.MENU);
    }

    @Override
    public void onNextLevel() {
        leaderboardView.nextLevel();
    }

    @Override
    public void onPreviousLevel() {
        leaderboardView.previousLevel();
    }
}
