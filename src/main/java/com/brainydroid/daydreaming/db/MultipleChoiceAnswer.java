package com.brainydroid.daydreaming.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class MultipleChoiceAnswer implements Answer {

	private static String TAG = "MultipleChoiceAnswer";

	private transient Gson gson;
	@Expose private final HashMap<String,HashSet<String>> choices;

	public MultipleChoiceAnswer() {

		//Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] MultipleChoiceAnswer");
		}

		choices = new HashMap<String,HashSet<String>>();
		gson = new Gson();
	}

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

		ArrayList<View> subQuestions = Question.getViewsByTag(questionLinearLayout, "subquestion");
		Iterator<View> subQuestionsIt = subQuestions.iterator();

		while (subQuestionsIt.hasNext()) {
			View subQuestion = subQuestionsIt.next();
			TextView mainTextView = (TextView)subQuestion.findViewById(R.id.question_multiple_choice_mainText);
			String mainText = mainTextView.getText().toString();
			addSubquestion(mainText);

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
					R.id.question_multiple_choices_otherCheckBox);
			if (otherCheck.isChecked()) {
				EditText otherEditText = (EditText)subQuestion.findViewById(
						R.id.question_multiple_choices_otherEditText);
				String otherText = otherEditText.getText().toString();

				addChoice(mainText, "Other: " + otherText);
			}
		}
	}

	private void addSubquestion(String questionString) {

		//Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addSubquestion");
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
