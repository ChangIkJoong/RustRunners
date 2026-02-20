package main.controller.inputs.commands;

import main.controller.facades.IGameActions;

public class MenuNameTypedCommand implements Command {

    private final IGameActions actions;
    private char typedChar;

    public MenuNameTypedCommand(IGameActions actions) {
        this.actions = actions;
    }

    public void setTypedChar(char typedChar) {
        this.typedChar = typedChar;
    }

    @Override
    public void execute() {
        actions.menuNameTyped(typedChar);
    }
}
