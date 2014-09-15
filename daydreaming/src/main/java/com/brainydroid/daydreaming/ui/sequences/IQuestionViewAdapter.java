package com.brainydroid.daydreaming.ui.sequences;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.brainydroid.daydreaming.sequence.Question;

public interface IQuestionViewAdapter {

    public void setQuestion(Question question);

    public LinearLayout inflate(Activity activity, RelativeLayout outerPageLayout,
                                LinearLayout parentLayout);

    public boolean validate();

    public void saveAnswer();

}
