package team.mkhwl.CityRun;

import java.util.ArrayList;

import team.mkhwl.CityRun.db.GamePathDT;
import team.mkhwl.CityRun.db.GamePointDT;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;

/**
 * @author Mush
 * 
 */

public class CreatGameOverlay extends Overlay {

	private final static String TAG = "CreatGameOverlayThread";
	private MapView mMapView;
	private Context context;
	private Handler tagHandler;
	private boolean saveFlag = false;
	private boolean isFirst = true;
	String pathCity = "";
	// 一个标记
	// private boolean flag = false;

	public GamePath path = new GamePath();
	GamePathDT PathDT = new GamePathDT();

	public CreatGameOverlay(MapView mMapView, Handler tagHandler) {
		super();
		this.mMapView = mMapView;
		this.context = mMapView.getContext();
		this.tagHandler = tagHandler;
	}


	/**
	 * 用于创建游戏点的Dialog
	 * 
	 * @param geoPoint
	 */
	public void addGamePointDialog(final GeoPoint geoPoint) {

		//String[] countriesStr = { "随便玩玩", "保存到我的游戏", "共享该游戏" };
		TextView lon;// lon经度 lat纬度
		TextView lat;
	//	ArrayAdapter<String> adapter;

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater factory = LayoutInflater.from(context);
		final View textEntryView = factory.inflate(R.layout.creatgame_dialog,
				null);
		builder.setView(textEntryView);

	//	adapter = new ArrayAdapter<String>(context,
				//android.R.layout.simple_spinner_item, countriesStr);

		builder.setTitle("创建游戏点");
		lon = (TextView) textEntryView.findViewById(R.id.lon);
		lat = (TextView) textEntryView.findViewById(R.id.lat);

		float latf = geoPoint.getLatitudeE6();
		float lonf = geoPoint.getLongitudeE6();
		lon.setText("经度：" + latf / 1000000);
		lat.setText("纬度：" + lonf / 1000000);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText name;
				EditText question;
				EditText answer;
				EditText msgEdit;

				name = (EditText) textEntryView.findViewById(R.id.name);
				question = (EditText) textEntryView.findViewById(R.id.question);
				answer = (EditText) textEntryView.findViewById(R.id.answer);
				msgEdit = (EditText) textEntryView.findViewById(R.id.msg);

				GamePoint gamePoint = new GamePoint(geoPoint);
				gamePoint.setGpName(name.getText().toString());
				gamePoint.setQuestion(question.getText().toString());
				gamePoint.setAnswer(answer.getText().toString());
				gamePoint.setMSG(msgEdit.getText().toString());

				mMapView.getOverlays().add(new GamePointOverlay(gamePoint));
				path.addGamePoint(gamePoint);
				commonDialog(context);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		builder.create().show();

	}

	/**
	 * 在给定的Context中弹出一个普通Dialog
	 * 
	 * @param context
	 */
	public void commonDialog(final Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("已经成功添加" + path.size() + "个检查点，是否继续？");
		builder.setTitle("提示");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

				/*
				 * 此处应该添加根据不同的游戏属性而进行的下一步操作,例如添加到数据库或上传服务器 *
				 */

				if (saveFlag) {
					//DBManager mgr;
					//mgr = new DBManager(context);
					CityRunAPP.mgr.addgamePath(PathDT, C.database.table_usergamepath);
					ArrayList<GamePointDT> points = new ArrayList<GamePointDT>();
					for (int i = 0; i < path.size(); i++) {
						points.add(path.getPath2List().get(i).toGPDT());
					}
					CityRunAPP.mgr.addgamePoints(points, C.database.table_usergamepoint);

				}
				Message msg = Message.obtain();
				msg.what = C.handler.start_game;
				msg.obj = path;
				tagHandler.sendMessage(msg);

			}
		});

		builder.setNegativeButton("继续",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}

				});

		builder.create().show();

	}

	public void creatGamePathDialog(final Context context,
			final String gameLocation) {
		final Dialog dialog;
		final Spinner mySpinner;
		String[] countriesStr = { "随便玩玩", "保存到我的游戏", "共享该游戏" };
		ArrayAdapter<String> adapter;

		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("创建游戏");
		builder.setIcon(R.drawable.mapmark);
		LayoutInflater facty = LayoutInflater.from(context);
		final View dialogView = facty.inflate(R.layout.creatgame_path_dialog,
				null);
		mySpinner = (Spinner) dialogView.findViewById(R.id.gameprop_spinner);
		adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, countriesStr);

		mySpinner.setAdapter(adapter);

		builder.setView(dialogView);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				EditText pathName;
				EditText pathMSG;

				pathName = (EditText) dialogView
						.findViewById(R.id.gamemsg_edittext);
				pathMSG = (EditText) dialogView
						.findViewById(R.id.gamemsg_edittext);

				PathDT.pathdescription = pathMSG.getText().toString();
				PathDT.pathname = pathName.getText().toString();
				PathDT.pathlocation = gameLocation;
				PathDT.type = C.database.table_usergamepoint;

				String str = mySpinner.getSelectedItem().toString();
				if (str.equals("保存到我的游戏")) {
					saveFlag = true;
				}

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		dialog = builder.create();

		dialog.show();

	}

	public boolean onTap(GeoPoint p, final MapView mapView) {

		// TODO Auto-generated method stub
		// 一个用于创建游戏点的Dialog
		if (isFirst) {
			 MKSearch mMKSearch = null;
			 
			 mMKSearch = new MKSearch();
				mMKSearch.init(CityRunAPP.mBMapMan, new MKSearchListener(){
					public void onGetAddrResult(MKAddrInfo result, int iError) {
						if (result == null) {
							return;
						}else {
							// 这里获取当前城市
							String str = result.strAddr;
							String strT = "";
							int index = str.indexOf("市");
							for(int i = index-1; i >= 0; i--){
								String oneStr = str.charAt(i)+"";
								if(oneStr.equals("省")|oneStr.equals("区")){
									break;
								}
								strT+=str.charAt(i);
							}
							for(int j =strT.length()-1; j>=0;j--){
								pathCity += strT.charAt(j);
							}
							creatGamePathDialog(mapView.getContext(),pathCity);
							//山东省烟台市芝罘区
							//山东省烟台市栖霞市
							//北京市东城区
						}
					}
					//其他未实现的方法
					public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
					}
					public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
					}
					public void onGetPoiResult(MKPoiResult result, int arg1, int arg2) {
					}
					public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
					}
					public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
							int arg1) {
					}
					public void onGetSuggestionResult(MKSuggestionResult arg0,
							int arg1) {
					}
				});
				
				mMKSearch.reverseGeocode(p);
		
			
			isFirst = false;
		} else {
			addGamePointDialog(p);
		}

		return false;
	}

}
