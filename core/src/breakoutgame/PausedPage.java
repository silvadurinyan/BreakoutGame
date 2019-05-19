
package breakoutgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;





public class PausedPage implements Screen {

    BreakoutGame game;
    Screen returnTo;
    Texture background;
    Sprite back;

    public PausedPage(BreakoutGame game, Screen returnTo) {
        this.game = game;
        this.returnTo = returnTo;
        background = new Texture("core/image/background.png");
        back = new Sprite(background);
        back.setPosition(0,0);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)||Gdx.input.justTouched()|| Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(returnTo);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(background,back.getX(),back.getY());
        game.batch.end();

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
    }

}
