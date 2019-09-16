package ru.denis.test.apps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class CanvasView extends View {

    private Paint paint;

    public CanvasView(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
        init();
        get_screen_size(ctx);
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.FILL);
    }
    private void get_screen_size(Context ctx){
        WindowManager manager = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
