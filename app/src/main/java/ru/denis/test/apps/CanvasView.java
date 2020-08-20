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

    Paint redPaint;
    Paint bluePaint;


    class Pointer
    {
        PointF prev, curr;
        int id;
        public Pointer()
        {
            prev = new PointF(0,0);
            curr = new PointF(0,0);
            id = -1;
        }
    }
    Pointer ptrA, ptrB;

    PointF TouchPointPrev, SecondTouchPointPrev;
    int PointerId, PointerIdSecond;

    PointF activeMoveOffset;
    boolean bGesture = false;

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
        activeMoveOffset = new PointF(0,0);
        ptrA = new Pointer();
        ptrB = new Pointer();


        //draw
        paint = new Paint();
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);

        redPaint = new Paint();
        redPaint.setAntiAlias(false);
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setColor(Color.RED);

        bluePaint = new Paint();
        bluePaint.setAntiAlias(false);
        bluePaint.setStyle(Paint.Style.FILL);
        bluePaint.setColor(Color.BLUE);

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
        double a = vec.x*vec.x;
        double b = vec.y*vec.y;
        double c = a + b;
        if(a<=0)
        {
            Log.i(DBG_TAG, "Norma x: "+Double.toString(vec.x)+"^2 <= "+Double.toString(a));
        }
        if(b<=0)
        {
            Log.i(DBG_TAG, "Norma y: "+Double.toString(vec.y)+"^2 <= "+Double.toString(b));
        }
        return Math.sqrt(c);
    }

    protected double Mult(PointF A, PointF B)
    {
        double a = A.x*B.x;
        double b = A.y*B.y;
        double c = a + b;
        if(a<=0)
        {
            Log.i(DBG_TAG, "Mult: x+x < 0");
        }
        if(b<=0)
        {
            Log.i(DBG_TAG, "Mult: y+y < 0");
        }
        return c;
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
        double a = Mult(A,B);
        double b = Norma(A);
        double c = Norma(B);
        double cos = a / (b+c);
        Log.i(DBG_TAG, "cos"+Double.toString(cos));
        double angle = Math.cos(cos) * 180.0 / Math.PI;
        return angle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, bitmap_position.x, bitmap_position.y, paint);
        canvas.drawCircle(ptrA.curr.x, ptrA.curr.y, 20, redPaint);
        canvas.drawCircle(ptrA.prev.x, ptrA.prev.y, 10, redPaint);

        canvas.drawCircle(ptrB.curr.x, ptrB.curr.y, 20, bluePaint);
        canvas.drawCircle(ptrB.prev.x, ptrB.prev.y, 10, bluePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // событие
        int actionMask = event.getActionMasked();
        // индекс касания
        int pointerIndex = event.getActionIndex();
        // число касаний
        int pointerCount = event.getPointerCount();

        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                //start touch
            case MotionEvent.ACTION_POINTER_DOWN:
                if(ptrA.id == -1)
                {
                    ptrA.id = event.getPointerId(pointerIndex);
                    ptrA.curr.x = event.getX(pointerIndex);
                    ptrA.curr.y = event.getY(pointerIndex);
                    ptrA.prev.x = event.getX(pointerIndex);
                    ptrA.prev.y = event.getY(pointerIndex);
                }
                else if(ptrB.id == -1)
                {
                    bGesture = true;
                    ptrB.id = event.getPointerId(pointerIndex);
                    ptrB.curr.x = event.getX(pointerIndex);
                    ptrB.curr.y = event.getY(pointerIndex);
                    ptrB.prev.x = event.getX(pointerIndex);
                    ptrB.prev.y = event.getY(pointerIndex);
                }
                //add pointer
                break;
            case MotionEvent.ACTION_UP:
                //end touch
            case MotionEvent.ACTION_POINTER_UP:
                //remove pointer
                if(ptrA.id == event.getPointerId(pointerIndex))
                {
                    bGesture = false;
                    ptrA.id = -1;
                }
                if(ptrB.id == event.getPointerId(pointerIndex))
                {
                    bGesture = false;
                    ptrB.id = -1;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                //think it all input interupt
                break;

            case MotionEvent.ACTION_MOVE:
                if(bGesture)
                {
                    //update
                    for(int i=0; i<pointerCount; i++) {
                        if (ptrA.id == event.getPointerId(i)) {
                            if((Math.abs(ptrA.curr.x - event.getX(i)) > 0.01) ||
                               (Math.abs(ptrA.curr.y - event.getY(i)) > 0.01) ) {
                                ptrA.prev.x = ptrA.curr.x;
                                ptrA.prev.y = ptrA.curr.y;
                                ptrA.curr.x = event.getX(i);
                                ptrA.curr.y = event.getY(i);
                            }
                        }
                        if (ptrB.id == event.getPointerId(i)) {
                            if( (Math.abs(ptrB.curr.x - event.getX(i)) > 0.01) ||
                                (Math.abs(ptrB.curr.y - event.getY(i)) > 0.01) ) {
                                ptrB.prev.x = ptrB.curr.x;
                                ptrB.prev.y = ptrB.curr.y;
                                ptrB.curr.x = event.getX(i);
                                ptrB.curr.y = event.getY(i);
                            }
                        }
                    }

                    //vector
                    PointF vecA = new PointF(),
                            vecB = new PointF();
                    vecA.x = ptrA.curr.x - ptrA.prev.x;
                    vecA.y = ptrA.curr.y - ptrA.prev.y;
                    vecB.x = ptrB.curr.x - ptrB.prev.x;
                    vecB.y = ptrB.curr.y - ptrB.prev.y;

                    double AB = (vecA.x * vecB.x) + (vecA.y * vecB.y);
                    double nA = Norma(vecA),
                            nB = Norma(vecB);
                    if( (nA!=0) && (nB!=0) && (AB!=0) ) {
                        double cos = AB / (nA * nB);
                        cos = cos > 1.0 ? 0.99 : cos;
                        double angle = Math.acos(cos) * 180.0 / Math.PI;

                    }
                    invalidate();
                }
                break;

            default:
                break;
        }
        return true;
    }
}
