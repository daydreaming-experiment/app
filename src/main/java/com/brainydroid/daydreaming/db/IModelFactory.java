package com.brainydroid.daydreaming.db;

public interface IModelFactory<T extends Model> {

    public T create();

}
