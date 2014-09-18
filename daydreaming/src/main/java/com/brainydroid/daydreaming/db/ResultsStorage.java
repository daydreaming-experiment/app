package com.brainydroid.daydreaming.db;


import android.app.Application;
import android.content.Context;

import com.brainydroid.daydreaming.background.ErrorHandler;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;

@Singleton
public class ResultsStorage {

    private static String TAG = "ResultsStorage";

    private static String RESULTS_FILENAME = "results";
    private static String STORAGE_DIRNAME = "resultsStorage";

    @Inject StatusManager statusManager;
    @Inject HashMap<String, File> resultsFiles;
    @Inject ErrorHandler errorHandler;
    private File storageDir;

    @Inject
    public ResultsStorage(Application application) {
        Logger.d(TAG, "Initializing ResultsStorage");
        storageDir = application.getDir(STORAGE_DIRNAME, Context.MODE_PRIVATE);
    }

    private synchronized File getResultsFile() {
        String currentModeName = statusManager.getCurrentModeName();
        Logger.v(TAG, "{} - Getting resultsFile", currentModeName);

        if (!resultsFiles.containsKey(currentModeName)) {
            resultsFiles.put(currentModeName,
                    new File(storageDir, currentModeName + RESULTS_FILENAME));
        }
        return resultsFiles.get(currentModeName);
    }

    public synchronized String getResults() {
        Logger.d(TAG, "{} - Reading results from file", statusManager.getCurrentModeName());
        if (statusManager.getResultsDownloadTimestamp() == -1) {
            Logger.e(TAG, "StatusManager reports results were never downloaded. " +
                    "There's going to be an error.");
        }
        try {
            BufferedReader buf;
            buf = new BufferedReader(new FileReader(getResultsFile()));

            StringBuilder resultsBuilder = new StringBuilder();
            String line = null;
            String ls = System.getProperty("line.separator");
            while ((line = buf.readLine()) != null) {
                resultsBuilder.append(line);
                resultsBuilder.append(ls);
            }
            String results = resultsBuilder.toString();
            buf.close();
            return results;
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "{} - results file not found", statusManager.getCurrentModeName());
            errorHandler.logError(e);
            return null;
        } catch (IOException e) {
            Logger.e(TAG, "{} - IO error reading results file", statusManager.getCurrentModeName());
            errorHandler.logError(e);
            return null;
        }
    }

    public synchronized boolean saveResults(String resultsString) {
        Logger.d(TAG, "Saving results to file");
        try {
            if (getResultsFile().createNewFile()) {
                Logger.d(TAG, "{} - Created new file for results",
                        statusManager.getCurrentModeName());
            } else {
                Logger.w(TAG, "{} - Overwriting existing file for results",
                        statusManager.getCurrentModeName());
            }
            BufferedWriter resultsBuf = new BufferedWriter(new FileWriter(getResultsFile()));
            resultsBuf.write(resultsString);
            resultsBuf.close();

            statusManager.setResultsDownloadedToNow();
            return true;
        } catch (IOException e) {
            Logger.e(TAG, "{} - IO error writing to results file",
                    statusManager.getCurrentModeName());
            errorHandler.logError(e);
            return false;
        }
    }
}
