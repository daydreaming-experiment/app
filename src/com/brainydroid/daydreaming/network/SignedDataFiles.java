package com.brainydroid.daydreaming.network;

import java.io.File;

import android.util.Log;

public class SignedDataFiles {

	private static String TAG = "SignedDataFiles";

	private final File data;
	private final File signature;

	public SignedDataFiles(File d, File s) {

		// Debug
		Log.d(TAG, "[fn] SignedDataFiles");

		data = d;
		signature = s;
	}

	boolean deleteFiles() {

		// Debug
		Log.d(TAG, "[fn] deleteFiles");

		return data.delete() && signature.delete();
	}

	public File getDataFile() {

		// Verbose
		Log.v(TAG, "[fn] getDataFile");

		return data;
	}

	public File getSignatureFile() {

		// Verbose
		Log.v(TAG, "[fn] getSignatureFile");

		return signature;
	}
}
