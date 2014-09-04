package com.brainydroid.daydreaming.ui.sequences;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.Question;
import com.google.inject.Inject;

import java.util.ArrayList;

import roboguice.inject.InjectView;

public abstract class BaseQuestionViewAdapter
        implements IQuestionViewAdapter {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionViewAdapter";

    public static String QUESTION_VIEW_ADAPTER_SUFFIX = "QuestionViewAdapter";

    protected Question question;

    @Inject Context context;
    @Inject LayoutInflater layoutInflater;

    public void setQuestion(Question question) {
        this.question = question;
    }

    public LinearLayout inflate(ViewGroup.LayoutParams layoutParams) {
        Logger.d(TAG, "Inflating question view");

        int index = 0;
        LinearLayout layout = (LinearLayout)layoutInflater.inflate(R.layout.question_layout, null);
        ArrayList<View> views = inflateViews();

        for (View view : views) {
            layout.addView(view, index, layoutParams);
            index++;
        }

        return layout;
    }

    protected abstract ArrayList<View> inflateViews();

}
