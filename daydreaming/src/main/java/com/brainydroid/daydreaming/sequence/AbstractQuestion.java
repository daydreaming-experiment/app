package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.db.IQuestionDetails;

abstract public class AbstractQuestion {

    private static String TAG = "AbstractQuestion";

    abstract public String getName();

    abstract public String getPosition();

    abstract public IQuestionDetails getDetails();
}
