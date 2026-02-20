package main.controller.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.controller.facades.IGameActions;
import main.controller.inputs.commands.Command;
import main.controller.inputs.commands.GoToMenuCommand;
import main.controller.inputs.commands.JumpPressCommand;
import main.controller.inputs.commands.JumpReleaseCommand;
import main.controller.inputs.commands.LeaderboardNextLevelCommand;
import main.controller.inputs.commands.LeaderboardPreviousLevelCommand;
import main.controller.inputs.commands.MenuNameControlKeyCommand;
import main.controller.inputs.commands.MenuNameTypedCommand;
import main.controller.inputs.commands.MoveLeftPressCommand;
import main.controller.inputs.commands.MoveLeftReleaseCommand;
import main.controller.inputs.commands.MoveRightPressCommand;
import main.controller.inputs.commands.MoveRightReleaseCommand;
import main.controller.inputs.commands.NoOpCommand;
import main.controller.inputs.commands.PlayJumpSoundCommand;
import main.controller.inputs.commands.TogglePauseCommand;

public class KeyboardInputs implements KeyListener {

    private final MenuNameTypedCommand menuNameTypedCommand;

    private final Map<Integer, List<Command>> pressedCommands = new HashMap<>();
    private final Map<Integer, List<Command>> releasedCommands = new HashMap<>();
    private final Map<Integer, Command> typedCommands = new HashMap<>();

    public KeyboardInputs(IGameActions actions) {
        this.menuNameTypedCommand = new MenuNameTypedCommand(actions);
        initCommands(actions);
    }

    private void initCommands(IGameActions actions) {
        Command moveLeftPress = new MoveLeftPressCommand(actions);
        Command moveLeftRelease = new MoveLeftReleaseCommand(actions);
        Command moveRightPress = new MoveRightPressCommand(actions);
        Command moveRightRelease = new MoveRightReleaseCommand(actions);

        Command jumpPress = new JumpPressCommand(actions);
        Command jumpRelease = new JumpReleaseCommand(actions);
        Command playJumpSound = new PlayJumpSoundCommand(actions);

        Command togglePause = new TogglePauseCommand(actions);
        Command goToMenu = new GoToMenuCommand(actions);
        Command leaderboardPrevious = new LeaderboardPreviousLevelCommand(actions);
        Command leaderboardNext = new LeaderboardNextLevelCommand(actions);

        Command menuNameEnter = new MenuNameControlKeyCommand(actions, KeyEvent.VK_ENTER);
        Command menuNameEscape = new MenuNameControlKeyCommand(actions, KeyEvent.VK_ESCAPE);
        Command menuNameBackspace = new MenuNameControlKeyCommand(actions, KeyEvent.VK_BACK_SPACE);
        Command noOp = new NoOpCommand();

        bindPressed(KeyEvent.VK_A, moveLeftPress);
        bindPressed(KeyEvent.VK_LEFT, moveLeftPress);
        bindPressed(KeyEvent.VK_LEFT, leaderboardPrevious);
        bindPressed(KeyEvent.VK_D, moveRightPress);
        bindPressed(KeyEvent.VK_RIGHT, moveRightPress);
        bindPressed(KeyEvent.VK_RIGHT, leaderboardNext);

        bindPressed(KeyEvent.VK_SPACE, playJumpSound);
        bindPressed(KeyEvent.VK_SPACE, jumpPress);
        bindPressed(KeyEvent.VK_W, playJumpSound);
        bindPressed(KeyEvent.VK_W, jumpPress);
        bindPressed(KeyEvent.VK_UP, playJumpSound);
        bindPressed(KeyEvent.VK_UP, jumpPress);

        bindPressed(KeyEvent.VK_P, togglePause);
        bindPressed(KeyEvent.VK_ESCAPE, goToMenu);
        bindPressed(KeyEvent.VK_ESCAPE, menuNameEscape);
        bindPressed(KeyEvent.VK_ENTER, menuNameEnter);
        bindPressed(KeyEvent.VK_BACK_SPACE, menuNameBackspace);

        bindReleased(KeyEvent.VK_A, moveLeftRelease);
        bindReleased(KeyEvent.VK_LEFT, moveLeftRelease);
        bindReleased(KeyEvent.VK_D, moveRightRelease);
        bindReleased(KeyEvent.VK_RIGHT, moveRightRelease);

        bindReleased(KeyEvent.VK_SPACE, jumpRelease);
        bindReleased(KeyEvent.VK_W, jumpRelease);
        bindReleased(KeyEvent.VK_UP, jumpRelease);

        bindTyped('\b', noOp);
        bindTyped('\n', noOp);
        bindTyped('\r', noOp);
        bindTyped(27, noOp);
    }

    private void bindPressed(int keyCode, Command command) {
        List<Command> commands = pressedCommands.get(keyCode);
        if (commands == null) {
            commands = new ArrayList<>();
            pressedCommands.put(keyCode, commands);
        }
        commands.add(command);
    }

    private void bindReleased(int keyCode, Command command) {
        List<Command> commands = releasedCommands.get(keyCode);
        if (commands == null) {
            commands = new ArrayList<>();
            releasedCommands.put(keyCode, commands);
        }
        commands.add(command);
    }

    private void bindTyped(int typedChar, Command command) {
        typedCommands.put(typedChar, command);
    }

    @Override
    public void keyTyped(KeyEvent event) {
        executeTyped(event);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        executePressed(event);
    }

    @Override
    public void keyReleased(KeyEvent event) {
        executeReleased(event);
    }

    private void executePressed(KeyEvent event) {
        executeCommands(pressedCommands.get(event.getKeyCode()));
    }

    private void executeReleased(KeyEvent event) {
        executeCommands(releasedCommands.get(event.getKeyCode()));
    }

    private void executeTyped(KeyEvent event) {
        int typedChar = event.getKeyChar();
        Command command = typedCommands.get(typedChar);
        if (command == null) {
            menuNameTypedCommand.setTypedChar(event.getKeyChar());
            command = menuNameTypedCommand;
        }
        executeCommand(command);
    }

    private void executeCommand(Command command) {
        if (command != null) {
            command.execute();
        }
    }

    private void executeCommands(List<Command> commands) {
        if (commands == null) {
            return;
        }

        for (Command command : commands) {
            command.execute();
        }
    }
}
