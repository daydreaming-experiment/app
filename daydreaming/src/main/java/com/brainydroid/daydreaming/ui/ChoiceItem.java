package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;

public class ChoiceItem extends LinearLayout {

    private static String TAG = "ChoiceItem";

    private boolean isChecked = false;
    private ImageButton imageButton;
    private TextView textView;

    public ChoiceItem(Context context) {
        super(context);
    }

    public ChoiceItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(11)
    public ChoiceItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // TODO: add image here once we have the icons
    public void initialize(String text) {
        Logger.v(TAG, "Initializing");
        imageButton = (ImageButton)findViewById(R.id.question_matrix_choice_item_image);
        textView = (TextView)findViewById(R.id.question_matrix_choice_item_text);
        textView.setText(text);

        setClickable(true);
        imageButton.setClickable(false);
    }

    public boolean isChecked() {
        return isChecked;
    }

    private void setChecked(boolean isChecked) {
        Logger.v(TAG, "Setting checked to " + (isChecked ? "true" : "false"));

        this.isChecked = isChecked;
        imageButton.setBackgroundResource(isChecked ?
                R.drawable.choice_item_image_selected_selector :
                R.drawable.choice_item_image_selector);
        textView.setTextColor(isChecked ?
                getResources().getColorStateList(R.color.choice_item_text_selected_selector) :
                getResources().getColorStateList(R.color.choice_item_text_selector));
    }

    public void toggleChecked() {
        Logger.v(TAG, "Toggling checked");
        setChecked(!isChecked);
    }

    public String getText() {
        return textView.getText().toString();
    }
}
