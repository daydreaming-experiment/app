package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.ArrayList;

public class ResultsWrapper<T> {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "ResultsWrapper";

    @JsonView(Views.Public.class)
    private ArrayList<Result<T>> results;

    public ResultsWrapper() {}

    @Inject
    public ResultsWrapper(CryptoStorage cryptoStorage,
                          @Assisted ArrayList<T> datas) {
        String maiId = cryptoStorage.getMaiId();
        results = new ArrayList<Result<T>>();
        for (T data : datas) {
            results.add(new Result<T>(maiId, data));
        }
    }

    public synchronized ArrayList<T> getDatas() {
        ArrayList<T> datas = new ArrayList<T>();
        for (Result<T> result : results) {
            datas.add(result.getData());
        }

        return datas;
    }

}
