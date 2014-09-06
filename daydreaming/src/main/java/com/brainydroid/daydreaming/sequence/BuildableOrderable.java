package com.brainydroid.daydreaming.sequence;

import java.util.ArrayList;

abstract public class BuildableOrderable<T> {

    private static String TAG = "BuildableOrderable";

    abstract public String getName();

    abstract public Position getPosition();

    abstract public T build(Sequence sequence);

}
