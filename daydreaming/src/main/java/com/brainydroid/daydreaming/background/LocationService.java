package com.brainydroid.daydreaming.background;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import com.google.inject.Inject;
import roboguice.service.RoboService;

/**
 * Listen for location updates and pass them on to any registered callbacks.
 * <p/>
 * This service is started by using {@link LocationServiceConnection},
 * which can bind, start, unbind. Callbacks for the location updates are
 * also registered through the {@link LocationServiceConnection}.
 * <p/>
 * The lifecycle is as follows: a {@code Service} or an {@code Activity} (in
 * practice {@link LocationPointService} or {@link
 * com.brainydroid.daydreaming.ui.questions.QuestionActivity} wants to obtain some
 * location data. To do so it needs to listen for a given period of time to
 * leave the time to the location backend to acquire a proper location. So
 * it will start {@link LocationService} (through the {@link
 * LocationServiceConnection}) and register a callback to be called when
 * location updates are received. When it has waited long enough,
 * it will un-register its callback on the {@link LocationService} (again
 * through the {@link LocationServiceConnection}). The {@link
 * LocationService} will stop itself if no other callback is registered
 * (indeed, a {@link com.brainydroid.daydreaming.ui.QuestionActivity} and a
 * {@link LocationPointService} could be listening for location updates at
 * the same time ; when one of the finishes and un-registers,
 * the other one doesn't want {@link LocationService} to stop until it is
 * also done).
 * <p/>
 * For further details on how exactly the {@link LocationService} should be
 * started and how to register callbacks on it,
 * see {@link LocationServiceConnection}.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see LocationServiceConnection
 * @see LocationCallback
 * @see ServiceConnectionCallback
 * @see com.brainydroid.daydreaming.db.LocationPoint
 * @see com.brainydroid.daydreaming.db.Question
 */
public class LocationService extends RoboService {

    private static String TAG = "LocationService";

    private LocationListener locationListener;
    private LocationCallback questionLocationCallback = null;
    private LocationCallback locationPointCallback = null;
    private Location lastLocation;

    // We can't inject an inner class using Guice
    private IBinder mBinder = new LocationServiceBinder();

    @Inject LocationManager locationManager;

    /**
     * {@link IBinder} interface used by {@link LocationServiceConnection}
     * to get a handle on the {@link LocationService} after binding.
     */
    public class LocationServiceBinder extends Binder {

        /**
         * Get a handle to the bound {@link LocationService}.
         *
         * @return Currently bound {@link LocationService}
         */
        LocationService getService() {
            return LocationService.this;
        }

    }

    /**
     * Set the {@link com.brainydroid.daydreaming.db.LocationPoint}-tagged
     * callback for location updates. This callback should be created by
     * {@link LocationPointService}.
     *
     * @param callback Callback to set
     */
    public synchronized void setLocationPointCallback(
            LocationCallback callback) {
        Logger.d(TAG, "Setting LocationPoint callback");

        // Set the callback
        locationPointCallback = callback;

        // If we already received location data, forward it straight away
        // to the callback.
        if (lastLocation != null && locationPointCallback != null) {
            Logger.d(TAG, "Calling back the callback with previously " +
                    "collected location");
            locationPointCallback.onLocationReceived(lastLocation);
        } else {
            Logger.v(TAG, "No previous location to call back the callback " +
                    "for");
        }
    }

    /**
     * Set the {@link com.brainydroid.daydreaming.ui
     * .QuestionActivity}-tagged callback for location updates. This
     * callback should be created by {@link com.brainydroid.daydreaming.ui
     * .QuestionActivity}.
     *
     * @param callback Callback to set
     */
    public synchronized void setQuestionLocationCallback(
            LocationCallback callback) {
        Logger.d(TAG, "Setting Question location callback");

        // Set the callback
        questionLocationCallback = callback;

        // If we already received location data, forward it straight away
        // to the callback.
        if (lastLocation != null && questionLocationCallback != null) {
            Logger.d(TAG, "Calling back the callback with previously " +
                    "collected location");
            questionLocationCallback.onLocationReceived(lastLocation);
        } else {
            Logger.v(TAG, "No previous location to call back the callback " +
                    "for");
        }
    }

    @Override
    public synchronized void onCreate() {
        Logger.d(TAG, "LocationService created");

        super.onCreate();
        // Start listening for location updates
        startLocationListener();
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags,
                                           int startId) {
        Logger.d(TAG, "LocationService started");

        // Nothing to do here, the logic is in onCreate
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    @Override
    public synchronized void onDestroy() {
        Logger.d(TAG, "Destroying LocationService");

        // Stop listening for location updates
        stopLocationListenerIfExists();
        super.onDestroy();
    }

    @Override
    public synchronized IBinder onBind(Intent intent) {
        Logger.d(TAG, "Binding to LocationService");

        // Allow binding. Will be used by the LocationServiceConnection.
        return mBinder;
    }

    @Override
    public synchronized void onRebind(Intent intent) {
        Logger.d(TAG, "Rebinding to LocationService");

        super.onRebind(intent);
        // This function is for logging purposes. This way we can see
        // rebinds in the logs.
    }

    @Override
    public synchronized boolean onUnbind(Intent intent) {
        Logger.d(TAG, "Unbinding from LocationService");

        super.onUnbind(intent);

        // If we neither a callback for LocationPointService nor for
        // QuestionActivity, stop ourselves.
        if (locationPointCallback == null &&
                questionLocationCallback == null) {
            Logger.i(TAG, "No LocationPoint callback nor Question location " +
                    "callbacks set -> stopping self");
            stopSelf();
        } else {
            Logger.v(TAG, "A LocationPoint callback or a Question callback " +
                    "is set -> continuing service");
        }

        // Make sure onUnbind is called again if some clients rebind and
        // re-unbind.
        return true;
    }

    /**
     * Start listening to location updates. This is done by registering a
     * {@link LocationListener} on the {@link LocationManager}.
     */
    private synchronized void startLocationListener() {
        Logger.d(TAG, "Starting location listening");

        // If we're already listening, stop it and start from scratch to
        // make sure we have the right callbacks. (Is this really useful?)
        stopLocationListenerIfExists();

        locationListener = new LocationListener() {

            private String TAG = "LocationListener";

            @Override
            public void onLocationChanged(Location location) {
                Logger.d(TAG, "New location received");

                // Remember location
                lastLocation = location;

                // Send the location data to the LocationPointService
                if (locationPointCallback != null) {
                    Logger.d(TAG, "Calling back LocationPoint callback with" +
                            " new location");
                    locationPointCallback.onLocationReceived(location);
                } else {
                    Logger.v(TAG, "No LocationPoint callback to call back");
                }

                // Send the location data to the QuestionActivity
                if (questionLocationCallback != null) {
                    Logger.d(TAG, "Calling back Question location callback " +
                            "with new location");
                    questionLocationCallback.onLocationReceived(location);
                } else {
                    Logger.v(TAG, "No Question location callback to call " +
                            "back");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}

        };

        // Register our listener
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    /**
     * Stop listening to location updates, if we were listening.
     */
    private synchronized void stopLocationListenerIfExists() {
        if (locationListener != null) {
            Logger.d(TAG, "Removing existing location listener");
            locationManager.removeUpdates(locationListener);
            locationListener = null;
        } else {
            Logger.v(TAG, "No location listener to remove");
        }
    }

}
