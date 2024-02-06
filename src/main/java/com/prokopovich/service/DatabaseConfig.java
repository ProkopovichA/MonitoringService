package com.prokopovich.service;
/**
 * Класс для работы с application.properties
 */

import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDatabaseUrlModel() {
        return properties.getProperty("db.url") + "?currentSchema=model";
    }

    /**
     * Метод возвращает usr для схемы service
     *
     * @return
     */
    public static String getDatabaseUrlService() {
        return properties.getProperty("db.url") + "?currentSchema=service";
    }

    public static String getDatabaseUsername() {
        return properties.getProperty("db.username");
    }

    public static String getDatabasePassword() {
        return properties.getProperty("db.password");
    }
}