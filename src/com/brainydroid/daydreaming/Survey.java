package com.brainydroid.daydreaming;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class Survey extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
		setContentView(R.layout.survey);	
	}
	
	public void exit(View v){
		finish();
	}

}
