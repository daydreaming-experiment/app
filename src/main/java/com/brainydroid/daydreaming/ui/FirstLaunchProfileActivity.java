package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.StatusManager;

public class FirstLaunchProfileActivity extends SherlockActivity {

	private static String TAG = "FirstLaunchProfileActivity";

    public static String PROFILE_PREFERENCES = "profilePreferences";
    public static String PROFILE_AGE = "profileAge";
    public static String PROFILE_GENDER = "profileGender";

    private StatusManager status;

	private EditText ageEditText;
	private Spinner genderSpinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_launch_profile);

		status = StatusManager.getInstance(this);

		ageEditText = (EditText)findViewById(R.id.firstLaunchProfile_editAge);
		genderSpinner = (Spinner)findViewById(R.id.firstLaunchProfile_genderSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.genders, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genderSpinner.setAdapter(adapter);
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

	private void checkFirstLaunch() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] checkFirstLaunch");
		}

		if (status.isFirstLaunchCompleted()) {
			finish();
		}
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

            SharedPreferences sharedPrefs = getSharedPreferences(PROFILE_PREFERENCES, 0);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt(PROFILE_AGE, Integer.parseInt(ageEditText.getText().toString()));
            editor.putString(PROFILE_GENDER, genderSpinner.toString());
            editor.commit();

            Toast.makeText(this, genderSpinner.getSelectedItem().toString() +
                    ", " + ageEditText.getText().toString(), Toast.LENGTH_LONG).show();
            //launchMeasuresActivity();
            launchPullActivity();
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

    private void launchPullActivity() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] launchPullActivity");
        }

        Intent intent = new Intent(this, FirstLaunchPullActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
