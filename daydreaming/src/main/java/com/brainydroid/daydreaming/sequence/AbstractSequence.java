package com.brainydroid.daydreaming.sequence;

import java.util.ArrayList;

abstract public class AbstractSequence {

    private static String TAG = "AbstractSequence";

    abstract public ArrayList<? extends AbstractPageGroup> getPageGroups();

}
