package team.mkhwl.CityRun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import team.mkhwl.CityRun.db.GamePathDT;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class QueryPathActivity extends ListActivity implements
		OnItemLongClickListener {
	private static final String TAG = "QueryPathActivity";
	private Dialog dialog;
	private static Handler tagHandler;
	private String cityname;
	private ListView listView;
	private int itemIndex = -1;
	List<GamePathDT> paths = CityRunAPP.mgr.queryPaths(cityname);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 接收参数
		Intent intent = getIntent();
		cityname = intent.getStringExtra("cityname");

		Builder builder = new AlertDialog.Builder(QueryPathActivity.this);
		builder.setTitle("本地的游戏");
		builder.setIcon(R.drawable.mapmark);
		LayoutInflater facty = LayoutInflater.from(QueryPathActivity.this);
		View dialogView = facty.inflate(R.layout.selectgame_dialog, null);

		this.listView = (ListView) dialogView
				.findViewById(R.id.selectgame_listView);
		this.listView.setCacheColorHint(0);

		builder.setView(dialogView);
		dialog = builder.create();
		paths.addAll(CityRunAPP.mgr.queryPaths(cityname));
		if (0 == paths.size()) {
			Toast.makeText(QueryPathActivity.this, "您似乎来到了猴桑没有探索过的未知区域... ...",
					Toast.LENGTH_LONG).show();
		}

		final ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

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
			OnItemClickListener onItemClickListener = new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub

					dialog.dismiss();
					finish();

				}

			};
			listView.setOnItemClickListener(onItemClickListener);
		} else {
			listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				public void onCreateContextMenu(ContextMenu menu, View arg1,
						ContextMenuInfo arg2) {
					menu.setHeaderTitle("" + itemIndex);
					menu.add(0, 0, 0, "游戏信息");
					menu.add(0, 1, 1, "开始游戏");
					menu.add(0, 2, 2, "删除游戏");
					menu.add(0, 3, 3, "编辑游戏");
				}

			});
			listView.setOnItemLongClickListener(this);
			listView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					GamePath gamePath = new GamePath((CityRunAPP.mgr
							.queryPoints(paths.get((int) arg3).pathid,
									paths.get((int) arg3).type)), paths
							.get((int) arg3));
					// mapView.getOverlays().remove(mapView.getOverlays());
					Message msg = Message.obtain();
					msg.what = C.handler.start_game;
					msg.obj = gamePath;
					tagHandler.sendMessage(msg);
					// queryAllPointsDialog(ct,paths.get((int)arg3).pathid);
					dialog.dismiss();
					finish();
				}
				

			});
		}
		SimpleAdapter adapter = new SimpleAdapter(QueryPathActivity.this, list,
				android.R.layout.simple_list_item_2, new String[] { "name",
						"info" }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		listView.setAdapter(adapter);

		dialog.show();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Toast.makeText(QueryPathActivity.this,
					"菜单第" + item.getItemId() + "游戏信息", Toast.LENGTH_SHORT)
					.show();
			finish();
			break;
		case 1:
			Toast.makeText(QueryPathActivity.this,
					"菜单第" + item.getItemId() + "开始游戏", Toast.LENGTH_SHORT)
					.show();
			finish();
			break;
		case 2:
			Toast.makeText(QueryPathActivity.this,
					"菜单第" + item.getItemId() + "删除游戏", Toast.LENGTH_SHORT)
					.show();
			finish();
			break;
		case 3:
			Toast.makeText(QueryPathActivity.this,
					"菜单第" + item.getItemId() + "编辑游戏", Toast.LENGTH_SHORT)
					.show();
			finish();
			break;

		}
		return super.onContextItemSelected(item);
	}

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long id) {
		itemIndex = position;
		return false;// 此处需要注意，如果改成true，不会再次触发ListView的setOnCreateContextMenuListener事件
	}
	
	

	public static void init(Handler INIT_tagHandler) {
		tagHandler = INIT_tagHandler;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			this.finish();
			return super.onKeyDown(keyCode, event);
		}
		return false;
		
	}
}