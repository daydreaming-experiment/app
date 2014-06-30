package com.brainydroid.daydreaming.network;

import java.util.ArrayList;

public interface ResultsWrapperFactory<T> {

    public ResultsWrapper<T> create(ArrayList<T> datas);

}
