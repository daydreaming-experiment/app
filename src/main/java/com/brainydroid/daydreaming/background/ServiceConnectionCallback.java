package com.brainydroid.daydreaming.background;

/**
 * Interface for callbacks used when a {@link LocationServiceConnection}
 * has connected to its {@link LocationService}.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see LocationService
 * @see LocationServiceConnection
 */
public interface ServiceConnectionCallback {

    /**
     * Should be called by the connection ({@link
     * LocationServiceConnection} once it is connected to the service
     * ({@link LocationService}).
     */
    public void onServiceConnected();

}
