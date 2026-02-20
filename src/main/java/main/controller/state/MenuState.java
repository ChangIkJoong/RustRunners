package main.controller.state;

import java.awt.Graphics;
import java.util.function.Supplier;

import audio.controller.AudioController;
import main.controller.GameController;
import main.view.states.Actions.MainMenuActions;
import main.view.states.MainMenu;

public class MenuState extends GameBaseState implements MainMenuActions {

    private final MainMenu menuView;

    public MenuState(GameController controller, Supplier<String> playerNameSupplier) {
        super(controller);
        this.menuView = new MainMenu(this, playerNameSupplier);
    }

    @Override
    public void update() {
        menuView.update();
    }

    @Override
    public void render(Graphics g) {
        menuView.draw(g);
    }

    @Override
    public void onEnter() {
        AudioController.getInstance().playMenuMusic();
    }

    @Override
    public void onGoToMenu() {
        // Already in menu; no state transition needed.
    }

    @Override
    public void onMouseMoved(int x, int y) {
        menuView.mouseMoved(x, y);
    }

    @Override
    public void onMousePressed(int x, int y) {
        menuView.mousePressed(x, y);
    }

    @Override
    public void onMouseReleased(int x, int y) {
        menuView.mouseReleased(x, y);
    }

    @Override
    public void onMenuNameTyped(char c) {
        menuView.handleNameKeyPressed(0, c);
    }

    @Override
    public void onMenuNameControlKey(int keyCode) {
        menuView.handleNameKeyPressed(keyCode, '\0');
    }

    @Override
    public void onPlay() {
        controller.setGameState(GameController.GameState.PLAYING);
    }

    @Override
    public void onOpenLevelSelect() {
        controller.setGameState(GameController.GameState.LEVEL_SELECT);
    }

    @Override
    public void onOpenLeaderboard() {
        controller.setGameState(GameController.GameState.LEADERBOARD);
    }

    @Override
    public void onQuit() {
        controller.exitGame();
    }

    @Override
    public void onSetPlayerName(String name) {
        controller.setPlayerName(name);
    }
}
