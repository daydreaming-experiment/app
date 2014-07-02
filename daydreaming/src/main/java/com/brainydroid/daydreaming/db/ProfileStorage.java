package com.brainydroid.daydreaming.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
    private static String PROFILE_PARAMETERS_VERSION =
            "profileParametersVersion";

    private boolean hasChangedSinceSyncStart = false;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor eSharedPreferences;

    @Inject ProfileFactory profileFactory;
    @Inject StatusManager statusManager;
    @Inject Context context;

    @SuppressLint("CommitPrefEdits")
    @Inject
    public ProfileStorage(SharedPreferences sharedPreferences) {
        Logger.d(TAG, "Creating");
        this.sharedPreferences = sharedPreferences;
        eSharedPreferences = sharedPreferences.edit();
    }

    public void setAge(String age) {
        Logger.d(TAG, "{} - Setting age to {}", statusManager.getCurrentModeName(), age);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + PROFILE_AGE, age);
        setIsDirtyAndCommit();
    }

    private String getAge() {
        return sharedPreferences.getString(statusManager.getCurrentModeName() + PROFILE_AGE, null);
    }

    public void setGender(String gender) {
        Logger.d(TAG, "{} - Setting gender to {}", statusManager.getCurrentModeName(), gender);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + PROFILE_GENDER, gender);
        setIsDirtyAndCommit();
    }

    private String getGender() {
        return sharedPreferences.getString(statusManager.getCurrentModeName() + PROFILE_GENDER, null);
    }

    public void setEducation(String education) {
        Logger.d(TAG, "{} - Setting education to {}", statusManager.getCurrentModeName(), education);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + PROFILE_EDUCATION, education);
        setIsDirtyAndCommit();
    }

    private String getEducation() {
        return sharedPreferences.getString(statusManager.getCurrentModeName() + PROFILE_EDUCATION, null);
    }

    public void setTipiAnswers(HashMap<String, Integer> tipiAnswers) {
        Logger.d(TAG, "{} - Setting tipi questionnaire answers", statusManager.getCurrentModeName());
        int index = 0;
        for (Map.Entry<String, Integer> answer : tipiAnswers.entrySet()) {
            eSharedPreferences.putString(
                    statusManager.getCurrentModeName() + PROFILE_TIPI_NAME_PREFIX + index, answer.getKey());
            eSharedPreferences.putInt(
                    statusManager.getCurrentModeName() + PROFILE_TIPI_ANSWER_PREFIX + index, answer.getValue());
            index++;
        }
        eSharedPreferences.putInt(statusManager.getCurrentModeName() + PROFILE_TIPI_NUMBER_OF_ANSWERS, index);
        setIsDirtyAndCommit();
    }

    private HashMap<String, Integer> getTipiAnswers() {
        Logger.d(TAG, "{} - Building tipiAnswers HashMap", statusManager.getCurrentModeName());

        HashMap<String, Integer> tipiAnswers =
                new HashMap<String, Integer>();
        int numberOfAnswers = sharedPreferences.getInt(statusManager.getCurrentModeName() +
                PROFILE_TIPI_NUMBER_OF_ANSWERS, 0);
        String questionName;
        int answer;

        for (int index = 0; index < numberOfAnswers; index++) {
            questionName = sharedPreferences.getString(
                    statusManager.getCurrentModeName() + PROFILE_TIPI_NAME_PREFIX + index, null);
            answer = sharedPreferences.getInt(
                    statusManager.getCurrentModeName() + PROFILE_TIPI_ANSWER_PREFIX + index, -1);
            tipiAnswers.put(questionName, answer);
        }

        return tipiAnswers;
    }

    private void clearTipiAnswers() {
        Logger.d(TAG, "{} - Clearing tipiAnswers", statusManager.getCurrentModeName());

        int numberOfAnswers = sharedPreferences.getInt(statusManager.getCurrentModeName() +
                PROFILE_TIPI_NUMBER_OF_ANSWERS, 0);
        for (int index = 0; index < numberOfAnswers; index++) {
            eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_TIPI_NAME_PREFIX + index);
        }

        eSharedPreferences.commit();
    }

    public void setParametersVersion(String version) {
        Logger.d(TAG, "{} - Setting parametersVersion to {}", statusManager.getCurrentModeName(), version);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + PROFILE_PARAMETERS_VERSION, version);
        setIsDirtyAndCommit();
    }

    private String getParametersVersion() {
        return sharedPreferences.getString(statusManager.getCurrentModeName() + PROFILE_PARAMETERS_VERSION, "not given");
    }

    private String getAppVersionName() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(TAG, "Package not found when retrieving app versionName");
            throw new RuntimeException(e);
        }
    }

    private int getAppVersionCode() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(TAG, "Package not found when retrieving app versionCode");
            throw new RuntimeException(e);
        }
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
                statusManager.getCurrentModeName());
        hasChangedSinceSyncStart = true;
        eSharedPreferences.putBoolean(statusManager.getCurrentModeName() + PROFILE_IS_DIRTY, true);
        eSharedPreferences.commit();
    }

    public void clearIsDirtyAndCommit() {
        Logger.d(TAG, "{} - Clearing isDirty and hasChangedSinceSyncStart flag and committing",
                statusManager.getCurrentModeName());
        hasChangedSinceSyncStart = false;
        eSharedPreferences.putBoolean(statusManager.getCurrentModeName() + PROFILE_IS_DIRTY, false);
        eSharedPreferences.commit();
    }

    public boolean isDirty() {
        return sharedPreferences.getBoolean(statusManager.getCurrentModeName() + PROFILE_IS_DIRTY, false);
    }

    public Profile getProfile() {
        Logger.d(TAG, "Building Profile instance from saved data");
        return profileFactory.create(getAge(), getGender(), getEducation(),
                getTipiAnswers(), getParametersVersion(), getAppVersionName(),
                getAppVersionCode(), statusManager.getCurrentModeName());
    }

    public boolean clearProfile() {
        Logger.d(TAG, "{} - Clearing profile", statusManager.getCurrentModeName());

        // First clear tipi answers, since it uses the tipiNumberOfQuestions key
        clearTipiAnswers();

        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_IS_DIRTY);
        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_AGE);
        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_GENDER);
        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_EDUCATION);
        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_TIPI_NUMBER_OF_ANSWERS);
        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_PARAMETERS_VERSION);
        eSharedPreferences.commit();
        return true;
    }
}
