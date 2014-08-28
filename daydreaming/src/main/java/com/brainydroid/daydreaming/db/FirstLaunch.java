package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

public class FirstLaunch {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FirstLaunch";

    public static String DEFAULT_WELCOME_TEXT = "n/c";
    public static String DEFAULT_DESCRIPTION_TEXT = "n/c";

    private String welcomeText = null;
    private String descriptionText = null;
    private TipiQuestionnaire tipiQuestionnaire = null;

    public synchronized String getWelcomeText() {
        return welcomeText;
    }

    public synchronized String getDescriptionText() {
        return descriptionText;
    }

    public synchronized TipiQuestionnaire getTipiQuestionnaire() {
        return tipiQuestionnaire;
    }

    public synchronized void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText;
    }

    public synchronized void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating firstLaunch");

        // Check firstLaunch.welcomeText
        if (welcomeText.equals(DEFAULT_WELCOME_TEXT)) {
            throw new JsonParametersException("firstLaunch.welcomeText can't be its unset value");
        }

        // Check firstLaunch.descriptionText
        if (descriptionText.equals(DEFAULT_DESCRIPTION_TEXT)) {
            throw new JsonParametersException("firstLaunch.descriptionText can't be its unset value");
        }

        tipiQuestionnaire.validateInitialization();
    }

}
