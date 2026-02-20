package main.controller.state;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.controller.GameController;
import main.view.states.Actions.LeaderboardActions;
import main.view.states.Leaderboard;

public class LeaderboardState extends GameBaseState implements LeaderboardActions {

    private final Leaderboard leaderboardView;

    public LeaderboardState(GameController controller, Leaderboard.LeaderboardDataSource dataSource) {
        super(controller);
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
        controller.setGameState(GameController.GameState.MENU);
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
        controller.setGameState(GameController.GameState.MENU);
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
