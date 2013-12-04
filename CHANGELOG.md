Changelog
=========

0.11
----

* New format for questions.json (grammar `v1`):
  * More ordering expressiveness
  * Hints are optionally shown or hidden
  * Questions can optionally be skipped

0.10
----

* UI fixes
* Update consent presentation
* Allow unlocated questions
* Record a `systemTimestamp` as well as an `ntpTimestamp` for questions answered when there was no internet connection

0.9
---

* UI fixes

0.8.1
-----

* Rephrasings
* Clean up the "Measures" activity

0.8
---

* Don't show probes if questions haven't been downloaded yet
* NullPointerException bug fixes
* Add `initialRating` option for `StarRating` question type

0.7.2
-----

* Fix questions.json url (again)

0.7.1
-----

* Fix questions.json url

0.7
---

* Many UI problems fixed
* Prevent notifications from appearing while answering a probe
* Added automatic crash reporting
* Redesign the "Measures" activity
* New first launch checkpoint after the personality questionnaire (TIPI)
* Version of quetions is sent in with the profile
* Addition of `StarRating` question type
* Addition of Android 2.3.3 as available target

0.6
---

* UI update
* Many bug fixes (unspecified)

0.5
---

* Major UI update
* Major internal models update
* Update in backend conversations

0.4 (jumped up to here internally)
----------------------------------

* Save and upload polls that aren't completely answered.
* Measure more location data: parallel to location data collected during the polls, location is measured every 20 minutes. This data is uploaded along with the polls, when a sync is triggered.
* Updated UI for untouched sliders in questions.
* Poll notifications do not expire any more: they wait until attended to, and are reactivated if a new poll is to be triggered. Notification time and poll-opening time are saved.
* Minor changes in question pool specification, with a new optional default position for sliders in questions.
* Follow Yelandur API v1, which includes Json Web Signatures for data signing.
* Moved to maven-based building.
* Minor (although numerous) bugfixes.


0.1
---

First internal release.
