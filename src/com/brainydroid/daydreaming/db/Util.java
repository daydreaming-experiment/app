package com.brainydroid.daydreaming.db;

import java.util.ArrayList;

import android.util.Log;

public class Util {

	private static String TAG = "Util";

	public static String joinStrings(ArrayList<String> strings, String joinString) {

		// Verbose
		Log.v(TAG, "[fn] joinStrings");

		StringBuilder sb = new StringBuilder();

		for (String s : strings) {
			sb.append(s);
			sb.append(joinString);
		}

		int sbLength = sb.length();
		sb.delete(sbLength - joinString.length(), sbLength);
		return sb.toString();
	}

	public static String multiplyString(String string, int times, String joinString) {

		// Verbose
		Log.v(TAG, "[fn] multiplyString");

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < times; i++) {
			sb.append(string);
			sb.append(joinString);
		}

		int sbLength = sb.length();
		sb.delete(sbLength - joinString.length(), sbLength);
		return sb.toString();
	}
}
