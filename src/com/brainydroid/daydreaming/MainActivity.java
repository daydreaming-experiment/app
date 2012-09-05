package com.brainydroid.daydreaming;


import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	
	
	
    final int HELLO_ID = 1;
    NotificationManager mNotificationManager ;
    Notification notification;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Get a reference to the notification manager
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) getSystemService(ns);
        main();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@SuppressLint("ParserError")
	public void main(){
    	
    	 // Instantiate the notification
        int icon = R.drawable.notification_icon;
        CharSequence tickerText = "Hello";
        long when = System.currentTimeMillis();
        notification = new Notification(icon, tickerText, when);
    
        // Define the notification's message and PendingIntent
        Context context = getApplicationContext();
        CharSequence contentTitle = "My notification";
        CharSequence contentText = "Hello World!";
        Intent notificationIntent = new Intent(this, Survey.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        // Pass the notification to the notificationManager

    	
    	mNotificationManager.notify(HELLO_ID, notification);
    	
    	
    	
    }
	
	// First Page of the app 
	public void App_presentation(View v){
		setContentView(R.layout.presentation);	
		TextView msg = (TextView)findViewById(R.id.presentation_textView);
	}
	
	// Presentation of the data and privacy intentions
	public void data_and_privacy(View v){
		setContentView(R.layout.data_and_privacy);	
		TextView msg = (TextView)findViewById(R.id.data_and_privacy_textView);
		msg.setText(Text.readTxt(R.raw.data_and_privacy,this));   
	}
	
	// Presentation of the data and privacy intentions
		public void init_parameters(View v){
		setContentView(R.layout.init_parameters);	
		TextView msg = (TextView)findViewById(R.id.init_parameters_textView);
     	msg.setText(Text.readTxt(R.raw.init_parameters,this));   
		}
		
		public void questionnaire_init(View v){
		setContentView(R.layout.questionnaire_init);	
		TextView msg = (TextView)findViewById(R.id.questionnaire_init_textView);
     	msg.setText(Text.readTxt(R.raw.questionnaire_init,this));   
     	
     	// Here, find an interesting way to 
     	
     	
		}
	
	
}
