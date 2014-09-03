package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

/**
 * Store and retrieve {@link LocationPoint} items stored in an SQLite database.
 * This class inherits most of its logic from {@link StatusModelStorage},
 * and you should read its documentation to understand how this class works.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see Model
 * @see StatusModel
 * @see ModelStorage
 * @see StatusModelStorage
 * @see LocationPoint
 */
@Singleton
public final class LocationPointsStorage extends
        StatusModelStorage<LocationPoint,LocationPointsStorage> {

    private static String TAG = "LocationPointsStorage";

    // Table name for our location points
    private static final String TABLE_LOCATION_POINTS = "locationPoints";

    @Inject
    public LocationPointsStorage(Storage storage) {
        super(storage);
    }

    @Override
    protected synchronized String getTableName() {
        return TABLE_LOCATION_POINTS;
    }

    /**
     * Retrieve uploadable {@link LocationPoint}s,
     * that is {@link LocationPoint}s that have a status of {@link
     * LocationPoint#STATUS_COMPLETED}.
     *
     * @return An {@link ArrayList} of completed {@link LocationPoint}s
     */
    public synchronized ArrayList<LocationPoint> getUploadableLocationPoints() {
        Logger.d(TAG, "Getting uploadable LocationPoints");
        return getModelsByStatuses(
                new String[]{LocationPoint.STATUS_COMPLETED});
    }

    /**
     * Retrieve {@link LocationPoint}s marked as currently collecting
     * location data. Those are the {@link LocationPoint}s that have a {@link
     * LocationPoint#STATUS_COLLECTING} status.
     *
     * @return An {@link ArrayList} of currently collecting {@link
     *         LocationPoint}s
     */
    public synchronized ArrayList<LocationPoint> getCollectingLocationPoints() {
        Logger.d(TAG, "Getting collecting LocationPoints");
        return getModelsByStatuses(
                new String[] {LocationPoint.STATUS_COLLECTING});
    }

    public synchronized void removeUploadableLocationPoints() {
        Logger.d(TAG, "Removing uploadable LocationPoints");
        remove(getUploadableLocationPoints());
    }

}
