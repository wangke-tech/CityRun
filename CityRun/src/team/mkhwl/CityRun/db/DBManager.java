package team.mkhwl.CityRun.db;

import java.util.ArrayList;
import java.util.List;

import team.mkhwl.CityRun.C;
import team.mkhwl.CityRun.CityRunAPP;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * @author Mush
 * 
 */
public class DBManager {
	private static final String TAG = "DBManger";

	public SQLiteDatabase db;

	public DBManager(Context context) {

		db = CityRunAPP.daHelper.getWritableDatabase();
	}

	/**
	 * 添加一组游戏点,通常这些点是属于同一个游戏路径的
	 * 
	 * @param gamePoints
	 */
	public void addgamePoints(List<GamePointDT> gamePoints, String tableName) {
		db.beginTransaction(); // 开始事务

		try {
			for (GamePointDT gamePoint : gamePoints) {
				if (tableName.equals("gamePoint")) {
					db.execSQL("INSERT INTO " + tableName
							+ " VALUES(null, ?, ?, ?, ?, ? ,?,?,?)",
							new Object[] { gamePoint.pathid, gamePoint.oridnal,
									gamePoint.gpname, gamePoint.lon,
									gamePoint.lat, gamePoint.question,
									gamePoint.answer, gamePoint.MSG });
				} else {
					db.execSQL("INSERT INTO " + tableName
							+ " VALUES(null, (select MAX(_id) from "
							+ C.database.table_usergamepath
							+ "), ?, ?, ?, ? ,?,?,?)", new Object[] {
							gamePoint.oridnal, gamePoint.gpname, gamePoint.lon,
							gamePoint.lat, gamePoint.question,
							gamePoint.answer, gamePoint.MSG });
				}
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * 添加一个游戏路径
	 * 
	 * @param gamePath
	 */
	public void addgamePath(GamePathDT gamePath, String tableName) {
		db.beginTransaction(); // 开始事务
		try {
			if (tableName.equals("gamePath")) {

				db.execSQL("INSERT INTO " + tableName
						+ " VALUES(null, ?, ?, ?,?,?)", new Object[] {
						gamePath.pathid, gamePath.type, gamePath.pathname,
						gamePath.pathdescription, gamePath.pathlocation });
			} else {
				db.execSQL("INSERT INTO " + C.database.table_usergamepath
						+ " VALUES(null,?,?, ?, ?)", new Object[] {
						C.database.table_usergamepoint, gamePath.pathname,
						gamePath.pathdescription, gamePath.pathlocation });
			}

			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * 查询一组游戏路径
	 * 
	 * @param cityName
	 *            如果为空则返回所有路径否则返回指定城市的路径
	 * @return List<GamePathDT>
	 */
	public List<GamePathDT> queryPaths(String cityName) {
		String sql = "";
		if ("" == cityName || null == cityName) {
			sql = "SELECT * FROM gamepath";
		} else {
			sql = "SELECT * FROM gamepath WHERE pathlocation = " + "\'"
					+ cityName + "\'";
		}

		String sql2 = "SELECT * FROM " + C.database.table_usergamepath
				+ " WHERE pathlocation = " + "\'" + cityName + "\'";
		ArrayList<GamePathDT> paths = new ArrayList<GamePathDT>();
		Cursor c = db.rawQuery(sql, null);
		Cursor c1 = db.rawQuery(sql2, null);
		if (c1 != null) {

			while (c1.moveToNext()) {
				GamePathDT path = new GamePathDT();
				path.pathid = c1.getInt(c1.getColumnIndex("_id"));
				path.type = c1.getString(c1.getColumnIndex("type"));
				path.pathname = c1.getString(c1.getColumnIndex("pathname"));
				path.pathdescription = c1.getString(c1
						.getColumnIndex("pathdescription"));
				path.pathlocation = c1.getString(c1
						.getColumnIndex("pathlocation"));

				paths.add(path);

			}
			c1.close();
		}
		if (c != null) {
			while (c.moveToNext()) {
				GamePathDT path = new GamePathDT();
				path._id = c.getInt(c.getColumnIndex("_id"));
				path.pathid = c.getInt(c.getColumnIndex("pathid"));
				path.type = c.getString(c.getColumnIndex("type"));
				path.pathname = c.getString(c.getColumnIndex("pathname"));
				path.pathdescription = c.getString(c
						.getColumnIndex("pathdescription"));
				path.pathlocation = c.getString(c
						.getColumnIndex("pathlocation"));

				paths.add(path);

			}
			c.close();
		}
		if (null == c && null == c1) {
			return null;
		}

		return paths;
	}

	/**
	 * 查询给定pathid的游戏路径的所有游戏点
	 * 
	 * @param pathId
	 * @return
	 */
	public List<GamePointDT> queryPoints(int pathId, String tableName) {
		ArrayList<GamePointDT> points = new ArrayList<GamePointDT>();

		String sql = "SELECT * FROM " + tableName + " WHERE pathid = " + pathId;
		Cursor c = db.rawQuery(sql, null);


		while (c.moveToNext()) {
			GamePointDT point = new GamePointDT();
			point._id = c.getInt(c.getColumnIndex("_id"));
			point.pathid = c.getInt(c.getColumnIndex("pathid"));
			point.oridnal = c.getInt(c.getColumnIndex("oridnal"));
			point.gpname = c.getString(c.getColumnIndex("gpname"));
			point.lon = c.getInt(c.getColumnIndex("lon"));
			point.lat = c.getInt(c.getColumnIndex("lat"));
			point.question = c.getString(c.getColumnIndex("question"));
			point.answer = c.getString(c.getColumnIndex("answer"));
			point.MSG = c.getString(c.getColumnIndex("msg"));

			points.add(point);
		}
		c.close();
		return points;
	}

	
	public Cursor queryTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM gamepoint", null);
		return c;
	}

	/**
	 * close database
	 */
	public void closeDB() {
		db.close();
	}
}
