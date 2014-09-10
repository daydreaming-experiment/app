package com.brainydroid.daydreaming.ui.sequences;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brainydroid.daydreaming.sequence.Question;

public interface IQuestionViewAdapter {

    public void setQuestion(Question question);

    public LinearLayout inflate(LinearLayout parentLayout);

    public boolean validate();

    public void saveAnswer();

}
