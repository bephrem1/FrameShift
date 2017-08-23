package benyamephrem.frameshift.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import benyamephrem.frameshift.Classes.LineView;
import benyamephrem.frameshift.Classes.Rectangle;
import benyamephrem.frameshift.Classes.Scoreboard;
import benyamephrem.frameshift.R;


public class SinglePlayer extends AppCompatActivity {

    private RelativeLayout mainView;
    MediaPlayer mp;
    Scoreboard mScoreboard = new Scoreboard();
    TextView scoreBoard, wrongSideTextView;
    public static TextView  tapToPlay;
    static Random randomGenerator = new Random();

    LineView mLine = null,
            topKillerLine = null,
            bottomKillerLine = null,
            leftKillerLine = null,
            rightKillerLine = null;

    Rectangle topRectangle = null,
            bottomRectangle = null,
            topKillerRectangle = null,
            bottomKillerRectangle = null,
            leftKillerRectangle = null,
            rightKillerRectangle = null;

    static int mScrWidth, mScrHeight;
    Timer mTmr = null;
    TimerTask mTsk = null;
    Handler RedrawHandler = new Handler(); //so redraw occurs in main thread

    boolean isGameStarted = false;
    boolean touchable = true;
    boolean movingUp = true;

    boolean topLineUp = false,
            bottomLineUp = true,
            leftLineLeft = false,
            rightLineLeft = true;

    int green = 0xBA96FFA7;
    int red = 0xB7FF9A99;
    int black = 0xEA000004;

    int lineSpeed;
    int killerLineSpeed;

    int topBoundary, bottomBoundary, leftBoundary, rightBoundary;

    int w;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
        //set app to full screen and keep screen on
        getWindow().setFlags(0xFFFFFFFF, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_player);


        //Declare views
        mainView = (RelativeLayout) findViewById(R.id.main_view);
        scoreBoard = (TextView) findViewById(R.id.scoreBoard);
        tapToPlay = (TextView) findViewById(R.id.tapToPlay);
        wrongSideTextView = (TextView) findViewById(R.id.wrongSideTextView);

        //Set high score
        Intent intent = getIntent();
        mScoreboard.setHighScore(intent.getIntExtra("highScore", 0));

        //Set Typefaces
        final Typeface type = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        scoreBoard.setTypeface(type);
        tapToPlay.setTypeface(type);

        //Set bottom textview invisible
        wrongSideTextView.setTypeface(type);
        wrongSideTextView.setVisibility(View.INVISIBLE);
        
                //Animate tapToPlay TextView to flash until touched
                final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                animation.setDuration(700); // duration - half a second
                animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
                animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                tapToPlay.startAnimation(animation);

        //get screen dimensions
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScrWidth = metrics.widthPixels;
        mScrHeight = metrics.heightPixels;

        //Set line speed class variables
        lineSpeed = mScrHeight / 300;
        killerLineSpeed = mScrWidth / 250;

        //Set media player
        mp = MediaPlayer.create(this, R.raw.blopsound);

        mLine = new LineView(this, 0, mScrHeight / 2, mScrWidth, mScrHeight / 2, 0xFF000000, mScrHeight / 50 );
        bottomKillerLine = new LineView(this, 0, mScrHeight, mScrWidth, mScrHeight, black);
        topKillerLine = new LineView(this, 0, 0, mScrWidth, 0, black);
        leftKillerLine = new LineView(this, 0, 0, 0, mScrHeight, black);
        rightKillerLine = new LineView(this, mScrWidth, 0, mScrWidth, mScrHeight, black);

        topRectangle = new Rectangle(this, 0, 0, mScrWidth, mScrHeight/2, green);
        bottomRectangle = new Rectangle(this, 0, mScrHeight /2, mScrWidth, mScrHeight, red );
        topKillerRectangle = new Rectangle(this, 0, 0, mScrWidth, 0, black);
        bottomKillerRectangle = new Rectangle(this, 0, mScrHeight, mScrWidth, mScrHeight, black);
        leftKillerRectangle = new Rectangle(this, 0, 0, 0, mScrHeight, black);
        rightKillerRectangle = new Rectangle(this, mScrWidth, 0, mScrWidth, mScrHeight, black);

