package team.mkhwl.CityRun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import team.mkhwl.CityRun.R;
import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.exception.RenrenAuthError;
import com.renren.api.connect.android.view.RenrenAuthListener;

public class LoginActivity extends Activity {// 使用OnClickListener接口监听按钮事件
	private Handler handler;
	// Renren renren;
	Button renrenlog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_activity);

		CityRunAPP.renren = new Renren(C.renren.API_KEY, C.renren.SECRET_KEY,
				C.renren.APP_ID, this);

		handler = new Handler();

		final RenrenAuthListener listener = new RenrenAuthListener() {
			// 登录成功
			public void onComplete(Bundle values) {

				handler.post(new Runnable() {
					public void run() {
						Intent intent = null;
						intent = new Intent(LoginActivity.this,
								MapActivityMain.class);
						//intent.setPackage("com.baidu.BaiduMap");
						LoginActivity.this.startActivity(intent);
/*						overridePendingTransition(R.anim.activity_left2right,
								R.anim.activity_right2left);*/
						LoginActivity.this.finish();
						Toast.makeText(
								LoginActivity.this,
								LoginActivity.this
										.getString(R.string.auth_success),
								Toast.LENGTH_SHORT).show();

					}
				});
			}

			// 登录失败
			public void onRenrenAuthError(RenrenAuthError renrenAuthError) {
				handler.post(new Runnable() {
					public void run() {
						Toast.makeText(
								LoginActivity.this,
								LoginActivity.this
										.getString(R.string.auth_failed),
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			public void onCancelLogin() {
			}

			public void onCancelAuth(Bundle values) {
			}
		};

		// 登录按钮的事件
		renrenlog = (Button) findViewById(R.id.renrenButton);
		renrenlog.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CityRunAPP.renren.authorize(LoginActivity.this, listener);
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onStop();
	}
}