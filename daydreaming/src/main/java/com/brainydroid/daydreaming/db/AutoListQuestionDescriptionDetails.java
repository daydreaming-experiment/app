package com.brainydroid.daydreaming.db;

import android.os.AsyncTask;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.PreLoadCallback;
import com.brainydroid.daydreaming.sequence.PreLoadable;
import com.brainydroid.daydreaming.ui.filtering.AutoCompleteAdapter;
import com.brainydroid.daydreaming.ui.filtering.AutoCompleteAdapterFactory;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashSet;

public class AutoListQuestionDescriptionDetails implements IQuestionDescriptionDetails, PreLoadable {

    private static String TAG = "AutoListQuestionDescriptionDetails";

    public static String TYPE = "AutoList";

    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private String type = TYPE;
    @SuppressWarnings("UnusedDeclaration")
    @JsonView(Views.Internal.class)
    private String text = null;
    @SuppressWarnings("UnusedDeclaration")
    @JsonView(Views.Internal.class)
    private String hint = null;
    @SuppressWarnings("UnusedDeclaration")
    @JsonView(Views.Internal.class)
    private ArrayList<String> possibilities = null;

    @Inject AutoCompleteAdapterFactory autoCompleteAdapterFactory;
    private boolean isPreLoaded = false;
    private boolean isPreLoading = false;
    @Inject private HashSet<PreLoadCallback> preLoadCallbacks;
    private AutoCompleteAdapter autoCompleteAdapter;

    @Override
    public synchronized boolean isPreLoaded() {
        return isPreLoaded;
    }

    @Override
    public synchronized void onPreLoaded(final PreLoadCallback preLoadCallback) {
        if (isPreLoaded) {
            Logger.v(TAG, "Question already pre-loaded -> calling possible callback");
            if (preLoadCallback != null) {
                preLoadCallback.onPreLoaded();
            }
        } else {
            if (preLoadCallback != null) {
                preLoadCallbacks.add(preLoadCallback);
            }

            if (isPreLoading) {
                Logger.v(TAG, "Already pre-loading, recorded potential additional callback");
            } else {
                Logger.v(TAG, "Pre-loading");
                isPreLoading = true;

                final AutoCompleteAdapter adapter = autoCompleteAdapterFactory.create();
                final ArrayList<String> initialPossibilities = getPossibilities();
                (new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Logger.v(TAG, "Initializing adapter");
                        adapter.initialize(initialPossibilities);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        Logger.v(TAG, "AutoList question pre-loaded -> calling possible callbacks");
                        isPreLoaded = true;
                        isPreLoading = false;
                        autoCompleteAdapter = adapter;

                        // Only non-null callbacks are stored
                        for (PreLoadCallback storedCallback : preLoadCallbacks) {
                            storedCallback.onPreLoaded();
                        }
                        preLoadCallbacks = new HashSet<PreLoadCallback>();
                    }
                }).execute();
            }
        }
    }

    @Override
    public synchronized Object getPreLoadedObject() {
        return autoCompleteAdapter;
    }

    @Override
    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getHint() {
        return hint;
    }

    public ArrayList<String> getPossibilities() {
        return possibilities;
    }

    @Override
    public void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating question details");

        if (text == null) {
            throw new JsonParametersException("text in AutoListQuestionDescriptionDetails "
                    + "can't be null");
        }

        if (possibilities == null) {
            throw new JsonParametersException("possibilities in " +
                    "AutoListQuestionDescriptionDetails can't by null");
        }
        if (possibilities.size() < 2) {
            throw new JsonParametersException("There must be at least two possibilities in "
                    + "a AutoListQuestionDescriptionDetails");
        }
    }
}
