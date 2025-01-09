package io.github.AstroidKush;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.w3c.dom.css.RGBColor;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    static final int world_width = 100;
    static final int world_height = 1000;



    private TextureAtlas atlas;
    private Array<Sprite> astroids;
    Array<Sprite> stars;
    private Sprite bullet;

    private Explosion explosion;

    private HealthBar healthMeter;


    private Sprite gameOverText;


    Rectangle shipRectangle;
    Rectangle astroidRectangle;

    Texture shipSheet;



    SpriteAnimation<TextureRegion> ship;



    boolean collision;



    boolean gameOver = false;
    float timeAtDeath;

    //Timer Variables

    float stateTime;



    float movementTimer;
    float starTimer;




    private OrthographicCamera cam;



    SpriteBatch spriteBatch;
    ExtendViewport viewport;

    @Override
    public void create() {






        /*
        Ship animation
        *
        */


        shipSheet = new Texture(Gdx.files.internal("SpaceShip.png"));







        explosion = new Explosion();
        //Splits sprite sheet into 9 indiviudal frames

        TextureRegion[][] tmp = TextureRegion.split(shipSheet, shipSheet.getWidth()/9, shipSheet.getHeight());


        TextureRegion[] shipFrames = new TextureRegion[9];


        for(int i = 0; i < 9;i++){shipFrames[i] = tmp[0][i];}



        //initialize animation

        ship = new SpriteAnimation<TextureRegion>(0.1f, shipFrames);
        ship.setScaling(0.12f);
        ship.setPlayMode(Animation.PlayMode.LOOP);



        collision = false;


        //Construct Orthological camera


        cam = new OrthographicCamera(world_width, world_height/10);
        cam.position.set(cam.viewportWidth/2f, cam.viewportHeight/2f, 0);
        cam.update();




        spriteBatch = new SpriteBatch();
        viewport = new ExtendViewport(8, 5);




        //Sprite initialisation


        atlas = new TextureAtlas(Gdx.files.internal("spacejunk.atlas"));


        astroids = new Array<>();

        healthMeter  = new HealthBar(cam,spriteBatch, atlas.findRegion("HealthUnit"));

        stateTime = 0f;


        stars = new Array<>();


        shipRectangle = new Rectangle();
        astroidRectangle = new Rectangle();





        //game over scene creation



        gameOverText = new Sprite(atlas.findRegion("GameOverText"));

        gameOverText.setSize(100,100);





    }


    @Override
    public void resize(int width, int height){
      viewport.update(width,height, true);

    }

    @Override
    public void pause(){}
    @Override
    public void resume(){}

    @Override
    public void render() {


        draw();

        logic();



    }


    private void input(float max){




        if (Gdx.input.isKeyPressed(Input.Keys.UP)){

            if (ship.yPos() >= cam.position.y + cam.viewportHeight/2 - shipRectangle.height){;}
            else{
            ship.moveY(3);}
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){


            if(ship.yPos() <= cam.position.y - 56){;
                // prevents ship from moving out of bounds

            }
            else {

                ship.moveY(-3);
            }}


        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){




            if(ship.xPos() < 0){
                // prevents ship from moving out of bounds

            }

            else {
                ship.moveX(-3);
            }


        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){

            if (ship.xPos() > world_width - (shipRectangle.width) - 10){;// prevents ship from moving out of bounds
                 }
            else {
                ship.moveX(3);
            }
        }





        //cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 100/cam.viewportWidth);

        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;


        cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth/2f);
        //cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight/2f);

    }


    float astroidTimer;

    private void logic(){



        //signals game over
        if(healthMeter.isDead()) {
            setGameOver();
        }

        //Timer Variables

        float time = Gdx.graphics.getDeltaTime();
        movementTimer += time;
        astroidTimer += time;
        starTimer += time;




        //Buffer value to prevent undue contact with empty whitespace
        int buffer = 10;

        shipRectangle.set(ship.xPos(),ship.yPos(),ship.width() - buffer,ship.height() - buffer);



        if (movementTimer >= 0.02f){


            slowPan(world_height, 2);
            movementTimer = 0;

        }

        if (astroidTimer >= 0.5f){

            createAstroid();
            astroidTimer = 0;

        }

        if (starTimer >= 0.0001f){generateStars(); starTimer = 0;}






       for (int i = astroids.size-1; i >= 0; i--) {

           Sprite astroid = astroids.get(i);
           astroidRectangle.set(astroid.getX(),astroid.getY(), astroid.getWidth() - buffer,astroid.getHeight() - buffer);

           if(astroidRectangle.overlaps(shipRectangle)){

               float explosionX = astroid.getX();
               float explosionY = astroid.getY();

               float size = astroid.getHeight();

               healthMeter.damage();
               astroids.removeIndex(i);



               explosion.setUp(explosionX,explosionY, size);

               collision = true;



               break;


           }







           //deletes atsroids when out of bounds
           if (astroids.get(i).getY() < cam.position.y - cam.viewportHeight){

               try {
                   astroids.removeIndex(i);
               }
               catch (IndexOutOfBoundsException e) {


                   astroids.removeIndex(astroids.size-1);
               }

           }
       }


       for (int i = stars.size-1; i >= 0; i--){

           if (stars.get(i).getY() < cam.position.y - cam.viewportHeight){stars.removeIndex(i);}

       }

    }
    private void draw(){

        float time;

        //Clears screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



        input(world_height - cam.viewportHeight);
        cam.update();
        spriteBatch.setProjectionMatrix(cam.combined);







        //Animation code
        stateTime += Gdx.graphics.getDeltaTime(); //acctumilated elapsed animation time

        spriteBatch.begin();








        if(collision) {


            explosion.draw(stateTime, spriteBatch);

            collision = false;
        }






        for(Sprite star: stars){star.draw(spriteBatch);}
        //map.draw(spriteBatch);



        for(Sprite astroid: astroids){astroid.draw(spriteBatch);}

        healthMeter.draw();




        if(!gameOver) {ship.draw(stateTime, spriteBatch);}


        else  {


            //game over drawing
            gameOverText.setY(cam.position.y - 50);


            gameOverText.draw(spriteBatch);

            explodeShip();
            explosion.draw(stateTime, spriteBatch);


        }



        spriteBatch.end();

    }

    private void slowPan(float max, float speed) {

        //Float max describes world size


        // speed dictates rate of movement

            if (cam.position.y >= max - viewport.getScreenHeight() / 2f) {
                relocateElements();
                cam.translate(0,-max, 0);



                //we want to compensate for max int so long as game is running
                if (!gameOver) ship.moveY(-max);


            }
            else {
                cam.translate(0, +2, 0);



                //we want ship to keep moving so long as game is running
                if (!gameOver) ship.moveY(speed);


            }
        }







        public void explodeShip(){


            timeAtDeath = Gdx.graphics.getDeltaTime();
            float explosionX = ship.xPos();
            float explosionY = ship.yPos();

            float size = ship.height();
            explosion.setUp(explosionX,explosionY,size);}




        private void generateStars(){

            Sprite star = new Sprite(atlas.findRegion("Star"));

            star.setColor(Color.WHITE);

            star.setX(MathUtils.random(0, world_width));
            star.setY(MathUtils.random(cam.position.y + cam.viewportHeight / 2, cam.position.y + cam.viewportHeight * 2 ));


            float size = MathUtils.random(0f, 0.5f);
            star.setSize(size, size);



            stars.add(star);
        }




        private void createAstroid(){


        float size = MathUtils.random(5f, 64f);
        float rotation = MathUtils.random(360f);


        float camPos = cam.position.y + cam.viewportHeight;

        Sprite astroid;
        int decider = MathUtils.random(2);

        if (decider == 1) astroid  = new Sprite(atlas.findRegion("Astroide"));
        else astroid = new Sprite(atlas.findRegion("Astroide2"));


        astroid.setSize(size,size);
        astroid.setRotation(rotation);


        astroid.setX(MathUtils.random(0f, world_width));
        astroid.setY(MathUtils.random(camPos, camPos + 100));


        astroids.add(astroid);

    }




   private void relocateElements(){

        for (Sprite astroid: astroids){
           float newY = astroid.getY() - world_height;
           astroid.setY(newY);
        }

        for (Sprite star: stars){

            float newY = star.getY() - world_height;
            star.setY(newY);

        }

   }




   public void setGameOver(){



       timeAtDeath = Gdx.graphics.getDeltaTime();
        gameOver = true;}






    @Override
    public void dispose() {

        spriteBatch.dispose();

        shipSheet.dispose();


        astroids.clear();
        stars.clear();

        atlas.dispose();

    }
}









