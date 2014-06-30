package com.brainydroid.daydreaming.network;

public class ServerConfig {

    public static String YE_URL_API = "/v1";
    public static String YE_URL_PROFILES = YE_URL_API + "/profiles";
    public static String YE_URL_RESULTS = YE_URL_API + "/results";

    public static int NETWORK_TIMEOUT = 10 * 1000; // 10 seconds
    
    public static String PARAMETERS_URL_BASE =
            "https://raw.github.com/daydreaming-experiment/parameters/master/grammar-v2/{}.json";

    // FIXME: move all this into parameters file in grammar v2
    public static int EXP_DURATION_DAYS = 30;
    public static String EXP_ID =
            "a5f0430af850a1c4cc278bf310379a0a65451a19c5a339d099fbb3c65cb14d2b";
    public static String SERVER_NAME = "http://api.qa.naja.cc";

}
