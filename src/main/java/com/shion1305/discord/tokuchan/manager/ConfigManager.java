package com.shion1305.discord.tokuchan.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

class ConfigManager {
    private static final Logger logger = Logger.getLogger("ConfigManager");
    private static final Properties config;
    private final static String configDir = System.getProperty("user.home") + "/ShionServerConfig/TokuChanConfig/TokuChan.properties";

    static {
        config = new Properties();
        try (FileInputStream s = new FileInputStream(configDir)) {
            logger.info("Configuration is Loaded");
            config.load(s);
        } catch (IOException e) {
            logger.severe("Configuration LOAD FAILED");
            e.printStackTrace();
        }
    }

    static String getConfig(String field) {
        return config.getProperty(field);
    }
}
