/**
 * $id$
 */
package com.renren.api.function;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.Util;
import com.renren.api.connect.android.common.AbstractRequestListener;
import com.renren.api.connect.android.exception.RenrenError;
import com.renren.api.connect.android.exception.RenrenException;
import com.renren.api.connect.android.feed.FeedPublishRequestParam;
import com.renren.api.connect.android.feed.FeedPublishResponseBean;
import com.renren.api.connect.android.status.StatusSetResponseBean;

/**
 * 演示如何使用系统提供的widget
 * 
 * @author Shaofeng Wang (shaofeng.wang@renren-inc.com)
 */
public class WidgetDialogDemo {
	
	/**
	 * 使用平台提供的widget dialog发布新鲜事
	 * 
	 * @param activity
	 * @param renren
	 */
	public static void showFeedDialog(final Activity activity, final Renren renren) {
		FeedPublishRequestParam feed = 
				new FeedPublishRequestParam(
						"城市雷达", 
						"城市雷达是一个基于百度地图API和人人开放API的Android应用,旨在以引导用户去发现身边常常被忽略的美丽.",
						"http://www.mushapi.com/cityradar/index.html", //新鲜事链接 
						"http://www.mushapi.com/cityradar/renrenfeed/feed.png", //新鲜事图片url 
						"————发现你未曾发现的美丽", 
						"下载城市雷达", 
						"http://www.mushapi.com/cityradar/download/CityRadar.apk", //动作链接, 
						"");
		final Handler handler = new Handler(activity.getMainLooper()); 
		AbstractRequestListener<FeedPublishResponseBean> listener = 
				new AbstractRequestListener<FeedPublishResponseBean>() {			
			@Override
			public void onRenrenError(RenrenError renrenError) {
				final int errorCode = renrenError.getErrorCode();
				handler.post(new Runnable() {
					public void run() {
						if(errorCode == RenrenError.ERROR_CODE_OPERATION_CANCELLED) {
							Toast.makeText(activity, "发布新鲜事取消", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(activity, "发布新鲜事失败", Toast.LENGTH_SHORT).show();							
						}
					}
				});
			}
			
			@Override
			public void onFault(Throwable fault) {
				handler.post(new Runnable() {
					public void run() {
						Toast.makeText(activity, "发布新鲜事失败", Toast.LENGTH_SHORT).show();	
					}
				});
			}
			
			@Override
			public void onComplete(FeedPublishResponseBean bean) {
				handler.post(new Runnable() {
					public void run() {
						Toast.makeText(activity, "发布新鲜事成功", Toast.LENGTH_SHORT).show();	
					}
				});
			}
		};
		
		try {
			renren.publishFeed(activity, feed, listener);
		} catch (RenrenException e) {
			Util.logger(e.getMessage());
			handler.post(new Runnable() {
				public void run() {
					Toast.makeText(activity, "发布新鲜事失败", Toast.LENGTH_SHORT).show();	
				}
			});
		}
	}

	public static void showStatusDialog(final Activity activity, final Renren renren) {
		final Handler handler = new Handler(activity.getMainLooper()); 
		AbstractRequestListener<StatusSetResponseBean> listener = 
				new AbstractRequestListener<StatusSetResponseBean>() {			
			@Override
			public void onRenrenError(RenrenError renrenError) {
				final int errorCode = renrenError.getErrorCode();
				handler.post(new Runnable() {
					public void run() {
						if(errorCode == RenrenError.ERROR_CODE_OPERATION_CANCELLED) {
							Toast.makeText(activity, "发布操作取消", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(activity, "发布状态失败", Toast.LENGTH_SHORT).show();							
						}
					}
				});
			}
			
			@Override
			public void onFault(Throwable fault) {
				handler.post(new Runnable() {
					public void run() {
						Toast.makeText(activity, "发布状态失败", Toast.LENGTH_SHORT).show();	
					}
				});
			}
			
			@Override
			public void onComplete(StatusSetResponseBean bean) {
				handler.post(new Runnable() {
					public void run() {
						Toast.makeText(activity, "发布状态成功", Toast.LENGTH_SHORT).show();	
					}
				});
			}
		};
		
		try {
			renren.publishStatus(activity, listener);
		} catch (RenrenException e) {
			handler.post(new Runnable() {
				public void run() {
					Toast.makeText(activity, "发布状态失败", Toast.LENGTH_SHORT).show();	
				}
			});
		}
	}
}
