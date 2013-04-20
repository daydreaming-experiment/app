package com.brainydroid.daydreaming.background;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.brainydroid.daydreaming.db.*;
import com.brainydroid.daydreaming.network.CryptoStorage;
import com.brainydroid.daydreaming.network.CryptoStorageCallback;
import com.brainydroid.daydreaming.network.HttpConversationCallback;
import com.brainydroid.daydreaming.network.HttpGetData;
import com.brainydroid.daydreaming.network.HttpGetTask;
import com.brainydroid.daydreaming.network.ServerTalker;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SyncService extends Service {

	private static String TAG = "SyncService";

	private static String EXP_ID = "6cb5e7782ca43681d6349a2280a8f99f74479d142971ac6c91dbd155ac58b4b3";
	private static String SERVER_NAME = "http://naja.cc";

	private static String QUESTIONS_VERSION_URL = "http://mehho.net:5001/questionsVersion";
	private static String QUESTIONS_URL = "http://mehho.net:5001/questions.json";

	private StatusManager status;
	private PollsStorage pollsStorage;
    private LocationsStorage locationsStorage;
	private QuestionsStorage questionsStorage;
	private CryptoStorage cryptoStorage;
	private ServerTalker serverTalker;
	private Gson gson;
	private ArrayList<Poll> uploadablePolls;
    private ArrayList<LocationItem> uploadableLocationItems;
	private HashSet<Integer> pollsLeftToUpload;
    private HashSet<Integer> locationItemsLeftToUpload;
	private boolean updateQuestionsDone = false;
	private boolean uploadPollsDone = false;
    private boolean uploadLocationItemsDone = false;

	@Override
	public void onCreate() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

		super.onCreate();

		initVarsAndUpdates();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onStartCommand");
		}

		super.onStartCommand(intent, flags, startId);

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onDestroy");
		}

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onBind");
		}

		// Don't allow binding
		return null;
	}

	// FIXME: if the servers don't answer, does the connection time out and does the
	// service exit?
	private void initVarsAndUpdates() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] initVarsAndUpdates");
		}

		status = StatusManager.getInstance(this);

		if (status.isDataEnabled()) {

			// Info
			Log.i(TAG, "data connection enabled -> starting sync tasks");

			if (Config.TOASTD) {
				Toast.makeText(this, "SyncService: starting sync...",
						Toast.LENGTH_SHORT).show();
			}

			pollsStorage = PollsStorage.getInstance(this);
			questionsStorage = QuestionsStorage.getInstance(this);
            locationsStorage = LocationsStorage.getInstance(this);
			gson = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.create();

			CryptoStorageCallback callback = new CryptoStorageCallback() {

				private final String TAG = "CryptoStorageCallback";

				@Override
				public void onCryptoStorageReady(boolean hasKeyPairAndMaiId) {

					// Debug
					if (Config.LOGD) {
						Log.d(TAG, "(callback) onCryptoStorageReady");
					}

					serverTalker = ServerTalker.getInstance(SERVER_NAME, cryptoStorage);

					if (hasKeyPairAndMaiId && status.isDataEnabled()) {
						//asyncUpdateQuestions(); // Line commented not to update questions at each launch of the SyncService
						asyncUploadPolls();
                        asyncUploadLocationItems();
					}
				}

			};

			// This will launch all calls through the callbacks
			cryptoStorage = CryptoStorage.getInstance(this, SERVER_NAME, callback);
		} else {

			// Info
			Log.i(TAG, "no data connection available -> exiting");

			if (Config.TOASTD) {
				Toast.makeText(this, "SyncService: no internet connection",
						Toast.LENGTH_SHORT).show();
			}
			stopSelf();
		}
	}

	private void asyncUpdateQuestions() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] asyncUpdateQuestions");
		}

		// FIXME: There might be a problem if the service is started from an Activity, and the
		// orientation of the display changes. That will stop and restart the worker process.
		// See http://developer.android.com/guide/components/processes-and-threads.html ,
		// right above the "Thread-safe methods" title.

