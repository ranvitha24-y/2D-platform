package com.realityshift.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/** Small generated pixel-art sprites let Level 1 run before external art is ready. */
public final class PixelArt {
    private PixelArt() { }

    public static Texture player() {
        return sprite(32, 48, new Painter() { public void paint(Pixmap p) {
            rect(p, 10, 2, 8, 15, "#292b42"); rect(p, 21, 2, 8, 15, "#292b42");
            rect(p, 7, 15, 21, 19, "#a94d32"); rect(p, 8, 16, 19, 3, "#e2a54e");
            rect(p, 10, 33, 16, 12, "#bd8057"); rect(p, 8, 43, 19, 3, "#382d37");
            rect(p, 9, 36, 18, 3, "#f3d07a"); rect(p, 22, 38, 4, 2, "#292b42");
        }});
    }

    public static Texture guardian() {
        return sprite(48, 52, new Painter() { public void paint(Pixmap p) {
            rect(p, 8, 2, 9, 15, "#d1a34b"); rect(p, 31, 2, 9, 15, "#d1a34b");
            rect(p, 5, 10, 38, 31, "#67378c"); rect(p, 10, 16, 28, 20, "#8b4fb0");
            rect(p, 12, 28, 7, 5, "#fff0a0"); rect(p, 29, 28, 7, 5, "#fff0a0");
            rect(p, 9, 40, 10, 10, "#39234d"); rect(p, 29, 40, 10, 10, "#39234d");
        }});
    }

    public static Texture crystal() {
        return sprite(28, 38, new Painter() { public void paint(Pixmap p) {
            rect(p, 12, 1, 8, 5, "#9fffff"); rect(p, 7, 6, 18, 6, "#49e7f3");
            rect(p, 4, 12, 23, 15, "#22b9dc"); rect(p, 8, 27, 15, 6, "#49e7f3");
            rect(p, 11, 33, 9, 4, "#b7ffff"); rect(p, 10, 10, 3, 17, "#d5ffff");
        }});
    }

    public static Texture portal() {
        return sprite(76, 136, new Painter() { public void paint(Pixmap p) {
            rect(p, 4, 4, 12, 132, "#d99a38"); rect(p, 60, 4, 12, 132, "#d99a38");
            rect(p, 12, 4, 52, 12, "#f2c45d"); rect(p, 16, 16, 44, 12, "#302a50");
            rect(p, 20, 28, 36, 96, "#17384a"); rect(p, 24, 32, 28, 88, "#207a83");
            rect(p, 28, 40, 20, 72, "#35c4ad"); rect(p, 32, 50, 12, 50, "#a9ffe2");
        }});
    }

    public static Texture jungleTile() {
        return sprite(64, 64, new Painter() { public void paint(Pixmap p) {
            rect(p, 0, 0, 64, 64, "#553a2d"); rect(p, 0, 0, 64, 12, "#4f9b55");
            rect(p, 0, 12, 64, 4, "#347442");
            for (int x = 4; x < 64; x += 16) rect(p, x, 22 + (x % 3) * 5, 5, 4, "#73503a");
            rect(p, 8, 47, 9, 5, "#426747"); rect(p, 39, 31, 11, 5, "#416344");
        }});
    }

    public static Texture canopy() {
        return sprite(96, 80, new Painter() { public void paint(Pixmap p) {
            rect(p, 8, 38, 80, 36, "#214c3a"); rect(p, 0, 28, 44, 30, "#2e6942");
            rect(p, 34, 12, 54, 38, "#377d48"); rect(p, 53, 3, 38, 32, "#438c4e");
            rect(p, 19, 32, 16, 10, "#5ca65c"); rect(p, 61, 13, 12, 8, "#69ad5c");
        }});
    }

    public static Texture thorn() {
        return sprite(64, 100, new Painter() { public void paint(Pixmap p) {
            rect(p, 8, 0, 8, 100, "#704236"); rect(p, 48, 0, 8, 100, "#704236");
            rect(p, 12, 18, 40, 7, "#914b38"); rect(p, 12, 48, 40, 7, "#914b38"); rect(p, 12, 78, 40, 7, "#914b38");
            rect(p, 3, 10, 14, 6, "#499250"); rect(p, 15, 34, 12, 6, "#62aa58");
            rect(p, 38, 62, 15, 6, "#499250"); rect(p, 26, 87, 13, 6, "#62aa58");
        }});
    }

