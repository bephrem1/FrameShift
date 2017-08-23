package benyamephrem.frameshift.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
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
import benyamephrem.frameshift.Classes.BallView;
import benyamephrem.frameshift.Classes.LineView;
import benyamephrem.frameshift.Classes.Rectangle;
import benyamephrem.frameshift.Classes.Scoreboard;
import benyamephrem.frameshift.R;

public class TiltMode extends AppCompatActivity {

    BallView mBallView = null;
    int ballColor = 0xFF60FFA6;
    Scoreboard mScoreboard = new Scoreboard();
    MediaPlayer mp;
    Handler RedrawHandler = new Handler(); //so redraw occurs in main thread
    Timer mTmr = null;
    TimerTask mTsk = null;
    static int mScrWidth, mScrHeight;
    static android.graphics.PointF mBallPos, mBallSpd;
    TextView timeTextView, tapToPlay, flatTextView;
    static boolean isNormalScreen = false,
            isLargeScreen = false;
    private RelativeLayout mainView = null;
    static Random randomGenerator = new Random();

    int mGameTime = 0;

    int black = 0xEA000004;

    boolean isGameStarted = false;
    boolean touchable = true;

    boolean topLineUp = false,
            bottomLineUp = true,
            leftLineLeft = false,
            rightLineLeft = true;

    LineView topKillerLine = null,
            bottomKillerLine = null,
            leftKillerLine = null,
            rightKillerLine = null;

    int killerLineSpeed;

    int topBoundary, bottomBoundary, leftBoundary, rightBoundary;

    Rectangle topKillerRectangle = null,
            bottomKillerRectangle = null,
            leftKillerRectangle = null,
            rightKillerRectangle = null;

    int w = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
        //set app to full screen and keep screen on
        getWindow().setFlags(0xFFFFFFFF, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tilt_mode);


            //create pointer to main screen
            tapToPlay = (TextView) findViewById(R.id.tapToPlay);
            mainView = (RelativeLayout) findViewById(R.id.main_view);
            timeTextView = (TextView)findViewById(R.id.timeTextView);
            timeTextView.setVisibility(View.INVISIBLE);
            flatTextView = (TextView) findViewById(R.id.flatTextView);
            flatTextView.setVisibility(View.INVISIBLE);

        //Set high score
        Intent intent = getIntent();
        mScoreboard.setHighScore(intent.getIntExtra("highScore", 0));

        //Set Typefaces
        final Typeface type = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        tapToPlay.setTypeface(type);
        timeTextView.setTypeface(type);

                //Animate tapToPlay TextView to flash until touched
                final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                animation.setDuration(700);
                animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
                animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                tapToPlay.startAnimation(animation);



            //get screen dimensions
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mScrWidth = metrics.widthPixels;
            mScrHeight = metrics.heightPixels;
            mBallPos = new PointF();
            mBallSpd = new PointF();

        //Set killer line speed class variable
        killerLineSpeed = mScrWidth / 200;

        //Set media player
        mp = MediaPlayer.create(this, R.raw.blopsound);

        //Add views
        bottomKillerLine = new LineView(this, 0, mScrHeight, mScrWidth, mScrHeight, black);
        topKillerLine = new LineView(this, 0, 0, mScrWidth, 0, black);
        leftKillerLine = new LineView(this, 0, 0, 0, mScrHeight, black);
        rightKillerLine = new LineView(this, mScrWidth, 0, mScrWidth, mScrHeight, black);

        topKillerRectangle = new Rectangle(this, 0, 0, mScrWidth, 0, black);
        bottomKillerRectangle = new Rectangle(this, 0, mScrHeight, mScrWidth, mScrHeight, black);
        leftKillerRectangle = new Rectangle(this, 0, 0, 0, mScrHeight, black);
        rightKillerRectangle = new Rectangle(this, mScrWidth, 0, mScrWidth, mScrHeight, black);

        //create initial ball

        mBallView = new BallView(this, mScrWidth / 2, mScrHeight / 2, mScrWidth/15);
        mBallView.setColor(ballColor);

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