        int randomNumber = randomGenerator.nextInt(4);

        if (randomNumber == 0) {
            //top left biased
            topBoundary = (mScrHeight / 11) * 2;
            bottomBoundary = (mScrHeight / 11) * 6;
            leftBoundary = (mScrWidth / 11) * 3;
            rightBoundary = (mScrWidth / 11) * 6;
        }

        else if (randomNumber == 1) {
            //top right biased
            topBoundary = (mScrHeight / 11) * 2;
            bottomBoundary = (mScrHeight / 11) * 6;
            leftBoundary = (mScrWidth / 11) * 7;
            rightBoundary = (mScrWidth / 11) * 10;
        }

        else if (randomNumber == 2) {
            //bottom left biased
            topBoundary = (mScrHeight / 11) * 6;
            bottomBoundary = (mScrHeight / 11) * 10;
            leftBoundary = (mScrWidth / 11) * 3;
            rightBoundary = (mScrWidth / 11) * 6;
        }

        else if (randomNumber == 3) {
            //bottom right biased
            topBoundary = (mScrHeight / 11) * 6;
            bottomBoundary = (mScrHeight / 11) * 10;
            leftBoundary = (mScrWidth / 11) * 7;
            rightBoundary = (mScrWidth / 11) * 10;
        }

        //GameLine
        mainView.addView(mLine);
        mLine.invalidate();


        //Lines
        mainView.addView(topKillerLine);
        topKillerLine.invalidate();

        mainView.addView(bottomKillerLine);
        bottomKillerLine.invalidate();

        mainView.addView(leftKillerLine);
        leftKillerLine.invalidate();

        mainView.addView(rightKillerLine);
        rightKillerLine.invalidate();


        //Rectangles
        mainView.addView(topRectangle);
        topRectangle.invalidate();

        mainView.addView(bottomRectangle);
        bottomRectangle.invalidate();

        mainView.addView(topKillerRectangle);
        topKillerRectangle.invalidate();

        mainView.addView(bottomKillerRectangle);
        bottomKillerRectangle.invalidate();

        mainView.addView(leftKillerRectangle);
        leftKillerRectangle.invalidate();

        mainView.addView(rightKillerRectangle);
        rightKillerRectangle.invalidate();

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

                    //Move killer lines

                        if (!topLineUp) {
                            topKillerLine.setStartY(topKillerLine.getStartY() + (killerLineSpeed));
                            topKillerLine.setStopY(topKillerLine.getStopY() + (killerLineSpeed));
                        }


                        if (topLineUp) {
                            topKillerLine.setStartY(topKillerLine.getStartY() - (killerLineSpeed));
                            topKillerLine.setStopY(topKillerLine.getStopY() - (killerLineSpeed));
                        }


                        if (bottomLineUp) {
                            bottomKillerLine.setStartY(bottomKillerLine.getStartY() - (killerLineSpeed));
                            bottomKillerLine.setStopY(bottomKillerLine.getStopY() - (killerLineSpeed));
                        }


                       if (!bottomLineUp) {
                           bottomKillerLine.setStartY(bottomKillerLine.getStartY() + (killerLineSpeed));
                           bottomKillerLine.setStopY(bottomKillerLine.getStopY() + (killerLineSpeed));
                       }

                    if (leftLineLeft) {
                        leftKillerLine.setStartX(leftKillerLine.getStartX() - (killerLineSpeed));
                        leftKillerLine.setStopX(leftKillerLine.getStopX() - (killerLineSpeed));
                    }


                    if (!leftLineLeft) {
                        leftKillerLine.setStartX(leftKillerLine.getStartX() + (killerLineSpeed));
                        leftKillerLine.setStopX(leftKillerLine.getStopX() + (killerLineSpeed));
                    }

                    if (rightLineLeft) {
                        rightKillerLine.setStartX(rightKillerLine.getStartX() - (killerLineSpeed));
                        rightKillerLine.setStopX(rightKillerLine.getStopX() - (killerLineSpeed));
                    }


