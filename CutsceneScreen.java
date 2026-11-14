package com.realityshift.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.realityshift.game.input.CommandSink;
import com.realityshift.game.input.GameCommand;
import java.io.FileNotFoundException;

/** Plays an optional WebM cutscene, or shows a timed story card when no video has been added. */
public class CutsceneScreen extends ScreenAdapter implements CommandSink {
    private final Main game;
    private final CutsceneData data;
    private final Runnable next;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final FitViewport viewport = new FitViewport(960,540,camera);
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture pixel;
    private VideoPlayer video;
    private boolean videoLoaded, done;
    private float fallbackTime;

    public CutsceneScreen(Main game, CutsceneData data, Runnable next) { this.game=game; this.data=data; this.next=next; }

    @Override public void show() {
        batch=new SpriteBatch(); font=new BitmapFont(); font.getData().setScale(1.3f); pixel=PixelArt.blank();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override public boolean keyDown(int keycode) {
                if (keycode==Input.Keys.ENTER || keycode==Input.Keys.SPACE || keycode==Input.Keys.ESCAPE) { finish(); return true; }
                return false;
            }
            @Override public boolean touchDown(int x,int y,int pointer,int button) { finish(); return true; }
        });
        try {
            if (Gdx.files.internal(data.videoPath).exists()) {
                video=VideoPlayerCreator.createVideoPlayer();
                if (video!=null) { videoLoaded=video.load(Gdx.files.internal(data.videoPath)); if(videoLoaded) video.play(); }
            }
        } catch (FileNotFoundException e) { Gdx.app.log("Cutscene", "Video not found; showing story card: " + data.videoPath); }
        catch (RuntimeException e) { Gdx.app.error("Cutscene", "Video could not be loaded; showing story card.", e); }
        catch (LinkageError e) { Gdx.app.error("Cutscene", "Video native library is unavailable; showing story card.", e); }
    }

    @Override public void render(float delta) {
        if(done)return;
        float dt=Math.min(delta,.05f); fallbackTime+=dt;
        if(videoLoaded && video!=null) {
            video.update();
            if(!video.isPlaying() && video.getCurrentTimestamp()>0) { finish(); return; }
        } else if(fallbackTime>=12f) { finish(); return; }
        ScreenUtils.clear(.015f,.025f,.04f,1); viewport.apply(); camera.position.set(480,270,0); camera.update(); batch.setProjectionMatrix(camera.combined); batch.begin();
        if(videoLoaded && video!=null && video.isBuffered() && video.getTexture()!=null) {
            Texture frame=video.getTexture(); int videoWidth=video.getVideoWidth(),videoHeight=video.getVideoHeight();
            TextureRegion region=new TextureRegion(frame,0,0,videoWidth,videoHeight);
            float scale=Math.min(960f/videoWidth,540f/videoHeight);
            float w=videoWidth*scale,h=videoHeight*scale; batch.draw(region,(960-w)/2,(540-h)/2,w,h);
        } else {
            batch.setColor(.04f,.10f,.17f,1); batch.draw(pixel,0,0,960,540);
            batch.setColor(.12f,.29f,.37f,1);
            for(int i=0;i<8;i++) batch.draw(pixel,50+i*125,70+(i%3)*23,75,160+(i%2)*55);
            batch.setColor(Color.WHITE);
            font.setColor(Color.valueOf("#f2bd58")); font.draw(batch,data.title,64,450);
            font.setColor(Color.WHITE); drawWrapped(data.narration,64,370,830);
            font.setColor(Color.LIGHT_GRAY); font.draw(batch,"Story preview (add video at " + data.videoPath + ")",64,78);
            font.draw(batch,"Press Enter / Space to continue  •  auto continues in " + Math.max(0,(int)Math.ceil(12-fallbackTime)),64,42);
        }
        batch.end();
    }

    private void drawWrapped(String text,float x,float y,float width) {
        String[] words=text.split(" "); String line="";
        for(String word:words) {
            String candidate=line.isEmpty()?word:line+" "+word;
            if(candidate.length()>62) { font.draw(batch,line,x,y,width,com.badlogic.gdx.utils.Align.left,true); y-=38; line=word; }
            else line=candidate;
        }
        if(!line.isEmpty()) font.draw(batch,line,x,y,width,com.badlogic.gdx.utils.Align.left,true);
    }

    @Override public void submit(GameCommand command,boolean pressed) { if(pressed) Gdx.app.postRunnable(this::finish); }
    private void finish() { if(done)return; done=true; next.run(); }
    @Override public void resize(int width,int height){viewport.update(width,height,true);}
    @Override public void dispose(){if(video!=null){video.stop();video.dispose();}if(batch!=null)batch.dispose();if(font!=null)font.dispose();if(pixel!=null)pixel.dispose();}
}
