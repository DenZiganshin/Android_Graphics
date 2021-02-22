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
	
	PointF LastDrawPoint;

	Point bitmap_position;
	Point bitmap_size;

	Paint redPaint;
	Paint bluePaint;

	PointF activeMoveOffset;

	Rect rectSrc = new Rect(0,0,300,300);
	RectF rectDst = new RectF(0,0,300,300);

	Bitmap tmpResizeBitmap;
	
	GestureDetect m_gestDect;

	float m_diag;
	
	boolean bNeedDrawPoint = false;


	public CanvasView(Context ctx, AttributeSet attrs){
		super(ctx, attrs);
		m_diag = get_diag(ctx);
		init();
		//get_screen_size(ctx);
	}

	private void init()
	{
		//classes
		m_gestDect = new GestureDetect(m_diag);
		
		//points
		bitmap_position = new Point(0,0);
		activeMoveOffset = new PointF(0,0);
		bitmap_size = new Point(300, 300);
		LastDrawPoint = new PointF(0,0);

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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int actionId = event.getActionMasked();

		m_gestDect.process(event);

		switch (actionId){
			case MotionEvent.ACTION_DOWN:
				//start draw
				break;
			case MotionEvent.ACTION_UP:
				//end draw
				break;
			case MotionEvent.ACTION_MOVE:
				if(event.getPointerCount() > 1){
					//cancel draw

					//move canvas
					PointF offset = m_gestDect.getOffset();
					float scale = m_gestDect.getScale();
					PointF centerPoint = m_gestDect.getCenter();

					rectDst.offset(offset.x, offset.y);

					RectF tmp = new RectF();

					tmp.top = rectDst.top - centerPoint.y;
					tmp.bottom = rectDst.bottom - centerPoint.y;
					tmp.left = rectDst.left - centerPoint.x;
					tmp.right = rectDst.right - centerPoint.x;

					tmp.top *= scale;
					tmp.bottom *= scale;
					tmp.left *= scale;
					tmp.right *= scale;

					tmp.top += centerPoint.y;
					tmp.bottom += centerPoint.y;
					tmp.left += centerPoint.x;
					tmp.right += centerPoint.x;

					rectDst.top = tmp.top;
					rectDst.bottom = tmp.bottom;
					rectDst.left = tmp.left;
					rectDst.right = tmp.right;

					Log.i("DBG", offset.toString());
					//Log.i("DBG", String.valueOf(scale));


					invalidate();
				}else{
					//draw
				}
				break;

			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_POINTER_UP:
				break;
		}

		return true;
	}

	private float get_diag(Context ctx){
		WindowManager manager = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return (float)Math.sqrt(size.x*size.x + size.y*size.y);
	}

	protected void drawResizePoints(Context ctx)
	{

	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bitmap, rectSrc, rectDst, paint);

		if(bNeedDrawPoint)
		{
			bitmapCanvas.drawCircle(LastDrawPoint.x, LastDrawPoint.y, 3, redPaint);
			bNeedDrawPoint = false;
		}
	}
}
