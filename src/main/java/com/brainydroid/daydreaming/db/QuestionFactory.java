package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;

public class QuestionFactory {

    private String TAG = "QuestionFactory";

    public IQuestion create(String className) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] create");
        }

        try {
            Class klass = Class.forName(className);
            return (IQuestion)klass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class " + className + " was not " +
                    "found");
        } catch (Exception e) {
            throw new RuntimeException("Class " + className + " could not " +
                    "be instantiated");
        }
    }

}
