package com.brainydroid.daydreaming.ui.questions;

import android.widget.LinearLayout;

import com.brainydroid.daydreaming.sequence.Page;

public class PageViewAdapter {

    public void setAdapters(Page page, LinearLayout layout);

    public void inflate(boolean isFirstQuestion);

    public boolean validate();

    public void saveAnswers();

}
