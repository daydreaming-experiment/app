package com.brainydroid.daydreaming.db;

import java.util.HashMap;
import java.util.HashSet;

import com.google.gson.annotations.Expose;

public class MultipleChoiceAnswer extends Answer {

	@Expose private final HashMap<String,HashSet<String>> choices;

	public MultipleChoiceAnswer() {
		choices = new HashMap<String,HashSet<String>>();
	}

	public void addSubquestion(String questionString) {
		choices.put(questionString, new HashSet<String>());
	}

	public void addChoice(String subQuestionString, String choice) {
		choices.get(subQuestionString).add(choice);
	}
}