            //create variables for ball position and speed
            mBallPos.x = mScrWidth / 2;
            mBallPos.y = mScrHeight / 2;
            mBallSpd.x = 0;
            mBallSpd.y = 0;


        mainView.addView(mBallView); //add ball to main screen
        mBallView.invalidate(); //call onDraw in BallView

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
        mainView.addView(topKillerRectangle);
        topKillerRectangle.invalidate();

        mainView.addView(bottomKillerRectangle);
        bottomKillerRectangle.invalidate();

        mainView.addView(leftKillerRectangle);
        leftKillerRectangle.invalidate();

        mainView.addView(rightKillerRectangle);
        rightKillerRectangle.invalidate();



        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_NORMAL) {

            isNormalScreen = true;

        }
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {

            isLargeScreen = true;

        }

//listener for accelerometer, use anonymous class for simplicity
        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {

                        if (isNormalScreen) {
                            //set ball speed based on phone tilt (ignore Z axis)
                            mBallSpd.x = -event.values[0] * 4;
                            mBallSpd.y = event.values[1] * 4;
                            //timer event will redraw ball
                        }
                        else if (isLargeScreen) {
                            //set ball speed based on phone tilt (ignore Z axis)
                            mBallSpd.x = -event.values[0] * (25/10);
                            mBallSpd.y = event.values[1] * (25/10);
                            //timer event will redraw ball
                        }

                        if (w == 0) {
                            if (event.values[0] > 3 || event.values[0] < -3) {
                                flatTextView.setVisibility(View.VISIBLE);
                            } else {
                                flatTextView.setVisibility(View.INVISIBLE);
                            }

                            if (event.values[1] > 3 || event.values[1] < -3) {
                                flatTextView.setVisibility(View.VISIBLE);
                            } else {
                                flatTextView.setVisibility(View.INVISIBLE);
                            }
                        }


                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    } //ignore
                },
                ((SensorManager) getSystemService(Context.SENSOR_SERVICE))
                        .getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),
                SensorManager.SENSOR_DELAY_NORMAL);


        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (w == 0) {
                    isGameStarted = true;
                    timeTextView.setVisibility(View.VISIBLE);

                    AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                    fadeOut.setDuration(500);
                    fadeOut.setFillAfter(true);
                    tapToPlay.startAnimation(fadeOut);
                    flatTextView.setVisibility(View.INVISIBLE);

                    w += 1;
                }
            }
        });

    }//OnCreate

    @Override
    public void onResume()
    {

        //************Set up rectangle moving mechanism and Logic************

        mTmr = new Timer();
        mTsk = new TimerTask() {
            @Override
            public void run() {

                if (isGameStarted) {

                    runCircle();

                    //Speed up game line speed
                    progressDifficulty();

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



                    final float boardWidth = (timeTextView.getRight() - timeTextView.getLeft()) / 2;
                    final float boardHeight = (timeTextView.getBottom() - timeTextView.getTop()) / 2;

                    //Update rectangle view to match line
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            topKillerRectangle.setBottom(topKillerLine.getStartY());
                            bottomKillerRectangle.setTop(bottomKillerLine.getStopY());
                            leftKillerRectangle.setRight(leftKillerLine.getStartX());
                            rightKillerRectangle.setLeft(rightKillerLine.getStartX());

                            timeTextView.setY(((topKillerRectangle.getTheBottom() + bottomKillerRectangle.getTheTop()) / 2) - boardHeight );
                            timeTextView.setX(((leftKillerRectangle.getTheRight() + rightKillerRectangle.getTheLeft()) / 2) - boardWidth  );


                        }
                    });

                    //Player Dies if ball hits edges
                    if (intersects(mBallView, bottomKillerRectangle) ||
                        intersects(mBallView, topKillerRectangle)||
                        intersects(mBallView, leftKillerRectangle)||
                        intersects(mBallView, rightKillerRectangle)) {
                        goToRedirectScreen();
                        mBallView.setColor(0xFFFF0000);
                    }


                    //Redraws the all the views to the new update values
                    RedrawHandler.post(new Runnable() {
                        public void run() {
                            topKillerLine.invalidate();
                            bottomKillerLine.invalidate();
                            leftKillerLine.invalidate();
                            rightKillerLine.invalidate();

                            topKillerRectangle.invalidate();
                            bottomKillerRectangle.invalidate();
                            leftKillerRectangle.invalidate();
                            rightKillerRectangle.invalidate();

                            timeTextView.invalidate();
                        }

                    });

                    mGameTime += 10;

                    if (mGameTime % 1000 == 0) {
                        addPoint();
                    }
                }

            }//run
        };
        mTmr.schedule(mTsk, 0, 10); //start timer (Timer 2)


        super.onResume();
    } // onResume

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
                timeTextView.setText(mScoreboard.getScore() + "");
            }
        });
    }

    private void runCircle() {

        //move ball based on current speed
        mBallPos.x += mBallSpd.x;
        mBallPos.y += mBallSpd.y;

        //if ball goes off screen, reposition to opposite side of screen
        if (mBallPos.x > mScrWidth) mBallPos.x = 0;
        if (mBallPos.y > mScrHeight) mBallPos.y = 0;
        if (mBallPos.x < 0) mBallPos.x = mScrWidth;
        if (mBallPos.y < 0) mBallPos.y = mScrHeight;

        //update ball class instance
        mBallView.x = mBallPos.x;
        mBallView.y = mBallPos.y;

        //redraw ball. Must run in background thread to prevent thread lock.
        RedrawHandler.post(new Runnable() {
            public void run() {
                mBallView.invalidate();
            }
        });

    }

    public boolean intersects(BallView ball, Rectangle rect){
        int ballRadius = ball.getR();
        int ballY = (int) ball.getY();
        int ballX = (int) ball.getX();

        double circleDistanceX;
        double circleDistanceY;
        double cornerDistance_sq;


        circleDistanceX = Math.abs(ballX - rect.getCenterX());
        circleDistanceY = Math.abs(ballY - rect.getCenterY());

        if (circleDistanceX > (rect.getRectWidth() / 2 + ballRadius)) { return false; }
        if (circleDistanceY > (rect.getRectHeight() / 2 + ballRadius)) { return false; }

        if (circleDistanceX <= (rect.getRectWidth() / 2)) { return true; }
        if (circleDistanceY <= (rect.getRectHeight() / 2)) { return true; }

        cornerDistance_sq = Math.pow((circleDistanceX - rect.getRectWidth()/2), 2) +
                Math.pow((circleDistanceY - rect.getRectHeight()/2), 2);

        return (cornerDistance_sq <= Math.pow(ball.getR(), 2));

    }

    private void progressDifficulty() {
        if (mScoreboard.getScore() == 5) {
            killerLineSpeed = mScrWidth / 190;
        }
        else if (mScoreboard.getScore() == 10) {
            killerLineSpeed = mScrWidth / 180;
        }
        else if (mScoreboard.getScore() == 15) {
            killerLineSpeed = mScrWidth / 170;
        }
        else if (mScoreboard.getScore() == 20) {
            killerLineSpeed = mScrWidth / 160;
        }
        else if (mScoreboard.getScore() == 25) {
            killerLineSpeed = mScrWidth / 150;
        }
        else if (mScoreboard.getScore() == 30) {
            killerLineSpeed = mScrWidth / 140;
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

    public void goToRedirectScreen() {
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
                SharedPreferences prefs = getBaseContext().getSharedPreferences("myPrefsKey1", Context.MODE_PRIVATE);
                int highScore = prefs.getInt("highScore", 0); //0 is the default value
                mScoreboard.setHighScore(highScore);

                if (mScoreboard.getScore() > highScore) {
                    mScoreboard.setHighScore(mScoreboard.getScore());
                }

                Intent intent = new Intent(getBaseContext(), TiltModeRedirect.class);
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
                mBallView.startAnimation(animation);
            }
        });
    }

}
