package org.autex.util;

import org.autex.exception.InvalidCredentials;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Configuration {
    private static final String PASSWORD = "password";
    private final Properties properties;
    List<String> sensitiveProperties = Arrays.asList(PASSWORD, "key", "secretKey");
    private String password;

    private Configuration() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream("configuration.properties")) {
            properties.load(fis);
        } catch (Exception ignored) {
        }
    }

    public void init(String password) {
        this.password = password;
        if (isInitialized()) {
            getStringProperty(PASSWORD);
        } else {
            setProperty(PASSWORD, password);
            storeProperties();
        }
    }

    public boolean isInitialized() {
        return properties.getProperty(PASSWORD) != null;
    }

    private static final Configuration instance = new Configuration();
    public static Configuration getInstance() {
        return instance;
    }

    public String getStringProperty(String name) {
        String value = properties.getProperty(name);
        if (sensitiveProperties.contains(name) && password != null && value != null) {
            try {
                value = Secure.decrypt(value, password);
            } catch (Exception e) {
                throw new InvalidCredentials();
            }
        }

        return value;
    }
    public Integer getIntegerProperty(String name) {
        return Integer.parseInt(getStringProperty(name));
    }

    public Map<String, String> getProperties() {
        Map<String, String> propertyMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            propertyMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }

        return propertyMap;
    }

    public Map<String, String> getSimpleProperties() {
        Map<String, String> simpleProperties = new HashMap<>();
        for (Map.Entry<Object, Object> property : properties.entrySet().stream().filter(entry -> !sensitiveProperties.contains(entry.getKey().toString())).collect(Collectors.toSet())) {
            String key = property.getKey().toString();
            String value = property.getValue().toString();
            simpleProperties.put(key, value);
        }

        return simpleProperties;
    }

    public void setProperty(String key, String value) {
        String storable = value;
        if (sensitiveProperties.contains(key) && password != null) {
            try {
                storable = Secure.encrypt(value.getBytes(StandardCharsets.UTF_8), password);
            } catch (Exception e) {
                throw new InvalidCredentials();
            }
        }

        properties.setProperty(key, storable);
    }

    public void storeProperties() {
        try (FileOutputStream fos = new FileOutputStream("configuration.properties")) {
            properties.store(fos, null);
        } catch (Exception ignored) {
        }
    }
}
