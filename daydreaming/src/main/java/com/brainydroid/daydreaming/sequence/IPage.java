package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.db.QuestionPositionDescription;

import java.util.ArrayList;

abstract public interface IPage {

    public ArrayList<QuestionPositionDescription> getQuestions();

}
