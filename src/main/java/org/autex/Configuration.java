package org.autex;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration {
    private final Properties properties;
    private final Properties credentials;

    private Configuration() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(new File("configuration.properties"))) {
            properties.load(fis);
        } catch (Exception ignored) {
        }

        credentials = new Properties();
        try (FileInputStream fis = new FileInputStream(new File("credentials.properties"))) {
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
}
