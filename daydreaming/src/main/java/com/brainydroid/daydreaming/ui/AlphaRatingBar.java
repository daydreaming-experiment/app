package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.widget.RatingBar;
import com.brainydroid.daydreaming.background.Logger;

public class AlphaRatingBar extends RatingBar {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "AlphaRatingBar";

    @SuppressWarnings("UnusedDeclaration")
    public AlphaRatingBar(Context context) {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public AlphaRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public AlphaRatingBar(Context context, AttributeSet attrs, int defStyle) {
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

    static public abstract class OnAlphaRatingBarChangeListener implements
            OnRatingBarChangeListener {

        public abstract void onRatingChanged(AlphaRatingBar ratingBar,
                                             float rating, boolean fromUser);

        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating,
                                    boolean fromUser) {
            onRatingChanged((AlphaRatingBar)ratingBar, rating, fromUser);
        }

    }

}
