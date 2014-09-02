package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;

public class SequenceDescriptionsArray extends ArrayList<SequenceDescription> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SequenceDescriptionsArray";

    public void validateInitialization() {
        Logger.v(TAG, "Validating initialization");

        if (size() == 0) {
            throw new JsonParametersException("SequenceDescriptionsArray can't be empty");
        }
        for (SequenceDescription s : this) {
            s.validateInitialization();
        }
    }
}
