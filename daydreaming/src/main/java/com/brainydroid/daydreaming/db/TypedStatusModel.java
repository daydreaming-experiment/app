package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class TypedStatusModel<M extends TypedStatusModel<M,S,F>,
        S extends TypedStatusModelStorage<M,S,F>, F extends ModelJsonFactory<M,S,F>>
        extends StatusModel<M,S,F> {

    private static String TAG = "TypedStatusModel";

    @JsonProperty protected String type;

    protected synchronized void setType(String type) {
        Logger.v(TAG, "Setting type");
        this.type = type;
        saveIfSync();
    }

    public synchronized String getType() {
        return type;
    }

}
