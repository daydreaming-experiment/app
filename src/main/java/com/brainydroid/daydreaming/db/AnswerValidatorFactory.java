package com.brainydroid.daydreaming.db;

import android.widget.LinearLayout;

public interface AnswerValidatorFactory {

    AnswerValidator create(Question question, LinearLayout questionLinearLayout);

}