                    if (!rightLineLeft) {
                        rightKillerLine.setStartX(rightKillerLine.getStartX() + (killerLineSpeed));
                        rightKillerLine.setStopX(rightKillerLine.getStopX() + (killerLineSpeed));
                    }




                    //Set killer line movement    *******Adjust for ads if you need to make room*******
                    if (topKillerLine.getStartY() <= 0) {
                        topLineUp = false;
                    }
                    else if (topKillerLine.getStartY() >= topBoundary) {
                        topLineUp = true;
                    }

                    if (bottomKillerLine.getStartY() >= mScrHeight) {
                        bottomLineUp = true;
                    }
                    else if (bottomKillerLine.getStartY() <= bottomBoundary) {
                        bottomLineUp = false;
                    }

                    if (leftKillerLine.getStartX() <= 0) {
                        leftLineLeft = false;
                    }
                    else if (leftKillerLine.getStartX() >= leftBoundary) {
                        leftLineLeft = true;
                    }

                    if (rightKillerLine.getStartX() >= mScrWidth) {
                        rightLineLeft = true;
                    }
                    else if (rightKillerLine.getStartX() <= rightBoundary) {
                        rightLineLeft = false;
                    }


                    final float boardWidth = (scoreBoard.getRight() - scoreBoard.getLeft()) / 2;
                    final float boardHeight = (scoreBoard.getBottom() - scoreBoard.getTop()) / 2;

                    //Update rectangle view to match line
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            topRectangle.setBottom(mLine.getStartY());
                            bottomRectangle.setTop(mLine.getStopY());
                            topKillerRectangle.setBottom(topKillerLine.getStartY());
                            bottomKillerRectangle.setTop(bottomKillerLine.getStopY());
                            leftKillerRectangle.setRight(leftKillerLine.getStartX());
                            rightKillerRectangle.setLeft(rightKillerLine.getStartX());

