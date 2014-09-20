package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
    private int drawableSelector;
    private int drawableChecked;


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
        setDrawableSelector(text);
        setDrawableChecked(text);
        textView = (TextView)findViewById(R.id.question_matrix_choice_item_text);
        textView.setText(text);
        setClickable(true);
        imageButton.setBackgroundResource(drawableSelector);
        textView.setTextColor(getResources().getColorStateList(R.color.choice_item_text_selector));
        imageButton.setClickable(false);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        Logger.v(TAG, "Setting checked to " + (isChecked ? "true" : "false"));

        this.isChecked = isChecked;
        imageButton.setBackgroundResource(isChecked ? drawableChecked : drawableSelector);
        textView.setTextColor(isChecked ?
                getResources().getColorStateList(R.color.choice_item_text_selected_selector):
                getResources().getColorStateList(R.color.choice_item_text_selector));
    }

    public void toggleChecked() {
        Logger.v(TAG, "Toggling checked");
        setChecked(!isChecked);
    }

    public String getText() {
        return textView.getText().toString();
    }

    public synchronized void setIcon(Drawable drawable) {
        imageButton.setImageDrawable(drawable);
    }

    public synchronized void setDrawableSelector(String choice) {
        Logger.d(TAG, "Loading icon : {}", choice);
        if (choice.equals("Home")) {
            //drawableSelector = context.getResources().getDrawable(R.drawable.button_location_home_selector);
            drawableSelector = R.drawable.button_location_home_selector;
        } else if (choice.equals("Commuting")) {
            //drawableSelector = context.getResources().getDrawable(R.drawable.button_location_commuting_selector);
            drawableSelector = R.drawable.button_location_commuting_selector;
        } else if (choice.equals("Work")) {
            //drawableSelector = context.getResources().getDrawable(R.drawable.button_location_work_selector);
            drawableSelector = R.drawable.button_location_work_selector;
        } else if (choice.equals("Public place")) {
            //drawableSelector = context.getResources().getDrawable(R.drawable.button_location_publicplace_selector);
            drawableSelector = R.drawable.button_location_publicplace_selector;
        } else if (choice.equals("Outside")) {
            //drawableSelector = context.getResources().getDrawable(R.drawable.button_location_outside_selector);
            drawableSelector = R.drawable.button_location_outside_selector;
        }
    }

    public synchronized void setDrawableChecked(String choice) {
        Logger.d(TAG, "Loading icon : {}", choice);
        if (choice.equals("Home")) {
            //drawableChecked = context.getResources().getDrawable(R.drawable.button_location_home_yellow);
            drawableChecked = R.drawable.button_location_home_yellow;
        } else if (choice.equals("Commuting")) {
            //drawableChecked = context.getResources().getDrawable(R.drawable.button_location_commuting_yellow);
            drawableChecked = R.drawable.button_location_commuting_yellow;
        } else if (choice.equals("Work")) {
            //drawableChecked = context.getResources().getDrawable(R.drawable.button_location_work_yellow);
            drawableChecked = R.drawable.button_location_work_yellow;
        } else if (choice.equals("Public place")) {
            //drawableChecked = context.getResources().getDrawable(R.drawable.button_location_publicplace_yellow);
            drawableChecked = R.drawable.button_location_publicplace_yellow;
        } else if (choice.equals("Outside")) {
            //drawableChecked = context.getResources().getDrawable(R.drawable.button_location_outside_yellow);
            drawableChecked = R.drawable.button_location_outside_yellow;
        }
    }

    public boolean getChecked() {
        return isChecked;
    }
}
