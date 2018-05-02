package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Game;
import com.mygdx.utils.Assets;
import com.mygdx.utils.Constants;

public class OptionsScreen extends DefaultScreen{

    private Viewport viewport;
    private Stage stage;

    private Label
            options, controls, player_1, player_2, left, down, up, right, bomb,
            sounds, effects, music, language, spanish, english;

    private TextButton left_1, left_2, up_1, up_2, right_1, right_2, down_1, down_2, bomb_1, bomb_2;

    private Label reset, save_changes, return_to_menu;

    private Slider effectsSlider, musicSlider;

    private CheckBox spanishBox, englishBox;

    private float bottomPadding = 20f;

    private InputMultiplexer inputMultiplexer;

    private Array<TextButton> inputButtons;

    private Texture title_background;

    public OptionsScreen(final Game game) {
        super(game);

        inputMultiplexer = new InputMultiplexer();

        viewport = new FitViewport(Constants.VIEWPORT_WIDTH*1.2f,
                Constants.VIEWPORT_HEIGHT*1.2f, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(new StageInputHandler());
        Gdx.input.setInputProcessor(inputMultiplexer);

        Label.LabelStyle labelStyle =  new Label.LabelStyle(game.bitmapFont, Color.WHITE);
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(Assets.getInstance().bombermanAtlas.findRegion("slider"));
        sliderStyle.knob = new TextureRegionDrawable(Assets.getInstance().bombermanAtlas.findRegion("knob"));

        TextButton.TextButtonStyle  buttonStyle = new TextButton.TextButtonStyle ();
        buttonStyle.font = game.bitmapFont;
        buttonStyle.fontColor = Color.GOLD;

        options = new Label(game.languageManager.getCurrentBundle().get("options"), labelStyle);
        controls = new Label(game.languageManager.getCurrentBundle().get("controls"), labelStyle);
        player_1 = new Label(game.languageManager.getCurrentBundle().get("player_1"), labelStyle);
        player_2 = new Label(game.languageManager.getCurrentBundle().get("player_2"), labelStyle);
        left = new Label(game.languageManager.getCurrentBundle().get("left"), labelStyle);
        down = new Label(game.languageManager.getCurrentBundle().get("down"), labelStyle);
        right = new Label(game.languageManager.getCurrentBundle().get("right"), labelStyle);
        up = new Label(game.languageManager.getCurrentBundle().get("up"), labelStyle);
        bomb = new Label(game.languageManager.getCurrentBundle().get("bomb"), labelStyle);
        sounds = new Label(game.languageManager.getCurrentBundle().get("sounds"), labelStyle);
        effects = new Label(game.languageManager.getCurrentBundle().get("effects"), labelStyle);
        music = new Label(game.languageManager.getCurrentBundle().get("music"), labelStyle);
        language = new Label(game.languageManager.getCurrentBundle().get("language"), labelStyle);
        spanish = new Label(game.languageManager.getCurrentBundle().get("spanish"), labelStyle);
        english = new Label(game.languageManager.getCurrentBundle().get("english"), labelStyle);

        labelStyle = new Label.LabelStyle(game.bitmapFont, Color.GOLD);

        reset = new Label(game.languageManager.getCurrentBundle().get("reset_settings"), labelStyle);
        save_changes = new Label(game.languageManager.getCurrentBundle().get("save_changes"), labelStyle);
        return_to_menu = new Label(game.languageManager.getCurrentBundle().get("return"), labelStyle);

        left_1 = new TextButton(Input.Keys.toString(game.preferences.getInteger
                (Constants.player_1_keys[Constants.LEFT], Input.Keys.A)).toUpperCase(), buttonStyle);

        right_1 = new TextButton(Input.Keys.toString(game.preferences.getInteger
                (Constants.player_1_keys[Constants.RIGHT], Input.Keys.D)).toUpperCase(), buttonStyle);

        up_1 = new TextButton(Input.Keys.toString(game.preferences.getInteger
                (Constants.player_1_keys[Constants.UP], Input.Keys.W)).toUpperCase(), buttonStyle);

        down_1 = new TextButton(Input.Keys.toString(game.preferences.getInteger
                (Constants.player_1_keys[Constants.DOWN], Input.Keys.S)).toUpperCase(), buttonStyle);

        bomb_1 = new TextButton(Input.Keys.toString(game.preferences.getInteger
                (Constants.player_1_keys[Constants.BOMB], Input.Keys.B)).toUpperCase(), buttonStyle);

        left_2 = new TextButton(Input.Keys.toString(game.preferences.getInteger
                (Constants.player_2_keys[Constants.LEFT], Input.Keys.LEFT)).toUpperCase(), buttonStyle);

        right_2 = new TextButton(Input.Keys.toString(game.preferences.getInteger
                (Constants.player_2_keys[Constants.RIGHT], Input.Keys.RIGHT)).toUpperCase(), buttonStyle);

        up_2 = new TextButton(Input.Keys.toString(game.preferences.getInteger
                (Constants.player_2_keys[Constants.UP], Input.Keys.UP)).toUpperCase(), buttonStyle);

        down_2 = new TextButton(Input.Keys.toString(game.preferences.getInteger
                (Constants.player_2_keys[Constants.DOWN], Input.Keys.DOWN)).toUpperCase(), buttonStyle);

        bomb_2 = new TextButton(Input.Keys.toString(game.preferences.getInteger
                (Constants.player_2_keys[Constants.BOMB], Input.Keys.P)).toUpperCase(), buttonStyle);


        inputButtons = new Array<TextButton>();

        inputButtons.add(up_1);
        inputButtons.add(down_1);
        inputButtons.add(left_1);
        inputButtons.add(right_1);
        inputButtons.add(bomb_1);

        inputButtons.add(up_2);
        inputButtons.add(down_2);
        inputButtons.add(left_2);
        inputButtons.add(right_2);
        inputButtons.add(bomb_2);

        for(int i = 0; i < inputButtons.size; i++){

            final TextButton t = inputButtons.get(i);

            t.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    for(TextButton textButton: inputButtons){
                        if(!textButton.equals(t)){
                            textButton.setChecked(false);
                            textButton.getLabel().setColor(Color.WHITE);
                        }
                    }
                    return false;
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if(!t.isChecked())
                        t.getLabel().setColor(Color.GREEN);
                    else
                        t.getLabel().setColor(Color.RED);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if(!t.isChecked())
                        t.getLabel().setColor(Color.WHITE);
                    else
                        t.getLabel().setColor(Color.RED);
                }
            });
        }

        effectsSlider = new Slider(0.0f, 1f, 0.01f, false, sliderStyle);
        musicSlider = new Slider(0.0f, 1f, 0.01f, false, sliderStyle);
        effectsSlider.setValue(game.preferences.getFloat(Constants.effects_key, 0.5f));
        musicSlider.setValue(game.preferences.getFloat(Constants.music_key, 0.5f));

        effectsSlider.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                game.preferences.putFloat(Constants.effects_key, effectsSlider.getValue());
            }
        });

        musicSlider.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                game.preferences.putFloat(Constants.music_key, musicSlider.getValue());
            }
        });

        reset.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                game.preferences.putInteger(Constants.player_1_keys[Constants.LEFT], Input.Keys.A);
                game.preferences.putInteger(Constants.player_1_keys[Constants.RIGHT], Input.Keys.D);
                game.preferences.putInteger(Constants.player_1_keys[Constants.UP], Input.Keys.W);
                game.preferences.putInteger(Constants.player_1_keys[Constants.DOWN], Input.Keys.S);
                game.preferences.putInteger(Constants.player_1_keys[Constants.BOMB], Input.Keys.B);

                left_1.setText(Input.Keys.toString(Input.Keys.A).toUpperCase());
                right_1.setText(Input.Keys.toString(Input.Keys.D).toUpperCase());
                up_1.setText(Input.Keys.toString(Input.Keys.W).toUpperCase());
                down_1.setText(Input.Keys.toString(Input.Keys.S).toUpperCase());
                bomb_1.setText(Input.Keys.toString(Input.Keys.B).toUpperCase());

                game.preferences.putInteger(Constants.player_2_keys[Constants.LEFT], Input.Keys.LEFT);
                game.preferences.putInteger(Constants.player_2_keys[Constants.RIGHT], Input.Keys.RIGHT);
                game.preferences.putInteger(Constants.player_2_keys[Constants.UP], Input.Keys.UP);
                game.preferences.putInteger(Constants.player_2_keys[Constants.DOWN], Input.Keys.DOWN);
                game.preferences.putInteger(Constants.player_2_keys[Constants.BOMB], Input.Keys.P);

                left_2.setText(Input.Keys.toString(Input.Keys.LEFT).toUpperCase());
                right_2.setText(Input.Keys.toString(Input.Keys.RIGHT).toUpperCase());
                up_2.setText(Input.Keys.toString(Input.Keys.UP).toUpperCase());
                down_2.setText(Input.Keys.toString(Input.Keys.DOWN).toUpperCase());
                bomb_2.setText(Input.Keys.toString(Input.Keys.P).toUpperCase());

                // reset music and sounds effects

                musicSlider.setValue(0.5f);
                effectsSlider.setValue(0.5f);

                game.preferences.putFloat(Constants.music_key, musicSlider.getValue());
                game.preferences.putFloat(Constants.effects_key, effectsSlider.getValue());

                return false;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                reset.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                reset.setColor(Color.WHITE);
            }
        });

        return_to_menu.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainMenuScreen(game));
                return false;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                return_to_menu.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                return_to_menu.setColor(Color.WHITE);
            }
        });

        save_changes.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.preferences.flush();
                System.out.println("Settings Saved");
                return false;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                save_changes.setColor(Color.GREEN);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                save_changes.setColor(Color.WHITE);
            }
        });

        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();

        Table table = new Table();
        table.setFillParent(true);
        table.pack();
        //table.debug();
        table.defaults().expand();

        table.row();
        table.add(options).padBottom(bottomPadding).colspan(3);
        table.row();
        table.add(controls).padBottom(bottomPadding).colspan(3);
        table.row();
        table.add().center().colspan(1);
        table.add(player_1).center().colspan(1);
        table.add(player_2).center().colspan(1);
        table.row();
        table.add(left).colspan(1);
        table.add(left_1).colspan(1);
        table.add(left_2).colspan(1);
        table.row();
        table.add(right).colspan(1);
        table.add(right_1).colspan(1);
        table.add(right_2).colspan(1);
        table.row();
        table.add(up).colspan(1);
        table.add(up_1).colspan(1);
        table.add(up_2).colspan(1);
        table.row();
        table.add(down).colspan(1);
        table.add(down_1).colspan(1);
        table.add(down_2).colspan(1);
        table.row();
        table.add(bomb).colspan(1);
        table.add(bomb_1).colspan(1);
        table.add(bomb_2).colspan(1);
        table.row();
        table.add(sounds).padBottom(bottomPadding).colspan(3);
        table.row();
        table.add(effects).colspan(1);
        table.add(effectsSlider).colspan(2);
        table.row();
        table.add(music).colspan(1);
        table.add(musicSlider).colspan(2);
        table.row();
        table.add(language).padBottom(bottomPadding).colspan(3);
        table.row();
        table.add(spanish).colspan(2);
        table.add(english).colspan(2).padRight(300f);
        table.row();
        table.add(return_to_menu).colspan(1);
        table.add(reset).colspan(1);
        table.add(save_changes).colspan(1);

        stage.addActor(table);

        title_background = Assets.getInstance().title_background;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void render(float delta) {
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        game.batch.begin();

        //game.batch.draw(title_background, 0,0, viewport.getWorldWidth(), viewport.getWorldHeight());

        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    class StageInputHandler extends InputAdapter{

        @Override
        public boolean keyDown(int keycode) {

            for(TextButton t: inputButtons){

                if(t.isChecked()){
                    t.setText(Input.Keys.toString(keycode).toUpperCase());
                    t.setChecked(false);
                    t.getLabel().setColor(Color.WHITE);

                    int index = inputButtons.indexOf(t, false);
                    String key;

                    if(index > 4){
                        index -= 5;
                        key = Constants.player_2_keys[index];
                    }else{
                        key = Constants.player_1_keys[index];
                    }

                    game.preferences.putInteger(key, keycode);
                }

            }
            return false;
        }
    }


}
