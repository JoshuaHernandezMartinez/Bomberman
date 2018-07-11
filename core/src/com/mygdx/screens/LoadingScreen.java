package com.mygdx.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Game;
import com.mygdx.utils.Assets;
import com.mygdx.utils.Constants;
import com.mygdx.utils.ScreenTransition;
import com.mygdx.utils.ScreenTransitionSlice;

public class LoadingScreen extends DefaultScreen{

    private Vector2 logoPos, pbPos;

    private boolean paused;

    public LoadingScreen(Game game) {
        super(game);
    }

    private Viewport viewport;

    @Override
    public void show() {

        viewport = new StretchViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT, new OrthographicCamera());

        Assets.getInstance().loadLoadingScreenAssets(game.assetManager);

        Assets.getInstance().init(game.assetManager);

        logoPos = new Vector2();

        logoPos.set(Constants.VIEWPORT_WIDTH/2 - Assets.getInstance().logo.getWidth() / 2, Constants.VIEWPORT_HEIGHT/2);

        pbPos = new Vector2();
        pbPos.set(logoPos.x, logoPos.y - (Assets.getInstance().logo.getHeight()));

    }

    @Override
    public void render(float dt) {
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        game.batch.begin();
        game.batch.draw(Assets.getInstance().background, 0, 0, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        game.batch.draw(Assets.getInstance().logo, logoPos.x, logoPos.y);
        game.batch.draw(Assets.getInstance().progressBarBase, pbPos.x, pbPos.y);
        game.batch.draw(Assets.getInstance().progressBar, pbPos.x, pbPos.y,
                Assets.getInstance().progressBar.getWidth() * game.assetManager.getProgress(),
                Assets.getInstance().progressBar.getHeight());
        game.batch.end();



        if(game.assetManager.update() && !paused) {

            Assets.getInstance().initializeStuff(game.assetManager);

            ScreenTransition sliceTransition = ScreenTransitionSlice.init(1f,
                    ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out);

            game.setScreen(new MainMenuScreen(game), sliceTransition);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {
        //System.out.println("this is the loading screen paused");
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public InputProcessor getInputProcessor() {
        return null;
    }

    @Override
    public String toString() {
        return "Loading Screen";
    }
}
