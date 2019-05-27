
package breakoutgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class GameOver implements Screen {

    public static final String filename = "breakoutdemo";
    private static final int LINE_SPACE = 15;

    BreakoutGame game;
    int score;
    List<Integer> scores;
    Texture img;
    Sprite logo;

    public GameOver(BreakoutGame game, int score) {
        this.game = game;
        this.score = score;
        scores = new ArrayList<>();
        scores.add(score);
        img = new Texture("core/image/game over.png");
        logo = new Sprite(img);
        logo.setPosition(0,0);
    }


    @Override
    public void show() {
       // read();
        Collections.sort(scores, Collections.reverseOrder());
        scores = scores.subList(0, (scores.size() > 10 ? 10 : scores.size()));
      //  write();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)
                || Gdx.input.isKeyPressed(Input.Keys.SPACE)
                || Gdx.input.justTouched()) {
            game.setScreen(new HomePage(game));
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.font.setColor(1, 0, 0, 1);
        game.batch.draw(logo, logo.getX(), logo.getY());


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