//        // Use this:
//        // set the connection timeout value to 30 seconds (30000 milliseconds)
//        final HttpParams httpParams = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
//        client = new DefaultHttpClient(httpParams);


		final HttpConversationCallback updateQuestionsCallback = new HttpConversationCallback() {

			private final String TAG = "HttpConversationCallback";

			@Override
			public void onHttpConversationFinished(boolean success, String serverAnswer) {

				// Debug
				if (Config.LOGD) {
					Log.d(TAG, "[fn] (updateQuestionsCallback) onHttpConversationFinished");
				}

				if (success) {

					// Info
					Log.i(TAG, "successfully retrieved new questions.json from server");

					if (Config.TOASTD) {
						Toast.makeText(SyncService.this,
								"SyncService: new questions downloaded from server",
								Toast.LENGTH_SHORT).show();
					}
					questionsStorage.importQuestions(serverAnswer);
				} else {
					// Warning
					Log.w(TAG, "error while retrieving new questions.json from server");
				}

				setUpdateQuestionsDone();
			}

		};

		HttpConversationCallback fullCallback = new HttpConversationCallback() {

			private final String TAG = "HttpConversationCallback";

			@Override
			public void onHttpConversationFinished(boolean success, String serverAnswer) {

				// Debug
				if (Config.LOGD) {
					Log.d(TAG, "[fn] (fullCallback) onHttpConversationFinished");
				}

				boolean willGetQuestions = false;

				if (success) {

					// Info
					Log.i(TAG, "successfully retrieved questionsVersion from server");

					try {
						int serverQuestionsVersion = Integer.parseInt(serverAnswer.split("\n")[0]);

						if (serverQuestionsVersion != questionsStorage.getQuestionsVersion()) {

							// Info
							Log.i(TAG, "server's questionsVersion is different from the local one -> trying to update questions");

							willGetQuestions = true;

							HttpGetData updateQuestionsData = new HttpGetData(QUESTIONS_URL,
									updateQuestionsCallback);
							HttpGetTask updateQuestionsTask = new HttpGetTask();
							updateQuestionsTask.execute(updateQuestionsData);
						} else {
							if (Config.TOASTD) {
								Toast.makeText(SyncService.this,
										"SyncService: no new questions to download",
										Toast.LENGTH_SHORT).show();
							}
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
				}
			}

		};

		HttpGetData getQuestionsVersionData = new HttpGetData(QUESTIONS_VERSION_URL,
				fullCallback);
		HttpGetTask getQuestionsVersionTask = new HttpGetTask();
		getQuestionsVersionTask.execute(getQuestionsVersionData);
	}

	private void asyncUploadPolls() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] asyncUploadPolls");
		}

		// FIXME: There might be a problem if the service is started from an Activity, and the
		// orientation of the display changes. That will stop and restart the worker process.
		// See http://developer.android.com/guide/components/processes-and-threads.html ,
		// right above the "Thread-safe methods" title.

		uploadablePolls = pollsStorage.getUploadablePolls();

		if (uploadablePolls == null) {

			// Info
			Log.i(TAG, "no polls to upload -> exiting");

			if (Config.TOASTD) {
				Toast.makeText(this,
						"SyncService: no polls to upload",
						Toast.LENGTH_SHORT).show();
			}

			setUploadPollsDone();
			return;
		}

		// Info
		Log.i(TAG, "trying to upload " + uploadablePolls.size() + " polls");

		if (Config.TOASTD) {
			Toast.makeText(this,
					"SyncService: trying to upload " + uploadablePolls.size() + " polls",
					Toast.LENGTH_SHORT).show();
		}

		pollsLeftToUpload = new HashSet<Integer>();
		for (Poll poll : uploadablePolls) {
			pollsLeftToUpload.add(poll.getId());
		}

		for (Poll poll : uploadablePolls) {

			final int pollId = poll.getId();

			HttpConversationCallback callback = new HttpConversationCallback() {

				private final String TAG = "HttpConversationCallback";

				@Override
				public void onHttpConversationFinished(boolean success, String serverAnswer) {

					// Debug
					if (Config.LOGD) {
						Log.d(TAG, "(callback) onHttpConversationFinished");
					}

					if (success) {

						// Info
						Log.i(TAG, "successfully uploaded poll (id: " +
								pollId + ") to server (serverAnswer: " +
								serverAnswer + ")");

						if (Config.TOASTD) {
							Toast.makeText(SyncService.this,
									"SyncService: uploaded poll (id: " + pollId +
									") (serverAnswer: " + serverAnswer + ")",
									Toast.LENGTH_LONG).show();
						}

						pollsStorage.removePoll(pollId);
						setUploadPollDone(pollId);
					} else {

						// Warning
						Log.w(TAG, "error while upload poll (id: " + pollId + ") to server");

						setUploadPollDone(pollId);
					}
				}

			};

			serverTalker.signAndUploadData(EXP_ID, gson.toJson(poll), callback);
		}
	}

    private void asyncUploadLocationItems() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] asyncUploadLocationItems");
        }

        // FIXME: There might be a problem if the service is started from an Activity, and the
        // orientation of the display changes. That will stop and restart the worker process.
        // See http://developer.android.com/guide/components/processes-and-threads.html ,
        // right above the "Thread-safe methods" title.

        uploadableLocationItems = locationsStorage.getUploadableLocationItems();

        if (uploadableLocationItems == null) {

            // Info
            Log.i(TAG, "no locationItems to upload -> exiting");

            if (Config.TOASTD) {
                Toast.makeText(this,
                        "SyncService: no locationItems to upload",
                        Toast.LENGTH_SHORT).show();
            }

            setUploadLocationItemsDone();
            return;
        }

        // Info
        Log.i(TAG, "trying to upload " + uploadableLocationItems.size() + " locationItems");

        if (Config.TOASTD) {
            Toast.makeText(this,
                    "SyncService: trying to upload " + uploadableLocationItems.size() + " locationItems",
                    Toast.LENGTH_SHORT).show();
        }

        locationItemsLeftToUpload = new HashSet<Integer>();
        for (LocationItem locationItem : uploadableLocationItems) {
            locationItemsLeftToUpload.add(locationItem.getId());
        }

        for (LocationItem locationItem : uploadableLocationItems) {

            final int locationItemId = locationItem.getId();

            HttpConversationCallback callback = new HttpConversationCallback() {

                private final String TAG = "HttpConversationCallback";

                @Override
                public void onHttpConversationFinished(boolean success, String serverAnswer) {

                    // Debug
                    if (Config.LOGD) {
                        Log.d(TAG, "(callback) onHttpConversationFinished");
                    }

                    if (success) {

                        // Info
                        Log.i(TAG, "successfully uploaded locationItem (id: " +
                                locationItemId + ") to server (serverAnswer: " +
                                serverAnswer + ")");

                        if (Config.TOASTD) {
                            Toast.makeText(SyncService.this,
                                    "SyncService: uploaded locationItem (id: " + locationItemId +
                                            ") (serverAnswer: " + serverAnswer + ")",
                                    Toast.LENGTH_LONG).show();
                        }

                        locationsStorage.removeLocationItem(locationItemId);
                        setUploadLocationItemDone(locationItemId);
                    } else {

                        // Warning
                        Log.w(TAG, "error while upload locationItem (id: " + locationItemId + ") to server");

                        setUploadLocationItemDone(locationItemId);
                    }
                }

            };

            serverTalker.signAndUploadData(EXP_ID, gson.toJson(locationItem), callback);
        }
    }

    private void setUpdateQuestionsDone() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setUpdateQuestionsDone");
		}

		updateQuestionsDone = true;
		stopSelfIfAllDone();
	}

	private void setUploadPollDone(int pollId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setUploadPollDone");
		}

		pollsLeftToUpload.remove(pollId);

		if (pollsLeftToUpload.size() == 0) {
			setUploadPollsDone();
		}
	}

	private void setUploadPollsDone() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setUploadPollsDone");
		}

		uploadPollsDone = true;
		stopSelfIfAllDone();
	}

    private void setUploadLocationItemDone(int locationItemId) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setUploadLocationItemDone");
        }

        locationItemsLeftToUpload.remove(locationItemId);

        if (locationItemsLeftToUpload.size() == 0) {
            setUploadLocationItemsDone();
        }
    }

    private void setUploadLocationItemsDone() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setUploadLocationItemsDone");
        }

        uploadLocationItemsDone = true;
        stopSelfIfAllDone();
    }

	private void stopSelfIfAllDone() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] stopSelfIfAllDone");
		}

		if (updateQuestionsDone && uploadPollsDone && uploadLocationItemsDone) {
			stopSelf();
		}
	}
}
