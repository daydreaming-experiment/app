package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.db.Poll;
import com.brainydroid.daydreaming.network.ResultsArray;

import java.util.ArrayList;

public interface ResultsArrayFactory {

    public ResultsArray create(ArrayList<Poll> polls);

}
