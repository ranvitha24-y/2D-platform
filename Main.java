package com.realityshift.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.realityshift.game.input.CommandSink;
import com.realityshift.game.input.GameCommand;
import com.realityshift.game.input.GestureCommandAdapter;
import com.realityshift.game.input.VoiceCommandAdapter;
import com.realityshift.game.animation.AnimationEvent;
import com.realityshift.game.animation.AnimationEventListener;
import com.realityshift.game.animation.CharacterAnimations;

/** Application entry point, story progression, and platform-independent input routing. */
public class Main extends Game implements CommandSink {
    private VoiceCommandAdapter voiceCommands;
    private GestureCommandAdapter gestureCommands;
    private GameProgress progress;
    private AnimationEventListener animationEventListener;
    private final CharacterAnimations characterAnimations=new CharacterAnimations();

    @Override public void create() {
        voiceCommands=new VoiceCommandAdapter(this); gestureCommands=new GestureCommandAdapter(this); progress=new GameProgress();
        showCutscene(CutsceneData.prologue(),()->showCutscene(CutsceneData.intro(StoryLevel.JUNGLE),()->showLevel(0)));
    }
    public VoiceCommandAdapter getVoiceCommandAdapter(){return voiceCommands;}
    public GestureCommandAdapter getGestureCommandAdapter(){return gestureCommands;}
    public GameProgress getProgress(){return progress;}
    public CharacterAnimations getCharacterAnimations(){return characterAnimations;}
    /** Connect teammate-authored animation/effect code without coupling it to gameplay classes. */
    public void setAnimationEventListener(AnimationEventListener listener){animationEventListener=listener;}
    public void emitAnimationEvent(AnimationEvent event,StoryLevel level,float worldX,float worldY){
        if(animationEventListener!=null)animationEventListener.onAnimationEvent(event,level,worldX,worldY);
    }

    public void showLevel(int levelIndex) {
        StoryLevel[] levels=StoryLevel.values();
        if(levelIndex<0 || levelIndex>=levels.length) { showCutscene(CutsceneData.ending(),()->openScreen(new EndingScreen(this))); return; }
        if(levelIndex==0) openScreen(new Level1Screen(this,progress));
        else openScreen(new AdventureScreen(this,progress,levels[levelIndex]));
    }
    public void finishLevel(StoryLevel level) {
        progress.completedLevels=Math.max(progress.completedLevels,level.number);
        if(level.number>=StoryLevel.values().length) { showCutscene(CutsceneData.ending(),()->openScreen(new EndingScreen(this))); return; }
        CutsceneData transition=new CutsceneData("RIFT TRANSITION",level.completion,"videos/level"+level.number+"-transition.webm");
        showCutscene(transition,()->showCutscene(CutsceneData.intro(StoryLevel.values()[level.number]),()->showLevel(level.number)));
    }
    public void showCutscene(CutsceneData data,Runnable next){openScreen(new CutsceneScreen(this,data,next));}
    public void showOverlayCutscene(CutsceneData data,Screen returnTo){
        setScreen(new CutsceneScreen(this,data,()->resumeScreen(returnTo)));
    }
    private void resumeScreen(Screen screen){Screen old=getScreen();if(old!=screen){if(old!=null)old.dispose();setScreen(screen);}}
    public void restartStory(){progress=new GameProgress();showCutscene(CutsceneData.prologue(),()->showCutscene(CutsceneData.intro(StoryLevel.JUNGLE),()->showLevel(0)));}
    public void openScreen(Screen next) {
        Screen old=getScreen(); if(old!=null) old.dispose(); setScreen(next);
    }
    @Override public void submit(GameCommand command,boolean pressed) {
        if(command==null)return;
        Gdx.app.postRunnable(()->{if(getScreen() instanceof CommandSink)((CommandSink)getScreen()).submit(command,pressed);});
    }
}
