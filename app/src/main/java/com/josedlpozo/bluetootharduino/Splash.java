package com.josedlpozo.bluetootharduino;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class Splash extends Activity implements Animation.AnimationListener {

    private static final long SPLASH_SCREEN_DELAY = 3000;
    private static final long ZOOM_TO_BLINK = 1500;
    private static final long BLINK_TO_MOVE = 1800;
    private static final long MOVE_TO_FADEIN = 500;


    // Animation
    Animation animFadein;
    Animation animBlink;
    Animation animZoomIn;
    Animation animMove;

    ImageView imagen;
    ImageView imagen2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        imagen = (ImageView) findViewById(R.id.img);
        imagen2 = (ImageView) findViewById(R.id.img2);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        animBlink = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
        animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
        animMove = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move);

        animFadein.setAnimationListener(this);
        animBlink.setAnimationListener(this);
        animZoomIn.setAnimationListener(this);
        animMove.setAnimationListener(this);

        imagen2.setVisibility(View.VISIBLE);


        imagen2.setAnimation(animZoomIn);
        imagen2.startAnimation(animZoomIn);

        Thread thread;

        thread = new Thread(){
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(ZOOM_TO_BLINK);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imagen2.setAnimation(animBlink);
                                imagen2.startAnimation(animBlink);
                            }
                        });

                        wait(BLINK_TO_MOVE);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imagen2.setAnimation(animMove);
                                imagen2.startAnimation(animMove);
                            }
                        });

                        wait(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                imagen.setVisibility(View.VISIBLE);
                                imagen.setAnimation(animFadein);
                                imagen.startAnimation(animFadein);
                                imagen2.setVisibility(View.GONE);
                            }
                        });

                        wait(2000);

                        Intent mainIntent = new Intent().setClass(Splash.this,Main.class);
                        startActivity(mainIntent);

                        finish();



                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            };
        };
        thread.start();



    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation



    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
