package com.brainydroid.daydreaming.sequence;

public interface PreLoadable {

    public boolean isPreLoaded();

    public void onPreLoaded(PreLoadCallback preLoadCallback);

}
