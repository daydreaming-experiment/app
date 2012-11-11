package com.brainydroid.daydreaming.db;

import java.util.HashMap;

import com.google.gson.annotations.Expose;

public class SliderAnswer extends Answer {

	@Expose private final HashMap<String,Integer> answers;

	public SliderAnswer() {
		answers = new HashMap<String,Integer>();
	}

	public void addAnswer(String questionString, int answer) {
		answers.put(questionString, answer);
	}
}