                            scoreBoard.setY(((topKillerRectangle.getTheBottom() + bottomKillerRectangle.getTheTop()) / 2) - boardHeight );
                            scoreBoard.setX(((leftKillerRectangle.getTheRight() + rightKillerRectangle.getTheLeft()) / 2) - boardWidth  );


                        }
                    });

                    //Player Dies if game like hits kill zone
                    if (mLine.getStartY() < topKillerLine.getStartY() || mLine.getStartY() > bottomKillerLine.getStartY() ) {
                        goToRedirectScreen1();
                    }

                    //Redraws the all the views to the new update values
                    RedrawHandler.post(new Runnable() {
                        public void run() {
                            mLine.invalidate();

                            topKillerLine.invalidate();
                            bottomKillerLine.invalidate();
                            leftKillerLine.invalidate();
                            rightKillerLine.invalidate();

                            topRectangle.invalidate();
                            bottomRectangle.invalidate();

                            topKillerRectangle.invalidate();
                            bottomKillerRectangle.invalidate();
                            leftKillerRectangle.invalidate();
                            rightKillerRectangle.invalidate();

                            scoreBoard.invalidate();
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
            int eventaction = event.getAction();
            int y = (int) event.getY();
            int x = (int) event.getX();
            switch (eventaction) {
                case MotionEvent.ACTION_DOWN:
                    //Top rectangle touched
                    if (y < mLine.getStartY() && y > topKillerLine.getStartY() && x > leftKillerLine.getStartX() && x < rightKillerLine.getStartX()) {
                        if (topRectangle.getColor() == green /*Green*/) {

                            //Starts he game and fades out textView
                            if (!isGameStarted) {
                                if (w == 0) {
                                    isGameStarted = true;

                                    AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                                    fadeOut.setDuration(500);
                                    fadeOut.setFillAfter(true);
                                    tapToPlay.startAnimation(fadeOut);

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
                        } else {
                            goToRedirectScreen2();
                        }
                    }
                    //Bottom rectangle touched
                    else if (y > mLine.getStartY() && y < bottomKillerLine.getStartY() && x > leftKillerLine.getStartX() && x < rightKillerLine.getStartX()) {
                        if (bottomRectangle.getColor() == green /*Green*/) {

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
                        } else {
                            if (isGameStarted) {
                                goToRedirectScreen3();
                            }
                            //Player hit the bottom...show "wrong side" TextView
                            else {
                                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                                wrongSideTextView.startAnimation(fadeIn);
                                wrongSideTextView.startAnimation(fadeOut);
                                fadeIn.setDuration(500);
                                fadeIn.setFillAfter(true);
                                fadeOut.setDuration(500);
                                fadeOut.setFillAfter(true);
                                fadeOut.setStartOffset(500 + fadeIn.getStartOffset());
                                ;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
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

        if (mScoreboard.getScore() % 5 == 0) {
            int randomNumber = randomGenerator.nextInt(4);

            if (randomNumber == 0) {
                //top left biased
                topBoundary = (mScrHeight / 11) * 2;
                bottomBoundary = (mScrHeight / 11) * 6;
                leftBoundary = (mScrWidth / 11) * 3;
                rightBoundary = (mScrWidth / 11) * 6;
            }

            else if (randomNumber == 1) {
                //top right biased
                topBoundary = (mScrHeight / 11) * 2;
                bottomBoundary = (mScrHeight / 11) * 6;
                leftBoundary = (mScrWidth / 11) * 7;
                rightBoundary = (mScrWidth / 11) * 10;
            }

            else if (randomNumber == 2) {
                //bottom left biased
                topBoundary = (mScrHeight / 11) * 6;
                bottomBoundary = (mScrHeight / 11) * 10;
                leftBoundary = (mScrWidth / 11) * 3;
                rightBoundary = (mScrWidth / 11) * 6;
            }

            else if (randomNumber == 3) {
                //bottom right biased
                topBoundary = (mScrHeight / 11) * 6;
                bottomBoundary = (mScrHeight / 11) * 10;
                leftBoundary = (mScrWidth / 11) * 7;
                rightBoundary = (mScrWidth / 11) * 10;
            }
        }
    }

    public void goToRedirectScreen1() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLine.setStrokeWidth(mScrHeight / 20);
                mLine.setColor(0xFFFF000C);

                final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
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
                //getting preferences
                SharedPreferences prefs = getBaseContext().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
                int highScore = prefs.getInt("highScore", 0); //0 is the default value
                mScoreboard.setHighScore(highScore);

                if (mScoreboard.getScore() > highScore) {
                    mScoreboard.setHighScore(mScoreboard.getScore());
                }

                Intent intent = new Intent(getBaseContext(), SinglePlayerRedirect.class);
                intent.putExtra("score", mScoreboard.getScore() + "");
                intent.putExtra("highScore", mScoreboard.getHighScore() + "");

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
                //getting preferences
                SharedPreferences prefs = getBaseContext().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
                int highScore = prefs.getInt("highScore", 0); //0 is the default value
                mScoreboard.setHighScore(highScore);

                if (mScoreboard.getScore() > highScore) {
                    mScoreboard.setHighScore(mScoreboard.getScore());
                }

                Intent intent = new Intent(getBaseContext(), SinglePlayerRedirect.class);
                intent.putExtra("score", mScoreboard.getScore() + "");
                intent.putExtra("highScore", mScoreboard.getHighScore() + "");

                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        topRectangle.startAnimation(animation);

    }

    public void goToRedirectScreen3() {
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
                //getting preferences
                SharedPreferences prefs = getBaseContext().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
                int highScore = prefs.getInt("highScore", 0); //0 is the default value
                mScoreboard.setHighScore(highScore);

                if (mScoreboard.getScore() > highScore) {
                    mScoreboard.setHighScore(mScoreboard.getScore());
                }

                Intent intent = new Intent(getBaseContext(), SinglePlayerRedirect.class);
                intent.putExtra("score", mScoreboard.getScore() + "");
                intent.putExtra("highScore", mScoreboard.getHighScore() + "");

                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bottomRectangle.startAnimation(animation);
    }

}
