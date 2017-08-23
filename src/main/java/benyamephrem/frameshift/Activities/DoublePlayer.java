package benyamephrem.frameshift.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import benyamephrem.frameshift.Classes.GraphicsView;
import benyamephrem.frameshift.Classes.LineView;
import benyamephrem.frameshift.Classes.Rectangle;
import benyamephrem.frameshift.Classes.Scoreboard;
import benyamephrem.frameshift.R;


public class DoublePlayer extends AppCompatActivity {

    MediaPlayer mp;
    Scoreboard mScoreboard = new Scoreboard();
    TextView scoreBoard;
    TextView Player1TextView, Player2TextView;
    GraphicsView curvedText, curvedText1;
    Timer mTmr1; //spin center text
    TimerTask mTsk1;

    LineView mLine = null;

    Rectangle topRectangle = null,
            bottomRectangle = null;

    static int mScrWidth, mScrHeight;
    Timer mTmr = null;
    TimerTask mTsk = null;
    Handler RedrawHandler = new Handler(); //so redraw occurs in main thread

    boolean isGameStarted = false;
    boolean touchable = true;
    boolean movingUp = true;

    int green = 0xBA96FFA7;
    int red = 0xB7FF9A99;

    int lineSpeed;

    int w;

    int player1Hits = 0, player1Misses = 0, player2Hits = 0, player2Misses = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
        //set app to full screen and keep screen on
        getWindow().setFlags(0xFFFFFFFF, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.double_player);

        //Declare main screen
        RelativeLayout mainView = (RelativeLayout) findViewById(R.id.main_view);
        scoreBoard = (TextView) findViewById(R.id.scoreBoard);
        Player1TextView = (TextView) findViewById(R.id.Player1TextView);
        Player2TextView = (TextView) findViewById(R.id.Player2TextView);

        //Set Typefaces
        final Typeface type = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        scoreBoard.setTypeface(type);
        Player1TextView.setTypeface(type);
        Player2TextView.setTypeface(type);

        //get screen dimensions
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScrWidth = metrics.widthPixels;
        mScrHeight = metrics.heightPixels;

        //Set line speed class varibles
        lineSpeed = mScrHeight / 300;

        //Set media player
        mp = MediaPlayer.create(this, R.raw.blopsound);

        mLine = new LineView(this, 0, mScrHeight / 2, mScrWidth, mScrHeight / 2, 0xFF000000, mScrHeight / 50 );

        topRectangle = new Rectangle(this, 0, 0, mScrWidth, mScrHeight/2, green);
        bottomRectangle = new Rectangle(this, 0, mScrHeight /2, mScrWidth, mScrHeight, red );

        //GameLine
        mainView.addView(mLine);
        mLine.invalidate();


        //Rectangles
        mainView.addView(topRectangle);
        topRectangle.invalidate();

        mainView.addView(bottomRectangle);
        bottomRectangle.invalidate();


        //Curved "Cross the Opponent's Line" pathView
        curvedText = new GraphicsView(this);
        curvedText.setUpView(mScrWidth, mScrHeight);
        mainView.addView(curvedText);
        curvedText.invalidate();

        curvedText1 = new GraphicsView(this);
        curvedText1.setUpView(mScrWidth, mScrHeight);
        curvedText1.setRotation(180);
        mainView.addView(curvedText1);
        curvedText1.invalidate();


