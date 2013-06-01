package com.brainydroid.daydreaming.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_first_launch_description)
public class FirstLaunchDescriptionActivity extends FirstLaunchActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FirstLaunchDescriptionActivity";

    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Next button clicked -> launching consent dialog");

        SherlockDialogFragment consentAlert = ConsentAlertDialogFragment.newInstance(
                R.string.consentAlert_title,
                R.string.consentAlert_text,
                R.string.consentAlert_button_no_consent,
                R.string.consentAlert_button_consent);
        consentAlert.show(getSupportFragmentManager(), "consentAlert");
    }

    public static class ConsentAlertDialogFragment extends SherlockDialogFragment {

        private static String TAG = "ConsentAlertDialogFragment";

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

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Logger.v(TAG, "Creating consent alert dialog");

            int title = getArguments().getInt("title");
            int text = getArguments().getInt("text");
            int negText = getArguments().getInt("negText");
            int posText = getArguments().getInt("posText");

            AlertDialog.Builder alertSettings = new AlertDialog.Builder(getActivity())
            .setTitle(title)
            .setMessage(text)
            .setNegativeButton(negText,
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            })
            .setPositiveButton(posText,
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ((FirstLaunchDescriptionActivity)getActivity()).
                            launchNextActivity(FirstLaunchTermsActivity.class);
                }
            }).setIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    R.drawable.ic_action_about_holo_light : R.drawable.ic_action_about_holo_dark);

            return alertSettings.create();
        }

    }

}
