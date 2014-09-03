package com.brainydroid.daydreaming.ui.sequences;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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
    @InjectView(R.id.question_layout_base) LinearLayout layout;

    public  BaseQuestionViewAdapter(Question question) {
        this.question = question;
    }

    public LinearLayout inflate() {
        Logger.d(TAG, "Inflating question view");

        int index = 0;
        ArrayList<View> views = inflateViews();

        for (View view : views) {
            layout.addView(view, index, layout.getLayoutParams());
            index++;
        }

        return layout;
    }

    protected abstract ArrayList<View> inflateViews();

}
