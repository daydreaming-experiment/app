package com.brainydroid.daydreaming;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class FirstLaunchDescriptionActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_launch_description);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_first_launch_description, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	public void onClick_buttonNext(View view) {
		Intent intent = new Intent(this, FirstLaunchMeasuresActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
}