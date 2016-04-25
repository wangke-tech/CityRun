
package team.mkhwl.CityRun.Update;

import team.mkhwl.CityRun.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class Update {
	private UpdateManager updateMan;
	private ProgressDialog updateProgressDialog;
	private Context context;

	/** Called when the activity is first created. */

	public Update(Context context) {
		// 检查是否有更新
		// 如果有更新提示下载
		this.context  = context;
		updateMan = new UpdateManager(context, appUpdateCb);
		updateMan.checkUpdate();
	}

	// 自动更新回调函数
	UpdateManager.UpdateCallback appUpdateCb = new UpdateManager.UpdateCallback() {

		public void downloadProgressChanged(int progress) {
			if (updateProgressDialog != null
					&& updateProgressDialog.isShowing()) {
				updateProgressDialog.setProgress(progress);
			}

		}

		public void downloadCompleted(Boolean sucess, CharSequence errorMsg) {
			if (updateProgressDialog != null
					&& updateProgressDialog.isShowing()) {
				updateProgressDialog.dismiss();
			}
			if (sucess) {
				updateMan.update();
			} else {
				DialogHelper.Confirm(context,
						R.string.dialog_error_title,
						R.string.dialog_downfailed_msg,
						R.string.dialog_downfailed_btnnext,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								updateMan.downloadPackage();

							}
						}, R.string.dialog_downfailed_btnnext, null);
			}
		}

		public void downloadCanceled() {
			// TODO Auto-generated method stub

		}

		public void checkUpdateCompleted(Boolean hasUpdate,
				CharSequence updateInfo) {
			if (hasUpdate) {
				DialogHelper.Confirm(
						context,
						context.getText(R.string.dialog_update_title),
						context.getText(R.string.dialog_update_msg).toString()
								+ updateInfo
								+ context.getText(R.string.dialog_update_msg2)
										.toString(),
										context.getText(R.string.dialog_update_btnupdate),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								updateProgressDialog = new ProgressDialog(
										context);
								updateProgressDialog
										.setMessage(context.getText(R.string.dialog_downloading_msg));
								updateProgressDialog.setIndeterminate(false);
								updateProgressDialog
										.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								updateProgressDialog.setMax(100);
								updateProgressDialog.setProgress(0);
								updateProgressDialog.show();

								updateMan.downloadPackage();
							}
						}, context.getText(R.string.dialog_update_btnnext), null);
			}

		}
	};
}