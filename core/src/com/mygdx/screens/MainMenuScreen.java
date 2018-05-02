package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Game;
import com.mygdx.utils.Assets;
import com.mygdx.utils.Constants;

public class MainMenuScreen extends DefaultScreen{

    private Viewport viewport;
    private Stage stage;

    private Label one_player;
    private Label two_players;
    private Label options;
    private Label credits;
    private Label exit;

    private Texture title_background;

    private I18NBundle bundle;

    public MainMenuScreen(final Game game){
        super(game);

        viewport = new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        Label.LabelStyle labelStyle =  new Label.LabelStyle(game.bitmapFont, Color.WHITE);

        bundle = game.languageManager.getCurrentBundle();

        one_player = new Label(bundle.get("one_player_mode"), labelStyle);
        two_players = new Label(bundle.get("two_players_mode"), labelStyle);
        options = new Label(bundle.get("options"), labelStyle);
        credits = new Label(bundle.get("credits"), labelStyle);
        exit = new Label(bundle.get("exit"), labelStyle);

        Table table = new Table();
        table.setFillParent(true);
        table.pack();

        table.row();
        table.add(one_player).padRight(700f);
        table.row();
        table.add(two_players).padRight(700f);
        table.row();
        table.add(options).padRight(700f);
        table.row();
        table.add(credits).padRight(700f);
        table.row();
        table.add(exit).padRight(700f);

        one_player.addListener(new ClickListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                one_player.setColor(Color.GREEN);super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                one_player.setColor(Color.WHITE);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                PlayScreen playScreen = new PlayScreen(game);
                game.setScreen(playScreen);
                playScreen.startGame(Constants.ONE_PLAYER_MODE);
            }
        });

        two_players.addListener(new ClickListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                two_players.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                two_players.setColor(Color.WHITE);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                PlayScreen playScreen = new PlayScreen(game);
                game.setScreen(playScreen);
                playScreen.startGame(Constants.TWO_PLAYER_MODE);
            }
        });

        options.addListener(new ClickListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                options.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                options.setColor(Color.WHITE);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new OptionsScreen(game));
            }
        });

        credits.addListener(new ClickListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                credits.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                credits.setColor(Color.WHITE);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });


        exit.addListener(new ClickListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                exit.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                exit.setColor(Color.WHITE);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });

        stage.addActor(table);

        title_background = Assets.getInstance().title_background;

    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void render(float delta) {
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        game.batch.begin();

        game.batch.draw(title_background, 0,0, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);

        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {

        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
