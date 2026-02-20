package main.controller.inputs.commands;

import main.controller.facades.IGameActions;

public class LeaderboardPreviousLevelCommand implements Command {

    private final IGameActions actions;

    public LeaderboardPreviousLevelCommand(IGameActions actions) {
        this.actions = actions;
    }

    @Override
    public void execute() {
        actions.leaderboardPreviousLevel();
    }
}
