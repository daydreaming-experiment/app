package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.text.MessageFormat;

/**
 * Log required information if the application configuration requires so.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public class Logger {

    /**
     * Log at Error level.
     *
     * @param tag Tag to attach to the log message
     * @param messagePattern Log message in MessageFormat pattern format
     * @param messageArgs Optional arguments for the messagePattern pattern
     */
    public static void e(String tag, String messagePattern,
                         Object... messageArgs) {
        Log.e(tag, MessageFormat.format(messagePattern, messageArgs));
    }

    /**
     * Log at Warn level.
     *
     * @param tag Tag to attach to the log message
     * @param messagePattern Log message in MessageFormat pattern format
     * @param messageArgs Optional arguments for the messagePattern pattern
     */
    public static void w(String tag, String messagePattern,
                         Object... messageArgs) {
        Log.w(tag, MessageFormat.format(messagePattern, messageArgs));
    }

    /**
     * Log at Info level.
     *
     * @param tag Tag to attach to the log message
     * @param messagePattern Log message in MessageFormat pattern format
     * @param messageArgs Optional arguments for the messagePattern pattern
     */
    public static void i(String tag, String messagePattern,
                         Object... messageArgs) {
        if (BuildConfig.LOGI) Log.i(tag, MessageFormat.format(messagePattern,
                messageArgs));
    }

    /**
     * Log at Debug level.
     *
     * @param tag Tag to attach to the log message
     * @param messagePattern Log message in MessageFormat pattern format
     * @param messageArgs Optional arguments for the messagePattern pattern
     */
    public static void d(String tag, String messagePattern,
                         Object... messageArgs) {
        if (BuildConfig.LOGD) Log.d(tag, MessageFormat.format(messagePattern,
                messageArgs));
    }

    /**
     * Log at Verbose level.
     *
     * @param tag Tag to attach to the log message
     * @param messagePattern Log message in MessageFormat pattern format
     * @param messageArgs Optional arguments for the messagePattern pattern
     */
    public static void v(String tag, String messagePattern,
                         Object... messageArgs) {
        if (BuildConfig.LOGV) Log.v(tag, MessageFormat.format(messagePattern,
                messageArgs));
    }

    /**
     * Toast log at Debug level.
     *
     * @param context Context from which the Toast is shown
     * @param messagePattern Log message in MessageFormat pattern format
     * @param messageArgs Optional arguments for the messagePattern pattern
     */
    public static void td(Context context, String messagePattern,
                          Object... messageArgs) {
        if (BuildConfig.TOASTD) Toast.makeText(context,
                MessageFormat.format(messagePattern, messageArgs),
                Toast.LENGTH_LONG).show();
    }

}
