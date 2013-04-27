package com.brainydroid.daydreaming.ui;

import android.widget.LinearLayout;
import com.brainydroid.daydreaming.db.Question;

public interface QuestionViewAdapterFactory {

    public QuestionViewAdapter create(Question question, LinearLayout questionLinearLayout);

}
