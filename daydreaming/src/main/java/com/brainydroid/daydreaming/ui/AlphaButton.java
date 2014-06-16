package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import com.brainydroid.daydreaming.background.Logger;

public class AlphaButton extends Button {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "AlphaButton";

    @SuppressWarnings("UnusedDeclaration")
    public AlphaButton(Context context) {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public AlphaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public AlphaButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

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
