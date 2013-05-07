package com.brainydroid.daydreaming.ui;

import android.util.Log;
import com.brainydroid.daydreaming.db.PollFactory;
import com.brainydroid.daydreaming.db.QuestionFactory;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class AppModule implements Module {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "AppModule";

    @Override
    public void configure(Binder binder) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] configure");
        }

        binder.install(new FactoryModuleBuilder().build(PollFactory.class));
        binder.install(new FactoryModuleBuilder()
                .build(QuestionFactory.class));
    }

}
