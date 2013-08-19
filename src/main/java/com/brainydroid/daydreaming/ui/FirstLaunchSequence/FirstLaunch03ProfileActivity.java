package com.brainydroid.daydreaming.ui.FirstLaunchSequence;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.inject.InjectResource;

import static android.view.View.*;

/**
 * Activity at first launch
 * Asking a few questions about user
 *
 * In first launch sequence of apps
 *
 * Previous activity :  FirstLaunch02TermsActivity
 * This activity     :  FirstLaunch03ProfileActivity
 * Next activity     :  FirstLaunch04PersonalityQuestionnaireActivity
 *
 */
@ContentView(R.layout.activity_first_launch_profile)
public class FirstLaunch03ProfileActivity extends FirstLaunchActivity {

    private static String TAG = "FirstLaunch03ProfileActivity";

    public static String PROFILE_AGE = "profileAge";
    public static String PROFILE_GENDER = "profileGender";
    public static String PROFILE_EDUCATION = "profileEducation";

    String[] strings = {"--"," Female", " Male"};
    int arr_images[] = { R.drawable.empty ,R.drawable.icon_female, R.drawable.icon_male };


    public boolean GENDER_SPINNER_TOUCHED = false;
    public boolean AGE_SPINNER_TOUCHED = false;
    public boolean EDUCATION_SPINNER_TOUCHED = false;

    public SharedPreferences prefs;

    @InjectView(R.id.firstLaunchProfile_genderSpinner) Spinner genderSpinner;
    @InjectView(R.id.firstLaunchProfile_educationSpinner) Spinner educationSpinner;
    @InjectView(R.id.firstLaunchProfile_ageSpinner) Spinner ageSpinner;
    //@InjectResource(R.string.) SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");

        super.onCreate(savedInstanceState);
    //    addPreferencesFromResource(R.string.profileAge);
     //   addPreferencesFromResource(R.string.profileEducation);
    //    addPreferencesFromResource(R.string.profileGender);

        prefs = getPreferences(MODE_PRIVATE);

        populate_spinners();

    }



    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {
        Logger.v(TAG, "Next button clicked");


        //Toast.makeText(this,Boolean.toString(checkForm()),Toast.LENGTH_SHORT).show();

        if (!checkForm()) {
            Logger.d(TAG, "Form check failed");
        } else {
            Logger.i(TAG, "Saving profile information to shared " +
                    "preferences_appsettings");

          SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PROFILE_AGE, ageSpinner.getSelectedItem().toString());
            editor.putString(PROFILE_GENDER, genderSpinner.getSelectedItem().toString());
            editor.putString(PROFILE_EDUCATION, educationSpinner.getSelectedItem().toString());
            editor.commit();

            Logger.td(this, "{0}, {1}",
                    genderSpinner.getSelectedItem().toString(),
                    ageSpinner.getSelectedItem().toString());
            Logger.d(TAG, "Launching next activity");
            launchNextActivity(FirstLaunch04PersonalityQuestionnaireActivity.class);
        }
    }

    private boolean checkForm() {
        boolean b =   GENDER_SPINNER_TOUCHED && AGE_SPINNER_TOUCHED && EDUCATION_SPINNER_TOUCHED;
        if (!b){ Logger.d(TAG, "At least one untouched spinner");
              Toast.makeText(this,"Please answer all fields",Toast.LENGTH_SHORT).show();
        }
        return b;
    }

    /**
     * Creating adapters and listeners for each spinner
     */
    public void populate_spinners(){

        ArrayAdapter<CharSequence> adapter_gender = ArrayAdapter.createFromResource(this,
                R.array.genders, R.layout.spinner_layout);
        adapter_gender.setDropDownViewResource(R.layout.spinner_layout);
        //genderSpinner.setAdapter(adapter_gender);

        genderSpinner.setAdapter(new MyAdapter(getApplicationContext(), R.layout.spinner_layout_icon, strings));


        ArrayAdapter<CharSequence> adapter_education = ArrayAdapter.createFromResource(this,
                R.array.education, R.layout.spinner_layout);
        adapter_education.setDropDownViewResource(R.layout.spinner_layout);
        educationSpinner.setAdapter(adapter_education);

        ArrayAdapter<CharSequence> adapter_age = ArrayAdapter.createFromResource(this,
                R.array.ages, R.layout.spinner_layout);
        adapter_age.setDropDownViewResource(R.layout.spinner_layout);
        ageSpinner.setAdapter(adapter_age);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i>0){GENDER_SPINNER_TOUCHED = true;}
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {GENDER_SPINNER_TOUCHED = false;}
        });

        educationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i>0){EDUCATION_SPINNER_TOUCHED = true;}
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {EDUCATION_SPINNER_TOUCHED = false;}
        });

        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"Selected item:"+Integer.toString(i),Toast.LENGTH_SHORT).show();
                if (i>0){AGE_SPINNER_TOUCHED = true;}
               }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { AGE_SPINNER_TOUCHED = false;}
        });

        //Toast.makeText(getApplicationContext(),Boolean.toString(checkForm()),Toast.LENGTH_SHORT).show();


    }


    public class MyAdapter extends ArrayAdapter<String>{

        public MyAdapter(Context context, int textViewResourceId,   String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.spinner_layout_icon, parent, false);
            TextView label=(TextView)row.findViewById(R.id.spinnerTarget);
            label.setText(strings[position]);


            ImageView icon=(ImageView)row.findViewById(R.id.image);
            icon.setImageResource(arr_images[position]);

            return row;
        }
    }
}



