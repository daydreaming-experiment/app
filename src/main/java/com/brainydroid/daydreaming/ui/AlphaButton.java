package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import com.brainydroid.daydreaming.background.Logger;

public class AlphaButton extends Button {

    private static String TAG = "AlphaButton";

    public AlphaButton(Context context) {
        super(context);
    }

    public AlphaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void customSetAlpha(float alpha) {
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
