package team.mkhwl.CityRun.Update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class UpdateManager {

	private String curVersion;
	private String newVersion;
	private int curVersionCode;
	private int newVersionCode;
	private String updateInfo;
	private UpdateCallback callback;
	private Context ctx;
	private String TAG = "UpdateManager";
	private int progress;
	private Boolean hasNewVersion;
	private Boolean canceled;

	// 存放更新APK文件的路径
	public static final String UPDATE_DOWNURL = "http://www.mushapi.com/cityrunweb/download/cityrun.apk";
	// 存放更新APK文件相应的版本说明路径
	public static final String UPDATE_CHECKURL = "http://www.mushapi.com/cityrunweb/update/update.html";

	public static final String UPDATE_APKNAME = "cityrun.apk";
	// public static final String UPDATE_VERJSON = "ver.txt";
	public static final String UPDATE_SAVENAME = "cityrun.apk";
	private static final int UPDATE_CHECKCOMPLETED = 1;
	private static final int UPDATE_DOWNLOADING = 2;
	private static final int UPDATE_DOWNLOAD_ERROR = 3;
	private static final int UPDATE_DOWNLOAD_COMPLETED = 4;
	private static final int UPDATE_DOWNLOAD_CANCELED = 5;

	// 从服务器上下载apk存放文件夹
	private static String savefolder = "/sdcard/download/";
	private static final String saveFileName = savefolder + "cityrun.apk";

	public UpdateManager(Context context, UpdateCallback updateCallback) {
		ctx = context;
		callback = updateCallback;
		// savefolder = context.getFilesDir();
		canceled = false;
		getCurVersion();
	}

	public String getNewVersionName() {
		return newVersion;
	}

	public String getUpdateInfo() {
		return updateInfo;
	}

	private void getCurVersion() {
		try {
			PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), 0);
			curVersion = pInfo.versionName;
			curVersionCode = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			curVersion = "1.1.1000";
			curVersionCode = 111000;
		}

	}

	public void checkUpdate() {
		hasNewVersion = false;
		new Thread() {

			@Override
			public void run() {
				try {

					String verjson = NetHelper.httpStringGet(UPDATE_CHECKURL);
					JSONArray array = new JSONArray(verjson);

					if (array.length() > 0) {
						JSONObject obj = array.getJSONObject(0);
						try {
							newVersionCode = Integer.parseInt(obj
									.getString("verCode"));
							newVersion = obj.getString("verName");
							updateInfo = "";
							if (newVersionCode > curVersionCode) {
								hasNewVersion = true;
							}
						} catch (Exception e) {
							newVersionCode = -1;
							newVersion = "";
							updateInfo = "";

						}
					}
				} catch (Exception e) {

				}
				updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);
			};
		}.start();

	}

	public void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);

		intent.setDataAndType(
				Uri.fromFile(new File(savefolder, UPDATE_SAVENAME)),
				"application/vnd.android.package-archive");
		ctx.startActivity(intent);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public void downloadPackage() {

		new Thread() {
			@Override
			public void run() {
				try {
					URL url = new URL(UPDATE_DOWNURL);

					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();
					File ApkFile = new File(savefolder, UPDATE_SAVENAME);

					File file = new File(savefolder);
					if (!file.exists()) {
						file.mkdir();
					}
					String apkFile = saveFileName;
					File ApkFile1 = new File(apkFile);
					FileOutputStream fos = new FileOutputStream(ApkFile1);
					int count = 0;
					byte buf[] = new byte[512];
					do {
						int numread = is.read(buf);
						count += numread;
						progress = (int) (((float) count / length) * 100);

						updateHandler.sendMessage(updateHandler
								.obtainMessage(UPDATE_DOWNLOADING));
						if (numread <= 0) {

							updateHandler
									.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED);
							break;
						}
						fos.write(buf, 0, numread);
					} while (!canceled);
					if (canceled) {
						updateHandler
								.sendEmptyMessage(UPDATE_DOWNLOAD_CANCELED);
					}
					fos.close();
					is.close();
				} catch (MalformedURLException e) {
					e.printStackTrace();

					updateHandler.sendMessage(updateHandler.obtainMessage(
							UPDATE_DOWNLOAD_ERROR, e.getMessage()));
				} catch (IOException e) {
					e.printStackTrace();

					updateHandler.sendMessage(updateHandler.obtainMessage(
							UPDATE_DOWNLOAD_ERROR, e.getMessage()));
				}

			}
		}.start();
	}

	public void cancelDownload() {
		canceled = true;
	}

	Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case UPDATE_CHECKCOMPLETED:

				callback.checkUpdateCompleted(hasNewVersion, newVersion);
				break;
			case UPDATE_DOWNLOADING:

				callback.downloadProgressChanged(progress);
				break;
			case UPDATE_DOWNLOAD_ERROR:

				callback.downloadCompleted(false, msg.obj.toString());
				break;
			case UPDATE_DOWNLOAD_COMPLETED:

				callback.downloadCompleted(true, "");
				break;
			case UPDATE_DOWNLOAD_CANCELED:

				callback.downloadCanceled();
			default:
				break;
			}
		}
	};

	public interface UpdateCallback {
		public void checkUpdateCompleted(Boolean hasUpdate,
				CharSequence updateInfo);

		public void downloadProgressChanged(int progress);

		public void downloadCanceled();

		public void downloadCompleted(Boolean sucess, CharSequence errorMsg);
	}

}
