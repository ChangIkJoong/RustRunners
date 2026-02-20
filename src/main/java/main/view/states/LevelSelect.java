package main.view.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.model.levels.LevelManager;
import main.view.states.Actions.LevelSelectActions;
import utilities.LoadSave;
import utilities.GameConfig;

public class LevelSelect {
    // Grid layout
    private static final int COLUMNS = 3;
    private static final int TOP_MARGIN = 120;
    private static final int PREVIEW_WIDTH = 300;
    private static final int PREVIEW_HEIGHT = 200;
    private static final int PREVIEW_SPACING = 30;

    //variables
    private final LevelSelectActions actions;
    private final LevelManager levelManager;
    private ArrayList<Rectangle> levelBounds = new ArrayList<>();
    private ArrayList<BufferedImage> levelPreviews = new ArrayList<>();
    private BufferedImage lockImage;
    private int hovered = -1;
    private Font titleFont = new Font("Arial", Font.BOLD, 48);
    private Font levelFont = new Font("Arial", Font.BOLD, 24);

    // Back button images and state
    private BufferedImage backButtonNormal;
    private BufferedImage backButtonHover;
    private BufferedImage backButtonClick;
    private ButtonState backButtonState = ButtonState.NORMAL;
    private Rectangle backButtonBounds;

    private enum ButtonState {
        NORMAL, HOVER, CLICK
    }

    public LevelSelect(LevelSelectActions actions, LevelManager levelManager) {
        this.actions = actions;
        this.levelManager = levelManager;
        loadLevelPreviews();
        loadButtonImages();
        calculateLevelBounds();
        calculateButtonBounds();
    }
    
    private void loadLevelPreviews() {
        // Load preview images for each level
        levelPreviews.add(LoadSave.getSpriteAtlas(LoadSave.LEVEL_ONE_DATA));
        levelPreviews.add(LoadSave.getSpriteAtlas(LoadSave.LEVEL_TWO_DATA));
        levelPreviews.add(LoadSave.getSpriteAtlas(LoadSave.LEVEL_THREE_DATA));
        levelPreviews.add(LoadSave.getSpriteAtlas(LoadSave.LEVEL_FOUR_DATA));
        levelPreviews.add(LoadSave.getSpriteAtlas(LoadSave.LEVEL_FIVE_DATA));
        levelPreviews.add(LoadSave.getSpriteAtlas(LoadSave.LEVEL_SIX_DATA));
        levelPreviews.add(LoadSave.getSpriteAtlas(LoadSave.LEVEL_SEVEN_DATA));
        
        // Load lock image
        lockImage = LoadSave.getSpriteAtlas(LoadSave.LOCK);
    }

    private void loadButtonImages() {
        backButtonNormal = LoadSave.getSpriteAtlas(LoadSave.BACK_BUTTON_NORMAL);
        backButtonHover = LoadSave.getSpriteAtlas(LoadSave.BACK_BUTTON_HOVER);
        backButtonClick = LoadSave.getSpriteAtlas(LoadSave.BACK_BUTTON_CLICK);
    }

    private void calculateButtonBounds() {
        // Set button bounds - adjust these values based on your button image sizes
        int buttonWidth = 120;  // Adjust this value for different back button width
        int buttonHeight = 50;  // Adjust this value for different back button height
        backButtonBounds = new Rectangle(20, 20, buttonWidth, buttonHeight);
    }

    private BufferedImage getButtonImage() {
        switch (backButtonState) {
        case CLICK:
            return backButtonClick != null ? backButtonClick : backButtonNormal;
        case HOVER:
            return backButtonHover != null ? backButtonHover : backButtonNormal;
        case NORMAL:
        default:
            return backButtonNormal;
        }
    }
    
    private void calculateLevelBounds() {
        int totalLevels = levelManager.getLevelCount();
        int startX = (GameConfig.GAME_WIDTH - (COLUMNS * PREVIEW_WIDTH + (COLUMNS - 1) * PREVIEW_SPACING)) / 2;
        
        for (int i = 0; i < totalLevels; i++) {
            int row = i / COLUMNS;
            int col = i % COLUMNS;
            int x = startX + col * (PREVIEW_WIDTH + PREVIEW_SPACING);
            int y = TOP_MARGIN + row * (PREVIEW_HEIGHT + PREVIEW_SPACING);
            levelBounds.add(new Rectangle(x, y, PREVIEW_WIDTH, PREVIEW_HEIGHT));
        }
    }
    
    public void update() {
        // Maybe add animations later?
    }
    
