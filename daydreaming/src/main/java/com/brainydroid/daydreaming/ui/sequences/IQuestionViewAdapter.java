package com.brainydroid.daydreaming.ui.sequences;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brainydroid.daydreaming.sequence.Question;

public interface IQuestionViewAdapter {

    public void setQuestion(Question question);

    public LinearLayout inflate(Activity activity, LinearLayout parentLayout);

    public boolean validate();

    public void saveAnswer();

}
