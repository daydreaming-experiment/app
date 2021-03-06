package com.brainydroid.daydreaming.db;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

@Singleton
public class Util {

    private static String TAG = "Util";

    /** Format of date and time for logging */
    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // Date and time formatter for logging
    private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    @Inject Random random;

    public static int dpToPx(Context context, int dp) {
        Resources resources = context.getResources();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                resources.getDisplayMetrics());
    }

    public static String multiplyString(String string, int times, String joinString) {
        Logger.v(TAG, "Multiplying strings");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(string);
            sb.append(joinString);
        }

        int sbLength = sb.length();
        sb.delete(sbLength - joinString.length(), sbLength);
        return sb.toString();
    }

    public static String joinStrings(Collection<?> s, String delimiter) {
        Logger.v(TAG, "Joining strings");

        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = s.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (!iterator.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }

    public static String joinStrings(String[] strings, String joinString) {
        return joinStrings(new ArrayList<String>(Arrays.asList(strings)), joinString);
    }

    public static String[] concatenateStringArrays(String[]... jobs) {
        int len = 0;
        for (final String[] job : jobs) {
            len += job.length;
        }

        final String[] result = new String[len];

        int currentPos = 0;
        for (final String[] job : jobs) {
            System.arraycopy(job, 0, result, currentPos, job.length);
            currentPos += job.length;
        }

        return result;
    }

    public static String convertStreamToString(InputStream is) {
        Logger.v(TAG, "Converting InputStream to String");

        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

    // hour from string HH:MM
    public static int getHour(String time) {
        Logger.v(TAG, "Getting hour value");
        String[] pieces = time.split(":");
        return(Integer.parseInt(pieces[0]));
    }

    // minutes from string HH:MM
    public static int getMinute(String time) {
        Logger.v(TAG, "Getting minute value");
        String[] pieces = time.split(":");
        return(Integer.parseInt(pieces[1]));
    }

    public static String formatDate(Date date) {
        Logger.v(TAG, "Formatting date");
        return sdf.format(date);
    }

    public <T> ArrayList<T> sample(ArrayList<T> list, int nSamples) {
        int size = list.size();
        Logger.v(TAG, "Sampling {0} distinct items in list (size {1})",
                nSamples, size);

        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, random);

        ArrayList<T> samples = new ArrayList<T>();
        for (int i = 0; i < nSamples; i++) {
            samples.add(list.get(indices.get(i)));
        }

        return samples;
    }

    public <T> void shuffle(ArrayList<T> list) {
        Logger.v(TAG, "Shuffling list (size {})", list.size());
        Collections.shuffle(list, random);
    }

}
