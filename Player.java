package com.realityshift.game;

import com.badlogic.gdx.math.Rectangle;
import com.realityshift.game.animation.AnimationState;

/** Player position, movement state, and collision bounds. */
public class Player {
    public static final float WIDTH = 34f, HEIGHT = 52f;
    public final Rectangle bounds = new Rectangle(90, 118, WIDTH, HEIGHT);
    public float velocityX, velocityY;
    public boolean grounded = true;
    public boolean facingRight = true;
    public int health = 5;
    public float invulnerability;
    /** Current animation state and elapsed time, consumed by CharacterAnimations. */
    public AnimationState animationState = AnimationState.IDLE;
    public float animationTime;

    public void play(AnimationState state) {
        if (state != animationState) {
            animationState = state;
            animationTime = 0;
        }
    }

    public void reset() {
        bounds.setPosition(90, 118); velocityX = velocityY = 0;
        grounded = true; facingRight = true; health = 5; invulnerability = 0;
        animationState = AnimationState.IDLE; animationTime = 0;
    }
}
