package com.brainydroid.daydreaming.db;

import android.widget.LinearLayout;

public interface AnswerValidatorFactory {

    public AnswerValidator create(BaseQuestion question, LinearLayout questionLinearLayout);

}
