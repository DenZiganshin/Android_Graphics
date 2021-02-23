package ru.denis.test.apps;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

public class GestureDetect {

	class TouchPt{
		PointF m_curr;
		PointF m_prev;
		int m_id;


		public TouchPt(){
			m_curr = new PointF();
			m_prev = new PointF();
			m_id = -1;
		}
		public TouchPt(TouchPt pt){
			m_curr = new PointF(pt.m_curr.x, pt.m_curr.y);
			m_prev = new PointF(pt.m_prev.x, pt.m_prev.y);
			m_id = pt.m_id;
		}
		public TouchPt(PointF pt, int tid){
			m_curr = new PointF(pt.x, pt.y);
			m_prev = new PointF(pt.x, pt.y);;
			m_id = tid;
		}
		public void set(float x, float y, int id){
			m_curr.x = x;
			m_curr.y = y;
			m_prev.x = x;
			m_prev.y = y;
			m_id = id;
		}
		public void update(float x, float y){
			m_prev.x = m_curr.x;
			m_prev.y = m_curr.y;
			m_curr.x = x;
			m_curr.y = y;
		}

		@Override
		public String toString() {
			return "{" +
					"m_curr=" + m_curr +
					", m_prev=" + m_prev +
					", m_id=" + m_id +
					'}';
		}
	}

	TouchPt[] m_points;
	PointF m_centerPoint;
	PointF m_prevCenterPoint;
	float m_pointsDist, m_prevPointsDist;
	boolean m_InProgress;
	float m_diag;
	float m_scale;

	/* constructor */
	public GestureDetect(float diag){
		m_points = new TouchPt[2];
		m_points[0] = new TouchPt();
		m_points[1] = new TouchPt();
		m_centerPoint = new PointF();
		m_prevCenterPoint = new PointF();
		m_pointsDist = 0;
		m_prevPointsDist = 0;
		m_InProgress = false;
		m_diag = diag;
		Log.i("DBG_diag", String.valueOf(m_diag));
	}

	/* difference PointF pair */
	protected PointF diff(PointF A, PointF B)
	{
		PointF diff = new PointF();
		diff.x = A.x - B.x;
		diff.y = A.y - B.y;
		return  diff;
	}

	/* calc norma of PointF */
	protected float norma(PointF vec)
	{
		float a = vec.x*vec.x;
		float b = vec.y*vec.y;
		float c = a + b;
		return (float)Math.sqrt(c);
	}

	/* MotionEvent processor */
	public void process(MotionEvent event){
		int iEventCode = event.getActionMasked();
		int pointerCount = event.getPointerCount();

		switch (iEventCode){
			case MotionEvent.ACTION_MOVE:
				if(pointerCount < 2){
					m_InProgress = false;
					return;
				}
				if(m_InProgress)
				{
					//process gesture
					//update coords
					for(int i=0; i<pointerCount; ++i){
						if (m_points[0].m_id == event.getPointerId(i)) {
							m_points[0].update(event.getX(m_points[0].m_id), event.getY(m_points[0].m_id));
						}
						if (m_points[1].m_id == event.getPointerId(i)) {
							m_points[1].update(event.getX(m_points[1].m_id), event.getY(m_points[1].m_id));
						}
					}

					// dist between points
					PointF prevAB = diff(m_points[0].m_prev, m_points[1].m_prev), currAB = diff(m_points[0].m_curr, m_points[1].m_curr);
					m_prevPointsDist = norma(prevAB);
					m_pointsDist = norma(currAB);

					//calc center point
					m_prevCenterPoint.x = m_centerPoint.x;
					m_prevCenterPoint.y = m_centerPoint.y;
					m_centerPoint.x = (m_points[0].m_curr.x + m_points[1].m_curr.x) / 2;
					m_centerPoint.y = (m_points[0].m_curr.y + m_points[1].m_curr.y) / 2;

					//Log.i("DBG", m_points[0].m_curr.toString());
					///Log.i("DBG", m_points[1].m_curr.toString());
					//Log.i("DBG", m_centerPoint.toString());
					//Log.i("DBG", "===============");

				}else{
					//new gesture

					//set points
					int id = event.getPointerId(0);
					m_points[0].set(event.getX(id), event.getY(id), id);
					id = event.getPointerId(1);
					m_points[1].set(event.getX(id), event.getY(id), id);

					//Log.i("DBG", m_points[0].toString());
					//Log.i("DBG", m_points[1].toString());

					//set center point
					m_centerPoint.x = (m_points[0].m_curr.x + m_points[1].m_curr.x) / 2;
					m_centerPoint.y = (m_points[0].m_curr.y + m_points[1].m_curr.y) / 2;
					m_prevCenterPoint.x = m_centerPoint.x;
					m_prevCenterPoint.y = m_centerPoint.y;

					m_InProgress = true;

				}
				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_POINTER_UP:
				//clear
				m_InProgress = false;
				break;
		}
	}

	public float getScale(){
		float diff = m_pointsDist - m_prevPointsDist;
		if(diff == 0) {
			return  1;
		}
		m_scale = Math.abs(diff) / 1080;
		if(m_scale < 0.001){
			return 1;
		}
		if(diff > 0){
			m_scale = m_scale + 1;
		}
		else
		{
			m_scale = 1 - m_scale;
		}
		return m_scale;
	}

	public PointF getOffset(){
		return new PointF(m_centerPoint.x - m_prevCenterPoint.x,m_centerPoint.y - m_prevCenterPoint.y);
	}

	public float getOffsetX(){
		return m_centerPoint.x - m_prevCenterPoint.x;
	}

	public float getOffsetY(){
		return m_centerPoint.y - m_prevCenterPoint.y;
	}

	public PointF getCenter(){
		return m_centerPoint;
	}
}
