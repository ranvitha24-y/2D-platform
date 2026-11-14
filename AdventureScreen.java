package com.realityshift.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.realityshift.game.input.CommandSink;
import com.realityshift.game.input.GameCommand;
import com.realityshift.game.animation.AnimationEvent;
import com.realityshift.game.animation.AnimationState;
import com.realityshift.game.animation.CharacterId;
import com.realityshift.game.animation.LevelEffects;

/** Reusable 2D platform adventure screen. StoryLevel supplies each world's own tasks and theme. */
public class AdventureScreen extends ScreenAdapter implements CommandSink {
    private static final float VIEW_W=960,VIEW_H=540,WORLD_W=2600,GROUND=118,SPEED=245,GRAVITY=1450;
    private final Main game;
    private final GameProgress progress;
    private final StoryLevel level;
    private final Player player=new Player();
    private final boolean[] held=new boolean[GameCommand.values().length];
    private final Array<Rectangle> platforms=new Array<Rectangle>();
    private final Array<LevelEnemy> enemies=new Array<LevelEnemy>();
    private final OrthographicCamera camera=new OrthographicCamera();
    private final FitViewport viewport=new FitViewport(VIEW_W,VIEW_H,camera);
    private final GlyphLayout layout=new GlyphLayout();
    private final float[] itemX;
    private final String[] itemNames;
    private final float[] puzzleX;
    private final Element[] puzzleElements;
    private final String[] puzzleNames;
    private final boolean[] itemDone,puzzleDone;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture hero,boss,crystal,portal,ground,canopy,thorn,pixel;
    private Texture[] itemTextures;
    private final LevelEffects levelEffects;
    private Rectangle waterBridge,earthRise;
    private Element element=Element.FIRE;
    private boolean bossDefeated,completed,bossIntroShown;
    private int bossHealth,bossElementMask;
    private float attackCooldown,enemyTimer,toastTimer,worldTime,windBoost;
    private float actionAnimationTimer;
    private AnimationState bossAnimationState=AnimationState.ENEMY_IDLE;
    private float bossAnimationTime,bossReactionTime;
    private String toast="";

    public AdventureScreen(Main game,GameProgress progress,StoryLevel level) {
        this.game=game;this.progress=progress;this.level=level;
        levelEffects=new LevelEffects(level);
        itemX=itemPositions(level);itemNames=itemNames(level);puzzleX=puzzlePositions(level);puzzleElements=puzzleElements(level);puzzleNames=puzzleNames(level);
        itemDone=new boolean[itemX.length];puzzleDone=new boolean[puzzleX.length];bossHealth=level==StoryLevel.NULL_REALM?4:5;
        buildPlatforms();
    }

    private void buildPlatforms(){
        if(level==StoryLevel.JUNGLE){
            platforms.add(new Rectangle(0,70,890,48));platforms.add(new Rectangle(1020,70,490,48));platforms.add(new Rectangle(1650,70,WORLD_W-1650,48));
        }else platforms.add(new Rectangle(0,70,WORLD_W,48));
        for(int i=0;i<9;i++){float x=240+i*260;float y=180+(i%3)*38;platforms.add(new Rectangle(x,y,150,22));}
        String enemyName=level==StoryLevel.JUNGLE?"Riftling":level==StoryLevel.AGRABAH?"Sand Wraith":level==StoryLevel.ACADEMY?"Cursed Book":level==StoryLevel.REAL_WORLD?"Glitch Echo":"Void Echo";
        enemies.add(new LevelEnemy(enemyName,680,GROUND,630,770,62));
        enemies.add(new LevelEnemy(enemyName,1410,GROUND,1350,1510,-75));
        enemies.add(new LevelEnemy(enemyName,1800,GROUND,1740,1880,55));
    }

    @Override public void show(){
        if(batch==null){batch=new SpriteBatch();font=new BitmapFont();font.getData().setScale(1.05f);
            hero=PixelArt.player();boss=PixelArt.guardian();crystal=PixelArt.crystal();portal=PixelArt.portal();
            ground=PixelArt.jungleTile();canopy=PixelArt.canopy();thorn=PixelArt.thorn();pixel=PixelArt.blank();}
        if(itemTextures==null){itemTextures=new Texture[itemX.length];for(int i=0;i<itemTextures.length;i++)itemTextures[i]=PixelArt.storyObject(level,i);}
        Gdx.input.setInputProcessor(new InputAdapter(){
            @Override public boolean keyDown(int key){GameCommand c=keyCommand(key);if(c!=null)submit(c,true);
                if(key==Input.Keys.ENTER||key==Input.Keys.SPACE){submit(GameCommand.CONFIRM,true);return true;}
                if(key==Input.Keys.R){reset();return true;}return c!=null;}
            @Override public boolean keyUp(int key){GameCommand c=keyCommand(key);if(c!=null)submit(c,false);return c!=null;}
        });
        say(level.introduction,4);
    }

