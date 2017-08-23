package benyamephrem.frameshift.Classes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Vista on 5/12/15.
 */

public class LineView extends View {
    float startX;
    float startY;
    float stopX;
    float stopY;
    public float strokeWidth;
    public int mColor;
    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LineView(Context context, float startX, float startY, float stopX, float stopY, int color, int strokeWidth) {
        super(context);
        mPaint.setColor(color);  //not transparent. color is white
        mColor = color;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
        this.strokeWidth = strokeWidth;
        mPaint.setStrokeWidth(strokeWidth);
    }

    public LineView(Context context, float startX, float startY, float stopX, float stopY, int color) {
        super(context);
        mPaint.setColor(color);  //not transparent. color is white
        mColor = color;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(startX, startY, stopX, stopY, mPaint);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getStopX() {
        return stopX;
    }

    public void setStopX(float stopX) {
        this.stopX = stopX;
    }

    public float getStopY() {
        return stopY;
    }

    public void setStopY(float stopY) {
        this.stopY = stopY;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        mPaint.setStrokeWidth(strokeWidth);
        this.strokeWidth = strokeWidth;
    }
}
