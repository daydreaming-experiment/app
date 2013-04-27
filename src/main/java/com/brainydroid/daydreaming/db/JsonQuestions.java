package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;

import java.util.ArrayList;

public class JsonQuestions {

	private static String TAG = "JsonQuestions";

	public int questionsVersion = -1;
    @Inject ArrayList<Question> questions;

	private void completeQuestions() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] completeQuestions");
		}

		for (Question q : questions) {
			q.setQuestionsVersion(questionsVersion);
		}
	}

	public ArrayList<Question> getQuestionsArrayList() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getQuestionsArrayList");
		}

		completeQuestions();
		return questions;
	}
}
