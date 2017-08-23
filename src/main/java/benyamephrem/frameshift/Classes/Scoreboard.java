package benyamephrem.frameshift.Classes;

/**
 * Created by Vista on 5/14/15.
 */
public class Scoreboard {
    private int mScore = 0;
    private int mHighScore = 0;

    public void addPoint() {
        mScore += 1;
    }

    public void subtractPoint() {
        mScore -= 1;
    }

    public int getHighScore() {
        return mHighScore;
    }

    public void setHighScore(int highScore) {
        mHighScore = highScore;
    }

    public int getScore() {
        return mScore;
    }

}