    public static Texture storyObject(final StoryLevel level, final int index) {
        if (level == StoryLevel.AGRABAH && index < 2) return index == 0 ? player() : guardian();
        if (level == StoryLevel.ACADEMY && index == 0) return player();
        if (level == StoryLevel.ACADEMY && index == 1) return book();
        if (level == StoryLevel.ACADEMY && index == 2) return potion();
        if (level == StoryLevel.REAL_WORLD) return recording();
        if (level == StoryLevel.AGRABAH && index == 2) return lamp();
        if (level == StoryLevel.ACADEMY && index == 3) return crystal();
        if (level == StoryLevel.ACADEMY && index == 4) return emblem();
        return crystal();
    }

    public static Texture book() {
        return sprite(38, 34, new Painter() { public void paint(Pixmap p) {
            rect(p,3,4,15,27,"#8b4a37"); rect(p,20,4,15,27,"#9e6842");
            rect(p,7,8,9,19,"#f4dfa7"); rect(p,22,8,9,19,"#fff0c3");
            rect(p,17,5,4,26,"#4b293e"); rect(p,9,13,6,2,"#a77c4d"); rect(p,24,13,6,2,"#a77c4d");
        }});
    }

    public static Texture potion() {
        return sprite(32, 40, new Painter() { public void paint(Pixmap p) {
            rect(p,10,2,12,5,"#c3a16b"); rect(p,8,7,16,6,"#e9d6a2");
            rect(p,5,13,22,23,"#5cd3c4"); rect(p,8,17,16,15,"#d67cba");
            rect(p,12,18,5,7,"#f4b9e4"); rect(p,9,35,14,3,"#9a7cbb");
        }});
    }

    public static Texture lamp() {
        return sprite(44, 36, new Painter() { public void paint(Pixmap p) {
            rect(p,8,15,25,14,"#e7b545"); rect(p,3,18,7,8,"#f3d46a");
            rect(p,28,11,8,7,"#f3d46a"); rect(p,34,7,5,7,"#d08a35");
            rect(p,9,28,24,4,"#b27632"); rect(p,17,10,10,5,"#f9e59a");
            rect(p,14,7,16,3,"#fff2b2");
        }});
    }

    public static Texture recording() {
        return sprite(38, 34, new Painter() { public void paint(Pixmap p) {
            rect(p,3,5,32,24,"#34465c"); rect(p,7,9,24,15,"#79d6dd");
            rect(p,10,12,4,9,"#d8f5e4"); rect(p,17,12,4,9,"#f4cb61"); rect(p,24,12,4,9,"#ef8065");
            rect(p,10,30,18,3,"#9ba8b1");
        }});
    }

    public static Texture emblem() {
        return sprite(36, 38, new Painter() { public void paint(Pixmap p) {
            rect(p,5,4,26,28,"#d6dce7"); rect(p,9,8,18,20,"#75849c");
            rect(p,11,12,5,7,"#f6e18c"); rect(p,20,12,5,7,"#f6e18c");
            rect(p,15,18,7,7,"#f2be58"); rect(p,13,28,10,5,"#bcc8d5");
        }});
    }

    public static Texture blank() { return sprite(2, 2, new Painter() { public void paint(Pixmap p) { rect(p,0,0,2,2,"#ffffff"); }}); }

    private interface Painter { void paint(Pixmap pixmap); }
    private static Texture sprite(int width, int height, Painter painter) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None); painter.paint(pixmap);
        Texture texture = new Texture(pixmap); texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose(); return texture;
    }
    private static void rect(Pixmap p, int x, int y, int width, int height, String hex) {
        p.setColor(Color.valueOf(hex)); p.fillRectangle(x, p.getHeight() - y - height, width, height);
    }
}
