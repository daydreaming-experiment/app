package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.sequence.PreLoadable;

/**
 * Interface to implement to represent details of a question.
 * <p/>
 * See {@link QuestionPositionDescription} for further details on how this interface plays
 * along with the other classes and interfaces.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see QuestionPositionDescription
 */
public interface IQuestionDescriptionDetails extends PreLoadable {

    /**
     * Get the type of question for which this instance is the details field.
     * <p/>
     *
     * @return Question type
     */
    public String getType();

    public Object getPreLoadedObject();

    /**
     * Validate the question details are properly initialized.
     * <p/>
     * Throw a {@link com.brainydroid.daydreaming.db.JsonParametersException} if not.
     *
     * @throws JsonParametersException
     */
    public void validateInitialization() throws JsonParametersException;

}
