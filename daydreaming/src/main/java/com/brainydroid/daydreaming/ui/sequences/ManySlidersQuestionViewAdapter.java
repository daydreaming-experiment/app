package com.brainydroid.daydreaming.ui.sequences;

import android.annotation.TargetApi;
import android.app.Activity;
import android.text.method.LinkMovementMethod;
import android.util.FloatMath;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.ManySlidersQuestionDescriptionDetails;
import com.brainydroid.daydreaming.sequence.ManySlidersAnswer;
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
    @InjectResource(R.string.page_edit_mode_done) String editTextDone;
    @InjectResource(R.string.page_edit_mode_edit) String editTextEdit;

    @Inject private ArrayList<LinearLayout> sliderLayouts;
    @Inject ManySlidersAnswer answer;

    private LinearLayout rowContainer;
    private boolean isEditMode = false;

    @Override
    protected ArrayList<View> inflateViews(Activity activity, final LinearLayout questionLayout) {
        Logger.d(TAG, "Inflating question views");

        ManySlidersQuestionDescriptionDetails details =
                (ManySlidersQuestionDescriptionDetails)question.getDetails();
        ArrayList<String> userSliders = parametersStorage.getUserPossibilities(
                question.getQuestionName());
        if (userSliders.size() == 0) {
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

        // TODO: Set auto-complete button listener



        // Add edit button
        final Button editButton = (Button)((RelativeLayout)questionLayout
                .getParent().getParent()).findViewById(R.id.page_editModeButton);
        editButton.setVisibility(View.VISIBLE);

        Button.OnClickListener clickListener = new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                Logger.v(TAG, "Edit mode button clicked, toggling mode");
                isEditMode = !isEditMode;

                // Toggle delete buttons visibility
                for (LinearLayout sliderLayout : sliderLayouts) {
                    ((ImageButton)sliderLayout.findViewById(
                            R.id.question_many_sliders_sliderDelete))
                            .setVisibility(isEditMode ? View.VISIBLE : View.GONE);
                }

                // Toggle and, if necessary, load dropdown list to add/remove
                RelativeLayout addLayout = (RelativeLayout)questionLayout.findViewById(
                        R.id.question_many_sliders_addLayout);
                addLayout.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
                // TODO: load autoview

                // Toggle add button text
                editButton.setText(isEditMode ? editTextDone : editTextEdit);
            }

        };

        ArrayList<View> views = new ArrayList<View>();
        views.add(questionView);

        return views;
    }

    @TargetApi(11)
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
        sliderSeek.setProgressDrawable(sliderLayout.getResources().getDrawable(
                R.drawable.question_slider_progress));
        sliderSeek.setThumb(sliderLayout.getResources().getDrawable(
                R.drawable.question_slider_thumb));
        // Lint erroneously catches this as a call that requires API >= 11
        // (which is exactly why AlphaSeekBar exists),
        // hence the @TargetApi(11) above.
        sliderSeek.setAlpha(0.5f);

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
            answer.addSlider(sliderText, progress);
        }

        question.setAnswer(answer);
    }
}
