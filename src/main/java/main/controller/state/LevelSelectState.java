package main.controller.state;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.controller.GameController;
import main.model.levels.LevelManager;
import main.view.states.Actions.LevelSelectActions;
import main.view.states.LevelSelect;

public class LevelSelectState extends GameBaseState implements LevelSelectActions {

    private final LevelSelect levelSelectView;

    public LevelSelectState(GameController controller, LevelManager levelManager) {
        super(controller);
        this.levelSelectView = new LevelSelect(this, levelManager);
    }

    @Override
    public void update() {
        levelSelectView.update();
    }

    @Override
    public void render(Graphics g) {
        levelSelectView.draw(g);
    }

    @Override
    public void onEnter() {
        AudioController.getInstance().playJump();
    }

    @Override
    public void onGoToMenu() {
        controller.setGameState(GameController.GameState.MENU);
    }

    @Override
    public void onMouseMoved(int x, int y) {
        levelSelectView.mouseMoved(x, y);
    }

    @Override
    public void onMousePressed(int x, int y) {
        levelSelectView.mousePressed(x, y);
    }

    @Override
    public void onMouseReleased(int x, int y) {
        levelSelectView.mouseReleased(x, y);
    }

    @Override
    public void onBackToMenu() {
        controller.setGameState(GameController.GameState.MENU);
    }

    @Override
    public void onSelectLevel(int levelIndex) {
        controller.getLevelManager().setCurrentLevelIndex(levelIndex);
        controller.setGameState(GameController.GameState.PLAYING);
    }
}
