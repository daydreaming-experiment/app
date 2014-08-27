package com.brainydroid.daydreaming.db;

/**
 * Created by vincent on 25/08/14.
 */
public class TipiQuestion {
    // class is a literal implementation of object structure in the grammar-v2.1 definition

    private static String TAG = "TipiQuestion";

    private String text = "";
    private Integer initialPosition = 50;

    public static final String TIPI_QUESTION_CATEGORY = "TipiQuestion";
    public static final String TIPI_QUESTION_SUBCATEGORY = "";

    public synchronized String getText(){
        return text;
    }

    public synchronized int getInitialPosition(){
        return initialPosition;
    }


}
