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

	Rect rectSrc = new Rect(0,0,300,300);
	RectF rectDst = new RectF(0,0,300,300);

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
		activeMoveOffset = new PointF(0,0);
		bitmap_size = new Point(300, 300);

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

		bitmap = Bitmap.createBitmap(rectSrc.width(), rectSrc.height(), Bitmap.Config.RGB_565);
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
		canvas.drawBitmap(bitmap, rectSrc, rectDst, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		gestureChecker.touchEventProcess(event);
		
		switch (gestureChecker.getGesture())
		{
			case PointerPress:
				//start touch
				gestureChecker.getX();
				gestureChecker.getY();
				Log.i(DBG_TAG, "press");
				break;
			case PointerMove:
				//move
				Log.i(DBG_TAG, "move");
				break;
			case PointerRelease:
				//end touch
				Log.i(DBG_TAG, "release");
				break;
			case Move:
				//move gesture
				Log.i(DBG_TAG, "gesture_move");
				activeMoveOffset = gestureChecker.getOffset();
				rectDst.offset(activeMoveOffset.x, activeMoveOffset.y);
				invalidate();
				break;
			case Scale:
				//scale gesture
				Log.i(DBG_TAG, "gesture_scale");
				double resizeOn = gestureChecker.getScale();
				//if(bitmap_size.x + resizeOn > 0) {
					rectDst.left += (-resizeOn/2);
					rectDst.right += (resizeOn/2);
					//bitmap_size.x += resizeOn;
					//bitmap_position.x -= resizeOn;
				//}
				//if(bitmap_size.y + resizeOn > 0) {
					rectDst.top += (-resizeOn/2);
					rectDst.bottom += (resizeOn/2);
					//bitmap_size.y += resizeOn;
					//bitmap_position.y -= resizeOn;
				//}
				invalidate();
				break;
		}
		return true;
	}
}
