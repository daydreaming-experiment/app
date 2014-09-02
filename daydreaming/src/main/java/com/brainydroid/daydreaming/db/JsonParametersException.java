package com.brainydroid.daydreaming.db;

public class JsonParametersException extends RuntimeException {
    public JsonParametersException(String msg) {
        super(msg);
    }

    public JsonParametersException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JsonParametersException(Throwable cause) {
        super(cause);
    }
}
