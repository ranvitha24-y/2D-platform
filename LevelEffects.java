package com.realityshift.game.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.realityshift.game.StoryLevel;

/** Small procedural environmental motion while final art/effects are being authored. */
public final class LevelEffects {
    private final StoryLevel level;
    private final float[] particleX = new float[30];
    private final float[] particleY = new float[30];
    private final float[] phase = new float[30];
    private float time;

    public LevelEffects(StoryLevel level) {
        this.level = level;
        for (int i = 0; i < phase.length; i++) phase[i] = i * .618f;
    }

    public void update(float delta) { time += delta; }

    /** Draws intentionally simple fallback motion using the shared 1x1 pixel texture. */
    public void draw(SpriteBatch batch, Texture pixel, float left, float right) {
        Color old = batch.getColor().cpy();
        switch (level) {
            case JUNGLE:
                // Waterfall ribbons and drifting spores.
                for (int i = 0; i < 5; i++) {
                    float x = left + 130 + i * 185;
                    batch.setColor(.48f, .83f, .87f, .35f + .2f * MathUtils.sin(time * 3 + i));
                    batch.draw(pixel, x, 150 + MathUtils.sin(time * 2 + i) * 3, 5, 68 + i % 3 * 12);
                }
                drawDust(batch, pixel, left, right, new Color(.76f, 1f, .68f, .65f), 20, 160);
                break;
            case AGRABAH:
                drawDust(batch, pixel, left, right, new Color(1f, .79f, .38f, .5f), 27, 120);
                break;
            case ACADEMY:
                drawDust(batch, pixel, left, right, new Color(.73f, .69f, 1f, .68f), 18, 280);
                for (int i = 0; i < 7; i++) {
                    float x = left + 85 + i * 145;
                    float y = 350 + MathUtils.sin(time * 1.5f + i) * 8;
                    batch.setColor(.94f, .78f, .45f, .75f);
                    batch.draw(pixel, x, y, 4, 10);
                }
                break;
            case REAL_WORLD:
                float flicker = .25f + .55f * Math.abs(MathUtils.sin(time * 4.2f));
                batch.setColor(.42f, .88f, 1f, flicker);
                for (int i = 0; i < 5; i++) batch.draw(pixel, left + 150 + i * 190, 215 + (i % 2) * 35, 3, 30);
                drawDust(batch, pixel, left, right, new Color(.64f, .91f, 1f, .45f), 12, 360);
                break;
            case NULL_REALM:
                drawDust(batch, pixel, left, right, new Color(.84f, .49f, 1f, .7f), 26, 390);
                batch.setColor(.48f, .19f, .75f, .3f);
                for (int i = 0; i < 4; i++) {
                    float x = left + 120 + i * 235;
                    batch.draw(pixel, x, 270 + MathUtils.sin(time + i) * 15, 38, 5);
                }
                break;
        }
        batch.setColor(old);
    }

    private void drawDust(SpriteBatch batch, Texture pixel, float left, float right, Color tint, int count, float height) {
        batch.setColor(tint);
        for (int i = 0; i < count; i++) {
            float span = Math.max(1, right - left);
            float x = left + positiveMod(phase[i] * 137 + time * (level == StoryLevel.AGRABAH ? 34 : 9), span);
            float y = 130 + positiveMod(phase[i] * 83 + time * (4 + i % 3), height);
            float size = 2 + i % 3;
            batch.draw(pixel, x, y, size, size);
        }
    }

    private float positiveMod(float value, float divisor) {
        float result = value % divisor;
        return result < 0 ? result + divisor : result;
    }
}
