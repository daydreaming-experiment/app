package com.brainydroid.daydreaming.ui;

import android.graphics.Color;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.Answer;
import com.brainydroid.daydreaming.db.MultipleChoiceAnswer;
import com.brainydroid.daydreaming.db.Question;
import com.brainydroid.daydreaming.db.SliderAnswer;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class QuestionViewAdapter {

    private static String TAG = "QuestionViewAdapter";

    private Question question;
    private LinearLayout questionLinearLayout;

    @Inject LayoutInflater layoutInflater;

    @Inject
    public QuestionViewAdapter(@Assisted Question question, @Assisted LinearLayout questionLinearLayout) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] QuestionViewAdapter");
        }

        this.question = question;
        this.questionLinearLayout = questionLinearLayout;
    }

    // Select questions by tags.
    // Tags only used to identify subquestions when they exist
    public static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getViewsByTag");
        }

        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup)child, tag));
            }

            Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }
        }

        return views;
    }

    public void populateViews(Boolean isFirstQuestion) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] populateViews");
        }

        ArrayList<View> views;
        String type = question.getType();

        if (type == null) {
            throw new RuntimeException("Question type not set");
        } else if (type.equals(Question.TYPE_SLIDER)) {
            views = createViewsSlider();
        } else if (type.equals(Question.TYPE_MULTIPLE_CHOICE)) {
            views = createViewsMultipleChoice();
        } else {
            throw new RuntimeException("Question type not recognized");
        }

        int index = isFirstQuestion ? 1 : 0;
        for (View view : views) {
            questionLinearLayout.addView(view, index, questionLinearLayout.getLayoutParams());
            index++;
        }
    }

    private View createViewSlider(String mainText, final ArrayList<String> parametersTexts) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] createViewSlider");
        }

        View view = layoutInflater.inflate(R.layout.question_slider, null);

        TextView qText = (TextView)view.findViewById(R.id.question_slider_mainText);
        qText.setText(mainText);

        TextView leftHintText = (TextView)view.findViewById(R.id.question_slider_leftHint);
        leftHintText.setText(parametersTexts.get(0));

        TextView rightHintText = (TextView)view.findViewById(R.id.question_slider_rightHint);
        rightHintText.setText(parametersTexts.get(parametersTexts.size() - 1));

        SeekBar seekBar = (SeekBar)view.findViewById(R.id.question_slider_seekBar);
        final TextView selectedSeek = (TextView)view.findViewById(R.id.question_slider_selectedSeek);
        final int maxSeek = parametersTexts.size();

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                int index = (int) FloatMath.floor((progress / 101f) * maxSeek);
                selectedSeek.setText(parametersTexts.get(index));
                seekBar.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        int defaultPosition = question.getDefaultPosition();
        if (defaultPosition != -1) {
            seekBar.setProgress(defaultPosition);
        }

        seekBar.setBackgroundColor(Color.argb(255,255,205,205));
        seekBar.setOnSeekBarChangeListener(listener);

        return view;
    }

    private ArrayList<View> createViewsSlider() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] createViewsSlider");
        }

        ArrayList<View> views = new ArrayList<View>();

        ArrayList<String> mainTexts = getParsedMainText();
        ArrayList<ArrayList<String>> allParametersTexts = getParsedParametersText();
        Iterator<ArrayList<String>> ptsIt = allParametersTexts.iterator();

        for (String mainText : mainTexts) {
            ArrayList<String> parametersTexts = ptsIt.next();
            View view = createViewSlider(mainText, parametersTexts);
            views.add(view);
        }

        return views;
    }

    private View createViewMultipleChoice(String mainText, ArrayList<String> parametersTexts) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] createViewMultipleChoice");
        }

        View view = layoutInflater.inflate(R.layout.question_multiple_choice, null);

        TextView qText = (TextView)view.findViewById(R.id.question_multiple_choice_mainText);
        qText.setText(mainText);

        final CheckBox otherCheck = (CheckBox)view.findViewById(R.id.question_multiple_choice_otherCheckBox);
        final EditText otherEdit = (EditText)view.findViewById(R.id.question_multiple_choice_otherEditText);

        CompoundButton.OnCheckedChangeListener otherCheckListener = new CompoundButton.OnCheckedChangeListener() {

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

        View.OnClickListener otherEditClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                otherCheck.setChecked(true);
            }

        };

        View.OnFocusChangeListener otherEditFocusListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    otherCheck.setChecked(true);
                }
            }
        };

        View.OnKeyListener onSoftKeyboardDonePress = new View.OnKeyListener() {

            @Inject
            InputMethodManager inputMethodManager;

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    inputMethodManager.hideSoftInputFromWindow(otherEdit.getApplicationWindowToken(), 0);
                }
                return false;
            }
        };

        otherCheck.setOnCheckedChangeListener(otherCheckListener);
        otherEdit.setOnClickListener(otherEditClickListener);
        otherEdit.setOnFocusChangeListener(otherEditFocusListener);
        otherEdit.setOnKeyListener(onSoftKeyboardDonePress);

        LinearLayout checksLayout = (LinearLayout)view.findViewById(R.id.question_multiple_choice_rootChoices);

        for (String parameter : parametersTexts) {
            CheckBox checkBox = (CheckBox)layoutInflater.inflate(R.layout.question_multiple_choice_item, null);
            checkBox.setText(parameter);
            checksLayout.addView(checkBox);
        }

        return view;
    }

    private ArrayList<View> createViewsMultipleChoice() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] createViewsMultipleChoice");
        }

        ArrayList<View> views = new ArrayList<View>();

        ArrayList<String> mainTexts = getParsedMainText();
        ArrayList<ArrayList<String>> allParametersTexts = getParsedParametersText();
        Iterator<ArrayList<String>> ptsIt = allParametersTexts.iterator();

        for (String mainText : mainTexts) {
            ArrayList<String> parametersTexts = ptsIt.next();
            View view = createViewMultipleChoice(mainText, parametersTexts);
            views.add(view);
        }

        return views;
    }

    // Parsing sub-functions

    private ArrayList<String> parseString(String toParse, String sep) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] parseString");
        }

        return new ArrayList<String>(Arrays.asList(toParse.split(sep)));
    }

    private ArrayList<String> getParsedMainText() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getParsedMainText");
        }

        return parseString(question.getMainText(), Question.PARAMETER_SPLITTER);
    }

    private ArrayList<ArrayList<String>> getParsedParametersText() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getParsedParametersText");
        }

        ArrayList<ArrayList<String>> parsedParametersText = new ArrayList<ArrayList<String>>();
        ArrayList<String> preParsed = parseString(question.getParametersText(), Question.PARAMETER_SPLITTER);

        for (String subParametersToParse : preParsed) {
            ArrayList<String> subParameters = parseString(subParametersToParse, Question.SUBPARAMETER_SPLITTER);
            parsedParametersText.add(subParameters);
        }

        return parsedParametersText;
    }

    // Answer saving

    private Answer newAnswerType() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] newAnswerType");
        }

        String type = question.getType();
        if (type == null) {
            throw new RuntimeException("Question type not set");
        } else if (type.equals(Question.TYPE_MULTIPLE_CHOICE)) {
            return new MultipleChoiceAnswer();
        } else if (type.equals(Question.TYPE_SLIDER)) {
            return new SliderAnswer();
        } else {
            throw new RuntimeException("Question type not recognized");
        }
    }

    public void saveAnswers() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] saveAnswers");
        }

        Answer answer = newAnswerType();
        answer.getAnswersFromLayout(questionLinearLayout);
        question.setAnswer(answer);
    }

}
