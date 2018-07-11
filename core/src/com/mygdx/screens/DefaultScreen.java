package com.mygdx.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.DirectedGame;
import com.mygdx.game.Game;
import com.mygdx.utils.Assets;

public abstract class DefaultScreen implements Screen{

    protected Game game;

    public DefaultScreen(Game game){
        this.game = game;
    }

    @Override
    public abstract void render(float dt);
    public abstract void resize (int width, int height);
    public abstract void show ();
    public abstract void hide ();
    public abstract void pause ();

    @Override
    public void resume() { Assets.getInstance().init(game.assetManager); }

    @Override
    public void dispose() {
        Assets.getInstance().dispose();
    }

    public abstract InputProcessor getInputProcessor();

}
