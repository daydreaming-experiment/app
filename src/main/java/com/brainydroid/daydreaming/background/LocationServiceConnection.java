package com.brainydroid.daydreaming.background;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.brainydroid.daydreaming.background.LocationService.LocationServiceBinder;
import com.google.inject.Inject;

/**
 * Manage the lifecycle of {@link LocationService} and pass messages
 * on to it.
 * <p/>
 * Use this class as the handler for {@code LocationService}. You can
 * start, stop, bind and unbind to the service using this interface. The
 * main use is to pass callbacks to a running or to-be-started {@code
 * LocationService} so as to receive location updates on the right objects.
 * The main constraint to remember is that you can only send messages to the
 * {@code LocationService} (e.g. register or un-register callbacks) when it
 * is bound. And in that case, they are transmitted instantly (i.e. not
 * depending on a callback).
 * <p/>
 * There are two use cases:
 * <ul>
 *     <li>{@link LocationPointService}: needs to start the {@code
 *     LocationService} ({@code startLocationService()}),
 *     register a callback on it and stop itself. Registering the callback
 *     is done through this {@code LocationServiceConnection}, as follows:
 *     <ol>
 *         <li>Record the callback to be set, with {@code
 *         setLocationPointCallback()}</li>
 *         <li>Create and register ({@code
 *         setOnServiceConnectionCallback()}) a {@link
 *         ServiceConnectionCallback} to stop the {@code
 *         LocationPointService} and unbind ({@code
 *         unbindLocationService()})</li>
 *         <li>Bind the {@code LocationService} to pass the message
 *         ({@code bindLocationService()}) ; once bound,
 *         the {@link ServiceConnectionCallback} stops the
 *         {@code LocationPointService} and unbinds. The {@code
 *         LocationService} keeps running on its own,
 *         sending location updates to the registered callback.</li>
 *     </ol>
 *     After some time, the {@code LocationPointService} starts again
 *     and clears its listener, which will stop the {@code LocationService}
 *     if no other listeners are registered (i.e. if no {@link
 *     com.brainydroid.daydreaming.ui.QuestionActivity} is listening for
 *     updates). This is done with the same procedure: record the message
 *     to pass ({@code clearLocationPointCallback()}),
 *     set a {@link ServiceConnectionCallback} to stop the {@code
 *     LocationPointService} and unbind, then bind to start the chain of
 *     callbacks.</li>
 *     <li>{@link com.brainydroid.daydreaming.ui.QuestionActivity}: needs
 *     to start the {@code LocationService}, bind to it,
 *     register a callback, and stay bound to be able to send messages at
 *     will (the difference with {@code LocationPointService} is that the
 *     {@code QuestionActivity} keeps running all the time while it is
 *     listening for location updates. When {@code QuestionActivity} stops,
 *     it clears its callback and unbinds. That will stop the {@code
 *     LocationService} if no other callback is registered (i.e. if no
 *     {@code LocationPoint} is listening for updates).
 *     </li>
 * </ul>
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public class LocationServiceConnection implements ServiceConnection {

    private static String TAG = "LocationServiceConnection";

    // Should we set a LocationPointCallback when possible?
    private boolean setLocationPointCallback = false;
    // If so we will set this one
    private LocationCallback locationPointCallbackToSet = null;

    // Should we set a QuestionLocationCallback when possible?
    private boolean setQuestionLocationCallback = false;
    // If so we will set this one
    private LocationCallback questionLocationCallbackToSet = null;

    // The LocationService we connect to
    private LocationService locationService;

    // Called once we are connected to the LocationService
    private ServiceConnectionCallback serviceConnectionCallback = null;

    // true if we are connecting or connected,
    // false if we are disconnecting or disconnected.
    private boolean sBound = false;

    @Inject Context context;

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        Logger.d(TAG, "LocationService connected");

        // Get our service and set our callbacks
        locationService = ((LocationServiceBinder)binder).getService();
        setLocationServiceCallbacks();

        // If we can, call our ServiceConnectionCallback
        if (serviceConnectionCallback != null) {
            Logger.d(TAG, "Calling back serviceConnectionCallback");
            serviceConnectionCallback.onServiceConnected();
        } else {
            Logger.v(TAG, "No serviceConnectionCallback to call");
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Logger.d(TAG, "LocationService disconnected");

        // The service is dead, forget about it
        locationService = null;
    }

    /**
     * Start the {@link LocationService}.
     */
    public void startLocationService() {
        Logger.d(TAG, "Starting LocationService");
        Intent locationServiceIntent = new Intent(context,
                LocationService.class);
        context.startService(locationServiceIntent);
    }

    /**
     * Bind to the {@link LocationService}.
     */
    public void bindLocationService() {
        Logger.d(TAG, "Binding to LocationService");

        // Only bind if we're not waiting for a previous bindService() to
        // complete
        if (!sBound) {
            Intent locationServiceIntent = new Intent(context,
                    LocationService.class);
            context.bindService(locationServiceIntent, this, 0);
            sBound = true;
        } else {
            Logger.v(TAG, "LocationService already binding or bound -> " +
                    "leaving it so");
        }
    }

    /**
     * Unbind from the {@link LocationService}.
     */
    public void unbindLocationService() {
        Logger.d(TAG, "Unbinding from LocationService");

        // Only unbind if we're not waiting for a previous unbindService()
        // to complete
        if (sBound) {
            context.unbindService(this);
            sBound = false;
        } else {
            Logger.w(TAG, "Already unbinding or unbound from " +
                    "LocationService -> leaving it so");
        }
    }

    /**
     * Register recorded callbacks on the {@link LocationService}.
     */
    private void setLocationServiceCallbacks() {
        Logger.d(TAG, "Setting callbacks on LocationService");

        // If we remembered to set a LocationPoint callback,
        // do so. Then flush our memory of this.
        if (setLocationPointCallback) {
            Logger.d(TAG, "Setting locationPointCallback");
            locationService.setLocationPointCallback(
                    locationPointCallbackToSet);
            locationPointCallbackToSet = null;
            setLocationPointCallback = false;
        } else {
            Logger.v(TAG, "No locationPointCallback to set");
        }

        // If we remembered to set a QuestionLocation callback,
        // do so. Then flush our memory of this.
        if (setQuestionLocationCallback) {
            Logger.d(TAG, "Setting questionLocationCallback");
            locationService.setQuestionLocationCallback(
                    questionLocationCallbackToSet);
            questionLocationCallbackToSet = null;
            setQuestionLocationCallback = false;
        } else {
            Logger.v(TAG, "No questionLocationCallback to set");
        }
    }

    /**
     * Register a {@link ServiceConnectionCallback}.
     *
     * @param serviceConnectionCallback Callback to register
     */
    public void setOnServiceConnectedCallback(
            ServiceConnectionCallback serviceConnectionCallback) {
        this.serviceConnectionCallback = serviceConnectionCallback;
    }

    /**
     * Remember to set a {@code LocationPoint} callback when possible.
     *
     * @param callback Callback to remember
     */
    public void setLocationPointCallback(LocationCallback callback) {
        // Set the callback straight away if possible. If not possible,
        // remember to do it later.
        if (sBound && locationService != null) {
            Logger.d(TAG, "Setting locationPointCallback");
            locationService.setLocationPointCallback(callback);
            locationPointCallbackToSet = null;
            setLocationPointCallback = false;
        } else {
            Logger.d(TAG, "Recording locationPointCallback for later " +
                    "setting");
            locationPointCallbackToSet = callback;
            setLocationPointCallback = true;
        }
    }

    /**
     * Remember to set a {@code QuestionLocation} callback when possible.
     *
     * @param callback Callback to remember
     */
    public void setQuestionLocationCallback(LocationCallback callback) {
        // Set the callback straight away if possible. If not possible,
        // remember to do it later.
        if (sBound && locationService != null) {
            Logger.d(TAG, "Setting questionLocationCallback");
            locationService.setQuestionLocationCallback(callback);
            questionLocationCallbackToSet = null;
            setQuestionLocationCallback = false;
        } else {
            Logger.d(TAG, "Recording questionLocationCallback for later " +
                    "setting");
            questionLocationCallbackToSet = callback;
            setQuestionLocationCallback = true;
        }
    }

    /**
     * Remember to clear {@code LocationPoint} callback when possible.
     */
    public void clearLocationPointCallback() {
        Logger.d(TAG, "Clearing locationPointCallback");
        setLocationPointCallback(null);
    }

    /**
     * Remember to clear {@code QuestionLocation} callback when possible.
     */
    public void clearQuestionLocationCallback() {
        Logger.d(TAG, "Clearing questionLocationCallback");
        setQuestionLocationCallback(null);
    }

}
