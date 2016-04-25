package team.mkhwl.CityRun;

import team.mkhwl.CityRun.db.DBHelper;
import team.mkhwl.CityRun.db.DBManager;
import android.app.Application;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.renren.api.connect.android.Renren;

public class CityRunAPP extends Application {

	public static CityRunAPP mDemoApp;
	// 人人信息
	public static Renren renren;
	// 手机信息
	public String IMEI;
	// 网络连接
	// public static Connect connect = null;
	// 百度MapAPI的管理类
	public static BMapManager mBMapMan = null;
	// 数据库
	public static DBHelper daHelper = null;
	public static DBManager mgr = null;
	// 位置管理
	public static LocationManager locationManager;
	// 用户当前城市
	public static String userCity = "";
	// 授权Key
	boolean m_bKeyRight = true; // 授权Key正确，验证通过

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {
		public void onGetNetworkState(int iError) {

			Toast.makeText(CityRunAPP.mDemoApp.getApplicationContext(),
					"您的网络出错啦！", Toast.LENGTH_LONG).show();
		}

		public void onGetPermissionState(int iError) {

			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(CityRunAPP.mDemoApp.getApplicationContext(),
						"授权错误！！！\n请联系开发者btyh17mxy@hotmail.com报告错误",
						Toast.LENGTH_LONG).show();
				CityRunAPP.mDemoApp.m_bKeyRight = false;
			}
		}
	}

	@Override
	public void onCreate() {

		mDemoApp = this;
		mBMapMan = new BMapManager(this);
		mBMapMan.init(C.baidu.Key, new MyGeneralListener());

		daHelper = new DBHelper(this);
		if(mgr == null){
			mgr = new DBManager(this);
		}
		
		// renren = new Renren(C.renren.API_KEY, C.renren.SECRET_KEY,
		// C.renren.APP_ID, CityRunAPP.mDemoApp.getApplicationContext());

		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		IMEI = tm.getDeviceId();
		/*
		 * connect = new Connect("192.168.1.103"); Connect.INIT(); String hehe =
		 * "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		 * "<ResultSet Command=\"first\">\n"+ "<INIT>\n"+
		 * "<IMEI>00a00000325a1276</IMEI>\n"+ "</INIT>\n"+ "</ResultSet>\n";
		 * Connect.send(hehe);
		 */
		// connect.run();
		// if (mBMapMan != null) {
		// mBMapMan.destroy();
		// mBMapMan = null;
		// }

		super.onCreate();
	}

	@Override
	// 建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		// TODO Auto-generated method stub
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}

}
