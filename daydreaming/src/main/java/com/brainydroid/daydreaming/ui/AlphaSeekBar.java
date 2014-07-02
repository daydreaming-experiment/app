package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.widget.SeekBar;

import com.brainydroid.daydreaming.background.Logger;

public class AlphaSeekBar extends SeekBar {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "AlphaSeekBar";

    @SuppressWarnings("UnusedDeclaration")
    public AlphaSeekBar(Context context) {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public AlphaSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public AlphaSeekBar(Context context, AttributeSet attrs, int defStyle) {
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

    static public abstract class OnAlphaSeekBarChangeListener implements
            OnSeekBarChangeListener {

        public abstract void onProgressChanged(AlphaSeekBar seekBar, int progress, boolean fromUser);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            onProgressChanged((AlphaSeekBar)seekBar, progress, fromUser);
        }

        public abstract void onStartTrackingTouch(AlphaSeekBar seekBar);

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            onStartTrackingTouch((AlphaSeekBar) seekBar);
        }

        public abstract void onStopTrackingTouch(AlphaSeekBar seekBar);

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            onStopTrackingTouch((AlphaSeekBar) seekBar);
        }

    }

}
