package team.mkhwl.CityRun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends Activity {

	public Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_activity);
		context = WelcomeActivity.this;
		Handler x = new Handler();
		x.postDelayed(new splashhandler(), 2000);

	}

	class splashhandler implements Runnable {

		public void run() {
			boolean ifShwoHelp = true;
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					"SharedPreferences", MODE_PRIVATE);
			// 存入数据
			Editor editor = sharedPreferences.edit();
			editor.commit();
			ifShwoHelp = sharedPreferences.getBoolean("IS_FIRST_INSTALL",
					ifShwoHelp);
			if (false == ifShwoHelp) {

				startActivity(new Intent(getApplication(), LoginActivity.class));
				overridePendingTransition(R.anim.acivity_out_alpha_long,
						R.anim.activity_in_alpha);
			} else {

				editor.putBoolean("IS_FIRST_INSTALL", false);
				editor.commit();
				startActivity(new Intent(getApplication(),
						HelpSlideActivity.class));
				overridePendingTransition(R.anim.acivity_out_alpha_long,
						R.anim.activity_in_alpha);
			}

			WelcomeActivity.this.finish();
		}

	}
}