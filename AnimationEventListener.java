package com.realityshift.game.animation;

import com.realityshift.game.StoryLevel;

/** Optional bridge to external animation/effects code. No asset or recognition code is required. */
public interface AnimationEventListener {
    void onAnimationEvent(AnimationEvent event, StoryLevel level, float worldX, float worldY);
}
