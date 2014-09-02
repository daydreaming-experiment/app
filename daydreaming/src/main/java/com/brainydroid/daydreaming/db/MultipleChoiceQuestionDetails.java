package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;

public class MultipleChoiceQuestionDetails implements IQuestionDetails {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "MultipleChoiceQuestionDetails";

    @SuppressWarnings("FieldCanBeLocal")
    private String type = "MultipleChoice";
    @SuppressWarnings("UnusedDeclaration")
    private String text = null;
    private String glossaryText = null;
    @SuppressWarnings("UnusedDeclaration")
    private ArrayList<String> choices = new ArrayList<String>();

    @Override
    public synchronized String getType() {
        return type;
    }

    public synchronized String getText() {
        return text;
    }

    public synchronized String getGlossaryText() {
        return glossaryText;
    }

    public synchronized ArrayList<String> getChoices() {
        return choices;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating question details");

        if (text == null) {
            throw new JsonParametersException("text in MultipleChoiceQuestionDetails "
                    + "can't be null");
        }
        if (choices.size() < 2) {
            throw new JsonParametersException("There must be at least two choices in "
                    + "a MultipleChoiceQuestionsDetails");
        }
    }

}
