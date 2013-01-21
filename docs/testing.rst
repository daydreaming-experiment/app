.. _testing:

Testing
=======


.. _get-the-app:

Get the app
-----------

Grab the ``daydreaming-XXX.apk`` file from the test server at http://mehho.net:5001/, or install by scanning the ``daydreaming-XXX_qrcode.png`` QR code on that same server.


Testing guidelines
------------------

Testing is easy! To do it properly and make sure we get the most out of it, keep close to the following guidelines.


App user interface
^^^^^^^^^^^^^^^^^^

#. If you've already used a previous version (or even the current version) of the app, start from scratch by uninstalling; this will also clear previous data which might interfere with the tests.
#. Download and install the latest version of the app from the server in :ref:`get-the-app`. From now on, **write down anything that seems strange, unexpected, not immediately clear, or even mildly surprising**. And especially anything that's different from the :ref:`description`, if you've read it. Send that back with a few explanations to whoever told you testing was a good idea.
#. Start the app. Go through the first-launch screens. You can try this with internet on or off, and with network-location activated or disabled.
#. When you get to the dashboard, adjust the settings to your liking (try them out, play around with the times).
#. Nothing else will happen on the dashboard, but questions should pop up every now and then, and will keep appearing until you uninstall the app (or clear the app's data).
#. Try using the app in all possible situations: with or without internet activated, with internet activated but no signal, with or without network-location allowed, etc.

If you have a `GitHub <https://github.com>`_ account (you know what testing is like!) please also report any problems you encounter in the `issue system for the project <https://github.com/wehlutyk/daydreaming/issues?state=open>`_, after checking that the problem you found isn't already identified in the existing issues.


A peek at the data
^^^^^^^^^^^^^^^^^^

You can have a look at your data (as well as other devices', for testing purposes) on `Naja <http://naja.cc>`_, the server collecting the data.

This is **very user-unfriendly** for the moment. But if you insist... Log in using BrowserID with the following account:

Email address:
   ``daydreamingdemo@mehho.net``

Password:
   ``daydreamingdemo``

*After logging in*, go to the `daydreaming results page <http://naja.cc/daydreamingdemo/daydreaming>`_ and see if you can find your ``device_id`` by looking at the times results were uploaded at (your device uploads data when polls are notified). If you can't find yours, pick any device and copy its ``device_id``.

Then navigate to ``http://naja.cc/api/v1/devices/<device_id>/exps/6cb5e7782ca43681d6349a2280a8f99f74479d142971ac6c91dbd155ac58b4b3/results/``, where ``<device_id>`` is the id you copied. You should see some JSON data representing whatever was uploaded by that device. If you think this is your own device's data, you can try checking that the data you're shown is indeed what you answered.

One example check (and fun and creepy) is to take the latitudes and longitudes and put them on a map (like `OpenStreetMap <http://www.openstreetmap.org>`_, by searching for ``latitude.longitude`` in their search box, where you've replace ``latitude`` and ``longitude`` with their floating point values).
