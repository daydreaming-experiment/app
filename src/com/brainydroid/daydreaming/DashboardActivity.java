package com.brainydroid.daydreaming;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.Toast;

public class DashboardActivity extends Activity {

	private SharedPreferences mPrefs;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mPrefs = getPreferences(MODE_PRIVATE);
        if (!mPrefs.getBoolean(PreferenceKeys.HAS_FIRST_RUN, false)) {
        	// Debug
        	Toast.makeText(DashboardActivity.this, "Exp never run", Toast.LENGTH_SHORT).show();
        }
        
        setContentView(R.layout.activity_dashboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_day_dreaming, menu);
        return true;
    }
}
