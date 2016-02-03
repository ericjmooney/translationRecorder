package wycliffeassociates.recordingapp.AudioVisualization;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import wycliffeassociates.recordingapp.Playback.WavPlayer;
import wycliffeassociates.recordingapp.R;

public abstract class CanvasView extends View {

    protected Paint mPaint;
    int fps = 0;
    protected boolean doneDrawing = false;
    protected UIDataManager mManager;
    protected GestureDetectorCompat mDetector;
    protected Paint mPaintText;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(mDetector!= null)
            mDetector.onTouchEvent(ev);
        return true;
    }

    public boolean isDoneDrawing(){
        return doneDrawing;
    }

    public void setIsDoneDrawing(boolean c){
        doneDrawing = c;
    }

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        init();
    }

    public int getFps(){
        return fps;
    }

    public void resetFPS(){
        fps = 0;
    }

    protected void init(){
        mPaint = new Paint();
        mPaint.setColor(Color.DKGRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1f);

        mPaintText = new Paint();
        mPaintText.setTextSize(28.f);
        mPaintText.setColor(Color.GREEN);

    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(getResources().getColor(R.color.bright_blue));
        mPaint.setStrokeWidth(3f);
        canvas.drawLine(0.f, this.getMeasuredHeight() / 2, this.getMeasuredWidth(), this.getMeasuredHeight() / 2, mPaint);
    }

    public synchronized void drawWaveform(float[] samples, Canvas canvas){
        mPaint.setStrokeWidth(1.5f);
        mPaint.setColor(getResources().getColor(R.color.off_white));
        canvas.drawLines(samples, mPaint);
        fps++;
        doneDrawing = true;
    }

    public void redraw(){
        if(WavPlayer.isPlaying())
        mManager.updateUI();
    }

    public void drawPlaybackSection(Canvas c, int start, int end){
        mPaint.setColor(getResources().getColor(R.color.dark_moderate_lime_green));
        c.drawLine(start, 0, start, c.getHeight(), mPaint);
        mPaint.setColor(getResources().getColor(R.color.vivid_red));
        c.drawLine(end, 0, end, c.getHeight(), mPaint);
    }

    public void setUIDataManager(UIDataManager manager){
        mManager = manager;
    }

}
