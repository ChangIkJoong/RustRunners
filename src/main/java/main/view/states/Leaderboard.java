package main.view.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import main.model.leaderboard.ScoreEntry;
import main.view.states.Actions.LeaderboardActions;
import utilities.GameConfig;
import utilities.LoadSave;

public class Leaderboard {

    public interface LeaderboardDataSource {
        List<ScoreEntry> loadEntriesForLevel(int levelIndex);

        int getLevelCount();
    }

    private final LeaderboardActions actions;
    private final LeaderboardDataSource dataSource;
    private final Font titleFont = new Font("Arial", Font.BOLD, 48);
    private final Font headerFont = new Font("Arial", Font.BOLD, 24);
    private final Font rowFont = new Font("Arial", Font.PLAIN, 20);

    private int currentLevelIndex = 0;
    private BufferedImage backgroundImage;

    public Leaderboard(LeaderboardActions actions, LeaderboardDataSource dataSource) {
        this.actions = actions;
        this.dataSource = dataSource;
        loadBackgroundImage();
    }

    public void update() {
    }

    public void backToMenu() {
        actions.onBackToMenu();
    }

    public void nextLevel() {
        int totalLevels = Math.max(1, dataSource.getLevelCount());
        currentLevelIndex = (currentLevelIndex + 1) % totalLevels;
    }

    public void previousLevel() {
        int totalLevels = Math.max(1, dataSource.getLevelCount());
        currentLevelIndex = (currentLevelIndex - 1 + totalLevels) % totalLevels;
    }

    private void loadBackgroundImage() {
        backgroundImage = LoadSave.getSpriteAtlas(LoadSave.BACKGROUND);
    }

    public void draw(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, GameConfig.GAME_WIDTH, GameConfig.GAME_HEIGHT, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, GameConfig.GAME_WIDTH, GameConfig.GAME_HEIGHT);
        }

        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        String title = "LEVEL " + (currentLevelIndex + 1);
        FontMetrics font = g.getFontMetrics();
        int calculatedWidth = (GameConfig.GAME_WIDTH - font.stringWidth(title)) / 2;
        g.drawString(title, calculatedWidth, 200);

        g.setFont(headerFont);
        String leftArrow = "<<";
        String rightArrow = ">>";
        int arrowY = 200;
        g.drawString(leftArrow, calculatedWidth - 60, arrowY);
        g.drawString(rightArrow, calculatedWidth + font.stringWidth(title) + 40, arrowY);

        int startX = 300;
        int startY = 260;
        int headerCalculatedColumn = startX;
        int headerCalculatedColumnName = startX + 150;
        int headerCalculatedColumnDeaths = startX + 360;
        int headerCalculatedColumnTime = startX + 520;

        g.drawString("RANK", headerCalculatedColumn, startY);
        g.drawString("NAME", headerCalculatedColumnName, startY);
        g.drawString("DEATHS", headerCalculatedColumnDeaths, startY);
        g.drawString("TIME (s)", headerCalculatedColumnTime, startY);

        g.setFont(rowFont);
        List<ScoreEntry> entries = dataSource.loadEntriesForLevel(currentLevelIndex);
        int rowY = startY + 30;
        for (int i = 0; i < entries.size(); i++) {
            ScoreEntry entry = entries.get(i);
            g.drawString(String.valueOf(i + 1), headerCalculatedColumn, rowY);
            g.drawString(entry.getPlayerName(), headerCalculatedColumnName, rowY);
            g.drawString(String.valueOf(entry.getDeaths()), headerCalculatedColumnDeaths, rowY);
            g.drawString(String.format("%.2f", entry.getTimeSeconds()), headerCalculatedColumnTime, rowY);
            rowY += 26;
        }

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Use LEFT or RIGHT to change level, ESC to return", 200, GameConfig.GAME_HEIGHT - 170);
    }
}
