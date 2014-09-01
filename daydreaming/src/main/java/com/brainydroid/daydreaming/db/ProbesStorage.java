package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.Probe;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.ArrayList;

public class ProbesStorage extends StatusModelStorage<Probe,ProbesStorage,ProbeFactory> {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "ProbesStorage";

    private static final String TABLE_PROBES = "probes";

    @Inject Provider<ParametersStorage> questionsStorageProvider;
    @Inject ProbeFactory probeFactory;

    @Inject
    public ProbesStorage(Storage storage) {
        super(storage);
    }

    @Override
    protected synchronized String getTableName() {
        return TABLE_PROBES;
    }


    public synchronized ArrayList<Probe> getUploadableProbes() {
        Logger.v(TAG, "Getting uploadable polls");
        return getModelsByStatuses(
                new String[] {Poll.STATUS_COMPLETED, Poll.STATUS_PARTIALLY_COMPLETED});
    }

    public synchronized ArrayList<Probe> getPendingProbes() {
        Logger.d(TAG, "Getting pending polls");
        return getModelsByStatuses(new String[] {Poll.STATUS_PENDING});
    }

    public synchronized void removeUploadableProbes() {
        Logger.d(TAG, "Removing uploadable probes");
        remove(getUploadableProbes());
    }
}
