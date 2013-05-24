package com.brainydroid.daydreaming.background;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.brainydroid.daydreaming.db.*;
import com.brainydroid.daydreaming.network.*;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;
import roboguice.service.RoboService;

import java.util.ArrayList;

// FIXME: Worker thread restart on runtime configuration change.
// There might be a problem if the service is started from an
// Activity, and the orientation of the display changes. That will stop and
// restart the worker thread. See
// http://developer.android.com/guide/components/processes-and-threads.html
// right above the "Thread-safe methods" title.

/**
 * Update the question pool, upload answers and location points.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public class SyncService extends RoboService {

    private static String TAG = "SyncService";

    @Inject StatusManager statusManager;
    @Inject PollsStorage pollsStorage;
    @Inject LocationPointsStorage locationPointsStorage;
    @Inject QuestionsStorage questionsStorage;
    @Inject CryptoStorage cryptoStorage;
    @Inject ServerTalker serverTalker;
    @Inject Json json;

    /**
     * Callback called once the {@link CryptoStorage} is ready,
     * launching the synchronization tasks.
     */
    CryptoStorageCallback cryptoStorageCallback =
            new CryptoStorageCallback() {

        private String TAG = "CryptoStorageCallback";

        @Override
        public void onCryptoStorageReady(
                boolean hasKeyPairAndMaiId) {

            // Debug
            if (Config.LOGD) {
                Log.d(TAG, "(callback) onCryptoStorageReady");
            }

            // Only launch the synchronization tasks if CryptoStorage is
            // really ready and we have Internet access.
            if (hasKeyPairAndMaiId && statusManager.isDataEnabled()) {
                // We only update questions once in the experiment's
                // lifecycle.
                if (!statusManager.areQuestionsUpdated()) {
                    asyncUpdateQuestions();
                }

                asyncUploadPolls();
                asyncUploadLocationItems();
            }
        }

    };

    /**
     * Callback called when the questions are finished downloading,
     * to import them into the {@link QuestionsStorage}.
     */
    HttpConversationCallback updateQuestionsCallback =
            new HttpConversationCallback() {

        private String TAG = "HttpConversationCallback";

        @Override
        public void onHttpConversationFinished(boolean success,
                                               String serverAnswer) {

            // Debug
            if (Config.LOGD) {
                Log.d(TAG, "[fn] (updateQuestionsCallback) " +
                        "onHttpConversationFinished");
            }

            if (success) {
                // Info
                Log.i(TAG, "successfully retrieved questions.json " +
                        "from server");

                // Toast debug
                if (Config.TOASTD) {
                    Toast.makeText(SyncService.this,
                            "SyncService: new questions downloaded " +
                                    "from server",
                            Toast.LENGTH_SHORT).show();
                }

                // Import the questions, and remember not to update
                // questions again.
                questionsStorage.importQuestions(serverAnswer);
                statusManager.setQuestionsUpdated();
            } else {
                // Warning
                Log.w(TAG, "error while retrieving new questions.json " +
                        "from server");
            }
        }

    };

    @Override
    public void onCreate() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onCreate");
        }

        super.onCreate();

        // Launch synchronization tasks
        startUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onStartCommand");
        }

        super.onStartCommand(intent, flags, startId);

        // Do nothing. Logging purposes.
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onDestroy");
        }

        super.onDestroy();
        // Do nothing. Logging purposes.
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

    private void startUpdates() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] startUpdates");
        }

        if (statusManager.isDataEnabled()) {
            // Info
            Log.i(TAG, "data connection enabled -> starting sync tasks");

            // Toast debug
            if (Config.TOASTD) {
                Toast.makeText(this, "SyncService: starting sync...",
                        Toast.LENGTH_SHORT).show();
            }

            // This will launch all the calls through the callback
            cryptoStorage.onReady(cryptoStorageCallback);
        } else {
            // Info
            Log.i(TAG, "no data connection available -> exiting");

            // Toast debug
            if (Config.TOASTD) {
                Toast.makeText(this, "SyncService: no internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        }

        // We stop immediately, but the worker threads keep running until
        // they finish or time out.
        stopSelf();
    }

    /**
     * Download and import {@link com.brainydroid.daydreaming.db.Question}s
     * from the server into our pool of questions, asynchronously.
     */
    private void asyncUpdateQuestions() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] asyncUpdateQuestions");
        }

        // Self-evident
        HttpGetData updateQuestionsData =
                new HttpGetData(ServerConfig.QUESTIONS_URL,
                        updateQuestionsCallback);
        HttpGetTask updateQuestionsTask = new HttpGetTask();
        updateQuestionsTask.execute(updateQuestionsData);
    }

    /**
     * Upload answered {@link Poll}s to the server and remove them from local
     * storage, asynchronously.
     */
    private void asyncUploadPolls() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] asyncUploadPolls");
        }

        // Do we have any polls to upload?
        ArrayList<Poll> uploadablePolls = pollsStorage.getUploadablePolls();
        if (uploadablePolls == null) {
            // Info
            Log.i(TAG, "no polls to upload -> exiting");

            // Toast debug
            if (Config.TOASTD) {
                Toast.makeText(this, "SyncService: no polls to upload",
                        Toast.LENGTH_SHORT).show();
            }

            return;
        }

        // Wrap uploadable polls in a single structure to provide a root
        // node when jsonifying
        final PollsArray pollsArray = new PollsArray(uploadablePolls);

        // Called once the HttpPostTask completes or times out
        HttpConversationCallback callback = new HttpConversationCallback() {

            private String TAG = "HttpConversationCallback";

            @Override
            public void onHttpConversationFinished(boolean success,
                                                   String serverAnswer) {

                // Debug
                if (Config.LOGD) {
                    Log.d(TAG, "(callback) onHttpConversationFinished");
                }

                if (success) {
                    // Info
                    Log.i(TAG, "successfully uploaded polls to server " +
                            "(serverAnswer: " + serverAnswer + ")");

                    // Toast debug
                    if (Config.TOASTD) {
                        Toast.makeText(SyncService.this,
                                "SyncService: uploaded polls " +
                                        "(serverAnswer: " +
                                        serverAnswer + ")",
                                Toast.LENGTH_LONG).show();
                    }

                    pollsStorage.remove(pollsArray.getPolls());
                } else {
                    // Warning
                    Log.w(TAG, "error while uploading polls to server");
                }
            }

        };

        // Sign our data to identify us, and upload
        serverTalker.signAndUploadData(ServerConfig.EXP_ID,
                json.toJsonExposed(pollsArray), callback);
    }

    /**
     * Upload collected {@link LocationPoint}s to the server and remove
     * them from local storage, asynchronously.
     */
    private void asyncUploadLocationItems() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] asyncUploadLocationItems");
        }

        // Do we have any location points to upload?
        ArrayList<LocationPoint> uploadableLocationPoints =
                locationPointsStorage.getUploadableLocationPoints();
        if (uploadableLocationPoints == null) {
            // Info
            Log.i(TAG, "no locationItems to upload -> exiting");

            // Toast debug
            if (Config.TOASTD) {
                Toast.makeText(this,
                        "SyncService: no locationItems to upload",
                        Toast.LENGTH_SHORT).show();
            }

            return;
        }

        // Wrap uploadable location points in a single structure to provide
        // a root node when jsonifying.
        final LocationPointsArray locationPoints =
                new LocationPointsArray(uploadableLocationPoints);

        // Called when the HttPPostTask finishes or times out
        HttpConversationCallback callback = new HttpConversationCallback() {

            private final String TAG = "HttpConversationCallback";

            @Override
            public void onHttpConversationFinished(boolean success,
                                                   String serverAnswer) {

                // Debug
                if (Config.LOGD) {
                    Log.d(TAG, "(callback) onHttpConversationFinished");
                }

                if (success) {
                    // Info
                    Log.i(TAG, "successfully uploaded locationPoints to " +
                            "server (serverAnswer: " + serverAnswer + ")");

                    if (Config.TOASTD) {
                        Toast.makeText(SyncService.this,
                                "SyncService: uploaded locationPoints " +
                                        "(serverAnswer: " +
                                        serverAnswer + ")",
                                Toast.LENGTH_LONG).show();
                    }

                    locationPointsStorage.remove(
                            locationPoints.getLocationPoints());
                } else {
                    // Warning
                    Log.w(TAG, "error while uploading locationPoints " +
                            "to server");
                }
            }

        };

        // Sign our data to identify us, and upload
        serverTalker.signAndUploadData(ServerConfig.EXP_ID,
                json.toJsonExposed(locationPoints), callback);
    }

}