    private GameCommand keyCommand(int key){switch(key){
        case Input.Keys.A:case Input.Keys.LEFT:return GameCommand.MOVE_LEFT;
        case Input.Keys.D:case Input.Keys.RIGHT:return GameCommand.MOVE_RIGHT;
        case Input.Keys.W:case Input.Keys.UP:case Input.Keys.SPACE:return GameCommand.JUMP;
        case Input.Keys.J:case Input.Keys.F:return GameCommand.ATTACK;
        case Input.Keys.NUM_1:return GameCommand.SELECT_FIRE;case Input.Keys.NUM_2:return GameCommand.SELECT_WATER;
        case Input.Keys.NUM_3:return GameCommand.SELECT_EARTH;case Input.Keys.NUM_4:return GameCommand.SELECT_WIND;
        case Input.Keys.E:return GameCommand.INTERACT;case Input.Keys.P:case Input.Keys.ESCAPE:return GameCommand.PAUSE;default:return null;
    }}

    @Override public void submit(GameCommand command,boolean pressed){
        if(command==null)return;
        if(command==GameCommand.MOVE_LEFT||command==GameCommand.MOVE_RIGHT){held[command.ordinal()]=pressed;return;}
        if(!pressed)return;
        if(command==GameCommand.CONFIRM){if(completed)game.finishLevel(level);return;}
        if(command==GameCommand.PAUSE){paused=!paused;return;}
        if(paused||completed)return;
        switch(command){
            case JUMP:jump();break;case ATTACK:attack();break;
            case SELECT_FIRE:element=Element.FIRE;say("Fire selected",1);break;
            case SELECT_WATER:element=Element.WATER;say("Water selected",1);break;
            case SELECT_EARTH:element=Element.EARTH;say("Earth selected",1);break;
            case SELECT_WIND:element=Element.WIND;say("Wind selected",1);break;
            case INTERACT:interact();break;default:break;
        }
    }
    private boolean paused;

