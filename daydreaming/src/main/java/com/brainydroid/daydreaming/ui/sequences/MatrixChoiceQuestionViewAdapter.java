package com.brainydroid.daydreaming.ui.sequences;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.MatrixChoiceQuestionDescriptionDetails;
import com.brainydroid.daydreaming.sequence.MatrixChoiceAnswer;
import com.brainydroid.daydreaming.ui.ChoiceButton;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.util.ArrayList;

import roboguice.inject.InjectResource;

@SuppressWarnings("UnusedDeclaration")
public class MatrixChoiceQuestionViewAdapter extends BaseQuestionViewAdapter
        implements IQuestionViewAdapter {

    private static String TAG = "MatrixChoiceQuestionViewAdapter";

    @SuppressWarnings("FieldCanBeLocal")
    private static int MAX_BUTTONS_PER_ROW = 3;

    @Inject private ArrayList<ChoiceButton> choiceButtons;

    @InjectResource(R.string.questionMatrixChoice_please_select_one)
    String errorCheckOne;
    @Inject Context context;
    @Inject MatrixChoiceAnswer answer;
    @Inject Injector injector;

    private static class ChoiceClickListener implements View.OnClickListener {

        private static String TAG = "ChoiceClickListener";

        private ChoiceButton button;

        public ChoiceClickListener(ChoiceButton button) {
            Logger.v(TAG, "Initializing");
            this.button = button;
        }

        @Override
        public void onClick(View view) {
            button.toggleChecked();
        }
    }

    private ArrayList<ArrayList<String>> flowChoices(ArrayList<String> choices) {
        Logger.v(TAG, "Flowing choices");

        ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
        // This initialization is not necessary (it's done again at the first iteration inside
        // the loop), but lint doesn't know that, and won't compile otherwise.
        ArrayList<String> row = new ArrayList<String>();
        int indexInRow = 0;
        for (String choice : choices) {
            if (indexInRow == MAX_BUTTONS_PER_ROW) {
                indexInRow = 0;
            }
            if (indexInRow == 0) {
                row = new ArrayList<String>();
                rows.add(row);
            }
            row.add(choice);
            indexInRow++;
        }

        return rows;
    }

    @Override
    protected ArrayList<View> inflateViews(LinearLayout questionLayout) {
        Logger.d(TAG, "Inflating question views");

        MatrixChoiceQuestionDescriptionDetails details =
                (MatrixChoiceQuestionDescriptionDetails)question.getDetails();
        ArrayList<String> choices = details.getChoices();
        View questionView = layoutInflater.inflate(
                R.layout.question_matrix_choice, questionLayout, false);

        LinearLayout rowContainer = (LinearLayout) questionView.findViewById(
                R.id.question_matrix_choice_rowContainer);
        TextView qText = (TextView) questionView.findViewById(
                R.id.question_matrix_choice_mainText);
        String initial_qText = details.getText();
        qText.setText(getExtendedQuestionText(initial_qText));
        qText.setMovementMethod(LinkMovementMethod.getInstance());

        ArrayList<ArrayList<String>> choiceRows = flowChoices(choices);

        for (ArrayList<String> choiceRow : choiceRows) {
            LinearLayout rowLayout = (LinearLayout)layoutInflater.inflate(
                    R.layout.question_matrix_choice_row, rowContainer, false);
            rowLayout.addView(layoutInflater.inflate(
                    R.layout.question_matrix_choice_inter_view, rowLayout, false));

            rowContainer.addView(rowLayout);

            for (String choiceItem : choiceRow) {
                Logger.v(TAG, "Inflating choice {0}", choiceItem);

                ChoiceButton choiceButton = (ChoiceButton)layoutInflater.inflate(
                        R.layout.question_matrix_choice_button, rowLayout, false);
                rowLayout.addView(choiceButton);
                rowLayout.addView(layoutInflater.inflate(
                        R.layout.question_matrix_choice_inter_view, rowLayout, false));

                choiceButton.setOnClickListener(new ChoiceClickListener(choiceButton));
                choiceButtons.add(choiceButton);
            }
        }

        ArrayList<View> views = new ArrayList<View>();
        views.add(questionView);

        return views;
    }

    @Override
    public boolean validate() {
        Logger.i(TAG, "Validating choices");

        boolean hasCheck = false;
        for (ChoiceButton button : choiceButtons) {
            if (button.isChecked()) {
                Logger.v(TAG, "At least one option is checked");
                hasCheck = true;
                break;
            }
        }

        if (!hasCheck) {
            Logger.v(TAG, "No option checked");
            Toast.makeText(context, errorCheckOne, Toast.LENGTH_SHORT).show();
        }

        return hasCheck;
    }

    @Override
    public void saveAnswer() {
        Logger.i(TAG, "Saving question answer");

        int index = 0;
        for (ChoiceButton button : choiceButtons) {
            if (button.isChecked()) {
                answer.addChoice(String.valueOf(index));
            }
            index++;
        }

        question.setAnswer(answer);
    }
}
