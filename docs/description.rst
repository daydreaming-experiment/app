.. _description:

Description
===========

Below follows a detailed description of the expected behaviour of the app. Hopefully the UI is sufficiently self-explanatory for no user to ever want to read this document. But it's still important to have a reference protocol against which the real behaviour of the app can be compared.

We start from the user's perspective, and then explain what things look like from the researcher's side.


User (subject) side
-------------------


First launch
^^^^^^^^^^^^

On first launch the app will:

#. Present itself
#. Ask for IRB agreement
#. Ask the user to adjust location settings, if necessary

When the first launch finishes, first contact is made with the backend server:

#. A public/private cryptographic keypair is generated for further signing of updoaded data
#. The public key is sent to the server, in exchange of which the app gets assigned a unique ``device_id`` (more about that later on)
#. Finally, a synchronisation event is triggered which creates a pool of questions from the server

As described below, :ref:`polls` and :ref:`location-measurements` start getting scheduled from this point on. The first location measurement, if successful, is likely to complete before the synchronisation event is over and will therefore see its data uploaded straight away.


Issues
""""""

`Issue #15 <https://github.com/wehlutyk/daydreaming/issues/15>`_: *Age and gender are not saved*

`Issue #16 <https://github.com/wehlutyk/daydreaming/issues/16>`_: *Only update questions on first launch, not at every sync*


The dashboard
^^^^^^^^^^^^^

Once the first launch is over you're presented with the *dashboard*.

It's static for now, but should eventually show:

* How many days the experiment has been running for (`issue #17 <https://github.com/wehlutyk/daydreaming/issues/17>`_)
* A webview with the user's results (in a future update)

A button in the upper right corner opens the settings. There you can adjust:

* Parameters for poll notifications (blink, make a sound, vibrate)
* The time window during which notifications are aloud; this should also work for time windows going through midnight. If the time window you enter is shorter than 5 hours, values are reset to default.

An additional testing button lets you artificially trigger a poll.

In the current state the user can't stop the experiment (short of clearing the app's data or uninstalling it), so polls and location measurements will keep being scheduled *ad vitam eternam* as described below (`issue #18 <https://github.com/wehlutyk/daydreaming/issues/18>`_).


Issues
""""""

`Issue #17 <https://github.com/wehlutyk/daydreaming/issues/17>`_: *Show how long the experiment has been running for*

`Issue #18 <https://github.com/wehlutyk/daydreaming/issues/18>`_: *Let the user pause or finish the experiment*


.. _polls:

Polls
^^^^^

After the first launch has finished polls start getting scheduled and notified to the user. When a poll gets triggered a notification appears (respecting the user's notification type and time settings) and waits forever. The time at which the notification appears is logged and included in the data pertaining to the poll (for future uploading). If a new poll was to be scheduled and the user still hasn't attended the waiting poll, that waiting poll is renotified (and no new poll is created), and the notification timestamp is updated.

The inter-poll interval is sampled from a normal distribution of 2 hour mean and 1 hour standard deviation, with a minimum of 10 minutes (if the sampled interval is shorter than 10 minutes, we resample; this is the same as thresholding and renormalising the normal distribution).

A poll consists of three questions randomly sampled from the pool of questions. Each question consists either of:

* Multiple choices, with an additional ``Other`` field if the user doesn't find a suitable option; the UI requires at least one choice to be ticked before continuing to the next question or finishing the poll (if ``Other`` is ticked, the UI requires it to contain some text)
* One or several sliders; the UI requires all the sliders to be at least touched before continuing to the next question or finishing the poll

When a poll is opened a synchronisation event is triggered and the app starts measuring location in the background. If the location settings don't allow this, or if data connection isn't available (necessary for network-based location to work), the UI will require the user to adjust his settings before continuing to the next question. Each question gets its own location data as well as a timestamp.

Leaving the poll halfway through (e.g. by pressing the ``Home`` button) will dismiss that poll: it won't be available to the user any more. If the user tries to leave the poll or come back to the previous question by pressing the ``Back`` button, the UI asks for confirmation and pressing ``Back`` again will dismiss the poll (ideally the ``Home`` button should behave the same, but the Android system doesn't let an app do that for security reasons). Finishing or leaving the poll halfway through saves any answers that were given and triggers a synchronisation event, so answers are uploaded straight away.

Finally, all timestamps (either for poll or question) are obtained using network time, not the device's time. This bypasses any concerns of the device having wrong time settings.


.. _location-measurements:

Location measurements
^^^^^^^^^^^^^^^^^^^^^

After the first launch has finished location measurements start getting scheduled every 20 minutes. If location settings don't allow it or if data connection is unavailable when a measurement starts, that measurement is skipped and the next one is scheduled (20 minutes later).

Location data is saved and uploaded whenever a synchronisation event is triggered.


Researcher side
---------------

The server pool of questions is located at http://mehho.net:5001/questions.json, and the version number for that pool of questions is located at http://mehho.net:5001/questionsVersion (the version of the pool is also included in the full pool file, but a separate questionsVersion file is needed to be able to check the version without downloading the whole pool). If the version the app has isn't the last one, the pool of questions gets replaced with the new one. That means you can change the questions at will (and update the version number), and any device that connects to the internet will automatically update (but see `issue #16 <https://github.com/wehlutyk/daydreaming/issues/16>`_).

The uploaded data is all JSON and can be viewed in the researcher's dashboard on the website. This is further detailed in :ref:`testing`.


Issues
^^^^^^

`Issue #16 <https://github.com/wehlutyk/daydreaming/issues/16>`_: *Only update questions on first launch, not at every sync*
