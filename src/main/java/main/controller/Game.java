package main.controller;

import main.controller.inputs.KeyboardInputs;
import main.controller.inputs.MouseInputs;
import main.view.GamePanel;
import main.view.GameWindow;

public class Game implements Runnable {

    private static final int FPS_SET = 120;
    private static final int UPS_SET = 200;

    private final GameController controller;
    private final GamePanel gamePanel;
    @SuppressWarnings("unused")
    private final GameWindow gameWindow;
    private final Thread gameThread;

    public Game() {
        this.controller = new GameController();
        this.gamePanel = new GamePanel(controller);

        KeyboardInputs keyboardInputs = new KeyboardInputs(controller);
        MouseInputs mouseInputs = new MouseInputs(controller);
        gamePanel.attachInputListeners(keyboardInputs, mouseInputs, mouseInputs);

        this.gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();

        this.gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double timePerFrame = 1_000_000_000.0 / FPS_SET;
        double timePerUpdate = 1_000_000_000.0 / UPS_SET;
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
                controller.update();
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
}
