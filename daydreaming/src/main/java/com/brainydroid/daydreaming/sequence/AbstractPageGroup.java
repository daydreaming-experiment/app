package com.brainydroid.daydreaming.sequence;

import java.util.ArrayList;

abstract public class AbstractPageGroup {
    
    private static String TAG = "AbstractPageGroup";

    abstract public String getFriendlyName();

    abstract public ArrayList<? extends AbstractPage> getPages();

}
