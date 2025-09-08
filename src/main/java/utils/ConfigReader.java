package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (is == null) {
                throw new IllegalStateException("config/config.properties not found on classpath");
            }
            PROPS.load(is);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load config.properties: " + e);
        }
    }

    private ConfigReader() {}

    public static String get(String key) {
        String val = PROPS.getProperty(key);
        if (val == null) throw new IllegalArgumentException("Missing key: " + key);
        return val.trim();
    }

    public static boolean getBoolean(String key) { return Boolean.parseBoolean(get(key)); }
    public static int getInt(String key) { return Integer.parseInt(get(key)); }
}
