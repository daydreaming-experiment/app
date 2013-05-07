package com.brainydroid.daydreaming.db;

import android.widget.LinearLayout;

/**
 * Interface for answers to {@link Poll} {@link BaseQuestion}s,
 * sent to server once filled.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public interface Answer {

    /**
     * Parse a layout and fill the instance with the user's answers present
     * in the layout.
     *
     * @param questionLinearLayout The layout to parse through
     */
    public void getAnswersFromLayout(LinearLayout questionLinearLayout);

}
