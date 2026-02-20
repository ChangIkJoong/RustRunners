package main.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import main.controller.Game;

import static main.controller.Game.GAME_HEIGHT;
import static main.controller.Game.GAME_WIDTH;

public class GamePanel extends JPanel {
    private final Game game;

    public GamePanel(Game game) {
        this.game = game;
        setPanelSize();
    }

    public void attachInputListeners(
            KeyListener keyListener, MouseListener mouseListener, MouseMotionListener mouseMotionListener) {
        if (keyListener != null) {
            addKeyListener(keyListener);
        }
        if (mouseListener != null) {
            addMouseListener(mouseListener);
        }
        if (mouseMotionListener != null) {
            addMouseMotionListener(mouseMotionListener);
        }
    }

    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setPreferredSize(size);
        System.out.println("size:" + GAME_WIDTH + " : " + GAME_HEIGHT);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        game.render(g);
    }

    public void onWindowFocusLost() {
        game.windowFocusLost();
    }
}
