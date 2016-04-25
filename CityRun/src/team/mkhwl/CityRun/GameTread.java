package team.mkhwl.CityRun;

import java.util.ArrayList;
import java.util.Collection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.CoordinateConvert;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;

public class GameTread extends HandlerThread implements Runnable {

	private static final String TAG = "GameTread";

	private GamePath gamePath;
	private Collection<GamePointOverlay> gamePathOverlays;
	private MapView mMapView;
	private static boolean dialogIsShow = false;
	private String answerQA, pointname, pointquestion;
	public boolean isRunning = false;
	private Handler hostHandler;

	private CRLocationListeneri myCRLocationListeneri = new CRLocationListeneri(){

		ArrayList<GeoPoint> algeopoints = new ArrayList<GeoPoint>(1);
		PathOverlay pho;
		
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			GamePoint gpWgs84 = new GamePoint(
					(int) (location.getLatitude() * 1e6),
					(int) (location.getLongitude() * 1e6));
			GeoPoint gpBaidu = CoordinateConvert
					.bundleDecode(CoordinateConvert
							.fromWgs84ToBaidu(gpWgs84));
			//调试代码///////////////////////////////////////////////////////////////////////////
			//gpBaidu = gpWgs84;
			// 得到路径集合

			algeopoints.add(gpBaidu);
			if (mMapView.getOverlays().contains(pho)) {
				mMapView.getOverlays().remove(pho);
			}
			pho = new PathOverlay(algeopoints);
			// 绘制路径
			mMapView.getOverlays().add(pho);

			if (null == gamePath.getnow()) {
				// 这里表示游戏完成!
				CRLocationlistener.removeUpdates(myCRLocationListeneri);
				isRunning = false;
				//将游戏完成的消息通知主线程
				hostHandler.sendEmptyMessage(C.handler.finish_game);
				return;
			}
			if (gamePath.getnow().onPoint(gpBaidu)) {
				if (dialogIsShow == false) {
					// 弹出检查点问题
					checkAnswerDialog();
				}
			}
			
		}};

	

	public void checkAnswerDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				mMapView.getContext());
		LayoutInflater factory = LayoutInflater.from(mMapView.getContext());
		final View check_point_QA = factory.inflate(R.layout.check_point_qa,
				null);
		builder.setView(check_point_QA);
		TextView pointname;
		TextView pointquestion;
		pointname = (TextView) check_point_QA.findViewById(R.id.point_Qname);
		pointquestion = (TextView) check_point_QA
				.findViewById(R.id.point_Question);
		pointname.setText("监测点名字: " + gamePath.getnow().getGpName());
		pointquestion.setText("监测点问题: " + gamePath.getnow().getQuestion());
		builder.setTitle("请输入您的答案");

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText answer;

				answer = (EditText) check_point_QA
						.findViewById(R.id.point_answer);
				answerQA = answer.getText().toString();
				if (gamePath.getnow().checkAnswer(answerQA)) {
					Toast.makeText(mMapView.getContext(),
							"已完成" + gamePath.getnow().getGpName(),
							Toast.LENGTH_LONG).show();

					gamePath.getnow()
							.setStatus(C.game.gamePointStatus_Finished);
					gamePath.getnow().setFinishFlag(true);
					if(gamePath.getnow()!=null){
						gamePath.getnow().setStatus(C.game.gamePointStatus_OnGoing);
					}else{
						//这里表示游戏结束
						isRunning = false;
					}
					

				} else {
					Toast.makeText(mMapView.getContext(), "答案错误,重新输入!!",
							Toast.LENGTH_LONG).show();

					checkAnswerDialog();
				}
				dialogIsShow = false;

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				dialogIsShow = false;// 不论答案对错都得赋值 点取消后需要再次运行

			}
		});

		builder.create().show();
		dialogIsShow = true;
	}

	public GameTread(String name, GamePath gamePath, MapView mMapView, Handler handler) {
		super(name);
		// TODO Auto-generated constructor stub
		this.gamePath = gamePath;
		this.mMapView = mMapView;
		this.hostHandler = handler;
	}

	
	public void run() {
		// TODO Auto-generated method stub
		if (0 == gamePath.size()) {
			return;
		}
		this.isRunning = true;
		gamePathOverlays = gamePath.getPahtOverlays();
		mMapView.getOverlays().addAll(gamePathOverlays);
		CRLocationlistener.requestUpdates(myCRLocationListeneri);
		gamePath.getnow().setStatus(C.game.gamePointStatus_OnGoing);
	}
	
	public void exit(){
		CRLocationlistener.removeUpdates(myCRLocationListeneri);
		mMapView.getOverlays().removeAll(mMapView.getOverlays());
		this.isRunning = false;
	}
	

}
