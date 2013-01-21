Changelog
=========


Version 0.4 (jumped up to here internally)
------------------------------------------

* Save and upload polls that aren't completely answered.
* Measure more location data: parallel to location data collected during the polls, location is measured every 20 minutes. This data is uploaded along with the polls, when a sync is triggered.
* Updated UI for untouched sliders in questions.
* Poll notifications do not expire any more: they wait until attended to, and are reactivated if a new poll is to be triggered. Notification time and poll-opening time are saved.
* Minor changes in question pool specification, with a new optional default position for sliders in questions.
* Follow Yelandur API v1, which includes Json Web Signatures for data signing.
* Moved to maven-based building.
* Minor (although numerous) bugfixes.


Version 0.1
-----------

First internal release.
