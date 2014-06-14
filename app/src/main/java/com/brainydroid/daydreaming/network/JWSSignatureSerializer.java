package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.*;

import java.lang.reflect.Type;

public class JWSSignatureSerializer implements JsonSerializer<JWSSignature> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "JWSSignatureSerializer";

    public static String PROTECTED = "protected";
    public static String SIGNATURE = "signature";

    @Override
    public JsonElement serialize(JWSSignature src, Type typeOfSrc,
                                 JsonSerializationContext context)
            throws JsonParseException {
        Logger.v(TAG, "Serializing jwsSignature");

        // Serialize the properties we're interested in, nothing else
        JsonObject jsonSrc = new JsonObject();
        jsonSrc.addProperty(PROTECTED, src.getProtected());
        jsonSrc.addProperty(SIGNATURE, src.getSignature());

        return jsonSrc;
    }

}
