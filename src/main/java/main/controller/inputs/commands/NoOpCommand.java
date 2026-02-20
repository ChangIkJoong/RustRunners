package main.controller.inputs.commands;

public class NoOpCommand implements Command {
    @Override
    public void execute() {
        // Intentionally empty.
    }
}
