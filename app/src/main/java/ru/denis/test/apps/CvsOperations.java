package ru.denis.test.apps;

import android.graphics.*;

public class CvsOperations {

	// visual representation of resize markers. Visible only in 100% scale (for now)
	RectF[] m_resizeMarkers;

	// index of currently moved marker inside m_resizeMarkers
	int m_selectedMarkerIndex;

	// new rect for canvas. drawed wired when resize marker moving
	RectF m_newCvsRect;

	// original canvas width & height
	PointF m_CvsSize;

	// canvas offset. Updated when two-fingers move
	PointF m_bitmapOffset;

	// paint for m_newCvsRect
	Paint m_resizePaint;

	// paint for m_resizeMarkers
	Paint m_markerPaint;

	// flag. One of resize markers moving
	boolean m_bResizeInProgress;

	// flag. is canvas scaled. if it is - do not show resize markers
	boolean m_bIsScaled;

	// scale value
	float m_scale;

	// rect for canvas on-screen visualization
	RectF m_RectDst;

	public CvsOperations(){

	}

	public void init(Point bitmap_size){
		m_bResizeInProgress = false;
		m_bIsScaled = false;
		m_selectedMarkerIndex = -1;
		m_scale = 1;
		m_newCvsRect = new RectF();
		m_bitmapOffset = new PointF();
		m_CvsSize = new PointF(bitmap_size.x, bitmap_size.y);
		PointF MarkerSize = new PointF(40,40);
		m_RectDst = new RectF(0,0,bitmap_size.x,bitmap_size.y);
		m_resizeMarkers = new RectF[4];
		m_resizeMarkers[0] = new RectF(bitmap_size.x/2 - MarkerSize.x/2, -MarkerSize.y/2, bitmap_size.x/2 + MarkerSize.x/2, +MarkerSize.y/2);
		m_resizeMarkers[1] = new RectF(bitmap_size.x - MarkerSize.x/2, bitmap_size.y/2 - MarkerSize.y/2, bitmap_size.x + MarkerSize.x/2, bitmap_size.y/2+MarkerSize.y/2);
		m_resizeMarkers[2] = new RectF(bitmap_size.x/2 - MarkerSize.x/2, bitmap_size.y-MarkerSize.y/2, bitmap_size.x/2 + MarkerSize.x/2, bitmap_size.y+MarkerSize.y/2);
		m_resizeMarkers[3] = new RectF(-MarkerSize.x/2, bitmap_size.y/2-MarkerSize.y/2, MarkerSize.x/2, bitmap_size.y/2+MarkerSize.y/2);

		m_resizePaint = new Paint();
		m_resizePaint.setAntiAlias(false);
		m_resizePaint.setStyle(Paint.Style.STROKE);
		m_resizePaint.setColor(Color.BLACK);

		m_markerPaint = new Paint();
		m_markerPaint.setAntiAlias(false);
		m_markerPaint.setStyle(Paint.Style.FILL);
		m_markerPaint.setColor(Color.BLUE);
	}

	public void MoveScale(float offsetX, float offsetY, float scale, PointF centerPoint){
		//move canvas
		m_scale = scale;
		m_bIsScaled = true;
		//apply offset for on-screen cvs rect, append offset tom_bitmapOffset
		m_RectDst.offset(offsetX, offsetY);
		m_bitmapOffset.offset(offsetX, offsetY);

		//scale
		RectF tmp = new RectF();

		// centerPoint to axis origin
		tmp.top = m_RectDst.top - centerPoint.y;
		tmp.bottom = m_RectDst.bottom - centerPoint.y;
		tmp.left = m_RectDst.left - centerPoint.x;
		tmp.right = m_RectDst.right - centerPoint.x;

		//update coords for scale
		tmp.top *= scale;
		tmp.bottom *= scale;
		tmp.left *= scale;
		tmp.right *= scale;

		//roll back center coord
		tmp.top += centerPoint.y;
		tmp.bottom += centerPoint.y;
		tmp.left += centerPoint.x;
		tmp.right += centerPoint.x;

		//minimal scaling - 100%
		if((tmp.width() <= m_CvsSize.x) || (tmp.height() <= m_CvsSize.y)){
			tmp.top = m_RectDst.top;
			tmp.left = m_RectDst.left;
			tmp.bottom = m_RectDst.top + m_CvsSize.y;
			tmp.right = m_RectDst.left + m_CvsSize.x;
			m_scale = 1;
			m_bIsScaled = false;
		}

		// copy scaled rect to m_RectDst
		m_RectDst.top = tmp.top;
		m_RectDst.bottom = tmp.bottom;
		m_RectDst.left = tmp.left;
		m_RectDst.right = tmp.right;

		// recalc positions of resize markers
		recalcResizeMarkers();
	}

