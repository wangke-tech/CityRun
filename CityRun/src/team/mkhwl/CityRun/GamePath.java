package team.mkhwl.CityRun;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.baidu.mapapi.GeoPoint;

import team.mkhwl.CityRun.db.GamePathDT;
import team.mkhwl.CityRun.db.GamePointDT;



/**
 * @author Mush
 * 
 */
public class GamePath {
	
	/**
	 * 游戏属性,是自定义还是预设
	 */
	private String type;
	private String pathLocation = "";//游戏所在地理位置（烟台市芝罘区）
	private String pathName = "";//游戏名字
	private String pathMSG = "";//游戏信息
	private int pahtid;
	
	//private int zoomClass;//根据点的分布计算出合适的缩放比例
	//private GamePoint centre;//游戏路径的中心
	
	private ArrayList<GamePoint> path = new ArrayList<GamePoint>();
	//private Collection<GamePointOverlay> pathOverlay = new ArrayList<GamePointOverlay>();
	/**
	 * 初始化一个空的路径
	 */
	public GamePath() {

	}

	/**
	 * 按照列表原有的顺序初始化游戏路径
	 * 
	 * @param path
	 */
	public GamePath(ArrayList<GamePoint> path) {
		this.path = path;
	}

	public GamePath(List<GamePointDT> pointDTs, GamePathDT path){
		this.pathName = path.pathname;
		this.setPathMSG(path.pathdescription);
		this.pathLocation = path.pathlocation;
		
		for(GamePointDT pointDT : pointDTs){
			GamePoint gp = new GamePoint(pointDT.lat, pointDT.lon);
			gp.setGpName(pointDT.gpname);
			gp.setQuestion(pointDT.question);
			gp.setAnswer(pointDT.answer);
			gp.setMSG(pointDT.MSG);
			this.path.add(gp);
		}
	}
	/**
	 * @param gamePoint
	 */
	public GamePath(GamePoint[] gamePoint) {
		int i = 0;

		for (i = 0; i < gamePoint.length; i++) {
			this.path.add(gamePoint[i]);
		}
	}

	/**
	 * 将游戏点添加到当前路径的末尾
	 * 
	 * @param gamepoint
	 */
	public void addGamePoint(GamePoint gamepoint) {
		gamepoint.index = path.size();
		this.path.add(gamepoint);
	}

	/**
	 * 将游戏点添加到路径的指定位置
	 * 
	 * @param gamepoint
	 * @param no
	 */
/*	public void addGamePoint(GamePoint gamepoint, int index) {
		gamepoint.index = index;
		this.path.add(index, gamepoint);
	}*/
	

	/**
	 * 以数组的形式返回当前游戏路径
	 * 
	 * @return GamePoint[]
	 */
	public GamePoint[] getPath() {
		GamePoint[] asfd = null;
		return this.path.toArray(asfd);
		// return null;

	}
	
	/**以Collection的形式返回路径中所有点的覆盖物
	 * @param marker
	 * @return
	 */
	public Collection<GamePointOverlay> getPahtOverlays(){
		GamePointOverlay t;
		Collection<GamePointOverlay> pathOverlay = new ArrayList<GamePointOverlay>();
		for (int i = 0; i < this.path.size(); i++){
			t = new GamePointOverlay(this.path.get(i));
			pathOverlay.add(t);
		}
		return pathOverlay;
	}

	public GamePoint getnow(){
		for (int i = 0; i < this.path.size(); i++){

			if (!this.path.get(i).isFinished()){
				return this.path.get(i);
			}
		}
		return null;
	}
	

	/**
	 * 返回当前路径点的长度
	 * 
	 * @return 路径的
	 */
	public int size() {
		return path.size();
	}

	/**
	 * @return the pahtid
	 */
	public int getPahtid() {
		return pahtid;
	}

	/**
	 * @param pahtid the pahtid to set
	 */
	public void setPahtid(int pahtid) {
		this.pahtid = pahtid;
	}

	/**
	 * @return the pathName
	 */
	public String getPathName() {
		return pathName;
	}

	/**
	 * @param pathName the pathName to set
	 */
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	/**
	 * @return the pathCity
	 */
	public String getPathLocation() {
		return pathLocation;
	}
	
	public ArrayList<GamePoint> getPath2ArrayList(){
		return this.path;
	}
	
	public List<GamePoint> getPath2List(){
		return this.path;
	}
	public ArrayList<GeoPoint> getPath2GeoList(){
		ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
		for(GamePoint gp: path){
			geoPoints.add(new GeoPoint(gp.getLatitudeE6(),gp.getLongitudeE6()));
		}
		return geoPoints;
		
	}

	/**
	 * @param pathCity the pathCity to set
	 */
	public void setPathCity(String pathCity) {
		this.pathLocation = pathCity;
	}

	/**
	 * @return the pathtype
	 */
	public String getPathtype() {
		return type;
	}

	/**
	 * @param pathtype the pathtype to set
	 */
	public void setPathtype(String pathtype) {
		this.type = pathtype;
	}

	/**
	 * @return the pathMSG
	 */
	public String getPathMSG() {
		return pathMSG;
	}

	/**
	 * @param pathMSG the pathMSG to set
	 */
	public void setPathMSG(String pathMSG) {
		this.pathMSG = pathMSG;
	}


}
