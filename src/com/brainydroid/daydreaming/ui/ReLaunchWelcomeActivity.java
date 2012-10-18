package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.StatusManager;

public class ReLaunchWelcomeActivity extends ActionBarActivity {

	private StatusManager status;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_re_launch_welcome);

		status = StatusManager.getInstance(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		checkFirstLaunch();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void onClick_start(View view) {
		Intent intent = new Intent(this, FirstLaunchDescriptionActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private void checkFirstLaunch() {
		if (status.isFirstLaunchCompleted() || status.isClearing()) {
			if (status.isClearing()) {
				status.finishClear();
			}
			finish();
		}
	}
}