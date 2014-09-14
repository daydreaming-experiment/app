package com.brainydroid.daydreaming.ui.sequences;

import android.annotation.TargetApi;
import android.app.Activity;
import android.text.method.LinkMovementMethod;
import android.util.FloatMath;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.ManySlidersQuestionDescriptionDetails;
import com.brainydroid.daydreaming.sequence.SliderAnswer;
import com.brainydroid.daydreaming.ui.AlphaSeekBar;
import com.google.inject.Inject;

import java.util.ArrayList;

import roboguice.inject.InjectResource;

public class ManySlidersQuestionViewAdapter
        extends BaseQuestionViewAdapter implements IQuestionViewAdapter {

    private static String TAG = "ManySlidersQuestionViewAdapter";

    @InjectResource(R.string.questionSlider_sliders_untouched_multiple)
    String errorUntouched;
    @InjectResource(R.string.questionSlider_please_slide) String textPleaseSlide;

    private LinearLayout rowContainer;
    @Inject private ArrayList<LinearLayout> sliderLayouts;
    @Inject SliderAnswer answer;

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

        rowContainer = (LinearLayout)questionView.findViewById(
                R.id.question_many_sliders_rowContainer);
        TextView qText = (TextView)questionView.findViewById(
                R.id.question_many_sliders_mainText);
        String initial_qText = details.getText();
        qText.setText(getExtendedQuestionText(initial_qText));
        qText.setMovementMethod(LinkMovementMethod.getInstance());

        for (String sliderText : userSliders) {
            LinearLayout sliderLayout = inflateSlider(sliderText);
            sliderLayouts.add(sliderLayout);
            rowContainer.addView(sliderLayout);
        }

        // TODO: add edit mode button listener (in parent view)
        // Shows and loads dropdown list from which to add
        // Shows '-' buttons
        // Adds Done button.
        // Saves to userPossibilities

        ArrayList<View> views = new ArrayList<View>();
        views.add(questionView);

        return views;
    }

    private LinearLayout inflateSlider(String sliderText) {

        Logger.v(TAG, "Inflating slider {}", sliderText);

        ManySlidersQuestionDescriptionDetails details =
                (ManySlidersQuestionDescriptionDetails)question.getDetails();
        final LinearLayout sliderLayout = (LinearLayout)layoutInflater.inflate(
                R.layout.question_many_sliders_slider, rowContainer, false);

        // Set text
        ((TextView)sliderLayout.findViewById(
                R.id.question_many_sliders_slider_mainText)).setText(sliderText);

        // Set extremity hints
        final ArrayList<String> hints = details.getHints();
        final int nHints = details.getHints().size();
        ((TextView)sliderLayout.findViewById(
                R.id.question_many_sliders_slider_leftHint)).setText(hints.get(0));
        ((TextView)sliderLayout.findViewById(
                R.id.question_many_sliders_slider_rightHint)).setText(hints.get(nHints - 1));

        // Set initial position
        final AlphaSeekBar sliderSeek = (AlphaSeekBar)sliderLayout.findViewById(
                R.id.question_many_sliders_slider_seekBar);
        sliderSeek.setProgress(details.getInitialPosition());

        // Set live indication
        final TextView selectedSeek = (TextView)sliderLayout.findViewById(
                R.id.question_many_sliders_slider_selectedSeek);
        selectedSeek.setVisibility(details.isShowLiveIndication() ? View.VISIBLE : View.GONE);

        // Set progress listener
        AlphaSeekBar.OnAlphaSeekBarChangeListener progressListener =
                new AlphaSeekBar.OnAlphaSeekBarChangeListener() {

            @TargetApi(11)
            @Override
            public void onProgressChanged(AlphaSeekBar seekBar, int progress,
                                          boolean fromUser) {
                Logger.v(TAG, "SeekBar progress changed -> changing text and transparency");
                int index = (int) FloatMath.floor((progress / 100f) * nHints);
                if (index == nHints) {
                    // Have an open interval to the right
                    index -= 1;
                }

                selectedSeek.setText(hints.get(index));
                // Lint erroneously catches this as a call that requires API >= 11
                // (which is exactly why AlphaSeekBar exists),
                // hence the @TargetApi(11) above.
                seekBar.setAlpha(1f);
            }

            @Override
            public void onStartTrackingTouch(AlphaSeekBar seekBar) {
                seekBar.setThumb(context.getResources().getDrawable(
                        R.drawable.question_slider_thumb_big));
            }

            @Override
            public void onStopTrackingTouch(AlphaSeekBar seekBar) {
                seekBar.setThumb(context.getResources().getDrawable(
                        R.drawable.question_slider_thumb));
            }
        };

        sliderSeek.setOnSeekBarChangeListener(progressListener);

        // TODO: add deletion listener
        // removes from user possibilities

        return sliderLayout;
    }

    @Override
    public boolean validate() {
        Logger.i(TAG, "Validating answer");

        for (LinearLayout sliderLayout : sliderLayouts) {
            TextView selectedSeek = (TextView)sliderLayout.findViewById(
                    R.id.question_many_sliders_slider_selectedSeek);
            if (selectedSeek.getText().equals(textPleaseSlide)) {
                Logger.v(TAG, "Found an untouched slider");
                Toast.makeText(context, errorUntouched, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    @Override
    public void saveAnswer() {
        Logger.i(TAG, "Saving question answer");

        for (LinearLayout sliderLayout : sliderLayouts) {
            AlphaSeekBar seekBar = (AlphaSeekBar)sliderLayout.findViewById(
                    R.id.question_many_sliders_slider_seekBar);
            TextView mainText = (TextView)sliderLayout.findViewById(
                    R.id.question_many_sliders_slider_mainText);
            String sliderText = mainText.getText().toString();
            int progress = seekBar.getProgress();
            answer.addAnswer(sliderText, progress);
        }

        question.setAnswer(answer);
    }
}
