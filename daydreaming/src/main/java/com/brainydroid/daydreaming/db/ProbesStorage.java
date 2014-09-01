package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.sequence.Probe;
import com.google.inject.Inject;
import com.google.inject.Provider;

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

}
