package com.brainydroid.daydreaming.db;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;

public class JsonQuestions {

	private static String TAG = "JsonQuestions";

	private final int questionsVersion;
    @Inject ArrayList<Question> questions;

	public JsonQuestions() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] JsonQuestions");
		}

		questionsVersion = -1;
	}

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
