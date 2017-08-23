package benyamephrem.frameshift.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import benyamephrem.frameshift.R;

public class DoublePlayerRedirect extends AppCompatActivity {

    int mScore, mPlayer1Hits, mPlayer1Misses, mPlayer2Hits, mPlayer2Misses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
        //set app to full screen and keep screen on
        getWindow().setFlags(0xFFFFFFFF, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.double_player_redirect);


        final ImageButton playAgainButton = (ImageButton) findViewById(R.id.playAgainButton);
        TextView playerThatWonTextView = (TextView) findViewById(R.id.playerThatWonTextView);
        TextView turnOffAds = (TextView) findViewById(R.id.turnOffAds);

        TextView Player1TextView = (TextView) findViewById(R.id.Player1TextView);
        TextView player1WinsTextView = (TextView) findViewById(R.id.player1WinsTextView);
        TextView player1Wins = (TextView) findViewById(R.id.player1Wins);
        TextView player1PointsTextView = (TextView) findViewById(R.id.player1PointsTextView);
        TextView player1Points = (TextView) findViewById(R.id.player1Points);
        TextView player1AccuracyTextView = (TextView) findViewById(R.id.player1AccuracyTextView);
        TextView player1Accuracy = (TextView) findViewById(R.id.player1Accuracy);

        TextView Player2TextView = (TextView) findViewById(R.id.Player2TextView);
        TextView player2WinsTextView = (TextView) findViewById(R.id.player2WinsTextView);
        TextView player2Wins = (TextView) findViewById(R.id.player2Wins);
        TextView player2PointsTextView = (TextView) findViewById(R.id.player2PointsTextView);
        TextView player2Points = (TextView) findViewById(R.id.player2Points);
        TextView player2AccuracyTextView = (TextView) findViewById(R.id.player2AccuracyTextView);
        TextView player2Accuracy = (TextView) findViewById(R.id.player2Accuracy);

        ImageButton homeButton = (ImageButton) findViewById(R.id.homeButton);

        //Set Typefaces
        final Typeface type = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        playerThatWonTextView.setTypeface(type);

        final Typeface type1 = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        turnOffAds.setTypeface(type1);
        Player1TextView.setTypeface(type1);
        player1WinsTextView.setTypeface(type1);
        player1Wins.setTypeface(type1);
        player1PointsTextView.setTypeface(type1);
        player1Points.setTypeface(type1);
        player1AccuracyTextView.setTypeface(type1);
        player1Accuracy.setTypeface(type1);
        Player2TextView.setTypeface(type1);
        player2WinsTextView.setTypeface(type1);
        player2Wins.setTypeface(type1);
        player2PointsTextView.setTypeface(type1);
        player2Points.setTypeface(type1);
        player2AccuracyTextView.setTypeface(type1);
        player2Accuracy.setTypeface(type1);

        //Get score and highscore from previous activity
        Intent intent = getIntent();
        int mPlayerThatWon = intent.getIntExtra("playerThatWon", 1);
        int mScore = intent.getIntExtra("score", 0);
        int mPlayer1Hits = intent.getIntExtra("player1Hits", 0);
        int mPlayer1Misses = intent.getIntExtra("player1Misses", 0);
        int mPlayer2Hits = intent.getIntExtra("player2Hits", 0);
        int mPlayer2Misses = intent.getIntExtra("player2Misses", 0);

        //Get persistent data
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int player1TotalPoints = prefs.getInt("player1TotalPoints", 0);
        int player2TotalPoints = prefs.getInt("player2TotalPoints", 0);
        int player1GameWins = prefs.getInt("player1Wins", 0); //0 is the default value
        int player2GameWins = prefs.getInt("player2Wins", 0);
        float player1TotalHits = prefs.getFloat("player1Hits", 0);
        float player1TotalMisses = prefs.getFloat("player1Misses", 0);
        float player2TotalHits = prefs.getFloat("player2Hits", 0);
        float player2TotalMisses = prefs.getFloat("player2Misses", 0);

        //Set texts

            //Player that won textView
            playerThatWonTextView.setText("Player " + mPlayerThatWon + " Wins");

            //# of wins textView
            if (mPlayerThatWon == 1) {
                player1GameWins += 1;
                player1TotalPoints += mScore;

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("player1TotalPoints", player1TotalPoints);
                editor.putInt("player1Wins", player1GameWins);
                editor.apply();
            }
            else if (mPlayerThatWon == 2) {
                player2GameWins += 1;
                player2TotalPoints += mScore;

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("player2TotalPoints", player2TotalPoints);
                editor.putInt("player2Wins", player2GameWins);
                editor.apply();
            }

            player1Wins.setText(player1GameWins + "");
            player2Wins.setText(player2GameWins + "");

            //Total points textViews
            player1Points.setText(player1TotalPoints + "");
            player2Points.setText(player2TotalPoints + "");

            //Accuracy TextView
            float player1H = mPlayer1Hits + player1TotalHits;
            float player2H = mPlayer2Hits + player2TotalHits;
            float player1M = mPlayer1Misses + player1TotalMisses;
            float player2M = mPlayer2Misses + player2TotalMisses;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("player1Hits", player1H);
            editor.putFloat("player1Misses", player2H);
            editor.putFloat("player2Hits", player1M);
            editor.putFloat("player2Misses", player2M);
            editor.apply();

            player1Accuracy.setText(Math.round(((player1H / (player1H + player1M))) * 100) + "%");
            player2Accuracy.setText(Math.round(((player2H / (player2H + player2M)) * 100)) + "%");



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

    }//OnCreate

    @Override
    public void onPause() //app moved to background, stop background threads
    {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        super.onPause();
    }

    public void goToGameScreen() {
        Intent intent = new Intent(this, DoublePlayer.class);
        startActivity(intent);
    }

    public void goToHomeScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
