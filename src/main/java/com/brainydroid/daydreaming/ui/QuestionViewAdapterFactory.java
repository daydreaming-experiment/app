package com.brainydroid.daydreaming.ui;

import android.widget.LinearLayout;
import com.brainydroid.daydreaming.db.BaseQuestion;

public interface QuestionViewAdapterFactory {

    public QuestionViewAdapter create(BaseQuestion question, LinearLayout questionLinearLayout);

}
