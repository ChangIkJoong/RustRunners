package main.controller.inputs.commands;

import main.controller.facades.IGameActions;

public class MenuNameControlKeyCommand implements Command {

    private final IGameActions actions;
    private final int keyCode;

    public MenuNameControlKeyCommand(IGameActions actions, int keyCode) {
        this.actions = actions;
        this.keyCode = keyCode;
    }

    @Override
    public void execute() {
        actions.menuNameControlKey(keyCode);
    }
}
