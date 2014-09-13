package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;

public class AutoListQuestionDescriptionDetails implements IQuestionDescriptionDetails {

    private static String TAG = "AutoListQuestionDescriptionDetails";

    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private String type = "AutoList";
    @SuppressWarnings("UnusedDeclaration")
    @JsonView(Views.Internal.class)
    private String text = null;
    @SuppressWarnings("UnusedDeclaration")
    @JsonView(Views.Internal.class)
    private String hint = null;
    @JsonView(Views.Internal.class)
    private String glossaryText = null;
    @SuppressWarnings("UnusedDeclaration")
    @JsonView(Views.Internal.class)
    private ArrayList<String> possibilities = null;

    @Override
    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getHint() {
        return hint;
    }

    public String getGlossaryText() {
        return glossaryText;
    }

    public ArrayList<String> getPossibilities() {
        return possibilities;
    }

    @Override
    public void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating question details");

        if (text == null) {
            throw new JsonParametersException("text in AutoListQuestionDescriptionDetails "
                    + "can't be null");
        }

        if (possibilities == null) {
            throw new JsonParametersException("possibilities in " +
                    "AutoListQuestionDescriptionDetails can't by null");
        }
        if (possibilities.size() < 2) {
            throw new JsonParametersException("There must be at least two possibilities in "
                    + "a AutoListQuestionDescriptionDetails");
        }
    }
}
