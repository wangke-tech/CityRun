package team.mkhwl.CityRun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import team.mkhwl.CityRun.Update.Update;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.renren.api.activity.MyCaremaActivity;
import com.renren.api.connect.android.view.ProfileNameView;
import com.renren.api.connect.android.view.ProfilePhotoView;
import com.renren.api.function.StatusDemo;
import com.renren.api.function.WidgetDialogDemo;

public class MapActivityMain extends MapActivity implements
		OnItemClickListener, OnClickListener {
	public final static String tag = "MapActivityMain";
	/**
	 * 常量
	 * */
	private final static int NUMBER_OF_MENUITEM = 11;
	// private final static int LOCATION_CHANGEN = 2;
	// private final int ONGAME = 1;
	/**
	 * 地图
	 * */
	private MapView mMapView = null;
	private boolean satelliteflag = false;
	/**
	 * 组件-
	 * */
	private Button satelliteButton;
	private TextView info = null;
	private SlidingDrawer mDrawer;
	// private LinearLayout container;
	// 定义ListView组件
	private ListView datalist = null;
	// 定义显示的内容包
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	// 进行数据的转换操作
	private SimpleAdapter simpleAdapter = null;
	GamePath path;

	// 位置管理
	// private LocationManager locationManager;
	private LocationListener mLocationListener = null;
	private CRMyLocationOverlay myLocationOverlay;
	//
	private GameTread mGameThread = null;

	// handler
	private Handler mainMapActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case C.handler.creat_gamepath: {
				mMapView.getOverlays().removeAll(mMapView.getOverlays());
				path = (GamePath) msg.obj;
				Toast.makeText(
						MapActivityMain.this,
						"收到一个长度为" + path.size() + "的游戏"
								+ path.getnow().getGpName(), Toast.LENGTH_LONG)
						.show();
				if (!CRMyLocationOverlay.isShow) {
					CRMyLocationOverlay.enableMyLocation();
				}
				break;
			}
			case C.handler.start_game: {
				mMapView.getOverlays().removeAll(mMapView.getOverlays());
				path = (GamePath) msg.obj;
				mMapView.getOverlays().remove(mMapView.getOverlays());
				mMapView.getController().animateTo(path.getnow());
				new DrawGamePath(path.getPath2GeoList(), CityRunAPP.mBMapMan,
						mMapView);
				mGameThread = new GameTread("game_thread", path, mMapView,
						mainMapActivityHandler);
				mGameThread.start();
				if (!CRMyLocationOverlay.isShow) {
					CRMyLocationOverlay.enableMyLocation();
				}

				break;
			}
			case C.handler.finish_game: {
				sendmag2renren();
				path = null;
			}
			case C.handler.location_changed: {

				break;
			}

			}
		};
	};;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(tag, "onCreate");
		setContentView(R.layout.map_activity);

		satelliteButton = (Button) findViewById(R.id.satelliteButton);
		satelliteButton.setOnClickListener(this);
		mMapView = (MapView) findViewById(R.id.bmapsView);

		CityRunAPP app = (CityRunAPP) this.getApplication();
		if (CityRunAPP.mBMapMan == null) {
			CityRunAPP.mBMapMan = new BMapManager(getApplication());
			CityRunAPP.mBMapMan.init(C.baidu.Key,
					new CityRunAPP.MyGeneralListener());
		}

		super.initMapActivity(CityRunAPP.mBMapMan);
		Log.d(tag, "onCreate \t initmap");
		
		mMapView.setBuiltInZoomControls(true);
		// 设置在缩放动画过程中也显示overlay,默认为不绘制
		mMapView.setDrawOverlayWhenZooming(true);

		/** 设置抽屉 **/
		// 头像和用户名
