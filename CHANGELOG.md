Changelog
=========

0.26.0
------

* Enhancements
  * [#316, #337]Changes and enhancements in app text
  * [#330] Add storage versioning
* Bug fixes
  * [#331] StringIndexOutOfBoundsException in SettingsActivity
  * [#332] NullPointerException in DailySequenceService
  * [#333] BEQ Notification not removed when BEQs completed
  * [#336] Morning questionnaire beeping and sound for other notifications
  * [#346] Empty results uploaded
  * Fix sync pooling using a bad timer
  * Fix upload of incomplete/missed/dismissed morning and evening questionnaires

0.25.0
------

* Enhancements
  * [#305] MatrixQuestion doesn't need to scroll
  * [#306] If intro text is empty, hide the extra space
  * Add mother tongue in profile and first launch
* Bug fixes
  * Add Jérôme in Credits, was missing

0.24.1
------

* Bug fixes
  * Fix NullPointerException in probe expiry

0.24.0
------

* Enhancements
  * [#123] Glossary word matching in questions
  * [#274] Questionnaire activity redesign
  * ]#224] autoList question design and usability
  * [#267] manySliders question design and usability
  * Many design enhancements, and work nearly done on the matrixQuestion design

0.23.1
------

* Bug fixes
  * [#288] Launching a begin/end questionnaire crashes
  * [#276] Fix back button behaviour in page activity

0.23.0
------

* Enhancements
  * [#273] Add test MEQ buttons in test dashboard
* Bug fixes
  * [#263] Scheduling problems, settings not honoured
  * [#241] Parameters not re-updating if quick failure
  * Server-side, upload of questions with '.' in names is fixed

0.22.0
------

* Enhancements
  * [#91] Add progress information in sequences
  * [#200] Matrix choice usability
  * [#214] Notification lifecycle
* Bug fixes
  * [#235] Dashboard instructions usability

0.21.1
------

* Bug fixes
  * If getting BEQ type before it's set, set it instead of crashing

0.21.0
------

* Enhancements
  * [#232] Add instructions in dashboard
  * [#193] Results button properly styled
  * Finer-grain parameter-update view in dashbaord
  * [#118] Implement initial questionnaires
  * [#119] Implement final questionnaires
  * [#173] Results page fully implemented
* Bug fixes
  * [#123] Issues in questionnaire lifecycle

0.20.0
------

Aborted release because of mistake in the process.

0.19.0
------

* Enhancements
  * Set different launcher icons for QA and Debug
  * Add some debug information to the test dashboard
  * Update parameter-downloading UI (if errored, restarts automatically)
  * [#229] Swipe in dashboard initiates self-report
  * [#196] Editable ManySliders questions type, for activity questionnaire
* Bug fixes
  * [#202] Don't have two syncs running at the same time
  * [#218] Sntp resolving in dashboard when parameters are not set
  * [#178] Background JSON problems, and clean error hanlding and reporting

0.18.0
------

* Enhancements
  * [#174] Add a MatrixChoiceQuestion type (not yet themed properly)
  * [#94] Start the results page (next is #173)
  * [#106] Set up APK signing for future release
  * [#182] Show an introduction text in all questions
  * Add a questionnaire management screen and button to dashboard
  * [#191] Add an auto-complete list type question (not yet themed properly)
* Bug fixes
  * [#149, #188, #208] Design issues in MultipleChoiceQuestion
  * [#148] Design issue in SliderQuestion
  * [#185] Too much vertical space in questions
  * [#195] Only ask about bonus questions once in sequence, and skip all or take all
  * [#206] Design issues in Consent activity
  * [#204, #207, #209] Design issues in Terms, About, Profile activities

0.17.2
------

* Bug fixes
  * Use non-dev grammar repository

0.17.1
------

* Bug fixes
  * Fix NullPointerException in PageActivity if nextPage is bonus with no following pageGroup

0.17.0
------

* Enhancements
  * [#164] Use storage efficiently
  * [#117] Generalized sequences builder with corresponding grammar v3 basis
  * [#163] Switch to Jackson for JSON handling (answers weren't serialized)
  * [#121] Add glossary in dashboard, link it to questions
  * [#114] Implement bonus slots and relative positioning of items
  * Redesign About page
* Bug fixes
  * [#162] Questions gone beserk
  * [#159] Restart lost background services when reopening dashboard
  * [#179] Deserialization failing, switch to JSON Views in Jackson

0.16.0
------

* Enhancements
  * [#124] Add a Credits screen on the Dashboard
  * [#125] Use grammar v2.1
  * [#155] Updated UI for when the parameters are loading in the Dashboard
  * Part of [#116]: started reducing first launch (tipi questionnaire has disappeared for the moment)
* Bug fixes
  * [#145] No space between "Experiment is" and "Running" on Dashboard

0.15.1
------

* Bug fixes
  * [#150] `schedulingMinDelay` and `schedulingMeanDelay` are ignored

0.15.0
------

* Enhancements
  * [#126] Use grammar v2
* Bug fixes
  * Duplicated `parametersVersion` between `ProfileStorage` and `ParametersStorage` (leading to bugs)

0.14.0
------

* Enhancements
  * [#111] `appVersionName` and `appVersionCode` are included in the user's profile
  * [#102] Switched to gradle build system for build variables and debug/qa/release builds
  * [#98] Cleaned up debug/qa/release build variables thanks to gradle build system
  * [#140] Add test scripts to check consistency and correctness of profiles on server
  * [#93] Outsource in-app password and ACRA credentials to local out-of-git file
  * [#138] Cancel pending notifications, running location collections, and network operations, when switching app mode
* Bug fixes
  * [#104] App crashes on API 10
  * [#108] Inconsistent profiles on server due to bad test/production mode switches
  * [#139] Buggy activity paths leading to inconsistent profiles on server

0.13.1
------

* No changes, just doing the release process fully (only half done in 0.13)

0.13
----

* Fix two IllegalStateException crashes
* Minor UI update on test mode button

0.12
----

* New "test mode" in the app, allowing easy updating of parameters without breaking running subjects' setups (uses multiple profiles in background)
* Test mode comes with the new `daydreaming-experiment/parameters` repository to deal with test parameters and different grammar versions
* Update and clean up some dependencies
* Switch back average inter-probe interval to 2 hours

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
