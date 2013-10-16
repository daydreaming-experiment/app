package com.brainydroid.daydreaming.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import com.brainydroid.daydreaming.background.Logger;

public class AlphaLinearLayout extends LinearLayout {

    private static String TAG = "AlphaLinearLayout";

    public AlphaLinearLayout(Context context) {
        super(context);
    }

    public AlphaLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

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
