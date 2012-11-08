package com.brainydroid.daydreaming.ui;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.LocationCallback;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.Poll;
import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.db.Question;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.network.SntpClientCallback;

public class QuestionActivity extends ActionBarActivity {

	public static String EXTRA_POLL_ID = "pollId";
	public static String EXTRA_QUESTION_INDEX = "questionIndex";

	private PollsStorage pollsStorage;
	private int pollId;
	private Poll poll;
	private int questionIndex;
	private Question question;
	private int nQuestions;
	private boolean isContinuing = false;
	private LinearLayout questionLinearLayout;
	private StatusManager status;

	public static class LocationAlertDialogFragment extends DialogFragment {

		public static LocationAlertDialogFragment newInstance(int title, int text, int posText) {
			LocationAlertDialogFragment frag = new LocationAlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			args.putInt("text", text);
			args.putInt("posText", posText);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			int title = getArguments().getInt("title");
			int text = getArguments().getInt("text");
			int posText = getArguments().getInt("posText");

			AlertDialog.Builder alertSettings = new AlertDialog.Builder(getActivity())
			.setTitle(title)
			.setMessage(text)
			.setPositiveButton(posText,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					((QuestionActivity)getActivity()).launchSettings();
				}
			}).setIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
					R.drawable.ic_action_about_holo_light : R.drawable.ic_action_about_holo_dark);

			return alertSettings.create();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_question);

		initVars();
		checkPollStatus();
		setChrome();
		populateViews();
		setTitle(getString(R.string.app_name) + " " + (questionIndex + 1) + "/" + nQuestions);
		// TODO: add a call to ScheduleManager / SchedulerService
	}

	@Override
	public void onStart() {
		super.onStart();
		if(checkPollStatus()) {
			poll.setStatus(Poll.STATUS_RUNNING);

			if (status.isDataAndLocationEnabled()) {
				startListeningTasks();
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (!isContinuing() && !poll.isOver()) {
			poll.setStatus(Poll.STATUS_STOPPED);
			status.stopLocationService();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (!isFirstQuestion()) {
			setIsContinuing();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}
	}

	private void initVars() {
		Intent intent = getIntent();
		pollsStorage = PollsStorage.getInstance(this);
		pollId = intent.getIntExtra(EXTRA_POLL_ID, -1);
		poll = pollsStorage.getPoll(pollId);
		questionIndex = intent.getIntExtra(EXTRA_QUESTION_INDEX, -1);
		question = poll.getQuestionByIndex(questionIndex);
		nQuestions = poll.getLength();
		questionLinearLayout = (LinearLayout)findViewById(R.id.question_linearLayout);
		status = StatusManager.getInstance(this);
	}

	private boolean checkPollStatus() {
		boolean isOver = poll.isOver();
		if (isOver) {
			finish();
		}
		return !isOver;
	}

	private void setChrome() {
		if (!isFirstQuestion()) {
			LinearLayout question_linearLayout = (LinearLayout)findViewById(R.id.question_linearLayout);
			TextView welcomeText = (TextView)question_linearLayout.findViewById(R.id.question_welcomeText);
			question_linearLayout.removeView(welcomeText);

			if (isLastQuestion()) {
				Button nextButton = (Button)findViewById(R.id.question_nextButton);
				nextButton.setText(getString(R.string.question_button_finish));
			}
		}
	}

	private void startListeningTasks() {
		SntpClientCallback sntpCallback = new SntpClientCallback() {

			@Override
			public void onTimeReceived(SntpClient sntpClient) {
				if (sntpClient != null) {
					question.setTimestamp(sntpClient.getNow());
				}
			}

		};

		SntpClient sntpClient = new SntpClient();
		sntpClient.asyncRequestTime(sntpCallback);

		status.startLocationService();

		LocationCallback locationCallback = new LocationCallback() {

			@Override
			public void onLocationReceived(Location location) {
				question.setLocation(location);
			}

		};

		status.setLocationCallback(locationCallback);
	}

	private void populateViews() {
		ArrayList<View> views = question.createViews(this);

		Iterator<View> vIt = views.iterator();
		int i = isFirstQuestion() ? 1 : 0;
		while (vIt.hasNext()) {
			questionLinearLayout.addView(vIt.next(), i, questionLinearLayout.getLayoutParams());
			i++;
		}
	}

	public void onClick_nextButton(View view) {
		if (question.validate(this, questionLinearLayout)) {
			poll.saveAnswers(questionLinearLayout, questionIndex);
			if (isLastQuestion()) {
				finishPoll();
			} else {
				if (status.isDataAndLocationEnabled()) {
					launchNextQuestion();
				} else {
					launchLocationAlertDialog();
				}
			}
		}
	}

	private void launchLocationAlertDialog() {
		int titleId;
		int textId;
		if (!status.isNetworkLocEnabled()) {
			if (!status.isDataEnabled()) {
				titleId = R.string.locationAlert_title_location_and_data;
				textId = R.string.locationAlert_text_location_and_data;
			} else {
				titleId = R.string.locationAlert_title_location;
				textId = R.string.locationAlert_text_location;
			}
		} else {
			titleId = R.string.locationAlert_title_data;
			textId = R.string.locationAlert_text_data;
		}

		DialogFragment locationAlert = LocationAlertDialogFragment.newInstance(
				titleId, textId, R.string.locationAlert_button_settings);
		locationAlert.show(getSupportFragmentManager(), "locationAlert");
	}

	private void launchNextQuestion() {
		setIsContinuing();
		Intent intent = new Intent(this, QuestionActivity.class);
		intent.putExtra(EXTRA_POLL_ID, pollId);
		intent.putExtra(EXTRA_QUESTION_INDEX, questionIndex + 1);
		intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private void launchSettings() {
		Intent settingsIntent;
		if (!status.isNetworkLocEnabled()) {
			settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		} else {
			settingsIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.Settings");
				settingsIntent.setComponent(cName);
			}
			settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		}
		startActivity(settingsIntent);
	}

	private void finishPoll() {
		Toast.makeText(this, getString(R.string.question_thank_you), Toast.LENGTH_SHORT).show();
		poll.setStatus(Poll.STATUS_COMPLETED);
		status.stopLocationService();
		finish();
	}

	private boolean isLastQuestion() {
		return questionIndex == nQuestions - 1;
	}

	private boolean isFirstQuestion() {
		return questionIndex == 0;
	}

	private boolean isContinuing() {
		return isContinuing;
	}

	private void setIsContinuing() {
		isContinuing = true;
	}
}
