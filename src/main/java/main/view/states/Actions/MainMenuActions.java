package main.view.states.Actions;

public interface MainMenuActions {
    void onPlay();
    void onOpenLevelSelect();
    void onOpenLeaderboard();
    void onQuit();
    void onSetPlayerName(String name);
}

