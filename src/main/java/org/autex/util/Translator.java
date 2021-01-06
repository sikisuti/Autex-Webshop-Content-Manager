package org.autex.util;

import java.util.HashMap;
import java.util.Map;

public class Translator {
    private static final Map<String, String> dictionary;

    private Translator() {}

    static {
        dictionary = new HashMap<>();
        dictionary.put("weight", "Súly");
        dictionary.put("category", "Kategória");
        dictionary.put("defaultPath", "Keresési könyvtár");
        dictionary.put("host", "Webszerver host");
        dictionary.put("productsPath", "Termékek elérési útvonala");
        dictionary.put("noOfCallThreads", "Párhuzamos feldolgozási szálak száma");
    }

    public static String translate(String key) {
        return dictionary.getOrDefault(key, key);
    }
}
