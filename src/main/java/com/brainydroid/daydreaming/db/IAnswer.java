package com.brainydroid.daydreaming.db;

/**
 * Interface to implement to represent answers to a certain type of
 * question.
 * <p/>
 * See {@link Question} for further details on how this interface plays
 * along with the other classes and interfaces.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see Question
 */
public interface IAnswer {

    /**
     * Get the type of question to which this instance is an answer.
     * <p/>
     * Never used, but makes sure we won't forget to add a {@code type}
     * member in the implementations.
     *
     * @return Question type
     */
    @SuppressWarnings("UnusedDeclaration")
    public String getType();

}
