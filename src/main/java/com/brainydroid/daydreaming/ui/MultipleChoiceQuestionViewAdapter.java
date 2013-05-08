package com.brainydroid.daydreaming.ui;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.MultipleChoiceAnswer;
import com.brainydroid.daydreaming.db.MultipleChoiceQuestionDetails;
import com.google.inject.Inject;
import roboguice.inject.InjectResource;

import java.util.ArrayList;

@SuppressWarnings("UnusedDeclaration")
public class MultipleChoiceQuestionViewAdapter
        extends BaseQuestionViewAdapter implements IQuestionViewAdapter {

    private static String TAG = "MultipleChoiceQuestionViewAdapter";

    private View choicesView;

    @InjectResource(R.string.questionMultipleChoice_other_please_fill)
    String errorFillOther;
    @InjectResource(R.string.questionMultipleChoice_please_check_one)
    String errorCheckOne;
    @Inject Context context;
    @Inject MultipleChoiceAnswer answer;

    @Override
    protected ArrayList<View> inflateViews() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] inflateViews");
        }

        MultipleChoiceQuestionDetails details =
                (MultipleChoiceQuestionDetails)question.getDetails();
        ArrayList<String> choices = details.getChoices();
        choicesView = layoutInflater.inflate(
                R.layout.question_multiple_choice, null);

        TextView qText = (TextView)choicesView.findViewById(
                R.id.question_multiple_choice_mainText);
        qText.setText(details.getText());

        final CheckBox otherCheck = (CheckBox)choicesView.findViewById(
                R.id.question_multiple_choice_otherCheckBox);
        final EditText otherEdit = (EditText)choicesView.findViewById(
                R.id.question_multiple_choice_otherEditText);

        CompoundButton.OnCheckedChangeListener otherCheckListener =
                new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    otherEdit.requestFocus();
                } else {
                    ((LinearLayout)otherEdit.getParent()).requestFocus();
                    otherEdit.setText("");
                }
            }

        };

        View.OnClickListener otherEditClickListener =
                new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                otherCheck.setChecked(true);
            }

        };

        View.OnFocusChangeListener otherEditFocusListener =
                new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    otherCheck.setChecked(true);
                }
            }
        };

        View.OnKeyListener onSoftKeyboardDonePress =
                new View.OnKeyListener() {

            @Inject
            InputMethodManager inputMethodManager;

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    inputMethodManager.hideSoftInputFromWindow(
                            otherEdit.getApplicationWindowToken(), 0);
                }
                return false;
            }
        };

        otherCheck.setOnCheckedChangeListener(otherCheckListener);
        otherEdit.setOnClickListener(otherEditClickListener);
        otherEdit.setOnFocusChangeListener(otherEditFocusListener);
        otherEdit.setOnKeyListener(onSoftKeyboardDonePress);

        LinearLayout checksLayout = (LinearLayout)choicesView.findViewById(
                R.id.question_multiple_choice_rootChoices);

        for (String choice : choices) {
            CheckBox checkBox = (CheckBox)layoutInflater.inflate(
                    R.layout.question_multiple_choice_item, null);
            checkBox.setText(choice);
            checksLayout.addView(checkBox);
        }

        ArrayList<View> views = new ArrayList<View>();
        views.add(choicesView);

        return views;
    }

    @Override
    public boolean validate() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] validate");
        }

        LinearLayout rootChoices = (LinearLayout)choicesView.findViewById(
                R.id.question_multiple_choice_rootChoices);

        int childCount = rootChoices.getChildCount();
        boolean hasCheck = false;

        for (int i = 0; i < childCount; i++) {
            CheckBox child = (CheckBox)rootChoices.getChildAt(i);

            if (child.isChecked()) {
                hasCheck = true;
                break;
            }
        }

        CheckBox otherCheck = (CheckBox)choicesView.findViewById(
                R.id.question_multiple_choice_otherCheckBox);
        boolean hasOtherCheck = otherCheck.isChecked();

        if (hasOtherCheck) {
            EditText otherEditText = (EditText)choicesView.findViewById(
                    R.id.question_multiple_choice_otherEditText);

            if (otherEditText.getText().length() == 0) {
                Toast.makeText(context, errorFillOther,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (!hasCheck && !hasOtherCheck) {
            Toast.makeText(context, errorCheckOne,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void saveAnswer() {

        //Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] saveAnswer");
        }

        LinearLayout rootChoices = (LinearLayout)choicesView.findViewById(
                R.id.question_multiple_choice_rootChoices);
        int childCount = rootChoices.getChildCount();

        // Get choices in a list
        for (int i = 0; i < childCount; i++) {
            CheckBox child = (CheckBox)rootChoices.getChildAt(i);
            if (child.isChecked()) {
                answer.addChoice(child.getText().toString());
            }
        }

        // Get the "Other" field
        CheckBox otherCheck = (CheckBox)choicesView.findViewById(
                R.id.question_multiple_choice_otherCheckBox);
        if (otherCheck.isChecked()) {
            EditText otherEditText = (EditText)choicesView.findViewById(
                    R.id.question_multiple_choice_otherEditText);
            answer.addChoice("Other: " + otherEditText.getText().toString());
        }

        question.setAnswer(answer);
    }

}