    private void jump(){if(player.grounded){player.velocityY=element==Element.WIND?690:590;player.grounded=false;animate(AnimationState.JUMP,.24f,AnimationEvent.PLAYER_JUMP);}}
    private void attack(){
        if(attackCooldown>0)return;attackCooldown=.38f;
        animate(elementAnimation(element),.34f,element==Element.FIRE||element==Element.WATER||element==Element.EARTH||element==Element.WIND?AnimationEvent.PLAYER_POWER:AnimationEvent.PLAYER_ATTACK);
        game.emitAnimationEvent(AnimationEvent.PLAYER_ATTACK,level,player.bounds.x,player.bounds.y);
        int puzzle=nearPuzzle(105);
        if(puzzle>=0){
            if(element!=puzzleElements[puzzle]){say(puzzleNames[puzzle]+" needs "+puzzleElements[puzzle]+".",2);return;}
            puzzleDone[puzzle]=true;
            game.emitAnimationEvent(AnimationEvent.PUZZLE_SOLVED,level,puzzleX[puzzle],GROUND);
            if(level==StoryLevel.JUNGLE&&puzzle==1){waterBridge=new Rectangle(890,70,130,48);platforms.add(waterBridge);}
            if(level==StoryLevel.JUNGLE&&puzzle==2){earthRise=new Rectangle(1215,210,170,20);platforms.add(earthRise);}
            if(element==Element.WIND){windBoost=1.2f;if(player.grounded)jump();}
            say(puzzleNames[puzzle]+" solved with "+element+".",2.3f);return;
        }
        if(element==Element.EARTH)player.invulnerability=Math.max(player.invulnerability,1.8f);
        if(element==Element.WIND&&player.grounded)jump();
        LevelEnemy target=nearestEnemy(attackRange(element));
            if(target!=null&&facing(target.bounds.x)){
            target.hit();say(target.alive?element+" hits the "+target.name+".":"The "+target.name+" is defeated.",1.6f);return;
        }
        boolean facingBoss=player.facingRight?player.bounds.x<bossX():player.bounds.x>bossX();
        float range=element==Element.FIRE?360:element==Element.WATER?320:element==Element.EARTH?120:160;
        if(!bossDefeated&&objectivesReady()&&facingBoss&&Math.abs(player.bounds.x-bossX())<range){
            if(level==StoryLevel.NULL_REALM){
                int bit=1<<element.ordinal();
                if((bossElementMask&bit)!=0){say("Zarvok resists repeated "+element+". Switch elements!",1.8f);return;}
                bossElementMask|=bit;
            }
            bossHealth-=element==Element.FIRE||element==Element.EARTH?2:1;
            bossAnimationState=AnimationState.ENEMY_DAMAGE;bossAnimationTime=0;bossReactionTime=.22f;
            if(bossHealth<=0){bossDefeated=true;onBossDefeated();}
            else say(level.boss+" is weakened by "+element+".",1.5f);
        }else if(element==Element.EARTH)say("Earth raises a temporary barrier.",1.4f);
        else if(element==Element.WIND)say("Wind propels your jump.",1.4f);
        else if(element==Element.WATER)say("Water surges forward.",1.4f);
        else say("A fire bolt flashes forward.",1.4f);
    }
    private void onBossDefeated(){
        if(level==StoryLevel.AGRABAH){progress.lamp=true;progress.emblem=true;say("The shadow falls. Aamir awards the silver owl-and-star emblem.",3);}
        else if(level==StoryLevel.ACADEMY){progress.astralCrystal=true;say("The Astral Crystal reveals that your real world is next in danger.",3);}
        else if(level==StoryLevel.REAL_WORLD){progress.nullRealmMap=true;say("The recordings reveal Zarvok's transfer route to the Null Realm.",3);}
        else if(level==StoryLevel.NULL_REALM){progress.fireCrystal=progress.waterCrystal=progress.earthCrystal=progress.windCrystal=true;animate(AnimationState.VICTORY,3,null);game.emitAnimationEvent(AnimationEvent.PLAYER_VICTORY,level,player.bounds.x,player.bounds.y);game.emitAnimationEvent(AnimationEvent.HEART_RESTORED,level,player.bounds.x,player.bounds.y);say("The Heart is restored. Zarvok loses his power.",3);}
        else say("The Jungle Guardian is defeated. The temple portal is open.",3);
        bossAnimationState=AnimationState.ENEMY_DEFEAT;bossAnimationTime=0;
        game.emitAnimationEvent(AnimationEvent.BOSS_DEFEATED,level,bossX(),GROUND);
    }
    private void interact(){
        animate(AnimationState.INTERACT,.45f,AnimationEvent.PLAYER_INTERACT);
        int item=nearItem(92);
        if(item>=0){itemDone[item]=true;onItemCollected(item);return;}
        if(nearBoss(110)&&!bossDefeated){say(level.boss+" blocks the way. Solve the world objectives, then attack.",2.8f);return;}
        if(Math.abs(player.bounds.x-PORTAL_X)<100){
            if(!objectivesReady())say("The portal is sealed. Complete the world objectives first.",2.2f);
            else if(!bossDefeated)say("Defeat "+level.boss+" to open the portal.",2.2f);
            else{completed=true;game.emitAnimationEvent(AnimationEvent.PORTAL_OPENED,level,PORTAL_X,GROUND);if(level==StoryLevel.NULL_REALM)say("THE END... OR THE BEGINNING OF ANOTHER REALITY.",6);else say("Press Enter or say continue to travel through the portal.",3);}
            return;
        }
        if(level==StoryLevel.REAL_WORLD&&itemDone[0]&&itemDone[1]&&itemDone[2])say("The lab console traces energy into the Null Realm.",2.5f);
        else say("Explore the area and interact with the highlighted story objects.",2);
    }
    private void onItemCollected(int i){
        game.emitAnimationEvent(AnimationEvent.ITEM_COLLECTED,level,itemX[i],GROUND);
        if(level==StoryLevel.JUNGLE){progress.collect(Element.values()[i]);say(Element.values()[i]+" crystal collected.",2);}
        else if(level==StoryLevel.AGRABAH){String[] lines={"Aamir asks you to find the lamp.","Jahmir the Guardian Genie marks the Cave of Crescent.","The magical lamp is yours. Face the shadow creature."};say(lines[i],2.8f);}
        else if(level==StoryLevel.ACADEMY){String[] lines={"Professor Elowen explains the failing wards.","A floating-book cipher is solved.","The potion lab mechanism is activated.","The Forbidden Forest path is revealed.","You found the stolen Astral Crystal."};say(lines[i],2.5f);}
        else if(level==StoryLevel.REAL_WORLD){say("Recording "+(i+1)+" recovered. Check the laboratory evidence.",2.5f);if(i==2)progress.nullRealmMap=true;}
        else say(Element.values()[i]+" crystal secured for the Heart.",2);
    }
    private boolean objectivesReady(){return count(itemDone)>=level.itemCount&&count(puzzleDone)>=level.puzzleCount;}
    private int nearItem(float distance){for(int i=0;i<itemX.length;i++)if(!itemDone[i]&&Math.abs(player.bounds.x-itemX[i])<distance)return i;return -1;}
    private int nearPuzzle(float distance){for(int i=0;i<puzzleX.length;i++)if(!puzzleDone[i]&&Math.abs(player.bounds.x-puzzleX[i])<distance)return i;return -1;}
    private boolean nearBoss(float distance){return Math.abs(player.bounds.x-bossX())<distance;}
    private float attackRange(Element e){return e==Element.FIRE?360:e==Element.WATER?320:e==Element.EARTH?120:160;}
    private boolean facing(float x){return player.facingRight?player.bounds.x<x:player.bounds.x>x;}
    private LevelEnemy nearestEnemy(float range){LevelEnemy result=null;float distance=range;for(LevelEnemy e:enemies)if(e.alive&&facing(e.bounds.x)&&Math.abs(player.bounds.x-e.bounds.x)<distance){result=e;distance=Math.abs(player.bounds.x-e.bounds.x);}return result;}
    private float bossX(){return 2070;}
    private static int count(boolean[] values){int n=0;for(boolean v:values)if(v)n++;return n;}
    private void say(String message,float time){toast=message;toastTimer=time;}

