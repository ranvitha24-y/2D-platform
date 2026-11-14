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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.realityshift.game.input.CommandSink;
import com.realityshift.game.input.GameCommand;

/** End card after the final cinematic; confirm starts a new story. */
public class EndingScreen extends ScreenAdapter implements CommandSink {
    private final Main game;
    private final OrthographicCamera camera=new OrthographicCamera();
    private final FitViewport viewport=new FitViewport(960,540,camera);
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture pixel;
    private final GlyphLayout layout=new GlyphLayout();
    public EndingScreen(Main game){this.game=game;}
    @Override public void show(){
        batch=new SpriteBatch();font=new BitmapFont();font.getData().setScale(1.5f);pixel=PixelArt.blank();
        Gdx.input.setInputProcessor(new InputAdapter(){@Override public boolean keyDown(int key){if(key==Input.Keys.ENTER||key==Input.Keys.SPACE){game.restartStory();return true;}return false;}});
    }
    @Override public void render(float delta){
        ScreenUtils.clear(.035f,.025f,.07f,1);viewport.apply();camera.position.set(480,270,0);camera.update();batch.setProjectionMatrix(camera.combined);batch.begin();
        batch.setColor(.10f,.24f,.31f,1);for(int i=0;i<8;i++)batch.draw(pixel,30+i*130,70+(i%3)*24,75,140+(i%2)*50);batch.setColor(Color.WHITE);
        font.setColor(Color.valueOf("#f2bd58"));draw("THE WORLDS ARE RESTORED",330);
        font.setColor(Color.WHITE);draw("Zarvok's power is gone. The Heart of the Rift shines again.",260);
        font.setColor(Color.valueOf("#9de8dd"));draw("THE END... OR THE BEGINNING OF ANOTHER REALITY.",200);
        font.setColor(Color.LIGHT_GRAY);draw("Press Enter to begin again",115);batch.end();
    }
    private void draw(String text,float y){layout.setText(font,text);font.draw(batch,text,(960-layout.width)/2f,y);}
    @Override public void submit(GameCommand command,boolean pressed){if(pressed)Gdx.app.postRunnable(game::restartStory);}
    @Override public void resize(int width,int height){viewport.update(width,height,true);}
    @Override public void dispose(){if(batch!=null)batch.dispose();if(font!=null)font.dispose();if(pixel!=null)pixel.dispose();}
}
