package com.brainydroid.daydreaming.db;

abstract public class ModelFactory<M extends Model<M,S,F>,
        S extends ModelStorage<M,S,F>, F extends ModelFactory<M,S,F>> {

    private static String TAG = "ModelFactory";

    abstract public M createFromJson(String jsonContent);

}
