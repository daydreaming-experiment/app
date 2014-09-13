package com.brainydroid.daydreaming.ui.sequences;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.sequence.Question;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseQuestionViewAdapter
        implements IQuestionViewAdapter {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionViewAdapter";

    public static String QUESTION_VIEW_ADAPTER_SUFFIX = "QuestionViewAdapter";

    protected Question question;

    @Inject Context context;
    @Inject LayoutInflater layoutInflater;
    @Inject ParametersStorage parametersStorage;

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public LinearLayout inflate(Activity activity, LinearLayout pageLayout) {
        Logger.d(TAG, "Inflating question view");

        int index = 0;
        LinearLayout questionLayout = (LinearLayout)layoutInflater.inflate(
                R.layout.question_layout, pageLayout, false);
        ArrayList<View> views = inflateViews(activity, questionLayout);

        for (View view : views) {
            questionLayout.addView(view, index);
            index++;
        }

        return questionLayout;
    }

    protected abstract ArrayList<View> inflateViews(Activity activity, LinearLayout questionLayout);

    public SpannableString getExtendedQuestionText(String qText) {
        HashMap<String,String> dictionary = parametersStorage.getGlossary();
        final SpannableString sbqText = new SpannableString( qText );

        // Looping over glossary entries
        for (Map.Entry<String,String> glossaryPair : dictionary.entrySet()) {
            final String term = glossaryPair.getKey();

            // if term is in question text
            if (qText.contains(term)) {
                final String definition = glossaryPair.getValue();
                int i_start = qText.indexOf(term);
                int i_end = i_start + term.length();
                // set style bold
                sbqText.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ui_dark_blue_color)), i_start, i_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // set clickable
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Toast.makeText(context, definition,
                                Toast.LENGTH_LONG).show();
                    }
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                };
                sbqText.setSpan(clickableSpan, i_start, i_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        }
        return sbqText;

    }
}
