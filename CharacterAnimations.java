package com.realityshift.game.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Animation registry for artist-provided sprite frames. Until a state is registered,
 * gameplay displays the generated fallback texture supplied by the game.
 * The caller owns and disposes textures registered here.
 */
public final class CharacterAnimations {
    private final ObjectMap<CharacterId, ObjectMap<AnimationState, Animation<TextureRegion>>> characters =
            new ObjectMap<CharacterId, ObjectMap<AnimationState, Animation<TextureRegion>>>();
    /** Registers an animation created from the teammate's sprite-sheet frames. */
    public void register(CharacterId character, AnimationState state, Animation<TextureRegion> animation) {
        if (character == null || state == null || animation == null) throw new IllegalArgumentException("Character, state, and animation are required.");
        ObjectMap<AnimationState, Animation<TextureRegion>> animations = characters.get(character);
        if (animations == null) { animations = new ObjectMap<AnimationState, Animation<TextureRegion>>(); characters.put(character, animations); }
        animations.put(state, animation);
    }

    /** Convenience helper for a horizontal sprite strip with equally sized frames. */
    public void registerStrip(CharacterId character, AnimationState state, Texture sheet, int frameWidth, int frameHeight,
                              float frameDuration, Animation.PlayMode playMode) {
        if (sheet == null || frameWidth <= 0 || frameHeight <= 0
                || sheet.getWidth() < frameWidth || sheet.getHeight() < frameHeight) {
            throw new IllegalArgumentException("Sprite sheet dimensions do not contain the requested frame.");
        }
        TextureRegion[][] split = TextureRegion.split(sheet, frameWidth, frameHeight);
        TextureRegion[] frames = split[0];
        Animation<TextureRegion> animation = new Animation<TextureRegion>(frameDuration, frames);
        animation.setPlayMode(playMode);
        register(character, state, animation);
    }

    /** Returns the frame for an animation state, or the temporary fallback art. */
    public TextureRegion frame(CharacterId character, AnimationState state, float stateTime) {
        ObjectMap<AnimationState, Animation<TextureRegion>> animations = characters.get(character);
        if (animations == null) return null;
        Animation<TextureRegion> animation = animations.get(state);
        if (animation == null) return null;
        TextureRegion frame = animation.getKeyFrame(stateTime);
        return frame;
    }

    public boolean hasAnimation(CharacterId character, AnimationState state) {
        ObjectMap<AnimationState, Animation<TextureRegion>> animations = characters.get(character);
        return animations != null && animations.containsKey(state);
    }
}
