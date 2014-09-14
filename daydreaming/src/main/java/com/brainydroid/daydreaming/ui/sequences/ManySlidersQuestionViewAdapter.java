package com.brainydroid.daydreaming.ui.sequences;

import android.app.Activity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.ManySlidersQuestionDescriptionDetails;
import com.brainydroid.daydreaming.db.MatrixChoiceQuestionDescriptionDetails;
import com.brainydroid.daydreaming.ui.ChoiceItem;
import com.google.inject.Inject;

import java.util.ArrayList;

public class ManySlidersQuestionViewAdapter
        extends BaseQuestionViewAdapter implements IQuestionViewAdapter {

    private static String TAG = "ManySlidersQuestionViewAdapter";

    @Inject private ArrayList<LinearLayout> sliderLayouts;

    @Override
    protected ArrayList<View> inflateViews(Activity activity, LinearLayout questionLayout) {
        Logger.d(TAG, "Inflating question views");

        ManySlidersQuestionDescriptionDetails details =
                (ManySlidersQuestionDescriptionDetails)question.getDetails();
        ArrayList<String> userSliders = parametersStorage.getUserPossibilities(
                question.getQuestionName());
        if (userSliders == null) {
            userSliders = details.getDefaultSliders();
        }

        View questionView = layoutInflater.inflate(
                R.layout.question_many_sliders, questionLayout, false);

        LinearLayout rowContainer = (LinearLayout)questionView.findViewById(
                R.id.question_many_sliders_rowContainer);
        TextView qText = (TextView)questionView.findViewById(
                R.id.question_many_sliders_mainText);
        String initial_qText = details.getText();
        qText.setText(getExtendedQuestionText(initial_qText));
        qText.setMovementMethod(LinkMovementMethod.getInstance());

        for (String sliderText : userSliders) {
            Logger.v(TAG, "Inflating slider {}", sliderText);
            LinearLayout sliderLayout = (LinearLayout)layoutInflater.inflate(
                    R.layout.question_many_sliders_slider, rowContainer, false);

            // TODO: set text
            // TODO: set hints
            // TODO: set initial position
            // TODO: if live indication, set it + listener
            // TODO: add

            sliderLayouts.add(sliderLayout);
            rowContainer.addView(sliderLayout);
        }

        // TODO: add edit mode button listener
        // Shows and loads dropdown list from which to add
        // Shows '-' buttons
        // Adds Done button.
        // Saves to userPossibilities

        ArrayList<View> views = new ArrayList<View>();
        views.add(questionView);

        return views;
    }

    @Override
    public boolean validate() {
        // TODO
        return false;
    }

    @Override
    public void saveAnswer() {
        // TODO
    }
}
