package ru.denis.test.apps;

import android.graphics.*;

public class Painter {

	private Bitmap m_BackBitmap;
	private Bitmap m_MainBitmap;
	private Canvas m_MainCanvas;


	public static final int TOOL_DRAW = 1;
	public static final int TOOL_ERASE = 2;

	int m_CurrentTool;

	Point m_bitmapSize;

	Paint m_PenPaint;
	Paint m_RedPaint;
	Paint m_BluePaint;
	Paint m_ClearPaint;
	Paint m_CurrentPaint = null;

	public Painter(){

	}

	public void init(int w, int h){

		//point
		m_bitmapSize = new Point(w,h);

		//draw
		m_PenPaint = new Paint();
		m_PenPaint.setAntiAlias(false);
		m_PenPaint.setStyle(Paint.Style.STROKE);
		m_PenPaint.setStrokeWidth(1);
		m_PenPaint.setColor(Color.BLACK);

		m_RedPaint = new Paint();
		m_RedPaint.setAntiAlias(false);
		m_RedPaint.setStyle(Paint.Style.FILL);
		m_RedPaint.setColor(Color.RED);

		m_BluePaint = new Paint();
		m_BluePaint.setAntiAlias(false);
		m_BluePaint.setStyle(Paint.Style.FILL);
		m_BluePaint.setColor(Color.BLUE);

		m_ClearPaint = new Paint();
		m_ClearPaint.setAntiAlias(false);
		m_ClearPaint.setStyle(Paint.Style.STROKE);
		m_ClearPaint.setStrokeWidth(3);
		m_ClearPaint.setColor(Color.WHITE);

		m_CurrentPaint = m_PenPaint;
		m_CurrentTool = TOOL_DRAW;

		m_MainBitmap = Bitmap.createBitmap(m_bitmapSize.x, m_bitmapSize.y, Bitmap.Config.RGBA_F16, true);
		m_MainCanvas = new Canvas(m_MainBitmap);
		//m_MainCanvas.drawColor(Color.WHITE);

		//grid background
		m_BackBitmap = Bitmap.createBitmap(m_bitmapSize.x, m_bitmapSize.y, Bitmap.Config.RGBA_F16, true);
		Paint gridPaint = new Paint();
		gridPaint.setAntiAlias(false);
		gridPaint.setStyle(Paint.Style.STROKE);
		gridPaint.setStrokeWidth(3);
		gridPaint.setColor(Color.GRAY);
		Canvas backCanvas = new Canvas(m_BackBitmap);
		backCanvas.drawColor(Color.rgb(237,237,231));
		for( int i=0; i<m_bitmapSize.x; i+=50){
			backCanvas.drawLine(i, 0, i, m_bitmapSize.y, gridPaint);
		}
		for( int i=0; i<m_bitmapSize.y; i+=50){
			backCanvas.drawLine(0, i, m_bitmapSize.x, i, gridPaint);
		}
	}

