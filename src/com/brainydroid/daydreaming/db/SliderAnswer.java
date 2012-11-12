package com.brainydroid.daydreaming.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class SliderAnswer implements Answer {

	private static String TAG = "SliderAnswer";

	private transient Gson gson;
	@Expose private final HashMap<String,Integer> sliders;

	public SliderAnswer() {

		//Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] SliderAnswer");
		}

		sliders = new HashMap<String,Integer>();
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
			SeekBar seekBar = (SeekBar)subQuestion.findViewById(
					R.id.question_slider_seekBar);
			TextView mainTextView = (TextView)subQuestion.findViewById(R.id.question_slider_mainText);
			String mainText = mainTextView.getText().toString();
			addAnswer(mainText, seekBar.getProgress());
		}
	}

	private void addAnswer(String questionString, int answer) {

		//Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addAnswer");
		}

		sliders.put(questionString, answer);
	}
}
