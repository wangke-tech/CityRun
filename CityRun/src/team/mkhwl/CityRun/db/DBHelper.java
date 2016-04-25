package team.mkhwl.CityRun.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import team.mkhwl.CityRun.R;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
	private static final int NUMBER_OF_File = 3;// 游戏点文件的数量
	private static final String TAG = "DBHelper";
	private static final String DATABASE_NAME = "cityrun.db";
	private static final int DATABASE_VERSION = 3;
	Context context;

	public boolean isFiretCreat = false;

	public DBHelper(Context context) {

		// CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	// 数据库第一次被创建时onCreate会被调用
	@Override
	public void onCreate(final SQLiteDatabase db) {

		this.isFiretCreat = true;
		db.execSQL("CREATE TABLE IF NOT EXISTS gamepoint ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "pathid INTEGER, " + "oridnal INTEGER,"
				+ "gpname NVARCHAR(15)," + "lon INTEGER, " + "lat INTEGER, "
				+ "question NVARCHAR(240), " + "answer NVARCHAR(24) , msg NVARCHAR(240))");
		db.execSQL("CREATE TABLE IF NOT EXISTS gamepath ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "pathid INTEGER,type VARCHAR(24),"
				+ "pathname NVARCHAR(15)," + "pathdescription NVARCHAR(240),"
				+ "pathlocation NVARCHAR(24) )");
		db.execSQL("CREATE TABLE IF NOT EXISTS userdata("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "userid INTEGER, albumid INTEGER, " 
				+ "mileage INTENGER, maxspeed)");
		db.execSQL("CREATE TABLE IF NOT EXISTS cityname("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "zh NVARCHAR(4), " + "en VARCHAR(10))");
		db.execSQL("CREATE TABLE IF NOT EXISTS usergamepoint ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "pathid INTEGER, " + "oridnal INTEGER,"
				+ "gpname NVARCHAR(15)," + "lon INTEGER, " + "lat INTEGER, "
				+ "question NVARCHAR(240), " + "answer NVARCHAR(24) , msg NVARCHAR(240))");
		db.execSQL("CREATE TABLE IF NOT EXISTS usergamepath ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR(24),"
				+ "pathname NVARCHAR(15)," + "pathdescription NVARCHAR(240),"
				+ "pathlocation NVARCHAR(24) )");


	//	final SQLiteDatabase dbb = db;
		new Thread() {
			@Override
			public void run() {

				for (int i = 0; i < NUMBER_OF_File; i++) {
					loadDEFPoints(i, db);
				}

			}
		}.start();

	}

	private void loadDEFPoints(int fileID, SQLiteDatabase db) {

		Resources res = context.getResources();
		InputStream in = null;
		BufferedReader br = null;

		try {
			in = res.openRawResource(R.raw.data_0 + fileID);
			String str = "";
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			str = br.readLine();

			if (str.equals("points")) {
				while ((str = br.readLine()) != null) {
					GamePointDT gamePoint = new GamePointDT();
					// Log.v("读取一行", str);
					String tempStr = "";
					int count = 0;
					for (int i = 0; i < str.length(); i++) {
						if (str.charAt(i) != '|') {
							// Log.v("读了一个字符", "" + str.charAt(i));
							tempStr = tempStr + (str.charAt(i) + "");
						} else {
							// Log.v("读了一个数据", tempStr);
							count++;
							switch (count) {
							case 1:
								break;
							case 2:
								gamePoint.pathid = Integer.parseInt(tempStr);
								break;// patchid
							case 3:
								gamePoint.oridnal = Integer.parseInt(tempStr);
								break;// oridnal
							case 4:
								gamePoint.gpname = tempStr;
								break;// gpname
							case 5:
								gamePoint.MSG = tempStr;
								break;
							case 6:
								gamePoint.lon = Integer.parseInt(tempStr);
								break;// lon
							case 7:
								gamePoint.lat = Integer.parseInt(tempStr);
								break;// lat
							case 8:
								gamePoint.question = tempStr;
								break;// question
							case 9:
								gamePoint.answer = tempStr;
								break;// answer
							}
							tempStr = "";
						}

					}
					db.execSQL(
							"INSERT INTO gamepoint VALUES(null, ?, ?, ?, ?, ? ,?,?,?)",
							new Object[] { gamePoint.pathid, gamePoint.oridnal,
									gamePoint.gpname, gamePoint.lon,
									gamePoint.lat, gamePoint.question,
									gamePoint.answer ,gamePoint.MSG});
				}
			} else if (str.equals("paths")) {
				while ((str = br.readLine()) != null) {
					GamePathDT gamePath = new GamePathDT();
					// Log.v("读取一行", str);
					String tempStr = "";
					int count = 0;
					for (int i = 0; i < str.length(); i++) {
						if (str.charAt(i) != '|') {
							// Log.v("读了一个字符", "" + str.charAt(i));
							tempStr = tempStr + (str.charAt(i) + "");
						} else {
							// Log.v("读了一个数据", tempStr);
							count++;
							switch (count) {
							case 1:
								break;
							case 2:
								gamePath.pathid = Integer.parseInt(tempStr);
								break;
							case 3:
								gamePath.type = tempStr;
								break;
							case 4:
								gamePath.pathname = tempStr;
								break;
							case 5:
								gamePath.pathdescription = tempStr;
								break;
							case 6:
								gamePath.pathlocation = tempStr;
								break;
							}
							tempStr = "";
						}
						

					}
					//gamePath.pathtype = C.game_prop.DEF_GMAE;
					db.execSQL(
								"INSERT INTO gamepath VALUES(null,?, ?,?,?,?)",
								new Object[] { gamePath.pathid,
										gamePath.type,
										gamePath.pathname,
										gamePath.pathdescription,
										gamePath.pathlocation ,
										});

				}
			} else if (str.equals("cityname")) {
				while ((str = br.readLine()) != null) {
					String cityname[] = new String[2];
					// Log.v("读取一行", str);
					String tempStr = "";
					int count = 0;
					for (int i = 0; i < str.length(); i++) {
						if (str.charAt(i) != '|') {
							// Log.v("读了一个字符", "" + str.charAt(i));
							tempStr = tempStr + (str.charAt(i) + "");
						} else {
							// Log.v("读了一个数据", tempStr);
							count++;
							switch (count) {
							case 1:
								cityname[0] = tempStr;
								break;
							case 2:
								cityname[1] = tempStr;
								break;
							}
							tempStr = "";
						}

					}
					db.execSQL("INSERT INTO cityname VALUES(null, ?, ?)",
							new Object[] { cityname[0], cityname[1] });
				}
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE gamepoint ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE gamepath ADD COLUMN other STRING");
	}

}
