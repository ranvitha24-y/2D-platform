package com.realityshift.game;

import com.badlogic.gdx.math.Rectangle;
import com.realityshift.game.animation.AnimationState;

/** A simple patrol enemy shared by all worlds. */
public class LevelEnemy {
    public final Rectangle bounds;
    public final float leftLimit,rightLimit;
    public final String name;
    private final float startX,startSpeed;
    public float speed;
    public int health=2;
    public boolean alive=true;
    public AnimationState animationState=AnimationState.ENEMY_IDLE;
    public float animationTime;
    private float reactionTime;
    public LevelEnemy(String name,float x,float y,float left,float right,float speed){
        this.name=name;bounds=new Rectangle(x,y,36,44);leftLimit=left;rightLimit=right;this.speed=speed;startX=x;startSpeed=speed;
    }
    public void update(float dt){
        if(!alive){animationState=AnimationState.ENEMY_DEFEAT;return;}
        animationTime+=dt;
        if(reactionTime>0){reactionTime-=dt;if(reactionTime<=0)animationState=AnimationState.ENEMY_MOVE;}
        else animationState=AnimationState.ENEMY_MOVE;
        bounds.x+=speed*dt;
        if(bounds.x<leftLimit){bounds.x=leftLimit;speed=Math.abs(speed);}
        if(bounds.x>rightLimit){bounds.x=rightLimit;speed=-Math.abs(speed);}
    }
    public void hit(){if(alive){animationState=AnimationState.ENEMY_DAMAGE;reactionTime=.2f;animationTime=0;if(--health<=0){alive=false;animationState=AnimationState.ENEMY_DEFEAT;}}}
    public void reset(){bounds.x=startX;speed=startSpeed;health=2;alive=true;animationState=AnimationState.ENEMY_IDLE;animationTime=0;reactionTime=0;}
}
