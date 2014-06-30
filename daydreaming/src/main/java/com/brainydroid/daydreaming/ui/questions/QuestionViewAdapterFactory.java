package com.brainydroid.daydreaming.ui.questions;

import android.widget.LinearLayout;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Question;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class QuestionViewAdapterFactory {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionViewAdapterFactory";

    @SuppressWarnings("FieldCanBeLocal")
    private static String QUESTION_VIEW_ADAPTER_SUFFIX =
            "QuestionViewAdapter";

    @Inject Injector injector;

    public IQuestionViewAdapter create(Question question,
                                       LinearLayout layout) {
        String PACKAGE_PREFIX = getClass().getPackage().getName() + ".";
        String className = PACKAGE_PREFIX + question.getDetails().getType() +
                QUESTION_VIEW_ADAPTER_SUFFIX;
        Logger.v(TAG, "Creating new QuestionViewAdapter: {0}", className);

        try {
            Class klass = Class.forName(className);
            IQuestionViewAdapter questionViewAdapter =
                    (IQuestionViewAdapter)klass.newInstance();
            injector.injectMembers(questionViewAdapter);
            questionViewAdapter.setAdapters(question, layout);
            return questionViewAdapter;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class " + className + " was not " +
                    "found");
        } catch (Exception e) {
            throw new RuntimeException("Class " + className + " could not " +
                    "be instantiated");
        }
    }

}
