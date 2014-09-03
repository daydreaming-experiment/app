package com.brainydroid.daydreaming.ui.sequences;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brainydroid.daydreaming.sequence.Question;

public interface IQuestionViewAdapter {

    public void setQuestion(Question question);

    public LinearLayout inflate(ViewGroup.LayoutParams layoutParams);

    public boolean validate();

    public void saveAnswer();

}
