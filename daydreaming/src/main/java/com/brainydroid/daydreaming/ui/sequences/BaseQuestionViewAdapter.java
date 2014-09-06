package com.brainydroid.daydreaming.ui.sequences;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import roboguice.inject.InjectView;

public abstract class BaseQuestionViewAdapter
        implements IQuestionViewAdapter {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionViewAdapter";

    public static String QUESTION_VIEW_ADAPTER_SUFFIX = "QuestionViewAdapter";

    protected Question question;

    @Inject Context context;
    @Inject LayoutInflater layoutInflater;
    @Inject
    ParametersStorage parametersStorage;

    public void setQuestion(Question question) {
        this.question = question;
    }

    public LinearLayout inflate(ViewGroup.LayoutParams layoutParams) {
        Logger.d(TAG, "Inflating question view");

        int index = 0;
        LinearLayout layout = (LinearLayout)layoutInflater.inflate(R.layout.question_layout, null);
        ArrayList<View> views = inflateViews();

        for (View view : views) {
            layout.addView(view, index, layoutParams);
            index++;
        }

        return layout;
    }

    protected abstract ArrayList<View> inflateViews();

    public SpannableString getExtentedQuestionText(String qText) {
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
                sbqText.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), i_start, i_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sbqText.setSpan(new ForegroundColorSpan(Color.YELLOW), i_start, i_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // set clickable
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Toast.makeText(context, definition,
                                Toast.LENGTH_SHORT).show();
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
