package com.brainydroid.daydreaming.background;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.LocationsStorage;
import com.brainydroid.daydreaming.ui.Config;

import java.util.ArrayList;

public class LocationItemService extends Service {

	private static String TAG = "LocationsItemService";

    public static String STOP_LISTENING_TASKS = "stopListeningTasks";

	private LocationsStorage locationsStorage;
	private AlarmManager alarmManager;

	@Override
	public void onCreate() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onStartCommand");
		}

		super.onStartCommand(intent, flags, startId);

		initVars();
        if (intent.getBooleanExtra(STOP_LISTENING_TASKS, false)) {
            stopListeningTasks();
        } else {
            scheduleNextService();
            startListeningTasks();
        }

		stopSelf();
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

	private void initVars() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] initVars");
		}

		locationsStorage = LocationsStorage.getInstance(this);
		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	}

    private void stopListeningTasks() {
        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] stopListeningTasks");
        }
    }

    private void startListeningTasks() {
        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] startListeningTasks");
        }
    }

    private void scheduleNextService() {
        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] scheduleNextService");
        }
    }
}
