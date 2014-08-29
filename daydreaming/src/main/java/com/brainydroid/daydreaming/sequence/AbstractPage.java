package com.brainydroid.daydreaming.sequence;

import java.util.ArrayList;

abstract public class AbstractPage {

    private static String TAG = "AbstractPage";

    abstract public ArrayList<? extends AbstractQuestion> getQuestions();

}
