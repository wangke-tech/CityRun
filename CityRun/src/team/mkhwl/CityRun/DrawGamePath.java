package team.mkhwl.CityRun;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.Projection;

public class DrawGamePath extends MapActivity {
	private int num = 1;//用于计数
	private int distanse = 0;//大约路程
	//储存详细的路径点
	private ArrayList<GeoPoint> gps = new ArrayList<GeoPoint>();
	private MKSearch mMKSearch = null;
	//要找路径的点
	MKPlanNode startPlanNode = new MKPlanNode();
	MKPlanNode endPlanNode = new MKPlanNode();
	public  MapView mMapView = null;
	ArrayList<GeoPoint> gamegps = null;

	public DrawGamePath(ArrayList<GeoPoint> gamegps, BMapManager mBMapMan, MapView mMapView){

		if(gamegps.size() > 1){
			this.mMapView = mMapView;
			this.gamegps = gamegps;
			// 初始化MKSearch 
			mMKSearch = new MKSearch();  
			mMKSearch.init(mBMapMan, new MyMKSearchListener());  
			startPlanNode.pt = gamegps.get(0);
			endPlanNode.pt = gamegps.get(1); 
			mMKSearch.walkingSearch("", startPlanNode, "", endPlanNode);
		}
		

	}
	public class MyMKSearchListener implements  MKSearchListener{
		// 步行路线搜索结果 
		public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
			Log.v("---->>>>>>>>>>>>>>", "监听器启动。。。");
			if (result == null) {
				//判断是否还有需要查找的点
				if (++num < gamegps.size()){
					startPlanNode.pt = gamegps.get(num - 1);
					endPlanNode.pt = gamegps.get(num);
					//开始找路径
					mMKSearch.walkingSearch("烟台", startPlanNode, "烟台", endPlanNode);
				}else{
					//绘制路径
					mMapView.getOverlays().add(new GPathOverlay(gps));
				}
				return;
			}else{
				distanse = distanse + result.getPlan(0).getRoute(0).getDistance();
				ArrayList<ArrayList<GeoPoint>> algps = result.getPlan(0).getRoute(0).getArrayPoints();
				gps.addAll(algps.get(0));
				//储存详细点
				for (int i = 1; i < algps.size(); i++){
					for (int j = 1; j < algps.get(i).size(); j++){
						gps.add(algps.get(i).get(j));
					}
				}
				//判断是否还有需要查找的点
				if (++num < gamegps.size()){
					startPlanNode.pt = gamegps.get(num - 1);
					endPlanNode.pt = gamegps.get(num); 
					//开始找路径
					mMKSearch.walkingSearch("烟台", startPlanNode, "烟台", endPlanNode);
				}else{
					//绘制路径
					mMapView.getOverlays().add(new GPathOverlay(gps));
				}
			}
		}
		//其他未实现的方法
		public void onGetAddrResult(MKAddrInfo result, int iError) {  
		}
		public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {  
		}
		public void onGetPoiResult(MKPoiResult result, int type, int iError) {  
		}
		public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {  
		}
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		}
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
	}
	//获取大约路程
	public int getDistanse(){
		return distanse;
	}
	//绘制路径覆盖物
	public class GPathOverlay extends Overlay {
		private ArrayList<GeoPoint> algeopoints = null;
		public GPathOverlay(ArrayList<GeoPoint> algeopoints) {
			super();
			this.algeopoints = algeopoints;
		}
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, false);
			// 经度转像素
			Projection projection = mapView.getProjection();
			Point p = projection.toPixels(algeopoints.get(0), null);
			//画笔 画线
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paint.setDither(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStrokeWidth(4);
			// 连接
			int size = algeopoints.size();//得到坐标的个数
			Path path = new Path();//定义绘制路径
			path.moveTo(p.x, p.y);
			//将绘制路径连接
			for (int i = 1; i < size; i++) {
				Point pt = projection.toPixels(algeopoints.get(i), null);
				path.lineTo(pt.x, pt.y);
			}
			// 画出路径
			canvas.drawPath(path, paint);
		}
	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
