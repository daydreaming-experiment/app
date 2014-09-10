package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.IQuestionDescriptionDetails;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.db.QuestionDescription;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.db.Views;
import com.brainydroid.daydreaming.ui.sequences.BaseQuestionViewAdapter;
import com.brainydroid.daydreaming.ui.sequences.IQuestionViewAdapter;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;
import com.google.inject.Injector;

// TODO: add some way to saveIfSync the phone's timezone and the user's
// preferences_appSettings
// about what times he allowed notifications to appear at.
public class Question implements IQuestion {

    private static String TAG = "Question";

    @JsonView(Views.Public.class)
    protected String questionName = null;
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXTERNAL_PROPERTY, property="type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value=SliderAnswer.class, name="slider"),
            @JsonSubTypes.Type(value=StarRatingAnswer.class, name="starRating"),
            @JsonSubTypes.Type(value=MultipleChoiceAnswer.class, name="multipleChoiceAnswer"),
            @JsonSubTypes.Type(value=MatrixChoiceAnswer.class, name="matrixChoiceAnswer")})
    @JsonView(Views.Public.class)
    private IAnswer answer = null;

    @JsonView(Views.Internal.class)
    private int sequenceId = -1;

    private Sequence sequenceCache = null;
    private IQuestionDescriptionDetails detailsCache = null;

    @Inject @JacksonInject private Injector injector;
    @Inject @JacksonInject private SequencesStorage sequencesStorage;
    @Inject @JacksonInject private ParametersStorage parametersStorage;

    public synchronized void importFromQuestionDescription(QuestionDescription description) {
        Logger.d(TAG, "Importing information from QuestionDescription");
        setQuestionName(description.getQuestionName());
        detailsCache = description.getDetails();
    }

    public synchronized IQuestionViewAdapter getAdapter() {
        String logSuffix = "for question " + questionName + " of type " + getDetails().getType();
        Logger.d(TAG, "Getting adapter {}", logSuffix);

        String packagePrefix = BaseQuestionViewAdapter.class.getPackage().getName() + ".";
        try {
            Class klass = Class.forName(packagePrefix + getDetails().getType() +
                    BaseQuestionViewAdapter.QUESTION_VIEW_ADAPTER_SUFFIX);
            IQuestionViewAdapter questionViewAdapter = (IQuestionViewAdapter)klass.newInstance();
            questionViewAdapter.setQuestion(this);
            injector.injectMembers(questionViewAdapter);
            return questionViewAdapter;
        } catch (ClassNotFoundException e) {
            Logger.e(TAG, "Could not find adapter class {}", logSuffix);
            e.printStackTrace();
            throw new RuntimeException("Could not find adapter class " + logSuffix);
        } catch (InstantiationException e) {
            Logger.e(TAG, "Could not instantiate adapter {}", logSuffix);
            e.printStackTrace();
            throw new RuntimeException("Could not instantiate adapter class " + logSuffix);
        } catch (IllegalAccessException e) {
            Logger.e(TAG, "Not allowed to access adapter class {}", logSuffix);
            e.printStackTrace();
            throw new RuntimeException("Not allowed to access adapter class" + logSuffix);
        }
    }

    public synchronized String getQuestionName() {
        return questionName;
    }

    private synchronized void setQuestionName(String questionName) {
        Logger.v(TAG, "Setting questionName");
        this.questionName = questionName;
        saveIfSync();
    }

    public synchronized IQuestionDescriptionDetails getDetails() {

        // We don't directly save the details here because for some obscure reason gson
        // serializes them to {} when they're nested this deep. So we get them from the
        // parametersStorage instead. The same problem is solved in the parametersStorage with
        // a custom QuestionDescriptionDeserializer.

        if (detailsCache == null) {
            detailsCache = parametersStorage.getQuestionDescription(questionName).getDetails();
        }
        return detailsCache;
    }


    public synchronized IAnswer getAnswer() {
        return answer;
    }

    public synchronized void setAnswer(IAnswer answer) {
        Logger.v(TAG, "Setting answer");
        this.answer = answer;
        saveIfSync();
    }

    public synchronized void setSequence(Sequence sequence) {
        this.sequenceCache = sequence;
        this.sequenceId = sequenceCache.getId();
        if (sequenceId == -1) {
            String msg = "Can't set sequence in a question in the sequence that has no id " +
                    "(i.e. it hasn't been saved yet)";
            Logger.e(TAG, msg);
            throw new RuntimeException(msg);
        }
        saveIfSync();
    }

    private synchronized Sequence getSequence() {
        if (sequenceCache == null) {
            sequenceCache = sequencesStorage.get(sequenceId);
        }
        return sequenceCache;
    }

    private synchronized boolean hasSequence() {
        return sequenceId != -1;
    }

    private synchronized void saveIfSync() {
        Logger.d(TAG, "Saving if in syncing sequence");
        if (hasSequence()) {
            getSequence().saveIfSync();
        } else {
            Logger.v(TAG, "Not saved since no sequence present");
        }
    }

}
