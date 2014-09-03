package com.brainydroid.daydreaming.ui.sequences;

import android.widget.LinearLayout;

import com.brainydroid.daydreaming.sequence.Question;

public interface IQuestionViewAdapter {

    public void setQuestion(Question question);

    public LinearLayout inflate();

    public boolean validate();

    public void saveAnswer();

}
