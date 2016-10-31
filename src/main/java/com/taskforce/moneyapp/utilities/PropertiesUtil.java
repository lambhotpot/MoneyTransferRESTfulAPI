package com.taskforce.moneyapp.utilities;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;


public class PropertiesUtil {

    private static Properties properties = new Properties();

    static Logger log = Logger.getLogger(PropertiesUtil.class);

    public static void loadConfig(String fileName) {
        if (fileName == null) {
            log.warn("loadConfig: config file name cannot be null");
        } else {
            try {
                URL url = Loader.getResource(fileName);
                String filePath = url.getFile();
                log.info("loadConfig(): Loading config file from path: " + url.getPath());
                FileInputStream fis = new FileInputStream(filePath);
                properties.load(fis);

            } catch (FileNotFoundException fne) {
                log.error("loadConfig(): file name not found " + fileName, fne);
            } catch (IOException ioe) {
                log.error("loadConfig(): error when reading the config " + fileName, ioe);
            }
        }

    }


    public static String getStringProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            value = System.getProperty(key);
        }
        return value;
    }

    /**
     * @param key:       property key
     * @param defaultVal the default value if the key not present in config file
     * @return string property based on lookup key
     */
    public static String getStringProperty(String key, String defaultVal) {
        String value = getStringProperty(key);
        return value != null ? value : defaultVal;
    }


    public static int getIntegerProperty(String key, int defaultVal) {
        String valueStr = getStringProperty(key);
        if (valueStr == null) {
            return defaultVal;
        } else {
            try {
                return Integer.parseInt(valueStr);

            } catch (Exception e) {
                log.warn("getIntegerProperty(): cannot parse integer from properties file for: " + key + "fail over to default value: " + defaultVal, e);
                return defaultVal;
            }
        }
    }

    //initialise

    static {
        String configFileName = System.getProperty("moneyapp.properties");

        if (configFileName == null) {
            configFileName = "moneyapp.properties";
        }
        loadConfig(configFileName);

    }


}
