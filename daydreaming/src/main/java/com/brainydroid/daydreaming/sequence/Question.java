package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.IQuestionDescriptionDetails;
import com.brainydroid.daydreaming.db.QuestionDescription;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.ui.sequences.BaseQuestionViewAdapter;
import com.brainydroid.daydreaming.ui.sequences.IQuestionViewAdapter;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import com.google.inject.Injector;

// TODO: add some way to saveIfSync the phone's timezone and the user's
// preferences_appSettings
// about what times he allowed notifications to appear at.
public class Question implements IQuestion {

    private static String TAG = "Question";

    @Expose protected String name = null;
    @Expose private IAnswer answer = null;

    private IQuestionDescriptionDetails details = null;
    private int sequenceId = -1;

    private transient Sequence sequenceCache = null;
    @Inject private transient Injector injector;
    @Inject private transient SequencesStorage sequencesStorage;

    public Question(QuestionDescription questionDescription, Sequence sequence) {
        Logger.d(TAG, "Creating question {}", questionDescription.getName());
        setName(questionDescription.getName());
        setDetails(questionDescription.getDetails());
        setSequence(sequence);
        saveIfSync();
    }

    public synchronized IQuestionViewAdapter getAdapter() {
        Logger.d(TAG, "Getting adapter for question");

        String logSuffix = "for question " + name + " of type " + details.getType();
        String packagePrefix = BaseQuestionViewAdapter.class.getPackage().getName() + ".";
        try {
            Class klass = Class.forName(packagePrefix +
                    details.getType() + BaseQuestionViewAdapter.QUESTION_VIEW_ADAPTER_SUFFIX);
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

    public synchronized String getName() {
        return name;
    }

    private synchronized void setName(String name) {
        Logger.v(TAG, "Setting name");
        this.name = name;
        saveIfSync();
    }

    public synchronized IQuestionDescriptionDetails getDetails() {
        return details;
    }

    private synchronized void setDetails(IQuestionDescriptionDetails details) {
        Logger.v(TAG, "Setting details");
        this.details = details;
        saveIfSync();
    }

    public synchronized IAnswer getAnswer() {
        return answer;
    }

    public synchronized void setAnswer(IAnswer answer) {
        Logger.v(TAG, "Setting answer");
        this.answer = answer;
        saveIfSync();
    }

    private synchronized void setSequence(Sequence sequence) {
        this.sequenceCache = sequence;
        this.sequenceId = sequenceCache.getId();
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
