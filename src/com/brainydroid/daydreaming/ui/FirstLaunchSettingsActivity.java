package com.brainydroid.daydreaming.ui;



import com.brainydroid.daydreaming.R;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;

public class FirstLaunchSettingsActivity extends PreferenceActivity {
    @SuppressWarnings("deprecation")
	@Override
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preference);
        
        Preference button = (Preference)findPreference("button");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference arg0) { 
                        	launchDashboard();
                        	return true;
                        }
                    });
    }
    
    
    
    
    // TODO
    // - save and restore preference state (when screen rotates)
    // - save value for time window dialog
    // - listening for preference change ? (if time window is set to 7pm while a notification is scheduled for 8)
    // - status checks (good insertion in the firstlaunch sequence) 
    // DONE
    // - saving preference : automatic
    // - setting default values in xml
    
    private void launchDashboard() {
		Intent dashboardIntent = new Intent(this, DashboardActivity.class);
		dashboardIntent.putExtra(DashboardActivity.EXTRA_COMES_FROM_FIRST_LAUNCH, true);
		startActivity(dashboardIntent);
	}

    
    
}
