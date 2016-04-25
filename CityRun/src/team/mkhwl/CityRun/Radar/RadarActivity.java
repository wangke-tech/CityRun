package team.mkhwl.CityRun.Radar;

import java.text.DecimalFormat;
import java.util.ArrayList;

import team.mkhwl.CityRun.CRLocationListeneri;
import team.mkhwl.CityRun.CRLocationlistener;
import team.mkhwl.CityRun.CityRunAPP;
import team.mkhwl.CityRun.GamePoint;
import team.mkhwl.CityRun.R;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ZoomControls;

public class RadarActivity extends Activity {
	private static final String TAG = "RadarActivity";
	// private LocationManager locationManager;
	private LocationListener mLocationListener = null;
	public static ArrayList<GamePoint> points = new ArrayList<GamePoint>(1);
	RadarView radarView;
	private TextView lMSGTextView;
	private ZoomControls radarZoomer;
	private CRLocationListeneri myCRLocationListeneri = new CRLocationListeneri() {

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			String show;
			DecimalFormat sixFmt = new DecimalFormat("#.000000");
			DecimalFormat twoFmt = new DecimalFormat("#.00");
			double latitude = location.getLatitude(); // 经度
			double longitude = location.getLongitude(); // 纬度
			float degree = location.getBearing(); // 航向（北偏东的角度）
			float speed = location.getSpeed();// 航速
			float accuracy = location.getAccuracy();// 精确度
			double altitude = location.getAltitude(); // 海拔

			show = "当前坐标信息" + "\n精度：" + twoFmt.format(accuracy) + "\n经度："
					+ sixFmt.format(latitude) + "\n纬度："
					+ sixFmt.format(longitude) + "\n海拔：" + twoFmt.format(altitude)
					+ "\n航向：" + twoFmt.format(degree)  + "\n航速：" + twoFmt.format(speed) ;
			
			lMSGTextView.setText(show);

		}
	};

	public static void init(ArrayList<GamePoint> points) {
		RadarActivity.points = points;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.radar_activity);
		radarView = (RadarView) findViewById(R.id.radarview);
		lMSGTextView = (TextView) findViewById(R.id.LMSGTextView);
		lMSGTextView.getBackground().setAlpha(123);

		radarZoomer = (ZoomControls) findViewById(R.id.zoomControls);
		radarZoomer.show();
		radarZoomer.setIsZoomInEnabled(true);
		radarZoomer.setIsZoomOutEnabled(true);
		radarZoomer.setOnZoomInClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				radarView.getDrawRadarThread().zoonIn();

			}

		});
		radarZoomer.setOnZoomOutClickListener(new OnClickListener() {

			public void onClick(View v) {
				radarView.getDrawRadarThread();
				// TODO Auto-generated method stub
				radarView.getDrawRadarThread().zoonOut();

			}

		});

		mLocationListener = new CRLocationlistener();
		CityRunAPP.locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		radarView.setPoints(points);

	}

	@Override
	protected void onStart() {

		super.onStart();

	}

	@Override
	protected void onPause() {
		super.onPause();
		radarView.getDrawRadarThread().isFinished();
		CityRunAPP.locationManager.removeUpdates(mLocationListener);
		CRLocationlistener.removeUpdates(myCRLocationListeneri);
		this.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();

		CityRunAPP.locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
		CRLocationlistener.requestUpdates(myCRLocationListeneri);

	}

	@Override
	protected void onStop() {
		super.onStop();
		this.onDestroy();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
