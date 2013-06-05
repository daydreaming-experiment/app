package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.brainydroid.daydreaming.db.Util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Log required information if the application configuration requires so.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public class Logger {

    @SuppressWarnings("FieldCanBeLocal")
    private static String INDENT = "   ";

    public static boolean TOASTD = true;
    public static boolean LOGI = true;
    public static boolean LOGD = true;
    public static boolean LOGV = true;

    // Map of (thread, depth of stack) for log formatting
    private static Map<String,Integer> threadMap =
            new HashMap<String, Integer>();

    private static String buildIndent() {
        String threadName = Thread.currentThread().getName();
        if(threadMap.get(threadName) == null) threadMap.put(threadName, 0);

        int stackDepth = threadMap.get(threadName) + 1;
        threadMap.put(threadName, stackDepth);

        return Util.multiplyString(INDENT, stackDepth);
    }

    /**
     * Log at Error level.
     *
     * @param tag Tag to attach to the log message
     * @param messagePattern Log message in MessageFormat pattern format
     * @param messageArgs Optional arguments for the messagePattern pattern
     */
    public static void e(String tag, String messagePattern,
                         Object... messageArgs) {
        Log.e(tag, buildIndent() + MessageFormat.format(messagePattern,
                messageArgs));
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
        Log.w(tag, buildIndent() + MessageFormat.format(messagePattern,
                messageArgs));
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
        if (LOGI) Log.i(tag, buildIndent() +
                MessageFormat.format(messagePattern, messageArgs));
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
        if (LOGD) Log.d(tag, buildIndent() +
                MessageFormat.format(messagePattern, messageArgs));
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
        if (LOGV) Log.v(tag, buildIndent() +
                MessageFormat.format(messagePattern, messageArgs));
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
        if (TOASTD) Toast.makeText(context,
                MessageFormat.format(messagePattern, messageArgs),
                Toast.LENGTH_LONG).show();
    }

}
