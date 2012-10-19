package com.brainydroid.daydreaming.ui;

import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.Poll;
import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.db.QuestionsStorage;

public class DashboardActivity extends ActionBarActivity {

	public static String EXTRA_COMES_FROM_FIRST_LAUNCH = "comesFromFirstLaunch";

	private StatusManager status;
	private PollsStorage pollsStorage;
	private QuestionsStorage questionsStorage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		status = StatusManager.getInstance(this);
		pollsStorage = PollsStorage.getInstance(this);
		questionsStorage = QuestionsStorage.getInstance(this);
		checkFirstRun();

		setContentView(R.layout.activity_dashboard);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.dashboard, menu);

		// Calling super after populating the menu is necessary here to ensure that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
			break;

		case R.id.menu_settings:
			Toast.makeText(this, "Tapped settings", Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void checkFirstRun() {
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
		return getIntent().getBooleanExtra(EXTRA_COMES_FROM_FIRST_LAUNCH, false);
	}

	public void onClick_quitExperiment(View view) {
		quitExperiment();
	}

	private void quitExperiment() {
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

	String convertStreamToString(InputStream is) {
		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}

	public void loadQuestions(View view) {

		InputStream questionsIS = null;

		try {
			questionsIS = getResources().openRawResource(R.raw.questions);
			questionsStorage.importQuestions(convertStreamToString(questionsIS));
			questionsIS.close();
			Toast.makeText(this, "Questions loaded", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public void runPoll(View view) {
		Poll poll = Poll.create(this, 3);
		poll.save();

		Intent intent = new Intent(this, QuestionActivity.class);
		intent.putExtra(QuestionActivity.EXTRA_POLL_ID, poll.getId());
		intent.putExtra(QuestionActivity.EXTRA_QUESTION_INDEX, 0);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
}