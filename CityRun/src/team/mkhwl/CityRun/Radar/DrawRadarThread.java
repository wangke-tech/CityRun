package team.mkhwl.CityRun.Radar;

import java.util.ArrayList;

import team.mkhwl.CityRun.CRLocationListeneri;
import team.mkhwl.CityRun.CRLocationlistener;
import team.mkhwl.CityRun.GamePoint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.location.Location;
import android.util.Log;
import android.view.SurfaceHolder;

import com.baidu.mapapi.CoordinateConvert;
import com.baidu.mapapi.GeoPoint;

public class DrawRadarThread extends Thread {
	private static final String TAG = "DrawRadarThread";
	private boolean isRunning = false;
	// 管理surface的Handle
	private SurfaceHolder surfaceHolder;
	// 数据
	private ArrayList<GamePoint> points = new ArrayList<GamePoint>(1);;
	// private Location myPoint = null;
	private GeoPoint myLocation = new GeoPoint(37481300, 121464333);
	private static double scale = 0.001;// 比例尺px/m
	private float centerX = 240;// 中心点
	private float centerY = 300;// 中心点
	private Canvas canvas = null;
	private float MAX_R = 240;// 最大半径
	private CRLocationListeneri myCRLocationListeneri = new CRLocationListeneri() {

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub

			if (!isRunning) {
				CRLocationlistener.removeUpdates(myCRLocationListeneri);
				return;
			}
			GeoPoint gpWgs84 = new GeoPoint(
					(int) (location.getLatitude() * 1e6),
					(int) (location.getLongitude() * 1e6));
			myLocation = CoordinateConvert.bundleDecode(CoordinateConvert
					.fromWgs84ToBaidu(gpWgs84));

			Log.v(TAG, "接收到坐标 :  " + myLocation.toString());

			try {
				canvas = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					Log.v(TAG, "画点");
					doDraw(canvas, myLocation);
				}
			} finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
					canvas = null;
				}
			}

		}
	};

	/**
	 * 实例化是将路径点传入
	 * 
	 * @param points
	 *            需要在屏幕绘制的路径点
	 * @param myPoint
	 *            表示用户当前位置的点,从LocationListener获取
	 */
	public DrawRadarThread(SurfaceHolder surfaceHolder,
			ArrayList<GamePoint> points) {
		Log.v(TAG, "DrawRadarThread  构造");
		this.surfaceHolder = surfaceHolder;
		Log.v(TAG, surfaceHolder.toString());
		this.points = points;
		Log.v(TAG, points.toString());

	}

	@Override
	public void run() {
		// Canvas canvas = null;
		Log.v(TAG, "Run");
		isRunning = true;
		CRLocationlistener.requestUpdates(myCRLocationListeneri);

		// 绘制背景
		try {
			Log.v(TAG, "第一次画背景");
			canvas = surfaceHolder.lockCanvas(null);
			Log.v(TAG, canvas.toString());
			// 初始化画板的中心坐标
			centerX = canvas.getWidth() / 2;
			centerY = centerX + 40;
			MAX_R = centerX;
			Log.v(TAG, "" + centerX + "|" + centerY);
			Log.v(TAG, "第一次画背景");
			synchronized (surfaceHolder) {
				drawBackGround(canvas);
			}
		} finally {
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
				canvas = null;
			}
		}

	}

	public void addPoints(ArrayList<GamePoint> points) {
		this.points = points;
	}

	public void drawBackGround(Canvas canvas) {
		Log.v(TAG, "画背景     drawBackGround");

		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		canvas.drawPaint(mPaint);

		Paint paint = new Paint();// 画笔
		// 画背景
		// 画斜线
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(2);// 设置线粗
		for (int i = 0; i <= 11; i++) {

			canvas.drawLine(centerX, centerY,
					(float) (centerX + MAX_R * Math.sin((Math.PI / 6) * i)),
					(float) (centerY - MAX_R * Math.cos((Math.PI / 6) * i)),
					paint);
		}

		paint.setStrokeWidth((float) 3.0);
		paint.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除，大家一看效果就明白了
		PathEffect effects1 = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1);// 画虚线
		PathEffect effects2 = new DashPathEffect(new float[] { 1, 2, 4, 8 }, 1);
		PathEffect effects3 = new DashPathEffect(new float[] { 1, 2, 4, 6 }, 1);

		paint.setColor(Color.rgb(51, 102, 51));// 设置色
		paint.setAlpha(200);// 设置透明度
		paint.setStyle(Style.FILL);

		canvas.drawCircle(centerX, centerY, centerX, paint);

		paint.setAlpha(0);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);

		paint.setPathEffect(effects1);
		canvas.drawCircle(centerX, centerY, centerX / 4 * 3, paint);

		paint.setPathEffect(effects3);
		canvas.drawCircle(centerX, centerY, centerX / 4 * 2, paint);

		paint.setPathEffect(effects2);
		canvas.drawCircle(centerX, centerY, centerX / 4 * 1, paint);

	}

	public void doDraw(Canvas canvas, GeoPoint cLocation) {
		Log.v(TAG, "doDraw");
		Log.v(TAG, "我的坐标   " + cLocation.toString());
		// 先清屏再画
		drawBackGround(canvas);
		Paint paint = new Paint();// 画笔
		int i = 0;

		for (GamePoint point : this.points) {
			i++;
			double lat = (double) point.getLatitudeE6() / 1000000;
			double lon = (double) point.getLongitudeE6() / 1000000;
			Log.v(TAG, i + "经纬度:  " + lat + "|" + lon);

			double radius = 0;// 图上的半径px
			double alpha = 0;// 图上角度
			float pic_xB;
			float pic_yB;

			radius = scale
					* Calculate.getDistance(
							((double) cLocation.getLatitudeE6() / 1000000),
							((double) cLocation.getLongitudeE6() / 1000000),
							lat, lon);
			alpha = Calculate.getAngle(
					((double) cLocation.getLatitudeE6() / 1000000),
					((double) cLocation.getLongitudeE6() / 1000000), lat, lon);
			alpha *= Math.PI / 180;// 转换成弧度

			pic_xB = (float) (centerX + radius * (Math.sin(alpha)));
			pic_yB = (float) (centerY - radius * (Math.cos(alpha)));

			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.RED);
			canvas.drawCircle(pic_xB, pic_yB, 5, paint);
			paint.setColor(Color.rgb(0, 0, 102));
			paint.setTextSize(18);
			canvas.drawText(point.getGpName(), pic_xB, pic_yB, paint);
		}

	}

	public void isFinished() {
		this.isRunning = false;
	}

	public void zoonIn() {
		scale *= 5;
		try {
			canvas = surfaceHolder.lockCanvas(null);
			synchronized (surfaceHolder) {
				Log.v(TAG, "画点");
				doDraw(canvas, myLocation);
			}
		} finally {
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
				canvas = null;
			}
		}
		Log.v(TAG, "zoomIn");
	}

	public void zoonOut() {
		scale /= 5;
		try {
			canvas = surfaceHolder.lockCanvas(null);
			synchronized (surfaceHolder) {
				Log.v(TAG, "画点");
				doDraw(canvas, myLocation);
			}
		} finally {
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
				canvas = null;
			}
		}
		Log.v(TAG, "zoonOut");
	}

}
