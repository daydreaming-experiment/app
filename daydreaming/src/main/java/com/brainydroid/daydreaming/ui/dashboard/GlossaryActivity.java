package com.brainydroid.daydreaming.ui.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.Glossary;
import com.brainydroid.daydreaming.db.Json;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

import java.lang.reflect.Type;
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
    @Inject ParametersStorage parametersStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);

        ViewGroup godfatherView = (ViewGroup) this.getWindow().getDecorView();
        populateGlossary();
        FontUtils.setRobotoFont(this, godfatherView);
    }

    public void populateGlossary(){

        HashMap<String,String> dictionnary = parametersStorage.getGlossary();
        Iterator it = dictionnary.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry glossaryPair = (Map.Entry) it.next();
            glossaryPairsViews.put(glossaryPair.getKey().toString(), inflateView(glossaryPair));
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

    private LinearLayout inflateView(Map.Entry glossaryPair) {
        Logger.v(TAG, "Inflating view for glossary");

        LinearLayout glossary_items_layout =
                (LinearLayout)findViewById(R.id.glossary_items_layout);

        LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(
                R.layout.glossary_item_layout, glossary_items_layout, false);

        TextView glossary_key =
                (TextView)linearLayout.findViewById(R.id.glossary_item_key);
        TextView glossary_value =
                (TextView)linearLayout.findViewById(R.id.glossary_item_value);

        Logger.d(TAG, "Glossary key: {}", glossaryPair.getKey().toString());
        Logger.d(TAG, "Glossary value: {}", glossaryPair.getValue().toString());

        glossary_key.setText(glossaryPair.getKey().toString());
        glossary_value.setText(glossaryPair.getValue().toString());

        glossary_items_layout.addView(linearLayout);
        return linearLayout;
    }

}
