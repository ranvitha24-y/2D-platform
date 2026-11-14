package com.realityshift.game.input;

/** Integration point for an external gesture recognizer (for example, an OpenCV module). */
public final class GestureCommandAdapter {
    private final CommandSink sink;

    public GestureCommandAdapter(CommandSink sink) { this.sink = sink; }

    /** Call when a gesture begins or ends; no image processing or recognition is done here. */
    public void onGesture(GameCommand command, boolean active) {
        if (command != null) sink.submit(command, active);
    }

    /** Compatibility overload for code written against the first prototype. */
    @Deprecated public void onGesture(GameAction action, boolean active) {
        if (action != null) sink.submit(GameCommand.valueOf(action.name()), active);
    }
}