	public void init(Bitmap bitmap){

		//point
		m_bitmapSize = new Point(bitmap.getWidth(), bitmap.getHeight());

		//draw
		m_PenPaint = new Paint();
		m_PenPaint.setAntiAlias(false);
		m_PenPaint.setStyle(Paint.Style.STROKE);
		m_PenPaint.setStrokeWidth(1);
		m_PenPaint.setColor(Color.BLACK);

		m_RedPaint = new Paint();
		m_RedPaint.setAntiAlias(false);
		m_RedPaint.setStyle(Paint.Style.FILL);
		m_RedPaint.setColor(Color.RED);

		m_BluePaint = new Paint();
		m_BluePaint.setAntiAlias(false);
		m_BluePaint.setStyle(Paint.Style.FILL);
		m_BluePaint.setColor(Color.BLUE);

		m_ClearPaint = new Paint();
		m_ClearPaint.setAntiAlias(false);
		m_ClearPaint.setStyle(Paint.Style.STROKE);
		m_ClearPaint.setStrokeWidth(3);
		m_ClearPaint.setColor(Color.WHITE);
		m_ClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

		m_CurrentPaint = m_PenPaint;
		m_CurrentTool = TOOL_DRAW;

		m_MainBitmap = Bitmap.createBitmap(bitmap);
		m_MainCanvas = new Canvas(m_MainBitmap);

		//grid background
		m_BackBitmap = Bitmap.createBitmap(m_bitmapSize.x, m_bitmapSize.y, Bitmap.Config.RGBA_F16, true);
		Paint gridPaint = new Paint();
		gridPaint.setAntiAlias(false);
		gridPaint.setStyle(Paint.Style.STROKE);
		gridPaint.setStrokeWidth(3);
		gridPaint.setColor(Color.GRAY);
		Canvas backCanvas = new Canvas(m_BackBitmap);
		backCanvas.drawColor(Color.rgb(237,237,231));
		for( int i=0; i<m_bitmapSize.x; i+=50){
			backCanvas.drawLine(i, 0, i, m_bitmapSize.y, gridPaint);
		}
		for( int i=0; i<m_bitmapSize.y; i+=50){
			backCanvas.drawLine(0, i, m_bitmapSize.x, i, gridPaint);
		}
	}


	public void drawLine(float x1, float y1, float x2, float y2){
		m_MainCanvas.drawLine(x1, y1, x2, y2, m_CurrentPaint);
	}

	public void changeTool(int toolId){
		switch (toolId){
			case TOOL_DRAW:
				m_CurrentPaint = m_PenPaint;
				m_CurrentTool = TOOL_DRAW;
				break;
			case TOOL_ERASE:
				m_CurrentPaint = m_ClearPaint;
				m_CurrentTool = TOOL_ERASE;
				break;
		}
	}

	public int getCurrentColor(){
		if(m_CurrentTool == TOOL_DRAW){
			return m_PenPaint.getColor();
		}else if(m_CurrentTool == TOOL_ERASE) {
			return m_ClearPaint.getColor();
		}
		return Color.BLACK;
	}

	public int getCurrentSize(){
		if(m_CurrentTool == TOOL_DRAW){
			return (int)m_PenPaint.getStrokeWidth();
		}else if(m_CurrentTool == TOOL_ERASE) {
			return (int)m_ClearPaint.getStrokeWidth();
		}
		return 1;
	}

	public Bitmap getMainBitmap(){
		return m_MainBitmap;
	}

	public Bitmap getBackBitmap(){ return  m_BackBitmap; }

	public void changeCurrentTool(int color, int size){
		if(m_CurrentTool == TOOL_DRAW){
			m_PenPaint.setStrokeWidth(size);
			m_PenPaint.setColor(color);
		}else if(m_CurrentTool == TOOL_ERASE){
			m_ClearPaint.setStrokeWidth(size);
			m_ClearPaint.setColor(color);
		}
	}

	public void resizeEdge(int new_w, int new_h, int edge){
		Bitmap tmp = Bitmap.createBitmap(m_MainBitmap);

		m_MainBitmap = Bitmap.createBitmap(new_w, new_h, Bitmap.Config.RGBA_F16, true);
		m_MainCanvas = new Canvas(m_MainBitmap);
		//m_MainCanvas.drawColor(Color.WHITE);

		switch (edge){
			case 0:
				m_MainCanvas.drawBitmap(tmp, 0, new_h - m_bitmapSize.y, null);
				break;
			case 1:
				m_MainCanvas.drawBitmap(tmp, 0, 0, null);
				break;
			case 2:
				m_MainCanvas.drawBitmap(tmp, 0, 0, null);
				break;
			case 3:
				m_MainCanvas.drawBitmap(tmp, new_w - m_bitmapSize.x, 0, null);
				break;
		}

		m_bitmapSize.x = new_w;
		m_bitmapSize.y = new_h;
	}
}
