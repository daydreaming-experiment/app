package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_first_launch_cloud_transition)
public class FirstLaunchCloudTransition extends FirstLaunchActivity {

    private static String TAG = "FirstLaunchCloudTransitionActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");

        super.onCreate(savedInstanceState);

        Class activity;
        Bundle extras = getIntent().getExtras();
        activity= (Class) extras.getSerializable("nextClass");

        try {
            transit(activity);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void transit(Class activity) throws InterruptedException {
        Logger.v(TAG, "transiting");
        Intent intent = new Intent(this, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


}
