package io.github.rusted.simplestock.util;

import android.content.Context;

import java.io.IOException;
import java.util.Properties;

public class AndroidConfig implements Config {
    private static final String CONFIG_FILE = "application.properties";
    private static final Properties properties = new Properties();

    public AndroidConfig(Context context) {
        try {
            properties.load(context.getAssets().open(CONFIG_FILE));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Unable to open " + CONFIG_FILE, e);
        }
    }

    @Override
    public String get(String key) {
        return properties.getProperty(key);
    }
}
