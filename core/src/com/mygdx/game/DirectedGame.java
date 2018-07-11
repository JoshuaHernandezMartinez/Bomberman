package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.mygdx.screens.DefaultScreen;
import com.mygdx.utils.Constants;
import com.mygdx.utils.ScreenTransition;

public abstract class DirectedGame implements ApplicationListener{

    private boolean init;
    private DefaultScreen currentScreen;
    private DefaultScreen nextScreen;
    private FrameBuffer currentBuffer;
    private FrameBuffer nextBuffer;
    private SpriteBatch batch;
    private float t;
    private ScreenTransition screenTransition;

    public void setScreen(DefaultScreen screen){
        setScreen(screen, null);
    }

    public void setScreen(DefaultScreen screen, ScreenTransition screenTransition){
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        if(!init){
            currentBuffer = new FrameBuffer(Pixmap.Format.RGB888, w, h, false);
            nextBuffer = new FrameBuffer(Pixmap.Format.RGB888, w, h, false);
            batch = new SpriteBatch();
            init = true;
        }

        nextScreen = screen;
        nextScreen.show();
        nextScreen.resize(w, h);
        if(currentScreen != null) currentScreen.pause();
        nextScreen.pause();
        Gdx.input.setInputProcessor(null);
        this.screenTransition = screenTransition;
        t = 0;
    }

    @Override
    public void render() {

        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60.0f);

        if(nextScreen == null){
            if(currentScreen != null) currentScreen.render(deltaTime);
        }else{
            float duration = 0;
            if(screenTransition != null) duration = screenTransition.getDuration();
            t = Math.min(t + deltaTime, duration);
            if(screenTransition == null || t >= duration){
                if(currentScreen != null) currentScreen.hide();
                nextScreen.resume();
                Gdx.input.setInputProcessor(nextScreen.getInputProcessor());
                currentScreen = nextScreen;
                currentScreen.render(0);
                nextScreen = null;
                screenTransition = null;

            }else{
                currentBuffer.begin();
                if(currentScreen != null) currentScreen.render(deltaTime);
                currentBuffer.end();
                nextBuffer.begin();
                nextScreen.render(deltaTime);
                nextBuffer.end();
                float alpha = t / duration;

                screenTransition.render(batch, currentBuffer.getColorBufferTexture(),
                        nextBuffer.getColorBufferTexture(), alpha);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        if(currentScreen != null)
            currentScreen.resize(width, height);
        if(nextScreen != null)
            nextScreen.resize(width, height);
    }

    @Override
    public void pause() {
        if(currentScreen != null) currentScreen.pause();
    }

    @Override
    public void resume() {
        if(currentScreen != null) currentScreen.resume();
    }

    @Override
    public void dispose() {
        if(currentScreen != null) currentScreen.hide();
        if(nextScreen != null) nextScreen.hide();

        if(init){
            currentBuffer.dispose();
            currentScreen = null;
            nextBuffer.dispose();
            nextScreen = null;
            batch.dispose();
            init = false;
        }
    }
}
