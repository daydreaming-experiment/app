package com.brainydroid.daydreaming.db;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.IOException;

public class InjectingDeserializer<Object>
        extends StdDeserializer<Object> implements ResolvableDeserializer {

    private static String TAG = "InjectingDeserializer";

    @Inject private Injector injector;

    private JsonDeserializer<?> defaultDeserializer;

    public InjectingDeserializer(JsonDeserializer<?> defaultDeserializer) {
        super(BaseObject.class);
        this.defaultDeserializer = defaultDeserializer;
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        Object out = (Object)defaultDeserializer.deserialize(jp, ctxt);
        injector.injectMembers(out);
        return out;
    }

    @Override
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        ((ResolvableDeserializer)defaultDeserializer).resolve(ctxt);
    }
}
