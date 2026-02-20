package main.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.model.GameModel;
import main.view.render.LevelRenderer;
import main.view.render.PlayerRenderer;

public class GameView {
    private final GameModel model;
    private final int gameWidth;
    private final int gameHeight;

    private final LevelRenderer levelRenderer;
    private final PlayerRenderer playerRenderer;

    public GameView(GameModel model, int gameWidth, int gameHeight) {
        this.model = model;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.levelRenderer = new LevelRenderer();
        this.playerRenderer = new PlayerRenderer();
    }

    public void renderGame(Graphics g) {
        levelRenderer.renderBackgroundAndTerrain(g, model.getLevelManager());
        levelRenderer.renderObjectLayer(g, model.getLevelManager());

        playerRenderer.render(model.getPlayer(), g);

        levelRenderer.renderSpawnPlatform(g, model.getLevelManager().getCurrentLvl());
        drawHUD(g);

        if (model.isPaused()) {
            drawPauseOverlay(g);
        }
    }

    public void renderTransition(Graphics g, BufferedImage transitionImage) {
        if (!model.isInTransition() || transitionImage == null) {
            return;
        }

        float transitionScale = model.getTransitionScale();

        int scaledWidth = (int) (gameWidth * transitionScale * 1.5f);
        int scaledHeight = (int) (gameHeight * transitionScale * 1.5f);

        int x = (int) (model.getPlayer().getHitbox().x - (scaledWidth / 2.0f));
        int y = (int) (model.getPlayer().getHitbox().y - (scaledHeight / 2.0f));

        g.drawImage(transitionImage, x, y, scaledWidth, scaledHeight, null);
    }

    private void drawHUD(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(10, 10, 200, 60, 10, 10);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Level: " + (model.getLevelManager().getCurrentLevelIndex() + 1), 20, 35);
        g.drawString("Deaths: " + model.getPlayer().getDeathCount(), 20, 55);
    }

    private void drawPauseOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, gameWidth, gameHeight);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String text = "PAUSED";
        FontMetrics fm = g.getFontMetrics();
        int x = (gameWidth - fm.stringWidth(text)) / 2;
        int y = (gameHeight - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, x, y);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String hint = "Press P to resume game";
        fm = g.getFontMetrics();
        int hx = (gameWidth - fm.stringWidth(hint)) / 2;
        int hy = y + 40;
        g.drawString(hint, hx, hy);
    }
}
