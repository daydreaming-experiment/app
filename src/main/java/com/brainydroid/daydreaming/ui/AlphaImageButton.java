package com.brainydroid.daydreaming.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import com.brainydroid.daydreaming.background.Logger;

public class AlphaImageButton extends ImageButton {

    private static String TAG = "AlphaImageButton";

    public AlphaImageButton(Context context) {
        super(context);
    }

    public AlphaImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaImageButton(Context context, AttributeSet attrs, int defStyle) {
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
