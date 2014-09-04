package com.brainydroid.daydreaming.db;



import android.content.SharedPreferences;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by vincent on 02/09/14.
 */
public class Glossary {

    private static String TAG = "Glossary";

    @Inject Json json;

    private HashMap<String,String> dictionnary = new HashMap<String, String>();

    public String getDefinitionOfTerm(String term) {
        Logger.v(TAG, "Getting definition of " + term);
        if (term != null) {
            if (dictionnary.containsKey(term)) {
                Iterator it = dictionnary.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    if (pairs.getKey().equals(term)) {
                        return (String) pairs.getValue();
                    }
                }
            } else {
                Logger.v(TAG, term + "is not in dict");
                throw new RuntimeException("term is empty");
            }
        } else {
            throw new RuntimeException("term is null");
        }
        return "";
    }




    /**
     * Constructor from Json string
     * @param jsonString
     */
    public Glossary(String jsonString) {
        dictionnary = hashmapFromJsonString(jsonString);
    }

    public Glossary(HashMap<String,String> dict) {
        dictionnary = dict;
    }


    public HashMap<String, String> hashmapFromJsonString(String jsonString) {
        Logger.v(TAG, "building hashmap from Json");
        Type hmType = new TypeToken<HashMap<String, String>>() {}.getType();
        return json.fromJson(jsonString, hmType);
    }


    public HashMap<String,String> getDictionnary() {
        return dictionnary;
    }

}
