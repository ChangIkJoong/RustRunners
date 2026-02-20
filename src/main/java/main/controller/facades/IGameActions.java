package main.controller.facades;

public interface IGameActions {
    void moveLeftPressed();

    void moveLeftReleased();

    void moveRightPressed();

    void moveRightReleased();

    void jumpPressed();

    void jumpReleased();

    void togglePause();

    void goToMenu();

    void playJumpSound();

    void leaderboardNextLevel();

    void leaderboardPreviousLevel();

    void menuNameTyped(char c);

    void menuNameControlKey(int keyCode);

    void mouseMoved(int x, int y);

    void mousePressed(int x, int y);

    void mouseReleased(int x, int y);
}
