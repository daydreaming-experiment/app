package com.brainydroid.daydreaming.network;

import android.location.Location;
import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.*;

import java.lang.reflect.Type;

public class SignatureSerializer implements JsonSerializer<Signature> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SignatureSerializer";

    public static String PROTECTED = "protected";
    public static String SIGNATURE = "signature";

    @Override
    public JsonElement serialize(Signature src, Type typeOfSrc,
                                 JsonSerializationContext context)
            throws JsonParseException {
        Logger.v(TAG, "Serializing signature");

        // Serialize the properties we're interested in, nothing else
        JsonObject jsonSrc = new JsonObject();
        jsonSrc.addProperty(PROTECTED, src.getProtected());
        jsonSrc.addProperty(SIGNATURE, src.getSignature());

        return jsonSrc;
    }

}
