package org.autex.util;

import org.autex.exception.InvalidCredentials;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Configuration {
    private static final String PASSWORD = "password";
    private static final Properties properties;
    private static final List<String> sensitiveProperties = Arrays.asList(PASSWORD, "key", "secretKey");
    private static String initiatedPassword;

    static {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream("configuration.properties")) {
            properties.load(fis);
        } catch (Exception ignored) {
        }
    }

    private Configuration() {}

    public static void init(String password) {
        Configuration.initiatedPassword = password;
        if (isInitialized()) {
            getStringProperty(PASSWORD);
        } else {
            setProperty(PASSWORD, password);
            storeProperties();
        }
    }

    public static boolean isInitialized() {
        return properties.getProperty(PASSWORD) != null;
    }

    public static String getStringProperty(String name) {
        String value = properties.getProperty(name);
        if (sensitiveProperties.contains(name) && initiatedPassword != null && value != null) {
            try {
                value = Secure.decrypt(value, initiatedPassword);
            } catch (Exception e) {
                throw new InvalidCredentials();
            }
        }

        return value;
    }
    public static Integer getIntegerProperty(String name) {
        return Integer.parseInt(getStringProperty(name));
    }

    public static Map<String, String> getProperties() {
        Map<String, String> propertyMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            propertyMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }

        return propertyMap;
    }

    public static Map<String, String> getSimpleProperties() {
        Map<String, String> simpleProperties = new HashMap<>();
        for (Map.Entry<Object, Object> property : properties.entrySet().stream().filter(entry -> !sensitiveProperties.contains(entry.getKey().toString())).collect(Collectors.toSet())) {
            String key = property.getKey().toString();
            String value = property.getValue().toString();
            simpleProperties.put(key, value);
        }

        return simpleProperties;
    }

    public static void setProperty(String key, String value) {
        String storable = value;
        if (sensitiveProperties.contains(key) && initiatedPassword != null) {
            try {
                storable = Secure.encrypt(value.getBytes(StandardCharsets.UTF_8), initiatedPassword);
            } catch (Exception e) {
                throw new InvalidCredentials();
            }
        }

        properties.setProperty(key, storable);
    }

    public static void storeProperties() {
        try (FileOutputStream fos = new FileOutputStream("configuration.properties")) {
            properties.store(fos, null);
        } catch (Exception ignored) {
        }
    }
}
