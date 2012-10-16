package com.brainydroid.daydreaming;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionActivity extends ActionBarActivity {

	public static String EXTRA_POLL_ID = "pollId";
	public static String EXTRA_QUESTION_INDEX = "questionIndex";

	private PollsStorage pollsStorage;
	private int pollId;
	private Poll poll;
	private int questionIndex;
	private Question question;
	private int nQuestions;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_question);

		initVars();
		// TODO: If poll completed, finish
		setChrome();
		populateViews();
		setTitle(getString(R.string.app_name) + " " + (questionIndex + 1) + "/" + nQuestions);
	}

	private void initVars() {
		Intent intent = getIntent();
		pollsStorage = PollsStorage.getInstance(this);
		pollId = intent.getIntExtra(EXTRA_POLL_ID, -1);
		poll = pollsStorage.getPoll(pollId);
		questionIndex = intent.getIntExtra(EXTRA_QUESTION_INDEX, -1);
		question = poll.getQuestionByIndex(questionIndex);
		nQuestions = poll.getLength();
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

	private void populateViews() {
		LinearLayout rootLinearLayout = (LinearLayout)findViewById(R.id.question_linearLayout);
		ArrayList<View> views = question.getViews(this);

		Iterator<View> vIt = views.iterator();
		int i = isFirstQuestion() ? 1 : 0;
		while (vIt.hasNext()) {
			rootLinearLayout.addView(vIt.next(), i, rootLinearLayout.getLayoutParams());
			i++;
		}
	}

	public void onClick_nextButton(View view) {
		if (question.validate()) {
			poll.saveAnswer(questionIndex);
			if (isLastQuestion()) {
				Toast.makeText(this, getString(R.string.question_thank_you), Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Intent intent = new Intent(this, QuestionActivity.class);
				intent.putExtra(EXTRA_POLL_ID, pollId);
				intent.putExtra(EXTRA_QUESTION_INDEX, questionIndex + 1);
				startActivity(intent);
			}
		}

	}

	private boolean isLastQuestion() {
		return questionIndex == nQuestions - 1;
	}

	private boolean isFirstQuestion() {
		return questionIndex == 0;
	}
}
