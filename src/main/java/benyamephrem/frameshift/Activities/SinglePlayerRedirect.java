package benyamephrem.frameshift.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import benyamephrem.frameshift.R;

public class SinglePlayerRedirect extends AppCompatActivity {

    String mScore; //Current score just reached
    String mHighScore;

    //Persistent HighScore
    int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
        //set app to full screen and keep screen on
        getWindow().setFlags(0xFFFFFFFF, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_player_redirect);


        final ImageButton playAgainButton = (ImageButton) findViewById(R.id.playAgainButton);
        Button newBestScoreView = (Button) findViewById(R.id.newBestScoreView);
        ImageButton homeButton = (ImageButton) findViewById(R.id.homeButton);
        TextView gameOverTextView = (TextView) findViewById(R.id.playerThatWonTextView);
        TextView scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        TextView bestScoreTextView = (TextView) findViewById(R.id.bestScoreTextView);
        TextView rewardTextView = (TextView) findViewById(R.id.rewardTextView);
        TextView turnOffAds = (TextView) findViewById(R.id.turnOffAds);
        TextView currentScore = (TextView) findViewById(R.id.currentScore);
        TextView bestScore = (TextView) findViewById(R.id.bestScore);
        ImageView imageView = (ImageView) findViewById(R.id.rewardImageView);
        Drawable rewardCircle = imageView.getDrawable();

        //Set Typefaces
        final Typeface type = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        gameOverTextView.setTypeface(type);
        newBestScoreView.setTypeface(type);

        final Typeface type1 = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        scoreTextView.setTypeface(type1);
        bestScoreTextView.setTypeface(type1);
        rewardTextView.setTypeface(type1);
        turnOffAds.setTypeface(type1);
        currentScore.setTypeface(type1);
        bestScore.setTypeface(type1);

        //Set invisible until needed
        newBestScoreView.setVisibility(View.INVISIBLE);

        //Get score and highscore from previous activity
        Intent intent = getIntent();
        mScore = intent.getStringExtra("score");
        mHighScore = intent.getStringExtra("highScore");

        //Get persistent high score
        SharedPreferences prefs1 = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        highScore = prefs1.getInt("highScore", 0); //0 is the default value

        //check if score was a new highscore
        if (highScore < Integer.parseInt(mScore)){
            newBestScoreView.setVisibility(View.VISIBLE);
        }

        //Update highscore data persistence
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("highScore", Integer.parseInt(mHighScore));
        editor.apply();

        //Set Score TextViews
        currentScore.setText(mScore);
        bestScore.setText(mHighScore);

        //Set reward circle
        if (Integer.parseInt(mScore) > 40) {
            rewardCircle = rewardCircle.mutate();
            rewardCircle.setColorFilter(0xFFE8CB9B, PorterDuff.Mode.MULTIPLY);
        }
        else if (Integer.parseInt(mScore) > 700) {
            rewardCircle = rewardCircle.mutate();
            rewardCircle.setColorFilter(0xFFD7D5C5, PorterDuff.Mode.MULTIPLY);
        }
        else if (Integer.parseInt(mScore) > 100) {
            rewardCircle = rewardCircle.mutate();
            rewardCircle.setColorFilter(0xFFFFFC06, PorterDuff.Mode.MULTIPLY);
        }

        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToGameScreen();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHomeScreen();
            }
        });

    } //onCreate

    @Override
    public void onPause() //app moved to background, stop background threads
    {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        super.onPause();
    }


    public void goToGameScreen() {
        Intent intent = new Intent(this, SinglePlayer.class);
        intent.putExtra("highScore", highScore);
        startActivity(intent);
    }

    public void goToHomeScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
