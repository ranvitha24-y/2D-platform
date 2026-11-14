package com.realityshift.game;

/** Progress shared across the five worlds. */
public class GameProgress {
    public int completedLevels;
    public boolean fireCrystal, waterCrystal, earthCrystal, windCrystal, lamp, emblem, astralCrystal, nullRealmMap;
    public boolean hasCrystal(Element element) {
        switch (element) {
            case FIRE: return fireCrystal; case WATER: return waterCrystal;
            case EARTH: return earthCrystal; case WIND: return windCrystal; default: return false;
        }
    }
    public void collect(Element element) {
        switch (element) {
            case FIRE: fireCrystal=true; break; case WATER: waterCrystal=true; break;
            case EARTH: earthCrystal=true; break; case WIND: windCrystal=true; break; default: break;
        }
    }
}
