package com.brainydroid.daydreaming.ui.Questions;

import android.content.Context;
import android.util.FloatMath;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.SliderAnswer;
import com.brainydroid.daydreaming.db.SliderQuestionDetails;
import com.brainydroid.daydreaming.db.SliderSubQuestion;
import com.google.inject.Inject;
import roboguice.inject.InjectResource;

import java.util.ArrayList;

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

    @Inject Context context;
    @Inject ArrayList<View> subQuestionsViews;
    @Inject SliderAnswer answer;

    @Override
    protected ArrayList<View> inflateViews() {
        Logger.d(TAG, "Inflating question views");

        ArrayList<SliderSubQuestion> subQuestions =
                ((SliderQuestionDetails)question.getDetails())
                        .getSubQuestions();

        for (SliderSubQuestion subQuestion : subQuestions) {
            View view = inflateView(subQuestion);
            subQuestionsViews.add(view);
        }
        return subQuestionsViews;

    }

    private View inflateView(SliderSubQuestion subQuestion) {
        Logger.v(TAG, "Inflating view for subQuestion");

        View view = layoutInflater.inflate(R.layout.question_slider, null);
        final ArrayList<String> hints = subQuestion.getHints();
        final int hintsNumber = hints.size();

        TextView qText = (TextView)view.findViewById(R.id.question_slider_mainText);
        qText.setText(subQuestion.getText());

        TextView leftHintText = (TextView)view.findViewById(R.id.question_slider_leftHint);
        leftHintText.setText(hints.get(0));

        TextView rightHintText = (TextView)view.findViewById(R.id.question_slider_rightHint);
        rightHintText.setText(hints.get(hintsNumber - 1));

        SeekBar seekBar = (SeekBar)view.findViewById(R.id.question_slider_seekBar);
        seekBar.setProgressDrawable(view.getResources().getDrawable(R.drawable.question_slider_progress));
        seekBar.setThumb(view.getResources().getDrawable(R.drawable.question_slider_thumb));
        seekBar.setAlpha(0.5f);

        final TextView selectedSeek = (TextView)view.findViewById(R.id.question_slider_selectedSeek);

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                Logger.v(TAG, "SeekBar progress changed -> changing text " +
                        "and background");
                int index = (int)FloatMath.floor((progress / 101f) * hintsNumber);
                selectedSeek.setText(hints.get(index));
                seekBar.setAlpha(1f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        int initialPosition = subQuestion.getInitialPosition();
        if (initialPosition != -1) {
            Logger.v(TAG, "Setting seekBar initial position to {0}",
                    initialPosition);
            seekBar.setProgress(initialPosition);
        }

       // seekBar.setBackgroundColor(Color.argb(255,255,205,205));
        seekBar.setOnSeekBarChangeListener(listener);

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
            SeekBar seekBar = (SeekBar)subQuestionView.findViewById(
                    R.id.question_slider_seekBar);
            TextView textView = (TextView)subQuestionView.findViewById(
                    R.id.question_slider_mainText);
            String text = textView.getText().toString();
            answer.addAnswer(text, seekBar.getProgress());
        }

        question.setAnswer(answer);
    }

}
