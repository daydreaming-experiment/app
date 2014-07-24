package com.brainydroid.daydreaming.ui.questions;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.FloatMath;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.StarRatingAnswer;
import com.brainydroid.daydreaming.db.StarRatingQuestionDetails;
import com.brainydroid.daydreaming.db.StarRatingSubQuestion;
import com.brainydroid.daydreaming.ui.AlphaRatingBar;
import com.google.inject.Inject;
import roboguice.inject.InjectResource;

import java.util.ArrayList;

@SuppressWarnings("UnusedDeclaration")
public class StarRatingQuestionViewAdapter extends BaseQuestionViewAdapter
        implements IQuestionViewAdapter {

    private static String TAG = "StarRatingQuestionViewAdapter";

    @InjectResource(R.string.questionStarRating_please_rate) String
            textPleaseRate;
    @InjectResource(R.string.questionStarRating_star_ratings_untouched_multiple)
    String errorUntouchedMultiple;
    @InjectResource(R.string.questionStarRating_star_ratings_untouched_single)
    String errorUntouchedSingle;
    @InjectResource(R.string.questionStarRating_skipped) String textSkipped;

    @Inject Context context;
    @Inject ArrayList<View> subQuestionsViews;
    @Inject StarRatingAnswer answer;

    @Override
    protected ArrayList<View> inflateViews() {
        Logger.d(TAG, "Inflating question views");

        ArrayList<StarRatingSubQuestion> subQuestions =
                ((StarRatingQuestionDetails)question.getDetails())
                        .getSubQuestions();

        for (StarRatingSubQuestion subQuestion : subQuestions) {
            View view = inflateView(subQuestion);
            subQuestionsViews.add(view);
        }
        return subQuestionsViews;

    }

    @TargetApi(11)
    private View inflateView(StarRatingSubQuestion subQuestion) {
        Logger.v(TAG, "Inflating view for subQuestion");

        View view = layoutInflater.inflate(R.layout.question_star_rating, null);
        final ArrayList<String> hints = subQuestion.getHints();
        final int hintsNumber = hints.size();

        TextView qText = (TextView)view.findViewById(R.id.question_star_rating_mainText);
        qText.setText(subQuestion.getText());

        TextView leftHintText = (TextView)view.findViewById(R.id.question_star_rating_leftHint);
        leftHintText.setText(hints.get(0));

        TextView rightHintText = (TextView)view.findViewById(R.id.question_star_rating_rightHint);
        rightHintText.setText(hints.get(hintsNumber - 1));

        final AlphaRatingBar ratingBar =
                (AlphaRatingBar)view.findViewById(R.id.question_star_rating_ratingBar);
        ratingBar.setProgressDrawable(view.getResources().getDrawable(R.drawable.question_slider_progress));
        ratingBar.setThumb(view.getResources().getDrawable(R.drawable.question_slider_thumb));
        // Lint erroneously catches this as a call that requires API >= 11
        // (which is exactly why AlphaRatingBar exists),
        // hence the @TargetApi(11) above.
        ratingBar.setAlpha(0.5f);

        final int numStars = subQuestion.getNumStars();
        final int effectiveNumStars;
        if (numStars != -1) {
            Logger.v(TAG, "Setting ratingBar numStars to {0}", numStars);
            ratingBar.setNumStars(numStars);
            effectiveNumStars = numStars;
        } else {
            effectiveNumStars = 5;
        }

        float stepSize = subQuestion.getStepSize();
        if (stepSize != -1f) {
            Logger.v(TAG, "Setting ratingBar stepSize to {}", stepSize);
            ratingBar.setStepSize(stepSize);
        }

        final float initialRating = subQuestion.getInitialRating();
        if (initialRating != -1) {
            Logger.v(TAG, "Setting ratingBar initial rating to {0}",
                    initialRating);
            ratingBar.setRating(initialRating);
        }

        final TextView selectedRating = (TextView)view.findViewById(R.id.question_star_rating_selectedRating);
        if (!subQuestion.getShowLiveIndication()) {
            selectedRating.setVisibility(View.GONE);
        }

        final CheckBox naCheckBox =
                (CheckBox)view.findViewById(R.id.question_star_rating_naCheckBox);
        if (!subQuestion.getNotApplyAllowed()) {
            naCheckBox.setVisibility(View.GONE);
        }

        AlphaRatingBar.OnAlphaRatingBarChangeListener listener = new AlphaRatingBar.OnAlphaRatingBarChangeListener() {

            @TargetApi(11)
            @Override
            public void onRatingChanged(AlphaRatingBar ratingBar,
                                        float rating, boolean fromUser) {
                if (!naCheckBox.isChecked()) {
                    Logger.v(TAG, "RatingBar rating changed -> changing text " +
                            "and transparency");
                    int index = (int) FloatMath.floor((rating / (float)effectiveNumStars) * hintsNumber);
                    if (index == hintsNumber) {
                        // Have an open interval to the right
                        index -= 1;
                    }

                    selectedRating.setText(hints.get(index));
                    // Lint erroneously catches this as a call that requires API >= 11
                    // (which is exactly why AlphaRatingBar exists),
                    // hence the @TargetApi(11) above.
                    ratingBar.setAlpha(1f);
                } else {
                    // Only reset the rating if the change came from the
                    // user, otherwise we might generate an infinite loop
                    if (fromUser) {
                        Logger.v(TAG, "RatingBar touched by user but skipping" +
                                " checkbox checked -> doing nothing");
                        ratingBar.setRating(initialRating);
                    }
                }
            }

        };

        ratingBar.setOnRatingBarChangeListener(listener);

        CheckBox.OnCheckedChangeListener naListener =
                new CheckBox.OnCheckedChangeListener() {

            @TargetApi(11)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ratingBar.setRating(initialRating);
                // Lint erroneously catches this as a call that requires API >= 11
                // (which is exactly why AlphaRatingBar exists),
                // hence the @TargetApi(11) above.
                ratingBar.setAlpha(0.5f);
                if (b) {
                    Logger.d(TAG, "Skipping checkbox checked, " +
                            "disabling the rating bar");
                    selectedRating.setText(textSkipped);
                } else {
                    Logger.d(TAG, "Skipping checkbox unchecked, " +
                            "re-enabling the rating bar");
                    selectedRating.setText(textPleaseRate);
                }
            }

        };

        naCheckBox.setOnCheckedChangeListener(naListener);

        return view;
    }

    @Override
    public boolean validate() {
        Logger.i(TAG, "Validating answer");

        boolean isMultiple = subQuestionsViews.size() > 1;

        for (View subQuestionView : subQuestionsViews) {
            TextView selectedRating = (TextView)subQuestionView.findViewById(
                    R.id.question_star_rating_selectedRating);

            if (selectedRating.getText().equals(textPleaseRate)) {
                Logger.v(TAG, "Found an untouched star rating");
                Toast.makeText(context,
                        isMultiple ? errorUntouchedMultiple :
                                errorUntouchedSingle,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    @Override
    public void saveAnswer() {
        Logger.i(TAG, "Saving question answer");

        for (View subQuestionView : subQuestionsViews) {
            AlphaRatingBar ratingBar = (AlphaRatingBar)subQuestionView.findViewById(
                    R.id.question_star_rating_ratingBar);
            TextView textView = (TextView)subQuestionView.findViewById(
                    R.id.question_star_rating_mainText);
            String text = textView.getText().toString();
            CheckBox naCheckBox = (CheckBox)subQuestionView.findViewById(
                    R.id.question_star_rating_naCheckBox);
            float rating = naCheckBox.isChecked() ? -1 : ratingBar.getRating();
            answer.addAnswer(text, rating);
        }

        question.setAnswer(answer);
    }

}
