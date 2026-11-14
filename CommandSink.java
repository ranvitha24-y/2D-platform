package com.realityshift.game.input;

/** Receives normalized game actions from any input source. */
public interface CommandSink {
    void submit(GameCommand command, boolean pressed);
}
