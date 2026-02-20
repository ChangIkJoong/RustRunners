package main.controller.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import main.controller.facades.IGameActions;
import main.controller.facades.IGameRead;
import main.controller.inputs.commands.Command;
import main.controller.inputs.commands.GoToMenuCommand;
import main.controller.inputs.commands.JumpPressCommand;
import main.controller.inputs.commands.JumpReleaseCommand;
import main.controller.inputs.commands.MoveLeftPressCommand;
import main.controller.inputs.commands.MoveLeftReleaseCommand;
import main.controller.inputs.commands.MoveRightPressCommand;
import main.controller.inputs.commands.MoveRightReleaseCommand;
import main.controller.inputs.commands.TogglePauseCommand;

public class KeyboardInputs implements KeyListener {

    private final IGameActions actions;
    private final IGameRead read;
    private boolean keyDown = false;

    private final Map<Integer, Command> pressedCommands = new HashMap<>();
    private final Map<Integer, Command> releasedCommands = new HashMap<>();

    public KeyboardInputs(IGameActions actions, IGameRead read) {
        this.actions = actions;
        this.read = read;
        initCommands();
    }

    private void initCommands() {
        // movement
        Command moveLeftPress = new MoveLeftPressCommand(actions);
        Command moveLeftRelease = new MoveLeftReleaseCommand(actions);
        Command moveRightPress = new MoveRightPressCommand(actions);
        Command moveRightRelease = new MoveRightReleaseCommand(actions);

        // jump
        Command jumpPress = new JumpPressCommand(actions);
        Command jumpRelease = new JumpReleaseCommand(actions);

        // control
        Command togglePause = new TogglePauseCommand(actions);
        Command goToMenu = new GoToMenuCommand(actions);

        // map keys to commands (press)
        pressedCommands.put(KeyEvent.VK_A, moveLeftPress);
        pressedCommands.put(KeyEvent.VK_LEFT, moveLeftPress);
        pressedCommands.put(KeyEvent.VK_D, moveRightPress);
        pressedCommands.put(KeyEvent.VK_RIGHT, moveRightPress);

        pressedCommands.put(KeyEvent.VK_SPACE, jumpPress);
        pressedCommands.put(KeyEvent.VK_W, jumpPress);
        pressedCommands.put(KeyEvent.VK_UP, jumpPress);

        pressedCommands.put(KeyEvent.VK_P, togglePause);
        pressedCommands.put(KeyEvent.VK_ESCAPE, goToMenu);

        // map keys to commands (release)
        releasedCommands.put(KeyEvent.VK_A, moveLeftRelease);
        releasedCommands.put(KeyEvent.VK_LEFT, moveLeftRelease);
        releasedCommands.put(KeyEvent.VK_D, moveRightRelease);
        releasedCommands.put(KeyEvent.VK_RIGHT, moveRightRelease);

        releasedCommands.put(KeyEvent.VK_SPACE, jumpRelease);
        releasedCommands.put(KeyEvent.VK_W, jumpRelease);
        releasedCommands.put(KeyEvent.VK_UP, jumpRelease);
    }

    private boolean isEditingName() {
        return read.getGameState() == main.controller.Game.GameState.MENU && read.isEditingPlayerName();
    }

    private boolean isJumpKey(int keyCode) {
        return keyCode == KeyEvent.VK_SPACE ||
               keyCode == KeyEvent.VK_W ||
               keyCode == KeyEvent.VK_UP;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // when editing name in main menu, collect characters here
        if (isEditingName()) {
            actions.menuNameTyped(e.getKeyChar());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isEditingName()) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
                actions.menuNameControlKey(code);
            }
            return;
        }

        int keyCode = e.getKeyCode();

        // LEFT/RIGHT navigation for leaderboarding...
        if (read.getGameState() == main.controller.Game.GameState.LEADERBOARD) {
            if (keyCode == KeyEvent.VK_LEFT) {
                actions.leaderboardPreviousLevel();
                return;
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                actions.leaderboardNextLevel();
                return;
            }
        }

        //TODO, abstract this to the key in the command, or put it into a listener. choices..
        // Play jump sound once per press
        if (isJumpKey(keyCode) && !keyDown) {
            actions.playJumpSound();
            keyDown = true;
        }

        Command cmd = pressedCommands.get(keyCode);
        if (cmd != null) {
            cmd.execute();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (isEditingName()) {
            return;
        }

        int keyCode = e.getKeyCode();
        if (isJumpKey(keyCode)) {
            keyDown = false;
        }

        Command cmd = releasedCommands.get(keyCode);
        if (cmd != null) {
            cmd.execute();
        }
    }
}
