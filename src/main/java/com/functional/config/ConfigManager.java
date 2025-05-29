package com.functional.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final Properties properties = new Properties();
    private static ConfigManager instance;

    private ConfigManager() {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getBaseUrl() {
        return getProperty("base.url");
    }

    public String getLoginEmail() {
        return getProperty("login.email");
    }

    public String getLoginPassword() {
        return getProperty("login.password");
    }

    public String getfullName() {
        return getProperty("shipping.fullname");
    }

    public String getAddressLine1() {
        return getProperty("shipping.address_line1");
    }

    public String getAddressLine2() {
        return getProperty("shipping.address_line2");
    }

    public String getCity() {
        return getProperty("shipping.city");
    }

    public String getState() {
        return getProperty("shipping.state");
    }

    public String getPostalCode() {
        return getProperty("shipping.pincode");
    }

    public String getPhoneNumber() {
        return getProperty("shipping.phone");
    }

    public String getProductSearchTerm() {
        return getProperty("product.searchTerm");
    }

}