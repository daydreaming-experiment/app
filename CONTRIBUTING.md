# Contributing

If you're thinking of contributing, be it only a tiny patch, let us hail you!
You are very welcome.

Here goes a description of the tools and workflow we use in development; it
should help in getting up and running.

## Development tools

### IDE

We use [Android
Studio](https://developer.android.com/sdk/installing/studio.html) as IDE.
Instructions follow on how get up and running.

### Building the project

Once Android Studio is configured (see below) you can use it directly to build
the project, but the simplest and most reproducible way is to use
[Gradle](http://www.gradle.org/) directly from the command line. That's what we
use.

### Version control

We use [Git](http://git-scm.com/) as version control as you might have noticed,
but we use it with the
[HubFlow](http://dev.datasift.com/blog/hubflow-github-and-gitflow-model-together)
workflow, which is an adaptation of the
[GitFlow](http://nvie.com/posts/a-successful-git-branching-model/) model to
GitHub.

### Continuous integration

We want every commit on the `master` branch to be flawless (ideally), and we use
[Travis-CI](http://about.travis-ci.org/) to help us with that.

### Related software

The app needs to talk to a server to upload its answers: that role is fulfilled
by the [Yelandur](https://github.com/wehlutyk/yelandur) backend server, which in
turn talks to an Ajax client, [Naja](https://github.com/wehlutyk/naja).

As you will also see in the code, we use
[RoboGuice](https://github.com/roboguice/roboguice) for dependency injection and
[Robolectric](http://pivotal.github.io/robolectric/) for automated tests.

## Detailed steps to set up your environment

There are two steps: first a few things to get the Gradle build running, then
the configuration of Android Studio.

### Gradle build

If you know Travis-CI a little, these steps are exactly what is configured in
the `.travis.yml` file.

#### Android SDK Tools

First we need the SDK Tools from the Android website:

```sh
wget -O android-sdk-linux.tgz http://dl.google.com/android/android-sdk_r22.6.2-linux.tgz
tar xzf android-sdk-linux.tgz
```

Next, configure the environment variable telling the system where the
Android SDK lives; do that by first running the following commands:

```sh
export ANDROID_HOME="<absolute-path-to-parent-folder-of-android-sdk>/android-sdk-linux"
export PATH="${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/build-tools/17.0.0"
```

where you replace `<absolute-path-to-parent-folder-of-android-sdk>` by the
absolute path to the folder containing your Android SDK folder, obviously (that
should be the output of the `pwd` command, if you haven't moved from where you
untared the SDK). Next, add those same two lines to the end of your `.bashrc`
(or `.bash_aliases`, or `.profile`, or zsh or fish equivalents). That way the
environment variables will be set in all your future command line sessions. Once
you've done that you should be able to call the `android` command from any
folder at the command line, and it will show you some help on that command (it
comes from the SDK). If instead you're greeted with a `command not found`,
something's wrong with that part of the configuration.

Now, install the necessary SDK packages by running the following command
(from any folder):

```sh
android update sdk --no-ui --force --filter build-tools-19.1.0,android-16,platform-tools,extra-android-support,extra-android-m2repository
```

(And answering `y` to the license agreement.)

#### Configure Gradle to have the right Android SDK home

Gradle doesn't know where your Android SDK lives, especially when run from
Android Studio (where the environment variables don't get set as we configured
them above). Let us enlighten it: in the root daydreaming repository, edit
`local.properties` to make it look like this:

```
sdk.dir=<absolute-path-to-the-android-sdk-folder>
```

where you replace `<absolute-path-to-the-android-sdk-folder>` by the absolute
path to the Android SDK folder. This file is not checked into version control,
which is normal.

You should now be able to build the whole app and run the tests in a single
command from the repository's root: `./gradlew assemble check`. This will
automatically download the required Gradle distribution, all the project
dependencies, build the different versions of the app (test, qa, release, etc.)
and run the tests and checks.

## Setting up Android Studio

Now that we've set up Gradle and that we can build the app, it's time to get the
IDE running. I'm using Android Studio version 0.6.1; any later version should
also work well, but if your experience is different with a different version
please send us feedback!

### Importing the Gradle project

Fire up Android Studio and choose `Import Project` from the welcome screen. At
the popup dialog, navigate to daydreaming's repository and select `build.gradle`
(the one inside the root `app` folder of the repository, *not* the one inside
the `app/daydreaming` folder). Click `OK`, and Android Studio should import the
whole project automatically.

You're good to go now: you should be able to edit the code, and build the app
either from within Android Studio (the green arrow in the toolbar) or from the
command line with `./gradlew assemble` (to build and package the APKs) and
`./gradlew check` (to run the tests and checks).

If you're running the app from inside Android Studio, it will ask you to
configure an Android virtual device if you don't already have one, unless you
plug in your own phone.  That's beyond the scope of this document :-).

Enjoy! And most of all, please tell us if you encounter any problem.
