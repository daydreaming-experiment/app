package com.brainydroid.daydreaming.db;

import java.util.HashMap;

import com.google.gson.annotations.Expose;

public class SliderAnswer extends Answer {

	@Expose private final HashMap<String,Integer> sliders;

	public SliderAnswer() {
		sliders = new HashMap<String,Integer>();
	}

	public void addAnswer(String questionString, int answer) {
		sliders.put(questionString, answer);
	}
}
