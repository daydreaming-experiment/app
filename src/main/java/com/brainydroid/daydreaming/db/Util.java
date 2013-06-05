package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

import java.io.InputStream;

public class Util {

    private static String TAG = "Util";

    public static String multiplyString(String string, int times) {
        return multiplyString(string, times, null);
    }

    public static String multiplyString(String string, int times, String joinString) {
        Logger.v(TAG, "Multiplying string");

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

}
