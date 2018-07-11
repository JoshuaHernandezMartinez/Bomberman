package com.mygdx.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Texture;

public class Assets implements Disposable, AssetErrorListener{
    private static final Assets ourInstance = new Assets();

    public static Assets getInstance() {
        return ourInstance;
    }

    private Assets() {

    }

    // Assets

    public TextureAtlas bomberman_atlas;

    public BitmapFont font;

    public Texture title_background;

    public Skin libgdxSkin;

    public TextureAtlas map_minies;

    // sounds

    public Sound number_3;
    public Sound number_2;
    public Sound number_1;
    public Sound go;
    public Sound power_up;
    public Sound congratulations;
    public Sound game_over;
    public Sound you_win;
    public Sound you_lose;

    public void init(AssetManager assetManager){

        assetManager.setErrorListener(this);

        assetManager.load(Constants.bomberman, TextureAtlas.class);

        assetManager.load(Constants.font, BitmapFont.class);

        assetManager.load(Constants.title_background, Texture.class);

        assetManager.load(Constants.number_3, Sound.class);
        assetManager.load(Constants.number_2, Sound.class);
        assetManager.load(Constants.number_1, Sound.class);
        assetManager.load(Constants.go, Sound.class);
        assetManager.load(Constants.power_up, Sound.class);
        assetManager.load(Constants.congratulations, Sound.class);
        assetManager.load(Constants.game_over, Sound.class);
        assetManager.load(Constants.you_win, Sound.class);
        assetManager.load(Constants.yow_lose, Sound.class);
        assetManager.load(Constants.libgdxSkinJson, Skin.class, new SkinLoader.SkinParameter(Constants.libgdxSkinAtlas));
        assetManager.load(Constants.map_minies, TextureAtlas.class);

    }

    public void initializeStuff(AssetManager assetManager){
        bomberman_atlas = assetManager.get(Constants.bomberman, TextureAtlas.class);

        font = assetManager.get(Constants.font, BitmapFont.class);

        title_background = assetManager.get(Constants.title_background, Texture.class);

        // sounds

        number_3 = assetManager.get(Constants.number_3, Sound.class);
        number_2 = assetManager.get(Constants.number_2, Sound.class);
        number_1 = assetManager.get(Constants.number_1, Sound.class);
        go = assetManager.get(Constants.go, Sound.class);
        power_up = assetManager.get(Constants.power_up, Sound.class);
        congratulations = assetManager.get(Constants.congratulations, Sound.class);
        game_over = assetManager.get(Constants.game_over, Sound.class);
        you_lose = assetManager.get(Constants.yow_lose, Sound.class);
        you_win = assetManager.get(Constants.you_win, Sound.class);
        libgdxSkin = assetManager.get(Constants.libgdxSkinJson, Skin.class);
        map_minies = assetManager.get(Constants.map_minies, TextureAtlas.class);

        Array<Texture> out = new Array<Texture>();

        out = assetManager.getAll(Texture.class, out);

        for(Texture t: out){
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }

    public Texture background, logo, progressBar, progressBarBase;


    public void loadLoadingScreenAssets(AssetManager assetManager){

        assetManager.load(Constants.background, Texture.class);
        assetManager.load(Constants.logo, Texture.class);
        assetManager.load(Constants.progress_bar, Texture.class);
        assetManager.load(Constants.progress_bar_base, Texture.class);

        assetManager.finishLoading();

        background = assetManager.get(Constants.background, Texture.class);
        logo = assetManager.get(Constants.logo, Texture.class);
        progressBar = assetManager.get(Constants.progress_bar, Texture.class);
        progressBarBase = assetManager.get(Constants.progress_bar_base, Texture.class);

    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        System.out.println("Something happened with the asset loader");
    }

    @Override
    public void dispose() {

    }

}
