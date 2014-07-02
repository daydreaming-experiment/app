package com.brainydroid.daydreaming.ui.questions;

import android.widget.LinearLayout;
import com.brainydroid.daydreaming.db.Question;

public interface IQuestionViewAdapter {

    public void setAdapters(Question question, LinearLayout layout);

    public void inflate(boolean isFirstQuestion);

    public boolean validate();

    public void saveAnswer();

}
