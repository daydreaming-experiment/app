package com.brainydroid.daydreaming;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;

public class FirstLaunchDescriptionActivity extends FragmentActivity {

	SharedPreferences mFLPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mFLPrefs = getSharedPreferences(getString(R.pref.firstLaunchPrefs), MODE_PRIVATE);

		setContentView(R.layout.activity_first_launch_description);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_first_launch_description, menu);
		return true;
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

	public void onClick_buttonNext(View view) {
		DialogFragment consentAlert = ConsentAlertDialogFragment.newInstance(
				R.string.consentAlert_title,
				R.string.consentAlert_text,
				R.string.consentAlert_button_notconsent,
				R.string.consentAlert_button_consent);
		consentAlert.show(getSupportFragmentManager(), "consentAlert");
	}

	private void launchMeasuresActivity() {
		Intent intent = new Intent(this, FirstLaunchMeasuresActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private void checkFirstRun() {
		if (mFLPrefs.getBoolean(getString(R.pref.firstLaunchCompleted), false)) {
			finish();
		}
	}

	public static class ConsentAlertDialogFragment extends DialogFragment {

		public static ConsentAlertDialogFragment newInstance(int title, int text,
				int negText, int posText) {
			ConsentAlertDialogFragment frag = new ConsentAlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			args.putInt("text", text);
			args.putInt("negText", negText);
			args.putInt("posText", posText);
			frag.setArguments(args);
			return frag;
		}

		@TargetApi(11)
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int title = getArguments().getInt("title");
			int text = getArguments().getInt("text");
			int negText = getArguments().getInt("negText");
			int posText = getArguments().getInt("posText");

			AlertDialog.Builder alertSettings = new AlertDialog.Builder(getActivity())
			.setTitle(title)
			.setMessage(text)
			.setNegativeButton(negText,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			})
			.setPositiveButton(posText,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					((FirstLaunchDescriptionActivity)getActivity()).launchMeasuresActivity();
				}
			});

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				alertSettings.setIconAttribute(android.R.attr.alertDialogIcon);
			} else {
				alertSettings.setIcon(android.R.drawable.ic_dialog_alert);
			}

			return alertSettings.create();
		}
	}
}