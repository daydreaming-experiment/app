package com.brainydroid.daydreaming.network;

public class ServerConfig {

    public static String EXP_ID = "6cb5e7782ca43681d6349a2280a8f99f74479d142971ac6c91dbd155ac58b4b3";
    public static String SERVER_NAME = "http://yelandur.herokuapp.com";

    public static String QUESTIONS_VERSION_URL = "http://mehho.net:5001/questionsVersion";
    public static String QUESTIONS_URL = "http://mehho.net:5001/questions.json";

    public static String YE_URL_API = "/v1";
    public static String YE_URL_DEVICES = YE_URL_API + "/devices/";
    public static String YE_EXPS = "/exps/";
    public static String YE_RESULTS = "/results/";

    public static int NETWORK_TIMEOUT = 10 * 1000; // 10 seconds

}
