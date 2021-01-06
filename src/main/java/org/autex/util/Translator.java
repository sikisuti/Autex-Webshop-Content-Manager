package org.autex.util;

import java.util.HashMap;
import java.util.Map;

public class Translator {
    private static final Map<String, String> dictionary;

    private Translator() {}

    static {
        dictionary = new HashMap<>();
        dictionary.put("weight", "súly");
        dictionary.put("category", "kategória");
    }

    public static String translate(String key) {
        return dictionary.getOrDefault(key, key);
    }
}
