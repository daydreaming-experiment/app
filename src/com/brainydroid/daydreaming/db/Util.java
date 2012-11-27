package com.brainydroid.daydreaming.db;

import java.io.InputStream;
import java.util.ArrayList;

import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class Util {

	private static String TAG = "Util";

	public static String joinStrings(ArrayList<String> strings, String joinString) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] joinStrings");
		}

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
		if (Config.LOGV) {
			Log.v(TAG, "[fn] multiplyString");
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < times; i++) {
			sb.append(string);
			sb.append(joinString);
		}

		int sbLength = sb.length();
		sb.delete(sbLength - joinString.length(), sbLength);
		return sb.toString();
	}

	public static String convertStreamToString(InputStream is) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] convertStreamToString");
		}

		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}

	// hour from string HH:MM
	public static int getHour(String time) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getHour");
		}

		String[] pieces = time.split(":");
		return(Integer.parseInt(pieces[0]));
	}

	// minutes from string HH:MM
	public static int getMinute(String time) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getMinute");
		}

		String[] pieces = time.split(":");
		return(Integer.parseInt(pieces[1]));
	}
}
