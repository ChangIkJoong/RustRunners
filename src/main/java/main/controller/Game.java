package main.controller;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import audio.controller.AudioController;
import main.controller.facades.IGameActions;
import main.controller.inputs.KeyboardInputs;
import main.controller.inputs.MouseInputs;
import main.model.GameModel;
import main.model.entities.entity.Player;
import main.model.leaderboard.LeaderboardService;
import main.model.leaderboard.ScoreEntry;
import main.model.levels.LevelManager;
import main.view.GamePanel;
import main.view.GameView;
import main.view.GameWindow;
import main.controller.state.GameBaseState;
import main.controller.state.GamingState;
import main.controller.state.LeaderboardState;
import main.controller.state.LevelSelectState;
import main.controller.state.MenuState;
import main.view.states.Leaderboard;
import utilities.GameConfig;
import utilities.LoadSave;

public class Game implements Runnable, IGameActions {

    public static final float SCALE = GameConfig.SCALE;
    public static final int GAME_WIDTH = GameConfig.GAME_WIDTH;
    public static final int GAME_HEIGHT = GameConfig.GAME_HEIGHT;

    private GamePanel gamePanel;
    private GameWindow gameWindow;
    private Thread gametThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;

    private GameModel model;
    private GameView view;

    private Player player;
    private LevelManager levelManager;
    private AudioController audioController;
    private final LeaderboardService leaderboardService = new LeaderboardService();

    public enum GameState {MENU, PLAYING, LEADERBOARD, LEVEL_SELECT}

    private GameState gameState = GameState.MENU;

    private GameBaseState currentState;
    private GamingState gamingState;
    private MenuState menuState;
    private LeaderboardState leaderboardState;
    private LevelSelectState levelSelectState;

    private BufferedImage transitionImage;
    private boolean wasPlayerDead = false;
    private boolean wasInTransition = false;

    public Game() {
        audioController = AudioController.getInstance();
        initClasses();

        if (currentState != null) {
            currentState.onEnter();
        }

        gamePanel = new GamePanel(this);
        KeyboardInputs keyboardInputs = new KeyboardInputs(this);
        MouseInputs mouseInputs = new MouseInputs(this);
        gamePanel.attachInputListeners(keyboardInputs, mouseInputs, mouseInputs);

        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();

        startGameLoop();
    }

    private void initClasses() {
        levelManager = new LevelManager();

        player = new Player(200, 550, (int) (32 * SCALE), (int) (32 * SCALE));
        model = new GameModel(player, levelManager);
        model.reloadPlayerForCurrentLevel();

        view = new GameView(model, GAME_WIDTH, GAME_HEIGHT);
        transitionImage = LoadSave.getSpriteAtlas(LoadSave.TRANSITION_IMG);

        gamingState = new GamingState(this);
        menuState = new MenuState(this, model::getPlayerName);
        levelSelectState = new LevelSelectState(this, levelManager);
        leaderboardState = new LeaderboardState(this, new Leaderboard.LeaderboardDataSource() {
            @Override
            public List<ScoreEntry> loadEntriesForLevel(int levelIndex) {
                return leaderboardService.loadEntriesForLevel(levelIndex);
            }

            @Override
            public int getLevelCount() {
                return levelManager.getLevelCount();
            }
        });

        currentState = menuState;
        wasPlayerDead = player.isDead();
        wasInTransition = model.isInTransition();
    }

    private void update() {
        model.update();
        handleModelSideEffects();
        currentState.update();
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

    public void render(Graphics g) {
        currentState.render(g);
        view.renderTransition(g, transitionImage);
    }

    public void renderGame(Graphics g) {
        view.renderGame(g);
    }

    private void startGameLoop() {
        gametThread = new Thread(this);
        gametThread.start();
    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;
        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }
            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;
            }
            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setPlayerName(String playerName) {
        model.setPlayerName(playerName);
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

    public void togglePauseInternal() {
        model.togglePause();
    }

    public LevelManager getLevelManager() {
        return model.getLevelManager();
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
