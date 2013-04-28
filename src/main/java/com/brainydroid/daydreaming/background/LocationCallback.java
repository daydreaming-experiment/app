package com.brainydroid.daydreaming.background;

import android.location.Location;

/**
 * Interface for callbacks used when location data is received.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public interface LocationCallback {

    /**
     * Should be called by the class having received location information.
     *
     * @param location The {@code Location} received.
     */
    public void onLocationReceived(Location location);

}
