package com.brainydroid.daydreaming.db;

import android.content.SharedPreferences;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.network.Profile;
import com.brainydroid.daydreaming.network.ProfileFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ProfileStorage {

    private static String TAG = "ProfileStorage";

    private static String PROFILE_IS_DIRTY = "profileIsDirty";
    private static String PROFILE_AGE = "profileAge";
    private static String PROFILE_GENDER = "profileGender";
    private static String PROFILE_EDUCATION = "profileEducation";
    private static String PROFILE_TIPI_NAME_PREFIX = "profileTipiQuestionName";
    private static String PROFILE_TIPI_ANSWER_PREFIX = "profileTipiAnswer";
    private static String PROFILE_TIPI_NUMBER_OF_ANSWERS =
            "profileTipiNumberOfAnswers";
    private static String PROFILE_QUESTIONS_VERSION =
            "profileQuestionsVersion";

    public static String PROFILE_NAME_TEST = "test";
    public static String PROFILE_NAME_PROD = "prod";

    private boolean hasChangedSinceSyncStart = false;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor eSharedPreferences;

    @Inject ProfileFactory profileFactory;
    @Inject StatusManager statusManager;

    @Inject
    public ProfileStorage(SharedPreferences sharedPreferences) {
        Logger.d(TAG, "Creating");
        this.sharedPreferences = sharedPreferences;
        eSharedPreferences = sharedPreferences.edit();
    }

    public void setAge(String age) {
        Logger.d(TAG, "{} - Setting age to {}", statusManager.getProfileName(), age);
        eSharedPreferences.putString(statusManager.getProfileName() + PROFILE_AGE, age);
        setIsDirtyAndCommit();
    }

    private String getAge() {
        return sharedPreferences.getString(statusManager.getProfileName() + PROFILE_AGE, null);
    }

    public void setGender(String gender) {
        Logger.d(TAG, "{} - Setting gender to {}", statusManager.getProfileName(), gender);
        eSharedPreferences.putString(statusManager.getProfileName() + PROFILE_GENDER, gender);
        setIsDirtyAndCommit();
    }

    private String getGender() {
        return sharedPreferences.getString(statusManager.getProfileName() + PROFILE_GENDER, null);
    }

    public void setEducation(String education) {
        Logger.d(TAG, "{} - Setting education to {}", statusManager.getProfileName(), education);
        eSharedPreferences.putString(statusManager.getProfileName() + PROFILE_EDUCATION, education);
        setIsDirtyAndCommit();
    }

    private String getEducation() {
        return sharedPreferences.getString(statusManager.getProfileName() + PROFILE_EDUCATION, null);
    }

    public void setTipiAnswers(HashMap<String, Integer> tipiAnswers) {
        Logger.d(TAG, "{} - Setting tipi questionnaire answers", statusManager.getProfileName());
        int index = 0;
        for (Map.Entry<String, Integer> answer : tipiAnswers.entrySet()) {
            eSharedPreferences.putString(
                    statusManager.getProfileName() + PROFILE_TIPI_NAME_PREFIX + index, answer.getKey());
            eSharedPreferences.putInt(
                    statusManager.getProfileName() + PROFILE_TIPI_ANSWER_PREFIX + index, answer.getValue());
            index++;
        }
        eSharedPreferences.putInt(statusManager.getProfileName() + PROFILE_TIPI_NUMBER_OF_ANSWERS, index);
        setIsDirtyAndCommit();
    }

    private HashMap<String, Integer> getTipiAnswers() {
        Logger.d(TAG, "{} - Building tipiAnswers HashMap", statusManager.getProfileName());

        HashMap<String, Integer> tipiAnswers =
                new HashMap<String, Integer>();
        int numberOfAnswers = sharedPreferences.getInt(statusManager.getProfileName() +
                PROFILE_TIPI_NUMBER_OF_ANSWERS, 0);
        String questionName;
        int answer;

        for (int index = 0; index < numberOfAnswers; index++) {
            questionName = sharedPreferences.getString(
                    statusManager.getProfileName() + PROFILE_TIPI_NAME_PREFIX + index, null);
            answer = sharedPreferences.getInt(
                    statusManager.getProfileName() + PROFILE_TIPI_ANSWER_PREFIX + index, -1);
            tipiAnswers.put(questionName, answer);
        }

        return tipiAnswers;
    }

    public void setQuestionsVersion(int questionsVersion) {
        Logger.d(TAG, "{} - Setting questionsVersion to {}", statusManager.getProfileName(), questionsVersion);
        eSharedPreferences.putInt(statusManager.getProfileName() + PROFILE_QUESTIONS_VERSION, questionsVersion);
        setIsDirtyAndCommit();
    }

    private int getQuestionsVersion() {
        return sharedPreferences.getInt(statusManager.getProfileName() + PROFILE_QUESTIONS_VERSION, -1);
    }

    public void setSyncStart() {
        Logger.d(TAG, "Setting hasChangedSinceSyncStart to false");
        hasChangedSinceSyncStart = false;
    }

    public boolean hasChangedSinceSyncStart() {
        return hasChangedSinceSyncStart;
    }

    private void setIsDirtyAndCommit() {
        Logger.d(TAG, "{} - Setting isDirty and hasChangedSinceSyncStart flags and committing",
                statusManager.getProfileName());
        hasChangedSinceSyncStart = true;
        eSharedPreferences.putBoolean(statusManager.getProfileName() + PROFILE_IS_DIRTY, true);
        eSharedPreferences.commit();
    }

    public void clearIsDirtyAndCommit() {
        Logger.d(TAG, "{} - Clearing isDirty and hasChangedSinceSyncStart flag and committing",
                statusManager.getProfileName());
        hasChangedSinceSyncStart = false;
        eSharedPreferences.putBoolean(statusManager.getProfileName() + PROFILE_IS_DIRTY, false);
        eSharedPreferences.commit();
    }

    public boolean isDirty() {
        return sharedPreferences.getBoolean(statusManager.getProfileName() + PROFILE_IS_DIRTY, false);
    }

    public Profile getProfile() {
        Logger.d(TAG, "Building Profile instance from saved data");
        return profileFactory.create(getAge(), getGender(), getEducation(),
                getTipiAnswers(), getQuestionsVersion());
    }

    public boolean clearProfile(){
        eSharedPreferences.remove(statusManager.getProfileName() + PROFILE_IS_DIRTY);
        eSharedPreferences.remove(statusManager.getProfileName() + PROFILE_AGE);
        eSharedPreferences.remove(statusManager.getProfileName() + PROFILE_GENDER);
        eSharedPreferences.remove(statusManager.getProfileName() + PROFILE_EDUCATION);
        eSharedPreferences.remove(statusManager.getProfileName() + PROFILE_TIPI_NAME_PREFIX);
        eSharedPreferences.remove(statusManager.getProfileName() + PROFILE_TIPI_ANSWER_PREFIX);
        eSharedPreferences.remove(statusManager.getProfileName() + PROFILE_TIPI_NUMBER_OF_ANSWERS);
        eSharedPreferences.remove(statusManager.getProfileName() + PROFILE_QUESTIONS_VERSION);
        eSharedPreferences.commit();
        return true;
    }
}
