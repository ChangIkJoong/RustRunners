package main.controller.inputs.commands;

import main.controller.facades.IGameActions;

public class PlayJumpSoundCommand implements Command {

    private final IGameActions actions;

    public PlayJumpSoundCommand(IGameActions actions) {
        this.actions = actions;
    }

    @Override
    public void execute() {
        actions.playJumpSound();
    }
}
