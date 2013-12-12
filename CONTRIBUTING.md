# Contributing

If you're thinking of contributing, be it only a tiny patch, let us hail
you! You are very welcome.

Here goes a description of the tools and workflow we use in development
; it should help in getting up and running.

## Development tools

### IDE

We use [IntelliJ IDEA](http://www.jetbrains.com/idea/) as IDE.
Instructions follow on how get up and running.

### Building the project

Once IntelliJ IDEA is well configured (see below) you can use it directly to
build the project, but the simplest and most reproducible way is to use [Apache
Maven](http://maven.apache.org/). That's what we use.

### Version control

We use [Git](http://git-scm.com/) as version control as you might have
noticed, but we use it with the
[HubFlow](http://dev.datasift.com/blog/hubflow-github-and-gitflow-model-together)
workflow, which is an adaptation of the
[GitFlow](http://nvie.com/posts/a-successful-git-branching-model/) model
to GitHub.

### Continuous integration

We want every commit on the `master` branch to be flawless (ideally),
and we use [Travis-CI](http://about.travis-ci.org/) to help us with
that.

### Related software

The app needs to talk to a server to upload its answers: that role is
fulfilled by the [Yelandur](https://github.com/wehlutyk/yelandur)
backend server, which in turn talks to an Ajax client,
[Naja](https://github.com/wehlutyk/naja).

As you will also see in the code, we use
[RoboGuice](https://github.com/roboguice/roboguice) for dependency
injection and [Robolectric](http://pivotal.github.io/robolectric/) for
automated tests.

## Detailed steps to set up your environment

There are two steps: first a few things to get the Maven build running,
then the configuration of IntelliJ.

### Maven build

If you know Travis-CI a little, these steps are nearly what is
configured in the `.travis.yml` file. I say nearly because here we also
download the Android Platform 14, which isn't needed for building the
project, but is needed to have IntelliJ run smoothly with
ActionBarSherlock.

#### Android SDK Tools

First we need the SDK Tools from the Android website:

```sh
wget -O android-sdk-linux.tgz http://dl.google.com/android/android-sdk_r22.3-linux.tgz
tar xzf android-sdk-linux.tgz
```

Next, configure the environment variable telling the system where the
Android SDK lives ; do that by first running the following commands:

```sh
export ANDROID_HOME="<absolute-path-to-parent-folder-of-android-sdk>/android-sdk-linux"
export PATH="${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/build-tools/17.0.0"
```

where you replace `<absolute-path-to-parent-folder-of-android-sdk>` by
the absolute path to the folder containing your Android SDK folder,
obviously (that should be the output of the `pwd` command, if you
haven't moved from where you untared the SDK). Next, add those same two
lines at the end of your `.bashrc` (or `.bash_aliases`, or `.profile`,
or zsh equivalents). That way the environment variables will be set in
all your future command line sessions. Once you've done that you should
be able to call the `android` command from any folder at the command
line, and it will show you some help on that command (it comes from the
SDK). If instead you're greeted with a `command not found`, something's
wrong with that part of the configuration.

Now, install the necessary SDK packages by running the following command
(from any folder):

```sh
android update sdk --no-ui --force --filter platform-tools,build-tools-19.0.0,android-16
```

(And answering `y` to the license agreement.)

#### Configure Maven to have the right Android SDK home

Maven doesn't always know where your Android SDK lives, especially when
run from IntelliJ (where the environment variables don't get set as we
configured above). Let us enlight it: edit `~/.m2/settings.xml` in your
home folder to make it look like this:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
        http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <profiles>
        <profile>
            <id>android</id>
            <properties>
                <android.sdk.path>
                    <!-- Replace this with something that looks like
                    /home/wehlutyk/Code/Android/android-sdk-linux -->
                    /path/to/your/android/sdk/folder
                </android.sdk.path>
            </properties>
        </profile>
    </profiles>
    <activeProfiles>
        <!-- Make the profile active all the time -->
        <activeProfile>android</activeProfile>
    </activeProfiles>
</settings>
```

## Setting up IntelliJ IDEA

Now that we've set up Maven and that we can build the app, it's time to
get the IDE running. I'm using IntelliJ IDEA Community Edition, version
13 ; if your experience is different with a different version, please
send us feedback!

### Importing the Maven project

Fire up IntelliJ and choose `Import Project` from the welcome screen.
At the popup dialog, navigate to daydreaming's repository and select
`pom.xml`. Click `OK`.

At the following window, leave all the default options as they are and
click `Next` directly.

At the following window, check the `android` profile, then click `Next`.

At the following window, the `com.brainydroid.daydreaming:x.y.z` project
should already be selected (if not, select it for import), so you can
click `Next`.

At the following window, we'll start by configuring a Java SDK (i.e.
JDK) if you don't already have one. Click the green `+` button (top
left) and choose `JDK`. In the popup window, navigate to the home of
your default JDK (mine is `/usr/lib/jvm/default-java`) and click `OK`
(better if it's version 1.7). Now click that green `+` button again, and
choose `Android SDK` this time. In the popup window, navigate to the
Android SDK home folder (the one we configured in the `ANDROID_HOME`
environment variable), and click `OK`. Accept the `Java SDK` version
(`1.7` for me) and the `Build target` (`Android 4.1.2` normally) by
clicking `OK` at the popup dialog. Now click `Next`, and then `Finish`
at the next window to finalize the import.

### Configuring IntelliJ

IntelliJ has its own build system (configured by importing the Maven
settings), and there's still a few options to fix before you can cleanly
build from inside IntelliJ (remember you can always `mvn package` at the
command line if IntelliJ won't cooperate).

Go to `Run > Edit Configurations...` ; create a new run
configuration by clicking the green `+` button and selecting `Android
Application`. Now on the right side, you can name the configuration
`daydreaming-run` ; in the `General` tab, select `daydreaming` in the
`Module` dropdown list. Confirm the changes by clicking `OK` at the
bottom.

We're done! You can compile and launch the app through IntelliJ by
clicking the green triangle button in the toolbar.  After compilation,
IntelliJ will ask you to configure an Android virtual device if you
don't already have one, unless you plug in your own phone.  That's
beyond the scope of this document :-).

Enjoy! And most of all, please tell us if you encounter any problem.
