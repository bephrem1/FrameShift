package benyamephrem.frameshift.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import benyamephrem.frameshift.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
        //set app to full screen and keep screen on
        getWindow().setFlags(0xFFFFFFFF, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView title = (TextView) findViewById(R.id.gameTextView);
        Button doublePlayerButton = (Button) findViewById(R.id.doublePlayerButton);
        Button singlePlayerButton = (Button) findViewById(R.id.singlePlayerButton);
        Button tiltModeButton = (Button) findViewById(R.id.tiltModeButton);
        Button leaderboardButton = (Button) findViewById(R.id.leaderboardButton);
        Button rateButton = (Button) findViewById(R.id.rateButton);
        Button tweetButton = (Button) findViewById(R.id.tweetButton);
        Button adsButton = (Button) findViewById(R.id.adsButton);

        //Set Typeface
        final Typeface type = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        title.setTypeface(type);

        final Typeface type1 = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        doublePlayerButton.setTypeface(type1);
        singlePlayerButton.setTypeface(type1);
        tiltModeButton.setTypeface(type1);
        leaderboardButton.setTypeface(type1);
        rateButton.setTypeface(type1);
        tweetButton.setTypeface(type1);
        adsButton.setTypeface(type1);

        doublePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToDoublePlayer();
            }
        });

        singlePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSinglePlayer();
            }
        });

        tiltModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTiltMode();
            }
        });

    }//OnCreate

    @Override
    public void onPause() //app moved to background, stop background threads
    {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        super.onPause();
    }

    public void goToTiltMode() {
        Intent intent = new Intent(this, TiltMode.class);
        startActivity(intent);
    }

    public void goToSinglePlayer() {
        Intent intent = new Intent(this, SinglePlayer.class);
        startActivity(intent);
    }

    public void goToDoublePlayer() {
        Intent intent = new Intent(this, DoublePlayer.class);
        startActivity(intent);
    }

}
