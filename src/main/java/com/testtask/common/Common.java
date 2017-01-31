package com.testtask.common;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Common used functions and constants.
 *
 */
public class Common
{
    public final static String KEY_VALUE_DELIMITER = ",";
    private final static String SEPARATOR = "\\";
    public static final String FILE_PATH = "filePath";
    public static final int DEFAULT_LINES_NUMBER = 100;
    private static final String PROPERTIES_FILE = "/config.properties";

    private static final Logger log = Logger.getLogger(Common.class);

    public static String getPropertyValue(String propertyName) {
        String propertyVal;
        Properties property = new Properties();
        try {
            property.load(Common.class.getResourceAsStream(PROPERTIES_FILE));
            propertyVal = property.getProperty(propertyName);
        } catch (IOException e) {
            log.error(e);
			throw new RuntimeException(e);
        }
        return propertyVal;
    }

    public static String getFilePath(String fullFileName) {
        return fullFileName.substring(0, fullFileName.lastIndexOf(SEPARATOR) + 1);
    }

    public static void createPathIfNeeded(String filePath) {
        File path = new File(filePath);
        if (!path.exists()){
            path.mkdir();
        }
    }
}
