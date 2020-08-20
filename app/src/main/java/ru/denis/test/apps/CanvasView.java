package ru.denis.test.apps;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class CanvasView extends View {

    private static final String DBG_TAG = "DBG_MAIN";

    private Paint paint;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;

    Point bitmap_position;
    Point bitmap_size;

    PointF TouchPointStart, SecondTouchPointStart;
    PointF TouchPointPrev, SecondTouchPointPrev;
    int PointerId, PointerIdSecond;
    PointF TouchVector, TouchVectorSecond;

    PointF activeMoveOffset;

    int scale = 1;

    Bitmap tmpResizeBitmap;


    public CanvasView(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
        init();
        get_screen_size(ctx);
    }

    private void init()
    {
        //points
        bitmap_position = new Point(0,0);
        TouchPointStart = new PointF(0,0);
        TouchPointPrev = new PointF(0,0);
        TouchVector = new PointF(0,0);
        PointerId = -1;
        SecondTouchPointStart = new PointF(0,0);
        SecondTouchPointPrev = new PointF(0,0);
        TouchVectorSecond = new PointF(0,0);
        PointerIdSecond = -1;
        activeMoveOffset = new PointF(0,0);

        //draw
        paint = new Paint();
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);

        bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);
        bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.drawColor(Color.GRAY);

    }
    private void get_screen_size(Context ctx){
        WindowManager manager = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
    }

    protected void drawResizePoints(Context ctx)
    {

    }

    protected double Norma(PointF vec)
    {
        return Math.sqrt(vec.x*vec.x + vec.y+vec.y);
    }

    protected float Mult(PointF A, PointF B)
    {
        return  A.x*B.x + A.y*B.y;
    }
    protected PointF Diff(PointF A, PointF B)
    {
        PointF diff = new PointF();
        diff.x = A.x - B.x;
        diff.y = A.y - B.y;
        return  diff;
    }

    protected double GetDeg(PointF A, PointF B)
    {
        Log.i(DBG_TAG, "mult"+Double.toString(Mult(A,B)));
        Log.i(DBG_TAG, "normA"+Double.toString(Norma(A)));
        Log.i(DBG_TAG, "normB"+Double.toString(Norma(B)));
        double cos = Mult(A,B) / (Norma(A) * Norma(B));
        Log.i(DBG_TAG, "cos"+Double.toString(cos));
        double angle = Math.cos(cos) * 180.0 / Math.PI;
        return angle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, bitmap_position.x, bitmap_position.y, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int pointerIndex = event.getActionIndex();

        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                Log.i(DBG_TAG, "First Point");
                //save touch start coord
                TouchPointStart.x = event.getX();
                TouchPointStart.y = event.getY();
                //startPoint also prevPoint to ACTION_MOVE
                TouchPointPrev = TouchPointStart;
                PointerId = event.getPointerId(pointerIndex);
                //bitmapCanvas.drawCircle(x, y, 20, paint);
                //invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i(DBG_TAG, "Second Point");
                //skip if we already have second touch
                if(PointerIdSecond != -1)
                {
                    break;
                }
                //save touch start coord
                SecondTouchPointStart.x = event.getX();
                SecondTouchPointStart.y = event.getY();
                //startPoint also prevPoint to ACTION_MOVE
                SecondTouchPointPrev = SecondTouchPointStart;
                PointerIdSecond = event.getPointerId(pointerIndex);
                break;
            case MotionEvent.ACTION_UP:
                Log.i(DBG_TAG, "all release");

            case MotionEvent.ACTION_POINTER_UP:
                Log.i(DBG_TAG, "pointer release");
                if(PointerId == event.getPointerId(pointerIndex))
                {
                    PointerId = -1;
                }
                if(PointerIdSecond == event.getPointerId(pointerIndex))
                {
                    PointerIdSecond = -1;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                //think it all input interupt
                PointerId = -1;
                PointerIdSecond = -1;
                break;

            case MotionEvent.ACTION_MOVE:
                if( (PointerId != -1) && (PointerIdSecond != -1) )
                {
                    double Distance;
                    int firstIndex = 0 , secondIndex = 0;

                    if(PointerId == event.getPointerId(0)) {
                        firstIndex = 0;
                        secondIndex = 1;
                    }
                    else if(PointerId == event.getPointerId(1)) {
                        firstIndex = 1;
                        secondIndex = 0;
                    }
                    else
                    {
                        Log.i(DBG_TAG, "interrupt");
                        break;
                    }

                    //get vector
                    TouchVector.x = event.getX(firstIndex) - TouchPointPrev.x;
                    TouchVector.y = event.getY(firstIndex) - TouchPointPrev.y;
                    TouchPointPrev.x = event.getX(firstIndex);
                    TouchPointPrev.y = event.getY(firstIndex);

                    //get vector
                    TouchVectorSecond.x = event.getX(secondIndex) - SecondTouchPointPrev.x;
                    TouchVectorSecond.y = event.getY(secondIndex) - SecondTouchPointPrev.y;
                    SecondTouchPointPrev.x = event.getX(secondIndex);
                    SecondTouchPointPrev.y = event.getY(secondIndex);

                    double angle = GetDeg(TouchVectorSecond, TouchVector);
                    Log.i(DBG_TAG, Double.toString(angle));
                    if( (angle >= 0) && (angle <= 20) )
                    {
                        //same course
                        // move on distance ?
                        activeMoveOffset.x = x - TouchPointStart.x;
                        activeMoveOffset.y = y - TouchPointStart.y;

                        bitmap_position.x += activeMoveOffset.x;
                        bitmap_position.y += activeMoveOffset.y;
                        invalidate();
                    }
                }
                break;

            default:
                break;
        }
        return true;
    }
}
