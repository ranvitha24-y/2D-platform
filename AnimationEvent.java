package com.realityshift.game.animation;

/** Gameplay moments that can trigger teammate-authored character or visual effects. */
public enum AnimationEvent {
    PLAYER_JUMP, PLAYER_LAND, PLAYER_ATTACK, PLAYER_DAMAGE, PLAYER_POWER,
    PLAYER_INTERACT, PLAYER_VICTORY, ITEM_COLLECTED, PUZZLE_SOLVED,
    BOSS_INTRO, BOSS_DEFEATED, PORTAL_OPENED, HEART_RESTORED
}
