package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;

public class ChoiceItem extends LinearLayout {

    private static String TAG = "ChoiceItem";

    private boolean isChecked = false;
    private ImageView imageView;
    private TextView textView;
    private int drawableUnchecked;
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

    public void initialize(String text) {
        Logger.v(TAG, "Initializing");
        imageView = (ImageView)findViewById(R.id.question_matrix_choice_item_image);
        setDrawableUnchecked(text);
        setDrawableChecked(text);
        textView = (TextView)findViewById(R.id.question_matrix_choice_item_text);
        textView.setText(text);
        isChecked = false;
        updateDrawable();
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void swapStatus() {
        Logger.v(TAG, "Setting checked to " + (!isChecked ? "true" : "false"));
        isChecked = !isChecked;
        updateDrawable();
    }

    public void updateDrawable() {
        imageView.setBackgroundResource(isChecked ? drawableChecked : drawableUnchecked);
        textView.setTextColor(isChecked ?
                getResources().getColorStateList(R.color.ui_yellow):
                getResources().getColorStateList(R.color.ui_white_text_color));
    }

    public String getText() {
        return textView.getText().toString();
    }


    public synchronized void setDrawableUnchecked(String choice) {
        Logger.d(TAG, "Loading icon : {}", choice);
        if (choice.equals("Home")) {
            drawableUnchecked = R.drawable.button_location_home;
        } else if (choice.equals("Commuting")) {
            drawableUnchecked = R.drawable.button_location_commuting;
        } else if (choice.equals("Work")) {
            drawableUnchecked = R.drawable.button_location_work;
        } else if (choice.equals("Public place")) {
            drawableUnchecked = R.drawable.button_location_publicplace;
        } else if (choice.equals("Outside")) {
            drawableUnchecked = R.drawable.button_location_outside;
        }
    }

    public synchronized void setDrawableChecked(String choice) {
        Logger.d(TAG, "Loading icon : {}", choice);
        if (choice.equals("Home")) {
            drawableChecked = R.drawable.button_location_home_yellow;
        } else if (choice.equals("Commuting")) {
            drawableChecked = R.drawable.button_location_commuting_yellow;
        } else if (choice.equals("Work")) {
            drawableChecked = R.drawable.button_location_work_yellow;
        } else if (choice.equals("Public place")) {
            drawableChecked = R.drawable.button_location_publicplace_yellow;
        } else if (choice.equals("Outside")) {
            drawableChecked = R.drawable.button_location_outside_yellow;
        }
    }

}
