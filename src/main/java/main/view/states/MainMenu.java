package main.view.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.Supplier;

import main.view.states.Actions.MainMenuActions;
import utilities.GameConfig;

public class MainMenu {
    private final MainMenuActions actions;
    private final Supplier<String> playerNameSupplier;
    private ArrayList<String> options = new ArrayList<>();
    private ArrayList<Rectangle> bounds = new ArrayList<>();
    private int selected = -1;
    private Font titleFont = new Font("Algerian", Font.BOLD, 48);
    private Font optionsFont = new Font("Calibri", Font.PLAIN, 24);

    private boolean editingName = false;
    private StringBuilder nameBuffer = new StringBuilder();

    // Background image
    private BufferedImage backgroundImage;

    // Button images and states
    private ArrayList<ButtonState> buttonStates = new ArrayList<>();
    private ArrayList<BufferedImage[]> buttonImages = new ArrayList<>(); // [normal, hover, click] for each button

    private enum ButtonState {
        NORMAL, HOVER, CLICK
    }

    public MainMenu(MainMenuActions actions, Supplier<String> playerNameSupplier) {
        this.actions = actions;
        this.playerNameSupplier = playerNameSupplier;

        options.add("PLAY"); // index 0
        options.add("CHANGE PLAYER"); // index 1
        options.add("SELECT LEVEL"); // index 2
        options.add("LEADERBOARDS"); // index 3
        options.add("QUIT"); // index 4

        int buttonWidth = 350;  // Adjust this value for different button width
        int buttonHeight = 80;  // Adjust this value for different button height
        int startextNumber = (GameConfig.GAME_WIDTH - buttonWidth) / 2;
        int startY = (GameConfig.GAME_HEIGHT / 2 - (options.size() * (buttonHeight + 10)) / 2) + 50;
        for (int i = 0; i < options.size(); i++) {
            bounds.add(new Rectangle(startextNumber, startY + i * (buttonHeight + 10), buttonWidth, buttonHeight));
        }

        loadButtonImages();
        loadBackgroundImage();
        initializeButtonStates();
    }

