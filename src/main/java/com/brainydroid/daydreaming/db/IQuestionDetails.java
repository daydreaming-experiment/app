package com.brainydroid.daydreaming.db;

/**
 * Interface to implement to represent details of a question.
 * <p/>
 * See {@link Question} for further details on how this interface plays
 * along with the other classes and interfaces.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see Question
 */
public interface IQuestionDetails {

    /**
     * Get the type of question for which this instance is the details field.
     * <p/>
     *
     * @return Question type
     */
    public String getType();

}
