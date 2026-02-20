package main.controller.facades;

import main.controller.Game;

public interface IGameRead {
    Game.GameState getGameState();

    boolean isEditingPlayerName();
}
