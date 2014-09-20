package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.widget.Toast;

import com.brainydroid.daydreaming.db.Json;
import com.brainydroid.daydreaming.network.ServerErrorWrapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.acra.ACRA;
import org.json.JSONException;

@Singleton
public class ErrorHandler {

    private static String TAG = "ErrorHandler";

    @Inject Context context;
    @Inject Json json;

    public synchronized void handleServerError(String serverAnswer, Exception parsingException) {
        Logger.e(TAG, "Attempting to parse an error from the server:");
        Logger.eRaw(TAG, serverAnswer);

        // Report this silently
        ACRA.getErrorReporter().handleSilentException(parsingException);

        Toast.makeText(context, "There was an error talking to the server, please try again later. " +
                        "The developers have been notified.",
                Toast.LENGTH_LONG).show();

        // If it's JSON, it could a standard error. Try to parse that
        try {
            ServerErrorWrapper errorWrap = json.fromJson(serverAnswer, ServerErrorWrapper.class);
            // The answer was indeed a JSON error. Toast it.
            errorWrap.getError().debugToastError();
        } catch (JSONException e) {
            // The answer was not a JSON error. Toast that.
            Logger.td(context, "Server answered an error we can't parse");
            Logger.tdRaw(context, serverAnswer);
        }
    }

    public synchronized void handleBaseJsonError(String errorJson, Exception parsingException) {
        Logger.e(TAG, "Handling JSON parsing error while parsing:");
        Logger.eRaw(TAG, errorJson);

        // Report this silently
        ACRA.getErrorReporter().handleSilentException(parsingException);
        Logger.td(context, "Internal error parsing JSON: {}",
                parsingException.getMessage());
    }

    public synchronized void logError(String error, Exception exception) {
        Logger.eRaw(TAG, error);
        ACRA.getErrorReporter().handleSilentException(exception);
    }
}
