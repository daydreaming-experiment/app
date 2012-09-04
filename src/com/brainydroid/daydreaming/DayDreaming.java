package com.brainydroid.daydreaming;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DayDreaming extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_dreaming);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_day_dreaming, menu);
        return true;
    }
}
