package ku.iui.imotion.socceruserstudy;

/**
 * Created by ozymaxx on 12.07.2016.
 * taken from https://examples.javacodegeeks.com/android/core/graphics/canvas-graphics/android-canvas-example/
 * why would I invent the wheel from scratch? :D
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.YELLOW;

public class CanvasView extends ImageView {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private ArrayList<Path> mPaths;
    Context context;
    private ArrayList<Paint> mPaints;
    private float mX, mY;
    private static final float TOLERANCE = 0;

    private Sketch sketch;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        mPaths = new ArrayList<Path>();
        mPaints = new ArrayList<Paint>();

        // we set a new Path
        Path mPath = new Path();
        mPaths.add(mPath);

        // and we set a new Paint with the desired attributes
        mPaints.add(newPaint(WHITE,4f));

        sketch = new Sketch();
    }

    public Paint newPaint(int c,float strokeWidth) {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(c);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(strokeWidth);

        return mPaint;
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw

        for (int i = 0; i < mPaths.size(); i++) {
            canvas.drawPath(mPaths.get(i), mPaints.get(i));
        }
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        mPaths.get(mPaths.size()-1).moveTo(x, y);
        mX = x;
        mY = y;

        sketch.newStroke();
        sketch.addPoint(x,y);
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPaths.get(mPaths.size()-1).quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            sketch.addPoint(x,y);
        }
    }

    public void clearCanvas() {
        for (Path mPath : mPaths) {
            mPath.reset();
        }

        mPaths = new ArrayList<Path>();
        mPaths.add(new Path());

        mPaints = new ArrayList<Paint>();
        mPaints.add(newPaint(WHITE,4f));

        invalidate();

        sketch = new Sketch();
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        mPaths.get(mPaths.size()-1).lineTo(mX, mY);

        sketch.addPoint(mX,mY);
        sketch.leaveStroke();

        mPaths.add(new Path());

        Paint mPaint = mPaints.get(mPaints.size()-1);
        mPaints.add(newPaint(mPaint.getColor(),mPaint.getStrokeWidth()));
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }

    public void changeModeAndColor(int color) {
        Paint mPaint = mPaints.get(mPaints.size()-1);

        switch (color) {
            case 1:
                mPaint.setColor(Color.argb(150,0xcc,0,0));
                mPaint.setStrokeWidth(6f);
                break;
            case 2:
                mPaint.setColor(Color.argb(150,0xff,0xff,0));
                mPaint.setStrokeWidth(6f);
                break;
            case 3:
                mPaint.setColor(Color.argb(150,0,0x99,0xcc));
                mPaint.setStrokeWidth(6f);
                break;
            case 4:
                mPaint.setColor(Color.argb(150,0xaa,0x66,0xcc));
                mPaint.setStrokeWidth(6f);
                break;
            case 5:
                mPaint.setColor(Color.argb(150,0x70,0x06,0x06));
                mPaint.setStrokeWidth(6f);
                break;
            case 6:
                mPaint.setColor(WHITE);
                mPaint.setStrokeWidth(4f);
                break;
        }
    }
}
