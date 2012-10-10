package com.brainydroid.daydreaming;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ReLaunchWelcomeActivity extends ActionBarActivity {

	private StatusManager status;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_re_launch_welcome);

		status = StatusManager.getInstance(this);
		checkFirstRun();
	}

	@Override
	public void onStart() {
		super.onStart();
		checkFirstRun();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void onClick_start(View view) {
		Intent intent = new Intent(this, FirstLaunchDescriptionActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private void checkFirstRun() {
		if (status.isFirstLaunchCompleted()) {
			finish();
		}
	}
}