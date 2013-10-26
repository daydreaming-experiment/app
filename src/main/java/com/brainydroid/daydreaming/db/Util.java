package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

@Singleton
public class Util {

    private static String TAG = "Util";

    /** Format of date and time for logging */
    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // Date and time formatter for logging
    private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    @Inject Random random;

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

    public static String joinStrings(String[] strings, String joinString) {
        Logger.v(TAG, "Joining strings");

        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string);
            sb.append(joinString);
        }

        int sbLength = sb.length();
        sb.delete(sbLength - joinString.length(), sbLength);
        return sb.toString();
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
        Logger.v(TAG, "Sampling {} items in ArrayList", nSamples);

        int size = list.size();
        ArrayList<T> samples = new ArrayList<T>();

        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            indices.add(i);
        }

        for (int used = 0; used < nSamples; used++) {
            samples.add(list.get(indices.get(random.nextInt(size))));
        }

        return samples;
    }

    public <T> ArrayList<T> shuffle(ArrayList<T> list) {
        Logger.v(TAG, "Shuffling list");
        return sample(list, list.size());
    }

}
