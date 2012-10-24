package com.brainydroid.daydreaming.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {

	private StatusManager status;
	private boolean updateQuestionsDone = false;
	private boolean uploadAnswersDone = false;

	@Override
	public void onCreate() {
		super.onCreate();

		initVars();
		if (status.isDataEnabled()) {
			asyncUpdateQuestions();
			asyncUploadAnswers();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Don't allow binding
		return null;
	}

	private void initVars() {
		status = StatusManager.getInstance(this);
	}

	private void asyncUpdateQuestions() {
		// TODO: Create an AsyncTask to update questions. Then, notify the main service that it can exit.
		// There might be a problem if the service is started from an Activity, and the
		// orientation of the display changes. That will stop and restart the worker process.
		// See http://developer.android.com/guide/components/processes-and-threads.html ,
		// right above the "Thread-safe methods" title.
	}

	private void asyncUploadAnswers() {
		// TODO: Create an AsyncTask to upload answers. Then, notify the main service that it can exit.
		// There might be a problem if the service is started from an Activity, and the
		// orientation of the display changes. That will stop and restart the worker process.
		// See http://developer.android.com/guide/components/processes-and-threads.html ,
		// right above the "Thread-safe methods" title.
	}

	private void setUpdateQuestionsDone() {
		updateQuestionsDone = true;
	}

	private void setUploadAnswersDone() {
		uploadAnswersDone = true;
	}

	private void stopSelfIfAllDone() {
		if (updateQuestionsDone && uploadAnswersDone) {
			stopSelf();
		}
	}
}