package main.controller;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import audio.controller.AudioController;
import main.controller.facades.IGameActions;
import main.controller.state.GameBaseState;
import main.controller.state.GamingState;
import main.controller.state.LeaderboardState;
import main.controller.state.LevelSelectState;
import main.controller.state.MenuState;
import main.model.GameModel;
import main.model.entities.entity.Player;
import main.model.leaderboard.LeaderboardService;
import main.model.leaderboard.ScoreEntry;
import main.model.levels.LevelManager;
import main.view.GameView;
import main.view.states.Leaderboard;
import utilities.GameConfig;
import utilities.LoadSave;

public class GameController implements IGameActions {

    public enum GameState {MENU, PLAYING, LEADERBOARD, LEVEL_SELECT}

    private final GameModel model;
    private final GameView view;
    private final Player player;
    private final LevelManager levelManager;
    private final AudioController audioController;
    private final LeaderboardService leaderboardService = new LeaderboardService();

    private final BufferedImage transitionImage;

    private GameState gameState = GameState.MENU;

    private GameBaseState currentState;
    private final GamingState gamingState;
    private final MenuState menuState;
    private final LeaderboardState leaderboardState;
    private final LevelSelectState levelSelectState;

    private boolean wasPlayerDead = false;
    private boolean wasInTransition = false;

    public GameController() {
        this.audioController = AudioController.getInstance();

        this.levelManager = new LevelManager();
        this.player = new Player(200, 550, (int) (32 * GameConfig.SCALE), (int) (32 * GameConfig.SCALE));
        this.model = new GameModel(player, levelManager);
        model.reloadPlayerForCurrentLevel();

        this.view = new GameView(model, GameConfig.GAME_WIDTH, GameConfig.GAME_HEIGHT);
        this.transitionImage = LoadSave.getSpriteAtlas(LoadSave.TRANSITION_IMG);

        this.gamingState = new GamingState(this);
        this.menuState = new MenuState(this, model::getPlayerName);
        this.levelSelectState = new LevelSelectState(this, levelManager);
        this.leaderboardState = new LeaderboardState(this, new Leaderboard.LeaderboardDataSource() {
            @Override
            public List<ScoreEntry> loadEntriesForLevel(int levelIndex) {
                return leaderboardService.loadEntriesForLevel(levelIndex);
            }

            @Override
            public int getLevelCount() {
                return levelManager.getLevelCount();
            }
        });

        this.currentState = menuState;
        this.wasPlayerDead = player.isDead();
        this.wasInTransition = model.isInTransition();
        currentState.onEnter();
    }

    public void update() {
        model.update();
        handleModelSideEffects();
        currentState.update();
    }

    public void render(Graphics g) {
        currentState.render(g);
        view.renderTransition(g, transitionImage);
    }

    public void renderGame(Graphics g) {
        view.renderGame(g);
    }

    public void onWindowFocusLost() {
        player.resetDirBooleans();
    }

    public void setPlayerName(String playerName) {
        model.setPlayerName(playerName);
    }

    public Player getPlayer() {
        return player;
    }

    public LevelManager getLevelManager() {
        return model.getLevelManager();
    }

    public void togglePauseInternal() {
        model.togglePause();
    }

    public void exitGame() {
        System.exit(0);
    }

    public void setGameState(GameState newState) {
        GameState oldState = this.gameState;
        this.gameState = newState;

        model.setGameActive(newState == GameState.PLAYING);

        GameBaseState previousState = currentState;

        if (newState == GameState.MENU && oldState == GameState.PLAYING) {
            model.onEnterMenuFromPlaying();
        }

        if (newState == GameState.PLAYING && oldState == GameState.MENU) {
            model.onEnterPlayingFromMenu();
        }

        if (newState == GameState.PLAYING && oldState == GameState.LEVEL_SELECT) {
            model.onEnterPlayingFromLevelSelect();
        }

        switch (newState) {
        case MENU -> currentState = menuState;
        case LEVEL_SELECT -> currentState = levelSelectState;
        case PLAYING -> currentState = gamingState;
        case LEADERBOARD -> currentState = leaderboardState;
        default -> currentState = menuState;
        }

        if (previousState != null && previousState != currentState) {
            previousState.onExit();
        }
        if (currentState != null && previousState != currentState) {
            currentState.onEnter();
        }
    }

    private void handleModelSideEffects() {
        if (model.doPlatformTriggered()) {
            audioController.playPlatformSound();
        }

        boolean isPlayerDead = player.isDead();
        if (!wasPlayerDead && isPlayerDead) {
            audioController.playDead();
            levelManager.getCurrentLvl().triggerSpawnPlatform();
        } else if (wasPlayerDead && !isPlayerDead) {
            audioController.playRespawn();
            levelManager.getCurrentLvl().resetPlatforms();
        }
        wasPlayerDead = isPlayerDead;

        boolean inTransition = model.isInTransition();
        if (!wasInTransition && inTransition) {
            recordLevelCompletion();
            audioController.playNextLevel();
        }
        wasInTransition = inTransition;

        if (model.doRunCompleted()) {
            setGameState(GameState.MENU);
            wasPlayerDead = player.isDead();
            wasInTransition = model.isInTransition();
        }
    }

    private void recordLevelCompletion() {
        long startTime = model.getStartTime();
        if (startTime <= 0L) {
            return;
        }

        long runEndTimeNanos = System.nanoTime();
        double timeSeconds = (runEndTimeNanos - startTime) / 1_000_000_000.0;
        int levelIndex = levelManager.getCurrentLevelIndex();
        LoadSave.appendToScoreFile(model.getPlayerName(), levelIndex, timeSeconds, model.getTotalDeaths());
    }

    @Override
    public void moveLeftPressed() {
        currentState.onMoveLeftPressed();
    }

    @Override
    public void moveLeftReleased() {
        currentState.onMoveLeftReleased();
    }

    @Override
    public void moveRightPressed() {
        currentState.onMoveRightPressed();
    }

    @Override
    public void moveRightReleased() {
        currentState.onMoveRightReleased();
    }

    @Override
    public void jumpPressed() {
        currentState.onJumpPressed();
    }

    @Override
    public void jumpReleased() {
        currentState.onJumpReleased();
    }

    @Override
    public void goToMenu() {
        currentState.onGoToMenu();
    }

    @Override
    public void togglePause() {
        currentState.onTogglePause();
    }

    @Override
    public void playJumpSound() {
        currentState.onPlayJumpSound();
    }

    @Override
    public void leaderboardNextLevel() {
        currentState.onLeaderboardNextLevel();
    }

    @Override
    public void leaderboardPreviousLevel() {
        currentState.onLeaderboardPreviousLevel();
    }

    @Override
    public void menuNameTyped(char c) {
        currentState.onMenuNameTyped(c);
    }

    @Override
    public void menuNameControlKey(int keyCode) {
        currentState.onMenuNameControlKey(keyCode);
    }

    @Override
    public void mouseMoved(int x, int y) {
        currentState.onMouseMoved(x, y);
    }

    @Override
    public void mousePressed(int x, int y) {
        currentState.onMousePressed(x, y);
    }

    @Override
    public void mouseReleased(int x, int y) {
        currentState.onMouseReleased(x, y);
    }
}
