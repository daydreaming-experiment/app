# Logging

Logging helps diagnose a bug and check that everything is working as desired. 


## What is logged
There are various logging levels, and things should be logged differently depending on what they do. Below we describe what things should be logged at each level.


### Information

 * The logic


### Debug

 * _All_ function calls, except:
    * Trivial `get*` / `set*` functions: that is, `get*` / `set*` that have _no_ logic in them (i.e. even the slightest logic must be logged) _and_ have no lasting effect on the behaviour of the class. This means that `get*` / `set*` functions on passive attributes are not logged at this level, but `get*` / `set*` on an attribute that tells the class to behave differently (e.g. do something on destroy) _should_ be logged at this level. It also means that any `is*` or `has*` function should be logged at this level (regardless of the kind of logic involved). Finally, a `get*` / `set*` function that calls a trivial `get*` / `set*` function on a private attribute _should also_ be logged at this level.
    * Generic utility functions such as `parseString`, `joinStrings`, etc.


### Verbose

 * the remaining function calls, that is:
    * `get*` / `set*` functions even if they include no logic whatsoever
    * generic utility functions


## How things are logged

_Every_ class (including nested classes) gets a `private static String TAG` attribute containing the name of the class (non-`static` if the class is nested).


### Function calls

Function calls are logged by including a log line at the very beginning of each function body: that is, the log line is the _first_ line in a function, except when a `super` constructor is required to come before (in which case the log line comes right after the `super` constructor).

A function is logged at debug level like this:

        // Debug
        Log.d(TAG, "[fn] functionName");

and at verbose level like this:

        // Verbose
        Log.v(TAG, "[fn] functionName");
