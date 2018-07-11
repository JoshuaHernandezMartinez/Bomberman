package com.mygdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;

public class ScreenTransitionSlide implements ScreenTransition {

    public static final int LEFT =  1;
    public static final int RIGHT =  2;
    public static final int UP =  3;
    public static final int DOWN =  4;

    private static final ScreenTransitionSlide instance = new ScreenTransitionSlide();

    private float duration;
    private int direction;
    private boolean slideOut;
    private Interpolation easing;

    public static ScreenTransitionSlide init(float duration, int direction, boolean slideOut, Interpolation easing){
        instance.duration = duration;
        instance.direction = direction;
        instance.slideOut = slideOut;
        instance.easing = easing;
        return instance;
    }

    @Override
    public float getDuration() {
        return duration;
    }

    @Override
    public void render(SpriteBatch batch, Texture currentScreen, Texture nextScreen, float alpha) {
        float w = currentScreen.getWidth();
        float h = currentScreen.getHeight();
        float x = 0;
        float y = 0;
        if(easing != null) alpha = easing.apply(alpha);

        switch (direction){
            case LEFT:
                x = - w * alpha;
                if(!slideOut) x += w;
                break;
            case RIGHT:
                x = w * alpha;
                if(!slideOut) x -= w;
                break;
            case UP:
                y = h * alpha;
                if(!slideOut) y -= h;
                break;
            case DOWN:
                y = -h * alpha;
                if(!slideOut) y += h;
                break;
        }

        Texture texBottom = slideOut ? nextScreen: currentScreen;
        Texture texTop = slideOut ? currentScreen: nextScreen;

        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        batch.draw(texBottom, 0, 0, 0, 0, w, h, 1, 1, 0, 0, 0,
                currentScreen.getWidth(), currentScreen.getHeight(),
                false, true);

        batch.draw(texTop, x, y, 0, 0, w, h, 1, 1, 0, 0, 0,
                nextScreen.getWidth(), nextScreen.getHeight(),
                false, true);
        batch.end();


    }
}
