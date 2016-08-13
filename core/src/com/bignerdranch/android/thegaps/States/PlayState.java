package com.bignerdranch.android.thegaps.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.bignerdranch.android.thegaps.TheGaps;
import com.bignerdranch.android.thegaps.sprites.Ball;
import com.bignerdranch.android.thegaps.sprites.Blocks;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by nafis on 31-Jul-16.
 */
public class PlayState extends State {

    public static final int BLOCK_SPACING = 100;
    public static final int BLOCK_COUNTS = 4;
    private Ball ball;
    private Texture background,bk,playbtn;
    public static Texture pausebtn;
    public ArrayList<Blocks> block;
    public ArrayList<PauseState> pauseState2;
    public static int points;
    private String Score,Highscore;
    BitmapFont font,fonthighscore;
    public static Preferences prefs;
    private static String mBackground;
    public static int pause = 0;
    private PauseState pauseState;

    public PlayState(GameStateManager gsm) {

         super(gsm);

         randomizer();

         ball= new Ball(400,80);

         background = new Texture(mBackground);

         cam.setToOrtho(false,TheGaps.WIDTH,TheGaps.HEIGHT);

         block = new ArrayList<Blocks>();
         pauseState2 = new ArrayList<PauseState>();
         pausebtn = new Texture("media_pause.png");
         playbtn = new Texture("playbtn2.png");
         bk = new Texture("pausebk.png");

         for(int i = 0; i< BLOCK_COUNTS;i++){
            block.add(new Blocks(i*(BLOCK_SPACING + Blocks.BLOCK_HEIGHT )));
         }

            points = 0;
            Score= "0";
            font = new BitmapFont();
            font.getData().setScale(4,4);
            fonthighscore = new BitmapFont();
            fonthighscore.getData().setScale(2,2);
            prefs = Gdx.app.getPreferences("saved_highscore");
            if(prefs.getInteger("high")== 0){
                prefs.putInteger("high",0);
                prefs.flush();
            }
            Highscore="Best: 0";
            pauseState = new PauseState();

    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched() && pause!=1){

            Vector3 tmp = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            cam.unproject(tmp);
            Rectangle textureBounds=new Rectangle(10,TheGaps.HEIGHT-pausebtn.getHeight(),pausebtn.getWidth(),pausebtn.getHeight());

            if(textureBounds.contains(tmp.x,tmp.y))
            {
                pause = 1;
                pauseState = new PauseState(ball.getPostion().x) ;
                for(Blocks blocks : block) {
                    pauseState2.add(new PauseState(blocks.getPosBlock().x,blocks.getPosBlock().y));
                }

            }else{
                if(pause != 1){ball.move();}}
        }
        else if (Gdx.input.justTouched() && pause ==  1){
            Vector3 tmp = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            cam.unproject(tmp);
            Rectangle textureBounds=new Rectangle((TheGaps.WIDTH/2)-(playbtn.getWidth()/2),(TheGaps.HEIGHT/2)-(playbtn.getHeight()/2),playbtn.getWidth(),playbtn.getHeight());

            if(textureBounds.contains(tmp.x,tmp.y))
            {
                pause = 0;
                //Settiing postion of objects after resume is pressed
                ball.setPosition(PauseState.ballpos.getFloat("pos"));

                for(int i = 0; i< BLOCK_COUNTS;i++) {
                   block.add(new Blocks(pauseState2.get(i).getblockPosx(), pauseState2.get(i).getblockPosy()));

                }

            }
        }
    }

    @Override
    public void update(float dt) {

       if(Blocks.TEMP_COUNT == 1) {
           if ((TheGaps.WIDTH / 2) - 20 <= ball.getPostion().x && ball.getPostion().x <= (TheGaps.WIDTH / 2) + 20) {

               points++;
               Score = "" + points;
               if(points > prefs.getInteger("high")){
                    prefs.putInteger("high",points);
                    prefs.flush();
               }
           }
       }
        handleInput();
        ball.update(dt);


        if(pause!=1){
        for(int i=0 ; i<BLOCK_COUNTS ; i++){
            block.get(i).update(dt);
        }
        for(Blocks blocks : block) {
            if (blocks.getPosBlock().y + Blocks.BLOCK_HEIGHT < 0) {

                if (points >= 10) {
                    //for random positioning of the blocks in the x-axis
                    blocks.reposition((float) (Math.random() * (TheGaps.WIDTH - blocks.getBlock().getWidth())), blocks.getPosBlock().y + ((Blocks.BLOCK_HEIGHT + BLOCK_SPACING) * BLOCK_COUNTS));
                    //increases speed
                    if (points >= 15) {
                        Blocks.MOVEMENT = -400;
                        if (points >= 25) {
                            Blocks.MOVEMENT = -450;
                            if (points >= 35) {
                                Blocks.MOVEMENT = -500;
                                if (points >= 45) {
                                    Blocks.MOVEMENT = -550;
                                    if (points >= 50) {
                                        Blocks.MOVEMENT = -600;
                                        if (points >= 60) {
                                            Blocks.MOVEMENT = -700;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //default postioning
                else {
                    blocks.reposition((TheGaps.WIDTH / 2) - blocks.getBlock().getWidth() / 2, blocks.getPosBlock().y + ((Blocks.BLOCK_HEIGHT + BLOCK_SPACING) * BLOCK_COUNTS));
                }
            }
            if (blocks.collides(ball.getBounds())) {
                System.out.println("colliding");
                gsm.set(new GameOverState(gsm));
            }
        }
        }
           cam.update();
           Highscore = "Best: " + prefs.getInteger("high");
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sb.setProjectionMatrix(cam.combined);
        sb.enableBlending();
        sb.begin();
        sb.draw(background,0,0,TheGaps.WIDTH,TheGaps.HEIGHT);
        sb.draw(ball.getBall(), ball.getPostion().x, ball.getPostion().y);
            for(Blocks blocks : block){
                sb.draw(blocks.getBlock(),blocks.getPosBlock().x,blocks.getPosBlock().y);
            }
        font.setColor(Color.BLACK);
        font.draw(sb, Score, TheGaps.WIDTH -65, TheGaps.HEIGHT -10);
        fonthighscore.setColor(Color.BLACK);
        fonthighscore.draw(sb,Highscore ,TheGaps.WIDTH-115, TheGaps.HEIGHT - 80);
        sb.draw(pausebtn,10,TheGaps.HEIGHT-pausebtn.getHeight());
        //pausemenu
        if(pause == 1){
            //background
            Color c = sb.getColor();
            sb.setColor(c.r, c.g, c.b, .3f);
            sb.draw(bk, 0, 0, 400, 800);
            //foreground
            c = sb.getColor();
            sb.setColor(c.r, c.g, c.b, 1f);
            sb.draw(playbtn,(TheGaps.WIDTH/2)-(playbtn.getWidth()/2),(TheGaps.HEIGHT/2)-(playbtn.getHeight()/2));

        }

        sb.end();

    }

    @Override
    public void dispose() {
        background.dispose();
        ball.dispose();

        for(Blocks blocks : block){
            blocks.dispose();
        }



    }

    private void randomizer(){
        Random rand = new Random();

        int randomNum = rand.nextInt((5 - 1) + 1) + 1;

        switch (randomNum){
            case 1: {
                mBackground = "background.png";
                Blocks.mBlocks = "block.png";
                break;
            }
            case 2: {
                mBackground = "background2.png";
                Blocks.mBlocks = "block2.png";
                break;
            }
            case 3: {
                mBackground = "background3.png";
                Blocks.mBlocks = "block3.png";
                break;
            }
            case 4: {
                mBackground = "op-bk2.jpg";
                Blocks.mBlocks = "block.png";
                break;
            }
            case 5: {
                mBackground = "op-bk.jpg";
                Blocks.mBlocks = "block4.png";
                break;
            }
            default:{
                mBackground = "background.png";
                Blocks.mBlocks = "block.png";
                break;
            }
        }
    }
}
