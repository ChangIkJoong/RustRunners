package main.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import main.controller.GameController;
import utilities.GameConfig;

public class GamePanel extends JPanel {
    private final GameController controller;

    public GamePanel(GameController controller) {
        this.controller = controller;
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
        Dimension size = new Dimension(GameConfig.GAME_WIDTH, GameConfig.GAME_HEIGHT);
        setPreferredSize(size);
        System.out.println("size:" + GameConfig.GAME_WIDTH + " : " + GameConfig.GAME_HEIGHT);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        controller.render(g);
    }

    public void onWindowFocusLost() {
        controller.onWindowFocusLost();
    }
}
