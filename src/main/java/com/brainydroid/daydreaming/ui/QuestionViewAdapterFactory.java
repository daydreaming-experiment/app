package com.brainydroid.daydreaming.ui;

import android.util.Log;
import android.widget.LinearLayout;
import com.brainydroid.daydreaming.db.Question;

public class QuestionViewAdapterFactory {

    private static String TAG = "QuestionViewAdapterFactory";

    private static String QUESTION_VIEW_ADAPTER_SUFFIX =
            "QuestionViewAdapter";

    public IQuestionViewAdapter create(Question question,
                                       LinearLayout layout) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] create");
        }

        String className = question.getDetails().getType() +
                QUESTION_VIEW_ADAPTER_SUFFIX;
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
