package com.brainydroid.daydreaming.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.StatusManager;


import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.StatusManager;
import org.w3c.dom.Text;


/**
 * Created with IntelliJ IDEA.
 * User: vincenta
 * Date: 17/04/13
 * Time: 13:21
 * To change this template use File | Settings | File Templates.
 */
public class FirstLaunchTermsActivity  extends ActionBarActivity {

    private static String TAG = "FirstLaunchTermsActivity";

    private StatusManager status;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onCreate");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch_terms);

        TextView consent = (TextView)findViewById(R.id.firstLaunchTerms_textConsent);
        consent.setText(Text.readTxt(R.raw.terms, (Activity) this));

        status = StatusManager.getInstance(this);
    }

    @Override
    public void onStart() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onStart");
        }

        super.onStart();
        checkFirstLaunch();
    }

    @Override
    public void onResume() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onResume");
        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onBackPressed");
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public void onClick_buttonNext(View view) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onClick_buttonNext");
        }

    }

    private void launchProfileActivity() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] launchProfileActivity");
        }

        Intent intent = new Intent(this, FirstLaunchProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void checkFirstLaunch() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] checkFirstLaunch");
        }

        if (status.isFirstLaunchCompleted() || status.isClearing()) {
            finish();
        }
    }


}