    private AnimationState elementAnimation(Element selected){
        switch(selected){case FIRE:return AnimationState.FIRE;case WATER:return AnimationState.WATER;
            case EARTH:return AnimationState.EARTH;default:return AnimationState.WIND;}
    }
    private void animate(AnimationState state,float duration,AnimationEvent event){
        player.play(state);actionAnimationTimer=duration;
        if(event!=null)game.emitAnimationEvent(event,level,player.bounds.x,player.bounds.y);
    }

    private void reset(){player.reset();for(int i=0;i<held.length;i++)held[i]=false;for(LevelEnemy enemy:enemies)enemy.reset();if(waterBridge!=null){platforms.removeValue(waterBridge,true);waterBridge=null;}if(earthRise!=null){platforms.removeValue(earthRise,true);earthRise=null;}paused=false;completed=false;bossDefeated=false;bossHealth=level==StoryLevel.NULL_REALM?4:5;bossElementMask=0;bossAnimationState=AnimationState.ENEMY_IDLE;bossAnimationTime=bossReactionTime=0;enemyTimer=0;actionAnimationTimer=0;for(int i=0;i<itemDone.length;i++)itemDone[i]=false;for(int i=0;i<puzzleDone.length;i++)puzzleDone[i]=false;}

    @Override public void render(float delta){
        float dt=Math.min(delta,.04f);if(!paused&&!completed)update(dt);
        Color sky=skyColor();ScreenUtils.clear(sky.r,sky.g,sky.b,1);
        camera.position.set(MathUtils.clamp(player.bounds.x,VIEW_W/2,WORLD_W-VIEW_W/2),VIEW_H/2,0);viewport.apply();camera.update();batch.setProjectionMatrix(camera.combined);batch.begin();
        drawWorld();drawHud();batch.end();
    }
    private void update(float dt){
        worldTime+=dt;levelEffects.update(dt);player.animationTime+=dt;bossAnimationTime+=dt;
        if(bossReactionTime>0){bossReactionTime-=dt;if(bossReactionTime<=0&&!bossDefeated)bossAnimationState=AnimationState.ENEMY_IDLE;}
        float direction=(held[GameCommand.MOVE_RIGHT.ordinal()]?1:0)-(held[GameCommand.MOVE_LEFT.ordinal()]?1:0);
        if(direction!=0){player.facingRight=direction>0;moveHorizontal(direction*(SPEED+(windBoost>0?110:0))*dt);}
        player.velocityY-=GRAVITY*dt;float oldY=player.bounds.y;boolean wasGrounded=player.grounded;player.bounds.y+=player.velocityY*dt;player.grounded=false;
        for(Rectangle p:platforms)if(player.bounds.overlaps(p)){
            if(player.velocityY<=0&&oldY>=p.y+p.height-2){player.bounds.y=p.y+p.height;player.velocityY=0;player.grounded=true;}
            else if(player.velocityY>0&&oldY+Player.HEIGHT<=p.y+2){player.bounds.y=p.y-Player.HEIGHT;player.velocityY=0;}
        }
        if(!wasGrounded&&player.grounded){animate(AnimationState.LAND,.13f,AnimationEvent.PLAYER_LAND);}
        if(player.bounds.y<-90){player.reset();say("You fell. Return to the start and try the obstacle again.",2.8f);}
        if(actionAnimationTimer>0){actionAnimationTimer=Math.max(0,actionAnimationTimer-dt);}
        if(actionAnimationTimer<=0){
            if(!player.grounded)player.play(player.velocityY>0?AnimationState.JUMP:AnimationState.FALL);
            else if(direction!=0)player.play(windBoost>0?AnimationState.RUN:AnimationState.WALK);
            else player.play(AnimationState.IDLE);
        }
        if(player.invulnerability>0)player.invulnerability-=dt;attackCooldown=Math.max(0,attackCooldown-dt);toastTimer=Math.max(0,toastTimer-dt);windBoost=Math.max(0,windBoost-dt);
        for(LevelEnemy enemy:enemies){enemy.update(dt);if(enemy.alive&&player.invulnerability<=0&&player.bounds.overlaps(enemy.bounds)){player.health--;player.invulnerability=1.1f;animate(AnimationState.DAMAGE,.3f,AnimationEvent.PLAYER_DAMAGE);say("A "+enemy.name+" struck you.",1.5f);if(player.health<=0){player.reset();say("You were defeated and returned to the entrance.",2.6f);}}}
        if(!bossDefeated&&objectivesReady()&&nearBoss(95)){
            if(!bossIntroShown){bossIntroShown=true;game.emitAnimationEvent(AnimationEvent.BOSS_INTRO,level,bossX(),GROUND);game.showOverlayCutscene(CutsceneData.bossIntro(level),this);return;}
            enemyTimer-=dt;if(enemyTimer<=0){enemyTimer=1.15f;bossAnimationState=AnimationState.ENEMY_ATTACK;bossAnimationTime=0;bossReactionTime=.35f;if(player.invulnerability<=0){player.health--;player.invulnerability=1.1f;animate(AnimationState.DAMAGE,.3f,AnimationEvent.PLAYER_DAMAGE);say(level.boss+" struck you! Use an elemental power.",1.6f);}if(player.health<=0){player.reset();say("You were defeated and returned to the entrance.",2.6f);}}
        }
    }
    private void moveHorizontal(float dx){
        float next=player.bounds.x+dx;
        for(int i=0;i<puzzleX.length;i++)if(!puzzleDone[i]&&player.bounds.y<220&&overlap(next,puzzleX[i]-23,46))return;
        for(Rectangle p:platforms)if(p.y<=GROUND&&p.height>30&&player.bounds.y<p.y+p.height&&player.bounds.y+Player.HEIGHT>p.y&&overlap(next,p.x,p.width))return;
        player.bounds.x=MathUtils.clamp(next,0,WORLD_W-Player.WIDTH);
    }
    private boolean overlap(float x,float ox,float w){return x<ox+w&&x+Player.WIDTH>ox;}

