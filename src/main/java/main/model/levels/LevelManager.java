package main.model.levels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import audio.controller.AudioController;
import main.model.entities.entity.Player;
import utilities.LoadSave;

public class LevelManager {

    private List<Level> levels;
    private int currentLevelIndex = 0;
    private final Set<Integer> completedLevels = new HashSet<>();
    private AudioController audioController;

    public LevelManager() {
        buildAllLevels();
        // Level 1 is always unlocked.
        completedLevels.add(0);

        completedLevels.add(1);
        completedLevels.add(2);
        completedLevels.add(3);
        completedLevels.add(4);
        completedLevels.add(5);
        completedLevels.add(6);
    }

    public void setAudioController(AudioController audioController) {
        this.audioController = audioController;
        for (Level level : levels) {
            level.setAudioControllerForPlatforms(audioController);
        }
    }

    private void buildAllLevels() {
        levels = new ArrayList<>();

        addLevel(LoadSave.LEVEL_ONE_DATA, LoadSave.LEVEL_ONE_OBSTACLE_DATA,
                LoadSave.LEVEL_ONE_OBJ_DATA, "level1.txt");
        addLevel(LoadSave.LEVEL_TWO_DATA, LoadSave.LEVEL_TWO_OBSTACLE_DATA,
                LoadSave.LEVEL_TWO_OBJ_DATA, "level2.txt");
        addLevel(LoadSave.LEVEL_THREE_DATA, LoadSave.LEVEL_THREE_OBSTACLE_DATA,
                LoadSave.LEVEL_THREE_OBJ_DATA, "level3.txt");
        addLevel(LoadSave.LEVEL_FOUR_DATA, LoadSave.LEVEL_FOUR_OBSTACLE_DATA,
                LoadSave.LEVEL_FOUR_OBJ_DATA, "level4.txt");
        addLevel(LoadSave.LEVEL_FIVE_DATA, LoadSave.LEVEL_FIVE_OBSTACLE_DATA,
                LoadSave.LEVEL_FIVE_OBJ_DATA, "level5.txt");
        addLevel(LoadSave.LEVEL_SIX_DATA, LoadSave.LEVEL_SIX_OBSTACLE_DATA,
                LoadSave.LEVEL_SIX_OBJ_DATA, "level6.txt");
        addLevel(LoadSave.LEVEL_SEVEN_DATA, LoadSave.LEVEL_SEVEN_OBSTACLE_DATA,
                LoadSave.LEVEL_SEVEN_OBJ_DATA, "level7.txt");
    }

    private void addLevel(String levelDataKey, String obstacleDataKey, String objectDataKey, String configFile) {
        LevelConfigLoader.LevelConfig config = LevelConfigLoader.loadConfig(configFile);
        Level level = new Level(
                LoadSave.getLevelData(levelDataKey),
                LoadSave.getLevelObstacleData(obstacleDataKey),
                LoadSave.getLevelObjData(objectDataKey),
                config.spawnX,
                config.spawnY
        );
        LevelConfigLoader.applyConfig(level, config);
        level.setAudioControllerForPlatforms(audioController);
        levels.add(level);
    }

    public void update(Player player) {
        getCurrentLvl().updatePlatforms(player);
        getCurrentLvl().updateTriggerSpikes(player);
        getCurrentLvl().updateSpawnPlatform();
    }

    public Level getCurrentLvl() {
        return levels.get(currentLevelIndex);
    }

    /**
     * @return true if the manager advanced to a new level, false when already at the final level.
     */
    public boolean loadNextLevel() {
        markLevelCompleted(currentLevelIndex);

        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
            return true;
        }

        return false;
    }

    public void markCurrentLevelCompleted() {
        markLevelCompleted(currentLevelIndex);
    }

    public void markLevelCompleted(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levels.size()) {
            completedLevels.add(levelIndex);
            if (levelIndex + 1 < levels.size()) {
                completedLevels.add(levelIndex + 1);
            }
        }
    }

    public boolean isLevelUnlocked(int levelIndex) {
        return completedLevels.contains(levelIndex);
    }

    public int getLevelCount() {
        return levels.size();
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public void setCurrentLevelIndex(int index) {
        if (index >= 0 && index < levels.size()) {
            currentLevelIndex = index;
        }
    }

    public void setLevelScore(int death) {
        Level currLevel = levels.get(getCurrentLevelIndex());
        currLevel.updateDeathScore(death);
    }

    public void resetToFirstLevel() {
        currentLevelIndex = 0;
    }
}
