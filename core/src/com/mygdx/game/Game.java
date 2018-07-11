package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.screens.LoadingScreen;
import com.mygdx.utils.ScreenTransition;
import com.mygdx.utils.ScreenTransitionSlice;

public class Game extends DirectedGame {

	public SpriteBatch batch;

	public AssetManager assetManager;

	@Override
	public void create() {
		assetManager = new AssetManager();
		batch = new SpriteBatch();

		this.setScreen(new LoadingScreen(this));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f,
				1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}


}
