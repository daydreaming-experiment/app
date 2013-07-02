package com.brainydroid.daydreaming.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_first_launch_profile)
public class FirstLaunchProfileActivity extends FirstLaunchActivity {

    private static String TAG = "FirstLaunchProfileActivity";

//    public static String PROFILE_AGE = "profileAge";
//    public static String PROFILE_GENDER = "profileGender";

    //@InjectView(R.id.firstLaunchProfile_editAge) EditText ageEditText;
    @InjectView(R.id.firstLaunchProfile_genderSpinner) Spinner genderSpinner;
    @InjectView(R.id.firstLaunchProfile_educationSpinner) Spinner educationSpinner;
    @InjectView(R.id.firstLaunchProfile_ageSpinner) Spinner ageSpinner;
//    @Inject SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");

        super.onCreate(savedInstanceState);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        genderSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.education, R.layout.spinner_layout);
        adapter1.setDropDownViewResource(R.layout.spinner_layout);
        educationSpinner.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.ages, R.layout.spinner_layout);
        adapter2.setDropDownViewResource(R.layout.spinner_layout);
        ageSpinner.setAdapter(adapter2);




    }

    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {
        Logger.v(TAG, "Next button clicked");

        if (!checkForm()) {
            Logger.d(TAG, "Form check failed");
            Toast.makeText(this, getString(R.string.firstLaunchProfile_fix_age),
                    Toast.LENGTH_SHORT).show();
        } else {
            Logger.i(TAG, "Saving profile information to shared " +
                    "preferences");

      //      SharedPreferences.Editor editor = sharedPreferences.edit();
      //      editor.putInt(PROFILE_AGE, Integer.parseInt(ageEditText.getText().toString()));
      //      editor.putString(PROFILE_GENDER, genderSpinner.toString());
      //      editor.commit();

            Logger.td(this, "{0}, {1}",
                    genderSpinner.getSelectedItem().toString(),
                    ageSpinner.getSelectedItem().toString());
            Logger.d(TAG, "Launching next activity");
            launchNextActivity(FirstLaunchPullActivity.class);
        }
    }

    private boolean checkForm() {
        try {
      //      int age = Integer.parseInt(ageEditText.getText().toString());
      //      return (5 <= age && age <= 100);
            return true; // to change
        } catch (NumberFormatException e) {
            Logger.d(TAG, "Form does not contain a number -> returning " +
                    "failure");
            return false;
        }
    }

}
