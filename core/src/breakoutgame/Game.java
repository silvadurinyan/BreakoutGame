
package breakoutgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class Game implements Screen {

    // scaling factor for physics
    static final float WORLD_SCALE = 100f;

    // collision bitmasks
    static final short WORLD_BOUND = 1;
    static final short PADDLE = 1 << 1;
    static final short BRICK = 1 << 2;
    static final short BALL = 1 << 3;
    static final short BALL_KILLER = 1 << 4;

    // make sure the ball moves at least this fast
    static final float BALL_SENTINEL = 0.5f;

    // game state
    BreakoutGame game;
    World world;
    Texture padTex;
    Texture ballTex;
    Texture brickTex;
    static Texture background;
    int brickVPad = 5;
    int brickHPad = 5;
    int areaPad = 150;
    Fixture ballKiller;
    Sprite back;
    Array<PhysicalSprite> bricks;
    Array<Body> bumpers;
    PhysicalSprite paddle;

    Array<PhysicalSprite> toBeDestroyed;

    // more game state
    private boolean ballStarted = false;
    private boolean resetBall = false;
    private boolean mouse = false;
    private int score = 0;
    private int scoreMultiplier;
    private int lives = 3;
    private int screenWidth;
    private int screenHeight;

    Game(BreakoutGame game) {
        this(game, 0, 1, 3);
    }


    Game(BreakoutGame game, int score, int scoreMultiplier, int lives) {
        this.game = game;
        this.score = score;
        this.scoreMultiplier = scoreMultiplier;
        this.lives = lives;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        padTex = new Texture("core/image/paddle.png");
        ballTex = new Texture("core/image/ball.png");
        brickTex = new Texture("core/image/brick.png");
        background = new Texture("core/image/background.png");
        back = new Sprite(background);
        back.setPosition(0,0);

        createEdges();
        createPaddle();
        createBricks();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
     
         while (toBeDestroyed.size != 0) {
            PhysicalSprite dead = toBeDestroyed.first();
            toBeDestroyed.removeIndex(0);
            world.destroyBody(dead.body);
            if (bricks.contains(dead, true)) {
                bricks.removeValue(dead, true);
                score += scoreMultiplier;
            }
        }

        // level up
        if (bricks.size == 0) {
            game.setScreen(new Game(
                    game, score, scoreMultiplier + 1,
                    // bonus life every 5 levels
                    lives + (scoreMultiplier % 5 == 0 ? 1 : 0)));
            dispose();
            return;
        }

        paddle.update();
        
        for (PhysicalSprite brick : bricks) {
            brick.update();
        }

       

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(background, back.getX(), back.getY());
        paddle.draw(game.batch);
        for (PhysicalSprite brick : bricks) {
            brick.draw(game.batch);

        }

        game.batch.end();

    }

    @Override
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
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
       
        brickTex.dispose();
        background.dispose();

    }

   
    private void createBricks() {
        int brickWidth = brickTex.getWidth();
        int brickHeight = brickTex.getHeight();
        brickWidth += brickHPad * 2;
        brickHeight += brickVPad * 2;
        // number of rows and columns of bricks that can be made
        int numCols = screenWidth / brickWidth;
        int numRows = (screenHeight - areaPad) / brickHeight;

        for (int i = 0; i < numCols; i++) {
            for (int j = 0; j < numRows; j++) {
                int col = i * brickWidth;
                int row = screenHeight - brickHeight - (j * brickHeight);
                PhysicalSprite.Defs defs = PhysicalSprite.Defs.fromScreenCoordinates(
                        brickTex, col, row, WORLD_SCALE);

                defs.bodyDef.type = BodyType.DynamicBody;

                defs.fixtureDef.density = 0.1f;
                defs.fixtureDef.filter.categoryBits = BRICK;
                defs.fixtureDef.filter.maskBits = (BALL | WORLD_BOUND);

                PhysicalSprite brick = new PhysicalSprite(defs, world);

                bricks.add(brick);
            }
        }
    }




    private void createPaddle() {
        float x = (screenWidth / 2) - (padTex.getWidth() / 2);
        float y = 3;
        PhysicalSprite.Defs defs = PhysicalSprite.Defs.fromScreenCoordinates(
                padTex, x, y, WORLD_SCALE);

        defs.bodyDef.type = BodyType.DynamicBody;
        defs.bodyDef.fixedRotation = true;

        defs.fixtureDef.density = 0.1f;
        defs.fixtureDef.filter.categoryBits = PADDLE;
        defs.fixtureDef.filter.maskBits = (BALL | WORLD_BOUND);

        paddle = new PhysicalSprite(defs, world);
        paddle.body.setLinearDamping(2.0f);
    }

 

   
    private void createEdges() {
        float w = Gdx.graphics.getWidth() / WORLD_SCALE;
        float h = Gdx.graphics.getHeight() / WORLD_SCALE;


        
        for (Vector2[] points : new Vector2[][]{
            {new Vector2(0, 0), new Vector2(0, h)}, // left
            {new Vector2(w, 0), new Vector2(w, h)}, // right
            {new Vector2(0, 0), new Vector2(w, 0)}, // top
            {new Vector2(0, h), new Vector2(w, h)}} // bottom
                ) {
            Vector2 p1, p2;
            p1 = points[0];
            p2 = points[1];
      

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(0f, 0f);

            EdgeShape shape = new EdgeShape();
            shape.set(p1, p2);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = WORLD_BOUND;

            Body body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);

            bumpers.add(body);
            shape.dispose();
        }

       
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0f, 0f);

        EdgeShape shape = new EdgeShape();
        shape.set(0, 1 / WORLD_SCALE, w, 1 / WORLD_SCALE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = BALL_KILLER;

        Body body = world.createBody(bodyDef);
        ballKiller = body.createFixture(fixtureDef);

        bumpers.add(body);
        shape.dispose();
    }
}
