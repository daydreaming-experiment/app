package com.brainydroid.daydreaming.ui;

import android.widget.LinearLayout;
import com.brainydroid.daydreaming.db.IQuestion;

public interface IQuestionViewAdapter {

    public void setAdapters(IQuestion question, LinearLayout layout);

    public void inflate(boolean isFirstQuestion);

    public boolean validate();

    public void saveAnswer();

}
