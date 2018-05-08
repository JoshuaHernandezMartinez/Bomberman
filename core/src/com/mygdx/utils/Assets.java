package com.mygdx.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Texture;

public class Assets implements Disposable, AssetErrorListener{
    private static final Assets ourInstance = new Assets();

    public static Assets getInstance() {
        return ourInstance;
    }

    private AssetManager assetManager;

    private Assets() {}

    // Assets

    public TextureAtlas bomberman_atlas;

    public BitmapFont font;

    public Texture title_background;

    public TextureAtlas blood_animation;

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
        this.assetManager = assetManager;
        assetManager.setErrorListener(this);

        assetManager.load(Constants.bomberman, TextureAtlas.class);

        assetManager.load(Constants.blood_animation, TextureAtlas.class);

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

        assetManager.finishLoading();

        bomberman_atlas = assetManager.get(Constants.bomberman, TextureAtlas.class);

        font = assetManager.get(Constants.font, BitmapFont.class);

        title_background = assetManager.get(Constants.title_background, Texture.class);

        blood_animation = assetManager.get(Constants.blood_animation, TextureAtlas.class);

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

        setFiler(bomberman_atlas);
        setFiler(blood_animation);

        title_background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    }

    private void setFiler(TextureAtlas atlas){
        for(Texture t: atlas.getTextures()){
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        System.out.println("Something happened with the asset loader");
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

}
