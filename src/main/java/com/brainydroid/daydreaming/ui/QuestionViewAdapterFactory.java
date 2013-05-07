package com.brainydroid.daydreaming.ui;

import android.util.Log;
import android.widget.LinearLayout;
import com.brainydroid.daydreaming.db.BaseQuestion;
import com.brainydroid.daydreaming.db.IQuestion;

public class QuestionViewAdapterFactory {

    private static String TAG = "QuestionViewAdapterFactory";

    public IQuestionViewAdapter create(IQuestion question,
                                      LinearLayout layout) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] create");
        }

        String className = question.getClassName() + "ViewAdapter";
        try {
            Class klass = Class.forName(className);
            return (IQuestionViewAdapter)klass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class " + className + " was not " +
                    "found");
        } catch (Exception e) {
            throw new RuntimeException("Class " + className + " could not " +
                    "be instantiated");
        }
    }

}