class HealthBar{



    int lives;


    Array<Sprite> healthBar;
    SpriteBatch batch;


    OrthographicCamera cam;


    float posX = 3;
    float posY = 5;
    int spacing = 2;
    int size = 5;

    boolean dead;
    HealthBar(OrthographicCamera cam, SpriteBatch batch, TextureAtlas.AtlasRegion source){



        dead = false;
        this.cam = cam;
        healthBar = new Array<>();
        this.batch = batch;

        lives = 10;


        for (int i = 0; i <= lives; i++){
            healthBar.add(new Sprite(source));
        }


    }



    public void damage(){

        if(lives <= 0) dead = true;

        try {
            healthBar.removeIndex(lives - 1);
            lives--;
        }
        catch (IndexOutOfBoundsException e) {
            dead = true;
        }

    }



    public boolean isDead(){return dead;}



    public void draw(){

        for(int i = 0; i <  healthBar.size -1; i++){batch.draw(healthBar.get(i), posX + (spacing * i), (cam.position.y + cam.viewportHeight/2) - posY , size, size);}

    }


    }




class Explosion{

        Texture explosionSheet;



        SpriteAnimation<TextureRegion> explosion;


        Explosion(){

                    /*
        Explosion Animation

         */



            explosionSheet = new Texture(Gdx.files.internal("Explosion.png"));

            TextureRegion[][] expTmp = new TextureRegion().split(explosionSheet, explosionSheet.getWidth()/8,explosionSheet.getHeight());

            TextureRegion[] explosiveFrames = new TextureRegion[8];


            for(int i = 0; i < 8;i++){explosiveFrames[i] = expTmp[0][i];}



            explosion = new SpriteAnimation<>(0.1f, explosiveFrames);

            explosion.setPlayMode(Animation.PlayMode.LOOP);


            explosion.setFrameDuration(0.1f);



        }


