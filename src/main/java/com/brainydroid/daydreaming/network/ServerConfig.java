package com.brainydroid.daydreaming.network;

public class ServerConfig {

    public static String EXP_ID =
            "a5f0430af850a1c4cc278bf310379a0a65451a19c5a339d099fbb3c65cb14d2b";
    public static String SERVER_NAME = "http://api.qa.naja.cc";

    public static String QUESTIONS_URL = "https://raw.github.com/wehlutyk/daydreaming/feature/star-rating/res/raw/questions.json";
    //public static String QUESTIONS_URL = " https://raw.github.com/wehlutyk/daydreaming/master/res/raw/questions.json"

    public static String YE_URL_API = "/v1";
    public static String YE_URL_PROFILES = YE_URL_API + "/profiles";
    public static String YE_URL_RESULTS = YE_URL_API + "/results";

    public static int NETWORK_TIMEOUT = 10 * 1000; // 10 seconds

    // FIXME: put this in a proper place
    public static int EXP_DURATION_DAYS = 30;

}
