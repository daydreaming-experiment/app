package com.brainydroid.daydreaming.db;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.ui.Config;
import com.brainydroid.daydreaming.ui.QuestionViewAdapter;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MultipleChoiceAnswer implements Answer {

	private static String TAG = "MultipleChoiceAnswer";

	@Expose @Inject HashMap<String,HashSet<String>> choices;
    @Inject transient Gson gson;

	@Override
	public String toJson() {

		//Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] toJson");
		}

		return gson.toJson(this, this.getClass());
	}

	@Override
	public void getAnswersFromLayout(LinearLayout questionLinearLayout) {

		//Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getAnswersFromLayout");
		}

		ArrayList<View> subQuestions = QuestionViewAdapter.getViewsByTag(questionLinearLayout, "subQuestion");

		for (View subQuestion : subQuestions) {
			TextView mainTextView = (TextView)subQuestion.findViewById(R.id.question_multiple_choice_mainText);
			String mainText = mainTextView.getText().toString();
			addSubQuestion(mainText);

			LinearLayout rootChoices = (LinearLayout)subQuestion.findViewById(
					R.id.question_multiple_choice_rootChoices);
			int childCount = rootChoices.getChildCount();

			// Get choices in a list
			for (int i = 0; i < childCount; i++) {
				CheckBox child = (CheckBox)rootChoices.getChildAt(i);
				if (child.isChecked()) {
					addChoice(mainText, child.getText().toString());
				}
			}

			// Get the "Other" field
			CheckBox otherCheck = (CheckBox)subQuestion.findViewById(
					R.id.question_multiple_choice_otherCheckBox);
			if (otherCheck.isChecked()) {
				EditText otherEditText = (EditText)subQuestion.findViewById(
						R.id.question_multiple_choice_otherEditText);
				String otherText = otherEditText.getText().toString();

				addChoice(mainText, "Other: " + otherText);
			}
		}
	}

	private void addSubQuestion(String questionString) {

		//Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addSubQuestion");
		}

		choices.put(questionString, new HashSet<String>());
	}

	private void addChoice(String subQuestionString, String choice) {

		//Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addChoice");
		}

		choices.get(subQuestionString).add(choice);
	}

}
