package com.realityshift.game.input;

import java.util.Locale;
import com.badlogic.gdx.utils.Timer;

/** Maps recognized speech text to game actions. Recognition is supplied by the host app. */
public final class VoiceCommandAdapter {
    private final CommandSink sink;
    private Timer.Task movementRelease;
    private GameCommand movingCommand;

    public VoiceCommandAdapter(CommandSink sink) { this.sink = sink; }

    /** Accepts phrases such as "move left", "jump", "fire", "attack", and "pause". */
    public boolean accept(String phrase) {
        if (phrase == null) return false;
        String command = phrase.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        GameCommand action;
        if (matches(command, "left", "move left", "go left")) action = GameCommand.MOVE_LEFT;
        else if (matches(command, "right", "move right", "go right")) action = GameCommand.MOVE_RIGHT;
        else if (matches(command, "jump", "leap")) action = GameCommand.JUMP;
        else if (matches(command, "attack", "strike", "use power")) action = GameCommand.ATTACK;
        else if (matches(command, "fire", "select fire")) action = GameCommand.SELECT_FIRE;
        else if (matches(command, "water", "select water")) action = GameCommand.SELECT_WATER;
        else if (matches(command, "earth", "select earth")) action = GameCommand.SELECT_EARTH;
        else if (matches(command, "wind", "select wind")) action = GameCommand.SELECT_WIND;
        else if (matches(command, "interact", "talk", "use", "open")) action = GameCommand.INTERACT;
        else if (matches(command, "pause", "resume", "unpause")) action = GameCommand.PAUSE;
        else if (matches(command, "continue", "confirm", "begin", "start")) action = GameCommand.CONFIRM;
        else return false;
        if (action == GameCommand.MOVE_LEFT || action == GameCommand.MOVE_RIGHT) {
            stopMovement();
            movingCommand = action;
            sink.submit(action, true);
            final GameCommand movement = action;
            movementRelease = Timer.schedule(new Timer.Task() {
                @Override public void run() {
                    sink.submit(movement, false);
                    if (movingCommand == movement) movingCommand = null;
                    movementRelease = null;
                }
            }, .4f);
        } else {
            stopMovement();
            sink.submit(action, true);
        }
        return true;
    }

    private void stopMovement() {
        if (movementRelease != null) {
            movementRelease.cancel(); movementRelease = null;
        }
        if (movingCommand != null) { sink.submit(movingCommand, false); movingCommand = null; }
    }

    private static boolean matches(String command, String... choices) {
        for (String choice : choices) if (command.equals(choice)) return true;
        return false;
    }
}
