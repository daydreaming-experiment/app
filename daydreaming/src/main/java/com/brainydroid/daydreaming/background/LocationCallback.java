package com.brainydroid.daydreaming.background;

import android.location.Location;

/**
 * Interface for callbacks used when location data is received.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see com.brainydroid.daydreaming.db.LocationPoint
 * @see com.brainydroid.daydreaming.db.Question
 * @see LocationService
 * @see LocationServiceConnection
 */
public interface LocationCallback {

    /**
     * Should be called by the class having received location information.
     *
     * @param location The {@link Location} received
     */
    public void onLocationReceived(Location location);

}
