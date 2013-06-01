package com.brainydroid.daydreaming.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import com.brainydroid.daydreaming.db.LocationPoint;
import com.brainydroid.daydreaming.db.LocationPointsStorage;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.network.SntpClientCallback;
import com.google.inject.Inject;
import roboguice.service.RoboService;

import java.util.ArrayList;

/**
 * Start and manage {@link LocationService} to obtain a {@link
 * LocationPoint}.
 * <p/>
 * The service starts {@code LocationService} and lets it listen for {@code
 * LISTENING_TIME} milliseconds. That is, it starts the {@code
 * LocationService} and schedules itself ({@code LocationPointService}) to
 * start again later to stop that same {@code LocationService} after the
 * listening period. When stopping the listener, it also schedules itself
 * to start again after {@code SAMPLE_INTERVAL} milliseconds for the next
 * listening period.
 * <p/>
 * {@code LocationService} is only ever started if data and location
 * accesses are allowed.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public class LocationPointService extends RoboService {

    private static String TAG = "LocationPointService";

    /** Duration to listen for location updates. */
    public static long LISTENING_TIME = 2 * 60 * 1000;    // 2 min (in ms)
    /** Time to wait before starting to listen again. */
    public static long SAMPLE_INTERVAL = 18 * 60 * 1000;  // 18 min (in ms)
    /** Extra to set to {@code true} to stop the listening. */
    public static String STOP_LOCATION_LISTENING = "stopLocationListening";

    @Inject SntpClient sntpClient;
    @Inject LocationPoint locationPoint;
    @Inject LocationPointsStorage locationPointsStorage;
    @Inject AlarmManager alarmManager;
    @Inject StatusManager statusManager;
    @Inject LocationServiceConnection locationServiceConnection;

    // Callback for LocationServiceConnection to stop us and unbind
    // from LocationService.
    private ServiceConnectionCallback serviceConnectionCallback =
            new ServiceConnectionCallback() {

        private String TAG = "ServiceConnectionCallback";

        @Override
        public void onServiceConnected() {
            Logger.d(TAG, "LocationPointService connected");

            // Stop LocationPointService, we're done. This in turn will
            // trigger the unbind through LocationPointService.onDestroy.
            Logger.d(TAG, "Stopping LocationService and unbinding");
            LocationPointService.this.stopSelf();

            // Don't forget to unbind from the LocationService
            locationServiceConnection.unbindLocationService();
        }

    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "LocationPointService started");
        super.onStartCommand(intent, flags, startId);

        // Were we started to start or to stop the listening?
        if (intent.getBooleanExtra(STOP_LOCATION_LISTENING, false)) {
            Logger.d(TAG, "Meant to stop location listening");

            // If so, schedule ourselves for the next listening
            stopLocationListening();
            scheduleNextService();
        } else {
            Logger.d(TAG, "Meant to start location listening");

            // Only start listening if there a chance to obtain something
            if (statusManager.isDataAndLocationEnabled()) {
                Logger.d(TAG, "Data and location are enabled");

                // Don't forget to stop listening after a few minutes
                startLocationListening();
                scheduleStopLocationListening();
            } else {
                Logger.d(TAG, "Either data or location not enabled");

                // If we can't get any location now,
                // wait for the next occasion
                scheduleNextService();
            }
        }

        // The service stops itself through callbacks set in
        // stopLocationListening or startLocationListening,
        // so no need to stop ourselves here.
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Don't allow binding to ourselves
        return null;
    }

    /**
     * Stop the {@link LocationService} service and stop ourselves when
     * that's done.
     */
    private void stopLocationListening() {
        Logger.d(TAG, "Stopping location listening");

        // locationServiceConnection will clear our listener (registered on
        // the LocationService) when it binds to LocationService. We do
        // this even if the LocationService is not running (see test below),
        // since it will flush any other callback that could have been
        // waiting to be transferred to the LocationService on bind.
        locationServiceConnection.clearLocationPointCallback();

        // Mark all location points as uploadable. Only complete ones will
        // have been saved to DB. This ArrayList should in fact always be
        // reduced to only one item.
        ArrayList<LocationPoint> collectingLocationPoints =
                locationPointsStorage.getCollectingLocationPoints();
        if (collectingLocationPoints != null) {
            Logger.d(TAG, "Setting collecting LocationPoints to completed");

            for (LocationPoint collectingLocationPoint :
                    collectingLocationPoints) {
                collectingLocationPoint.setStatus(
                        LocationPoint.STATUS_COMPLETED);
            }

            // Try and spot bugs
            if (collectingLocationPoints.size() != 1) {
                // Warning
                Logger.w(TAG, "collectingLocationPoints.size() should be 1," +
                        " but is {0}", collectingLocationPoints.size());
            }
        } else {
            Logger.v(TAG, "No LocationPoints to set to completed");
        }

        // If LocationService is not running there's no need to stop it,
        // we can exit.
        if (!statusManager.isLocationServiceRunning()) {
            // We're not doing a service connection, so we're all done
            Logger.d(TAG, "LocationService isn't running -> stopping self");
            stopSelf();
            return;
        }

        // When bound, locationServiceConnection will stop us and unbind
        Logger.d(TAG, "Setting locationServiceConnection's callback");
        locationServiceConnection.setOnServiceConnectedCallback(
                serviceConnectionCallback);

        // Making the bind transfers the "clear listener" message to the
        // LocationService. Upon connection to the LocationService,
        // the locationServiceConnection also stops us through the above
        // callback. That, in turn, will make locationServiceConnection
        // unbind from the LocationService (see onDestroy). At that point
        // the LocationService will stop itself if it has no other listeners
        // registered.
        //
        // This sounds terribly complicated but it's the only way to deal
        // with the callbacks on a single thread. Calling
        // locationServiceConnection.unbindLocationService() right here
        // would unbind before the connection to LocationService has taken
        // place: indeed, the connection is made after this function
        // returns.
        Logger.d(TAG, "Binding to LocationService through " +
                "locationServiceConnection");
        locationServiceConnection.bindLocationService();

    }

    /**
     * Start the {@link LocationService} service and register a listener on
     * it.
     */
    private void startLocationListening() {
        Logger.d(TAG, "Starting location listening");

        // Mark the location point as not yet uploadable
        Logger.v(TAG, "Setting LocationPoint's status to collecting");
        locationPoint.setStatus(LocationPoint.STATUS_COLLECTING);

        // This will be called by LocationService when it receives location
        // data. It gets registered on the LocationService when the
        // locationServiceConnection binds.
        LocationCallback locationCallback = new LocationCallback() {

            private String TAG = "LocationCallback";

            @Override
            public void onLocationReceived(Location location) {
                Logger.i(TAG, "New location received, " +
                        "setting on the locationPoint");
                locationPoint.setLocation(location);

                // Only save if both timestamp and location have been
                // found. Otherwise it would create unusable data.
                if (locationPoint.isComplete()) {
                    Logger.i(TAG, "LocationPoint complete, saving to DB");
                    locationPoint.save();
                }
            }

        };

        // We also want an accurate timestamp for this location data (i.e.
        // not dependent on the user's settings), so we'll get it with NTP.
        // This callback is called by the sntpClient when the NTP request
        // completes.
        SntpClientCallback sntpCallback = new SntpClientCallback() {

            private String TAG = "SntpClientCallback";

            @Override
            public void onTimeReceived(SntpClient sntpClient) {
                Logger.d(TAG, "NTP request completed");

                if (sntpClient != null) {
                    Logger.i(TAG, "NTP request successful, " +
                            "setting new time on the locationPoint");
                    locationPoint.setTimestamp(sntpClient.getNow());

                    // Only save if both timestamp and location have been
                    // found. Otherwise it would create unusable data.
                    if (locationPoint.isComplete()) {
                        Logger.i(TAG, "LocationPoint is complete, " +
                                "saving to DB");
                        locationPoint.save();
                    }
                } else {
                    Logger.i(TAG, "NTP request failed, sntpClient is null");
                }
            }

        };

        Logger.d(TAG, "Setting locationServiceConnection callbacks");
        locationServiceConnection.setOnServiceConnectedCallback(
                serviceConnectionCallback);
        locationServiceConnection.setLocationPointCallback(locationCallback);

        Logger.d(TAG, "Starting NTP request");
        sntpClient.asyncRequestTime(sntpCallback);

        // If the service isn't already running, it needs to be started as
        // well as bound, to make sure it stays alive after we unbind. If it
        // is already running, someone else took care of starting it.
        if (!statusManager.isLocationServiceRunning()) {
            Logger.d(TAG, "LocationService not running");
            Logger.d(TAG, "Binding to and starting LocationService");
            locationServiceConnection.bindLocationService();
            locationServiceConnection.startLocationService();
        } else {
            Logger.d(TAG, "LocationService running");
            Logger.d(TAG, "Binding to LocationService");
            locationServiceConnection.bindLocationService();
        }
    }

    /**
     * Schedule the next run of {@code LocationPointService},
     * after SAMPLE_INTERVAL milliseconds.
     */
    private void scheduleNextService() {
        Logger.d(TAG, "Scheduling next location listening");

        // Build the scheduled time
        long scheduledTime = SystemClock.elapsedRealtime() + SAMPLE_INTERVAL;

        // Create the PendingIntent. Any previous one is cancelled.
        Intent intent = new Intent(this, LocationPointService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // And set the alarm
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                scheduledTime, pendingIntent);
    }

    /**
     * Schedule the end of the listening to location updates,
     * when we stop the LocationService after LISTENING_TIME.
     */
    private void scheduleStopLocationListening() {
        Logger.d(TAG, "Scheduling stopping of location listening");

        // Build the scheduled time
        long scheduledTime = SystemClock.elapsedRealtime() + LISTENING_TIME;

        // Create the PendingIntent with a flag telling
        // LocationPointService to stop the listening. Any previous
        // PendingIntent is cancelled.
        Intent intent = new Intent(this, LocationPointService.class);
        intent.putExtra(STOP_LOCATION_LISTENING, true);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // And set the alarm
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                scheduledTime, pendingIntent);
    }

}
