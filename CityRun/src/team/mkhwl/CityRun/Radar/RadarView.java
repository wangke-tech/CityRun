package team.mkhwl.CityRun.Radar;

import java.util.ArrayList;

import team.mkhwl.CityRun.GamePoint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RadarView extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "RadarView";
	private DrawRadarThread thread;
	private ArrayList<GamePoint> points = new  ArrayList<GamePoint>(1);

	public RadarView(Context context, ArrayList<GamePoint> points) {
		super(context);
		this.points = points;
		// TODO Auto-generated constructor stub
		setKeepScreenOn(true);
		SurfaceHolder holder = getHolder();
		holder.setFormat(PixelFormat.TRANSPARENT);
		setZOrderOnTop(true);
		holder.addCallback(this);
	}


	
	/**
	 * 该构造方法在使用layout文件构造view时调用
	 * @param context
	 * @param attrs
	 */

	public RadarView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// TODO Auto-generated constructor stub
		setKeepScreenOn(true);


		 getHolder().setFormat(PixelFormat.TRANSPARENT);

		setZOrderOnTop(true);

		getHolder().addCallback(this);

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		thread = new DrawRadarThread(holder, this.points);

		thread.start();


	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}
	
	/**获取SurfaceHolder
	 * @return SurfaceHolder
	 */
	public SurfaceHolder getSurfaceHolder(){
		return this.getHolder();
	}
	
	public DrawRadarThread getDrawRadarThread(){
		return this.thread;
	}
	public void setPoints(ArrayList<GamePoint> points){
		this.points = points;
	}

}
