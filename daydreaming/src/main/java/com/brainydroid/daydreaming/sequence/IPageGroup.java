package com.brainydroid.daydreaming.sequence;

import java.util.ArrayList;

abstract public interface IPageGroup {

    public String getName();

    public String getFriendlyName();

    public ArrayList<? extends IPage> getPages();

}
