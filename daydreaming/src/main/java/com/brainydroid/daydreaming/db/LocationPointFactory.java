package com.brainydroid.daydreaming.db;

/**
 * Create {@link LocationPoint}s whenever we need an instance that can't be
 * injected with Guice.
 * <p/>
 * Guice handles the implementation of this interface,
 * we only need to register it in a module (which is done in {@link
 * com.brainydroid.daydreaming.ui.AppModule}.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see LocationPoint
 */
public interface LocationPointFactory {

    /**
     * Create an empty {@link LocationPoint} instance with injected
     * dependencies (using Guice).
     *
     * @return New {@link LocationPoint} instance
     */
    public LocationPoint create();

}
