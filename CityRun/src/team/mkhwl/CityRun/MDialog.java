package team.mkhwl.CityRun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import team.mkhwl.CityRun.db.GamePathDT;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MDialog {
	private static final String TAG = "querDidalog";
	private static Dialog dialog;

	public static void queryPathsDialog(Context context,
			final Handler tagHandler, String cityname) {
		// final DBManager mgr = new DBManager(context);
		// final Context ct = context;

		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("本地的游戏");
		builder.setIcon(R.drawable.mapmark);
		LayoutInflater facty = LayoutInflater.from(context);
		View dialogView = facty.inflate(R.layout.selectgame_dialog, null);

		ListView listView;
		listView = (ListView) dialogView.findViewById(R.id.selectgame_listView);
		// 防止重绘ListView时ListView变黑
		listView.setCacheColorHint(0);

		builder.setView(dialogView);
		dialog = builder.create();
		final List<GamePathDT> paths = CityRunAPP.mgr.queryPaths(cityname);
		if (0 == paths.size()) {
			Toast.makeText(context, "您似乎来到了猴桑没有探索过的未知区域... ...",
					Toast.LENGTH_LONG).show();
			// return;
		}

		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (GamePathDT path : paths) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", path.pathname);
			map.put("info", "游戏编号:" + path.pathid + "\n游戏名称:" + path.pathname
					+ "\n游戏描述:" + path.pathdescription + "\n游戏地区:"
					+ path.pathlocation);
			list.add(map);
		}

		if (0 == list.size()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", "此处没有预设游戏");
			map.put("info", "您似乎来到了我们没有探索过的未知区域,在这您只能自己设定游戏,自娱自乐也不错哦!\n"
					+ "PS:我们会加倍努力完善游戏的.\n又PS: 提供游戏请联系btyh17mxy@outlook.com");
			list.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(context, list,
				android.R.layout.simple_list_item_2, new String[] { "name",
						"info" }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				GamePath gamePath = new GamePath((CityRunAPP.mgr.queryPoints(
						paths.get((int) arg3).pathid,
						paths.get((int) arg3).type)), paths.get((int) arg3));
				// mapView.getOverlays().remove(mapView.getOverlays());
				Message msg = Message.obtain();
				msg.what = C.handler.start_game;
				msg.obj = gamePath;
				tagHandler.sendMessage(msg);
				// queryAllPointsDialog(ct,paths.get((int)arg3).pathid);
				dialog.dismiss();
			}

		});

		dialog.show();
	}

	// public static void queryAllPointsDialog(Context context, int pahtId,
	// String tableName) {
	// Log.v(TAG, "queryAllPaths");
	//
	//
	// Builder builder = new AlertDialog.Builder(context);
	// LayoutInflater facty = LayoutInflater.from(context);
	// View dialogView = facty.inflate(R.layout.selectgame_dialog, null);
	//
	// ListView listView;
	// listView = (ListView) dialogView.findViewById(R.id.selectgame_listView);
	// // //////////////////////////////////////////////////////////////
	// List<GamePointDT> points = CityRunAPP.mgr.queryPoints(pahtId, tableName);
	// ArrayList<Map<String, String>> list = new ArrayList<Map<String,
	// String>>();
	// for (GamePointDT point : points) {
	// HashMap<String, String> map = new HashMap<String, String>();
	// map.put("name", point.gpname);
	// map.put("info", "游戏编号:" + point.pathid + "\n序号:" + point.oridnal
	// + "\n" + point.lon + "\n" + point.lat + "\n"
	// + point.question + "\n");
	// list.add(map);
	// }
	// SimpleAdapter adapter = new SimpleAdapter(context, list,
	// android.R.layout.simple_list_item_2, new String[] { "name",
	// "info" }, new int[] { android.R.id.text1,
	// android.R.id.text2 });
	// listView.setAdapter(adapter);
	// // //////////////////////////////////////////////////////
	//
	// builder.setView(dialogView);
	// builder.create().show();
	// }

	public static void dialog_Exit(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				android.os.Process.killProcess(android.os.Process.myPid());
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

	/**
	 * 一个有两个按钮的警告Dialog,并不影响当前进程.
	 * 
	 * @param context
	 * @param Msg
	 *            Dialog显示的信息
	 * @param Title
	 *            Dialog标题
	 * @param buttonStr1
	 *            左边按钮上显示的文字
	 * @param buttonStr2
	 *            右边按钮上显示的问题
	 */
	public static void warningDialog(Context context, String Msg, String Title,
			String buttonStr1, String buttonStr2) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(Msg);
		builder.setTitle(Title);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton(buttonStr1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.setNegativeButton(buttonStr2,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}

				});

		builder.create().show();
	}
}
