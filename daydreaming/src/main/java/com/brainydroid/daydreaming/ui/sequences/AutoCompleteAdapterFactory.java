package com.brainydroid.daydreaming.ui.sequences;

import com.google.inject.assistedinject.Assisted;

import java.util.ArrayList;

public interface AutoCompleteAdapterFactory {

    public AutoCompleteAdapter create(@Assisted("possibilities") ArrayList<String> possibilities);

}
