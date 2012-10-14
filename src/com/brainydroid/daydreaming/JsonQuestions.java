package com.brainydroid.daydreaming;

import java.util.ArrayList;
import java.util.Iterator;

public class JsonQuestions {

	private final int questionsVersion;
	private final ArrayList<Question> questions;

	public JsonQuestions() {
		questionsVersion = -1;
		questions = new ArrayList<Question>();
	}

	private void completeQuestions() {
		Iterator<Question> qIt = questions.iterator();

		while (qIt.hasNext()) {
			Question q = qIt.next();
			q.setQuestionsVersion(questionsVersion);
		}
	}

	public ArrayList<Question> getQuestionsArrayList() {
		completeQuestions();
		return questions;
	}
}
