package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.I18NBundle;
import com.mygdx.screens.MainMenuScreen;
import com.mygdx.screens.PlayScreen;
import com.mygdx.utils.Assets;
import com.mygdx.utils.LanguageManager;

import java.util.Locale;

public class Game extends com.badlogic.gdx.Game {

	public SpriteBatch batch;
	public BitmapFont bitmapFont;

	public Preferences preferences;

	public LanguageManager languageManager;

	@Override
	public void create() {
		languageManager = new LanguageManager();

		FileHandle englishFileHandle = Gdx.files.internal("language/strings_en_GB");

		FileHandle spanishFileHandle = Gdx.files.internal("language/strings_es_ES");

		languageManager.loadLanguage("English", englishFileHandle, Locale.UK);
		languageManager.loadLanguage("Spanish", spanishFileHandle,  new Locale("es", "ES"));

		languageManager.setCurrentLanguage("english");

		preferences = Gdx.app.getPreferences(Game.class.getName());
		Assets.getInstance().init(new AssetManager());
		batch = new SpriteBatch();
		bitmapFont = Assets.getInstance().font;
		this.setScreen(new MainMenuScreen(this));
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
