package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.db.IQuestionDescriptionDetails;

abstract public class AbstractQuestion {

    private static String TAG = "AbstractQuestion";

    abstract public IQuestionDescriptionDetails getDetails();
}
