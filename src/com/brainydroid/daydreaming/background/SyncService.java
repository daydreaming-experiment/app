package com.brainydroid.daydreaming.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.network.CryptoStorage;
import com.brainydroid.daydreaming.network.CryptoStorageCallback;
import com.google.gson.Gson;

public class SyncService extends Service {

	private static String BS_EXP_APP_ID = "app1";
	private static String BS_SERVER_NAME = "http://mehho.net:5000/";

	private StatusManager status;
	private PollsStorage pollsStorage;
	private CryptoStorage cryptoStorage;
	private Gson gson;
	private boolean updateQuestionsDone = false;
	private boolean uploadAnswersDone = false;

	@Override
	public void onCreate() {
		super.onCreate();

		Toast.makeText(this, "Starting SyncService", Toast.LENGTH_SHORT).show();
		initVarsAndUpdates();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Stopping SyncService", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Don't allow binding
		return null;
	}

	private void initVarsAndUpdates() {
		status = StatusManager.getInstance(this);
		pollsStorage = PollsStorage.getInstance(this);
		gson = new Gson();

		CryptoStorageCallback callback = new CryptoStorageCallback() {

			@Override
			public void onCryptoStorageReady(boolean hasKeyPairAndMaiId) {
				if (hasKeyPairAndMaiId && status.isDataEnabled()) {
					asyncUpdateQuestions();
					asyncUploadAnswers();
				}
			}

		};

		cryptoStorage = CryptoStorage.getInstance(this, BS_SERVER_NAME, callback);
	}

	private void asyncUpdateQuestions() {
		// TODO: Create an AsyncTask to update questions. Then, notify the main service that it can exit.
		// There might be a problem if the service is started from an Activity, and the
		// orientation of the display changes. That will stop and restart the worker process.
		// See http://developer.android.com/guide/components/processes-and-threads.html ,
		// right above the "Thread-safe methods" title.
		Toast.makeText(this, "Will update questions", Toast.LENGTH_SHORT).show();
		setUpdateQuestionsDone();
		stopSelfIfAllDone();
	}

	private void asyncUploadAnswers() {
		// TODO: Create an AsyncTask to upload answers. Then, notify the main service that it can exit.
		// There might be a problem if the service is started from an Activity, and the
		// orientation of the display changes. That will stop and restart the worker process.
		// See http://developer.android.com/guide/components/processes-and-threads.html ,
		// right above the "Thread-safe methods" title.

		//		AsyncTask<Void, Void, Void> uploaderTask = new AsyncTask<Void, Void, Void>() {
		//
		//			private ArrayList<Poll> uploadablePolls;
		//
		//			@Override
		//			protected void onPreExecute() {
		//				uploadablePolls = pollsStorage.getUploadablePolls();
		//			}
		//
		//			@Override
		//			protected Void doInBackground(Void... params) {
		//
		//				if (uploadablePolls == null) {
		//					return null;
		//				}
		//
		//				for (Poll poll : uploadablePolls) {
		//					String jsonPoll = gson.toJson(poll);
		//					// TODO: upload poll
		//					pollsStorage.removePoll(poll.getId());
		//				}
		//
		//				return null;
		//			}
		//
		//			@Override
		//			protected void onPostExecute(Void result) {
		//				setUploadAnswersDone();
		//				stopSelfIfAllDone();
		//			}
		//		};
		//
		//		uploaderTask.execute();

		Toast.makeText(this, "Will upload answers", Toast.LENGTH_SHORT).show();
		setUploadAnswersDone();
		stopSelfIfAllDone();
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