    public void draw(Graphics g) {
        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameConfig.GAME_WIDTH, GameConfig.GAME_HEIGHT);
        
        // Title
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        String title = "Select Level";
        FontMetrics fontMetrics = g.getFontMetrics();
        int titleX = (GameConfig.GAME_WIDTH - fontMetrics.stringWidth(title)) / 2;
        g.drawString(title, titleX, 60);
        
        // Draw level previews in grid
        g.setFont(levelFont);
        FontMetrics levelFontMetrics = g.getFontMetrics();
        
        for (int i = 0; i < levelBounds.size() && i < levelPreviews.size(); i++) {
            Rectangle bounds = levelBounds.get(i);
            BufferedImage preview = levelPreviews.get(i);
            boolean isUnlocked = levelManager.isLevelUnlocked(i);
            
            // Draw border
            if (i == hovered && isUnlocked) {
                g.setColor(Color.YELLOW);
                g.fillRect(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6);
            } else {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
            }
            
            // Draw preview image (scaled to fit)
            if (preview != null) {
                // Darken locked levels
                if (!isUnlocked) {
                    g.setColor(new Color(0, 0, 0, 180));
                    g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                }
                g.drawImage(preview, bounds.x, bounds.y, bounds.width, bounds.height, null);
            } else {
                // Fallback if image not loaded
                g.setColor(Color.GRAY);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
            
            // Draw lock overlay for locked levels
            if (!isUnlocked && lockImage != null) {
                int lockSize = 80;
                int lockX = bounds.x + (bounds.width - lockSize) / 2;
                int lockY = bounds.y + (bounds.height - lockSize) / 2;
                g.drawImage(lockImage, lockX, lockY, lockSize, lockSize, null);
            }
            
            // Draw level number overlay
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(bounds.x, bounds.y + bounds.height - 40, bounds.width, 40);
            g.setColor(isUnlocked ? Color.WHITE : Color.GRAY);
            String levelText = "Level " + (i + 1);
            int textX = bounds.x + (bounds.width - levelFontMetrics.stringWidth(levelText)) / 2;
            int textY = bounds.y + bounds.height - 10;
            g.drawString(levelText, textX, textY);
        }
        
        // Back button
        BufferedImage buttonImage = getButtonImage();
        if (buttonImage != null) {
            g.drawImage(buttonImage, backButtonBounds.x, backButtonBounds.y,
                       backButtonBounds.width, backButtonBounds.height, null);
        } else {
            // Fallback if image not loaded
            g.setColor(Color.DARK_GRAY);
            g.fillRect(backButtonBounds.x, backButtonBounds.y,
                      backButtonBounds.width, backButtonBounds.height);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            FontMetrics backFontMetrics = g.getFontMetrics();
            int backTextX = backButtonBounds.x + (backButtonBounds.width - backFontMetrics.stringWidth("Back")) / 2;
            int backTextY = backButtonBounds.y + (backButtonBounds.height + backFontMetrics.getAscent()) / 2 - 4;
            g.drawString("Back", backTextX, backTextY);
        }
    }
    
    public void mouseMoved(int x, int y) {
        hovered = -1;
        for (int i = 0; i < levelBounds.size(); i++) {
            if (levelBounds.get(i).contains(x, y)) {
                hovered = i;
                break;
            }
        }

        // Update back button state
        if (backButtonBounds.contains(x, y)) {
            if (backButtonState != ButtonState.CLICK) {
                backButtonState = ButtonState.HOVER;
            }
        } else {
            backButtonState = ButtonState.NORMAL;
        }
    }
    
    public void mousePressed(int x, int y) {
        // Check back button
        if (backButtonBounds.contains(x, y)) {
            backButtonState = ButtonState.CLICK;
            return;
        }

        // Check level selection
        for (int i = 0; i < levelBounds.size(); i++) {
            if (levelBounds.get(i).contains(x, y)) {
                handleLevelSelection(i);
                return;
            }
        }
    }

    public void mouseReleased(int x, int y) {
        // Check back button release
        if (backButtonBounds.contains(x, y) && backButtonState == ButtonState.CLICK) {
            actions.onBackToMenu();
            backButtonState = ButtonState.NORMAL;
            return;
        }

        // Reset button state if released outside
        backButtonState = ButtonState.NORMAL;
    }

    private void handleLevelSelection(int levelIndex) {
        // Only allow selecting unlocked levels
        if (!levelManager.isLevelUnlocked(levelIndex)) {
            return; // Level is locked, do nothing
        }
        
        // ställer in på nuvarande lvl index och spelar
        actions.onSelectLevel(levelIndex);
    }
}
