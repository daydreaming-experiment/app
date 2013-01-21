package com.brainydroid.daydreaming.db;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class JsonQuestions {

	private static String TAG = "JsonQuestions";

	private final int questionsVersion;
	private final ArrayList<Question> questions;

	public JsonQuestions() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] JsonQuestions");
		}

		questionsVersion = -1;
		questions = new ArrayList<Question>();
	}

	private void completeQuestions() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] completeQuestions");
		}

		Iterator<Question> qIt = questions.iterator();

		while (qIt.hasNext()) {
			Question q = qIt.next();
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
