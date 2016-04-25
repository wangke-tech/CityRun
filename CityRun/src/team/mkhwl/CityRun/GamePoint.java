package team.mkhwl.CityRun;

import team.mkhwl.CityRun.db.GamePointDT;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;

/**
 * @author Mush
 * 
 */
public class GamePoint extends GeoPoint {


	private int pathId;
	public int index = -1;
	private String gpName = null;
	private String question = null;
	private String answer = null;
	private String msg = null;


	private int status = C.game.gamePointStatus_NotFinish;//0表示未完成,1表示正在进行,2表示已经完成
	private boolean finishFlag = false;	
	public final int MAX_ACCURACY = 10;
	public final int MIN_ACCURACY = 1;
	public final int MIDDL_ACCURACY = 5;
	/**
	 * 使用给定的经纬度初始化一个GamePoint
	 * 
	 * @param arg0
	 *            经度
	 * @param arg1
	 *            纬度
	 */
	GamePoint(int lat, int lon) {
		super(lat, lon);
		// TODO Auto-generated constructor stub
	}

	GamePoint(GeoPoint gp) {
		super(gp.getLatitudeE6(), gp.getLongitudeE6());
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the gpName
	 */
	public String getGpName() {
		return gpName;
	}

	/**
	 * @param gpName
	 *            the gpName to set
	 */
	public void setGpName(String gpName) {
		this.gpName = gpName;
	}

	/**
	 * 设置游戏点问题
	 * @param question 问题描述
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * @return 检查点问题
	 */
	public String getQuestion() {
		return this.question;
	}

	/**设置检查点问题的答案
	 * @param answer
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**检查答案是否正确
	 * @param answer
	 * @return
	 */
	public boolean checkAnswer(String answer) {
		if (this.answer.equals(answer)) {
			return true;
		}
		return false;
	}
	
	public String getAnswer(){
		return this.answer;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	public void setMSG(String msg){
		this.msg = msg;
	}
	public String getMSG(){
		return this.msg;
	}
	/**设置游戏点是否完成的标志
	 * @param flag
	 */
	public void setFinishFlag(boolean flag) {
		this.finishFlag = flag;
	}

	/**若游戏点已完成则返回true
	 * @return
	 */
	public boolean isFinished() {
		return this.finishFlag;
	}

	/**
	 * 检查当前是否到达游戏,如果到达怎将status置为1,finishFlag置为true,并返回true.
	 * 
	 * @param tagPoint
	 *            目标点
	 * @return
	 */
	public boolean onPoint(GeoPoint tagPoint) {

		if (this.distance(tagPoint) < MAX_ACCURACY) {
			return true;
		}
		return false;
	}

	public GamePointDT toGPDT(){

		return new GamePointDT(this.pathId, this.index,this.getLongitudeE6() ,
				this.getLatitudeE6(),this.gpName,this.question,this.answer,this.msg);
	}
	/**
	 * 计算与目标点之间的直线距离
	 * 
	 * @param tagPoint
	 *            目标点
	 * @return
	 */
	public double distance(GeoPoint tagPoint) {

		double lat1 = this.getLatitudeE6();
		double lat2 = tagPoint.getLatitudeE6();
		double lng1 = this.getLongitudeE6();
		double lng2 = tagPoint.getLongitudeE6();

		double radLat1 = lat1 / 1000000 * Math.PI / 180;
		double radLat2 = lat2 / 1000000 * Math.PI / 180;
		double a = radLat1 - radLat2;
		double b = lng1 / 1000000 * Math.PI / 180 - lng2 / 1000000 * Math.PI
				/ 180;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * 6378137.0;// 取WGS84标准参考椭球中的地球长半径(单位:m)
		s = Math.round(s * 10000) / 10000;

		return (int) s;

	}

}

/*
 * class GamePointOverlay extends ItemizedOverlay<OverlayItem>{ private
 * List<OverlayItem> GeoList = new ArrayList<OverlayItem>(); private Context
 * mContext;
 * 
 * private double mLat1 = 39.90923;//39.9022; // point1纬度 private double mLon1 =
 * 116.397428;//116.3822; // point1经度
 * 
 * private double mLat2 = 39.9022; private double mLon2 = 116.3922;
 * 
 * private double mLat3 = 39.917723; private double mLon3 = 116.3722;
 * 
 * public GamePointOverlay(Drawable marker, Context context) {
 * super(boundCenterBottom(marker));
 * 
 * this.mContext = context;
 * 
 * // 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6) GeoPoint p1 = new GeoPoint((int) (mLat1
 * * 1E6), (int) (mLon1 * 1E6)); GeoPoint p2 = new GeoPoint((int) (mLat2 * 1E6),
 * (int) (mLon2 * 1E6)); GeoPoint p3 = new GeoPoint((int) (mLat3 * 1E6), (int)
 * (mLon3 * 1E6));
 * 
 * GeoList.add(new OverlayItem(p1, "P1", "point1")); GeoList.add(new
 * OverlayItem(p2, "P2", "point2")); GeoList.add(new OverlayItem(p3, "P3",
 * "point3")); populate(); //createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法 }
 * 
 * @Override protected OverlayItem createItem(int i) { return GeoList.get(i); }
 * 
 * @Override public int size() { return GeoList.size(); }
 * 
 * @Override // 处理当点击事件 protected boolean onTap(int i) {
 * Toast.makeText(this.mContext, GeoList.get(i).getSnippet(),
 * Toast.LENGTH_SHORT).show(); return true; }
 * 
 * }
 */

class GamePointOverlay extends Overlay {
	private final int ONTapdB = 20;
	GamePoint tagPoint;
	
	

	public GamePointOverlay(GamePoint tagPoint) {
		this.tagPoint = tagPoint;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Drawable marker = null;
		Resources res = mapView.getContext().getResources();
		//根据不同状态读取不同的Drawable
		if(C.game.gamePointStatus_NotFinish == tagPoint.getStatus()){
			marker = res.getDrawable(R.drawable.mark_r);
		}else if(C.game.gamePointStatus_OnGoing == tagPoint.getStatus()){
			marker = res.getDrawable(R.drawable.mark_y);
		}else if(C.game.gamePointStatus_Finished == tagPoint.getStatus()){
			marker = res.getDrawable(R.drawable.mark_g);
		}
		
		// 在指定GamePoint绘制一个String
		Point point = mapView.getProjection().toPixels(tagPoint, null);
		super.drawAt(canvas, marker, point.x, point.y, false);
		Paint paint = new Paint();
		paint.setTextSize(18);
		canvas.drawText(tagPoint.getGpName(), point.x, point.y, paint);
	}

	@Override
	public boolean onTap(GeoPoint gp, MapView mapView) {
		// ItemizedOverlayDemo.mPopView.setVisibility(View.GONE);

		Point tag_point = mapView.getProjection().toPixels(tagPoint, null);
		Point gp_point = mapView.getProjection().toPixels(gp, null);
		// 判断是否在游戏点响应范围内
		if ((Math.abs(tag_point.x - gp_point.x)) <= ONTapdB
				&& (Math.abs(tag_point.y - gp_point.y)) <= ONTapdB) {
			new AlertDialog.Builder(mapView.getContext()).setTitle(tagPoint.getGpName())
					.setMessage(tagPoint.getMSG())
					.setPositiveButton("确定", null).show();
		}
		return super.onTap(gp, mapView);
	}

	public void setPoint(GeoPoint gp) {
		// this.tagPoint = new GamePoint(gp);
		tagPoint.setLatitudeE6(gp.getLatitudeE6());
		tagPoint.setLongitudeE6(gp.getLongitudeE6());
	}

}
