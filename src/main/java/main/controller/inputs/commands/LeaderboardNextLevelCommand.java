package main.controller.inputs.commands;

import main.controller.facades.IGameActions;

public class LeaderboardNextLevelCommand implements Command {

    private final IGameActions actions;

    public LeaderboardNextLevelCommand(IGameActions actions) {
        this.actions = actions;
    }

    @Override
    public void execute() {
        actions.leaderboardNextLevel();
    }
}
