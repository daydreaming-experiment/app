package com.brainydroid.daydreaming.ui.sequences;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.Page;
import com.brainydroid.daydreaming.sequence.Question;
import com.google.inject.Inject;

import java.util.ArrayList;

public class PageViewAdapter {

    private static String TAG = "PageViewAdapter";

    protected Page page;

    @Inject Context context;
    @Inject ArrayList<View> questionViews;
    @Inject ArrayList<IQuestionViewAdapter> questionViewAdapters;
    @Inject LayoutInflater layoutInflater;

    public void setPage(Page page) {
        this.page = page;
    }

    public void inflate(LinearLayout layout) {
        Logger.d(TAG, "Inflating page view");

        inflateViews(layout.getLayoutParams());

        int index = 0;
        for (View view : questionViews) {
            layout.addView(view, index, layout.getLayoutParams());
            index++;
        }
    }

    private void inflateViews(ViewGroup.LayoutParams layoutParams) {
        Logger.d(TAG, "Inflating question views inside page");

        ArrayList<Question> questions = page.getQuestions();
        IQuestionViewAdapter questionViewAdapter;
        View view;
        for (Question question : questions) {
            questionViewAdapter = question.getAdapter();
            view = questionViewAdapter.inflate(layoutParams);
            questionViews.add(view);
            questionViewAdapters.add(questionViewAdapter);
        }
    }

    public boolean validate() {
        Logger.d(TAG, "Validating page answers");

        boolean ok = true;
        for (IQuestionViewAdapter adapter : questionViewAdapters) {
            ok &= adapter.validate();
        }
        return ok;
    }

    public void saveAnswers() {
        Logger.d(TAG, "Saving page answers");
        for (IQuestionViewAdapter adapter : questionViewAdapters) {
            adapter.saveAnswer();
        }
    }

}