/*		ProfilePhotoView profilePhotoView = (ProfilePhotoView) findViewById(R.id.renrenpoto);
		profilePhotoView.setUid(CityRunAPP.renren.getCurrentUid());
		ProfileNameView profileNameView = (ProfileNameView) findViewById(R.id.renrenname);
		profileNameView.setUid(CityRunAPP.renren.getCurrentUid(),
				CityRunAPP.renren);*/

		mDrawer = (SlidingDrawer) findViewById(R.id.menuSliding);
		this.datalist = (ListView) super.findViewById(R.id.datalist); // 取得组件
		this.datalist.setDivider(getResources().getDrawable(
				R.drawable.list_divider));
		// 生成动态数组，加入数据
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < NUMBER_OF_MENUITEM; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.listbutton01 + i);// 图像资源的IDi
			map.put("ItemText",
					"  "
							+ getResources().getString(
									R.string.menulist01_found_game + i));
			listItem.add(map);
		}

		// 生成适配器的Item和动态数组对应的元素
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
				R.layout.mainactivity_listview,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemText" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemText });

		this.datalist.setAdapter(listItemAdapter);
		this.datalist.setOnItemClickListener(this);
		// 防止重绘ListView时ListView变黑
		this.datalist.setCacheColorHint(0);
		// 注册定位事件
		CityRunAPP.locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new CRLocationlistener();
		myLocationOverlay = new CRMyLocationOverlay(mMapView);
		WidgetDialogDemo
				.showFeedDialog(MapActivityMain.this, CityRunAPP.renren);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.satelliteButton: {

			if (satelliteflag) {
				mMapView.setSatellite(false);
				satelliteflag = false;
				satelliteButton.setText("卫星");
			} else {
				mMapView.setSatellite(true);
				satelliteflag = true;
				satelliteButton.setText("地图");
			}

			break;
		}

		}

	}

	// 监听点击事件
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		if (0 == id) {// 查找游戏

			if (null == CityRunAPP.userCity | "" == CityRunAPP.userCity) {
				Toast.makeText(MapActivityMain.this, "请等待首次定位完成!!!",
						Toast.LENGTH_LONG).show();
				return;
			}
			MDialog.queryPathsDialog(MapActivityMain.this,
					mainMapActivityHandler, CityRunAPP.userCity);

			/*
			 * Intent intent = new Intent(); intent.putExtra("cityname",
			 * CityRunAPP.userCity);
			 * QueryPathActivity.init(mainMapActivityHandler);
			 * intent.setClass(this, QueryPathActivity.class);
			 * this.startActivity(intent);
			 */

		} else if (1 == id) {// 创建游戏

			CRMyLocationOverlay.disableMyLocation();

			mMapView.getOverlays().removeAll(mMapView.getOverlays());
			CreatGameOverlay creatGameOverlay = new CreatGameOverlay(mMapView,
					mainMapActivityHandler);
			mMapView.getOverlays().add(creatGameOverlay);

		} else if (2 == id) {// 雷达模式
			if (null == this.path) {
				MDialog.warningDialog(this, "游戏路径为空,必须添加游戏后才能开启雷达模式.", "警告",
						"确认", "好吧");
				return;
			}
			Intent intent = new Intent();

			team.mkhwl.CityRun.Radar.RadarActivity.init(path
					.getPath2ArrayList());

			intent.setClass(this, team.mkhwl.CityRun.Radar.RadarActivity.class);

			this.startActivity(intent);
		} else if (3 == id) {// 指南针
			Intent intent = new Intent(MapActivityMain.this,
					team.mkhwl.CityRun.Comopass.ComopassActivity.class);

			MapActivityMain.this.startActivity(intent);

		} /*
		 * else if (4 == id) {// 记录足迹 CRMyLocationOverlay.eableMyPath();
		 * 
		 * }
		 */
		else if (4 == id) {// 人人分享
			StatusDemo.publishStatusOneClick(MapActivityMain.this,
					CityRunAPP.renren);
		} else if (5 == id) {// 拍照上传
			Intent intent = new Intent(MapActivityMain.this,
					MyCaremaActivity.class);

			MapActivityMain.this.startActivity(intent);

		} else if (6 == id) {// 检查更新
			Update update = new Update(this);
		} else if (7 == id) {// 查看帮助
			Intent intent = new Intent(MapActivityMain.this,
					HelpSlideActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean("ifjump", false);
			intent.putExtras(bundle);
			startActivity(intent);
		} else if (8 == id) {// 解除绑定

			CityRunAPP.renren.logout(getApplicationContext());
			this.finish();
			Intent intent = null;
			intent = new Intent(MapActivityMain.this, LoginActivity.class);
			startActivity(intent);
		} else if (9 == id) {// 退出
			MDialog.dialog_Exit(MapActivityMain.this);
		} else if (10 == id) {// 关于我们
			Uri uri = Uri
					.parse("http://www.mushapi.com/cityrunweb/about/mkhwl.html");
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(it);
		} /*
		 * else if (11 == id) { Intent intent = new Intent();
		 * intent.putExtra("cityname", CityRunAPP.userCity);
		 * QueryPathActivity.init(mainMapActivityHandler); intent.setClass(this,
		 * QueryPathActivity.class); this.startActivity(intent); }
		 */
		// 点击抽屉菜单后关闭抽屉
		mDrawer.close();
	}

	@Override
	// 重写撤销键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mDrawer.isOpened()) {
				mDrawer.close();
				return true;
			} else if (mGameThread != null && mGameThread.isRunning) {

				AlertDialog.Builder builder = new Builder(this);
				builder.setMessage("确定要退出该游戏吗?");
				builder.setTitle("退出游戏");
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								mGameThread.exit();
								path = null;
								dialog.dismiss();

							}
						});

				builder.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}

						});

				builder.create().show();
				return true;

			} else if (!mDrawer.isOpened()) {
				MDialog.dialog_Exit(MapActivityMain.this);
				return true;

			}

		} else if (keyCode == KeyEvent.KEYCODE_MENU) {

			if (mDrawer.isOpened()) {
				mDrawer.close();
				return true;
			} else {
				mDrawer.open();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void sendmag2renren() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("您已经完了这个游戏,希望您玩的愉快. 是否马上发送消息到人人网跟好友分享一下呢?");
		builder.setTitle("游戏完成");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				StatusDemo.publishStatusOneClick(MapActivityMain.this,
						CityRunAPP.renren);
				dialog.dismiss();

			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}

				});

		builder.create().show();
	}

	@Override
	protected void onPause() {
		if (mLocationListener != null) {
			CityRunAPP.locationManager.removeUpdates(mLocationListener);
			mLocationListener = null;
		}
		CRMyLocationOverlay.disableMyLocation();
		// CityRunAPP.mBMapMan.stop();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// 注册定位事件，定位后将地图移动到定位点
		if (mLocationListener == null) {
			mLocationListener = new CRLocationlistener();
		}
		CityRunAPP.locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
		CRMyLocationOverlay.enableMyLocation();
		CityRunAPP.mBMapMan.start();
		super.onResume();	
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
