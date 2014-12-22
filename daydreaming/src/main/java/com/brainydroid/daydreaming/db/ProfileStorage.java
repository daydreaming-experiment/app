package com.brainydroid.daydreaming.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.brainydroid.daydreaming.background.ErrorHandler;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.network.Profile;
import com.brainydroid.daydreaming.network.ProfileFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.json.JSONException;

import java.util.HashMap;

@Singleton
public class ProfileStorage {

    private static String TAG = "ProfileStorage";

    private static String PROFILE_IS_DIRTY = "profileIsDirty";
    private static String PROFILE_AGE = "profileAge";
    private static String PROFILE_GENDER = "profileGender";
    private static String PROFILE_EDUCATION = "profileEducation";
    private static String PROFILE_MOTHER_TONGUE = "profileMotherTongue";
    public static String PROFILE_HASHMAP_BOTHER_TIME = "profileHashmapBotherTime";

    public static String PROFILE_PARAMETERS_VERSION =
            "profileParametersVersion";

    public static String EMPTY_STRING = "";


    private boolean hasChangedSinceSyncStart = false;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor eSharedPreferences;

    @Inject ProfileFactory profileFactory;
    @Inject StatusManager statusManager;
    @Inject Provider<ParametersStorage> parametersStorageProvider;
    @Inject Context context;
    @Inject Json json;
    @Inject ErrorHandler errorHandler;


    @SuppressLint("CommitPrefEdits")
    @Inject
    public ProfileStorage(SharedPreferences sharedPreferences) {
        Logger.d(TAG, "Creating");
        this.sharedPreferences = sharedPreferences;
        eSharedPreferences = sharedPreferences.edit();
    }

    public void setAge(String age) {
        Logger.d(TAG, "{0} - Setting age to {1}", statusManager.getCurrentModeName(), age);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + PROFILE_AGE, age);
        setIsDirtyAndCommit();
    }

    private String getAge() {
        return sharedPreferences.getString(statusManager.getCurrentModeName() + PROFILE_AGE, null);
    }

    public void setGender(String gender) {
        Logger.d(TAG, "{0} - Setting gender to {1}", statusManager.getCurrentModeName(), gender);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + PROFILE_GENDER, gender);
        setIsDirtyAndCommit();
    }

    private String getGender() {
        return sharedPreferences.getString(statusManager.getCurrentModeName() + PROFILE_GENDER, null);
    }

    public void setEducation(String education) {
        Logger.d(TAG, "{0} - Setting education to {1}", statusManager.getCurrentModeName(), education);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + PROFILE_EDUCATION, education);
        setIsDirtyAndCommit();
    }

    private String getEducation() {
        return sharedPreferences.getString(statusManager.getCurrentModeName() + PROFILE_EDUCATION, null);
    }

    public void setMotherTongue(String motherTongue) {
        Logger.d(TAG, "{0} - Setting motherTongue to {1}", statusManager.getCurrentModeName(), motherTongue);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + PROFILE_MOTHER_TONGUE, motherTongue);
        setIsDirtyAndCommit();
    }

    private String getMotherTongue() {
        return sharedPreferences.getString(statusManager.getCurrentModeName() + PROFILE_MOTHER_TONGUE, null);
    }

    public void setParametersVersion(String version) {
        Logger.d(TAG, "{0} - Setting parametersVersion to {1}", statusManager.getCurrentModeName(), version);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + PROFILE_PARAMETERS_VERSION, version);
        setIsDirtyAndCommit();
    }

    public String getParametersVersion() {
        return sharedPreferences.getString(
                statusManager.getCurrentModeName() + PROFILE_PARAMETERS_VERSION,
                ServerParametersJson.DEFAULT_PARAMETERS_VERSION);
    }

    public synchronized void clearParametersVersion() {
        Logger.d(TAG, "{} - Clearing parametersVersion", statusManager.getCurrentModeName());
        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_PARAMETERS_VERSION);
        setIsDirtyAndCommit();
    }

    public synchronized String getBotherWindowMapJson() {
        String botherWindowMapJson = sharedPreferences.getString(statusManager.getCurrentModeName() + PROFILE_HASHMAP_BOTHER_TIME, EMPTY_STRING);
        Logger.d(TAG, "{0} - botherWindowMapJson is {1}", statusManager.getCurrentModeName(), botherWindowMapJson);
        return botherWindowMapJson;
    }

    public synchronized HashMap<String, String> getBotherWindowMap() {
        String botherWindowMapJson = getBotherWindowMapJson();
        HashMap<String, String> botherWindowMap;
        if (botherWindowMapJson.equals(EMPTY_STRING) || botherWindowMapJson == null) {
            botherWindowMap = new HashMap<String, String>();
        } else {
            try {
                botherWindowMap = json.fromJson(botherWindowMapJson,
                        new TypeReference<HashMap<String, String>>() {
                        }
                );
            } catch (JSONException e) {
                errorHandler.handleBaseJsonError(botherWindowMapJson, e);
                throw new RuntimeException(e);
            }
        }
        return botherWindowMap;
    }

    public synchronized void setBotherWindowMap(HashMap<String, String> botherWindowMap) {
        String botherWindowMapJson = json.toJsonInternal(botherWindowMap);
        Logger.d(TAG, "{0} - Setting botherWindowMapJson to {1}", statusManager.getCurrentModeName(), botherWindowMapJson);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + PROFILE_HASHMAP_BOTHER_TIME, botherWindowMapJson);
        setIsDirtyAndCommit();
    }

    public String getAppVersionName() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(TAG, "Package not found when retrieving app versionName");
            throw new RuntimeException(e);
        }
    }

    public int getAppVersionCode() {
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

    public void setIsDirtyAndCommit() {
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
        return profileFactory.create(parametersStorageProvider.get().getBackendExpId(),
                getAge(), getGender(), getEducation(), getMotherTongue(),
                getParametersVersion(), getAppVersionName(),
                getAppVersionCode(), statusManager.getCurrentModeName(),
                getBotherWindowMapJson());
    }

    public boolean clearProfile() {
        Logger.d(TAG, "{} - Clearing profile", statusManager.getCurrentModeName());

        // Clear parameters storage
        statusManager.clearParametersUpdated();
        parametersStorageProvider.get().flush();

        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_IS_DIRTY);
        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_AGE);
        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_GENDER);
        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_MOTHER_TONGUE);
        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_EDUCATION);
        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_PARAMETERS_VERSION);
        eSharedPreferences.remove(statusManager.getCurrentModeName() + PROFILE_HASHMAP_BOTHER_TIME);
        eSharedPreferences.commit();
        return true;
    }
}