        //Make textViews flash periodically
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Animate tapToPlay TextView to flash until touched
                final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                animation.setDuration(1000); // duration - half a second
                animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
                animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                Player1TextView.startAnimation(animation);
                Player2TextView.startAnimation(animation);
            }
        }, 1000);

        //Rotate curved texts
        mTmr1 = new Timer();
        mTsk1 = new TimerTask() {
            @Override
            public void run() {
               final float newRotation = curvedText.getRotation() + 1;
                final float newRotation1 = curvedText1.getRotation() + 1;

                RedrawHandler.post(new Runnable() {
                    public void run() {
                        curvedText.setRotation(newRotation);
                        curvedText1.setRotation(newRotation1);
                        curvedText.invalidate();
                        curvedText1.invalidate();
                    }

                });
            }
        };
        mTmr1.schedule(mTsk1, 1200, 20);


    }//onCreate

    @Override
    public void onResume() //app moved to background, stop background threads
    {
        mTmr = new Timer();
        mTsk = new TimerTask() {
            @Override
            public void run() {

                if (isGameStarted) {

                    //Speed up game line speed
                    progressDifficulty();

                    //Move main game line
                    if (movingUp) {
                        //Updates points Y values
                        mLine.setStartY(mLine.getStartY() - (lineSpeed));
                        mLine.setStopY(mLine.getStopY() - (lineSpeed));
                    }
                    else {
                        mLine.setStartY(mLine.getStartY() + (lineSpeed));
                        mLine.setStopY(mLine.getStopY() + (lineSpeed));
                    }

                    //Update rectangle view to match line
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            topRectangle.setBottom(mLine.getStartY());
                            bottomRectangle.setTop(mLine.getStopY());
                        }
                    });

                    //Player Dies if game like hits kill zone
                    if (mLine.getStartY() < 0 ) {
                        goToRedirectScreen2();
                    }
                    else if (mLine.getStartY() > mScrHeight) {
                        goToRedirectScreen1();
                    }

                    //Redraws the all the views to the new update values
                    RedrawHandler.post(new Runnable() {
                        public void run() {
                            mLine.invalidate();
                            topRectangle.invalidate();
                            bottomRectangle.invalidate();
                        }

                    });

                }

            }//Run
        };
        mTmr.schedule(mTsk, 0, 5);


        super.onResume();
    }// OnResume

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchable) {

            int action = MotionEventCompat.getActionMasked(event);
            int y = (int) event.getY();

            Log.d("Frame", "The action is " + actionToString(action));
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (action == MotionEvent.ACTION_DOWN) {
                //Top rectangle touched
                if (y < mLine.getStartY()) {
                    if (topRectangle.getColor() == green /*Green*/) {

                        if (!isGameStarted) {
                            if (w == 0) {
                                isGameStarted = true;

                                Player1TextView.setAlpha(0);
                                Player2TextView.setAlpha(0);
                                curvedText.setVisibility(View.GONE);
                                curvedText1.setVisibility(View.GONE);

                                w += 1;
                            }
                        }

                        addPoint();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                topRectangle.setColor(red);
                                bottomRectangle.setColor(green);
                            }
                        });

                        if (movingUp) {
                            movingUp = false;
                        } else {
                            movingUp = true;
                        }

                        player1Hits += 1;
                    }
                    else {
                        player1Misses += 1;
                    }
                }
                //Bottom rectangle touched
                else if (y > mLine.getStartY()) {
                    if (bottomRectangle.getColor() == green /*Green*/) {

                        if (isGameStarted) {
                            addPoint();

                            if (movingUp) {
                                movingUp = false;
                            } else {
                                movingUp = true;
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                topRectangle.setColor(green);
                                bottomRectangle.setColor(red);
                            }
                        });

                        player2Hits += 1;
                    }
                    else {
                        player2Misses += 1;
                    }
                }
            }
            else if (action == MotionEvent.ACTION_POINTER_DOWN) {  /*Fix pointer down red bug!!!!!!*/
                //Top rectangle touched
                if (y > mLine.getStartY()) {
                    Log.d("Frame", "Above the Line");
                    if (topRectangle.getColor() == green /*Green*/) {
                        Log.d("Frame", "Above the Line and Green");

                        if (isGameStarted) {
                            addPoint();

                            if (movingUp) {
                                movingUp = false;
                            } else {
                                movingUp = true;
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                topRectangle.setColor(red);
                                bottomRectangle.setColor(green);
                            }
                        });


                        player1Hits += 1;
                    }
                    else {
                        Log.d("Frame", "Above the Line and Red");
                        player1Misses += 1;
                    }
                }
                //Bottom rectangle touched
                else if (y < mLine.getStartY()) {
                    Log.d("Frame", "Below the Line");
                    if (bottomRectangle.getColor() == green /*Green*/) {
                        Log.d("Frame", "Below the Line and Green");

                        addPoint();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                topRectangle.setColor(green);
                                bottomRectangle.setColor(red);
                            }
                        });

                        if (movingUp) {
                            movingUp = false;
                        } else {
                            movingUp = true;
                        }

                        player2Hits += 1;
                    }
                    else {
                        Log.d("Frame", "Below the Line and Red");
                        player2Misses += 1;
                    }
                }
            }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }

        return super.onTouchEvent(event);
    }


    @Override
    public void onPause() //app moved to background, stop background threads
    {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mTmr.cancel(); //kill\release timer (our only background thread)
        mTmr = null;
        mTsk = null;

        super.onPause();
    }

    private void addPoint() {
        mScoreboard.addPoint();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreBoard.setText(mScoreboard.getScore() + "");
            }
        });
    }

    private void progressDifficulty() {
        if (mScoreboard.getScore() == 10) {
            lineSpeed = mScrHeight / 280;
        }
        else if (mScoreboard.getScore() == 20) {
            lineSpeed = mScrHeight / 240;
        }
        else if (mScoreboard.getScore() == 30) {
            lineSpeed = mScrHeight / 200;
        }
        else if (mScoreboard.getScore() == 40) {
            lineSpeed = mScrHeight / 160;
        }
        else if (mScoreboard.getScore() == 50) {
            lineSpeed = mScrHeight / 140;
        }
        else if (mScoreboard.getScore() == 60) {
            lineSpeed = mScrHeight / 120;
        }
        else if (mScoreboard.getScore() == 70) {
            lineSpeed = mScrHeight / 100;
        }
        else if (mScoreboard.getScore() == 80) {
            lineSpeed = mScrHeight / 90;
        }
    }

    public void goToRedirectScreen1() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLine.setStrokeWidth(mScrHeight / 20);
                mLine.setColor(0xFFFF000C);

                final Animation animation = new AlphaAnimation(1, .4f); // Change alpha from fully visible to invisible
                animation.setDuration(200); // duration - half a second

                topRectangle.setAnimation(animation);
                bottomRectangle.setAnimation(animation);

                animation.setFillAfter(true);
            }
        });

        mp.start();

        isGameStarted = false;
        touchable = false;

        final Animation animation = new AlphaAnimation(1, .2f); // Change alpha from fully visible to invisible
        animation.setDuration(200); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(3); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getBaseContext(), DoublePlayerRedirect.class);
                intent.putExtra("playerThatWon", 1);
                intent.putExtra("score", mScoreboard.getScore());
                intent.putExtra("player1Hits", player1Hits);
                intent.putExtra("player1Misses", player1Misses);
                intent.putExtra("player2Hits", player2Hits);
                intent.putExtra("player2Misses", player2Misses);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLine.startAnimation(animation);
            }
        });
    }

    public void goToRedirectScreen2() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLine.setStrokeWidth(mScrHeight / 20);
                mLine.setColor(0xFFFF000C);

                final Animation animation = new AlphaAnimation(1, .4f); // Change alpha from fully visible to invisible
                animation.setDuration(200); // duration - half a second

                topRectangle.setAnimation(animation);
                bottomRectangle.setAnimation(animation);

                animation.setFillAfter(true);
            }
        });

        mp.start();

        isGameStarted = false;
        touchable = false;

        final Animation animation = new AlphaAnimation(1, .2f); // Change alpha from fully visible to invisible
        animation.setDuration(200); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(3); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getBaseContext(), DoublePlayerRedirect.class);
                intent.putExtra("playerThatWon", 2);
                intent.putExtra("score", mScoreboard.getScore());
                intent.putExtra("player1Hits", player1Hits);
                intent.putExtra("player1Misses", player1Misses);
                intent.putExtra("player2Hits", player2Hits);
                intent.putExtra("player2Misses", player2Misses);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLine.startAnimation(animation);
            }
        });
    }

    // Given an action int, returns a string description
    public static String actionToString(int action) {
        switch (action) {

            case MotionEvent.ACTION_DOWN: return "Down";
            case MotionEvent.ACTION_MOVE: return "Move";
            case MotionEvent.ACTION_POINTER_DOWN: return "Pointer Down";
            case MotionEvent.ACTION_UP: return "Up";
            case MotionEvent.ACTION_POINTER_UP: return "Pointer Up";
            case MotionEvent.ACTION_OUTSIDE: return "Outside";
            case MotionEvent.ACTION_CANCEL: return "Cancel";
        }
        return "";
    }

}
