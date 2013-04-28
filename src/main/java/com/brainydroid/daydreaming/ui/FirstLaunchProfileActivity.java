package com.brainydroid.daydreaming.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.google.inject.Inject;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_first_launch_profile)
public class FirstLaunchProfileActivity extends FirstLaunchActivity {

    private static String TAG = "FirstLaunchProfileActivity";

    public static String PROFILE_AGE = "profileAge";
    public static String PROFILE_GENDER = "profileGender";

    @InjectView(R.id.firstLaunchProfile_editAge) EditText ageEditText;
    @InjectView(R.id.firstLaunchProfile_genderSpinner) Spinner genderSpinner;
    @Inject SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onCreate");
        }

        super.onCreate(savedInstanceState);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
    }

    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onClick_buttonNext");
        }

        if (!checkForm()) {
            Toast.makeText(this, getString(R.string.firstLaunchProfile_fix_age),
                    Toast.LENGTH_SHORT).show();
        } else {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(PROFILE_AGE, Integer.parseInt(ageEditText.getText().toString()));
            editor.putString(PROFILE_GENDER, genderSpinner.toString());
            editor.commit();

            Toast.makeText(this, genderSpinner.getSelectedItem().toString() +
                    ", " + ageEditText.getText().toString(), Toast.LENGTH_LONG).show();
            launchNextActivity(FirstLaunchPullActivity.class);
        }
    }

    private boolean checkForm() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] checkForm");
        }

        try {
            int age = Integer.parseInt(ageEditText.getText().toString());
            return (5 <= age && age <= 100);
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