    private void drawWorld(){
        float left=camera.position.x-VIEW_W/2-100;
        levelEffects.draw(batch,pixel,left,left+VIEW_W+220);
        for(int i=0;i<13;i++){batch.setColor(level==StoryLevel.JUNGLE?Color.valueOf("#ffffff"):themeTint());batch.draw(canopy,left+i*100,270+(i%3)*23,150,120);}
        batch.setColor(Color.WHITE);
        // City and academy silhouettes in the far background.
        if(level==StoryLevel.REAL_WORLD||level==StoryLevel.ACADEMY||level==StoryLevel.AGRABAH){
            Color building=level==StoryLevel.AGRABAH?Color.valueOf("#9b6338"):level==StoryLevel.ACADEMY?Color.valueOf("#554f80"):Color.valueOf("#546779");batch.setColor(building);
            for(int i=0;i<10;i++){float x=left+i*210;batch.draw(pixel,x,118,110,180+(i%4)*30);batch.setColor(Color.valueOf("#f3ca70"));for(int w=0;w<3;w++)batch.draw(pixel,x+12+w*29,155+(i%3)*15,9,18);batch.setColor(building);}
        }
        float start=Math.max(0,(float)Math.floor((camera.position.x-VIEW_W/2)/64)*64),end=Math.min(WORLD_W,camera.position.x+VIEW_W/2+64);
        Color terrain=terrainTint();batch.setColor(terrain);
        for(Rectangle p:platforms){for(float x=Math.max(start,p.x);x<Math.min(end,p.x+p.width);x+=64)batch.draw(ground,x,p.y,64,p.height);}
        // Elemental barriers and level-specific obstacle art.
        for(int i=0;i<puzzleX.length;i++)if(!puzzleDone[i]){
            float x=puzzleX[i];if(Math.abs(x-camera.position.x)<VIEW_W/2+100){float sway=MathUtils.sin(worldTime*2.2f+i)*3;batch.setColor(elementColor(puzzleElements[i]));batch.draw(thorn,x-25+sway,GROUND,50,96);batch.setColor(Color.WHITE);}
        }
        // Story objects and collectibles.
        for(int i=0;i<itemX.length;i++)if(!itemDone[i]){
            Color itemTint=level==StoryLevel.JUNGLE?elementColor(Element.values()[i]):level==StoryLevel.NULL_REALM?elementColor(Element.values()[i]):Color.valueOf("#f3d678");
            Texture itemSprite=itemTextures[i];
            if(level==StoryLevel.JUNGLE||level==StoryLevel.NULL_REALM||level==StoryLevel.ACADEMY&&i>=3){batch.setColor(itemTint);}else batch.setColor(Color.WHITE);
            float iw=level==StoryLevel.AGRABAH&&i<2?46:34,ih=level==StoryLevel.AGRABAH&&i<2?52:38;
            float itemY=128+MathUtils.sin(worldTime*2+i)*4;CharacterId storyCharacter=storyCharacter(i);
            TextureRegion actorFrame=storyCharacter==null?null:game.getCharacterAnimations().frame(storyCharacter,AnimationState.IDLE,worldTime);
            if(actorFrame!=null)batch.draw(actorFrame,itemX[i]-iw/2,itemY,iw,ih);else batch.draw(itemSprite,itemX[i]-iw/2,itemY,iw,ih);batch.setColor(Color.WHITE);
        }
        for(LevelEnemy enemy:enemies)if(enemy.alive){batch.setColor(themeTint());TextureRegion enemyFrame=game.getCharacterAnimations().frame(enemyCharacter(),enemy.animationState,enemy.animationTime);if(enemyFrame==null)enemyFrame=game.getCharacterAnimations().frame(CharacterId.LEVEL_ENEMY,enemy.animationState,enemy.animationTime);if(enemyFrame!=null)batch.draw(enemyFrame,enemy.bounds.x,enemy.bounds.y,36,44);else batch.draw(boss,enemy.bounds.x,enemy.bounds.y,36,44);batch.setColor(Color.WHITE);}
        if(!bossDefeated){batch.setColor(level==StoryLevel.NULL_REALM?Color.valueOf("#261a32"):Color.valueOf("#ffffff"));TextureRegion bossFrame=game.getCharacterAnimations().frame(bossCharacter(),bossAnimationState,bossAnimationTime);if(bossFrame!=null)batch.draw(bossFrame,bossX(),GROUND,56,60);else batch.draw(boss,bossX(),GROUND,56,60);batch.setColor(Color.DARK_GRAY);batch.draw(pixel,bossX()-4,345,64,9);batch.setColor(Color.RED);batch.draw(pixel,bossX()-2,347,60*bossHealth/(level==StoryLevel.NULL_REALM?4f:5f),5);batch.setColor(Color.WHITE);}
        float portalGlow=.84f+.16f*MathUtils.sin(worldTime*2.4f);batch.setColor(1,portalGlow,.78f+portalGlow*.22f,1);batch.draw(portal,PORTAL_X-38,GROUND,76,136);batch.setColor(Color.WHITE);
        if(player.invulnerability<=0||(int)(worldTime*18)%2==0){float bob=player.animationState==AnimationState.IDLE?MathUtils.sin(worldTime*4)*2:0;TextureRegion frame=game.getCharacterAnimations().frame(CharacterId.PLAYER,player.animationState,player.animationTime);batch.setColor(Color.WHITE);if(frame!=null){float drawX=player.facingRight?player.bounds.x:player.bounds.x+Player.WIDTH;batch.draw(frame,drawX,player.bounds.y+bob,0,0,Player.WIDTH,Player.HEIGHT,player.facingRight?1:-1,1,0);}else batch.draw(hero,player.facingRight?player.bounds.x:player.bounds.x+Player.WIDTH,player.bounds.y+bob,player.facingRight?Player.WIDTH:-Player.WIDTH,Player.HEIGHT);batch.setColor(elementColor(element));batch.draw(pixel,player.bounds.x+7,player.bounds.y+31,20,5);batch.setColor(Color.WHITE);}
        if(attackCooldown>.22f){batch.setColor(elementColor(element));float fx=player.facingRight?player.bounds.x+Player.WIDTH:player.bounds.x-38;batch.draw(pixel,fx,player.bounds.y+25,36,20);batch.setColor(Color.WHITE);}
    }
    private static final float PORTAL_X=2480;

