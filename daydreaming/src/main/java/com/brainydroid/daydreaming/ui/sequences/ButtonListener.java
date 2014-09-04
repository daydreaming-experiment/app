package com.brainydroid.daydreaming.ui.sequences;

import com.brainydroid.daydreaming.ui.firstlaunchsequence.ScrollViewExt;

public interface ButtonListener {
    void onScrollChanged(ScrollViewExt scrollView,
                         int x, int y, int oldx, int oldy);
}