    private void loadButtonImages() {
        // Load images for each button [normal, hover, click]
        buttonImages.add(new BufferedImage[]{
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.PLAY_BUTTON_NORMAL),
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.PLAY_BUTTON_HOVER),
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.PLAY_BUTTON_CLICK)
        });
        buttonImages.add(new BufferedImage[]{
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.CHANGE_PLAYER_BUTTON_NORMAL),
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.CHANGE_PLAYER_BUTTON_HOVER),
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.CHANGE_PLAYER_BUTTON_CLICK)
        });
        buttonImages.add(new BufferedImage[]{
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.SELECT_LEVEL_BUTTON_NORMAL),
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.SELECT_LEVEL_BUTTON_HOVER),
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.SELECT_LEVEL_BUTTON_CLICK)
        });
        buttonImages.add(new BufferedImage[]{
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.LEADERBOARDS_BUTTON_NORMAL),
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.LEADERBOARDS_BUTTON_HOVER),
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.LEADERBOARDS_BUTTON_CLICK)
        });
        buttonImages.add(new BufferedImage[]{
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.QUIT_BUTTON_NORMAL),
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.QUIT_BUTTON_HOVER),
            utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.QUIT_BUTTON_CLICK)
        });
    }

    private void loadBackgroundImage() {
        backgroundImage = utilities.LoadSave.getSpriteAtlas(utilities.LoadSave.MENU_BACKGROUND);
    }

    private void initializeButtonStates() {
        for (int i = 0; i < options.size(); i++) {
            buttonStates.add(ButtonState.NORMAL);
        }
    }

    private BufferedImage getButtonImage(int buttonIndex) {
        if (buttonIndex < 0 || buttonIndex >= buttonImages.size()) {
            return null;
        }

        BufferedImage[] images = buttonImages.get(buttonIndex);
        int stateIndex;

        switch (buttonStates.get(buttonIndex)) {
        case CLICK:
            stateIndex = 2; // click image
            break;
        case HOVER:
            stateIndex = 1; // hover image
            break;
        case NORMAL:
        default:
            stateIndex = 0; // normal image
            break;
        }

        return images[stateIndex];
    }

    public void update() {
        //maybe dynamic screens later?
    }

    public void draw(Graphics g) {
        // background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, GameConfig.GAME_WIDTH, GameConfig.GAME_HEIGHT, null);
        } else {
            // Fallback if image not loaded
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, GameConfig.GAME_WIDTH, GameConfig.GAME_HEIGHT);
        }

        // title
        g.setFont(titleFont);
        g.setColor(Color.WHITE);

        // current player name
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String nameText = "Player: " + playerNameSupplier.get();
        g.drawString(nameText, 20, 40);

        // the several options displayed:
        for (int i = 0; i < options.size(); i++) {
            Rectangle rectangle = bounds.get(i);
            BufferedImage buttonImage = getButtonImage(i);
            if (buttonImage != null) {
                g.drawImage(buttonImage, rectangle.x, rectangle.y, rectangle.width, rectangle.height, null);
            } else {
                // Fallback if image not loaded
                g.setFont(optionsFont);
                FontMetrics ofm = g.getFontMetrics();
                if (buttonStates.get(i) == ButtonState.CLICK) {
                    g.setColor(Color.GRAY);
                } else if (buttonStates.get(i) == ButtonState.HOVER) {
                    g.setColor(Color.LIGHT_GRAY);
                } else {
                    g.setColor(Color.DARK_GRAY);
                }
                g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                g.setColor(Color.WHITE);
                int sx = rectangle.x + (rectangle.width - ofm.stringWidth(options.get(i))) / 2;
                int sy = rectangle.y + (rectangle.height + ofm.getAscent()) / 2 - 4;
                g.drawString(options.get(i), sx, sy);
            }
        }

        if (editingName) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(Color.WHITE);
            String prompt = "Enter name (letters/numbers), ENTER to confirm, ESC to cancel";
            int pw = g.getFontMetrics().stringWidth(prompt);
            int px = (GameConfig.GAME_WIDTH - pw) / 2;
            int py = GameConfig.GAME_HEIGHT - 80;
            g.drawString(prompt, px, py);
            String current = nameBuffer.toString();
            int cw = g.getFontMetrics().stringWidth(current + "_");
            int cx = (GameConfig.GAME_WIDTH - cw) / 2;
            g.drawString(current + "_", cx, py + 30);
        }
    }

    public void mouseMoved(int x, int y) {
        if (editingName) {
            return;
        }
        selected = -1;
        for (int i = 0; i < bounds.size(); i++) {
            if (bounds.get(i).contains(x, y)) {
                selected = i;
                if (buttonStates.get(i) != ButtonState.CLICK) {
                    buttonStates.set(i, ButtonState.HOVER);
                }
            } else {
                buttonStates.set(i, ButtonState.NORMAL);
            }
        }
    }

    public void mousePressed(int x, int y) {
        if (editingName) {
            return;
        }
        for (int i = 0; i < bounds.size(); i++) {
            if (bounds.get(i).contains(x, y)) {
                buttonStates.set(i, ButtonState.CLICK);
                return;
            }
        }
    }

    public void mouseReleased(int x, int y) {
        if (editingName) {
            return;
        }
        for (int i = 0; i < bounds.size(); i++) {
            if (bounds.get(i).contains(x, y) && buttonStates.get(i) == ButtonState.CLICK) {
                handleSelection(i);
            }
            buttonStates.set(i, ButtonState.NORMAL);
        }
    }

    private void handleSelection(int choice) {
        switch (choice) {
        case 0:
            actions.onPlay();
            break;
        case 1: // Change Name
            startEditingName();
            break;
        case 2: // Select Level
            actions.onOpenLevelSelect();
            break;
        case 3: // Leaderboard
            actions.onOpenLeaderboard();
            break;
        case 4: // Quit
            actions.onQuit();
            break;
        default:
            break;
        }
    }

    private void startEditingName() {
        editingName = true;
        nameBuffer.setLength(0);
        nameBuffer.append(playerNameSupplier.get());
    }

    public boolean isEditingName() {
        return editingName;
    }

    public void handleNameKeyPressed(int keyCode, char keyChar) {
        if (!editingName) {
            return;
        }

        if (keyCode == java.awt.event.KeyEvent.VK_ENTER) {
            if (!nameBuffer.isEmpty()) {
                actions.onSetPlayerName(nameBuffer.toString());
            }
            editingName = false;
            return;
        }
        if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) {
            editingName = false;
            return;
        }
        if (keyCode == java.awt.event.KeyEvent.VK_BACK_SPACE) {
            if (!nameBuffer.isEmpty()) {
                nameBuffer.deleteCharAt(nameBuffer.length() - 1);
            }
            return;
        }

        if (keyCode == 0 && Character.isLetterOrDigit(keyChar) && nameBuffer.length() < 16) {
            nameBuffer.append(keyChar);
        }
    }
}
