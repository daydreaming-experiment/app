package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.background.SyncService;
import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.db.QuestionsStorage;

public class DashboardActivity extends ActionBarActivity {

	private static String TAG = "DashboadActivity";

	public static String EXTRA_COMES_FROM_FIRST_LAUNCH = "comesFromFirstLaunch";

	private StatusManager status;
	private PollsStorage pollsStorage;
	private QuestionsStorage questionsStorage;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Debug
		Log.d(TAG, "[fn] onCreate");

		super.onCreate(savedInstanceState);

		status = StatusManager.getInstance(this);
		pollsStorage = PollsStorage.getInstance(this);
		questionsStorage = QuestionsStorage.getInstance(this);
		checkFirstRun();

		setContentView(R.layout.activity_dashboard);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Debug
		Log.d(TAG, "[fn] onCreateOptionsMenu");

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.dashboard, menu);

		// Calling super after populating the menu is necessary here to ensure that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onStart() {

		// Debug
		Log.d(TAG, "[fn] onStart");

		super.onStart();
	}

	@Override
	public void onResume() {

		// Debug
		Log.d(TAG, "[fn] onResume");

		super.onResume();
	}

	@Override
	public void onStop() {

		// Debug
		Log.d(TAG, "[fn] onStop");

		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Debug
		Log.d(TAG, "[fn] onOptionsItemSelected");

		switch (item.getItemId()) {
		case android.R.id.home:
			Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
			break;

		case R.id.menu_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			Toast.makeText(this, "Tapped settings", Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void checkFirstRun() {

		// Debug
		Log.d(TAG, "[fn] checkFirstRun");

		if (!status.isFirstLaunchCompleted()) {
			Intent intent;
			if (!status.isFirstLaunchStarted()) {
				intent = new Intent(this, FirstLaunchWelcomeActivity.class);
			} else {
				intent = new Intent(this, ReLaunchWelcomeActivity.class);
			}

			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(intent);
			finish();
		}
	}

	private boolean comesFromFirstLaunch() {

		// Debug
		Log.d(TAG, "[fn] comesFromFirstLaunch");

		return getIntent().getBooleanExtra(EXTRA_COMES_FROM_FIRST_LAUNCH, false);
	}

	public void onClick_quitExperiment(View view) {

		// Debug
		Log.d(TAG, "[fn] onClick_quitExperiment");

		quitExperiment();
	}

	private void quitExperiment() {

		// Debug
		Log.d(TAG, "[fn] quitExperiment");

		Toast.makeText(this, "This will clear everything. It should ask for confirmation",
				Toast.LENGTH_SHORT).show();
		status.startClear();
		// Delete saved data
		pollsStorage.dropAll();
		questionsStorage.dropAll();
		if (!comesFromFirstLaunch()) {
			status.finishClear();
		}
		finish();
	}

	public void runPoll(View view) {

		// Debug
		Log.d(TAG, "[fn] runPoll");

		Intent schedulerIntent = new Intent(this, SchedulerService.class);
		startService(schedulerIntent);
	}

	public void startSyncService(View view) {

		// Debug
		Log.d(TAG, "[fn] startSyncService");

		Intent syncIntent = new Intent(this, SyncService.class);
		startService(syncIntent);
	}
}
