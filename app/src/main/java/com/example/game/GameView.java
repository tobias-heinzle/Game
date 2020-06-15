package com.example.game;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.graphics.Paint;

import java.util.Random;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private MainThread thread;
    private ObjectManager objManager;
    private int screenWidth;
    private int screenHeight;
    private int count; //framecounter
    private Random rng;




    //Sprite resources
    private Sprite seekerSprite;



    private Paint paint;

    float newX, newY;
    float healthPercent;


    private OnTouchListener eventListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            newX = event.getX()/screenWidth;
            newY = event.getY()/screenHeight;
            if (newX <  0.2){
            newX = 0;
            }
            if(newX > 0.8){
                newX = 1;
            }
            objManager.updateId("player",newX,newY);
            objManager.updateType("seeker", 0, 0);
            return false;
        }
    };

    public GameView(Context context){
        super(context);

        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this); //Programmablauf wird hier abgewickelt
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);

        rng = new Random();

        setFocusable(true);

        this.setOnTouchListener(eventListener);

        objManager = new ObjectManager(new Sprite(BitmapFactory.decodeResource(getResources(),R.drawable.test)));

        newX = 0;
        newY = 0;

        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        seekerSprite = new Sprite(BitmapFactory.decodeResource(getResources(),R.drawable.seeker));

        healthPercent = 1.0f;


        count = 0;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry){
            try {
                thread.setRunning(false);
                thread.join();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        objManager.addBackground("floor",90,90,190);
        objManager.addObject("player","player", 1.0f/2.0f,9.0f/10.0f , new Sprite(BitmapFactory.decodeResource(getResources(),R.drawable.robovampire)));
        thread.setRunning(true);
        thread.start();
    }

    public void update(){
        if (healthPercent < 0){
            return;
        }

        if(count%40 == 0){
            spawnEnemey(1.0f/2.0f, 0, 2);
        }

        if (count%60 == 0){
            objManager.updateType("seeker",0,0);
        }
        objManager.update();
        healthPercent = objManager.getHealthPercent();
        count++;
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
        if(canvas != null){

            objManager.draw(canvas);
            drawUI(canvas);
        }
    }

    public void drawUI(Canvas canvas){
        paint.setColor(Color.GREEN);
        canvas.drawRect(0,0,screenWidth*objManager.getHealthPercent(),screenHeight/12, paint);
        paint.setColor(Color.WHITE);
        canvas.drawText(Double.toString(thread.fps()),10,50,paint);//bei canvas muss mit pixeln gearbeitet werden
        canvas.drawText(Double.toString(objManager.getHealthPercent()),10,110,paint);//bei canvas muss mit pixeln gearbeitet werden

    }

    public void spawnEnemey(float x, float y, int n){
        float rng_x;
        for (int i = 0; i <= n; i++){
            rng_x =rng.nextInt(100)/100.0f;
            Sprite tmp = seekerSprite.clone();
            objManager.addObject("seeker", "dat boi", rng_x , y, tmp);
        }

    }


    /*
    class touchInputListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
    */


}
