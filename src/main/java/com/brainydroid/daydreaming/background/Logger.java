package com.brainydroid.daydreaming.background;

import android.util.Log;
import com.brainydroid.daydreaming.db.Util;
import com.brainydroid.daydreaming.ui.Config;

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
     * Log at Warn level.
     *
     * @param tag Tag to attach to the log message
     * @param message Log message
     */
    public static void w(String tag, String message) {
        Log.w(tag, buildIndent() + message);
    }

    /**
     * Log at Info level.
     *
     * @param tag Tag to attach to the log message
     * @param message Log message
     */
    public static void i(String tag, String message) {
        if (Config.LOGI) Log.i(tag, buildIndent() + message);
    }

    /**
     * Log at Debug level.
     *
     * @param tag Tag to attach to the log message
     * @param message Log message
     */
    public static void d(String tag, String message) {
        if (Config.LOGD) Log.d(tag, buildIndent() + message);
    }

    /**
     * Log at Verbose level.
     *
     * @param tag Tag to attach to the log message
     * @param message Log message
     */
    public static void v(String tag, String message) {
        if (Config.LOGV) Log.v(tag, buildIndent() + message);
    }

}
