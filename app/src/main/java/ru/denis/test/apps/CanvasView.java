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

	PointF activeMoveOffset;

	int scale = 1;

	Bitmap tmpResizeBitmap;
	
	GestureChecker gestureChecker;


	public CanvasView(Context ctx, AttributeSet attrs){
		super(ctx, attrs);
		init();
		get_screen_size(ctx);
	}

	private void init()
	{
		//classes
		gestureChecker = new GestureChecker();
		
		//points
		bitmap_position = new Point(0,0);

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

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bitmap, bitmap_position.x, bitmap_position.y, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		gestureChecker.touchEventProcess(event);
		
		switch (gestureChecker.getGesture())
		{
			case GestureChecker.GestureType.PointerPress:
				//start touch
				gestureChecker.getX();
				gestureChecker.getY();
				break;
			case GestureChecker.GestureType.PointerMove:
				//move
				break;
			case GestureChecker.GestureType.PointerRelease:
				//end touch
				break;
			case GestureChecker.GestureType.Move:
				//move gesture
				gestureChecker.getOffset();
				break;
			case GestureChecker.GestureType.Scale:
				//scale gesture
				break;
		}
		return true;
	}
}
