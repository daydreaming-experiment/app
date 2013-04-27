package com.brainydroid.daydreaming.background;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.brainydroid.daydreaming.background.LocationService.LocationServiceBinder;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;

public class LocationServiceConnection implements ServiceConnection {

	private static String TAG = "LocationServiceConnection";

    private ServiceConnectionCallback serviceConnectionCallback = null;
	private LocationCallback locationItemCallbackToSet = null;
    private boolean setLocationItemCallback = false;
    private LocationCallback questionLocationCallbackToSet = null;
    private boolean setQuestionLocationCallback = false;
	private LocationService locationService;
	private boolean sBound = false;

    @Inject Context context;

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onServiceConnected");
		}

		LocationServiceBinder binder = (LocationServiceBinder)service;
		locationService = binder.getService();
		setLocationServiceCallbacks();

        if (serviceConnectionCallback != null) {
            serviceConnectionCallback.onServiceConnected();
        }
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onServiceDisconnected");
		}

		locationService = null;
	}

	public void startLocationService() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] startLocationService");
		}

		Intent locationServiceIntent = new Intent(context, LocationService.class);
		context.startService(locationServiceIntent);
	}

	public void bindLocationService() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] bindLocationService");
		}

        if (!sBound) {
            Intent locationServiceIntent = new Intent(context, LocationService.class);
            context.bindService(locationServiceIntent, this, 0);
            sBound = true;
        }
	}

	public void unbindLocationService() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] unbindLocationService");
		}

		if (sBound) {
			context.unbindService(this);
			sBound = false;
		}
	}

	private void setLocationServiceCallbacks() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setLocationServiceCallbacks");
		}

		if (setLocationItemCallback) {
			locationService.setLocationItemCallback(locationItemCallbackToSet);
			locationItemCallbackToSet = null;
            setLocationItemCallback = false;
		}

        if (setQuestionLocationCallback) {
            locationService.setQuestionLocationCallback(questionLocationCallbackToSet);
            questionLocationCallbackToSet = null;
            setQuestionLocationCallback = false;
        }
	}

    public void setOnServiceConnectedCallback(ServiceConnectionCallback serviceConnectionCallback) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setOnServiceConnectedCallback");
        }

        this.serviceConnectionCallback = serviceConnectionCallback;
    }

    public void setLocationItemCallback(LocationCallback callback) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setLocationItemCallback");
        }

        if (sBound && locationService != null) {
            locationService.setLocationItemCallback(callback);
            locationItemCallbackToSet = null;
            setLocationItemCallback = false;
        } else {
            locationItemCallbackToSet = callback;
            setLocationItemCallback = true;
        }
    }

	public void setQuestionLocationCallback(LocationCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setQuestionLocationCallback");
		}

		if (sBound && locationService != null) {
			locationService.setQuestionLocationCallback(callback);
			questionLocationCallbackToSet = null;
            setQuestionLocationCallback = false;
		} else {
			questionLocationCallbackToSet = callback;
            setQuestionLocationCallback = true;
		}
	}

    public void clearLocationItemCallback() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] clearLocationItemCallback");
        }

        setLocationItemCallback(null);
    }

    public void clearQuestionLocationCallback() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] clearQuestionLocationCallback");
        }

        setQuestionLocationCallback(null);
    }

}
