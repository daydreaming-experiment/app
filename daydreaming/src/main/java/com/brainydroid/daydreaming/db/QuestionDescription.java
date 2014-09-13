package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.IQuestion;
import com.brainydroid.daydreaming.sequence.QuestionBuilder;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

public class QuestionDescription implements IQuestion {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionDescription";

    @JsonView(Views.Internal.class)
    private String name = null;
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXTERNAL_PROPERTY, property="type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value=SliderQuestionDescriptionDetails.class, name="slider"),
            @JsonSubTypes.Type(value=StarRatingQuestionDescriptionDetails.class, name="starRating"),
            @JsonSubTypes.Type(value=MultipleChoiceQuestionDescriptionDetails.class, name="multipleChoice"),
            @JsonSubTypes.Type(value=MatrixChoiceQuestionDescriptionDetails.class, name="matrixChoice"),
            @JsonSubTypes.Type(value=AutoListQuestionDescriptionDetails.class, name="autoList")})
    @JsonView(Views.Internal.class)
    private IQuestionDescriptionDetails details = null;

    @Inject @JacksonInject private QuestionBuilder questionBuilder;

    public String getQuestionName() {
        return name;
    }

    public IQuestionDescriptionDetails getDetails() {
        return details;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating question");

        // Check root parameters
        if (name == null) {
            throw new JsonParametersException("name in question can't be null");
        }
        if (details == null) {
            throw new JsonParametersException("details in question can't be null");
        }

        // Check the details
        details.validateInitialization();
    }

}
