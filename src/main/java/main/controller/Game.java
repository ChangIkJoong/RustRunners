package main.controller;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import audio.controller.AudioController;
import main.controller.facades.IGameActions;
import main.controller.facades.IGameRead;
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
import main.view.states.Actions.LeaderboardActions;
import main.view.states.Actions.LevelSelectActions;
import main.view.states.Actions.MainMenuActions;
import main.view.states.Leaderboard;
import main.view.states.LevelSelect;
import main.view.states.MainMenu;
import utilities.GameConfig;
import utilities.LoadSave;

public class Game implements Runnable, IGameActions, IGameRead,
        MainMenuActions, LevelSelectActions, LeaderboardActions {

    //public static final int TILES_DEAFULT_SIZE = GameConfig.TILES_DEFAULT_SIZE;
    public static final float SCALE = GameConfig.SCALE;
    //public static final int TILES_IN_WIDTH = GameConfig.TILES_IN_WIDTH;
    //public static final int TILES_IN_HEIGHT = GameConfig.TILES_IN_HEIGHT;
    //public static final int TILES_SIZE = GameConfig.TILES_SIZE;
    public static final int GAME_WIDTH = GameConfig.GAME_WIDTH;
    public static final int GAME_HEIGHT = GameConfig.GAME_HEIGHT;

    private MainMenu mainMenu;
    private Leaderboard leaderboard;
    private LevelSelect levelSelect;

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
        KeyboardInputs keyboardInputs = new KeyboardInputs(this, this);
        MouseInputs mouseInputs = new MouseInputs(this);
        gamePanel.attachInputListeners(keyboardInputs, mouseInputs, mouseInputs);

        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();

        startGameLoop();
    }

    private void initClasses() {
        levelManager = new LevelManager();

        player = new Player(200, 550, (int) (32 * SCALE), (int) (32 * SCALE));
        loadPlayerForCurrentLevel();

        model = new GameModel(player, levelManager);

        view = new GameView(model, GAME_WIDTH, GAME_HEIGHT);
        transitionImage = LoadSave.getSpriteAtlas(LoadSave.TRANSITION_IMG);

        mainMenu = new MainMenu(this, model::getPlayerName);
        levelSelect = new LevelSelect(this, levelManager);
        leaderboard = new Leaderboard(this, new Leaderboard.LeaderboardDataSource() {
            @Override
            public List<ScoreEntry> loadEntriesForLevel(int levelIndex) {
                return leaderboardService.loadEntriesForLevel(levelIndex);
            }

            @Override
            public int getLevelCount() {
                return levelManager.getLevelCount();
            }
        });

        gamingState = new GamingState(this);
        menuState = new MenuState(this);
        leaderboardState = new LeaderboardState(this);
        levelSelectState = new LevelSelectState(this);

        currentState = menuState;
        wasPlayerDead = player.isDead();
        wasInTransition = model.isInTransition();
    }

    private void loadPlayerForCurrentLevel() {
        main.model.levels.Level currentLevel = levelManager.getCurrentLvl();
        player.setSpawnPoint(currentLevel.getSpawnX(), currentLevel.getSpawnY());
        player.loadLvlData(currentLevel.getLevelData());
        player.setCurrentLevel(currentLevel);
        player.spawnAtLevelStart();
        currentLevel.resetPlatforms();
        currentLevel.clearDeathPositions();
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

    @Override
    public GameState getGameState() {
        return gameState;
    }

    public AudioController getAudioController() {
        return audioController;
    }

    public MainMenu getMainMenuView() {
        return mainMenu;
    }

    public Leaderboard getLeaderboardView() {
        return leaderboard;
    }

    public LevelSelect getLevelSelectView() {
        return levelSelect;
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

    public void togglePause() {
        model.togglePause();
    }

    public LevelManager getLevelManager() {
        return model.getLevelManager();
    }

    @Override
    public boolean isEditingPlayerName() {
        return mainMenu != null && mainMenu.isEditingName();
    }

    @Override
    public void moveLeftPressed() {
        player.setLeft(true);
    }

    @Override
    public void moveLeftReleased() {
        player.setLeft(false);
    }

    @Override
    public void moveRightPressed() {
        player.setRight(true);
    }

    @Override
    public void moveRightReleased() {
        player.setRight(false);
    }

    @Override
    public void jumpPressed() {
        player.setJump(true);
    }

    @Override
    public void jumpReleased() {
        player.setJump(false);
    }

    @Override
    public void goToMenu() {
        setGameState(GameState.MENU);
    }

    @Override
    public void playJumpSound() {
        audioController.playJump();
    }

    @Override
    public void leaderboardNextLevel() {
        if (leaderboard != null) {
            leaderboard.nextLevel();
        }
    }

    @Override
    public void leaderboardPreviousLevel() {
        if (leaderboard != null) {
            leaderboard.previousLevel();
        }
    }

    @Override
    public void menuNameTyped(char c) {
        if (mainMenu != null) {
            mainMenu.handleNameKeyPressed(0, c);
        }
    }

    @Override
    public void menuNameControlKey(int keyCode) {
        if (mainMenu != null) {
            mainMenu.handleNameKeyPressed(keyCode, '\0');
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        if (gameState == GameState.MENU && mainMenu != null) {
            mainMenu.mouseMoved(x, y);
        } else if (gameState == GameState.LEVEL_SELECT && levelSelect != null) {
            levelSelect.mouseMoved(x, y);
        }
    }

    @Override
    public void mousePressed(int x, int y) {
        if (gameState == GameState.MENU && mainMenu != null) {
            mainMenu.mousePressed(x, y);
        } else if (gameState == GameState.LEVEL_SELECT && levelSelect != null) {
            levelSelect.mousePressed(x, y);
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        if (gameState == GameState.MENU && mainMenu != null) {
            mainMenu.mouseReleased(x, y);
        } else if (gameState == GameState.LEVEL_SELECT && levelSelect != null) {
            levelSelect.mouseReleased(x, y);
        }
    }

    @Override
    public void onPlay() {
        setGameState(GameState.PLAYING);
    }

    @Override
    public void onOpenLevelSelect() {
        setGameState(GameState.LEVEL_SELECT);
    }

    @Override
    public void onOpenLeaderboard() {
        setGameState(GameState.LEADERBOARD);
    }

    @Override
    public void onQuit() {
        System.exit(0);
    }

    @Override
    public void onSetPlayerName(String name) {
        setPlayerName(name);
    }

    @Override
    public void onBackToMenu() {
        setGameState(GameState.MENU);
    }

    @Override
    public void onSelectLevel(int levelIndex) {
        levelManager.setCurrentLevelIndex(levelIndex);
        setGameState(GameState.PLAYING);
    }

    @Override
    public void onNextLevel() {
        leaderboardNextLevel();
    }

    @Override
    public void onPreviousLevel() {
        leaderboardPreviousLevel();
    }
}
