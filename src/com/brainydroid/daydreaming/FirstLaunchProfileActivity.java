package com.brainydroid.daydreaming;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class FirstLaunchProfileActivity extends Activity {

	private SharedPreferences mFLPrefs;
	private EditText ageEditText;
	private Spinner genderSpinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_launch_profile);

		mFLPrefs = getSharedPreferences(getString(R.pref.firstLaunchPrefs), MODE_PRIVATE);
		ageEditText = (EditText)findViewById(R.id.firstLaunchProfile_editAge);

		genderSpinner = (Spinner)findViewById(R.id.firstLaunchProfile_genderSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.genders, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genderSpinner.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		checkFirstRun();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	private void checkFirstRun() {
		if (mFLPrefs.getBoolean(getString(R.pref.firstLaunchCompleted), false)) {
			finish();
		}
	}

	public void onClick_buttonNext(View view) {
		if (!checkForm()) {
			Toast.makeText(this, getString(R.string.firstLaunchProfile_fix_age),
					Toast.LENGTH_SHORT).show();
		} else {
			launchMeasuresActivity();
		}
	}

	private boolean checkForm() {
		try {
			int age = Integer.parseInt(ageEditText.getText().toString());
			return (5 <= age && age <= 100);
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private void launchMeasuresActivity() {
		Intent intent = new Intent(this, FirstLaunchMeasuresActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
}