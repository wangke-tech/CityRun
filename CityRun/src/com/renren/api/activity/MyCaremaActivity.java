package com.renren.api.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import team.mkhwl.CityRun.CityRunAPP;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.renren.api.function.PhotoDemo;


public class MyCaremaActivity extends Activity {

	private static final String TAG="MyCaremaActivity";

	public String fileName = ""; 
	Intent intent;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File file = new File("/sdcard/myImage/");
		file.mkdirs();// 创建文件夹
		String name = new SimpleDateFormat("yyyyMMddHHmmss")
        .format(new Date()) + ".jpg";// 照片命名
		fileName = "/sdcard/myImage/"+name;
		Log.v(TAG,"1"+fileName);
		Intent it = new Intent("android.media.action.IMAGE_CAPTURE");		
		Log.v(TAG,"2"+fileName);
		it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName)));
		Log.v(TAG,"3"+fileName);
		startActivityForResult(it, Activity.DEFAULT_KEYS_DIALER);
		Log.v(TAG,"4"+fileName);
	//	PhotoDemo.uploadPhotoWithActivity(MyCaremaActivity.this, renren, fileName);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	     super.onActivityResult(requestCode, resultCode, data); 
	     Log.v(TAG,"5"+CityRunAPP.renren.toString());
	     Log.v(TAG,"5"+fileName);

	     PhotoDemo.uploadPhotoWithActivity(MyCaremaActivity.this, CityRunAPP.renren,fileName);
	     Log.v(TAG,"6"+fileName);
	     this.finish();
	} 
	
	@Override
	protected void onPause() {
		Log.v(TAG, "onPause");
		super.onPause();
		this.onDestroy();
	}

	@Override
	protected void onResume() {
		Log.v(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onStop() {
		Log.v(TAG, "onStop");
		super.onStop();
	}

}