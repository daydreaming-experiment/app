package com.brainydroid.daydreaming.background;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;

/**
 * Log required information if the application configuration requires so.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public class Logger {

    /**
     * Log at Info level.
     *
     * @param tag Tag to attach to the log message
     * @param message Log message
     */
    public static void i(String tag, String message) {
        if (Config.LOGI) Log.i(tag, message);
    }

    /**
     * Log at Debug level.
     *
     * @param tag Tag to attach to the log message
     * @param message Log message
     */
    public static void d(String tag, String message) {
        if (Config.LOGD) Log.d(tag, message);
    }

    /**
     * Log at Verbose level.
     *
     * @param tag Tag to attach to the log message
     * @param message Log message
     */
    public static void v(String tag, String message) {
        if (Config.LOGV) Log.v(tag, message);
    }

}
