package org.autex;

import org.autex.controller.NotificationController;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class Configuration {
    private final Properties properties;

    private Configuration() {
        properties = new Properties();
        URL url = Thread.currentThread().getContextClassLoader().getResource("configuration.properties");
        try (FileInputStream fis = new FileInputStream(new File(url.toURI()))) {
            properties.load(fis);
        } catch (Exception e) {
            NotificationController.notify("Konfiguráció betöltése sikertelen");
        }
    }

    private static final Configuration instance = new Configuration();
    public static Configuration getInstance() {
        return instance;
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    public Map<String, String> getProperties() {
        Map<String, String> propertyMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            propertyMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }

        return propertyMap;
    }
}
