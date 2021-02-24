package ru.denis.test.apps;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class CanvasView extends View {

	private static final String DBG_TAG = "DBG_MAIN";
	public static final int TOOL_DRAW = 1;
	public static final int TOOL_ERASE = 2;

	Point bitmap_size;

	Painter m_Painter;

	Rect rectSrc;

	GestureDetect m_gestDect;
	CvsOperations m_cvsOp;

	float m_diag;

	PointF m_firstCoord;

	private static final int STATE_DRAW = 1;
	private static final int STATE_SCALE = 2;
	private static final int STATE_RESIZE = 3;
	int m_CurrentState;


	public CanvasView(Context ctx, AttributeSet attrs){
		super(ctx, attrs);
		m_diag = get_diag(ctx);
		//get_screen_size(ctx);
	}

	public void changeTool(int toolId){
		m_Painter.changeTool(toolId);
	}

	public void init()
	{
		m_CurrentState = STATE_DRAW;

		//classes
		m_gestDect = new GestureDetect(m_diag);
		m_cvsOp = new CvsOperations();
		m_Painter = new Painter();

		//points
		m_firstCoord = new PointF();
		Point size = get_screen_size(getContext());
		bitmap_size = new Point(size.x, size.y);
		rectSrc = new Rect(0,0,bitmap_size.x, bitmap_size.y);
		m_cvsOp.init(bitmap_size);
		m_Painter.init(bitmap_size.x, bitmap_size.y);

	}

	public void init(Bitmap bitmap)
	{
		m_CurrentState = STATE_DRAW;

		//classes
		m_gestDect = new GestureDetect(m_diag);
		m_cvsOp = new CvsOperations();
		m_Painter = new Painter();

		//points
		m_firstCoord = new PointF();
		bitmap_size = new Point(bitmap.getWidth(), bitmap.getHeight());
		rectSrc = new Rect(0,0,bitmap_size.x, bitmap_size.y);
		m_cvsOp.init(bitmap_size);
		m_Painter.init(bitmap);

	}

	private Point get_screen_size(Context ctx){
		WindowManager manager = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public Bitmap getBitmap(){
		return m_Painter.getMainBitmap();
	}

	public Painter getPainter(){
		return m_Painter;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int actionId = event.getActionMasked();

		m_gestDect.process(event);

		float tx = event.getX();
		float ty = event.getY();

		switch (actionId){
			case MotionEvent.ACTION_DOWN:
				//start draw
				if(m_CurrentState == STATE_DRAW) {
					RectF rectDst = m_cvsOp.getDstRect();
					if(rectDst.contains(tx, ty)) {
						PointF conv = m_cvsOp.getConvertedCoord(tx,ty);
						m_firstCoord.set(conv.x, conv.y);
					}

					//check resize markers
					if(m_cvsOp.isStartResize(tx,ty)){
						m_CurrentState = STATE_RESIZE;
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				//end draw
				if(m_CurrentState == STATE_DRAW){
					RectF rectDst = m_cvsOp.getDstRect();
					if(rectDst.contains(tx, ty)) {
						PointF conv = m_cvsOp.getConvertedCoord(tx,ty);
						m_Painter.drawLine(m_firstCoord.x, m_firstCoord.y, conv.x, conv.y);
						m_firstCoord.set(conv.x, conv.y);
					}
				}else if(m_CurrentState == STATE_RESIZE) {
					PointF result = m_cvsOp.stopResize();
					if (result != null) {
						rectSrc.right = (int) result.x;
						rectSrc.bottom = (int) result.y;
						int edge = m_cvsOp.getLastResizeEdge();
						m_Painter.resizeEdge(rectSrc.width(), rectSrc.height(), edge);
					}
				}

				m_CurrentState = STATE_DRAW;
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				if(event.getPointerCount() > 1){
					//cancel draw

					m_CurrentState = STATE_SCALE;

					m_cvsOp.MoveScale(m_gestDect.getOffsetX(), m_gestDect.getOffsetY(), m_gestDect.getScale(), m_gestDect.getCenter());

					invalidate();
				}else{
					//draw
					if(m_CurrentState == STATE_DRAW){
						RectF rectDst = m_cvsOp.getDstRect();
						if(rectDst.contains(tx, ty)) {
							PointF conv = m_cvsOp.getConvertedCoord(tx,ty);
							m_Painter.drawLine(m_firstCoord.x, m_firstCoord.y, conv.x, conv.y);
							m_firstCoord.set(conv.x, conv.y);
						}
					}else if(m_CurrentState == STATE_RESIZE){
						//Log.i("DBG_RES", String.valueOf(tx) + "_"+String.valueOf(ty));
						m_cvsOp.progressResize(tx,ty);
					}

					invalidate();
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

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.GRAY);
		RectF rectDst = m_cvsOp.getDstRect();
		Bitmap bitmap = m_Painter.getMainBitmap();
		Bitmap back = m_Painter.getBackBitmap();
		canvas.drawBitmap(back, rectSrc, rectDst, null);
		canvas.drawBitmap(bitmap, rectSrc, rectDst, null);

		m_cvsOp.draw(canvas);
	}
}
