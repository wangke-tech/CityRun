package team.mkhwl.CityRun;

import java.util.ArrayList;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.baidu.mapapi.CoordinateConvert;
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

public class CRLocationlistener implements LocationListener {
	
	private static final String TAG = "CRLocationlistener";
	private static ArrayList<CRLocationListeneri> LocationListeneriList = new ArrayList<CRLocationListeneri>(
			2);


	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		if ("" == CityRunAPP.userCity | null == CityRunAPP.userCity) {
			MKSearch mMKSearch = null;
			GamePoint gpWgs84 = new GamePoint(
					(int) (location.getLatitude() * 1e6),
					(int) (location.getLongitude() * 1e6));
			GeoPoint baidu09 = CoordinateConvert.bundleDecode(CoordinateConvert
					.fromWgs84ToBaidu(gpWgs84));

			mMKSearch = new MKSearch();
			mMKSearch.init(CityRunAPP.mBMapMan, new MKSearchListener() {
				public void onGetAddrResult(MKAddrInfo result, int iError) {
					if (result == null) {
						return;
					} else {
						// 这里获取当前城市
						String str = result.strAddr;
						String strT = "";
						int index = str.indexOf("市");
						for (int i = index - 1; i >= 0; i--) {
							String oneStr = str.charAt(i) + "";
							if (oneStr.equals("省") | oneStr.equals("区")) {
								break;
							}
							strT += str.charAt(i);
						}
						for (int j = strT.length() - 1; j >= 0; j--) {
							CityRunAPP.userCity += strT.charAt(j);
						}
					}
				}

				// 其他未实现的方法
				public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				}

				public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
						int arg1) {
				}

				public void onGetPoiResult(MKPoiResult result, int arg1,
						int arg2) {
				}

				public void onGetTransitRouteResult(MKTransitRouteResult arg0,
						int arg1) {
				}

				public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
						int arg1) {
				}

				public void onGetSuggestionResult(MKSuggestionResult arg0,
						int arg1) {
				}
			});

			mMKSearch.reverseGeocode(baidu09);
		}
		int i = 0;
		for (i = 0; i < LocationListeneriList.size(); i++) {

			LocationListeneriList.get(i).onLocationChanged(location);
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	
	public synchronized static void requestUpdates(
			CRLocationListeneri LocationListeneri) {
		LocationListeneriList.add(LocationListeneri);
	}

	public synchronized static void removeUpdates(
			CRLocationListeneri LocationListeneri) {
		LocationListeneriList.remove(LocationListeneri);
	}


	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
