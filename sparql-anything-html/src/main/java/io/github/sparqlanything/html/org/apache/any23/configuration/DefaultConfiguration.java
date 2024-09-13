/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.html.org.apache.any23.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Default implementation of {@link Configuration}. The default property values are read from the
 * <i>/default-configuration.properties</i> properties file in classpath.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class DefaultConfiguration implements Configuration {

    /**
     * Default configuration file.
     */
    public static final String DEFAULT_CONFIG_FILE = "/default-configuration.properties";

    public static final String FLAG_PROPERTY_ON = "on";

    public static final String FLAG_PROPERTY_OFF = "off";

    protected static final Logger logger = LoggerFactory.getLogger(DefaultConfiguration.class);

    protected static final DefaultConfiguration singleton = new DefaultConfiguration();

    protected final Properties properties;

    protected DefaultConfiguration(Properties properties) {
        this.properties = properties;
    }

    private DefaultConfiguration() {
        this(loadDefaultProperties());
    }

    /**
     * @return the singleton configuration instance. Such instance is unmodifiable.
     */
    public static synchronized DefaultConfiguration singleton() {
        return singleton;
    }

    /**
     * @return a copy of the singleton instance. such instance is modifiable.
     */
    public static synchronized ModifiableConfiguration copy() {
        final Properties propertiesCopy = (Properties) singleton.properties.clone();
        return new DefaultModifiableConfiguration(propertiesCopy);
    }

    private static Properties loadDefaultProperties() {
        final Properties properties = new Properties();
        try {
            properties.load(DefaultConfiguration.class.getResourceAsStream(DEFAULT_CONFIG_FILE));
        } catch (IOException ioe) {
            throw new IllegalStateException("Error while loading default configuration.", ioe);
        }
        return properties;
    }

    @Override
    public synchronized String[] getProperties() {
        return properties.keySet().toArray(new String[properties.size()]);
    }

    @Override
    public synchronized boolean defineProperty(String propertyName) {
        return properties.containsKey(propertyName);
    }

    @Override
    public synchronized String getProperty(String propertyName, String defaultValue) {
        final String value = getPropertyValue(propertyName);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public synchronized String getPropertyOrFail(String propertyName) {
        final String propertyValue = getPropertyValue(propertyName);
        if (propertyValue == null) {
            throw new IllegalArgumentException("The property '" + propertyName + "' is expected to be declared.");
        }
        if (propertyValue.trim().length() == 0) {
            throw new IllegalArgumentException(
                    "Invalid value '" + propertyValue + "' for property '" + propertyName + "'");
        }
        return propertyValue;
    }

    @Override
    public synchronized int getPropertyIntOrFail(String propertyName) {
        final String value = getPropertyOrFail(propertyName);
        final String trimValue = value.trim();
        try {
            return Integer.parseInt(trimValue);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("The retrieved property is not a valid Integer: '" + trimValue + "'");
        }
    }

    @Override
    public synchronized boolean getFlagProperty(final String propertyName) {
        final String value = getPropertyOrFail(propertyName);
        if (value == null) {
            return false;
        }
        if (FLAG_PROPERTY_ON.equals(value)) {
            return true;
        }
        if (FLAG_PROPERTY_OFF.equals(value)) {
            return false;
        }
        throw new IllegalArgumentException(String.format(java.util.Locale.ROOT,
                "Invalid value [%s] for flag property [%s]. Supported values are %s|%s", value, propertyName,
                FLAG_PROPERTY_ON, FLAG_PROPERTY_OFF));
    }

    @Override
    public synchronized String getConfigurationDump() {
        final String[] defaultProperties = getProperties();
        final StringBuilder sb = new StringBuilder();
        sb.append("\n======================= Configuration Properties =======================\n");
        for (String defaultProperty : defaultProperties) {
            sb.append(defaultProperty).append('=').append(getPropertyValue(defaultProperty)).append('\n');
        }
        sb.append("========================================================================\n");
        return sb.toString();
    }

    private String getPropertyValue(String propertyName) {
        if (!defineProperty(propertyName)) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format(java.util.Locale.ROOT,
                        "Property '%s' is not declared in default configuration file [%s]", propertyName,
                        DEFAULT_CONFIG_FILE));
            }
            return null;
        }
        final String systemValue = System.getProperties().getProperty(propertyName);
        if (systemValue == null) {
            return properties.getProperty(propertyName);
        }
        return systemValue;
    }

}
