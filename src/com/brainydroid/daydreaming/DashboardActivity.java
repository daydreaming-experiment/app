package com.brainydroid.daydreaming;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class DashboardActivity extends Activity {

	private SharedPreferences mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = getPreferences(MODE_PRIVATE);
		checkFirstRun();

		setContentView(R.layout.activity_dashboard);
	}

	@Override
	public void onStart() {
		super.onStart();

		checkFirstRun();
	}

	@Override
	public void onResume() {
		super.onResume();

		checkFirstRun();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_day_dreaming, menu);
		return true;
	}

	private void checkFirstRun() {
		if (!mPrefs.getBoolean(PreferenceKeys.FIRST_RUN_COMPLETED, false)) {
			// Debug
			Toast.makeText(DashboardActivity.this, "First run never completed", Toast.LENGTH_SHORT).show();

			Intent intent;
			if (!mPrefs.getBoolean(PreferenceKeys.FIRST_RUN_STARTED, false)) {
				// Debug
				Toast.makeText(DashboardActivity.this, "First run never even started", Toast.LENGTH_SHORT).show();

				intent = new Intent(this, FirstLaunchWelcomeActivity.class);
			} else {
				intent = new Intent(this, ReLaunchWelcomeActivity.class);
			}

			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			finish();
		}
	}
}