	protected void recalcResizeMarkers(){
		if(m_bIsScaled)
		{
			return;
		}

		float halfWidth = m_RectDst.width() / 2;
		float halfHeight = m_RectDst.height() / 2;
		float halfWSize = m_resizeMarkers[0].width()/2;
		float halfHSize = m_resizeMarkers[0].height()/2;

		m_resizeMarkers[0].set(m_RectDst.left + halfWidth - halfWSize, m_RectDst.top-halfHSize, m_RectDst.left + halfWidth + halfWSize, m_RectDst.top+halfHSize);
		m_resizeMarkers[1].set(m_RectDst.right - halfWSize, m_RectDst.top+halfHeight-halfHSize, m_RectDst.right + halfWSize, m_RectDst.top+halfHeight+halfHSize);
		m_resizeMarkers[2].set(m_RectDst.left + halfWidth - halfWSize, m_RectDst.bottom-halfHSize, m_RectDst.left + halfWidth + halfWSize, m_RectDst.bottom+halfHSize);
		m_resizeMarkers[3].set(m_RectDst.left - halfWSize, m_RectDst.top+halfHeight-halfHSize, m_RectDst.left + halfWSize, m_RectDst.top+halfHeight+halfHSize);
	}

	public void draw(Canvas canvas){
		if(!m_bIsScaled) {
			for (int i = 0; i < m_resizeMarkers.length; ++i) {
				canvas.drawRect(m_resizeMarkers[i], m_markerPaint);
			}
		}

		if(m_bResizeInProgress){
			canvas.drawRect(m_newCvsRect, m_resizePaint);
		}
	}

	public RectF getDstRect(){
		return m_RectDst;
	}

	public PointF getConvertedCoord(float tx, float ty){
		float cvsX = (tx - m_RectDst.left) * (m_CvsSize.x/m_RectDst.width());
		float cvsY = (ty - m_RectDst.top) * (m_CvsSize.y/m_RectDst.height());
		return new PointF(cvsX, cvsY);
	}

	public boolean isStartResize(float tx, float ty){
		if (!m_bIsScaled) {
			for (int i = 0; i < m_resizeMarkers.length; ++i) {
				if (m_resizeMarkers[i].contains(tx, ty)) {
					m_selectedMarkerIndex = i;
					m_bResizeInProgress = true;
					m_newCvsRect.set(m_RectDst);
					return true;
				}
			}
		}

		return false;
	}

	public void progressResize(float tx, float ty){
		switch (m_selectedMarkerIndex)
		{
			case 0:
				m_newCvsRect.top = ty;
				break;
			case 1:
				m_newCvsRect.right = tx;
				break;
			case 2:
				m_newCvsRect.bottom = ty;
				break;
			case 3:
				m_newCvsRect.left = tx;
				break;
		}
	}

	public PointF stopResize(){
		if(!m_bResizeInProgress){
			return null;
		}

		m_CvsSize.x = (int)m_newCvsRect.width();
		m_CvsSize.y = (int)m_newCvsRect.height();
		m_RectDst.set(m_newCvsRect);
		recalcResizeMarkers();
		m_bResizeInProgress = false;

		return m_CvsSize;
	}

	public int getLastResizeEdge(){
		return m_selectedMarkerIndex;
	}

}
