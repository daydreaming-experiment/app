package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import com.brainydroid.daydreaming.background.Logger;

public class AlphaLinearLayout extends LinearLayout {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "AlphaLinearLayout";

    @SuppressWarnings("UnusedDeclaration")
    public AlphaLinearLayout(Context context) {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public AlphaLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Missing public AlphaLinearLayout(Context context, AttributeSet attrs, int defStyle)
    // since it requires API >= 11

    @TargetApi(11)
    @Override
    public void setAlpha(float alpha) {
        Logger.v(TAG, "Setting alpha to {}", alpha);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.setAlpha(alpha);
        } else {
            AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(0);
            animation.setFillAfter(true);
            startAnimation(animation);
        }
    }

}