    private CharacterId bossCharacter(){switch(level){case JUNGLE:return CharacterId.JUNGLE_GUARDIAN;case AGRABAH:case ACADEMY:return CharacterId.SHADOW_CREATURE;case NULL_REALM:return CharacterId.ZARVOK;default:return CharacterId.LEVEL_BOSS;}}
    private CharacterId enemyCharacter(){switch(level){case AGRABAH:return CharacterId.SHADOW_CREATURE;case ACADEMY:return CharacterId.SHADOW_CREATURE;default:return CharacterId.LEVEL_ENEMY;}}
    private CharacterId storyCharacter(int item){if(level==StoryLevel.AGRABAH&&item==0)return CharacterId.AAMIR;if(level==StoryLevel.AGRABAH&&item==1)return CharacterId.JAHMIR;if(level==StoryLevel.ACADEMY&&item==0)return CharacterId.PROFESSOR_ELOWEN;return null;}

    private void drawHud(){
        float oldX=camera.position.x,oldY=camera.position.y;camera.position.set(VIEW_W/2,VIEW_H/2,0);camera.update();batch.setProjectionMatrix(camera.combined);
        batch.setColor(0,0,0,.7f);batch.draw(pixel,0,450,VIEW_W,90);batch.setColor(Color.WHITE);
        font.setColor(Color.WHITE);font.draw(batch,"LEVEL "+level.number+" / 5  •  "+level.title,20,522);
        font.setColor(elementColor(element));font.draw(batch,"POWER "+element+"  [1 FIRE  2 WATER  3 EARTH  4 WIND]",20,495);
        font.setColor(Color.WHITE);font.draw(batch,"HEALTH",660,522);for(int i=0;i<5;i++){batch.setColor(i<player.health?Color.valueOf("#e65b54"):Color.valueOf("#51464e"));batch.draw(pixel,728+i*24,510,18,12);}batch.setColor(Color.WHITE);
        font.setColor(Color.LIGHT_GRAY);font.draw(batch,"A/D move  •  Space jump  •  J attack  •  E interact  •  P pause",20,23);
        centered("OBJECTIVE: "+level.objectives,56,Color.WHITE);
        font.setColor(Color.valueOf("#f2d783"));font.draw(batch,level.objectiveItem+": "+count(itemDone)+" / "+level.itemCount,20,470);
        if(level.puzzleCount>0){font.setColor(Color.valueOf("#9de8dd"));font.draw(batch,"PUZZLES: "+count(puzzleDone)+" / "+level.puzzleCount,360,470);}
        if(toastTimer>0&&!paused&&!completed)centered(toast,90,Color.WHITE);
        if(paused){overlay();centered("PAUSED",300,Color.WHITE);centered("Press P or Escape to continue",245,Color.LIGHT_GRAY);}
        if(completed){overlay();centered(level.number==5?"THE HEART OF THE RIFT IS RESTORED":"PORTAL OPEN",320,Color.valueOf("#f2bd58"));centered(level.completion,265,Color.WHITE);centered("Press Enter or say continue for the next chapter",205,Color.LIGHT_GRAY);}
        camera.position.set(oldX,oldY,0);camera.update();batch.setProjectionMatrix(camera.combined);
    }
    private void overlay(){batch.setColor(0,0,0,.82f);batch.draw(pixel,100,145,760,250);batch.setColor(Color.WHITE);}
    private void centered(String text,float y,Color color){layout.setText(font,text);font.setColor(color);font.draw(batch,text,(VIEW_W-layout.width)/2,y);}
    private Color skyColor(){switch(level){case JUNGLE:return Color.valueOf("#4a8792");case AGRABAH:return Color.valueOf("#d8954d");case ACADEMY:return Color.valueOf("#615a87");case REAL_WORLD:return Color.valueOf("#68849b");default:return Color.valueOf("#171322");}}
    private Color terrainTint(){switch(level){case JUNGLE:return Color.WHITE;case AGRABAH:return Color.valueOf("#d9a85e");case ACADEMY:return Color.valueOf("#8a7fbd");case REAL_WORLD:return Color.valueOf("#788b9b");default:return Color.valueOf("#51465f");}}
    private Color themeTint(){switch(level){case JUNGLE:return Color.WHITE;case AGRABAH:return Color.valueOf("#8a7655");case ACADEMY:return Color.valueOf("#7c70a7");case REAL_WORLD:return Color.valueOf("#657883");default:return Color.valueOf("#41364f");}}
    private Color elementColor(Element e){switch(e){case FIRE:return Color.valueOf("#ff633d");case WATER:return Color.valueOf("#48c8ef");case EARTH:return Color.valueOf("#bc9250");default:return Color.valueOf("#8ce8c4");}}
    private Color elementColor(){return elementColor(element);}

