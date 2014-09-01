package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.sequence.BuildableOrderable;
import com.brainydroid.daydreaming.sequence.IPage;
import com.brainydroid.daydreaming.sequence.Page;
import com.brainydroid.daydreaming.sequence.PageBuilder;
import com.google.inject.Inject;

import java.util.ArrayList;

public class PageDescription extends BuildableOrderable<Page> implements IPage {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "PageDescription";

    private String name = null;
    private String position = null;
    private int nSlots = -1;
    private ArrayList<QuestionDescription> questions = new ArrayList<QuestionDescription>();
    @Inject private transient PageBuilder pageBuilder;

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public int getNSlots() {
        return nSlots;
    }

    public ArrayList<QuestionDescription> getQuestions() {
        return questions;
    }

    public Page build() {
        return pageBuilder.build(this);
    }

}
