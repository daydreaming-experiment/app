package com.brainydroid.daydreaming.network;

import java.io.File;

public class SignedDataFiles {

	private final File data;
	private final File signature;

	public SignedDataFiles(File d, File s) {
		data = d;
		signature = s;
	}

	boolean deleteFiles() {
		return data.delete() && signature.delete();
	}

	public File getDataFile() {
		return data;
	}

	public File getSignatureFile() {
		return signature;
	}
}