    private static float[] itemPositions(StoryLevel l){switch(l){case JUNGLE:return new float[]{300,740,1190,1570};case AGRABAH:return new float[]{330,680,1180};case ACADEMY:return new float[]{330,640,930,1270,1660};case REAL_WORLD:return new float[]{410,990,1580};default:return new float[]{330,700,1080,1460};}}
    private static String[] itemNames(StoryLevel l){switch(l){case AGRABAH:return new String[]{"Aamir","Jahmir the Genie","Cave of Crescent lamp"};case ACADEMY:return new String[]{"Professor Elowen","floating-book cipher","potion mechanism","Forbidden Forest clue","Astral Crystal"};case REAL_WORLD:return new String[]{"recording one","recording two","laboratory recording"};default:return new String[]{"Fire","Water","Earth","Wind"};}}
    private static float[] puzzlePositions(StoryLevel l){switch(l){case JUNGLE:return new float[]{480,850,1230,1460};case AGRABAH:return new float[]{500,880,1370,1690};case ACADEMY:return new float[]{470,790,1110,1410};default:return new float[0];}}
    private static Element[] puzzleElements(StoryLevel l){switch(l){case JUNGLE:return new Element[]{Element.FIRE,Element.WATER,Element.EARTH,Element.WIND};case AGRABAH:return new Element[]{Element.FIRE,Element.WATER,Element.EARTH,Element.WIND};case ACADEMY:return new Element[]{Element.WIND,Element.FIRE,Element.WATER,Element.EARTH};default:return new Element[0];}}
    private static String[] puzzleNames(StoryLevel l){switch(l){case JUNGLE:return new String[]{"Thorn vines","Frozen river","Ruined bridge","Ravine gust"};case AGRABAH:return new String[]{"Market brazier","Oasis channel","Cave rubble","Sandstorm pass"};case ACADEMY:return new String[]{"Moving staircase","Floating books","Potion lab","Forbidden Forest ward"};default:return new String[0];}}
    @Override public void resize(int width,int height){viewport.update(width,height,true);}
    @Override public void dispose(){dispose(hero);dispose(boss);dispose(crystal);dispose(portal);dispose(ground);dispose(canopy);dispose(thorn);dispose(pixel);if(itemTextures!=null)for(Texture t:itemTextures)dispose(t);if(batch!=null)batch.dispose();if(font!=null)font.dispose();}
    private void dispose(Texture t){if(t!=null)t.dispose();}
}
