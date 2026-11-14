package com.realityshift.game;

/** Story and objective data for each playable world. */
public enum StoryLevel {
    JUNGLE(1, "Enchanted Jungle", "Jungle Guardian", "Fire crystal", 4, 4, "Fire parts vines; Water freezes the river; Earth raises ruins; Wind crosses the ravine.",
            "Zarvok shattered the Heart of the Rift. Gather the four elemental crystals and open the temple portal.", "The Guardian falls. The temple portal opens to the Lost Kingdom.", "jungle"),
    AGRABAH(2, "Lost Kingdom of Agrabah", "Shadow Creature", "Silver owl-and-star emblem", 3, 4, "Meet Aamir and Jahmir, cross the market and caves, find the lamp in the Cave of Crescent.",
            "Aamir needs help reaching the Cave of Crescent. Jahmir the Guardian Genie can guide you to the lamp.", "The shadow is defeated. Aamir gives you the silver owl-and-star emblem.", "desert"),
    ACADEMY(3, "Academy of Arcane", "Astral Warden", "Astral Crystal", 5, 4, "Meet Professor Elowen, solve the staircase, candle and Forbidden Forest puzzles.",
            "Professor Elowen asks you to recover the stolen Astral Crystal before the academy's wards fail.", "The Astral Crystal reveals that the worlds are bleeding into your real world.", "academy"),
    REAL_WORLD(4, "Real World", "Rift Echo", "Null Realm map", 3, 0, "Investigate three recordings in the city and laboratory to trace Zarvok's energy transfer.",
            "Dimensional glitches have appeared in the city. Follow the recordings to discover where Zarvok sent the Rift energy.", "The recordings reveal the Null Realm and a route through the laboratory portal.", "city"),
    NULL_REALM(5, "Null Realm", "Zarvok", "Restored Heart of the Rift", 4, 0, "Use all four elements, defeat Zarvok and return the crystals to the Heart.",
            "The Null Realm combines fragments of every world. Restore the elemental crystals to the Heart of the Rift.", "The worlds are restored. Zarvok loses his power.", "null");

    public final int number;
    public final String title, boss, objectiveItem, objectives, introduction, completion, theme;
    public final int itemCount, puzzleCount;
    StoryLevel(int number, String title, String boss, String objectiveItem, int itemCount, int puzzleCount, String objectives, String introduction, String completion, String theme) {
        this.number=number; this.title=title; this.boss=boss; this.objectiveItem=objectiveItem;
        this.itemCount=itemCount; this.puzzleCount=puzzleCount;
        this.objectives=objectives; this.introduction=introduction; this.completion=completion; this.theme=theme;
    }
}
