package com.brainydroid.daydreaming.ui;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.*;
import com.brainydroid.daydreaming.network.ResultsArrayFactory;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class AppModule implements Module {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "AppModule";

    @Override
    public void configure(Binder binder) {
        Logger.d(TAG, "Configuring application module");

        binder.install(new FactoryModuleBuilder().build(PollFactory.class));
        binder.install(new FactoryModuleBuilder()
                .build(QuestionFactory.class));
        binder.install(new FactoryModuleBuilder()
                .build(LocationPointFactory.class));
        binder.install(new FactoryModuleBuilder()
                .build(new TypeLiteral<ResultsArrayFactory<Poll>>() {}));
        binder.install(new FactoryModuleBuilder()
                .build(new TypeLiteral<ResultsArrayFactory<LocationPoint>>()
                {}));
    }

}