        public void setSize(float scale){explosion.setScaling(scale/32);}


    public void draw(float StateTime, SpriteBatch batch){explosion.draw(StateTime, batch);}

    public void setUp(float posX, float posY, float size ){


            explosion.setX(posX);explosion.setY(posY);

            setSize(size);

            }





    }





class SpriteAnimation<T> extends Animation<T> {

    float scaleX;
    float scaleY;


    float xLocation;
    float yLocation;


    float newWidth;
    float newHeight;



    SpriteAnimation(float fps, T[] frames) {
        super(fps, frames);

        xLocation = 50;
        yLocation = 0;


    }


    public void moveX(float amount) {

        xLocation += amount;

    }

    public void moveY(float amount) {

        yLocation += amount;
    }

    public void setScaling(float scale) {

        scaleX = scale;
        scaleY = scale;

    }

    public void setX(float newX){xLocation = newX;}
    public void setY(float newY){yLocation = newY;}

    public void draw(float stateTime, SpriteBatch batch) {

        TextureRegion region = (TextureRegion) this.getKeyFrame(stateTime);


        newHeight = region.getRegionHeight() * scaleY;
        newWidth = region.getRegionWidth() * scaleX;



        batch.draw(region, xLocation, yLocation, (int) newWidth, (int) newHeight);


    }


    public float xPos() {
        return xLocation;
    }

    public float yPos() {
        return yLocation;
    }



    public float width(){return newWidth;}
    public float height(){return newHeight;}
}
