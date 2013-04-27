package com.brainydroid.daydreaming.db;

import android.util.Log;
import android.view.View;
import android.widget.*;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.ui.Config;
import com.brainydroid.daydreaming.ui.QuestionViewAdapter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.ArrayList;

public class AnswerValidator {

    private static String TAG = "AnswerValidator";

    private Question question;
    private LinearLayout questionLinearLayout;

    @Inject android.content.Context context;

    @Inject
    public AnswerValidator(@Assisted Question question, @Assisted LinearLayout questionLinearLayout) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] AnswerValidator");
        }

        this.question = question;
        this.questionLinearLayout = questionLinearLayout;
    }

    // --- Validation functions

    public boolean validate() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] validate");
        }

        String type = question.getType();
        if (type == null) {
            throw new RuntimeException("Question type not set");
        } else if (type.equals(Question.TYPE_SLIDER)) {
            return validateSlider(questionLinearLayout);
        } else if (type.equals(Question.TYPE_MULTIPLE_CHOICE)) {
            return validateMultipleChoice(questionLinearLayout);
        } else {
            throw new RuntimeException("Question type not recognized");
        }
    }

    // For slider, checks whether or not sliders were kept untouched
    private boolean validateSlider(LinearLayout questionsLinearLayout) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] validateSlider");
        }

        ArrayList<View> subquestions = QuestionViewAdapter.getViewsByTag(questionsLinearLayout, "subQuestion");
        boolean isMultiple = subquestions.size() > 1;

        for (View subquestion : subquestions) {

            TextView selectedSeek = (TextView)subquestion.findViewById(R.id.question_slider_selectedSeek);

            if (selectedSeek.getText().equals(
                    context.getString(R.string.questionSlider_please_slide))) {

                Toast.makeText(context,
                        context.getString(isMultiple ? R.string.questionSlider_sliders_untouched_multiple :
                                R.string.questionSlider_sliders_untouched_single),
                        Toast.LENGTH_SHORT).show();

                return false;
            }
        }

        return true;
    }

    // This will behave badly when there are multiple sub-multiple choice questions
    private boolean validateMultipleChoice(LinearLayout questionsLinearLayout) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] validateMultipleChoice");
        }

        ArrayList<View> subquestions = QuestionViewAdapter.getViewsByTag(questionsLinearLayout, "subQuestion");

        for (View subquestionLinearLayout : subquestions) {

            LinearLayout rootChoices = (LinearLayout)subquestionLinearLayout.findViewById(
                    R.id.question_multiple_choice_rootChoices);

            int childCount = rootChoices.getChildCount();
            boolean hasCheck = false;

            CheckBox otherCheck = (CheckBox)subquestionLinearLayout.findViewById(
                    R.id.question_multiple_choice_otherCheckBox);
            boolean hasOtherCheck = otherCheck.isChecked();

            if (hasOtherCheck) {

                EditText otherEditText = (EditText)subquestionLinearLayout.findViewById(
                        R.id.question_multiple_choice_otherEditText);

                if (otherEditText.getText().length() == 0) {

                    Toast.makeText(context,
                            context.getString(R.string.questionMultipleChoice_other_please_fill),
                            Toast.LENGTH_SHORT).show();

                    return false;
                }
            }

            for (int i = 0; i < childCount; i++) {
                CheckBox child = (CheckBox)rootChoices.getChildAt(i);

                if (child.isChecked()) {
                    hasCheck = true;
                    break;
                }
            }

            if (!hasCheck && !hasOtherCheck) {

                Toast.makeText(context,
                        context.getString(R.string.questionMultipleChoice_please_check_one),
                        Toast.LENGTH_SHORT).show();

                return false;
            }
        }

        return true;
    }

}
