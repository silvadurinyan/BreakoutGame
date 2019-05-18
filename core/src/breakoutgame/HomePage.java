/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package breakoutgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;


public class HomePage implements Screen {

    Texture img;
    BreakoutGame game;
    Sprite backgroundImage;

    HomePage(BreakoutGame game) {
        this.game = game;
        img = new Texture("core/image/welcome.png");
        backgroundImage = new Sprite(img);
        backgroundImage.setPosition(0,0);
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(backgroundImage, backgroundImage.getX(), backgroundImage.getY());
        game.batch.end();
       
       if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new Game(game));
            dispose();
        }
     

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        img.dispose();
    }

}
