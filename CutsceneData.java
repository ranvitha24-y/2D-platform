package com.realityshift.game;

/** A story beat and its optional desktop video asset. Videos are optional until supplied. */
public class CutsceneData {
    public final String title, narration, videoPath;
    public CutsceneData(String title, String narration, String videoPath) {
        this.title=title; this.narration=narration; this.videoPath=videoPath;
    }
    public static CutsceneData prologue() {
        return new CutsceneData("PROLOGUE: THE HEART OF THE RIFT",
                "The universe once existed in perfect balance. Different worlds were separated and protected by ancient energy sources. An ancient sorcerer named Zarvok believed that separating the worlds was a weakness. He wanted to merge every realm into one universe and control all magic, life and reality. He searched for the Heart of the Rift, the force connecting dimensions. His experiment failed, tearing holes between worlds and scattering fragments of the Heart. Barriers weakened, strange creatures appeared, magic became unstable and guardians lost their powers. A prophecy speaks of a traveller who will discover elemental powers, restore the fragments and uncover the truth. The journey begins in the Enchanted Jungle.",
                "videos/prologue.webm");
    }
    public static CutsceneData intro(StoryLevel level) {
        String story;
        switch (level) {
            case JUNGLE: story = "Enormous trees, waterfalls, ancient rocks and forgotten temples covered in vines surround the traveller. Dangerous plants, collapsing bridges, swinging vines and hidden traps guard an ancient ruin where the four elemental crystals awaken."; break;
            case AGRABAH: story = "The traveller enters a desert kingdom of golden palaces, crowded markets, ruins and dunes. Sandstorms and magical creatures curse Agrabah. A clever young thief named Aamir knows the secret passages leading toward the Cave of the Crescent."; break;
            case ACADEMY: story = "A mountain valley holds a magnificent magical castle, mist, ancient forests and cliffs. Floating candles, enchanted moving books, changing staircases and hidden rooms surround the Academy of the Arcane."; break;
            case REAL_WORLD: story = "The traveller enters a familiar modern world of cities, roads, buildings, laboratories and ordinary people. Streetlights flicker with magic, buildings change shape, objects disappear and reappear, and cracks in the sky reveal other worlds."; break;
            default: story = "Broken fragments of every visited world drift through the Null Realm: jungle trees grow through palace walls, golden desert towers float beside academy structures, modern buildings twist in the sky, and staircases, rivers and temples hang in the void."; break;
        }
        return new CutsceneData("WORLD " + level.number + ": " + level.title.toUpperCase(), story,
                "videos/level" + level.number + "-intro.webm");
    }
    public static CutsceneData bossIntro(StoryLevel level) {
        String story;
        if (level == StoryLevel.NULL_REALM) {
            story = "Zarvok stands at the centre, gathering energy to merge all realms. \"You have travelled through every world to stop me,\" Zarvok declares. \"But you still do not understand. Every world has its own laws, its own limits, its own suffering. I will create one reality where nothing is beyond my control.\"";
        } else if (level == StoryLevel.AGRABAH) {
            story = "A shadow creature guards the magical lamp in the Cave of the Crescent.";
        } else if (level == StoryLevel.ACADEMY) {
            story = "Zarvok's shadow creatures have invaded the Academy and stolen the Astral Crystal. The traveller enters the Forbidden Forest to defeat them and retrieve it.";
        } else if (level == StoryLevel.JUNGLE) {
            story = "The mysterious guardian explains that the crystals protect the boundaries between worlds and that someone has stolen the Heart of the Rift.";
        } else {
            story = "At the original experiment site, a massive gateway opens in the sky and leads into the Null Realm.";
        }
        return new CutsceneData(level.boss.toUpperCase(), story, "videos/level" + level.number + "-boss.webm");
    }
    public static CutsceneData ending() {
        return new CutsceneData("THE WORLDS ARE RESTORED",
                "The four elements combine into a brilliant sphere of light. Corruption disappears. The jungle, Agrabah, academy and real world are restored. Zarvok loses control. The Null Realm dissolves and the Heart closes the gateway. The traveller returns to the real world. The crystals remain, softly glowing. THE END... OR THE BEGINNING OF ANOTHER REALITY.",
                "videos/ending.webm");
    }
}
