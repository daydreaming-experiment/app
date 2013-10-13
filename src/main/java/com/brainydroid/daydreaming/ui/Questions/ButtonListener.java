package com.brainydroid.daydreaming.ui.Questions;

import com.brainydroid.daydreaming.ui.FirstLaunchSequence.ScrollViewExt;

public interface ButtonListener {
    void onScrollChanged(ScrollViewExt scrollView,
                         int x, int y, int oldx, int oldy);
}