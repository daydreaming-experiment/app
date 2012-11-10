package com.brainydroid.daydreaming.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.db.QuestionsStorage;
import com.brainydroid.daydreaming.network.CryptoStorage;
import com.brainydroid.daydreaming.network.CryptoStorageCallback;
import com.brainydroid.daydreaming.network.HttpConversationCallback;
import com.brainydroid.daydreaming.network.HttpGetData;
import com.brainydroid.daydreaming.network.HttpGetTask;
import com.google.gson.Gson;

public class SyncService extends Service {

	private static String TAG = "SyncService";

	private static String BS_EXP_APP_ID = "app1";
	private static String BS_SERVER_NAME = "http://mehho.net:5000/";

	private static String QUESTIONS_VERSION_URL = "http://mehho.net:5001/questionsVersion";
	private static String QUESTIONS_URL = "http://mehho.net:5001/questions.json";

	private StatusManager status;
	private PollsStorage pollsStorage;
	private QuestionsStorage questionsStorage;
	private CryptoStorage cryptoStorage;
	private Gson gson;
	private boolean updateQuestionsDone = false;
	private boolean uploadAnswersDone = false;

	@Override
	public void onCreate() {

		// Debug
		Log.d(TAG, "[fn] onCreate");

		super.onCreate();

		initVarsAndUpdates();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Debug
		Log.d(TAG, "[fn] onStartCommand");

		super.onStartCommand(intent, flags, startId);

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {

		// Debug
		Log.d(TAG, "[fn] onDestroy");

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {

		// Debug
		Log.d(TAG, "[fn] onBind");

		// Don't allow binding
		return null;
	}

	private void initVarsAndUpdates() {

		// Debug
		Log.d(TAG, "[fn] initVarsAndUpdates");

		status = StatusManager.getInstance(this);
		pollsStorage = PollsStorage.getInstance(this);
		questionsStorage = QuestionsStorage.getInstance(this);
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

		// Debug
		Log.d(TAG, "[fn] asyncUpdateQuestions");

		// FIXME: There might be a problem if the service is started from an Activity, and the
		// orientation of the display changes. That will stop and restart the worker process.
		// See http://developer.android.com/guide/components/processes-and-threads.html ,
		// right above the "Thread-safe methods" title.


		final HttpConversationCallback updateQuestionsCallback = new HttpConversationCallback() {

			private final String TAG = "HttpConversationCallback";

			@Override
			public void onHttpConversationFinished(boolean success, String serverAnswer) {

				// Debug
				Log.d(TAG, "[fn] (updateQuestionsCallback) onHttpConversationFinished");

				if (success) {

					// Info
					Log.i(TAG, "successfully retrieved new questions.json from server");

					Toast.makeText(SyncService.this,
							"SyncService: new questions downloaded from server",
							Toast.LENGTH_SHORT).show();
					questionsStorage.importQuestions(serverAnswer);
				} else {
					// Warning
					Log.w(TAG, "error while retrieving new questions.json from server");
				}

				setUpdateQuestionsDone();
				stopSelfIfAllDone();
			}

		};

		HttpConversationCallback fullCallback = new HttpConversationCallback() {

			private final String TAG = "HttpConversationCallback";

			@Override
			public void onHttpConversationFinished(boolean success, String serverAnswer) {

				// Debug
				Log.d(TAG, "[fn] (fullCallback) onHttpConversationFinished");

				boolean willGetQuestions = false;

				if (success) {

					// Info
					Log.i(TAG, "successfully retrieved questionsVersion from server");

					try {
						int serverQuestionsVersion = Integer.parseInt(serverAnswer.split("\n")[0]);

						if (serverQuestionsVersion != questionsStorage.getQuestionsVersion()) {
							willGetQuestions = true;

							HttpGetData updateQuestionsData = new HttpGetData(QUESTIONS_URL,
									updateQuestionsCallback);
							HttpGetTask updateQuestionsTask = new HttpGetTask();
							updateQuestionsTask.execute(updateQuestionsData);
						}
					} catch (Exception e) {
						// Warning
						Log.w(TAG, "error while parsing questionsVersion answer from server");
					}
				} else {
					// Warning
					Log.w(TAG, "error while retrieving questionsVersion from server");
				}

				if (!willGetQuestions) {
					setUpdateQuestionsDone();
					stopSelfIfAllDone();
				}
			}

		};

		HttpGetData getQuestionsVersionData = new HttpGetData(QUESTIONS_VERSION_URL,
				fullCallback);
		HttpGetTask getQuestionsVersionTask = new HttpGetTask();
		getQuestionsVersionTask.execute(getQuestionsVersionData);
	}

	private void asyncUploadAnswers() {

		// Debug
		Log.d(TAG, "[fn] asyncUploadAnswers");

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

		setUploadAnswersDone();
		stopSelfIfAllDone();
	}

	private void setUpdateQuestionsDone() {

		// Debug
		Log.d(TAG, "[fn] setUpdateQuestionsDone");

		updateQuestionsDone = true;
	}

	private void setUploadAnswersDone() {

		// Debug
		Log.d(TAG, "[fn] setUploadAnswersDone");

		uploadAnswersDone = true;
	}

	private void stopSelfIfAllDone() {

		// Debug
		Log.d(TAG, "[fn] stopSelfIfAllDone");

		if (updateQuestionsDone && uploadAnswersDone) {
			stopSelf();
		}
	}
}
