package com.brainydroid.daydreaming.ui.dashboard;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Glossary;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;


/**
 * Created by vincent on 02/09/14.
 */

@ContentView(R.layout.activity_glossary)

public class GlossaryActivity extends RoboFragmentActivity {

    private static String TAG = "GlossaryActivity";
    @Inject HashMap<String, View> glossaryPairsViews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        ViewGroup godfatherView = (ViewGroup) this.getWindow().getDecorView();
        populateGlossary();
        FontUtils.setRobotoFont(this, godfatherView);
        super.onCreate(savedInstanceState);
    }

    public void populateGlossary(){
        Glossary glossary = new Glossary();
        HashMap<String,String> dictionnary = (HashMap<String, String>) glossary.getDictionnary();

        Iterator it = dictionnary.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry glossaryPair = (Map.Entry) it.next();
            glossaryPairsViews.put((String)glossaryPair.getKey(), inflateView(glossaryPair));
        }
    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }

    public void onClick_backToDashboard(@SuppressWarnings("UnusedParameters") View v) {
        Logger.v(TAG, "Back to dashboard button clicked");
        onBackPressed();
    }

    private View inflateView(Map.Entry glossaryPair) {
        Logger.v(TAG, "Inflating view for glossary");

        RelativeLayout glossary_items_layout =
                (RelativeLayout)findViewById(R.id.glossary_items_layout);
        View view = getLayoutInflater().inflate(
                R.layout.personality_question_layout, glossary_items_layout, false);

        TextView glossary_key =
                (TextView)view.findViewById(R.id.glossary_item_key);
        TextView glossary_value =
                (TextView)view.findViewById(R.id.glossary_item_value);

        glossary_key.setText((String) glossaryPair.getKey());
        glossary_value.setText((String) glossaryPair.getValue());

        glossary_items_layout.addView(view);
        return view;
    }

}
