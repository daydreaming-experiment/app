package com.brainydroid.daydreaming;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

public class DashboardActivity extends Activity {

	private SharedPreferences mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = getSharedPreferences(getString(R.pref.firstLaunchPrefs), MODE_PRIVATE);
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
		if (!mPrefs.getBoolean(getString(R.pref.firstLaunchCompleted), false)) {
			Intent intent;
			if (!mPrefs.getBoolean(getString(R.pref.firstLaunchStarted), false)) {
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