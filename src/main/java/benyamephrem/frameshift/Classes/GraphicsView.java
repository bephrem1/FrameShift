package benyamephrem.frameshift.Classes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * Created by Vista on 5/22/15.
 */
public class GraphicsView extends View {
    private static final String QUOTE = "Cross the Opponent's Line !";
    private Path circle;
    private Paint tPaint;

    public GraphicsView(Context context) {
        super(context);
    }

    public void setUpView(int screenWidth, int screenHeight) {

        circle = new Path();
        circle.addCircle(screenWidth / 2, screenHeight / 2, screenHeight / 5, Path.Direction.CW);

        tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setStyle(Paint.Style.STROKE);
        tPaint.setColor(0x826B6A68);
        tPaint.setTextSize(screenHeight/25);
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawTextOnPath(QUOTE, circle, 200, 20, tPaint);
    }

}
