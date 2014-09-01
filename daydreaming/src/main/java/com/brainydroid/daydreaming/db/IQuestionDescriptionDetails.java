package com.brainydroid.daydreaming.db;

/**
 * Interface to implement to represent details of a question.
 * <p/>
 * See {@link QuestionDescription} for further details on how this interface plays
 * along with the other classes and interfaces.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see QuestionDescription
 */
public interface IQuestionDescriptionDetails {

    /**
     * Get the type of question for which this instance is the details field.
     * <p/>
     *
     * @return Question type
     */
    public String getType();

    /**
     * Validate the question details are properly initialized.
     * <p/>
     * Throw a {@link com.brainydroid.daydreaming.db.JsonParametersException} if not.
     *
     * @throws JsonParametersException
     */
    public void validateInitialization() throws JsonParametersException;

}
