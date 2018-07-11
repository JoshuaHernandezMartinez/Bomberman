package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Game;
import com.mygdx.networking.PlayerClient;
import com.mygdx.utils.Constants;

import io.socket.client.Socket;

public class PlayScreen extends DefaultScreen {

    private OrthographicCamera camera;
    private StretchViewport viewport;

    private GameWorld gameWorld;

    public PlayScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new StretchViewport(Constants.VIEWPORT_WIDTH / Constants.PPM,
                Constants.VIEWPORT_HEIGHT / Constants.PPM, camera);

        camera.position.set(Constants.CELL_SIZE * Constants.MAZE_WIDTH/2 / Constants.PPM,
                Constants.CELL_SIZE * Constants.MAZE_HEIGHT/2 / Constants.PPM, 0);

        camera.update();

        gameWorld = null;

    }

    public void start_solo_game(){
        gameWorld = new SoloGame(game, camera);
    }

    public void start_versus_game(){
        gameWorld = new VersusGame(game, camera);
    }

    public void start_multiplayer_game(Socket socket, Array<PlayerClient> clients, int mapIndex){
        gameWorld = new MultiplayerGame(game, socket, clients, mapIndex, camera);
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void render ( float delta){
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f,
                    1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameWorld.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public InputProcessor getInputProcessor() {
        return gameWorld.getInputMultiplexer();
    }

    @Override
    public void dispose() {
        super.dispose();
        gameWorld.dispose();
    }
}