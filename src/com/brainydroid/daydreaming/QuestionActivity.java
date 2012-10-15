package com.brainydroid.daydreaming;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class QuestionActivity extends ActionBarActivity {

	Question question;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_question);

		question = QuestionsStorage.getInstance(this).getQuestion("currentActivity_doing");
		populateViews();
		setTitle("Day Dreaming 1/2");
	}

	private void populateViews() {
		LinearLayout rootLinearLayout = (LinearLayout)findViewById(R.id.question_linearLayout);
		ArrayList<View> views = question.getViews(this);

		Iterator<View> vIt = views.iterator();
		int i = 0;
		while (vIt.hasNext()) {
			rootLinearLayout.addView(vIt.next(), i, rootLinearLayout.getLayoutParams());
			i++;
		}
	}
}
