package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.PreLoadCallback;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.ArrayList;

public class MatrixChoiceQuestionDescriptionDetails implements IQuestionDescriptionDetails {

    @SuppressWarnings({"FieldCanBeLocal"})
    private static String TAG = "MatrixChoiceQuestionDescriptionDetails";

    public static String TYPE = "MatrixChoice";

    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private String type = TYPE;
    @SuppressWarnings("UnusedDeclaration")
    @JsonView(Views.Internal.class)
    private String text = null;
    @JsonView(Views.Internal.class)
    private String glossaryText = null;
    @SuppressWarnings("UnusedDeclaration")
    @JsonView(Views.Internal.class)
    private ArrayList<String> choices = null;

    @Override
    public synchronized boolean isPreLoaded() {
        return true;
    }

    @Override
    public synchronized void onPreLoaded(PreLoadCallback preLoadCallback) {
        // This question is always already loaded
        if (preLoadCallback != null) {
            preLoadCallback.onPreLoaded();
        }
    }

    @Override
    public synchronized Object getPreLoadedObject() {
        return null;
    }

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
            throw new JsonParametersException("text in MatrixChoiceQuestionDescriptionDetails "
                    + "can't be null");
        }

        if (choices == null) {
            throw new JsonParametersException("choices in MatrixChoiceQuestionDescriptionDetails " +
                    "can't by null");
        }
        if (choices.size() < 2) {
            throw new JsonParametersException("There must be at least two choices in "
                    + "a MatrixChoiceQuestionsDescriptionDetails");
        }
    }

}
