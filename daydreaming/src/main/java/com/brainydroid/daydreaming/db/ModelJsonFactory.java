package com.brainydroid.daydreaming.db;

abstract public class ModelJsonFactory<M extends Model<M,S,F>,
        S extends ModelStorage<M,S,F>, F extends ModelJsonFactory<M,S,F>> {

    private static String TAG = "ModelJsonFactory";

    abstract public M createFromJson(String jsonContent);

}
