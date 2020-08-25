package ru.denis.test.apps;

import android.util.Log;
import android.view.MotionEvent;
import android.graphics.*;


class GestureChecker
{
	private static final String DBG_TAG = "DBG_Gesture";

	/* struct with Pointer description*/
	class Pointer
	{
		// previous & current position
		PointF prev, curr;
		// touch Id
		int id;
		
		public Pointer()
		{
			prev = new PointF(0,0);
			curr = new PointF(0,0);
			id = -1;
		}
	}
	
	/* Gesture type */
	public enum GestureType
	{
		NoEvent,
		PointerPress,
		PointerMove,
		PointerRelease,
		Scale,
		Move
	};
	
	Pointer ptrA, ptrB;
	int pointersInUse;
	PointF currentCoord, moveOffset;
	double distBetweenPointers;
	PointF startPointA, startPointB;
	GestureType currentGesture;
	boolean bComplexGesture = false, bStopUntilAllEnd = false;
	
	
	public GestureChecker()
	{
		ptrA = new Pointer();
		ptrB = new Pointer();
		startPointA = new PointF();
		startPointB = new PointF();
		currentCoord = new PointF();
		moveOffset = new PointF();
		distBetweenPointers = 0;
		pointersInUse = 0;
	}
	
	/* calc norma of PointF */
	protected double norma(PointF vec)
    {
        double a = vec.x*vec.x;
        double b = vec.y*vec.y;
        double c = a + b;
        return Math.sqrt(c);
    }

	/* multiply PointF pair */
    protected double mult(PointF A, PointF B)
    {
        double a = A.x*B.x;
        double b = A.y*B.y;
        double c = a + b;
        return c;
    }
    
    /* difference PointF pair */
    protected PointF diff(PointF A, PointF B)
    {
        PointF diff = new PointF();
        diff.x = A.x - B.x;
        diff.y = A.y - B.y;
        return  diff;
    }
    
    /* process MotionEvent. Convert to GestureType */
	public void touchEventProcess(MotionEvent event)
	{
		int pointerIndex = event.getActionIndex();
		int pointerCount = event.getPointerCount();
		
		currentGesture = GestureType.NoEvent;


		switch (event.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
				if(bStopUntilAllEnd)
				{
					break;
				}
				Log.i(DBG_TAG, "ACTION_DOWN");
				//start touch
			case MotionEvent.ACTION_POINTER_DOWN:
				if(bStopUntilAllEnd)
				{
					break;
				}
				Log.i(DBG_TAG, "ACTION_POINTER_DOWN");
				if(ptrA.id == -1)
				{
					ptrA.id = event.getPointerId(pointerIndex);
					ptrA.curr.x = event.getX(pointerIndex);
					ptrA.curr.y = event.getY(pointerIndex);
					ptrA.prev.x = event.getX(pointerIndex);
					ptrA.prev.y = event.getY(pointerIndex);
					pointersInUse += 1;
				}
				else if(ptrB.id == -1)
				{
					ptrB.id = event.getPointerId(pointerIndex);
					ptrB.curr.x = event.getX(pointerIndex);
					ptrB.curr.y = event.getY(pointerIndex);
					ptrB.prev.x = event.getX(pointerIndex);
					ptrB.prev.y = event.getY(pointerIndex);
					
					//save start point of gesture
					startPointA.x = ptrA.curr.x;
					startPointA.y = ptrA.curr.y;
					startPointB.x = ptrB.curr.x;
					startPointB.y = ptrB.curr.y;
					pointersInUse += 1;
				}
				if(pointersInUse > 1)
				{
					bComplexGesture = true;
				}
				else if (pointersInUse == 1)
				{
					currentGesture = GestureType.PointerPress;
					currentCoord.x = ptrA.curr.x;
					currentCoord.y = ptrA.curr.y;
				}
				//add pointer
				break;
			case MotionEvent.ACTION_UP:
				Log.i(DBG_TAG, "ACTION_UP");
				//all touches ended
				currentGesture = GestureType.PointerRelease;
				bStopUntilAllEnd = false;
				ptrA.id = -1;
				ptrB.id = -1;
				pointersInUse = 0;
				currentCoord.x = ptrA.curr.x;
				currentCoord.y = ptrA.curr.y;
				break;
				//end touch
			case MotionEvent.ACTION_POINTER_UP:
				Log.i(DBG_TAG, "ACTION_POINTER_UP");
				//remove pointer
				//TODO stop all process, until all-new pointers
				bComplexGesture = false;
				bStopUntilAllEnd = true;
				break;

			case MotionEvent.ACTION_CANCEL:
				Log.i(DBG_TAG, "ACTION_CANCEL");
				//think it all input interupt
				currentGesture = GestureType.PointerRelease;
				pointersInUse = 0;
				bStopUntilAllEnd = false;
				break;

			case MotionEvent.ACTION_MOVE:
				if(bStopUntilAllEnd)
				{
					break;
				}
				Log.i(DBG_TAG, "ACTION_MOVE");
				if(!bComplexGesture)
				{
					currentGesture = GestureType.PointerMove;
					currentCoord.x = event.getX();
					currentCoord.y = event.getY();
				}
				else
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
					vecA = diff(ptrA.curr, ptrA.prev);
					vecB = diff(ptrB.curr, ptrB.prev);

					double AB = mult(vecA, vecB);
					double nA = norma(vecA),
							nB = norma(vecB);
					//zero check
					if( (nA==0) || (nB==0) || (AB==0) )
					{
						break;
					}
					//calc cos
					double cos = AB / (nA * nB);
					// 0.0 < cos < 1.0
					cos = cos > 1.0 ? 0.99 : cos;
					//angle
					double angle = Math.acos(cos) * 180.0 / Math.PI;

					if( (angle >=0) && (angle <= 30) )
					{
						//same course
						//move gesture
						currentGesture = GestureType.Move;
						//calc offset - between current and previous call
						moveOffset = diff(ptrA.curr, ptrA.prev);
					}
					
					if( (angle >= 160) && (angle <= 180) )
					{
						//oposite course
						//scale gesture
						currentGesture = GestureType.Scale;
						//dist
						PointF prevAB = diff(ptrA.prev, ptrB.prev), currAB = diff(ptrA.curr, ptrB.curr);
						double prevDist = norma(prevAB), currDist = norma(currAB);
						distBetweenPointers = currDist-prevDist;
					}
				}
				break;

			default:
				break;
		}
	}//end touchEventProcess
	
	public GestureType getGesture()
	{
		return currentGesture;
	}
	
	public float getX()
	{
		if( (currentGesture == GestureType.PointerPress) ||
			(currentGesture == GestureType.PointerMove) ||
			(currentGesture == GestureType.PointerRelease) )
		{
			return currentCoord.x;
		}
		return 0;
	}
	
	public float getY()
	{
		if( (currentGesture == GestureType.PointerPress) ||
			(currentGesture == GestureType.PointerMove) ||
			(currentGesture == GestureType.PointerRelease) )
		{
			return currentCoord.y;
		}
		return 0;
	}
	
	public PointF getOffset()
	{
		if(currentGesture == GestureType.Move)
		{
			return moveOffset;
		}
		return null;
	}
	
	public double getScale()
	{
		return distBetweenPointers;
	}
}
