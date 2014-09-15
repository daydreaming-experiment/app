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

    public static void eRaw(String tag, String message) {
        Log.e(tag, message);
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

    public static void wRaw(String tag, String message) {
        Log.w(tag, message);
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
        if (LoggerConfig.LOGI) Log.i(tag, MessageFormat.format(messagePattern,
                messageArgs));
    }

    public static void iRaw(String tag, String message) {
        if (LoggerConfig.LOGI) Log.i(tag, message);
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
        if (LoggerConfig.LOGD) Log.d(tag, MessageFormat.format(messagePattern,
                messageArgs));
    }

    public static void dRaw(String tag, String message) {
        if (LoggerConfig.LOGD) Log.d(tag, message);
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
        if (LoggerConfig.LOGV) Log.v(tag, MessageFormat.format(messagePattern,
                messageArgs));
    }

    public static void vRaw(String tag, String message) {
        if (LoggerConfig.LOGV) Log.v(tag, message);
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
        if (LoggerConfig.TOASTD) Toast.makeText(context,
                MessageFormat.format(messagePattern, messageArgs),
                Toast.LENGTH_LONG).show();
    }

    public static void tdRaw(Context context, String message) {
        if (LoggerConfig.TOASTD) Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
