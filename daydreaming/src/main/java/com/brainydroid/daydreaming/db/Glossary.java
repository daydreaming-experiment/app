package com.brainydroid.daydreaming.db;



import android.content.SharedPreferences;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by vincent on 02/09/14.
 */
public class Glossary {

    private static String TAG = "Glossary";

    private SharedPreferences sharedPreferences;

    private Map<String,String> dictionnary = new HashMap<String, String>();

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


    public HashMap<String, String> hashmapFromJsonString(String jsonString) {
        Logger.v(TAG, "building hashmap from Json");
        Gson gson = new GsonBuilder().create();
        Type hmType = new TypeToken<HashMap<String, String>>() {}.getType();
        return gson.fromJson(jsonString, hmType);
    }

    /**
     * Constructor from Json string
     * @param jsonString
     */
    public Glossary(String jsonString) {
        dictionnary = hashmapFromJsonString(jsonString);
    }


    /**
     * Constructor from loaded parameters saved in shared preferences
    */
    public Glossary() {
        Logger.v(TAG, "building hashmap from Parameters (loading from shared preferences)");

        String glossaryString = sharedPreferences.getString(ParametersStorage.GLOSSARY, ServerParametersJson.DEFAULT_GLOSSARY);
        Logger.v(TAG, "glossaryString: " + glossaryString );

        dictionnary = hashmapFromJsonString(glossaryString);
    }

    public Map<String,String> getDictionnary() {
        return dictionnary;
    }

}
