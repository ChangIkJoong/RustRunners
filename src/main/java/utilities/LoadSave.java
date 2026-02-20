package utilities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

public class LoadSave {

    public static final String PLAYER_ATLAS = "Character.png";
    public static final String PLAYER_DEAD = "Dead.png";
    public static final String LEVEL_ATLAS = "MAP_TILES.png";
    public static final String OBJECT_ATLAS = "OBJ_TILES.png";

    public static final String BG_DATA = "Background.png";
    public static final String TRANSITION_IMG = "Transition.png";
    public static final String SPAWN_TUBE = "SpawnTube.png";
    public static final String LOCK = "Lock.png";

    // UI Backgrounds
    public static final String MENU_BACKGROUND = "MenuBackground.png";
    public static final String BACKGROUND = "BackgroundLeaderboards.png";

    // UI Buttons
    public static final String BACK_BUTTON_NORMAL = "QuitButton.png";
    public static final String BACK_BUTTON_HOVER = "QuitButtonHover.png";
    public static final String BACK_BUTTON_CLICK = "QuitButtonClick.png";

    // Main Menu Buttons
    public static final String PLAY_BUTTON_NORMAL = "PlayButton.png";
    public static final String PLAY_BUTTON_HOVER = "PlayButtonHover.png";
    public static final String PLAY_BUTTON_CLICK = "PlayButtonClick.png";

    public static final String CHANGE_PLAYER_BUTTON_NORMAL = "ChangePlayerButton.png";
    public static final String CHANGE_PLAYER_BUTTON_HOVER = "ChangePlayerButtonHover.png";
    public static final String CHANGE_PLAYER_BUTTON_CLICK = "ChangePlayerButtonClick.png";

    public static final String SELECT_LEVEL_BUTTON_NORMAL = "SelectLevelButton.png";
    public static final String SELECT_LEVEL_BUTTON_HOVER = "SelectLevelButtonHover.png";
    public static final String SELECT_LEVEL_BUTTON_CLICK = "SelectLevelButtonClick.png";

    public static final String LEADERBOARDS_BUTTON_NORMAL = "LeaderboardsButton.png";
    public static final String LEADERBOARDS_BUTTON_HOVER = "LeaderboardsButtonHover.png";
    public static final String LEADERBOARDS_BUTTON_CLICK = "LeaderboardsButtonClick.png";

    public static final String QUIT_BUTTON_NORMAL = "QuitButton.png";
    public static final String QUIT_BUTTON_HOVER = "QuitButtonHover.png";
    public static final String QUIT_BUTTON_CLICK = "QuitButtonClick.png";

    // LEVEL1
    public static final String LEVEL_ONE_DATA = "Level1.png";
    public static final String LEVEL_ONE_OBSTACLE_DATA = "Level1Obstacles.png";
    public static final String LEVEL_ONE_OBJ_DATA = "Level1Objects.png";
    // LEVEL2
    public static final String LEVEL_TWO_DATA = "Level2.png";
    public static final String LEVEL_TWO_OBSTACLE_DATA = "Level2Obstacles.png";
    public static final String LEVEL_TWO_OBJ_DATA = "Level2Objects.png";
    // LEVEL3
    public static final String LEVEL_THREE_DATA = "Level3.png";
    public static final String LEVEL_THREE_OBSTACLE_DATA = "Level3Obstacles.png";
    public static final String LEVEL_THREE_OBJ_DATA = "Level3Objects.png";
    // LEVEL4
    public static final String LEVEL_FOUR_DATA = "Level4.png";
    public static final String LEVEL_FOUR_OBSTACLE_DATA = "Level4Obstacles.png";
    public static final String LEVEL_FOUR_OBJ_DATA = "Level4Objects.png";
    // LEVEL5
    public static final String LEVEL_FIVE_DATA = "Level5.png";
    public static final String LEVEL_FIVE_OBSTACLE_DATA = "Level5Obstacles.png";
    public static final String LEVEL_FIVE_OBJ_DATA = "Level5Objects.png";
    // LEVEL6
    public static final String LEVEL_SIX_DATA = "Level6.png";
    public static final String LEVEL_SIX_OBSTACLE_DATA = "Level6Obstacles.png";
    public static final String LEVEL_SIX_OBJ_DATA = "Level6Objects.png";
    // LEVEL7
    public static final String LEVEL_SEVEN_DATA = "Level7.png";
    public static final String LEVEL_SEVEN_OBSTACLE_DATA = "Level7Obstacles.png";
    public static final String LEVEL_SEVEN_OBJ_DATA = "Level7Objects.png";

    // leaderboard
    private static final String LEADERBOARD_FILE_NAME = "leaderboard.txt";

    public static BufferedImage getSpriteAtlas(String fileName) {
        BufferedImage img = null;
        InputStream inputStream = LoadSave.class.getResourceAsStream("/" + fileName);

        try {
            if (inputStream == null) {
                System.err.println("Could not load image: /" + fileName);
                return null;
            }
            img = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return img;
    }

    public static int[][] getLevelData(String levelFileName) {
        int[][] lvlData = new int[GameConfig.TILES_IN_HEIGHT][GameConfig.TILES_IN_WIDTH];
        BufferedImage img = getSpriteAtlas(levelFileName);
        if (img == null) {
            return lvlData;
        }
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int value = color.getRed();
                if (value >= 80) {
                    value = 80;
                }
                lvlData[j][i] = value;
            }
        }
        return lvlData;
    }

    public static int[][] getLevelObstacleData(String levelFileName) {
        int[][] lvlData = new int[GameConfig.TILES_IN_HEIGHT][GameConfig.TILES_IN_WIDTH];
        BufferedImage img = getSpriteAtlas(levelFileName);
        if (img == null) {
            return lvlData;
        }
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int value = color.getGreen();
                if (value >= 80) {
                    value = 80;
                }
                lvlData[j][i] = value;
            }
        }
        return lvlData;
    }

    public static int[][] getLevelObjData(String levelFileName) {
        int[][] lvlData = new int[GameConfig.TILES_IN_HEIGHT][GameConfig.TILES_IN_WIDTH];
        BufferedImage img = getSpriteAtlas(levelFileName);
        if (img == null) {
            return lvlData;
        }
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int value = color.getBlue();
                if (value >= 80) {
                    value = 80;
                }
                lvlData[j][i] = value;
            }
        }
        return lvlData;
    }

    private static Path getLeaderboardPath() {
        Path path = Paths.get("src", "main", "resources");

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path.resolve(LEADERBOARD_FILE_NAME);
    }

    public static void appendToScoreFile(String playerName, int levelIndex, double timeSeconds, int deaths) {
        Path path = getLeaderboardPath();
        int humanLevel = levelIndex + 1;

        String line = playerName + ";" + humanLevel + ";" +
                deaths + ";" + String.format(Locale.US, "%.2f", timeSeconds);

        try (BufferedWriter writer = Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readScoreFile() {
        List<String> lines = new ArrayList<>();
        Path path = getLeaderboardPath();
        if (!Files.exists(path)) {
            return lines;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
