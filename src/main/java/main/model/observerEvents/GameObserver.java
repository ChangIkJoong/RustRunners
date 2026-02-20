package main.model.observerEvents;
/**
 * Observer Pattern:
 * https://refactoring.guru/design-patterns/observer
 **/

public interface GameObserver {
    void onPlayerDied();
    void onPlayerRespawn();
    void onLevelCompleted();
    void onLevelLoadRequested();
    void onTransitionComplete();
    void onRunCompleted();
}
