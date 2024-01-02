package org.autex.util;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import org.autex.exception.InvalidCredentials;

public class Configuration {
    private static final String PASSWORD = "password";
    private static final Properties properties;
    private static final List<String> sensitiveProperties = Arrays.asList(PASSWORD, "key", "secretKey");
    private static String initiatedPassword;

  public static final String CONFIGURATION_PROPERTIES = "configuration.properties";

  static {
    properties = new Properties();
    if (Files.exists(Path.of(CONFIGURATION_PROPERTIES))) {
      loadProperties();
    } else {
      loadTemplateProperties();
    }
  }

  private static void loadProperties() {
    try (var fis = new FileInputStream(CONFIGURATION_PROPERTIES)) {
      properties.load(fis);
    } catch (Exception e) {
      throw new RuntimeException("cannot read property file", e);
    }
  }

  private static void loadTemplateProperties() {
    try (var fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration-template.properties")) {
      properties.load(fis);
    } catch (Exception e) {
      throw new RuntimeException("cannot read template property file", e);
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
    if (sensitiveProperties.contains(name)
        && initiatedPassword != null
        && isNotBlank(value)) {
      try {
        value = Secure.decrypt(value, initiatedPassword);
      } catch (Exception e) {
        throw new InvalidCredentials();
      }
    }

    return value;
  }

  public static boolean isCredentialsMissing() {
    var key = getStringProperty("key");
    var secretKey = getStringProperty("secretKey");

    return isBlank(key) || isBlank(secretKey);
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
        try (FileOutputStream fos = new FileOutputStream(CONFIGURATION_PROPERTIES)) {
            properties.store(fos, null);
        } catch (Exception ignored) {
        }
    }
}
