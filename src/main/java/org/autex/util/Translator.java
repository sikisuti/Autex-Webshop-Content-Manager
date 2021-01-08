package org.autex.util;

import java.io.FileInputStream;
import java.util.Properties;

public class Translator {
    private static final Properties properties;

    static {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream("hun.properties")) {
            properties.load(fis);
        } catch (Exception ignored) {
        }
    }

    private Translator() {}

    public static String translate(String key) {
        String value = properties.getProperty(key);
        return value == null ? key : value;
    }
}
