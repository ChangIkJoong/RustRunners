package main.model;

import main.model.entities.entity.Player;
import main.model.levels.LevelManager;

public class GameModel {
    private static final float TRANSITION_SPEED = 0.015f;

    private final Player player;
    private final LevelManager levelManager;

    // Controller-independent playing flag
    private boolean isActive = false;

    // Transition State
    private boolean inTransition = false;
    private float transitionScale = 0f;
    private boolean scalingUp = true;
    private boolean isLevelLoaded = false;

    // Logic Flags
    private boolean wasPlayerDead = false;

    // Stats
    private String playerName = "Player1";
    private long startTime;
    private int totalDeaths;

    private boolean isPaused = false;
    private boolean runCompleted = false;

    public GameModel(Player player, LevelManager levelManager) {
        this.player = player;
        this.levelManager = levelManager;
    }

    //Update Loop---------------------------
    public void update() {
        if (inTransition) {
            updateTransition();
            return;
        }

        if (isActive) {
            updatePlaying();
        }
    }

    private void updatePlaying() {
        if (isPaused) {
            return;
        }

        player.update();
        levelManager.update(player);

        boolean isPlayerDead = player.isDead();

        if (!wasPlayerDead && isPlayerDead) {
            totalDeaths++;
        }
        wasPlayerDead = isPlayerDead;

        if (player.hasReachedLevelEnd()) {
            startLevelTransition();
            player.resetLevelEnd();
        }
    }

    private void updateTransition() {
        if (scalingUp) {
            transitionScale += TRANSITION_SPEED;
            if (transitionScale >= 2f) {
                transitionScale = 2f;
                if (!isLevelLoaded) {
                    // Logic for swapping levels
                    levelManager.setLevelScore(player.getDeathCount());
                    player.resetDeathCount();
                    boolean advanced = levelManager.loadNextLevel();
                    player.resetLevelEnd();
                    isLevelLoaded = true;

                    if (advanced) {
                        reloadPlayerForCurrentLevel();
                    } else {
                        resetTransition();
                        runCompleted = true;
                        return;
                    }
                }
                scalingUp = false;
            }
        } else {
            transitionScale -= TRANSITION_SPEED;
            if (transitionScale <= 0f) {
                transitionScale = 0f;
                inTransition = false;
            }
        }
    }

    public void startLevelTransition() {
        inTransition = true;
        scalingUp = true;
        isLevelLoaded = false;
        transitionScale = 0f;
    }

    public void resetTransition() {
        inTransition = false;
        scalingUp = true;
        isLevelLoaded = false;
        transitionScale = 0f;
    }

    public void resetStats() {
        totalDeaths = 0;
        startTime = 0L;
    }

    public void startNewTimer() {
        totalDeaths = 0;
        startTime = System.nanoTime();
    }

    public void togglePause() {
        if (isActive && !inTransition) {
            isPaused = !isPaused;
        }
    }

    public void onEnterMenuFromPlaying() {
        resetTransition();
        resetStats();
        levelManager.resetToFirstLevel();
        reloadPlayerForCurrentLevel();
    }

    public void onEnterPlayingFromMenu() {
        resetTransition();
        resetStats();
        startNewTimer();
    }

    public void onEnterPlayingFromLevelSelect() {
        resetTransition();
        resetStats();
        reloadPlayerForCurrentLevel();
        startNewTimer();
    }

    private void reloadPlayerForCurrentLevel() {
        main.model.levels.Level currentLevel = levelManager.getCurrentLvl();
        player.setSpawnPoint(currentLevel.getSpawnX(), currentLevel.getSpawnY());
        player.loadLvlData(currentLevel.getLevelData());
        player.setCurrentLevel(currentLevel);
        player.spawnAtLevelStart();
        currentLevel.resetPlatforms();
        currentLevel.clearDeathPositions();
    }

    //Getters & Setters ---
    public Player getPlayer() {
        return player;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isInTransition() {
        return inTransition;
    }

    public float getTransitionScale() {
        return transitionScale;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        if (name != null && !name.isBlank()) {
            this.playerName = name.trim();
        }
    }

    public void setGameActive(boolean isPlaying) {
        this.isActive = isPlaying;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean consumeRunCompleted() {
        boolean value = runCompleted;
        runCompleted = false;
        return value;
    }
}
