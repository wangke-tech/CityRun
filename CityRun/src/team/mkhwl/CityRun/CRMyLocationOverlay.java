package team.mkhwl.CityRun;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.baidu.mapapi.CoordinateConvert;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.Projection;

public class CRMyLocationOverlay extends Overlay{

	private static final int ONTapdB = 10;
	private static CRMyLocationOverlay myLocationOverlay;
	private static Location myLocation;
	private static GeoPoint tagPoint;
	private static MapView mMapView;
	public static boolean isShow = false;
	public static boolean isMyPathOn = false;
	private	static ArrayList<GeoPoint> algeopoints = new ArrayList<GeoPoint>(1);
	private	static PathOverlay pho;	
	private static CRLocationListeneri myCRLocationListeneri = new CRLocationListeneri(){


		
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			myLocation = location;
			GamePoint gpWgs84 = new GamePoint(
					(int) (location.getLatitude() * 1e6),
					(int) (location.getLongitude() * 1e6));
			 GeoPoint baidu09 =
			 CoordinateConvert.bundleDecode(CoordinateConvert
			 .fromWgs84ToBaidu(gpWgs84));

			tagPoint = baidu09;
			
			if(isMyPathOn){
				// 得到路径集合
				algeopoints.add(baidu09);
				if (mMapView.getOverlays().contains(pho)) {
					mMapView.getOverlays().remove(pho);
				}
				pho = new PathOverlay(algeopoints);
				// 绘制路径
				mMapView.getOverlays().add(pho);
			}
			

			mMapView.getController().animateTo(tagPoint);
			if (mMapView.getOverlays().contains(myLocationOverlay)) {

				mMapView.getOverlays().remove(myLocationOverlay);
			}

			mMapView.getOverlays().add(myLocationOverlay);
			
		}};

/*	private static Handler myLocationHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case C.handler.location_changed: {

				location = (Location) msg.obj;

				GamePoint gpWgs84 = new GamePoint(
						(int) (location.getLatitude() * 1e6),
						(int) (location.getLongitude() * 1e6));
				Log.v("CRMyLocationOverlay:", gpWgs84.toString());
				 GeoPoint baidu09 =
				 CoordinateConvert.bundleDecode(CoordinateConvert
				 .fromWgs84ToBaidu(gpWgs84));

				tagPoint = baidu09;

				mMapView.getController().animateTo(tagPoint);
				if (mMapView.getOverlays().contains(myLocationOverlay)) {

					mMapView.getOverlays().remove(myLocationOverlay);
				}

				mMapView.getOverlays().add(myLocationOverlay);
				break;
			}

			}
		};
	};*/


	public CRMyLocationOverlay(MapView mMapView) {
		CRMyLocationOverlay.mMapView = mMapView;
		myLocationOverlay = this;
	}

	/**
	 * 启用我的位置
	 * 注意:需要调用disableMyLocation()来关闭我的位置
	 */
	public static void enableMyLocation() {
		CRLocationlistener.requestUpdates(myCRLocationListeneri);
		isShow = true;
		//CRLocationlistener.requestUpdates(this);
	}

	/**
	 * 当地图不可见时调用该方法
	 */
	public static void disableMyLocation() {
		CRLocationlistener.removeUpdates(myCRLocationListeneri);
		isShow = false;
		//CRLocationlistener.removeUpdates(this);
	}

	public static void eableMyPath(){
		CRMyLocationOverlay.isMyPathOn = true;
	}
	public static void disableMyPath(){
		CRMyLocationOverlay.isMyPathOn = false;
		algeopoints = new ArrayList<GeoPoint>(1);
		pho = null;
	}
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		
		super.draw(canvas, mapView, shadow);

		// 经度转像素
		Projection projection = mapView.getProjection();
		Point p = projection.toPixels(tagPoint, null);
		//定义一个画笔
		Paint fillPaint = new Paint();
		//去锯齿
		fillPaint.setAntiAlias(true);
		fillPaint.setColor(Color.BLUE);
		fillPaint.setStyle(Style.FILL);//实心填充
		//设置透明度
		fillPaint.setAlpha(50);
		canvas.drawCircle(p.x, p.y, projection.metersToEquatorPixels(myLocation.getAccuracy()), fillPaint);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(3);
		canvas.drawCircle(p.x, p.y, 9, paint);
		
		
		Drawable marker = null;
		Resources res = mapView.getContext().getResources();
		marker = res.getDrawable(R.drawable.mylocation1);

		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
				.getIntrinsicHeight());   //为maker定义位置和边界
		super.drawAt(canvas, marker, p.x+7, p.y+7, false);

/*		Drawable marker;
		Resources res = mMapView.getContext().getResources();
		marker = res.getDrawable(R.drawable.mark_r);
		// 在指定GamePoint绘制一个String
		Point point = mapView.getProjection().toPixels(tagPoint, null);
		super.drawAt(canvas, marker, point.x, point.y, false);
		canvas.drawText("我的位置", point.x, point.y, paint);*/
				
	}

	@Override
	public boolean onTap(GeoPoint gp, MapView mapView) {

		Point tag_point = mapView.getProjection().toPixels(tagPoint, null);
		Point gp_point = mapView.getProjection().toPixels(gp, null);
		// 判断是否在游戏点响应范围内
		if ((Math.abs(tag_point.x - gp_point.x)) <= ONTapdB
				&& (Math.abs(tag_point.y - gp_point.y)) <= ONTapdB) {
			new AlertDialog.Builder(mapView.getContext()).setTitle("标题")
					.setMessage("精确度" + CRMyLocationOverlay.myLocation.getAccuracy())
					.setPositiveButton("确定", null).show();
		}
		return super.onTap(gp, mapView);
	}


}
