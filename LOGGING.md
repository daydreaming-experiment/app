# Logging


## What is logged

Depending on the logging level:


### Information

 * the logic


### Debug

 * _all_ function calls, except:
    * `get*` / `set*` functions that have _no_ logic in them (i.e. even the slightest logic must be logged)
    * generic utility functions such as `parseString`


### Verbose

 * the remaining function calls, that is:
    * `get*` / `set*` functions even if they include no logic whatsoever
    * generic utility functions


## How it is logged

_Every_ class (including nested classes) gets a `private static String TAG` attribute containing the name of the class (non-`static` if the class is nested).


### Function calls

Function calls are logged by including a log line at the very beginning of each function body: that is, the log line is the _first_ line in a function, except when a `super` constructor is required to come before.

A function is logged at debug level like this:

        // Debug
        Log.d(TAG, "[fn] functionName");

and at verbose level like this:

        // Verbose
        Log.v(TAG, "[fn] functionName");
