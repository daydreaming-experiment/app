package com.brainydroid.daydreaming.network;

public class ServerConfig {

    public static String YE_URL_API = "/v1";
    public static String YE_URL_PROFILES = YE_URL_API + "/profiles";
    public static String YE_URL_RESULTS = YE_URL_API + "/results";

    public static int NETWORK_TIMEOUT = 10 * 1000; // 10 seconds

    public static String PARAMETERS_URL_BASE =
            "https://raw.githubusercontent.com/daydreaming-experiment/parameters/feature/glossary/grammar-v2.1/glossary-{}.json";

}
