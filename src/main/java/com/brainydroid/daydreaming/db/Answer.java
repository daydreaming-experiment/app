package com.brainydroid.daydreaming.db;

import android.widget.LinearLayout;

/**
 * Interface for answers to {@link Poll} {@link Question}s,
 * sent to server once filled.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public interface Answer {

    /**
     * Convert the {@link Answer} to JSON. Implementation depends on the
     * type of {@link Question} the answer is answering.
     *
     * @return JSON representation of the answer
     */
    public String toJson();

    /**
     * Parse a layout and fill the instance with the user's answers present
     * in the layout.
     *
     * @param questionLinearLayout The layout to parse through
     */
    public void getAnswersFromLayout(LinearLayout questionLinearLayout);

}
