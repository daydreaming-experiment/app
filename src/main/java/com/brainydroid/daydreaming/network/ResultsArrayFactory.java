package com.brainydroid.daydreaming.network;

import java.util.ArrayList;

public interface ResultsArrayFactory<T> {

    public ResultsArray<T> create(ArrayList<T> datas);

}
