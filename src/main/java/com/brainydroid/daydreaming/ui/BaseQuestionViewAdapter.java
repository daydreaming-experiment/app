package com.brainydroid.daydreaming.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Question;
import com.google.inject.Inject;

import java.util.ArrayList;

public abstract class BaseQuestionViewAdapter
        implements IQuestionViewAdapter {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionViewAdapter";

    protected Question question;
    protected LinearLayout layout;

    @Inject LayoutInflater layoutInflater;

    public void setAdapters(Question question, LinearLayout layout) {
        this.question = question;
        this.layout = layout;
    }

    public void inflate(boolean isFirstQuestion) {
        Logger.d(TAG, "Inflating question view");

        int index = isFirstQuestion ? 1 : 0;
        ArrayList<View> views = inflateViews();

        for (View view : views) {
            layout.addView(view, index, layout.getLayoutParams());
            index++;
        }
    }

    protected abstract ArrayList<View> inflateViews();

}
