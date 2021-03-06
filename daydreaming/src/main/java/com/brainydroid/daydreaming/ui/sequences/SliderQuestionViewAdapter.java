package com.brainydroid.daydreaming.ui.sequences;

import android.app.Activity;
import android.text.method.LinkMovementMethod;
import android.util.FloatMath;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.SliderQuestionDescriptionDetails;
import com.brainydroid.daydreaming.db.SliderSubQuestion;
import com.brainydroid.daydreaming.sequence.SliderAnswer;
import com.brainydroid.daydreaming.ui.AlphaSeekBar;
import com.google.inject.Inject;

import java.util.ArrayList;

import roboguice.inject.InjectResource;

@SuppressWarnings("UnusedDeclaration")
public class SliderQuestionViewAdapter extends BaseQuestionViewAdapter
        implements IQuestionViewAdapter {

    private static String TAG = "SliderQuestionViewAdapter";

    @InjectResource(R.string.questionSlider_please_slide) String
            textPleaseSlide;
    @InjectResource(R.string.questionSlider_sliders_untouched_multiple)
    String errorUntouchedMultiple;
    @InjectResource(R.string.questionSlider_sliders_untouched_single)
    String errorUntouchedSingle;
    @InjectResource(R.string.questionSlider_skipped) String textSkipped;

    @Inject ArrayList<View> subQuestionsViews;
    @Inject SliderAnswer answer;

    @Override
    protected ArrayList<View> inflateViews(Activity activity, RelativeLayout outerPageLayout,
                                           LinearLayout questionLayout) {
        Logger.d(TAG, "Inflating question views");

        ArrayList<SliderSubQuestion> subQuestions =
                ((SliderQuestionDescriptionDetails)question.getDetails())
                        .getSubQuestions();

        for (SliderSubQuestion subQuestion : subQuestions) {
            View view = inflateView(questionLayout, subQuestion);
            subQuestionsViews.add(view);
        }
        return subQuestionsViews;

    }

    private View inflateView(LinearLayout questionLayout, SliderSubQuestion subQuestion) {
        Logger.v(TAG, "Inflating view for subQuestion");

        View view = layoutInflater.inflate(R.layout.question_slider, questionLayout, false);
        final ArrayList<String> hints = subQuestion.getHints();
        final int hintsNumber = hints.size();

        final TextView qText =
                (TextView)view.findViewById(R.id.question_slider_mainText);
        String initial_qText = subQuestion.getText();
        qText.setText(getExtendedQuestionText(initial_qText));
        qText.setMovementMethod(LinkMovementMethod.getInstance());


        TextView leftHintText = (TextView)view.findViewById(R.id.question_slider_leftHint);
        leftHintText.setText(hints.get(0));

        TextView rightHintText = (TextView)view.findViewById(R.id.question_slider_rightHint);
        rightHintText.setText(hints.get(hintsNumber - 1));

        final AlphaSeekBar seekBar =
                (AlphaSeekBar)view.findViewById(R.id.question_slider_seekBar);
        seekBar.setProgressDrawable(view.getResources().getDrawable(R.drawable.question_slider_progress));
        seekBar.setThumb(view.getResources().getDrawable(R.drawable.question_slider_thumb));

        final int initialPosition = subQuestion.getInitialPosition();
        if (initialPosition != SliderSubQuestion.DEFAULT_INITIAL_POSITION) {
            Logger.v(TAG, "Setting seekBar initial position to {0}",
                    initialPosition);
            seekBar.setProgress(initialPosition);
        }

        final TextView selectedSeek = (TextView)view.findViewById(R.id.question_slider_selectedSeek);
        if (!subQuestion.getShowLiveIndication()) {
            selectedSeek.setVisibility(View.GONE);
        }

        if (subQuestion.getAlreadyValid()) {
            Logger.v(TAG, "SeekBar is initially valid -> setting hint");
            int progress = seekBar.getProgress();
            int index = (int) FloatMath.floor((progress / 100f) * hintsNumber);
            if (index == hintsNumber) {
                // Have an open interval to the right
                index -= 1;
            }

            selectedSeek.setText(hints.get(index));
        } else {
            Logger.v(TAG, "SeekBar is initially not valid -> setting transparent");
            seekBar.setAlpha(0.5f);
        }

        final CheckBox naCheckBox =
                (CheckBox)view.findViewById(R.id.question_slider_naCheckBox);
        if (!subQuestion.getNotApplyAllowed()) {
            naCheckBox.setVisibility(View.GONE);
        }

        AlphaSeekBar.OnAlphaSeekBarChangeListener progressListener =
                new AlphaSeekBar.OnAlphaSeekBarChangeListener() {

            @Override
            public void onProgressChanged(AlphaSeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (!naCheckBox.isChecked()) {
                    Logger.v(TAG, "SeekBar progress changed -> changing text " +
                            "and transparency");
                    int index = (int) FloatMath.floor((progress / 100f) * hintsNumber);
                    if (index == hintsNumber) {
                        // Have an open interval to the right
                        index -= 1;
                    }

                    selectedSeek.setText(hints.get(index));
                    seekBar.setAlpha(1f);
                } else {
                    // Only reset the progress if the change came from the
                    // user, otherwise we might generate an infinite loop
                    if (fromUser) {
                        Logger.v(TAG, "SeekBar touched by user but skipping " +
                                "checkbox checked -> doing nothing");
                        seekBar.setProgress(initialPosition);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(AlphaSeekBar seekBar) {
                seekBar.setThumb(context.getResources().getDrawable(R.drawable.question_slider_thumb_big));
            }

            @Override
            public void onStopTrackingTouch(AlphaSeekBar seekBar) {
                seekBar.setThumb(context.getResources().getDrawable(R.drawable.question_slider_thumb));
            }
        };

        seekBar.setOnSeekBarChangeListener(progressListener);

        CheckBox.OnCheckedChangeListener naListener =
                new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                seekBar.setProgress(initialPosition);
                seekBar.setAlpha(0.5f);
                if (b) {
                    Logger.d(TAG, "Skipping checkBox checked, " +
                            "disabling the slider");
                    selectedSeek.setText(textSkipped);
                } else {
                    Logger.d(TAG, "Skipping checkBox unchecked, " +
                            "re-enabling the slider");
                    selectedSeek.setText(textPleaseSlide);
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
            TextView selectedSeek = (TextView)subQuestionView.findViewById(
                    R.id.question_slider_selectedSeek);

            if (selectedSeek.getText().equals(textPleaseSlide)) {
                Logger.v(TAG, "Found an untouched slider");
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
            AlphaSeekBar seekBar = (AlphaSeekBar)subQuestionView.findViewById(
                    R.id.question_slider_seekBar);
            TextView textView = (TextView)subQuestionView.findViewById(
                    R.id.question_slider_mainText);
            String text = textView.getText().toString();
            CheckBox naCheckBox = (CheckBox)subQuestionView.findViewById(
                    R.id.question_slider_naCheckBox);
            int progress = naCheckBox.isChecked() ? -1 : seekBar.getProgress();
            answer.addAnswer(text, progress);
        }

        question.setAnswer(answer);
    }





}
