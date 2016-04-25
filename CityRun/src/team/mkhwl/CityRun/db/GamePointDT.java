package team.mkhwl.CityRun.db;

import team.mkhwl.CityRun.GamePoint;

/**
 * @author Mush
 * 该类给出了游戏点在数据库中储存的内容,相当于一条记录的内容
 *
 */
public class GamePointDT {
	public String gpname = null;
	public String question = null;
	public String answer = null;
	public int pathid;
	public int oridnal;
	public int lon;
	public int lat;
	public int _id;
	public String MSG;


	public GamePointDT(){
		
	}

	/**
	 * @param pathid 所属游戏
	 * @param oridnal 在游戏中的序号
	 * @param lon 经度
	 * @param lat 纬度
	 * @param gpName 检查点名字
	 * @param question 问题
	 * @param answer 答案
	 * @param MSG 游戏点描述
	 */
	public GamePointDT(int pathid, int oridnal, int lon, int lat, String gpname, String question, String answer,String MSG) {
		this.pathid = pathid;
		this.oridnal = oridnal;
		this.lon = lon;
		this.lat = lat;
		this.gpname = gpname;
		this.question = question;
		this.answer = answer;
		this.MSG = MSG;
	}
	
	/**
	 * 转换游戏点
	 * @param gamePoint 游戏点
	 */
	public GamePointDT(GamePoint gamePoint){
		this.lon = gamePoint.getLongitudeE6();
		this.lat = gamePoint.getLatitudeE6();
		this.gpname = gamePoint.getGpName();
		this.question = gamePoint.getQuestion();
		this.answer = gamePoint.getAnswer();
	}

}
