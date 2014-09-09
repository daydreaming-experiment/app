package com.brainydroid.daydreaming.ui.sequences;

import android.content.Context;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.MultipleChoiceQuestionDescriptionDetails;
import com.brainydroid.daydreaming.sequence.MultipleChoiceAnswer;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.util.ArrayList;

import roboguice.inject.InjectResource;

@SuppressWarnings("UnusedDeclaration")
public class MultipleChoiceQuestionViewAdapter
        extends BaseQuestionViewAdapter implements IQuestionViewAdapter {

    private static String TAG = "MultipleChoiceQuestionViewAdapter";

    @Inject private ArrayList<LinearLayout> regularChoiceLayouts;
    private LinearLayout otherChoiceLayout;

    @InjectResource(R.string.questionMultipleChoice_other_please_fill)
    String errorFillOther;
    @InjectResource(R.string.questionMultipleChoice_please_check_one)
    String errorCheckOne;
    @Inject Context context;
    @Inject MultipleChoiceAnswer answer;
    @Inject Injector injector;

    @Override
    protected ArrayList<View> inflateViews(LinearLayout questionLayout) {
        Logger.d(TAG, "Inflating question views");

        MultipleChoiceQuestionDescriptionDetails details =
                (MultipleChoiceQuestionDescriptionDetails)question.getDetails();
        ArrayList<String> choices = details.getChoices();
        LinearLayout choicesView = (LinearLayout)layoutInflater.inflate(
                R.layout.question_multiple_choice, questionLayout, false);

        TextView qText = (TextView)choicesView.findViewById(
                R.id.question_multiple_choice_mainText);
        String initial_qText = details.getText();
        qText.setText(getExtendedQuestionText(initial_qText));
        qText.setMovementMethod(LinkMovementMethod.getInstance());

        final CheckBox otherCheck = (CheckBox)choicesView.findViewById(
                R.id.question_multiple_choice_otherCheckBox);
        final EditText otherEdit = (EditText)choicesView.findViewById(
                R.id.question_multiple_choice_otherEditText);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            otherEdit.setTextColor(context.getResources().getColor(
                    R.color.ui_dark_blue_color));
        }

        CompoundButton.OnCheckedChangeListener otherCheckListener =
                new CompoundButton.OnCheckedChangeListener() {

            @Inject InputMethodManager inputMethodManager;

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    Logger.v(TAG, "Other checked -> requesting focus");
                    otherEdit.requestFocus();
                    inputMethodManager.showSoftInput(otherEdit, 0);
                } else {
                    Logger.v(TAG, "Other unchecked -> releasing focus and " +
                            "emptying field");
                    ((LinearLayout)otherEdit.getParent()).requestFocus();
                    otherEdit.setText("");
                    inputMethodManager.hideSoftInputFromWindow(
                            otherEdit.getApplicationWindowToken(), 0);
                }
            }

        };

        injector.injectMembers(otherCheckListener);

        View.OnClickListener otherEditClickListener =
                new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Logger.v(TAG, "Other clicked -> checking the option");
                otherCheck.setChecked(true);
            }

        };

        View.OnFocusChangeListener otherEditFocusListener =
                new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Logger.v(TAG, "Other received focus -> checking the " +
                            "option");
                    otherCheck.setChecked(true);
                }
            }
        };

        View.OnKeyListener onSoftKeyboardDonePress =
                new View.OnKeyListener() {

            @Inject InputMethodManager inputMethodManager;

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    Logger.v(TAG, "Other received enter key -> hiding soft " +
                            "keyboard");
                    inputMethodManager.hideSoftInputFromWindow(
                            otherEdit.getApplicationWindowToken(), 0);
                }
                return false;
            }
        };

        injector.injectMembers(onSoftKeyboardDonePress);

        otherCheck.setOnCheckedChangeListener(otherCheckListener);
        otherEdit.setOnClickListener(otherEditClickListener);
        otherEdit.setOnFocusChangeListener(otherEditFocusListener);
        otherEdit.setOnKeyListener(onSoftKeyboardDonePress);

        LinearLayout checksLayout = (LinearLayout)choicesView.findViewById(
                R.id.question_multiple_choice_rootChoices);

        for (String choice : choices) {
            Logger.v(TAG, "Inflating choice {0}", choice);
            LinearLayout checkBoxLayout = (LinearLayout)layoutInflater.inflate(
                    R.layout.question_multiple_choice_item, checksLayout, false);
            TextView tv = (TextView)checkBoxLayout.findViewById(R.id.question_multiple_choice_checkBox_text);
            tv.setText(choice);

            LinearLayout.OnClickListener checkboxClickListener = new LinearLayout.OnClickListener(){
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox)v.findViewById(R.id.question_multiple_choice_checkBox);
                        checkBox.setChecked(!checkBox.isChecked());
                }
            };
            checkBoxLayout.setOnClickListener(checkboxClickListener);
            checkBoxLayout.setClickable(true);
            checksLayout.addView(checkBoxLayout);
            regularChoiceLayouts.add(checkBoxLayout);
        }
        otherChoiceLayout = (LinearLayout)choicesView.findViewById(
                R.id.question_multiple_choice_other);

        ArrayList<View> views = new ArrayList<View>();
        views.add(choicesView);

        return views;
    }

    @Override
    public boolean validate() {
        Logger.i(TAG, "Validating choices");

        boolean hasCheck = false;
        for (LinearLayout regularChoiceLayout : regularChoiceLayouts) {
            CheckBox checkbox = (CheckBox)regularChoiceLayout.findViewById(
                    R.id.question_multiple_choice_checkBox);

            if (checkbox.isChecked()) {
                Logger.v(TAG, "At least one regular option is checked");
                hasCheck = true;
                break;
            }
        }

        CheckBox otherCheck = (CheckBox)otherChoiceLayout.findViewById(
                R.id.question_multiple_choice_otherCheckBox);
        boolean hasOtherCheck = otherCheck.isChecked();

        if (hasOtherCheck) {
            EditText otherEditText = (EditText)otherChoiceLayout.findViewById(
                    R.id.question_multiple_choice_otherEditText);

            if (otherEditText.getText().length() == 0) {
                Logger.v(TAG, "Other is checked but has no text");
                Toast.makeText(context, errorFillOther,
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Logger.v(TAG, "Other is checked and has text");
            }
        }

        if (!hasCheck && !hasOtherCheck) {
            Logger.v(TAG, "Nothing checked");
            Toast.makeText(context, errorCheckOne,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void saveAnswer() {
        Logger.i(TAG, "Saving question answer");

        for (LinearLayout regularChoiceLayout : regularChoiceLayouts) {
            CheckBox checkBox = (CheckBox)regularChoiceLayout.findViewById(
                    R.id.question_multiple_choice_checkBox);
            TextView textView = (TextView)regularChoiceLayout.findViewById(
                    R.id.question_multiple_choice_checkBox_text);
            if (checkBox.isChecked()) {
                answer.addChoice(textView.getText().toString());
            }
        }

        // Get the "Other" field
        CheckBox otherCheck = (CheckBox)otherChoiceLayout.findViewById(
                R.id.question_multiple_choice_otherCheckBox);
        if (otherCheck.isChecked()) {
            EditText otherEditText = (EditText)otherChoiceLayout.findViewById(
                    R.id.question_multiple_choice_otherEditText);
            answer.addChoice("Other: " + otherEditText.getText().toString());
        }

        question.setAnswer(answer);
    }

}
