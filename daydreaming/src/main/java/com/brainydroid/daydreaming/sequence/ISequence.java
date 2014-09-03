package com.brainydroid.daydreaming.sequence;

import java.util.ArrayList;

abstract public interface ISequence {

    public String getName();

    public String getType();

    public ArrayList<? extends IPageGroup> getPageGroups();

}
