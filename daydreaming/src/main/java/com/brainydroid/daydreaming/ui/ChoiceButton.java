package com.brainydroid.daydreaming.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;

public class ChoiceButton extends ImageButton {

    private static String TAG = "ChoiceButton";

    private boolean isChecked = false;

    public ChoiceButton(Context context) {
        super(context);
    }

    public ChoiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChoiceButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isChecked() {
        return isChecked;
    }

    private void setChecked(boolean isChecked) {
        Logger.v(TAG, "Setting checked to " + (isChecked ? "true" : "false"));
        this.isChecked = isChecked;
        setBackgroundResource(isChecked ?
                R.drawable.button_cloud_test_item_selected_selector :
                R.drawable.button_cloud_test_item_selector);
    }

    public void toggleChecked() {
        Logger.v(TAG, "Toggling checked");
        setChecked(!isChecked);
    }
}
