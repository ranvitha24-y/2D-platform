package com.realityshift.game.input;

/** @deprecated Use {@link GameCommand}; kept for compatibility with the earlier adapter API. */
@Deprecated
public enum GameAction {
    MOVE_LEFT, MOVE_RIGHT, JUMP, ATTACK,
    SELECT_FIRE, SELECT_WATER, SELECT_EARTH, SELECT_WIND,
    INTERACT, PAUSE
}
