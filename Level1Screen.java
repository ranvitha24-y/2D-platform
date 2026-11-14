package com.realityshift.game;

/** Named Level 1 entry point; all five worlds share the reusable AdventureScreen rules. */
public final class Level1Screen extends AdventureScreen {
    public Level1Screen(Main game, GameProgress progress) {
        super(game, progress, StoryLevel.JUNGLE);
    }
}
