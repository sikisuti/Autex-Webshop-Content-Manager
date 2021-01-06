package org.autex.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Configuration {
    private final Properties properties;
    private final Properties credentials;
    private String password;

    private Configuration() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream("configuration.properties")) {
            properties.load(fis);
        } catch (Exception ignored) {
        }

        credentials = new Properties();
        try (FileInputStream fis = new FileInputStream("credentials.properties")) {
            credentials.load(fis);
        } catch (Exception ignored) {
        }
    }

    private static final Configuration instance = new Configuration();
    public static Configuration getInstance() {
        return instance;
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }
    public Integer getIntegerProperty(String name) {
        return Integer.parseInt(properties.getProperty(name));
    }
    public String getCredentialsProperty(String name) {
        return credentials.getProperty(name);
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
        List<String> ignoredFields = Arrays.asList("password", "key", "privateKey");
        for (Map.Entry<Object, Object> property : properties.entrySet().stream().filter(entry -> !ignoredFields.contains(entry.getKey().toString())).collect(Collectors.toSet())) {
            String key = property.getKey().toString();
            String value = property.getValue().toString();
            simpleProperties.put(key, value);
        }

        return simpleProperties;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEncryptedProperty(String propertyName, String unencryptedValue) {
        try (FileOutputStream fos = new FileOutputStream("configuration.properties")) {
            properties.setProperty(propertyName, Secure.encrypt(unencryptedValue.getBytes(StandardCharsets.UTF_8), password));
            properties.store(fos, null);
        } catch (Exception ignored) {
        }
    }
}
