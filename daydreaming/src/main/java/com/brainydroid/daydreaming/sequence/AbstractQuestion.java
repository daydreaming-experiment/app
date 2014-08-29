package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.db.IQuestionDetails;

abstract public class AbstractQuestion {

    private static String TAG = "AbstractQuestion";

    abstract public IQuestionDetails getDetails();
}
