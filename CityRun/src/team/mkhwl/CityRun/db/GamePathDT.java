package team.mkhwl.CityRun.db;

import team.mkhwl.CityRun.GamePath;


public class GamePathDT {
	public int _id;
	public int pathid;
	public String pathlocation;
	public String pathname;
	public String pathdescription;
	public String type;
	
	public GamePathDT(){
		
	}
	/**
	 * @param _id
	 * @param pathid
	 * @param pathlocation
	 * @param pathname
	 */
	public GamePathDT(int _id, int pathid, String pathlocation, String pathname, String pathtype){
		this._id = _id;
		this.pathid = pathid;
		this.pathlocation = pathlocation;
		this.pathname = pathname;
		this.type = pathtype;
	}
	/**
	 * @param pathid
	 * @param pathlocation
	 * @param pathname
	 */
	public GamePathDT(int pathid, String pathlocation, String pathname){
		this.pathid = pathid;
		this.pathlocation = pathlocation;
		this.pathname = pathname;
	}
	/**
	 * @param gamepath
	 */
	public GamePathDT(GamePath gamepath){
		this.pathid = gamepath.getPahtid();
		this.pathlocation = gamepath.getPathLocation();
		this.pathname = gamepath.getPathName();
		this.type = gamepath.getPathtype();
	}


}
